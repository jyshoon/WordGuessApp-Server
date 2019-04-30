package test;

import java.util.ArrayList;

public class GameManager {
	private ArrayList<Game> gameList = new ArrayList<Game> ();
	
	public void addGame (Game game) {
		gameList.add(game);
		game.setGameManager(this);
	}
	
	public void removeGame (Game game) {
		gameList.remove(game);
	}
	
	public Game findGame (String roomName) {
	
		for (Game game : gameList) {
			if (game.getRoomName().compareTo(roomName) == 0 )
				return game;
		}
		return null;
	}
	
	
	public String[] getRoomList () {
		
		String[] roomList = new String[gameList.size()];
		
		int i = 0;
		for (Game g : gameList) {
			roomList[i] = g.getRoomName();
			roomList[i] += "::" + g.getCurrentNumPlayers();
			roomList[i] += "/" + g.getMaxNumPlayers();
			switch (g.getStatus()) {
			case Game.WAITING:
				roomList[i] += "::WAITING"; break;
			case Game.PLAYING:
				roomList[i] += "::PLAYING"; break;
			case Game.ENDING:
				roomList[i] += "::ENDING"; break;
			}
			i++;
		}
		
		return roomList;
	}
	
}
//
