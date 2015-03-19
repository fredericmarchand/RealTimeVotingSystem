package view;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.Candidate;
import model.District;
import model.Voter;
import controller.ClientController;

public class VotingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// The District
	District district;
	
	// The Voter
	Voter voter;
	
	// These are the components
	JButton          submitButton;
	JButton          cancelButton;
	JList<Candidate> districtCandidatesList;
	JScrollPane      districtCandidatesScrollPane;
	JLabel           partyLabel;
	JTextField       partyField;
	JLabel           candidateLabel;
	JTextField       candidateField;
	JLabel           districtLabel;
	JTextField       districtField;
	
	public VotingPanel(District district) {
		
		this.district = district;
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		districtCandidatesList = Utilities.newJList(ClientController.getDistrictCandidates(district), new Font("Courier New", Font.BOLD, 24));
		
		add(Utilities.newJLabel(district.getName()+" Candidates",0,0,layout));
		districtCandidatesScrollPane = Utilities.newJScrollPane(districtCandidatesList, 0, 1, 1, 4, layout);
		add(districtCandidatesScrollPane);
		
		candidateLabel = Utilities.newJLabel("Candidate:", 1, 1, layout);
		add(candidateLabel);
		
		candidateField = Utilities.newJTextField("", 2, 1, 2, 1, layout);
		candidateField.setEnabled(false);
		add(candidateField);
		
		partyLabel = Utilities.newJLabel("Party:", 1, 2, layout);
		candidateField.setEnabled(false);
		add(partyLabel);
		
		partyField = Utilities.newJTextField("", 2, 2, 1, 1, layout);
		partyField.setEnabled(false);
		add(partyField);
		
		districtLabel = Utilities.newJLabel("District:", 1, 3, layout);
		add(districtLabel);
		
		districtField = Utilities.newJTextField("", 2, 3, 1, 1, layout);
		districtField.setEnabled(false);
		add(districtField);
		
		cancelButton = Utilities.newJButton("Cancel", 1, 4, 1, 1, layout);
		add(cancelButton);
		
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
	}
	
	public void setVoter(Voter voter) {
		this.voter = voter;
	}
	
	public boolean vote() {
		if(districtCandidatesList.getSelectedValue() != null) {
			Candidate selected = districtCandidatesList.getSelectedValue();
			System.out.println(selected +" voted by " + voter);
			ClientController.vote(selected, voter);
			return true;
		}
		return false;
	}
	
	public JButton getSubmitButton() {
		return submitButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
}
