package controller;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import networking.Message;
import networking.MessageCorruptException;
import networking.WSocket;
import model.*;

public class ClientController {
	
	
	public static boolean registerUser(Voter v, WSocket socket) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.REGISTER, v);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean loginUser(Voter v, WSocket socket) {
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.LOGIN, v);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public static boolean userHasVoted(Voter v, WSocket socket) {		
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.HAS_VOTED, v);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void vote(Candidate c, Voter v, WSocket socket) {
		Vote vote = new Vote(v, c);
		try {
			Message newMsg = new Message(Message.Method.POST, RtvsType.VOTE, vote);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			//Dont expect response
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Candidate> getDistrictCandidates(District d, WSocket socket) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		
		//fetch from district server;
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.CANDIDATES, "Ottawa-South");
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			candidates = (ArrayList<Candidate>)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return candidates;
	}
	
	public static HashMap<Candidate, Integer> getLocalResults(District d, WSocket socket) {
		HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();
		
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, d);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet)newMsg.getData()).getDistrictVotes();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return results;
	}
	
	public static HashMap<Party, Integer> getNationalResults(WSocket socket) {
		HashMap<Party, Integer> results = new HashMap<Party, Integer>();
		
		try {
			Message newMsg = new Message(Message.Method.GET, RtvsType.RESULTS, null);
			socket.sendTo(newMsg, 60002); //Get port from list of district servers
			newMsg = socket.receive();
			results = ((ResultSet)newMsg.getData()).getTotalVotes();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public static void main(String[] args) {
		
		//Define global variables accessible to every thread
		WSocket socket = null;
		try {
			socket = new WSocket().connect(60002);
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-2); // can't continue without a socket..
		}
		//Create GUI
		
			//Click Registration button
				//Show Registration Panel
				//Submit Registration information (event handler)
			//Click Voting button
				//Show Voting Panel
				//Submit Vote (event handler)
			//Click View Results button
				//Show Results panel
		
	}

}
