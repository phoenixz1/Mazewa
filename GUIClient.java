/*
Copyright (C) 2004 Geoffrey Alan Washburn
      
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
      
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
      
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.
*/
import java.net.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * An implementation of {@link LocalClient} that is controlled by the keyboard
 * of the computer on which the game is being run.  
 * @author Geoffrey Washburn &lt;<a href="mailto:geoffw@cis.upenn.edu">geoffw@cis.upenn.edu</a>&gt;
 * @version $Id: GUIClient.java 343 2004-01-24 03:43:45Z geoffw $
 */

public class GUIClient extends LocalClient implements KeyListener {
		int defaultport= 8002;
        /**
         * Create a GUI controlled {@link LocalClient}.  
         */
        public GUIClient(String name, int ctype, String hostname, int port, ScoreTableModel model) {
	    super(name, ctype, hostname, port, model);
        }
        
        /**
         * Handle a key press.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyPressed(KeyEvent e) {
                //Create a new packet to be sent out to the server
                MazewarPacket outPkt = new MazewarPacket();
                outPkt.cID = this.getName();
                outPkt.event = e;
                
                if((e.getKeyChar() == 'q') || (e.getKeyChar() == 'Q')) {
			if(this.isleader == true && LocalClient.clients.size() > 1) {
				//try{
				// send MW_ELECTION packet to the next client
				//ObjectOutputStream nextstream = new ObjectOutputStream(LocalClient.nextclientSkt.getOutputStream());
				MazewarPacket electionPkt = new MazewarPacket();
				electionPkt.type = MazewarPacket.MW_ELECTION;
				electionPkt.nextclient = this.nextclient;
				if(this.nextclient != null) {
						
						this.p2pthreads.get(nextclient).send(electionPkt);
					
				}
				
				// stop tickerthread
				LocalClient.ticker.notinterrupted = false;		
			}
                        outPkt.type = MazewarPacket.MW_BYE;
			outPkt.nextclient = LocalClient.nextclient;
                }
                else {
                        outPkt.type = MazewarPacket.MW_REQUEST;
                }
		try{
			this.outQueue.put(outPkt); // queue this packet to be multicasted
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		System.out.println("added key events. outqueue size = "+ outQueue.size()+" , Inqueue size = "+ inQueue.size());
                //this.inQueue.add(outPkt);
                // Send the packet out through the socket's output stream
//		try {                
			//this.outStream.writeObject(outPkt);
//			this.outQueue.add(outPkt); // queue this packet to be multicasted
//		} catch (IOException ex) {
//		 System.err.println("ERROR: Couldn't get I/O for the connection.");
//		 System.exit(1);
//		}
        }
        
        /**
         * Handle a key release. Not needed by {@link GUIClient}.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyReleased(KeyEvent e) {
        }
        
        /**
         * Handle a key being typed. Not needed by {@link GUIClient}.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyTyped(KeyEvent e) {
        }
        
        /**
         * Send a packet to the Mazewar server indicating that you want to join other clients in the Mazewar game
         */
        public void joinOtherClients(){
              // Create a packet to send to the server
	    try {
              MazewarPacket pktToServ = new MazewarPacket();

              pktToServ.type = MazewarPacket.JOIN_SERV;
              pktToServ.cID = this.getName();
              pktToServ.newsocket = new SocketInfo(InetAddress.getLocalHost(), defaultport);//new client address
              
              // Send the packet through the socket's output stream

              	LocalClient.outStream.writeObject(pktToServ);
	      }
	      catch (IOException e) {
		 System.err.println("ERROR: Couldn't get I/O for the connection3.");
		 e.printStackTrace();
	       }
	      
        }

}
