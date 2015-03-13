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
		
		//Launch Periodic thread
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Query Votes from District Servers
				//Push back to every district server
				
			}
			
		});
		
		thread.start();
		
		for (;;) {
			Message msg = null;
	            
			try {
				msg = socket.receive();
				int sender = msg.getSenderPort();
			    
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
