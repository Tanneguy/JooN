package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.StatsShell.StatsShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.StatsShell;

/**
 * Class implements opening Statistics window.
 */

public class OpenStatsActionListener implements ActionListener
{
	// controllers
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private ConcordanceShellController concordanceController;

	/**
	 * Constructor.
	 * 
	 * @param corpusController
	 *            - corpus controller from Concordance
	 * @param textController
	 *            - text controller from Concordance
	 * @param concordanceController
	 *            - controller of Concordance
	 */
	public OpenStatsActionListener(CorpusEditorShellController corpusController,
			TextEditorShellController textController, ConcordanceShellController concordanceController)
	{
		super();

		this.corpusController = corpusController;
		this.textController = textController;
		this.concordanceController = concordanceController;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// open Statistics window
		StatsShell statsShell = new StatsShell();
		Launcher.getDesktopPane().add(statsShell);
		statsShell.setVisible(true);

		// set adequate title
		if (corpusController != null && corpusController.getShell() != null)
			statsShell.setTitle("Statistical Analysis of " + corpusController.getFullName());
		else if (textController != null)
			statsShell.setTitle("Statistical Analysis of " + textController.getTextName());

		// create controller and tie it up with Statistics window
		StatsShellController statsController = new StatsShellController(corpusController, textController,
				concordanceController, statsShell);

		statsShell.setStatsController(statsController);
	}
}