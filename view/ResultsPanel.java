package view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.District;

import org.jfree.chart.ChartPanel;

import controller.CentralServer;

public class ResultsPanel extends JPanel {
	
	class resultsChartThread implements Runnable {
		
		public void run() {
			while(true) {
				try {
					Thread.sleep(CentralServer.PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateResultsChart();
			}
		}
	} 
	
	private static final long serialVersionUID = 1L;
	
	// The district
	private District district;
	
	// Thread responsible for updating the results chart
	Thread thread;
	
	private static final String title = "Current Election Results";
	
	private ChartPanel  chartPanel;
	private JButton     loginButton;
	private JButton     registerButton;
	
	GridBagLayout layout;
	
	public ResultsPanel(District district) {
		super();
		
		this.district = district;
		
		layout = new GridBagLayout();
		setLayout(layout);
		
		chartPanel = Utilities.newResultsChartPanel(title, 0, 0, 4, 3, layout, this.district);
		add(chartPanel);

		loginButton = Utilities.newJButton("Vote", 0, 4, 1, 1, layout);
		add(loginButton);
		
		registerButton = Utilities.newJButton("Register", 1, 4, 1, 1, layout);
		add(registerButton);
		
		thread = new Thread(new resultsChartThread());
		thread.start();
	}
	
	public void updateResultsChart() {
		chartPanel.setChart(Utilities.newResultsChart(title, district));
	}
	
	public JButton getLoginButton() {
		return loginButton;
	}
	
	public JButton getRegisterButton() {
		return registerButton;
	}
}

