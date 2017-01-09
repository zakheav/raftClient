package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import util.Integer_byte_transform;

public class ClientSocket {
	public Socket socket;

	public ClientSocket(Socket socket) {
		this.socket = socket;
	}

	private byte[] wrap(byte[] payload) {// add payload length to the bag
		byte[] bag = new byte[payload.length + 4];
		byte[] payloadLength = Integer_byte_transform.intToByteArray(payload.length);
		bag[0] = payloadLength[0];
		bag[1] = payloadLength[1];
		bag[2] = payloadLength[2];
		bag[3] = payloadLength[3];
		for (int i = 0; i < payload.length; ++i) {
			bag[i + 4] = payload[i];
		}
		return bag;
	}

	public void write(String massage) throws IOException {
		byte[] massageByte = wrap(massage.getBytes());
		OutputStream output = socket.getOutputStream();
		output.write(massageByte);// block
		output.flush();
	}

	public String read() throws IOException, SocketTimeoutException {
		byte[] buffer = new byte[1024 * 10];
		InputStream input = socket.getInputStream();
		int length = input.read(buffer);// block

		StringBuffer massageBuffer = new StringBuffer(1024 * 10);
		for (int i = 4; i < length; ++i) {
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
