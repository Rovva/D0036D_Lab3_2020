package server;

import shared.GameState;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Protocol {
	
	GameState serverGameState;
	
	public Protocol() {
		serverGameState = new GameState(30);
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
		
		if(direction == 1) {
			newX = originalX - 1;
		} else if(direction == 2) {
			newY = originalY - 1;
		} else if(direction == 3) {
			newX = originalX + 1;
		} else if(direction == 4) {
			newY = originalY + 1;
		}
		
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getLocation().x == newX) {
				return false;
			} else if(serverGameState.getPlayers().get(i).getLocation().y == newY) {
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
	
    public String processInput(String theInput) {
    	System.out.println("Server got: " + theInput);
    	String[] message = theInput.split(" ");
    	if(theInput.contains("_JOIN_")) {
    		String newplayer = newPlayer();
    		String returnvalue = "_NEWPLAYER_ " + newplayer;
    		System.out.println("Server: " + returnvalue);
    		return returnvalue;
    	} else if(message[0].contains("_MOVE_REQ_")) {
    		String[] split = theInput.split(" ");
    		if(canPlayerMove(Integer.parseInt(split[1]), Integer.parseInt(split[2]))) {
    			String moveplayer = movePlayer(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    			System.out.println("_MOVE_ " + moveplayer);
    			return "_MOVE_ " + moveplayer;
    		}
    	}
		return null;
    }
}
