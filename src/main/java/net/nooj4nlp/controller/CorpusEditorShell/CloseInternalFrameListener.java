package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.ConcordanceShell.CustomForegroundTableRenderer;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Utils;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AlphabetDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class CloseInternalFrameListener implements InternalFrameListener
{
	private CorpusEditorShellController controller;
	private JInternalFrame frame;

	public CloseInternalFrameListener(CorpusEditorShellController controller, JInternalFrame frame)
	{
		super();
		this.controller = controller;
		this.frame = frame;
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		// Added because of listener reuse for other dialogs connected to shell
		if (this.frame.getClass().equals(CorpusEditorShell.class))
		{
			this.frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

			if (Launcher.backgroundWorking)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_CLOSE_WINDOW_MESSAGE,
						Constants.NOOJ_PROCESS_RUNNING_CAPTION, JOptionPane.INFORMATION_MESSAGE);

				this.controller.setBeingClosed(false);
				this.frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

				return;
			}

			this.controller.setBeingClosed(true);

			if (this.controller.getCorpus() == null)
				return;

			// Close all related windows
			CorpusEditorShell shell = this.controller.getShell();
			if (shell.getAlphabetDialog() != null)
				shell.getAlphabetDialog().dispose();
			if (shell.getDigramsDialog() != null)
				shell.getDigramsDialog().dispose();
			if (shell.getTokensDialog() != null)
				shell.getTokensDialog().dispose();
			if (shell.getAmbiguitiesDialog() != null)
				shell.getAmbiguitiesDialog().dispose();
			if (shell.getUnAmbiguitiesDialog() != null)
				shell.getUnAmbiguitiesDialog().dispose();

			TextEditorShellController textController = controller.getTextController();
			ConcordanceShellController concordanceController = controller.getConcordanceController();

			if (concordanceController != null)
			{
				concordanceController.getConcordanceShell().dispose();
				if (textController != null)
					textController.setConcordanceController(null);
				concordanceController.setTableModel(null);
				// Changed when removing static variables
				CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceController
						.getConcordanceTable().getDefaultRenderer(Object.class);
				customForegroundTableRenderer.setSortedPreview(false);
				customForegroundTableRenderer.setColoredRowsMap(new HashMap<Integer, Color>());
			}

			if (textController != null)
			{
				TextEditorShell textShell = textController.getTextShell();
				textShell.setAlphabetDialog(null);
				textShell.dispose();
			}

			if (controller.getLocateDialog() != null)
				controller.getLocateDialog().dispose();
			if (shell.getExportXmlDialog() != null)
				shell.getExportXmlDialog().dispose();

			if (this.controller.isModified() && !Launcher.projectMode)
			{
				int answer = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), Constants.SAVE_CORPUS_MESSAGE,
						Constants.SAVE_CORPUS_CAPTION_MESSAGE, JOptionPane.YES_NO_OPTION);

				// On form close, user needs to be asked if he wants to save changes
				if (answer == JOptionPane.YES_OPTION)
				{
					this.controller.saveCorpus();
				}
				else
				{
					// Need to delete the folder because each .jnot might be inconsistent with the global corpus data
					// (e.g. Corpus.annotations might not contain annotations of every text)
					String corpusDirName = this.controller.getFullPath() + Constants.DIRECTORY_SUFFIX;
					File corpusDir = new File(corpusDirName);
					Utils.deleteDir(corpusDir);
				}
			}

			this.controller.desactivateOps();
		}

		// if the characters needs to be closed
		else if (this.frame.getClass().equals(AlphabetDialog.class))
		{
			AlphabetDialog dialog = (AlphabetDialog) frame;

			if (dialog.getCorpusController() != null)
				dialog.getCorpusController().getShell().setAlphabetDialog(null);
			else if (dialog.getTextController() != null)
				dialog.getTextController().getTextShell().setAlphabetDialog(null);

			dialog.dispose();
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