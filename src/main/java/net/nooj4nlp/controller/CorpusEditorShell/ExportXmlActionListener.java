package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.ExportXmlDialog;
import net.nooj4nlp.gui.main.Launcher;

public class ExportXmlActionListener implements ActionListener
{
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private boolean isCorpus;

	private ExportXmlDialog exportXmlDialog;

	public ExportXmlActionListener(CorpusEditorShellController corpusController)
	{
		super();
		this.corpusController = corpusController;
		this.textController = null;
		this.isCorpus = true;
	}

	public ExportXmlActionListener(TextEditorShellController textController)
	{
		super();
		this.textController = textController;
		this.corpusController = null;
		this.isCorpus = false;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		exportXmlDialog = new ExportXmlDialog(corpusController, textController, isCorpus);
		if (isCorpus)
		{
			corpusController.getShell().setExportXmlDialog(exportXmlDialog);
		}
		else
		{
			if (textController.isModified())
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIRST_SAVE_TEXT_MESSAGE,
						Constants.FIRST_SAVE_TEXT_CAPTION, JOptionPane.ERROR_MESSAGE);
				return;
			}

			textController.getTextShell().setExportXmlDialog(exportXmlDialog);
		}
		Launcher.getDesktopPane().add(exportXmlDialog);
		exportXmlDialog.setVisible(true);
	}

}
