package net.nooj4nlp.controller.HistoryDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class PurgeActionListener implements ActionListener
{

	private GrammarEditorShell formGrammar;

	public PurgeActionListener(GrammarEditorShell shell)
	{
		formGrammar = shell;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		for (Object g : formGrammar.getController().grammar.graphs)
		{
			if (g != null)
				((Graph) g).purgeHistory();
		}
		formGrammar.getController().ModifyExamples();
	}

}
