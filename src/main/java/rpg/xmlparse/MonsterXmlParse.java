package rpg.xmlparse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rpg.pojo.Monster;

/**
 * monsterXml解析
 * @author ljq
 *
 */
@Component
@Slf4j
public class MonsterXmlParse implements XmlParse {

	private HashMap<Integer, Monster> monsterMap;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\monster.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			monsterMap=new HashMap<Integer, Monster>(100);
			for (Element e : elementList) {
				Monster monster = new Monster();
				monster.setName(e.elementText("name"));
				monster.setAliveFlag(true);
				monster.setHp(Integer.valueOf(e.elementText("hp")));
				monster.setAck(Integer.valueOf(e.elementText("ack")));
				monster.setId(Integer.valueOf(e.elementText("id")));
				monster.setExp(Integer.valueOf(e.elementText("exp")));
				monster.setMoney(Integer.valueOf(e.elementText("money")));
				String[] split = e.elementText("award").split(",");
				ArrayList<Integer> awardList = new ArrayList<>();
				for (String awardId : split) {
					Integer id = Integer.valueOf(awardId);
					awardList.add(id);
				}
				monster.setAwardList(awardList);
				Monster monster1 = (Monster) monster.clone();
				monsterMap.put(Integer.valueOf(e.elementText("id")), monster1);
			}
			log.info("怪物配置加载完成..........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Monster getMonsterById(int id) {
		Monster monster = monsterMap.get(id);
		if (monster != null) {
			return monster;
		}
		return null;
	}
}
