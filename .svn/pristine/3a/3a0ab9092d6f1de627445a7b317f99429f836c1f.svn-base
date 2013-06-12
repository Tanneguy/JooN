package net.nooj4nlp.controller.GrammarDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

public class RuleGraphicalRadioListener implements ActionListener
{

	private JRadioButton rdbtnRuleEditor;
	private JCheckBox chckbxLockGrammar;
	private JRadioButton rdbtnNoDisplay;
	private JRadioButton rdbtnCommunity;
	private JPasswordField fldPassword;
	private JLabel lblPassword;

	public RuleGraphicalRadioListener(JRadioButton rdbtnRuleEditor, JCheckBox chckbxLockGrammar,
			JRadioButton rdbtnNewRadioButton, JRadioButton rdbtnCommunity, JPasswordField fldPassword,
			JLabel lblPassword)
	{
		super();
		this.rdbtnRuleEditor = rdbtnRuleEditor;
		this.chckbxLockGrammar = chckbxLockGrammar;
		this.rdbtnNoDisplay = rdbtnNewRadioButton;
		this.rdbtnCommunity = rdbtnCommunity;
		this.fldPassword = fldPassword;
		this.lblPassword = lblPassword;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		rdbtnNoDisplay.setEnabled(false);
		rdbtnNoDisplay.setSelected(false);
		rdbtnCommunity.setEnabled(false);
		rdbtnCommunity.setSelected(false);

		lblPassword.setEnabled(false);
		fldPassword.setText("");
		fldPassword.setEnabled(false);

		chckbxLockGrammar.setSelected(false);
		chckbxLockGrammar.setEnabled(!rdbtnRuleEditor.isSelected());
	}

}
