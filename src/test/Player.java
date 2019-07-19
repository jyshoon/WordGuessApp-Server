package test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Player {
	

	private String id;
	private int resId;
	private int number;
	private Socket sock;
	private boolean isReady = false;
	private boolean isPlayReady = false;
	private int score = 0;
	private BufferedWriter bw = null;
	private boolean hasAnswerChance = true;
	
	
	public Player (Socket s) {
		sock = s;
		
		try {
			bw = new BufferedWriter (new OutputStreamWriter (
					sock.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	void initGame () {
		isReady = false;
		isPlayReady = false;
		score = 0;
		hasAnswerChance = true;
	}
	
	public void setAnswerChance (boolean flag) {
		hasAnswerChance = flag;
	}
	
	public boolean getAnswerChance () {
		return hasAnswerChance;
	}
	
	public void sendMessage (String type, String data) {
	
		String mesg;
		mesg = type ;
		mesg += "####" + data;
		
		try {
			
			System.out.printf("Send [%s]\n\n", mesg);
			bw.write(mesg);
			bw.newLine();
			bw.flush();
			
			//Thread.sleep (200);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public void sendMessage (String type) {
		try {
			
			System.out.printf("Send [%s]\n\n", type);
			bw.write(type);
			bw.newLine();
			bw.flush();
			
			//Thread.sleep (200);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void sendMessage (String type, String[] data) {
	
		String mesg;
		mesg = type ;
		for (int i = 0; i < data.length; i++) 
			mesg += "####" + data[i];
		
		try {
			
			System.out.printf("Send [%s]\n\n", mesg);
			bw.write(mesg);
			bw.newLine();
			bw.flush();
			
			//Thread.sleep (200);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public int getScore () {
		return score;
	}
	
	public void addScore (int point) {
		score += point;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Socket getSock() {
		return sock;
	}
	public void setSock(Socket sock) {
		this.sock = sock;
	}
	public boolean isPlayReady() {
		return isPlayReady;
	}
	public void setPlayReady(boolean isReady) {
		this.isPlayReady = isReady;
	}
	
	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public void setResId(int myResId) {
		resId = myResId;
	}
	
	public int getResId () {
		return resId;
	}

}
//