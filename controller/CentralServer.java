package controller;

import java.io.IOException;

import networking.Message;
import networking.MessageCorruptException;
import networking.WSocket;

public class CentralServer {

	public static final int CENTRAL_SERVER_PORT = 60001;
	public static final int PERIOD = 1000; //milliseconds
	
	public static void main(String args[]) {
		
		WSocket socket = new WSocket(CENTRAL_SERVER_PORT);
		socket.listen();
		while ( true ) {
			Message msg = null;
	            
			try {
			    msg = socket.receive();
			    
			    //Handle Message
			    //Launch Thread
			    
			} catch (MessageCorruptException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
