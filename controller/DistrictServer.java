package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import networking.Message;
import networking.MessageCorruptException;
import networking.WSocket;
import model.Address;
import model.Candidate;
import model.District;
import model.Province;
import model.ServerPorts;
import model.Vote;
import model.Voter;

public class DistrictServer {
	
	private District district;
	private WSocket socket;
	
	public DistrictServer(String name, Province province, int port) {
		district = new District (name, province);
		socket = new WSocket(port);
	}
	
	public District getDistrict() {
		return district;
	}
	
	public WSocket getSocket() {
		return socket;
	}
	
	public static void main(String[] args) {
		HashMap<Integer, Voter> registeredVoters = new HashMap<Integer, Voter>();
		HashSet<Vote> votes = new HashSet<Vote>();

		//votes
		
		DistrictServer server = new DistrictServer("Ottawa-South", Province.Ontario, ServerPorts.DISTRICT_SERVER1);
		server.getSocket().listen();
		
		while (true) {
			
			//Receive
			Message msg = null;
			try {
				msg = server.getSocket().receive();
				int sender = msg.getSenderPort();
			
				switch (msg.getType()) {
					case CANDIDATES:
						break;
					case REGISTER:
						Voter voter1 = (Voter)msg.getData();
						if (registeredVoters.containsKey(voter1.getSIN())) {
							//Voter already registered
							//Return false
							msg = new Message(Message.Method.GET, Message.Type.REGISTER, Boolean.FALSE);
							server.getSocket().sendTo(msg, sender);
						}
						else {
							//Register voter
							registeredVoters.put(voter1.getSIN(), voter1);
							
							//Return true
							msg = new Message(Message.Method.GET, Message.Type.REGISTER, Boolean.TRUE);
							server.getSocket().sendTo(msg, sender);
						}
						break;
					case LOGIN:
						Voter voter2 = (Voter)msg.getData();
						if (registeredVoters.containsKey(voter2.getSIN()) && 
							registeredVoters.get(voter2.getSIN()).getPassword() == voter2.getPassword()) {
							
							//Voter is registered return true
							msg = new Message(Message.Method.GET, Message.Type.LOGIN, Boolean.TRUE);
							server.getSocket().sendTo(msg, sender);
						}
						else {
							//Voter is not registered or doesnt have the right password return false
							msg = new Message(Message.Method.GET, Message.Type.LOGIN, Boolean.FALSE);
							server.getSocket().sendTo(msg, sender);
						}
						break;
					case RESULTS:
						break;
					case HAS_VOTED:
						Voter voter3 = (Voter)msg.getData();
						boolean hasVoted = false;
						if (registeredVoters.containsKey(voter3.getSIN())) {
							hasVoted = registeredVoters.get(voter3.getSIN()).hasVoted();
						}
						msg = new Message(Message.Method.GET, Message.Type.HAS_VOTED, hasVoted);
						server.getSocket().sendTo(msg, sender);
						break;
					case VOTE:
						Vote vote = (Vote)msg.getData();
						if (votes.contains(vote)) {
							votes.add(vote);
						}
						break;
					default:
						break;
				}
			} catch (IOException | MessageCorruptException e) {
				e.printStackTrace();
			}
		}
	}
}
