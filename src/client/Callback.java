package client;

import java.util.List;
import java.util.Map;

public class Callback {
	public void callback(List<Map<String, Object>> results) {
		for(Map<String, Object> row : results) {
			int index = (int)row.get("logIndex");
			String command = (String)row.get("command");
			System.out.println(index + " " + command);
		}
	}
}
