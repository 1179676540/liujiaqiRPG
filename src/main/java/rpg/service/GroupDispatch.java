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
import rpg.pojo.Group;
import rpg.pojo.User;
import rpg.service.group.GroupCache;
import rpg.service.task.TaskManage;
import rpg.service.user.UserResources;
import rpg.util.SendMsg;

/**
 * 组队
 * 
 * @author ljq
 *
 */
@Component
public class GroupDispatch {

	@Autowired
	private TaskManage taskManage;

	/**
	 * 组队邀请 指令：group 用户
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void group(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if (user2.getGroupId() != null) {
							List<String> list = GroupCache.getInstance().getUserApplyCache().get(msg[1]);
							list.add(user.getNickname());
							SendMsg.send("向-" + user2.getNickname() + "-申请加入队伍请求已发送", ch);
							Channel channel = AllOnlineUser.userchMap.get(user2);
							SendMsg.send(user.getNickname() + "-申请加入队伍", channel);
						} else {
							SendMsg.send("邀请-" + user2.getNickname() + "-组队请求已发送", ch);
							Channel channel = AllOnlineUser.userchMap.get(user2);
							SendMsg.send(user.getNickname() + "-邀请你组队", channel);
							SendMsg.send("group yes 用户-接受" + "  group no 用户-拒绝", channel);
							String groupId = UUID.randomUUID().toString();
							user.setGroupId(groupId);
							Group group2 = new Group();
							group2.setId(groupId);
							group2.setUser(user);
							ArrayList<User> list = new ArrayList<>();
							list.add(user);
							group2.setList(list);
							GroupCache.getInstance().getUserGroupMpCache().put(groupId, group2);
							taskManage.checkTaskCompleteBytaskid(user, 7);
						}
					}
				}
			}
		}
	}

	/**
	 * 接受组队申请
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void groupAccept(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			List<String> list = GroupCache.getInstance().getUserApplyCache().get(user.getNickname());
			for (String username : list) {
				if (msg[1].equals(username)) {
					Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
					if (group2 != null) {
						List<User> list1 = group2.getList();
						User user2 = UserResources.nameMap.get(username);
						user2.setGroupId(user.getGroupId());
						list1.add(user2);
						SendMsg.send(user2.getNickname() + "-加入队伍成功", ch);
						Channel channel = AllOnlineUser.userchMap.get(user2);
						SendMsg.send("你已经成功加入" + user.getNickname() + "的队伍", channel);
						list.remove(username);
						taskManage.checkTaskCompleteBytaskid(user2, 7);
						break;
					}
				}
			}
		}
	}

	/**
	 * 队伍申请列表
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showGroupAccept(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			List<String> list = GroupCache.getInstance().getUserApplyCache().get(user.getNickname());
			String word = "---队伍组队申请列表---\n";
			for (String username : list) {
				word += username + "\n";
			}
			SendMsg.send(word, ch);
		}
	}

	/**
	 * 离队
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void groupDiv(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			if (user.getGroupId() != null) {
				Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
				if (group2.getUser().getNickname().equals(user.getNickname())) {
					List<User> list = group2.getList();
					if (list.size() == MsgSize.MAX_MSG_SIZE_1.getValue()) {
						list.remove(user);
						user.setGroupId(null);
						SendMsg.send("你已经解散队伍", ch);
					} else {
						User user2 = list.get(MsgSize.MSG_INDEX_1.getValue());
						group2.setUser(user2);
						list.remove(user);
						user.setGroupId(null);
						SendMsg.send("离开队伍成功", ch);
						for (User user3 : list) {
							if (user3 != user) {
								Channel channel = AllOnlineUser.userchMap.get(user3);
								SendMsg.send(user.getNickname() + "离开队伍," + user2.getNickname() + "成为队长", channel);
							}
						}
					}
				} else {
					List<User> list = group2.getList();
					list.remove(user);
					user.setGroupId(null);
					SendMsg.send("离开队伍成功", ch);
					for (User user3 : list) {
						if (user3 != user) {
							Channel channel = AllOnlineUser.userchMap.get(user3);
							SendMsg.send(user.getNickname() + "离开队伍", channel);
						}
					}
				}
			} else {
				SendMsg.send("你不在队伍中", ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 展示队伍列表
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showgroup(User user, Channel ch, ChannelGroup group, String msgR) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			SendMsg.send("队长" + group2.getUser().getNickname(), ch);
			SendMsg.send("队员：", ch);
			for (User user2 : list) {
				if (group2.getUser() != user2) {
					SendMsg.send(user2.getNickname() + " ", ch);
				}
			}
		} else {
			SendMsg.send("不存在队伍", ch);
		}
	}

	/**
	 * 拒絕加入隊伍
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void groupNo(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[MsgSize.MSG_INDEX_1.getValue()].equals(user2.getNickname())) {
						Channel channel = AllOnlineUser.userchMap.get(user2);
						SendMsg.send("拒绝加入队伍成功", ch);
						SendMsg.send(user.getNickname() + "拒绝加入队伍", channel);
					}
				}
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 接受组队请求
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void groupYes(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[MsgSize.MSG_INDEX_1.getValue()].equals(user2.getNickname())) {
						Channel channel = AllOnlineUser.userchMap.get(user2);
						Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user2.getGroupId());
						if (group2 != null) {
							List<User> list = group2.getList();
							user.setGroupId(user2.getGroupId());
							list.add(user);
							SendMsg.send("你已进入" + user2.getNickname() + "队伍", ch);
							SendMsg.send(user.getNickname() + "进入队伍", channel);
							taskManage.checkTaskCompleteBytaskid(user, 7);
						}
					}
				}
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 踢出队伍功能
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void groupT(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
			List<User> list = group2.getList();
			for (User user2 : list) {
				if (user2.getNickname().equals(msg[MsgSize.MSG_INDEX_1.getValue()])) {
					user2.setGroupId(null);
					list.remove(user2);
					Channel channel = AllOnlineUser.userchMap.get(user2);
					SendMsg.send("你已被踢出队伍", channel);
					SendMsg.send("踢出队伍成功", ch);
					break;
				}
			}
		}
	}
}
