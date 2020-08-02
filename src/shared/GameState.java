package shared;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

public class GameState extends Observable {
		
	ArrayList<Player> players = new ArrayList<Player>();
	int PLAYER_SIZE;
	int playerID;
	
	public GameState(int PLAYER_SIZE) {
		this.PLAYER_SIZE = PLAYER_SIZE;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void setPlayerID(int ID) {
		this.playerID = ID;
	}
	
	public int getPlayerSize() {
		return PLAYER_SIZE;
	}
	
	public int numberOfPlayers() {
		return this.players.size();
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	
	public void newPlayer(int ID, Point location) {
		this.players.add(new Player(ID, location));
		System.out.println(players.size());
		setChanged();
        notifyObservers();
	}
	
	public void removePlayer(int ID) {
		
	}
	
	public void movePlayer(int id, int x, int y) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == id) {
				players.get(i).setLocation(new Point(x, y));
			}
		}
		setChanged();
        notifyObservers();
	}
}
