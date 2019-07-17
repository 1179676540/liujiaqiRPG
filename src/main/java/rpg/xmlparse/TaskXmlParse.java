package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.core.ThreadResource;
import rpg.pojo.Task;

/**
 * TaskXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class TaskXmlParse implements XmlParse {
	
	private HashMap<Integer, Task> taskMap;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\task.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			taskMap = new HashMap<Integer, Task>(100);
			for (Element e : elementList) {
				Task task = new Task();
				task.setId(Integer.valueOf(e.elementText("id")));
				task.setName(e.elementText("name"));
				task.setReqid(Integer.valueOf(e.elementText("reqid")));
				task.setNum(Integer.valueOf(e.elementText("num")));
				task.setMoney(Integer.valueOf(e.elementText("money")));
				task.setAwardId(Integer.valueOf(e.elementText("award")));
//				IOsession.taskMp.put(Integer.valueOf(e.elementText("id")), task);
				taskMap.put(Integer.valueOf(e.elementText("id")), task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Task getTaskById(int id) {
		Task task = taskMap.get(id);
		if(task!=null) {
			return task;
		}
		return null;
	}
}
