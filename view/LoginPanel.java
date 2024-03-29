package view;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Voter;
import controller.ClientController;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField SINField;
	private JPasswordField passwordField;

	public LoginPanel() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		add(Utilities.newJLabel("SIN:", 0, 0, layout));
		add(Utilities.newJLabel("Password:", 0, 1, layout));

		SINField = Utilities.newJTextField("", 1, 0, 1, 1, layout);
		passwordField = Utilities.newJPasswordField("", 1, 1, layout);
		SINField.setPreferredSize(new Dimension(200, 25));
		passwordField.setPreferredSize(new Dimension(200, 25));

		add(SINField);
		add(passwordField);
	}

	public Voter login(JFrame frame) {
		String SIN;
		String password;

		ImageIcon icon = new ImageIcon("img/login_icon.png");

		while (true) {
			int res = JOptionPane.showConfirmDialog(frame, this, "Login",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					icon);
			
			if (res != JOptionPane.OK_OPTION) {
				clearFields();
				return null;
			}

			SIN = SINField.getText();
			password = new String(passwordField.getPassword());

			Voter voter = ClientController.loginUser(SIN, password);
			boolean hasVoted = (voter == null) ? false : ClientController
					.userHasVoted(voter);

			if (voter != null && !hasVoted) {
				clearFields();
				return voter;
			}

			if (hasVoted) {
				JOptionPane.showMessageDialog(frame, "You have already voted!",
						"Error", JOptionPane.OK_OPTION);
				clearFields();
				return null;
			} else {
				JOptionPane.showMessageDialog(frame, "Invalid credentials",
						"Error", JOptionPane.OK_OPTION);
			}
		}
	}

	private void clearFields() {
		SINField.setText("");
		passwordField.setText("");
	}

}