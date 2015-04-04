package controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;

import model.Candidate;
import model.Connection;
import model.ResultSet;
import model.ServerPorts;
import networking.Message;
import networking.WSocket;

public class CentralServer {

	public static final int CENTRAL_SERVER_PORT = ServerPorts.CENTRAL_SERVER;
	public static final int PERIOD = 3000; // milliseconds
	private HashSet<Connection> districts;
	private ResultSet totals;

	private WSocket servSocket;

	public void init() {
		try {
			districts = new HashSet<Connection>();
			totals = new ResultSet(ResultSet.NATIONAL);
			servSocket = new WSocket(CENTRAL_SERVER_PORT);
		} catch (UnknownHostException | SocketException e1) {
			e1.printStackTrace();
		}
	}

	public void receiveMessages() {
		try {
			while (true) {
				final Message msg = servSocket.receive();
				Thread t = new Thread(new Runnable() {
					public void run() {
						processMessages(msg);
					}
				});
				t.start();
			}
		} catch (Exception e) {
			servSocket.close();
			e.printStackTrace();
		}
	}

	private void processMessages(Message msg) {
		try {
			int sender = msg.getSenderPort();

			switch (msg.getType()) {

			case RtvsType.CONNECT:
				districts.add(new Connection(msg.getSenderHost(), (int)msg.getData()));
				break;
			
			case RtvsType.RESULTS:
				msg = new Message(Message.Method.POST, RtvsType.RESULTS, totals);
				servSocket.sendTo(msg, sender);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	public void getLocalResults() {
		totals = new ResultSet(ResultSet.NATIONAL);
		
		for (Connection c: districts) {
			HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();
			Message msg;
			
			try {
				msg = new Message(Message.Method.GET, RtvsType.RESULTS, "");
				servSocket.sendTo(msg, c.getPort());
				msg = servSocket.receive();
				results = ((ResultSet) msg.getData()).getDistrictVotes();
				
				for (Candidate can: results.keySet()) {
					synchronized(totals) {
						if (totals.getTotalVotes().containsKey(can.getParty())) {
							totals.getTotalVotes().put(can.getParty(), totals.getTotalVotes().get(can.getParty()) + results.get(can));
						}
						else {
							totals.getTotalVotes().put(can.getParty(), results.get(can));
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public static void main(String args[]) {

		final CentralServer server = new CentralServer();
		server.init();

		Thread t = new Thread(new Runnable() {
			public void run() {
				server.receiveMessages();
			}
		});
		t.start();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Query Votes from District Servers
				server.getLocalResults();
			}

		});

		thread.start();
	}
}