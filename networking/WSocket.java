//
// @author Brandon Schurman
//
package networking;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 * A Wrapper-Socket to simulate 
 * a TCP socket using UDP
 */
public class WSocket
{
    public final int TIMEOUT = 10000; 
    public final int PACKET_LEN = 500; 
    public final int FRAG_LEN = 80;

    private DatagramSocket socket;
    private InetAddress addr;
    private int port;


    /**
     * e.g. 4444
     */
    public WSocket ( int port ) {
        this("localhost", port);
    }

    public WSocket ( String host, int port ) {
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
    
    private void sendConfirmation ( int port, InetAddress host ) throws IOException {
    	 Message confirm = new Message(
         		Message.Method.POST, 
         		"%%received%%", 
         		"%%received%%");
         confirm.setSender(this.port, this.addr);
         byte[] buffer = confirm.getBytes();
         DatagramPacket packet = new DatagramPacket(
                 buffer,
                 buffer.length,
                 host,
                 port);
         socket.send(packet);
    }

    public Message receive() 
    throws IOException {
        byte[] buffer = new byte[576];
        Message msg = null;
        while ( msg == null ) {
        	try { 	
        		DatagramPacket response = new DatagramPacket(
		                    buffer,
		                    buffer.length,
		                    this.socket.getInetAddress(),
		                    this.socket.getLocalPort());
		        socket.receive(response);
		        msg = new Message(response.getData());
		        msg.setSender(response.getPort(), response.getAddress());
	        } catch ( MessageCorruptException e ) { 
	        	System.out.println(e);
	        }
        }
        
        this.sendConfirmation(msg.getSenderPort(), msg.getSenderHost());

        if ( msg.getType().equals("%%start-fragments%%") )
            msg = receiveFragments();

        return msg;
    }

    public void send ( Message msg )     
    throws IOException {
        this.sendTo(msg, this.port, this.addr);
    }

    public void sendTo ( Message msg, int port ) 
    throws IOException {
        this.sendTo(msg, port, this.addr);
    }
            
    public void sendTo ( Message msg, int port, InetAddress host ) 
    throws IOException {

    	msg.setSender(this.socket.getLocalPort(), this.socket.getInetAddress());
        final byte[] data = msg.getBytes();

        if ( !msg.getType().equals("%%fragment%%") && data.length > PACKET_LEN ) {
        	System.out.println("fragments...");
            this.sendFragments(msg, port, host);
            return;
        }
        
        boolean msg_rcvd = false;
        
        while ( !msg_rcvd ) { 
        	try { 
		        DatagramPacket request = new DatagramPacket(
		                data, 
		                data.length,
		                host,
		                port);
		        socket.send(request); 
		        byte[] buffer = new byte[PACKET_LEN];
		        DatagramPacket response = new DatagramPacket(
		                    buffer,
		                    buffer.length,
		                    this.socket.getLocalAddress(),
		                    this.socket.getLocalPort());
		        socket.receive(response);
		        Message res = new Message(response.getData());
		        res.setSender(response.getPort(), response.getAddress());
		        if ( res.getData().equals("%%received%%") )
		        	msg_rcvd = true;
		       else 
		        	System.out.println("huh? "+msg);
		        msg_rcvd = true;
        	} catch ( SocketTimeoutException e ) { 
        		System.out.println(e);
        	} catch ( MessageCorruptException e ) {
        		System.out.println(e);
        	}
        }
    }

    public Message sendReceive ( Message msg, int port, InetAddress host ) 
    throws IOException {

        Message res = null;
        boolean msg_rcvd = false; 

        while ( !msg_rcvd ) {
            try {

                this.sendTo(msg, port, host);
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
    
    public Message sendReceive ( Message msg ) throws IOException {
    	return this.sendReceive(msg, this.port, this.addr);
    }
    
    private Message receiveFragments() 
    throws IOException { 
    	 int port = -10;
    	 InetAddress host = null;
    	 
         ArrayList<Byte> total_data = new ArrayList<Byte>();
         
         boolean done_receiving = false;
         while ( !done_receiving ) {
        	 Message msg = this.receive();
        	 
        	 if ( port == -10 ) {
        		 port = msg.getSenderPort();
        		 host = msg.getSenderHost();
        	 }
        	 System.out.println("receive fragment: "+msg);
        	 
        	 if ( msg.getType().equals("%%fragment%%") ) {
	        	 byte[] frag = (byte[])msg.getData();
	        	 for ( int i=0; i<frag.length; i++ )
	        		 total_data.add(frag[i]);
        	 } else if ( msg.getType().equals("%%done%%") ) {
        		 done_receiving = true;
        	 } else { 
        		 System.out.println("error, should be of type %%done%% or %%fragment%% " + msg);
        	 }
         }
         byte[] bytes = new byte[total_data.size()];
         for ( int i=0; i<bytes.length; i++ ) 
        	 bytes[i] = total_data.get(i);
         
         Message msg = null;
         try {
        	 msg = new Message(bytes);
        	 msg.setSender(port, host);
         } catch ( Exception e ) {
        	 e.printStackTrace();
         }
         return msg;
    }
    
    private void sendFragments ( Message msg, int port, InetAddress host ) 
    throws IOException { 
    	
        byte[] data = msg.getBytes();
        boolean done_sending = false;
        int pos = 0; 

        Message note = new Message(
                Message.Method.POST,
                "%%start-fragments%%",
                "%%start-fragments%%");
        this.sendTo(note, port, host);

        while ( !done_sending ) {
            byte[] frag 
                = (pos > data.length - FRAG_LEN) 
                ?   new byte[data.length-pos]
                :   new byte[FRAG_LEN];

            for ( int i=0; i<FRAG_LEN; i++ ) { 
                if ( i+pos < data.length ) 
                    frag[i] = data[pos+i];
                else  
                    done_sending = true;
            }

            pos += FRAG_LEN;

            //System.out.println("seding fragment ");
            Message msg_frag = new Message(
                    msg.getMethod(),
                    "%%fragment%%",
                    frag);
            this.sendTo(msg_frag, port, host);
            boolean msg_rcvd = false;

	        if ( done_sending ) {
	            Message done = new Message(
	                    Message.Method.POST,
	                    "%%done%%",
	                    "%%done%%");
	            this.sendTo(done, port, host);
            }
	        
        }
    }
}
