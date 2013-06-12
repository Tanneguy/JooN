package net.nooj4nlp.gui.actions.shells.control;

import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * InternalFrameListener that adds a DICTIONARY menu to the menu bar each time a dictionary editor is activated, and
 * removes it whenever the editor loses focus
 * 
 */
public class DictionaryCommandInternalFrameListener implements InternalFrameListener
{

	private JMenuBar menuBar;

	private DictionaryEditorShellController controller;

	public DictionaryCommandInternalFrameListener(DictionaryEditorShellController conroller)
	{
		this.controller = conroller;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
		
		menuBar = Launcher.getMenuBar();
		controller.getShell().getMnDictionary().setVisible(true);
		menuBar.add(controller.getShell().getMnDictionary());
		Launcher.mnEdit.setVisible(true);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		controller.close();
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
		

		controller.getShell().getMnDictionary().setVisible(false);
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
