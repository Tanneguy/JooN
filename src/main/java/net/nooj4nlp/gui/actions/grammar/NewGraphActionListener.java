package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class NewGraphActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public NewGraphActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Graph ngrf = new Graph();
		ngrf.wholeGrammar = controller.grammar;
		ngrf.name = "Untitled " + controller.graphnum;
		controller.graphnum++;
		ngrf.addNode("<E>", 30, 30);
		ngrf.addNode("", 50, 60);

		controller.grammar.graphs.add(ngrf);
		controller.current = controller.grammar.graphs.size() - 1;
		controller.grf = controller.grammar.graphs.get(controller.current);

		if (controller.dialogHistory != null && controller.dialogHistory.isVisible())
			controller.dialogHistory.getController().updateNewFor(controller.editor);
		controller.modify("add new graph", true, false);
		controller.updateFormHeader();
	}

}
