package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import model.Candidate;
import model.District;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import controller.ClientController;

public final class Utilities {
	
	private static Font UIFont = new Font("Courier New", Font.BOLD, 16);
	private static GridBagConstraints constraints = new GridBagConstraints();
	private static Comparator<Candidate> comparator = new Comparator<Candidate>() {
		public int compare(Candidate candidate1, Candidate candidate2) {
			return candidate1.getName().compareTo(candidate2.getName());
		}
	};
	
	public static JScrollPane newJScrollPane(JList<Candidate> jList, int gridx, int gridy, int gridwidth, int gridheight, GridBagLayout layout) {
		JScrollPane jScrollPane = new JScrollPane(jList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jScrollPane, constraints);
		
		jScrollPane.setPreferredSize(new Dimension(80,120));
		
		return jScrollPane;
	}
	
	public static ChartPanel newResultsChartPanel(String title, int gridx, int gridy, int gridwidth, int gridheight, GridBagLayout layout, District district) {
		ChartPanel chartPanel = new ChartPanel(newResultsChart(title, district));
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		
        constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		layout.setConstraints(chartPanel, constraints);
		
		return chartPanel;
	}
	
	public static JFreeChart newResultsChart(String title, District district) {
		CategoryDataset dataset = getElectionResults(district);
		JFreeChart chart = createChart(dataset, title);
		return chart;
	}
	
	private static CategoryDataset getElectionResults(District district) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();		
		HashMap<Candidate, Integer> results = ClientController.getLocalResults(district);
		
		// To have results returned in alphabetical order
		SortedSet<Candidate> keys = new TreeSet<Candidate>(comparator);
		keys.addAll(results.keySet());
		
	    Iterator<Candidate> it = keys.iterator();
	    while (it.hasNext()) {
	    	Candidate candidate = it.next();
	        dataset.addValue((Number)results.get(candidate), (Comparable<String>)candidate.getName(), (Comparable<String>)candidate.getName());
	        it.remove();
	    }

		return dataset;
	}
	 
	private static JFreeChart createChart(CategoryDataset dataset, String title) {
		JFreeChart chart = ChartFactory.createBarChart(title, "Party", "Votes", dataset);
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		chart.getLegend().setFrame(BlockBorder.NONE);
		return chart;
	}
	
	public static JList<Candidate> newJList(ArrayList<Candidate> list, Font font) {
		JList<Candidate> jList = new JList<Candidate>(list.toArray(new Candidate[list.size()]));
		jList.setFont(font);
		jList.setCellRenderer(new CandidateCellRenderer());
		
		return jList;
	}
	
	public static JButton newJButton(String label, int gridx, int gridy, int gridwidth, int gridheight, GridBagLayout layout) {
		JButton jButton = new JButton(label);
		jButton.setFont(UIFont);
		
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jButton, constraints);
		
		return jButton;
	}
	
	public static JComboBox<String> newJComboBox(ArrayList<String> list, int gridx, int gridy, GridBagLayout layout) {
		JComboBox<String> jComboBox = new JComboBox<String>(list.toArray(new String[list.size()]));
		jComboBox.setFont(UIFont);
		
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
		jLabel.setFont(UIFont);
		
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
	
	public static JTextField newJTextField(String text, int gridx, int gridy, int gridwidth, int gridheight, GridBagLayout layout) {
		JTextField jValidationTextField = new JTextField(text);
		jValidationTextField.setFont(UIFont);

		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jValidationTextField, constraints);
		
		return jValidationTextField;
	}
	
	public static JPasswordField newJPasswordField(String text, int gridx, int gridy, GridBagLayout layout) {
		JPasswordField jPasswordField = new JPasswordField(text);
		jPasswordField.setFont(UIFont);

		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		layout.setConstraints(jPasswordField, constraints);
		
		return jPasswordField;
	}
}
