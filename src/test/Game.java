package test;

import java.util.ArrayList;
import java.util.Random;

public class Game {
	public static final int WAITING = 0;
	public static final int PLAYING = 1;
	public static final int ENDING = 2;
	
	private String roomName;
	private int maxNumPlayers;
	private int status;
	
	private int round = 1;
	private int stage = 0;
	private int hostPlayerNum = 0;
	private String answer;
	private String koreanAnswer;
	
	private GameManager gameMngr = null;
	
	private int finalRound = 3;
	
	private ArrayList<Player> playerList = new ArrayList<Player>();
	
	

	public Game (String name, int maxPlayers) {
		roomName = name;
		maxNumPlayers = maxPlayers;
		status = WAITING;
	}

	
	public void setGameManager (GameManager gameMngr) {
		this.gameMngr = gameMngr;
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
			String[] args = new String[3];
			args[0] = p.getNumber() + "";
			args[1] = p.getId();
			args[2] = p.getResId() +"";

			player.sendMessage("S2P_CLIENT_NUMBER", args);
			
			if (p != player) {
				args[0] = player.getNumber() + "";
				args[1] = player.getId();
				args[2] = player.getResId() +"";
				p.sendMessage("S2P_CLIENT_NUMBER", args );
			}
		}
	}
	
	public void processReadyGame (int number) {
		
		String arg;
		arg = "" + number;
		
		boolean isAllReady = true;
		for (Player p : playerList) {
			if (p.isReady() == false)
				isAllReady = false;
		}
		
		if(!isAllReady) {
			broadcastMesg("S2P_PLAYER_GAME_READY", arg);
		}
		
		if (isAllReady) {
			System.out.println ("-------------> All Ready  \n");
			status = PLAYING;
			broadcastMesg ("S2P_START_GAME", playerList.size() +"");
		}
	}
	
	public void processSendHitListEnd (String[] mesg){
		broadcastMesg ("S2P_RECV_HINT_LIST_END", mesg);
		
	}
	
	public void processSendHitList (String[] mesg){
		broadcastMesg ("S2P_RECV_HINT_LIST", mesg);
		
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
		
		stage = 0;
		answer = generateAnswer ();
		playerList.get(hostPlayerNum).setAnswerChance(false);
		
		String[] args = new String[1];
		args[0] = answer;
		playerList.get(hostPlayerNum).sendMessage("S2P_RECV_ANSWER", args);
		for (Player p : playerList) {
			if (p != playerList.get(hostPlayerNum)) {
				p.sendMessage("S2P_RECV_HINT_READY");
				p.setAnswerChance(true);
			}
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
		
		gameMngr.removeGame(this);
	}
	
	private void hitAnswer (int number, String answer) {
		String[] args = new String[playerList.size()+1];
		
		// TODO: �젏�닔 怨꾩궛 
		playerList.get(number).addScore(10);
		playerList.get(hostPlayerNum).addScore(5);
		
		args[0] = number + "";
		for (int i = 0; i < playerList.size(); i++) {
			args[i+1] = "" + playerList.get(i).getScore();
		}
		broadcastMesg ("S2P_CORRECT_ANSWER", args);
		
		if (checkEndOfGame ())
			endGame ();
		else {
			newProblem ();	
		}
		
	}
	
	
	
	public void processSendGuessAnswer (int number, String playerAnswer) {
		String[] args = new String[3];
		args[0] = number + "";
		args[1] = playerAnswer;
		
		
		String[] answerList = {"Giraffe", "Hedgehog", "Leopard", "Cat", 
				"Raccon", "Lion", "Pigeon", "Rabbit",
				"Wolf", "Dog", "Smartphone", "Elephant",
				"Butterfly", "Strawberry", "Blueberry"};
		
		
		if (answer.compareTo(playerAnswer) == 0 || koreanAnswer.compareTo(playerAnswer)==0) {
			args[2] = "1"; // Correct answer
			System.out.printf ("Ohhh.. the player [%s] hits the answer !!\n", playerList.get(number).getId());

		}
		else {
			args[2] = "0"; // Wrong answer
			System.out.printf ("No.. the player [%s] gives a wrong answer !!\n", playerList.get(number).getId());
		}
		
		broadcastMesg ("S2P_RECV_GUESS_ANSWER", args);			//정답인가 오답인가 보내줌

		
		if (answer.compareTo(playerAnswer) == 0 || koreanAnswer.compareTo(playerAnswer)==0) {				//정답이라면	
			hitAnswer (number, playerAnswer);
		}
		else {													//오답이라면
			playerList.get(number).setAnswerChance (false);
			boolean allWrongAnswer = true;
			for (Player p : playerList) {
				if (p.getAnswerChance() == true)
					allWrongAnswer = false;
			}
			
			if (allWrongAnswer) { // Everyone gets wrong answer
				processAnswerTimeOver ();
			}
		}
		
	}
	
	public void processAnswerTimeOver () {
		System.out.println ("stage = "+ stage + "\n");
		
		if(stage == 1) {
			
			broadcastMesg("S2P_WRONG_ANSWER");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (checkEndOfGame ())
				endGame ();
			else {
				newProblem ();	
			}
		}
		else {
			stage++;
			String[] args = new String[1];
			args[0] = stage + "";
			
			for (Player p : playerList)
				p.setAnswerChance(true);
			playerList.get(hostPlayerNum).setAnswerChance(false);
			
			broadcastMesg("S2P_NEW_STAGE", args);
		}
		
	}

	
	private void initGame () {
		round = 1;
		stage = 0;
		hostPlayerNum = 0;

		for (Player p : playerList)
			p.setAnswerChance(true);
		playerList.get(hostPlayerNum).setAnswerChance(false);
	}
	
	private String generateAnswer () {
		String[] answerList = {"Giraffe", "Hedgehog", "Leopard", "Cat", 
								"Raccon", "Lion", "Pigeon", "Rabbit",
								"Wolf", "Dog", "Smartphone", "Elephant",
								"Butterfly", "Strawberry", "Blueberry"};
		
		String[] koreanAnswerList = {"기린", "고슴도치","표범","고양이","너구리","사자","비둘기","토끼","늑대","개","스마트폰","코끼리","나비","딸기","블루베리"};
		
		Random rand = new Random ();
		
		int n = rand.nextInt (answerList.length);
		this.koreanAnswer = koreanAnswerList[n];
		
		return answerList[n];
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
			playerList.get(hostPlayerNum).sendMessage("S2P_RECV_ANSWER", args);	//문제담당
			for (Player p : playerList) {
				if (p != playerList.get(hostPlayerNum))
					p.sendMessage("S2P_RECV_HINT_READY");						//나머지
			}
			
		}
	}
	
	public void broadcastMesg (String type, String data) {
		
		System.out.println ("Broadcast : " + type + "\n");
		for (Player player : playerList) 
			player.sendMessage(type, data);
	}
	public void broadcastMesg (String type) {
		
		System.out.println ("Broadcast : " + type + "\n");
		for (Player player : playerList) 
			player.sendMessage(type);
	}
	public void broadcastMesg (String type, String[] data) {
		
		System.out.println ("Broadcast : " + type + "\n");
		for (Player player : playerList) 
			player.sendMessage(type, data);
	}
	
}
//