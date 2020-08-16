package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import client.Controller;
import shared.GameState;

public class GUI implements Observer, ActionListener {
	
	Controller controller;
	GameState gameState;
	GamePanel gamePanel;
	
	JFrame frame;
	JPanel panel;
	SpringLayout layout;
	Container contentPane;
	JButton connectButton;
	JButton disconnectButton;
	
	int x_size = 500, y_size = 500;
	
	public GUI(Controller controller, GameState gameState) {
		this.controller = controller;
		this.gameState = gameState;
		frame = new JFrame("Stickman Tournament");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(x_size, y_size);
		frame.setVisible(true);
		layout = new SpringLayout();
		contentPane = frame.getContentPane();
		contentPane.setLayout(layout);
		contentPane.setBackground(Color.WHITE);

		this.connectButton = new JButton("Connect");
		this.disconnectButton = new JButton("Disconnect");
		
		this.contentPane.add(connectButton);
		this.contentPane.add(disconnectButton);
		
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);

		connectButton.setEnabled(true);
		disconnectButton.setEnabled(false);
		
		this.layout.putConstraint(SpringLayout.NORTH, connectButton, 5, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, connectButton, 5, SpringLayout.WEST, contentPane);
		
		this.layout.putConstraint(SpringLayout.NORTH, disconnectButton, 5, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, disconnectButton, -5, SpringLayout.EAST, contentPane);
		
		this.gamePanel = new GamePanel(gameState);
		this.contentPane.add(gamePanel);
		this.gamePanel.setFocusable(true);

        gameState.addObserver(gamePanel);
        gameState.addObserver(this);
        
		this.layout.putConstraint(SpringLayout.NORTH, gamePanel, 5, SpringLayout.SOUTH, connectButton);
	}
	/**/
	
	public void addMoveListener(KeyListener movelistener) {
		gamePanel.grabFocus();
		gamePanel.addKeyListener(movelistener);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String op = arg0.getActionCommand();
		if(op == "Connect") {
			//String ipadress = JOptionPane.showInputDialog(frame,
            //        "Address:port", null);
			try {
				controller.Connect("127.0.0.1:4444");
				controller.sendJoin();
				connectButton.setEnabled(false);
				disconnectButton.setEnabled(true);
				initKeys();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(op == "Disconnect") {
			
		}
	}
    private void initKeys() {
    	this.addMoveListener(
    			new KeyListener() {
    				@Override
    				public void keyPressed(KeyEvent ke) {
    			        int keyCode = ke.getKeyCode();
    			        int direction = 0;
    			        if(keyCode == KeyEvent.VK_LEFT) {
    			        	System.out.println("Left");
    			        	//gameState.movePlayer(1);
    			        	direction = 1;
    			        } else if(keyCode == KeyEvent.VK_UP) {
    			        	System.out.println("UP");
    			        	//gameState.movePlayer(2);
    			        	direction = 2;
    			        } else if(keyCode == KeyEvent.VK_RIGHT) {
    			        	System.out.println("Right");
    			        	//gameState.movePlayer(3);
    			        	direction = 3;
    			        } else if(keyCode == KeyEvent.VK_DOWN) {
    			        	System.out.println("Down");
    			        	//gameState.movePlayer(4);
    			        	direction = 4;
    			        }
			        	try {
							controller.sendMove(direction);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		frame.repaint();
	}
}
