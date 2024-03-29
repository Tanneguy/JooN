package net.nooj4nlp.controller.LocateDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

public class LocateDialogTextActionListener implements ActionListener
{
	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;
	private LocateDialog locateShell;

	/**
	 * @wbp.parser.entryPoint
	 */
	public LocateDialogTextActionListener(TextEditorShellController textController,
			CorpusEditorShellController corpusController)
	{
		super();
		this.textController = textController;
		this.corpusController = corpusController;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		LocateDialog locateDialog;

		if (corpusController == null)
		{
			TextEditorShell textShell = textController.getTextShell();
			locateDialog = textShell.getLocateDialog();

			if (locateDialog != null)
			{
				Helper.putDialogOnTheTop(locateDialog);
				return;
			}

			locateShell = new LocateDialog(this.textController, null, false);
			textShell.setLocateDialog(locateShell);
		}
		else
		{
			locateDialog = corpusController.getLocateDialog();

			if (locateDialog != null)
			{
				Helper.putDialogOnTheTop(locateDialog);
				return;
			}

			locateShell = new LocateDialog(null, this.corpusController, true);
			corpusController.setLocateDialog(locateShell);
		}
		
		Launcher.getDesktopPane().add(locateShell);
		locateShell.setVisible(true);
	}
}