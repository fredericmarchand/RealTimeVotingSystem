package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class Utilities {
	
	private static Font UIFont = new Font("Courier New", Font.BOLD, 16);
	private static GridBagConstraints constraints = new GridBagConstraints();
	
	public static JButton newJButton(String label, int gridx, int gridy, GridBagLayout layout) {
		JButton jButton = new JButton(label);
		
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jButton, constraints);
		
		return jButton;
	}
	
	public static JComboBox newJComboBox(ArrayList list, int gridx, int gridy, GridBagLayout layout) {
		JComboBox<String> jComboBox = new JComboBox(list.toArray());
		
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jComboBox, constraints);
		
		return jComboBox;
	}
	
	public static JLabel newJLabel(String label, int gridx, int gridy, GridBagLayout layout) {
		JLabel jLabel = new JLabel(label);
		
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jLabel, constraints);
		
		return jLabel;
	}
	
	public static JTextField newJTextField(String text, int gridx, int gridy, GridBagLayout layout) {
		JTextField jTextField = new JTextField(text);
		jTextField.setFont(UIFont);

		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jTextField, constraints);
		
		return jTextField;
	}
}
