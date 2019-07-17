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
import rpg.pojo.Enhance;

/**
 * EnhanceXmlParse
 * @author ljq
 *
 */
@Component
@Slf4j
public class EnhanceXmlParse implements XmlParse {
	
	private HashMap<Integer, Enhance> enhanceMap;
	
	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\enhance.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			enhanceMap=new HashMap<Integer, Enhance>(100);
			for (Element e : elementList) {
				Enhance enhance = new Enhance();
				enhance.setId(Integer.valueOf(e.elementText("id")));
				enhance.setSuccesRate(Integer.valueOf(e.elementText("succesRate")));
				enhance.setMoney(Integer.valueOf(e.elementText("money")));
				enhanceMap.put(Integer.valueOf(e.elementText("id")), enhance);
			}
			log.info("强化表加载完成..........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Enhance getEnhanceById(int id) {
		Enhance enhance = enhanceMap.get(id);
		if(enhance!=null) {
			return enhance;
		}
		return null;
	}
}
