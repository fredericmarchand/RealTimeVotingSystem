package networking;

import java.io.IOException;

public class WServerSocket 
{
	WSocket socket;
	
	public WServerSocket ( int port, String host ) { 
		try { 
			socket = new WSocket(port, host);
		} catch ( Exception e ) { 
			e.printStackTrace();
		}
	}
	
	public WServerSocket ( int port ) { 
		this(port, "127.0.0.1");
	}
	
	public synchronized WSocket accept() { 
		try {
			Message msg = socket.receive();
			if ( !msg.getData().equals("%%%connect%%%") ) 
				System.err.println("huh "+msg);
			WSocket conn = new WSocket();
			Message note = new Message(
					Message.Method.POST,
					"%%%new-connection%%%",
					new Integer(conn.port));
			socket.sendTo(note, msg.getSenderPort());
			System.out.println("connection accepted on "+conn.port);
			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
