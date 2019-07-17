package rpg.core;

import java.util.List;

import rpg.pojo.Monster;
import rpg.pojo.User;

/**
 * channel存储的信息
 * @author ljq
 *
 */
public class SessionData {
	private User user;
	private int status;
	private List<Monster> monsterMp;
	
	
	public SessionData(User user, int status, List<Monster> monsterMp) {
		super();
		this.user = user;
		this.status = status;
		this.monsterMp = monsterMp;
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
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the monsterMp
	 */
	public List<Monster> getMonsterMp() {
		return monsterMp;
	}
	/**
	 * @param monsterMp the monsterMp to set
	 */
	public void setMonsterMp(List<Monster> monsterMp) {
		this.monsterMp = monsterMp;
	}
	
	
}
