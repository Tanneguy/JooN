package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AlphabetDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AmbiguitiesUnambiguitiesDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

/**
 * Class for implementation of double click actions of Text Editor window.
 * 
 */
public class ComputeMouseAdapterForText extends MouseAdapter
{
	// text controller
	private TextEditorShellController controller;

	// text editor shell and its components
	private TextEditorShell textShell;
	private Ntext myText;
	private JTextPane textPane;
	private JList list;

	// dialogs
	private AlphabetDialog alphabetDialog;
	private TokensDigramsDialog tokensDialog;
	private TokensDigramsDialog digramsDialog;
	private AmbiguitiesUnambiguitiesDialog ambiguitiesDialog;
	private AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog;

	/**
	 * Constructor.
	 * 
	 * @param textShell
	 *            - shell of an opened text
	 */
	public ComputeMouseAdapterForText(TextEditorShellController controller)
	{
		this.controller = controller;

	}

	public void mouseClicked(MouseEvent e)
	{
		// if double clicked...
		if (e.getClickCount() == 2)
		{
			try
			{
				this.textShell = controller.getTextShell();
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

				// initialize variables...
				myText = controller.getMyText();
				textPane = textShell.getTextPane();
				list = textShell.getListOfResults();

				// if text was edited, get it
				if (controller.isTextWasEdited() || myText.buffer.equals(""))
				{
					myText.buffer = textPane.getText().replace("\r", "");
					controller.setTextWasEdited(false);
					myText.mft = null;
				}

				textPane.setEditable(false);

				// get clicked item and open adequate dialog
				int index = list.locationToIndex(e.getPoint());
				ListModel listModel = list.getModel();
				Object item = listModel.getElementAt(index);
				list.ensureIndexIsVisible(index);

				if (item.equals(Constants.CHARACTERS_LIT))
				{
					alphabetDialog = textShell.getAlphabetDialog();

					if (alphabetDialog == null)
						alphabetDialog = new AlphabetDialog(null, controller);
					
					else
					{
						Helper.putDialogOnTheTop(alphabetDialog);
						return;
					}

					alphabetDialog.fillInTheData(false);

					textShell.setAlphabetDialog(alphabetDialog);
					Launcher.getDesktopPane().add(alphabetDialog);
					alphabetDialog.setVisible(true);
					Helper.putDialogOnTheTop(alphabetDialog);
				}
				else if (item.equals(Constants.TOKENS_LIT))
				{
					if (myText.annotations == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PERFORM_LING_ANAL_FIRST_MESSAGE, Constants.NOOJ_APPLICATION_NAME,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					else
					{
						tokensDialog = textShell.getTokensDialog();

						if (tokensDialog == null)
							tokensDialog = new TokensDigramsDialog(null, controller, true);
						else
						{
							Helper.putDialogOnTheTop(tokensDialog);
							return;
						}

						tokensDialog.fillInTheData();

						textShell.setTokensDialog(tokensDialog);
						Launcher.getDesktopPane().add(tokensDialog);
						tokensDialog.setVisible(true);
						Helper.putDialogOnTheTop(tokensDialog);
					}
				}
				else if (item.equals(Constants.DIGRAMS_LIT))
				{
					// bug fix for not showing digrams dialog if linguistic analysis wasn't done
					if (myText.annotations == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PERFORM_LING_ANAL_FIRST_MESSAGE, Constants.NOOJ_APPLICATION_NAME,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					digramsDialog = textShell.getDigramsDialog();

					if (digramsDialog == null)
						digramsDialog = new TokensDigramsDialog(null, controller, false);
					else
					{
						Helper.putDialogOnTheTop(digramsDialog);
						return;
					}

					digramsDialog.fillInTheData();

					textShell.setDigramsDialog(digramsDialog);
					Launcher.getDesktopPane().add(digramsDialog);
					digramsDialog.setVisible(true);
					Helper.putDialogOnTheTop(digramsDialog);
				}
				// 'AnnotationsDialog' and 'UnknownsDialog' use DictionaryEditorShell for showing data.
				else if (item.equals(Constants.ANNOTATIONS_LIT))
				{
					DictionaryEditorShell annotationsEditor = new DictionaryEditorShell();
					controller.fillInVocabulary(annotationsEditor);

					annotationsEditor.getLblnTus().setText(
							"Vocabulary contains " + DictionaryDialogController.count(annotationsEditor.getTextPane())
									+ " entries.");

					Launcher.getDesktopPane().add(annotationsEditor);
					annotationsEditor.setVisible(true);
					annotationsEditor.getController().modify();
				}
				// 'AnnotationsDialog' and 'UnknownsDialog' use DictionaryEditorShell for showing data.
				else if (item.equals(Constants.UNKNOWNS_LIT))
				{
					DictionaryEditorShell unknownsEditor = new DictionaryEditorShell();
					controller.fillInUnknowns(unknownsEditor);

					unknownsEditor.getLblnTus().setText(
							"Unknowns are " + DictionaryDialogController.count(unknownsEditor.getTextPane())
									+ " entries.");

					Launcher.getDesktopPane().add(unknownsEditor);
					unknownsEditor.setVisible(true);
					unknownsEditor.getController().modify();
				}
				else if (item.equals(Constants.AMBIGUITIES_LIT))
				{
					ambiguitiesDialog = textShell.getAmbiguitiesDialog();

					if (ambiguitiesDialog == null)
						ambiguitiesDialog = new AmbiguitiesUnambiguitiesDialog(null, controller, true);
					else
					{
						Helper.putDialogOnTheTop(ambiguitiesDialog);
						return;
					}

					ambiguitiesDialog.fillInTheData();

					textShell.setAmbiguitiesDialog(ambiguitiesDialog);
					Launcher.getDesktopPane().add(ambiguitiesDialog);
					ambiguitiesDialog.setVisible(true);
					Helper.putDialogOnTheTop(ambiguitiesDialog);
				}
				else if (item.equals(Constants.UNAMBIGUOUS_WORDS_LIT))
				{
					unAmbiguitiesDialog = textShell.getUnAmbiguitiesDialog();

					if (unAmbiguitiesDialog == null)
						unAmbiguitiesDialog = new AmbiguitiesUnambiguitiesDialog(null, controller, false);
					else
					{
						Helper.putDialogOnTheTop(unAmbiguitiesDialog);
						return;
					}

					unAmbiguitiesDialog.fillInTheData();

					textShell.setUnAmbiguitiesDialog(unAmbiguitiesDialog);
					Launcher.getDesktopPane().add(unAmbiguitiesDialog);
					unAmbiguitiesDialog.setVisible(true);
					Helper.putDialogOnTheTop(unAmbiguitiesDialog);
				}
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				JMenu mnText = controller.getTextShell().getMnText();
				mnText.setVisible(false);
			}

			JCheckBox cbTAS = textShell.getChckbxShowTextAnnotation();
			if (cbTAS.isSelected())
			{
				Container container = textShell.getContentPane();
				cbTAS.setSelected(false);
				JSplitPane splitPane = textShell.getSplitPane();
				JScrollPane textScroll = textShell.getScrollPane();
				JScrollPane panelScroll = textShell.getPanelScrollPane();

				textShell.getTasActionListener()
						.start(container, cbTAS, splitPane, textScroll, panelScroll, controller);
			}

			controller.modify();
		}
	}
}