package server;

import java.net.*;
import java.io.*;

 
public class ServerThread extends Thread {
    private Socket socket = null;
    private Protocol proto;
 
    public ServerThread(Socket socket, Protocol proto) {
        super("ServerThread");
        this.socket = socket;
        this.proto = proto;
    }
     
    public void run() {
        	
			try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
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
                    outputLine = proto.processInput(inputLine);
                    out.writeUTF(outputLine);
                    out.flush();
                    if (outputLine.equals("Bye"))
                        break;
                }
            }
            socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}