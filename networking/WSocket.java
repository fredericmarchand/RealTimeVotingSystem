//
// @author Brandon Schurman
//
package networking;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 * A Wrapper-Socket to simulate a TCP socket using UDP
 */
public class WSocket {
	public static final int PACKET_LEN = 500;   // length of one datagram packet
	public static final int FRAG_LEN = 200;     // length to break data by fragments
	public static final int SEND_ATTEMPTS = 3;  // #times to try resending a message 

	private int TIMEOUT = 10000; // timeout if no response is received

	private DatagramSocket socket;
	private InetAddress addr;
	public int port;

    /**
     * init on any available port with default host "localhost"
     */
	public WSocket() throws SocketException, UnknownHostException {
		this.socket = new DatagramSocket();
		this.port = socket.getLocalPort();
		this.addr = InetAddress.getByName("localhost");
	}
    
    /**
     * init on specified port and host
     */
	public WSocket(int port, String host) throws UnknownHostException,
			SocketException {
		this.socket = new DatagramSocket(port, addr);
		this.port = port;
		this.addr = InetAddress.getByName(host);
	}

    /**
     * init on the specified port, with default "localhost"
     */
	public WSocket(int port) throws UnknownHostException, SocketException {
		this(port, "localhost");
	}

    /**
     * Establish a connection with a WServerSocket
     * this method changes the internal port number
     * after a new socket is created by WServerSocket
     */
	public synchronized WSocket connect(int port, String host)
	throws UnknownHostException, SocketException, SocketTimeoutException {
		this.socket = new DatagramSocket();
		try {
			System.out.println("connecting");
			Message msg = new Message(Message.Method.POST, "%%%connect%%%",
					"%%%connect%%%");
			this.sendTo(msg, port);
			Message res = this.receive();
			if (res.getType().equals("%%%new-connection%%%")) {
				this.port = (Integer) res.getData();
				System.out.println("conncet received, new port = " + this.port
						+ ", new host = " + addr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

    /**
     * Establish a connection with a WServerSocket
     * this method changes the internal port number
     * after a new socket is created by WServerSocket
     */
	public WSocket connect(int port) 
	throws UnknownHostException, SocketException, SocketTimeoutException {
		return this.connect(port, "localhost");
	}

    /**
     * change the timeout limit
     */
	public void setTimeout(int timeout) {
		this.TIMEOUT = timeout;
	}

    /**
     * close the socket and free the port
     */
	public void close() {
		if (socket != null)
			socket.close();
	}

	private void sendConfirmation(int port, InetAddress host)
	throws IOException, SocketTimeoutException {
		Message confirm = new Message(Message.Method.POST, "%%received%%",
				"%%received%%");
		confirm.setSender(this.port, this.addr);
		byte[] buffer = confirm.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host,
				port);
		socket.send(packet);
	}

    /**
     * Wait for a message on the port
     * @return a deserialized Message
     */
	public synchronized Message receive() 
	throws IOException, SocketTimeoutException {
		byte[] buffer = new byte[576];
		Message msg = null;
		while (msg == null) {
			try {
				socket.setSoTimeout(TIMEOUT);
				DatagramPacket response = new DatagramPacket(buffer,
						buffer.length, this.socket.getInetAddress(),
						this.socket.getLocalPort());
				socket.receive(response);
				msg = new Message(response.getData());
				msg.setSender(response.getPort(), response.getAddress());
			} catch (MessageCorruptException e) {
				e.printStackTrace();
			}
		}

		this.sendConfirmation(msg.getSenderPort(), msg.getSenderHost());

		if (msg.getType().equals("%%start-fragments%%"))
			msg = receiveFragments();

		return msg;
	}

    /**
     * Send the message to the initialized port and host
     */
	public void send(Message msg) throws IOException, SocketTimeoutException {
		this.sendTo(msg, this.port, this.addr);
	}

    /**
     * Send the message to the specified port on the initialized host
     */
	public void sendTo(Message msg, int port) throws IOException, SocketTimeoutException {
		// System.out.println(port + " " + this.addr.toString());
		this.sendTo(msg, port, this.addr);
	}

    /**
     * Send the message to the specified port and host address
     */
	public synchronized void sendTo(Message msg, int port, InetAddress host)
			throws IOException, SocketTimeoutException {

		msg.setSender(this.socket.getLocalPort(), this.socket.getInetAddress());
		final byte[] data = msg.getBytes();

		if (!msg.getType().equals("%%fragment%%") && data.length > PACKET_LEN) {
			// System.out.println("fragments...");
			this.sendFragments(msg, port, host);
			return;
		}

		boolean msg_rcvd = false;

		int timeoutCount = 0;
		
		while (!msg_rcvd) {
			try {
				socket.setSoTimeout(TIMEOUT);
				DatagramPacket request = new DatagramPacket(data, data.length,
						host, port);
				socket.send(request);
				byte[] buffer = new byte[PACKET_LEN];
				// receive confirmation
				DatagramPacket response = new DatagramPacket(buffer,
						buffer.length, this.socket.getLocalAddress(),
						this.socket.getLocalPort());
				socket.receive(response);
				Message res = new Message(response.getData());
				res.setSender(response.getPort(), response.getAddress());
				if (res.getData().equals("%%received%%"))
					msg_rcvd = true;
				else
					System.out.println("huh? " + msg);
				msg_rcvd = true;
			} catch (SocketTimeoutException e) {
				timeoutCount++;
				e.printStackTrace();
				if ( timeoutCount >  SEND_ATTEMPTS-1 ) { 
					throw e;
				}
			} catch (MessageCorruptException e) {
				e.printStackTrace();
			}
			//socket.setSoTimeout(1000000);
		}
	}

    /**
     * send the message, then wait for a reply
     */
	public synchronized Message sendReceive(Message msg, int port,
			InetAddress host) throws IOException {

		this.sendTo(msg, port, host);
		Message res = this.receive();

		return res;
	}

    /**
     * send the message, then wait for a reply
     */
	public Message sendReceive(Message msg) throws IOException {
		return this.sendReceive(msg, port, addr);
	}

	private Message receiveFragments() throws IOException {
		int port = -10;
		InetAddress host = null;

		ArrayList<Byte> total_data = new ArrayList<Byte>();

		boolean done_receiving = false;
		while (!done_receiving) {
			Message msg = this.receive();

			if (port == -10) {
				port = msg.getSenderPort();
				host = msg.getSenderHost();
			}
			// System.out.println("receive fragment: "+msg);

			if (msg.getType().equals("%%fragment%%")) {
				byte[] frag = (byte[]) msg.getData();
				for (int i = 0; i < frag.length; i++)
					total_data.add(frag[i]);
			} else if (msg.getType().equals("%%done%%")) {
				done_receiving = true;
			} else {
				System.err
						.println("error, should be of type %%done%% or %%fragment%% "
								+ msg);
			}
		}
		byte[] bytes = new byte[total_data.size()];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = total_data.get(i);

		Message msg = null;
		try {
			msg = new Message(bytes);
			msg.setSender(port, host);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	private void sendFragments(Message msg, int port, InetAddress host)
			throws IOException {

		byte[] data = msg.getBytes();
		boolean done_sending = false;
		int pos = 0;

		Message note = new Message(Message.Method.POST, "%%start-fragments%%",
				"%%start-fragments%%");
		this.sendTo(note, port, host);

		while (!done_sending) {
			byte[] frag = (pos > data.length - FRAG_LEN) ? new byte[data.length
					- pos] : new byte[FRAG_LEN];

			for (int i = 0; i < FRAG_LEN; i++) {
				if (i + pos < data.length)
					frag[i] = data[pos + i];
				else
					done_sending = true;
			}

			pos += FRAG_LEN;

			// System.out.println("sending fragment ");
			Message msg_frag = new Message(msg.getMethod(), "%%fragment%%",
					frag);
			this.sendTo(msg_frag, port, host);

			if (done_sending) {
				Message done = new Message(Message.Method.POST, "%%done%%",
						"%%done%%");
				this.sendTo(done, port, host);
			}
		}
	}
	
	public int getPort() { 
		return this.port; 
	}
	
	public InetAddress getHost() { 
		return this.addr; 
	}
	
	@Override
	public String toString() { 
		return addr+" : "+port;
	}
}
