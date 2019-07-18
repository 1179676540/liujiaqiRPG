package rpg.xmlparse;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import rpg.pojo.Draw;

/**
 * DrawXmlParse
 * 
 * @author ljq
 *
 */
@Component
public class DrawXmlParse implements XmlParse {

	private Draw draw;

	@Override
	@PostConstruct
	public void init() {
		SAXReader sr = new SAXReader();
		try {
			Document document = sr.read(new File("src\\main\\resources\\rpg.conf\\Draw.xml"));
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			draw = new Draw();
			for (Element e : elementList) {
				draw.setName(e.elementText("name"));
				draw.setNum(Integer.valueOf(e.elementText("num")));
				draw.setMin(Integer.valueOf(e.elementText("min")));
				draw.setMax(Integer.valueOf(e.elementText("max")));
				String[] moneyIds = e.elementText("money").split(",");
				HashMap<Integer, Integer> map = new HashMap<>(100);
				HashMap<Integer, Double> moneyMap = new HashMap<>(100);
				int cout = 1;
				for (String money : moneyIds) {
					String[] split = money.split(":");
					moneyMap.put(Integer.valueOf(split[0]), Double.valueOf(split[1]));
					map.put(cout++, Integer.valueOf(split[0]));
				}
				draw.setMoneyMap(moneyMap);
				draw.setIdToMoney(map);
				String[] zbIds = e.elementText("zb").split(",");
				HashMap<Integer, Integer> mapZb = new HashMap<>(100);
				HashMap<Integer, Double> zbMap = new HashMap<>(100);
				for (String zb : zbIds) {
					String[] split = zb.split(":");
					zbMap.put(Integer.valueOf(split[0]), Double.valueOf(split[1]));
					mapZb.put(cout++, Integer.valueOf(split[0]));
				}
				draw.setZbMap(zbMap);
				draw.setIdToZb(mapZb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Draw getDraw() {
		return draw;
	}
}
