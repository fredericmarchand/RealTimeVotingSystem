//
// @author Brandon Schurman
//
package networking;
import java.net.*;
import java.io.*;

/**
 * A Wrapper-Socket to simulate 
 * a TCP socket using UDP
 */
public class WSocket
{
    public final int TIMEOUT = 10; 

    private DatagramSocket socket;
    private InetAddress addr;
    @SuppressWarnings("unused")
	private String host;
    private int port;
    @SuppressWarnings("unused")
	private int senderID;

    public WSocket () {
    	
    }
    
    public WSocket ( int port ) {
        this("localhost", port);
    }

    public WSocket ( String host, int port ) {
        this.host = host;
        this.port = port;
        
        try {
			addr = InetAddress.getByName(host);
		} catch ( UnknownHostException e ) {
			System.err.println("\nerror resolving host\n");
			e.printStackTrace();
			System.exit(-1);
		}
    }

    public void listen() {
        try { 
            this.socket = new DatagramSocket(port, addr);
            //this.socket.setSoTimeout(TIMEOUT);
        } catch ( SocketException e ) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            this.socket = new DatagramSocket(); 
            this.port = socket.getPort();
            this.addr = socket.getInetAddress();
            this.host = socket.getInetAddress().toString();
            // set a timeout on the socket
            this.socket.setSoTimeout(TIMEOUT); 
        } catch ( SocketException e ) {
            e.printStackTrace();
        } 
    }

    public void close() { 
        if ( socket != null ) 
            socket.close();
    }

	public Message receive() 
    throws IOException, MessageCorruptException {
		byte[] buffer = new byte[576];
		DatagramPacket response 
		    = new DatagramPacket(
		            buffer,
		            buffer.length);
		socket.receive(response);
        Message msg = new Message(response.getData());
        msg.setSender(response.getPort(), response.getAddress());
		return msg;
	}
    
    public void send ( Message msg )     
    throws IOException, SocketTimeoutException {
        this.sendTo(msg, this.port, this.addr);
    }

    public void sendTo ( Message msg, int port ) 
    throws IOException, SocketTimeoutException {
        this.sendTo(msg, port, this.addr);
    }

    public void sendTo ( Message msg, int port, InetAddress host ) 
    throws IOException, SocketTimeoutException {
        byte[] data = msg.getBytes();
    	DatagramPacket request = new DatagramPacket(
                data, 
                data.length,
                host,
                port);
        socket.send(request); 
    }

    public Message sendReceive ( Message msg ) 
    throws IOException, MessageCorruptException {

        Message res = null;
        boolean msg_rcvd = false; 

        while ( !msg_rcvd ) {
            try {
                
                this.send(msg);

                res = this.receive();

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
