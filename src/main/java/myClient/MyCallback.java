package myClient;

import java.util.List;
import java.util.Map;

import client.Callback;

public class MyCallback implements Callback {
	public void callback(List<Map<String, Object>> results) {
		for(Map<String, Object> row : results) {
			String name = (String)row.get("name");
			System.out.println(name);
		}
	}
}
