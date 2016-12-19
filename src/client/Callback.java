package client;

import java.util.List;
import java.util.Map;

public interface Callback {
	public void callback(List<Map<String, Object>> results);
}
