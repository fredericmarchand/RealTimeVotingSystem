package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.io.IOException;
import java.lang.Thread;
import java.net.SocketTimeoutException;

import networking.*;
import model.*;
import testing.SystemPopulator;

public class DistrictServer {

	// limit the max number of connected clients
	// note that the central server counts as one of these connections
	public final static int CONN_LIMIT = 10000; 
			
	private final Semaphore semConnection = new Semaphore(CONN_LIMIT, false);
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	private District district;
	private WServerSocket servSocket;
	private WSocket sendSocket;
	private HashMap<String, Party> parties;
	private HashMap<String, Candidate> candidates;
	private HashMap<String, Voter> registeredVoters;
	private HashSet<Vote> votes;
	private ResultSet totals;

	public DistrictServer(String name, Province province, int port) {
		district = new District(name, province);
		totals = new ResultSet(ResultSet.NATIONAL);
		try {
			servSocket = new WServerSocket(port);
			sendSocket = new WSocket().connect(CentralServer.CENTRAL_SERVER_PORT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		parties = new HashMap<String, Party>();
		candidates = new HashMap<String, Candidate>();
		registeredVoters = new HashMap<String, Voter>();
		votes = new HashSet<Vote>();
	}

	public District getDistrict() {
		return district;
	}

	public HashMap<String, Party> getParties() {
		return parties;
	}

	public HashMap<String, Candidate> getCandidates() {
		return candidates;
	}

	public void populateParties(String inputFile) {
		System.out.println("Populating the Server with Parties...");
		ArrayList<Party> parties = new ArrayList<Party>();
		SystemPopulator.populateParties(inputFile, parties);
		for (int i = 0; i < parties.size(); ++i) {
			Party party = parties.get(i);
			this.parties.put(party.getName(), party);
		}
		System.out.println("Total Parties: " + this.parties.size());
		System.out.println("Done Populating.");
	}

	//Accept connections from clients and start receiving messages
	public void receiveMessages() {
		try {
			while (true) {
				final WSocket client = servSocket.accept();
				Runnable task = new Runnable() {
					public void run() {
						try { 
							semConnection.acquire();
							System.out.println("Numbre of available connections: "
									+ semConnection.availablePermits());
							handleClient(client);
						} catch ( InterruptedException e ) { 
							e.printStackTrace();
						} finally {
							semConnection.release();
						}
					}
				};
				threadPool.submit(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//handle client requests
	private void handleClient(WSocket socket) {
		while (true) {
			try {
				Message msg = socket.receive();
				int sender = msg.getSenderPort();

				switch (msg.getType()) {
				case RtvsType.CANDIDATES:
					ArrayList<Candidate> response1;
					synchronized (candidates) {
						response1 = new ArrayList<Candidate>(
								candidates.values());
					}
					msg = new Message(Message.Method.GET, RtvsType.CANDIDATES,
							response1);
					socket.sendTo(msg, sender);
					break;
				case RtvsType.REGISTER:
					Voter voter1 = (Voter) msg.getData();
					synchronized (registeredVoters) {
						if (voter1 == null
								|| registeredVoters.containsKey(String
										.valueOf(voter1.getSIN()))) {
							// Voter already registered
							// Return false
							msg = new Message(Message.Method.GET,
									RtvsType.REGISTER, Boolean.FALSE);
						}

						else {
							// Register voter
							registeredVoters.put(
									String.valueOf(voter1.getSIN()), voter1);

							// Return true
							msg = new Message(Message.Method.GET,
									RtvsType.REGISTER, Boolean.TRUE);
						}
					}
					socket.sendTo(msg, sender);
					break;
				case RtvsType.RUN:
					Candidate candidate1 = (Candidate) msg.getData();
					synchronized (registeredVoters) {
						synchronized (parties) {
							if (candidate1 == null
									|| !registeredVoters.containsKey(candidate1
											.getUsername())
									|| candidate1.getParty() == null
									|| !parties.containsKey(candidate1
											.getParty().getName())) {
								// Candidate not registered or party does not
								// exist
								// Return false
								msg = new Message(Message.Method.GET,
										RtvsType.RUN, Boolean.FALSE);
							} else {

								synchronized (candidates) {
									candidates.put(candidate1.getUsername(),
											candidate1);
									Party party = parties.get(candidate1
											.getParty().getName());
									if (party.getLeader() == null) {
										party.setLeader(candidate1);
									}
								}
								// Return true
								msg = new Message(Message.Method.GET,
										RtvsType.RUN, Boolean.TRUE);
							}
						}
					}
					socket.sendTo(msg, sender);
					break;
				case RtvsType.LOGIN:
					Voter voter = null;
					try {
						String[] loginInfo = ((String) msg.getData())
								.split("\n");
						String SIN = loginInfo[0];
						String password = loginInfo[1];
						synchronized (registeredVoters) {
							if (registeredVoters.containsKey(SIN)
									&& registeredVoters.get(SIN).getPassword()
											.equals(password)) {

								// Voter is registered return the voter
								voter = registeredVoters.get(SIN);
							} else {
								// Voter is not registered or doesnt have the
								// right password return null
								voter = null;
							}
						}
					} catch (Exception e) {
						voter = null;
					}
					msg = new Message(Message.Method.GET, RtvsType.LOGIN, voter);
					socket.sendTo(msg, sender);
					break;
				case RtvsType.RESULTS:
					if (((String)msg.getData()).equals("district")) {
						ResultSet rs = new ResultSet(ResultSet.DISTRICT);
						for (Candidate c : this.getCandidates().values()) {
							int totalVotes = 0;
							for (Vote v : votes) {
								if (v.getCandidate().getName().equals(c.getName()))
									totalVotes++;
							}
							rs.getDistrictVotes().put(c, totalVotes);
						}
	
						msg = new Message(Message.Method.GET, RtvsType.RESULTS, rs);
					}
					else {
						synchronized (totals) {
							msg = new Message(Message.Method.GET, RtvsType.RESULTS, totals);
						}
					}
					socket.sendTo(msg, sender);

					break;
				case RtvsType.PARTIES:
					ArrayList<Party> response;
					synchronized (parties) {
						response = new ArrayList<Party>(parties.values());
					}
					msg = new Message(Message.Method.GET, RtvsType.PARTIES,
							response);
					socket.sendTo(msg, sender);
					break;
				case RtvsType.HAS_VOTED:
					Voter voter2 = (Voter) msg.getData();
					String SIN = String.valueOf(voter2.getSIN());
					boolean hasVoted = false;
					synchronized (registeredVoters) {
						if (registeredVoters.containsKey(SIN)) {
							hasVoted = registeredVoters.get(SIN).hasVoted();
						}
					}
					msg = new Message(Message.Method.GET, RtvsType.HAS_VOTED,
							hasVoted);
					socket.sendTo(msg, sender);
					break;
				case RtvsType.VOTE:
					boolean ret = false;
					Vote vote = (Vote) msg.getData();
					synchronized (votes) {
						if (!votes.contains(vote)
								&& registeredVoters.keySet().contains(
										"" + vote.getVoter().getSIN())) {
							votes.add(vote);
							for (Voter v : registeredVoters.values()) {
								if (v.getSIN() == vote.getVoter().getSIN()) {
									registeredVoters.get(
											String.valueOf(v.getSIN())).vote();
								}
							}
							ret = true;
						}
					}
					msg = new Message(Message.Method.GET, RtvsType.VOTE, ret);
					socket.sendTo(msg, sender);
					break;
					
				case RtvsType.DISCONNECT: 
					System.out.println("Client disconnected from port "+socket);
					socket.close();
					return;
					
				default:
					break;
				}
			} catch ( SocketTimeoutException e ) {
				System.out.println("Client disconnected (timeout) from port "+socket);
				socket.close();
				return;
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}
	
	//Periodic thread that gets the national results from the central server
	public void getNationalResults() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(CentralServer.PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	
					try {
						Message msg = new Message(Message.Method.GET, RtvsType.RESULTS, null);
						sendSocket.send(msg);
						msg = sendSocket.receive();
						synchronized (totals) {
							totals = (ResultSet)msg.getData();
						}
						for (String p: totals.getTotalVotes().keySet()) {
							System.out.println(p + ": " + totals.getTotalVotes().get(p));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		thread.start();
	}
	
	//Connect to the central server
	public void connectToCentralServer() {
		try {
			Message msg = new Message(Message.Method.POST, RtvsType.CONNECT, servSocket.getPort());
			sendSocket.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String districtName = args[0];
			String provinceName = args[1];
			int port = Integer.parseInt(args[2]);

			//Create district server instance
			final DistrictServer server = new DistrictServer(districtName,
					Province.getProvinceFromName(provinceName), port);

			//populate candidates/parties
			server.getParties().put(Party.CONSERVATIVES,
					new Party(Party.CONSERVATIVES));
			server.getParties().put(Party.LIBERALS, new Party(Party.LIBERALS));
			server.getParties().put(Party.NDP, new Party(Party.NDP));

			Candidate candidate1 = new Candidate("Javaris Javar",
					"Javarison-Lamar", new Address(), 789798987, new Party(
							Party.CONSERVATIVES));
			server.getCandidates().put(candidate1.getName(), candidate1);
			server.getParties().get(candidate1.getParty().getName())
					.setLeader(candidate1);

			Candidate candidate2 = new Candidate("Scoish Velociraptor",
					"Maloish", new Address(), 465464132, new Party(
							Party.LIBERALS));
			server.getCandidates().put(candidate2.getName(), candidate2);
			server.getParties().get(candidate2.getParty().getName())
					.setLeader(candidate2);

			Candidate candidate3 = new Candidate("X-wing", "@aliciousness",
					new Address(), 111111111, new Party(Party.NDP));
			server.getCandidates().put(candidate3.getName(), candidate3);
			server.getParties().get(candidate3.getParty().getName())
					.setLeader(candidate3);

			System.out.println(districtName + " Server running on port " + port);

			System.out.println("Connecting to Central Server");
			server.connectToCentralServer();
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					server.receiveMessages();
				}
			});
			t.start();
			
			server.getNationalResults();

		} catch (Exception e) {
			System.out.println("Usage: DistrictServer <districtName> <provinceName> <port> [<inputFile>]");
			e.printStackTrace();
		}
	}
}