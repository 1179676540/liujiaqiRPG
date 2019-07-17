package rpg.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.configure.RoleType;
import rpg.core.ThreadResource;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserkeyMapper;
import rpg.data.dao.UserlevelMapper;
import rpg.pojo.BossScene;
import rpg.pojo.Level;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.Userkey;
import rpg.pojo.Userlevel;
import rpg.pojo.UserlevelExample;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.pojo.UserlevelExample.Criteria;
import rpg.service.task.TaskManage;
import rpg.xmlparse.LevelXmlParse;
import rpg.xmlparse.YaopinXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 工具类
 * 
 * @author ljq
 *
 */
@Component
public class RpgUtil {
	
	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private TaskManage taskManage;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private LevelXmlParse levelXmlParse;
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserlevelMapper userlevelMapper;
	@Autowired
	private UserkeyMapper userkeyMapper;

	/**
	 * 装备入包
	 * 
	 * @param user
	 */
	public  void putZb(User user, Zb zb) {
		List<Userbag> list = user.getUserbags();
		Userbag userbag = new Userbag();
		userbag.setId(UUID.randomUUID().toString());
		userbag.setUsername(user.getNickname());
		userbag.setGid(zb.getId());
		userbag.setNumber(1);
		userbag.setNjd(zb.getNjd());
		userbag.setIsadd(0);
		userbag.setEnhance(1);
		list.add(userbag);
		userbagMapper.insert(userbag);
	}

	/**
	 * 装备入包带耐久度和等级
	 * 
	 * @param sendUser
	 * @param zb
	 * @param njd
	 * @param enhance 
	 */
	public  void putZbWithNJD(User sendUser, Zb zb, Integer njd, Integer enhance) {
		List<Userbag> list = sendUser.getUserbags();
		Userbag userbag = new Userbag();
		userbag.setId(UUID.randomUUID().toString());
		userbag.setUsername(sendUser.getNickname());
		userbag.setGid(zb.getId());
		userbag.setNumber(1);
		userbag.setNjd(njd);
		userbag.setIsadd(0);
		userbag.setEnhance(enhance);
		list.add(userbag);
		userbagMapper.insert(userbag);
	}

	/**
	 * 药品入包
	 * 
	 * @param user
	 * @param zb
	 */
	public  void putYaopin(User user, Yaopin yaopin) {
		List<Userbag> list = user.getUserbags();
		boolean flag = false;
		for (Userbag userbag1 : list) {
			if (yaopin.getId() == userbag1.getGid()) {
				userbag1.setNumber(userbag1.getNumber() + 1);
				userbagMapper.updateByPrimaryKeySelective(userbag1);
				flag = true;
				break;
			}
		}
		if (!flag) {
			Userbag userbag = new Userbag();
			userbag.setId(UUID.randomUUID().toString());
			userbag.setUsername(user.getNickname());
			userbag.setGid(yaopin.getId());
			userbag.setNumber(1);
			userbag.setNjd(0);
			userbag.setIsadd(1);
			userbag.setEnhance(0);
			list.add(userbag);
			userbagMapper.insert(userbag);
		}
	}

	/**
	 * 药品入包带数量
	 * 
	 * @param user
	 * @param zb
	 */
	public  void putYaopin(User user, Yaopin yaopin, int num) {
		List<Userbag> list = user.getUserbags();
		boolean flag = false;
		for (Userbag userbag1 : list) {
			if (yaopin.getId() == userbag1.getGid()) {
				userbag1.setNumber(userbag1.getNumber() + num);
				userbagMapper.updateByPrimaryKeySelective(userbag1);
				flag = true;
				break;
			}
		}
		if (!flag) {
			Userbag userbag = new Userbag();
			userbag.setId(UUID.randomUUID().toString());
			userbag.setUsername(user.getNickname());
			userbag.setGid(yaopin.getId());
			userbag.setNumber(1);
			userbag.setNjd(0);
			userbag.setIsadd(1);
			userbag.setEnhance(0);
			list.add(userbag);
			userbagMapper.insert(userbag);
		}
	}

	/**
	 * 战斗结算奖励
	 * 
	 * @param user
	 * @param ch
	 */
	public  void ackEnd(User user, Channel ch, Monster monster) {
		StringBuilder string = new StringBuilder();
		int exp = monster.getExp();
		int checkLevel = checkLevel(user, exp, string);
		List<Integer> awardList = monster.getAwardList();
		Random random = new Random();
		int randomId = random.nextInt(awardList.size());
		user.setMoney(user.getMoney() + monster.getMoney());
		userMapper.updateByPrimaryKeySelective(user);
		// 奖励物品id
		Integer id = awardList.get(randomId);
		Zb zb = zbXmlParse.getZbById(id);
		Yaopin yaopin = yaopinXmlParse.getYaopinById(id);
		if (zb != null) {
			putZb(user, zb);
			string.append("获得金钱：" + monster.getMoney() + "获得装备：" + zb.getName() + "\n");
		} else if (yaopin != null) {
			putYaopin(user, yaopin);
			string.append("获得金钱：" + monster.getMoney() + "获得药品：" + yaopin.getName() + "\n");
		}
		String s = "" + string;
		SendMsg.send(s, ch);
		taskManage.checkMoneyTaskCompleteBytaskid(user, 11);
		if (checkLevel == 1) {
			taskManage.checkTaskCompleteBytaskid(user, 2);
		}
	}

	/**
	 * 检查等级
	 * 
	 * @param user
	 * @param exp
	 * @param string
	 * @return
	 */
	private  int checkLevel(User user, int exp, StringBuilder string) {
		Userlevel userLevel = new Userlevel();
		UserlevelExample userlevelExample = new UserlevelExample();
		Criteria createCriteria = userlevelExample.createCriteria();
		createCriteria.andUsernameEqualTo(user.getNickname());
		int userlevel = user.getLevel();
		int userexp = user.getExp();
		Level level = levelXmlParse.getLevelById(userlevel + 1);
		if (userexp + exp >= level.getExpl()) {
			userexp = userexp + exp - level.getExpl();
			user.setExp(userexp);
			user.setLevel(userlevel + 1);
			user.setHp(user.getHp()+level.getHp());
			UserAttribute userAttribute = user.getUserAttribute();
			userAttribute.setAck(userAttribute.getAck()+level.getAck());
			userAttribute.setDef(userAttribute.getDef()+level.getDef());
			userLevel.setExp(userexp);
			userLevel.setLevel(user.getLevel());
			userLevel.setAck(userAttribute.getAck());
			userLevel.setDef(userAttribute.getDef());
			userlevelMapper.updateByExampleSelective(userLevel, userlevelExample);
			userMapper.updateByPrimaryKey(user);
			string.append("恭喜你升级---当前等级" + user.getLevel() + "---当前经验" + user.getExp() + "/" + level.getExpr() + "\n");
			return 1;
		} else {
			user.setExp(userexp + exp);
			userLevel.setExp(user.getExp());
			userlevelMapper.updateByExampleSelective(userLevel, userlevelExample);
			string.append("获得经验" + exp + "\n");
			return 0;
		}
	}
	
	/**
	 * 改变技能
	 * @param string
	 * @param user
	 * @return
	 */
	public  String skillChange(String string, User user) {
		Userkey userkey = userkeyMapper.selectByPrimaryKey(user.getNickname());
		string=String.valueOf(userkey.getSkillid());
		return string;
	}
	
	/**
	 *boss最后一击奖励 
	 * @param user
	 * @param ch
	 * @param bossScene 
	 */
	public  void bossEndAward(User user, Channel ch, BossScene bossScene) {
		Zb zb = zbXmlParse.getZbById(105);
		putZb(user, zb);
		SendMsg.send("恭喜你获得最后一击奖励----"+zb.getName(), ch);
	}
}
