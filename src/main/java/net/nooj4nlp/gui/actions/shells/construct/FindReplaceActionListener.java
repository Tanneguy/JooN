package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.controller.FlexDescEditorShell.FlexDescEditorShellController;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.PropDefEditorShell.PropDefEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.DictionaryDialog;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

/**
 * 
 * ActionListener that opens the Find/Replace dialog.
 * 
 */
public class FindReplaceActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	/**
	 * Constructor.
	 * 
	 * @param dp
	 *            - background desktop pane -
	 */

	public FindReplaceActionListener(JDesktopPane dp)
	{
		desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// get selected frame
		JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

		// if no window has been opened
		if (selectedFrame == null)
		{
			JOptionPane.showMessageDialog(desktopPane, "Please first open a text or a dictionary",
					"NooJ: Nowhere to look in", 2);
			return;
		}

		/*
		 * Get class of opened window and if it's desired, open Find/Replace dialog or refresh its data, if it's already
		 * opened
		 */
		String title = "";
		String cls = selectedFrame.getClass().getSimpleName();

		if (cls.equals("TextEditorShell"))
		{
			TextEditorShell textShell = (TextEditorShell) selectedFrame;
			File activeTextFile = textShell.getTextController().getFileToBeOpenedOrImported();
			title = "Find/Replace in Text " + (activeTextFile == null ? "" : activeTextFile.getName());

			FindReplaceDialog findReplaceDialog = textShell.getFindReplaceDialog();

			if (findReplaceDialog == null)
				textShell.setFindReplaceDialog(openFindReplaceDialog(selectedFrame, title, 0));
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(textShell.getTextPane());
			}
		}

		else if (cls.equals("DictionaryEditorShell"))
		{
			DictionaryEditorShell dictionaryShell = (DictionaryEditorShell) selectedFrame;
			DictionaryEditorShellController dictionaryController = dictionaryShell.getController();

			File activeTextFile = new File(dictionaryController.getFullName());
			title = "Find/Replace/Extract/Count in "
					+ (activeTextFile == null ? "Dictionary" : activeTextFile.getName());

			FindReplaceDialog findReplaceDialog = dictionaryController.getFindReplaceDialog();

			if (findReplaceDialog == null)
				dictionaryController.setFindReplaceDialog(openFindReplaceDialog(selectedFrame, title, 0));
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(dictionaryShell.getTextPane());
			}
		}

		else if (cls.equals("FlexDescEditorShell"))
		{
			FlexDescEditorShell flexDescShell = (FlexDescEditorShell) selectedFrame;
			FlexDescEditorShellController flexDescController = flexDescShell.getController();

			String pathOfFile = flexDescController.getFullName();

			if (pathOfFile == null)
				pathOfFile = "";

			File activeTextFile = new File(pathOfFile);
			title = Constants.FIND_REPLACE_TITLE
					+ (!activeTextFile.exists() ? "Inflectional Description" : activeTextFile.getName());

			FindReplaceDialog findReplaceDialog = flexDescController.getFindReplaceDialog();

			if (findReplaceDialog == null)
				flexDescController.setFindReplaceDialog(openFindReplaceDialog(selectedFrame, title, 0));
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(flexDescShell.getTextPane());
			}
		}

		else if (cls.equals("PropDefEditorShell"))
		{
			PropDefEditorShell propDefShell = (PropDefEditorShell) selectedFrame;
			PropDefEditorShellController propDefController = propDefShell.getController();

			File activeTextFile = new File(propDefController.getFullName());
			title = Constants.FIND_REPLACE_TITLE
					+ (activeTextFile == null ? "Properties' definition" : activeTextFile.getName());

			FindReplaceDialog findReplaceDialog = propDefController.getFindReplaceDialog();

			if (findReplaceDialog == null)
				propDefController.setFindReplaceDialog(openFindReplaceDialog(selectedFrame, title, 0));
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(propDefShell.getTextPane());
			}
		}

		else if (cls.equals("GrammarEditorShell"))
		{
			GrammarEditorShell grammarShell = (GrammarEditorShell) selectedFrame;
			GrammarEditorShellController grammarController = grammarShell.getController();

			File activeTextFile = new File(grammarController.getFullName());
			title = Constants.FIND_REPLACE_TITLE + (activeTextFile == null ? "Grammar" : activeTextFile.getName());

			FindReplaceDialog findReplaceDialog = grammarController.getFindReplaceDialog();

			if (findReplaceDialog == null)
				grammarController.setFindReplaceDialog(openFindReplaceDialog(selectedFrame, title, 0));
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(null);
			}
		}

		else if (cls.equals("DictionaryDialog"))
		{
			DictionaryDialog dictionaryDialog = (DictionaryDialog) selectedFrame;
			DictionaryDialogController dicDialogControlller = dictionaryDialog.getController();

			title = "Replace in Lab's Dictionary";

			String buffer = DictionaryDialogController.getDictionaryContent(dicDialogControlller.getTxtDictionaryName()
					.getText());
			if (buffer == null)
				return;

			FindReplaceDialog findReplaceDialog = dicDialogControlller.getFindReplaceDialog();

			if (findReplaceDialog == null)
			{
				int context = 0;
				if (e.getActionCommand().equals("LabDicoReplace"))
					context = 1;
				else if (e.getActionCommand().equals("LabDicoExtract"))
					context = 2;

				findReplaceDialog = openFindReplaceDialog(selectedFrame, title, context);
				dicDialogControlller.setFindReplaceDialog(findReplaceDialog);
			}
			else
			{
				Helper.putDialogOnTheTop(findReplaceDialog);

				findReplaceDialog.setTitle(title);
				findReplaceDialog.setRtb(null);
			}

			findReplaceDialog.getListener().setBuffer(buffer);

		}
		else
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.FIND_REPLACE_BEFORE_OPENING_FIND_REPLACE,
					Constants.FIND_REPLACE_BEFORE_OPENING_FIND_REPLACE_TITLE, JOptionPane.WARNING_MESSAGE);

	}

	private FindReplaceDialog openFindReplaceDialog(JInternalFrame frame, String title, int labDicoContext)
	{
		FindReplaceDialog findReplace = new FindReplaceDialog(frame, title, labDicoContext);
		desktopPane.add(findReplace);
		findReplace.setVisible(true);
		return findReplace;
	}
}