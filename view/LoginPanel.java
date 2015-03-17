package view;

import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	JTextField     usernameField;
	JPasswordField passwordField;

	public LoginPanel() {
	    GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
	    
	    add(Utilities.newJLabel("SIN", 0, 0, layout));
	    add(Utilities.newJLabel("Password", 0, 1, layout));
	    
	    usernameField = Utilities.newJTextField("", 1, 0, 1, 1, layout);
	    passwordField = Utilities.newJPasswordField("", 1, 1, layout);
	    
	    add(usernameField);
	    add(passwordField);
	}
	
	public boolean login(JFrame frame) {
		String username;
		String password;
		
		JOptionPane.showConfirmDialog(frame, this, "Login", JOptionPane.OK_CANCEL_OPTION);
		
		username = usernameField.getText();
		password = new String(passwordField.getPassword());
		
		return true;
	}
	
}