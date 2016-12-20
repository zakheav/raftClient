package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientSocket {
	public Socket socket;

	public ClientSocket(Socket socket) {
		this.socket = socket;
	}

	public void write(String massage) throws IOException {
		byte[] massageByte = massage.getBytes();
		OutputStream output = socket.getOutputStream();
		output.write(massageByte);// block
		output.flush();
	}

	public String read() throws IOException, SocketTimeoutException {
		byte[] buffer = new byte[1024*10];
		InputStream input = socket.getInputStream();
		int length =  input.read(buffer);// block
		
		StringBuffer massageBuffer = new StringBuffer(1024 * 10);
		for (int i = 0; i < length; ++i) {
			massageBuffer.append((char) buffer[i]);
		}
		return massageBuffer.toString();
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("connection close");
		}
	}
	
}
