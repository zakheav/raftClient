package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.id.Hex;

import util.JSON;

public class Client {// 只是用于测试，不是完整版的client

	private ClientSocket clientSocket;
	private List<String> serverAddrList; // 服务器地址列表
	private int serverNum; // 服务器数量
	private int tryTimes; // 最多尝试次数
	private String commandId;

	private Client() {
		serverNum = 3;
		tryTimes = 5;
		serverAddrList = new ArrayList<String>();
		for (int i = 0; i < serverNum; ++i) {
			int port = 8080 + i;
			serverAddrList.add("127.0.0.1:" + port);
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

	public void sendCommand(String command) {
		int count = 0;
		while (count < tryTimes) {
			++count;
			if(tryConnectingLeader()) {
				List<Object> msg8 = new ArrayList<Object>();
				msg8.add(8);
				msg8.add(command);
				msg8.add(commandId);
				String massage8 = JSON.ArrayToJSON(msg8);// 打包指令消息
				try {// 发送指令
					clientSocket.write(massage8);
				} catch(IOException e) {
					clientSocket.close();
					continue;
				}
				
				try {
					List<Object> msg = JSON.JSONToArray(clientSocket.read());
					Integer msgType = (Integer)msg.get(0);
					if(msgType == 9) {
						System.out.println("收到server响应");
						count = 1000;
					} else {
						continue;
					}
				} catch(IOException e) {
					clientSocket.close();
					continue;
				}
			} else {
				System.out.println("集群不稳定");
				count = 1000;
			}
			try {
				Thread.sleep(1000*5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Client().sendCommand("hello woshigehaoren3");
	}
}
