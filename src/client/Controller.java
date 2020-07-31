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
import shared.GameState;

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
    	//initKeys();
    }

}
