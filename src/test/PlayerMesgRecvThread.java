package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class PlayerMesgRecvThread extends Thread {
//
	private Game game = null;
	private Player player = null;
	private InputStream stream = null;
	private BufferedReader br = null;
	private GameManager gameManager = null;
	
	public PlayerMesgRecvThread (GameManager gameManager, Game game, Player player, Socket sock) {
		try {
			stream = sock.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		br = new BufferedReader (
				new InputStreamReader (stream));
	
		this.gameManager = gameManager;
		this.player = player;
		this.game = game;
	}
	
	
	private void process_P2S_CONNECT_CLIENT (String mesg) {
		String myId = MessageParser.getMessageData(mesg, 1);
		
		player.setId(myId);
			
		String data = "";
		data += player.getNumber() + " " + myId;
		
		//player.sendMessage("S2P_CLIENT_NUMBER", data);
		game.processNewClient (player);
	}
	
	private void process_P2S_READY_GAME (String mesg) {
		String data = MessageParser.getMessageData(mesg, 1);
		int number = Integer.parseInt(data);
		
		player.setReady(true);
		game.processReadyGame ();
	}
	
	private void process_P2S_READY_PLAY (String mesg) {
		
		player.setPlayReady(true);
		game.processReadyPlay ();
	}
	
	private void process_P2S_SEND_HINT_LIST (String mesg) {
		String[] parsedStr = mesg.split(" ");
		
		game.processSendHitList (parsedStr);
		
	}
	
	private void process_P2S_SEND_GAME_READY_CHAT (String mesg) {
		String[] parsedStr = mesg.split(" ");

		int number = Integer.parseInt(parsedStr[1]);
		
		String chatData = "";
		for (int i = 2; i < parsedStr.length; i++)
			chatData += parsedStr[i] + " ";
		
		game.processSendGameReadyChat (number, chatData);
		
	}
	
	private void process_P2S_SEND_GUESS_ANSWER (String mesg) {
		String[] parsedStr = mesg.split(" ");
		
		int number = Integer.parseInt(parsedStr[1]);
		String answer = parsedStr[2];
		
		game.processSendGuessAnswer (number, answer);
	}
	
	private void process_P2S_REQ_ROOM_LIST (String mesg) {
		
		String[] roomList = TCPServerTest.gameManager.getRoomList();
		
		String data = "" + roomList.length;
		for (int i = 0; i < roomList.length; i++)
			data += " " + roomList[i];
		
		player.sendMessage ("S2P_SEND_ROOM_LIST", data);
		
	}
	
	private void process_P2S_ENTER_ROOM (String mesg) {
		
		String[] parsedStr = mesg.split(" ");
		
		//TODO: 방번호 해당하는 게임에 player 관리해야함
		
		String roomName = parsedStr[1];
		
		game = gameManager.findGame (roomName); 
		
		
		if (game == null)
			player.sendMessage ("S2P_ENTER_ROOM_FAIL", "");
		else {
			game.addPlayer(player);
			player.sendMessage ("S2P_ENTER_ROOM_OK", "");
		}		
	}
	
	private void process_P2S_CREATE_ROOM (String mesg) {
		String[] parsedStr = mesg.split(" ");
		
		String roomName = parsedStr[1];
		
		game = gameManager.findGame (roomName);
		
		if (game == null) {
			game = new Game ();
			game.setRoomName(roomName);
			gameManager.addGame(game);
			
			game.addPlayer(player);
			
			player.sendMessage ("S2P_CREATE_ROOM_OK", "");
		}
		else {
			player.sendMessage ("S2P_CREATE_ROOM_FAIL", "");
		}
	}
	
	public void run () {
		while (true) {
			String mesg = null;
			try {
				mesg = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			System.out.printf("Recved : [%s] \n", mesg);
			
			switch (MessageParser.getMessageType(mesg)) {
			
			case MessageParser.P2S_REQ_ROOM_LIST:
				process_P2S_REQ_ROOM_LIST (mesg);
				break;
			
			case MessageParser.P2S_ENTER_ROOM:
				process_P2S_ENTER_ROOM (mesg);
				break;
				
			case MessageParser.P2S_CREATE_ROOM:
				process_P2S_CREATE_ROOM (mesg);
				break;
			
			case MessageParser.P2S_CONNECT_CLIENT:
				process_P2S_CONNECT_CLIENT (mesg);
				break;
				
			case MessageParser.P2S_READY_GAME:
				
				process_P2S_READY_GAME (mesg);
				break;
			
			case MessageParser.P2S_READY_PLAY:
				process_P2S_READY_PLAY (mesg);
				break;
			
			case MessageParser.P2S_SEND_HINT_LIST:
				process_P2S_SEND_HINT_LIST (mesg);
				break;
				
			case MessageParser.P2S_SEND_GAME_READY_CHAT:
				process_P2S_SEND_GAME_READY_CHAT (mesg);
				break;
			
			case MessageParser.P2S_SEND_GUESS_ANSWER:
				process_P2S_SEND_GUESS_ANSWER (mesg);
				break;
			}
		}
	}
}
