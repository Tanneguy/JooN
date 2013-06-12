package net.nooj4nlp.controller.GrammarDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class ButtonListener implements ActionListener
{
   
    
    
    
	private JDialog dialog;
	private JList listInputLang;
	private JList listOutputLang;
	private JTextField fldAuthor;
	private JTextField fldInsitution;
	private JRadioButton rdbtnRuleEditor;

	public ButtonListener(JDialog dialog, JList listInputLang, JList listOutputLang, JTextField fldAuthor,
			JTextField fldInsitution, JRadioButton rdbtnRuleEditor)
	{
		super();
		this.dialog = dialog;
		this.listInputLang = listInputLang;
		this.listOutputLang = listOutputLang;
		this.fldAuthor = fldAuthor;
		this.fldInsitution = fldInsitution;
		this.rdbtnRuleEditor = rdbtnRuleEditor;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		// select input language
		String ilanguage = (String) listInputLang.getSelectedValue();
		Language ilan = new Language(ilanguage);

		// select grammar type
		String buttonText = ((JButton) arg0.getSource()).getText();
		GramType gt;
		if (buttonText.equals("Inflection & Derivation"))
		{
			gt = GramType.FLX;
		}
		else if (buttonText.equals("Productive Morphology"))
		{
			gt = GramType.MORPHO;
		}
		else
		{
			gt = GramType.SYNTAX;
		}

		// select output language
		Language olan = null;
		String olanguage = null;
		if (gt == GramType.FLX)
		{
			olanguage = (String) listInputLang.getSelectedValue();
			olan = new Language(ilanguage);
		}
		else
		{
			olanguage = (String) listOutputLang.getSelectedValue();
			olan = new Language(olanguage);
		}

		// author & institution
		String author = fldAuthor.getText();
		String institution = fldInsitution.getText();

		// select grammar editor
		if (rdbtnRuleEditor.isSelected())
		{
			// new RULE grammar
			FlexDescEditorShell rEditor = new FlexDescEditorShell();
			rEditor.getController().initLoad(gt, ilan, olan, author, institution);
			Launcher.getDesktopPane().add(rEditor);
			rEditor.setVisible(true);
			dialog.dispose();
			return;
		}
		else
		{
			String password = "";
			short locktype = 0;
			GrammarEditorShell gEditor = new GrammarEditorShell(ilan, olan);
			
			gEditor.getController().newGrammar(gt, author, institution, password, locktype, ilanguage, olanguage,
					Launcher.preferences);
			Launcher.getDesktopPane().add(gEditor);
			gEditor.setVisible(true);
			
			dialog.dispose();
			return;
		}
	}
}