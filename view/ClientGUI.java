package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import model.District;
import model.Voter;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JFrame frame = this;

	// The District
	private District district;

	private RegistrationPanel registrationPanel;
	private LoginPanel loginPanel;
	private ResultsPanel resultsPanel;
	private VotingPanel votingPanel;

	public ClientGUI(District district) {
		super();

		this.district = district;

		registrationPanel = new RegistrationPanel(this.district);
		loginPanel = new LoginPanel();
		votingPanel = new VotingPanel(this.district);
		resultsPanel = new ResultsPanel(this.district);

		resultsPanel.getLoginButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Voter voter = loginPanel.login(frame);
				if (voter != null) {
					votingPanel.setVoter(voter);
					setContentPane(votingPanel);
					setSize(1000, 300);
					setVisible(true);
				}
			}
		});

		resultsPanel.getRegisterButton().addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						setContentPane(registrationPanel);
						setSize(525, 525);
						setVisible(true);
					}
				});

		registrationPanel.getSubmitButton().addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if (registrationPanel.validateFields()) {
							if (registrationPanel.registerUser()) {
								setContentPane(resultsPanel);
								setSize(1000, 500);
								setVisible(true);
							}
						}
					}
				});

		registrationPanel.getCancelButton().addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						registrationPanel.clearFields();
						setContentPane(resultsPanel);
						setSize(1000, 500);
						setVisible(true);
					}
				});

		votingPanel.getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				setContentPane(resultsPanel);
				setSize(1000, 500);
				setVisible(true);
			}
		});

		votingPanel.getSubmitButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (votingPanel.vote()) {
					resultsPanel.updateResultsCharts();
					setContentPane(resultsPanel);
					setSize(1000, 500);
					setVisible(true);
				}
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);
		setSize(1000, 500);
		setResizable(false);
		setContentPane(resultsPanel);
		setVisible(true);
	}
}
