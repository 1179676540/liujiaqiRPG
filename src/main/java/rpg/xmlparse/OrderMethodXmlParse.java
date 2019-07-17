package rpg.xmlparse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.pojo.OrderMethod;

/**
 * OrderMethodXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class OrderMethodXmlParse implements XmlParse {
	
	private HashMap<String, OrderMethod> orderMethodMap;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\orderMethod.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			orderMethodMap= new HashMap<String, OrderMethod>(100);
			for (Element e : elementList) {
				OrderMethod orderMethod = new OrderMethod();
				orderMethod.setOrder(e.elementText("order"));
				orderMethod.setClassName(e.elementText("className"));
				orderMethod.setMethodName(e.elementText("method"));
				String[] split = e.elementText("status").split(",");
				ArrayList<Integer> list = new ArrayList<>();
				for (String status : split) {
					list.add(Integer.valueOf(status));
				}
				orderMethod.setStatus(list);
//				IOsession.orderFindMethod.put(e.elementText("order"), orderMethod);
				orderMethodMap.put(e.elementText("order"), orderMethod);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OrderMethod getById(String id) {
		OrderMethod orderMethod = orderMethodMap.get(id);
		if(orderMethod!=null) {
			return orderMethod;
		}
		return null;
	}
}
