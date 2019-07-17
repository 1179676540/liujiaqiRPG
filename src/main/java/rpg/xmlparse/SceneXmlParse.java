package rpg.xmlparse;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.pojo.Monster;
import rpg.pojo.Npc;
import rpg.service.area.Area;
import rpg.service.area.Scene;

/**
 * scenexml解析
 * 
 * @author ljq
 *
 */
@Component
public class SceneXmlParse implements XmlParse {

	@Autowired
	private NpcXmlParse npcXmlParse;
	@Autowired
	private MonsterXmlParse monsterXmlParse;

	@Override
	@PostConstruct
	public void init() {
		try {
			SAXReader sr = new SAXReader();
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\Scene.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
//			List<Scene> sceneList = new ArrayList();
			for (Element e : elementList) {
				Scene scene = new Scene();
				scene.setName(e.elementText("name"));
				scene.setId(Integer.valueOf(e.elementText("id")));
				// 设置npc
				String[] npcId = e.elementText("npc").split(",");
				LinkedList<Npc> list = new LinkedList<Npc>();
				for (String npc : npcId) {
					Integer id = Integer.valueOf(npc);
					Npc npc2 = npcXmlParse.getNpcById(id);
					list.add(npc2);
				}
				scene.setNpcList(list);
				// 设置怪物
				String[] monsterId = e.elementText("monster").split(",");
				LinkedList<Monster> list2 = new LinkedList<>();
				for (String monster : monsterId) {
					Integer id = Integer.valueOf(monster);
					Monster monster2 = monsterXmlParse.getMonsterById(id);
					Monster monster3 = (Monster) monster2.clone();
					list2.add(monster3);
				}
				scene.setMonsterList(list2);
				// 设置地图的连通
				String[] near = e.elementText("near").split(",");
				for (String near1 : near) {
					Area.mp2[Integer.valueOf(e.elementText("id"))][Integer.valueOf(near1)] = 1;
				}
				//
				Area.mp1.put(e.elementText("name"), Integer.valueOf(e.elementText("id")));
				Area.sceneList.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
