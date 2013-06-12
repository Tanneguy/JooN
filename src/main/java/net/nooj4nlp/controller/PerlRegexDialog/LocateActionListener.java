package net.nooj4nlp.controller.PerlRegexDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class LocateActionListener implements ActionListener
{

	private JTextPane pane;
	private JButton button;
	private JRadioButton rdbtnPerl;
	private JRadioButton rdbtnString;
	private JComboBox comboPattern;
	private JDesktopPane desktopPane;

	public LocateActionListener(JTextPane pane, JButton button, JRadioButton rdbtnPerl, JRadioButton rdbtnString,
			JComboBox comboPattern, JDesktopPane desktopPane)
	{
		super();
		this.pane = pane;
		this.button = button;
		this.rdbtnPerl = rdbtnPerl;
		this.rdbtnString = rdbtnString;
		this.comboPattern = comboPattern;
		this.desktopPane = desktopPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object o = comboPattern.getSelectedItem();
		if (o == null)
		{
			JOptionPane.showMessageDialog(desktopPane, "Invalid pattern", "NooJ: string pattern is empty",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String query = comboPattern.getSelectedItem().toString();

		StyledDocument doc = pane.getStyledDocument();
		Style style = pane.addStyle("Color", null);
		if (button.getName().equals("btnN"))
			StyleConstants.setForeground(style, Color.red);
		else if (button.getName().equals("btnO"))
			StyleConstants.setForeground(style, Color.green);
		else if (button.getName().equals("btnO_1"))
			StyleConstants.setForeground(style, Color.magenta);
		else if (button.getName().equals("btnJ"))
			StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, true);

		if (rdbtnString.isSelected())
		{
			int begaddress = pane.getText().indexOf(query);
			while (begaddress >= 0)
			{
				int endaddress = begaddress + query.length();
				StyleContext sc = StyleContext.getDefaultStyleContext();
				sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.red);

				doc.setCharacterAttributes(begaddress, query.length(), pane.getStyle("Color"), true);

				begaddress = endaddress;
				begaddress = pane.getText().indexOf(query, begaddress);
			}
		}
		else if (rdbtnPerl.isSelected())
		{
			Pattern p;
			try
			{
				p = Pattern.compile(query);
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(desktopPane, e1.toString(), "NooJ: PERL regular expression is invalid",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			Matcher myMatcher = p.matcher(pane.getText());
			while (myMatcher.find())
				doc.setCharacterAttributes(myMatcher.start(), myMatcher.end() - myMatcher.start(),
						pane.getStyle("Color"), true);
		}
		pane.setCaretPosition(0);
	}

}
