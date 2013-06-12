package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class DeleteGraphActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public DeleteGraphActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (controller.currentNode == null)
			return;

		String gname = controller.grf.name;
		if (controller.grammar.graphs.size() > 1)
		{
			controller.removeCurrentGraph();
			if (this.controller.current > 0)
				controller.current--;
			controller.modify("delete graph " + gname, true, false);
		}
		controller.updateFormHeader();
		controller.editor.invalidate();
		controller.editor.repaint();
	}
}
