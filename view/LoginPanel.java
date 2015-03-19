package view;

import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Voter;
import controller.ClientController;

public class LoginPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField     SINField;
	private JPasswordField passwordField;

	public LoginPanel() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
	    
	    add(Utilities.newJLabel("SIN:", 0, 0, layout));
	    add(Utilities.newJLabel("Password:", 0, 1, layout));
	    
	    SINField = Utilities.newJTextField("", 1, 0, 1, 1, layout);
	    passwordField = Utilities.newJPasswordField("", 1, 1, layout);
	    
	    add(SINField);
	    add(passwordField);
	}
	
	public Voter login(JFrame frame) {
		String SIN;
		String password;
		
		JOptionPane.showConfirmDialog(frame, this, "Login", JOptionPane.OK_CANCEL_OPTION);
		
		SIN = SINField.getText();
		password = new String(passwordField.getPassword());
		
		Voter voter = ClientController.loginUser(SIN, password);
		
		if(voter != null && !ClientController.userHasVoted(voter)) {
			return voter;
		}
		return null;
	}
	
}