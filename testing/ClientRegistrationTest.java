package testing;

import static org.junit.Assert.*;

import java.net.SocketException;
import java.net.UnknownHostException;

import model.Address;
import model.Voter;
import networking.WSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ClientController;
import controller.DistrictServer;

public class ClientRegistrationTest 
{
	WSocket socket;
	
	@Before
	public void setup() {
		try {
			socket = new WSocket().connect(60002);
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-2); // can't continue without a socket..
		}
	}

	@Test
	public void registerNewUser() { 
		// TODO delete this test code
		Voter v = new Voter("Robin", "Banks", new Address(), 999888777);
		boolean b = ClientController.registerUser(v, socket);
		assertTrue("registering new user: ", b);
	}
	
	@Test
	public void registerExistingUser() { 
		Voter v = new Voter("Sharon", "Needles", new Address(), 777666555);
		boolean b = ClientController.registerUser(v, socket);
		b = ClientController.registerUser(v, socket);
		assertFalse("attempt to register same user: ",b);
	}
	
	@Test
	public void loginExistingUser() { 
		Voter v = new Voter("Hugh", "Jarms", new Address(), 111222333);
		boolean b = ClientController.registerUser(v, socket);
		b = ClientController.loginUser(v, socket);
		assertTrue("loging in existing user: ", b);
	}
	
	@After
	public void tearDown() throws InterruptedException { 
		socket.close();
	}
}