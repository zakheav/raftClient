package myClient;

import java.util.List;
import java.util.Map;

import client.Callback;

public class MyCallback implements Callback {
	public void callback(List<Map<String, Object>> results) {
		for(Map<String, Object> row : results) {
			int index = (Integer)row.get("logIndex");
			String command = (String)row.get("command");
			System.out.println(index + " " + command);
		}
	}
}
