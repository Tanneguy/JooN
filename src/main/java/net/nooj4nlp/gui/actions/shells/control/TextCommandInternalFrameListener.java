package net.nooj4nlp.gui.actions.shells.control;

import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * InternalFrameListener that adds a TEXT menu to the menu bar each time a text editor is activated, and removes it
 * whenever the editor loses focus
 * 
 */
public class TextCommandInternalFrameListener implements InternalFrameListener
{

	private TextEditorShellController controller;

	private JMenuBar menuBar;

	public TextCommandInternalFrameListener(TextEditorShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
		menuBar = Launcher.getMenuBar();
		controller.getTextShell().getMnText().setVisible(true);
		menuBar.add(controller.getTextShell().getMnText());
		Launcher.mnEdit.setVisible(true);
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
		controller.getTextShell().getMnText().setVisible(false);
		menuBar.repaint();
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