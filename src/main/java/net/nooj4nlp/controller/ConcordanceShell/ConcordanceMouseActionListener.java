package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.OpenTextFromCorpusActionListener;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.SyntacticTreeShell;
import net.nooj4nlp.gui.shells.TextEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

/**
 * Class for implementation of Double click event on Concordance's JTable.
 * 
 */
public class ConcordanceMouseActionListener extends MouseAdapter
{
	// controllers
	private ConcordanceShellController controller;
	private TextEditorShellController textController;

	// components
	private JTable concordanceTable;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 */
	public ConcordanceMouseActionListener(ConcordanceShellController controller)
	{
		this.controller = controller;
		this.concordanceTable = this.controller.getConcordanceTable();
		this.textController = this.controller.getTextController();
	}

	public void mouseClicked(MouseEvent e)
	{
		// if double clicked...
		if (e.getClickCount() == 2)
		{
			// if deselected in meanwhile, do nothing
			if (concordanceTable.getSelectedRowCount() < 1)
				return;

			List<Object> theItems = controller.getTheItems();

			// get double clicked row and items
			int selectedRow = concordanceTable.getSelectedRow();
			Object[] item = new Object[6];

			// get the active model, color and annotation of clicked row...
			DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();
			Object fifth = tableModel.getValueAt(selectedRow, 4);
			Object sixth = tableModel.getValueAt(selectedRow, 5);

			// ...when the matched row is found in The Items, set the actual item
			for (int i = 0; i < theItems.size(); i += 4)
			{
				Object[] tempItem = (Object[]) theItems.get(i + 1);

				if (controller.getColorMap().get(tempItem[4]) == (Integer) fifth)
				{
					if (sixth.equals(tempItem[5]))
					{
						item = tempItem;
						break;
					}
				}

			}

			ArrayList<?> annotations = (ArrayList<?>) item[5];

			CorpusEditorShellController corpusController = controller.getCorpusController();

			if (corpusController != null && corpusController.getShell() != null)
			{
				Corpus corpus = corpusController.getCorpus();

				// find text
				String fileName = item[0].toString();
				String fullPath = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX
						+ System.getProperty("file.separator") + fileName;
				Ntext myText = null;

				TextEditorShellController textController = corpusController.getTextController();
				String clickedFilePath = "";
				if (textController != null)
				{
					clickedFilePath = OpenTextFromCorpusActionListener.getClickedFilePath();
					if (fullPath.equals(clickedFilePath))
						myText = textController.getMyText();
					else
					{
						try
						{
							myText = Ntext.loadForCorpus(fullPath, corpus.lan, corpus.multiplier);
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					textController.getTextShell().dispose();
					textController = null;
				}
				else
				{
					try
					{
						myText = Ntext.loadForCorpus(fullPath, corpus.lan, corpus.multiplier);
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				TextEditorShell textShell = new TextEditorShell(corpusController, myText, fileName,
						myText.getDelimPattern(), false);
				corpusController.getShell().setTextEditorShell(textShell);
				textController = textShell.getTextController();

				MouseListener rightClickListener = textShell.getRightClickListener();
				textShell.removeMouseListener(rightClickListener);
				textShell.getTextPane().removeMouseListener(rightClickListener);

				Launcher.getDesktopPane().add(textShell);
				textShell.setVisible(true);

				File file = new File(fullPath);
				textController.setFileToBeOpenedOrImported(file);
				corpusController.setTextController(textController);

				myText.annotations = corpus.annotations;

				// compute beg and end addresses
				// absolute end address is unnecessary in our implementation, and so is removed!
				double absoluteBeginAddress0 = (Double) annotations.get(2);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;

				textController.resetShellText();
				textController.setCurrentTextUnitIsBlack(true);
				// get beginning of TU and put it to spinner
				textController.getTextShell().getTextPane().setCaretPosition(absoluteBeginAddress);
			}

			else
			{
				// exit if there is no annotation!
				if (annotations == null)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EMPTY_ANNOTATIONS,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				// absolute end address is unnecessary in our implementation, and so is removed!
				double absoluteBeginAddress0 = (Double) annotations.get(2);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;

				// get beginning of TU and put it to spinner
				textController.getTextShell().getTextPane().setCaretPosition(absoluteBeginAddress);

				TextEditorShell textShell = textController.getTextShell();
				Helper.putDialogOnTheTop(textShell);
			}

			if (controller.getSyntacticTreeShell() != null)
			{
				SyntacticTreeShell syntacticTree = controller.getSyntacticTreeShell();
				syntacticTree.getController().setConcordanceIndex(
						controller.getConcordanceShell().getConcordanceTable().getSelectedRow());
				syntacticTree.setVisible(true);
				syntacticTree.invalidate();
				syntacticTree.validate();
				syntacticTree.repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		Component component = e.getComponent();
		CursorChangeEffect.setCrossCursor(component);
	}
}
