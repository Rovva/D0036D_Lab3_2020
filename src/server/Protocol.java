package server;

import shared.GameState;
import shared.Messages;

import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import server.ServerThread;

public class Protocol {
	
	GameState serverGameState;
	int portNumber;
	ArrayList<ServerThread> threads;
	
	public Protocol(int portNumber) {
		serverGameState = new GameState(30);
		this.portNumber = portNumber;
		this.threads = new ArrayList<ServerThread>();
		startServer();
	}
	
	private void startServer() {
		boolean listening = true;
		try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) { 
			int i = 0;
            while (listening) {
            	System.out.println("Listening and stuff at port: " + this.portNumber);
                threads.add(new ServerThread(serverSocket.accept(), this));			//.start() Kör Run() i ServerThread
                threads.get(i).start();
                i++;
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + this.portNumber);
            System.exit(-1);
        }
	}
	
	private void sendToAll(byte[] data) throws IOException {
		for(int i = 0; i < threads.size(); i++) {
			threads.get(i).sendMessage(data);
		}
	}
	
	private byte[] newPlayer() {
		int ID, x, y;
		Random randX = new Random();
		Random randY = new Random();
		x = randX.nextInt(30);
		y = randY.nextInt(30);
		ID = serverGameState.numberOfPlayers();
		System.out.println("THE ID IS " + ID);
		serverGameState.newPlayer(ID, new Point(x, y));
		//int[] returnvalue = {ID, x, y};
		byte[] data = new byte[4];
		data[0] = (byte) Messages.JOIN.ordinal();
		data[1] = (byte) ID;
		data[2] = (byte) x;
		data[3] = (byte) y;
		return data;
		//return String.valueOf(ID) + " " + String.valueOf(x) + " " + String.valueOf(y);
	}
	
	private boolean canPlayerMove(int ID, int direction) {
		int originalX = 0, originalY = 0, newX = 0, newY = 0;
		
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		newX = originalX;
		newY = originalY;
		if(direction == 1) {
			//newX = originalX - 1;
			newX--;
		} else if(direction == 2) {
			//newY = originalY - 1;
			newY--;
		} else if(direction == 3) {
			//newX = originalX + 1;
			newX++;
		} else if(direction == 4) {
			//newY = originalY + 1;
			newY++;
		}
		
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getLocation().x == newX && 
					serverGameState.getPlayers().get(i).getLocation().y == newY) {
				return false;
			}
		}
		return true;
	}
	
	public byte[] movePlayer(int ID, int direction) {
		int originalX = 0, originalY = 0, newX = 0, newY = 0;
		
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		
		newX = originalX;
		newY = originalY;
		
		if(direction == 1) {
			newX = originalX - 1;
		} else if(direction == 2) {
			newY = originalY - 1;
		} else if(direction == 3) {
			newX = originalX + 1;
		} else if(direction == 4) {
			newY = originalY + 1;
		}
		byte[] returnValue = {(byte) ID,(byte) newX,(byte) newY};
		serverGameState.getPlayers().get(ID).setLocation(new Point(newX, newY));
		return returnValue;
	}
	
	public void resetGame() throws IOException {
		int x = 0, lastX = 0, y = 0, lastY = 0, ID = 0;
		ArrayList<Integer> listX = new ArrayList<Integer>();
		ArrayList<Integer> listY = new ArrayList<Integer>();
		Random rand = new Random();
		ArrayList<Point> placesTaken = new ArrayList<Point>();
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			x = rand.nextInt(30);
			y = rand.nextInt(30);
			listX.add(x);
			listY.add(y);
			placesTaken.add(new Point(x, y));
		}
		byte[] sendValues = new byte[4];
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			ID = serverGameState.getPlayers().get(i).getID();
			serverGameState.getPlayers().get(i).setLocation(placesTaken.get(i));
			sendValues[0] = (byte) Messages.RESET.ordinal();
			sendValues[1] = (byte) ID;
			sendValues[2] = (byte) listX.get(i).byteValue();
			sendValues[3] = (byte) listY.get(i).byteValue();
			sendToAll(sendValues);
			//sendToAll("_RESET_ " + String.valueOf(ID) + " " + String.valueOf(listX.get(i)) + 
			//		" " + String.valueOf(listY.get(i)));
		}
		
		
	}
	
    public void processInput(byte[] data, ServerThread lol) throws IOException {
    	System.out.println("Server got: " + (int) data[0]);
    	//String[] message = theInput.split(" ");
    	
    	if((int) data[0] == Messages.JOIN.ordinal()) {
    		System.out.println("Adding new player...");
    		byte[] newplayer = newPlayer();
    		//String returnvalue = "_NEWPLAYER_ " + newplayer;
    		//System.out.println("Server: " + returnvalue);
    		sendToAll(newplayer);
    		resetGame();
    	} else if((int) data[0] == Messages.MOVE.ordinal()) {
    		
    		if(canPlayerMove((int) data[1], (int) data[2])) {
    			byte[] temp = new byte[3];
    			byte[] movePlayer = new byte[4];
    			temp = movePlayer((int) data[1], (int) data[2]);
    			movePlayer[0] = (byte) Messages.PLAYER_MOVED.ordinal();
    			movePlayer[1] = temp[0];
    			movePlayer[2] = temp[1];
    			movePlayer[3] = temp[2];
    			sendToAll(movePlayer);
    		}
    		
    		//String[] split = theInput.split(" ");
    		//if(canPlayerMove(Integer.parseInt(split[1]), Integer.parseInt(split[2]))) {
    		//	String moveplayer = movePlayer(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    		//	System.out.println("_MOVE_ " + moveplayer);
    			//sendToAll("_MOVE_ " + moveplayer);
    	//	}
    	}
    }
    
    
}
