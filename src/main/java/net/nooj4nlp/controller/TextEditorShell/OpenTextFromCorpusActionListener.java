package net.nooj4nlp.controller.TextEditorShell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Class for implementation open text from corpus context action.
 */
public class OpenTextFromCorpusActionListener extends MouseAdapter
{
	private JTable table;
	private CorpusEditorShellController controller;
	private Corpus corpus;
	private static String clickedFilePath;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - corpus controller from which text was selected for opening
	 * @param table
	 *            - table of corpus files
	 */
	public OpenTextFromCorpusActionListener(CorpusEditorShellController controller, JTable table)
	{
		this.controller = controller;
		this.table = table;
		this.corpus = this.controller.getCorpus();
	}

	public void mouseClicked(MouseEvent e)
	{
		// if double clicked...
		if (e.getClickCount() == 2)
		{
			// get the file and its full path
			int index = table.getSelectedRow();
			index = controller.getTableTexts().convertRowIndexToModel(index);
			TableModel model = table.getModel();
			String fileName = ((DefaultTableModel) model).getValueAt(index, 0).toString() + "."
					+ Constants.JNOT_EXTENSION;
			String corpusParentPath = (new File(controller.getFullPath())).getParent();
			clickedFilePath = corpusParentPath + System.getProperty("file.separator") + controller.getFullName()
					+ Constants.DIRECTORY_SUFFIX + System.getProperty("file.separator") + fileName;

			// fill Ntext
			Ntext myText = null;
			try
			{
				myText = Ntext.loadForCorpus(clickedFilePath, corpus.lan, corpus.multiplier);
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (myText == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_TEXT_MESSAGE + fileName,
						Constants.CANNOT_LOAD_TEXT_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
				myText.annotations = corpus.annotations;

				// delimit text
				boolean modifyAndUpdate = false;
				if (myText.mft == null)
				{
					modifyAndUpdate = true;
					String errorMessage = controller.getEngine().delimitTextUnits(myText);

					if (!errorMessage.equals(""))
					{
						Dic.writeLog(errorMessage);
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.NOOJ_ERROR,
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				TextEditorShell textShell = null;

				// if there is no text opened from corpus context, open a new one...
				if (controller.getShell().getTextEditorShell() == null)
				{
					textShell = new TextEditorShell(controller, myText, fileName, myText.getDelimPattern(), false);
					MouseListener rightClickListener = textShell.getRightClickListener();
					textShell.removeMouseListener(rightClickListener);
					textShell.getTextPane().removeMouseListener(rightClickListener);
					controller.getShell().setTextEditorShell(textShell);
					textShell.getTextController().setFileToBeOpenedOrImported(new File(clickedFilePath));
					controller.setTextController(textShell.getTextController());
				}
				// ...else dispose of the old one, and open new one, with reseting the old flags
				else
				{
					controller.getTextShell().dispose();
					textShell = new TextEditorShell(controller, myText, fileName, myText.getDelimPattern(), false);
					MouseListener rightClickListener = textShell.getRightClickListener();
					textShell.getTextPane().removeMouseListener(rightClickListener);
					textShell.removeMouseListener(rightClickListener);
					textShell.getTextController().setFileToBeOpenedOrImported(new File(clickedFilePath));
					textShell.getTextController().resetShellText();
					controller.setTextController(textShell.getTextController());
				}

				// set number of text units to responsible label
				textShell.getLblnTus().setText("/ " + textShell.getText().nbOfTextUnits + " TUs");

				if (modifyAndUpdate)
				{
					textShell.getTextController().modify();
					textShell.getTextController().updateTextPaneStats();
				}

				// to ensure that there won't be conflicts, unit selection listener is temporarily taken off
				UnitSelectionListener unitSelectionListener = textShell.getUnitSelectionListener();
				JTextPane textPane = textShell.getTextPane();
				textPane.removeCaretListener(unitSelectionListener);
				textShell.getTextController().rtbTextUpdate(true);
				textPane.addCaretListener(unitSelectionListener);

				Launcher.getDesktopPane().add(textShell);
				textShell.setVisible(true);

				textShell.getUnitSelectionListener().paintTextInRGB();
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}
	}

	public static String getClickedFilePath()
	{
		return clickedFilePath;
	}
}
