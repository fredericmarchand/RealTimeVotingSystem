package controller;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;

import networking.Message;
import networking.WSocket;
import model.District;
import model.Province;
import model.ServerPorts;
import model.Vote;
import model.Voter;

public class DistrictServer {
	
	private District district;
	private WSocket socket;
	private int port;
	
	public DistrictServer(String name, Province province, int port) {
		district = new District (name, province);
		socket = new WSocket();
		this.port = port;
	}
	
	public District getDistrict() {
		return district;
	}
	
	public WSocket getSocket() {
		return socket;
	}
	
	public int getPort() { 
		return port;
	}
	
	public static void main(String[] args) {
		HashMap<Integer, Voter> registeredVoters = new HashMap<Integer, Voter>();
		HashSet<Vote> votes = new HashSet<Vote>();

		//votes
		
		DistrictServer server = new DistrictServer("Ottawa-South", Province.Ontario, ServerPorts.DISTRICT_SERVER1);
		System.out.println("DistrictServer: running on port "+ServerPorts.DISTRICT_SERVER1);
		try {
			server.getSocket().listen(server.getPort());
		} catch (UnknownHostException | SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-3);
		}
		
		while (true) {
			
			//Receive
			Message msg = null;
			try {
				msg = server.getSocket().receive();
				int sender = msg.getSenderPort();
			
				switch (msg.getType()) {
					case RtvsType.CANDIDATES:
						break;
					case RtvsType.REGISTER:
						Voter voter1 = (Voter)msg.getData();
						if (registeredVoters.containsKey(voter1.getSIN())) {
							//Voter already registered
							//Return false
							msg = new Message(Message.Method.GET, RtvsType.REGISTER, Boolean.FALSE);
							server.getSocket().sendTo(msg, sender);
						}
						else {
							//Register voter
							registeredVoters.put(voter1.getSIN(), voter1);
							
							//Return true
							msg = new Message(Message.Method.GET, RtvsType.REGISTER, Boolean.TRUE);
							server.getSocket().sendTo(msg, sender);
						}
						break;
					case RtvsType.LOGIN:
						Voter voter2 = (Voter)msg.getData();
						if (registeredVoters.containsKey(voter2.getSIN()) && 
							registeredVoters.get(voter2.getSIN()).getPassword() == voter2.getPassword()) {
							
							//Voter is registered return true
							msg = new Message(Message.Method.GET, RtvsType.LOGIN, Boolean.TRUE);
							server.getSocket().sendTo(msg, sender);
						}
						else {
							//Voter is not registered or doesnt have the right password return false
							msg = new Message(Message.Method.GET, RtvsType.LOGIN, Boolean.FALSE);
							server.getSocket().sendTo(msg, sender);
						}
						break;
					case RtvsType.RESULTS:
						break;
					case RtvsType.HAS_VOTED:
						Voter voter3 = (Voter)msg.getData();
						boolean hasVoted = false;
						if (registeredVoters.containsKey(voter3.getSIN())) {
							hasVoted = registeredVoters.get(voter3.getSIN()).hasVoted();
						}
						msg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, hasVoted);
						server.getSocket().sendTo(msg, sender);
						break;
					case RtvsType.VOTE:
						Vote vote = (Vote)msg.getData();
						if (votes.contains(vote)) {
							votes.add(vote);
						}
						break;
					default:
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
