package rpg.pojo;

import java.util.List;

/**npc类
 * @author ljq
 *
 */
public class Npc {
	private int id;
	private String name;
	private String msg;
	private List<Integer> taskidList;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	/**
	 * @return the taskidList
	 */
	public List<Integer> getTaskidList() {
		return taskidList;
	}

	/**
	 * @param taskidList the taskidList to set
	 */
	public void setTaskidList(List<Integer> taskidList) {
		this.taskidList = taskidList;
	}

	@Override
	public String toString() {
		return "Npc [name=" + name + "]";
	}
	public String talk() {
		return msg;
	}
}
