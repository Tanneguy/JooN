package net.nooj4nlp.controller.HistoryDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class ForwardActionListener implements ActionListener
{

	private GrammarEditorShell formGrammar;
	private HistoryDialogController controller;

	public ForwardActionListener(GrammarEditorShell shell, HistoryDialogController hd)
	{
		formGrammar = shell;
		controller = hd;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (formGrammar == null)
			return;
		if (formGrammar.getController().grf == null)
			return;
		formGrammar.getController().grf.redo();
		controller.updateCurrentFor(formGrammar);
	}

}
