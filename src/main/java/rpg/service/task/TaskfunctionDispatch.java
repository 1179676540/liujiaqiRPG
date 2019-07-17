package rpg.service.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.data.dao.UsertaskprocessMapper;
import rpg.pojo.Npc;
import rpg.pojo.Task;
import rpg.pojo.User;
import rpg.pojo.Userfinishtask;
import rpg.pojo.Usertaskprocess;
import rpg.service.area.Area;
import rpg.service.area.Scene;
import rpg.util.SendMsg;
import rpg.xmlparse.NpcXmlParse;
import rpg.xmlparse.TaskXmlParse;

/**
 * 任务功能处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class TaskfunctionDispatch {

	@Autowired
	private TaskXmlParse taskXmlParse;
	@Autowired
	private NpcXmlParse npcXmlParse;
	@Autowired
	private UsertaskprocessMapper usertaskprocessMapper;

	/**
	 * 展示已完成任务
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showFinishTask(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
			String string = "--------已完成任务--------\n";
			if (finishTask != null) {
				for (Userfinishtask taskProcess : finishTask.values()) {
					Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
					string += "任务名称：" + task.getName() + "\n";
				}
			}
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(string);
			SendMsg.send(builder.build(), ch);
		}
	}

	/**
	 * 展示任务
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showTask(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
			String string = "----------进行中任务----------\n";
			if (doingTask != null) {
				for (Usertaskprocess taskProcess : doingTask.values()) {
					Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
					string += "任务名称：" + taskProcess.getName() + "----任务进度" + taskProcess.getNum() + "/" + task.getNum()
							+ "\n";
				}
			}
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(string);
			SendMsg.send(builder.build(), ch);
		}
	}

	/**
	 * 展示npc的任务
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showNpcTask(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			Scene scene = Area.sceneList.get(user.getAreaid() - 1);
			LinkedList<Npc> npcList = scene.getNpcList();
			String string = "";
			for (Npc npc : npcList) {
				if (npc.getName().equals(msg[MsgSize.MSG_INDEX_1.getValue()])) {
					List<Integer> taskidList = npc.getTaskidList();
					string = "---------" + npc.getName() + "可接受任务----------\n";
					for (Integer integer : taskidList) {
						Task task = taskXmlParse.getTaskById(integer);
						Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
						Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
						if (doingTask.get(task.getId()) == null && finishTask.get(task.getId()) == null) {
							string += "任务id:"+task.getId()+"-任务名称：" + task.getName() + "\n";
						}
					}
				}
			}
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(string);
			SendMsg.send(builder.build(), ch);
		}
	}

	/**
	 * 接受Npc任务
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void acceptNpcTask(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			Scene scene = Area.sceneList.get(user.getAreaid() - 1);
			LinkedList<Npc> npcList = scene.getNpcList();
			for (Npc npc : npcList) {
				if (npc.getName().equals(msg[MsgSize.MSG_INDEX_1.getValue()])) {
					List<Integer> taskidList = npc.getTaskidList();
					for (Integer integer : taskidList) {
						if (integer.equals(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]))) {
							Task task = taskXmlParse.getTaskById(integer);
							Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
							Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
							if (doingTask.get(task.getId()) == null && finishTask.get(task.getId()) == null) {
								Usertaskprocess usertaskprocess = new Usertaskprocess();
								usertaskprocess.setUsername(user.getNickname());
								usertaskprocess.setTaskid(task.getId());
								usertaskprocess.setName(task.getName());
								usertaskprocess.setReqid(task.getReqid());
								usertaskprocess.setNum(0);
								if (task.getId() == 2) {
									usertaskprocess.setNum(1);
								}
								usertaskprocessMapper.insert(usertaskprocess);
								doingTask.put(task.getId(), usertaskprocess);
								
								Builder builder = ServerRespPacket.Resp.newBuilder();
								builder.setData("成功接取任务---" + task.getName());
								SendMsg.send(builder.build(), ch);
							}
							break;
						}
					}
				}
			}
		}
	}
}
