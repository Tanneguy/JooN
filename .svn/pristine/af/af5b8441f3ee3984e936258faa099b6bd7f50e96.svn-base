package net.nooj4nlp.gui.dialogs.OpenCorpusDialog;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Class implements custom closing events of TokensDigramsDialog.
 * 
 */
public class TokensDigramsOnCloseEvents implements InternalFrameListener
{
	private InversiveSortActionListener inversiveListener;
	private TokensDigramsDialog tokensDigramsDialog;

	/**
	 * Constructor.
	 * 
	 * @param tokensDigramsDialog
	 *            - dialog to be closed
	 * @param inversiveListener
	 *            - listener of dialog's sorted table
	 */

	public TokensDigramsOnCloseEvents(TokensDigramsDialog tokensDigramsDialog,
			InversiveSortActionListener inversiveListener)
	{
		this.inversiveListener = inversiveListener;
		this.tokensDigramsDialog = tokensDigramsDialog;
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
		inversiveListener.setTokensDigramsDialog(null);

		TextEditorShellController textController = tokensDigramsDialog.getTextController();
		if (textController != null)
		{
			TextEditorShell textShell = textController.getTextShell();
			if (textShell != null)
			{
				if (tokensDigramsDialog.isToken())
					textShell.setTokensDialog(null);
				else
					textShell.setDigramsDialog(null);
			}
		}

		CorpusEditorShellController corpusController = tokensDigramsDialog.getController();
		if (corpusController != null && corpusController.getShell() != null)
		{
			CorpusEditorShell shell = corpusController.getShell();
			if (shell != null)
			{
				if (tokensDigramsDialog.isToken())
					shell.setTokensDialog(null);
				else
					shell.setDigramsDialog(null);
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