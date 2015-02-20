//
// @author Brandon Schurman
//
import java.net.*;
import java.io.*;

public class ClientService
{
    public final int TIMEOUT = 10; 

    private DatagramSocket socket;
    private InetAddress addr;
    private String host;
    private int port;
    private int senderID;


    /**
     * e.g. 4444
     */
    public ClientService ( int port ) {
        this("localhost", port);
    }

    public ClientService ( String host, int port ) {
        this.host = host;
        this.port = port;
        this.senderID = (int)(Math.random()*100000); 
        
        try {
			addr = InetAddress.getByName(host);
		} catch ( UnknownHostException e ) {
			System.err.println("\nerror resolving host\n");
			e.printStackTrace();
			System.exit(-1);
		}
    }

    public void connect() {
        try {
            this.socket = new DatagramSocket(); 
            // set a timeout on the socket
            this.socket.setSoTimeout(TIMEOUT); 
        } catch ( SocketException e ) {
            e.printStackTrace();
        } 
    }

    public void disconnect() { 
        if ( socket != null ) 
            socket.close();
    }

	public Message receiveResponse() 
    throws IOException, ClassNotFoundException {
		byte[] buffer = new byte[576];
		DatagramPacket response 
		    = new DatagramPacket(
		            buffer,
		            buffer.length);
		socket.receive(response);
		return new Message(response.getData());
	}
    
    public void sendRequest ( Message msg )
    throws IOException, SocketTimeoutException {
        byte[] data = msg.getBytes();
    	DatagramPacket request = new DatagramPacket(
                data, 
                data.length,
                addr,
                port);
        socket.send(request);
    }

    public Message sendReceive ( Message msg ) 
    throws IOException, ClassNotFoundException {

        Message res = null;
        boolean msg_rcvd = false; 

        while ( !msg_rcvd ) {
            try {
                
                sendRequest(msg);

                res = receiveResponse();

                msg_rcvd = true;

            } catch ( SocketTimeoutException e ) {
                System.out.println("err: "
                        + "server did not receive request."
                        + "resending..");
                msg_rcvd = false;
            } 
        }

        return res;
    }
}
