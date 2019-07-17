package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.core.ThreadResource;
import rpg.pojo.Zb;

/**
 * ZbXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class ZbXmlParse implements XmlParse {

	private HashMap<Integer, Zb> zbMap;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\zb.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			zbMap = new HashMap<Integer, Zb>(100);
			for (Element e : elementList) {
				Zb zb = new Zb();
				zb.setId(Integer.valueOf(e.elementText("id")));
				zb.setLevel(Integer.valueOf(e.elementText("level")));
				zb.setType(Integer.valueOf(e.elementText("type")));
				zb.setName(e.elementText("name"));
				zb.setAck(Integer.valueOf(e.elementText("attribute")));
				zb.setPrice(Integer.valueOf(e.elementText("price")));
				zb.setNjd(Integer.valueOf(e.elementText("njd")));
				zbMap.put(Integer.valueOf(e.elementText("id")), zb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Zb getZbById(int id) {
		Zb zb = zbMap.get(id);
		if (zb != null) {
			return zb;
		}
		return null;
	}
}
