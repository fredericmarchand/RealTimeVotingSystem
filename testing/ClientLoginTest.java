package testing;

import static org.junit.Assert.assertTrue;

import java.net.SocketTimeoutException;

import model.Address;
import model.Voter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ClientController;

public class ClientLoginTest {
	ClientController controller;

	@Before
	public void setup() throws SocketTimeoutException {
		controller = new ClientController(60002);
	}

	@Test
	public void loginExistingUser() {
		Voter v1 = new Voter("Hugh", "Jarms", new Address(), 111222333);
		v1.setPassword("secr3t");
		@SuppressWarnings("unused")
		boolean b = ClientController.registerUser(v1);
		Voter v2 = ClientController.loginUser(v1.getUsername(),
				v1.getPassword());
		assertTrue("Logged in user should be same as registered user",
				v2.equals(v1));
	}

	@Test
	public void loginNonexistingUser() {
		Voter v = new Voter("Yo", "Mama", new Address(), 000111222);
		v.setPassword("p@ssw0rd");
		assertTrue(ClientController.loginUser(v.getUsername(), v.getPassword()) == null);
	}

	@After
	public void teardown() throws SocketTimeoutException {
		if (controller != null) {
			controller.closeSocket();
		}
	}
}
