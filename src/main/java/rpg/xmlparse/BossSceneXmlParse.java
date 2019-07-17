package rpg.xmlparse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.pojo.BossScene;
import rpg.pojo.Monster;

/**
 * 解析boss场景
 * @author ljq
 *
 */
@Component
public class BossSceneXmlParse {
	
	/**
	 * 解析boss场景
	 * @param scene
	 * @throws Exception
	 */
	public void analysisBossScene(BossScene scene) throws Exception {
		// 解析bossScene.xml文件，创建副本
		SAXReader sr1 = new SAXReader();
		Document document1 = sr1.read(new File("src\\main\\resources\\rpg.conf\\bossScene.xml"));
		Element root1 = document1.getRootElement();
		List<Element> elementList1 = root1.elements();
		for (Element e : elementList1) {
			scene.setSceneid(Integer.valueOf(e.elementText("sceneid")));
			scene.setName(e.elementText("name"));
			scene.setLastedTime(Integer.valueOf(e.elementText("lastedTime")));
			scene.setLayer(Integer.valueOf(e.elementText("layer")));
			String[] split = e.elementText("bossid").split(",");
			ArrayList<Integer> bossidList = new ArrayList<>();
			for (String bossid : split) {
				bossidList.add(Integer.valueOf(bossid));
			}
			scene.setBossid(bossidList);
			HashMap<Integer, Integer> hashMap = new HashMap<>(500);
			String[] split2 = e.elementText("struct").split(",");
			for (String string : split2) {
				String[] split3 = string.split(":");
				hashMap.put(Integer.valueOf(split3[0]), Integer.valueOf(split3[1]));
			}
			scene.setStruct(hashMap);
		}
	}
	
	/**解析boss
	 * @param scene
	 * @return
	 * @throws DocumentException
	 */
	public ArrayList<Monster> analysisBoss(BossScene scene) throws DocumentException {
		List<Integer> bossid = scene.getBossid();
		ArrayList<Monster> monsterList = new ArrayList<>();
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\boss.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Monster monster = new Monster();
			monster.setId(Integer.valueOf(e.elementText("id")));
			monster.setName(e.elementText("name"));
			monster.setAliveFlag(true);
			monster.setHp(Integer.valueOf(e.elementText("hp")));
			monster.setAck(Integer.valueOf(e.elementText("ack")));
			monster.setCountAcker(0);
			monster.setMoney(Integer.valueOf(e.elementText("money")));
			String[] split = e.elementText("award").split(",");
			ArrayList<Integer> awardList = new ArrayList<>();
			for (String awardId : split) {
				Integer id = Integer.valueOf(awardId);
				awardList.add(id);
			}
			monster.setAwardList(awardList);
			String[] split1 = e.elementText("skill").split(",");
			ArrayList<Integer> skillList = new ArrayList<>();
			for (String skillId : split1) {
				Integer id = Integer.valueOf(skillId);
				skillList.add(id);
			}
			monster.setSkillList(skillList);
			if (bossid.contains(monster.getId())) {
				monsterList.add(monster);
			}
		}
		return monsterList;
	}
}
