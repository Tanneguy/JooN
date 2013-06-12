package net.nooj4nlp.controller.LocateDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.CorpusEditorShell.AddActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Regexp;
import net.nooj4nlp.engine.Regexps;
import net.nooj4nlp.engine.TheSolutions;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.components.ColoredJButtonUI;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Action Listener class for colored NooJ buttons of a Locate Dialog.
 */
public class ConcordanceLocateActionListener implements ActionListener, PropertyChangeListener
{
	// corpus variables
	private Corpus corpus;
	private String corpusName;
	private String corpusDirPath;
	private String corpusPath;

	// corpus/text determination flag
	private boolean isACorpus;

	// color of clicked button
	private Color currentColor;

	// controllers; one is always null
	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;

	// current language, engine and text
	private Language myLan;
	private Engine myEngine;
	private Ntext myText;

	// solve conflict flag determinator
	private boolean confirmConflict = true;

	// combo boxes
	private JComboBox regexCombo;
	private JComboBox grammarCombo;

	private JCheckBox cbReset;

	// text of grammar and regex combo boxes
	private String grammarComboText;
	private String regexComboText;

	private LocateDialog locateDialog;

	/**
	 * Constructor. One controller is always null, depending on flag.
	 * 
	 * @param corpus
	 *            - flag to determinate whether Locate Dialog is opened from corpus or text context.
	 * @param textController
	 *            - text controller of a text if such exists
	 * @param corpusController
	 *            - corpus controller of a corpus if such exists
	 * @wbp.parser.entryPoint
	 */
	public ConcordanceLocateActionListener(boolean corpus, TextEditorShellController textController,
			CorpusEditorShellController corpusController, LocateDialog locateDialog)
	{
		this.isACorpus = corpus;
		this.corpusController = corpusController;
		this.textController = textController;
		this.locateDialog = locateDialog;

		regexCombo = this.locateDialog.getNooJRegeXCombo();
		grammarCombo = this.locateDialog.getNooJGrammarPathCombo();
		this.cbReset = this.locateDialog.getResetConcordanceCBox();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
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

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// get background color of buttons...if it's a gray button, set color to be a black
		JButton button = (JButton) e.getSource();
		currentColor = ((ColoredJButtonUI) button.getUI()).getBackgroundColor();

		if (currentColor.equals(Color.GRAY))
			currentColor = Color.BLACK;

		// get text of grammar combo box
		grammarComboText = this.grammarCombo.getSelectedItem().toString();
		regexComboText = this.regexCombo.getSelectedItem().toString();

		// if NooJ regular expression radio button is selected, add item to its combo box and to memory list
		if (!this.locateDialog.getRbNooJGrammar().isSelected())
		{
			if (isACorpus)
			{
				List<String> regexList = corpusController.getLocateRegexMemoryList();
				regexList.add(regexComboText);
				corpusController.setLocateRegexMemoryList(regexList);
			}
			else
			{
				List<String> regexList = textController.getLocateRegexMemoryList();
				regexList.add(regexComboText);
				textController.setLocateRegexMemoryList(regexList);
			}
			regexCombo.addItem(regexComboText);
		}

		ConcordanceShellController concordanceController;
		// if it's Locate Dialog of a corpus, initialize variables and launch
		if (isACorpus)
		{
			corpus = corpusController.getCorpus();
			corpusName = corpusController.getFullName();
			corpusPath = corpusController.getFullPath();
			corpusDirPath = corpusPath + "_dir";

			if (corpus.annotations == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EDIT_CORPUS_MESSAGE,
						Constants.EDIT_CORPUS_CAPTION_MESSAGE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// get concordance controller and shell, and open a new if there is no concordance window opened
			ConcordanceShell concordanceShell;
			concordanceController = corpusController.getConcordanceController();

			if (concordanceController != null)
			{
				concordanceShell = concordanceController.getConcordanceShell();
				// Changed when removing static variables
				concordanceShell.getCustomForegroundTableRenderer().setSortedPreview(false);
				// clear old color map
				concordanceShell.getCustomForegroundTableRenderer().setColoredRowsMap(new HashMap<Integer, Color>());

				concordanceShell.dispose();
				concordanceController.setTableModel(null);
				concordanceController = null;
			}

			concordanceShell = new ConcordanceShell(corpusController, corpusController.getTextController());
			Launcher.getDesktopPane().add(concordanceShell);
			concordanceShell.setVisible(true);
			corpusController.setConcordanceController(concordanceShell.getController());

			concordanceController = concordanceShell.getController();

			if (cbReset.isSelected())
				concordanceController.setTheItems(new ArrayList<Object>());
		}

		// if it's Locate Dialog of a text, just launch
		else
		{
			textController.setListOfColors(null);
			textController.setAbsoluteBeginAddresses(null);
			textController.setAbsoluteEndAddresses(null);

			if (textController.isTextWasEdited())
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EDIT_TEXT_MESSAGE,
						Constants.EDIT_TEXT_CAPTION_MESSAGE, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (textController.getMyText().mft == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EDIT_TEXT_MESSAGE,
						Constants.NO_TAS_IS_AVAILABLE, JOptionPane.ERROR_MESSAGE);
				return;
			}
			myText = textController.getMyText();

			// get concordance controller and shell, and open a new if there is no concordance window opened
			ConcordanceShell concordanceShell;
			concordanceController = textController.getConcordanceController();

			if (concordanceController == null)
			{
				concordanceShell = new ConcordanceShell(corpusController, textController);
				Launcher.getDesktopPane().add(concordanceShell);
				concordanceShell.setVisible(true);
				textController.setConcordanceController(concordanceShell.getController());
				concordanceController = concordanceShell.getController();
			}
			else
			{
				concordanceShell = concordanceController.getConcordanceShell();

				// Changed when removing static variables
				concordanceShell.getCustomForegroundTableRenderer().setSortedPreview(false);
				// clear old color map
				concordanceShell.getCustomForegroundTableRenderer().setColoredRowsMap(new HashMap<Integer, Color>());
			}

			if (cbReset.isSelected())
				concordanceController.setTheItems(new ArrayList<Object>());

		}

		initializeLaunchLocate(concordanceController);
	}

	/**
	 * Initializator launcher function.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @wbp.parser.entryPoint
	 */

	private void initializeLaunchLocate(ConcordanceShellController controller)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		// desactivate all formCorpus operations
		
		if (isACorpus)
			corpusController.desactivateOps();
		else
			textController.desactivateOps();

		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.progressMessage = "Locating pattern...";
		Launcher.getStatusBar().getProgressLabel().setText("Locating pattern...");

		if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;

			if (isACorpus)
				Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.CORPUS_LOCATE, null,
						corpusController, null);
			else
				Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.TEXT_LOCATE, textController, null,
						null);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
		else
		{
			// mono-thread
			launchLocate();

			if (isACorpus)
				corpusController.reactivateOps();
			else
				textController.reactivateOps();

			controller.refreshConcordance();

			Date now = new Date();
			long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
			Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
			

			controller.getConcordanceShell().show();
		}
	}

	/**
	 * Launcher function for colored buttons of Locate Dialog.
	 * @wbp.parser.entryPoint
	 */
	public void launchLocate()
	{
		myLan = null;
		myEngine = null;

		// get engine and language
		if (isACorpus)
			myEngine = corpusController.getEngine();
		else
			myEngine = textController.getEngine();

		myLan = myEngine.Lan;

		myEngine.backgroundWorker = Launcher.backgroundWorker;
		myEngine.BackgroundWorking = Launcher.backgroundWorking;

		if (!myEngine.ResourcesLoaded)
		{
			RefObject<String> errMessage = new RefObject<String>(null);
			try
			{
				if (!myEngine.loadResources(Launcher.preferences.ldic.get(myLan.isoName),
						Launcher.preferences.lsyn.get(myLan.isoName), true, errMessage))
				{
					int answer = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), errMessage.argvalue
							+ Constants.PROCEED_ANYWAY_MESSAGE, Constants.NOOJ_WARNING, JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.NO_OPTION)
						return;
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
						+ myText.Lan.isoName, errMessage.argvalue, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		// if number of occurrences radio button is selected, parse the value and send error messages if failed
		int limit = -1;
		if (this.locateDialog.getRbOnly().isSelected())
		{
			String inputText = this.locateDialog.getTxtNumberOfOccurrences().getText();
			try
			{
				limit = Integer.valueOf(inputText);
				int length = 0;
				if (isACorpus)
				{
					corpus = corpusController.getCorpus();
					List<String> listOfTextFiles = corpus.listOfFileTexts;
					for (int i = 0; i < listOfTextFiles.size(); i++)
					{
						String filePath = corpusDirPath + System.getProperty("file.separator") + listOfTextFiles.get(i);
						File file = new File(filePath);
						length += file.length();
					}
				}
				else
					length = textController.getTextShell().getTextPane().getText().length();
				if (limit < 1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_TEXT_RANGE_INPUT_MESSAGE,
							Constants.NOOJ_APPLICATION_NAME + " error, length of text: " + length,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_NUMBER_INPUT_MESSAGE,
						Constants.NOOJ_APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
			}
		}
		boolean onlyOneExample = this.locateDialog.getOneOccPerMatchCBox().isSelected();

		// get selected type of matches
		char typeOfMatch;
		if (this.locateDialog.getRbShortestMatches().isSelected())
			typeOfMatch = 'S';
		else if (this.locateDialog.getRbLongestMatches().isSelected())
			typeOfMatch = 'L';
		else
			typeOfMatch = 'A';

		// launch adequate function for selected values
		if (this.locateDialog.getRbStringPattern().isSelected())
			stringMatch(limit, onlyOneExample, currentColor, regexComboText);
		else if (this.locateDialog.getRbPerlPattern().isSelected())
			perlMatch(limit, onlyOneExample, currentColor, regexComboText);
		else if (this.locateDialog.getRbNooJPattern().isSelected())
			regexpMatch(typeOfMatch, limit, onlyOneExample, currentColor, regexComboText);
		else
			grammarMatch(typeOfMatch, limit, onlyOneExample, currentColor, grammarComboText, this.locateDialog
					.getSyntacticAnalysisCBox().isSelected());
	}

	/**
	 * Function for processing concordance of a selected string match of Locate Dialog.
	 * 
	 * @param limit
	 *            - input limit if such exists
	 * @param onlyOneExample
	 *            - flag for determination if check box "only one example" is checked
	 * @param currentColor
	 *            - color of clicked button
	 * @param regexComboCurrentText
	 *            - input query text
	 * @wbp.parser.entryPoint
	 */
	private void stringMatch(int limit, boolean onlyOneExample, Color currentColor, String regexComboCurrentText)
	{
		// temporary begin and end of a query pattern in search
		int absoluteBeginAddress = 0, absoluteEndAddress = 0;

		if (regexComboCurrentText.length() == 0)
			return;

		int counter = 0;

		// if it's a corpus process every single text
		if (isACorpus)
		{
			Dic.writeLog("Locating string in corpus " + corpusName);
			int itemNb = 0;
			for (String fileName : corpus.listOfFileTexts)
			{
				itemNb++;

				if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (Launcher.processName.substring(0, 6).equals("corpus"))
					{
						int nprogress = (int) (itemNb * 100.0F / corpusController.getCorpus().listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}
				String filePath = corpusDirPath + System.getProperty("file.separator") + fileName;
				try
				{
					myText = Ntext.loadForCorpus(filePath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (myText == null)
					continue;

				// find first occurrence in text...
				Dic.writeLog(Constants.LOG_PARSING + fileName);
				absoluteBeginAddress = myText.buffer.indexOf(regexComboCurrentText, 0);

				// ...and find others too
				while (absoluteBeginAddress >= 0)
				{
					absoluteEndAddress = absoluteBeginAddress + regexComboCurrentText.length();

					ConcordanceShellController concordanceController = corpusController.getConcordanceController();
					boolean effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, -1,
							absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "STRING = "
									+ regexComboCurrentText);

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;

					absoluteBeginAddress = absoluteEndAddress;
					absoluteBeginAddress = myText.buffer.indexOf(regexComboCurrentText, absoluteBeginAddress);

					if (limit != -1 && counter >= limit)
						break;
				}
				if (limit != -1 && counter >= limit)
					break;
			}
		}
		else
		{
			Dic.writeLog("Locating string in text " + textController.getTextName());
			absoluteBeginAddress = myText.buffer.indexOf(regexComboCurrentText, 0);
			while (absoluteBeginAddress >= 0)
			{
				absoluteEndAddress = absoluteBeginAddress + regexComboCurrentText.length();

				ConcordanceShellController concordanceController = textController.getConcordanceController();
				boolean effectivelyAdded = concordanceController.AddData(myText, "", currentColor, -1,
						absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "STRING = "
								+ regexComboCurrentText);

				if (effectivelyAdded)
					counter++;

				if (limit != -1 && counter >= limit)
					break;
				absoluteBeginAddress = absoluteEndAddress;
				absoluteBeginAddress = myText.buffer.indexOf(regexComboCurrentText, absoluteBeginAddress);
			}
		}
	}

	/**
	 * Function for processing concordance of a selected Perl match of Locate Dialog.
	 * 
	 * @param limit
	 *            - input limit if such exists
	 * @param onlyOneExample
	 *            - flag for determination if check box "only one example" is checked
	 * @param currentColor
	 *            - color of clicked button
	 * @param regexComboCurrentText
	 *            - input query text
	 * @wbp.parser.entryPoint
	 */
	private void perlMatch(int limit, boolean onlyOneExample, Color currentColor, String regexComboCurrentText)
	{
		// temporary begin and end of a query pattern in search
		int absoluteBeginAddress = 0, absoluteEndAddress = 0;

		if (regexComboCurrentText.length() == 0)
			return;

		Pattern rexp = Pattern.compile(regexComboCurrentText, Pattern.MULTILINE);

		int counter = 0;
		// if it's a corpus process every single text
		if (isACorpus)
		{
			Dic.writeLog("Applying PERL-type query to corpus " + corpusName);
			int itemNb = 0;
			for (String fileName : corpus.listOfFileTexts)
			{
				itemNb++;

		if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (Launcher.processName.substring(0, 6).equals("corpus"))
					{
						int nprogress = (int) (itemNb * 100.0F / corpusController.getCorpus().listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}

				String filePath = corpusDirPath + System.getProperty("file.separator") + fileName;
				try
				{
					myText = Ntext.loadForCorpus(filePath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (myText == null)
					continue;
				Dic.writeLog(Constants.LOG_PARSING + fileName);
				absoluteBeginAddress = myText.buffer.indexOf(regexComboCurrentText, 0);

				Matcher mc = rexp.matcher(myText.buffer);
				while (mc.find())
				{
					absoluteBeginAddress = mc.start();
					absoluteEndAddress = mc.end();

					ConcordanceShellController concordanceController = corpusController.getConcordanceController();
					boolean effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, -1,
							absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "PERL RE = "
									+ regexComboCurrentText);

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;
				}
				if (limit != -1 && counter >= limit)
					break;
			}
		}
		else
		{
			Dic.writeLog("Applying PERL-type query to text " + textController.getTextName());

			Matcher mc = rexp.matcher(myText.buffer);
			while (mc.find())
			{
				absoluteBeginAddress = mc.start();
				absoluteEndAddress = mc.end();

				ConcordanceShellController concordanceController = textController.getConcordanceController();
				boolean effectivelyAdded = concordanceController.AddData(myText, "", currentColor, -1,
						absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "PERL RE = "
								+ regexComboCurrentText);

				if (effectivelyAdded)
					counter++;

				if (limit != -1 && counter >= limit)
					break;
			}
		}
	}

	/**
	 * Function for processing concordance of a selected NooJ regular expression match of Locate Dialog.
	 * 
	 * @param typeOfMatch
	 *            - selected value type of match
	 * @param limit
	 *            - input limit if such exists
	 * @param onlyOneExample
	 *            - flag for determination if check box "only one example" is checked
	 * @param currentColor
	 *            - color of clicked button
	 * @param regexComboCurrentText
	 *            - input query text
	 * @wbp.parser.entryPoint
	 */
	private void regexpMatch(char typeOfMatch, int limit, boolean onlyOneExample, Color currentColor,
			String regexComboCurrentText)
	{
		int counter = 0;
		Regexp regexp;
		Regexps regexps;

		// get regular expressions and prepare for parsing
		try
		{
			regexp = new Regexp(myLan, regexComboCurrentText, GramType.SYNTAX);
			regexp.Grm.prepareForParsing();
			regexps = new Regexps(myLan, GramType.SYNTAX, regexp.Grm, myEngine);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_INVALID_REGULAR_EXPRESSION,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// create grammars
		// Gram grm = (Gram) regexps.grammar.grams.get("Main");

		// if it's a corpus process every single text
		if (isACorpus)
		{
			Dic.writeLog("Applying regular expression to corpus " + corpusName);
			int itemNb = 0;
			for (String fileName : corpus.listOfFileTexts)
			{
				itemNb++;

		if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (Launcher.processName.substring(0, 6).equals("corpus"))
					{
						int nprogress = (int) (itemNb * 100.0F / corpusController.getCorpus().listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}

				String filePath = corpusDirPath + System.getProperty("file.separator") + fileName;
				try
				{
					myText = Ntext.loadForCorpus(filePath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (myText == null)
					continue;

				Dic.writeLog(Constants.LOG_PARSING + fileName);

				if (myText.mft == null)
				{
					Dic.writeLog(" ! no linguistic data available for text " + fileName);
					continue;
				}

				// initialization of variables
				TheSolutions solutions = null;
				boolean thereIsUnAmbiguity = false;
				String errorMessage = "";
				if (onlyOneExample)
				{
					try
					{
						solutions = myEngine.syntacticParsing(corpus, myText, corpus.annotations, regexps.grammar,
								typeOfMatch, -1, false, false, new RefObject<Boolean>(thereIsUnAmbiguity),
								new RefObject<String>(errorMessage));
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (ClassNotFoundException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE + myText.Lan.isoName, e.getMessage(),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				else
				{
					try
					{
						solutions = myEngine.syntacticParsing(corpus, myText, corpus.annotations, regexps.grammar,
								typeOfMatch, limit, false, false, new RefObject<Boolean>(thereIsUnAmbiguity),
								new RefObject<String>(errorMessage));
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (ClassNotFoundException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE + myText.Lan.isoName, e.getMessage(),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				if (!errorMessage.equals(""))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
							Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (solutions == null || (solutions != null && solutions.list.size() == 0))
					continue;

				int sizeOfSolutionsList = solutions.list.size();
				for (int isol = 0; isol < sizeOfSolutionsList; isol++)
				{
					int tuNb = solutions.getTuNb(isol);
					double absoluteBeginAddress = solutions.getBegAddress(isol) + myText.mft.tuAddresses[tuNb];
					double absoluteEndAddress = absoluteBeginAddress + solutions.getLength(isol);

					// regular expression can have outputs too!
					ArrayList<Double> relAddresses = solutions.getInput(isol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[tuNb]);
					ArrayList<String> output = solutions.getOutput(isol);
					ArrayList<Object> seqOfAnnotations = myEngine.mergeIntoAnnotations(myText.buffer, tuNb,
							absoluteBeginAddress, absoluteEndAddress, absAddresses, output, false);
					boolean effectivelyAdded = false;

					ConcordanceShellController concordanceController = corpusController.getConcordanceController();

					if (seqOfAnnotations == null)
						effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "RE = "
										+ regexComboCurrentText);
					else
						effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, seqOfAnnotations, onlyOneExample, "RE = "
										+ regexComboCurrentText);

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;
				}
				if (limit != -1 && counter >= limit)
					break;
			}
		}
		else
		{
			if (myText == null || myText.buffer == null || myText.mft == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.REAPPLY_LINGUISTIC_RESOURCES_MESSAGE, Constants.CORRUPTED_TEXT_FILE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			Dic.writeLog("Applying regular expression to text " + textController.getTextName());

			// initialization of variables
			TheSolutions solutions = null;
			boolean thereIsUnAmbiguity = false;
			String errorMessage = "";

			// if no duplicates of pattern allowed
			if (onlyOneExample)
			{
				try
				{
					solutions = myEngine.syntacticParsing(null, myText, myText.annotations, regexps.grammar,
							typeOfMatch, -1, false, false, new RefObject<Boolean>(thereIsUnAmbiguity),
							new RefObject<String>(errorMessage));
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
							+ myText.Lan.isoName, e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else
			{
				try
				{
					solutions = myEngine.syntacticParsing(null, myText, myText.annotations, regexps.grammar,
							typeOfMatch, limit, false, false, new RefObject<Boolean>(thereIsUnAmbiguity),
							new RefObject<String>(errorMessage));
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
							+ myText.Lan.isoName, e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			if (!errorMessage.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
						Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			int sizeOfSolutionsList = solutions.list.size();

			if (solutions != null && sizeOfSolutionsList > 0)
			{
				for (int isol = 0; isol < sizeOfSolutionsList; isol++)
				{
					int tuNb = solutions.getTuNb(isol);
					double absoluteBeginAddress = solutions.getBegAddress(isol) + myText.mft.tuAddresses[tuNb];
					double absoluteEndAddress = absoluteBeginAddress + solutions.getLength(isol);

					ArrayList<Double> relAddresses = solutions.getInput(isol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[tuNb]);
					ArrayList<String> output = solutions.getOutput(isol);
					ArrayList<Object> seqOfAnnotations = myEngine.mergeIntoAnnotations(myText.buffer, tuNb,
							absoluteBeginAddress, absoluteEndAddress, absAddresses, output, false);

					boolean effectivelyAdded = false;

					ConcordanceShellController concordanceController = textController.getConcordanceController();

					if (seqOfAnnotations == null)
						effectivelyAdded = concordanceController.AddData(myText, "", currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "RE = "
										+ regexComboCurrentText);
					else
						effectivelyAdded = concordanceController.AddData(myText, "", currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, seqOfAnnotations, onlyOneExample, "RE = "
										+ regexComboCurrentText);

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;
				}
			}
		}
	}

	/**
	 * Function for processing concordance of a selected grammar match of Locate Dialog.
	 * 
	 * @param typeOfMatch
	 *            - selected value type of match
	 * @param limit
	 *            - input limit if such exists
	 * @param onlyOneExample
	 *            - flag for determination if check box "only one example" is checked
	 * @param currentColor
	 *            - color of clicked button
	 * @param selectedGrammar
	 *            - path of grammar file
	 * @param isSyntacticAnalysisCBSelected
	 *            - flag for determination if check box "Syntactic analysis" is checked
	 * @wbp.parser.entryPoint
	 */
	private void grammarMatch(char typeOfMatch, int limit, boolean onlyOneExample, Color currentColor,
			String selectedGrammar, boolean isSyntacticAnalysisCBSelected)
	{
		String errorMessage = null;

		// get grammar file if exists. If not, send error message
		File grammarFile = new File(selectedGrammar);
		if (!grammarFile.exists())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_FIND_FILE,
					Constants.CANNOT_FIND_GRAMMAR_MESSAGE, JOptionPane.ERROR_MESSAGE);
			errorMessage = "Grammar file " + selectedGrammar + " does not exists!";
			Dic.writeLog(errorMessage);
			return;
		}

		String grammarFileName = grammarFile.getName();
		Dic.writeLog("Compiling grammar...");
		Launcher.progressMessage = "Compiling grammar...";

		Grammar grammar = null;

		// load grammar and if null, send error message
		boolean isTextual = Grammar.isItTextual(selectedGrammar);
		if (isTextual)
		{
			RefObject<String> errorRef = new RefObject<String>(errorMessage);
			grammar = Grammar.loadTextual(selectedGrammar, GramType.SYNTAX, errorRef);
			errorMessage = errorRef.argvalue;
			if (errorMessage != null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_HANDLE_GRAMMAR_MESSAGE
						+ grammarFileName + ":\n" + errorMessage, Constants.CANNOT_HANDLE_GRAMMAR_MESSAGE_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else
			
			grammar = Grammar.loadONooJGrammar(selectedGrammar);

		if (grammar == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_GRAMMAR_MESSAGE
					+ grammarFileName, Constants.CANNOT_LOAD_GRAMMAR_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// compile and compute grammar
		errorMessage = grammar.compileAll(myEngine);
		if (errorMessage != null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
					Constants.CANNOT_LOAD_GRAMMAR_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// if grammar language is different from language from preferences, send option dialog
		String languageName = grammar.iLanguage;
		if (!myLan.isoName.equals(languageName))
		{
			int optionType = JOptionPane.OK_CANCEL_OPTION;
			int messageType = JOptionPane.WARNING_MESSAGE; // no standard icon
			final JButton ok = new JButton("OK");
			final JButton cancel = new JButton("Cancel");
			// listener for "Ok" button; flag is true
			ok.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					confirmConflict = true;
				}
			});
			// listener for "Cancel" button; close the message box and set flag to false
			cancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					confirmConflict = false;
					AddActionListener.closeDialogWindow(cancel);
				}
			});
			// construct options
			Object[] selValues = { ok, cancel };
			// show dialog as normal, selected index will be returned.
			JOptionPane.showOptionDialog(Launcher.getDesktopPane(), Constants.NOOJ_LANGUAGE_CONFLICT,
					Constants.NOOJ_LANGUAGE_INCONSISTENCY, optionType, messageType, null, selValues, selValues[0]);

			// if "Cancel" is selected, stop all processes
			if (!confirmConflict)
				return;
		}
		Launcher.progressMessage = "Applying grammar...";

		int counter = 0;
		// if it's a corpus process every single text
		if (isACorpus)
		{
			Dic.writeLog("Applying grammar to corpus " + corpusName);
			int itemNb = 0;
			for (String fileName : corpus.listOfFileTexts)
			{
				itemNb++;

				// Tracking Progress
			if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (Launcher.processName.substring(0, 6).equals("corpus"))
					{
						Launcher.progressMessage = "Applying grammar (" + counter + ")...";

						int nprogress = (int) (itemNb * 100.0F / corpusController.getCorpus().listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}

				String filePath = corpusDirPath + System.getProperty("file.separator") + fileName;
				try
				{
					myText = Ntext.loadForCorpus(filePath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (myText == null)
					continue;

				Dic.writeLog(Constants.LOG_PARSING + fileName);
				if (myText.mft == null)
				{
					Dic.writeLog(" ! no linguistic data available for " + fileName);
					return;
				}

				// initialization of variables
				TheSolutions solutions = null;
				boolean thereIsUnAmbiguity = false;
				RefObject<String> errmessageRef = new RefObject<String>(errorMessage);

				if (onlyOneExample)
				{
					try
					{
						solutions = myEngine.syntacticParsing(corpus, myText, corpus.annotations, grammar, typeOfMatch,
								-1, isSyntacticAnalysisCBSelected, true, new RefObject<Boolean>(thereIsUnAmbiguity),
								errmessageRef);
						errorMessage = errmessageRef.argvalue;
					}
					catch (ClassNotFoundException e)
					{
						JOptionPane
								.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
										Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_NO_CLASS,
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_IO, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
				{
					try
					{
						solutions = myEngine.syntacticParsing(corpus, myText, corpus.annotations, grammar, typeOfMatch,
								limit, isSyntacticAnalysisCBSelected, true, new RefObject<Boolean>(thereIsUnAmbiguity),
								errmessageRef);
						errorMessage = errmessageRef.argvalue;
					}
					catch (ClassNotFoundException e)
					{
						JOptionPane
								.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
										Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_NO_CLASS,
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_IO, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				if (errorMessage != null && solutions == null)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
							Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				int sizeOfSolutionsList = solutions.list.size();

				if (solutions == null || sizeOfSolutionsList == 0)
					continue;

				for (int isol = 0; isol < sizeOfSolutionsList; isol++)
				{
					int tuNb = solutions.getTuNb(isol);
					double absoluteBeginAddress = solutions.getBegAddress(isol) + myText.mft.tuAddresses[tuNb];
					double absoluteEndAddress = absoluteBeginAddress + solutions.getLength(isol);

					// regular expression can have outputs too!
					ArrayList<Double> relAddresses = solutions.getInput(isol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[tuNb]);
					ArrayList<String> output = solutions.getOutput(isol);
					ArrayList<Object> seqOfAnnotations = myEngine.mergeIntoAnnotations(myText.buffer, tuNb,
							absoluteBeginAddress, absoluteEndAddress, absAddresses, output,
							isSyntacticAnalysisCBSelected);
					boolean effectivelyAdded = false;

					ConcordanceShellController concordanceController = corpusController.getConcordanceController();

					if (seqOfAnnotations == null)
						effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "GRAM = "
										+ FilenameUtils.removeExtension(selectedGrammar));
					else
						effectivelyAdded = concordanceController.AddData(myText, fileName, currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, seqOfAnnotations, onlyOneExample, "GRAM = "
										+ FilenameUtils.removeExtension(selectedGrammar));

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;
				}
				if (limit != -1 && counter >= limit)
					break;
			}
		}
		else
		{
			Dic.writeLog("Applying regular expression to text " + textController.getTextName());
			if (myText == null || myText.buffer == null || myText.mft == null)
			{
				Dic.writeLog(" error.");
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.REAPPLY_LINGUISTIC_RESOURCES_MESSAGE, Constants.CORRUPTED_TEXT_FILE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// initialization of variables
			TheSolutions solutions = null;
			boolean thereIsUnAmbiguity = false;
			RefObject<String> errmessageRef = new RefObject<String>(errorMessage);

			// if no duplicates of pattern allowed
			if (onlyOneExample)
			{
				try
				{
					solutions = myEngine.syntacticParsing(null, myText, myText.annotations, grammar, typeOfMatch, -1,
							isSyntacticAnalysisCBSelected, true, new RefObject<Boolean>(thereIsUnAmbiguity),
							errmessageRef);
					errorMessage = errmessageRef.argvalue;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_NO_CLASS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_IO, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				try
				{
					solutions = myEngine.syntacticParsing(null, myText, myText.annotations, grammar, typeOfMatch,
							limit, isSyntacticAnalysisCBSelected, true, new RefObject<Boolean>(thereIsUnAmbiguity),
							errmessageRef);
					errorMessage = errmessageRef.argvalue;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_NO_CLASS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_IO, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (errorMessage != null && solutions == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
						Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			int sizeOfSolutionsList = solutions != null ? solutions.list.size() : 0;

			if (solutions != null && sizeOfSolutionsList > 0)
			{
				for (int isol = 0; isol < sizeOfSolutionsList; isol++)
				{
					int tuNb = solutions.getTuNb(isol);
					double absoluteBeginAddress = solutions.getBegAddress(isol) + myText.mft.tuAddresses[tuNb];
					double absoluteEndAddress = absoluteBeginAddress + solutions.getLength(isol);

					ArrayList<Double> relAddresses = solutions.getInput(isol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[tuNb]);
					ArrayList<String> output = solutions.getOutput(isol);
					ArrayList<Object> seqOfAnnotations = myEngine.mergeIntoAnnotations(myText.buffer, tuNb,
							absoluteBeginAddress, absoluteEndAddress, absAddresses, output,
							isSyntacticAnalysisCBSelected);

					boolean effectivelyAdded = false;

					ConcordanceShellController concordanceController = textController.getConcordanceController();

					if (seqOfAnnotations == null)
						effectivelyAdded = concordanceController.AddData(myText, "", currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, null, onlyOneExample, "GRAM = "
										+ FilenameUtils.removeExtension(selectedGrammar));
					else
						effectivelyAdded = concordanceController.AddData(myText, "", currentColor, tuNb,
								absoluteBeginAddress, absoluteEndAddress, seqOfAnnotations, onlyOneExample, "GRAM = "
										+ FilenameUtils.removeExtension(selectedGrammar));

					if (effectivelyAdded)
						counter++;

					if (limit != -1 && counter >= limit)
						break;
				}
			}

			else if (solutions == null || sizeOfSolutionsList == 0)
				return;

			else
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
						Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
}