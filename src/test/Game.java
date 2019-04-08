package test;

import java.util.ArrayList;

public class Game {

	private String roomName;
	private int round = 1;
	private int stage = 1;
	private int hostPlayerNum = 0;
	private String answer;
	
	private ArrayList<Player> playerList = new ArrayList<Player>();
	
	public void setRoomName (String _roomName) {
		roomName = _roomName;
	}
	
	public String getRoomName () {
		return roomName;
	}
	
	public void addPlayer (Player player) {
		
		player.setNumber(playerList.size());
		playerList.add(player);
		
	}
	
	
	public void processNewClient (Player player) {
		
		
		for (Player p : playerList) {
			String[] args = new String[2];
			args[0] = player.getNumber() + "";
			args[1] = player.getId();
			
			p.sendMessage("S2P_CLIENT_NUMBER", args);
			
			if (p != player) {
				player.sendMessage("S2P_CLIENT_NUMBER", args );
			}
		}
	}
	
	public void processReadyGame () {
		
		boolean isAllReady = true;
		for (Player p : playerList) {
			if (p.isReady() == false)
				isAllReady = false;
		}
		
		if (isAllReady) {
			System.out.println ("-------------> All Ready  ");
			broadcastMesg ("S2P_START_GAME");
		}
	}
	
	public void processSendHitList (String[] mesg)
	{
		//mesg ���� Player�� ���� �޽��� �״�� ����
		// mesg = P2S_SEND_HIT_LIST, stage, Hin1, Hint2, Hint3
		
		//String returnMesgData = mesg[1] + " " + mesg[2] + " " + mesg[3] + " " + mesg[4];
		broadcastMesg ("S2P_RECV_HINT_LIST", mesg);
		//System.out.println ("---> " + returnMesgData);
		
	}
	
	public void processSendGameReadyChat (int number, String chatData) {
		String[] args = new String[2];
		args[0] = number + "";
		args[1] = chatData;
		
		broadcastMesg ("S2P_SEND_GAME_READY_CHAT", args);
	}
	
	private void newGame () {
		if (hostPlayerNum == playerList.size() - 1) {
			round ++;
			String[] args = new String[1];
			args[0] = round + "";
			broadcastMesg ("S2P_NEW_ROUND", args);
			hostPlayerNum = 0;
		}
		else 
			hostPlayerNum ++;
		
		answer = generateAnswer ();
		
		String[] args = new String[1];
		args[0] = answer;
		playerList.get(hostPlayerNum).sendMessage("S2P_RECV_ANSWER", args);
		for (Player p : playerList) {
			if (p != playerList.get(hostPlayerNum))
				p.sendMessage("S2P_RECV_HINT_READY");
		}
	}
	
	public void processSendGuessAnswer (int number, String playerAnswer) {
		
		String[] args = new String[2];
		args[0] = number + "";
		args[1] = playerAnswer;
		broadcastMesg ("S2P_RECV_GUESS_ANSWER", args);

		if (answer.compareTo(playerAnswer) == 0) {
			System.out.printf ("Ohhh.. the player [%s] hits the answer !!\n", playerList.get(number).getId());
			// ���߸� CORRECT_ANSWER number ����
			playerList.get(number).addScore(10);
			
			args = new String[2];
			args[0] = number + "";
			args[1] = "" + playerList.get(number).getScore();
			broadcastMesg ("S2P_CORRECT_ANSWER", args);
			
			newGame ();
		}
		else {
			System.out.printf ("No.. the player [%s] gives a wrong answer !!\n", playerList.get(number).getId());
		}
		
	}
	
	private void initGame () {
		round = 1;
		stage = 1;
		hostPlayerNum = 0;

		
	}
	
	private String generateAnswer () {
		String[] answerList = {"Girin", "Tiger", "Pig", "Lion", "Cat", "Dog", "Mouse"};
		
		return "Girin";
	}
	
	public void processReadyPlay () {
		boolean isAllReady = true;
		for (Player p : playerList) {
			if (p.isPlayReady() == false)
				isAllReady = false;
		}
		
		if (isAllReady) {
			System.out.println ("-------------> All Ready for Play  ");
			initGame ();
			answer = generateAnswer ();
			
			String[] args = new String[1];
			args[0] = answer;
			playerList.get(hostPlayerNum).sendMessage("S2P_RECV_ANSWER", args);
			for (Player p : playerList) {
				if (p != playerList.get(hostPlayerNum))
					p.sendMessage("S2P_RECV_HINT_READY");
			}
			
		}
	}
	public void broadcastMesg (String type) {
		
		System.out.println ("Broadcast : " + type);
		for (Player player : playerList) 
			player.sendMessage(type);
	}
	public void broadcastMesg (String type, String[] data) {
		
		System.out.println ("Broadcast : " + type);
		for (Player player : playerList) 
			player.sendMessage(type, data);
	}
}
//