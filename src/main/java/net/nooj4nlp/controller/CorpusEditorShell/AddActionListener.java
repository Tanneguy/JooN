package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Mft;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.TextIO;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * Class for implementation of adding *.jnot files to corpus.
 * 
 */

public class AddActionListener implements ActionListener
{
	// finals
	private static final String PROCESS_NONE_TXT_FILES_AS_TXT = "Are you sure you want to process files as RAW TXTs?";
	private static final String PROCESS_NONE_TXT_FILES_AS_TXT_TITLE = "NooJ: suspicious file name extension";

	private CorpusEditorShellController controller;
	private JInternalFrame frame;
	private Corpus corpus;
	private String encodingName;
	// flag for determination if message box for accepting raw files conversion has been displayed
	private boolean alreadyAsked = false;

	public AddActionListener(CorpusEditorShellController controller, JInternalFrame frame)
	{
		super();
		this.controller = controller;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		corpus = this.controller.getCorpus();
		encodingName = corpus.encodingName;
		FileFilter filter = null;

		// determination of encoding name and creating filter
		if (encodingName.equalsIgnoreCase("WORD"))
			filter = new FileNameExtensionFilter("MS-WORD Documents (*.doc)|*.doc", new String[] { "doc" });
		else if (encodingName.equalsIgnoreCase("HTML"))
			filter = new FileNameExtensionFilter("HTML Pages (*.htm;*.html)|*.htm; *.html", new String[] { "htm" });
		else if (encodingName.equalsIgnoreCase("XML"))
			filter = new FileNameExtensionFilter("XML Documents (*.xml)|*.xml", new String[] { "xml" });
		else if (encodingName.equalsIgnoreCase("RTF"))
			filter = new FileNameExtensionFilter("RTF Documents (*.rtf)|*.rtf", new String[] { "rtf" });
		else
			filter = new FileNameExtensionFilter("Text Files (*.txt)|*.txt", new String[] { "txt" });
		// creating file chooser with multi selection and custom filter
		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		jFileChooser.setMultiSelectionEnabled(true);
		jFileChooser.setFileFilter(filter);
		int result = jFileChooser.showOpenDialog(frame);
		// selected files are final!
		final File[] selectedFiles = jFileChooser.getSelectedFiles();
		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			// remove old filter to avoid duplication
			jFileChooser.removeChoosableFileFilter(filter);
			// if encoding name = default + if any of selected files have those extensions...
			if (encodingName.equals("Default"))
			{
				for (File file : selectedFiles)
				{
					String extension = FilenameUtils.getExtension(file.getName());
					if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("htm")
							|| extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("xml")
							|| extension.equalsIgnoreCase("rtf"))
					{
						// ...show message box and break the loop
						alreadyAsked = true;
						break;
					}
				}
			}
			// message box part:
			if (alreadyAsked)
			{
				int optionType = JOptionPane.OK_CANCEL_OPTION;
				int messageType = JOptionPane.WARNING_MESSAGE; // no standard icon

				final JButton ok = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				// listener for "Ok" button; flag is false, close the message box and build up *.jnot files
				ok.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						alreadyAsked = false;
						closeDialogWindow(ok);
						processFilesAndUpdate(selectedFiles);
						// doda
					}
				});
				// listener for "Cancel" button; just close the message box
				cancel.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						closeDialogWindow(cancel);
					}
				});
				// construct options
				Object[] selValues = { ok, cancel };
				// show dialog as normal, selected index will be returned.
				JOptionPane.showOptionDialog(Launcher.getDesktopPane(), PROCESS_NONE_TXT_FILES_AS_TXT,
						PROCESS_NONE_TXT_FILES_AS_TXT_TITLE, optionType, messageType, null, selValues, selValues[0]);
			}
			// if there's no raw files selected, just process files
			else
				processFilesAndUpdate(selectedFiles);
			// update window
			updateEditCorpusWindow();
		}
		// remove filter if canceled
		else if (result == JFileChooser.CANCEL_OPTION)
			jFileChooser.removeChoosableFileFilter(filter);

		JTable table = controller.getTableTexts();
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		TableSorterActionListener tableSorterActionListener = controller.getShell().getTableSorterActionListener();
		tableSorterActionListener.sortTheTable(controller.getTableTexts(), tableModel,
				tableSorterActionListener.getIndex());
	}

	/**
	 * Function for creating *.jnot files out of a regular, text files and adding them to a corpus.
	 * 
	 * @param selectedFiles
	 *            - files that needs to be added to a corpus as a *.jnot files
	 * 
	 */
	private void processFilesAndUpdate(File[] selectedFiles)
	{
		for (File f : selectedFiles)
		{
			// add file to corpus
			addFileToCorpus(f);
			Ntext nText = null;
			String filePath = f.getAbsolutePath();
			if (FilenameUtils.getExtension(f.getName()).equals("jnot") && this.encodingName.equals("Default"))
			{
				String errorMessage = "";
				RefObject<String> errorMessageRef = new RefObject<String>(errorMessage);
				try
				{
					nText = Ntext.load(filePath, corpus.languageName, errorMessageRef);
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (nText == null)
					System.out.println(Constants.FILE_FORMAT_CONFLICT_ERROR + Constants.INCORRECT_FILE_FORMAT_ERROR
							+ filePath + "\n" + errorMessageRef.argvalue);
			}

			else
			{
				// import text file
				nText = new Ntext(corpus);
				try
				{
					nText.buffer = TextIO.loadText(filePath, corpus.encodingType, corpus.encodingCode,
							this.encodingName, corpus.lan.chartable);
				}
				catch (IOException e)
				{
					System.out.println(Constants.FILE_FORMAT_CONFLICT_ERROR + Constants.INCORRECT_FILE_FORMAT_ERROR
							+ filePath + "\n" + e.getMessage());
					return;
				}
				catch (BadLocationException e)
				{
					System.out.println(Constants.FILE_FORMAT_CONFLICT_ERROR + Constants.INCORRECT_FILE_FORMAT_ERROR
							+ filePath + "\n" + e.getMessage());
					return;
				}
			}

			if (nText.buffer.equals(""))
			{
				System.out.println(Constants.CANNOT_LOAD_TEXT_MESSAGE + "from file " + filePath);
				return;
			}

			// delimit text
			nText.DelimPattern = corpus.delimPattern;
			nText.XmlNodes = corpus.xmlNodes;
			Engine engine = controller.getEngine();
			if (engine != null)
			{
				if (corpus.xmlNodes != null)
					nText.delimitXmlTextUnitsAndImportXmlTags(corpus, engine, corpus.xmlNodes, corpus.annotations,
							corpus.hLexemes, corpus.hPhrases);
				else
					nText.delimitTextUnits(engine);
			}
			Mft nTextMft = nText.mft;
			if (nTextMft != null)
				nTextMft.beforeSaving(nTextMft.multiplier);

			// name of file without the extension
			String fileNameWithoutExtension = FilenameUtils.removeExtension(f.getName());
			String fullFilePath = this.controller.getFullPath() + Constants.DIRECTORY_SUFFIX
					+ System.getProperty("file.separator") + fileNameWithoutExtension + "." + Constants.JNOT_EXTENSION;
			// newly created *.jnot file
			File file = new File(fullFilePath);
			long fileSize = file.length();

			TableModel model = controller.getTableTexts().getModel();
			Date date = new Date(f.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String dateString = sdf.format(date);

			Object[] obj = new Object[4];
			obj[0] = fileNameWithoutExtension;
			obj[1] = fileSize;
			obj[2] = dateString;
			obj[3] = nText;
			((DefaultTableModel) model).addRow(obj);
		}

		this.controller.getCorpus().annotations = null;
		this.controller.setModified(true);
		this.controller.updateTitle();
		this.controller.updateTextPaneStats();
		this.controller.updateResults();
	}

	/**
	 * Function for adding file to corpus.
	 * 
	 * @param file
	 *            - file to add
	 * 
	 */
	private void addFileToCorpus(File file)
	{
		int i = 0;
		// using while loop so we could jump to suiteFunction anytime we want
		while (i < 1)
		{
			// check if exists charvariants.txt file, if not, start adding without it.
			if (corpus.lan.chartable == null)
			{
				String chartName = Paths.docDir + System.getProperty("file.separator") + corpus.lan.isoName
						+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
						+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_PATH;
				File fileCharvians = new File(chartName);
				if (!fileCharvians.exists())
				{
					chartName = Paths.docDir + System.getProperty("file.separator") + corpus.lan.isoName
							+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
							+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_SUFFIX_PATH;
					fileCharvians = new File(chartName);
					if (!fileCharvians.exists())
						break;
					else
					{
						tryToLoadCharVariants(chartName, corpus.lan);
						break;
					}
				}
				else
				{
					tryToLoadCharVariants(chartName, corpus.lan);
					break;
				}
			}
			else
				break;
		}
		// add file to the corpus
		suiteFunction(file);
	}

	/**
	 * Function for closing window if one of its component is known.
	 * 
	 * @param componentInWindow
	 *            - component that belongs to window we want to close
	 */
	public static void closeDialogWindow(Component componentInWindow)
	{
		Window w = SwingUtilities.getWindowAncestor(componentInWindow);
		if (w != null)
			w.dispose();
	}

	/**
	 * Suite function that calls function to add text files to corpus or returns an error.
	 * 
	 * @param selectedFiles
	 *            - files that needs to be added to a corpus as a *.jnot files
	 * 
	 */
	private void suiteFunction(File file)
	{
		String corpusDirName = this.controller.getFullPath() + Constants.DIRECTORY_SUFFIX;
		try
		{
			corpus.addTextFile(corpusDirName, file.getAbsolutePath(), this.controller.getEngine());
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_ADD_TEXT_FILE + file.getName(),
					Constants.NOOJ_WARNING + Constants.CANNOT_ADD_TEXT_FILE, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		this.controller.setModified(true);
	}

	/**
	 * Loading char variants from charvariants.txt file. Popping up message box for a charvariants.txt load error if
	 * there's no such file.
	 * 
	 * @param chartName
	 *            - file which have initiated an error
	 * 
	 */
	public static void tryToLoadCharVariants(String chartName, Language lan)
	{
		StringBuilder errorMessage = new StringBuilder("");
		try
		{
			if (!lan.loadCharacterVariants(chartName, errorMessage))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_CHARACTER_VARIANTS_FILE
						+ chartName, Constants.NOOJ_WARNING + errorMessage, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_CHARACTER_VARIANTS_FILE
					+ chartName, Constants.NOOJ_WARNING + errorMessage, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	/**
	 * Function for updating "Edit Corpus" window.
	 * 
	 * @param c
	 *            - controller from which we'll extract shell
	 * 
	 */
	private void updateEditCorpusWindow()
	{
		controller.updateTextPaneStats();
		// if corpus was already updated, but not saved, don't add "modified" tag again
		controller.updateTitle();
	}
}