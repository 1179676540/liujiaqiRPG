package rpg.pojo;

/**邮件类
 * @author ljq
 *
 */
public class EmailRpg {
	private String id;
	private String msg;
	private Userbag fujian;
	private User user;
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
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the fujian
	 */
	public Userbag getFujian() {
		return fujian;
	}
	/**
	 * @param fujian the fujian to set
	 */
	public void setFujian(Userbag fujian) {
		this.fujian = fujian;
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
	
}
