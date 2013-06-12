package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.HistoryDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

/**
 * 
 * ActionListener that opens the Find/Replace dialog
 * 
 */
public class HistoryDialogActionListener implements ActionListener
{
	private GrammarEditorShell editor;

	public HistoryDialogActionListener(GrammarEditorShell shell)
	{
		editor = shell;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		HistoryDialog history = editor.getController().dialogHistory;
		history.getController().updateNewFor(editor);
		Launcher.getDesktopPane().add(history);
		history.setVisible(true);
	}
}