//
// @author BrandonSchurman
//
package networking;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


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

        WSocket s_socket = new WSocket(8080);
        s_socket.listen();

        while ( true ) {

            Message msg = null;
            
            try {
                msg = s_socket.receive();
            } catch ( MessageCorruptException e ) {
                ////
                // The checksums did not match!
                System.err.println(e);
                msg = new Message(
                        Message.Method.POST,
                        "ERROR",
                        "corrupted message");
            }

            // send until client has received
            boolean msg_rcvd = false;
            while ( !msg_rcvd ) {
                try { 
                    s_socket.sendTo(msg, msg.getSenderPort());
                    msg_rcvd = true;
                } catch ( SocketTimeoutException e ) {
                    System.err.println(e);
                    msg_rcvd = false;
                }
            }
        }
    }

    /**
     * This method can be called from a background thread
     * that handles network interactions with the server.
     * This way, the GUI thread would not be blocked.
     */ 
    public static void processResponse ( Message msg ) { 
        System.out.println("\nResponse from server: "+msg+"\n");

        if ( msg.getType().equals("ERROR") ) {
            System.out.println("an error occurred "
                    + "while processing your vote. "
                    + "please try again.  "
                    + "if this message persists, "
                    + "contact the system administrator.");
        }
    }

    /**
     * Say this is an event handler for a button in the GUI
     */
    public static void userPressedVoteButton() {
        try { 
            Message req = new Message(
                    Message.Method.GET,
                    "echo-request",
                    "echo");

            ////
            // run this in a background thread
            // as to not BLOCK the GUI thread
            new Thread( new Runnable() {
                @Override public void run() {
                    try { 
                        Message res = c_socket.sendReceive(req);
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

        c_socket = new WSocket(8080);
        c_socket.connect();


        // ...

        while ( true ) { 
            // pretend this is a running GUI update loop 
            // or something

            userPressedVoteButton();

            try { 
                Thread.sleep(3000);
            } catch ( Exception e ) {
                ;;
            }
        }
    }
}