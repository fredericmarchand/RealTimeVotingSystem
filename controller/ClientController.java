package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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
	
	public void close() { 
		this.socket.close();
	}
	
	public boolean registerUser(Voter v) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.REGISTER, v);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ArrayList<Party> getParties() {
		ArrayList<Party> parties = new ArrayList<Party>();
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.PARTIES, "");
			socket.sendTo(newMsg, districtServerPort);
			newMsg = socket.receive();
			parties = (ArrayList<Party>)newMsg.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return parties;
	}

	public boolean updateCandidate(Candidate c, Party party) {
		c.runFor(party);
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.RUN, c);
			socket.sendTo(newMsg, districtServerPort);
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();

			if (!result) {
				c.runFor(null);			
			}
			else if (party.getLeader() == null) {
				party.setLeader(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Voter loginUser(String username, String password) {
		Voter result = null;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.LOGIN, username+"\n"+password);
			socket.sendTo(newMsg, districtServerPort);
			newMsg = socket.receive();
			result = (Voter)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public boolean userHasVoted(Voter v) {		
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, v);
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
			Message newMsg = new Message(Message.Method.POST, RtvsType.VOTE, vote);
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
			Message newMsg = new Message(Message.Method.GET, RtvsType.CANDIDATES, "Ottawa-South");
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
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, d);
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
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, null);
			socket.sendTo(newMsg, districtServerPort); //Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet)newMsg.getData()).getTotalVotes();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}

	public void simulate(String inputFile, String outputFile) {

		BufferedWriter out = null;

  	    try { 
            out = new BufferedWriter(new FileWriter(outputFile));
            out.write("Populating the system with Voters and Candidates...");
            out.newLine();
            SystemPopulator.populateVotersAndCandidates(inputFile);
            ArrayList<Person> voters = SystemPopulator.getVoters();
      		ArrayList<Person> candidates = SystemPopulator.getCandidates();
            out.write("Total Voters: " + voters.size());
            out.newLine();
            out.write("Done Populating.");
            out.newLine();
            out.newLine();

            out.write("Registering Voters on the Server.");
            out.newLine();
            for (int i = 0; i < voters.size(); ++i) {
            	Voter voter = (Voter)voters.get(i);
				if (registerUser(voter)) {
					out.write("Registration Successful: " + voter.toString());
				}
				else {
					out.write("Registration Failed: " + voter.toString());
				}
				out.newLine();
			}
			out.newLine();

			out.write("Getting Parties from the Server.");
            out.newLine();
            ArrayList<Party> parties = getParties();

            if (parties.size() > 0) {
            	out.write("District Parties:");
            	out.newLine();
            	for (int i = 0; i < parties.size(); ++i) {
            		out.write(parties.get(i).getName());
            		out.newLine();
            	}
            	out.newLine();

            	out.write("Updating Candidates and their Parties on the Server.");
	            out.newLine();
	            for (int i = 0; i < candidates.size(); ++i) {
	            	Candidate candidate = (Candidate)candidates.get(i);
					if (updateCandidate(candidate, parties.get((i%parties.size())))) {
						out.write("Candidate Run Successful: " + candidate.toString());
					}
					else {
						out.write("Candidate Run Failed: " + candidate.toString());
					}
					out.newLine();
				}
            }
            else {
            	out.write("There are no parties in the District Server");
            	out.newLine();
            	out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
  	    		client.simulate(args[1], args[2]);
  	    	}
  	    	else {
  	    		client.startUI();
  	    	}

	    } catch (Exception e) {
	    	System.out.println("Usage: ClientController <serverPort> [<inputFile> <outputFile>]");
	    }
	}

}
