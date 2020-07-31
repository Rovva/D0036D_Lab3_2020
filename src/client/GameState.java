package client;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import client.Player;

public class GameState extends Observable {
		
	ArrayList<Player> players = new ArrayList<Player>();
	int PLAYER_SIZE;
	
	public GameState(int PLAYER_SIZE) {
		this.PLAYER_SIZE = PLAYER_SIZE;
//		players.add(new Player(0, new Point(1,1)));
//		players.add(new Player(1, new Point(2,10)));
	}
	
	public int getPlayerSize() {
		return PLAYER_SIZE;
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	
	public void newPlayer() {
		
	}
	
	public void removePlayer() {
		
	}
	
	public void movePlayer(int id, int direction) {
		int x,y;
		x = getPlayers().get(id).getLocation().x;
		y = getPlayers().get(id).getLocation().y;
		if(direction == 1) {
			x = x - 1;
		} else if(direction == 2) {
			y = y - 1;
		} else if(direction == 3) {
			x = x + 1;
		} else if(direction == 4) {
			y = y + 1;
		}
		getPlayers().get(id).setLocation(new Point(x, y));
		setChanged();
        notifyObservers();
	}
}
