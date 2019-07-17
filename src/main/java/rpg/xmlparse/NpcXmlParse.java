package rpg.xmlparse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rpg.pojo.Npc;

/**
 * NpcXmlParse
 * 
 * @author ljq
 *
 */
@Component
@Slf4j
public class NpcXmlParse implements XmlParse {

	private List<Npc> npcList;

	@Override
	@PostConstruct
	public void init() {
		long currentTimeMillis = System.currentTimeMillis();
		try {
			SAXReader sr = new SAXReader();
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\npc.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			npcList = new ArrayList<>();
			for (Element e : elementList) {
				Npc npc = new Npc();
				npc.setId(Integer.valueOf(e.elementText("id")));
				npc.setName(e.elementText("name"));
				npc.setMsg(e.elementText("msg"));
				String[] split = e.elementText("taskid").split(",");
				ArrayList<Integer> taskidList = new ArrayList<Integer>();
				for (String taskId : split) {
					Integer id = Integer.valueOf(taskId);
					taskidList.add(id);
				}
				npc.setTaskidList(taskidList);
				npcList.add(npc);
			}
			log.info("npc加载完毕.............耗时"+(System.currentTimeMillis()-currentTimeMillis)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Npc getNpcById(int id) {
		for (Npc npc : npcList) {
			if (npc.getId() == id) {
				return npc;
			}
		}
		return null;
	}
}
