package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class PasteActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public PasteActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (Launcher.graphClipboard == null || Launcher.graphClipboard.label.size() == 0)
			return;
		Launcher.iGraphClipboard += 10;

		// copy all nodes
		int nodebasis = controller.grf.label.size();
		for (int inode = 0; inode < Launcher.graphClipboard.label.size(); inode++)
		{
			String labl = Launcher.graphClipboard.label.get(inode);
			if (labl == null || labl == "")
				labl = "<E>";
			controller.grf.addNode(labl, Launcher.graphClipboard.posX.get(inode) + Launcher.iGraphClipboard,
					Launcher.graphClipboard.posY.get(inode) + Launcher.iGraphClipboard);
			ArrayList<Integer> clpchildren = Launcher.graphClipboard.child.get(inode);

			for (int idest = 0; idest < clpchildren.size(); idest++)
			{
				int dest = clpchildren.get(idest);
				ArrayList<Integer> dstchildren = controller.grf.child.get(nodebasis + inode);
				dstchildren.add(nodebasis + dest);
			}
		}

		// select all new nodes
		for (int inode = 0; inode < nodebasis; inode++)
			controller.grf.selected.set(inode, false);
		for (int inode = nodebasis; inode < controller.grf.label.size(); inode++)
			controller.grf.selected.set(inode, true);
	}

}
