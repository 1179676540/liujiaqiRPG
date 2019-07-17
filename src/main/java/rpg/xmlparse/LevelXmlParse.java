package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rpg.pojo.Level;

/**
 * LevelXmlParse
 * @author ljq
 *
 */
@Component
@Slf4j
public class LevelXmlParse implements XmlParse {
	
	private HashMap<Integer, Level> levelMap;
	
	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\level.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			levelMap=new HashMap<Integer, Level>(100);
			for (Element e : elementList) {
				Level level = new Level();
				level.setId(Integer.valueOf(e.elementText("id")));
				level.setExpl(Integer.valueOf(e.elementText("expL")));
				level.setExpr(Integer.valueOf(e.elementText("expR")));
				level.setHp(Integer.valueOf(e.elementText("hp")));
				level.setAck(Integer.valueOf(e.elementText("ack")));
				level.setDef(Integer.valueOf(e.elementText("def")));
				levelMap.put(Integer.valueOf(e.elementText("id")), level);
			}
			log.info("登记表加载完成..........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Level getLevelById(int id) {
		Level level = levelMap.get(id);
		if(level!=null) {
			return level;
		}
		return null;
	}
}
