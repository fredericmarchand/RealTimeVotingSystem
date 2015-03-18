package testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Address;
import model.Voter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ClientController;

public class ClientLoginTest 
{
	ClientController controller;
	
	@Before
	public void setup() {
		controller = new ClientController(60002);
	}
	
	@Test
	public void loginExistingUser() { 
		Voter v = new Voter("Hugh", "Jarms", new Address(), 111222333);
		v.setPassword("secr3t");
		boolean b = controller.registerUser(v);
		b = controller.loginUser(v);
		assertTrue("loging in existing user: ", b);
	}
	
	@Test
	public void loginNonexistingUser() { 
		Voter v = new Voter("Yo", "Mama", new Address(), 000111222);
		v.setPassword("p@ssw0rd");
		assertFalse(controller.loginUser(v));
	}
	
	@After
	public void teardown() { 
		if ( controller != null ) { 
			controller.close();
		}
	}
}
