package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

@Component
public class RoleXmlParse implements XmlParse {

	private HashMap<Integer, String> roleMap;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\role.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			roleMap = new HashMap<Integer, String>(100);
			for (Element e : elementList) {
				roleMap.put(Integer.valueOf(e.elementText("id")), e.elementText("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNameById(int id) {
		String name = roleMap.get(id);
		if (name != null) {
			return name;
		}
		return null;
	}
}
