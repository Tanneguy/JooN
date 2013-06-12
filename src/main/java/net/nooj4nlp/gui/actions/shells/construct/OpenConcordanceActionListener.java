package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Class responsible for opening saved concordance.
 */
public class OpenConcordanceActionListener implements ActionListener
{
	private JDesktopPane desktopPane;

	public OpenConcordanceActionListener(JDesktopPane dp)
	{
		this.desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JInternalFrame selectedFrame = this.desktopPane.getSelectedFrame();
		if (selectedFrame == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SELECT_FOR_CONCORDANCE_MESSAGE,
					Constants.SELECT_FOR_CONCORDANCE_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		ConcordanceShell concordanceShell = null;

		String cls = selectedFrame.getClass().getSimpleName();
		if (cls.equals("TextEditorShell") || cls.equals("CorpusEditorShell"))
		{
			if (Launcher.getOpenConcordanceChooser().showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;

			if (cls.equals("TextEditorShell"))
			{
				TextEditorShell textEditorShell = (TextEditorShell) selectedFrame;
				TextEditorShellController textController = textEditorShell.getTextController();

				concordanceShell = new ConcordanceShell(null, textController);

				ConcordanceShellController concordanceController = new ConcordanceShellController(concordanceShell);
				textController.setConcordanceController(concordanceController);

				concordanceController.loadConcordance(Launcher.getOpenConcordanceChooser().getSelectedFile()
						.getAbsolutePath());
			}
			else if (cls.equals("CorpusEditorShell"))
			{
				CorpusEditorShell corpusEditorShell = (CorpusEditorShell) selectedFrame;
				CorpusEditorShellController corpusController = corpusEditorShell.getController();

				concordanceShell = new ConcordanceShell(corpusController, null);

				ConcordanceShellController concordanceController = new ConcordanceShellController(concordanceShell);
				corpusController.setConcordanceController(concordanceController);

				concordanceController.loadConcordance(Launcher.getOpenConcordanceChooser().getSelectedFile()
						.getAbsolutePath());
			}
		}
		else
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SELECT_TEXT_OR_CORPUS_MESSAGE,
					Constants.SELECT_TEXT_OR_CORPUS_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}
