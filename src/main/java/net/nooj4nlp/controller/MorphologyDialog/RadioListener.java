package net.nooj4nlp.controller.MorphologyDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JRadioButton;

public class RadioListener implements ActionListener
{

	private JComboBox comboWordRoot, comboCommandSuffix, comboLemma, comboExpression, comboLookup;
	private JRadioButton rdbtnWordCommand, rdbtnLemmaExpression, rdbtnLookup;

	public RadioListener(JComboBox comboWordRoot, JComboBox comboCommandSuffix, JComboBox comboLemma,
			JComboBox comboExpression, JComboBox comboLookup, JRadioButton rdbtnWordCommand,
			JRadioButton rdbtnLemmaExpression, JRadioButton rdbtnLookup)
	{
		super();
		this.comboWordRoot = comboWordRoot;
		this.comboCommandSuffix = comboCommandSuffix;
		this.comboLemma = comboLemma;
		this.comboExpression = comboExpression;
		this.comboLookup = comboLookup;
		this.rdbtnWordCommand = rdbtnWordCommand;
		this.rdbtnLemmaExpression = rdbtnLemmaExpression;
		this.rdbtnLookup = rdbtnLookup;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (rdbtnWordCommand.isSelected())
		{
			comboWordRoot.setEnabled(true);
			comboCommandSuffix.setEnabled(true);
			comboLemma.setEnabled(false);
			comboExpression.setEnabled(false);
			comboLookup.setEnabled(false);
		}
		else if (rdbtnLemmaExpression.isSelected())
		{
			comboWordRoot.setEnabled(false);
			comboCommandSuffix.setEnabled(false);
			comboLemma.setEnabled(true);
			comboExpression.setEnabled(true);
			comboLookup.setEnabled(false);
		}
		else if (rdbtnLookup.isSelected())
		{
			comboWordRoot.setEnabled(false);
			comboCommandSuffix.setEnabled(false);
			comboLemma.setEnabled(false);
			comboExpression.setEnabled(false);
			comboLookup.setEnabled(true);
		}
	}

}
