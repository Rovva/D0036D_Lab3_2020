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
                threads.add(new ServerThread(serverSocket.accept(), this));
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
		serverGameState.newPlayer(ID, new Point(x, y));
		byte[] data = new byte[4];
		data[0] = (byte) Messages.JOIN.ordinal();
		data[1] = (byte) ID;
		data[2] = (byte) x;
		data[3] = (byte) y;
		return data;
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
			newX--;
		} else if(direction == 2) {
			newY--;
		} else if(direction == 3) {
			newX++;
		} else if(direction == 4) {
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
	
	public int canKillWho(int ID, int direction) {
		int originalX = 0, originalY = 0;
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		
		if(direction == 1) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == (originalX-1) && 
						serverGameState.getPlayers().get(i).getLocation().y == originalY) {
					return i;
				}
			}
		} else if(direction == 2) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == originalX && 
						serverGameState.getPlayers().get(i).getLocation().y == (originalY-1)) {
					return i;
				}
			}
		} else if(direction == 3) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == (originalX+1) && 
						serverGameState.getPlayers().get(i).getLocation().y == originalY) {
					return i;
				}
			}
		} else if(direction == 4) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == originalX && 
						serverGameState.getPlayers().get(i).getLocation().y == (originalY+1)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public boolean checkIfDead(int ID) {
		if(serverGameState.checkDead(ID)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void killPlayer(int ID) {
		serverGameState.getPlayers().get(ID).setDead();
	}
	
	public void removePlayer(int ID) {
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				serverGameState.removePlayer(i);
			}
		}
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
			serverGameState.getPlayers().get(i).setLiving();
			sendValues[0] = (byte) Messages.RESET.ordinal();
			sendValues[1] = (byte) ID;
			sendValues[2] = (byte) listX.get(i).byteValue();
			sendValues[3] = (byte) listY.get(i).byteValue();
			sendToAll(sendValues);
		}
		
		
	}
	
    public void processInput(byte[] data, ServerThread thread) throws IOException {
    	if(data[0] == Messages.JOIN.ordinal()) {
    		System.out.println("Adding new player...");
    		byte[] newplayer = newPlayer();
    		sendToAll(newplayer);
    		resetGame();
    	} else if(data[0] == Messages.MOVE.ordinal()) {
    		if(canPlayerMove((int) data[1], (int) data[2])) {
    			if(!checkIfDead((int) data[1])) {
        			byte[] temp = new byte[3];
        			byte[] movePlayer = new byte[4];
        			temp = movePlayer((int) data[1], (int) data[2]);
        			movePlayer[0] = (byte) Messages.PLAYER_MOVED.ordinal();
        			movePlayer[1] = temp[0];
        			movePlayer[2] = temp[1];
        			movePlayer[3] = temp[2];
        			sendToAll(movePlayer);
    			}
    		}
    	} else if(data[0] == Messages.PLAYER_HIT.ordinal()) {
    		int killedID = -1;
    		byte[] killPlayer;
    		
    		for(int i = 1; i <= 4; i++) {
    			killedID = -1;
    			killedID = canKillWho((int) data[1], i);
    			killPlayer = new byte[2];
    			System.out.println("Checking nearby players...");
    			if(killedID != -1) {
    				if(!checkIfDead(killedID)) {
    					System.out.println("Killed player: " + killedID);
        				killPlayer(killedID);
        				killPlayer[0] = (byte) Messages.PLAYER_KILLED.ordinal();
        				killPlayer[1] = (byte) killedID;
        				sendToAll(killPlayer);
    				}
    			}
    		}
    	} else if(data[0] == Messages.LEAVE.ordinal()) {
    		byte[] temp = new byte[2];
    		temp[0] = (byte) Messages.LEAVE.ordinal();
    		temp[1] = data[1];
    		sendToAll(temp);
    		removePlayer((int) data[1]);
    		thread.stopThread();
    		resetGame();
    	}
    }
    
    
}
