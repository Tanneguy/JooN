package net.nooj4nlp.controller.TextEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;

import net.nooj4nlp.controller.CorpusEditorShell.AddActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.TextCorpusDialog;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * Action listener for OK button of TextCorpusDialog - case text, sub-case importing text.
 */

public class ImportTextActionListener implements ActionListener
{
	// dialog window
	private TextCorpusDialog dialog;

	// list of languages
	private JList languages;

	// flag to determinate if radio button "XML Text Nodes" is selected when "OK" is clicked
	public static boolean xmlButtonChecked = false;

	private boolean corpus;
	private File fileToBeImported;

	/**
	 * Constructor.
	 * 
	 * @param listOfLanguages
	 *            - list of languages to select
	 * @param dialog
	 *            - dialog window
	 * @param corpus
	 *            - true if this is called from corpus context, false if this is called from text context
	 */
	public ImportTextActionListener(JList listOfLanguages, TextCorpusDialog dialog, boolean corpus,
			File fileToBeImported)
	{
		this.dialog = dialog;
		this.languages = listOfLanguages;

		this.corpus = corpus;
		this.fileToBeImported = fileToBeImported;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// input and chosen details needs to be saved
		if (TextCorpusDialog.getRdbtnPerlRegExpr().isSelected())
		{
			JComboBox comboPerl = TextCorpusDialog.getComboPerl();
			int selectedIndex = comboPerl.getSelectedIndex();
			List<String> perlList = Launcher.getRegexMemoryList();

			// if it's the case of an input text...
			if (selectedIndex == -1)
			{
				// ...select the last one and add it to the list
				selectedIndex = comboPerl.getItemCount();
				perlList.add(comboPerl.getSelectedItem().toString());
				Launcher.setRegexMemoryList(perlList);
			}

			// remember radio button and combo index that was last chosen
			Launcher.setCorpusTextRadioButtonSelectionMemory(3);
			Launcher.setRegexMemoryIndex(selectedIndex);
		}
		else if (TextCorpusDialog.getRdbtnXmlTextNodes().isSelected())
		{
			JComboBox comboXml = TextCorpusDialog.getComboXml();
			int selectedIndex = comboXml.getSelectedIndex();
			List<String> xmlList = Launcher.getXmlMemoryList();

			// if it's the case of an input text...
			if (selectedIndex == -1)
			{
				// ...select the last one and add it to the list
				selectedIndex = comboXml.getItemCount();
				xmlList.add(comboXml.getSelectedItem().toString());
				Launcher.setXmlMemoryList(xmlList);
			}

			// remember radio button and combo index that was last chosen
			Launcher.setCorpusTextRadioButtonSelectionMemory(4);
			Launcher.setXmlMemoryIndex(selectedIndex);
		}
		else if (TextCorpusDialog.getRdbtnNoDelimiterwhole().isSelected())
			Launcher.setCorpusTextRadioButtonSelectionMemory(1);
		else
			Launcher.setCorpusTextRadioButtonSelectionMemory(2);

		// get selected language
		String languageName = languages.getSelectedValue().toString();

		// create directories for selected language if they don't exist already
		String directory = Paths.docDir + System.getProperty("file.separator") + languageName;
		File dir = new File(directory);
		if (!dir.exists())
			new File(directory).mkdir();
		String directory2 = directory + System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH;
		File dir2 = new File(directory2);
		if (!dir2.exists())
			new File(directory2).mkdir();
		String directory3 = directory + System.getProperty("file.separator") + Constants.SYNTACTIC_ANALYSIS_PATH;
		File dir3 = new File(directory3);
		if (!dir3.exists())
			new File(directory3).mkdir();

		// determine type of encoding, selected code and encoding name
		int encodingType;
		String encodingCode = null;
		String encodingName = "";
		if (dialog.getRdbtnAsciiUnicode().isSelected())
		{
			encodingType = 1;
			encodingCode = null;
			encodingName = "Default";
		}
		// if other raw text formats are selected, get selected encoding from the list
		else if (TextCorpusDialog.getRdbtnOtherRawText().isSelected())
		{
			encodingType = 2;
			String fmt = TextCorpusDialog.getListFFormats().getSelectedValue().toString();

			
			try
			{
				Charset enc = Charset.forName(fmt.substring(0, fmt.indexOf('[')));
				encodingCode = enc.name();
				encodingName = enc.displayName();
			}
			catch (Exception e1)
			{
				// TODO: show code number instead of encodingType in error message!
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.toString(),
						Constants.CANNOT_HANDLE_ENCODING + encodingType, JOptionPane.INFORMATION_MESSAGE);
				encodingCode = null;
				encodingName = "Default";
			}
		}
		else if (dialog.getRdbtnRichTextFormat().isSelected())
		{
			encodingType = 3;
			encodingName = "RTF";
		}
		else if (dialog.getRdbtndoc().isSelected())
		{
			encodingType = 4;
			encodingName = "WORD";
		}
		else if (dialog.getRdbtnHtmlPage().isSelected())
		{
			encodingType = 5;
			encodingName = "HTML";
		}
		else if (dialog.getRdbtnPdfDocument().isSelected())
		{
			encodingType = 6;
			encodingName = "PDF";
		}
		else
		{
			encodingType = 7;
			encodingCode = null;
			encodingName = "Default";
		}

		Language lan = new Language(languageName);
		String delimiterPattern;
		String[] xmlTags = null;

		// determination of delimiter pattern
		if (TextCorpusDialog.getRdbtnLineDelimiter().isSelected())
			delimiterPattern = "\n";
		else if (TextCorpusDialog.getRdbtnPerlRegExpr().isSelected())
			delimiterPattern = TextCorpusDialog.getComboPerl().getSelectedItem().toString();
		// if XML is selected, parse all tags and proceed with string array, not with string pattern!
		else if (TextCorpusDialog.getRdbtnXmlTextNodes().isSelected())
		{
			// set button selection flag
			xmlButtonChecked = true;
			delimiterPattern = "";
			// get the value
			String comboString = TextCorpusDialog.getComboXml().getSelectedItem().toString();
			ArrayList<String> tmp = new ArrayList<String>();

			for (int i = 0; i < comboString.length(); i++)
			{
				// if we hit open tag char...
				if (comboString.charAt(i) == '<')
				{
					int j = 0;
					while (i + j < comboString.length() && comboString.charAt(j) != '>')
					{
						j++;
					}

					tmp.add(comboString.substring(i, j + 1 - i));
					i += j;
					break;
				}
			}

			xmlTags = new String[tmp.size()];
			for (int k = 0; k < tmp.size(); k++)
			{
				xmlTags[k] = tmp.get(k);
			}
		}
		else
			delimiterPattern = "";

		// while loop so we could jump to suite function anytime we want; read charvariants file if it exists
		int i = 0;
		while (i < 1)
		{
			String chartName = Paths.docDir + System.getProperty("file.separator") + lan.isoName
					+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
					+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_PATH;
			File fileCharvians = new File(chartName);
			if (!fileCharvians.exists())
			{
				chartName = Paths.docDir + System.getProperty("file.separator") + lan.isoName
						+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
						+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_SUFFIX_PATH;
				fileCharvians = new File(chartName);
				if (!fileCharvians.exists())
				{
					chartName = Paths.docDir + System.getProperty("file.separator") + lan.isoName
							+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
							+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_SUFFIX_PATH;
					fileCharvians = new File(chartName);
					if (!fileCharvians.exists())
						break;
					else
					{
						AddActionListener.tryToLoadCharVariants(chartName, lan);
						break;
					}
				}
				else
				{
					AddActionListener.tryToLoadCharVariants(chartName, lan);
					break;
				}
			}
			else
				break;
		}

		// Actions are different when new text and new corpus are opened
		if (!corpus)
		{
			TextEditorShellController textController = new TextEditorShellController(fileToBeImported);
			textController.suiteFunction(lan, languageName, delimiterPattern, xmlTags, encodingType, encodingCode,
					encodingName);

			// close the window, cause new one will be opened
			dialog.dispose();

		}
		else
		{
			File corpusDirectory = fileToBeImported.getParentFile();
			String corpusName = FilenameUtils.removeExtension(fileToBeImported.getName());

			CorpusEditorShellController controller = new CorpusEditorShellController(null, null, null, null);

			controller.createAndSaveNewCorpus(delimiterPattern, xmlTags, encodingType, encodingCode, encodingName,
					languageName, corpusDirectory, corpusName);
			// close the window and open edit corpus window
			dialog.dispose();

			controller.openNoojCorpus(new File(corpusDirectory.getPath() + System.getProperty("file.separator")
					+ corpusName + "." + Constants.JNOC_EXTENSION), true);
			controller.openNoojEngine();
		}
	}
}
