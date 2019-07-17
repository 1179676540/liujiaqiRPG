package rpg.xmlparse;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.pojo.Skill;
import rpg.service.skill.SkillList;

/**
 * SkillXmlParse
 * @author ljq
 *
 */
@Component
public class SkillXmlParse implements XmlParse {
	
	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\skill.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			for (Element e : elementList) {
				Skill skill = new Skill();
				skill.setId(Integer.valueOf(e.elementText("id")));
				skill.setName(e.elementText("name"));
				skill.setCd(Integer.valueOf(e.elementText("cd")));
				skill.setMp(Integer.valueOf(e.elementText("mp")));
				skill.setHurt(Integer.valueOf(e.elementText("hurt")));
				skill.setEffect(e.elementText("effect"));
				SkillList.getInstance().getSkillMp().put(e.elementText("id"), skill);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
