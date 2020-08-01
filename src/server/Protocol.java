package server;

import shared.GameState;

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
	
	private void sendToAll(String message) throws IOException {
		for(int i = 0; i < threads.size(); i++) {
			threads.get(i).sendMessage(message);
		}
	}
	
	private String newPlayer() {
		int ID, x, y;
		Random randX = new Random();
		Random randY = new Random();
		x = randX.nextInt(30);
		y = randY.nextInt(30);
		ID = serverGameState.numberOfPlayers();
		System.out.println("THE ID IS " + ID);
		serverGameState.newPlayer(ID, new Point(x, y));
		int[] returnvalue = {ID, x, y};
		return String.valueOf(ID) + " " + String.valueOf(x) + " " + String.valueOf(y);
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
	
	public String movePlayer(int ID, int direction) {
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
		int[] returnValue = {ID, newX, newY};
		serverGameState.getPlayers().get(ID).setLocation(new Point(newX, newY));
		return ID + " " + newX + " " + newY;
	}
	
	public void resetGame() {
		int x = 0, y = 0;
		Random rand = new Random();
		ArrayList<Point> placesTaken = new ArrayList<Point>();
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			x = rand.nextInt(30);
			y = rand.nextInt(30);
			placesTaken.add(new Point(x, y));
		}
		
	}
	
    public void processInput(String theInput, ServerThread lol) throws IOException {
    	System.out.println("Server got: " + theInput);
    	String[] message = theInput.split(" ");
    	if(theInput.contains("_JOIN_")) {
    		String newplayer = newPlayer();
    		String returnvalue = "_NEWPLAYER_ " + newplayer;
    		System.out.println("Server: " + returnvalue);
    		sendToAll(returnvalue);
    	} else if(message[0].contains("_MOVE_REQ_")) {
    		String[] split = theInput.split(" ");
    		if(canPlayerMove(Integer.parseInt(split[1]), Integer.parseInt(split[2]))) {
    			String moveplayer = movePlayer(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    			System.out.println("_MOVE_ " + moveplayer);
    			sendToAll("_MOVE_ " + moveplayer);
    		}
    	}
    }
    
    
}
