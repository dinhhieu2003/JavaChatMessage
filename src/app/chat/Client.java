package app.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private BufferedReader bufferedReader;
	private BufferedWriter bufferWriter;
	private Socket socket;
	private String username;
	
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.username = username;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch(IOException e) {
			closeAll(socket, bufferedReader, bufferWriter);
			e.printStackTrace();
		}
	}
	
	private void closeAll(Socket socket2, BufferedReader bufferedReader2, BufferedWriter bufferedWriter2) {
		try {
			if(socket2 != null) {
				socket2.close();
			}
			if(bufferedReader2 != null) {
				bufferedReader2.close();
			}
			if(bufferedWriter2 != null) {
				bufferedWriter2.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage() {
		try {
			bufferWriter.write(username);
			bufferWriter.newLine();
			bufferWriter.flush();
			
			Scanner sc = new Scanner(System.in);
			while(socket.isConnected()) {
				String message = sc.nextLine();
				bufferWriter.write(username + ": " + message);
				bufferWriter.newLine();
				bufferWriter.flush();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listenFromGroup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(socket.isConnected()) {
						String messageFromGroup = bufferedReader.readLine();
						System.out.println(messageFromGroup);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", 1234);
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter your username: ");
			String username = sc.nextLine();
			Client client = new Client(socket, username);
			client.listenFromGroup();
			client.sendMessage();
		} catch(IOException e ) {
			e.printStackTrace();
		}
		
	}
}
