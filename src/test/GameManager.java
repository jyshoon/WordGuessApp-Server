package test;

import java.util.ArrayList;

public class GameManager {
	private ArrayList<Game> gameList = new ArrayList<Game> ();
	
	public void addGame (Game game) {
		gameList.add(game);
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
		
		for (int i = 0; i < gameList.size(); i++) {
			
			roomList[i] = gameList.get(i).getRoomName();
		}
		
		return roomList;
	}
}
//
