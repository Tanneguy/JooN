package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AlphabetDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AmbiguitiesUnambiguitiesDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

public class ComputeMouseAdapter extends MouseAdapter
{
	private CorpusEditorShellController controller;
	private JList list;

	private AlphabetDialog alphabetDialog;
	private TokensDigramsDialog tokensDialog;
	private TokensDigramsDialog digramsDialog;
	private AmbiguitiesUnambiguitiesDialog ambiguitiesDialog;
	private AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog;

	public ComputeMouseAdapter(CorpusEditorShellController controller, JList list)
	{
		this.controller = controller;
		this.list = list;
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

				int index = list.locationToIndex(e.getPoint());
				ListModel listModel = list.getModel();
				Object item = listModel.getElementAt(index);
				list.ensureIndexIsVisible(index);

				if (item.equals(Constants.CHARACTERS_LIT))
				{
					alphabetDialog = controller.getShell().getAlphabetDialog();

					if (alphabetDialog == null)
						alphabetDialog = new AlphabetDialog(controller, null);
					
					else
					{
						Helper.putDialogOnTheTop(alphabetDialog);
						return;
					}

					alphabetDialog.fillInTheData(true);

					controller.getShell().setAlphabetDialog(alphabetDialog);
					Launcher.getDesktopPane().add(alphabetDialog);
					alphabetDialog.setVisible(true);
					Helper.putDialogOnTheTop(alphabetDialog);
				}
				else if (item.equals(Constants.TOKENS_LIT))
				{
					if (controller.getCorpus().annotations == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PERFORM_LING_ANAL_FIRST_MESSAGE, Constants.NOOJ_APPLICATION_NAME,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					else
					{
						tokensDialog = controller.getShell().getTokensDialog();

						if (tokensDialog == null)
							tokensDialog = new TokensDigramsDialog(controller, null, true);
						else
						{
							Helper.putDialogOnTheTop(tokensDialog);
							return;
						}

						tokensDialog.fillInTheData();

						controller.getShell().setTokensDialog(tokensDialog);
						Launcher.getDesktopPane().add(tokensDialog);
						tokensDialog.setVisible(true);
						Helper.putDialogOnTheTop(tokensDialog);
					}
				}
				else if (item.equals(Constants.DIGRAMS_LIT))
				{
					digramsDialog = controller.getShell().getDigramsDialog();
					if (digramsDialog == null)
						digramsDialog = new TokensDigramsDialog(controller, null, false);
					else
					{
						Helper.putDialogOnTheTop(digramsDialog);
						return;
					}

					digramsDialog.fillInTheData();

					controller.getShell().setDigramsDialog(digramsDialog);
					Launcher.getDesktopPane().add(digramsDialog);
					digramsDialog.setVisible(true);
					Helper.putDialogOnTheTop(digramsDialog);
				}
				// 'AnnotationsDialog' and 'UnknownsDialog' use DictionaryEditorShell for showing data.
				else if (item.equals(Constants.ANNOTATIONS_LIT))
				{
					DictionaryEditorShell annotationsEditor = new DictionaryEditorShell();
					controller.fillInVocabulary(annotationsEditor);
					Launcher.getDesktopPane().add(annotationsEditor);
					annotationsEditor.setVisible(true);

					annotationsEditor.getController().modify();
				}
				// 'AnnotationsDialog' and 'UnknownsDialog' use DictionaryEditorShell for showing data.
				else if (item.equals(Constants.UNKNOWNS_LIT))
				{
					DictionaryEditorShell unknownsEditor = new DictionaryEditorShell();
					controller.fillInUnknowns(unknownsEditor);
					Launcher.getDesktopPane().add(unknownsEditor);
					unknownsEditor.setVisible(true);

					unknownsEditor.getController().modify();
				}
				else if (item.equals(Constants.AMBIGUITIES_LIT))
				{
					ambiguitiesDialog = controller.getShell().getAmbiguitiesDialog();
					if (ambiguitiesDialog == null)
						ambiguitiesDialog = new AmbiguitiesUnambiguitiesDialog(controller, null, true);
					else
					{
						Helper.putDialogOnTheTop(ambiguitiesDialog);
						return;
					}

					ambiguitiesDialog.fillInTheData();
					controller.getShell().setAmbiguitiesDialog(ambiguitiesDialog);
					Launcher.getDesktopPane().add(ambiguitiesDialog);
					ambiguitiesDialog.setVisible(true);
					Helper.putDialogOnTheTop(ambiguitiesDialog);
				}
				else if (item.equals(Constants.UNAMBIGUOUS_WORDS_LIT))
				{
					unAmbiguitiesDialog = controller.getShell().getUnAmbiguitiesDialog();
					if (unAmbiguitiesDialog == null)
						unAmbiguitiesDialog = new AmbiguitiesUnambiguitiesDialog(controller, null, false);
					else
					{
						Helper.putDialogOnTheTop(unAmbiguitiesDialog);
						return;
					}

					unAmbiguitiesDialog.fillInTheData();
					controller.getShell().setUnAmbiguitiesDialog(unAmbiguitiesDialog);
					Launcher.getDesktopPane().add(unAmbiguitiesDialog);
					unAmbiguitiesDialog.setVisible(true);
					Helper.putDialogOnTheTop(unAmbiguitiesDialog);
				}
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}
	}
}