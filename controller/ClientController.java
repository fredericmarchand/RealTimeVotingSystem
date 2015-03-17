package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.SocketException;
import java.net.UnknownHostException;

import networking.*;
import model.*;
import testing.SystemPopulator;

public class ClientController {
	
	private WSocket socket;
	private int districtServerPort;

	public ClientController (int serverPort) {
		districtServerPort = serverPort;
		 try {
			socket = new WSocket().connect(districtServerPort);
        } catch (UnknownHostException | SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}
	
	public boolean registerUser(Voter v, String password) {
		boolean result = false;
		try {
			v.setPassword(password);
			Message newMsg = new Message(Message.Method.POST, Message.Type.REGISTER, v);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean loginUser(Voter v) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, Message.Type.LOGIN, v);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public boolean userHasVoted(Voter v) {		
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.GET, Message.Type.HAS_VOTED, v);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void vote(Candidate c, Voter v) {
		Vote vote = new Vote(v, c);
		try {
			Message newMsg = new Message(Message.Method.POST, Message.Type.VOTE, vote);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			//Dont expect response
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Candidate> getDistrictCandidates(District d, WSocket socket) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		
		//fetch from district server;
		try {
			Message newMsg = new Message(Message.Method.GET, Message.Type.CANDIDATES, "Ottawa-South");
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			candidates = (ArrayList<Candidate>)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return candidates;
	}
	
	public HashMap<Candidate, Integer> getLocalResults(District d, WSocket socket) {
		HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();
		
		try {
			Message newMsg = new Message(Message.Method.GET, Message.Type.RESULTS, d);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet)newMsg.getData()).getDistrictVotes();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return results;
	}
	
	public HashMap<Party, Integer> getNationalResults(WSocket socket) {
		HashMap<Party, Integer> results = new HashMap<Party, Integer>();
		
		try {
			Message newMsg = new Message(Message.Method.GET, Message.Type.RESULTS, null);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet)newMsg.getData()).getTotalVotes();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}

	public void simulate(String inputFile) {
		SystemPopulator.populateVotersAndCandidates(inputFile);

		ArrayList<Person> voters = SystemPopulator.getVoters();
		for (int i = 0; i < voters.size(); ++i) {
			registerUser((Voter)voters.get(i), "password");
		}
	}

	public void startUI() {
		//Create GUI
		
		//Click Registration button
		//Show Registration Panel
		//Submit Registration information (event handler)
		//Click Voting button
		//Show Voting Panel
		//Submit Vote (event handler)
		//Click View Results button
		//Show Results panel

		System.out.println("Started GUI");
	}
	
	public static void main(String[] args) {
		try {
  	    	int serverPort = Integer.valueOf(args[0]);

  	    	final ClientController client = new ClientController(serverPort);

  	    	if (args.length > 1) {
  	    		client.simulate(args[1]);
  	    	}
  	    	else {
  	    		client.startUI();
  	    	}

	    } catch (Exception _) {
	    	_.printStackTrace();
	    	System.out.println("Usage: ClientController <serverPort> [<inputFile>]");
	    }
	}

}
