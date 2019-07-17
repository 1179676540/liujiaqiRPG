package rpg.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.configure.BuffType;
import rpg.configure.MsgSize;
import rpg.core.AllOnlineUser;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.data.dao.UserskillMapper;
import rpg.pojo.Buff;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.service.skill.SkillList;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.xmlparse.BuffXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 用户服务
 * 
 * @author ljq
 *
 */
@Component
public class UserService {

	private static final String SPECIAL = "4";
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private BuffXmlParse buffXmlParse;

	/**
	 * 获取技能列表
	 * 
	 * @param user
	 * @return
	 */
	public List<Userskill> getSkillList(User user) {
		Integer id = user.getAreaid();
		String nickname = user.getNickname();
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userskill> list = userskillMapper.selectByExample(example);
		return list;
	}

	/**
	 * cd状态，0-技能未被使用过，1-技能cd已到，2-技能cd未到。
	 * 
	 * @param user
	 * @param skillId
	 * @param skill
	 * @return
	 */
	public int cdStatus(User user, String skillId, Skill skill) {
		// 获取当前时间毫秒值
		long millis = System.currentTimeMillis();
		HashMap<String, Long> map = SkillList.getInstance().getCdMpCache().get(user);
		if (map != null) {
			Long lastmillis = map.get(skillId);
			if (lastmillis != null) {
				if (millis - lastmillis >= skill.getCd()) {
					return 1;
				} else {
					return 2;
				}
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * 存储上次使用技能时间
	 * 
	 * @param user
	 * @param skillId
	 */
	public void saveLastCdTime(User user, String skillId) {
		long currentTimeMillis = System.currentTimeMillis();
		HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
		curSkill.put(skillId, currentTimeMillis);
		SkillList.getInstance().getCdMpCache().put(user, curSkill);
	}

	/**
	 * 检查耐久度，并重新计算攻击力
	 * 
	 * @param user
	 * @param attribute
	 */
	public void checkNjd(User user, UserAttribute attribute) {
		List<Userzb> list1 = user.getUserzbs();
		for (Userzb userzb : list1) {
			if (userzb.getNjd() <= 0) {
//				Zb zb = IOsession.zbMp.get(userzb.getZbid());
				Zb zb = zbXmlParse.getZbById(userzb.getZbid());
				if (zb != null && attribute != null) {
					attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
					userzb.setIsuse(0);
				}
			}
		}
	}

	/**
	 * 更新人物buff
	 * 
	 * @param user
	 * @param skill
	 */
	public void updateUserBuff(User user, Skill skill) {
		// 找到所产生的Buff
		Buff buff = buffXmlParse.getBuffByid(Integer.valueOf(skill.getEffect()));
//		ConcurrentHashMap<Integer, Long> buffTime2 = IOsession.buffTimeMp.get(user);
		ConcurrentHashMap<Integer, Long> buffTime2 = user.getBuffStartTime();
		if (buff != null) {
			// 判断是否有玩家存在吟唱
			if (buffTime2 != null && buffTime2.get(BuffType.ADDHP.getValue()) != null) {
				if(SPECIAL.equals(skill.getEffect())) {
				Long long2 = buffTime2.get(BuffType.ADDHP.getValue());
				long2 = null;
				buffTime2.remove(BuffType.ADDHP.getValue());
				Channel channel = AllOnlineUser.userchMap.get(user);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData("吟唱被眩晕打破.........");
				SendMsg.send(builder.build(), channel);
				}
			}
			// 存储上次使用buff时间
			long currentTimeMillis = System.currentTimeMillis();
			if (user.getBuffStartTime() == null) {
				ConcurrentHashMap<Integer, Long> buffMap = new ConcurrentHashMap<Integer, Long>(500);
				buffMap.put(buff.getId(), currentTimeMillis);
				user.setBuffStartTime(buffMap);
			} else {
				ConcurrentHashMap<Integer, Long> buffMap = user.getBuffStartTime();
				buffMap.put(buff.getId(), currentTimeMillis);
			}
		}
	}

	/**
	 * 更新怪物buff
	 * 
	 * @param user
	 * @param skill
	 * @param monster
	 */
	public void updateMonsterBuff(User user, Skill skill, Monster monster) {
		// 找到所产生的Buff
		Buff buff = buffXmlParse.getBuffByid(Integer.valueOf(skill.getEffect()));
		if (buff != null) {
			// 存储上次使用buff时间
			long currentTimeMillis = System.currentTimeMillis();
			if (monster.getMonsterBuffStartTime() == null) {
				HashMap<Integer, Long> buffMap = new HashMap<Integer, Long>(500);
				buffMap.put(buff.getId(), currentTimeMillis);
				monster.setMonsterBuffStartTime(buffMap);
			} else {
				HashMap<Integer, Long> buffMap = monster.getMonsterBuffStartTime();
				buffMap.put(buff.getId(), currentTimeMillis);
			}
		}
	}

	/**
	 * 更新怪物buff
	 * @param buff
	 * @param monster
	 */
	public void updateMonsterBuff(Buff buff, Monster monster) {
		if (buff != null) {
			// 存储上次使用buff时间
			long currentTimeMillis = System.currentTimeMillis();
			if (monster.getMonsterBuffStartTime() == null) {
				HashMap<Integer, Long> buffMap = new HashMap<Integer, Long>(500);
				buffMap.put(buff.getId(), currentTimeMillis);
				monster.setMonsterBuffStartTime(buffMap);
			} else {
				HashMap<Integer, Long> buffMap = monster.getMonsterBuffStartTime();
				buffMap.put(buff.getId(), currentTimeMillis);
			}
		}
	}

	/**
	 * 检查怪物Buff
	 * 
	 * @param monster
	 * @param ch
	 */
	public String checkMonsterBuff(Monster monster, Channel ch) {
		HashMap<Integer,Long> buffTime = monster.getMonsterBuffStartTime();
		String msg = "";
		if (buffTime != null) {
			for (Entry<Integer, Long> entry : buffTime.entrySet()) {
				// 通过buffID找到具体的buff
				Integer buffId = entry.getKey();
				Buff buff = buffXmlParse.getBuffByid(buffId);
				// 获取使用Buff的时间
				Long lastTime = entry.getValue();
				long currentTimeMillis = System.currentTimeMillis();
				if (currentTimeMillis - lastTime < buff.getLastedTime()) {
					switch (buff.getId()) {
					case 2:
						int monHp = monster.getHp() - buff.getMp();
						if (monHp <= 0) {
							monHp = 0;
							monster.setDeadType(1);
						}
						monster.setHp(monHp);
						msg += monster.getName() + "受到" + buff.getName() + "伤害:" + buff.getMp() + "怪物血量剩余"
								+ monster.getHp();
						break;
					case 5:
						int monHp1 = monster.getHp() - buff.getMp();
						if (monHp1 <= 0) {
							monHp1 = 0;
							monster.setDeadType(1);
						}
						monster.setHp(monHp1);
						msg += monster.getName() + "受到" + buff.getName() + "伤害:" + buff.getMp() + "怪物血量剩余"
								+ monster.getHp();
						break;
					default:
						break;
					}
				}
			}
			return msg;
		}
		return msg;
	}
}
