package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.ProduceParaphrasesDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

/**
 * 
 * ActionListener that opens the Find/Replace dialog
 * 
 */
public class ProduceParaphrasesActionListener implements ActionListener
{
	private GrammarEditorShell editor;

	public ProduceParaphrasesActionListener(GrammarEditorShell shell)
	{
		editor = shell;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		ProduceParaphrasesDialog produceParaphrases = new ProduceParaphrasesDialog(editor);
		Launcher.getDesktopPane().add(produceParaphrases);
		produceParaphrases.setVisible(true);
	}
}
