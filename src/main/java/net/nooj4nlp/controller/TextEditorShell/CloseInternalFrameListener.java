package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.ConcordanceShell.CustomForegroundTableRenderer;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class CloseInternalFrameListener implements InternalFrameListener
{
	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;
	private JInternalFrame frame;

	public CloseInternalFrameListener(TextEditorShellController textController, JInternalFrame frame,
			CorpusEditorShellController corpusController)
	{
		super();
		this.textController = textController;
		this.corpusController = corpusController;
		this.frame = frame;
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		this.frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_CLOSE_WINDOW_MESSAGE,
					Constants.NOOJ_PROCESS_RUNNING_CAPTION, JOptionPane.INFORMATION_MESSAGE);

			this.frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

			return;
		}

		if (corpusController != null && corpusController.getShell() != null)
		{
			CorpusEditorShell corpusEditorShell = corpusController.getShell();
			if (corpusEditorShell != null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CLOSE_CORPUS_MESSAGE,
						Constants.CLOSE_CORPUS_CAPTION, JOptionPane.WARNING_MESSAGE);
				return;
			}
			// Close all related forms
			corpusController.setBeingClosed(true);
		}

		TextEditorShell textEditorShell = textController.getTextShell();
		ConcordanceShellController concordanceController = textController.getConcordanceController();

		if (textEditorShell.getAlphabetDialog() != null)
			textEditorShell.getAlphabetDialog().dispose();
		if (textEditorShell.getTokensDialog() != null)
			textEditorShell.getTokensDialog().dispose();
		if (textEditorShell.getDigramsDialog() != null)
			textEditorShell.getDigramsDialog().dispose();
		if (textEditorShell.getAmbiguitiesDialog() != null)
			textEditorShell.getAmbiguitiesDialog().dispose();
		if (textEditorShell.getUnAmbiguitiesDialog() != null)
			textEditorShell.getUnAmbiguitiesDialog().dispose();
		if (textEditorShell.getFindReplaceDialog() != null)
			textEditorShell.getFindReplaceDialog().dispose();

		if (concordanceController != null)
		{
			concordanceController.getConcordanceShell().dispose();
			textController.setConcordanceController(null);
			concordanceController.setTableModel(null);

			CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceController
					.getConcordanceTable().getDefaultRenderer(Object.class);
			customForegroundTableRenderer.setSortedPreview(false);
			customForegroundTableRenderer.setColoredRowsMap(new HashMap<Integer, Color>());
		}

		if (textEditorShell.getLocateDialog() != null)
			textEditorShell.getLocateDialog().dispose();
		if (textEditorShell.getExportXmlDialog() != null)
			textEditorShell.getExportXmlDialog().dispose();

		if (textController.isModified() && !Launcher.projectMode)
		{
			int answer = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), Constants.SAVE_TEXT_MESSAGE,
					Constants.SAVE_TEXT_CAPTION_MESSAGE, JOptionPane.YES_NO_OPTION);

			// On form close, user needs to be asked if he wants to save changes
			if (answer == JOptionPane.YES_OPTION)
			{
				try
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
					textController.saveText();
				}

				finally
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				}
			}
		}
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
