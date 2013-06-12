package net.nooj4nlp.gui.components;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomCell extends JPanel
{
	private static final long serialVersionUID = 1L;
	public JCheckBox checkBox;
	public JLabel label;

	public CustomCell()
	{
		checkBox = new JCheckBox("");
		checkBox.setOpaque(false);
		label = new JLabel("File");
		this.setLayout(null);
		checkBox.setBounds(0, 0, checkBox.getPreferredSize().width, checkBox.getPreferredSize().height);
		label.setLocation(checkBox.getPreferredSize().width,
				(checkBox.getPreferredSize().height - label.getPreferredSize().height) / 2);
		this.add(checkBox);
		this.add(label);
	}

	public JCheckBox getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(JCheckBox checkBox)
	{
		this.checkBox = checkBox;
	}

	public JLabel getLabel()
	{
		return label;
	}
}