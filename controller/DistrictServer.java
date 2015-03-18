package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.lang.Thread;
import java.net.SocketException;
import java.net.UnknownHostException;

import networking.*;
import model.*;

public class DistrictServer {
	
	private District district;
	private WSocket socket;
	HashMap<Integer, Voter> registeredVoters;
	HashSet<Vote> votes;
	
	public DistrictServer(String name, Province province, int port) {
		district = new District (name, province);
		try {
			socket = new WSocket().listen(port);
        } catch (UnknownHostException | SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		registeredVoters = new HashMap<Integer, Voter>();
		votes = new HashSet<Vote>();
	}
	
	public District getDistrict() {
		return district;
	}
	
	public WSocket getSocket() {
		return socket;
	}
 	
 	public void receiveMessages() {
		try {
	        while ( true ) {
	            Message msg = null;            
	            try {                
	                msg = socket.receive();
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
								socket.sendTo(msg, sender);
							}
							else {
								//Register voter
								registeredVoters.put(voter1.getSIN(), voter1);
							
								//Return true
								msg = new Message(Message.Method.GET, RtvsType.REGISTER, Boolean.TRUE);
								socket.sendTo(msg, sender);
							}
							break;
						case RtvsType.LOGIN:
							Voter voter2 = (Voter)msg.getData();
							if (registeredVoters.containsKey(voter2.getSIN()) && 
								registeredVoters.get(voter2.getSIN()).getPassword().equals(voter2.getPassword())) {
								
								//Voter is registered return true
								msg = new Message(Message.Method.GET, RtvsType.LOGIN, Boolean.TRUE);
								socket.sendTo(msg, sender);
							}
							else {
								//Voter is not registered or doesnt have the right password return false
								msg = new Message(Message.Method.GET, RtvsType.LOGIN, Boolean.FALSE);
								socket.sendTo(msg, sender);
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
							socket.sendTo(msg, sender);
							break;
						case RtvsType.VOTE:
							Vote vote = (Vote)msg.getData();
							if (!votes.contains(vote)) { // <-- negated proposition, i think this is what was intended
								votes.add(vote);
								System.out.println("vote");
							}
							break;
						default:
							break;
					}
	            } catch ( Exception e ) {
	                ////
	                // The checksums did not match!
	                System.err.println(e);
	                // do nothing, 
	                // the client should resend the same message
	            }
	        }
	    } catch (Exception e) {
	    	socket.close();
	    	e.printStackTrace();
	    }
   	}
	
	public static void main(String[] args) {
		try {
			String districtName = args[0];
			String provinceName = args[1];
			int port = Integer.parseInt(args[2]);
			
			final DistrictServer server = new DistrictServer(districtName, Province.getProvinceFromName(provinceName), port);
			System.out.println("DistrictServer "+districtName+", "+provinceName+" running on "+port);
			new Thread(new Runnable() {
				public void run() {
					server.receiveMessages();
				}
			}).start();

			/*// Connect to database
			try {

				// direct java to the sqlite-jdbc driver jar code
				// load the sqlite-JDBC driver using the current class loader
				Class.forName("org.sqlite.JDBC");

				// create connection to a database in the project home directory.
				// if the database does not exist one will be created in the home
				// directory
				Connection database = DriverManager.getConnection("jdbc:sqlite:" + server.getDistrict().getName());
				
				

				database.close(); //close connection to database

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		} catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Usage: DistrictServer <port>");
	    }
	}
}
