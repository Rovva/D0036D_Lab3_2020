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

import client.Model;

public class GUI implements Observer, ActionListener {
	
	Model mod;
	GameState gameState;
	GamePanel gamePanel;
	
	JFrame frame;
	JPanel panel;
	SpringLayout layout;
	Container contentPane;
	
	int x_size = 300, y_size = 300;
	
	public GUI(Model mod, GameState gameState) {
		this.mod = mod;
		this.gameState = gameState;
		frame = new JFrame("Stickman Tournament");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(x_size, y_size);
		frame.setVisible(true);
		layout = new SpringLayout();
		contentPane = frame.getContentPane();
		contentPane.setLayout(layout);
		contentPane.setBackground(Color.WHITE);

		JButton connectButton = new JButton("Connect");
		JButton disconnectButton = new JButton("Disconnect");
		
		this.contentPane.add(connectButton);
		this.contentPane.add(disconnectButton);
		
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);
		
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
			String ipadress = JOptionPane.showInputDialog(frame,
                    "Address:port", null);
			try {
				mod.connectToServer(ipadress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
	}
}
