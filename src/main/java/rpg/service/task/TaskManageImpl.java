package rpg.service.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.core.AllOnlineUser;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserfinishtaskMapper;
import rpg.data.dao.UsertaskprocessMapper;
import rpg.pojo.Task;
import rpg.pojo.User;
import rpg.pojo.Userfinishtask;
import rpg.pojo.Usertaskprocess;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.TaskXmlParse;
import rpg.xmlparse.YaopinXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 任务管理实现
 * 
 * @author ljq
 *
 */
@Component
public class TaskManageImpl implements TaskManage {

	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private TaskXmlParse taskXmlParse;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UsertaskprocessMapper usertaskprocessMapper;
	@Autowired
	private UserfinishtaskMapper userfinishtaskMapper;

	/**
	 * 检查任务是否完成,带对象id
	 * 
	 * @param user
	 * @param reqid
	 */
	public void checkTaskComplete(User user, int reqid) {
		Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
		String string = "";
		if (doingTask != null) {
			for (Usertaskprocess taskProcess : doingTask.values()) {
				if (taskProcess.getReqid() == reqid) {
					int num = taskProcess.getNum();
					Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
					int num2 = task.getNum();
					if (num + 1 < num2) {
						taskProcess.setNum(num + 1);
						usertaskprocessMapper.updateByPrimaryKey(taskProcess);
						string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
					} else {
						Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
						Userfinishtask userfinishtask = new Userfinishtask();
						userfinishtask.setTaskid(task.getId());
						userfinishtask.setUsername(user.getNickname());
						finishTask.put(taskProcess.getTaskid(), userfinishtask);
						userfinishtaskMapper.insert(userfinishtask);
						usertaskprocessMapper.deleteByPrimaryKey(taskProcess.getId());
						doingTask.remove(taskProcess.getTaskid());
						string += "任务名称---" + task.getName() + "---完成" + "\n";
						String string2 = getAward(user, task);
						string += string2;
					}
				}
			}
			Channel channel = AllOnlineUser.userchMap.get(user);
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(string);
			SendMsg.send(builder.build(), channel);
		}
	}

	/**
	 * 检查任务是否完成,带任务id
	 * 
	 * @param user
	 * @param reqid
	 */
	public void checkTaskCompleteBytaskid(User user, int taskid) {
		String string = "";
		Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			Usertaskprocess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				int num = taskProcess.getNum();
				Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (num + 1 < num2) {
					taskProcess.setNum(num + 1);
					usertaskprocessMapper.updateByPrimaryKey(taskProcess);
					string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
				} else {
					Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
					Userfinishtask userfinishtask = new Userfinishtask();
					userfinishtask.setTaskid(task.getId());
					userfinishtask.setUsername(user.getNickname());
					finishTask.put(taskProcess.getTaskid(), userfinishtask);
					userfinishtaskMapper.insert(userfinishtask);
					usertaskprocessMapper.deleteByPrimaryKey(taskProcess.getId());
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = AllOnlineUser.userchMap.get(user);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(string);
				SendMsg.send(builder.build(), channel);
			}
		}
	}

	public void checkTaskCompleteBytaskidWithzb(User user, int taskid, Zb zb) {
		String string = "";
		Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			Usertaskprocess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				if (zb.getLevel() >= taskProcess.getReqid()) {
					int num = taskProcess.getNum();
					Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
					int num2 = task.getNum();
					if (num + 1 < num2) {
						taskProcess.setNum(num + 1);
						usertaskprocessMapper.updateByPrimaryKey(taskProcess);
						string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
					} else {
						Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
						Userfinishtask userfinishtask = new Userfinishtask();
						userfinishtask.setTaskid(task.getId());
						userfinishtask.setUsername(user.getNickname());
						finishTask.put(taskProcess.getTaskid(), userfinishtask);
						userfinishtaskMapper.insert(userfinishtask);
						usertaskprocessMapper.deleteByPrimaryKey(taskProcess.getId());
						doingTask.remove(taskProcess.getTaskid());
						string += "任务名称---" + task.getName() + "---完成" + "\n";
						String string2 = getAward(user, task);
						string += string2;
					}
					Channel channel = AllOnlineUser.userchMap.get(user);
					Builder builder = ServerRespPacket.Resp.newBuilder();
					builder.setData(string);
					SendMsg.send(builder.build(), channel);
				}
			}
		}
	}

	public void checkTaskCompleteByTaskidWithZbList(User user, int taskid, List<Userzb> list) {
		int num = 0;
		for (Userzb userzb : list) {
			Zb zb = zbXmlParse.getZbById(userzb.getZbid());
			num += zb.getLevel();
		}
		String string = "";
		Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			Usertaskprocess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (num >= num2) {
					Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
					Userfinishtask userfinishtask = new Userfinishtask();
					userfinishtask.setTaskid(task.getId());
					userfinishtask.setUsername(user.getNickname());
					finishTask.put(taskProcess.getTaskid(), userfinishtask);
					userfinishtaskMapper.insert(userfinishtask);
					usertaskprocessMapper.deleteByPrimaryKey(taskProcess.getId());
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = AllOnlineUser.userchMap.get(user);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(string);
				SendMsg.send(builder.build(), channel);
			}
		}
	}

	public void checkMoneyTaskCompleteBytaskid(User user, int taskid) {
		String string = "";
		Map<Integer, Usertaskprocess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			Usertaskprocess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				int num = taskProcess.getNum();
				Task task = taskXmlParse.getTaskById(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (user.getMoney() < num2) {
					taskProcess.setNum(user.getMoney());
					usertaskprocessMapper.updateByPrimaryKey(taskProcess);
					string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2 + "\n";
				} else {
					Map<Integer, Userfinishtask> finishTask = user.getFinishTask();
					Userfinishtask userfinishtask = new Userfinishtask();
					userfinishtask.setTaskid(task.getId());
					userfinishtask.setUsername(user.getNickname());
					finishTask.put(taskProcess.getTaskid(), userfinishtask);
					userfinishtaskMapper.insert(userfinishtask);
					usertaskprocessMapper.deleteByPrimaryKey(taskProcess.getId());
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = AllOnlineUser.userchMap.get(user);
				Builder builder = ServerRespPacket.Resp.newBuilder();
				builder.setData(string);
				SendMsg.send(builder.build(), channel);
			}
		}
	}

	/**
	 * 获取任务奖励
	 * 
	 * @param user
	 * @param task
	 * @return
	 */
	private String getAward(User user, Task task) {
		String string = "";
		user.setMoney(user.getMoney() + task.getMoney());
		userMapper.updateByPrimaryKeySelective(user);
		int id = task.getAwardId();
		Zb zb = zbXmlParse.getZbById(id);
		Yaopin yaopin = yaopinXmlParse.getYaopinById(id);
		if (zb != null) {
			rpgUtil.putZb(user, zb);
			string += "任务奖励---获得金钱：" + task.getMoney() + "获得装备：" + zb.getName() + "\n";
		} else if (yaopin != null) {
			rpgUtil.putYaopin(user, yaopin);
			string += "任务奖励---获得金钱：" + task.getMoney() + "获得药品：" + yaopin.getName() + "\n";
		}
		checkMoneyTaskCompleteBytaskid(user, 11);
		return string;
	}
}