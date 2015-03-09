package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import networking.Message;
import networking.MessageCorruptException;
import networking.WSocket;
import model.*;

public class ClientController {
	
	
	public static boolean registerUser(Person p, WSocket socket) {
		//Push information to district server
		//Wait for reply on registration acceptance
		
		boolean result = false;
		try {
			Message newMsg = new Message(Message.Method.POST, Message.Type.REGISTER, p);
			socket.sendTo(newMsg, 60002);
			newMsg = socket.receive();
			result = (boolean)newMsg.getData();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessageCorruptException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static ArrayList<Candidate> getDistrictCandidates(District d, WSocket socket) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		
		//fetch from district server;
		
		return candidates;
	}
	
	public static void vote(Candidate c, Person p, WSocket socket) {
		
	}
	
	public static HashMap<Candidate, Integer> getResults(District d, WSocket socket) {
		HashMap<Candidate, Integer> results = new HashMap<Candidate, Integer>();
		
		//fetch from district server
		
		return results;
	}
	
	public static void main(String[] args) {
		
		WSocket socket = new WSocket();
		socket.connect();
		
		//Define global variables accessible to every thread
		@SuppressWarnings("unused")
		Voter user;

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
