package rpg.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.core.AllOnlineUser;
import rpg.data.dao.UserfriendMapper;
import rpg.pojo.User;
import rpg.pojo.Userfriend;
import rpg.pojo.UserfriendExample;
import rpg.pojo.UserfriendExample.Criteria;
import rpg.service.friend.FriendCache;
import rpg.service.task.TaskManage;
import rpg.service.user.UserResources;
import rpg.util.SendMsg;

/**
 * 好友功能
 * 
 * @author ljq
 *
 */
@Component
public class FriendDispatch {

	@Autowired
	private UserfriendMapper userfriendMapper;
	@Autowired
	private TaskManage taskManage;

	/**
	 * 接受好友请求
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void accept(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			List<String> list = FriendCache.getInstance().getFriendMp().get(user.getNickname());
			boolean flag = false;
			for (String string : list) {
				if (string.equals(msg[1])) {
					Userfriend userfriend = new Userfriend();
					String name = user.getNickname();
					userfriend.setUsername(name);
					userfriend.setFriend(msg[1]);
					userfriendMapper.insert(userfriend);
					Userfriend userfriend2 = new Userfriend();
					userfriend2.setUsername(msg[1]);
					userfriend2.setFriend(name);
					userfriendMapper.insert(userfriend2);
					flag = true;
					list.remove(string);
					User user2 = UserResources.nameMap.get(msg[1]);
					Channel channel = AllOnlineUser.userchMap.get(user2);
					SendMsg.send("添加好友成功\n", ch);
					taskManage.checkMoneyTaskCompleteBytaskid(user, 12);
					SendMsg.send("你已经和" + name + "成为好友\n", channel);
					taskManage.checkMoneyTaskCompleteBytaskid(user2, 12);
					break;
				}
			}
			if (!flag) {
				SendMsg.send("指令错误", ch);
			}
		}
	}

	/**
	 * 战士好友列表
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void show(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			String nickname = user.getNickname();
			UserfriendExample example = new UserfriendExample();
			Criteria criteria = example.createCriteria();
			criteria.andUsernameEqualTo(nickname);
			List<Userfriend> list = userfriendMapper.selectByExample(example);
			String word = "-------好友列表-------\n";
			for (Userfriend userfriend : list) {
				word += userfriend.getFriend() + "\n";
			}
			SendMsg.send(word, ch);
		}
	}

	/**
	 * 展示好友申请
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void showsq(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			List<String> list = FriendCache.getInstance().getFriendMp().get(user.getNickname());
			String word = "-------好友申请列表-------\n";
			if (list != null) {
				for (String string : list) {
					word += string + "\n";
				}
				SendMsg.send(word, ch);
			}
		}
	}

	/**
	 * 添加好友
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void addFriend(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (AllOnlineUser.onlineUserMap != null) {
				boolean flag = true;
				for (User user2 : AllOnlineUser.onlineUserMap.values()) {
					if (msg[1].equals(user2.getNickname())) {
						UserfriendExample example = new UserfriendExample();
						Criteria criteria = example.createCriteria();
						criteria.andUsernameEqualTo(user.getNickname());
						List<Userfriend> userfriendList = userfriendMapper.selectByExample(example);
						for (Userfriend userfriend : userfriendList) {
							if (userfriend.getFriend().equals(user2.getNickname())) {
								flag = false;
								SendMsg.send("你们已经是好友了", ch);
								break;
							}
						}
						if (flag) {
							String username1 = user2.getNickname();
							List<String> list = new ArrayList<>();
							String username2 = user.getNickname();
							list.add(username2);
							FriendCache.getInstance().getFriendMp().put(username1, list);
							SendMsg.send("好友申请已发送", ch);
							Channel channel = AllOnlineUser.userchMap.get(user2);
							SendMsg.send(user.getNickname() + "申请与您成为好友", channel);
						}
						break;
					}
				}
			}
		}
	}
}
