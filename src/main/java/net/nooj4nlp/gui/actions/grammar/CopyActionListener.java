package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class CopyActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public CopyActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Launcher.graphClipboard = new Graph();
		Launcher.nGraphClipboard = new ArrayList<Integer>();
		Launcher.iGraphClipboard = 0;

		// copy all selected nodes to Clipboard
		for (int inode = 0; inode < controller.grf.label.size(); inode++)
		{
			boolean sel = controller.grf.selected.get(inode);
			if (!sel)
				continue;
			Launcher.graphClipboard.addNode(controller.grf.label.get(inode),
					controller.grf.posX.get(inode), controller.grf.posY.get(inode));
			Launcher.nGraphClipboard.add(inode);
		}

		// copy all connections to nodes in Clipboard
		for (int i = 0; i < Launcher.nGraphClipboard.size(); i++)
		{
			int inode = Launcher.nGraphClipboard.get(i);
			ArrayList<Integer> newchildren = Launcher.graphClipboard.child.get(i);

			ArrayList<Integer> grfchildren = controller.grf.child.get(inode);
			for (int idest = 0; idest < grfchildren.size(); idest++)
			{
				int dest = grfchildren.get(idest);
				int index = Launcher.nGraphClipboard.indexOf(dest);
				if (index != -1)
				{
					newchildren.add(index);
				}
			}
		}
	}

}
