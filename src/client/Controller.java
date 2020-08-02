package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import client.GUI;
import shared.GameState;

public class Controller {
	
	GameState gameState;
    GUI gui;
    int PLAYER_SIZE;
    
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    
    public Controller(int PLAYER_SIZE) {
    	this.PLAYER_SIZE = PLAYER_SIZE;
    	gameState = new GameState(PLAYER_SIZE);
    	gui = new GUI(this, gameState);
    	//initKeys();
    }
    
	void Connect(String ipPort) throws IOException { 
		String toParse = ipPort;
		String delims = "[:]";
		String[] parsed = toParse.split(delims);
        String hostName = parsed[0];
        int portNumber = Integer.parseInt(parsed[1]);
 
        try {
            socket = new Socket(hostName, portNumber);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
	}

	public class messageReader implements Runnable {
		DataInputStream in;
		GameState state;
		
		public messageReader(DataInputStream in, GameState state) {
			this.in = in;
			this.state = state;
		}
		
		public void messageProcessor(String input) {
			String[] message = input.split(" ");
			if(message[0].contains("_NEWPLAYER_")) {
				String[] temp = input.split(" ");
				int newID = Integer.parseInt(temp[1]);
				int newX = Integer.parseInt(temp[2]);
				int newY = Integer.parseInt(temp[3]);
				System.out.println("Adding " + newID + " " + newX + " " + newY);
				this.state.newPlayer(newID, new Point(newX, newY));
				
			} else if(message[0].contains("_MOVE_")) {
				String[] moveValues = input.split(" ");
				int moveID = Integer.parseInt(moveValues[1]);
				int moveX = Integer.parseInt(moveValues[2]);
				int moveY = Integer.parseInt(moveValues[3]);
				System.out.println(checkIfExist(moveID));
				if(checkIfExist(moveID)) {
					this.state.movePlayer(moveID, moveX, moveY);
				} else {
					System.out.println("Adding " + moveID + " " + moveX + " " + moveY);
					this.state.newPlayer(moveID, new Point(moveX, moveY));
					this.state.movePlayer(moveID, moveX, moveY);
				}
			}
		}
		
		public void run() {
        String inputLine;
		System.out.println("Starting messageReader...");
        for(;;) {
            try {
				inputLine = in.readUTF();
	            if(inputLine != null) {
	                System.out.println(inputLine);
	                messageProcessor(inputLine);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        }
	}
	
	public boolean checkIfExist(int ID) {
		for(int i = 0; i < gameState.numberOfPlayers(); i++) {
			if(gameState.getPlayers().get(i).getID() == ID) {
				return true;
			}
		}
		return false;
	}
	
	public void sendJoin() throws IOException {
		String message = "_JOIN_";
		out.writeUTF(message);
		String answer = in.readUTF();
		System.out.println(answer);
		String[] playerValues = answer.split(" ");
		int playerID = Integer.parseInt(playerValues[1]);
		int playerX = Integer.parseInt(playerValues[2]);
		int playerY = Integer.parseInt(playerValues[3]);
		
		gameState.newPlayer(playerID, new Point(playerX, playerY));
		gameState.setPlayerID(playerID);
		
        new Thread(new messageReader(this.in, this.gameState)).start();
	}
	
	public void sendMove(int direction) throws IOException {
		String message = "_MOVE_REQ_ " + String.valueOf(gameState.getPlayerID()) + " " + String.valueOf(direction);
		out.writeUTF(message);
	}
	
}
