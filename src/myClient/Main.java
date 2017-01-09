package myClient;

import client.Client;

public class Main {
	public static void main(String[] args) {
		new Client().send_command("select * from log where term = 3", false, new MyCallback());
	}
}
