import java.io.*;
import java.net.*;


public class MazewarServer {

    // create a global queue to store the incoming client packets
    public static final Queue<MazewarPacket> serverIncomingQueue = new LinkedList<MazewarPacket>();

    // global List of threads serving clients
    public static final List<MazewarServerHandlerThread> clients = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
        	if(args.length == 1) {
        		serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        	} else {
        		System.err.println("ERROR: Invalid arguments!");
        		System.exit(-1);
        	}
        } catch (IOException e) {
            System.err.println("ERROR: Could not listen on port!");
            System.exit(-1);
        }


        while (listening) {

        	MazewarServerHandlerThread client = new MazewarServerHandlerThread(serverSocket.accept()).start();
		clients.add(client);
        }
        serverSocket.close();
    }

   
}