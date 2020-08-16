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
    
    messageReader messages;
    Thread thread;
    
    public Controller(int PLAYER_SIZE) {
    	this.PLAYER_SIZE = PLAYER_SIZE;
    	gameState = new GameState(PLAYER_SIZE);
    	gui = new GUI(this, gameState);
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
	
	void Disconnect() throws IOException {
		messages.runThread = false;
		this.sendLeave();
		out.close();
		in.close();
		socket.close();
	}

	public class messageReader implements Runnable {
		DataInputStream in;
		GameState state;
		volatile boolean runThread;
		
		public messageReader(DataInputStream in, GameState state) {
			this.in = in;
			this.state = state;
		}
		
		public void messageProcessor(byte[] data) {
			if(data[0] == Messages.JOIN.ordinal()) {
				
				int newID = data[1];
				int newX = data[2];
				int newY = data[3];
				
				this.state.newPlayer(newID, new Point(newX, newY));
			} else if(data[0] == Messages.PLAYER_MOVED.ordinal()) {
				int moveID = (int) data[1];
				int moveX = (int) data[2];
				int moveY = (int) data[3];
				if(checkIfExist(moveID)) {
					this.state.movePlayer(moveID, moveX, moveY);
				} else {
					this.state.newPlayer(moveID, new Point(moveX, moveY));
				}
			} else if(data[0] == Messages.RESET.ordinal()) {
				int resetID = (int) data[1];
				int resetX = (int) data[2];
				int resetY = (int) data[3];
				if(!checkIfExist(resetID)) {
					this.state.newPlayer(resetID, new Point(resetX, resetY));
				} else {
					this.state.movePlayer(resetID, resetX, resetY);
				}
			} else if(data[0] == Messages.LEAVE.ordinal()) {
				int leaveID = data[1];
				if(checkIfExist(leaveID)) {
					this.state.removePlayer(leaveID);
				}
			}
			setChanged();
			notifyObservers();
		}
		
		public void run() {
	        byte[] data = new byte[4];
			System.out.println("Starting messageReader...");
			this.runThread = true;
	        while(runThread) {
	        	if(!socket.isClosed()) {
		            try {
			            in.read(data);
			            if(data != null) {
			            	messageProcessor(data);
			            }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
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
		out.writeByte(Messages.JOIN.ordinal());
		byte[] data = new byte[4];
		in.read(data);
		System.out.println("Recieved after join: " + data[0] + " " + data[1] + " " + data[2]);
		int playerID = data[1];
		int playerX = data[2];
		int playerY = data[3];
		
		gameState.newPlayer(playerID, new Point(playerX, playerY));
		gameState.setPlayerID(playerID);
		
		messages = new messageReader(this.in, this.gameState);
		thread = new Thread(messages);
		thread.start();
	}
	
	public void sendMove(int direction) throws IOException {
		byte[] data = new byte[4];
		data[0] = (byte) Messages.MOVE.ordinal();
		data[1] = (byte) gameState.getPlayerID();
		data[2] = (byte) direction;
		out.write(data);
	}
	
	public void sendLeave() throws IOException {
		byte[] data = new byte[2];
		data[0] = (byte) Messages.LEAVE.ordinal();
		data[1] = (byte) gameState.getPlayerID();
		out.write(data);
	}
	
}
