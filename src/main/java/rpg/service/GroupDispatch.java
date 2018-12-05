package rpg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Group;
import rpg.pojo.User;
import rpg.session.IOsession;

/**
 * 组队
 * @author ljq
 *
 */
@Component
public class GroupDispatch {
	// 组队邀请 指令：group 用户
	public void group(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		// 接受组队请求 指令：group yes 用户
		if (msg[1].equals("yes")&&msg.length>1) {
			groupYes(user, ch, group, msgR);
		}
		// 拒绝组队请求 指令：group no 用户
		else if (msg[1].equals("no")&&msg.length>1) {
			groupNo(user, ch, group, msgR);
		} 
		else if(msg[1].equals("div")&&msg.length>1) {
			groupDiv(user,ch,group,msgR);
		}
		else {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if(user2.getGroupId()!=null) {
							ch.writeAndFlush("该玩家已在队伍中");
						} else {
						ch.writeAndFlush("邀请-" + user2.getNickname() + "-组队请求已发送");
						Channel channel = IOsession.userchMp.get(user2);
						channel.writeAndFlush(user.getNickname() + "-邀请你组队");
						channel.writeAndFlush("group yes 用户-接受" + "  group no 用户-拒绝");
						String groupId = UUID.randomUUID().toString();
						user.setGroupId(groupId);
						Group group2 = new Group();
						group2.setId(groupId);
						group2.setUser(user);
						ArrayList<User> list = new ArrayList<>();
						list.add(user);
						group2.setList(list);
						IOsession.userGroupMp.put(groupId, group2);
					}
					}
				}
			}
		}
	}

	private void groupDiv(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if(msg.length==2) {
			if(user.getGroupId()!=null) {
			Group group2 = IOsession.userGroupMp.get(user.getGroupId());
			if(group2.getUser().getNickname().equals(user.getNickname())) {
				ch.writeAndFlush("队长不能退队");
			}else {
				List<User> list = group2.getList();
				list.remove(user);
				user.setGroupId(null);
				for (User user3 : list) {
					if(user3!=user) {
					Channel channel = IOsession.userchMp.get(user3);
					channel.writeAndFlush(user.getNickname()+"离开队伍");
					}
				}
			}
			}else {
				ch.writeAndFlush("你不在队伍中");
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}

	// 展示队伍列表
	public void showgroup(User user, Channel ch, ChannelGroup group, String msgR) {
		Group group2 = IOsession.userGroupMp.get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			ch.writeAndFlush("队长" + group2.getUser().getNickname());
			ch.writeAndFlush("队员：");
			for (User user2 : list) {
				if(group2.getUser()!=user2)
				ch.writeAndFlush(user2.getNickname() + " ");
			}
		} else {
			ch.writeAndFlush("不存在队伍");
		}
	}

	private void groupNo(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 3) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[2].equals(user2.getNickname())) {
						Channel channel = IOsession.userchMp.get(user2);
						ch.writeAndFlush("拒绝加入队伍成功");
						channel.writeAndFlush(user.getNickname() + "拒绝加入队伍");
					}
				}
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}

	private void groupYes(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 3) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[2].equals(user2.getNickname())) {
						Channel channel = IOsession.userchMp.get(user2);
						Group group2 = IOsession.userGroupMp.get(user2.getGroupId());
						if (group2 != null) {
							List<User> list = group2.getList();
							user.setGroupId(user2.getGroupId());
							list.add(user);
							ch.writeAndFlush("你已进入" + user2.getNickname() + "队伍");
							channel.writeAndFlush(user.getNickname() + "进入队伍");
						}
					}
				}
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}
}
