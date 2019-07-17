package rpg.pojo;

/**装备类
 * @author ljq
 *
 */
public class Zb {
	private int id;
	private String name;
	private int ack;
	private int price;
	private int njd;
	/**
	 * 1攻击力 2防御力
	 */
	private int type;
	private int level;
	
	public int getNjd() {
		return njd;
	}
	
	public void setNjd(int njd) {
		this.njd = njd;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

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

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
