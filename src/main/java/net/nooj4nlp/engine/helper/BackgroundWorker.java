/*
 * This file is part of Nooj. Copyright (C) 2012 Silberztein Max
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.nooj4nlp.engine.helper;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dictionary;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * stub class for C# BackgroundWorker
 * 
 * @author Silberztein Max
 * 
 */
public class BackgroundWorker extends SwingWorker<Void, Void>
{
	public static final String CORPUS_ALPHABETISATION = "corpus alphabetisation";
	private static final String CORPUS_TOKENIZATION = "corpus tokenization";
	public static final String CORPUS_AMBIGUITIES = "corpus ambiguities";
	public static final String CORPUS_UNAMBIGUITIES = "corpus unambiguities";
	private static final String CORPUS_DIGRAMIZATION = "corpus digramization";
	public static final String CORPUS_LING_ANALYSIS = "corpus linguistic analysis";
	public static final String CORPUS_LOCATE = "corpus locate";
	public static final String TEXT_ALPHABETISATION = "text alphabetisation";
	public static final String TEXT_TOKENIZATION = "text tokenization";
	public static final String TEXT_AMBIGUITIES = "text ambiguities";
	public static final String TEXT_UNAMBIGUITIES = "text unambiguities";
	public static final String TEXT_DIGRAMIZATION = "text digramization";
	public static final String TEXT_LING_ANALYSIS = "text linguistic analysis";
	public static final String TEXT_LOCATE = "text locate";
	public static final String DIC_COMPILE = "dic compile";
	public static final String DIC_INFLECT = "dic inflect";

	private String processName;

	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;
	private DictionaryDialogController dictionaryController;

	private boolean cancellationPending;
	private boolean isBusy;

	public BackgroundWorker()
	{
		super();
		this.processName = "";

		this.textController = null;
		this.corpusController = null;
		this.dictionaryController = null;
	}

	public BackgroundWorker(String processName, TextEditorShellController textController,
			CorpusEditorShellController corpusController, DictionaryDialogController dictionaryController)
	{
		super();
		this.processName = processName;

		this.textController = textController;
		this.corpusController = corpusController;
		this.dictionaryController = dictionaryController;

		this.isBusy = false;
		this.cancellationPending = false;
	}

	public void reportProgress(int i)
	{
		setProgress(i);
	}

	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground()
	{
		this.isBusy = true;

		Launcher.processName = processName;

		if (this.processName.equals(CORPUS_ALPHABETISATION))
		{
			corpusController.computeAlphabet();
		}
		else if (this.processName.equals(CORPUS_TOKENIZATION))
		{
			corpusController.computeTokens();
		}
		else if (this.processName.equals(CORPUS_AMBIGUITIES))
		{
			corpusController.computeAmbiguities(true);
		}
		else if (this.processName.equals(CORPUS_UNAMBIGUITIES))
		{
			corpusController.computeAmbiguities(false);
		}
		else if (this.processName.equals(CORPUS_DIGRAMIZATION))
		{
			corpusController.computeDigrams();
		}
		else if (this.processName.equals(CORPUS_LING_ANALYSIS))
		{
			corpusController.linguisticAnalysis();
		}
		else if (this.processName.equals(CORPUS_LOCATE))
		{
			corpusController.getLocateDialog().getConcordanceLocateActionListener().launchLocate();
		}
		else if (this.processName.equals(TEXT_ALPHABETISATION))
		{
			textController.computeAlphabet();
		}
		else if (this.processName.equals(TEXT_TOKENIZATION))
		{
			textController.computeTokens();
		}
		else if (this.processName.equals(TEXT_AMBIGUITIES))
		{
			textController.computeAmbiguities(true);
		}
		else if (this.processName.equals(TEXT_UNAMBIGUITIES))
		{
			textController.computeAmbiguities(false);
		}
		else if (this.processName.equals(TEXT_DIGRAMIZATION))
		{
			textController.computeDigrams();
		}
		else if (this.processName.equals(TEXT_LING_ANALYSIS))
		{
			textController.linguisticAnalysis();
		}
		else if (this.processName.equals(TEXT_LOCATE))
		{
			textController.getTextShell().getLocateDialog().getConcordanceLocateActionListener().launchLocate();
		}
		else if (this.processName.equals(DIC_COMPILE))
		{
			String fullName = dictionaryController.getTxtDictionaryName().getText();
			String fName = FilenameUtils.removeExtension(org.apache.commons.io.FilenameUtils.getName(fullName));
			String dirName = FilenameUtils.getFullPath(fullName);
			String resName = dirName + fName + "." + Constants.JNOD_EXTENSION;
			// boolean checkAgreement = dictionaryController.getChckbxCheckAgreement().isSelected();
			Language lan;

			try
			{
				lan = new Language(Dictionary.getLanguage(fullName));
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary has not a valid format.",
						"NooJ: cannot read dictionary language", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}

			try
			{
				Dictionary.compile(fullName, resName, Launcher.checkAgreement, lan);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (this.processName.equals(DIC_INFLECT))
		{
			String fullName = dictionaryController.getTxtDictionaryName().getText();
			String fName = FilenameUtils.removeExtension(org.apache.commons.io.FilenameUtils.getName(fullName));
			String dirName = FilenameUtils.getFullPath(fullName);
			String resName = dirName + fName + "." + Constants.JNOD_EXTENSION;
			
			Language lan;

			try
			{
				lan = new Language(Dictionary.getLanguage(fullName));
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary has not a valid format.",
						"NooJ: cannot read dictionary language", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}

			try
			{
				Dictionary.inflect(fullName, resName, Launcher.checkAgreement, lan, Launcher.preferences);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}

		return null;
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done()
	{
		

		Date now = new Date();
		long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;

		

		Launcher.getStatusBar().getBtnCancel().setEnabled(false);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.black);
		Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
		Launcher.getStatusBar().getProgressBar().setValue(0);

		Launcher.backgroundWorking = false;

		if (this.isCancelled())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROCESS_CANCELED_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
		}

		// reactivate all operations
		if (this.processName.equals(CORPUS_ALPHABETISATION))
		{
			corpusController.getShell().getAlphabetDialog().fillInTheAlphabet(corpusController.getCorpus().charlist);

			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			Launcher.progressMessage = "Displaying alphabet...";

			corpusController.getShell().getAlphabetDialog().show();
		}
		else if (this.processName.equals(CORPUS_TOKENIZATION))
		{
			corpusController.getShell().getTokensDialog().fillInTheTokensDigramsTable(true);

			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			Launcher.progressMessage = "Displaying tokens...";

			corpusController.getShell().getTokensDialog().show();
		}
		else if (this.processName.equals(CORPUS_AMBIGUITIES))
		{
			corpusController.getShell().getAmbiguitiesDialog().fillInTheTable();

			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			Launcher.progressMessage = "Displaying ambiguities...";

			corpusController.getShell().getAmbiguitiesDialog().show();
		}
		else if (this.processName.equals(CORPUS_UNAMBIGUITIES))
		{
			corpusController.getShell().getUnAmbiguitiesDialog().fillInTheTable();

			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			Launcher.progressMessage = "Displaying unambiguous words...";

			corpusController.getShell().getUnAmbiguitiesDialog().show();
		}
		else if (this.processName.equals(CORPUS_DIGRAMIZATION))
		{
			corpusController.getShell().getDigramsDialog().fillInTheTokensDigramsTable(true);

			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			Launcher.progressMessage = "Displaying digrams...";

			corpusController.getShell().getDigramsDialog().show();
		}
		else if (this.processName.equals(CORPUS_LING_ANALYSIS))
		{
			corpusController.reactivateOps();
			corpusController.updateTitle();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();
		}
		else if (this.processName.equals(CORPUS_LOCATE))
		{
			corpusController.reactivateOps();

			Launcher.progressMessage = "Displaying concordance...";

			corpusController.getConcordanceController().refreshConcordance();
		}
		else if (this.processName.equals(TEXT_ALPHABETISATION))
		{
			textController.getTextShell().getAlphabetDialog().fillInTheAlphabet(textController.getMyText().charlist);

			textController.reactivateOps();
			textController.updateTextPaneStats();
			textController.rtbTextUpdate(false);

			Launcher.progressMessage = "Displaying alphabet...";

			textController.getTextShell().getAlphabetDialog().show();
		}
		else if (this.processName.equals(TEXT_TOKENIZATION))
		{
			textController.getTextShell().getTokensDialog().fillInTheTokensDigramsTable(false);

			textController.reactivateOps();
			textController.updateTextPaneStats();
			textController.rtbTextUpdate(false);

			Launcher.progressMessage = "Displaying tokens...";

			textController.getTextShell().getTokensDialog().show();
		}
		else if (this.processName.equals(TEXT_AMBIGUITIES))
		{
			textController.getTextShell().getAmbiguitiesDialog().fillInTheTable();

			textController.reactivateOps();
			textController.updateTextPaneStats();

			Launcher.progressMessage = "Displaying ambiguities...";

			textController.getTextShell().getAmbiguitiesDialog().show();
		}
		else if (this.processName.equals(TEXT_UNAMBIGUITIES))
		{
			textController.getTextShell().getUnAmbiguitiesDialog().fillInTheTable();

			textController.reactivateOps();
			textController.updateTextPaneStats();

			Launcher.progressMessage = "Displaying unambiguous words...";

			textController.getTextShell().getUnAmbiguitiesDialog().show();
		}
		else if (this.processName.equals(TEXT_DIGRAMIZATION))
		{
			textController.getTextShell().getDigramsDialog().fillInTheTokensDigramsTable(false);

			textController.reactivateOps();
			textController.updateTextPaneStats();
			textController.rtbTextUpdate(false);

			Launcher.progressMessage = "Displaying digrams...";

			textController.getTextShell().getDigramsDialog().show();
		}
		else if (processName.equals(TEXT_LING_ANALYSIS))
		{
			

			textController.linguisticAnalysisForNewText(textController.getTextShell().getTextPane(),
					textController.getTextShell());

			textController.reactivateOps();
			textController.updateTextPaneStats();
			textController.rtbTextUpdate(false);
			textController.modify();
			// these lines serves for deselecting text after linguistic analysis without invoke later
			// to ensure that there won't be conflicts, unit selection listener is temporarily taken off
			JTextPane textPane = textController.getTextShell().getTextPane();
			int caretPosition = textPane.getCaretPosition();
			UnitSelectionListener selectionListener = textController.getTextShell().getUnitSelectionListener();
			textPane.removeCaretListener(selectionListener);
			textPane.setCaretPosition(caretPosition);
			textPane.getCaret().setVisible(true);
			textPane.addCaretListener(selectionListener);
		}
		else if (this.processName.equals(TEXT_LOCATE))
		{
			textController.reactivateOps();

			Launcher.progressMessage = "Displaying concordance...";

			textController.getConcordanceController().refreshConcordance();
		}
		else if (this.processName.equals(DIC_COMPILE))
		{
			dictionaryController.reactivateOps();
		}
		else if (this.processName.equals(DIC_INFLECT))
		{
			dictionaryController.reactivateOps();
		}

		this.isBusy = false;
		this.cancellationPending = false;
	}

	public boolean isBusy()
	{
		return isBusy;
	}

	public boolean isCancellationPending()
	{
		return cancellationPending;
	}

	public void setCancellationPending(boolean cancellationPending)
	{
		this.cancellationPending = cancellationPending;
	}
}