package net.nooj4nlp.controller.StatsShell;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.helper.PenAttributes;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.StatsShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Controller class of Statistic Shell.
 */

public class StatsShellController
{
	// shell
	private StatsShell statsShell;

	// controllers
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private ConcordanceShellController concordanceController;

	// drawing variables
	private HashMap<String, PenAttributes<Color, Float>> pens;
	private float[] discontiniousArray;
	private Font font;
	private Color brush = Color.BLACK;

	// variables
	private HashMap<String, Double> tfIDfAllTerms;
	private HashMap<String, ArrayList<Object>> distances;
	private ArrayList<Object> freqSortedTerms;
	private ArrayList<ArrayList<Object>> allSS;
	private ArrayList<HashMap<String, Double>> tfIDfFreqInEachText;
	private String[] fileNames;
	private String[] tfIDfFileNames;
	private long[] freqInCorpus;
	private long[][] freqInEachText;
	private int nbOfColors;
	private int sizeOfCorpus;
	private int freqNumberOfTerms;
	private int freqMaxFrequency;
	private int[] tfIDfSizeOfEachText;
	private int[] sizeOfEachText;

	// flags for determination if tables are updated or not
	private boolean tfIDf_firstTime;
	private boolean distancesFirstTime;

	/**
	 * Constructor.
	 * 
	 * @param corpusController
	 *            - corpus controller from Concordance
	 * @param textController
	 *            - text controller from Concordance
	 * @param concordanceController
	 *            - controller of Concordance
	 * @param statsShell
	 *            - window shell of Statistics
	 */

	public StatsShellController(CorpusEditorShellController corpusController, TextEditorShellController textController,
			ConcordanceShellController concordanceController, StatsShell statsShell)
	{
		// setting controllers and shell
		this.corpusController = corpusController;
		this.textController = textController;
		this.concordanceController = concordanceController;
		this.statsShell = statsShell;

		// initializing font and drawing variables
		font = Launcher.preferences.TFont;
		pens = new HashMap<String, PenAttributes<Color, Float>>();
		pens.put("pen", new PenAttributes<Color, Float>(Color.BLACK, 1));
		pens.put("pen2", new PenAttributes<Color, Float>(Color.BLACK, 2));
		pens.put("penr", new PenAttributes<Color, Float>(Color.RED, 1));
		pens.put("pen2r", new PenAttributes<Color, Float>(Color.RED, 2));

		discontiniousArray = new float[] { 8.0F, 8.0F, 2.0F, 8.0F };

		// reset all results
		freqSortedTerms = null;
		allSS = null;
		tfIDfFreqInEachText = null;
		tfIDfSizeOfEachText = null;
		tfIDfAllTerms = null;
		distances = null;

		// select adequate starting radio button and fill the Card Layout with panels
		// this was not done before because we wanted to evade paint conflicts
		JPanel displayPanel = this.statsShell.getDisplayPanel();
		displayPanel.add(this.statsShell.getPanelOfFrequencies(), Constants.FREQUENCIES);
		this.statsShell.getRbFrequencies().setSelected(true);
		changePanel(displayPanel, Constants.FREQUENCIES);
		displayPanel.add(this.statsShell.getPanelOfStandardScore(), Constants.STANDARD_SCORE);
		displayPanel.add(this.statsShell.getPanelOfRelevances(), Constants.RELEVANCES);
		displayPanel.add(this.statsShell.getPanelOfSimilarities(), Constants.SIMILARITY);

		tfIDf_firstTime = true;
		distancesFirstTime = true;
	}

	/**
	 * Function for substring or concatenating of text, depending of given number. If the number is larger than size of
	 * the text, function adds white space to the end of text, otherwise, returns substring starting from index equal to
	 * the given number.
	 * 
	 * @param text
	 *            - text to work on
	 * @param len
	 *            - desired number
	 * @return - a new text
	 */

	private String lenFormat(String text, int len)
	{
		int textLength = text.length();

		if (text == null || text.equals(""))
			return text;

		else if (textLength == len)
			return text;

		else if (textLength > len)
			return text.substring(len);

		else
		{
			String concatString = "";

			for (int i = 0; i < textLength - len; i++)
				concatString += ' ';

			return text + concatString;
		}
	}

	/**
	 * Function computes frequency of concordance's terms(sequences).
	 */

	private void computeFrequency()
	{
		// get terms and frequencies from concordance
		freqMaxFrequency = 1;
		HashMap<String, Integer> hTerms = new HashMap<String, Integer>();

		DefaultTableModel concordanceTableModel = (DefaultTableModel) concordanceController.getConcordanceTable()
				.getModel();
		for (int i = 0; i < concordanceTableModel.getRowCount(); i++)
		{
			// get term
			String term = concordanceTableModel.getValueAt(i, 2).toString();

			if (hTerms.containsKey(term))
			{
				int freq = hTerms.get(term);
				hTerms.put(term, freq + 1);
				if (freq + 1 > freqMaxFrequency)
					freqMaxFrequency = freq + 1;
			}

			else
				hTerms.put(term, 1);
		}

		freqNumberOfTerms = hTerms.size();

		// sort frequencies high to low
		freqSortedTerms = new ArrayList<Object>();

		for (String term : hTerms.keySet())
		{
			int freq = hTerms.get(term);
			int i;

			for (i = 0; i < freqSortedTerms.size(); i += 2)
			{
				int cFreq = (Integer) freqSortedTerms.get(i + 1);
				if (cFreq >= freq)
					continue;
				break;
			}

			if (i < freqSortedTerms.size())
			{
				freqSortedTerms.add(i, term);
				freqSortedTerms.add(i + 1, freq);
			}
			else
			{
				freqSortedTerms.add(term);
				freqSortedTerms.add(freq);
			}
		}
	}

	/**
	 * Function draws frequencies of concordance's sequences to Stats panel.
	 */

	public void paintFrequency(Graphics g)
	{
		// 2D graphics for anti aliasing
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		DrawingJPanel parentPanel = this.statsShell.getPanelOfFrequencies();

		// Repaints graphics.
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, parentPanel.getWidth(), parentPanel.getHeight());

		g2D.setColor(pens.get("pen").color);
		g2D.setStroke(new BasicStroke(pens.get("pen").stroke));

		// axis x
		g2D.drawLine(50, 550, 795, 550); // x
		g2D.drawLine(795, 550, 785, 547);
		g2D.drawLine(795, 550, 785, 553);

		g2D.setFont(font);
		g2D.setColor(brush);

		g2D.drawString("Rank", 763, 568);

		int numberOfTerms = freqNumberOfTerms;

		if (numberOfTerms > 30)
			numberOfTerms = 30;

		int width = 700 / numberOfTerms;

		g2D.setColor(pens.get("pen").color);
		g2D.setStroke(new BasicStroke(pens.get("pen").stroke));

		// axis y
		g2D.drawLine(50, 5, 50, 550); // y
		g2D.drawLine(50, 5, 47, 15);
		g2D.drawLine(50, 5, 53, 15);

		g2D.setFont(font);
		g2D.setColor(brush);
		g2D.drawString("Frequency", 60, 25);

		int yGrad;
		if (freqMaxFrequency < 10)
			yGrad = 1;
		else if (freqMaxFrequency < 100)
			yGrad = 10;
		else if (freqMaxFrequency < 1000)
			yGrad = 100;
		else if (freqMaxFrequency < 10000)
			yGrad = 1000;
		else if (freqMaxFrequency < 100000)
			yGrad = 10000;
		else if (freqMaxFrequency < 1000000)
			yGrad = 100000;
		else
			yGrad = 100000;

		for (int i = 1; yGrad * i < freqMaxFrequency; i++)
		{
			int y = 550 - (int) (yGrad * i * 500.0 / freqMaxFrequency);
			g2D.drawLine(47, y, 53, y); // y full graduations
			g2D.drawString(String.valueOf(yGrad * i), 30, y + 12);

			y = 550 - (int) (500.0 * yGrad * (i - 0.5) / freqMaxFrequency);
			g2D.drawLine(47, y, 53, y); // demi-graduations, e.g. 1.5, 2.5
		}

		g2D.drawString(String.valueOf(freqMaxFrequency), 30, 45); // max frequency

		BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
				discontiniousArray, 0.0f);
		g2D.setStroke(dashed);

		g2D.drawLine(50, 50, 795, 50); // dash line for max frequency

		// display every term
		for (int i = 0; i < freqSortedTerms.size(); i += 2)
		{
			double angleOfRotation = Math.PI / 2;
			String term = freqSortedTerms.get(i).toString();
			int freq = (Integer) freqSortedTerms.get(i + 1);

			int x = 50 + i / 2 * width + width;
			int y = 550 - (int) (freq * 500.0 / freqMaxFrequency);

			g2D.setColor(pens.get("pen2r").color);
			g2D.setStroke(new BasicStroke(pens.get("pen2r").stroke));

			g2D.translate(50 + width * (i / 2 + 1), 545);
			g2D.rotate(-angleOfRotation);

			g2D.setFont(font);
			g2D.setColor(brush);
			g2D.drawString(term, 4, 14); // x label
			g2D.rotate(angleOfRotation);
			g2D.translate(-50 - width * (i / 2 + 1), -545);

			g2D.setColor(pens.get("pen2r").color);
			g2D.setStroke(new BasicStroke(pens.get("pen2r").stroke));

			// histogram bar
			g2D.drawLine(x, y, x, 550);
			g2D.drawLine(x - 5, y, x + 5, y);

			// x graduation
			if (i == 0 || i == 8 || i == 18)
			{
				String label = String.valueOf(i / 2 + 1);
				g2D.setFont(font);
				g2D.setColor(brush);
				g2D.drawString(label, x, 565);
			}
		}
	}

	/**
	 * Function implements export button function of Frequencies panel.
	 */

	private void createFrequencyReport()
	{
		if (freqSortedTerms == null)
			return;

		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		// only text files
		jFileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("Text (*.txt)", Constants.TXT_EXTENSION);
		jFileChooser.setFileFilter(filter);

		int result = jFileChooser.showOpenDialog(statsShell);

		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jFileChooser.getSelectedFile();

			String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();
			String inputFileName = FilenameUtils.removeExtension(selectedFile.getName()) + "."
					+ Constants.TXT_EXTENSION;
			String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

			File newFile = new File(pathOfInputFile);
			// if file exists, overwrite it
			if (newFile.exists())
			{
				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
						+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;
			}

			PrintWriter pw = null;

			// create the file and tie print writer to the file
			try
			{
				pw = new PrintWriter(pathOfInputFile);
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_CANNOT_CREATE_REPORT
						+ selectedFile.getName() + "\r" + e.getMessage(),
						Constants.STATISTICS_CANNOT_SAVE_REPORT_TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// get table model of concordance...
			DefaultTableModel concordanceTableModel = (DefaultTableModel) concordanceController.getConcordanceTable()
					.getModel();

			if (corpusController != null && corpusController.getShell() != null)
			{
				pw.write("Corpus " + corpusController.getFullPath() + "\n\n");
				pw.write("Corpus contains "
						+ ((DefaultTableModel) corpusController.getShell().getTableTexts().getModel()).getRowCount()
						+ "texts.\n\n");

				// compute corpus size
				String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
				sizeOfCorpus = 0;

				Corpus corpus = corpusController.getCorpus();

				for (String fileName : corpus.listOfFileTexts)
				{
					String fullPath = corpusDirName + System.getProperty("file.separator") + fileName;
					Ntext myText = null;
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

					if (myText == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_CORRUPTED_CORPUS
								+ fullPath, Constants.NOOJ_PROBLEM, JOptionPane.ERROR_MESSAGE);
						continue;
					}

					sizeOfCorpus += myText.buffer.length();
				}

				pw.write("Corpus contains " + sizeOfCorpus + " characters.\n\n");
				pw.write("Concordance has " + concordanceTableModel.getRowCount() + " matches.\n\n");
			}

			else
			{
				pw.write("Text " + textController.getFileToBeOpenedOrImported().getAbsolutePath() + "\n\n");
				pw.write("Text contains " + textController.getMyText().buffer.length() + " characters.\n\n");
				pw.write("Concordance has " + concordanceTableModel.getRowCount() + " matches.\n\n");
			}

			pw.write("Rank\tTerm\tFrequency\n");

			for (int i = 0; i < freqSortedTerms.size(); i += 2)
			{
				String term = freqSortedTerms.get(i).toString();
				int freq = (Integer) freqSortedTerms.get(i + 1);
				pw.write(String.valueOf(i / 2 + 1) + "\t" + term + "\t" + freq + "\n");
			}

			pw.write("\n");
			pw.close();

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.STATISTICS_FREQUENCY_REPORT_SAVED_TO_A_FILE + newFile.getName(),
					Constants.STATISTICS_FREQUENCY_REPORT_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Function implements export button function of Standard Score panel.
	 */

	private void createStandardScoreReport()
	{
		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		// only text files
		jFileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("Text (*.txt)", Constants.TXT_EXTENSION);
		jFileChooser.setFileFilter(filter);

		int result = jFileChooser.showOpenDialog(statsShell);

		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jFileChooser.getSelectedFile();

			String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();
			String inputFileName = FilenameUtils.removeExtension(selectedFile.getName()) + "."
					+ Constants.TXT_EXTENSION;
			String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

			File newFile = new File(pathOfInputFile);
			// if file exists, overwrite it
			if (newFile.exists())
			{
				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
						+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;
			}

			PrintWriter pw = null;

			// create the file and tie print writer to the file
			try
			{
				pw = new PrintWriter(pathOfInputFile);
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_CANNOT_CREATE_REPORT
						+ selectedFile.getName() + "\r" + e.getMessage(),
						Constants.STATISTICS_CANNOT_SAVE_REPORT_TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// first compute global measures: NT, NM, SD
			int nt = fileNames.length;

			if (corpusController != null)
			{
				pw.write("Corpus " + corpusController.getFullPath() + "\n");
				pw.write("\n");

				pw.write(corpusController.getTableTexts().getModel().getRowCount() + " texts\t");
				pw.write(sizeOfCorpus + " characters\n");
				pw.write("\n");

				pw.write("NT: \tnumber of texts in corpus = " + nt + "\n");
				pw.write("NM: \tnumber of matches in corpus\n");
				pw.write("AF: \tabsolute frequency in each text\n");
				pw.write("RS: \trelative size of each text (in chars) = size of each text / total size of corpus\n");
				pw.write("EF: \texpected frequency in each text = NM * RS\n");
				pw.write("SD: \tstandard deviation = Sqr (Sum (AF - EF)^2) / NT)\n");
				pw.write("NSD: \tnormalized standard deviation = SD/NM\n");
				pw.write("SS: \tstandard score = (AF - EF) / SD\n");
				pw.write("\n");
				pw.write("\n");

				pw.write(lenFormat("Text", 15) + "\t");
				pw.write("Size   \t");
			}

			else
			{
				pw.write("Text " + textController.getTextName() + "\n");
				pw.write("\n");

				pw.write("20 parts\t");
				pw.write(sizeOfCorpus + " characters\n");
				pw.write("\n");

				pw.write("NT: \tnumber of parts in text = 20\n");
				pw.write("NM: \tnumber of matches in text\n");
				pw.write("AF: \tabsolute frequency in each part\n");
				pw.write("RS: \trelative size of each part = 1/20\n");
				pw.write("EF: \texpected frequency in each part = AF * RS\n");
				pw.write("SD: \tstandard deviation = Sqr (Sum ((AF - EF)^2 / NT))\n");
				pw.write("NSD: \tnormalized standard deviation = SD/NM\n");
				pw.write("SS: \tstandard score = (AF - EF) / SD\n");
				pw.write("\n");
				pw.write("\n");
			}

			// compute all NMs and SDs (each color has its own)
			double[] sds = new double[freqInEachText.length];
			double[] nsds = new double[freqInEachText.length];

			for (int iColor = 0; iColor < freqInEachText.length; iColor++)
			{
				// compute NM, SD
				long nm = 0;
				double sd = 0.0;

				for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
				{
					long af = freqInEachText[iColor][iTxt];
					nm += af;
					double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;

					sd += (af - ef) * (af - ef) / nt;
				}

				sds[iColor] = Math.sqrt(sd);

				// compute NSD
				double nsd = 0.0;
				for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
				{
					long af = freqInEachText[iColor][iTxt];
					double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;
					nsd += ((af - ef) / nm) * ((af - ef) / nm) / nt;
				}

				nsds[iColor] = Math.sqrt(nsd);

				if (freqInEachText.length > 1)
				{
					pw.write("NM[" + (iColor + 1) + "] = " + nm + "\n");
					pw.write("SD[" + (iColor + 1) + "] = " + sds[iColor] + "\n");
					pw.write("NSD[" + (iColor + 1) + "] = " + nsds[iColor] + "\n");
				}
				else
				{
					pw.write("NM = " + nm + "\n");
					pw.write("SD = " + sds[iColor] + "\n");
					pw.write("NSD = " + nsds[iColor] + "\n");
				}
			}

			pw.write(lenFormat("Part", 15) + "\t");
			pw.write("Size   \t");

			for (int iColor = 0; iColor < freqInEachText.length; iColor++)
			{
				if (freqInEachText.length > 1)
				{
					pw.write("AF[" + (iColor + 1) + "]\t");
					pw.write("EF[" + (iColor + 1) + "]\t");
					pw.write("SS[" + (iColor + 1) + "]\t");
				}
				else
				{
					pw.write("AF\t");
					pw.write("EF\t");
					pw.write("SS\t");
				}
			}

			pw.write("\n");

			for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
			{
				pw.write(lenFormat(fileNames[iTxt], 15) + "\t");
				pw.write(sizeOfEachText[iTxt] + "\t");

				for (int iColor = 0; iColor < freqInEachText.length; iColor++)
				{
					// AF = Absolute Frequency
					long af = freqInEachText[iColor][iTxt];
					pw.write(af + "\t");

					// EF = Expected Frequency
					double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;
					pw.write(ef + "\t");

					// SS = Standard Score
					double ss = (1.0 * freqInEachText[iColor][iTxt] - ef) / sds[iColor];
					pw.write(ss + "\t");
				}

				pw.write("\n");
			}

			pw.close();
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.STATISTICS_STANDARD_SCORE_SAVED_TO_A_FILE + newFile.getName(),
					Constants.STATISTICS_STANDARD_SCORE_REPORT_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Function implements export button function of Relevance panel.
	 */

	private void createTfIDfReport()
	{
		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		// only text files
		jFileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("Text (*.txt)", Constants.TXT_EXTENSION);
		jFileChooser.setFileFilter(filter);

		int result = jFileChooser.showOpenDialog(statsShell);

		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jFileChooser.getSelectedFile();

			String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();
			String inputFileName = FilenameUtils.removeExtension(selectedFile.getName()) + "."
					+ Constants.TXT_EXTENSION;
			String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

			File newFile = new File(pathOfInputFile);
			// if file exists, overwrite it
			if (newFile.exists())
			{
				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
						+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;
			}

			PrintWriter pw = null;

			// create the file and tie print writer to the file
			try
			{
				pw = new PrintWriter(pathOfInputFile);
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(
						Launcher.getDesktopPane(),
						Constants.STATISTICS_CANNOT_SAVE_RELEVANCE_REPORT + selectedFile.getName() + "\r"
								+ e.getMessage(), Constants.STATISTICS_CANNOT_SAVE_RELEVANCE_REPORT_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// first compute global measures: NT, NM, SD
			if (corpusController != null)
			{
				pw.write("Corpus " + corpusController.getFullPath() + "\n");
				pw.write("\n");

				pw.write(corpusController.getTableTexts().getModel().getRowCount() + " documents\t");
				pw.write("\n");
			}
			else
			{
				pw.write("Text " + textController.getFileToBeOpenedOrImported().getAbsolutePath() + "\n");
				pw.write("\n");

				pw.write("20 parts\t");
				pw.write("\n");
			}

			pw.write("ND: \tnumber of documents in corpus = " + tfIDfFileNames.length + "\n");
			pw.write("ND(T): \tnumber of documents in which term T occurs\n");
			pw.write("C(T,D): \tnumber of occurrences of term T in document D\n");
			pw.write("TF: \tnormalized frequency of term T in document D = C(T,D) * Term size / Document size\n");
			pw.write("IDF: \tinverse document frequency = ND / ND(T)\n");
			pw.write("Relevance:\t TF-IDF = Weight of term T in document D = TF * log10 (ND / IDF)\n");
			pw.write("\n");

			for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
			{
				HashMap<String, Double> htfIDf = tfIDfFreqInEachText.get(iTxt);

				for (String term : htfIDf.keySet())
				{
					BigDecimal convertedValue = BigDecimal.valueOf(htfIDf.get(term));
					String tfIDFString = String.valueOf(convertedValue.setScale(20, BigDecimal.ROUND_HALF_UP));

					pw.write(tfIDfFileNames[iTxt] + ";" + term + ";" + tfIDFString + "\n");
				}
			}

			pw.close();
			JOptionPane
					.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_RELEVANCE_SAVED_TO_A_FILE
							+ newFile.getName(), Constants.STATISTICS_RELEVANCE_REPORT_SUCCESS,
							JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Function implements export button function of Similarities panel.
	 */

	private void createDistReport()
	{
		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		// only text files
		jFileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("Text (*.txt)", Constants.TXT_EXTENSION);
		jFileChooser.setFileFilter(filter);

		int result = jFileChooser.showOpenDialog(statsShell);

		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jFileChooser.getSelectedFile();

			String parentPath = jFileChooser.getCurrentDirectory().getAbsolutePath();
			String inputFileName = FilenameUtils.removeExtension(selectedFile.getName()) + "."
					+ Constants.TXT_EXTENSION;
			String pathOfInputFile = parentPath + System.getProperty("file.separator") + inputFileName;

			File newFile = new File(pathOfInputFile);
			// if file exists, overwrite it
			if (newFile.exists())
			{
				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), inputFileName + " already exists."
						+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;
			}

			PrintWriter pw = null;

			// create the file and tie print writer to the file
			try
			{
				pw = new PrintWriter(pathOfInputFile);
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(
						Launcher.getDesktopPane(),
						Constants.STATISTICS_CANNOT_SAVE_SIMILARITY_REPORT + selectedFile.getName() + "\r"
								+ e.getMessage(), Constants.STATISTICS_CANNOT_SAVE_SIMILARITY_REPORT_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// first compute global measures: NT, NM, SD
			if (corpusController != null)
			{
				pw.write("Corpus " + corpusController.getFullPath() + "\n\n");

				pw.write(corpusController.getTableTexts().getModel().getRowCount() + " documents\t\n");
			}
			else
			{
				pw.write("Text " + textController.getFileToBeOpenedOrImported().getAbsolutePath() + "\n\n");

				pw.write("20 parts\t\n");
			}

			pw.write("ND: \tnumber of documents in corpus = " + tfIDfFileNames.length + "\n");
			pw.write("ND(T): \tnumber of documents in which term T occurs\n");
			pw.write("C(T,D): \tnumber of occurrences of term T in document D\n");
			pw.write("TF: \tnormalized frequency of term T in document D = C(T,D) * Term size / Document size\n");
			pw.write("IDF: \tinverse document frequency = ND / ND(T)\n");
			pw.write("TF-IDF: \tWeight of term T in document D = TF * log10 (ND / IDF)\n");
			pw.write("Similarity: \tDistance between TFIDF of term1 and TFIDF of term2 DIST = d(TFIDF1,TFIDF2)\n\n");

			// header: each term
			for (String term1 : distances.keySet())
				pw.write("\t" + term1);
			pw.write("\n");

			for (String term1 : distances.keySet())
			{
				pw.write(term1 + "\t");

				ArrayList<String> listOfDists = new ArrayList<String>();
				listOfDists.add(term1);

				ArrayList<Object> dist1 = distances.get(term1);

				for (String term2 : distances.keySet())
				{
					double dist2 = -1.0;

					for (int i = 0; i < dist1.size(); i += 2)
					{
						String term3 = dist1.get(i).toString();

						if (term3.equals(term2))
						{
							dist2 = (Double) dist1.get(i + 1);
							break;
						}
					}

					pw.write(String.valueOf(dist2));
					pw.write("\t");
				}

				pw.write("\n");
			}

			pw.close();
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_SIMILARITY_SAVED_TO_A_FILE
					+ newFile.getName(), Constants.STATISTICS_SIMILARITY_REPORT_SUCCESS,
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Function term frequencies of two terms in texts.
	 */

	private void computeTfidf()
	{
		tfIDfFreqInEachText = null;
		tfIDfSizeOfEachText = null;
		tfIDfAllTerms = new HashMap<String, Double>();
		int nbOfDocuments;
		DefaultTableModel concordanceTableModel = (DefaultTableModel) concordanceController.getConcordanceTable()
				.getModel();

		if (corpusController != null)
		{
			String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
			DefaultTableModel tableModel = (DefaultTableModel) corpusController.getTableTexts().getModel();
			nbOfDocuments = tableModel.getRowCount();

			// get data from concordance
			tfIDfSizeOfEachText = new int[nbOfDocuments];
			tfIDfFileNames = new String[nbOfDocuments];

			// compute size of each text
			int iTxt = 0;
			Corpus corpus = corpusController.getCorpus();

			for (String fileName : corpus.listOfFileTexts)
			{
				String fullPath = corpusDirName + System.getProperty("file.separator") + fileName;
				Ntext myText = null;

				try
				{
					myText = Ntext.loadJustBufferForCorpus(fullPath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (myText == null)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_CORRUPTED_CORPUS
							+ fullPath, Constants.NOOJ_PROBLEM, JOptionPane.ERROR_MESSAGE);
					continue;
				}

				int sizeOfMyText = myText.buffer.length();
				tfIDfSizeOfEachText[iTxt] = sizeOfMyText;
				tfIDfFileNames[iTxt] = FilenameUtils.removeExtension(fileName);
				sizeOfCorpus += sizeOfMyText;
				iTxt++;
			}

			// compute number of occurrences of each term in each text
			tfIDfFreqInEachText = new ArrayList<HashMap<String, Double>>();

			for (int i = 0; i < nbOfDocuments; i++)
				tfIDfFreqInEachText.add(new HashMap<String, Double>());

			for (int i = 0; i < concordanceTableModel.getRowCount(); i++)
			{
				Object tag = concordanceTableModel.getValueAt(i, 5);

				if (tag == null)
					continue;

				// get filename
				String fileName = FilenameUtils.removeExtension(concordanceTableModel.getValueAt(i, 0).toString());
				iTxt = -1;

				for (int j = 0; j < tfIDfFileNames.length; j++)
				{
					if (fileName.equals(tfIDfFileNames[j]))
					{
						iTxt = j;
						break;
					}
				}

				if (iTxt == -1)
					continue;

				// get term and add its number of occurrences
				Map<String, Double> hFreq = tfIDfFreqInEachText.get(iTxt);
				String term = concordanceTableModel.getValueAt(i, 2).toString();

				if (hFreq.containsKey(term))
				{
					int freq = hFreq.get(term).intValue();
					hFreq.put(term, new Double(freq + 1));
				}
				else
				{
					hFreq.put(term, new Double(1));
					if (!tfIDfAllTerms.containsKey(term))
						tfIDfAllTerms.put(term, new Double(0));
				}
			}
		}

		else
		{
			// compute nb of partitions
			nbOfDocuments = 20;
			int sizeOfCorpus = textController.getMyText().buffer.length();
			tfIDfSizeOfEachText = new int[20];
			tfIDfSizeOfEachText = new int[20];
			tfIDfFileNames = new String[20];

			for (int i = 0; i < 20; i++)
			{
				tfIDfFileNames[i] = "Part #" + (i + 1);
				tfIDfSizeOfEachText[i] += sizeOfCorpus / 20;
			}

			// compute number of occurrences for each term in each partition
			tfIDfFreqInEachText = new ArrayList<HashMap<String, Double>>();

			for (int i = 0; i < 20; i++)
				tfIDfFreqInEachText.add(new HashMap<String, Double>());

			for (int i = 0; i < concordanceTableModel.getRowCount(); i++)
			{
				Object tag = concordanceTableModel.getValueAt(i, 5);

				if (tag == null)
					continue;

				// get partition #
				ArrayList<?> annotation = (ArrayList<?>) tag;
				int absoluteBeginAddress = (new Double(annotation.get(2).toString())).intValue();
				int partition = (int) (absoluteBeginAddress * 20.0 / sizeOfCorpus);

				// get term and add its number of occurrences
				Map<String, Double> hCount = tfIDfFreqInEachText.get(partition);
				String term = concordanceTableModel.getValueAt(i, 2).toString();

				if (hCount.containsKey(term))
				{
					int count = hCount.get(term).intValue();
					hCount.put(term, new Double(count + 1));
				}
				else
				{
					hCount.put(term, new Double(1));
					if (!tfIDfAllTerms.containsKey(term))
						;
					tfIDfAllTerms.put(term, new Double(0));
				}
			}
		}

		// compute normalized frequency of each term in each text
		for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
		{
			Map<String, Double> hCount = tfIDfFreqInEachText.get(iTxt);
			HashMap<String, Double> hFreq = new HashMap<String, Double>();

			for (String term : hCount.keySet())
			{
				int nbOfOccurences = hCount.get(term).intValue();
				int termSize = term.length();
				double freq = 1.0 * nbOfOccurences * termSize / tfIDfSizeOfEachText[iTxt];
				hFreq.put(term, freq); // replace count with frequency, normalized to each text's size
			}
			tfIDfFreqInEachText.set(iTxt, hFreq);
		}

		// compute number of documents that contains each term
		for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
		{
			HashMap<String, Double> hFreq = tfIDfFreqInEachText.get(iTxt);

			for (String term : hFreq.keySet())
			{
				int nbOfDocumentsForTerm = tfIDfAllTerms.get(term).intValue();
				tfIDfAllTerms.put(term, new Double(nbOfDocumentsForTerm + 1));
			}
		}

		// compute tfidf: term frequency x log10 (nb of documents / nb of documents that contain the term)
		for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
		{
			Map<String, Double> hFreq = tfIDfFreqInEachText.get(iTxt);
			HashMap<String, Double> htfIDf = new HashMap<String, Double>();

			for (String term : hFreq.keySet())
			{
				double freq = hFreq.get(term);
				double tfIDf = freq * Math.log10(nbOfDocuments / tfIDfAllTerms.get(term).intValue());
				htfIDf.put(term, tfIDf);
			}

			tfIDfFreqInEachText.set(iTxt, htfIDf);
		}
	}

	/**
	 * Function fills in the table of Concordance's statistics while 'Relevances' radio button is selected.
	 */

	private void fillInTfIDf()
	{
		JTable tableOfRelevences = statsShell.getPanelOfRelevances().getTableOfRelevances();
		DefaultTableModel tableModel = (DefaultTableModel) tableOfRelevences.getModel();
		ArrayList<String[]> tableData = new ArrayList<String[]>();

		// compute columns
		for (String fileName : tfIDfFileNames)
			tableModel.addColumn(fileName);

		// get cell matrix
		CustomCellRenderer customCellRenderer = (CustomCellRenderer) tableOfRelevences.getDefaultRenderer(Object.class);
		boolean[][] cellMatrix = customCellRenderer.getCellMatrix();

		boolean matrixInitialized = false;
		int counter = 0;
		ArrayList<String> listOfFreqs = new ArrayList<String>();

		// fill in lvtfidf
		for (String term : tfIDfAllTerms.keySet())
		{
			listOfFreqs.add(term);
			double maxTfIDf = 0.0;

			// create future table row and max value for a term
			for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
			{
				HashMap<String, Double> hFreq = tfIDfFreqInEachText.get(iTxt);

				if (!hFreq.containsKey(term))
					listOfFreqs.add("");
				else
				{
					double tfIDf = hFreq.get(term);

					if (tfIDf == 0.0)
						listOfFreqs.add("");
					else
					{
						String tfIDFString = String.valueOf(tfIDf);

						if (tfIDFString.length() < 13)
							listOfFreqs.add(tfIDFString);
						else
						{
							BigDecimal convertedValue = BigDecimal.valueOf(tfIDf);
							tfIDFString = String.valueOf(convertedValue.setScale(10, BigDecimal.ROUND_HALF_UP));
							listOfFreqs.add(tfIDFString);
						}

						if (tfIDf > maxTfIDf)
							maxTfIDf = tfIDf;
					}
				}
			}

			// size of a row
			int sizeOfFreqList = listOfFreqs.size() / (counter + 1);

			// create the line as sublist of total freq list
			String[] line = new String[sizeOfFreqList + 1];
			line = Arrays.copyOf(listOfFreqs.subList(counter * sizeOfFreqList, (counter + 1) * sizeOfFreqList)
					.toArray(), sizeOfFreqList + 1, String[].class);

			// set the max value to the end of the line
			line[sizeOfFreqList] = String.valueOf(maxTfIDf);
			tableData.add(line);
			counter++;
		}

		// sort table data alphabetically, ignoring case
		Collections.sort(tableData, new Comparator<String[]>()
		{
			public int compare(String[] strings, String[] strings2)
			{
				return strings[0].compareToIgnoreCase(strings2[0]);
			}
		});

		// table renderer needs to know which cells to color in yellow/red
		for (int l = 0; l < counter; l++)
		{
			// get the line
			String[] line = tableData.get(l);
			int lengthOfLine = line.length;

			// iterate without first and last (name and max value)
			for (int i = 1; i < lengthOfLine - 1; i++)
			{
				String txt = line[i];

				if (txt.equals(""))
					continue;

				double tfIDf = Double.parseDouble(txt);

				// last value of line is a max value
				if (tfIDf >= new Double(line[lengthOfLine - 1]) - 0.001)
				{
					// if matrix is not initialized, fill it with false values
					if (!matrixInitialized)
					{
						int sizeOfAllTerms = tfIDfAllTerms.size();
						boolean[][] tempCellMatrix = new boolean[lengthOfLine][sizeOfAllTerms];
						for (int j = 0; j < lengthOfLine; j++)
						{
							for (int k = 0; k < sizeOfAllTerms; k++)
								tempCellMatrix[j][k] = false;
						}
						cellMatrix = tempCellMatrix;
						matrixInitialized = true;
					}

					// if value satisfies if condition, set true value to its matrix correspondent
					// and set matrix and flag in renderer
					cellMatrix[i][l] = true;
					customCellRenderer.setCellMatrix(cellMatrix);
					customCellRenderer.setTableHasColoredCells(true);
				}
			}
		}

		// add rows to the table
		for (int i = 0; i < tableData.size(); i++)
			tableModel.addRow(tableData.get(i));

		// auto-resize column widths depending on title and data
		for (int i = 0; i < tableModel.getColumnCount(); i++)
			setWidthOfTableColumn(tableOfRelevences, tableModel, i);
	}

	/**
	 * Function fills in the table of Concordance's statistics while 'Similarities' radio button is selected.
	 */

	private void fillInDistances()
	{
		// compute columns
		JTable tableOfDistances = statsShell.getPanelOfSimilarities().getTableOfDistances();
		DefaultTableModel tableModel = (DefaultTableModel) tableOfDistances.getModel();

		// compute columns
		for (String term2 : distances.keySet())
			tableModel.addColumn(term2);

		// fill in distances
		for (String term1 : distances.keySet())
		{
			ArrayList<String> listOfDists = new ArrayList<String>();
			listOfDists.add(term1);
			ArrayList<Object> dist1 = distances.get(term1);

			for (String term2 : distances.keySet())
			{
				double dist2 = -1.0;

				for (int i = 0; i < dist1.size(); i += 2)
				{
					String term3 = dist1.get(i).toString();
					if (term3.equals(term2))
					{
						dist2 = (Double) dist1.get(i + 1);
						break;
					}
				}

				if (dist2 == 0.0)
					listOfDists.add("");
				else
				{
					String distString = String.valueOf(dist2);

					if (distString.length() < 13)
						listOfDists.add(distString);
					else
					{
						BigDecimal convertedValue = BigDecimal.valueOf(dist2);
						distString = String.valueOf(convertedValue.setScale(10, BigDecimal.ROUND_HALF_UP));
						listOfDists.add(distString);
					}
				}
			}

			int sizeOfDistList = listOfDists.size();
			String[] line = new String[sizeOfDistList];
			line = Arrays.copyOf(listOfDists.toArray(), sizeOfDistList, String[].class);
			tableModel.addRow(line);
		}

		// auto-resize column widths depending on title and data
		for (int i = 0; i < tableModel.getColumnCount(); i++)
			setWidthOfTableColumn(tableOfDistances, tableModel, i);
	}

	/**
	 * Function computes distance between two terms in texts.
	 * 
	 * @param term1
	 *            - first string term to compare
	 * @param term2
	 *            - second string term to compare
	 * @return - distance between two terms
	 */

	private double computeDistance(String term1, String term2)
	{
		double distance = 0.0;

		if (term1.equals(term2))
			return 0.0;

		for (int iTxt = 0; iTxt < tfIDfFreqInEachText.size(); iTxt++)
		{
			HashMap<String, Double> htfIDf = tfIDfFreqInEachText.get(iTxt);
			double tfIDf1 = 0.0;

			if (htfIDf.containsKey(term1))
				tfIDf1 = htfIDf.get(term1);

			double tfIDf2 = 0.0;
			if (htfIDf.containsKey(term2))
				tfIDf2 = htfIDf.get(term2);

			double dist = (tfIDf2 - tfIDf1) * (tfIDf2 - tfIDf1);
			distance += dist;
		}

		return Math.sqrt(distance);
	}

	/**
	 * Function computes distance between two terms in texts.
	 */

	private void computeDistances()
	{
		if (tfIDfFreqInEachText == null)
			this.computeTfidf();

		distances = new HashMap<String, ArrayList<Object>>();

		for (String term1 : tfIDfAllTerms.keySet())
		{
			ArrayList<Object> dist1 = new ArrayList<Object>();

			for (String term2 : tfIDfAllTerms.keySet())
			{
				double distance2 = computeDistance(term1, term2);
				dist1.add(term2);
				dist1.add(distance2);
			}

			distances.put(term1, dist1);
		}
	}

	/**
	 * Function computes standard score between two terms in texts.
	 */

	private void computeStandardScore()
	{
		nbOfColors = 0;
		freqInEachText = null;
		freqInCorpus = null;
		sizeOfCorpus = 0;
		Map<Color, Integer> colors = new HashMap<Color, Integer>();
		ArrayList<Color> aColors = new ArrayList<Color>();
		DefaultTableModel tableModel = (DefaultTableModel) concordanceController.getConcordanceTable().getModel();

		// get data from concordance
		if (corpusController != null)
		{
			int corpusTableSize = corpusController.getShell().getTableTexts().getModel().getRowCount();
			sizeOfEachText = new int[corpusTableSize];
			fileNames = new String[corpusTableSize];
			String corpusDirName = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;

			// compute nb of texts
			int iTxt = 0;
			Corpus corpus = corpusController.getCorpus();

			for (String fileName : corpus.listOfFileTexts)
			{
				String fullPath = corpusDirName + System.getProperty("file.separator") + fileName;
				Ntext myText = null;

				try
				{
					myText = Ntext.loadJustBufferForCorpus(fullPath, corpus.lan, corpus.multiplier);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (myText == null)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.STATISTICS_CORRUPTED_CORPUS
							+ fullPath, Constants.NOOJ_PROBLEM, JOptionPane.ERROR_MESSAGE);
					continue;
				}

				sizeOfEachText[iTxt] = myText.buffer.length();
				sizeOfCorpus += myText.buffer.length();
				fileNames[iTxt] = FilenameUtils.removeExtension(fileName);
				iTxt++;
			}

			for (int i = 0; i < tableModel.getRowCount(); i++)
			{
				Color c;
				int color = (Integer) tableModel.getValueAt(i, 4);

				// determine which color value is set in hidden columns
				if (color == 1)
					c = Color.BLACK;
				else if (color == 2)
					c = Color.RED;
				else if (color == 3)
					c = Color.GREEN;
				else
					c = Color.BLUE;

				if (!colors.containsKey(c))
				{
					colors.put(c, nbOfColors);
					aColors.add(c);
					nbOfColors++;
				}
			}

			freqInEachText = new long[nbOfColors][];
			freqInCorpus = new long[nbOfColors];

			for (int iCol = 0; iCol < nbOfColors; iCol++)
				freqInEachText[iCol] = new long[corpusTableSize];

			for (int i = 0; i < tableModel.getRowCount(); i++)
			{
				String fileName = FilenameUtils.removeExtension(tableModel.getValueAt(i, 0).toString());
				iTxt = -1;

				for (int j = 0; j < fileNames.length; j++)
				{
					if (fileName.equals(fileNames[j]))
					{
						iTxt = j;
						break;
					}
				}

				if (iTxt == -1)
					continue;

				int color = (Integer) tableModel.getValueAt(i, 4);
				Color c;

				if (color == 1)
					c = Color.BLACK;
				else if (color == 2)
					c = Color.RED;
				else if (color == 3)
					c = Color.GREEN;
				else
					c = Color.BLUE;

				int colorNb = colors.get(c);
				freqInEachText[colorNb][iTxt]++;
				freqInCorpus[colorNb]++;
			}
		}

		else
		{
			// compute nb of partitions
			sizeOfEachText = new int[20];
			fileNames = new String[20];
			sizeOfEachText = new int[20];
			fileNames = new String[20];

			sizeOfCorpus = textController.getMyText().buffer.length();

			for (int iTxt = 0; iTxt < 20; iTxt++)
			{
				sizeOfEachText[iTxt] += sizeOfCorpus / 20;
				fileNames[iTxt] = "Part #" + (iTxt + 1);
			}

			// compute number of colors
			for (int i = 0; i < tableModel.getRowCount(); i++)
			{
				Color c;
				int color = (Integer) tableModel.getValueAt(i, 4);

				// determine which color value is set in hidden columns
				if (color == 1)
					c = Color.BLACK;
				else if (color == 2)
					c = Color.RED;
				else if (color == 3)
					c = Color.GREEN;
				else
					c = Color.BLUE;

				if (!colors.containsKey(c))
				{
					colors.put(c, nbOfColors);
					aColors.add(c);
					nbOfColors++;
				}
			}

			freqInEachText = new long[nbOfColors][];
			freqInCorpus = new long[nbOfColors];

			for (int iCol = 0; iCol < nbOfColors; iCol++)
				freqInEachText[iCol] = new long[20];

			for (int i = 0; i < tableModel.getRowCount(); i++)
			{
				Object tag = tableModel.getValueAt(i, 5);

				if (tag == null)
					continue;

				ArrayList<?> annotation = (ArrayList<?>) tag;
				int absoluteBeginAddress = (new Double(annotation.get(2).toString())).intValue();
				int partition = (int) (absoluteBeginAddress * 20.0 / sizeOfCorpus);

				Color c;
				int color = (Integer) tableModel.getValueAt(i, 4);

				if (color == 1)
					c = Color.BLACK;
				else if (color == 2)
					c = Color.RED;
				else if (color == 3)
					c = Color.GREEN;
				else
					c = Color.BLUE;

				int colorNb = colors.get(c);
				freqInEachText[colorNb][partition]++;
				freqInCorpus[colorNb]++;
			}
		}

		// now compute Standard Scores
		// first compute global measures: NT, NM, SD
		int nt = fileNames.length;

		// compute all NMs and SDs (each color has its own)
		double[] sds = new double[freqInEachText.length];
		double[] nsds = new double[freqInEachText.length];

		for (int iColor = 0; iColor < freqInEachText.length; iColor++)
		{
			// compute NM, SD
			long nm = 0;
			double sd = 0.0;

			for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
			{
				long af = freqInEachText[iColor][iTxt];
				nm += af;
				double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;

				sd += (af - ef) * (af - ef) / nt;
			}

			sds[iColor] = Math.sqrt(sd);

			// compute NSD
			double nsd = 0.0;
			for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
			{
				long af = freqInEachText[iColor][iTxt];
				double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;
				nsd += ((af - ef) / nm) * ((af - ef) / nm) / nt;
			}

			nsds[iColor] = Math.sqrt(nsd);
		}

		this.allSS = new ArrayList<ArrayList<Object>>();

		for (int iColor = 0; iColor < freqInEachText.length; iColor++)
		{
			ArrayList<Object> color = new ArrayList<Object>();
			color.add(aColors.get(iColor));

			for (int iTxt = 0; iTxt < fileNames.length; iTxt++)
			{
				// EF = Expected Frequency
				double ef = 1.0 * freqInCorpus[iColor] * sizeOfEachText[iTxt] / sizeOfCorpus;

				// SS = Standard Score
				double ss = (1.0 * freqInEachText[iColor][iTxt] - ef) / sds[iColor];
				color.add(ss);
			}

			allSS.add(color);
		}
	}

	/**
	 * Function draws standard score of concordance's sequences to Stats panel.
	 */

	public void paintStandardScore(Graphics g)
	{
		// 2D graphics for anti aliasing
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		DrawingJPanel parentPanel = this.statsShell.getPanelOfStandardScore();

		// Repaints graphics.
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, parentPanel.getWidth(), parentPanel.getHeight());

		g2D.setColor(pens.get("pen").color);
		g2D.setStroke(new BasicStroke(pens.get("pen").stroke));

		// axis x
		g2D.drawLine(5, 300, 795, 300); // x
		g2D.drawLine(795, 300, 785, 297);
		g2D.drawLine(795, 300, 785, 303);

		int nbOfPoints = allSS.get(0).size();
		double angleOfRotation = Math.PI / 4;
		double width = 700.0 / nbOfPoints;

		if (nbOfPoints <= 30)
		{
			for (int i = 0; i < nbOfPoints; i++)
			{
				int lineParameter = 50 + (int) (width * i);
				g2D.drawLine(lineParameter, 297, lineParameter, 303); // x

				if (i < fileNames.length)
				{
					// display every single filename
					int translateParameter = 45 + (int) (width * (i + 1));

					g2D.translate(translateParameter, 290);
					g2D.rotate(-angleOfRotation);

					g2D.setFont(font);
					g2D.setColor(brush);
					g2D.drawString(this.fileNames[i], 0, 0); // x label

					g2D.rotate(angleOfRotation);
					g2D.translate(-translateParameter, -290);
				}
			}
		}

		else
		{
			int interval = nbOfPoints / 20;
			for (int i = 0; i < nbOfPoints; i++)
			{
				if (i < fileNames.length && ((i % interval == 0) || (i == nbOfPoints - 1)))
				{
					int lineParameter = 50 + (int) (width * i);
					int translateParameter = 45 + (int) (width * (i + 1));

					g2D.drawLine(lineParameter, 297, lineParameter, 303); // x

					// display every 10 filename
					g2D.translate(translateParameter, 290);
					g2D.rotate(-angleOfRotation);

					g2D.setFont(font);
					g2D.setColor(brush);
					g2D.drawString(this.fileNames[i], 10, 0); // x label

					g2D.rotate(angleOfRotation);
					g2D.translate(-translateParameter, -290);
				}
			}
		}

		g2D.setColor(pens.get("pen").color);
		g2D.setStroke(new BasicStroke(pens.get("pen").stroke));

		// axis y
		g2D.drawLine(50, 5, 50, 595); // y
		g2D.drawLine(50, 5, 47, 15);
		g2D.drawLine(50, 5, 53, 15);

		g2D.setFont(font);
		g2D.setColor(brush);
		g2D.drawString("Standard Score", 58, 25);
		g2D.setColor(pens.get("pen").color);
		g2D.setStroke(new BasicStroke(pens.get("pen").stroke));

		double maxSS = 0.0;

		for (ArrayList<Object> color : allSS)
		{
			for (int i = 1; i < color.size(); i++)
			{
				double ss = new Double(color.get(i).toString());
				if (ss > maxSS)
					maxSS = ss;
			}
		}

		int height;

		if (maxSS <= 2.2)
			height = (int) (250 / 2.2);
		else
			height = (int) (250 / maxSS);

		double highestGrade;

		if (maxSS < 2.2)
			highestGrade = 3.2;
		else
			highestGrade = maxSS + 1;

		for (int i = -(int) maxSS - 1; i < highestGrade; i++)
		{
			int lineParameter = 300 - i * height; // y

			g2D.drawLine(47, lineParameter, 53, lineParameter);
			g2D.setFont(font);
			g2D.setColor(brush);
			g2D.drawString(String.valueOf(i), 34, lineParameter + 11);

			if (i == 2 || i == -2)
			{
				BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
						discontiniousArray, 0.0f);
				g2D.setStroke(dashed);
				g2D.drawLine(50, lineParameter, 795, lineParameter); // dash line for ss = -2 and ss = +2
			}
		}

		if (allSS != null & allSS.size() > 0)
		{
			for (ArrayList<Object> color : allSS)
			{
				Color c = (Color) color.get(0);
				g2D.setColor(c);
				BasicStroke thickness = new BasicStroke(2.0f);
				g2D.setStroke(thickness);
				Point pt = new Point(0, 0);

				for (int i = 1; i < color.size(); i++)
				{
					double ss = new Double(color.get(i).toString());
					int parameter = (int) (300.0F - (float) ss * height);
					int lineParameter = 50 + (int) (width * i);

					if (pt.x != 0 || pt.y != 0)
						g2D.drawLine(pt.x, pt.y, 50 + (int) (width * i), parameter);

					g2D.drawLine(50 + (int) (width * i - 3.0), parameter, 50 + (int) (width * i + 3.0), parameter);
					g2D.drawLine(lineParameter, parameter - 3, 50 + (int) (width * i), parameter + 3);
					pt.x = lineParameter;
					pt.y = parameter;
				}
			}
		}
	}

	/**
	 * Function launches adequate JPanel of Statistics.
	 */

	private void launchAnalysis()
	{
		if (statsShell.getRbFrequencies().isSelected())
		{
			if (freqSortedTerms == null)
				computeFrequency();

			this.statsShell.getPanelOfFrequencies().repaint();
		}
		else if (statsShell.getRbStandardScore().isSelected())
		{
			if (allSS == null)
				computeStandardScore();

			this.statsShell.getRbStandardScore().repaint();
		}
		else if (statsShell.getRbTfIDf().isSelected() && tfIDf_firstTime)
		{
			if (tfIDfFreqInEachText == null)
				computeTfidf();
			fillInTfIDf();
			tfIDf_firstTime = false;
		}
		else if (statsShell.getRbDistances().isSelected() && distancesFirstTime)
		{
			if (distances == null)
				computeDistances();

			fillInDistances();
			distancesFirstTime = false;
		}
	}

	/**
	 * Function exports statistics to a text file.
	 */

	public void exportStatistics()
	{
		if (statsShell.getRbFrequencies().isSelected())
			createFrequencyReport();
		else if (statsShell.getRbStandardScore().isSelected())
			createStandardScoreReport();
		else if (statsShell.getRbTfIDf().isSelected())
			createTfIDfReport();
		else if (statsShell.getRbDistances().isSelected())
			createDistReport();
	}

	/**
	 * Function changes active panel in Card Layout
	 * 
	 * @param panel
	 *            - main panel of Card Layout
	 * @param previewPanelName
	 *            - name of new active, desired panel
	 */

	public void changePanel(JPanel panel, String previewPanelName)
	{
		CardLayout cards = (CardLayout) panel.getLayout();
		cards.show(panel, previewPanelName);
		launchAnalysis();
	}

	/**
	 * Function for dynamically auto sorting of column widths. Takes care of small columns, too.
	 * 
	 * @param table
	 *            - table whose columns widths needs to be fixed
	 * @param tableModel
	 *            - model of a table
	 * @param column
	 *            - actual column that needs to be sorted
	 */

	private static void setWidthOfTableColumn(JTable table, DefaultTableModel tableModel, int column)
	{
		int width = 0;

		// for every row, calculate preferred size of the width, and set maximum
		for (int row = 0; row < tableModel.getRowCount(); row++)
		{
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component comp = table.prepareRenderer(renderer, row, column);
			width = Math.max(comp.getPreferredSize().width, width);
		}

		// get the text from column headers and its width, and if it's bigger than maximum, set the maximum
		String columnText = table.getColumnName(column);
		int preferedWidthOfHeader = table.getGraphics().getFontMetrics().stringWidth(columnText);

		if (width < preferedWidthOfHeader)
			width = preferedWidthOfHeader;

		// Set preferred width (increased for 5 pixels to avoid the dots) to column of a table
		table.getColumnModel().getColumn(column).setPreferredWidth(width + 10);
	}
}