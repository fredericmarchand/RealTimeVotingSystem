package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import model.District;

public class ClientGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	JFrame frame = this;

	RegistrationPanel registrationPanel;
	LoginPanel        loginPanel;
	ResultsPanel      resultsPanel;
	VotingPanel       votingPanel;

	public ClientGUI() {
		super();
		
		registrationPanel = new RegistrationPanel();
		loginPanel = new LoginPanel();
		votingPanel = new VotingPanel(new District("Some District"));
		resultsPanel = new ResultsPanel();
		
		resultsPanel.getLoginButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	if(true) {
		    		setContentPane(votingPanel);
		    		setSize(700,250);
			    	setVisible(true);
		    	}
		    }
		});
		
		resultsPanel.getRegisterButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	setContentPane(registrationPanel);
		    	setSize(500,500);
		    	setVisible(true);
		    }
		});
		
		registrationPanel.getSubmitButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		        if(registrationPanel.validateFields()) {
		        	registrationPanel.registerUser();
		        	setContentPane(resultsPanel);
			    	setVisible(true);
		        }
		    }
		});
		
		registrationPanel.getCancleButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	setContentPane(resultsPanel);
		    	setSize(500,500);
		    	setVisible(true);
		    }
		});
		
		votingPanel.getCancleButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	setContentPane(resultsPanel);
		    	setSize(500,500);
		    	setVisible(true);
		    }
		});
		
		votingPanel.getSubmitButton().addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	setContentPane(resultsPanel);
		    	setSize(500,500);
		    	setVisible(true);
		    }
		});
		
		setDefaultLookAndFeelDecorated(true);
		setSize(500,500);
		setResizable(false);
		setContentPane(resultsPanel);
		setVisible(true);
	}

	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
	}
}
