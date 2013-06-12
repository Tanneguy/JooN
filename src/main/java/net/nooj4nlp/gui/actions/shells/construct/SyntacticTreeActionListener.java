package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.SyntacticTreeShell;

public class SyntacticTreeActionListener implements ActionListener
{

	public SyntacticTreeActionListener(ConcordanceShellController controller)
	{
		this.controller = controller;
	}

	private ConcordanceShellController controller;

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		SyntacticTreeShell shell = new SyntacticTreeShell(controller);
		controller.setSyntacticTreeShell(shell);

		Launcher.getDesktopPane().add(shell);
		shell.setVisible(true);
	}
}