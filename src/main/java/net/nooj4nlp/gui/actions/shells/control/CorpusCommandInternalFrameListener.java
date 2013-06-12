package net.nooj4nlp.gui.actions.shells.control;

import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * InternalFrameListener that adds a CORPUS menu to the menu bar each time a text editor is activated, and removes it
 * whenever the editor loses focus
 * 
 */
public class CorpusCommandInternalFrameListener implements InternalFrameListener
{
	private JMenuBar menuBar;
	private CorpusEditorShellController corpusController;

	public CorpusCommandInternalFrameListener(CorpusEditorShellController corpusController)
	{
		super();
		this.corpusController = corpusController;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
		menuBar = Launcher.getMenuBar();
		corpusController.getShell().getMnCorpus().setVisible(true);
		menuBar.add(corpusController.getShell().getMnCorpus());
		Launcher.mnEdit.setVisible(true);
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
		corpusController.getShell().getMnCorpus().setVisible(false);
		menuBar.repaint();
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}
}