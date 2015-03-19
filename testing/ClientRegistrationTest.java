package testing;

import static org.junit.Assert.*;
import model.Address;
import model.Voter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ClientController;

public class ClientRegistrationTest 
{
	ClientController controller;
	
	@Before
	public void setup() {
		controller = new ClientController(60002);
	}

	@Test
	public void registerNewUser() { 
		Voter v = new Voter("Robin", "Banks", new Address(), 999888777);
		v.setPassword("secr3t");
		boolean b = ClientController.registerUser(v);
		assertTrue("registering new user should return true", b);
	}
	
	@Test
	public void registerExistingUser() { 
		Voter v = new Voter("Sharon", "Needles", new Address(), 777666555);
		v.setPassword("secr3t");
		boolean b = ClientController.registerUser(v);
		b = ClientController.registerUser(v);
		assertFalse("attempting to register an existing user should return false", b);
	}
	
	@After
	public void teardown() { 
		if ( controller != null ) { 
			controller.closeSocket();
		}
	}
}