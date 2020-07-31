package client;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import shared.GameState;
import shared.Messages;

public class Model extends Observable {
	
    String fromServer;
    String fromUser;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    
    int playerID;
    
    GameState gameState;
    
	public Model(GameState gameState) {
		this.gameState = gameState;
	}
	
	void connectToServer(String ipadress) throws IOException {
		// Store response from server
		//String status = "Connected";
		Connect(ipadress);
		//return status;
	}
    
	void Connect(String ipPort) throws IOException{ 
		String toParse = ipPort;
		String delims = "[:]";
		String[] parsed = toParse.split(delims);
        String hostName = parsed[0];
        int portNumber = Integer.parseInt(parsed[1]);
 
        try {
            socket = new Socket(hostName, portNumber);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
	}
	
	public void sendJoin() throws IOException {
		String message = "JOIN";
		out.writeUTF(message);
		String answer = in.readUTF();
		System.out.println(answer);
		String[] playerValues = answer.split(" ");
		gameState.newPlayer(Integer.parseInt(playerValues[1]), new Point(Integer.parseInt(playerValues[2]), Integer.parseInt(playerValues[3])));
		this.playerID = Integer.parseInt(playerValues[1]);
	}
	
	public void sendMove(int direction) throws IOException {
		String message = "MOVE_REQ " + String.valueOf(this.playerID) + " " + String.valueOf(direction);
		out.writeUTF(message);
		String answer = in.readUTF();
		System.out.println(answer);
		String[] moveValues = answer.split(" ");
		if(moveValues[0].contains("MOVE")) {
			gameState.movePlayer(Integer.parseInt(moveValues[1]), Integer.parseInt(moveValues[2]), Integer.parseInt(moveValues[3]));
		}
	}
}
