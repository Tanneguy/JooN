package net.nooj4nlp.controller.HistoryDialog;

import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.dialogs.HistoryDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;

public class HistoryDialogController
{
	public HistoryDialog dialog;
	public boolean hack_clearing;
	private GrammarEditorShell formGrammar;

	public HistoryDialogController(HistoryDialog historyDialog)
	{
		dialog = historyDialog;
		if (dialog.tv != null)
		{
			((DefaultMutableTreeNode) dialog.tv.getModel().getRoot()).removeAllChildren();
			((DefaultTreeModel) dialog.tv.getModel()).reload();
		}
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("+");
		((DefaultTreeModel) dialog.tv.getModel()).setRoot(top);
	}

	public void updateNewFor(GrammarEditorShell fg)
	{
		formGrammar = fg;
		Graph grf = formGrammar.getController().grf;
		if (grf == null)
			return;

	
		hack_clearing = true;
		if (dialog.tv != null)
		{
			((DefaultMutableTreeNode) dialog.tv.getModel().getRoot()).removeAllChildren();
			((DefaultTreeModel) dialog.tv.getModel()).reload();
		}
		hack_clearing = false;

		String filename = "";
		if (formGrammar.getController().getFullName() != null)
		{
			filename = FilenameUtils.removeExtension((new File(formGrammar.getController().getFullName())).getName());
		}

		dialog.setTitle(filename + ":" + grf.name);

		if (grf.history != null)
		{
			for (int imodifs = 0; imodifs < grf.history.size(); imodifs += 2)
			{
				String modification = (String) grf.history.get(imodifs);
				DefaultMutableTreeNode tnode = new DefaultMutableTreeNode(modification);
				// A custom JTreeRenderer is needed for this purpose, but it's not implemented!
				
				((DefaultMutableTreeNode) dialog.tv.getModel().getRoot()).add(tnode);

			}
		}

		// A custom JTreeRenderer is needed for this purpose, but it's not implemented!
	
		setTreeState(dialog.tv, true);
		try
		{
			formGrammar.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		formGrammar.invalidate();
		formGrammar.repaint();
		dialog.setTitle("History of " + filename);
	}

	public void updateCurrentFor(GrammarEditorShell fg)
	{
		formGrammar = fg;
		Graph grf = formGrammar.getController().grf;

		for (int inode = 0; inode < ((DefaultMutableTreeNode) dialog.tv.getModel().getRoot()).getChildCount(); inode++)
		{
			// Unused code
			
			int imodif = inode * 2;
			if (imodif < grf.iHistory)
			{
			}
			
			else
			{
			}
			
		}
		try
		{
			formGrammar.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		setTreeState(dialog.tv, true);

		formGrammar.invalidate();
		formGrammar.repaint();
	}

	

	private static void setTreeState(JTree tree, boolean expanded)
	{
		Object root = tree.getModel().getRoot();
		setTreeState(tree, new TreePath(root), expanded);
	}

	private static void setTreeState(JTree tree, TreePath path, boolean expanded)
	{
		Object lastNode = path.getLastPathComponent();
		for (int i = 0; i < tree.getModel().getChildCount(lastNode); i++)
		{
			Object child = tree.getModel().getChild(lastNode, i);
			TreePath pathToChild = path.pathByAddingChild(child);
			setTreeState(tree, pathToChild, expanded);
		}
		if (expanded)
			tree.expandPath(path);
		else
			tree.collapsePath(path);
	}
}
