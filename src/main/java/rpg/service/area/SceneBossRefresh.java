package rpg.service.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import rpg.configure.BuffType;
import rpg.configure.MsgResp;
import rpg.configure.RoleType;
import rpg.configure.UserStatus;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.core.SessionData;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.pojo.BossScene;
import rpg.pojo.Buff;
import rpg.pojo.Group;
import rpg.pojo.Level;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.service.AckBossDispatch;
import rpg.service.bossscene.BossSceneCache;
import rpg.service.group.GroupCache;
import rpg.service.skill.SkillList;
import rpg.service.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.util.UserService;
import rpg.xmlparse.BuffXmlParse;
import rpg.xmlparse.LevelXmlParse;

/**
 * 场景Boss线程
 * 
 * @author ljq
 *
 */
public class SceneBossRefresh implements Runnable {

	private UserService userService;
	private User initUser;
	private BossScene bossScene;
	private Channel oldch;
	private Group firstGroup;
	private UserAttribute attribute;
	private TaskManage taskManage;
	private RpgUtil rpgUtil;
	private BuffXmlParse buffXmlParse;
	private LevelXmlParse levelXmlParse;

	public SceneBossRefresh(UserService userService, User initUser, BossScene bossScene, Channel oldch,
			Group firstGroup, UserAttribute attribute, TaskManage taskManage, RpgUtil rpgUtil,
			BuffXmlParse buffXmlParse,LevelXmlParse levelXmlParse) {
		super();
		this.userService = userService;
		this.initUser = initUser;
		this.bossScene = bossScene;
		this.oldch = oldch;
		this.firstGroup = firstGroup;
		this.attribute = attribute;
		this.taskManage=taskManage;
		this.rpgUtil=rpgUtil;
		this.buffXmlParse=buffXmlParse;
		this.levelXmlParse=levelXmlParse;
	}

	@Override
	public void run() {
		while (true) {
			boolean exitFlag = false;
			List<User> list3 = firstGroup.getList();
			User user = initUser;
			if (list3.size() == 0) {
				BossScene bossScene = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
				if (bossScene != null) {
					bossScene = null;
					BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
				}
				break;
			}
			if (firstGroup.getUser() != initUser) {
				for (User userLeader : list3) {
					user = userLeader;
					break;
				}
			}
			Channel ch = AllOnlineUser.userchMap.get(user);
			List<Monster> list4 = AllOnlineUser.monsterMp.get(ch.remoteAddress());
			// 达到挑战时间
			if (System.currentTimeMillis() - bossScene.getStartTime() >= bossScene.getLastedTime()) {
				challengeTimeArrivalHandling(user);
				break;
			} else {
				// 挑战时间未到
				exitFlag = allMonsterAck(exitFlag, list3, user, ch, list4);
				if (exitFlag) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 所有怪物进行攻击
	 * 
	 * @param exitFlag
	 * @param list3
	 * @param user
	 * @param ch
	 * @param list4
	 * @return
	 */
	public boolean allMonsterAck(boolean exitFlag, List<User> list3, User user, Channel ch, List<Monster> list4) {
		for (int index = 0; index < list4.size(); index++) {
			Monster monster = list4.get(index);
//			boolean ackstatus = IOsession.chStatus.containsKey(ch);
			SessionData sessionData = ChannelUtil.getSessionData(ch);
			int status = sessionData.getStatus();
//			if (ackstatus) {
				if (status == UserStatus.ACK_BOSS.getValue()) {
					// 检验怪物Buff
					chechMonsterBuff(user, ch, monster);
					// 怪物存活
					if (monster.getHp() > 0) {
						boolean wudiFlag = getWudiFlag(list3);
						// 检测用户状态
						if (wudiFlag) {
							checkUserStatus(user, monster);
						} else {
							List<User> userList = monster.getUserList();
							Random random = new Random();
							int monsterSkillId = random.nextInt(2);
							int ackuserId = random.nextInt(userList.size());
							for (int i = 0; i < list3.size(); i++) {
								User user2 = list3.get(i);
//								ConcurrentHashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp.get(user2);
								ConcurrentHashMap<Integer, Long> buffTime1 = user2.getBuffStartTime();
								if (buffTime1 != null && buffTime1.get(6) != null) {
									ackuserId = i;
								}
							}
							// 第一种攻击
							if (monsterSkillId == 0) {
								User user3 = userList.get(ackuserId);
								int hp = user3.getHp() - monster.getAck();
								Channel ch1 = AllOnlineUser.userchMap.get(user3);
								Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user3.getGroupId());
								if (hp > 0) {
									selectionSkillAck(monster, random, user3, hp, ch1, group2);
								} else {
									userDeathHandle(user, user3, ch1, group2);
									exitFlag = true;
									break;
								}
							}
							// 第二种攻击
							else {
								exitFlag = secondAck(exitFlag, user, monster);
							}
						}
					}
					// 怪物死亡
					else {
						if (monster.getDeadType() == 1) {
							if (list4.size() != 1) {
								index = updateMonsterList(user, list4, index, monster);
							} else {
								// 产生新的boss
								ArrayList<Monster> monsterList = bossScene.getMonsterList();
								if (bossScene.getLayer() - 1 > bossScene.getId()) {
									produceNewBoss(user, list4, monsterList);
								} else {
									eliminateBossHandle(user, ch, monster);
									exitFlag = true;
									break;
								}
							}
						} else {
							killBossHandle(user);
							exitFlag = true;
							break;
						}
					}
				} else {
					exitFlag = true;
					break;
				}
//			}
		}
		return exitFlag;
	}

	/**
	 * 获取无敌状态
	 * 
	 * @param list3
	 * @return
	 */
	public boolean getWudiFlag(List<User> list3) {
		boolean wudiFlag = false;
		for (User user2 : list3) {
//			ConcurrentHashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp.get(user2);
			ConcurrentHashMap<Integer, Long> buffTime1 = user2.getBuffStartTime();
			if (buffTime1 != null && buffTime1.get(3) != null) {
				wudiFlag = true;
				break;
			}
		}
		return wudiFlag;
	}

	/**
	 * 杀死boss处理
	 * 
	 * @param user
	 */
	public void killBossHandle(User user) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list2 = group2.getList();
			for (User user2 : list2) {
				Channel channel = AllOnlineUser.userchMap.get(user2);
				ChannelUtil.getSessionData(channel).setStatus(1);
			}
		}
		// 回收boss场景，怪物线程
		BossScene bossScene1 = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
		bossScene1 = null;
		BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
	}

	/**
	 * 消灭Boss处理
	 * 
	 * @param user
	 * @param ch
	 * @param monster
	 */
	public void eliminateBossHandle(User user, Channel ch, Monster monster) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list31 = group2.getList();
			for (User user3 : list31) {
				Channel channel1 = AllOnlineUser.userchMap.get(user3);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(MsgResp.BOSSSCENE_SUCCESS);
				SendMsg.send(builder.build(), channel1);
				rpgUtil.ackEnd(user3, channel1, monster);
				taskManage.checkTaskComplete(user3, bossScene.getSceneid());
				ChannelUtil.getSessionData(channel1).setStatus(1);
			}
		}
		monster.setAliveFlag(false);
		ChannelUtil.getSessionData(ch).setStatus(1);
		AckBossDispatch.removeUserlist(user, bossScene);
		// 回收boss场景
		bossScene = null;
		BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
	}

	/**
	 * 产生新boss
	 * 
	 * @param user
	 * @param list4
	 * @param monsterList
	 * @return
	 */
	public void produceNewBoss(User user, List<Monster> list4, ArrayList<Monster> monsterList) {
		bossScene.setId(bossScene.getId() + 1);
		// Monster monster2 =
		// monsterList.get(bossScene.getId());
		Map<Integer, Integer> struct = bossScene.getStruct();
		Integer csid1 = struct.get(bossScene.getId());
		Integer csid2 = struct.get(bossScene.getId() - 1);
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		// 将怪物指定用户
		if (group2 != null) {
			List<User> list31 = group2.getList();
			for (User user3 : list31) {
				LinkedList<Monster> linkedList2 = new LinkedList<>();
				for (int i = csid2 + 1; i <= csid1; i++) {
					Monster monster2 = monsterList.get(i);
					if (group2 != null) {
						List<User> list311 = group2.getList();
						ArrayList<User> userList1 = new ArrayList<>();
						for (User user31 : list311) {
							userList1.add(user31);
						}
						monster2.setUserList(userList1);
					}
					// monster = null;
					// monster = monster2;
					linkedList2.add(monster2);
				}
//				list4 = null;
				list4 = linkedList2;
				Channel channel1 = AllOnlineUser.userchMap.get(user3);
				AllOnlineUser.monsterMp.put(channel1.remoteAddress(), list4);
				String word11 = "";
				for (Monster monster21 : list4) {
					word11 += monster21.getName() + "-血量:" + monster21.getHp() + "攻击力:" + monster21.getAck() + "\n";
				}
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData("boss已被消灭,新的Boss出现\n" + word11);
				SendMsg.send(builder.build(), channel1);
			}
		}
	}

	/**
	 * 更新怪物列表
	 * 
	 * @param user
	 * @param list4
	 * @param index
	 * @param monster
	 * @return
	 */
	public int updateMonsterList(User user, List<Monster> list4, int index, Monster monster) {
		list4.remove(monster);
		// 更新怪物列表
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list31 = group2.getList();
			for (User user3 : list31) {
				Channel channel = AllOnlineUser.userchMap.get(user3);
				AllOnlineUser.monsterMp.put(channel.remoteAddress(), list4);
//				SendMsg.send("消灭了" + monster.getName(), channel);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData("消灭了" + monster.getName());
				SendMsg.send(builder.build(), channel);
			}
		}
		monster = null;
		index--;
		return index;
	}

	/**
	 * 第二种攻击
	 * 
	 * @param exitFlag
	 * @param user
	 * @param monster
	 * @return
	 */
	public boolean secondAck(boolean exitFlag, User user, Monster monster) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list2 = group2.getList();
			for (User user2 : list2) {
				Channel channel = AllOnlineUser.userchMap.get(user2);
				int monsterAck = monster.getAck() - attribute.getDef();
				if (monsterAck <= 0) {
					monsterAck = 1;
				}
				int hp = user2.getHp() - monsterAck;
				// 血量满足
				if (hp > 0) {
					user2.setHp(hp);
					ServerRespPacket.MonsterAckResp.Builder builder = ServerRespPacket.MonsterAckResp.newBuilder();
					builder.setData("-你受到" + monster.getName() + "全体全屏技能(无视护盾)伤害：" + monsterAck + "-你的血量剩余：" + hp);
					SendMsg.send(builder.build(), channel);
				} else {
					Channel ch1 = AllOnlineUser.userchMap.get(user2);
					userDeathHandle(user, user2, ch1, group2);
					exitFlag = true;
					break;
				}
			}
		}
		return exitFlag;
	}

	/**
	 * 用户死亡处理
	 * 
	 * @param user
	 * @param user3
	 * @param ch1
	 * @param group2
	 */
	public void userDeathHandle(User user, User user3, Channel ch1, Group group2) {
//		SendMsg.send("你已被打死，副本挑战失败，你已被传送出副本", ch1);
		Builder builder = ServerRespPacket.Resp.newBuilder();
		builder.setData(MsgResp.BOSSSCENE_DEAD);
		SendMsg.send(builder.build(), ch1);
		if (group2 != null) {
			List<User> list = group2.getList();
			for (User user2 : list) {
				Level level = levelXmlParse.getLevelById(user2.getLevel());
				user2.setHp(level.getHp());
				Channel channel = AllOnlineUser.userchMap.get(user2);
				if (channel != ch1) {
//					SendMsg.send(user3.getNickname() + "已被打死，副本挑战失败，你已被传送出副本", channel);
					Builder builder1 = ServerRespPacket.Resp.newBuilder();
					builder1.setData(user3.getNickname() + "已被打死，副本挑战失败，你已被传送出副本");
					SendMsg.send(builder1.build(), channel);
				}
				ChannelUtil.getSessionData(channel).setStatus(1);
			}
		}
		Level level = levelXmlParse.getLevelById(user3.getLevel());
		user3.setHp(level.getHp());
		ChannelUtil.getSessionData(ch1).setStatus(1);
		// 回收boss场景，怪物线程
		BossScene bossScene1 = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
		bossScene1 = null;
		BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
	}

	/**
	 * 选定技能进行攻击
	 * 
	 * @param monster
	 * @param random
	 * @param user3
	 * @param hp
	 * @param ch1
	 * @param group2
	 */
	public void selectionSkillAck(Monster monster, Random random, User user3, int hp, Channel ch1, Group group2) {
		// 选定具体技能
		List<Integer> skillList = monster.getSkillList();
		int randomId = random.nextInt(skillList.size());
		Integer integer = skillList.get(randomId);
		Skill skill2 = SkillList.getInstance().getSkillMp().get(String.valueOf(integer));
		userService.updateUserBuff(user3, skill2);
		Buff getBuff = buffXmlParse.getBuffByid(Integer.valueOf(skill2.getEffect()));
		user3.setHp(hp);
		// 判断是否召唤师
		if (RoleType.ZHAOHUANSHI.getValue() == user3.getRoletype()) {
			List<Monster> list2 = AllOnlineUser.monsterMp.get(ch1.remoteAddress());
			for (Monster monster1 : list2) {
				HashMap<Integer, Long> map = monster1.getMonsterBuffStartTime();
				if (map != null) {
					Long long1 = map.get(BuffType.PETACK.getValue());
					if (long1 != null) {
						long1 = null;
						map.remove(BuffType.PETACK.getValue());
					}
				}
			}
			Buff buff = buffXmlParse.getBuffByid(BuffType.PETACK.getValue());
			userService.updateMonsterBuff(buff, monster);
		}
		// 推送攻击消息
		ServerRespPacket.MonsterAckResp.Builder builder = ServerRespPacket.MonsterAckResp.newBuilder();
		builder.setData("-你受到" + monster.getName() + "单体技能(无视防御)" + skill2.getName() + "伤害：" + monster.getAck()
		+ "-你的血量剩余：" + hp + "你产生了" + getBuff.getName());
		SendMsg.send(builder.build(), ch1);
		if (group2 != null) {
			List<User> list = group2.getList();
			for (User user2 : list) {
				Channel channel = AllOnlineUser.userchMap.get(user2);
				if (channel != ch1) {
					ServerRespPacket.MonsterAckResp.Builder builder1 = ServerRespPacket.MonsterAckResp.newBuilder();
					builder1.setData("-" + user3.getNickname() + "受到" + monster.getName() + "单体技能(无视防御)伤害："
							+ monster.getAck() + "-血量剩余：" + hp);
					SendMsg.send(builder1.build(), channel);
				}
			}
		}
	}

	/**
	 * 检查用户状态
	 * 
	 * @param user
	 * @param monster
	 */
	public void checkUserStatus(User user, Monster monster) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			for (User user2 : list) {
				Channel channel = AllOnlineUser.userchMap.get(user2);
				ServerRespPacket.MonsterAckResp.Builder builder = ServerRespPacket.MonsterAckResp.newBuilder();
				builder.setData("-" + monster.getName() + "-你有最强护盾护体，免疫伤害，你的血量剩余：" + user2.getHp());
				SendMsg.send(builder.build(), channel);
			}
		}
	}

	/**
	 * 检查怪物buff
	 * 
	 * @param user
	 * @param ch
	 * @param monster
	 */
	public void chechMonsterBuff(User user, Channel ch, Monster monster) {
		String word = userService.checkMonsterBuff(monster, ch);
		Group group6 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group6 != null) {
			List<User> list = group6.getList();
			for (User user6 : list) {
				Channel channel = AllOnlineUser.userchMap.get(user6);
				ServerRespPacket.MonsterBufResp.Builder builder = ServerRespPacket.MonsterBufResp.newBuilder();
				builder.setData(word);
				SendMsg.send(builder.build(), channel);
			}
		}
	}

	/**
	 * 挑战时间到达处理
	 * 
	 * @param user
	 */
	public void challengeTimeArrivalHandling(User user) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list2 = group2.getList();
			for (User user2 : list2) {
				Channel channel = AllOnlineUser.userchMap.get(user2);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(MsgResp.BOSSSCENE_TIME_OVER);
				SendMsg.send(builder.build(), channel);
				ChannelUtil.getSessionData(channel).setStatus(1);
			}
		}
		// 回收boss场景，怪物线程
		BossScene bossScene1 = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
		bossScene1 = null;
		BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
	}
}