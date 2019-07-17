package rpg.pojo;

import java.util.concurrent.ConcurrentHashMap;

/**交易类
 * @author ljq
 *
 */
public class Jy {
	private String id;
	/**
	 * 开始时间
	 */
	private long startTime;
	private User sendUser;
	private User acceptUser;
	/**
	 * 0正常 1已有人确认
	 */
	private int acceptFlag;
	private ConcurrentHashMap<User,Integer> jyMoney;
	private ConcurrentHashMap<User,Userbag> jycontentMap;
	/**
	 * @return the jyMoney
	 */
	public ConcurrentHashMap<User, Integer> getJyMoney() {
		return jyMoney;
	}
	/**
	 * @param jyMoney the jyMoney to set
	 */
	public void setJyMoney(ConcurrentHashMap<User, Integer> jyMoney) {
		this.jyMoney = jyMoney;
	}
	/**
	 * @return the jycontentMap
	 */
	public ConcurrentHashMap<User,Userbag> getJycontentMap() {
		return jycontentMap;
	}
	/**
	 * @param jycontentMap the jycontentMap to set
	 */
	public void setJycontentMap(ConcurrentHashMap<User, Userbag> jycontentMap) {
		this.jycontentMap = jycontentMap;
	}
	/**
	 * @return the acceptFlag
	 */
	public int getAcceptFlag() {
		return acceptFlag;
	}
	/**
	 * @param acceptFlag the acceptFlag to set
	 */
	public void setAcceptFlag(int acceptFlag) {
		this.acceptFlag = acceptFlag;
	}
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
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the sendUser
	 */
	public User getSendUser() {
		return sendUser;
	}
	/**
	 * @param sendUser the sendUser to set
	 */
	public void setSendUser(User sendUser) {
		this.sendUser = sendUser;
	}
	/**
	 * @return the acceptUser
	 */
	public User getAcceptUser() {
		return acceptUser;
	}
	/**
	 * @param acceptUser the acceptUser to set
	 */
	public void setAcceptUser(User acceptUser) {
		this.acceptUser = acceptUser;
	}
	
}
