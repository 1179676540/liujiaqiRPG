package rpg.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Npc;
import rpg.pojo.User;
import rpg.service.area.Area;
import rpg.service.task.TaskManage;
import rpg.util.SendMsg;

/**
 * 对话处理器
 * 
 * @author ljq
 *
 */
@Component
public class TalkDispatch {
	
	@Autowired
	private TaskManage taskManage;
	
	public void talk(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		Integer id = user.getAreaid();
		LinkedList<Npc> npcList = Area.sceneList.get(id - 1).getNpcList();
		for (Npc npc : npcList) {
			if (npc.getName().equals(msg[1])) {
				SendMsg.send(npc.talk(), ch);
				taskManage.checkTaskComplete(user, npc.getId());
				return;
			}
		}
		SendMsg.send("找不到该Npc", ch);
	}
}
