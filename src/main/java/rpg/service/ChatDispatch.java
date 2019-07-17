package rpg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.core.AllOnlineUser;
import rpg.pojo.EmailRpg;
import rpg.pojo.Group;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.service.email.EmailCache;
import rpg.service.group.GroupCache;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.YaopinXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 聊天功能
 * 
 * @author ljq
 *
 */
@Component
public class ChatDispatch {

	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;

	/**
	 * 全服聊天
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg1
	 */
	public void chatAll(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			for (Channel channel : group) {
				SendMsg.send("全服喇叭----" + user.getNickname() + ":" + msg[1], channel);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 组队聊天
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg1
	 */
	public void groupChat(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
			if (group2 != null) {
				List<User> list = group2.getList();
				for (User user2 : list) {
					Channel channel = AllOnlineUser.userchMap.get(user2);
					SendMsg.send("队伍----" + user.getNickname() + ":" + msg[1], channel);
				}
			} else {
				SendMsg.send("请先组队吧", ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 私聊
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg1
	 */
	public void chat(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[1].equals(user2.getNickname())) {
						SendMsg.send(user.getNickname() + ":" + msg[2], ch);
						Channel channel = AllOnlineUser.userchMap.get(user2);
						SendMsg.send(user.getNickname() + ":" + msg[2], channel);
					}
				}
			}
		} else {
			SendMsg.send("错误指令", ch);
		}
	}

	/**
	 * 发送和提取邮件
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg1
	 */
	public void email(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			if (EmailCache.getInstance().getAlluserEmailCache().get(msg[1]) != null) {
				List<Userbag> userbagList = user.getUserbags();
				for (Userbag wp : userbagList) {
					// 找到物品
					if (msg[2].equals(wp.getId())) {
						EmailRpg emailRpg = new EmailRpg();
						String emailRpgId = UUID.randomUUID().toString();
						emailRpg.setId(emailRpgId);
						emailRpg.setFujian(wp);
						emailRpg.setUser(user);
						// 移除物品
						Yaopin yaopin = yaopinXmlParse.getYaopinById(wp.getGid());
						if (yaopin != null) {
							wp.setNumber(wp.getNumber() - 1);
						} else {
							userbagList.remove(wp);
						}
						// 存储邮件内容
						ArrayList<EmailRpg> list = EmailCache.getInstance().getAlluserEmailCache().get(msg[1]);
						list.add(emailRpg);
//								System.out.println(list.get(0).getFujian().getUsername());
						SendMsg.send("发送邮件成功", ch);
						if (AllOnlineUser.onlineUserMap != null) {
							for (User user2 : AllOnlineUser.onlineUserMap.values()) {
								Channel channel = AllOnlineUser.userchMap.get(user2);
								if (user2.getNickname().equals(msg[1])) {
									SendMsg.send("收到一封来自" + user.getNickname() + "邮件", channel);
								}
							}
						}
						break;
					}
				}
			} else {
				SendMsg.send("用户不存在", ch);
			}
		} else if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			ArrayList<EmailRpg> list = EmailCache.getInstance().getAlluserEmailCache().get(user.getNickname());
			EmailRpg emailRpg = list.get(Integer.valueOf(msg[1]));
			Zb zb = zbXmlParse.getZbById(emailRpg.getFujian().getGid());
			Yaopin yaopin = yaopinXmlParse.getYaopinById(emailRpg.getFujian().getGid());
			if (zb != null) {
				rpgUtil.putZb(user, zb);
				int index = Integer.valueOf(msg[1]);
				list.remove(index);
				SendMsg.send("提取邮件成功", ch);
			} else if (yaopin != null) {
				rpgUtil.putYaopin(user, yaopin);
				int index = Integer.valueOf(msg[1]);
				list.remove(index);
				SendMsg.send("提取邮件成功", ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 展示邮件
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg1
	 */
	public void showEmail(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == 1) {
			ArrayList<EmailRpg> list = EmailCache.getInstance().getAlluserEmailCache().get(user.getNickname());
			int i = 0;
			String zbmsg = "";
			String yaopinmsg = "";
			for (EmailRpg emailRpg : list) {
//				Zb zb = IOsession.zbMp.get(emailRpg.getFujian().getGid());
				Zb zb = zbXmlParse.getZbById(emailRpg.getFujian().getGid());
				if (zb != null) {
					zbmsg += i + "---来自" + emailRpg.getUser().getNickname() + "-" + zb.getName() + "-耐久度："
							+ emailRpg.getFujian().getNjd();
					i++;
				}
			}
			for (EmailRpg emailRpg : list) {
//				Yaopin yaopin = IOsession.yaopinMp.get(emailRpg.getFujian().getGid());
				Yaopin yaopin = yaopinXmlParse.getYaopinById(emailRpg.getFujian().getGid());
				if (yaopin != null) {
					yaopinmsg += i + "---来自" + emailRpg.getUser().getNickname() + "-" + yaopin.getName();
					i++;
				}
			}
			SendMsg.send(zbmsg + yaopinmsg, ch);
		} else {
			SendMsg.send("指令错误", ch);
		}
	}
}
