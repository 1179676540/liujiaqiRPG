package rpg.service.area;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 地图类
 * 
 * @author ljq
 *
 */
public class Area {
	public static LinkedList<Scene> sceneList = new LinkedList<Scene>();
	public static HashMap<String, Integer> mp1 = new HashMap<String, Integer>();
	public static int[][] mp2 = new int[30][30];

	public static int checkArea(String msg, int idfrom) {
		Integer id = mp1.get(msg);
		if(id!=null) {
		if (mp2[idfrom][id] == 1) {
			return id;
		} else {
			return 0;
		}
		} else {
			return 0;
		}
	}
}