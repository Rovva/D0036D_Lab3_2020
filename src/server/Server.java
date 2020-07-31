package server;
import java.net.*;
import java.io.*;
 
public class Server {
    public static void main(String[] args) throws IOException {
    	
    	int portNumber = 4444; 
        boolean listening = true;
        Protocol proto = new Protocol();
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
            	System.out.println("Listening and stuff at port: " + portNumber);
                new ServerThread(serverSocket.accept(), proto).start();			//.start() K�r Run() i ServerThread
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
