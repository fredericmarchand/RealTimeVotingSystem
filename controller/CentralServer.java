package controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;

import model.Candidate;
import model.Party;
import model.ResultSet;
import model.Vote;
import model.Voter;
import networking.Message;
import networking.WSocket;

public class CentralServer {

	public static final int CENTRAL_SERVER_PORT = 60001;
	public static final int PERIOD = 3000; // milliseconds
	private HashSet<Vote> votes;

	private WSocket servSocket;

	public void init() {
		try {
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

			case RtvsType.RESULTS:
				ResultSet rs = new ResultSet(ResultSet.NATIONAL);

				msg = new Message(Message.Method.POST, RtvsType.RESULTS, rs);
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
				// Push back to every district server

			}

		});

		thread.start();
	}
}