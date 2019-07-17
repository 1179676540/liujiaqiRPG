package rpg.pojo;

/**药品类
 * @author ljq
 *
 */
public class Yaopin {
	private int id;
	private String name;
	private int buff;
	private int price;
	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}
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
	 * @return the buff
	 */
	public int getBuff() {
		return buff;
	}
	/**
	 * @param buff the buff to set
	 */
	public void setBuff(int buff) {
		this.buff = buff;
	}
	
}
