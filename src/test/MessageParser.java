package test;

public class MessageParser {

	public static final int P2S_CONNECT_CLIENT = 100;
	public static final int P2S_READY_GAME = 101;
	public static final int P2S_READY_PLAY = 102;
	public static final int P2S_SEND_HINT_LIST = 103;
	public static final int P2S_SEND_GAME_READY_CHAT = 104;
	public static final int P2S_SEND_GUESS_ANSWER = 105;
	public static final int P2S_REQ_ROOM_LIST = 106;
	public static final int P2S_ENTER_ROOM = 107;
	public static final int P2S_CREATE_ROOM = 108;
	public static final int P2S_SEND_HINT_LIST_END = 109;

	

	public static int getMessageType (String mesg) {
		String type_str = mesg.split("####")[0];
		
		
		if (type_str.compareTo("P2S_REQ_ROOM_LIST") == 0)
			return P2S_REQ_ROOM_LIST;
		
		if (type_str.compareTo("P2S_ENTER_ROOM") == 0)
			return P2S_ENTER_ROOM;
		
		if (type_str.compareTo("P2S_CREATE_ROOM") == 0)
			return P2S_CREATE_ROOM;
		
		if (type_str.compareTo("P2S_CONNECT_CLIENT") == 0)
			return P2S_CONNECT_CLIENT;
		
		if (type_str.compareTo("P2S_READY_GAME") == 0)
			return P2S_READY_GAME;
		
		if (type_str.compareTo("P2S_READY_PLAY") == 0)
			return P2S_READY_PLAY;
		
		if (type_str.compareTo("P2S_SEND_HINT_LIST_END") == 0)
			return P2S_SEND_HINT_LIST_END;
		
		if (type_str.compareTo("P2S_SEND_HINT_LIST") == 0)
			return P2S_SEND_HINT_LIST;
		
		if (type_str.compareTo("P2S_SEND_GAME_READY_CHAT") == 0) 
			return P2S_SEND_GAME_READY_CHAT;
		
		if (type_str.compareTo("P2S_SEND_GUESS_ANSWER") == 0) 
			return P2S_SEND_GUESS_ANSWER;
		return -1;
	}
	
	
	/*
	public static String getMessageData (String mesg, int ith) {
		String arg = mesg.split("$$$$")[ith];
		
		return arg;
	}*/
}
//
