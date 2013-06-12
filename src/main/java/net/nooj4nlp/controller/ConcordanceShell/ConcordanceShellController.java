package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ConcordanceShell;
import net.nooj4nlp.gui.shells.SyntacticTreeShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

public class ConcordanceShellController
{
	// controllers
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;

	// corpus & shells
	private ConcordanceShell concordanceShell;
	private Corpus corpus = null;

	// components
	private JRadioButton rbCharacters;
	private JRadioButton rbWordForms;
	private JTable concordanceTable;
	private JCheckBox cbMatches;
	private JCheckBox cbOutputs;

	// language of text
	private Language Lan;

	// flags:
	// flag to determine whether text is read from right to left or not
	private boolean RightToLeft;
	// flag to determine whether concordance should be refreshed or not
	private boolean doNotRefresh = false;
	// flag to determine whether check box "Matches" is pressed
	private boolean cbMatchesIsPressed = true;
	// flag to determine whether radio button "Characters" is pressed
	private boolean rbCharactersIsPressed = false;
	// flag to determine whether check box "Outputs" is pressed
	private boolean cbOutputsIsPressed = false;
	// flag to determine whether concordance table is sorted or not
	private boolean isConcordanceTableSorted = false;

	// items of concordance table
	private List<Object> theItems = null;

	private SyntacticTreeShell syntacticTreeShell = null;

	private static int before = 5;
	private static int after = 5;

	// hash map with sorting values for each color:
	private Map<Color, Integer> colorMap = new HashMap<Color, Integer>();
	// actual table model
	private DefaultTableModel tableModel = null;

	/**
	 * Constructor.
	 * 
	 * @param concordanceShell
	 *            - shell of concordance
	 */
	public ConcordanceShellController(ConcordanceShell concordanceShell)
	{
		// sorting pairs...
		colorMap.put(Color.BLACK, 1);
		colorMap.put(Color.RED, 2);
		colorMap.put(Color.GREEN, 3);
		colorMap.put(Color.BLUE, 4);

		// initializing of variables
		boolean isItAText = false;
		this.concordanceShell = concordanceShell;
		this.corpusController = this.concordanceShell.getCorpusController();
		this.textController = this.concordanceShell.getTextController();

		if (corpusController != null && corpusController.getShell() != null)
		{
			this.corpus = corpusController.getCorpus();
			Lan = this.corpus.lan;
			this.textController = corpusController.getTextController();
		}

		else if (textController != null)
		{
			Lan = textController.getMyText().Lan;
			isItAText = true;
			this.textController.setConcordanceController(this);
		}

		this.RightToLeft = Lan.rightToLeft;
		this.doNotRefresh = false;

		this.rbCharacters = this.concordanceShell.getRbCharacters();
		this.rbWordForms = this.concordanceShell.getRbWordForms();
		this.concordanceTable = this.concordanceShell.getConcordanceTable();

		this.concordanceTable.setFont(Launcher.preferences.TFont);

		this.cbMatches = this.concordanceShell.getCbMatches();
		this.cbOutputs = this.concordanceShell.getCbOutputs();

		if (isItAText)
		{
			// no space => no wordform in concordance
			if ((Lan.isoName.equals("zh") || Lan.isoName.equals("ja")) && !rbCharacters.isSelected())
			{
				doNotRefresh = false;
				rbCharacters.setSelected(true);
				this.rbWordForms.setEnabled(false);
				doNotRefresh = true;
			}
			else if (Lan.rightToLeft)
			{
				DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) concordanceTable
						.getDefaultRenderer(Object.class);
				renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				concordanceTable.setDefaultRenderer(Object.class, renderer);
			}
			else
			{
				DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();
				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();
			}
		}
		else
		{
			if ((Lan.isoName.equals("zh") || Lan.isoName.equals("ja")) && !rbCharacters.isSelected())
			{
				doNotRefresh = false;
				rbCharacters.setSelected(true);
				this.rbWordForms.setEnabled(false);
				doNotRefresh = true;
			}
			if (Lan.rightToLeft)
			{
				DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) concordanceTable
						.getDefaultRenderer(Object.class);
				renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				concordanceTable.setDefaultRenderer(Object.class, renderer);
			}
			else
			{
				DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();
				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();
			}
		}

		// clear items
		theItems = new ArrayList<Object>();
	}

	/**
	 * Function adds item to "theItems" of concordance.
	 * 
	 * @param text
	 *            - Ntext from which data is parsed
	 * @param fileName
	 *            - file name
	 * @param c
	 *            - future text's color
	 * @param tuNb
	 *            - text unit number
	 * @param beginAddress0
	 *            - beginning index
	 * @param endAddress0
	 *            - ending index
	 * @param seqOfAnnotations
	 *            - annotations
	 * @param onlyOneExample
	 *            - flag for determination if there should be only one example
	 * @param query
	 *            - query description
	 * @return - status if adding data was successful
	 */
	public boolean AddData(Ntext text, String fileName, Color c, int tuNb, double beginAddress0, double endAddress0,
			ArrayList<Object> seqOfAnnotations, boolean onlyOneExample, String query)
	{

		if (fileName == null)
			fileName = "";

		String lCon = "", seq = "", rCon = "", mSeq = ""; // the four columns

		int beginAddress = (int) beginAddress0;
		int endAddress = (int) endAddress0;

		// if end address is shorter than it should be, extend it
		if (endAddress < endAddress0)
		{
			while (endAddress < text.buffer.length() && Language.isLetter(text.buffer.charAt(endAddress)))
				endAddress++;
		}

		seq = text.buffer.substring(beginAddress, endAddress);

		String output = "";

		if (seqOfAnnotations != null)
		{
			// sequence of annotations includes: tunb, output,begaddress,endaddress, and then each atomic annotation
			output = seqOfAnnotations.get(1).toString();

			if (output != null && output != "")
				seq += "/" + output;
		}

		// if it should be only one example, don't show the others
		if (onlyOneExample)
		{
			for (int i = 0; i < theItems.size(); i += 4)
			{
				Object[] item = (Object[]) theItems.get(i + 1);
				Color actualColor = (Color) item[4];

				if (!actualColor.equals(c))
					continue;

				String seq0 = item[2].toString();

				if (seq.equals(seq0))
					return false;
			}
		}

		// compute the data and get the values
		RefObject<String> lConRef = new RefObject<String>(lCon);
		RefObject<String> rConRef = new RefObject<String>(rCon);
		RefObject<String> mSeqRef = new RefObject<String>(mSeq);

		compute(rbCharacters.isSelected(), beginAddress, endAddress, text.buffer, before, after, seq, lConRef, mSeqRef,
				rConRef);

		lCon = lConRef.argvalue;
		rCon = rConRef.argvalue;
		mSeq = mSeqRef.argvalue;

		int iAlready = theItems.size() - 4;

		if (iAlready >= 0)
		{
			Object[] item = (Object[]) theItems.get(iAlready + 1);
			Color actualColor = (Color) item[4];
			if (actualColor.equals(c))
			{
				String fileName0 = item[0].toString();
				String lCon0 = item[1].toString();
				String seq0 = item[2].toString();
				String rCon0 = item[3].toString();

				if (fileName.equals(fileName0) && lCon.equals(lCon0) && seq.equals(seq0) && rCon.equals(rCon0))
					return false;
			}
		}

		// There is no Tag object for JTable, do so, if we want to tie Tag "annotations"
		// with an Item, 5th member of array will be Tag!

		Object[] cItem = new Object[6];
		List<Object> annotations = new ArrayList<Object>();

		annotations.add(tuNb);
		annotations.add(seqOfAnnotations);
		annotations.add(beginAddress0);
		annotations.add(endAddress0);

		if (RightToLeft)
			cItem = new Object[] { fileName, rCon, mSeq, lCon, c, annotations };
		else
			cItem = new Object[] { fileName, lCon, mSeq, rCon, c, annotations };

		// add data to items
		theItems.add(query); // string query
		theItems.add(cItem); // ListViewItem item
		theItems.add(true); // bool selected
		theItems.add(output); // output

		return true;
	}

	/**
	 * Function responsible for computing values of concordance columns.
	 * 
	 * @param characterMode
	 *            - flag to determine whether computing should be done in char mode or not
	 * @param beginAddress
	 *            - beginning index
	 * @param endAddress
	 *            - ending index
	 * @param textBuffer
	 *            - buffer of a text
	 * @param before
	 *            - value of concordance's before text box
	 * @param after
	 *            - value of concordance's after text box
	 * @param result
	 *            - given result
	 * @param lConRef
	 *            - reference to value of future "before" column of concordance
	 * @param mSeqRef
	 *            - reference to value of future "seq" column of concordance
	 * @param rConRef
	 *            - reference to value of future "after" column of concordance
	 */
	public void compute(boolean characterMode, int beginAddress, int endAddress, String textBuffer, int before,
			int after, String result, RefObject<String> lConRef, RefObject<String> mSeqRef, RefObject<String> rConRef)
	{
		lConRef.argvalue = rConRef.argvalue = mSeqRef.argvalue = "";
		int bufferLength = textBuffer.length();

		if (characterMode)
		{
			if (beginAddress > before)
				lConRef.argvalue = textBuffer.substring(beginAddress - before, beginAddress);
			else
				lConRef.argvalue = textBuffer.substring(0, beginAddress);

			if (bufferLength > endAddress + after)
				rConRef.argvalue = textBuffer.substring(endAddress, endAddress + after);
			else
				rConRef.argvalue = textBuffer.substring(endAddress, bufferLength);
		}
		else
		{
			int nbOfTokens = 0;
			boolean inAWord = false;
			int i;
			for (i = beginAddress - 1; i > 0; i--)
			{
				if (Language.isLetter(textBuffer.charAt(i)))
				{
					if (!inAWord)
						nbOfTokens++;
					inAWord = true;
				}
				else
				{
					if (nbOfTokens >= before)
						break;
					if (inAWord)
						inAWord = false;
				}
			}

			if (i < 0)
				i = 0;

			lConRef.argvalue = textBuffer.substring(i, beginAddress);

			// token mode: right context
			nbOfTokens = 0;
			inAWord = true;

			for (i = endAddress; i < bufferLength; i++)
			{
				if (Language.isLetter(textBuffer.charAt(i)))
				{
					if (!inAWord)
						nbOfTokens++;
					inAWord = true;
				}
				else
				{
					if (nbOfTokens >= after)
						break;
					if (inAWord)
						inAWord = false;
				}
			}

			if (i > bufferLength)
				i = bufferLength;
			rConRef.argvalue = textBuffer.substring(endAddress, i);
		}

		if (cbMatches.isSelected())
		{
			int index = result.indexOf('\0');

			if (index == -1 || index == result.length() - 1)
				mSeqRef.argvalue = result;
			else
			{
				mSeqRef.argvalue = result.substring(0, index);
				if (cbOutputs.isSelected())
					mSeqRef.argvalue += "/" + result.substring(index + 1);
			}
		}

		else if (cbOutputs.isSelected())
		{
			int index = result.indexOf('\0');

			if (index == -1 || index < result.length() - 1)
				mSeqRef.argvalue = "";
			else
				mSeqRef.argvalue = result.substring(index + 1);
		}
		else
			mSeqRef.argvalue = "";
	}

	/**
	 * Function responsible for refreshing the data table of concordance.
	 */
	public void refreshConcordance()
	{
		// if concordance table is sorted, set the models and remove columns. Change the sorted flag to false (adding
		// items to
		// concordance returns unsorted model
		if (tableModel != null && isConcordanceTableSorted)
		{
			concordanceTable.setModel(tableModel);
			// Removing column - it is not supposed to be seen!
			concordanceTable.removeColumn(concordanceTable.getColumnModel().getColumn(5));
			concordanceTable.removeColumn(concordanceTable.getColumnModel().getColumn(4));

			setConcordanceTableSorted(false);
		}

		// remove all the elements from the table
		DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();

		if (textController != null)
		{
			List<Color> listOfColors = textController.getListOfColors();
			if (textController != null && listOfColors != null)
			{
				textController.setListOfColors(new ArrayList<Color>());
				textController.setAbsoluteBeginAddresses(new ArrayList<Integer>());
				textController.setAbsoluteEndAddresses(new ArrayList<Integer>());
			}
		}

		if (syntacticTreeShell != null)
		{
			syntacticTreeShell.dispose();
			setSyntacticTreeShell(null);
		}

		// if concordance was opened from corpus context...
		String lastFullPath = "", lastBuffer = "";

		TextEditorShellController corpusTextController = null;

		if (corpusController != null && corpusController.getShell() != null)
			corpusTextController = corpusController.getTextController();

		if (corpusController != null && corpusController.getShell() != null && corpusTextController != null)
		{
			lastFullPath = corpusTextController.getFileToBeOpenedOrImported().getAbsolutePath();
			lastBuffer = corpusTextController.getMyText().buffer;
		}

		// if there is no data, show error message
		if (theItems == null || theItems.size() == 0)
		{
			concordanceShell.dispose();
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CONCORDANCE_IS_EMPTY,
					Constants.NOOJ_NO_MATCH, JOptionPane.ERROR_MESSAGE);

			if (corpusController != null && corpusController.getShell() != null)
				corpusController.setConcordanceController(null);

			if (textController != null)
				textController.setConcordanceController(null);
			return;
		}

		for (int i = 0; i < theItems.size(); i += 4)
		{
			Object[] item = (Object[]) theItems.get(i + 1);
			boolean isSelected = (Boolean) theItems.get(i + 2);
			String fileName = item[0].toString();
			if (!isSelected)
				continue;

			// determine type of output
			List<?> annotation = (ArrayList<?>) item[5];

			ArrayList<?> seqOfOutputs = (ArrayList<?>) annotation.get(1);

			if (seqOfOutputs == null || seqOfOutputs.size() == 0)
			{
				concordanceShell.getMntmAddRemoveAnnotations().setEnabled(false);
				concordanceShell.getMntmDisplaySyntacticAnalysis().setEnabled(false);
				RightClickPopupMenuForConcordance.setenableSyntacticAndAnnotations(false);
			}
			else
			{
				String output = seqOfOutputs.get(1).toString();
				if (output == null || output.equals(""))
				{
					concordanceShell.getMntmAddRemoveAnnotations().setEnabled(false);
					concordanceShell.getMntmDisplaySyntacticAnalysis().setEnabled(false);
					RightClickPopupMenuForConcordance.setenableSyntacticAndAnnotations(false);
				}
				else if (output.charAt(0) == '<' || output.charAt(0) == '$')
				{
					concordanceShell.getMntmAddRemoveAnnotations().setEnabled(true);
					concordanceShell.getMntmDisplaySyntacticAnalysis().setEnabled(true);
					RightClickPopupMenuForConcordance.setenableSyntacticAndAnnotations(true);
				}
				else
				{
					concordanceShell.getMntmAddRemoveAnnotations().setEnabled(false);
					concordanceShell.getMntmDisplaySyntacticAnalysis().setEnabled(false);
					RightClickPopupMenuForConcordance.setenableSyntacticAndAnnotations(false);
				}
			}

			// recompute lcon and rcon
			String myTextBuffer = "";

			if (corpusController != null && corpusController.getShell() != null)
			{
				String fullPath = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX
						+ System.getProperty("file.separator") + item[0].toString();

				if (!fullPath.equals(lastFullPath))
				{
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
						return;

					myTextBuffer = myText.buffer;
					lastFullPath = fullPath;
					lastBuffer = myTextBuffer;
				}
				else
					myTextBuffer = lastBuffer;
			}
			else
				myTextBuffer = textController.getMyText().buffer;

			// compute beg and end addresses
			double absoluteBeginAddress0 = (Double) annotation.get(2);
			double absoluteEndAddress0 = (Double) annotation.get(3);
			int absoluteBeginAddress = (int) absoluteBeginAddress0;
			int absoluteEndAddress = (int) absoluteEndAddress0;

			int lengthOfBuffer = myTextBuffer.length();
			if (absoluteBeginAddress > lengthOfBuffer || absoluteEndAddress > lengthOfBuffer)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CONCORDANCE_NOT_SYNC_WITH_TEXT,
						Constants.NOOJ_SYNC_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (absoluteEndAddress < absoluteEndAddress0)
			{
				while (absoluteEndAddress < lengthOfBuffer
						&& Language.isLetter(myTextBuffer.charAt(absoluteEndAddress)))
					absoluteEndAddress++;
			}

			// compute output
			String sOutput = "";

			ArrayList<?> seqOfAnnotations = (ArrayList<?>) ((ArrayList<?>) item[5]).get(1);

			if (seqOfAnnotations == null)
				;
			else
			{
				// clean up '#' from soutput
				String sOutput0 = seqOfAnnotations.get(1).toString();
				sOutput = sOutput0.replace("#", "");
			}

			// compute left context, modified sequence and right context
			String lCon = "", mSeq = "", rCon = "";

			String seq = myTextBuffer.substring(absoluteBeginAddress, absoluteEndAddress) + "\0" + sOutput;

			RefObject<String> lConRef = new RefObject<String>(lCon);
			RefObject<String> rConRef = new RefObject<String>(rCon);
			RefObject<String> mSeqRef = new RefObject<String>(mSeq);

			compute(rbCharacters.isSelected(), absoluteBeginAddress, absoluteEndAddress, myTextBuffer, before, after,
					seq, lConRef, mSeqRef, rConRef);

			lCon = lConRef.argvalue;
			rCon = rConRef.argvalue;
			mSeq = mSeqRef.argvalue;

			Object[] cItem = new Object[6];

			if (!RightToLeft)
				cItem = new Object[] { fileName, cleanUpConcordanceString(lCon), cleanUpConcordanceString(mSeq),
						cleanUpConcordanceString(rCon), colorMap.get(item[4]), item[5] };
			else
				// reorder left and right contexts
				cItem = new Object[] { fileName, cleanUpConcordanceString(rCon), cleanUpConcordanceString(mSeq),
						cleanUpConcordanceString(lCon), colorMap.get(item[4]), item[5] };

			// add pair of (row, color) to renderer's map and add row to table
			concordanceShell.getCustomForegroundTableRenderer().addColoredRowsToAMap(tableModel.getRowCount(),
					(Color) item[4]);
			// Changed when removing static variables
			// CustomForegroundTableRenderer.addColoredRowsToAMap(tableModel.getRowCount(), (Color) item[4]);
			tableModel.addRow(cItem);
		}

		// set adequate widths of columns
		setWidthOfTableColumn(concordanceTable, tableModel, 0);
		setWidthOfTableColumn(concordanceTable, tableModel, 1);
		setWidthOfTableColumn(concordanceTable, tableModel, 2);
		setWidthOfTableColumn(concordanceTable, tableModel, 3);

		// set table model to actual
		this.setTableModel(null);

		concordanceShell.getEntriesNBLabel().setText(concordanceTable.getRowCount() + "/" + (theItems.size() / 4));
		mColorClick();
	}

	/**
	 * Function responsible for removing control character and whitespaces from a string.
	 * 
	 * @param text
	 *            - string that needs to be cleaned
	 * @return - cleaned string
	 */
	public static String cleanUpConcordanceString(String text)
	{
		StringBuilder sb = new StringBuilder(text);

		for (int i = 0; i < sb.length(); i++)
		{
			char c = sb.charAt(i);

			if (Character.isISOControl(c) || Character.isWhitespace(c))
				sb.setCharAt(i, ' ');
		}

		return sb.toString();
	}

	/**
	 * Function for implementation of radio button events of concordance.
	 * 
	 * @param isRBCharactersSelected
	 *            - flag to determine whether characters radio button is selected
	 */
	public void radioButtonEvent(boolean isRBCharactersSelected)
	{
		if (isRBCharactersSelected)
		{
			before *= 10;
			after *= 10;
			rbCharactersIsPressed = true;
		}
		else
		{
			before = (before - (before % 10)) / 10;
			after = (after - (after % 10)) / 10;
			rbCharactersIsPressed = false;
		}

		if (doNotRefresh)
			return;

		refreshConcordance();
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
	public void setWidthOfTableColumn(JTable table, DefaultTableModel tableModel, int column)
	{
		int width = 0;

		// for every row, calculate preferred size of the width, and set maximum
		for (int row = 0; row < tableModel.getRowCount(); row++)
		{
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component comp = table.prepareRenderer(renderer, row, column);
			width = Math.max(comp.getPreferredSize().width, width);
		}

		// part that takes care of small columns; if it's first column and needs to be wider...
		if (column == 0 && width < 50)
			width = 50;

		// if a column is less than third of a table
		else if (width < 166)
			width = 166;

		// if "Matches" is pressed, text is removed, so the column needs to be wider
		if (column == 2 && !cbMatchesIsPressed)
			width = 50;

		// set max and preffered width (increased for 5 pixels to avoid the dots) to column of a table
		table.getColumnModel().getColumn(column).setMaxWidth(width + 5);
		table.getColumnModel().getColumn(column).setPreferredWidth(width + 5);
	}

	/**
	 * Menu function for adding/removing annotation of text in concordance.
	 */
	public void annotate()
	{
		int iAdded = 0;
		int iRemoved = 0;
		RefObject<Integer> iAddedRef = new RefObject<Integer>(iAdded);
		RefObject<Integer> iRemovedRef = new RefObject<Integer>(iRemoved);

		ArrayList<Object> annotations;
		if (corpusController != null && corpusController.getShell() != null)
			annotations = corpus.annotations;
		else
			annotations = textController.getMyText().annotations;

		String lastFullPath = "";
		Ntext myLastText = null;

		if (corpusController != null && corpusController.getShell() != null && textController != null)
		{
			lastFullPath = textController.getFileToBeOpenedOrImported().getAbsolutePath();
			myLastText = textController.getMyText();
		}

		Engine engine = null;

		// if no item is selected, then process all, otherwise only process the ones that are selected
		boolean processAll = false;

		int[] selectedRows = concordanceTable.getSelectedRows();
		int selectedRowsLength = selectedRows.length;

		if (selectedRowsLength == 0)
			processAll = true;

		for (int i = 0; i < concordanceTable.getRowCount(); i++)
		{
			if (!processAll && !concordanceTable.isRowSelected(i))
				continue;

			DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();
			ArrayList<?> annotation = (ArrayList<?>) tableModel.getValueAt(i, 5);
			int TUnb = (Integer) annotation.get(0);

			if (TUnb <= 0)
				continue; // that could happen if there are some STRING or PERL queries in the concordance

			if (corpusController != null && corpusController.getShell() != null)
			{
				String corpusDirPath = corpusController.getFullPath() + Constants.DIRECTORY_SUFFIX;
				engine = corpusController.getEngine();
				// find text
				String fileName = tableModel.getValueAt(i, 0).toString();
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
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
						return;
					}
					lastFullPath = fullPath;
					myLastText = myText;
				}

				if (myText == null)
					return;

				int xRefNb = corpus.annotations.size();
				// add annotation piece by piece (begaddr,output,endaddr)
				ArrayList<?> seqOfAnnotations = (ArrayList<?>) annotation.get(1);
				if (seqOfAnnotations != null)
				{
					String seq = seqOfAnnotations.get(1).toString();
					if (seqOfAnnotations.size() <= 4)
					{
						// not an annotation
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Output \"" + seq
								+ "\" is not a proper annotation command!", Constants.NOOJ_ANNOTATING_STOPPED,
								JOptionPane.ERROR_MESSAGE);
						break;
					}
					if (seq.charAt(0) != '<')
					{
						// not an annotation
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Output \"" + seqOfAnnotations.get(1)
								+ "\" is not a proper annotation command!", Constants.NOOJ_ANNOTATING_STOPPED,
								JOptionPane.ERROR_MESSAGE);
						break;
					}
					annotationHelpFunction(engine, myText, seqOfAnnotations, annotations, TUnb, xRefNb, iAddedRef,
							iRemovedRef, true);
				}

				// save text results
				try
				{
					myText.saveForCorpus(fullPath);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_SAVE_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			else
			{
				engine = textController.getEngine();
				Ntext myText = textController.getMyText();
				int xRefNb = myText.annotations.size();

				// add annotation piece by piece (begaddr,output,endaddr)
				// Unchecked cast cannot be avoided here - annotation is an ArrayList of unknown objects.
				ArrayList<Object> seqOfAnnotations = (ArrayList<Object>) annotation.get(1);
				if (seqOfAnnotations != null)
				{

					String seq = seqOfAnnotations.get(1).toString();
					if (seqOfAnnotations.size() <= 4)
					{
						// not an annotation
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Output \"" + seq
								+ "\" is not a proper annotation command!", Constants.NOOJ_ANNOTATING_STOPPED,
								JOptionPane.ERROR_MESSAGE);
						break;
					}
					if (seq.charAt(0) != '<')
					{
						// not an annotation
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Output \"" + seqOfAnnotations.get(1)
								+ "\" is not a proper annotation command!", Constants.NOOJ_ANNOTATING_STOPPED,
								JOptionPane.ERROR_MESSAGE);
						break;
					}

					annotationHelpFunction(engine, myText, seqOfAnnotations, seqOfAnnotations, TUnb, xRefNb, iAddedRef,
							iRemovedRef, false);
				}
			}
		}

		iAdded = iAddedRef.argvalue;
		iRemoved = iRemovedRef.argvalue;
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), iAdded + " annotations were added; " + iRemoved
				+ " annotations were removed", Constants.NOOJ_TEXT_ANNOTATION_SUCCESS, JOptionPane.INFORMATION_MESSAGE);

		if (iAdded > 0 || iRemoved > 0)
		{
			if (corpusController != null && corpusController.getShell() != null)
			{
				if (textController != null)
					textController.getTextShell().getChckbxShowTextAnnotation().setSelected(false);
				corpusController.setModified(true);
				corpusController.updateTitle();
				corpusController.updateTextPaneStats();
				corpusController.updateResults();
			}
			else
			{
				textController.updateTextPaneStats();
				textController.modify();
			}
		}
	}

	/**
	 * Help function for annotate() concordance function. Done this way to decrease number of code lines with reusable
	 * code.
	 * 
	 * @param engine
	 *            - current engine
	 * @param myText
	 *            - current text
	 * @param seqOfAnnotations
	 *            - current sequence of annotations
	 * @param annotations
	 *            - current annotations
	 * @param TUnb
	 *            - current text unit number
	 * @param xRefNb
	 *            - X Ref number
	 * @param iAddedRef
	 *            - return number of added annotations
	 * @param iRemovedRef
	 *            - return number of removed annotations
	 * @param corpusContextCall
	 *            - define whether function was called from corpus context or not
	 */
	private void annotationHelpFunction(Engine engine, Ntext myText, ArrayList<?> seqOfAnnotations,
			ArrayList<Object> annotations, int TUnb, int xRefNb, RefObject<Integer> iAddedRef,
			RefObject<Integer> iRemovedRef, boolean corpusContextCall)
	{
		// first: filter out all disambiguation annotations
		for (int ia = 4; ia < seqOfAnnotations.size(); ia += 3)
		{
			double absoluteBeginAddress = (Double) seqOfAnnotations.get(ia);
			RefObject<Double> absBegAddressRef = new RefObject<Double>(absoluteBeginAddress);
			// make sure beginning of annotation does not start at spaces or XML tags
			engine.moveBegAddressOufOfSpaces(myText.buffer, myText.XmlNodes != null, absBegAddressRef);
			absoluteBeginAddress = absBegAddressRef.argvalue;

			String sOutput = seqOfAnnotations.get(ia + 1).toString();
			double absoluteEndAddress = (Double) seqOfAnnotations.get(ia + 2);

			if (absoluteBeginAddress == absoluteEndAddress) // this is a filter command
			{
				long hund_BeginAddress = (long) ((absoluteBeginAddress + 0.005) * 100)
						- (100 * myText.mft.tuAddresses[TUnb]);
				double beginAddress = hund_BeginAddress / 100.0;
				boolean anXRefWasRemoved = false;
				RefObject<Boolean> anXRefWasRemovedRef = new RefObject<Boolean>(anXRefWasRemoved);
				if (corpusContextCall)
					iRemovedRef.argvalue += myText.mft.filterTransitions(corpus.annotations, TUnb, beginAddress,
							sOutput, anXRefWasRemovedRef);
				else
					iRemovedRef.argvalue += myText.mft.filterTransitions(myText.annotations, TUnb, beginAddress,
							sOutput, anXRefWasRemovedRef);
				anXRefWasRemoved = anXRefWasRemovedRef.argvalue;
				if (anXRefWasRemoved)
					myText.mft.filterInconsistentXrefs(annotations, TUnb);
			}
		}

		// count the number of xref
		int nbXRef = 0;

		for (int ia = 4; ia < seqOfAnnotations.size(); ia += 3)
		{
			String sOutput = seqOfAnnotations.get(ia + 1).toString();
			if (!sOutput.equals("") && sOutput.indexOf("XREF") != -1)
				nbXRef++;
		}

		for (int ia = 4; ia < seqOfAnnotations.size(); ia += 3)
		{
			double absoluteBeginAddress = (Double) seqOfAnnotations.get(ia);
			RefObject<Double> absBegAddressRef = new RefObject<Double>(absoluteBeginAddress);
			// make sure beginning of annotation does not start at spaces or XML tags
			if (corpusContextCall)
				engine.moveBegAddressOufOfSpaces(myText.buffer, corpus.xmlNodes != null, absBegAddressRef);
			else
				engine.moveBegAddressOufOfSpaces(myText.buffer, myText.XmlNodes != null, absBegAddressRef);
			absoluteBeginAddress = absBegAddressRef.argvalue;
			String sOutput = seqOfAnnotations.get(ia + 1).toString();
			double absoluteEndAddress = (Double) seqOfAnnotations.get(ia + 2);

			if (absoluteBeginAddress < absoluteEndAddress) // Insert the annotation
			{
				double relativeBeginAddress = absoluteBeginAddress - myText.mft.tuAddresses[TUnb];
				double relativeEndAddress = absoluteEndAddress - myText.mft.tuAddresses[TUnb];
				if (corpusContextCall && corpus.hPhrases == null)
					corpus.hPhrases = new HashMap<String, Integer>();
				else if (!corpusContextCall && myText.hPhrases == null)
					myText.hPhrases = new HashMap<String, Integer>();

				String sInput = Dic.cleanupEntry(
						myText.buffer.substring((int) absoluteBeginAddress, (int) absoluteEndAddress),
						myText.XmlNodes != null);

				// add XREF numbers
				if (!sOutput.equals("") && sOutput.indexOf("XREF") != -1)
					sOutput = sOutput.replace("XREF", "XREF=" + xRefNb + "." + nbXRef);
				sOutput = Dic.cleanUpDoubleQuotes(sOutput);
				if (corpusContextCall)
				{
					if (!Dic.isALexicalAnnotation(sOutput))
					{
						// a syntactic annotation, e.g. DATE+Informal or NP+Human+plural
						String lexeme = sInput + ",SYNTAX," + sOutput;
						if (Dic.isALexicalAnnotation(lexeme))
							engine.addSyntaxToCorpus(annotations, null, corpus.hPhrases, lexeme, myText.mft, TUnb,
									relativeBeginAddress, relativeEndAddress, false);
						else
							engine.addSyntaxToCorpus(annotations, null, corpus.hPhrases, sInput + ",INVALIDANNOTATION",
									myText.mft, TUnb, relativeBeginAddress, relativeEndAddress, false);
					}
					else
						// a lexical annotation, e.g. eat,V
						engine.addLexemeToCorpus(annotations, corpus.hPhrases, sInput + "," + sOutput, myText.mft,
								TUnb, relativeBeginAddress, relativeEndAddress);
				}
				else
				{
					if (!Dic.isALexicalAnnotation(sOutput))
						engine.addLexemeToText(annotations, myText.hPhrases, sInput + ",SYNTAX," + sOutput, myText.mft,
								TUnb, relativeBeginAddress, relativeEndAddress);
					else
						engine.addLexemeToText(annotations, myText.hPhrases, sInput + "," + sOutput, myText.mft, TUnb,
								relativeBeginAddress, relativeEndAddress);
				}

				iAddedRef.argvalue++;
			}
		}
	}

	/**
	 * Reset button function of concordance.
	 */
	public void reset()
	{
		for (int i = 0; i < theItems.size(); i += 4)
			theItems.set(i + 2, true);

		JTextField beforeTF = concordanceShell.getBeforeTF();
		JTextField afterTF = concordanceShell.getAfterTF();

		// This construction was made when removing static variables and methods...
		TextBoxConcordanceActionListener textBoxConcordanceActionListener = (TextBoxConcordanceActionListener) concordanceShell
				.getBeforeTF().getKeyListeners()[0];
		textBoxConcordanceActionListener.reset(beforeTF, afterTF);
	}

	/**
	 * Function changes color of sequences in text to be equal as color of identical sequences in concordance.
	 */

	private void mColorClick()
	{
		if (corpusController != null && corpusController.getShell() != null)
		{
			textController = corpusController.getTextController();

			if (textController != null)
			{
				textController.getTextShell().dispose();
				corpusController.setTextController(null);
				textController = null;
			}

			corpusController.setListOfConcordanceFiles(new ArrayList<String>());
			corpusController.setListOfColors(new ArrayList<Color>());
			corpusController.setAbsoluteBeginAddresses(new ArrayList<Integer>());
			corpusController.setAbsoluteEndAddresses(new ArrayList<Integer>());

			for (int i = 0; i < theItems.size(); i += 4) // PAS DE LIMITATION A 250 LIGNES
			{
				Object[] item = (Object[]) theItems.get(i + 1);
				boolean isSelected = (Boolean) theItems.get(i + 2);
				if (!isSelected)
					continue;

				ArrayList<?> annotation = (ArrayList<?>) item[5];

				// compute beg and end addresses
				double absoluteBeginAddress0 = (Double) annotation.get(2);
				double absoluteEndAddress0 = (Double) annotation.get(3);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;
				int absoluteEndAddress = (int) absoluteEndAddress0;

				corpusController.getListOfConcordanceFiles().add(item[0].toString());
				corpusController.getListOfColors().add((Color) item[4]);
				corpusController.getAbsoluteBeginAddresses().add(absoluteBeginAddress);
				corpusController.getAbsoluteEndAddresses().add(absoluteEndAddress);

				corpusController.setColored(true);
			}
		}

		else
		{
			textController.setListOfColors(new ArrayList<Color>());
			textController.setAbsoluteBeginAddresses(new ArrayList<Integer>());
			textController.setAbsoluteEndAddresses(new ArrayList<Integer>());

			String myTextBuffer = textController.getMyText().buffer;

			for (int i = 0; i < theItems.size(); i += 4) // PAS DE LIMITATION A 250 LIGNES
			{
				Object[] item = (Object[]) theItems.get(i + 1);
				boolean isSelected = (Boolean) theItems.get(i + 2);
				if (!isSelected)
					continue;

				ArrayList<?> annotation = (ArrayList<?>) item[5];

				// compute beg and end addresses
				double absoluteBeginAddress0 = (Double) annotation.get(2);
				double absoluteEndAddress0 = (Double) annotation.get(3);
				int absoluteBeginAddress = (int) absoluteBeginAddress0;
				int absoluteEndAddress = (int) absoluteEndAddress0;

				if (absoluteBeginAddress0 != absoluteEndAddress0 && absoluteBeginAddress == absoluteEndAddress)
				{
					while (Language.isLetter(myTextBuffer.charAt(absoluteEndAddress)))
						absoluteEndAddress++;
				}

				textController.getListOfColors().add((Color) item[4]);
				textController.getAbsoluteBeginAddresses().add(absoluteBeginAddress);
				textController.getAbsoluteEndAddresses().add(absoluteEndAddress);

				textController.setColored(true);
			}

			TextEditorShell textShell = textController.getTextShell();
			UnitSelectionListener unitSelectionListener = textShell.getUnitSelectionListener();
			unitSelectionListener.paintTextInRGB();
			unitSelectionListener.partialColorText(textController, textController.getMyText(), textShell.getTextPane(),
					textShell, true);
		}
	}

	// Load and save
	public void loadConcordance(String fileName)
	{
		try
		{
			ConcordanceData cd = ConcordanceData.load(fileName);

			if (cd == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_CONCORDANCE,
						Constants.NOOJ_FILE_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			Launcher.getDesktopPane().add(concordanceShell);
			concordanceShell.setVisible(true);

			this.theItems.addAll(cd.getTheItems());
			this.refreshConcordance();
			
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_CONCORDANCE,
					Constants.NOOJ_FILE_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void saveConcordance()
	{
		if (Launcher.getSaveConcordanceChooser().showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return;

		File selectedFile = Launcher.getSaveConcordanceChooser().getSelectedFile();
		String fileName = FilenameUtils.removeExtension(selectedFile.getName()) + "." + Constants.JNCC_EXTENSION;
		String parentPath = selectedFile.getParent();
		String filePath = parentPath + System.getProperty("file.separator") + fileName;
		selectedFile = new File(filePath);

		if (selectedFile.exists())
		{
			int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), fileName + " already exists."
					+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, null, null);
			if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
				return;

			selectedFile.delete();
		}

		ConcordanceData cd;
		if (this.corpusController != null && corpusController.getShell() != null)
			cd = new ConcordanceData(corpusController.getFullPath(), this.theItems);
		else
			cd = new ConcordanceData(textController.getFileToBeOpenedOrImported().getAbsolutePath(), this.theItems);

		try
		{
			cd.save(filePath);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SAVE_CONCORDANCE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * Functions saves concordance in NooJ's default folder (not implemented).
	 */

	public void saveConcordanceForNooJ()
	{
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_NOT_IMPLEMENTED,
				Constants.CANNOT_SAVE_CONCORDANCE_FOR_NOOJ, JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	// getters and setters
	public static void setBefore(int before)
	{
		ConcordanceShellController.before = before;
	}

	public static void setAfter(int after)
	{
		ConcordanceShellController.after = after;
	}

	public void setTheItems(List<Object> theItems)
	{
		this.theItems = theItems;
	}

	public static int getBefore()
	{
		return before;
	}

	public static int getAfter()
	{
		return after;
	}

	public ConcordanceShell getConcordanceShell()
	{
		return concordanceShell;
	}

	public DefaultTableModel getTableModel()
	{
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel)
	{
		this.tableModel = tableModel;
	}

	public void setCbMatchesIsPressed(boolean cbMatchesIsPressed)
	{
		this.cbMatchesIsPressed = cbMatchesIsPressed;
	}

	public List<Object> getTheItems()
	{
		return theItems;
	}

	public JTable getConcordanceTable()
	{
		return concordanceTable;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public boolean isCbMatchesIsPressed()
	{
		return cbMatchesIsPressed;
	}

	public boolean isRbCharactersIsPressed()
	{
		return rbCharactersIsPressed;
	}

	public boolean isCbOutputsIsPressed()
	{
		return cbOutputsIsPressed;
	}

	public void setCbOutputsIsPressed(boolean cbOutputsIsPressed)
	{
		this.cbOutputsIsPressed = cbOutputsIsPressed;
	}

	public Map<Color, Integer> getColorMap()
	{
		return colorMap;
	}

	public Language getLan()
	{
		return Lan;
	}

	public boolean isConcordanceTableSorted()
	{
		return isConcordanceTableSorted;
	}

	public void setConcordanceTableSorted(boolean isConcordanceTableSorted)
	{
		this.isConcordanceTableSorted = isConcordanceTableSorted;
	}

	public void setConcordanceTable(JTable concordanceTable)
	{
		this.concordanceTable = concordanceTable;
	}

	public SyntacticTreeShell getSyntacticTreeShell()
	{
		return syntacticTreeShell;
	}

	public void setSyntacticTreeShell(SyntacticTreeShell syntacticTreeShell)
	{
		this.syntacticTreeShell = syntacticTreeShell;
	}
}