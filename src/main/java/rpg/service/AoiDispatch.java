package rpg.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.core.AllOnlineUser;
import rpg.pojo.Monster;
import rpg.pojo.Npc;
import rpg.pojo.Task;
import rpg.pojo.User;
import rpg.pojo.Userfinishtask;
import rpg.pojo.Usertaskprocess;
import rpg.service.area.Area;
import rpg.service.area.Scene;
import rpg.util.SendMsg;
import rpg.xmlparse.TaskXmlParse;

/**
 * aoi指令处理器
 * 
 * @author ljq
 *
 */
@Component
public class AoiDispatch {

	@Autowired
	private TaskXmlParse taskXmlParse;

	public void aoi(User user, Channel ch, ChannelGroup group, String messgage) {
		Integer id = user.getAreaid();
		select(user, ch, group, id);
	}

	public void select(User user, Channel ch, ChannelGroup group, int id) {
		String string = "----------Npc---------\n";
		Scene scene = Area.sceneList.get(id - 1);
		LinkedList<Npc> npcList = scene.getNpcList();
		for (Npc npc : npcList) {
			List<Integer> taskidList = npc.getTaskidList();
			string += npc.getName() + "----\n";
			for (Integer integer : taskidList) {
				Task task = taskXmlParse.getTaskById(integer);
				Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
				Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
				if (doingTask.get(task.getId()) == null && finishTask.get(task.getId()) == null) {
					string += "任务id:" + task.getId() + "-任务名称：" + task.getName() + "\n";
				}
			}
		}
		string += "----------Monster---------\n";
		LinkedList<Monster> monsterList = scene.getMonsterList();
		for (Monster monster : monsterList) {
			string += monster.toString();
		}
		string += "\n"+"本角色:" + user.getNickname()+"\n";
		for (Channel channel : group) {
			if (channel != ch) {
				if (AllOnlineUser.onlineUserMap.get(channel.remoteAddress()) != null) {
					User user2 = AllOnlineUser.onlineUserMap.get(channel.remoteAddress());
					if (user2.getAreaid().equals(user.getAreaid())) {
						string += "其他角色:" + user2.getNickname();
					}
				}
			}
		}
		SendMsg.send(string, ch);
	}
}
