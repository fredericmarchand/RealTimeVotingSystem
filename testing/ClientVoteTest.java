package testing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import model.Address;
import model.Candidate;
import model.District;
import model.Party;
import model.Voter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ClientController;

public class ClientVoteTest 
{
	ClientController controller;
	
	@Before
	public void setup() {
		controller = new ClientController(60002);
	}
	
	@Test
	public void voteExistingUser() { 
		Voter v = new Voter("Papa", "John", new Address(), 123456789);
		v.setPassword("pizz@"); 
		Candidate c = new Candidate("Green", "Bastard", new Address(), 987654321, new Party(Party.CONSERVATIVES));
		assertTrue("Register new user should pass", ClientController.registerUser(v));
		assertTrue("Loging in existing user should pass", ClientController.loginUser(v.getUsername(), v.getPassword()) != null);
		assertTrue("vote should be valid", ClientController.vote(c, v));
	}
	
	@Test
	public void voteNonexistingUser() { 
		Voter v = new Voter("Dont-exist", "Fictitious", new Address(), 123456789);
		v.setPassword("abc"); 
		Candidate c = new Candidate("Green", "Bastard", new Address(), 987654321, new Party(Party.CONSERVATIVES));
		assertFalse("Vote should be invalid", ClientController.vote(c, v));
	}
	
	@Test
	public void voteTwice() { 
		Voter v = new Voter("Homer", "Simpson", new Address(), 100100111);
		v.setPassword("d0h"); 
		Candidate c = new Candidate("Green", "Bastard", new Address(), 987654321, new Party(Party.CONSERVATIVES));
		assertTrue("Register new user should pass", ClientController.registerUser(v));
		assertTrue("Loging in existing user should pass", ClientController.loginUser(v.getUsername(), v.getPassword()) != null);
		assertTrue("vote should be valid", ClientController.vote(c, v));
		assertFalse("second vote should be invalid", ClientController.vote(c,v));
	}
	
	@After
	public void teardown() { 
		if ( controller != null ) { 
			controller.closeSocket();
		}
	}
}
