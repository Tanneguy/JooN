package net.nooj4nlp.gui.actions.documents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;

import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class SaveForNooJActionListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JInternalFrame selectedFrame = Launcher.getDesktopPane().getSelectedFrame();
		if (selectedFrame == null)
			return;
		String cls = selectedFrame.getClass().getSimpleName();

		if (cls.equals("CorpusEditorShell"))
		{
			((CorpusEditorShell) selectedFrame).getController().saveCorpusForNooJ();
		}
		else if (cls.equals("TextEditorShell"))
		{
			((TextEditorShell) selectedFrame).getTextController().saveTextForNooJ();
		}
		else if (cls.equals("GrammarEditorShell"))
		{
			((GrammarEditorShell) selectedFrame).getController().saveGrammarForNooJ();
		}
		else if (cls.equals("DictionaryEditorShell"))
		{
			((DictionaryEditorShell) selectedFrame).getController().saveDictionaryForNooJ();
		}
		else if (cls.equals("PropDefEditorShell"))
		{
			((PropDefEditorShell) selectedFrame).getController().saveDicoDefForNooJ();
		}
		else if (cls.equals("FlexDescEditorShell"))
		{
			((FlexDescEditorShell) selectedFrame).getController().saveFlexDescForNooJ();
		}
		else if (cls.equals("ConcordanceShell"))
		{
			((ConcordanceShell) selectedFrame).getController().saveConcordanceForNooJ();
		}
		else if (cls.equals("PackageConfigurationDialog"))
		{
			((PackageConfigurationDialog) selectedFrame).getPackageConfigurationDialogController().saveProjectForNooj();
		}
	}

}
