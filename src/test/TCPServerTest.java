package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
//system.out.println
public class TCPServerTest {
	
	
	// 김경훈 수정.
	private static final int PORT = 8000;
	//////////////
	public static GameManager gameManager = new GameManager();
	///
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Game game = new Game ();
		game.setRoomName("Room1");
		gameManager.addGame(game);
		
		game = new Game ();
		game.setRoomName("Room2");
		gameManager.addGame(game);
		
		game = new Game ();
		game.setRoomName("Room3");
		gameManager.addGame(game);
		
		
		
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket (PORT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println ("서버 (port = "+ PORT + ") started.");
			System.out.println ("     Waiting for a connection...");
			
			while (true) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println ("----> Accepted ");
				
				Player player = new Player (socket);
				PlayerMesgRecvThread thread = new PlayerMesgRecvThread (gameManager, game, player, socket);
				thread.start();
				//game.addPlayer(player);
				
					
			}
		
	}

}
//