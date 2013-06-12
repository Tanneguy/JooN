package net.nooj4nlp.gui.actions.documents;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.tree.TreeNode;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.controller.FlexDescEditorShell.FlexDescEditorShellController;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.PropDefEditorShell.PropDefEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.DictionaryDialog;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * ActionListener that searches for patterns in text
 * 
 */
public class FindReplaceEvents
{
	// variables
	private int indexToText;
	private int iMatchCollection;
	private int labDicoContext;
	private int graphNumber, nodeNumber, position, length;
	private Matcher matchCollection;
	private Pattern rexp;
	private String buffer;

	private int highlightRemainder = 0;

	// an active frame
	private JInternalFrame activeFrame;
	private JTextArea rtBox;
	// text pane of an active frame
	private JTextPane rtb;
	// painter of the text pane
	private DefaultHighlightPainter highlightPainter;
	// components of a frame
	private JComboBox findComboBox;
	private JComboBox replaceComboBox;
	private JRadioButton rbExact;
	private JButton buttonNext;
	private JButton buttonReplace;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            - Find Replace dialog
	 */
	public FindReplaceEvents(FindReplaceDialog dialog)
	{
		this.indexToText = -1;
		this.iMatchCollection = 0;
		this.matchCollection = null;
		this.highlightRemainder = 0;
		this.buffer = "";
		this.rtBox = null;

		this.highlightPainter = new DefaultHighlightPainter(Color.YELLOW);

		this.rtb = dialog.getRtb();
		this.activeFrame = dialog.getActiveFrame();
		this.findComboBox = dialog.getComboFindPattern();
		this.replaceComboBox = dialog.getComboReplacePattern();

		this.findComboBox.setFont(Launcher.preferences.DFont);
		this.replaceComboBox.setFont(Launcher.preferences.DFont);

		this.rbExact = dialog.getRdbtnExactPattern();
		this.buttonNext = dialog.getBtnNext();
		this.buttonReplace = dialog.getBtnReplaceThenNext();
		this.labDicoContext = dialog.getLabDicoContext();
	}

	/**
	 * Function for finding and highlighting first occurrence of pattern in text pane.
	 */
	public void find()
	{
		this.highlightRemainder = 0;
		String cls = this.activeFrame.getClass().getSimpleName();
		boolean grammarContextCall = cls.equals("GrammarEditorShell");

		if ((this.rtb == null || this.rtb.getStyledDocument().getLength() == 0) && this.buffer.equals("")
				&& !grammarContextCall)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_FIND_ACTION_ERROR,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Initialize the return value to false by default.
		String pattern = this.findComboBox.getSelectedItem().toString();

		// Ensure that a search string has been specified and a valid start point.

		if (pattern.length() == 0)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_EMPTY_PATTERN,
					Constants.FIND_REPLACE_EMPTY_PATTERN_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (grammarContextCall)
		{
			boolean found;
			GrammarEditorShell grammarShell = (GrammarEditorShell) activeFrame;
			GrammarEditorShellController grammarController = grammarShell.getController();
			graphNumber = grammarController.current;

			RefObject<Integer> graphNumberRef = new RefObject<Integer>(graphNumber);
			RefObject<Integer> nodeNumberRef = new RefObject<Integer>(nodeNumber);
			RefObject<Integer> positionRef = new RefObject<Integer>(position);
			RefObject<Integer> lengthRef = new RefObject<Integer>(length);

			if (rbExact.isSelected())
			{
				rexp = null;

				found = grammarController.findPattern(pattern, -1, graphNumberRef, nodeNumberRef, positionRef,
						lengthRef);
			}

			else
			{
				try
				{
					rexp = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				found = grammarController.findPattern(rexp, -1, graphNumberRef, nodeNumberRef, positionRef, lengthRef);
			}

			graphNumber = graphNumberRef.argvalue;
			nodeNumber = nodeNumberRef.argvalue;
			position = positionRef.argvalue;
			length = lengthRef.argvalue;

			if (!found)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_PATTERN_NOT_FOUND,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			grammarController.current = graphNumber;
			grammarController.grf = grammarController.grammar.graphs.get(graphNumber);

			for (int i = 0; i < grammarController.grf.selected.size(); i++)
				grammarController.grf.selected.set(i, false);

			grammarController.editor.paintImmediately(grammarController.editor.getBounds());

			grammarController.UpdateGraphStructureFromTop((TreeNode) grammarController.formGramStruct.tvGraphs
					.getModel().getRoot());

			grammarController.displayRtbox(nodeNumber);

			this.rtBox = grammarShell.getRtBox();

			try
			{
				rtBox.getHighlighter().removeAllHighlights();
				rtBox.getHighlighter().addHighlight(position, position + length, highlightPainter);
				rtBox.setCaretPosition(position);
			}
			catch (BadLocationException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			buttonNext.setEnabled(true);
			buttonReplace.setEnabled(true);
			return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			int start = 0;
			if (cls.equals("DictionaryEditorShell") || labDicoContext > 0)
				start = DictionaryEditorShellController.sizeOfHeader();

			if (rbExact.isSelected())
			{
				indexToText = rtb.getText().indexOf(pattern, start);

				if (indexToText == -1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_EXACT_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				try
				{
					rtb.getHighlighter().removeAllHighlights();
					rtb.getHighlighter().addHighlight(indexToText, indexToText + pattern.length(), highlightPainter);
					rtb.setCaretPosition(indexToText);
				}

				catch (BadLocationException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			else
			{
				try
				{
					rexp = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				matchCollection = rexp.matcher(rtb.getText());
				matchCollection.region(start, rtb.getText().length());

				if (!matchCollection.find())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_PERL_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				iMatchCollection = 0;

				try
				{
					rtb.getHighlighter().removeAllHighlights();
					rtb.getHighlighter().addHighlight(matchCollection.start(), matchCollection.end(), highlightPainter);
					rtb.setCaretPosition(matchCollection.start());
				}

				catch (BadLocationException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			buttonNext.setEnabled(true);
			buttonReplace.setEnabled(true);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for finding and highlighting next occurrence of pattern (if exists) in text pane.
	 */
	public void next()
	{
		String pattern = this.findComboBox.getSelectedItem().toString();

		if (pattern.length() == 0)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_INVALID_PATTERN_REGULAR,
					Constants.FIND_REPLACE_EMPTY_PATTERN_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String cls = this.activeFrame.getClass().getSimpleName();
		boolean grammarContextCall = cls.equals("GrammarEditorShell");

		if (grammarContextCall)
		{
			boolean found;
			GrammarEditorShellController grammarController = ((GrammarEditorShell) activeFrame).getController();
			graphNumber = grammarController.current;

			RefObject<Integer> graphNumberRef = new RefObject<Integer>(graphNumber);
			RefObject<Integer> nodeNumberRef = new RefObject<Integer>(nodeNumber);
			RefObject<Integer> positionRef = new RefObject<Integer>(position);
			RefObject<Integer> lengthRef = new RefObject<Integer>(length);

			if (rexp == null)
				found = grammarController.findNextPattern(pattern, -1, graphNumberRef, nodeNumberRef, positionRef,
						lengthRef);

			else
				found = grammarController.findNextPattern(rexp, -1, graphNumberRef, nodeNumberRef, positionRef,
						lengthRef);

			graphNumber = graphNumberRef.argvalue;
			nodeNumber = nodeNumberRef.argvalue;
			position = positionRef.argvalue;
			length = lengthRef.argvalue;

			if (!found)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_PATTERN_NOT_FOUND,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			grammarController.current = graphNumber;
			grammarController.grf = grammarController.grammar.graphs.get(graphNumber);

			for (int i = 0; i < grammarController.grf.selected.size(); i++)
				grammarController.grf.selected.set(i, false);

			grammarController.editor.paintImmediately(grammarController.editor.getBounds());

			grammarController.UpdateGraphStructureFromTop((TreeNode) grammarController.formGramStruct.tvGraphs
					.getModel().getRoot());

			grammarController.displayRtbox(nodeNumber);
			grammarController.editor.rtBox.select(position, position + length);

			buttonNext.setEnabled(true);
			buttonReplace.setEnabled(true);
			return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			int start = this.rtb.getHighlighter().getHighlights()[0].getEndOffset();

			if (rbExact.isSelected())
			{
				indexToText = rtb.getText().indexOf(pattern, start);

				if (indexToText == -1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_NO_MORE_MATCH,
							Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);

					buttonNext.setEnabled(false);
					buttonReplace.setEnabled(false);
					return;
				}

				try
				{
					rtb.getHighlighter().removeAllHighlights();
					rtb.getHighlighter().addHighlight(indexToText, indexToText + pattern.length(), highlightPainter);
					rtb.setCaretPosition(indexToText);
				}

				catch (BadLocationException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			else
			{
				iMatchCollection++;

				try
				{
					rexp = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!matchCollection.find())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_NO_MORE_MATCH,
							Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);

					buttonNext.setEnabled(false);
					buttonReplace.setEnabled(false);
					return;
				}

				try
				{
					rtb.getHighlighter().removeAllHighlights();
					rtb.getHighlighter().addHighlight(matchCollection.start() - highlightRemainder,
							matchCollection.end() - highlightRemainder, highlightPainter);
					rtb.setCaretPosition(matchCollection.start() - highlightRemainder);
				}

				catch (BadLocationException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for replacing highlighted text with a desired one and highlighting next occurrence of pattern (if
	 * exists) in text pane.
	 */

	public void replace()
	{
		String cls = this.activeFrame.getClass().getSimpleName();
		boolean grammarContextCall = cls.equals("GrammarEditorShell");

		// get patterns and their lengths
		String replacementText = this.replaceComboBox.getSelectedItem().toString();
		String pattern = this.findComboBox.getSelectedItem().toString();
		int patternLength = pattern.length();
		int replacementLength = replacementText.length();

		if (Launcher.preferences.multiplebackups)
		{
			if (cls.equals("DictionaryEditorShell"))
			{
				DictionaryEditorShellController dictionaryController = ((DictionaryEditorShell) activeFrame)
						.getController();
				if (dictionaryController.getFullName() != null && dictionaryController.isModified())
					dictionaryController.save();
			}
			else if (cls.equals("FlexDescEditorShell"))
			{
				FlexDescEditorShellController flexDescController = ((FlexDescEditorShell) activeFrame).getController();
				if (flexDescController.getFullName() != null && flexDescController.isModified())
					flexDescController.save();
			}
			else if (cls.equals("PropDefEditorShell"))
			{
				PropDefEditorShellController propDefController = ((PropDefEditorShell) activeFrame).getController();
				if (propDefController.getFullName() != null && propDefController.isModified())
					propDefController.save();
			}
		}

		if (grammarContextCall)
		{
			GrammarEditorShell grammarShell = (GrammarEditorShell) activeFrame;
			GrammarEditorShellController grammarController = grammarShell.getController();

			JTextArea rtBox = grammarShell.getRtBox();

			rtBox.replaceRange(replacementText, position, position + length);
			Graph grf = grammarController.grammar.graphs.get(graphNumber);

			grf.label.set(nodeNumber, rtBox.getText());
			grammarController.hideRtbox();

			next();

			return;
		}

		Highlight[] highlightList = rtb.getHighlighter().getHighlights();

		if (rtb == null || rtb.getText().length() == 0 || highlightList.length == 0)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_CANNOT_PERFORM_REPLACEMENT,
					Constants.NOOJ_RESOURCES_DIRECTORY + ": " + Constants.FIND_REPLACE_FIND_ACTION_ERROR,
					JOptionPane.ERROR_MESSAGE);

			buttonNext.setEnabled(false);
			buttonReplace.setEnabled(false);
			return;
		}

		String text = rtb.getText();

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			TextEditorShell textShell = null;

			if (rbExact.isSelected())
			{
				if (pattern.equals(""))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_ENTER_EXACT_PATTERN, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				Highlight highlighter = rtb.getHighlighter().getHighlights()[0];
				int start = highlighter.getEndOffset();
				indexToText = rtb.getText().indexOf(pattern, start);

				// replace n-th pattern from the text, starting from index
				int replaceStart = highlighter.getStartOffset();

				try
				{
					text = replaceFirstFrom(text, replaceStart, pattern, replacementText);
				}
				catch (BadLocationException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(), Constants.NOOJ_ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (cls.equals("TextEditorShell"))
				{
					textShell = (TextEditorShell) activeFrame;
					// remove and add Caret listeners to avoid concurent modifications
					rtb.removeCaretListener(textShell.getUnitSelectionListener());
				}

				rtb.setText(text);

				if (indexToText == -1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_CANNOT_PERFORM_REPLACEMENT,
							Constants.FIND_REPLACE_NO_MORE_MATCH_TO_WORK_WITH, JOptionPane.INFORMATION_MESSAGE);

					if (textShell != null)
						// remove and add Caret listeners to avoid concurent modifications
						rtb.addCaretListener(textShell.getUnitSelectionListener());

					buttonNext.setEnabled(false);
					buttonReplace.setEnabled(false);
					return;
				}

				indexToText = indexToText - (patternLength - replacementLength);
				try
				{
					if (textShell != null)
					{
						rtb.addCaretListener(textShell.getUnitSelectionListener());
						textShell.getTextController().textHasJustBeenEdited();
					}

					rtb.getHighlighter().removeAllHighlights();
					rtb.getHighlighter().addHighlight(indexToText, indexToText + pattern.length(), highlightPainter);
					rtb.setCaretPosition(indexToText);
				}

				catch (BadLocationException e)
				{
					JOptionPane
							.showMessageDialog(null, e.getMessage(), Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			else
			{
				if (cls.equals("TextEditorShell"))
				{
					textShell = (TextEditorShell) activeFrame;
					// remove and add Caret listeners to avoid concurent modifications
					rtb.removeCaretListener(textShell.getUnitSelectionListener());
				}

				int start = 0;
				if (cls.equals("DictionaryEditorShell") || labDicoContext > 0)
					start = DictionaryEditorShellController.sizeOfHeader();

				// create new matcher from actual text
				matchCollection = rexp.matcher(rtb.getText());
				matchCollection.region(start, rtb.getText().length());

				// count how many matching patterns are in replace string
				Matcher helpMatcher = rexp.matcher(replacementText);
				int patternInReplaceCounter = -1;
				while (helpMatcher.find())
					patternInReplaceCounter++;

				int helpVar = 0;
				StringBuffer buffer = new StringBuffer();
				// indices of new search region
				int startOfNewRegion = 0;
				int endOfNewRegion = 0;

				while (matchCollection.find())
				{
					if (helpVar == iMatchCollection)
					{
						int index = matchCollection.end();
						matchCollection.appendReplacement(buffer, replacementText);

						// count in all the new search matches from replaced string
						iMatchCollection += patternInReplaceCounter;
						// set difference remainder for pattern and replacement to use on Caret and highlight
						this.highlightRemainder = matchCollection.group().length() - replacementLength;

						// set new indices
						startOfNewRegion = index;
						endOfNewRegion = text.length();
						break;
					}

					helpVar++;
				}

				// set text buffer and new search region
				matchCollection.appendTail(buffer);
				matchCollection.region(startOfNewRegion, endOfNewRegion);

				rtb.setText(buffer.toString());

				if (textShell != null)
					// remove and add Caret listeners to avoid concurent modifications
					rtb.addCaretListener(textShell.getUnitSelectionListener());

				if (!matchCollection.find())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_CANNOT_PERFORM_REPLACEMENT,
							Constants.FIND_REPLACE_NO_MORE_MATCH_TO_WORK_WITH, JOptionPane.INFORMATION_MESSAGE);

					buttonNext.setEnabled(false);
					buttonReplace.setEnabled(false);
					return;
				}

				else
				{
					try
					{
						rtb.getHighlighter().removeAllHighlights();
						rtb.getHighlighter().addHighlight(matchCollection.start() - highlightRemainder,
								matchCollection.end() - highlightRemainder, highlightPainter);
						if (textShell != null)
							textShell.getTextController().textHasJustBeenEdited();
						rtb.setCaretPosition(matchCollection.start() - highlightRemainder);
					}

					catch (BadLocationException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_ERROR,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				iMatchCollection++;
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for replacing all occurrences of pattern (if exists) in text pane.
	 */

	public void replaceAll()
	{
		String cls = this.activeFrame.getClass().getSimpleName();
		boolean grammarContext = cls.equals("GrammarEditorShell");

		if (!grammarContext && (this.rtb == null || this.rtb.getText().length() == 0) && this.buffer.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_FIND_ACTION_ERROR,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (Launcher.preferences.multiplebackups)
		{
			if (cls.equals("DictionaryEditorShell"))
			{
				DictionaryEditorShellController dictionaryController = ((DictionaryEditorShell) activeFrame)
						.getController();
				if (dictionaryController.getFullName() != null && dictionaryController.isModified())
					dictionaryController.save();
			}
			else if (cls.equals("FlexDescEditorShell"))
			{
				FlexDescEditorShellController flexDescController = ((FlexDescEditorShell) activeFrame).getController();
				if (flexDescController.getFullName() != null && flexDescController.isModified())
					flexDescController.save();
			}
			else if (cls.equals("PropDefEditorShell"))
			{
				PropDefEditorShellController propDefController = ((PropDefEditorShell) activeFrame).getController();
				if (propDefController.getFullName() != null && propDefController.isModified())
					propDefController.save();
			}
		}

		// get patterns
		String replacementText = this.replaceComboBox.getSelectedItem().toString();
		String pattern = this.findComboBox.getSelectedItem().toString();

		if (pattern.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_ENTER_EXACT_PATTERN,
					Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (cls.equals("GrammarEditorShell"))
		{
			GrammarEditorShell grammarShell = (GrammarEditorShell) activeFrame;
			GrammarEditorShellController grammarController = grammarShell.getController();

			int count = 0;
			if (rbExact.isSelected())
			{
				rexp = null;
				count = grammarController.replaceAll(pattern, replacementText);
			}

			else
			{
				try
				{
					rexp = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				count = grammarController.replaceAllPerl(rexp, replacementText);
			}

			if (rtBox != null)
				grammarController.hideRtbox();

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), count + " replacements.",
					Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			TextEditorShell textShell = null;

			String text = "";
			String buffer2 = "";
			int start = 0;

			if (this.rtb != null)
				text = this.rtb.getText();
			else if (!this.buffer.equals(""))
				text = buffer;

			if (cls.equals("DictionaryEditorShell") || labDicoContext > 0)
				start = DictionaryEditorShellController.sizeOfHeader();
			else
				start = 0;

			int cpt = 0;

			if (rbExact.isSelected())
			{
				if (cls.equals("TextEditorShell"))
				{
					textShell = (TextEditorShell) activeFrame;
					// remove and add Caret listeners to avoid concurrent modifications
					rtb.removeCaretListener(textShell.getUnitSelectionListener());
				}

				start = text.indexOf(pattern, start);

				if (start < 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_EXACT_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				int currentPosition = 0;
				StringBuilder tmp = new StringBuilder();

				while (start >= 0)
				{
					tmp.append(text.substring(currentPosition, start));
					tmp.append(replacementText);
					currentPosition = start + pattern.length();

					start = text.indexOf(pattern, currentPosition);
					cpt++;
				}

				tmp.append(text.substring(currentPosition));

				if (labDicoContext > 0)
					buffer2 = tmp.toString();
				else if (this.rtb != null)
					this.rtb.setText(tmp.toString());

				if (textShell != null)
					// remove and add Caret listeners to avoid concurent modifications
					rtb.addCaretListener(textShell.getUnitSelectionListener());
			}

			else
			{
				Pattern rexp2;

				if (cls.equals("TextEditorShell"))
				{
					textShell = (TextEditorShell) activeFrame;
					// remove and add Caret listeners to avoid concurent modifications
					rtb.removeCaretListener(textShell.getUnitSelectionListener());
				}

				try
				{
					rexp2 = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				cpt = 0;

				// help matcher will count all occurrences in text
				Matcher helpMatcher = rexp2.matcher(rtb.getText());

				while (helpMatcher.find())
				{
					if (helpMatcher.end() < start)
						continue;
					else
						cpt++;
				}

				if (cpt == 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_PERL_PATTERN_NOT_FOUND_NOTIFICATION,
							Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				StringBuilder tmp = new StringBuilder();
				int currentPosition = 0;

				Matcher matchCollection2 = rexp2.matcher(rtb.getText());
				matchCollection2.region(start, rtb.getText().length());

				while (matchCollection2.find())
				{
					if (matchCollection2.end() < start)
						continue;
					tmp.append(rtb.getText().substring(currentPosition, matchCollection2.start()));
					tmp.append(replacementText);
					currentPosition = matchCollection2.start() + matchCollection2.group().length();
				}

				tmp.append(rtb.getText().substring(currentPosition));
				matchCollection2.replaceAll(replacementText);

				if (labDicoContext > 0)
					buffer2 = tmp.toString();
				else if (this.rtb != null)
					this.rtb.setText(tmp.toString());

				if (textShell != null)
					// remove and add Caret listeners to avoid concurent modifications
					rtb.addCaretListener(textShell.getUnitSelectionListener());
			}

			if (cls.equals("TextEditorShell"))
			{
				textShell = (TextEditorShell) activeFrame;
				if (!textShell.getTextPane().isEditable())
					textShell.getTextController().textHasJustBeenEdited();
			}

			if (labDicoContext > 0)
			{
				DictionaryDialog dictionaryDialog = (DictionaryDialog) activeFrame;
				DictionaryDialogController dicDialogController = dictionaryDialog.getController();

				String filePath = dicDialogController.getTxtDictionaryName().getText();
				File dicFile = new File(filePath);
				String fileName = "";
				String extension = "";

				if (dicFile != null)
				{
					String fileNameWithExtension = dicFile.getName();
					fileName = FilenameUtils.removeExtension(fileNameWithExtension);
					extension = FilenameUtils.getExtension(fileNameWithExtension);
				}

				String parentPath = dicFile.getParent();
				String fullPath = parentPath + System.getProperty("file.separator") + fileName + "-r" + "." + extension;
				PrintWriter pw;
				try
				{
					pw = new PrintWriter(fullPath);
				}
				catch (FileNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
					return;
				}

				pw.write(buffer2);
				pw.close();
				this.buffer = "";

				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_QUESTION_OF_WORKING_WITH_NEW_DIC + fullPath + " (" + cpt
								+ " replacement" + (cpt > 1 ? "s" : "") + ")?", Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;

				dicDialogController.getTxtDictionaryName().setText(fullPath);

				if (dicDialogController.loadLines(0, 500))
					dicDialogController.getTitledBorder().setTitle("Display " + (new File(fullPath)).getName());
				else
					dicDialogController.getTitledBorder().setTitle(
							"Display beginning of " + (new File(fullPath)).getName());

				dicDialogController.getFindReplaceDialog().dispose();
			}

			else
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), cpt + " replacement" + (cpt > 1 ? "s" : ""),
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);

			buttonNext.setEnabled(true);
			buttonReplace.setEnabled(true);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for extracting pattern (if exists) in text pane. Works for DictionaryEditorShell and DictionaryDialog.
	 */

	public void extract()
	{
		if ((this.rtb == null || this.rtb.getText().length() == 0) && this.buffer.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_FIND_ACTION_ERROR,
					Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			String pattern = this.findComboBox.getSelectedItem().toString();

			if (pattern.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_ENTER_EXACT_PATTERN,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.WARNING_MESSAGE);
				return;
			}

			int cpt = 0;

			String text = "";
			if (this.rtb != null)
				text = this.rtb.getText();
			else if (!this.buffer.equals(""))
				text = this.buffer;

			StringBuilder tmp = new StringBuilder();
			int start = DictionaryEditorShellController.sizeOfHeader();

			if (this.rbExact.isSelected())
			{
				int indexToText = text.indexOf(pattern, start);

				if (indexToText < 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_EXACT_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				while (indexToText >= 0)
				{
					int beginOfLine, endOfLine;
					for (beginOfLine = indexToText; beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					for (endOfLine = indexToText; endOfLine < text.length() && text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;

					if (beginOfLine >= start)
						tmp.append(text.substring(beginOfLine, endOfLine));

					start = indexToText + pattern.length();
					indexToText = text.indexOf(pattern, start);
					cpt++;
				}
			}

			else
			{
				Pattern rexp;

				try
				{
					rexp = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				matchCollection = rexp.matcher(text);
				matchCollection.region(DictionaryEditorShellController.sizeOfHeader(), text.length());

				// count all the occurrences in text starting below the title (if it exists)
				Matcher helpMatcher = rexp.matcher(text);
				helpMatcher.region(start, text.length());
				while (helpMatcher.find())
					cpt++;

				if (cpt == 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_PERL_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				int beginOfLine = -1;
				int endOfLine = -1;

				while (matchCollection.find())
				{
					for (beginOfLine = matchCollection.start(); beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					if (beginOfLine < endOfLine) // line has already been extracted previously
						continue;

					for (endOfLine = matchCollection.start(); endOfLine < text.length()
							&& text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;

					tmp.append(text.substring(beginOfLine, endOfLine));
				}
			}

			if (cpt == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_NO_LINE_HAS_BEEN_EXTRACTED, Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			String cls = this.activeFrame.getClass().getSimpleName();

			if (cls.equals("DictionaryEditorShell"))
			{
				DictionaryEditorShell dicShell = new DictionaryEditorShell();
				DictionaryEditorShellController dicController = dicShell.getController();
				dicController.initLoad(((DictionaryEditorShell) activeFrame).getController().getLan().isoName);
				String newRtbPaneText = dicController.getTextPane().getText() + "#extract \"" + pattern + "\"\n"
						+ tmp.toString();
				dicController.getTextPane().setText(newRtbPaneText);
				dicController.getLblnTus().setText(
						"Dictionary contains " + DictionaryDialogController.count(dicController.getTextPane())
								+ " entries.");

				Launcher.getDesktopPane().add(dicShell);
				dicShell.setVisible(true);

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), cpt + " match" + (cpt > 1 ? "es have" : "has")
						+ " been extracted.", Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
			}

			else if (cls.equals("DictionaryDialog"))
			{
				DictionaryDialog dicDialog = (DictionaryDialog) activeFrame;
				DictionaryDialogController dicController = dicDialog.getController();

				String filePath = dicController.getTxtDictionaryName().getText();
				File dicFile = new File(filePath);
				String fileName = "";
				String extension = "";

				if (dicFile != null)
				{
					String fileNameWithExtension = dicFile.getName();
					fileName = FilenameUtils.removeExtension(fileNameWithExtension);
					extension = FilenameUtils.getExtension(fileNameWithExtension);
				}

				String parentPath = dicFile.getParent();
				String fullPath = parentPath + System.getProperty("file.separator") + fileName + "-e" + "." + extension;
				PrintWriter pw;
				String isoName = "";

				if (this.rtb != null)
					isoName = DictionaryEditorShellController.getLanguage();

				try
				{
					pw = new PrintWriter(fullPath);
				}
				catch (FileNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
					return;
				}

				pw.write(DictionaryEditorShellController.buildHeader(isoName));
				pw.write("# extract \"" + pattern + "\"\n");
				pw.write(tmp.toString());
				pw.close();

				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_QUESTION_OF_WORKING_WITH_NEW_DIC + fullPath + " (" + cpt + " match"
								+ (cpt > 1 ? "es" : "") + ")?", Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;

				dicController.getTxtDictionaryName().setText(fullPath);

				if (dicController.loadLines(0, 500))
					dicController.getTitledBorder().setTitle("Display " + (new File(fullPath)).getName());
				else
					dicController.getTitledBorder().setTitle("Display beginning of " + (new File(fullPath)).getName());

				dicController.getFindReplaceDialog().dispose();
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for extracting pattern (if exists) in text pane. Works for DictionaryEditorShell and DictionaryDialog.
	 */

	public void filter()
	{
		String cls = this.activeFrame.getClass().getSimpleName();

		if ((this.rtb == null || this.rtb.getText().length() == 0) && this.buffer.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_FIND_ACTION_ERROR,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			String pattern = this.findComboBox.getSelectedItem().toString();
			if (pattern.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_ENTER_EXACT_PATTERN,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.WARNING_MESSAGE);
				return;
			}

			int cpt = 0;
			String text = "";

			if (cls.equals("DictionaryEditorShell"))
				text = this.rtb.getText();

			else if (cls.equals("DictionaryDialog"))
				text = this.buffer;

			StringBuilder tmp = new StringBuilder();

			int start = 0;
			if (cls.equals("DictionaryEditorShell") || labDicoContext > 0)
				start = DictionaryEditorShellController.sizeOfHeader();

			if (this.rbExact.isSelected())
			{
				int indexToText = text.indexOf(pattern, start);
				if (indexToText < 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_EXACT_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				int currentPosition = 0;
				while (indexToText >= 0)
				{
					int beginOfLine, endOfLine;
					for (beginOfLine = indexToText; beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					for (endOfLine = indexToText; endOfLine < text.length() && text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;

					if (currentPosition < beginOfLine)
						tmp.append(text.substring(currentPosition, beginOfLine));
					currentPosition = endOfLine;

					start = indexToText + pattern.length();
					indexToText = text.indexOf(pattern, start);
					cpt++;
				}

				tmp.append(text.substring(currentPosition));
			}

			else
			{
				Pattern rexp2;

				try
				{
					rexp2 = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				matchCollection = rexp2.matcher(text);
				matchCollection.region(start, text.length());

				// count all matches starting below the title (if exists)
				Matcher helpMatcher = rexp2.matcher(text);
				helpMatcher.region(start, text.length());

				while (helpMatcher.find())
					cpt++;

				if (cpt == 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_PERL_PATTERN_NOT_FOUND_NOTIFICATION,
							Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				int currentPosition = start;

				while (matchCollection.find())
				{
					int beginOfLine, endOfLine;
					for (beginOfLine = matchCollection.start(); beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					for (endOfLine = matchCollection.start(); endOfLine < text.length()
							&& text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;

					if (currentPosition < beginOfLine)
						tmp.append(text.substring(currentPosition, beginOfLine));
					currentPosition = endOfLine;
				}

				tmp.append(text.substring(currentPosition));
			}

			if (cpt == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_NO_LINE_HAS_BEEN_FILTERED, Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			if (cls.equals("DictionaryEditorShell"))
			{
				DictionaryEditorShell dicShell = new DictionaryEditorShell();
				DictionaryEditorShellController dicController = dicShell.getController();

				dicController.initLoad(((DictionaryEditorShell) activeFrame).getController().getLan().isoName);
				String newRtbPaneText = dicController.getTextPane().getText() + "# filter out \"" + pattern + "\"\n"
						+ tmp.toString();
				dicController.getTextPane().setText(newRtbPaneText);
				dicController.getLblnTus().setText(
						"Dictionary contains " + DictionaryDialogController.count(dicController.getTextPane())
								+ " entries.");

				Launcher.getDesktopPane().add(dicShell);
				dicShell.setVisible(true);
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), cpt + " match" + (cpt > 1 ? "es have" : "has")
						+ " been filtered out.", Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
			}

			else if (cls.equals("DictionaryDialog"))
			{
				DictionaryDialog dicDialog = (DictionaryDialog) activeFrame;
				DictionaryDialogController dicController = dicDialog.getController();

				String filePath = dicController.getTxtDictionaryName().getText();
				File dicFile = new File(filePath);
				String fileName = "";
				String extension = "";

				if (dicFile != null)
				{
					String fileNameWithExtension = dicFile.getName();
					fileName = FilenameUtils.removeExtension(fileNameWithExtension);
					extension = FilenameUtils.getExtension(fileNameWithExtension);
				}

				String parentPath = dicFile.getParent();
				String fullPath = parentPath + System.getProperty("file.separator") + fileName + "-f" + "." + extension;
				PrintWriter pw;
				String isoName = "";

				if (this.rtb != null)
					isoName = DictionaryEditorShellController.getLanguage();

				try
				{
					pw = new PrintWriter(fullPath);
				}
				catch (FileNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
					return;
				}

				pw.write(DictionaryEditorShellController.buildHeader(isoName));
				pw.write("# filter out \"" + pattern + "\"\n");
				pw.write(tmp.toString());
				pw.close();

				int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_QUESTION_OF_WORKING_WITH_NEW_DIC + fullPath + " (" + cpt + " match"
								+ (cpt > 1 ? "es" : "") + ")?", Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

				if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
					return;

				dicController.getTxtDictionaryName().setText(fullPath);

				if (dicController.loadLines(0, 500))
					dicController.getTitledBorder().setTitle("Display " + (new File(fullPath)).getName());
				else
					dicController.getTitledBorder().setTitle("Display beginning of " + (new File(fullPath)).getName());

				dicController.getFindReplaceDialog().dispose();
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function for counting all the occurrences of pattern in a Text Pane.
	 */

	public void count()
	{
		String cls = this.activeFrame.getClass().getSimpleName();

		if (this.rtb == null)
			return;

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			String text = this.rtb.getText();

			if (text.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.FIND_REPLACE_NOOJ_NO_TEXT_TO_LOOK_IN, Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int start = 0;
			if (cls.equals("DictionaryEditorShell"))
				start = DictionaryEditorShellController.sizeOfHeader();

			String pattern = this.findComboBox.getSelectedItem().toString();

			if (pattern.equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_ENTER_EXACT_PATTERN,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.WARNING_MESSAGE);
				return;
			}

			int cpt = 0;

			if (this.rbExact.isSelected())
			{
				int indexToText = text.indexOf(pattern, start);

				if (indexToText < 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_EXACT_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				while (indexToText >= 0)
				{
					int beginOfLine, endOfLine;
					for (beginOfLine = indexToText; beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					for (endOfLine = indexToText; endOfLine < text.length() && text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;

					start = indexToText + pattern.length();
					indexToText = text.indexOf(pattern, start);
					cpt++;
				}
			}

			else
			{
				Pattern rexp2;

				try
				{
					rexp2 = Pattern.compile(pattern, Pattern.MULTILINE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.FIND_REPLACE_INVALID_PATTERN, JOptionPane.ERROR_MESSAGE);
					return;
				}

				matchCollection = rexp2.matcher(text);
				matchCollection.region(start, text.length());

				// count all the occurrences in text starting below the title (if it exists)
				Matcher helpMatcher = rexp2.matcher(text);
				helpMatcher.region(start, text.length());
				while (helpMatcher.find())
					cpt++;

				if (cpt == 0)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.FIND_REPLACE_PERL_PATTERN_NOT_FOUND, Constants.NOOJ_RESOURCES_DIRECTORY,
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				while (matchCollection.find())
				{
					int beginOfLine, endOfLine;
					for (beginOfLine = matchCollection.start(); beginOfLine > 0 && text.charAt(beginOfLine) != '\n'; beginOfLine--)
						;
					if (text.charAt(beginOfLine) == '\n')
						beginOfLine++;

					for (endOfLine = matchCollection.start(); endOfLine < text.length()
							&& text.charAt(endOfLine) != '\n'; endOfLine++)
						;
					if (endOfLine < text.length())
						endOfLine++;
				}
			}

			if (cpt == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.FIND_REPLACE_NO_MATCHING_LINE,
						Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			else
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), cpt + " match"
						+ (cpt > 1 ? "es have" : " has") + " been found.", Constants.NOOJ_RESOURCES_DIRECTORY,
						JOptionPane.INFORMATION_MESSAGE);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function replaces all patterns in Text Pane, with a rule result from a chosen rule file.
	 */
	public void replaceAllFrom()
	{
		String cls = this.activeFrame.getClass().getSimpleName();
		int cpt = 0;
		int cptRules = 0;

		int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
				Constants.FIND_REPLACE_REPLACE_ALL_FROM_FILE_NOTIFY, Constants.NOOJ_RESOURCES_DIRECTORY,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

		if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
			return;

		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();
		int result = jFileChooser.showOpenDialog(this.activeFrame);
		// selected file is final!
		final File selectedFile = jFileChooser.getSelectedFile();
		// if file(s) has/ve been selected...
		if (result == JFileChooser.APPROVE_OPTION)
		{
			BufferedReader br = null;

			String patternFilePath = selectedFile.getAbsolutePath();

			File selectedFileFromPath = new File(patternFilePath);

			// if file name was incorrectly given from the input
			if (!selectedFile.exists())
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE + patternFilePath,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			try
			{
				br = new BufferedReader(new FileReader(selectedFileFromPath));
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (cls.equals("GrammarEditorShell"))
			{
				GrammarEditorShellController grammarController = ((GrammarEditorShell) activeFrame).getController();
				int count = 0;

				try
				{
					for (String line = br.readLine(); line != null; line = br.readLine())
					{
						// do not process comments in replacement file
						int i;

						for (i = 0; i < line.length() && line.charAt(i) != '#'; i++)
							;

						if (i < line.length())
							line = line.substring(0, i);

						if (line.equals(""))
							continue;

						i = line.indexOf(',');
						if (i == -1)
							continue;

						String pattern = line.substring(0, i);
						if (pattern.equals(""))
							continue;

						String replacementText = line.substring(i + 1);

						count += grammarController.replaceAll(pattern, replacementText);
					}

					br.close();

					if (rtBox != null)
						grammarController.hideRtbox();

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), count + " replacements.",
							Constants.NOOJ_RESOURCES_DIRECTORY, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				catch (IOException e)
				{
					try
					{
						if (br != null)
							br.close();
					}
					catch (IOException e1)
					{
						// Catch block does not do anything - message below should be written in each case.
					}

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			else
			{
				TextEditorShell textShell = null;

				try
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
					String text = this.rtb.getText();

					if (cls.equals("TextEditorShell"))
					{
						textShell = (TextEditorShell) activeFrame;
						textShell.getTextPane().removeCaretListener(textShell.getUnitSelectionListener());
					}

					try
					{
						for (String line = br.readLine(); line != null; line = br.readLine())
						{
							// do not process comments in replacement file
							int i;

							for (i = 0; i < line.length() && line.charAt(i) != '#'; i++)
								;

							if (i < line.length())
								line = line.substring(0, i);

							if (line.equals(""))
								continue;

							i = line.indexOf(',');
							if (i == -1)
								continue;

							String pattern = line.substring(0, i);
							if (pattern.equals(""))
								continue;

							String replacementText = line.substring(i + 1);

							// perform replacement pattern => to
							int start = 0;

							if (cls.equals("DictionaryEditorShell"))
							{
								String header = DictionaryEditorShellController.getHeader(text);
								start = header.length();
							}

							cptRules++;
							start = text.indexOf(pattern, start);

							if (start < 0)
								continue;

							int currentPosition = 0;
							StringBuilder tmp = new StringBuilder();

							while (start >= 0)
							{
								tmp.append(text.substring(currentPosition, start));
								tmp.append(replacementText);
								currentPosition = start + pattern.length();

								start = text.indexOf(pattern, currentPosition);
								cpt++;
							}

							tmp.append(text.substring(currentPosition));
							text = tmp.toString();
						}

						br.close();
						this.rtb.setText(text);

						if (textShell != null)
							textShell.getTextPane().addCaretListener(textShell.getUnitSelectionListener());

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), cptRules + " rules; " + cpt
								+ " replacement" + (cpt > 1 ? "s" : ""), Constants.NOOJ_RESOURCES_DIRECTORY,
								JOptionPane.INFORMATION_MESSAGE);
					}

					catch (IOException e)
					{
						try
						{
							if (br != null)
								br.close();
						}
						catch (IOException e1)
						{
							// Catch block does not do anything - message below should be written in each case.
						}

						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				finally
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				}
			}
		}
	}

	/**
	 * Function is replacing string in text from certain index.
	 * 
	 * @param text
	 *            - text for pattern search
	 * @param startingIndex
	 *            - index from which the search will start
	 * @param pattern
	 *            - searching pattern
	 * @param replacement
	 *            - replace pattern
	 * @return - text with replaced string pattern
	 * @throws BadLocationException
	 *             - if index is greater than text length
	 */

	private String replaceFirstFrom(String text, int startingIndex, String pattern, String replacement)
			throws BadLocationException
	{
		if (startingIndex > text.length())
			throw new BadLocationException("Invalid index!", text.length());
		String prefix = text.substring(0, startingIndex);
		String rest = text.substring(startingIndex);
		rest = rest.replaceFirst(pattern, replacement);
		return prefix + rest;
	}

	public void setBuffer(String buffer)
	{
		this.buffer = buffer;
	}
}