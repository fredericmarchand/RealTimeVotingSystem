package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.lang.Thread;
import java.net.SocketException;
import java.net.UnknownHostException;

import networking.*;
import model.*;
import testing.SystemPopulator;

public class DistrictServer {
	
	private District district;
	private WSocket recvSocket, sendSocket;
	private HashMap<String, Party> parties;
	private HashMap<String, Candidate> candidates;
	private HashMap<String, Voter> registeredVoters;
	private HashSet<Vote> votes;
	
	public DistrictServer(String name, Province province, int port) {
		district = new District (name, province);
		try {
			recvSocket = new WSocket().listen(port);
			sendSocket = new WSocket().connect(port);
        } catch (UnknownHostException | SocketException e1) {
            // TODO Auto-generated catch block
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
	
	public WSocket getSocket() {
		return recvSocket;
	}

	public void populateParties(String inputFile) {
		System.out.println("Populating the Server with Parties...");
		SystemPopulator.populateParties(inputFile);
		ArrayList<Party> parties = SystemPopulator.getParties();
		for (int i = 0; i < parties.size(); ++i) {
            Party party = parties.get(i);
            this.parties.put(party.getName(), party);
		}
		System.out.println("Total Parties: " + this.parties.size());
		System.out.println("Done Populating.");
	}
 	
 	public void receiveMessages() {
		try {
	        while ( true ) {
	        	final Message msg = recvSocket.receive();
	        	new Thread(new Runnable() {
					public void run() {
						processMessages(msg);
					}
				}).start();
	        }
	    } catch (Exception e) {
	    	recvSocket.close();
	    	sendSocket.close();
	    	e.printStackTrace();
	    }
   	}

   	private void processMessages(Message msg) {
   		try {                
            int sender = msg.getSenderPort();

			switch (msg.getType()) {
				case RtvsType.CANDIDATES:
					ArrayList<Candidate> response1;
					synchronized(candidates) {
						response1 = new ArrayList<Candidate>(candidates.values());
					}							
					msg = new Message(Message.Method.GET, RtvsType.CANDIDATES, response1);
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.REGISTER:
					Voter voter1 = (Voter)msg.getData();
					synchronized(registeredVoters) {
						if (voter1 == null || registeredVoters.containsKey(voter1.getUsername())) {
							//Voter already registered
							//Return false
							msg = new Message(Message.Method.GET, RtvsType.REGISTER, Boolean.FALSE);
						}

						else {
							//Register voter
							registeredVoters.put(voter1.getUsername(), voter1);
						
							//Return true
							msg = new Message(Message.Method.GET, RtvsType.REGISTER, Boolean.TRUE);
						}
					}
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.RUN:
					Candidate candidate1 = (Candidate)msg.getData();
					synchronized(registeredVoters) {
						synchronized(parties) {
							if (candidate1 == null || !registeredVoters.containsKey(candidate1.getUsername()) ||
								candidate1.getParty() == null || !parties.containsKey(candidate1.getParty().getName())) {
								//Candidate not registered or party does not exist
								//Return false
								msg = new Message(Message.Method.GET, RtvsType.RUN, Boolean.FALSE);
							}
							else {

								synchronized(candidates) {
									candidates.put(candidate1.getUsername(), candidate1);
									Party party = parties.get(candidate1.getParty().getName());
									if (party.getLeader() == null) {
										party.setLeader(candidate1);
									}
								}							
								//Return true
								msg = new Message(Message.Method.GET, RtvsType.RUN, Boolean.TRUE);
							}
						}
					}
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.LOGIN:
					Voter voter = null;
					try {
						String[]loginInfo = ((String)msg.getData()).split("\n");
						String username = loginInfo[0];
						String password = loginInfo[1];
						synchronized(registeredVoters) {
							if (registeredVoters.containsKey(username) && 
								registeredVoters.get(username).getPassword() == password) {
								
								//Voter is registered return the voter
								voter = registeredVoters.get(username);
							}
							else {
								//Voter is not registered or doesnt have the right password return null
								voter = null;
							}
						}
					} catch (Exception e) {
						voter = null;
					}
					msg = new Message(Message.Method.GET, RtvsType.LOGIN, voter);
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.RESULTS:
					
					
					
					
					
					break;
				case RtvsType.PARTIES:
					ArrayList<Party> response;
					synchronized(parties) {
						response = new ArrayList<Party>(parties.values());
					}							
					msg = new Message(Message.Method.GET, RtvsType.PARTIES, response);
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.HAS_VOTED:
					String username = (String)msg.getData();
					boolean hasVoted = false;
					synchronized(registeredVoters) {
						if (registeredVoters.containsKey(username)) {
							hasVoted = registeredVoters.get(username).hasVoted();
						}
					}
					msg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, hasVoted);
					sendSocket.sendTo(msg, sender);
					break;
				case RtvsType.VOTE:
					Vote vote = (Vote)msg.getData();
					synchronized(votes) {
						if (votes.contains(vote)) {
							votes.add(vote);
						}
					}
					break;
				default:
					break;
			}
        } catch ( Exception e ) {
            ////
            // The checksums did not match!
            System.err.println(e);
           	e.printStackTrace();
            // do nothing, 
            // the client should resend the same message
        }
   	}
	
	public static void main(String[] args) {
		try {
			String districtName = args[0];
			String provinceName = args[1];
			int port = Integer.parseInt(args[2]);
			
			final DistrictServer server = new DistrictServer(districtName, Province.getProvinceFromName(provinceName), port);

			if (args.length > 3) {
				server.populateParties(args[3]);
			}

			System.out.println();
			System.out.println(districtName + " Server running on port " + port);
			
			new Thread(new Runnable() {
				public void run() {
					server.receiveMessages();
				}
			}).start();

		} catch (Exception e) {
	    	System.out.println("Usage: DistrictServer <districtName> <provinceName> <port> [<inputFile>]");
	    	e.printStackTrace();
	    }
	}
}