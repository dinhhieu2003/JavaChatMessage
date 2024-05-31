package app.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
	
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket client;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;
	
	public ClientHandler(Socket client) {
		try {
			this.client = client;
			this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			this.clientUsername = bufferedReader.readLine();
			clientHandlers.add(this);
			broadcastMessage("SERVER: " + clientUsername + " entered room");
		} catch(IOException e) {
			e.printStackTrace();
			closeAll(client, bufferedReader, bufferedWriter);
		}
	}
	
	private void removeClient() {
		clientHandlers.remove(this);
		broadcastMessage("SERVER: " + clientUsername + " has left");
	}
	
	private void closeAll(Socket socket2, BufferedReader bufferedReader2, BufferedWriter bufferedWriter2) {
		removeClient();
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

	private void broadcastMessage(String messageToSend) {
		for(ClientHandler clientHandler: clientHandlers) {
			try {
				if(!clientHandler.clientUsername.equals(this.clientUsername)) {
					clientHandler.bufferedWriter.write(messageToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			} catch(IOException e) {
				e.printStackTrace();
				closeAll(client, bufferedReader, bufferedWriter);
			}
		}
	}

	@Override
	public void run() {
		String messageFromClient;
		while(client.isConnected()) {
			try {
				messageFromClient = bufferedReader.readLine();
				broadcastMessage(messageFromClient);
			} catch(IOException e) {
				e.printStackTrace();
				closeAll(client, bufferedReader, bufferedWriter);
				break;
			}
		}
	}

}
