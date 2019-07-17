package rpg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.data.dao.UserkeyMapper;
import rpg.data.dao.UserskillMapper;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.Userkey;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.service.skill.SkillList;
import rpg.util.SendMsg;

/**
 * 技能相关功能
 * 
 * @author ljq
 *
 */
@Component
public class SkillDispatch {

	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserkeyMapper userkeyMapper;

	/**
	 * 展示技能列表
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showSkill(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			UserskillExample example = new UserskillExample();
			Criteria criteria = example.createCriteria();
			criteria.andUsernameEqualTo(user.getNickname());
			List<Userskill> list = userskillMapper.selectByExample(example);
			String word = "---技能列表---\n";
			for (Userskill userskill : list) {
				String skillId = String.valueOf(userskill.getSkill());
				Skill skill = SkillList.getInstance().getSkillMp().get(skillId);
				if (skill.getId() != 1) {
					word += "技能id--" + skill.getId() + "--技能名称--" + skill.getName() + "--技能cd--" + skill.getCd()
							+ "--技能耗蓝--" + skill.getMp() + "\n";
				}
			}
			word += "---目前配置技能---\n";
			Userkey userkey = userkeyMapper.selectByPrimaryKey(user.getNickname());
			Skill skill = SkillList.getInstance().getSkillMp().get(String.valueOf(userkey.getSkillid()));
			word += "技能名称--" + skill.getName() + "\n";
			SendMsg.send(word, ch);
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 改变技能
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void changeSkill(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			UserskillExample example = new UserskillExample();
			Criteria criteria = example.createCriteria();
			criteria.andUsernameEqualTo(user.getNickname());
			List<Userskill> list = userskillMapper.selectByExample(example);
			for (Userskill userskill : list) {
				if (Integer.valueOf(userskill.getSkill()) != 1) {
					if (msg[1].equals(String.valueOf(userskill.getSkill()))) {
						Userkey userkey = userkeyMapper.selectByPrimaryKey(user.getNickname());
						userkey.setSkillid(Integer.valueOf(msg[1]));
						userkeyMapper.updateByPrimaryKey(userkey);
						Skill skill = SkillList.getInstance().getSkillMp().get(String.valueOf(userkey.getSkillid()));
						SendMsg.send("更改技能按钮为-" + skill.getName() + "-技能成功", ch);
					}
				}
			}
		}
	}
}
