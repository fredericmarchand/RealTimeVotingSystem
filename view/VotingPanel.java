package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class VotingPanel extends JFrame{

	private static final long serialVersionUID = 1L;
	
// These are the components
	private JButton searchButton;
	private JTextField searchText;
	@SuppressWarnings("rawtypes")
	private JList bookList;
	@SuppressWarnings("rawtypes")
	private JList songList;

	private Font UIFont = new Font("Courier New", Font.BOLD, 16);

	// These are the get methods that are used to access the components
	public JButton getSearchButton() {
		return searchButton;
	}

	@SuppressWarnings("rawtypes")
	public JList getBookList() {
		return bookList;
	}

	@SuppressWarnings("rawtypes")
	public JList getSongList() {
		return songList;
	}

	public JTextField getSearchText() {
		return searchText;
	}

	// This is the default constructor
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public VotingPanel() {
		super();

		// Use a GridBagLayout (lotsa fun)
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints layoutConstraints = new GridBagConstraints();
		setLayout(layout);

		// Add the bookList list
		bookList = new JList();
		bookList.setFont(UIFont);
		bookList.setPrototypeCellValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		JScrollPane scrollPane = new JScrollPane(bookList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		layoutConstraints.gridx = 0;
		layoutConstraints.gridy = 0;
		layoutConstraints.gridwidth = 1;
		layoutConstraints.gridheight = 5;
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.insets = new Insets(10, 10, 10, 10);
		layoutConstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 1.0;
		layout.setConstraints(scrollPane, layoutConstraints);
		add(scrollPane);

		// Add the Add button
		searchText = new JTextField("");
		searchText.setFont(UIFont);

		layoutConstraints.gridx = 1;
		layoutConstraints.gridy = 0;
		layoutConstraints.gridwidth = 1;
		layoutConstraints.gridheight = 1;
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.insets = new Insets(10, 10, 10, 10);
		layoutConstraints.anchor = GridBagConstraints.EAST;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 0.0;
		layout.setConstraints(searchText, layoutConstraints);
		add(searchText);

		// Add the Remove button
		searchButton = new JButton("Search");
		layoutConstraints.gridx = 2;
		layoutConstraints.gridy = 0;
		layoutConstraints.gridwidth = 1;
		layoutConstraints.gridheight = 1;
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.insets = new Insets(10, 10, 10, 10);
		layoutConstraints.anchor = GridBagConstraints.EAST;
		layoutConstraints.weightx = 0.0;
		layoutConstraints.weighty = 0.0;
		layout.setConstraints(searchButton, layoutConstraints);
		add(searchButton);

		// Add the songList list
		songList = new JList();
		songList.setFont(UIFont);
		songList.setPrototypeCellValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		scrollPane = new JScrollPane(songList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		layoutConstraints.gridx = 1;
		layoutConstraints.gridy = 1;
		layoutConstraints.gridwidth = 2;
		layoutConstraints.gridheight = 4;
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.insets = new Insets(10, 10, 10, 10);
		layoutConstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutConstraints.weightx = 2.0;
		layoutConstraints.weighty = 1.0;
		layout.setConstraints(scrollPane, layoutConstraints);
		add(scrollPane);

	}

	public static void main(String args[]) {
		VotingPanel pan = new VotingPanel();
		pan.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pan.setSize(600, 300);
		pan.setVisible(true);
	}
		
}
