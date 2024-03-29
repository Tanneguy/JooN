package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Class implements exporting concordance to a text file.
 */
public class ExportConcordanceActionListener implements ActionListener
{
	// export mode(text, web page, index)
	private int typeOfExport;

	// controllers
	private ConcordanceShellController controller;
	private CorpusEditorShellController corpusController;

	// values of concordance's texboxes
	private int before = 0;
	private int after = 0;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param typeOfExport
	 *            - type of export (1 - text, 2 - web page, 3 - index)
	 */
	public ExportConcordanceActionListener(ConcordanceShellController controller, int typeOfExport)
	{
		super();
		this.controller = controller;
		this.typeOfExport = typeOfExport;
		this.corpusController = this.controller.getCorpusController();
		before = ConcordanceShellController.getBefore();
		after = ConcordanceShellController.getAfter();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// exporting to txt file
		if (typeOfExport == 1)
		{
			FileFilter filter = new FileNameExtensionFilter("Tab Separated Text (*.txt)", Constants.TXT_EXTENSION);
			// creating file chooser for text (*.txt) files only
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setAcceptAllFileFilterUsed(false);
			jFileChooser.setDialogTitle("Save As");
			jFileChooser.setFileFilter(filter);
			int result = jFileChooser.showSaveDialog(controller.getConcordanceShell());
			// if file(s) has/ve been selected, or if file name is given manually...
			if (result == JFileChooser.APPROVE_OPTION)
			{
				// get parent folder
				String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();

				// remove extension (if given) and set .txt extension
				String inputFileName = FilenameUtils.removeExtension(jFileChooser.getSelectedFile().getName()) + "."
						+ Constants.TXT_EXTENSION;
				String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

				File newFile = new File(pathOfInputFile);

				// if such file exists, show option dialog, and if confirmed, delete the old file and create new
				if (newFile.exists())
				{
					int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName
							+ " already exists." + " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS,
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
						return;
					else
					{
						newFile.delete();
						saveConcordanceAsTXT(pathOfInputFile);
					}
				}
				else
					saveConcordanceAsTXT(pathOfInputFile);
			}
		}
		// exporting to htm file
		else if (typeOfExport == 2)
		{
			FileFilter filter = new FileNameExtensionFilter("Web Page (*.htm)", Constants.HTM_EXTENSION);
			// creating file chooser for web page (*.htm) files only
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setAcceptAllFileFilterUsed(false);
			jFileChooser.setDialogTitle("Save As");
			jFileChooser.setFileFilter(filter);
			int result = jFileChooser.showSaveDialog(controller.getConcordanceShell());
			// if file(s) has/ve been selected, or if file name is given manually...
			if (result == JFileChooser.APPROVE_OPTION)
			{
				// get parent folder
				String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();

				// remove extension (if given) and set .htm extension
				String inputFileName = FilenameUtils.removeExtension(jFileChooser.getSelectedFile().getName()) + "."
						+ Constants.HTM_EXTENSION;
				String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

				File newFile = new File(pathOfInputFile);

				// if such file exists, show option dialog, and if confirmed, delete the old file and create new
				if (newFile.exists())
				{
					int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName
							+ " already exists." + " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS,
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
						return;
					else
					{
						newFile.delete();
						saveConcordanceAsHTM(pathOfInputFile);
					}
				}
				else
					saveConcordanceAsHTM(pathOfInputFile);
			}
		}
		// exporting indexes (to *.txt file)
		else if (typeOfExport == 3)
		{
			FileFilter filter = new FileNameExtensionFilter("Text (*.txt)", Constants.TXT_EXTENSION);
			// creating file chooser for web page (*.txt) files only
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setAcceptAllFileFilterUsed(false);
			jFileChooser.setDialogTitle("Save As");
			jFileChooser.setFileFilter(filter);
			int result = jFileChooser.showSaveDialog(controller.getConcordanceShell());
			// if file(s) has/ve been selected, or if file name is given manually...
			if (result == JFileChooser.APPROVE_OPTION)
			{
				// get parent folder
				String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();

				// remove extension (if given) and set .htm extension
				String inputFileName = FilenameUtils.removeExtension(jFileChooser.getSelectedFile().getName()) + "."
						+ Constants.TXT_EXTENSION;
				String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

				File newFile = new File(pathOfInputFile);

				// if such file exists, show option dialog, and if confirmed, delete the old file and create new
				if (newFile.exists())
				{
					int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName
							+ " already exists." + " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS,
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
						return;
					else
					{
						newFile.delete();
						saveConcordancesIndex(pathOfInputFile);
					}
				}
				else
					saveConcordancesIndex(pathOfInputFile);
			}
		}
	}

	/**
	 * Function for saving concordance as text file.
	 * 
	 * @param pathOfFileToBeSaved
	 *            - path of future text file
	 */
	private void saveConcordanceAsTXT(String pathOfFileToBeSaved)
	{
		PrintWriter pw = null;
		// get items and values of textboxes
		List<Object> theItems = controller.getTheItems();

		File txtFile = new File(pathOfFileToBeSaved);
		if (txtFile.exists())
			return;

		// create the file and tie print writer to the file
		try
		{
			txtFile.createNewFile();
			pw = new PrintWriter(txtFile);
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_CREATE_CONCORDANCE_FILE + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_CREATE_CONCORDANCE_FILE + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// export all *visible* concordance entries to a tab-separated ".csv" file
		if (controller.getCorpusController() != null)
		{
			// corpus concordance
			String corpusDirPath = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
			String lastFullPath = "";
			Ntext myLastText = null;

			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);

				// compute textbuffer
				String fileName = item[0].toString();
				String fullPath = corpusDirPath + System.getProperty("file.separator") + fileName;
				Ntext myText = null;

				if (fullPath.equals(lastFullPath))
					myText = myLastText;
				else
				{
					Corpus corpus = corpusController.getCorpus();
					try
					{
						myText = Ntext.loadJustBufferForCorpus(fullPath, corpus.lan, corpus.multiplier);
					}
					catch (IOException e)
					{
						if (pw != null)
							pw.close();

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER, JOptionPane.ERROR_MESSAGE);
						return;
					}
					lastFullPath = fullPath;
					myLastText = myText;
				}

				if (myText == null)
				{
					pw.write(Constants.CANNOT_LOAD_TEXT_MESSAGE + fileName + ".");
					pw.close();
					return;
				}

				pw.write(fileName + "\t");

				exportTXThelpFunction(pw, item, myText);
			}
			pw.close();
		}

		else
		{
			// text concordance
			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);

				exportTXThelpFunction(pw, item, controller.getTextController().getMyText());
			}

			pw.close();
		}

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
				Constants.EXPORT_CONCORDANCE_SUCCESS + txtFile.getName(), Constants.NOOJ_EXPORT_CONCORDANCE,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Function for saving concordance as HTM file.
	 * 
	 * @param pathOfFileToBeSaved
	 *            - path of future text file
	 */
	private void saveConcordanceAsHTM(String pathOfFileToBeSaved)
	{
		PrintWriter pw = null;
		PrintWriter pwCon = null;

		File txtFile = new File(pathOfFileToBeSaved);
		if (txtFile.exists())
			return;

		// create the file and tie print writer to the file
		try
		{
			txtFile.createNewFile();
			pw = new PrintWriter(txtFile);
			pw.write(html_header("NooJ Concordance's Index") + "\n");
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_CREATE_CONCORDANCE_SITE + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_CREATE_CONCORDANCE_SITE + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String concordanceFullPathPrefix = txtFile.getParent() + System.getProperty("file.separator")
				+ Constants.CONCORDANCE_WEB_SITE_NAME;

		int concordanceNumber = 0;
		RefObject<Integer> concordanceNumberRef = new RefObject<Integer>(concordanceNumber);

		JTable concordanceTable = controller.getConcordanceTable();
		DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();

		// export all *visible* concordance entries to a tab-separated ".csv" file
		if (corpusController != null && corpusController.getShell() != null)
		{
			// corpus concordance

			String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
			String lastFullPath = "";
			Ntext myLastText = null;
			String lastIndexName = "";

			// ref values for help function
			RefObject<PrintWriter> pwConRef = new RefObject<PrintWriter>(pwCon);
			RefObject<PrintWriter> pwRef = new RefObject<PrintWriter>(pw);
			RefObject<String> lastIndexNameRef = new RefObject<String>(lastIndexName);

			for (int i = 0; i < tableModel.getRowCount(); i++)
			{
				// compute textbuffer

				String fileName = tableModel.getValueAt(i, 0).toString();
				String fullPath = corpusDirName + System.getProperty("file.separator") + fileName;
				Ntext myText = null;

				if (fullPath.equals(lastFullPath))
					myText = myLastText;
				else
				{
					Corpus corpus = corpusController.getCorpus();
					try
					{
						myText = Ntext.loadJustBufferForCorpus(fullPath, corpus.lan, corpus.multiplier);
					}
					catch (IOException e)
					{
						if (pw != null)
							pw.close();

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER, JOptionPane.ERROR_MESSAGE);
						return;
					}

					lastFullPath = fullPath;
					myLastText = myText;
				}

				if (myText == null)
				{
					pw.write(Constants.CANNOT_LOAD_TEXT_MESSAGE + fileName + ".");
					pw.close();
					return;
				}

				exportHTMhelpFunction(pwConRef, pwRef, myText, lastIndexNameRef, fileName, concordanceFullPathPrefix,
						tableModel, i, concordanceNumberRef, true);
			}

			pwCon = pwConRef.argvalue;
			// Potential resources leak comment: pw has to be closed after writing to it, not at this point.
			pw = pwRef.argvalue;

			pwCon.write(html_tailer());
			pwCon.close();

			pw.write(html_tailer());
			pw.close();
		}
		else
		{
			// text concordance

			String lastIndexName = "";
			Ntext myText = controller.getTextController().getMyText();

			// ref values for help function
			RefObject<PrintWriter> pwConRef = new RefObject<PrintWriter>(pwCon);
			RefObject<PrintWriter> pwRef = new RefObject<PrintWriter>(pw);
			RefObject<String> lastIndexNameRef = new RefObject<String>(lastIndexName);

			for (int i = 0; i < tableModel.getRowCount(); i++)
				exportHTMhelpFunction(pwConRef, pwRef, myText, lastIndexNameRef, "", concordanceFullPathPrefix,
						tableModel, i, concordanceNumberRef, false);

			pwCon = pwConRef.argvalue;
			pw = pwRef.argvalue;

			pwCon.write(html_tailer());
			pwCon.close();

			pw.write(html_tailer());
			pw.close();
		}

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
				Constants.EXPORT_CONCORDANCE_SUCCESS + txtFile.getName(), Constants.NOOJ_EXPORT_CONCORDANCE,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Function for saving concordance as indexed file.
	 * 
	 * @param pathOfFileToBeSaved
	 *            - path of future text file
	 */
	private void saveConcordancesIndex(String pathOfFileToBeSaved)
	{
		PrintWriter pw = null;

		// get items and values of textboxes
		List<Object> theItems = controller.getTheItems();

		File txtFile = new File(pathOfFileToBeSaved);
		if (txtFile.exists())
			return;

		// create the file and tie print writer to the file
		try
		{
			txtFile.createNewFile();
			pw = new PrintWriter(txtFile);
		}
		catch (FileNotFoundException e)
		{
			JOptionPane
					.showMessageDialog(null, Constants.CANNOT_CREATE_CONCORDANCE_INDEX_FILE + txtFile.getName() + "\r"
							+ e.getMessage(), Constants.CANNOT_EXPORT_INDEX_CONCORDANCE_TITLE,
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane
					.showMessageDialog(null, Constants.CANNOT_CREATE_CONCORDANCE_INDEX_FILE + txtFile.getName() + "\r"
							+ e.getMessage(), Constants.CANNOT_EXPORT_INDEX_CONCORDANCE_TITLE,
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		// export all *visible* entries to an index file
		if (corpusController != null && corpusController.getShell() != null)
		{
			// corpus index
			String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;

			// necessary lists
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> pages = new ArrayList<String>();
			ArrayList<Integer> addresses = new ArrayList<Integer>();

			String lastFullPath = "";
			Ntext myLastText = null;

			// list refs for help functions
			RefObject<ArrayList<String>> keysRef = new RefObject<ArrayList<String>>(keys);
			RefObject<ArrayList<String>> pagesRef = new RefObject<ArrayList<String>>(pages);
			RefObject<ArrayList<Integer>> addressesRef = new RefObject<ArrayList<Integer>>(addresses);

			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);
				// compute textbuffer
				String fileName = item[0].toString();
				String fullPath = corpusDirName + System.getProperty("file.separator") + fileName;
				Ntext myText = null;
				if (lastFullPath.equals(fullPath))
					myText = myLastText;
				else
				{
					Corpus corpus = corpusController.getCorpus();
					try
					{
						myText = Ntext.loadJustBufferForCorpus(fullPath, corpus.lan, corpus.multiplier);
					}
					catch (IOException e)
					{
						if (pw != null)
							pw.close();

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER, JOptionPane.ERROR_MESSAGE);
						return;
					}
					myLastText = myText;
				}

				if (myText == null)
				{
					pw.close();
					return;
				}

				String myTextBuffer = myText.buffer;

				exportIndexHelpFunction1(item, myTextBuffer, keysRef, pagesRef, addressesRef, fileName);
			}

			keys = keysRef.argvalue;
			pages = pagesRef.argvalue;
			addresses = addressesRef.argvalue;

			RefObject<PrintWriter> pwRef = new RefObject<PrintWriter>(pw);

			exportIndexHelpFunction2(pwRef, keys, pages, addresses, true);
			// Potential resources leak comment: pw has to be closed after writing to it, not at this point.
			pw = pwRef.argvalue;
		}
		else
		{
			// necessary lists and their refs for help function
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<Integer> addresses = new ArrayList<Integer>();

			RefObject<ArrayList<String>> keysRef = new RefObject<ArrayList<String>>(keys);
			RefObject<ArrayList<Integer>> addressesRef = new RefObject<ArrayList<Integer>>(addresses);

			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);
				String myTextBuffer = controller.getTextController().getMyText().buffer;

				exportIndexHelpFunction1(item, myTextBuffer, keysRef, null, addressesRef, "");
			}

			keys = keysRef.argvalue;
			addresses = addressesRef.argvalue;

			RefObject<PrintWriter> pwRef = new RefObject<PrintWriter>(pw);

			exportIndexHelpFunction2(pwRef, keys, null, addresses, false);
			pw = pwRef.argvalue;
		}

		if (pw != null)
			pw.close();

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EXPORT_INDEX_SUCCESS + txtFile.getName(),
				Constants.NOOJ_EXPORT_INDEX, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Help function for export concordance to text function.
	 * 
	 * @param pw
	 *            - print writer of a exported file
	 * @param item
	 *            - item that needs to be exported
	 * @param myText
	 *            - Ntext of current item
	 */
	private void exportTXThelpFunction(PrintWriter pw, Object[] item, Ntext myText)
	{
		String myTextBuffer = myText.buffer;

		// compute address
		ArrayList<?> annotation = (ArrayList<?>) item[5];
		double absoluteBeginAddress0 = (Double) annotation.get(2);
		double absoluteEndAddress0 = (Double) annotation.get(3);
		int absoluteBeginAddress = (int) absoluteBeginAddress0;
		int absoluteEndAddress = (int) absoluteEndAddress0;

		if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
		{
			while (Language.isLetter(myTextBuffer.charAt(absoluteEndAddress)))
				absoluteEndAddress++;
		}

		// compute output
		String sOutput;

		ArrayList<?> seqOfAnnotations = (ArrayList<?>) ((ArrayList<?>) item[5]).get(1);
		if (seqOfAnnotations == null)
			sOutput = "";
		else
			sOutput = seqOfAnnotations.get(1).toString();

		// compute left context, modified sequence and right context
		String lCon = "", mSeq = "", rCon = "";

		RefObject<String> lConRef = new RefObject<String>(lCon);
		RefObject<String> mSeqRef = new RefObject<String>(mSeq);
		RefObject<String> rConRef = new RefObject<String>(rCon);

		String seq = myTextBuffer.substring(absoluteBeginAddress, absoluteEndAddress);
		ConcordanceShell shell = controller.getConcordanceShell();

		if (shell.getCbOutputs().isSelected())
			seq += "\0" + sOutput;

		controller.compute(shell.getRbCharacters().isSelected(), absoluteBeginAddress, absoluteEndAddress,
				myTextBuffer, before, after, seq, lConRef, mSeqRef, rConRef);

		lCon = lConRef.argvalue;
		mSeq = mSeqRef.argvalue;
		rCon = rConRef.argvalue;

		if (before > 0)
			pw.write(ConcordanceShellController.cleanUpConcordanceString(lCon) + "\t");

		pw.write(ConcordanceShellController.cleanUpConcordanceString(mSeq) + "\t");

		if (after > 0)
			pw.write(ConcordanceShellController.cleanUpConcordanceString(rCon) + "\t");

		pw.write(absoluteBeginAddress + "\t" + absoluteEndAddress);
		pw.write("\n");
	}

	/**
	 * Help function for export concordance to HTML function.
	 * 
	 * @param pwConRef
	 *            - print writer of concordance's sequence. Needs to be a ref value.
	 * @param pwRef
	 *            - print writer of concordance. Needs to be a ref value.
	 * @param myText
	 *            - Ntext of concordance's sequence
	 * @param lastIndexNameRef
	 *            - name of last concordance's sequence file. Needs to be a ref value.
	 * @param fileName
	 *            - file name of concordance sequence
	 * @param concordanceFullPathPrefix
	 *            - path where concordance HTM file should be placed + separator + "concord"
	 * @param tableModel
	 *            - model of concordance's table
	 * @param i
	 *            - actual row of concordance's table
	 * @param concordanceNumber
	 *            - number of actual concordance's sequence file
	 * @param corpusContext
	 *            - flag to determine whether help function was called from corpus context or not
	 */
	private void exportHTMhelpFunction(RefObject<PrintWriter> pwConRef, RefObject<PrintWriter> pwRef, Ntext myText,
			RefObject<String> lastIndexNameRef, String fileName, String concordanceFullPathPrefix,
			DefaultTableModel tableModel, int i, RefObject<Integer> concordanceNumber, boolean corpusContext)
	{
		// getting values from input
		String myTextBuffer = myText.buffer;
		PrintWriter pwCon = pwConRef.argvalue;
		PrintWriter pw = pwRef.argvalue;
		String lastIndexName = lastIndexNameRef.argvalue;

		// compute address
		ArrayList<?> annotation = (ArrayList<?>) tableModel.getValueAt(i, 5);

		double absoluteBeginAddress0 = (Double) annotation.get(2);
		double absoluteEndAddress0 = (Double) annotation.get(3);
		int absoluteBeginAddress = (int) absoluteBeginAddress0;
		int absoluteEndAddress = (int) absoluteEndAddress0;

		if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
		{
			while (Language.isLetter(myTextBuffer.charAt(absoluteEndAddress)))
				absoluteEndAddress++;
		}

		// compute output
		String sOutput;

		ArrayList<?> seqOfAnnotations = (ArrayList<?>) annotation.get(1);
		if (seqOfAnnotations == null)
			sOutput = "";
		else
			sOutput = seqOfAnnotations.get(1).toString();

		String lCon = "", mSeq = "", rCon = "";

		RefObject<String> lConRef = new RefObject<String>(lCon);
		RefObject<String> mSeqRef = new RefObject<String>(mSeq);
		RefObject<String> rConRef = new RefObject<String>(rCon);

		String sInput = myTextBuffer.substring(absoluteBeginAddress, absoluteEndAddress);
		String indexKey = "", seq = "";
		ConcordanceShell shell = controller.getConcordanceShell();

		if (shell.getCbOutputs().isSelected())
			seq = sInput + "\0" + sOutput;
		else
			indexKey = seq = sInput;

		controller.compute(shell.getRbCharacters().isSelected(), absoluteBeginAddress, absoluteEndAddress,
				myTextBuffer, before, after, seq, lConRef, mSeqRef, rConRef);

		lCon = lConRef.argvalue;
		mSeq = mSeqRef.argvalue;
		rCon = rConRef.argvalue;

		// write concordance in current file and sequence in index file
		if (!indexKey.equals(lastIndexName))
		{
			if (pwCon != null)
				pwCon.close();
			concordanceNumber.argvalue++;
			String concordancePath = concordanceFullPathPrefix + concordanceNumber.argvalue + "."
					+ Constants.HTM_EXTENSION;

			File conFile = new File(concordancePath);
			if (conFile.exists())
				conFile.delete();
			try
			{
				conFile.createNewFile();
				pwCon = new PrintWriter(conFile);
				pwCon.write(html_header("Concordance for \"" + indexKey + "\"\n"));
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_CREATE_CONCORDANCE_SITE
						+ conFile.getName() + "\r" + e.getMessage(), Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_CREATE_CONCORDANCE_SITE
						+ conFile.getName() + "\r" + e.getMessage(), Constants.CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// file path needs to be fixed, because it doesn't work in Mozilla
			String concordancePathFixed = "file:///" + concordancePath;
			pw.write("<p><a href=\"" + concordancePathFixed + "\">" + indexKey + "</a></p>\n");
			lastIndexName = indexKey;
		}

		pwCon.write("<p>");
		if (corpusContext)
			pwCon.write("(<i>" + FilenameUtils.removeExtension(fileName) + "</i>) ");

		if (before > 0)
			pwCon.write(ConcordanceShellController.cleanUpConcordanceString(lCon) + "\t");

		pwCon.write("<b>" + ConcordanceShellController.cleanUpConcordanceString(sInput) + "</b>\t");

		if (after > 0)
			pwCon.write(ConcordanceShellController.cleanUpConcordanceString(rCon));

		pwCon.write("</p>");

		pwConRef.argvalue = pwCon;
		pwRef.argvalue = pw;
		lastIndexNameRef.argvalue = lastIndexName;
	}

	/**
	 * Function returns the header for concordance's HTML file.
	 * 
	 * @param title
	 *            - title of concordance
	 * @return - full header of new concordance's HTML file
	 */

	private String html_header(String title)
	{
		return "<!doctype html public \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n<html>\n<head>\n<title>" + title
				+ "</title>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>\n<body>\n";
	}

	/**
	 * Function returns the close brackets of HTML header.
	 * 
	 * @return - closing brackets of HTML header
	 */
	private String html_tailer()
	{
		return "</body>\n</html>";
	}

	/**
	 * Help function #1 for export index function.
	 * 
	 * @param item
	 *            - item that needs to be exported
	 * @param myTextBuffer
	 *            - current text buffer
	 * @param keysRef
	 *            - array list with concordance's sequences. Needs to be a ref value.
	 * @param pagesRef
	 *            - array list with concordance's files. Needs to be a ref value.
	 * @param addressesRef
	 *            - array list with concordance's addresses of sequences in text. Needs to be a ref value.
	 * @param fileName
	 *            - file name of current sequence
	 */
	private void exportIndexHelpFunction1(Object[] item, String myTextBuffer, RefObject<ArrayList<String>> keysRef,
			RefObject<ArrayList<String>> pagesRef, RefObject<ArrayList<Integer>> addressesRef, String fileName)
	{
		// get the values from refs
		ArrayList<String> keys = keysRef.argvalue;
		ArrayList<String> pages = new ArrayList<String>();
		if (!fileName.equals(""))
			pages = pagesRef.argvalue;
		ArrayList<Integer> addresses = addressesRef.argvalue;

		// compute address
		ArrayList<?> annotation = (ArrayList<?>) item[5];

		double absoluteBeginAddress0 = (Double) annotation.get(2);
		double absoluteEndAddress0 = (Double) annotation.get(3);
		int absoluteBeginAddress = (int) absoluteBeginAddress0;
		int absoluteEndAddress = (int) absoluteEndAddress0;

		if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
		{
			while (Language.isLetter(myTextBuffer.charAt(absoluteEndAddress)))
				absoluteEndAddress++;
		}

		// compute output
		String sOutput;

		ArrayList<?> seqOfAnnotations = (ArrayList<?>) ((ArrayList<?>) item[5]).get(1);
		if (seqOfAnnotations == null)
			sOutput = "";
		else
			sOutput = seqOfAnnotations.get(1).toString();

		String lCon = "", mSeq = "", rCon = "";

		RefObject<String> lConRef = new RefObject<String>(lCon);
		RefObject<String> mSeqRef = new RefObject<String>(mSeq);
		RefObject<String> rConRef = new RefObject<String>(rCon);

		String seq = myTextBuffer.substring(absoluteBeginAddress, absoluteEndAddress);
		ConcordanceShell shell = controller.getConcordanceShell();

		if (shell.getCbOutputs().isSelected())
			seq += "/" + sOutput;

		controller.compute(shell.getRbCharacters().isSelected(), absoluteBeginAddress, absoluteEndAddress,
				myTextBuffer, before, after, seq, lConRef, mSeqRef, rConRef);

		lCon = lConRef.argvalue;
		mSeq = mSeqRef.argvalue;
		rCon = rConRef.argvalue;

		keys.add(mSeq); // sequence with output
		if (!fileName.equals(""))
			pages.add(fileName); // text file name
		addresses.add(absoluteBeginAddress);

		// return values to refs
		keysRef.argvalue = keys;
		if (!fileName.equals(""))
			pagesRef.argvalue = pages;
		addressesRef.argvalue = addresses;
	}

	/**
	 * Help function #2 for export index function.
	 * 
	 * @param pwRef
	 *            - print writer of concordance. Needs to be a ref value.
	 * @param keys
	 *            - array list with concordance's sequences
	 * @param pages
	 *            - array list with concordance's files
	 * @param addresses
	 *            - array list with concordance's addresses of sequences in text
	 * @param corpusContext
	 *            - flag to determine whether help function was called from corpus context or not
	 */

	private void exportIndexHelpFunction2(RefObject<PrintWriter> pwRef, ArrayList<String> keys,
			ArrayList<String> pages, ArrayList<Integer> addresses, boolean corpusContext)
	{
		PrintWriter pw = pwRef.argvalue;

		// sort index by key
		for (int i = 0; i < keys.size() - 1; i++)
		{
			for (int j = i + 1; j < keys.size(); j++)
			{
				String keyI = keys.get(i);
				String keyJ = keys.get(j);

				if (keyJ.compareTo(keyI) < 0)
				{
					String keyT = keyI;
					keys.set(i, keyJ);
					keys.set(j, keyT);

					if (corpusContext)
					{
						String pageI = pages.get(i);
						String pageJ = pages.get(j);
						String pageT = pageI;
						pages.set(i, pageJ);
						pages.set(j, pageT);
					}

					int addT = addresses.get(i);
					addresses.set(i, addresses.get(j));
					addresses.set(j, addT);
				}
			}
		}

		if (corpusContext)
		{
			// sort index by page
			for (int i = 0; i < keys.size() - 1; i++)
			{
				for (int j = i + 1; j < keys.size(); j++)
				{
					String keyI = keys.get(i);
					String keyJ = keys.get(j);

					if (keyI.equals(keyJ))
					{
						String pageI = pages.get(i);
						String pageJ = pages.get(j);

						if (pageJ.compareTo(pageI) < 0)
						{
							String keyT = keyI;
							keys.set(i, keyJ);
							keys.set(j, keyT);

							String pageT = pageI;
							pages.set(i, pageJ);
							pages.set(j, pageT);

							int addT = addresses.get(i);
							addresses.set(i, addresses.get(j));
							addresses.set(j, addT);
						}
					}
				}
			}

			// print the result
			String lastKey = "", lastPage = "";
			for (int i = 0; i < keys.size(); i++)
			{
				String key = keys.get(i);
				String page = pages.get(i);

				if (key.equals(lastKey) && page.equals(lastPage))
					continue;

				if (!key.equals(lastKey))
				{
					pw.write("\n" + key + ", " + page);
					lastPage = page;
					lastKey = key;
					continue;
				}
				else if (page.equals(lastPage))
					continue;
				else
				{
					pw.write(", " + page);
					lastPage = page;
				}
			}
		}

		else
		{
			// print the result
			String lastKey = "";
			for (int i = 0; i < keys.size(); i++)
			{
				String key = keys.get(i);
				int address = addresses.get(i);

				if (!key.equals(lastKey))
				{
					pw.write("\n" + key + ", " + address);
					lastKey = key;
					continue;
				}
				else
					pw.write(", " + address);
			}
		}

		pw.write("\n");
		pw.close();

		pwRef.argvalue = pw;
	}
}
