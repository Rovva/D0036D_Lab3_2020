package shared;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

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
		getPlayers().get(id).setLocation(new Point(x, y));
		setChanged();
        notifyObservers();
	}
	
	/*
	public void movePlayer(int id, int x, int y) {
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
	}*/
}
