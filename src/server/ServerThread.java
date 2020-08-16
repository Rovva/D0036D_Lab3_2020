package server;

import java.net.*;
import java.io.*;

 
public class ServerThread extends Thread {
    private Socket socket = null;
    private Protocol proto;
    private DataOutputStream out;
    private int playerID;
    
    private boolean runThread;
 
    public ServerThread(Socket socket, Protocol proto) {
        super("ServerThread");
        this.socket = socket;
        this.proto = proto;
        this.runThread = true;
    }
    
    public void sendMessage(byte[] data) throws IOException {
    	if(runThread) {
        	out.write(data);
        	out.flush();
    	}
    }
    
    public void stopThread() {
    	this.runThread = false;
    }
     
    public void run() {
			try {
			out = new DataOutputStream(socket.getOutputStream());
	        DataInputStream in = new DataInputStream(socket.getInputStream());
            while(runThread) {
            	byte[] data = new byte[4];
            	in.read(data);
                if(data != null) {
                    proto.processInput(data, this);
                }
            }
            System.out.println("ServerThread is stopping...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}