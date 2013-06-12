package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.components.ColoredJButtonUI;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;

/**
 * Class implements tying up concordance with Digrams.
 * 
 */
public class ConcordanceOfTokensDigramsListener implements ActionListener
{
	// controllers
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private boolean isToken;
	private JTable tableTokensDigrams;
	private TokensDigramsDialog actualDialog;

	/**
	 * Constructor.
	 * 
	 * @param corpusController
	 *            - controller of corpus, if such exists
	 * @param textcController
	 *            - controller of a text
	 * @param isToken
	 *            - flag to determine whether concordance was called from tokens or digrams context
	 * @param actualDialog
	 *            - active dialog (Token or Digram)
	 * @param tableTokensDigrams
	 *            - active table (Token or Digram)
	 */
	public ConcordanceOfTokensDigramsListener(CorpusEditorShellController corpusController,
			TextEditorShellController textController, TokensDigramsDialog actualDialog, JTable tableTokensDigrams,
			boolean isToken)
	{
		this.tableTokensDigrams = tableTokensDigrams;
		this.corpusController = corpusController;
		this.textController = textController;
		this.isToken = isToken;
		this.actualDialog = actualDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// // set flag of a renderer, so it could read unsorted map
		// CustomForegroundTableRenderer.setSortedPreview(false);

		// get background color of buttons...if it's a gray button, set color to be a black
		JButton button = (JButton) e.getSource();
		Color currentColor = ((ColoredJButtonUI) button.getUI()).getBackgroundColor();

		if (currentColor.equals(Color.GRAY))
			currentColor = Color.BLACK;

		ConcordanceShell concordanceShell;
		ConcordanceShellController concordanceController;

		// if opened from context of corpus...
		if (corpusController != null && corpusController.getShell() != null)
		{
			// get concordance controller and shell, and open a new if there is no concordance window opened
			concordanceController = corpusController.getConcordanceController();

			if (concordanceController == null)
			{
				concordanceShell = new ConcordanceShell(corpusController, corpusController.getTextController());
				Launcher.getDesktopPane().add(concordanceShell);
				// concordanceShell.setVisible(true);
			}
			else
			{
				concordanceShell = concordanceController.getConcordanceShell();
				// Changed when removing static variables
				// set flag of a renderer, so it could read unsorted map
				concordanceShell.getCustomForegroundTableRenderer().setSortedPreview(false);
			}

			concordanceController = concordanceShell.getController();
			corpusController.setConcordanceController(concordanceController);

			// for each text, locate one text...
			JTable corpusTextsTable = corpusController.getShell().getTableTexts();
			DefaultTableModel tableModel = (DefaultTableModel) corpusTextsTable.getModel();
			for (int i = 0; i < corpusTextsTable.getRowCount(); i++)
			{
				String fileName = tableModel.getValueAt(i, 0) + "." + Constants.JNOT_EXTENSION;

				if (isToken)
				{
					actualDialog.setMyText((Ntext) tableModel.getValueAt(i, 3));
					if (actualDialog.getMyText() == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_TEXT_MESSAGE
								+ fileName, Constants.CANNOT_LOAD_TEXT_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
				{
					actualDialog.setMyText((Ntext) tableModel.getValueAt(i, 3));
					if (actualDialog.getMyText() == null)
						continue;
				}

				actualDialog.locateOneText(fileName, currentColor, concordanceController);
				concordanceShell.setVisible(true);
			}
		}
		// ...if it's opened from single text context
		else
		{
			// get concordance controller and shell, and open a new if there is no concordance window opened
			concordanceController = textController.getConcordanceController();

			if (concordanceController == null)
			{
				concordanceShell = new ConcordanceShell(corpusController, textController);
				Launcher.getDesktopPane().add(concordanceShell);
				// concordanceShell.setVisible(true);
			}
			else
			{
				concordanceShell = concordanceController.getConcordanceShell();
				// Changed when removing static variables
				// set flag of a renderer, so it could read unsorted map
				concordanceShell.getCustomForegroundTableRenderer().setSortedPreview(false);
			}

			concordanceController = concordanceShell.getController();

			// set the text and locate it
			actualDialog.setMyText(textController.getMyText());
			actualDialog.locateOneText("", currentColor, concordanceController);
			concordanceShell.setVisible(true);
		}

		// refresh concordance and unselect all selections in Tokens Digrams Dialog
		concordanceController.refreshConcordance();
		tableTokensDigrams.clearSelection();
	}
}