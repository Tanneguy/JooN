package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Indexkey;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.TextIO;
import net.nooj4nlp.engine.TuGraph;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.components.CustomJSpinner;
import net.nooj4nlp.gui.dialogs.TextCorpusDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Controller class for TextEditorShell.
 */
public class TextEditorShellController
{
	// variables
	private boolean modified;
	private boolean colored = false;

	private TextEditorShell textShell;
	private Engine engine;
	private JTextPane paneOfTextEditor;
	private boolean textIsBeingColored = false;
	private Ntext myText;
	private boolean currentTextUnitIsBlack;
	private File fileToBeOpenedOrImported;

	private boolean textWasEdited = false;
	private String textName;
	private List<String> locateGrammarMemoryList = new ArrayList<String>();
	private List<String> locateRegexMemoryList = new ArrayList<String>();
	private CorpusEditorShellController corpusController;
	private ConcordanceShellController concordanceController;
	private Corpus corpus;
	private boolean NooJRightToLeft;
	private JTextArea textInfo;

	private List<Color> listOfColors = null;
	private List<Integer> absoluteBeginAddresses = null;
	private List<Integer> absoluteEndAddresses = null;

	// TAS variables
	private TuGraph tuGraph;
	private double selectAllAnnotationsAtPosition;

	private ArrayList<Object> annotationsHistoric;

	public TextEditorShellController(TextEditorShell shell)
	{
		this.textShell = shell;
		this.corpusController = this.textShell.getCorpusController();

		this.myText = this.textShell.getText();

		if (this.corpusController != null && corpusController.getShell() != null)
		{
			engine = corpusController.getEngine();

			JList resultsList = this.textShell.getListOfResults();
			resultsList.setVisible(false);
			textShell.getScrollList().setVisible(false);
			textInfo = this.textShell.getTxtInfo();
			textInfo.setVisible(false);

		}
		else
			startNoojEngine();

		NooJRightToLeft = myText.Lan.rightToLeft;
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, (NooJRightToLeft ? StyleConstants.ALIGN_RIGHT
				: StyleConstants.ALIGN_LEFT));
		StyleConstants.setFontFamily(attributeSet, Launcher.preferences.TFont.getFamily());
		StyleConstants.setFontSize(attributeSet, Launcher.preferences.TFont.getSize());

		paneOfTextEditor = this.textShell.getTextPane();

		if (myText.buffer != null)
			paneOfTextEditor.getStyledDocument().setParagraphAttributes(0, myText.buffer.length(), attributeSet, true);
		else
		{
			paneOfTextEditor.getStyledDocument().setParagraphAttributes(0, 0, attributeSet, true);
		}

		this.fileToBeOpenedOrImported = null;
		
	}

	public TextEditorShellController(File fileToBeOpenedOrImported)
	{
		this.fileToBeOpenedOrImported = fileToBeOpenedOrImported;
		this.textName = fileToBeOpenedOrImported.getName();
	}

	/**
	 * Function for opening selected text file.
	 * 
	 * @param file
	 *            - text file be opened
	 */
	public TextEditorShell openText(File file)
	{
		fileToBeOpenedOrImported = file;
		textName = fileToBeOpenedOrImported.getName();

		// load text
		try
		{
			myText = Ntext.load(file.getAbsolutePath(), Launcher.preferences.deflanguage,
					new RefObject<String>("error"));
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE + file.getName(),
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE + file.getName(),
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// if load succeeds, but there is no text, send error message and exit
		if (myText == null || myText.buffer == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_CORRUPTED_TEXT,
					Constants.NOOJ_APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		startNoojEngine();

		String errorMessage = "";

		// if mft is null, delimit the text
		if (myText.mft == null)
		{
			errorMessage = engine.delimitTextUnits(myText);

			if (!errorMessage.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.NOOJ_ERROR + " "
						+ errorMessage, JOptionPane.ERROR_MESSAGE);
				return null;
			}

			// open text editor window and update stats
			textShell = new TextEditorShell(null, myText, file.getName(), myText.getDelimPattern(), false);

			if (textShell != null)
			{
				textShell.refreshListenersAndAdapters();
				TextEditorShellController controller = textShell.getTextController();
				controller.fileToBeOpenedOrImported = this.fileToBeOpenedOrImported;
				controller.textName = this.fileToBeOpenedOrImported.getName();
				// to ensure that there won't be conflicts, unit selection listener is temporarily taken off
				UnitSelectionListener unitSelectionListener = textShell.getUnitSelectionListener();
				textShell.getTextPane().removeCaretListener(unitSelectionListener);
				controller.rtbTextUpdate(true);
				textShell.getTextPane().addCaretListener(unitSelectionListener);
				controller.updateTextPaneStats();
			}

			// Is it really necessary to call Modify() function on file that was only open?
			// Yes, because myText object changes after delimiting text units.
			modify();
		}
		else
		{
			// open text editor window and update stats
			textShell = new TextEditorShell(null, myText, file.getName(), myText.getDelimPattern(), false);

			if (textShell != null)
			{
				textShell.refreshListenersAndAdapters();
				TextEditorShellController controller = textShell.getTextController();
				controller.fileToBeOpenedOrImported = this.fileToBeOpenedOrImported;
				controller.textName = this.fileToBeOpenedOrImported.getName();
				UnitSelectionListener unitSelectionListener = textShell.getUnitSelectionListener();
				textShell.getTextPane().removeCaretListener(unitSelectionListener);
				controller.rtbTextUpdate(true);
				textShell.getTextPane().addCaretListener(unitSelectionListener);
				controller.updateTextPaneStats();
			}
		}

		Launcher.getDesktopPane().add(textShell);
		textShell.setVisible(true);

		return textShell;
	}

	/**
	 * Function for importing selected text file.
	 * 
	 * @param selectedFile
	 *            - file selected for import
	 */

	public void importText(File selectedFile)
	{
		fileToBeOpenedOrImported = selectedFile;
		textName = fileToBeOpenedOrImported.getName();

		// open text corpus dialog
		TextCorpusDialog dialog = new TextCorpusDialog(false, true, Launcher.lastActive, fileToBeOpenedOrImported);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Import a text");
		// Selected language is taken from Preferences
		dialog.getListLanguages().setSelectedValue(Launcher.preferences.deflanguage, false);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	/**
	 * Function for enable/disable controls of components in TextEditorShell.
	 */
	public void rtbTextUpdate(boolean aNewText)
	{
		textIsBeingColored = false;
		JCheckBox cBox = textShell.getChckbxShowTextAnnotation();
		CustomJSpinner spinner = textShell.getSpinner();
		paneOfTextEditor = textShell.getTextPane();

		if (aNewText)
		{
			paneOfTextEditor.setText(myText.buffer);
			paneOfTextEditor.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
			paneOfTextEditor.setCaretPosition(0);
		}

		// if text mft is null, disable all components, paint whole text in black and exit
		if (myText.mft == null)
		{
			spinner.setEnabled(false);
			if (cBox.isSelected())
				cBox.setSelected(false);
			cBox.setEnabled(false);

			setTextColorOfAPane(paneOfTextEditor, Color.BLACK);
			return;
		}

		// set initial values
		if (!spinner.isEnabled())
			spinner.setEnabled(true);

		// enable text unit exploration
		if ((Integer) spinner.getValue() > myText.nbOfTextUnits)
			spinner.setValue(myText.nbOfTextUnits);
		else if ((Integer) spinner.getValue() < 1)
			spinner.setCustomValue(1);

		ArrayList<Object> annotations = new ArrayList<Object>();

		if (corpus != null)
			annotations = corpus.annotations;
		else
			annotations = myText.getAnnotations();

		if (annotations != null && myText.mft != null)
		{
			cBox.setEnabled(true);
			if (cBox.isSelected())
			{
				int currentUnit = (Integer) spinner.getValue();
				tuGraph = myText.mft.getTuGraph(currentUnit, tuGraph);

				if (tuGraph != null)
					tuGraph.setNeedToBeComputed(true);

				// Repainting components
				textShell.invalidate();
				textShell.validate();
				textShell.repaint();
			}

			textShell.getUnitSelectionListener().partialColorText(this, myText, paneOfTextEditor, textShell, true);
		}
	}

	/**
	 * Set text color to a text pane.
	 * 
	 * @param textPane
	 *            - text pane with text which color needs to be set
	 * @param color
	 *            - text color to be set
	 */
	private void setTextColorOfAPane(JTextPane textPane, Color color)
	{
		// get the attributes and set foreground
		MutableAttributeSet attributes = textPane.getInputAttributes();
		StyleConstants.setForeground(attributes, color);
		StyledDocument doc = textPane.getStyledDocument();
		// tie attributes to doc in text pane
		doc.setCharacterAttributes(0, doc.getLength() + 1, attributes, true);
	}

	/**
	 * Function for starting NooJ engine for opening a text file.
	 */

	private void startNoojEngine()
	{
		engine = new Engine(new RefObject<Language>(myText.Lan), Paths.applicationDir, Paths.docDir, Paths.projectDir,
				Launcher.projectMode, Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
	}

	/**
	 * Function responsible for updating results of Text Pane in Text Editor Dialog.
	 */
	public void updateTextPaneStats()
	{
		// initialize text
		textShell.getTxtInfo().setText("");

		StringBuilder textToBeSet = new StringBuilder();
		// setting language
		textToBeSet.append("Language is \"" + myText.Lan.engName + "\" (" + myText.Lan.isoName + ").\n");

		// if there're XmlNodes, add them too...
		String[] xmlNodes = myText.XmlNodes;
		if (xmlNodes != null)
		{
			textToBeSet.append("XML Text Nodes are: ");
			for (String tag : xmlNodes)
				textToBeSet.append(tag + " ");
			textToBeSet.append("\n");
		}
		else
		{
			// ...or add text delimiter
			if (myText.getDelimPattern().equals(""))
				textToBeSet.append("Text has no delimiter => processed as one TU\n");
			else if (myText.getDelimPattern().equals("\n"))
				textToBeSet.append("Text Delimiter is: \\n (NEWLINE)\n");
			else
				textToBeSet.append("Text Delimiter is: \"" + myText.getDelimPattern() + "\"\n");
		}

		// add other statistical data
		if (myText.nbOfTextUnits > 1)
			textToBeSet.append("Text contains " + myText.nbOfTextUnits + " Text Units (TUs).\n");

		if (myText.nbOfChars > 1 && myText.nbOfDiffChars > 1)
			textToBeSet.append(myText.nbOfChars + " characters " + "(" + myText.nbOfDiffChars + " diff), including\n");

		if (myText.nbOfLetters > 1 && myText.nbOfDiffLetters > 1)
			textToBeSet.append("  " + myText.nbOfLetters + " letters  (" + myText.nbOfDiffLetters + " diff),\n");

		if (myText.nbOfDigits > 1 && myText.nbOfDiffDigits > 1)
			textToBeSet.append("  " + myText.nbOfDigits + " digits  (" + myText.nbOfDiffDigits + " diff),\n");

		if (myText.nbOfBlanks > 1 && myText.nbOfDiffBlanks > 1)
			textToBeSet.append("  " + myText.nbOfBlanks + " blanks  (" + myText.nbOfDiffBlanks + " diff),\n");

		if (myText.nbOfDelimiters > 1 && myText.nbOfDiffDelimiters > 1)
			textToBeSet.append("  " + myText.nbOfDelimiters + " other delimiters  (" + myText.nbOfDiffDelimiters
					+ " diff),\n");

		if (myText.nbOfTokens > 1)
		{
			textToBeSet.append(myText.nbOfTokens + " tokens ");
			if (myText.nbOfDiffTokens > 1)
				textToBeSet.append(" (" + myText.nbOfDiffTokens + " diff) including:\n");
			if (myText.nbOfWords > 1)
				textToBeSet.append(myText.nbOfWords + " word forms\n");
			if (myText.nbOfDiffWords > 1)
				textToBeSet.append(" (" + myText.nbOfDiffWords + " diff)");
			if (myText.nbOfDigits > 1)
				textToBeSet.append(myText.nbOfDigits + " digits\n");
			if (myText.nbOfDiffDigits > 1)
				textToBeSet.append(" (" + myText.nbOfDiffDigits + " diff)");
			if (myText.nbOfDelimiters > 1)
				textToBeSet.append(myText.nbOfDelimiters + " delimiters\n");
			if (myText.nbOfDiffDelimiters > 1)
				textToBeSet.append(" (" + myText.nbOfDiffDelimiters + " diff)");
		}

		DefaultListModel model = ((DefaultListModel) textShell.getListOfResults().getModel());
		// clear double click options list and add default options
		model.clear();
		model.addElement(Constants.CHARACTERS_LIT);
		model.addElement(Constants.TOKENS_LIT);
		model.addElement(Constants.DIGRAMS_LIT);

		// set the counter label
		if (myText.mft != null && myText.nbOfTextUnits > 0)
		{
			textShell.getLblnTus().setEnabled(true);
			textShell.getLblnTus().setText("/ " + textShell.getText().nbOfTextUnits + " TUs");
		}
		// annotations
		if (myText.annotations != null && myText.mft != null)
		{
			textToBeSet.append("Text contains ");
			textToBeSet.append(myText.mft.nbOfTransitions + " annotations ");
			textToBeSet.append("(" + myText.annotations.size() + " different)\n");
			// TODO: demoversion condition?!
			if (myText.annotations.size() < 1000)
				model.addElement(Constants.ANNOTATIONS_LIT);
			if (myText.hUnknowns != null)
				model.addElement(Constants.UNKNOWNS_LIT);
			model.addElement(Constants.AMBIGUITIES_LIT);
			model.addElement(Constants.UNAMBIGUOUS_WORDS_LIT);
			textShell.getChckbxShowTextAnnotation().setEnabled(true);
		}
		else
			textShell.getChckbxShowTextAnnotation().setEnabled(false);

		// add resources of the text
		if (myText.listOfResources != null)
		{
			textToBeSet.append("Linguistic Resources applied to the text:\n");
			for (Object res : myText.listOfResources)
				textToBeSet.append(res.toString() + " ");
			textToBeSet.append("\n");
		}

		textShell.getTxtInfo().setText(textToBeSet.toString());
	}

	/**
	 * Suite function of import text.
	 * 
	 * @param lan
	 *            - language of text to be set
	 * @param language
	 *            - language name
	 * @param delimiter
	 *            - delimiter sign
	 * @param xmlTags
	 *            - chosen XML tags
	 * @param encodingType
	 *            - encoding type number
	 * @param encodingCode
	 *            - adequate encoding code
	 * @param encodingName
	 *            - adequate encoding name
	 */

	public void suiteFunction(Language lan, String language, String delimiter, String[] xmlTags, int encodingType,
			String encodingCode, String encodingName)
	{
		// create new Ntext with given parameters
		myText = new Ntext(language, delimiter, xmlTags);

		// load text
		try
		{
			myText.buffer = TextIO.loadText(fileToBeOpenedOrImported.getAbsolutePath(), encodingType, encodingCode,
					encodingName, lan.chartable);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE
					+ fileToBeOpenedOrImported.getName(), Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		catch (BadLocationException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE
					+ fileToBeOpenedOrImported.getName(), Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		if (myText.buffer == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_IMPORT_TEXT_FILE
					+ fileToBeOpenedOrImported.getName(), Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		// initialize engine
		startNoojEngine();

		String errorMessage = "";
		// if flag for XML text nodes is set to true, delimit XML...
		if (ImportTextActionListener.xmlButtonChecked)
		{
			errorMessage = engine.delimitXmlTextUnitsAndImportXmlTags(null, myText);
			if (!errorMessage.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.NOOJ_ERROR + " "
						+ errorMessage, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		// ...and if not, just delimit the text
		else
		{
			errorMessage = engine.delimitTextUnits(myText);
			if (!errorMessage.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.NOOJ_ERROR + " "
						+ errorMessage, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// open the text editor window
			textShell = new TextEditorShell(null, myText, "Import " + fileToBeOpenedOrImported.getName(),
					myText.getDelimPattern(), false);

			// Controller must be connected with this new formed shell!
			textShell.setTextController(this);
			textShell.refreshListenersAndAdapters();

			Launcher.getDesktopPane().add(textShell);
			textShell.setVisible(true);

			modify();

			rtbTextUpdate(true);
			updateTextPaneStats();

			return;
		}

		// open the text editor window
		textShell = new TextEditorShell(null, myText, "Import " + fileToBeOpenedOrImported.getName(),
				myText.getDelimPattern(), false);

		// Controller must be connected with this new formed shell!
		textShell.setTextController(this);
		textShell.refreshListenersAndAdapters();

		Launcher.getDesktopPane().add(textShell);
		textShell.setVisible(true);
	}

	/**
	 * Function responsible for textual linguistic analysis.
	 */
	public void linguisticAnalysis()
	{
		// if text was edited, remove edited parts
		if (textWasEdited)
		{
			textWasEdited = false;
			myText.mft = null;
			myText.buffer = null;
		}

		if (myText.buffer == null || myText.buffer.equals(""))
			myText.buffer = paneOfTextEditor.getText().replace("\r", "");

		// BackgroundWorking and multithreading are done in TextLinguisticAnalysisActionListener

		boolean thereAreNoResources = false;
		if (Launcher.preferences.ldic.get(myText.Lan.isoName) == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NO_SELECTED_LEX_RESOURCE1
					+ myText.Lan.isoName + Constants.NO_SELECTED_LEX_RESOURCE2, Constants.NOOJ_APPLICATION_NAME,
					JOptionPane.INFORMATION_MESSAGE);
			thereAreNoResources = true;
			return;
		}

		// Reload linguistic resources
		RefObject<Language> mytextLanRef = new RefObject<Language>(myText.Lan);
		engine = new Engine(mytextLanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		myText.Lan = mytextLanRef.argvalue;
		RefObject<String> errMessage = new RefObject<String>("");
		try
		{
			if (!engine.loadResources(Launcher.preferences.ldic.get(myText.Lan.isoName),
					Launcher.preferences.lsyn.get(myText.Lan.isoName), true, errMessage))
			{
				if (!thereAreNoResources)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LEXICAL_RESOURCE
							+ myText.Lan.isoName, errMessage.argvalue, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
					+ myText.Lan.isoName, errMessage.argvalue, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_LINGUISTIC_RESOURCE
					+ myText.Lan.isoName, errMessage.argvalue, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Compute list of linguistic resources
		myText.listOfResources = new ArrayList<String>();
		ArrayList<String> res = Launcher.preferences.ldic.get(myText.Lan.isoName);

		if (res != null)
		{
			for (String fullName : res)
			{
				String fName = FilenameUtils.getName(fullName);
				String prio = fName.substring(0, 2);
				fName = fName.substring(2);

				myText.listOfResources.add(fName + "(" + prio + ")");
			}
		}

		res = Launcher.preferences.lsyn.get(myText.Lan.isoName);
		if (res != null)
		{
			for (String fullName : res)
			{
				String fName = FilenameUtils.getName(fullName);
				String prio = fName.substring(0, 2);
				fName = fName.substring(2);

				myText.listOfResources.add(fName + "(" + prio + ")");
			}
		}

		if (myText.mft == null)
		{
			// delimit text units
		if (Launcher.multithread)
			{
				if (Launcher.backgroundWorker.isCancellationPending())
				{
					myText.mft = null;
					return;
				}
				Launcher.progressMessage = "Splitting text into text units...";
				Launcher.progressPercentage = 0;
				if (Launcher.backgroundWorker.isBusy())
					Launcher.backgroundWorker.reportProgress(0);
			}

			if (myText.XmlNodes == null) // RAW text
			{
				myText.mft = engine.delimit(myText);
				myText.annotations = new ArrayList<Object>();
				myText.hLexemes = new HashMap<String, Integer>();
			}
			else
				engine.delimitXmlTextUnitsAndImportXmlTags(null, myText);

			if (myText.mft == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SPLIT_TEXT_INTO_TU,
						errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else
		{
			if (myText.XmlNodes == null)
			{
				myText.mft.resetTransitions();
				myText.annotations = new ArrayList<Object>();
				myText.hLexemes = new HashMap<String, Integer>();
			}
		}

		// lexical analysis

		myText.hUnknowns = new HashMap<String, Integer>();

		if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				myText.annotations = null;
				myText.hLexemes = null;
				myText.hUnknowns = null;
				return;
			}
			Launcher.progressMessage = "Lexical Analysis...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		HashMap<String, ArrayList<String>> simpleWordCache = new HashMap<String, ArrayList<String>>();
		if (!engine.tokenize(null, myText, myText.annotations, simpleWordCache, errMessage))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_TOKENIZER_ERROR,
					errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
			myText.annotations = null;
			myText.hLexemes = null;
			myText.hUnknowns = null;
			return;
		}
		simpleWordCache = null;

		// syntactic parsing
		if (myText.XmlNodes == null)
			myText.hPhrases = new HashMap<String, Integer>();

		// syntactic parsing
	if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				myText.hPhrases = null;
				
				return;
			}
			Launcher.progressMessage = "Apply dictionary/grammars...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		// first apply dictionary/grammar pairs

		applySyntax(0, errMessage);

		// syntactic parsing
	if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				myText.hPhrases = null;
				
				return;
			}
			Launcher.progressMessage = "Apply syntactic grammars...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		applySyntax(1, errMessage);

		// enable menu
		reactivateOps();
	}

	/**
	 * Function for hiding dynamic text menu.
	 */
	public void desactivateOps()
	{
		
		Launcher.getMenuBar().repaint();
	}

	/**
	 * Function for displaying dynamic text menu.
	 */
	public void reactivateOps()
	{
		
		Launcher.getMenuBar().repaint();
	}

	/**
	 * Help function for main linguistic analysis function. Applies grammars.
	 * 
	 * @param startingPoint
	 *            - starting point of applying
	 * @param errMessage
	 *            - generated error message
	 */
	private void applySyntax(int startingPoint, RefObject<String> errMessage)
	{
		if (engine.synGrms != null && engine.synGrms.size() > 0)
		{
			boolean applyres = false;
			try
			{
				applyres = engine.applyAllGrammars(null, myText, myText.annotations, startingPoint, errMessage);
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_APPLY_GRAMMARS_ERROR,
						errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_APPLY_GRAMMARS_ERROR,
						errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
			}
			if (!applyres && errMessage != null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_SINT_PARSING_ERROR,
						errMessage.argvalue, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!applyres)
			{
				myText.hPhrases = null;
				return;
			}
			if (startingPoint == 0)
				myText.cleanupBadAnnotations(myText.annotations);
		}
	}

	/**
	 * Function for reseting all flags after reopening a text editor window.
	 */
	public void resetShellText()
	{
		textIsBeingColored = true;
		textInfo = textShell.getTxtInfo();

		if (corpusController != null && corpusController.getShell() != null)
		{
			corpus = corpusController.getCorpus();

			textShell.getListOfResults().setVisible(false);
			textShell.getScrollList().setVisible(false);
			textInfo.setVisible(false);
			engine = corpusController.getEngine();
		}
		else
		{
			engine = new Engine(new RefObject<Language>(myText.Lan), Paths.applicationDir, Paths.docDir,
					Paths.projectDir, Launcher.projectMode, Launcher.preferences, Launcher.backgroundWorking,
					Launcher.backgroundWorker);
		}

		NooJRightToLeft = myText.Lan.rightToLeft;
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, (NooJRightToLeft ? StyleConstants.ALIGN_RIGHT
				: StyleConstants.ALIGN_LEFT));
		StyleConstants.setFontFamily(attributeSet, Launcher.preferences.TFont.getFamily());
		StyleConstants.setFontSize(attributeSet, Launcher.preferences.TFont.getSize());

		paneOfTextEditor.getStyledDocument().setParagraphAttributes(0, myText.buffer.length(), attributeSet, true);

		if (textInfo.isVisible())
		{
			updateTextPaneStats();
			rtbTextUpdate(false);
		}
		
		textWasEdited = false;
	}

	/**
	 * Function for setting "modified" attribute to text that has been opened.
	 */
	public void modify()
	{
		modified = true;

		// set window title
		if (textName == null)
			textShell.setTitle("Untitled [Modified]");
		else
			textShell.setTitle(textName + " [Modified]");

		// deselect annotation check box
		if (textShell.getChckbxShowTextAnnotation().isSelected())
			textShell.getChckbxShowTextAnnotation().setSelected(false);

		annotationsHistoric = new ArrayList<Object>();
	}

	private void modifyKeepTasDisplayed()
	{
		modified = true;

		// set window title
		if (textName == null)
			textShell.setTitle("Untitled [Modified]");
		else
			textShell.setTitle(textName + " [Modified]");

		if (!textShell.isNewText())
		{
			CustomJSpinner spinner = textShell.getSpinner();
			spinner.setCustomValue((Integer) spinner.getValue());
		}
	}

	public void saveText()
	{
		if (this.fileToBeOpenedOrImported == null)
			saveAsText();
		else
		{
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
				save("");
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}
	}

	public void saveAsText()
	{
		String languageName = myText.Lan.isoName;

		String currentDirToBeSetPath = Paths.docDir + System.getProperty("file.separator") + languageName
				+ System.getProperty("file.separator") + Constants.PROJECTS_PATH;
		File currentDirToBeSet = new File(currentDirToBeSetPath);
		JFileChooser saveTextChooser = Launcher.getSaveTextChooser();
		saveTextChooser.setCurrentDirectory(currentDirToBeSet);

		if (saveTextChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
			return;

		File selectedFile = saveTextChooser.getSelectedFile();
		fileToBeOpenedOrImported = selectedFile;
		textName = fileToBeOpenedOrImported.getName();

		String parentPath = saveTextChooser.getCurrentDirectory().getAbsolutePath();
		String inputFileName = FilenameUtils.removeExtension(selectedFile.getName()) + "." + Constants.JNOT_EXTENSION;
		String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

		File newFile = new File(pathOfInputFile);

		if (newFile.exists())
		{
			int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
					+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, null, null);
			if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
				return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			save(pathOfInputFile);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	private void save(String path)
	{
		if (path.equals(""))
		{
			String fileName = fileToBeOpenedOrImported.getName();
			String jnotFileName = FilenameUtils.removeExtension(fileName) + "." + Constants.JNOT_EXTENSION;
			String parentPath = fileToBeOpenedOrImported.getParent();
			path = parentPath + System.getProperty("file.separator") + jnotFileName;
		}

		if (fileToBeOpenedOrImported == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SAVE_TEXT_MESSAGE,
					Constants.CANNOT_SAVE_TEXT_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if (fileToBeOpenedOrImported != null && path.equals(fileToBeOpenedOrImported.getAbsolutePath()))
			fileToBeOpenedOrImported.delete();

		textShell.getText().buffer = textShell.getTextPane().getText();
		save(path, false);
	}

	private void save(String fullName, boolean forNooJ)
	{
		if (!forNooJ)
		{
			// WARNING IF FILENAME STARTS WITH "_"
			String fNameNoExt = FilenameUtils.removeExtension(fullName);
			if (fNameNoExt.charAt(0) == '_')
			{
				int answer = JOptionPane
						.showConfirmDialog(Launcher.getDesktopPane(), Constants.FILENAME_PREFIX_WARNING,
								Constants.NOOJ_PROTECTED_RESOURCE, JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.NO_OPTION)
					return;
			}

			// MANAGE MULTIPLE BACKUPS
			try
			{
				Utilities.savePreviousVersion(fullName, Launcher.preferences.multiplebackups);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_SAVE_PREVIOUS_VERSION, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (this.textWasEdited)
		{
			myText.buffer = textShell.getTextPane().getText().replace("\r", "");
			myText.mft = null;
			this.textWasEdited = false;
		}

		
		try
		{
			myText.save(fullName);

			this.listOfColors = null;
			this.absoluteBeginAddresses = null;
			this.absoluteEndAddresses = null;

			this.modified = false;
			if (!forNooJ)
				textName = fullName;
			textShell.setTitle(FilenameUtils.getName(textName));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_SAVE_TEXT_MESSAGE + ": " + e.getMessage(), Constants.NOOJ_APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void saveTextForNooJ()
	{
		String languageName = myText.Lan.isoName;
		String dirName = Paths.applicationDir + "resources" + System.getProperty("file.separator") + "initial"
				+ System.getProperty("file.separator") + languageName + System.getProperty("file.separator")
				+ "Projects";
		String fileName = fileToBeOpenedOrImported.getName();
		String nooJName = dirName + System.getProperty("file.separator") + fileName;

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			File parent = new File(dirName);

			if (!parent.exists())
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_NO_FILE_PATH,
						Constants.ERROR_MESSAGE_TITLE_TEXT_SAVE, JOptionPane.ERROR_MESSAGE);
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

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File " + nooJName + " has been updated!",
				Constants.NOOJ_UPDATE_MESSAGE_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	public void modifyTextFont(String fontFamily, int fontSize)
	{
		NooJRightToLeft = myText.Lan.rightToLeft;

		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, (NooJRightToLeft ? StyleConstants.ALIGN_RIGHT
				: StyleConstants.ALIGN_LEFT));
		StyleConstants.setFontFamily(attributeSet, fontFamily);
		StyleConstants.setFontSize(attributeSet, fontSize);

		paneOfTextEditor.getStyledDocument().setParagraphAttributes(0, myText.buffer.length(), attributeSet, false);

		// Repainting component
		paneOfTextEditor.repaint();
	}

	// getters and setters
	public boolean isCurrentTextUnitIsBlack()
	{
		return currentTextUnitIsBlack;
	}

	public void setCurrentTextUnitIsBlack(boolean currentTextUnitIsBlack)
	{
		this.currentTextUnitIsBlack = currentTextUnitIsBlack;
	}

	public File getFileToBeOpenedOrImported()
	{
		return fileToBeOpenedOrImported;
	}

	public Ntext getMyText()
	{
		return myText;
	}

	public Engine getEngine()
	{
		return engine;
	}

	public TextEditorShell getTextShell()
	{
		return textShell;
	}

	public void setFileToBeOpenedOrImported(File fileToBeOpenedOrImported)
	{
		this.fileToBeOpenedOrImported = fileToBeOpenedOrImported;
	}

	public boolean isModified()
	{
		return modified;
	}

	public boolean isColored()
	{
		return colored;
	}

	public void setColored(boolean colored)
	{
		this.colored = colored;
	}

	public boolean isNooJRightToLeft()
	{
		return NooJRightToLeft;
	}

	public void fillInVocabulary(DictionaryEditorShell annotationsEditor)
	{
		if (!myText.updateAnnotationsForText())
			return;

		annotationsEditor.getController().initLoad(myText.Lan.isoName);
		StringBuilder sb = new StringBuilder();

		HashMap<String, String> frozenExpressions = new HashMap<String, String>();

		for (Object lexemeObj : myText.annotations)
		{
			String lexeme = (String) lexemeObj;

			if (lexeme == null)
				continue;

			String entry = "", lemma = "", info = "", category = "";
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> infoRef = new RefObject<String>(info);

			if (!Dic.parseDELAF(lexeme, entryRef, lemmaRef, infoRef))
				continue;

			info = infoRef.argvalue;
			lemma = lemmaRef.argvalue;
			entry = entryRef.argvalue;

			if (lemma.equals("SYNTAX"))
			{
				entry = Dic.protectComma(entry);
				sb.append(entry + "," + info + "\n");
			}
			else
			{
				String[] features = null;

				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String[]> featuresRef = new RefObject<String[]>(features);

				if (!Dic.parseDELAFFeatureArray(lexeme, entryRef, lemmaRef, categoryRef, featuresRef))
					continue;

				features = featuresRef.argvalue;
				category = categoryRef.argvalue;

				String newFeatures = Dic.getRidOfSpecialFeatures(features);
				entry = Dic.protectComma(entry);
				lemma = Dic.protectComma(lemma);
				if (entry.equals(lemma))
					sb.append(entry + "," + category + newFeatures + "\n");
				else
					sb.append(entry + "," + lemma + "," + category + newFeatures + "\n");
			}
		}

		if (frozenExpressions.size() > 0)
		{
			for (Object lexeme : frozenExpressions.values())
			{
				// TODO check this frozen expressions!
				String lexemeString = lexeme.toString();

				String entry = "", lemma = "", info = "", category = "";
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
					entry = Dic.protectComma(entry);
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

		annotationsEditor.getTextPane().setText(sb.toString());

	

		annotationsEditor.getController().sortDictionary(true);

	}

	public void fillInUnknowns(DictionaryEditorShell unknownsEditor)
	{
		String lang = myText.Lan.isoName;
		unknownsEditor.getController().initLoad(lang);

		StringBuilder builder = new StringBuilder();
		for (Object tokenObj : myText.annotations)
		{
			String token = (String) tokenObj;

			if (token == null)
				continue;

			String entry = "", lemma = "", category = "";
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

	public void computeAlphabet()
	{
		engine.computeAlphabet(myText);
	}

	public void computeTokens()
	{
		myText.hTokens = new HashMap<String, Indexkey>();
		engine.computeTokens(null, myText);

	}

	public void computeDigrams()
	{
		myText.hDigrams = new HashMap<String, Indexkey>();
		if (myText.mft != null)
			engine.computeDigrams(null, myText);
	}

	public void computeAmbiguities(boolean isAmbig)
	{
		if (isAmbig)
		{
			myText.hAmbiguities = new HashMap<String, ArrayList<Object>>();
			RefObject<HashMap<String, ArrayList<Object>>> refHash = new RefObject<HashMap<String, ArrayList<Object>>>(
					myText.hAmbiguities);
			engine.computeAmbiguities(null, null, myText, myText.annotations, refHash);
			myText.hAmbiguities = refHash.argvalue;
		}
		else
		{
			myText.hUnambiguities = new HashMap<String, ArrayList<Object>>();
			RefObject<HashMap<String, ArrayList<Object>>> refHash = new RefObject<HashMap<String, ArrayList<Object>>>(
					myText.hUnambiguities);
			engine.computeUnambiguities(null, null, myText, myText.annotations, refHash);
			myText.hUnambiguities = refHash.argvalue;
		}
	}

	/**
	 * Function locates ordinal number of text unit with index of its beginning.
	 * 
	 * @param beginAddressOfTU
	 *            - beginning address of text unit in a text
	 * @return - ordinal number of text unit
	 */

	public int locateTextUnit(int beginAddressOfTU)
	{
		int tuNb;
		for (tuNb = 1; tuNb < myText.nbOfTextUnits
				&& beginAddressOfTU > myText.mft.tuAddresses[tuNb] + myText.mft.tuLengths[tuNb]; tuNb++)
			;
		if (tuNb <= myText.nbOfTextUnits)
			return tuNb;
		else
			return -1;
	}

	// TAS related functions
	public void setTuGraph(int currentUnit)
	{
		tuGraph = myText.mft.getTuGraph(currentUnit, tuGraph);
		tuGraph.setNeedToBeComputed(true);
	}

	public void showAndScrollTas(int rtbPosition)
	{
		String rtbText = textShell.getTextPane().getText();
		if (rtbPosition >= 0 && rtbPosition < rtbText.length())
		{
			if (Language.isLetter(rtbText.charAt(rtbPosition)) && (!myText.Lan.asianTokenizer))
			{
				while (rtbPosition >= 0 && Language.isLetter(rtbText.charAt(rtbPosition)))
					rtbPosition--;
				rtbPosition++;
			}

			int currentUnit = (Integer) textShell.getSpinner().getValue();
			selectAllAnnotationsAtPosition = rtbPosition - myText.mft.tuAddresses[currentUnit];
		}
		

		textShell.getHiddenPanel().setSelectAllAnnotationsAtPosition(selectAllAnnotationsAtPosition);

		if (tuGraph.needToBeComputed)
		{
			Graphics g = textShell.getHiddenPanel().getGraphics();
			Graphics2D g2d = (Graphics2D) g;
			tuGraph.computeDrawing(myText.annotations, textShell.getHiddenPanel(), g2d);
		}
		tuGraph.computeCurrentFrameX(selectAllAnnotationsAtPosition, textShell.getHiddenPanel());
		tuGraph.computeXYcoord(textShell.getHiddenPanel(), selectAllAnnotationsAtPosition, -1);

		// Adding the 'button'
		JButton test = new JButton(Integer.toString((int) selectAllAnnotationsAtPosition));
		test.setLocation(new Point(tuGraph.xCoord, 0));
		test.setUI(new BasicButtonUI()
		{
			public void paint(Graphics g, JComponent component)
			{
				int thicknessOfBorders = 3;

				// 2D graphics for anti aliasing
				Graphics2D g2D = (Graphics2D) g;

				JButton myButton = (JButton) component;

				// set font (bold)
				Font buttonFont = new Font(myButton.getFont().getName(), Font.BOLD, myButton.getFont().getSize());
				myButton.setFont(buttonFont);

				myButton.setBorder(BorderFactory.createLineBorder(Color.RED, thicknessOfBorders));
				g2D.setColor(Color.WHITE);

				// set background color of a button
				g2D.fillRoundRect(thicknessOfBorders, thicknessOfBorders, component.getWidth() - thicknessOfBorders,
						component.getHeight() - thicknessOfBorders, thicknessOfBorders, thicknessOfBorders);

				super.paint(g2D, component);
			}
		});
		test.setSize(60, 20);

		textShell.getHiddenPanel().removeAll();
		textShell.getHiddenPanel().add(test);

		// Scroll mftpanel to selected annotations
		JScrollPane scroll = textShell.getPanelScrollPane();
		scroll.getVerticalScrollBar().setValue(0);
		scroll.getHorizontalScrollBar().setValue(tuGraph.xCoord);

		textShell.getTextPane().getCaret().setVisible(true);

		// Repainting components
		textShell.invalidate();
		textShell.validate();
		textShell.repaint();
	}

	public void textHasJustBeenEdited()
	{
		// return if text was edited already
		if (this.textWasEdited)
			return;

		this.textWasEdited = true;

		// set modified tags
		modify();

		// disable check box "Show Annotations", and if it's selected, deselect it
		JCheckBox cbShowAnnottation = textShell.getChckbxShowTextAnnotation();
		if (cbShowAnnottation.isSelected())
			cbShowAnnottation.setSelected(false);
		cbShowAnnottation.setEnabled(false);

		// reset all values of linguistic analysis
		// HACK! nbOfTextUnits reseting is being denied to avoid bug with spinner & UnitSelectionListener after
		// replacing the text
		myText.nbOfChars = myText.nbOfDiffChars = -1;
		myText.nbOfLetters = myText.nbOfDigits = myText.nbOfBlanks = myText.nbOfDelimiters = myText.nbOfTokens = -1;
		myText.listOfResources = null;

		this.setListOfColors(null);
		this.setAbsoluteBeginAddresses(null);
		this.setAbsoluteEndAddresses(null);

		TextEditorShell textEditorShell = this.getTextShell();
		ConcordanceShellController concordanceController = this.getConcordanceController();

		if (textEditorShell.getAlphabetDialog() != null)
			textEditorShell.getAlphabetDialog().dispose();
		if (textEditorShell.getTokensDialog() != null)
			textEditorShell.getTokensDialog().dispose();
		if (textEditorShell.getAmbiguitiesDialog() != null)
			textEditorShell.getAmbiguitiesDialog().dispose();
		if (textEditorShell.getUnAmbiguitiesDialog() != null)
			textEditorShell.getUnAmbiguitiesDialog().dispose();

		if (concordanceController != null)
		{
			concordanceController.getConcordanceShell().dispose();
			concordanceController = null;
		}

		if (textEditorShell.getLocateDialog() != null)
			textEditorShell.getLocateDialog().dispose();
		if (textEditorShell.getExportXmlDialog() != null)
			textEditorShell.getExportXmlDialog().dispose();

		// finally, update stats
		updateTextPaneStats();
	}

	// Add/remove annotations
	public void removeAnnotation()
	{
		if (!textShell.getTextPane().isVisible())
			return;
		if (tuGraph == null)
			return;
		if (tuGraph.selectedAnnotation_tokenId == -1)
			return;

		RefObject<Double> refRelEndAddress = new RefObject<Double>(0.0);
		int tuNbCurrent = (Integer) textShell.getSpinner().getValue();
		if (myText.mft.removeTransition(tuNbCurrent, tuGraph.selectedAnnotation_relBegAddress,
				tuGraph.selectedAnnotation_tokenId, refRelEndAddress))
		{
			annotationsHistoric.add(tuNbCurrent);
			annotationsHistoric.add(tuGraph.selectedAnnotation_relBegAddress);
			annotationsHistoric.add(tuGraph.selectedAnnotation_tokenId);
			annotationsHistoric.add(refRelEndAddress.argvalue);
		}

		tuGraph = myText.mft.getTuGraph(tuNbCurrent, tuGraph);
		if (tuGraph != null)
			tuGraph.needToBeComputed = true;

		updateTextPaneStats();
		modifyKeepTasDisplayed();

		// Repainting components
		textShell.invalidate();
		textShell.validate();
		textShell.repaint();
	}

	private void scrollToAnnotation(double relBegAddress)
	{
		int tuNbCurrent = (Integer) textShell.getSpinner().getValue();
		int rtbPosition = (int) relBegAddress + myText.mft.tuAddresses[tuNbCurrent];

		textShell.getTextPane().setCaretPosition(rtbPosition);

		if (tuGraph == null)
			return;

		tuGraph.needToBeComputed = true;

		if (textShell.getChckbxShowTextAnnotation().isSelected())
		{
			String rtbText = textShell.getTextPane().getText();
			if (Language.isLetter(rtbText.charAt(rtbPosition)))
			{
				while (rtbPosition >= 0 && Language.isLetter(rtbText.charAt(rtbPosition)))
					rtbPosition--;
				rtbPosition++;
			}

			selectAllAnnotationsAtPosition = relBegAddress;
			textShell.getHiddenPanel().setSelectAllAnnotationsAtPosition(selectAllAnnotationsAtPosition);

			if (tuGraph.needToBeComputed)
			{
				Graphics g = textShell.getHiddenPanel().getGraphics();
				Graphics2D g2d = (Graphics2D) g;
				tuGraph.computeDrawing(myText.annotations, textShell.getHiddenPanel(), g2d);
			}
			tuGraph.computeCurrentFrameX(selectAllAnnotationsAtPosition, textShell.getHiddenPanel());
			tuGraph.computeXYcoord(textShell.getHiddenPanel(), selectAllAnnotationsAtPosition, -1);

			// Adding the 'button'
			JButton test = new JButton(Integer.toString((int) selectAllAnnotationsAtPosition));
			test.setLocation(new Point(tuGraph.xCoord, 0));
			test.setUI(new BasicButtonUI()
			{
				public void paint(Graphics g, JComponent component)
				{
					int thicknessOfBorders = 3;

					// 2D graphics for anti aliasing
					Graphics2D g2D = (Graphics2D) g;

					JButton myButton = (JButton) component;

					// set font (bold)
					Font buttonFont = new Font(myButton.getFont().getName(), Font.BOLD, myButton.getFont().getSize());
					myButton.setFont(buttonFont);

					myButton.setBorder(BorderFactory.createLineBorder(Color.RED, thicknessOfBorders));
					g2D.setColor(Color.WHITE);

					// set background color of a button
					g2D.fillRoundRect(thicknessOfBorders, thicknessOfBorders,
							component.getWidth() - thicknessOfBorders, component.getHeight() - thicknessOfBorders,
							thicknessOfBorders, thicknessOfBorders);

					super.paint(g2D, component);
				}
			});
			test.setMinimumSize(new Dimension(60, 20));

			textShell.getHiddenPanel().removeAll();
			textShell.getHiddenPanel().add(test);

			// Scroll mftpanel to selected annotations
			JScrollPane scroll = textShell.getPanelScrollPane();
			scroll.getVerticalScrollBar().setValue(0);
			scroll.getHorizontalScrollBar().setValue(tuGraph.xCoord);

			// Repainting components
			textShell.invalidate();
			textShell.validate();
			textShell.repaint();
		}
	}

	public void addAnnotation()
	{
		if (annotationsHistoric.size() < 4)
			return;

		int i = annotationsHistoric.size() - 4;

		int tuNb = (Integer) annotationsHistoric.get(i);
		double relBegAddress = (Double) annotationsHistoric.get(i + 1);
		int tokenId = (Integer) annotationsHistoric.get(i + 2);
		double relEndAddress = (Double) annotationsHistoric.get(i + 3);

		myText.mft.addTransition(tuNb, relBegAddress, tokenId, relEndAddress);

		annotationsHistoric.remove(i);
		annotationsHistoric.remove(i);
		annotationsHistoric.remove(i);
		annotationsHistoric.remove(i);

		int tuNbCurrent = (Integer) textShell.getSpinner().getValue();
		if (tuNb != tuNbCurrent)
		{
			tuNbCurrent = tuNb;
			
			textShell.getSpinner().setValue(tuNb);
			rtbTextUpdate(false);
		}

		tuGraph = myText.mft.getTuGraph(tuNbCurrent, tuGraph);
		if (tuGraph != null)
			tuGraph.needToBeComputed = true;

		updateTextPaneStats();
		modifyKeepTasDisplayed();

		scrollToAnnotation(relBegAddress);
	}

	public void linguisticAnalysisForNewText(JTextPane textPane, TextEditorShell textShell)
	{
		int numberOfTU = this.getMyText().nbOfTextUnits;
		final CustomJSpinner spinner = new CustomJSpinner(textShell, numberOfTU);
		StyledDocument doc = textPane.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);

		// if linguistic analysis is being performed on a new text...
		if (textShell.isNewText())
		{
			// get container of a text shell, total number of text units
			Container contentPane = textShell.getContentPane();

			JLabel labelTU = textShell.getLblnTus();

			// remove old spinner and label from GUI and replace them with a new ones
			contentPane.remove(textShell.getSpinner());
			// catch "Enter" key event for a custom value
			final JFormattedTextField jtf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
			jtf.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						String text = jtf.getText();
						// show error dialogs if custom value is not regular
						try
						{
							Integer newValue = Integer.valueOf(text);

							if (newValue > spinner.getUpperLimit() || newValue < 1)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
										Constants.NOOJ_NUMBER_RANGE_INPUT_MESSAGE, Constants.NOOJ_APPLICATION_NAME
												+ " error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							spinner.setCustomValue(newValue);
						}
						catch (NumberFormatException ex)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.NOOJ_NUMBER_INPUT_MESSAGE, Constants.NOOJ_APPLICATION_NAME + " error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});

			textShell.setSpinner(spinner);
			textShell.getSpinner().setEnabled(true);

			contentPane.remove(labelTU);
			contentPane.add(spinner, "flowx, cell 0 0, alignx left, aligny top");
			contentPane.add(labelTU, "cell 0 0, alignx center, aligny top");
			labelTU.setText("/ " + numberOfTU);
			textShell.revalidate();
			textShell.repaint();

			// add unit selection listener
			UnitSelectionListener tuListener = new UnitSelectionListener(this, textPane);
			textPane.addCaretListener(tuListener);
			textShell.setUnitSelectionListener(tuListener);
			textPane.setCaretPosition(0);
			doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);
			spinner.setCustomValue(1);
		}
	}

	public ConcordanceShellController getConcordanceController()
	{
		return concordanceController;
	}

	public void setConcordanceController(ConcordanceShellController concordanceController)
	{
		this.concordanceController = concordanceController;
	}

	public List<Color> getListOfColors()
	{
		return listOfColors;
	}

	public void setListOfColors(List<Color> listOfColors)
	{
		this.listOfColors = listOfColors;
	}

	public List<Integer> getAbsoluteBeginAddresses()
	{
		return absoluteBeginAddresses;
	}

	public void setAbsoluteBeginAddresses(List<Integer> absoluteBeginAddresses)
	{
		this.absoluteBeginAddresses = absoluteBeginAddresses;
	}

	public List<Integer> getAbsoluteEndAddresses()
	{
		return absoluteEndAddresses;
	}

	public void setAbsoluteEndAddresses(List<Integer> absoluteEndAddresses)
	{
		this.absoluteEndAddresses = absoluteEndAddresses;
	}

	public TuGraph getTuGraph()
	{
		return tuGraph;
	}

	public double getSelectAllAnnotationsAtPosition()
	{
		return selectAllAnnotationsAtPosition;
	}

	public void setSelectAllAnnotationsAtPosition(double selectAllAnnotationsAtPosition)
	{
		this.selectAllAnnotationsAtPosition = selectAllAnnotationsAtPosition;
	}

	public void setMyText(Ntext myText)
	{
		this.myText = myText;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
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

	public String getTextName()
	{
		return textName;
	}

	public boolean isTextWasEdited()
	{
		return textWasEdited;
	}

	public void setTextWasEdited(boolean textWasEdited)
	{
		this.textWasEdited = textWasEdited;
	}

	public boolean isTextIsBeingColored()
	{
		return textIsBeingColored;
	}

	public void setTextIsBeingColored(boolean textIsBeingColored)
	{
		this.textIsBeingColored = textIsBeingColored;
	}
}