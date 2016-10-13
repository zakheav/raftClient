package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.id.Hex;
import util.JSON;
import util.XML;

public class Client {// 只是用于测试，不是完整版的client

	private ClientSocket clientSocket;
	private List<String> serverAddrList; // 服务器地址列表
	private int tryTimes; // 最多尝试次数
	private String commandId;

	@SuppressWarnings("unchecked")
	private Client() {
		Map<String, Object> conf = new XML().nodeConf();
		this.tryTimes = 5;
		this.serverAddrList = new ArrayList<String>();
		for (String ipport : (List<String>) (conf.get("ipport"))) {
			this.serverAddrList.add(ipport);
		}
		commandId = getCode();
	}

	private String getCode() {
		return new String(Hex.encodeHex(org.apache.commons.id.uuid.UUID.randomUUID().getRawBytes()));
	}

	private boolean tryConnectingLeader() {
		for (String ipport : serverAddrList) {
			String ip = ipport.split(":")[0];
			int port = Integer.parseInt(ipport.split(":")[1]);
			try {
				Socket socket = new Socket(ip, port);
				clientSocket = new ClientSocket(socket);

				List<Object> msg6 = new ArrayList<Object>();
				msg6.add(6);
				String massage6 = JSON.ArrayToJSON(msg6);
				clientSocket.write(massage6);// 发送client连接消息

				List<Object> msg7 = JSON.JSONToArray(clientSocket.read());// 接收server发回的相应
				Boolean success = (Boolean) msg7.get(1);
				if (success) {
					return true;
				}
			} catch (IOException e) {
				continue;
			}
		}
		return false;
	}

	public void sendCommand(String command, boolean read, Callback callback) {
		int count = 0;
		while (count < tryTimes) {
			++count;
			if (tryConnectingLeader()) {
				List<Object> msg8 = new ArrayList<Object>();
				msg8.add(8);
				msg8.add(read);
				msg8.add(command);
				msg8.add(commandId);
				String massage8 = JSON.ArrayToJSON(msg8);// 打包指令消息
				try {// 发送指令
					clientSocket.write(massage8);
				} catch (IOException e) {
					clientSocket.close();
					continue;
				}

				try {
					List<Object> msg = JSON.JSONToArray(clientSocket.read());
					Integer msgType = (Integer) msg.get(0);
					Object resp = msg.get(1);
					if (msgType == 9) {
						if (resp.equals("ok")) {
							System.out.println("接收到server的响应");
						} else {
							@SuppressWarnings("unchecked")
							List<Object> resultList = (List<Object>) resp;
							List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
							for (Object row : resultList) {
								@SuppressWarnings("unchecked")
								Map<String, Object> map = (Map<String, Object>) row;
								results.add(map);
							}
							callback.callback(results);
						}
						count = 1000;
					} else {
						continue;
					}
				} catch (IOException e) {
					clientSocket.close();
					continue;
				}
			} else {
				System.out.println("集群不稳定");
				count = 1000;
			}
			try {
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Client().sendCommand("select * from log where logIndex > 1", true, new Callback());
	}
}
