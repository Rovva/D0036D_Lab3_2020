package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import client.GUI;
import shared.GameState;
import shared.Messages;

public class Controller extends Observable {
	
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
		
		public void messageProcessor(byte[] data) {
			//String[] message = input.split(" ");
			if((int) data[0] == Messages.JOIN.ordinal()) {
				
				System.out.println("Adding in client: " + (int) data[1] + " " + (int) data[2] + " " + (int) data[3]);
				
				int newID = data[1];
				int newX = data[2];
				int newY = data[3];
				
				this.state.newPlayer(newID, new Point(newX, newY));
				/*
				String[] temp = input.split(" ");
				int newID = Integer.parseInt(temp[1]);
				int newX = Integer.parseInt(temp[2]);
				int newY = Integer.parseInt(temp[3]);
				System.out.println("Adding " + newID + " " + newX + " " + newY);
				this.state.newPlayer(newID, new Point(newX, newY));
				*/
			} else if((int) data[0] == Messages.PLAYER_MOVED.ordinal()) {
				int moveID = (int) data[1];
				int moveX = (int) data[2];
				int moveY = (int) data[3];
				System.out.println(checkIfExist(moveID));
				if(checkIfExist(moveID)) {
					this.state.movePlayer(moveID, moveX, moveY);
				} else {
					System.out.println("Adding in client: " + moveID + " " + moveX + " " + moveY);
					this.state.newPlayer(moveID, new Point(moveX, moveY));
				}
				
				/*
				String[] moveValues = input.split(" ");
				int moveID = Integer.parseInt(moveValues[1]);
				int moveX = Integer.parseInt(moveValues[2]);
				int moveY = Integer.parseInt(moveValues[3]);
				System.out.println(checkIfExist(moveID));
				if(checkIfExist(moveID)) {
					this.state.movePlayer(moveID, moveX, moveY);
				} else {
					System.out.println("Adding " + moveID + " " + moveX + " " + moveY);
					//this.state.newPlayer(moveID, new Point(moveX, moveY));
				}
				*/
			} else if(data[0] == Messages.RESET.ordinal()) {
				int resetID = (int) data[1];
				int resetX = (int) data[2];
				int resetY = (int) data[3];
				System.out.println(checkIfExist(resetID));
				if(!checkIfExist(resetID)) {
					this.state.newPlayer(resetID, new Point(resetX, resetY));
				} else {
					this.state.movePlayer(resetID, resetX, resetY);
				}
				
				/*
				String[] resetValues = input.split(" ");
				int resetID = Integer.parseInt(resetValues[1]);
				int resetX = Integer.parseInt(resetValues[2]);
				int resetY = Integer.parseInt(resetValues[3]);
				if(!checkIfExist(resetID)) {
					this.state.newPlayer(resetID, new Point(resetX, resetY));
				} else {
					this.state.movePlayer(resetID, resetX, resetY);
				}
				*/
			} else if(data[0] == Messages.LEAVE.ordinal()) {
				int leaveID = data[1];
				if(checkIfExist(leaveID));
				
				/*
				String[] leaveValues = input.split(" ");
				int leaveID = Integer.parseInt(leaveValues[1]);
				if(checkIfExist(leaveID)) {
					this.state.removePlayer(leaveID);
				}
				*/
			}
			setChanged();
			notifyObservers();
		}
		
		public void run() {
        //String inputLine;
        byte[] data = new byte[4];
		System.out.println("Starting messageReader...");
        for(;;) {
            try {
				/*inputLine = in.readUTF();
	            if(inputLine != null) {
	                System.out.println(inputLine);
	                messageProcessor(inputLine);
	            }*/
	            in.read(data);
	            if(data != null) {
	            	System.out.println(data);
	            	messageProcessor(data);
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

		//String message = "_JOIN_";
		out.writeByte(Messages.JOIN.ordinal());
		//out.writeUTF(message);
		//String answer = in.readUTF();
		byte[] data = new byte[4];
		in.read(data);
		System.out.println("Recieved after join: " + data[0] + " " + data[1] + " " + data[2]);
		//String[] playerValues = answer.split(" ");
		//int playerID = Integer.parseInt(playerValues[1]);
		//int playerX = Integer.parseInt(playerValues[2]);
		//int playerY = Integer.parseInt(playerValues[3]);

		int playerID = data[1];
		int playerX = data[2];
		int playerY = data[3];
		
		gameState.newPlayer(playerID, new Point(playerX, playerY));
		gameState.setPlayerID(playerID);
		
        new Thread(new messageReader(this.in, this.gameState)).start();
	}
	
	public void sendMove(int direction) throws IOException {
		//String message = "_MOVE_REQ_ " + String.valueOf(gameState.getPlayerID()) + " " + String.valueOf(direction);
		//out.writeUTF(message);
		byte[] data = new byte[4];
		data[0] = (byte) Messages.MOVE.ordinal();
		data[1] = (byte) gameState.getPlayerID();
		data[2] = (byte) direction;
		out.write(data);
	}
	
}
