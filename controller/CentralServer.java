package controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;

import networking.Message;
import networking.WSocket;

public class CentralServer {

	public static final int CENTRAL_SERVER_PORT = 60001;
	public static final int PERIOD = 1000; //milliseconds
	
	public static void main(String args[]) {
		
		WSocket socket = null;
		try {
			socket = new WSocket().listen(CENTRAL_SERVER_PORT);
		} catch (UnknownHostException | SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
