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
import rpg.pojo.Buff;

/**
 * BuffXmlParse
 * 
 * @author ljq
 *
 */
@Component
@Slf4j
public class BuffXmlParse implements XmlParse {

	private HashMap<Integer, Buff> buffMap;
	
	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\buff.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			buffMap = new HashMap<Integer, Buff>(100);
			for (Element e : elementList) {
				Buff buff = new Buff();
				buff.setId(Integer.valueOf(e.elementText("id")));
				buff.setName(e.elementText("name"));
				buff.setMp(Integer.valueOf(e.elementText("mp")));
				buff.setLastedTime(Long.valueOf(e.elementText("lastedTime")));
//				IOsession.buffMp.put(Integer.valueOf(e.elementText("id")), buff);
				buffMap.put(Integer.valueOf(e.elementText("id")), buff);
			}
			log.info("buff加载完成..........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Buff getBuffByid(int id) {
		Buff buff = buffMap.get(id);
		if (buff != null) {
			return buff;
		}
		return null;
	}
}
