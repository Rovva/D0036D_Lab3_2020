package server;

import java.net.*;
import java.io.*;

 
public class ServerThread extends Thread {
    private Socket socket = null;
    private Protocol proto;
    private DataOutputStream out;
 
    public ServerThread(Socket socket, Protocol proto) {
        super("ServerThread");
        this.socket = socket;
        this.proto = proto;
    }
    
    public void sendMessage(String message) throws IOException {
    	out.writeUTF(message);
    	out.flush();
    }
     
    public void run() {
        	
			try {
			out = new DataOutputStream(socket.getOutputStream());
	        DataInputStream in = new DataInputStream(socket.getInputStream());
            String inputLine, outputLine;
            //inputLine = in.readUTF();
           /* System.out.println(inputLine);
            outputLine = proto.processInput(inputLine);
            out.writeUTF(outputLine);
            out.flush();
            */
            for(;;) {
                inputLine = in.readUTF();
                if(inputLine != null) {
                    System.out.println(inputLine);
                    proto.processInput(inputLine, this);
                }
            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}