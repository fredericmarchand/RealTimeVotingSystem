//
// @author BrandonSchurman
//
import java.io.IOException;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * This class shows the intended use of the ClientService class
 */
public class ClientTest
{
    private static ClientService cs;

    /**
     * runs a simple echo server
     * also shows how to check CRC checksums on server side
     */
    public static void pretendServer() { 

        try {

            DatagramSocket socket = new DatagramSocket(8080);

            while ( true ) {

                byte[] rcv_data = new byte[576];

                DatagramPacket req = new DatagramPacket(
                        rcv_data, 
                        rcv_data.length);
                socket.receive(req);

                Message msg = null;

                ////
                // deserealize the message
                try { 
                    msg = new Message(req.getData());
                } catch ( ClassNotFoundException e ) { 
                    e.printStackTrace();
                    System.exit(-2);
                }

                ////
                // make sure the checksums match!
                if ( Message.calculateChecksum(msg.getData())
                        !=  msg.getChecksum() ) {
                    System.err.println("error: "
                            + "corrupted data detected. "
                            + "checksums do not match!");
                    msg = new Message(
                            Message.Method.POST,
                            "ERROR",
                            "corrupted message");
                        }

                byte[] snd_data = msg.getBytes();

                DatagramPacket res = new DatagramPacket(
                        snd_data,
                        snd_data.length,
                        req.getAddress(),
                        req.getPort());

                socket.send(res);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
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
                        Message res = cs.sendReceive(req);
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
     * shows general intended usage of ClientService
     */
    public static void main ( String args[] ) {

        ////
        // run a pretend echo server in the background
        new Thread(new Runnable() {
            @Override public void run() { 
                pretendServer();
            }
        }).start();

        ////
        // Pretend main() is a GUI thread!

        cs = new ClientService(8080);
        cs.connect();


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
