package net.nooj4nlp.gui.dialogs.OpenCorpusDialog;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Class implements custom closing events of AmbiguitiesUnambiguitiesDialog.
 * 
 */

public class AmbigUnambigOnCloseEvents implements InternalFrameListener
{
	private InversiveSortActionListener inversiveListener;
	private AmbiguitiesUnambiguitiesDialog ambigUnambigDialog;

	/**
	 * Constructor.
	 * 
	 * @param ambigUnambigDialog
	 *            - dialog to be closed
	 * @param inversiveSort
	 *            - listener of dialog's sorted table
	 */

	public AmbigUnambigOnCloseEvents(AmbiguitiesUnambiguitiesDialog ambigUnambigDialog,
			InversiveSortActionListener inversiveSort)
	{
		this.ambigUnambigDialog = ambigUnambigDialog;
		this.inversiveListener = inversiveSort;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		inversiveListener.setAmbigUnambigDialog(null);
		TextEditorShellController textController = ambigUnambigDialog.getTextController();
		if (textController != null)
		{
			TextEditorShell textShell = textController.getTextShell();
			if (textShell != null)
			{
				if (ambigUnambigDialog.isAreAmbiguities())
					textShell.setAmbiguitiesDialog(null);
				else
					textShell.setUnAmbiguitiesDialog(null);
			}
		}

		CorpusEditorShellController corpusController = ambigUnambigDialog.getCorpusController();
		if (corpusController != null && corpusController.getShell() != null)
		{
			CorpusEditorShell shell = corpusController.getShell();
			if (shell != null)
			{
				if (ambigUnambigDialog.isAreAmbiguities())
					shell.setAmbiguitiesDialog(null);
				else
					shell.setUnAmbiguitiesDialog(null);
			}
		}
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
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