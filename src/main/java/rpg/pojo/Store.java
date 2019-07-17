package rpg.pojo;

import java.util.HashMap;

/**商店类
 * @author ljq
 *
 */
public class Store {
	private String name;
	private HashMap<Integer,Yaopin> yaopinMap;
	private HashMap<Integer,Zb> zbMap;
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
	 * @return the yaopinMap
	 */
	public HashMap<Integer, Yaopin> getYaopinMap() {
		return yaopinMap;
	}
	/**
	 * @param yaopinMap the yaopinMap to set
	 */
	public void setYaopinMap(HashMap<Integer, Yaopin> yaopinMap) {
		this.yaopinMap = yaopinMap;
	}
	/**
	 * @return the zbMap
	 */
	public HashMap<Integer, Zb> getZbMap() {
		return zbMap;
	}
	/**
	 * @param zbMap the zbMap to set
	 */
	public void setZbMap(HashMap<Integer, Zb> zbMap) {
		this.zbMap = zbMap;
	}


}
