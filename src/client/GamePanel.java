package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import shared.GameState;

import java.awt.*;
import javax.swing.*;
public class GamePanel extends JPanel implements Observer {
	
	GameState gameState;
	
	public GamePanel(GameState gameState) {
		this.gameState = gameState;
		Dimension d = new Dimension(300, 300);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setMaximumSize(d);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		this.repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintCircles(g);
	}
	
	private void paintCircles(Graphics g) {
		for(int i = 0; i < gameState.getPlayers().size(); i++) {
			g.fillOval((this.getWidth()/gameState.getPlayerSize())*gameState.getPlayers().get(i).getLocation().x,
					(this.getHeight()/gameState.getPlayerSize())*gameState.getPlayers().get(i).getLocation().y,
					(this.getWidth()/gameState.getPlayerSize()),
					(this.getHeight()/gameState.getPlayerSize()));
		}
	}
}
