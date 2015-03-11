package view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Province;

public class RegistrationPanel extends JPanel {

	// These are the components
	private JTextField         firstNameText;
	private JTextField         lastNameText;
	private JTextField         idText;
	private JTextField         streetNumberText;
	private JTextField         streetText;
	private JTextField         cityText;
	private JTextField         postalCodeText;
	private JComboBox<String>  provincesComboBox;
	private JButton            submitButton;

	private final Color ERROR_COLOUR = Color.RED;
	private final Color SUCCESS_COLOUR = Color.GREEN;

	public RegistrationPanel() {
		super();

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		add(Utilities.newJLabel("First Name:",0,0,layout));
		firstNameText = Utilities.newJTextField("",1,0,layout);
		add(firstNameText);

		add(Utilities.newJLabel("Last Name:",0,1,layout));
		lastNameText = Utilities.newJTextField("",1,1,layout);
		add(lastNameText);

		add(Utilities.newJLabel("ID:",0,2,layout));
		idText = Utilities.newJTextField("",1,2,layout);
		add(idText);

		add(Utilities.newJLabel("Street Number:",0,3,layout));
		streetNumberText = Utilities.newJTextField("",1,3,layout);
		add(streetNumberText);

		add(Utilities.newJLabel("Street:",0,4,layout));
		streetText = Utilities.newJTextField("",1,4,layout);
		add(streetText);

		add(Utilities.newJLabel("City:",0,5,layout));
		cityText = Utilities.newJTextField("",1,5,layout);
		add(cityText);

		add(Utilities.newJLabel("Postal Code:",0,6,layout));
		postalCodeText = Utilities.newJTextField("",1,6,layout);
		add(postalCodeText);

		add(Utilities.newJLabel("Province:",0,7,layout));
		provincesComboBox = Utilities.newJComboBox(Province.getProvinceList(),1,7,layout);
		add(provincesComboBox);

		submitButton = Utilities.newJButton("Submit", 1, 8, layout);
		submitButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		        if(validateFields()) {
		        }
		    }
		});
		add(submitButton);
	}

	private boolean validateFields() {
		boolean bool = true;

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

		if(!idText.getText().isEmpty() && idText.getText().matches("^[0-9]+$")) {
			idText.setForeground(SUCCESS_COLOUR);
		} else {
			idText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!streetNumberText.getText().isEmpty() && streetNumberText.getText().matches("^[0-9]+$")) {
			streetNumberText.setForeground(SUCCESS_COLOUR);
		} else {
			streetNumberText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!streetText.getText().isEmpty() && streetText.getText().matches("^[a-zA-Z]+$")) {
			streetText.setForeground(SUCCESS_COLOUR);
		} else {
			streetText.setForeground(ERROR_COLOUR);
			bool = false;
		}

		if(!cityText.getText().isEmpty() && cityText.getText().matches("^[a-zA-Z]+$")) {
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

	public static void main(String[] args) {
		//Ask for window decorations provided by the look and feel.
		JFrame.setDefaultLookAndFeelDecorated(true);

		//Create the frame.
		JFrame frame = new JFrame("A window");

		frame.add(new RegistrationPanel());
		frame.setSize(500,500);
		frame.setVisible(true);
	}
}