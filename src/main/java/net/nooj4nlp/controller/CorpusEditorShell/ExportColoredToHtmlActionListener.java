package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

public class ExportColoredToHtmlActionListener implements ActionListener
{
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private boolean corpus;

	private List<Color> listOfColors;
	private List<Integer> absoluteBeginAddresses;
	private List<Integer> absoluteEndAddresses;
	private List<String> listOfConcordanceFiles;

	/**
	 * This listener is constructed either from text or corpus. One of controllers is always null.
	 * 
	 * @param corpusController
	 * @param textController
	 */
	public ExportColoredToHtmlActionListener(CorpusEditorShellController corpusController,
			TextEditorShellController textController)
	{
		super();
		this.corpusController = corpusController;
		this.textController = textController;

		if (corpusController != null && corpusController.getShell() != null)
		{
			this.corpus = true;

			listOfConcordanceFiles = corpusController.getListOfConcordanceFiles();
			listOfColors = corpusController.getListOfColors();
			absoluteBeginAddresses = corpusController.getAbsoluteBeginAddresses();
			absoluteEndAddresses = corpusController.getAbsoluteEndAddresses();
		}
		else if (textController != null)
		{
			this.corpus = false;

			listOfColors = textController.getListOfColors();
			absoluteBeginAddresses = textController.getAbsoluteBeginAddresses();
			absoluteEndAddresses = textController.getAbsoluteEndAddresses();
			// This list doesn't exist in textController
			listOfConcordanceFiles = null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Actions are different if corpus or text is active
		if (corpus)
		{
			String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
			String dName;

			if (Launcher.projectMode)
			{
				File dir = new File(Paths.projectDir);
				dName = dir.getParent(); // Outside of the project .nop file
			}
			else
			{
				File dir = new File(corpusController.getFullPath());
				dName = dir.getParent(); // Beside the corpus .noc file
			}

			String lastFullName = "";
			Ntext myLastText = null;

			for (String fName : corpusController.getCorpus().listOfFileTexts)
			{
				String fullName = corpusDirName + System.getProperty("file.separator") + fName;
				Ntext myText = null;

				if (fullName.equals(lastFullName))
					myText = myLastText;
				else
				{
					try
					{
						myText = Ntext.loadForCorpus(fullName, corpusController.getCorpus().lan,
								corpusController.getCorpus().multiplier);
						lastFullName = fullName;

						if (corpusController.getTextController() == null)
						{
							TextEditorShell textShell = new TextEditorShell(corpusController, myText, fullName,
									myText.DelimPattern, false);
							Launcher.getDesktopPane().add(textShell);
							corpusController.setTextController(textShell.getTextController());
							textShell.setVisible(true);
						}
						else
						{
							corpusController.getTextController().setMyText(myText);
							corpusController.getTextController().setFileToBeOpenedOrImported(new File(fullName));
							corpusController.getTextController().resetShellText();
						}

						if (myText == null)
							continue;

						String coloredFName = dName + System.getProperty("file.separator")
								+ FilenameUtils.removeExtension(fName) + ".html";

						String textBuffer = myText.buffer;

						if (listOfConcordanceFiles != null)
						{
							// If list of concordance files contains current file, save it as a colored text...
							if (listOfConcordanceFiles.contains(fName))
								saveColoredTextFromCorpus(fName, coloredFName, textBuffer);
							// ...otherwise, save it as an ordinary text.
							else
								saveNonColoredText(coloredFName, textBuffer);
						}
						else
							saveNonColoredText(coloredFName, textBuffer);
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.ERROR_EXPORT_COLORED_TEXTS_MESSAGE + corpusDirName,
								Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SUCCESS_EXPORT_COLORED_TEXTS_MESSAGE
						+ corpusDirName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		else
		{
			File file = textController.getFileToBeOpenedOrImported();

			String fName = file.getName();
			String coloredFName = FilenameUtils.removeExtension(file.getAbsolutePath()) + ".html";

			Ntext myText = textController.getMyText();

			saveColoredText(fName, coloredFName, myText.buffer);

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SUCCESS_EXPORT_COLORED_TEXT_MESSAGE
					+ coloredFName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	/**
	 * Helper function used for printing texts that come from corpus and have colored parts inside.
	 * 
	 * @param fName
	 * @param coloredFName
	 * @param textBuffer
	 */
	private void saveColoredTextFromCorpus(String fName, String coloredFName, String textBuffer)
	{
		ArrayList<ColorData> colorDataList = new ArrayList<ColorData>();

		// Data about current file - it's colored parts - is kept in list of ColorData objects
		for (int i = 0; i < listOfConcordanceFiles.size(); i++)
		{
			if (listOfConcordanceFiles.get(i).equals(fName))
			{
				ColorData cd = new ColorData(listOfColors.get(i), absoluteBeginAddresses.get(i),
						absoluteEndAddresses.get(i));
				colorDataList.add(cd);
			}
		}
		// Data about colors is sorted, for easier text painting
		Collections.sort(colorDataList);

		createColoredFile(colorDataList, coloredFName, textBuffer);
	}

	/**
	 * Helper function used for printing text that has colored parts inside.
	 * 
	 * @param fName
	 * @param coloredFName
	 * @param textBuffer
	 */
	private void saveColoredText(String fName, String coloredFName, String textBuffer)
	{
		ArrayList<ColorData> colorDataList = new ArrayList<ColorData>();

		for (int i = 0; i < absoluteBeginAddresses.size(); i++)
		{
			ColorData cd = new ColorData(listOfColors.get(i), absoluteBeginAddresses.get(i),
					absoluteEndAddresses.get(i));
			colorDataList.add(cd);
		}
		// Data about colors is sorted, for easier text painting
		Collections.sort(colorDataList);

		createColoredFile(colorDataList, coloredFName, textBuffer);
	}

	/**
	 * Helper function, used for creating colored file with data about it given in the colorDataList.
	 * 
	 * @param colorDataList
	 * @param coloredFName
	 * @param textBuffer
	 */
	private void createColoredFile(List<ColorData> colorDataList, String coloredFName, String textBuffer)
	{
		File file = new File(coloredFName);
		try
		{
			file.createNewFile();

			PrintWriter pw = new PrintWriter(coloredFName);

			pw.write("<pre>");

			int currentChar = 0;

			for (int j = 0; j < colorDataList.size(); j++)
			{
				ColorData cd = colorDataList.get(j);

				pw.append(textBuffer.substring(currentChar, cd.getBeginAddress()));

				pw.append("<font style=\"color:#");

				String rgb = Integer.toHexString(cd.getColor().getRGB());
				rgb = rgb.substring(2, rgb.length());
				pw.append(rgb);
				pw.append("\">");

				pw.append(textBuffer.substring(cd.getBeginAddress(), cd.getEndAddress()));
				pw.append("</font>");

				currentChar = cd.getEndAddress();
			}
			// Add the last part of buffer.
			pw.append(textBuffer.substring(currentChar));

			pw.append("</pre>");

			pw.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_EXPORT_COLORED_TEXT_MESSAGE
					+ coloredFName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	/**
	 * Helper function used for printing texts that do not have any colored parts inside.
	 * 
	 * @param fullName
	 * @param textBuffer
	 */
	private void saveNonColoredText(String coloredFName, String textBuffer)
	{
		File file = new File(coloredFName);
		try
		{
			file.createNewFile();
			PrintWriter pw = new PrintWriter(coloredFName);

			pw.write("<pre>");
			pw.append(textBuffer);
			pw.append("</pre>");

			pw.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_EXPORT_NONCOLORED_TEXT_MESSAGE
					+ coloredFName, Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	/**
	 * Helper class, used for comparing object made of data about text parts that need to be colored.
	 * 
	 */
	private class ColorData implements Comparable<Object>
	{
		private Color color;
		private Integer beginAddress;
		private Integer endAddress;

		public ColorData(Color c, Integer ba, Integer ea)
		{
			color = c;
			beginAddress = ba;
			endAddress = ea;
		}

		@Override
		public int compareTo(Object o)
		{
			ColorData other = (ColorData) o;

			if (beginAddress < other.beginAddress)
				return -1;
			else if (beginAddress > other.beginAddress)
				return 1;
			else
				return 0;
		}

		public Color getColor()
		{
			return color;
		}

		public Integer getBeginAddress()
		{
			return beginAddress;
		}

		public Integer getEndAddress()
		{
			return endAddress;
		}
	}
}
