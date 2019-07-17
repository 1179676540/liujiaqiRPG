package rpg.pojo;

import java.util.HashMap;
import java.util.List;

import com.sun.javafx.collections.MappingChange.Map;

/**队伍类
 * @author ljq
 *
 */
public class Group {
	private String id;
	private User user;
	private List<User> list;
	private HashMap<String, Integer> status;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the list
	 */
	public List<User> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List<User> list) {
		this.list = list;
	}
	/**
	 * @return the status
	 */
	public HashMap<String, Integer> getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(HashMap<String, Integer> status) {
		this.status = status;
	}
	
}
