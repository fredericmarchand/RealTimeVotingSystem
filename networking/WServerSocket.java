package networking;

import java.io.IOException;

/**
 * (Wrapper-ServerSocket)
 * This is analogus to the ServerSocket class 
 * contained in the java.net API
 */
public class WServerSocket {
	private WSocket socket;
	private int port;

    /**
     * init with a specified port and host address
     */
	public WServerSocket(int port, String host) {
		try {
			socket = new WSocket(port, host);
			socket.setTimeout(10000000);
			this.port = port;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * init with a specified port
     * and default "localhost"
     */
	public WServerSocket(int port) {
		this(port, "127.0.0.1");
	}

    /**
     * listens and accepts a new connection
     * @return a new WSocket on any available port
     */
	public synchronized WSocket accept() {
		try {
			Message msg = socket.receive();
			if (!msg.getData().equals("%%%connect%%%"))
				System.err.println("huh " + msg);
			WSocket conn = new WSocket();
			Message note = new Message(Message.Method.POST,
					"%%%new-connection%%%", new Integer(conn.port));
			socket.sendTo(note, msg.getSenderPort());
			//System.out.println("connection accepted on " + conn.port);
			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getPort() {
		return port;
	}
}
