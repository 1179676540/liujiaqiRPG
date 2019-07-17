package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.InstructionsType;
import rpg.configure.UserStatus;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.core.ThreadResource;
import rpg.data.dao.UserskillMapper;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.service.area.Area;
import rpg.service.skill.SkillList;
import rpg.service.task.TaskManage;
import rpg.service.user.UserResources;
import rpg.util.MsgType;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.util.UserService;
import rpg.xmlparse.MonsterXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 战斗逻辑
 * 
 * @author ljq
 *
 */
@Component
public class AckDispatch {

	private static final int MAX_LENGTH_MSG = 3;
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private MonsterXmlParse monsterXmlParse;
	@Autowired
	private TaskManage taskManage;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Value("${id}")
	private int spcid;

	public void ack(User user, Channel ch, ChannelGroup group, String msgR) {
		ChannelUtil.getSessionData(ch).setStatus(UserStatus.ACK_MONSTER.getValue());
		String[] msg = msgR.split("\\s+");
		// 获取用户技能列表
		Integer id = user.getAreaid();
		String nickname = user.getNickname();
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userskill> list = userskillMapper.selectByExample(example);
		// 获取地图中的怪物
		LinkedList<Monster> monsterList = Area.sceneList.get(id - 1).getMonsterList();
		// 第一次攻击
		if (msg.length == MAX_LENGTH_MSG) {
			ackFirst(user, ch, msg, list, monsterList);
		} else if (msg.length == 1) {
			// 二次及以上攻击
			ackSecondeAndMore(user, ch, group, msg, list, monsterList);
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 二次及以上攻击
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 * @param list
	 * @param monsterList
	 */
	public void ackSecondeAndMore(User user, Channel ch, ChannelGroup group, String[] msg, List<Userskill> list,
			LinkedList<Monster> monsterList) {
		List<Monster> list2 = AllOnlineUser.monsterMp.get(ch.remoteAddress());
		Monster monster = list2.get(list2.size() - 1);
		// 找到配置的技能
		if (msg[0].equals(InstructionsType.ESC.getValue())) {
			ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
			SendMsg.send("成功退出战斗", ch);
		} else if (msg[0].equals(InstructionsType.ACK.getValue())) {
			ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
			SendMsg.send("指令错误", ch);
		} else {
			if (msg[0].equals(InstructionsType.SKILL_KEY_3.getValue())) {
				String s = rpgUtil.skillChange(msg[0], user);
				msg[0] = s;
			}
			for (Userskill userskill : list) {
				String skillId = String.valueOf(userskill.getSkill());
				if (skillId.equals(msg[0])) {
					Skill skill = SkillList.getInstance().getSkillMp().get(msg[0]);
					// 获取当前时间毫秒值
					long millis = System.currentTimeMillis();
					HashMap<String, Long> map = SkillList.getInstance().getCdMpCache().get(user);
					Long lastmillis = map.get(skillId);
					// 蓝量满足
					if (skill.getMp() <= user.getMp()) {
						if (lastmillis != null) {
							skillUsed(user, ch, group, monsterList, monster, skillId, skill, millis, lastmillis);
						} else {
							// 技能未使用过
							skillNoUsed(user, ch, group, monsterList, monster, skillId, skill);
						}
					} else {
						// 蓝量不足
						SendMsg.send("蓝量不足，请充值", ch);
					}
				}
			}
		}
	}

	/**
	 * 技能未被使用过
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param monsterList
	 * @param monster
	 * @param skillId
	 * @param skill
	 */
	public void skillNoUsed(User user, Channel ch, ChannelGroup group, LinkedList<Monster> monsterList, Monster monster,
			String skillId, Skill skill) {
		saveLastSkillTime(user, skillId, skill);
		// 判断特殊技能
		if (skill.getId() == spcid) {
			user.getAndAddHp(user, skill.getHurt());
			SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp() + "\n" + "血量恢复"
					+ skill.getHurt() + "-剩余血量" + user.getHp(), ch);
		} else {
			// 更新人物buff
			if (skill.getId() != 9) {
				userService.updateUserBuff(user, skill);
			}
			// 更新怪物buff
			userService.updateMonsterBuff(user, skill, monster);
			// 判断装备是否还有耐久度
			UserAttribute attribute = user.getUserAttribute();
			if (attribute != null) {
				List<Userzb> list1 = user.getUserzbs();
				for (Userzb userzb : list1) {
					if (userzb.getNjd() <= 0) {
						Zb zb = zbXmlParse.getZbById(userzb.getZbid());
						if (zb != null && attribute != null) {
							attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
							userzb.setIsuse(0);
						}
					}
				}
				int ack = attribute.getAck();
				int hurt = skill.getHurt() + ack;
				int monsterHp = monster.getHp() - hurt;
				monster.setHp(monsterHp);
				if (monsterHp <= 0) {
					SendMsg.send("怪物已被消灭！你真棒", ch);
					rpgUtil.ackEnd(user, ch, monster);
					taskManage.checkTaskComplete(user, monster.getId());
					for (Channel channel : group) {
						if (ch != channel) {
							SendMsg.send(user.getNickname() + "消灭了" + monster.getName(), channel);
						}
					}
					List<User> userList = monster.getUserList();
					for (User user3 : userList) {
						Channel ch1 =AllOnlineUser.userchMap.get(user3);
						ChannelUtil.getSessionData(ch1).setStatus(UserStatus.ORDINARY.getValue());
					}
					Monster monster3 = monsterXmlParse.getMonsterById(monster.getId());
					Monster monster2 = (Monster) monster3.clone();
					monsterList.remove(monster);
					monsterList.add(monster2);
//					ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
					// 损耗装备耐久度
					for (Userzb userzb : list1) {
						userzb.setNjd(userzb.getNjd() - 5);
						if(userzb.getNjd()<0) {
							userzb.setNjd(0);
						}
					}
				} else {
					SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp() + "\n" + "攻击了"
							+ monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp(), ch);
					// 损耗装备耐久度
					for (Userzb userzb : list1) {
						userzb.setNjd(userzb.getNjd() - 5);
					}
				}
//									break;
			}
		}
	}

	/**
	 * 技能被使用过
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param monsterList
	 * @param monster
	 * @param skillId
	 * @param skill
	 * @param millis
	 * @param lastmillis
	 */
	public void skillUsed(User user, Channel ch, ChannelGroup group, LinkedList<Monster> monsterList, Monster monster,
			String skillId, Skill skill, long millis, Long lastmillis) {
		// 技能cd满足
		if (millis - lastmillis >= skill.getCd()) {
			skillNoUsed(user, ch, group, monsterList, monster, skillId, skill);
		} else {
			// cd未到
			SendMsg.send("技能冷却中", ch);
		}
	}

	/**
	 * 第一次攻击
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 * @param list
	 * @param monsterList
	 */
	public void ackFirst(User user, Channel ch, String[] msg, List<Userskill> list, LinkedList<Monster> monsterList) {
		boolean find = false;
		for (Monster monster : monsterList) {
			// 找到场景内怪物
			if (msg[1].equals(monster.getName())) {
				find = true;
				if (monster.getHp() > 0) {
					monster.setCountAcker(monster.getCountAcker() + 1);
					// 添加怪物的攻击者
					addMonsterAcker(user, monster);
					List<Monster> list2 = new ArrayList<>();
					list2.add(monster);
					AllOnlineUser.monsterMp.put(ch.remoteAddress(), list2);
					// 找到配置的技能
					for (Userskill userskill : list) {
						String skillId = String.valueOf(userskill.getSkill());
						if (skillId.equals(msg[2])) {
							Skill skill = SkillList.getInstance().getSkillMp().get(msg[2]);
							// 蓝量足够
							if (skill.getMp() <= user.getMp()) {
								// 存储上次使用技能时间
								saveLastSkillTime(user, skillId, skill);
								// 更新人物buff
								if (skill.getId() != 9) {
									userService.updateUserBuff(user, skill);
								}
								// 更新怪物buff
								userService.updateMonsterBuff(user, skill, monster);
								// 判断装备是否还有耐久度
								UserAttribute attribute = user.getUserAttribute();
								if (attribute != null) {
									List<Userzb> list1 = user.getUserzbs();
									for (Userzb userzb : list1) {
										if (userzb.getNjd() <= 0) {
											Zb zb = zbXmlParse.getZbById(userzb.getZbid());
											if (zb != null && attribute != null) {
												attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
												userzb.setIsuse(0);
											}
										}
									}
									int hurt = skill.getHurt();
									SendMsg.send("开始攻击" + monster.getName(), ch);
									// 损耗装备耐久度
									for (Userzb userzb : list1) {
										userzb.setNjd(userzb.getNjd() - 5);
										if(userzb.getNjd()<0) {
											userzb.setNjd(0);
										}
									}
									// 怪物攻击线程
									if (monster.getCountAcker() == 1) {
										startMonsterThread(user, monster, attribute);
										break;
									}
								}
							} else {
								// 蓝量不足
								SendMsg.send("蓝量不足，请充值", ch);
							}
						}
					}
					break;
				} else {
					ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
					SendMsg.send("怪物不存在", ch);
					break;
				}
			}
		}
		if (!find) {
			ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
			SendMsg.send("怪物不存在", ch);
		}
	}

	/**
	 * 存储上次使用技能时间
	 * 
	 * @param user
	 * @param skillId
	 * @param skill
	 */
	public void saveLastSkillTime(User user, String skillId, Skill skill) {
		long currentTimeMillis = System.currentTimeMillis();
		HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
		curSkill.put(skillId, currentTimeMillis);
		SkillList.getInstance().getCdMpCache().put(user, curSkill);
		user.getAndSetMp(user, user.getMp() - skill.getMp());
	}

	/**
	 * 开始怪物线程
	 * 
	 * @param user
	 * @param monster
	 * @param attribute
	 */
	public void startMonsterThread(User user, Monster monster, UserAttribute attribute) {
		ThreadResource.monsterThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					User user2 = UserResources.nameMap.get(user.getNickname());
					Channel newchannel = AllOnlineUser.userchMap.get(user2);
					int status = ChannelUtil.getSessionData(newchannel).getStatus();
					if (status == 2) {
						ConcurrentHashMap<Integer, Long> buffTime1 = user2.getBuffStartTime();
						// 检验怪物Buff
						String word1 = userService.checkMonsterBuff(monster, newchannel);
						SendMsg.sendMonsterbufMsg(word1, newchannel);
						// 检测用户状态
						if (buffTime1 != null && buffTime1.get(3) != null) {
							SendMsg.sendMonsterAckMsg("-你有最强护盾护体，免疫伤害，你的血量剩余：" + user.getHp(),
									newchannel);
						} else {
							int monsterAck = monster.getAck() - attribute.getDef();
							if (monsterAck <= 0) {
								monsterAck = 1;
							}
							int hp = user2.getHp() - monsterAck;
							// 怪物存活
							if (monster.getHp() > 0) {
								if (hp > 0) {
									user2.setHp(hp);
									SendMsg.sendMonsterAckMsg("-你受到伤害：" + monsterAck
											+ "-你的血量剩余：" + hp, newchannel);
								} else {
									SendMsg.send("你已被打死", newchannel);
									user2.setHp(1000);
									ChannelUtil.getSessionData(newchannel).setStatus(UserStatus.ORDINARY.getValue());
									break;
								}
							} else {
								// 怪物死亡
								List<User> userList = monster.getUserList();
								for (User user3 : userList) {
									Channel ch =AllOnlineUser.userchMap.get(user3);
									ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
								}
//								ChannelUtil.getSessionData(newchannel).setStatus(UserStatus.ORDINARY.getValue());
								break;
							}
						}
					} else {
						break;
					}
//					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * 添加怪物的攻击者
	 * 
	 * @param user
	 * @param monster
	 */
	public void addMonsterAcker(User user, Monster monster) {
		if (monster.getCountAcker() == 1) {
			LinkedList<User> userList = new LinkedList<>();
			userList.add(user);
			monster.setUserList(userList);
		} else {
			List<User> userList = monster.getUserList();
			userList.add(user);
		}
	}
}
