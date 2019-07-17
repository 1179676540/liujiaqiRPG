package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.pojo.Yaopin;

/**
 * YaopinXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class YaopinXmlParse implements XmlParse {

	private HashMap<Integer, Yaopin> yaopinMap;

	@Override
	@PostConstruct
	public void init() {

		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\yaopin.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			yaopinMap = new HashMap<Integer, Yaopin>(100);
			for (Element e : elementList) {
				Yaopin yaopin = new Yaopin();
				yaopin.setName(e.elementText("name"));
				yaopin.setId(Integer.valueOf(e.elementText("id")));
				yaopin.setBuff(Integer.valueOf(e.elementText("buff")));
				yaopin.setPrice(Integer.valueOf(e.elementText("price")));
				yaopinMap.put(Integer.valueOf(e.elementText("id")), yaopin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Yaopin getYaopinById(int id) {
		Yaopin yaopin = yaopinMap.get(id);
		if (yaopin != null) {
			return yaopin;
		}
		return null;
	}
}