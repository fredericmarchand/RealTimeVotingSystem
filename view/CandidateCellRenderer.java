package view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import model.Candidate;

public class CandidateCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	public CandidateCellRenderer() {
		super();
	}

	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value instanceof Candidate) {
			Candidate candidate = (Candidate) value;
			setText(candidate.getName());
			setToolTipText(candidate.toString());
		}
		return this;
	}
}
