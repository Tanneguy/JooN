package net.nooj4nlp.gui.actions.documents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class SaveActionListener implements ActionListener
{

	private boolean saveas;

	public SaveActionListener(boolean sa)
	{
		saveas = sa;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JInternalFrame selectedFrame = Launcher.getDesktopPane().getSelectedFrame();
		if (selectedFrame == null)
			return;
		String cls = selectedFrame.getClass().getSimpleName();

		if (!saveas)
		{
			if (cls.equals("CorpusEditorShell"))
			{
				((CorpusEditorShell) selectedFrame).getController().saveCorpus();
			}
			else if (cls.equals("TextEditorShell"))
			{
				((TextEditorShell) selectedFrame).getTextController().saveText();
			}
			else if (cls.equals("GrammarEditorShell"))
			{
				((GrammarEditorShell) selectedFrame).getController().saveGrammar();
			}
			else if (cls.equals("DictionaryEditorShell"))
			{
				((DictionaryEditorShell) selectedFrame).getController().saveDictionary();
			}
			else if (cls.equals("PropDefEditorShell"))
			{
				((PropDefEditorShell) selectedFrame).getController().saveDicoDef();
			}
			else if (cls.equals("FlexDescEditorShell"))
			{
				((FlexDescEditorShell) selectedFrame).getController().saveFlexDesc();
			}
			else if (cls.equals("ConcordanceShell"))
			{
				((ConcordanceShell) selectedFrame).getController().saveConcordance();
			}
			else if (cls.equals("PackageConfigurationDialog"))
			{
				((PackageConfigurationDialog) selectedFrame).getPackageConfigurationDialogController().saveProject();
			}
			else
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.DO_NOT_KNOW_SAVE_MESSAGE,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
		{
			if (cls.equals("CorpusEditorShell"))
			{
				((CorpusEditorShell) selectedFrame).getController().saveAsCorpus();
			}
			else if (cls.equals("TextEditorShell"))
			{
				((TextEditorShell) selectedFrame).getTextController().saveAsText();
			}
			else if (cls.equals("GrammarEditorShell"))
			{
				((GrammarEditorShell) selectedFrame).getController().saveAsGrammar();
			}
			else if (cls.equals("DictionaryEditorShell"))
			{
				((DictionaryEditorShell) selectedFrame).getController().saveAsDictionary();
			}
			else if (cls.equals("PropDefEditorShell"))
			{
				((PropDefEditorShell) selectedFrame).getController().saveAsDicoDef();
			}
			else if (cls.equals("FlexDescEditorShell"))
			{
				((FlexDescEditorShell) selectedFrame).getController().saveAsFlexDesc();
			}
			else if (cls.equals("ConcordanceShell"))
			{
				((ConcordanceShell) selectedFrame).getController().saveConcordance();
			}
			else if (cls.equals("PackageConfigurationDialog"))
			{
				((PackageConfigurationDialog) selectedFrame).getPackageConfigurationDialogController().saveAsProject();
			}
			else
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.DO_NOT_KNOW_SAVE_AS_MESSAGE,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}