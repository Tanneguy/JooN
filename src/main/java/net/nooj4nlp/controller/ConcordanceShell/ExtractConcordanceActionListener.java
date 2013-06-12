package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Class implements extracting text units whose text partially (don't) match with concordance's sequence.
 */
public class ExtractConcordanceActionListener implements ActionListener
{
	// controllers
	private ConcordanceShellController controller;
	private CorpusEditorShellController corpusController;
	// flag to determine whether function should extract matching TU or not
	private boolean matchingTU;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param matchingTU
	 *            - flag to determine whether function should extract matching TU or not
	 */

	public ExtractConcordanceActionListener(ConcordanceShellController controller, boolean matchingTU)
	{
		super();
		this.controller = controller;
		this.corpusController = this.controller.getCorpusController();
		this.matchingTU = matchingTU;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FileFilter filter = new FileNameExtensionFilter("UTF-8 Text File (*.txt)", Constants.TXT_EXTENSION);
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
				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
						+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);
				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;
				else
				{
					newFile.delete();
					extractTextUnits(pathOfInputFile);
				}
			}
			else
				extractTextUnits(pathOfInputFile);
		}
	}

	/**
	 * Function for extracting TUs and saving them to a text file.
	 * 
	 * @param pathOfFileToBeSaved
	 *            - path of future text file
	 */
	private void extractTextUnits(String pathOfFileToBeSaved)
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
					Constants.CANNOT_CREATE_SUBCORPUS + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXTRACT_TEXT_UNITS, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.CANNOT_CREATE_SUBCORPUS + txtFile.getName() + "\r" + e.getMessage(),
					Constants.CANNOT_EXTRACT_TEXT_UNITS, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// export all visible concordance entries to a tab-separated ".csv" file
		if (corpusController != null && corpusController.getShell() != null)
		{
			Boolean[] textUnitIsExported;
			Corpus corpus = corpusController.getCorpus();
			String corpusDirPath = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
			HashMap<String, Boolean[]> textsToBeExported = new HashMap<String, Boolean[]>();
			String lastFullPath = "";
			Ntext myLastText = null;

			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);

				// if this is a case of extracting non match text units, check annotations for null values, and exit if
				// positive
				if (!matchingTU && item[5] == null)
				{
					if (pw != null)
						pw.close();

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EMPTY_ANNOTATIONS, "NooJ: "
							+ Constants.EMPTY_ANNOTATIONS, JOptionPane.ERROR_MESSAGE);
					return;
				}
				ArrayList<?> annotation = (ArrayList<?>) item[5];

				// find text
				String fileName = item[0].toString();
				String fullPath = corpusDirPath + System.getProperty("file.separator") + fileName;
				Ntext myText = null;
				if (fullPath.equals(lastFullPath))
					myText = myLastText;
				else
				{
					try
					{
						myText = Ntext.loadForCorpus(fullPath, corpus.lan, corpus.multiplier);
					}
					catch (IOException e)
					{
						if (pw != null)
							pw.close();

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
						return;
					}
					lastFullPath = fullPath;
					myLastText = myText;
					TextEditorShellController textController = corpusController.getTextController();
					TextEditorShell textShell = null;
					if (textController == null)
					{
						// open text shell and set its data
						textShell = new TextEditorShell(corpusController, myText, fileName, myText.getDelimPattern(),
								false);
						textController = textShell.getTextController();
						corpusController.getShell().setTextEditorShell(textShell);
						corpusController.setTextController(textController);
						textController.setFileToBeOpenedOrImported(new File(fullPath));
						// set number of text units to responsible label
						textShell.getLblnTus().setText("/ " + textShell.getText().nbOfTextUnits + " TUs");

						Launcher.getDesktopPane().add(textShell);
						textShell.setVisible(true);
					}
					else
						textController.resetShellText();

					textController.setMyText(myText);

					if (textShell != null)
					{
						textShell.dispose();
						Launcher.getDesktopPane().remove(textShell);
						textShell.setVisible(false);
					}
				}

				if (myText == null)
				{
					if (pw != null)
						pw.close();

					return;
				}

				// compute beg and end addresses
				double absoluteBeginAddress0 = (Double) annotation.get(2);
				double absoluteEndAddress0 = (Double) annotation.get(3);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;
				int absoluteEndAddress = (int) absoluteEndAddress0;

				if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
				{
					while (Language.isLetter(myText.buffer.charAt(absoluteEndAddress)))
						absoluteEndAddress++;
				}

				if (!textsToBeExported.containsKey(fullPath))
				{
					Boolean[] booleanArray = new Boolean[myText.mft.tuAddresses.length + 1];
					for (int j = 0; j < booleanArray.length; j++)
						booleanArray[j] = false;
					textsToBeExported.put(fullPath, booleanArray);
				}

				textUnitIsExported = textsToBeExported.get(fullPath);

				int tuNb = (Integer) annotation.get(0);

				if (tuNb == -1)
					tuNb = corpusController.getTextController().locateTextUnit(absoluteBeginAddress);

				textUnitIsExported[tuNb] = true;
				textsToBeExported.get(fullPath)[tuNb] = true;
			}

			lastFullPath = "";
			myLastText = null;

			
			for (String fullPath2 : textsToBeExported.keySet())
			{
				Ntext myText2 = null;

				if (fullPath2.equals(lastFullPath))
					myText2 = myLastText;
				else
				{
					try
					{
						myText2 = Ntext.loadForCorpus(fullPath2, corpus.lan, corpus.multiplier);
					}
					catch (IOException f)
					{
						if (pw != null)
							pw.close();

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), f.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
						return;
					}

					lastFullPath = fullPath2;
					myLastText = myText2;
				}

				textUnitIsExported = textsToBeExported.get(fullPath2);

				for (int iTU = 1; iTU < myText2.mft.tuAddresses.length; iTU++)
				{
					// adequate statement for each case of matching
					if (textUnitIsExported[iTU] != null
							&& ((textUnitIsExported[iTU] && matchingTU) || (!textUnitIsExported[iTU] && !matchingTU)))
					{
						int iTUindex = myText2.mft.tuAddresses[iTU];
						pw.write("<TU>\n");
						pw.write(myText2.buffer.substring(iTUindex, iTUindex + myText2.mft.tuLengths[iTU]) + "\n");
						pw.write("</TU>\n\n");
					}
				}
			}

			if (pw != null)
				pw.close();
		}
		else
		{
			TextEditorShellController textController = controller.getTextController();
			Ntext myText = textController.getMyText();
			boolean[] textUnitIsExported = new boolean[myText.mft.tuAddresses.length + 1];

			for (int i = 0; i < theItems.size(); i += 4)
			{
				boolean visible = (Boolean) theItems.get(i + 2);
				if (!visible)
					continue;

				Object[] item = (Object[]) theItems.get(i + 1);

				// if this is a case of extracting non match text units, check annotations for null values, and exit if
				// positive
				if (!matchingTU && item[5] == null)
				{
					if (pw != null)
						pw.close();

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EMPTY_ANNOTATIONS, "NooJ: "
							+ Constants.EMPTY_ANNOTATIONS, JOptionPane.ERROR_MESSAGE);
					return;
				}

				ArrayList<?> annotation = (ArrayList<?>) item[5];

				// compute beg and end addresses
				double absoluteBeginAddress0 = (Double) annotation.get(2);
				double absoluteEndAddress0 = (Double) annotation.get(3);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;
				int absoluteEndAddress = (int) absoluteEndAddress0;

				if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
				{
					while (Language.isLetter(myText.buffer.charAt(absoluteEndAddress)))
						absoluteEndAddress++;
				}

				int tuNb = (Integer) annotation.get(0);
				if (tuNb == -1)
					tuNb = textController.locateTextUnit(absoluteBeginAddress);
				textUnitIsExported[tuNb] = true;
			}
			for (int iTU = 1; iTU < myText.mft.tuAddresses.length; iTU++)
			{
				// adequate statement for each case of matching
				if ((textUnitIsExported[iTU] && matchingTU) || (!textUnitIsExported[iTU] && !matchingTU))
				{
					int iTUindex = myText.mft.tuAddresses[iTU];
					pw.write("<TU>\n");
					pw.write(myText.buffer.substring(iTUindex, iTUindex + myText.mft.tuLengths[iTU]) + "\n");
					pw.write("</TU>\n\n");
				}
			}
		}

		pw.close();
		if (matchingTU)
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EXTRACT_MATCHING_TEXT_UNITS_SUCCESS
					+ txtFile.getName(), Constants.NOOJ_EXPORT_MATCHING_TEXT_UNITS, JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane
					.showMessageDialog(Launcher.getDesktopPane(), Constants.EXTRACT_NON_MATCHING_TEXT_UNITS_SUCCESS
							+ txtFile.getName(), Constants.NOOJ_EXPORT_NON_MATCHING_TEXT_UNITS,
							JOptionPane.INFORMATION_MESSAGE);
	}
}