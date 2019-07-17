package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.core.ThreadResource;
import rpg.pojo.Store;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;

/**
 * StoreXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class StoreXmlParse implements XmlParse {

	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private ZbXmlParse zbXmlParse;

	private Store store;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\store.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			store = new Store();
			for (Element e : elementList) {
				store.setName(e.elementText("name"));
				HashMap<Integer, Yaopin> yaopinMap = new HashMap<>(500);
				HashMap<Integer, Zb> zbMap = new HashMap<>(500);
				String[] yaopinId = e.elementText("yaopin").split(",");
				for (String yaopin : yaopinId) {
					Yaopin yaopin2 = yaopinXmlParse.getYaopinById(Integer.valueOf(yaopin));
					yaopinMap.put(yaopin2.getId(), yaopin2);
				}
				String[] zbId = e.elementText("zb").split(",");
				for (String zb : zbId) {
					Zb zb2 = zbXmlParse.getZbById(Integer.valueOf(zb));
					zbMap.put(zb2.getId(), zb2);
				}
//				IOsession.STORE_SYSTEM.setYaopinMap(yaopinMap);
//				IOsession.STORE_SYSTEM.setZbMap(zbMap);
				store.setYaopinMap(yaopinMap);
				store.setZbMap(zbMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Store getStore() {
		return store;
	}
}
