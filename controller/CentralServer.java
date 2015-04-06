package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import model.Candidate;
import model.Connection;
import model.ResultSet;
import model.ServerPorts;
import networking.Message;
import networking.WServerSocket;
import networking.WSocket;

public class CentralServer {

	public static final int CENTRAL_SERVER_PORT = ServerPorts.CENTRAL_SERVER;
	public static final int PERIOD = 3000; // milliseconds
	private HashSet<Connection> districts;
	private ResultSet totals;
	private HashMap<Integer, WSocket> districtServerSockets;
	private WServerSocket servSocket;

	public void init() {
		districts = new HashSet<Connection>();
		totals = new ResultSet(ResultSet.NATIONAL);
		servSocket = new WServerSocket(CENTRAL_SERVER_PORT);
		districtServerSockets = new HashMap<Integer, WSocket>();
	}

	public void receiveMessages() {
		try {
			while (true) {
				final WSocket socket = servSocket.accept();
				Thread t = new Thread(new Runnable() {
					public void run() {
						processMessages(socket);
					}
				});
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processMessages(WSocket socket) {
		while (true) {
			try {
				Message msg = socket.receive();
				int sender = msg.getSenderPort();
	
				switch (msg.getType()) {
	
				case RtvsType.CONNECT:
					districts.add(new Connection(msg.getSenderHost(), (int)msg.getData()));
					WSocket w = new WSocket().connect((int)msg.getData());
					districtServerSockets.put((int)msg.getData(), w);
					break;
				
				case RtvsType.RESULTS:
					synchronized (totals) {
						msg = new Message(Message.Method.POST, RtvsType.RESULTS, totals);
						socket.sendTo(msg, sender);
					}
					break;
	
				default:
					break;
				}
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}
	
	public void getLocalResults() {
		synchronized(totals) {
			totals = new ResultSet(ResultSet.NATIONAL);
			
			for (Connection c: districts) {
				HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();
				Message msg;
				
				try {
					msg = new Message(Message.Method.GET, RtvsType.RESULTS, "district");
					districtServerSockets.get(c.getPort()).send(msg);
					msg = districtServerSockets.get(c.getPort()).receive();
					results = ((ResultSet) msg.getData()).getDistrictVotes();
					
					for (Candidate can: results.keySet()) {
						if (!totals.getTotalVotes().containsKey(can.getParty().getName())) {
							totals.getTotalVotes().put(can.getParty().getName(), 0);
						}
					}
					
					String party = ((ResultSet) msg.getData()).getPartyWithMostVotes();
					if (party != null) {
						if (totals.getTotalVotes().containsKey(party)) {
							totals.getTotalVotes().put(party, totals.getTotalVotes().get(party) + 1);
						}
						else {
							totals.getTotalVotes().put(party, 1);
						}
					}
					
					/*for (Candidate can: results.keySet()) {
						
						if (totals.getTotalVotes().containsKey(can.getParty().getName())) {
							totals.getTotalVotes().put(can.getParty().getName(), totals.getTotalVotes().get(can.getParty().getName()) + results.get(can));
						}
						else {
							totals.getTotalVotes().put(can.getParty().getName(), results.get(can));
						}
						
					}*/
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (String p: totals.getTotalVotes().keySet()) {
			System.out.println(p + ": " + totals.getTotalVotes().get(p));
		}
		System.out.println();
	}

	public static void main(String args[]) {

		final CentralServer server = new CentralServer();
		server.init();
		System.out.println("Starting Central Server");
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				server.receiveMessages();
			}
		});
		System.out.println("Receiving");
		t.start();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Query Votes from District Servers
					server.getLocalResults();
				}
			}
		});

		System.out.println("Querying local results");
		thread.start();
	}
}