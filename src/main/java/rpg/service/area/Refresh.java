package rpg.service.area;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import rpg.configure.BuffType;
import rpg.core.AllOnlineUser;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.UserBufResp.Builder;
import rpg.pojo.Buff;
import rpg.pojo.Group;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.service.group.GroupCache;
import rpg.service.skill.SkillList;
import rpg.util.SendMsg;
import rpg.xmlparse.BuffXmlParse;

/**
 * 玩家的各种Buff
 * 
 * @author ljq
 *
 */
public class Refresh implements Runnable {
	
	private BuffXmlParse buffXmlParse;
	
	public Refresh(BuffXmlParse buffXmlParse) {
		this.buffXmlParse=buffXmlParse;
	}

	@Override
	public synchronized void run() {
		try {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user : AllOnlineUser.onlineUserMap.values()) {
					Channel channel = AllOnlineUser.userchMap.get(user);
					if (user.getLiveFlag() != 1) {
						ConcurrentHashMap<Integer, Long> buffTime = user.getBuffStartTime();
						int addMp = 5;
						int subHp = 0;
						if (buffTime != null) {
							for (Entry<Integer, Long> entry : buffTime.entrySet()) {
								// 通过buffID找到具体的buff
								Integer buffId = entry.getKey();
								if (buffId != null) {
									Buff buff = buffXmlParse.getBuffByid(buffId);
									// 获取使用Buff的时间
									Long lastTime = entry.getValue();
									if (lastTime != null) {
										long currentTimeMillis = System.currentTimeMillis();
										if (buff != null) {
											if (buffId.equals(BuffType.ADDHP.getValue())) {
												if (currentTimeMillis - lastTime >= buff.getLastedTime()) {
													addHpBuff(user, channel, buffTime, buffId, buff);
													break;
												}
											} else {
												addMp = otherBuff(user, channel, buffTime, addMp, subHp, buffId, buff,
														lastTime, currentTimeMillis);
											}
										}
									}
								}
							}
						}
						if (user.getMp() < 100) {
							if (user.getMp() + addMp > 100) {
								user.getAndSetMp(user, 100);
							} else {
								user.getAndSetMp(user, user.getMp() + addMp);
							}
							System.out.println(user.getNickname() + "-当前蓝量：" + user.getMp());
//							SendMsg.send("001" + user.getNickname() + "-当前蓝量：" + user.getMp(), channel);
							Builder builder = ServerRespPacket.UserBufResp.newBuilder();
							builder.setData(user.getNickname() + "-当前蓝量：" + user.getMp());
							SendMsg.send(builder.build(), channel);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**其他各种Buff
	 * @param user
	 * @param channel
	 * @param buffTime
	 * @param addMp
	 * @param subHp
	 * @param buffId
	 * @param buff
	 * @param lastTime
	 * @param currentTimeMillis
	 * @return
	 */
	public int otherBuff(User user, Channel channel, ConcurrentHashMap<Integer, Long> buffTime, int addMp, int subHp,
			Integer buffId, Buff buff, Long lastTime, long currentTimeMillis) {
		if (currentTimeMillis - lastTime < buff.getLastedTime()) {
			switch (buffId) {
			case 1:
				addMp += buff.getMp();
				break;
			case 2:
				subHp += buff.getMp();
				user.getAndSetHp(user, user.getHp() - subHp);
				System.out.println(user.getNickname() + "-血量减少" + subHp
						+ "-当前血量:" + user.getHp());
				Builder builder = ServerRespPacket.UserBufResp.newBuilder();
				builder.setData(user.getNickname() + "-血量减少" + subHp
						+ "-当前血量:" + user.getHp());
				SendMsg.send(builder.build(), channel);
				break;
			default:
				break;
			}
		} else {
			switch (buffId) {
			case 3:
				Long long1 = buffTime.get(3);
				long1 = null;
				buffTime.remove(3);
				break;
			case 4:
				Long long2 = buffTime.get(4);
				long2 = null;
				buffTime.remove(4);
				Builder builder = ServerRespPacket.UserBufResp.newBuilder();
				builder.setData("击晕时间已到");
				SendMsg.send(builder.build(), channel);
				break;
			case 6:
				Long long3 = buffTime.get(6);
				long3 = null;
				buffTime.remove(6);
				Builder builder2 = ServerRespPacket.UserBufResp.newBuilder();
				builder2.setData("嘲讽技能时间已到");
				SendMsg.send(builder2.build(), channel);
				break;
			default:
				break;
			}
		}
		return addMp;
	}

	/**回血buff
	 * @param user
	 * @param channel
	 * @param buffTime
	 * @param buffId
	 * @param buff
	 */
	public void addHpBuff(User user, Channel channel, ConcurrentHashMap<Integer, Long> buffTime, Integer buffId,
			Buff buff) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		Skill skill = SkillList.getInstance().getSkillMp().get("5");
		if (group2 != null) {
			List<User> list3 = group2.getList();
			for (User user3 : list3) {
				user3.getAndAddHp(user3, buff.getMp());
				if (!user3.getNickname().equals(user.getNickname())) {
					Channel channel1 = AllOnlineUser.userchMap.get(user3);
					Builder builder = ServerRespPacket.UserBufResp.newBuilder();
					builder.setData(user.getNickname() + "对你使用了"
							+ skill.getName() + "血量恢复" + buff.getMp()
							+ "-剩余血量" + user3.getHp());
					SendMsg.send(builder.build(), channel1);
				} else {
					Builder builder = ServerRespPacket.UserBufResp.newBuilder();
					builder.setData("使用了" + skill.getName() + "-蓝量消耗"
							+ skill.getMp() + "-剩余" + user.getMp() + "\n"
							+ "血量恢复" + buff.getMp() + "-剩余血量"
							+ user.getHp());
					SendMsg.send(builder.build(), channel);
				}
			}
		}
		Long long2 = buffTime.get(buffId);
		long2 = null;
		buffTime.remove(buffId);
	}
}
