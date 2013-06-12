package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class SelectAllActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public SelectAllActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// select all nodes
		for (int inode = 0; inode < controller.grf.label.size(); inode++)
			controller.grf.selected.set(inode, true);
		editor.invalidate();
		editor.repaint();
	}

}
