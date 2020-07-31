package server;

import java.net.*;
import java.io.*;

 
public class ServerThread extends Thread {
    private Socket socket = null;
 
    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
    }
     
    public void run() {
 
        try (
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	DataInputStream in2 = new DataInputStream(socket.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            //Protocol proto = new Protocol();
            inputLine = in2.readUTF();
            System.out.println(inputLine);
            //outputLine = proto.processInput(inputLine);
            //out.writeUTF(outputLine);
            //out.flush();
            
            while (inputLine != null) {
                inputLine = in2.readUTF();
                System.out.println(inputLine);
                //outputLine = proto.processInput(inputLine);
                //out.writeUTF(outputLine);
                //out.flush();
                //if (outputLine.equals("Bye"))
                //    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}