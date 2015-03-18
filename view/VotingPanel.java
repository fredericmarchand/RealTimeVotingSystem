package view;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import networking.WSocket;
import controller.ClientController;
import model.Address;
import model.Candidate;
import model.District;

public class VotingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// The Socket
	WSocket socket;
	
	// The District
	District district;
	
	// These are the components
	JButton          submitButton;
	JButton          cancleButton;
	JList<Candidate> districtCandidatesList;
	JScrollPane      districtCandidatesScrollPane;
	JLabel           partyLabel;
	JTextField       partyField;
	JLabel           candidateLabel;
	JTextField       candidateField;
	JLabel           districtLabel;
	JTextField       districtField;
	
	public VotingPanel(final District district, WSocket socket) {
		
		this.district = district;
		this.socket = socket;
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		districtCandidatesList = Utilities.newJList(ClientController.getDistrictCandidates(district, socket), new Font("Courier New", Font.BOLD, 24));
		
		add(Utilities.newJLabel(district.getName()+" Candidates",0,0,layout));
		districtCandidatesScrollPane = Utilities.newJScrollPane(districtCandidatesList, 0, 1, 1, 4, layout);
		add(districtCandidatesScrollPane);
		
		candidateLabel = Utilities.newJLabel("Candidate:", 1, 1, layout);
		add(candidateLabel);
		
		candidateField = Utilities.newJTextField("", 2, 1, 2, 1, layout);
		candidateField.setEnabled(false);
		add(candidateField);
		
		partyLabel = Utilities.newJLabel("Party:", 1, 2, layout);
		add(partyLabel);
		
		partyField = Utilities.newJTextField("", 2, 2, 1, 1, layout);
		partyField.setEnabled(false);
		add(partyField);
		
		districtLabel = Utilities.newJLabel("District:", 1, 3, layout);
		add(districtLabel);
		
		districtField = Utilities.newJTextField("", 2, 3, 1, 1, layout);
		districtField.setEnabled(false);
		add(districtField);
		
		cancleButton = Utilities.newJButton("Cancle", 1, 4, 1, 1, layout);
		add(cancleButton);
		
		submitButton = Utilities.newJButton("Submit", 2, 4, 1, 1, layout);
		add(submitButton);
		
		districtCandidatesList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		    	Candidate selected = districtCandidatesList.getSelectedValue();
		    	
		    	candidateField.setText(selected.getName());
		        partyField.setText(selected.getParty().getName());
		        districtField.setText(district.getName());
		    }
		});
		
		submitButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		        if(districtCandidatesList.getSelectedValue() != null) {
		        	System.out.println(districtCandidatesList.getSelectedValue().getName());
		        }
		    }
		});
	}
	
	public JButton getSubmitButton() {
		return submitButton;
	}
	
	public JButton getCancleButton() {
		return cancleButton;
	}
}
