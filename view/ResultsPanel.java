package view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.District;
import networking.WSocket;

import org.jfree.chart.ChartPanel;

public class ResultsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// The socket
	WSocket socket;
	
	// The district
	District district;
	
	ChartPanel  resultsChart;
	JButton     loginButton;
	JButton     registerButton;
	
	public ResultsPanel(District district, WSocket socket) {
		super();
		
		this.district = district;
		this.socket   = socket;
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		resultsChart = Utilities.newResultsChartPanel("Current Election Results", 0, 0, 4, 3, layout, district, socket);
		add(resultsChart);

		loginButton = Utilities.newJButton("Vote", 0, 4, 1, 1, layout);
		add(loginButton);
		
		registerButton = Utilities.newJButton("Register", 1, 4, 1, 1, layout);
		add(registerButton);
	}
	
	public JButton getLoginButton() {
		return loginButton;
	}
	
	public JButton getRegisterButton() {
		return registerButton;
	}
}

