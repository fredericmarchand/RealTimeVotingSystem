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
    public static final int PACKET_LEN = 500; 
    public static final int FRAG_LEN = 200;
    
    private int TIMEOUT = 10000;

    private DatagramSocket socket;
    private InetAddress addr;
    private int port;
    
    
    public WSocket listen ( int port, String host ) 
	throws UnknownHostException, SocketException {
        this.socket = new DatagramSocket(port, addr);
    	this.port = port;
    	this.addr = InetAddress.getByName(host);
    	return this;
    }

    public WSocket listen ( int port ) 
    throws UnknownHostException, SocketException {
    	return this.listen(port, "localhost");
    }
    
    public WSocket connect ( int port, String host )
    throws UnknownHostException, SocketException { 
         this.socket = new DatagramSocket(); 
         this.port = port;
         this.addr = InetAddress.getByName(host);
         return this;
    }

    public WSocket connect ( int port )
	throws UnknownHostException, SocketException {
       return this.connect(port, "localhost");
    }
    
    public void setTimeout( int timeout ) {
    	this.TIMEOUT = timeout;
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

    public synchronized Message receive() 
    throws IOException {
        byte[] buffer = new byte[576];
        Message msg = null;
        while ( msg == null ) {
        	try { 	
        		socket.setSoTimeout(1000000000);
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
    	System.out.println(port + " " + this.addr.toString());
        this.sendTo(msg, port, this.addr);
    }
            
    public synchronized void sendTo ( Message msg, int port, InetAddress host ) 
    throws IOException {

    	msg.setSender(this.socket.getLocalPort(), this.socket.getInetAddress());
        final byte[] data = msg.getBytes();

        if ( !msg.getType().equals("%%fragment%%") && data.length > PACKET_LEN ) {
        	//System.out.println("fragments...");
            this.sendFragments(msg, port, host);
            return;
        }
        
        boolean msg_rcvd = false;
        
        while ( !msg_rcvd ) { 
        	try { 
        		socket.setSoTimeout(TIMEOUT);
		        DatagramPacket request = new DatagramPacket(
		                data, 
		                data.length,
		                host,
		                port);
		        socket.send(request); 
		        byte[] buffer = new byte[PACKET_LEN];
		        // receive confirmation
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
        	socket.setSoTimeout(1000000);
        }
    }

    public synchronized Message sendReceive ( Message msg, int port, InetAddress host ) 
    throws IOException {

        this.sendTo(msg, port, host);
        Message res = this.receive();

        return res;
    }
    
    public Message sendReceive ( Message msg ) throws IOException {
    	return this.sendReceive(msg, port, addr);
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
        	 //System.out.println("receive fragment: "+msg);
        	 
        	 if ( msg.getType().equals("%%fragment%%") ) {
	        	 byte[] frag = (byte[])msg.getData();
	        	 for ( int i=0; i<frag.length; i++ )
	        		 total_data.add(frag[i]);
        	 } else if ( msg.getType().equals("%%done%%") ) {
        		 done_receiving = true;
        	 } else { 
        		 System.err.println("error, should be of type %%done%% or %%fragment%% " + msg);
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

            //System.out.println("sending fragment ");
            Message msg_frag = new Message(
                    msg.getMethod(),
                    "%%fragment%%",
                    frag);
            this.sendTo(msg_frag, port, host);

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
