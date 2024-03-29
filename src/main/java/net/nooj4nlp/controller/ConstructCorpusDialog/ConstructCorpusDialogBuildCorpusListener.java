package net.nooj4nlp.controller.ConstructCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.TextIO;
import net.nooj4nlp.engine.Utils;
import net.nooj4nlp.engine.Zip;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class ConstructCorpusDialogBuildCorpusListener implements ActionListener
{
	private ConstructCorpusDialogController controller;

	public ConstructCorpusDialogBuildCorpusListener(ConstructCorpusDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Check if file to be split exists
		String fileName = this.controller.getFldFileName().getText();
		if (fileName.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.EMPTY_FILENAME_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		File fileToBeSplit = new File(fileName);
		if (!fileToBeSplit.exists())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_OPEN_FILE_MESSAGE + fileName,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Check text fields
		String extension = FilenameUtils.getExtension(fileName);
		if (this.controller.getRdbtnPdf().isSelected())
			extension = "txt";

		if (this.controller.getRdbtnBuildACorpus().isSelected() && this.controller.getFldCorpus().getText().equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ENTER_CORPUS_FILENAME_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (this.controller.getRdbtnStoreAllFiles().isSelected() && this.controller.getFldFolder().getText().equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ENTER_DIRECTORY_FILENAME_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (!this.controller.getRdbtnPdf().isSelected() && this.controller.getFldPerlExpr().getText().equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ENTER_PATTERN_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int textNumber = 1;
		try
		{
			textNumber = Integer.parseInt(this.controller.getFldFileNumber().getText());
		}
		catch (NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ENTER_FIRST_FILE_NUMBER_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Start of analysis
		StringBuffer buffer = new StringBuffer(512);
		List<String> bufferList = null;
		String encodingCode = null;
		String encodingName = null;
		Pattern regexp = null;

		int iLen = 0;

		// Working with PDF
		if (this.controller.getRdbtnPdf().isSelected())
		{
			try
			{
				bufferList = TextIO.loadPdfFileToStrings(fileName);
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_READ_PDF_MESSAGE,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		else
		{
			BufferedReader reader = null;

			if (this.controller.getRdbtnAsciiOrBytemarked().isSelected())
			{
				try
				{
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeSplit), "UTF8"));
				}
				catch (FileNotFoundException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_OPEN_FILE_MESSAGE
							+ fileName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (UnsupportedEncodingException e3)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e3.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_UNSUPPORTED_ENCODING, JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				// Get encoding
				String selectedEncoding = (String) this.controller.getListTextFormats().getSelectedValue();
				Charset enc = Charset.forName(selectedEncoding.substring(0, selectedEncoding.indexOf('[')));
				encodingCode = enc.name();
				encodingName = enc.displayName();

				try
				{
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeSplit), encodingCode));
				}
				catch (FileNotFoundException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_OPEN_FILE_MESSAGE
							+ fileName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				catch (IOException e2)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_READ_FROM_FILE_MESSAGE
							+ fileName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			// Get data into buffer
			try
			{
				for (String line0 = reader.readLine(); line0 != null; line0 = reader.readLine())
				{
					buffer.append(line0);
				}
				reader.close();
			}
			catch (IOException e2)
			{
				try
				{
					if (reader != null)
						reader.close();
				}
				catch (IOException e3)
				{
					// Catch block does not do anything - message below should be written in each case.
				}

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_READ_FROM_FILE_MESSAGE
						+ fileName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			regexp = Pattern.compile(this.controller.getFldPerlExpr().getText(), Pattern.MULTILINE);
		}

		Corpus corpus = null;
		String corpusDirName = null;
		File corpusDir = null;
		String languageName = Launcher.preferences.deflanguage;

		if (this.controller.getRdbtnBuildACorpus().isSelected())
		{
			if (this.controller.getRdbtnAsciiOrBytemarked().isSelected() || this.controller.getRdbtnPdf().isSelected())
			{
				corpus = new Corpus("\n", null, 1, null, null, languageName); // 1 - default encoding
			}
			else
			{
				corpus = new Corpus("\n", null, 2, encodingCode, encodingName, languageName);
			}

			corpusDirName = this.controller.getFldCorpus().getText() + Constants.DIRECTORY_SUFFIX;
			corpusDir = new File(corpusDirName);

			// Attempt to load characters' variants table
			if (corpus.lan.chartable == null)
			{
				if (this.controller.getRdbtnAsciiOrBytemarked().isSelected()
						|| this.controller.getRdbtnPdf().isSelected())
				{
					String chartName = Paths.docDir + System.getProperty("file.separator") + corpus.lan.isoName
							+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
							+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_PATH;
					File chartFile = new File(chartName);
					if (!chartFile.exists())
					{
						chartName = Paths.docDir + System.getProperty("file.separator") + corpus.lan.isoName
								+ System.getProperty("file.separator") + Constants.LEXICAL_ANALYSIS_PATH
								+ System.getProperty("file.separator") + Constants.CHAR_VARIANTS_SUFFIX_PATH;
						chartFile = new File(chartName);
					}

					if (chartFile.exists())
					{
						StringBuilder errMessage = new StringBuilder("");
						try
						{
							if (!corpus.lan.loadCharacterVariants(chartName, errMessage))
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
										Constants.CANNOT_LOAD_CHARACTER_VARIANTS_FILE + chartName,
										Constants.NOOJ_WARNING + errMessage, JOptionPane.INFORMATION_MESSAGE);
								return;
							}
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.CANNOT_LOAD_CHARACTER_VARIANTS_FILE + chartName, Constants.NOOJ_WARNING
											+ errMessage, JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
				}
			}
		}

		if (this.controller.getRdbtnPdf().isSelected())
		{
			for (int i = 0; i < bufferList.size(); i++)
			{
				String rName = null;
				String resName = null;
				File resFile = null;

				// String for formatting the number in file name
				iLen = Integer.toString(bufferList.size()).length();
				String textNumberString = String.format("%0" + iLen + "d", textNumber);

				// Compute text file name - depends on which button is selected
				if (this.controller.getRdbtnStoreAllFiles().isSelected())
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = this.controller.getFldFolder().getText() + System.getProperty("file.separator") + rName;
				}
				else
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = Paths.docDir + System.getProperty("file.separator") + rName;
				}

				BufferedWriter writer = null;
				try
				{
					resFile = new File(resName);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(resFile));
					writer = new BufferedWriter(outputStreamWriter);
					writer.write(bufferList.get(i));
					writer.close();
				}
				catch (FileNotFoundException e1)
				{
					try
					{
						if (writer != null)
							writer.close();
					}
					catch (IOException e2)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
							+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				catch (IOException e1)
				{
					try
					{
						if (writer != null)
							writer.close();
					}
					catch (IOException e2)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
							+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (this.controller.getRdbtnBuildACorpus().isSelected())
				{
					try
					{
						corpus.addTextFile(corpusDirName, resName, null);
						resFile.delete();
					}
					catch (ClassNotFoundException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (BadLocationException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				textNumber++;
			}
		}
		else
		{
			OutputStreamWriter outputStreamWriter = null;

			Matcher mc = regexp.matcher(buffer);
			int start = 0;

			int groupCount = 0;
			while (mc.find())
			{
				groupCount++;
			}

			iLen = Integer.toString(groupCount).length();
			mc = regexp.matcher(buffer);

			while (mc.find())
			{
				int end = mc.start();
				int len = mc.end() - end;

				if (start == end)
					continue;

				String rName = null;
				String resName = null;
				File resFile = null;

				// String for formatting the number in file name
				String textNumberString = String.format("%0" + iLen + "d", textNumber);

				// Compute text file name - depends on which button is selected
				if (this.controller.getRdbtnStoreAllFiles().isSelected())
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = this.controller.getFldFolder().getText() + System.getProperty("file.separator") + rName;
				}
				else
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = Paths.docDir + System.getProperty("file.separator") + rName;
				}

				BufferedWriter writer = null;
				if (this.controller.getRdbtnAsciiOrBytemarked().isSelected())
				{
					try
					{
						resFile = new File(resName);
						outputStreamWriter = new OutputStreamWriter(new FileOutputStream(resFile));
						writer = new BufferedWriter(outputStreamWriter);
					}
					catch (FileNotFoundException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
								+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				else
				{
					try
					{
						// Writes file without appending!
						resFile = new File(resName);
						outputStreamWriter = new OutputStreamWriter(new FileOutputStream(resFile, false), encodingCode);
						writer = new BufferedWriter(outputStreamWriter);
					}
					catch (FileNotFoundException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
								+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					catch (UnsupportedEncodingException e2)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.ENCODING_NOT_SUPPORTED_MESSAGE + resName, Constants.NOOJ_APPLICATION_NAME,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				try
				{
					writer.write(buffer.substring(start, end));
					writer.close();
				}
				catch (IOException e1)
				{
					try
					{
						if (writer != null)
							writer.close();
					}
					catch (IOException e2)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
							+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (this.controller.getRdbtnBuildACorpus().isSelected())
				{
					try
					{
						corpus.addTextFile(corpusDirName, resName, null);
						resFile.delete();
					}
					catch (ClassNotFoundException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (BadLocationException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				start = end + len;
				textNumber++;
			}

			if (start < buffer.length())
			{
				String rName = null;
				String resName = null;
				String textNumberString = String.format("%0" + iLen + "d", textNumber);
				

				File resFile = null;
				String folderPath = this.controller.getFldFolder().getText();

				// Compute text file name - depends on which button is selected
				if (this.controller.getRdbtnStoreAllFiles().isSelected())
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = folderPath + System.getProperty("file.separator") + rName;
				}
				else
				{
					rName = this.controller.getFldBaseName().getText() + textNumberString + "." + extension;
					resName = Paths.docDir + System.getProperty("file.separator") + rName;
				}

				BufferedWriter writer = null;
				if (this.controller.getRdbtnAsciiOrBytemarked().isSelected())
				{
					try
					{
						resFile = new File(resName);
						outputStreamWriter = new OutputStreamWriter(new FileOutputStream(resFile));
						writer = new BufferedWriter(outputStreamWriter);
					}
					catch (FileNotFoundException e1)
					{
						try
						{
							if (outputStreamWriter != null)
								outputStreamWriter.close();
						}
						catch (IOException e2)
						{
							// Catch block does not do anything - message below should be written in each case.
						}

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
								+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				else
				{
					try
					{
						// Writes file without appending!
						resFile = new File(resName);
						outputStreamWriter = new OutputStreamWriter(new FileOutputStream(resFile, false), encodingCode);
						writer = new BufferedWriter(outputStreamWriter);
					}
					catch (FileNotFoundException e1)
					{
						try
						{
							if (outputStreamWriter != null)
								outputStreamWriter.close();
						}
						catch (IOException e2)
						{
							// Catch block does not do anything - message below should be written in each case.
						}

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
								+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					catch (UnsupportedEncodingException e2)
					{
						try
						{
							if (outputStreamWriter != null)
								outputStreamWriter.close();
						}
						catch (IOException e3)
						{
							// Catch block does not do anything - message below should be written in each case.
						}

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.ENCODING_NOT_SUPPORTED_MESSAGE + resName, Constants.NOOJ_APPLICATION_NAME,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				try
				{
					writer.write(buffer.substring(start));

					
				}
				catch (IOException e1)
				{
					try
					{
						if (writer != null)
							writer.close();
					}
					catch (IOException e3)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					try
					{
						if (outputStreamWriter != null)
							outputStreamWriter.close();
					}
					catch (IOException e4)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
							+ resName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				try
				{
					if (writer != null)
						writer.close();
				}
				catch (IOException e3)
				{
					// Catch block does not do anything - message below should be written in each case.
				}

				try
				{
					if (outputStreamWriter != null)
						outputStreamWriter.close();
				}
				catch (IOException e4)
				{
					// Catch block does not do anything - message below should be written in each case.
				}

				if (this.controller.getRdbtnBuildACorpus().isSelected())
				{
					try
					{
						corpus.addTextFile(corpusDirName, resName, null);
						resFile.delete();
					}
					catch (ClassNotFoundException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (BadLocationException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				textNumber++;
			}
		}

		// Saving corpus
		if (corpus != null)
		{
			try
			{
				corpus.saveIn(corpusDirName);
				String destinationZipFilePath = this.controller.getFldCorpus().getText();
				Zip.compressDir(corpusDirName, destinationZipFilePath);
				Utils.deleteDir(corpusDir);

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SUCCESS_CORPUS_MESSAGE
						+ destinationZipFilePath + Constants.CONTAINS_MESSAGE + (textNumber - 1)
						+ Constants.FILES_MESSAGE, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_CORPUS_SAVE_IN, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SUCCESS_MESSAGE + (textNumber - 1)
					+ Constants.FILES_CREATED_MESSAGE + this.controller.getFldFolder().getText(),
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}
}
