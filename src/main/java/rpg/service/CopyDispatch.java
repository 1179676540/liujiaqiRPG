package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.pojo.BossScene;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.service.bossscene.BossSceneCache;
import rpg.service.group.GroupCache;
import rpg.util.SendMsg;
import rpg.xmlparse.BossSceneXmlParse;

/**
 * Boss副本
 * 
 * @author ljq
 *
 */

@Component
public class CopyDispatch {

	private static final int FM = 3;
	private static final int FZ = 2;
	@Autowired
	private BossSceneXmlParse bossSceneXmlParse;

	/**
	 * 队长申请进入副本
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void copy(User user, Channel ch, ChannelGroup group, String msgR) {
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			if (group2.getList().size() > 1) {
				if (group2.getUser() == user) {
					if (group2.getStatus() != null) {
						group2.setStatus(null);
					}
					HashMap<String, Integer> map = new HashMap<String, Integer>(500);
					map.put(user.getNickname(), 1);
					group2.setStatus(map);
					List<User> list = group2.getList();
					SendMsg.send("申请进入副本，请等待其他玩家投票...", ch);
					for (User user2 : list) {
						if (user2 != group2.getUser()) {
							Channel channel = AllOnlineUser.userchMap.get(user2);
							SendMsg.send(user.getNickname() + "申请进入副本，请投票...", channel);
						}
					}
				} else {
					SendMsg.send("你不是队长不能申请进入副本", ch);
				}
			} else {
				SendMsg.send("组队人数不能少于2人", ch);
			}
		} else {
			SendMsg.send("请组队进入副本", ch);
		}
	}

	/**
	 * 进入副本
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void enterCopy(User user, Channel ch, ChannelGroup group, String msgR) {
		BossScene scene = new BossScene();
		// 解析并赋值
		try {
			bossSceneXmlParse.analysisBossScene(scene);
			scene.setGroupId(user.getGroupId());
			scene.setId(0);
			ArrayList<Monster> monsterList = bossSceneXmlParse.analysisBoss(scene);
			scene.setMonsterList(monsterList);
			BossSceneCache.getInstance().getUserBossCache().put(user.getGroupId(), scene);
			ChannelUtil.getSessionData(ch).setStatus(3);
			SendMsg.send("进入噩梦之地，Boss:" + "名字：" + monsterList.get(0).getName() + "-血量:" + monsterList.get(0).getHp()
					+ "-攻击力:" + monsterList.get(0).getAck(), ch);
			Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
			if (group2 != null) {
				List<User> list = group2.getList();
				for (User user2 : list) {
					if (user2 != user) {
						Channel channel = AllOnlineUser.userchMap.get(user2);
						ChannelUtil.getSessionData(channel).setStatus(3);
						SendMsg.send("进入噩梦之地，Boss:" + "名字：" + monsterList.get(0).getName() + "-血量:"
								+ monsterList.get(0).getHp() + "-攻击力:" + monsterList.get(0).getAck(), channel);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接受副本请求
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void acceptCopy(User user, Channel ch, ChannelGroup group, String msgR) {
		String word = "";
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			HashMap<String, Integer> status = group2.getStatus();
			status.put(user.getNickname(), 1);
			word += "已同意进入副本\n";
			SendMsg.send(word, ch);
			double sum = 0;
			if (status.size() == list.size()) {
				for (Integer id : status.values()) {
					sum += id;
				}
				if (sum / status.size() >= (double) FZ / FM) {
					group2.setStatus(null);
					enterCopy(user, ch, group, msgR);
				} else {
					group2.setStatus(null);
					for (User user2 : list) {
						Channel channel = AllOnlineUser.userchMap.get(user2);
						SendMsg.send("进入副本失败，少于2/3的人同意进入副本", channel);
					}
				}
			}
		}
	}

	/**
	 * 拒绝进入副本
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void refuseCopy(User user, Channel ch, ChannelGroup group, String msgR) {
		String word = "";
		Group group2 = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			HashMap<String, Integer> status = group2.getStatus();
			status.put(user.getNickname(), 0);
			word += "已拒绝进入副本\n";
			SendMsg.send(word, ch);
			double sum = 0;
			if (status.size() == list.size()) {
				for (Integer id : status.values()) {
					sum += id;
				}
				if (sum / status.size() >= (double) FZ / FM) {
					group2.setStatus(null);
					enterCopy(user, ch, group, msgR);
				} else {
					group2.setStatus(null);
					for (User user2 : list) {
						Channel channel = AllOnlineUser.userchMap.get(user2);
						SendMsg.send("进入副本失败，少于2/3的人同意进入副本", channel);
					}
				}
			}
		}
	}
}