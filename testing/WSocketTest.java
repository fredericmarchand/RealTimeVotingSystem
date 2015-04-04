//
// @author BrandonSchurman
//
package testing;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import networking.Message;
import networking.WServerSocket;
import networking.WSocket;
import model.*;


/**
 * This class shows the intended use of the WSocket class
 */
public class WSocketTest
{
    private static WSocket c_socket;

    /**
     * runs a simple echo server
     * also shows how to check CRC checksums on server side
     */
    public static void echoServer() throws IOException { 

        WServerSocket s_socket = new WServerSocket(8080, "localhost");

        while ( true ) {

        	final WSocket client_conn = s_socket.accept();
        	
        	new Thread( new Runnable() { 
        		@Override public void run() {
                    try {
                    	while ( true ) {
	                    	Message msg = client_conn.receive();
							client_conn.sendTo(msg, msg.getSenderPort());
                    	}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}).start();
        }
    }

    /**
     * This method can be called from a background thread
     * that handles network interactions with the server.
     * This way, the GUI thread would not be blocked.
     */ 
    public static void processResponse ( Message msg ) {
        // display response to user
        System.out.println("Response from server: "+msg+"\n");
    }

    /**
     * Say this is an event handler for a button in the GUI
     */
    public static void userPressedVoteButton() {
        try { 

        	ArrayList<Vote> big_data = new ArrayList<Vote>(10000);
        	
        	for ( int i=0; i<10000; i++ ) 
        		big_data.add(new Vote(
        				new Voter("Ronald", "McDonald", new Address(), 199299399), 
        				new Candidate("George", "Bush", new Address(), 616717818)));
        	
            final Message req = new Message(
                    Message.Method.GET,
                    "test",
                    "test");

            final WSocket socket = c_socket;
            
            ////
            // run this in a background thread
            // as to not BLOCK the GUI thread
            new Thread( new Runnable() {
                @Override public void run() {
                    try { 
                    	// will take a while to process with extremely large data sets
                        Message res = socket.sendReceive(req);
                        processResponse(res);
                    } catch ( Exception e ) {
                        System.err.println(
                                "error sending request");
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }


    /**
     * shows general intended usage of WSocket
     */
    public static void main ( String args[] ) {

        ////
        // run a simple echo server in the background
        new Thread(new Runnable() {
            @Override public void run() {
                try { 
                    echoServer();
                } catch ( IOException e ) {
                    e.printStackTrace(); 
                }
            }
        }).start();

        ////
        // Pretend main() is a GUI thread!

        try {
			c_socket = new WSocket().connect(8080, "localhost");
		} catch (UnknownHostException | SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


        // ...

        while ( true ) { 
            // pretend this is a running GUI update loop 
            // or something

            userPressedVoteButton();

            try { 
                Thread.sleep(1000);
            } catch ( Exception e ) {
                ;;
            }
        }
    }
}
