package myClient;

import client.Client;

public class Main {
	public static void main(String[] args) {
		new Client().send_command("muhahaha", false, new MyCallback());
	}
}
