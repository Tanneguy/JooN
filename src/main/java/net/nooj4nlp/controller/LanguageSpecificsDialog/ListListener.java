package net.nooj4nlp.controller.LanguageSpecificsDialog;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

public class ListListener implements ListSelectionListener
{

	private JList listIsoName;
	private JTextField textField, textField_1;
	private JLabel lblDate;

	public ListListener(JList listIsoName, JTextField textField, JTextField textField_1, JLabel lblDate)
	{
		super();
		this.listIsoName = listIsoName;
		this.textField = textField;
		this.textField_1 = textField_1;
		this.lblDate = lblDate;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0)
	{
		String lname = (String) listIsoName.getSelectedValue();
		if (lname == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please select a language", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Language lan = new Language(lname);

		Date today = new Date();
		lblDate.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, lan.locale).format(today));

		textField.setText(lan.natName);
		textField_1.setText(lan.engName);
	}
}