package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import client.GUI;
import client.Model;

public class Controller {
	
	GameState gameState;
    GUI gui;
    Model model;
    int PLAYER_SIZE;
    
    public Controller(int PLAYER_SIZE) {
    	this.PLAYER_SIZE = PLAYER_SIZE;
    	gameState = new GameState(PLAYER_SIZE);
    	model = new Model(gameState);
    	gui = new GUI(model, gameState);
    	initKeys();
    }
    
    private void initKeys() {
    	gui.addMoveListener(
    			new KeyListener() {
    				@Override
    				public void keyPressed(KeyEvent ke) {
    			        int keyCode = ke.getKeyCode();
    			        if(keyCode == KeyEvent.VK_LEFT) {
    			        	System.out.println("Left");
    			        	//gameState.movePlayer(1);
    			        } else if(keyCode == KeyEvent.VK_UP) {
    			        	System.out.println("UP");
    			        	//gameState.movePlayer(2);
    			        } else if(keyCode == KeyEvent.VK_RIGHT) {
    			        	System.out.println("Right");
    			        	//gameState.movePlayer(3);
    			        } else if(keyCode == KeyEvent.VK_DOWN) {
    			        	System.out.println("Down");
    			        	//gameState.movePlayer(4);
    			        }
    			      }

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}
    			});
    }

}
