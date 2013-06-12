package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Utils;
import net.nooj4nlp.engine.Zip;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

public class CorpusEditorShellController
{
	private Engine engine; // Linguistic engine
	private Corpus corpus;

	private String fullPath;
	private String fullName;

	private CorpusEditorShell shell;
	private TextEditorShell textShell;
	private TextEditorShellController textController = null;
	private ConcordanceShellController concordanceController = null;

	private LocateDialog locateDialog;

	private boolean modified;
	private boolean colored = false;
	private boolean beingClosed;

	private JTextPane textPaneStats;
	private JTable tableTexts;
	private JList listResults;
	private List<String> locateGrammarMemoryList = new ArrayList<String>();
	private List<String> locateRegexMemoryList = new ArrayList<String>();
	private List<Color> listOfColors = null;
	private List<Integer> absoluteBeginAddresses = null;
	private List<Integer> absoluteEndAddresses = null;
	private List<String> listOfConcordanceFiles = null;

	public CorpusEditorShellController(CorpusEditorShell shell, JTextPane textPaneStats, JTable tableTexts,
			JList listResults)
	{
		this.shell = shell;

		this.textPaneStats = textPaneStats;
		this.tableTexts = tableTexts;
		this.listResults = listResults;

		this.modified = false;
		this.beingClosed = false;
	}

	/**
	 * Helper function - used to create and save new (empty) corpus
	 * 
	 * @param delimPattern
	 * @param xmlNodes
	 * @param encodingType
	 * @param encodingCode
	 * @param encodingName
	 * @param languageName
	 * @param corpusDirectory
	 * @param corpusName
	 */
	public void createAndSaveNewCorpus(String delimPattern, String[] xmlNodes, int encodingType, String encodingCode,
			String encodingName, String languageName, File corpusDirectory, String corpusName)
	{
		corpus = new Corpus(delimPattern, xmlNodes, encodingType, encodingCode, encodingName, languageName);

		// create *.jnoc file and corpus directory if they don't exist!
		if (corpusDirectory.isDirectory())
		{
			String directoryPath = corpusDirectory.getPath();
			String nocFilePath = directoryPath + System.getProperty("file.separator") + corpusName + "."
					+ Constants.JNOC_EXTENSION;
			String corpusDirPath = nocFilePath + Constants.DIRECTORY_SUFFIX;
			File corpusDir = new File(corpusDirPath);

			// If directory exists, delete it, before creating empty one.
			if (corpusDir.exists())
				Utils.deleteDir(corpusDir);
			new File(corpusDirPath).mkdir();

			try
			{
				corpus.saveIn(corpusDirPath);
				Zip.compressDir(corpusDirPath, nocFilePath);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SAVE_NEW_CORPUS_MESSAGE,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	public CorpusEditorShell openNoojCorpus(File file, boolean isNewCorpus)
	{
		loadCorpusFromFile(file);

		CorpusEditorShell editor = new CorpusEditorShell(this);
		
		Launcher.getDesktopPane().add(editor);
		editor.setVisible(true);

		// Corpus file is modified if it is created for the first time
		if (isNewCorpus)
			this.setModified(true);
		else
			this.setModified(false);

		return editor;
	}

	private void loadCorpusFromFile(File file)
	{
		this.setFullPath(file.getAbsolutePath());
		this.setFullName(file.getName());

		try
		{
			this.setCorpus(Corpus.load(file.getAbsolutePath(), Launcher.preferences.deflanguage));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_CORPUS_LOAD, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_CORPUS_LOAD, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (this.getCorpus() == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE + file.getName(),
					Constants.NOOJ_CORRUPTED_CORPUS, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void openNoojEngine()
	{
		Corpus corpus = this.getCorpus();
		RefObject<Language> corpusLanRef = new RefObject<Language>(corpus.lan);
		Engine engine = new Engine(corpusLanRef, Paths.applicationDir, Paths.docDir, Launcher.preferences.openProjDir,
				Launcher.projectMode, Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		this.setEngine(engine);
		corpus.lan = corpusLanRef.argvalue;

		this.setCorpus(corpus);
	}

	public void updateTextPaneStats()
	{
		textPaneStats.setText("");

		StringBuilder textToBeSet = new StringBuilder();
		textToBeSet.append("Corpus language is \"" + corpus.lan.engName + "\"\n");
		textToBeSet.append("Original Text File format is \"" + corpus.encodingName + "\".\n");
		textToBeSet.append("Corpus consists of " + tableTexts.getModel().getRowCount() + " text files.\n");

		String[] xmlNodes = corpus.xmlNodes;
		if (xmlNodes != null)
		{
			textToBeSet.append("XML Text Nodes are: ");
			for (String tag : xmlNodes)
			{
				textToBeSet.append(tag + " ");
			}
			textToBeSet.append("\n");
		}
		else
		{
			if (corpus.delimPattern.equals(""))
				textToBeSet.append("Corpus has no delimiter => processed each text as one TU\n");
			else if (corpus.delimPattern.equals("\n"))
				textToBeSet.append("Text Delimiter is: \\n (NEWLINE)\n");
			else
				textToBeSet.append("Text Delimiter is: \"" + corpus.delimPattern + "\"\n");
		}

		if (corpus.nbOfTextUnits > 0)
			textToBeSet.append("Corpus contains " + corpus.nbOfTextUnits + " text units delimited by \""
					+ (corpus.delimPattern.equals("\n") ? "\\n" : corpus.delimPattern) + "\"\n");

		if (corpus.nbOfChars > 0)
			textToBeSet.append(corpus.nbOfChars + " characters");
		if (corpus.nbOfDiffChars > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffChars + " diff)");
		if (corpus.nbOfChars > 0)
			textToBeSet.append(", including\n");

		if (corpus.nbOfLetters > 0)
			textToBeSet.append("  " + corpus.nbOfLetters + " letters");
		if (corpus.nbOfDiffLetters > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffLetters + " diff)");
		if (corpus.nbOfLetters > 0)
			textToBeSet.append("\n");

		if (corpus.nbOfDigits > 0)
			textToBeSet.append("  " + corpus.nbOfDigits + " digits");
		if (corpus.nbOfDiffDigits > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffDigits + " diff)");
		if (corpus.nbOfDigits > 0)
			textToBeSet.append("\n");

		if (corpus.nbOfBlanks > 0)
			textToBeSet.append("  " + corpus.nbOfBlanks + " blanks");
		if (corpus.nbOfDiffBlanks > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffBlanks + " diff)");
		if (corpus.nbOfBlanks > 0)
			textToBeSet.append("\n");

		if (corpus.nbOfDelimiters > 0)
			textToBeSet.append("  " + corpus.nbOfDelimiters + " other delimiters");
		if (corpus.nbOfDiffDelimiters > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffDelimiters + " diff)");
		if (corpus.nbOfDelimiters > 0)
			textToBeSet.append("\n");

		if (corpus.nbOfTokens > 0)
			textToBeSet.append(corpus.nbOfTokens + " tokens");
		if (corpus.nbOfDiffTokens > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffTokens + " diff)");
		if (corpus.nbOfTokens > 0)
			textToBeSet.append("\n");

		if (corpus.nbOfWords > 0)
			textToBeSet.append(corpus.nbOfWords + " word forms");
		if (corpus.nbOfDiffWords > 0)
			textToBeSet.append(" (" + corpus.nbOfDiffWords + " diff)");
		if (corpus.nbOfWords > 0)
			textToBeSet.append("\n");

		if (corpus.listOfResources != null)
		{
			textToBeSet.append("Linguistic Resources applied to the text:\n");
			for (String res : corpus.listOfResources)
				textToBeSet.append(res + " ");
			textToBeSet.append("\n");
		}

		if (corpus.annotations != null)
		{
			textToBeSet.append("Corpus contains ");
			textToBeSet.append(corpus.annotations.size() + " different annotations.");
		}

		textPaneStats.setText(textToBeSet.toString());
	}

	public void resetLv()
	{
		if (corpus.listOfFileTexts == null)
		{
			((DefaultTableModel) (tableTexts.getModel())).getDataVector().removeAllElements();
			return;
		}
		else
		{
			int size = corpus.listOfFileTexts.size();
			((DefaultTableModel) (tableTexts.getModel())).getDataVector().removeAllElements();
			DefaultTableModel tableModel = (DefaultTableModel) tableTexts.getModel();

			for (int i = 0; i < size; i++)
			{
				String fileName = corpus.listOfFileTexts.get(i);
				String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
				String fullFilePath = getFullPath() + "_dir" + System.getProperty("file.separator") + fileName;
				File file = new File(fullFilePath);
				long fileSize = file.length();
				Date date = new Date(file.lastModified());

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String dateString = sdf.format(date);

				Object[] row = new Object[4];
				row[0] = fileNameWithoutExtension;
				row[1] = fileSize;
				row[2] = dateString;
				row[3] = "";
				tableModel.addRow(row);
			}
			// Removing Ntext column - it is not supposed to be seen!
			tableTexts.removeColumn(tableTexts.getColumnModel().getColumn(3));
		}
	}

	public void updateResults()
	{
		DefaultListModel model = ((DefaultListModel) listResults.getModel());
		model.clear();

		// listTexts
		if (tableTexts.getComponentCount() > 0)
		{
			model.addElement(Constants.CHARACTERS_LIT);
			model.addElement(Constants.TOKENS_LIT);
			model.addElement(Constants.DIGRAMS_LIT);
		}

		if (corpus.annotations != null)
		{
			if (corpus.annotations.size() < Constants.ANNOTATIONS_MAX_ITEMS)
			model.addElement(Constants.ANNOTATIONS_LIT);
			model.addElement(Constants.UNKNOWNS_LIT);
			model.addElement(Constants.AMBIGUITIES_LIT);
			model.addElement(Constants.UNAMBIGUOUS_WORDS_LIT);
		}
	}

	public void updateTitle()
	{
		StringBuilder titleStringBuilder = new StringBuilder("Corpus ");
		titleStringBuilder.append(fullName);
		if (modified)
			titleStringBuilder.append(" [Modified]");

		shell.setTitle(titleStringBuilder.toString());
	}

	// Methods to be used in listeners...
	public void computeAlphabet()
	{
		String corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;

		HashMap<Character, Integer> theChars = new HashMap<Character, Integer>();
		int itemNb = 0;

		for (String itemName : corpus.listOfFileTexts)
		{
			itemNb++;

			if (Launcher.multithread)
			{
				if (Launcher.backgroundWorker.isCancellationPending())
				{
					return;
				}
				if (Launcher.processName.equals(BackgroundWorker.CORPUS_ALPHABETISATION))
				{
					int nprogress = (int) (itemNb * 100.0F / this.corpus.listOfFileTexts.size());
					if (nprogress != Launcher.progressPercentage)
					{
						Launcher.progressPercentage = nprogress;
						if (Launcher.backgroundWorker.isBusy())
							Launcher.backgroundWorker.reportProgress(nprogress);
					}
				}
		}

			String itemFullName = corpusDirName + System.getProperty("file.separator") + itemName;
			try
			{
				Ntext myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier);

				if (myText == null)
					continue;

				Dic.writeLog(" > parsing " + itemName);

				// Compute charlist
				engine.countChars(myText, theChars);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		engine.computeAlphabet(corpus, theChars);
		modified = true;
	}

	public void computeTokens()
	{
		corpus.hTokens = new HashMap<String, Integer>();

		String corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;

		// listTexts
		DefaultTableModel tableModel = (DefaultTableModel) tableTexts.getModel();

		for (int i = 0; i < tableModel.getRowCount(); i++)
		{
			tableModel.getValueAt(i, 0);
			String itemNotName = tableModel.getValueAt(i, 0) + ".jnot";
			String itemFullName = corpusDirName + System.getProperty("file.separator") + itemNotName;

			Object itemTag = tableModel.getValueAt(i, 3);
			Ntext myText = (itemTag.equals("") ? null : (Ntext) itemTag);

			if (myText == null)
			{
				try
				{
					myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			// Tokenize text
			engine.computeTokens(corpus, myText);
			
			tableModel.setValueAt(myText, i, 3);
		}

		modified = true;
	}

	public void computeDigrams()
	{
		corpus.hDigrams = new HashMap<String, Integer>();

		String corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;

		// listTexts
		DefaultTableModel tableModel = (DefaultTableModel) tableTexts.getModel();

		for (int i = 0; i < tableTexts.getRowCount(); i++)
		{
			tableModel.getValueAt(i, 0);
			String itemNotName = tableModel.getValueAt(i, 0) + ".jnot";
			String itemFullName = corpusDirName + System.getProperty("file.separator") + itemNotName;

			Object itemTag = tableModel.getValueAt(i, 3);
			Ntext myText = (itemTag.equals("") ? null : (Ntext) itemTag);

			if (myText == null)
			{
				try
				{
					myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			engine.computeDigrams(corpus, myText);
			tableModel.setValueAt(myText, i, 3);
		}
		modified = true;
	}

	public void computeAmbiguities(boolean areAmbiguities)
	{
		if (areAmbiguities)
			corpus.hAmbiguities = new HashMap<String, ArrayList<Object>>();
		else
			corpus.hUnambiguities = new HashMap<String, ArrayList<Object>>();

		String corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;

		int itemNb = 0;

		for (String itemName : corpus.listOfFileTexts)
		{
			itemNb++;
			if (Launcher.multithread)
			{
				if (Launcher.backgroundWorker.isCancellationPending())
				{
					return;
				}
				if (Launcher.processName.equals(BackgroundWorker.CORPUS_AMBIGUITIES)
						|| Launcher.processName.equals(BackgroundWorker.CORPUS_UNAMBIGUITIES))
				{
					int nprogress = (int) (itemNb * 100.0F / this.corpus.listOfFileTexts.size());
					if (nprogress != Launcher.progressPercentage)
					{
						Launcher.progressPercentage = nprogress;
						if (Launcher.backgroundWorker.isBusy())
							Launcher.backgroundWorker.reportProgress(nprogress);
					}
				}
			}

			String itemFullName = corpusDirName + System.getProperty("file.separator") + itemName;

			Ntext myText = null;

			try
			{
				myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier);
				if (myText == null)
					continue;
				Dic.writeLog(" > parsing " + itemName);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (areAmbiguities)
			{
				RefObject<HashMap<String, ArrayList<Object>>> refHash = new RefObject<HashMap<String, ArrayList<Object>>>(
						corpus.hAmbiguities);
				engine.computeAmbiguities(corpus, itemName, myText, corpus.annotations, refHash);
			}
			else
			{
				RefObject<HashMap<String, ArrayList<Object>>> refHash = new RefObject<HashMap<String, ArrayList<Object>>>(
						corpus.hUnambiguities);
				engine.computeUnambiguities(corpus, itemName, myText, corpus.annotations, refHash);
			}
		}
	}

	public void linguisticAnalysis()
	{
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			if (Launcher.preferences.ldic.get(corpus.lan.isoName) == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_FIND_LEXICAL_RESOURCE
						+ corpus.lan.isoName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Reload linguistic resources
			RefObject<Language> corpusLanRef = new RefObject<Language>(corpus.lan);
			engine = new Engine(corpusLanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir,
					Launcher.projectMode, Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
			corpus.lan = corpusLanRef.argvalue;

			RefObject<String> errMessage = new RefObject<String>("");
			try
			{
				if (!engine.loadResources(Launcher.preferences.ldic.get(corpus.lan.isoName),
						Launcher.preferences.lsyn.get(corpus.lan.isoName), true, errMessage))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
							+ corpus.lan.isoName, errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
						+ corpus.lan.isoName, errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
						+ corpus.lan.isoName, errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Compute list of linguistic resources
			corpus.listOfResources = new ArrayList<String>();
			ArrayList<String> res = Launcher.preferences.ldic.get(corpus.lan.isoName);
			if (res != null)
			{
				for (String fullName : res)
				{
					String fName = FilenameUtils.getName(fullName);
					String prio = fName.substring(0, 2);
					fName = fName.substring(2);

					corpus.listOfResources.add(fName + "(" + prio + ")");
				}
			}

			res = Launcher.preferences.lsyn.get(corpus.lan.isoName);
			if (res != null)
			{
				for (String fullName : res)
				{
					String fName = FilenameUtils.getName(fullName);
					String prio = fName.substring(0, 2);
					fName = fName.substring(2);

					corpus.listOfResources.add(fName + "(" + prio + ")");
				}
			}

			corpus.annotations = new ArrayList<Object>();
			corpus.hLexemes = new HashMap<String, Integer>();
			corpus.hUnknowns = new HashMap<String, Integer>();
			corpus.hPhrases = new HashMap<String, Integer>();
			corpus.multiplier = 100.0;

			corpus.nbOfTextUnits = 0;
			corpus.nbOfTokens = 0;
			corpus.nbOfDigits = 0;
			corpus.nbOfDelimiters = 0;
			corpus.nbOfWords = 0;

			String corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;
			Dic.writeLog(Constants.LOG_LEX_ANALYSIS_FOR_CORPUS + fullName);
			Dic.writeLog((new Date()).toString());

			HashMap<String, ArrayList<String>> simpleWordCache = new HashMap<String, ArrayList<String>>();
			int itemNb = 0;

			for (String fName : corpus.listOfFileTexts)
			{
				itemNb++;

			if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
						return;
					if (Launcher.processName == "corpus linguistic analysis")
					{
						int nprogress = (int) (itemNb * 100.0F / this.corpus.listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}

				String itemFullName = corpusDirName + System.getProperty("file.separator") + fName;
				try
				{
					Ntext myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier);
					if (myText == null)
					{
						Dic.writeLog(Constants.LOG_CORPUS_FILE + fName + Constants.LOG_IS_CORRUPTED);
						return;
					}

					myText.XmlNodes = corpus.xmlNodes;

					Dic.writeLog(Constants.LOG_PARSING + fName);

					// Delimit text units
					if (corpus.xmlNodes == null) // RAW TXT
						myText.mft = engine.delimit(myText);
					else
						// XML
						myText.mft = engine.delimitXml(myText, corpus.xmlNodes, errMessage);

					if (myText.mft == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SPLIT_1 + fName
								+ Constants.CANNOT_SPLIT_2, errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
						return;
					}
					

					// Tokenize
					engine.tokenize(corpus, myText, corpus.annotations, simpleWordCache, errMessage);
					if (errMessage.argvalue != null && !errMessage.argvalue.equals(""))
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_TOKENIZER_ERROR,
								errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
						return;
					}
					corpus.nbOfTokens += myText.nbOfTokens;
					corpus.nbOfDigits += myText.nbOfDigits;
					corpus.nbOfDelimiters += myText.nbOfDelimiters;
					corpus.nbOfWords += myText.nbOfWords;

					myText.hPhrases = new HashMap<String, Integer>();

					// Apply grammars in dictionary-grammar pairs
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						boolean applyRes = engine.applyAllGrammars(corpus, myText, corpus.annotations, 0, errMessage);
						if (!applyRes && errMessage.argvalue != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_SINT_PARSING_ERROR,
									errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					// Save text results
					myText.saveForCorpus(itemFullName);

					// Merge text data into corpus data
					corpus.nbOfTextUnits += myText.nbOfTextUnits;
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				} // Load results
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_ENGINE_APPLY_ALL_GRAMMARS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			Dic.writeLog(Constants.LOG_SYN_ANALYSIS_FOR_CORPUS + fullName);
			Dic.writeLog((new Date()).toString());
			itemNb = 0;

			for (String fName : corpus.listOfFileTexts)
			{
				itemNb++;

				if (Launcher.multithread)
				{
					if (Launcher.backgroundWorker.isCancellationPending())
						return;
					if (Launcher.processName.equals(BackgroundWorker.CORPUS_LING_ANALYSIS))
					{
						int nprogress = (int) (itemNb * 100.0F / this.corpus.listOfFileTexts.size());
						if (nprogress != Launcher.progressPercentage)
						{
							Launcher.progressPercentage = nprogress;
							if (Launcher.backgroundWorker.isBusy())
								Launcher.backgroundWorker.reportProgress(nprogress);
						}
					}
				}

				String itemFullName = corpusDirName + System.getProperty("file.separator") + fName;

				try
				{
					Ntext myText = Ntext.loadForCorpus(itemFullName, corpus.lan, corpus.multiplier); // Load results

					if (myText == null)
					{
						Dic.writeLog(Constants.LOG_CORPUS_FILE + fName + Constants.LOG_IS_CORRUPTED);
						return;
					}

					myText.XmlNodes = corpus.xmlNodes;

					// Cleanup TAS
					myText.cleanupBadAnnotations(corpus.annotations);
					// Then apply syntactic grammars
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						boolean applyRes = engine.applyAllGrammars(corpus, myText, corpus.annotations, 1, errMessage);
						if (!applyRes && errMessage.argvalue != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_SINT_PARSING_ERROR,
									errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					// Save text results
					myText.saveForCorpus(itemFullName);

					DefaultTableModel tableModel = (DefaultTableModel) this.tableTexts.getModel();
					tableModel.setValueAt(myText, itemNb - 1, 3);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_ENGINE_APPLY_ALL_GRAMMARS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			try
			{
				corpus.saveIn(corpusDirName);
				modified = true;

				Dic.writeLog((new Date()).toString());
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_CORPUS_SAVE_IN, JOptionPane.ERROR_MESSAGE);
				return;
			}

			updateResults();
			updateTextPaneStats();
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	public void desactivateOps()
	{
		

		shell.getBtnAdd().setEnabled(false);
		shell.getBtnRemove().setEnabled(false);
	}

	public void reactivateOps()
	{
	

		shell.getBtnAdd().setEnabled(true);
		shell.getBtnRemove().setEnabled(true);
	}

	public void saveCorpus()
	{
		if (fullPath == null)
		{
			saveAsCorpus();
		}
		else
		{
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
				save(fullPath);
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}
	}

	public void saveAsCorpus()
	{
		String languageName = corpus.lan.isoName;
		String corpusDirName = "";

		if (!fullPath.equals(""))
		{
			// The corpus is being moved: I get the present "_dir" and will compress it elsewhere
			corpusDirName = fullPath + Constants.DIRECTORY_SUFFIX;
		}

		String currentDirToBeSetPath = Paths.docDir + System.getProperty("file.separator") + languageName
				+ System.getProperty("file.separator") + Constants.PROJECTS_PATH;
		File currentDirToBeSet = new File(currentDirToBeSetPath);
		JFileChooser saveCorpusChooser = Launcher.getSaveCorpusChooser();
		saveCorpusChooser.setCurrentDirectory(currentDirToBeSet);

		if (saveCorpusChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return;

		File selectedFile = saveCorpusChooser.getSelectedFile();

		fullPath = selectedFile.getAbsolutePath();
		fullName = selectedFile.getName();

		String parentPath = saveCorpusChooser.getCurrentDirectory().getAbsolutePath();
		String inputFileName = FilenameUtils.removeExtension(fullName) + "." + Constants.JNOC_EXTENSION;
		String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

		File newFile = new File(pathOfInputFile);

		if (newFile.exists())
		{
			int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), fullName + " already exists."
					+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, null, null);
			if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
				return;

			newFile.delete();
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			if (!corpusDirName.equals(""))
				save(corpusDirName, pathOfInputFile);
			else
				save(pathOfInputFile);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	public void saveCorpusForNooJ()
	{
		String languageName = corpus.lan.isoName;
		String dirName = Paths.applicationDir + "resources" + System.getProperty("file.separator") + "initial"
				+ System.getProperty("file.separator") + languageName + System.getProperty("file.separator")
				+ "Projects(java)";
		String fileName = fullName;
		String nooJName = dirName + System.getProperty("file.separator") + fileName;

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			File parent = new File(dirName);

			if (!parent.exists())
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_NO_FILE_PATH,
						Constants.ERROR_MESSAGE_TITLE_CORPUS_SAVE_IN, JOptionPane.ERROR_MESSAGE);
				return;
			}
			File file = new File(nooJName);

			if (file.exists())
				file.delete();

			save(nooJName);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Corpus File " + nooJName + " has been updated!",
				Constants.NOOJ_UPDATE_MESSAGE_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	private void save(String corpusDirPath, String fullPath)
	{
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			corpus.saveIn(corpusDirPath);

			File corpusDir = new File(fullPath);
			if (corpusDir.exists())
			{
				Utils.deleteDir(corpusDir);
			}

			Zip.compressDir(corpusDirPath, fullPath);

			modified = false;
			updateTitle();
		}

		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_CORPUS_SAVE_IN, JOptionPane.ERROR_MESSAGE);
			return;
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	private void save(String fullPath)
	{
		String corpusDirPath = fullPath + Constants.DIRECTORY_SUFFIX;
		File fileCorpus = new File(fullPath);

		if (fileCorpus.exists())
			fileCorpus.delete();

		// Function redesigned to avoid double code
		save(corpusDirPath, fullPath);
	}

	public void fillInUnknowns(DictionaryEditorShell unknownsEditor)
	{
		String lang = corpus.lan.isoName;
		unknownsEditor.getController().initLoad(lang);

		StringBuilder builder = new StringBuilder();
		for (Object tokenObj : corpus.annotations)
		{
			String token = (String) tokenObj;

			if (token == null)
				continue;

			String entry = null, lemma = null, category = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			String[] features = null;
			RefObject<String[]> featuresRef = new RefObject<String[]>(features);

			if (!Dic.parseDELAFFeatureArray(token, entryRef, lemmaRef, categoryRef, featuresRef))
			{
				builder.append(token + " # invalid lexeme\n");
				Dic.writeLog("Lexeme <" + token + "> is invalid");
			}
			else if (categoryRef.argvalue.equals("UNKNOWN"))
			{
				builder.append(token + "\n");
			}
		}

		if (builder.length() > 10000000) // 10MB
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.UNKNOWN_DICTIONARY_LARGE_MESSAGE,
					Constants.UNKNOWN_DICTIONARY_LARGE_CAPTION, JOptionPane.INFORMATION_MESSAGE);

			int answer = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(),
					Constants.UNKNOWN_DICTIONARY_SAVE_MESSAGE, Constants.NOOJ_APPLICATION_NAME,
					JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION)
			{
				String fn = "unknown.dic";
				String pn = Paths.applicationDir + fn;

				FileOutputStream fileOutputStream = null;
				try
				{
					fileOutputStream = new FileOutputStream(pn);
					fileOutputStream.write(unknownsEditor.getTextPane().getText().getBytes());
					fileOutputStream.write(builder.toString().getBytes());
					fileOutputStream.flush();

					fileOutputStream.close();

				}
				catch (FileNotFoundException e)
				{
					try
					{
						if (fileOutputStream != null)
							fileOutputStream.close();
					}
					catch (IOException e1)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (IOException e)
				{
					try
					{
						if (fileOutputStream != null)
							fileOutputStream.close();
					}
					catch (IOException e1)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.UNKNOWN_DICTIONARY_SAVED_MESSAGE
						+ pn, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}
			return;
		}

		String existingText = unknownsEditor.getTextPane().getText();
		StringBuilder newText = new StringBuilder(existingText);
		newText.append(builder.toString());
		unknownsEditor.getTextPane().setText(newText.toString());
		unknownsEditor.getController().sortDictionary(true);
	}

	public void fillInVocabulary(DictionaryEditorShell annotationsEditor)
	{
		if (!corpus.getRidOfUnusedAnnotationsForCorpus(fullPath))
			return;

		annotationsEditor.getController().initLoad(corpus.lan.isoName);
		StringBuilder sb = new StringBuilder();

		HashMap<String, String> frozenExpressions = new HashMap<String, String>();

		for (Object lexemeObj : corpus.annotations)
		{
			String lexeme = (String) lexemeObj;

			if (lexeme == null)
				continue;

			String entry = null, lemma = null, info = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> infoRef = new RefObject<String>(info);

			if (!Dic.parseDELAF(lexeme, entryRef, lemmaRef, infoRef))
				continue;

			info = infoRef.argvalue;
			lemma = lemmaRef.argvalue;
			entry = entryRef.argvalue;

			if (info != null && (Dic.lookFor("NW", info) != null || Dic.lookFor("FXC", info) != null))
				continue; // do

			// Not display non words
			int index = info.indexOf("XREF=");
			if (index != -1)
			{
				int i, i0 = index + "XREF=".length();

				for (i = 0; i0 + i < info.length() && Character.isDigit(info.charAt(i0 + i)); i++)
					;

				String xrefString = null;
				if (i0 + i < info.length())
					xrefString = info.substring(i0, i);
				else
					xrefString = info.substring(i0);

				if (frozenExpressions.containsKey(xrefString))
				{
					String old = frozenExpressions.get(xrefString);

					String oldEntry = null, oldLemma = null, oldInfo = null;
					RefObject<String> oldEntryRef = new RefObject<String>(oldEntry);
					RefObject<String> oldLemmaRef = new RefObject<String>(oldLemma);
					RefObject<String> oldInfoRef = new RefObject<String>(oldInfo);

					if (!Dic.parseDELAF(old, oldEntryRef, oldLemmaRef, oldInfoRef))
						continue;

					oldEntry = oldEntryRef.argvalue;
					oldLemma = oldLemmaRef.argvalue;
					oldInfo = oldInfoRef.argvalue;

					// TODO check frozen expressions!
					frozenExpressions.put(xrefString, oldEntry + "_" + entry + "," + oldInfo);
				}
				else
				{
					frozenExpressions.put(xrefString, entry + "," + Dic.removeFeature("XREF", info));
				}
				continue;
			}

			if (lemma.equals("SYNTAX"))
			{
				sb.append(entry + "," + info + "\n");
			}
			else
			{
				String category = null;
				String[] features = null;

				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String[]> featuresRef = new RefObject<String[]>(features);

				if (!Dic.parseDELAFFeatureArray(lexeme, entryRef, lemmaRef, categoryRef, featuresRef))
					continue;

				features = featuresRef.argvalue;
				String newFeatures = Dic.getRidOfSpecialFeatures(features); // Get rid of UNAMB FLX DRV COLOR

				category = categoryRef.argvalue;
				if (entry.equals(lemma))
				{
					sb.append(entry + "," + category + newFeatures + "\r");
				}
				else
				{
					sb.append(entry + "," + lemma + "," + category + newFeatures + "\r");
				}
			}
		}

		if (frozenExpressions.size() > 0)
		{
			for (Object lexeme : frozenExpressions.values())
			{
				// TODO check frozen expressions!
				String lexemeString = lexeme.toString();

				String entry = null, lemma = null, info = null, category = null;
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> lemmaRef = new RefObject<String>(lemma);
				RefObject<String> infoRef = new RefObject<String>(info);
				RefObject<String> categoryRef = new RefObject<String>(category);

				if (!Dic.parseDELAF(lexemeString, entryRef, lemmaRef, infoRef))
					continue;

				lemma = lemmaRef.argvalue;
				entry = entryRef.argvalue;
				info = infoRef.argvalue;

				if (lemma.equals("SYNTAX"))
				{
					sb.append(entry + "," + info + "\r");
				}
				else
				{
					String[] features = null;
					RefObject<String[]> featuresRef = new RefObject<String[]>(features);

					if (!Dic.parseDELAFFeatureArray(lexemeString, entryRef, lemmaRef, categoryRef, featuresRef))
						continue;

					features = featuresRef.argvalue;
					String newFeatures = Dic.getRidOfSpecialFeatures(features);

					if (entry.equals(lemma))
					{
						sb.append(entry + "," + category + newFeatures + "\r");
					}
					else
					{
						sb.append(entry + "," + lemma + "," + category + newFeatures + "\r");
					}
				}
			}
		}

		if (sb.length() > 10000000) // 10MB
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.DICTIONARY_LARGE_MESSAGE,
					Constants.DICTIONARY_LARGE_CAPTION, JOptionPane.INFORMATION_MESSAGE);

			int answer = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), Constants.DICTIONARY_SAVE_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION)
			{
				String fn = "dictionary.dic";
				String pn = Paths.applicationDir + fn;

				FileOutputStream fileOutputStream = null;
				try
				{
					fileOutputStream = new FileOutputStream(pn);
					fileOutputStream.write(annotationsEditor.getTextPane().getText().getBytes());
					fileOutputStream.write(sb.toString().getBytes());
					fileOutputStream.flush();

					fileOutputStream.close();
				}
				catch (FileNotFoundException e)
				{
					try
					{
						if (fileOutputStream != null)
							fileOutputStream.close();
					}
					catch (IOException e1)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (IOException e)
				{
					try
					{
						if (fileOutputStream != null)
							fileOutputStream.close();
					}
					catch (IOException e1)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.DICTIONARY_SAVED_MESSAGE + pn,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}
			return;
		}

		String existingText = annotationsEditor.getTextPane().getText();
		StringBuilder newText = new StringBuilder(existingText);
		newText.append(sb.toString());
		annotationsEditor.getTextPane().setText(newText.toString());

		if (!annotationsEditor.getController().check())
			annotationsEditor.getController().getErrorShell().setVisible(true);

		annotationsEditor.getController().sortDictionary(true);
	}

	// Getters & setters
	public Corpus getCorpus()
	{
		return corpus;
	}

	public void setCorpus(Corpus corpus)
	{
		this.corpus = corpus;
	}

	public String getFullPath()
	{
		return fullPath;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullPath(String fullPath)
	{
		this.fullPath = fullPath;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public CorpusEditorShell getShell()
	{
		return shell;
	}

	public void setShell(CorpusEditorShell shell)
	{
		this.shell = shell;
	}

	public JTextPane getTextPaneStats()
	{
		return textPaneStats;
	}

	public void setTextPaneStats(JTextPane textPaneStats)
	{
		this.textPaneStats = textPaneStats;
	}

	public JTable getTableTexts()
	{
		return tableTexts;
	}

	public void setTableTexts(JTable tableTexts)
	{
		this.tableTexts = tableTexts;
	}

	public JList getListResults()
	{
		return listResults;
	}

	public void setListResults(JList listResults)
	{
		this.listResults = listResults;
	}

	public Engine getEngine()
	{
		return engine;
	}

	public void setEngine(Engine engine)
	{
		this.engine = engine;
	}

	public boolean isModified()
	{
		return modified;
	}

	public void setModified(boolean modified)
	{
		this.modified = modified;
	}

	public boolean isColored()
	{
		return colored;
	}

	public void setColored(boolean colored)
	{
		this.colored = colored;
	}

	public boolean isBeingClosed()
	{
		return beingClosed;
	}

	public void setBeingClosed(boolean beingClosed)
	{
		this.beingClosed = beingClosed;
	}

	public List<Integer> getAbsoluteBeginAddresses()
	{
		return absoluteBeginAddresses;
	}

	public void setAbsoluteBeginAddresses(List<Integer> absoluteBeginAddresses)
	{
		this.absoluteBeginAddresses = absoluteBeginAddresses;
	}

	public List<Color> getListOfColors()
	{
		return listOfColors;
	}

	public void setListOfColors(List<Color> listOfColors)
	{
		this.listOfColors = listOfColors;
	}

	public List<Integer> getAbsoluteEndAddresses()
	{
		return absoluteEndAddresses;
	}

	public void setAbsoluteEndAddresses(List<Integer> absoluteEndAddresses)
	{
		this.absoluteEndAddresses = absoluteEndAddresses;
	}

	public LocateDialog getLocateDialog()
	{
		return locateDialog;
	}

	public void setLocateDialog(LocateDialog locateDialog)
	{
		this.locateDialog = locateDialog;
	}

	public ConcordanceShellController getConcordanceController()
	{
		return concordanceController;
	}

	public void setConcordanceController(ConcordanceShellController concordanceController)
	{
		this.concordanceController = concordanceController;
	}

	public List<String> getListOfConcordanceFiles()
	{
		return listOfConcordanceFiles;
	}

	public void setListOfConcordanceFiles(List<String> listOfConcordanceFiles)
	{
		this.listOfConcordanceFiles = listOfConcordanceFiles;
	}

	public List<String> getLocateGrammarMemoryList()
	{
		return locateGrammarMemoryList;
	}

	public void setLocateGrammarMemoryList(List<String> locateGrammarMemoryList)
	{
		this.locateGrammarMemoryList = locateGrammarMemoryList;
	}

	public List<String> getLocateRegexMemoryList()
	{
		return locateRegexMemoryList;
	}

	public void setLocateRegexMemoryList(List<String> locateRegexMemoryList)
	{
		this.locateRegexMemoryList = locateRegexMemoryList;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public void setTextController(TextEditorShellController textController)
	{
		this.textController = textController;
	}

	public TextEditorShell getTextShell()
	{
		return textShell;
	}

	public void setTextShell(TextEditorShell textShell)
	{
		this.textShell = textShell;
	}
}