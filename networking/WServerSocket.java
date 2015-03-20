package networking;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class WServerSocket 
{
	WSocket socket;
	
	public WServerSocket ( int port ) { 
		try {
			socket = new WSocket().listen(port);
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WSocket accept() { 
		try {
			Message msg = socket.receive();
			if ( !msg.getData().equals("%%%connect%%%") ) 
				System.err.println("huh "+msg);
			WSocket conn = new WSocket().listen();
			Message note = new Message(
					Message.Method.POST,
					"%%%new-connection%%%",
					new Integer(conn.port));
			socket.sendTo(note, msg.getSenderPort());
			System.out.println("connection accepted");
			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
