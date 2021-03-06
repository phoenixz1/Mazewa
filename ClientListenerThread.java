import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

public class ClientListenerThread extends Thread {
	
	public ServerSocket listenSocket = null;
	private LinkedBlockingQueue<MazewarPacket> inQueue;
        public static boolean isRunning = true;
	
	public ClientListenerThread (ServerSocket locSocket, LinkedBlockingQueue<MazewarPacket> incomingQueue){
		super("ClientListenerThread");
		listenSocket = locSocket;
		inQueue = incomingQueue;
	}
	
	public void run(){
		
		Socket connSocket = null;
		//ObjectInputStream connInStream = null;
		//ObjectOutputStream connOutStream = null;
		
		// Listen for new clients trying to join the game session
		// If a client wants to connect, create a new ClientReceiverThread for that client
		try {
			while (isRunning) {
				
				connSocket = listenSocket.accept();
				
				//connOutStream = new ObjectOutputStream(connSocket.getOutputStream()); 
				//connInStream = new ObjectInputStream(connSocket.getInputStream());
				System.out.println("Inside listener, creating new thread");
				new ClientReceiverThread(connSocket, inQueue).start();
				//connOutStream.close();
			}
			//Close any connected socket and the listening socket
			connSocket.close();
			listenSocket.close();
		} catch (IOException e) {
				System.err.println("ERROR: Couldn't get I/O for the naming server connection.");
				System.exit(1);
		}
	}

        public static void terminate(){
	    isRunning = false;
	}
}
