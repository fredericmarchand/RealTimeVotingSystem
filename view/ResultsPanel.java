package view;

import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.District;

import org.jfree.chart.ChartPanel;

import controller.CentralServer;

public class ResultsPanel extends JPanel {

	class resultsChartThread implements Runnable {

		public void run() {
			while (true) {
				try {
					Thread.sleep(CentralServer.PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateResultsCharts();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	// The district
	private District district;

	// Thread responsible for updating the results chart
	Thread thread;

	private static final String localTitle = "Local Election Results";
	private static final String nationalTitle = "National Election Results";

	private ChartPanel localChartPanel;
	private ChartPanel nationalChartPanel;
	private JButton loginButton;
	private JButton registerButton;

	GridBagLayout layout;

	public ResultsPanel(District district) {
		super();

		this.district = district;

		layout = new GridBagLayout();
		setLayout(layout);

		localChartPanel = Utilities.newLocalResultsChartPanel(localTitle, 0, 0,
				4, 3, layout, this.district);
		add(localChartPanel);

		nationalChartPanel = Utilities.newNationalResultsChartPanel(
				nationalTitle, 4, 0, 4, 3, layout);
		add(nationalChartPanel);

		loginButton = Utilities.newJButton("Vote    ", 0, 4, 4, 1, layout);
		loginButton.setIcon(new ImageIcon("img/vote_icon.png"));
		add(loginButton);

		registerButton = Utilities.newJButton("Register", 5, 4, 4, 1, layout);
		registerButton.setIcon(new ImageIcon("img/register_icon.png"));
		add(registerButton);

		thread = new Thread(new resultsChartThread());
		thread.start();
	}

	public void updateResultsCharts() {
		localChartPanel.setChart(Utilities.newLocalResultsChart(localTitle,
				district));
		nationalChartPanel.setChart(Utilities
				.newNationalResultsChart(nationalTitle));
	}

	public JButton getLoginButton() {
		return loginButton;
	}

	public JButton getRegisterButton() {
		return registerButton;
	}
}
