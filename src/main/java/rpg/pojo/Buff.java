package rpg.pojo;

/**场景类
 * @author ljq
 *
 */
public class Buff {
	private int id;
	private String name;
	private int mp;
	/**
	 * 持续时间
	 */
	private long lastedTime;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}
	/**
	 * @param mp the mp to set
	 */
	public void setMp(int mp) {
		this.mp = mp;
	}
	/**
	 * @return the lastedTime
	 */
	public long getLastedTime() {
		return lastedTime;
	}
	/**
	 * @param lastedTime the lastedTime to set
	 */
	public void setLastedTime(long lastedTime) {
		this.lastedTime = lastedTime;
	}
	
}
