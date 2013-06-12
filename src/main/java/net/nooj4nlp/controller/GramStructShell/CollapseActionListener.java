package net.nooj4nlp.controller.GramStructShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.components.JPGraph;
import net.nooj4nlp.gui.utilities.Helper;

public class CollapseActionListener implements ActionListener
{

	private GramStructShellController controller;

	public CollapseActionListener(GramStructShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		controller.expandAll(false);
		GrammarEditorShellController grammarController = controller.getFormGrammar();
		grammarController.current = 0;
		grammarController.grf = grammarController.grammar.graphs.get(0);

		if (grammarController.dialogHistory != null)
			grammarController.dialogHistory.getController().updateNewFor(grammarController.editor);

		grammarController.currentNode = (DefaultMutableTreeNode) controller.tvGraphs.getModel().getRoot();

	

		JPGraph pGraph = grammarController.editor.pGraph;
		pGraph.invalidate();
		pGraph.validate();
		pGraph.repaint();
		Helper.putDialogOnTheTop(controller.structShell);
	}
}