package view;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Address;
import model.District;
import model.Province;
import model.Voter;
import controller.ClientController;

public class RegistrationPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// The District
	District district;
	
	// These are the components
	private JTextField        sinText;
	private JPasswordField    passwordText;
	private JTextField        firstNameText;
	private JTextField        lastNameText;
	private JTextField        streetNumberText;
	private JTextField        streetText;
	private JTextField        cityText;
	private JTextField        postalCodeText;
	private JComboBox<String> provincesComboBox;
	private JButton           submitButton;
	private JButton           cancelButton;

	private final Color ERROR_COLOUR   = Color.RED;
	private final Color SUCCESS_COLOUR = Color.GREEN;
	private final Color DEFAULT_COLOUR = Color.BLACK;

	public RegistrationPanel(District district) {
		super();

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		add(Utilities.newJLabel("SIN:",0,0,layout));
		sinText = Utilities.newJTextField("", 1, 0, 1, 1, layout);
		add(sinText);
		
		add(Utilities.newJLabel("New Password:",0,1,layout));
		passwordText = Utilities.newJPasswordField("", 1, 1, layout);
		add(passwordText);
		
		add(Utilities.newJLabel("First Name:",0,2,layout));
		firstNameText = Utilities.newJTextField("", 1, 2, 1, 1, layout);
		add(firstNameText);

		add(Utilities.newJLabel("Last Name:",0,3,layout));
		lastNameText = Utilities.newJTextField("", 1, 3, 1, 1, layout);
		add(lastNameText);

		add(Utilities.newJLabel("Street Number:",0,4,layout));
		streetNumberText = Utilities.newJTextField("", 1, 4, 1, 1, layout);
		add(streetNumberText);

		add(Utilities.newJLabel("Street:",0,5,layout));
		streetText = Utilities.newJTextField("", 1, 5, 1, 1, layout);
		add(streetText);

		add(Utilities.newJLabel("City:",0,6,layout));
		cityText = Utilities.newJTextField("", 1, 6, 1, 1, layout);
		add(cityText);

		add(Utilities.newJLabel("Postal Code:",0,7,layout));
		postalCodeText = Utilities.newJTextField("", 1, 7, 1, 1, layout);
		add(postalCodeText);

		add(Utilities.newJLabel("Province:",0,8,layout));
		provincesComboBox = Utilities.newJComboBox(Province.getProvinceList(),1,8,layout);
		add(provincesComboBox);

		submitButton = Utilities.newJButton("Submit", 1, 9, 1, 1, layout);
		add(submitButton);
		
		cancelButton = Utilities.newJButton("Cancel", 0, 9, 1, 1, layout);
		add(cancelButton);
	}

	public boolean validateFields() {
		
		boolean bool = true;

		if(!sinText.getText().isEmpty() && sinText.getText().matches("^[0-9]{9}$")) {
			sinText.setForeground(SUCCESS_COLOUR);
		} else {
			sinText.setForeground(ERROR_COLOUR);
			bool = false;
		}
		
		if(!firstNameText.getText().isEmpty() && firstNameText.getText().matches("^[a-zA-Z]+$")) {
			firstNameText.setForeground(SUCCESS_COLOUR);
		} else {
			firstNameText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!lastNameText.getText().isEmpty() && lastNameText.getText().matches("^[a-zA-Z]+$")) {
			lastNameText.setForeground(SUCCESS_COLOUR);
		} else {
			lastNameText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!streetNumberText.getText().isEmpty() && streetNumberText.getText().matches("^[0-9]+$")) {
			streetNumberText.setForeground(SUCCESS_COLOUR);
		} else {
			streetNumberText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!streetText.getText().isEmpty() && streetText.getText().matches("^[a-zA-Z\\s]+$")) {
			streetText.setForeground(SUCCESS_COLOUR);
		} else {
			streetText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!cityText.getText().isEmpty() && cityText.getText().matches("^[a-zA-Z\\s]+$")) {
			cityText.setForeground(SUCCESS_COLOUR);
		} else {
			cityText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(postalCodeText.getText().matches("^[a-zA-Z][0-9][a-zA-Z]\\s?[0-9][a-zA-Z][0-9]$")) {
			postalCodeText.setForeground(SUCCESS_COLOUR);
		} else {
			postalCodeText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		return bool;
	}
	
	public boolean registerUser() {
    	if(ClientController.registerUser(
			new Voter(
					firstNameText.getText(),
					lastNameText.getText(),
					new Address(
							streetNumberText.getText() + " " + streetText.getText(),
							cityText.getText(),
							Province.getProvinceFromName(provincesComboBox.getSelectedItem().toString()),
							postalCodeText.getText()
					),
					Integer.parseInt(sinText.getText()),
					new String(passwordText.getPassword()),
					district
			)
    	)) {
    		clearFields();
    		return true;
    	} else {
    		sinText.setForeground(ERROR_COLOUR);
    		return false;
    	}
	}
	
	public void clearFields() {
		sinText.setText("");
		passwordText.setText("");
		firstNameText.setText("");
		lastNameText.setText("");
		streetNumberText.setText("");
		streetText.setText("");
		cityText.setText("");
		postalCodeText.setText("");
		provincesComboBox.setSelectedIndex(0);
		sinText.setForeground(DEFAULT_COLOUR);
		passwordText.setForeground(DEFAULT_COLOUR);
		firstNameText.setForeground(DEFAULT_COLOUR);
		lastNameText.setForeground(DEFAULT_COLOUR);
		streetNumberText.setForeground(DEFAULT_COLOUR);
		streetText.setForeground(DEFAULT_COLOUR);
		cityText.setForeground(DEFAULT_COLOUR);
		postalCodeText.setForeground(DEFAULT_COLOUR);
	}
	
	public JButton getSubmitButton() {
		return submitButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
}