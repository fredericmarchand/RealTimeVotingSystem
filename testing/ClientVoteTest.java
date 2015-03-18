package testing;

import static org.junit.Assert.assertTrue;
import model.Address;
import model.Candidate;
import model.District;
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
		Candidate c = new Candidate("Green", "Bastard", new Address(), new District("Parts Unknown"), 987654321);
		assertTrue(controller.registerUser(v));
		assertTrue(controller.loginUser(v));
		controller.vote(c, v);
	}
	
	@Test
	public void voteNonexistingUser() { 
		Voter v = new Voter("Dont-exist", "Fictitious", new Address(), 123456789);
		v.setPassword("abc"); 
		Candidate c = new Candidate("Green", "Bastard", new Address(), new District("Parts Unknown"), 987654321);
		controller.vote(c, v);	
	}
	
	@Test
	public void voteTwiceUser() { 
		Voter v = new Voter("Homer", "Simpson", new Address(), 987987987);
		v.setPassword("d0h"); 
		Candidate c = new Candidate("Green", "Bastard", new Address(), new District("Parts Unknown"), 987654321);
		assertTrue(controller.registerUser(v));
		assertTrue(controller.loginUser(v));
		controller.vote(c, v);
		controller.vote(c, v);
	}
	
	@After
	public void teardown() { 
		if ( controller != null ) { 
			controller.close();
		}
	}
}
