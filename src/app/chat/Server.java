package app.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
	private ServerSocket serverSocket;
	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void startServer() {
		try {
			while(!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println("A new client join room");
				ClientHandler client = new ClientHandler(socket);
				Thread thread = new Thread(client);
				thread.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeServer() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(1234);
			Server server = new Server(serverSocket);
			server.startServer();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
