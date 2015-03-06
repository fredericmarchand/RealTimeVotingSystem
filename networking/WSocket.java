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
    public final int PACKET_LEN = 256; 

    private DatagramSocket socket;
    private InetAddress addr;
    private String host;
    private int port;
    private int senderID;


    /**
     * e.g. 4444
     */
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

        if ( msg.getType().equals("%%start-fragments%%") )
            msg = receiveFragments();

        return msg;
    }

    private Message receiveFragments() 
    throws IOException, MessageCorruptException { 
         
    }

    public void send ( Message msg )     
    throws IOException, SocketTimeoutException {
        this.sendTo(msg, this.port, this.addr);
    }

    public void sendTo ( Message msg, int port ) 
    throws IOException, SocketTimeoutException {
        this.sendTo(msg, port, this.addr);
    }

    private void sendFragments ( 
            Message msg, int port, InetAdress host ) 
    throws IOException, SocketTimeoutException { 
        byte[] data = msg.getBytes();
        boolean done_sending = false;
        int pos = 0; 

        Message note = new Message(
                Message.Method.POST,
                "%%start-fragments%%",
                "%%start-fragments%%");
        byte[] bytes = note.getBytes();
        DatagramPacket packet = new DatagramPacket(
                bytes,
                bytes.length,
                host,
                port);
        socket.send(packet);

        //Message msg = this.receive(); // TODO handle confirmation

        while ( !done_sending ) {
            byte[] frag 
                = (pos > data.length - PACKET_LEN) 
                ?   new byte[data.length-pos]
                :   new byte[PACKET_LEN];

            for ( int i=0; i<PACKET_LEN; i++ ) { 
                if ( i+pos < bytes.length ) 
                    frag[i] = bytes[pos+i];
                else  
                    done_sending = true;
            }

            pos += PACKET_LEN;

            Message msg_frag = new Message(
                    msg.getMethod(),
                    "%%fragment%%",
                    frag);
            byte[] msg_frag_bytes = msg_frag.getBytes();

            boolean msg_rcvd = false;

            while ( !msg_rcvd ) {
                DatagramPacket packet = new DatagramPacket(
                        msg_frag_bytes,
                        msg_frag_bytes.length,
                        host,
                        port);
                socket.send(packet);

                try {
                    Message confrim = this.receive();
                    if ( confirm.getType().equals("%%received%%") )
                        msg_rcvd = true;
                    if ( done_sending ) {
                        Message done = new Message(
                                Message.Method.POST,
                                "%%done%%",
                                "%%done%%");
                        msg_frag_bytes = done.getBytes();
                        packet = new DatagramPacket(
                                msg_frag_bytes,
                                msg_frag_bytes.length,
                                host,
                                post);
                        socket.send(packet);
                    }
                } catch ( SocketTimeoutException e ) { 
                    msg_rcvd = false;
                } catch ( MessageCorruptException e ) {
                    msg_rcvd = false;
                }
            }
        }
    }
            
    public void sendTo ( Message msg, int port, InetAddress host ) 
    throws IOException, SocketTimeoutException {

        byte[] data = msg.getBytes();

        if ( data.length > PACKET_LEN ) {
            this.sendFragments(msg, port, host);
            return;
        }

        DatagramPacket request = new DatagramPacket(
                data, 
                data.length,
                host,
                port);
        socket.send(request); 
    }

    public Message sendReceive ( Message msg ) 
    throws IOException {

        Message res = null;
        boolean msg_rcvd = false; 

        while ( !msg_rcvd ) {
            try {

                this.send(msg);
                // TODO what if MessageCorruptException ?
                res = this.receive();

                msg_rcvd = true;

            } catch ( SocketTimeoutException e ) {
                System.out.println("err: "
                        + "server did not receive request."
                        + "resending..");
                msg_rcvd = false;
            } catch ( MessageCorruptException e ) {
                msg_rcvd = false;
            }
        }
        return res;
    }
}
