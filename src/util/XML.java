package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XML {
	public Map<String, Object> nodeConf() {
		Map<String, Object> r = new HashMap<String, Object>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File("conf/conf.xml"));
			Element root = document.getRootElement();
			Element nodeList = root.element("nodeList");
			@SuppressWarnings("unchecked")
			List<Element> elementList = nodeList.elements();
			List<String> ipportList = new ArrayList<String>();
			for (Element e : elementList) {
				ipportList.add(e.getText());
			}
			r.put("ipport", ipportList);
			return r;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
