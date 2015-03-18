package testing;

//import static org.junit.Assert.*;
import model.Address;
import model.Voter;

//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;

import controller.ClientController;

public class ClientRegistrationTest 
{
	ClientController controller;
	
	/*@Before
	public void setup() {
		controller = new ClientController(60002);
	}

	@Test
	public void registerNewUser() { 
		// TODO delete this test code
		Voter v = new Voter("Robin", "Banks", new Address(), 999888777);
		v.setPassword("secr3t");
		boolean b = controller.registerUser(v);
		assertTrue("registering new user: ", b);
	}
	
	@Test
	public void registerExistingUser() { 
		Voter v = new Voter("Sharon", "Needles", new Address(), 777666555);
		v.setPassword("secr3t");
		boolean b = controller.registerUser(v);
		b = controller.registerUser(v);
		assertFalse("attempt to register same user: ",b);
	}
	
	@After
	public void teardown() { 
		if ( controller != null ) { 
			controller.close();
		}
	}*/
}