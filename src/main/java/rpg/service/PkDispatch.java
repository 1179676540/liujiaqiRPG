package rpg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.core.AllOnlineUser;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.Userzb;
import rpg.service.skill.SkillList;
import rpg.service.task.TaskManage;
import rpg.util.SendMsg;
import rpg.util.UserService;

/**
 * pk处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class PkDispatch {

	@Autowired
	private UserService userService;
	@Autowired
	private TaskManage taskManage;

	public void pk(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		List<Userskill> skillList = userService.getSkillList(user);
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if (user2.getAreaid().equals(user.getAreaid())) {
							if (user2.getAreaid() != 1) {
								for (Userskill userskill : skillList) {
									String skillId = String.valueOf(userskill.getSkill());
									if (skillId.equals(msg[2])) {
										Skill skill = SkillList.getInstance().getSkillMp().get(msg[2]);
										// 蓝量满足
										if (skill.getMp() <= user.getMp()) {
											// 技能cd满足
											if (userService.cdStatus(user, skillId, skill) == 1) {
												userSkillAck(user, ch, user2, skillId, skill);
											} else if (userService.cdStatus(user, skillId, skill) == 2) {
												// cd未到
												SendMsg.send("技能冷却中", ch);
											} else {
												// 技能未被使用
												userSkillAck(user, ch, user2, skillId, skill);
											}
										} else {
											SendMsg.send("蓝量不足，请充值", ch);
										}
									}
								}
							} else {
								SendMsg.send("该场景不允许pk", ch);
							}
						} else {
							SendMsg.send("玩家不在当前场景", ch);
						}
						break;
					}
				}
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 使用技能攻击
	 * 
	 * @param user
	 * @param ch
	 * @param user2
	 * @param skillId
	 * @param skill
	 */
	public void userSkillAck(User user, Channel ch, User user2, String skillId, Skill skill) {
		// 存储上次使用技能时间
		userService.saveLastCdTime(user, skillId);
		// 消耗蓝量
		user.setMp(user.getMp() - skill.getMp());
		// 判断装备是否还有耐久度
		List<Userzb> list1 = user.getUserzbs();
		UserAttribute attribute = user.getUserAttribute();
		userService.checkNjd(user, attribute);
		int ack = attribute.getAck();
		int hurt = skill.getHurt() + ack;
		int userHp = user2.getHp() - hurt;
		user2.setHp(userHp);
		Channel channel = AllOnlineUser.userchMap.get(user2);
		if (userHp <= 0) {
			SendMsg.send("你已将" + user2.getNickname() + "打死！", ch);
			taskManage.checkTaskCompleteBytaskid(user, 10);
			user2.setHp(1000);
			user2.setAreaid(1);
			SendMsg.send("你已被" + user.getNickname() + "打死!" + "-你被传送至起始之地", channel);
			// 损耗装备耐久度
			for (Userzb userzb : list1) {
				userzb.setNjd(userzb.getNjd() - 5);
			}
		} else {
			SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余MP" + user.getMp() + "\n" + "攻击了"
					+ user2.getNickname() + "-造成" + hurt + "点伤害-对方血量" + user2.getHp(), ch);
			SendMsg.send(
					"你受到了" + user.getNickname() + "的技能" + skill.getName() + "-造成" + hurt + "点伤害-你的血量" + user2.getHp(),
					channel);
			// 损耗装备耐久度
			for (Userzb userzb : list1) {
				userzb.setNjd(userzb.getNjd() - 5);
			}
		}
	}
}