package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import util.JSON;
import util.XML;

public class Client {

	private ClientSocket clientSocket;
	private List<String> serverAddrList;
	private int serverNum;
	private int tryTimes;
	private String commandId;

	@SuppressWarnings("unchecked")
	public Client() {
		Map<String, Object> conf = new XML().nodeConf();
		this.tryTimes = 5;
		this.serverAddrList = new ArrayList<String>();
		int counter = 0;
		for (String ipport : (List<String>) (conf.get("ipport"))) {
			this.serverAddrList.add(ipport);
			++counter;
		}
		this.serverNum = counter;
		commandId = get_code();
	}

	private String get_code() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	private int tryConnectingLeader(int beginIdx) {
		int i = beginIdx;
		do {
			String ipport = serverAddrList.get(i % serverNum);
			String ip = ipport.split(":")[0];
			int port = Integer.parseInt(ipport.split(":")[1]);
			try {
				Socket socket = new Socket(ip, port);
				socket.setSoTimeout(20000);// read timeout 20 sec
				clientSocket = new ClientSocket(socket);

				List<Object> msg6 = new ArrayList<Object>();
				msg6.add(6);
				String massage6 = JSON.ArrayToJSON(msg6);
				clientSocket.write(massage6);
				List<Object> msg7 = JSON.JSONToArray(clientSocket.read());
				Boolean success = (Boolean) msg7.get(1);
				if (success) {
					return i % serverNum;
				}
			} catch (IOException e) {
				continue;
			}
			++i;
			
		} while(i % serverNum != beginIdx);
		return -1;
	}

	public void send_command(String command, boolean read, Callback callback) {
		command = command.replaceAll("\'", "\"");
		int count = 0;
		int beginIdx = 0;
		while (count < tryTimes) {
			++count;
			int available = tryConnectingLeader(beginIdx);
			if (available != -1) {
				List<Object> msg8 = new ArrayList<Object>();
				msg8.add(8);
				msg8.add(read);
				msg8.add(command);
				msg8.add(commandId);
				String massage8 = JSON.ArrayToJSON(msg8);
				try {// send command
					clientSocket.write(massage8);
				} catch (IOException e) {
					clientSocket.close();
					continue;
				}

				try {
					List<Object> msg = JSON.JSONToArray(clientSocket.read());// wait for response
					Integer msgType = (Integer) msg.get(0);
					Object resp = msg.get(1);
					if (msgType == 9) {
						if (resp.equals("ok")) {
							System.out.println("receive server response");
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
				} catch (SocketTimeoutException e1) {// long time no response
					System.out.println("client time out");
					clientSocket.close();
					beginIdx = (available + 1) % serverNum;
					continue;
				} catch (IOException e2) {
					clientSocket.close();
					continue;
				}
			} else {
				System.out.println("cluster not stable");
				count = 1000;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
