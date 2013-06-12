package net.nooj4nlp.controller.LanguageSpecificsDialog;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

public class CheckBoxListener implements ChangeListener
{
	private JTextField textField_2, textField_3;
	private JLabel lblEquals;
	private JCheckBox chckbxIgnoreCase;
	private JList listIsoName;

	public CheckBoxListener(JTextField textField_2, JTextField textField_3, JLabel lblEquals,
			JCheckBox chckbxIgnoreCase, JList listIsoName)
	{
		super();
		this.textField_2 = textField_2;
		this.textField_3 = textField_3;
		this.lblEquals = lblEquals;
		this.chckbxIgnoreCase = chckbxIgnoreCase;
		this.listIsoName = listIsoName;
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		String lname = (String) listIsoName.getSelectedValue();
		if (lname == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please select a language", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Language lan = new Language(lname);

		int cmp = lan.sortTexts(textField_2.getText(), textField_3.getText(), chckbxIgnoreCase.isSelected());
		if (cmp < 0)
			lblEquals.setText("is before");
		else if (cmp == 0)
			lblEquals.setText("equals");
		else
			lblEquals.setText("is after");
	}
}