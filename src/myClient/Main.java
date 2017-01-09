package myClient;

import client.Client;

class MyThread implements Runnable {
	@Override
	public void run() {
		new Client().send_command("xxxxxxx", false, new MyCallback());
	}
}

public class Main {
	public static void main(String[] args) throws InterruptedException {
		for(int i=0; i<4; ++i) {
			new Thread(new MyThread()).start();
		}
	}
}
