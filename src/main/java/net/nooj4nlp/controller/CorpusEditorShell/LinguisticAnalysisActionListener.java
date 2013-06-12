package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.main.Launcher;

public class LinguisticAnalysisActionListener implements ActionListener, PropertyChangeListener
{
	private CorpusEditorShellController controller;

	public LinguisticAnalysisActionListener(CorpusEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			Launcher.getStatusBar().getProgressBar().setIndeterminate(false);
			Launcher.getStatusBar().getProgressBar().setValue(progress);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		// desactivate all formText operations
		ConcordanceShellController concordanceController = controller.getConcordanceController();
		if (concordanceController != null && concordanceController.getConcordanceShell().isVisible())
			concordanceController.getConcordanceShell().hide();

		controller.desactivateOps();
		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.progressMessage = "Linguistic Analysis...";
		Launcher.getStatusBar().getProgressLabel().setText("Linguistic Analysis...");

		if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;

			Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.CORPUS_LING_ANALYSIS, null, controller,
					null);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
		else
		{
			// mono-thread
			controller.linguisticAnalysis();
			controller.reactivateOps();
			controller.updateTextPaneStats();
			controller.updateResults();

			Date now = new Date();
			long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
			Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
			
		}
	}
}
