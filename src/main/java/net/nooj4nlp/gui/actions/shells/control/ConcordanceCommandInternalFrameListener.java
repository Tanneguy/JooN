package net.nooj4nlp.gui.actions.shells.control;

import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;

/**
 * 
 * InternalFrameListener that adds a CONCORDANCE menu to the menu bar each time a concordance window is activated, and
 * removes it whenever the concordance window loses focus.
 * 
 */

public class ConcordanceCommandInternalFrameListener implements InternalFrameListener
{
	private JMenuBar menuBar;
	private ConcordanceShellController controller;

	public ConcordanceCommandInternalFrameListener(ConcordanceShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
		menuBar = Launcher.getMenuBar();
		controller.getConcordanceShell().getMnConcordance().setVisible(true);
		menuBar.add(controller.getConcordanceShell().getMnConcordance());
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
		controller.getConcordanceShell().getMnConcordance().setVisible(false);
		menuBar.repaint();

		ConcordanceShell shell = controller.getConcordanceShell();
		controller.setCbMatchesIsPressed(shell.getCbMatches().isSelected());
		controller.setCbOutputsIsPressed(shell.getCbOutputs().isSelected());
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}
}