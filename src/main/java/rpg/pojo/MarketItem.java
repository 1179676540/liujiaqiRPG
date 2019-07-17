package rpg.pojo;

/**
 * 交易行商品
 * @author ljq
 *
 */
public class MarketItem {
	private String id;
	private String name;
	private String ownername;
	/**
	 *  1、一口 2、竞拍
	 */
	private int type;
	private int oldprice;
	private int newprice;
	private int gid;
	private int njd;
	private int enhance;
	private Long time;
	private String auctioner;
	/**
	 * @return the auctioner
	 */
	public String getAuctioner() {
		return auctioner;
	}
	/**
	 * @param auctioner the auctioner to set
	 */
	public void setAuctioner(String auctioner) {
		this.auctioner = auctioner;
	}
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
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
	 * @return the ownername
	 */
	public String getOwnername() {
		return ownername;
	}
	/**
	 * @param ownername the ownername to set
	 */
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the oldprice
	 */
	public int getOldprice() {
		return oldprice;
	}
	/**
	 * @param oldprice the oldprice to set
	 */
	public void setOldprice(int oldprice) {
		this.oldprice = oldprice;
	}
	/**
	 * @return the newprice
	 */
	public int getNewprice() {
		return newprice;
	}
	/**
	 * @param newprice the newprice to set
	 */
	public void setNewprice(int newprice) {
		this.newprice = newprice;
	}
	/**
	 * @return the gid
	 */
	public int getGid() {
		return gid;
	}
	/**
	 * @param gid the gid to set
	 */
	public void setGid(int gid) {
		this.gid = gid;
	}
	/**
	 * @return the njd
	 */
	public int getNjd() {
		return njd;
	}
	/**
	 * @param njd the njd to set
	 */
	public void setNjd(int njd) {
		this.njd = njd;
	}
	/**
	 * @return the enhance
	 */
	public int getEnhance() {
		return enhance;
	}
	/**
	 * @param enhance the enhance to set
	 */
	public void setEnhance(int enhance) {
		this.enhance = enhance;
	}
	
}
