package net.nooj4nlp.controller.GramStructShell;

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.utilities.Helper;

public class SelectionListener implements TreeSelectionListener
{

	private GramStructShellController controller;

	public SelectionListener(GramStructShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0)
	{
		if (controller.isHack_clearing())
			return;

		// Compute Current and CurrentNode
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) controller.tvGraphs.getLastSelectedPathComponent();
		if (node == null)
			return;
		String label = controller.getNameFromTreeNodeLabel(node.toString());
		int index = controller.findGraph(label);
		if (index != -1)
		{
			// found graph
			controller.getFormGrammar().current = index;
			controller.getFormGrammar().grf = controller.getFormGrammar().grammar.graphs.get(controller
					.getFormGrammar().current);

			if (controller.getFormGrammar().dialogHistory != null)
				controller.getFormGrammar().dialogHistory.getController().updateNewFor(
						controller.getFormGrammar().editor);

			controller.getFormGrammar().currentNode = node;
			if (controller.isRecursive(label, node)) // is it a recursive call?
			{
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getParent();
				while (n != null)
				{
					if (label == controller.getNameFromTreeNodeLabel(n.toString()))
					{
						controller.getFormGrammar().currentNode = n;
						break;
					}
					n = (DefaultMutableTreeNode) n.getParent();
				}
			}
		}
		else
		// non-existing graph
		{
			if (JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Create New Graph?", "NooJ",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				Graph ngrf = new Graph();
				ngrf.wholeGrammar = controller.getFormGrammar().grammar;
				ngrf.name = controller.getNameFromTreeNodeLabel(node.toString());
				ngrf.addNode("<E>", 30, 30);
				ngrf.addNode("", 50, 60);

				controller.getFormGrammar().grammar.graphs.add(ngrf);
				controller.getFormGrammar().current = controller.getFormGrammar().grammar.graphs.size() - 1;
				controller.getFormGrammar().grf = controller.getFormGrammar().grammar.graphs.get(controller
						.getFormGrammar().current);

				if (controller.getFormGrammar().dialogHistory != null)
					controller.getFormGrammar().dialogHistory.getController().updateNewFor(
							controller.getFormGrammar().editor);

				controller.getFormGrammar().currentNode = node;
				controller.tvGraphs.getModel().valueForPathChanged(controller.tvGraphs.getSelectionPath(), ngrf.name);
				
			}
			else
			{
				
				controller.getFormGrammar().currentNode = (DefaultMutableTreeNode) node.getParent();
			}
		}
		controller.tvGraphs.setSelectionPath(new TreePath(controller.getFormGrammar().currentNode));
		controller.tvGraphs.expandPath(new TreePath(controller.getFormGrammar().currentNode));
		controller.getFormGrammar().visitHistory.add(controller.getFormGrammar().currentNode);

		controller.formGrammar.invalidate();
		controller.formGrammar.repaint();
		Helper.putDialogOnTheTop(controller.structShell);
	}
}