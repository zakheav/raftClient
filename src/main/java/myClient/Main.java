package myClient;

import client.Client;

class MyThread implements Runnable {

	public void run() {
		new Client().send_command("insert into user(name) values('www234')", false, new MyCallback());
		// new Client().send_command("select * from user where name = 'weiyuan222'", true, new MyCallback());
	}
}

public class Main {
	public static void main(String[] args) throws InterruptedException {
		for(int i=0; i<6; ++i) {
			new Thread(new MyThread()).start();
		}
	}
}
