import java.net.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class ClientReceiverThread extends Thread {


	public Socket socket = null;
	public ObjectInputStream inStream;
	private Queue<MazewarPacket> inQueue;

	public ClientReceiverThread (Socket socket, Queue<MazewarPacket> incomingQueue, ObjectInputStream inStream) 
	{
		super("ClientReceiverThread");
		this.socket = socket;
		this.inQueue = incomingQueue;
		this.inStream = inStream;

	}
	
	public void run() {
		
	    MazewarPacket packetFromServer;
	    int ricounter = 0;
	    try {
		while ((packetFromServer = (MazewarPacket) inStream.readObject()) != null) {
		    System.out.println("receives a packet of type "+packetFromServer.type);
		    // check acks and unpauses; ACK and RING_UNPAUSE packets are not queued
		    //synchronized(this) {
		    	if(packetFromServer.type == MazewarPacket.RING_UNPAUSE)
		    		LocalClient.ispaused = false;
		    	else if(packetFromServer.type == MazewarPacket.ACK)
			    	LocalClient.ACKnum++;
			else if(packetFromServer.type == MazewarPacket.RING_PAUSE) {
				LocalClient.p2psockets.add(packetFromServer.cID, this.socket);
				inQueue.add(packetFromServer);			
			}
			else if(packetFromServer.type == MazewarPacket.RING_INFO){
				ricounter++;
				inQueue.add(packetFromServer);
				if(ricounter == LocalClient.p2psockets.size()) {
					MazewarPacket np = new MazewarPacket();
					np.type = MazewarPacket.MCAST_REQ;
					inqueue.add(np)
				}
			}
		    	else { 
				inQueue.add(packetFromServer);
				System.out.println("queued packet. Queue size = "+LocalClient.inQueue.size());
                    	}
		}
		// connection ended
                 inStream.close();
                 socket.close();
	    } catch (IOException e) {
		 System.err.println("ERROR: Couldn't get I/O for the connection.");
		 System.exit(1);
	    } catch (ClassNotFoundException e) {
		 System.err.println("ERROR: Cannot find the class.");
		 System.exit(1);
	    }
	}

}
