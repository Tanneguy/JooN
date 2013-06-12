package net.nooj4nlp.controller.LocateDialog;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;

public class CloseInternalFrameListener implements InternalFrameListener
{
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;

	public CloseInternalFrameListener(CorpusEditorShellController corpusController,
			TextEditorShellController textController)
	{
		this.corpusController = corpusController;
		this.textController = textController;
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{

	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0)
	{
		if (corpusController != null && corpusController.getShell() != null)
			corpusController.setLocateDialog(null);

		if (textController != null)
			textController.getTextShell().setLocateDialog(null);
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

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{

	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{

	}

}
