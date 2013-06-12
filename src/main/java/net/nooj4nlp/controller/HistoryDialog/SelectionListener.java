package net.nooj4nlp.controller.HistoryDialog;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class SelectionListener implements TreeSelectionListener
{

	private HistoryDialogController controller;
	private GrammarEditorShell editor;

	public SelectionListener(GrammarEditorShell shell, HistoryDialogController ctrl)
	{
		controller = ctrl;
		editor = shell;
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0)
	{
		if (editor == null)
			return;
		Graph grf = editor.getController().grf;
		if (grf == null)
			return;

		// C# HACK (needed?): DO NOTHING WHILE CLEARING (unfortunately dialog.tv.Nodes.Clear() fires this event)
		// if (!controller.dialog.tv.hasFocus()) return;
		if (controller.hack_clearing)
			return;

		DefaultMutableTreeNode tnode = (DefaultMutableTreeNode) controller.dialog.tv.getLastSelectedPathComponent();

		int ihistory = 0;
		for (int i = 0; i < controller.dialog.tv.getRowCount(); i++)
		{
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) controller.dialog.tv
					.getModel().getRoot()).getChildAt(i);
			if (tn.equals(tnode))
				break;
			ihistory += 2;
		}

		grf.Do(ihistory + 2);
		controller.updateCurrentFor(editor);
	}
}