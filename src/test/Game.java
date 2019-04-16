package test;

import java.util.ArrayList;

public class Game {
	public static final int WAITING = 0;
	public static final int PLAYING = 1;
	public static final int ENDING = 2;
	
	private String roomName;
	private int maxNumPlayers;
	private int status;
	
	private int round = 1;
	private int stage = 1;
	private int hostPlayerNum = 0;
	private String answer;
	
	private int finalRound = 3;
	
	private ArrayList<Player> playerList = new ArrayList<Player>();

	public Game (String name, int maxPlayers) {
		roomName = name;
		maxNumPlayers = maxPlayers;
		status = WAITING;
	}
	
	public int getMaxNumPlayers () {
		return maxNumPlayers;
	}
	
	public int getStatus () {
		return status;
	}
	
	public void setRoomName (String _roomName) {
		roomName = _roomName;
	}
	
	public String getRoomName () {
		return roomName;
	}
	
	public int getCurrentNumPlayers () {
		return playerList.size();
	}
	
	public void addPlayer (Player player) {
		
		player.setNumber(playerList.size());
		playerList.add(player);
		
	}
	
	
	public void processNewClient (Player player) {
		
		
		for (Player p : playerList) {
			String[] args = new String[2];
			args[0] = p.getNumber() + "";
			args[1] = p.getId();
			
			player.sendMessage("S2P_CLIENT_NUMBER", args);
			
			if (p != player) {
				args[0] = player.getNumber() + "";
				args[1] = player.getId();
				p.sendMessage("S2P_CLIENT_NUMBER", args );
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
			status = PLAYING;
			broadcastMesg ("S2P_START_GAME");
		}
	}
	
	public void processSendHitList (String[] mesg)
	{
		//mesg 占쏙옙占쏙옙 Player占쏙옙 占쏙옙占쏙옙 占쌨쏙옙占쏙옙 占쌓댐옙占� 占쏙옙占쏙옙
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
	
	private void newProblem () {
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
	
	private boolean checkEndOfGame ()
	{
		if (round == finalRound && hostPlayerNum == playerList.size() - 1) 
			return true;
		else
			return false;
	}
	
	private void endGame ()
	{
		String[] args = new String[2 * playerList.size()];
		int i = 0;
		
		for (Player p : playerList) {
			args[i++] = p.getNumber() + "";
			args[i++] = p.getScore() + "";
		}
		broadcastMesg ("S2P_END_GAME", args);
	}
	
	private void hitAnswer (int number, String answer) {
		String[] args = new String[2];
		
		// TODO: �젏�닔 怨꾩궛 
		playerList.get(number).addScore(10);
		
		args[0] = number + "";
		args[1] = "" + playerList.get(number).getScore();
		broadcastMesg ("S2P_CORRECT_ANSWER", args);
		
		if (checkEndOfGame ())
			endGame ();
		else {
			newProblem ();	
		}
		
	}
	public void processSendGuessAnswer (int number, String playerAnswer) {
		
		String[] args = new String[2];
		args[0] = number + "";
		args[1] = playerAnswer;
		broadcastMesg ("S2P_RECV_GUESS_ANSWER", args);

		if (answer.compareTo(playerAnswer) == 0) {
			System.out.printf ("Ohhh.. the player [%s] hits the answer !!\n", playerList.get(number).getId());
			// 占쏙옙占쌩몌옙 CORRECT_ANSWER number 占쏙옙占쏙옙
			
			hitAnswer (number, playerAnswer);
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