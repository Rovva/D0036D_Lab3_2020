package client;

import java.awt.Point;

public class Player {
	
	int id;
	Point location;
	
	Player(int id, Point location) {
		this.id = id;
		this.location = location;
	}
	
	public int getID() {
		return id;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public void setLocation(Point newLocation) {
		this.location = newLocation;
	}
}
