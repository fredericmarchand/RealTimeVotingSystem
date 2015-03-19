package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import networking.*;
import model.*;
import testing.SystemPopulator;
import view.ClientGUI;

public class ClientController {
	
	public static final int SIMULATION_MODE = 0;
	public static final int USER_MODE = 1;
	
	private static WSocket socket;
	private static int districtServerPort;

	public ClientController (int serverPort) {
		districtServerPort = serverPort;
		 try {
			socket = new WSocket().connect(districtServerPort);
        } catch (UnknownHostException | SocketException e1) {
            e1.printStackTrace();
        }
	}
	
	public WSocket getSocket() {
		return socket;
	}
	
	public void closeSocket() { 
		socket.close();
	}
	
	public static boolean registerUser(Voter v) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.REGISTER, v);
			System.out.println(newMsg.toString() + " " + districtServerPort);
			socket.sendTo(newMsg, districtServerPort); 
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Party> getParties() {
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

	public static boolean updateCandidate(Candidate c, Party party) {
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
	
	public static Voter loginUser(String username, String password) {
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
	
	public static boolean userHasVoted(Voter v) {		
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, v);
			socket.sendTo(newMsg, districtServerPort); 
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean vote(Candidate c, Voter v) {
		Vote vote = new Vote(v, c);
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.VOTE, vote);
			socket.sendTo(newMsg, districtServerPort); 
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Candidate> getDistrictCandidates(District d) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		
		//fetch from district server;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.CANDIDATES, "Ottawa-South");
			socket.sendTo(newMsg, districtServerPort); 
			newMsg = socket.receive();
			candidates = (ArrayList<Candidate>)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return candidates;
	}
	
	public static HashMap<Candidate, Integer> getLocalResults(District d) {
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
	
	public HashMap<Party, Integer> getNationalResults() {
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
            ArrayList<Party> parties = ClientController.getParties();

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
		@SuppressWarnings("unused")
		ClientGUI gui = new ClientGUI(new District("Ottawa South"));

		System.out.println("Started GUI");
	}
	
	public static void main(String[] args) {
		try {
			int mode = Integer.parseInt(args[0]);
  	    	int serverPort = Integer.parseInt(args[1]);
  	    	final ClientController client = new ClientController(serverPort);

  	    	if (mode == ClientController.SIMULATION_MODE) {
  	    		client.simulate(args[2], args[3]);
  	    	}
  	    	else if (mode ==  ClientController.USER_MODE) {
  	    		client.startUI();
  	    	}

	    } catch (Exception e) {
	    	System.out.println("Usage: ClientController <serverPort> [<inputFile> <outputFile>]");
	    }
	}

}
