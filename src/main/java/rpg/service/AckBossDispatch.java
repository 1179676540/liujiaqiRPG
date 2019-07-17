package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.BuffType;
import rpg.configure.InstructionsType;
import rpg.configure.MsgResp;
import rpg.configure.MsgSize;
import rpg.configure.RoleType;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.core.ThreadResource;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.data.dao.UserskillMapper;
import rpg.pojo.BossScene;
import rpg.pojo.Buff;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.service.area.SceneBossRefresh;
import rpg.service.bossscene.BossSceneCache;
import rpg.service.group.GroupCache;
import rpg.service.skill.SkillList;
import rpg.service.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.util.UserService;
import rpg.xmlparse.BuffXmlParse;
import rpg.xmlparse.LevelXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 副本战斗逻辑
 * 
 * @author ljq
 *
 */
@Component
public class AckBossDispatch {

	private static final int YUN_BUFF = 4;
	private static final int FIRST_ACK_MAX_MSG_LENGTH = 3;
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private TaskManage taskManage;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private BuffXmlParse buffXmlParse;
	@Autowired
	private LevelXmlParse levelXmlParse;
	@Value("${id}")
	private int spcid;
	private ReentrantLock lock = new ReentrantLock();

	public void ack(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		// 获取用户技能列表
		Integer id = user.getAreaid();
		String nickname = user.getNickname();
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userskill> list = userskillMapper.selectByExample(example);
		// 获取地图中的怪物
		BossScene bossScene = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
		if (bossScene != null) {
			ArrayList<Monster> monsterList = bossScene.getMonsterList();
			// 第一次攻击
			if (msg.length == FIRST_ACK_MAX_MSG_LENGTH) {
				ackFirst(user, ch, group, msg, id, nickname, list, monsterList, bossScene);
			} else if (msg.length == 1) {
				// 二次及以上攻击
				secondAckAndMore(user, ch, msg, list, bossScene, monsterList);
			} else {
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(MsgResp.ORDER_ERR);
				SendMsg.send(builder.build(), ch);
			}
		}
	}

	/**
	 * 二次及以上攻击
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 * @param list
	 * @param bossScene
	 * @param monsterList
	 */
	public void secondAckAndMore(User user, Channel ch, String[] msg, List<Userskill> list, BossScene bossScene,
			ArrayList<Monster> monsterList) {
		List<Monster> list2 = AllOnlineUser.monsterMp.get(ch.remoteAddress());
		List<Monster> linkedList = AllOnlineUser.monsterMp.get(ch.remoteAddress());
		ConcurrentHashMap<Integer, Long> buffTime2 = user.getBuffStartTime();
		// 找到配置的技能
		if (msg[0].equals(InstructionsType.ESC.getValue())) {
			ChannelUtil.getSessionData(ch).setStatus(1);
			SendMsg.send("成功退出战斗", ch);
		} else if (msg[0].equals(InstructionsType.ACK.getValue())) {
			SendMsg.send("指令错误", ch);
		} else if (buffTime2 != null && buffTime2.get(YUN_BUFF) != null) {
			SendMsg.send("你被打晕了，无法进行攻击", ch);
		} else if (buffTime2 != null && buffTime2.get(BuffType.ADDHP.getValue()) != null) {
			SendMsg.send("你处于吟唱状态，无法进行攻击", ch);
		} else {
			if (msg[0].equals(InstructionsType.SKILL_KEY_1.getValue())
					|| msg[0].equals(InstructionsType.SKILL_KEY_3.getValue())) {
				ackProcess(user, ch, msg, list, bossScene, monsterList, list2, linkedList);
			} else {
				SendMsg.send("指令错误", ch);
			}
		}
	}

	/**
	 * 攻击过程
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 * @param list
	 * @param bossScene
	 * @param monsterList
	 * @param list2
	 * @param linkedList
	 */
	public void ackProcess(User user, Channel ch, String[] msg, List<Userskill> list, BossScene bossScene,
			ArrayList<Monster> monsterList, List<Monster> list2, List<Monster> linkedList) {
		findskill(user, msg);
		for (Userskill userskill : list) {
			String skillId = String.valueOf(userskill.getSkill());
			if (skillId.equals(msg[0])) {
				Skill skill = SkillList.getInstance().getSkillMp().get(msg[0]);
				// 获取当前时间毫秒值
				long millis = System.currentTimeMillis();
				HashMap<String, Long> map = SkillList.getInstance().getCdMpCache().get(user);
				if (map != null) {
					Long lastmillis = map.get(skillId);
					// 蓝量满足
					if (skill.getMp() <= user.getMp()) {
						if (lastmillis != null) {
							// 技能cd满足
							if (millis - lastmillis >= skill.getCd()) {
								// 存储上次使用技能时间
								saveLastUserSkillTime(user, skillId, skill);
								// 判断特殊技能
								if (skill.getId() == spcid) {
									judgeSpecialSkill(user, ch, skill);
									break;
								} else {
									// 更新人物buff
									startAck(user, ch, bossScene, monsterList, list2, linkedList, skill);
								}
								break;
							} else {
								// cd未到
								SendMsg.send("技能冷却中", ch);
							}
						} else {
							// 技能未使用过
							saveLastUserSkillTime(user, skillId, skill);
							// 判断特殊技能
							if (skill.getId() == spcid) {
								judgeSpecialSkill(user, ch, skill);
							} else {
								// 更新人物buff
								startAckSkillNoUse(user, ch, bossScene, monsterList, list2, linkedList, skill);
							}
						}
						break;
					} else {
						// 蓝量不足
						SendMsg.send("蓝量不足，请充值", ch);
					}
				} else {
					SendMsg.send("指令错误", ch);
				}
			}
		}
	}

	/**
	 * 开始攻击技能没被使用过
	 * 
	 * @param user
	 * @param ch
	 * @param bossScene
	 * @param monsterList
	 * @param list2
	 * @param linkedList
	 * @param skill
	 * @return
	 */
	public void startAckSkillNoUse(User user, Channel ch, BossScene bossScene, ArrayList<Monster> monsterList,
			List<Monster> list2, List<Monster> linkedList, Skill skill) {
		for (int index = 0; index < list2.size(); index++) {
			Monster monster = list2.get(index);
			if (skill.getId() != 9) {
				userService.updateUserBuff(user, skill);
			}
			// 更新怪物buff
			userService.updateMonsterBuff(user, skill, monster);
			// 判断装备是否还有耐久度
			UserAttribute attribute = user.getUserAttribute();
			List<Userzb> list1 = user.getUserzbs();
			judgeZbNjd(attribute, list1);
			if (skill.getId() == 7) {
				SendMsg.send(
						skill.getName() + "对" + monster.getName() + "进行攻击-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(),
						ch);
				break;
			}
			int ack = attribute.getAck();
			int hurt = skill.getHurt() + ack;
			int monsterHp = monster.getHp() - hurt;
			monster.setHp(monsterHp);
			if (monsterHp <= 0) {
				if (linkedList.size() != 1) {
					index = monsterKilledButNotNull(user, ch, linkedList, index, monster);
				} else {
					// 产生新的boss
					if (bossScene != null) {
						if (bossScene.getLayer() - 1 > bossScene.getId()) {
							produceNewBoss(user, bossScene, monsterList, linkedList);
						} else {
							bossScene = clearanceHandleWithSkillSecond(user, ch, bossScene, monster, list1);
						}
					}
				}
			} else {
				useSkillHandle(user, ch, skill, index, monster, list1, hurt);
			}
			if (skill.getId() != 6) {
				break;
			}
		}
	}

	/**
	 * 开始攻击
	 * 
	 * @param user
	 * @param ch
	 * @param bossScene
	 * @param monsterList
	 * @param list2
	 * @param linkedList
	 * @param skill
	 * @return
	 */
	public void startAck(User user, Channel ch, BossScene bossScene, ArrayList<Monster> monsterList,
			List<Monster> list2, List<Monster> linkedList, Skill skill) {
		for (int index = 0; index < list2.size(); index++) {
			Monster monster = list2.get(index);
			if (skill.getId() != 9) {
				userService.updateUserBuff(user, skill);
			}
			// 更新怪物buff
			userService.updateMonsterBuff(user, skill, monster);
			// 判断装备是否还有耐久度
			UserAttribute attribute = user.getUserAttribute();
			List<Userzb> list1 = user.getUserzbs();
			judgeZbNjd(attribute, list1);
			if (skill.getId() == 7) {
				SendMsg.send(
						skill.getName() + "对" + monster.getName() + "进行攻击-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(),
						ch);
				break;
			}
			int ack = attribute.getAck();
			int hurt = skill.getHurt() + ack;
			int monsterHp = monster.getHp() - hurt;
			monster.setHp(monsterHp);
			if (monsterHp <= 0) {
				if (linkedList.size() != 1) {
					index = monsterKilledButNotNull(user, ch, linkedList, index, monster);
				} else {
					// 产生新的boss
					if (bossScene != null) {
						if (bossScene.getLayer() - 1 > bossScene.getId()) {
							produceNewBoss(user, bossScene, monsterList, linkedList);
						} else {
							bossScene = clearanceHandle(user, ch, bossScene, monster, list1);
						}
					}
				}
			} else {
				useSkillHandle(user, ch, skill, index, monster, list1, hurt);
			}
			if (skill.getId() != 6) {
				break;
			}
		}
	}

	/**
	 * 找到具体的技能
	 * 
	 * @param user
	 * @param msg
	 */
	public void findskill(User user, String[] msg) {
		if (msg[0].equals(InstructionsType.SKILL_KEY_3.getValue())) {
			String s = rpgUtil.skillChange(msg[0], user);
			msg[0] = s;
		}
	}

	/**
	 * 通关处理
	 * 
	 * @param user
	 * @param ch
	 * @param bossScene
	 * @param monster
	 * @param list1
	 * @return
	 */
	public BossScene clearanceHandleWithSkillSecond(User user, Channel ch, BossScene bossScene, Monster monster,
			List<Userzb> list1) {
		try {
			lock.lock();
			if (bossScene != null) {
				SendMsg.send("boss已全被消灭，退出副本", ch);
				rpgUtil.ackEnd(user, ch, monster);
				rpgUtil.bossEndAward(user, ch, bossScene);
				taskManage.checkTaskComplete(user, bossScene.getSceneid());
				Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
				if (group2 != null) {
					List<User> list3 = group2.getList();
					for (User user3 : list3) {
						Channel channel1 = AllOnlineUser.userchMap.get(user3);
						if (channel1 != ch) {
							SendMsg.send(user.getNickname() + "消灭了" + monster.getName() + "-你已通关，退出副本" + "\n",
									channel1);
							rpgUtil.ackEnd(user3, channel1, monster);
							taskManage.checkTaskComplete(user3, bossScene.getSceneid());
						}
						ChannelUtil.getSessionData(channel1).setStatus(1);
					}
				}
				monster.setAliveFlag(false);
				ChannelUtil.getSessionData(ch).setStatus(1);
				// 损耗装备耐久度
				for (Userzb userzb : list1) {
					userzb.setNjd(userzb.getNjd() - 5);
					if(userzb.getNjd()<0) {
						userzb.setNjd(0);
					}
				}
				removeUserlist(user, bossScene);
				// 回收boss场景
				bossScene = null;
				BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
			}
		} finally {
			lock.unlock();
		}
		return bossScene;
	}

	/**
	 * 使用技能处理
	 * 
	 * @param user
	 * @param ch
	 * @param skill
	 * @param index
	 * @param monster
	 * @param list1
	 * @param hurt
	 */
	public void useSkillHandle(User user, Channel ch, Skill skill, int index, Monster monster, List<Userzb> list1,
			int hurt) {
		if (index == 0) {
			SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(), ch);
		}
		SendMsg.send("攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp(), ch);
		// 损耗装备耐久度
		for (Userzb userzb : list1) {
			userzb.setNjd(userzb.getNjd() - 5);
			if(userzb.getNjd()<0) {
				userzb.setNjd(0);
			}
		}
	}

	/**
	 * 通关处理
	 * 
	 * @param user
	 * @param ch
	 * @param bossScene
	 * @param monster
	 * @param list1
	 * @return
	 */
	public BossScene clearanceHandle(User user, Channel ch, BossScene bossScene, Monster monster, List<Userzb> list1) {
		try {
			lock.lock();
			if (bossScene != null) {
				SendMsg.send("boss已全被消灭，退出副本", ch);
				Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
				rpgUtil.ackEnd(user, ch, monster);
				rpgUtil.bossEndAward(user, ch, bossScene);
				taskManage.checkTaskComplete(user, bossScene.getSceneid());
				if (group2 != null) {
					List<User> list3 = group2.getList();
					for (User user3 : list3) {
						Channel channel1 = AllOnlineUser.userchMap.get(user3);
						if (channel1 != ch) {
							SendMsg.send(user.getNickname() + "消灭了" + monster.getName() + "-你已通关，退出副本", channel1);
							rpgUtil.ackEnd(user3, channel1, monster);
							taskManage.checkTaskComplete(user3, bossScene.getSceneid());
						}
						ChannelUtil.getSessionData(channel1).setStatus(1);
					}
				}
				monster.setAliveFlag(false);
				ChannelUtil.getSessionData(ch).setStatus(1);
				// 损耗装备耐久度
				for (Userzb userzb : list1) {
					userzb.setNjd(userzb.getNjd() - 5);
					if(userzb.getNjd()<0) {
						userzb.setNjd(0);
					}
				}
				removeUserlist(user, bossScene);
				// 回收boss场景
				bossScene = null;
				BossSceneCache.getInstance().getUserBossCache().remove(user.getGroupId());
			}
		} finally {
			lock.unlock();
		}
		return bossScene;
	}

	/**
	 * 产生新Boss
	 * 
	 * @param user
	 * @param bossScene
	 * @param monsterList
	 * @param linkedList
	 * @return
	 */
	public void produceNewBoss(User user, BossScene bossScene, ArrayList<Monster> monsterList,
			List<Monster> linkedList) {
		bossScene.setId(bossScene.getId() + 1);
		Map<Integer, Integer> struct = bossScene.getStruct();
		Integer csid1 = struct.get(bossScene.getId());
		Integer csid2 = struct.get(bossScene.getId() - 1);
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		// 将怪物指定用户
		if (group2 != null) {
			List<User> list3 = group2.getList();
			for (User user3 : list3) {
				LinkedList<Monster> linkedList2 = new LinkedList<>();
				for (int i = csid2 + 1; i <= csid1; i++) {
					Monster monster2 = monsterList.get(i);
					if (group2 != null) {
						List<User> list31 = group2.getList();
						ArrayList<User> userList1 = new ArrayList<>();
						for (User user31 : list31) {
							userList1.add(user31);
						}
						monster2.setUserList(userList1);
					}
					linkedList2.add(monster2);
				}
				linkedList = linkedList2;
				Channel channel1 = AllOnlineUser.userchMap.get(user3);
				AllOnlineUser.monsterMp.put(channel1.remoteAddress(), linkedList);
				String word1 = "";
				for (Monster monster21 : linkedList) {
					word1 += monster21.getName() + "-血量:" + monster21.getHp() + "攻击力:" + monster21.getAck() + "\n";
				}
				SendMsg.send("boss已被消灭,新的Boss出现\n" + word1, channel1);
			}
		}
	}

	/**
	 * 怪物被杀但不是空的
	 * 
	 * @param user
	 * @param ch
	 * @param linkedList
	 * @param index
	 * @param monster
	 * @return
	 */
	public int monsterKilledButNotNull(User user, Channel ch, List<Monster> linkedList, int index, Monster monster) {
		linkedList.remove(monster);
		SendMsg.send("消灭了" + monster.getName(), ch);
		monster = null;
		// 更新怪物列表
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list3 = group2.getList();
			for (User user3 : list3) {
				Channel channel = AllOnlineUser.userchMap.get(user3);
				AllOnlineUser.monsterMp.put(channel.remoteAddress(), linkedList);
			}
		}
		index--;
		return index;
	}

	/**
	 * 判断装备耐久度
	 * 
	 * @param attribute
	 * @param list1
	 */
	public void judgeZbNjd(UserAttribute attribute, List<Userzb> list1) {
		for (Userzb userzb : list1) {
			if (userzb.getNjd() <= 0) {
				Zb zb = zbXmlParse.getZbById(userzb.getZbid());
				if (zb != null && attribute != null) {
					attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
					userzb.setIsuse(0);
				}
			}
		}
	}

	/**
	 * 判断特殊技能
	 * 
	 * @param user
	 * @param ch
	 * @param skill
	 */
	public void judgeSpecialSkill(User user, Channel ch, Skill skill) {
		userService.updateUserBuff(user, skill);
		SendMsg.send(skill.getName() + "-技能开始吟唱...........", ch);
	}

	/**
	 * 存储上次使用技能时间
	 * 
	 * @param user
	 * @param skillId
	 * @param skill
	 */
	public void saveLastUserSkillTime(User user, String skillId, Skill skill) {
		long currentTimeMillis = System.currentTimeMillis();
		HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
		curSkill.put(skillId, currentTimeMillis);
		SkillList.getInstance().getCdMpCache().put(user, curSkill);
		user.getAndSetMp(user, user.getMp() - skill.getMp());
	}

	/**
	 * 改变攻击目标
	 * 
	 * @param ch
	 * @param msg
	 * @param user
	 */
	public void changeAckTarget(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if(msg.length==MsgSize.MAX_MSG_SIZE_2.getValue()) {
		List<Monster> list2 = AllOnlineUser.monsterMp.get(ch.remoteAddress());
		int index = -1;
		for (int i = 0; i < list2.size(); i++) {
			Monster monster = list2.get(i);
			if (monster.getName().equals(msg[1])) {
				if (monster.getHp() > 0) {
					index = i;
					break;
				} else {
					SendMsg.send("转换攻击目标失败-----怪物已死亡", ch);
				}
			}
		}
		if (index != -1) {
			Monster monster = list2.get(index);
			list2.remove(monster);
			list2.add(0, monster);
			if (user.getRoletype().equals(RoleType.ZHAOHUANSHI.getValue())) {
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
			SendMsg.send("转换攻击目标-" + monster.getName() + "-成功", ch);
		} else {
			SendMsg.send("指令错误", ch);
		}
	} else {
		SendMsg.send("指令錯誤", ch);
	}
	}

	/**
	 * 移除怪物列表
	 * 
	 * @param user
	 * @param bossScene
	 */
	public static void removeUserlist(User user, BossScene bossScene) {
		ArrayList<Monster> monsterList1 = bossScene.getMonsterList();
		// 找到场景内怪物
		for (int i = 0; i < monsterList1.size(); i++) {
			Monster monster1 = monsterList1.get(i);
			if (monster1 != null) {
				List<User> userList = monster1.getUserList();
				userList.remove(user);
			}
		}
	}

	/**
	 * 首次攻击
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 * @param id
	 * @param nickname
	 * @param list
	 * @param monsterList
	 * @param bossScene
	 */
	private void ackFirst(User user, Channel ch, ChannelGroup group, String[] msg, Integer id, String nickname,
			List<Userskill> list, ArrayList<Monster> monsterList, BossScene bossScene) {
		// 找到场景内怪物
		int bossId = bossScene.getId();
		Monster monster = monsterList.get(bossId);
		if (msg[1].equals(monster.getName())) {
			if (monster.getHp() > 0) {
				monster.setCountAcker(monster.getCountAcker() + 1);
				// 添加怪物的攻击者
				addMonsterAcker(user, monster);
				// 将怪物指定到用户
				addMonsterToUser(group, monster);
				// 找到配置的技能
				for (Userskill userskill : list) {
					String skillId = String.valueOf(userskill.getSkill());
					if (skillId.equals(msg[2])) {
						Skill skill = SkillList.getInstance().getSkillMp().get(msg[2]);
						// 蓝量足够
						if (skill.getMp() <= user.getMp()) {
							saveLastUserSkillTime(user, skillId, skill);
							// 更新人物buff
							if (skill.getId() != 9) {
								userService.updateUserBuff(user, skill);
							}
							// 更新怪物buff
							userService.updateMonsterBuff(user, skill, monster);
							SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(), ch);
							if (RoleType.ZHAOHUANSHI.getValue() == user.getRoletype()) {
								Buff buff = buffXmlParse.getBuffByid(BuffType.PETACK.getValue());
								userService.updateMonsterBuff(buff, monster);
							}
							// 判断装备是否还有耐久度
							UserAttribute attribute = user.getUserAttribute();
							if (attribute != null) {
								List<Userzb> list1 = user.getUserzbs();
								judgeZbNjd(attribute, list1);
								int ack = attribute.getAck();
								int hurt = skill.getHurt();
								SendMsg.send("开始攻击" + monster.getName(),ch);
								for (Userzb userzb : list1) {
									userzb.setNjd(userzb.getNjd() - 5);
									if(userzb.getNjd()<0) {
										userzb.setNjd(0);
									}
								}
								// 怪物攻击线程
								if (monster.getCountAcker() == 1) {
									bossScene.setStartTime(System.currentTimeMillis());
									Group firstGroup = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
									ThreadResource.monsterThreadPool.execute(new SceneBossRefresh(userService, user,
											bossScene, ch, firstGroup, attribute,taskManage,rpgUtil,buffXmlParse,levelXmlParse));
									break;
								}
							}
						}
						// 蓝量不足
						else {
							SendMsg.send("蓝量不足，请充值", ch);
						}
					}

				}
			} else {
				ChannelUtil.getSessionData(ch).setStatus(1);
				SendMsg.send("怪物不存在", ch);
			}
		} else {
			ChannelUtil.getSessionData(ch).setStatus(1);
			SendMsg.send("怪物不存在", ch);
		}
	}

	/**
	 * 将怪物指定到用户
	 * 
	 * @param group
	 * @param monster
	 */
	public void addMonsterToUser(ChannelGroup group, Monster monster) {
		for (Channel channel : group) {
			List<Monster> list2 = new ArrayList<>();
			list2.add(monster);
			AllOnlineUser.monsterMp.put(channel.remoteAddress(), list2);
		}
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