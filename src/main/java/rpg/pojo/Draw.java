package rpg.pojo;

import java.util.HashMap;

/**
 * 抽奖实体类
 * 
 * @author ljq
 *
 */
public class Draw {
	private String name;
	private Integer num;
	private HashMap<Integer, Integer> idToMoney;
	private HashMap<Integer, Double> moneyMap;
	private HashMap<Integer, Integer> idToZb;
	private HashMap<Integer, Double> zbMap;
	/**
	 * 中奖需要的最少次数
	 */
	private Integer min;
	/**
	 * 中奖需要的最多次数
	 */
	private Integer max;
	
	/**
	 * @return the num
	 */
	public Integer getNum() {
		return num;
	}
	/**
	 * @param num the num to set
	 */
	public void setNum(Integer num) {
		this.num = num;
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
	 * @return the idToMoney
	 */
	public HashMap<Integer, Integer> getIdToMoney() {
		return idToMoney;
	}
	/**
	 * @param idToMoney the idToMoney to set
	 */
	public void setIdToMoney(HashMap<Integer, Integer> idToMoney) {
		this.idToMoney = idToMoney;
	}
	/**
	 * @return the moneyMap
	 */
	public HashMap<Integer, Double> getMoneyMap() {
		return moneyMap;
	}
	/**
	 * @param moneyMap the moneyMap to set
	 */
	public void setMoneyMap(HashMap<Integer, Double> moneyMap) {
		this.moneyMap = moneyMap;
	}
	/**
	 * @return the idToZb
	 */
	public HashMap<Integer, Integer> getIdToZb() {
		return idToZb;
	}
	/**
	 * @param idToZb the idToZb to set
	 */
	public void setIdToZb(HashMap<Integer, Integer> idToZb) {
		this.idToZb = idToZb;
	}

	/**
	 * @return the zbMap
	 */
	public HashMap<Integer, Double> getZbMap() {
		return zbMap;
	}
	/**
	 * @param zbMap the zbMap to set
	 */
	public void setZbMap(HashMap<Integer, Double> zbMap) {
		this.zbMap = zbMap;
	}
	/**
	 * @return the min
	 */
	public Integer getMin() {
		return min;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(Integer min) {
		this.min = min;
	}
	/**
	 * @return the max
	 */
	public Integer getMax() {
		return max;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(Integer max) {
		this.max = max;
	}
}
