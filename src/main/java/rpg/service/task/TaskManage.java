package rpg.service.task;

import java.util.List;

import rpg.pojo.User;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;

/**
 * 任务管理接口
 * @author ljq
 *
 */
public interface TaskManage {

	public void checkTaskComplete(User user, int reqid);

	public void checkTaskCompleteBytaskid(User user, int taskid);

	public void checkTaskCompleteBytaskidWithzb(User user, int taskid, Zb zb);

	public void checkTaskCompleteByTaskidWithZbList(User user, int taskid, List<Userzb> list);

	public void checkMoneyTaskCompleteBytaskid(User user, int taskid);

}
