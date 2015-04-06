package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import networking.*;
import model.*;
import testing.SystemPopulator;
import view.ClientGUI;

public class ClientController {

	public static final int SIMULATION_MODE = 0;
	public static final int USER_MODE = 1;

	private static WSocket socket;
	private static int districtServerPort;

	public ClientController(int serverPort) throws SocketTimeoutException {
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
		sendDisconnect();
		socket.close();
	}

	public static synchronized boolean registerUser(Voter v) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.REGISTER, v);
			System.out.println(newMsg.toString() + " " + districtServerPort);
			socket.send(newMsg);
			newMsg = socket.receive();
			result = (boolean) newMsg.getData();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<Party> getParties() {
		ArrayList<Party> parties = new ArrayList<Party>();
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.PARTIES, "");
			socket.send(newMsg);
			newMsg = socket.receive();
			parties = (ArrayList<Party>) newMsg.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return parties;
	}

	public static synchronized boolean updateCandidate(Candidate c, Party party) {
		c.runFor(party);
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.RUN, c);
			socket.send(newMsg);
			newMsg = socket.receive();
			result = (boolean) newMsg.getData();

			if (!result) {
				c.runFor(null);
			} else if (party.getLeader() == null) {
				party.setLeader(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static synchronized Voter loginUser(String username, String password) {
		Voter result = null;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.LOGIN, username + "\n" + password);
			socket.send(newMsg);
			newMsg = socket.receive();
			result = (Voter) newMsg.getData();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static synchronized boolean userHasVoted(Voter v) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, v);
			socket.send(newMsg);
			newMsg = socket.receive();
			result = (boolean) newMsg.getData();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static synchronized boolean vote(Candidate c, Voter v) {
		Vote vote = new Vote(v, c);
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.VOTE, vote);
			socket.send(newMsg);
			newMsg = socket.receive();
			result = (boolean) newMsg.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<Candidate> getDistrictCandidates(District d) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();

		// fetch from district server;
		try {
			Message newMsg = new Message(Message.Method.GET,
					RtvsType.CANDIDATES, "Ottawa-South");
			socket.send(newMsg);
			newMsg = socket.receive();
			candidates = (ArrayList<Candidate>) newMsg.getData();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return candidates;
	}

	public static synchronized HashMap<Candidate, Integer> getLocalResults(District d) {
		HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();

		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, "district");
			socket.send(newMsg); // Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet) newMsg.getData()).getDistrictVotes();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	public static synchronized HashMap<String, Integer> getNationalResults() {
		HashMap<String, Integer> results = new HashMap<String, Integer>();

		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, "National");
			socket.send(newMsg); // Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet) newMsg.getData()).getTotalVotes();

		} catch (IOException e) {
			e.printStackTrace();
			reconnect();
		}

		return results;
	}
	
	public static void sendDisconnect() { 
		try { 
			Message msg = new Message(Message.Method.POST, RtvsType.DISCONNECT, null);
			socket.send(msg);
		} catch(IOException e) { 
			e.printStackTrace();
		} 
	}
	
	public static void reconnect() { 
		sendDisconnect();
		socket.close();
		try {
			socket = new WSocket().connect(districtServerPort);
		} catch ( SocketTimeoutException e ) { 
			// TODO should probably notify client in GUI then close down
			e.printStackTrace();
		} catch (UnknownHostException | SocketException e1) {
			e1.printStackTrace();
		}
	}

	public void simulate(String inputFolder, final String outputFolder) {
		BufferedWriter out = null;

		try {
			ArrayList<Thread> threads = new ArrayList<Thread>();
			File folder = new File(inputFolder);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				final File file = listOfFiles[i];
				final int fileCount = i + 1;
				if (file.isFile()) {
					threads.add(new Thread(new Runnable() {
						public void run() {
							ClientController.simulateFromFile(
									file.getAbsolutePath(), outputFolder
											+ File.separator + "client_output"
											+ fileCount + ".txt");
						}
					}));
				}
			}

			for (Thread t : threads)
				t.start();
			for (Thread t : threads)
				t.join();

			out = new BufferedWriter(new FileWriter(outputFolder
					+ File.separator + "final_output.txt"));
			out.write("Results after all voting session");
			out.newLine();
			HashMap<Candidate, Integer> results = getLocalResults(new District(
					"Ottawa South"));
			for (Map.Entry<Candidate, Integer> entry : results.entrySet()) {
				out.write(((Candidate) entry.getKey()).toString() + " - "
						+ ((Integer) entry.getValue()).toString() + " votes");
				out.newLine();
			}
		} catch (Exception e) {
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

	public static void simulateFromFile(String inputFile, String outputFile) {

		BufferedWriter out = null;
		District district = new District("Ottawa South");

		try {
			out = new BufferedWriter(new FileWriter(outputFile));
			out.write("Populating the system with Voters read in from "
					+ inputFile);
			out.newLine();

			ArrayList<Person> voters = new ArrayList<Person>();
			ArrayList<Person> candidates = new ArrayList<Person>();
			SystemPopulator.populateVotersAndCandidates(inputFile, voters,
					candidates);

			out.write("Total Voters: " + voters.size());
			out.newLine();
			out.write("Done Populating.");
			out.newLine();
			out.newLine();

			out.write("Registering Voters on the Server.");
			out.newLine();
			for (int i = 0; i < voters.size(); ++i) {
				Voter voter = (Voter) voters.get(i);
				if (registerUser(voter)) {
					out.write("Registration Successful: " + voter.toString());
				} else {
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
			} else {
				out.write("There are no parties in the District Server");
				out.newLine();
				out.newLine();
			}

			out.write("Getting Candidates from the Server.");
			out.newLine();
			ArrayList<Candidate> districtCandidates = getDistrictCandidates(district);
			int numCandidates = districtCandidates.size();
			if (numCandidates > 0) {
				out.write("District Candidates:");
				out.newLine();
				for (int i = 0; i < numCandidates; ++i) {
					out.write(districtCandidates.get(i).toString());
					out.newLine();
				}
				out.newLine();

				out.write("Voters voting for Candidates.");
				out.newLine();
				Random randomGenerator = new Random();
				for (int i = 0; i < voters.size(); ++i) {
					Voter voter = (Voter) voters.get(i);
					Voter serverVoter = loginUser(voter.getUsername(),
							voter.getPassword());
					if (serverVoter != null) {
						out.write("Login Successful: " + serverVoter.toString());
						out.newLine();
						int index = randomGenerator.nextInt(numCandidates);
						Candidate luckyCandidate = districtCandidates
								.get(index);
						if (vote(luckyCandidate, serverVoter)) {
							out.write("Vote Successful: Voted for "
									+ luckyCandidate.toString());
							out.newLine();
						} else {
							out.write("Vote Failed");
							out.newLine();
						}
					} else {
						out.write("Login Failed: " + voter.toString());
					}
					out.newLine();
				}
				out.newLine();
			} else {
				out.write("There are no Candidates in the District Server");
				out.newLine();
				out.newLine();
			}

			out.write("Results after current voting session");
			out.newLine();
			HashMap<Candidate, Integer> results = getLocalResults(district);
			for (Map.Entry<Candidate, Integer> entry : results.entrySet()) {
				out.write(((Candidate) entry.getKey()).toString() + " - "
						+ ((Integer) entry.getValue()).toString() + " votes");
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
		
		// To make the GUI look nicer
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		// Create GUI
		@SuppressWarnings("unused")
		ClientGUI gui = new ClientGUI(new District("Ottawa South"));
		
		System.out.println("Started GUI");
	}

	public static void main(String[] args) {
		try {
			int mode = Integer.parseInt(args[0]);
			int serverPort = Integer.parseInt(args[1]);
			final ClientController client = new ClientController(serverPort);
			
			Runtime.getRuntime().addShutdownHook(
				new Thread(new Runnable() { 
					@Override public void run() { 
						client.closeSocket();
					}
				})
			);
			
			System.out.println(client.getSocket().port);

			if (mode == ClientController.SIMULATION_MODE) {
				client.simulate(args[2], args[3]);
			} else if (mode == ClientController.USER_MODE) {
				client.startUI();
			}

		} catch (Exception e) {
			System.err.println(e);
			System.out.println("Usage: ClientController <mode> <serverPort> [<inputFolder> <outputFolder>]");
		}
	}

}
