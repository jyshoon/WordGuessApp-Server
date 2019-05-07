package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
//system.out.println
public class TCPServerTest {
	
	
	private static int PORT = 8000;
	//////////////
	public static GameManager gameManager = new GameManager();
	///
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Game game = new Game ("Room 1", 4);
		gameManager.addGame(game);
		
		game = new Game ("Room 2", 4);
		gameManager.addGame(game);
		
		game = new Game ("Room 3", 4);
		gameManager.addGame(game);
		
		Scanner sc = new Scanner (System.in);
		System.out.print ("Enter port number : ");
		PORT = sc.nextInt();
		
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket (PORT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println ("�꽌踰� (port = "+ PORT + ") started.");
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