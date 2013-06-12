package net.nooj4nlp.gui.actions.shells.modify;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.TuGraph;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.components.CustomJSpinner;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * 
 * CaretListener used to select individual text units (TUs) based on care position.
 * 
 */
public class UnitSelectionListener implements CaretListener
{

	private JTextPane textPane;
	private StyledDocument doc;
	private int paragraphStart;
	private String text = "";
	private String delimiter;
	private CustomJSpinner spinner;

	private TextEditorShellController controller;

	private TextEditorShell textShell;
	private int currentTUnb;
	private Ntext myText;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - text controller
	 * @param t
	 *            - text pane of a Text Editor Shell
	 */

	public UnitSelectionListener(TextEditorShellController controller, JTextPane t)
	{
		this.controller = controller;
		TextEditorShell textShell = controller.getTextShell();

		delimiter = textShell.getText().getDelimPattern();
		this.textShell = controller.getTextShell();

		textPane = t;
		textPane.setForeground(Color.gray);
		spinner = textShell.getSpinner();
		doc = textPane.getStyledDocument();
		myText = controller.getMyText();

		Style active = textPane.addStyle("Active", null);
		StyleConstants.setForeground(active, Color.black);

		Style inactive = textPane.addStyle("Inactive", null);
		StyleConstants.setForeground(inactive, Color.gray);

		Style redStyle = textPane.addStyle("Red", null);
		StyleConstants.setForeground(redStyle, Color.RED);

		Style blueStyle = textPane.addStyle("Blue", null);
		StyleConstants.setForeground(blueStyle, Color.BLUE);

		Style greenStyle = textPane.addStyle("Green", null);
		StyleConstants.setForeground(greenStyle, Color.GREEN);

		Style blackStyle = textPane.addStyle("Black", null);
		StyleConstants.setForeground(blackStyle, Color.BLACK);
		StyleConstants.setUnderline(blackStyle, true);

		try
		{
			// The newline character is represented differently within a Document ('\n') and
			// the String retrieved directly from the JTextPane (i.e. text.getText() retrieves
			// a delimiter that's platform dependent). Retrieving the string directly could result
			// in an index that's off by 1 for each new paragraph (in Windows ('\r\n')).
			text = textPane.getDocument().getText(0, textPane.getDocument().getLength());
		}
		catch (BadLocationException e1)
		{
			e1.printStackTrace();
		}
		paragraphStart = 0;

		// Added this check because of static variables removal
		if (!text.equals(""))
		{
			int firstParagraphEnd = text.indexOf(delimiter);
			String currentParagraph;
			// If delimiter was found.
			if (firstParagraphEnd != -1)
				currentParagraph = text.substring(paragraphStart, firstParagraphEnd);
			else
				currentParagraph = text;
			// Clear old style first
			doc.setCharacterAttributes(0, text.length(), textPane.getStyle("Inactive"), true);

			// Set new style (highlight new paragraph)
			doc.setCharacterAttributes(paragraphStart, currentParagraph.length(), textPane.getStyle("Active"), true);
		}
		else
		{
			String currentParagraph = "";
			// Clear old style first
			doc.setCharacterAttributes(0, text.length(), textPane.getStyle("Inactive"), true);

			// Set new style (highlight new paragraph)
			doc.setCharacterAttributes(paragraphStart, currentParagraph.length(), textPane.getStyle("Active"), true);
		}

		doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		currentTUnb = 1;
	}

	@Override
	public void caretUpdate(CaretEvent e)
	{
		int rtbPosition = e.getDot();
		int adjuster = 0;
		for (int i = 0; i < rtbPosition; i++)
		{
			if (text.charAt(i) == '\r')
				adjuster++;
		}
		rtbPosition += adjuster;

		int txtPosition = rtbPosition;
		int newTUnb = locateTextUnit(txtPosition);

		if (newTUnb != currentTUnb)
		{
			currentTUnb = newTUnb;
			spinner.setValue(currentTUnb);
			controller.rtbTextUpdate(false);
			textPane.getCaret().setVisible(true);
		}

		// Set up tuGraph
		if (textShell.getChckbxShowTextAnnotation().isSelected())
		{
			controller.setTuGraph(currentTUnb);
			controller.getTuGraph().setNeedToBeComputed(true);
			controller.showAndScrollTas(rtbPosition);
		}

		textShell.invalidate();
		textShell.validate();
		textShell.repaint();
	}

	/**
	 * Function paints concordance sequences in text with adequate colors.
	 */
	public void paintTextInRGB()
	{
		List<Color> listOfColors = null;
		List<Integer> absoluteBeginAddresses = null;
		List<Integer> absoluteEndAddresses = null;
		List<String> listOfFiles = null;
		CorpusEditorShellController corpusController = controller.getCorpusController();

		// get data (color, beginning index, ending index) from controller
		if (corpusController != null && corpusController.getShell() != null)
		{
			listOfColors = corpusController.getListOfColors();
			absoluteBeginAddresses = corpusController.getAbsoluteBeginAddresses();
			absoluteEndAddresses = corpusController.getAbsoluteEndAddresses();
			listOfFiles = corpusController.getListOfConcordanceFiles();
		}
		else
		{
			listOfColors = controller.getListOfColors();
			absoluteBeginAddresses = controller.getAbsoluteBeginAddresses();
			absoluteEndAddresses = controller.getAbsoluteEndAddresses();
		}

		// if there is data...
		if (absoluteBeginAddresses != null)
		{
			for (int i = 0; i < absoluteBeginAddresses.size(); i++)
			{
				String fileName = "";
				if (listOfFiles != null)
					fileName = listOfFiles.get(i);

				if (!fileName.equals("") && !fileName.equals(controller.getFileToBeOpenedOrImported().getName()))
					continue;

				Color color = listOfColors.get(i);
				String styleString = "";

				// get the color, and if it's black, do nothing
				if (color.equals(Constants.NOOJ_RED_BUTTON_COLOR))
					styleString = "Red";
				else if (color.equals(Constants.NOOJ_BLUE_BUTTON_COLOR))
					styleString = "Blue";
				else if (color.equals(Constants.NOOJ_GREEN_BUTTON_COLOR))
					styleString = "Green";
				else
					styleString = "Black";

				// paint sequence's style over current style
				int begin = absoluteBeginAddresses.get(i);
				doc.setCharacterAttributes(begin, absoluteEndAddresses.get(i) - begin, textPane.getStyle(styleString),
						false);
			}
		}
	}

	/**
	 * Function locates real text unit with address of text's index inside it.
	 * 
	 * @param address
	 *            - given index in text
	 * @return - active text unit
	 */
	private int locateTextUnit(int address)
	{
		// address in text buffer
		int tuNb;
		for (tuNb = 1; tuNb <= myText.nbOfTextUnits
				&& address > myText.mft.tuAddresses[tuNb] + myText.mft.tuLengths[tuNb]; tuNb++)
			;

		if (tuNb <= myText.nbOfTextUnits)
			return tuNb < 1 ? 1 : tuNb;
		else
			return myText.nbOfTextUnits < 1 ? 1 : myText.nbOfTextUnits;
	}

	/**
	 * Function corrects position in text (\r, \n symbols).
	 * 
	 * @param myText
	 *            - given text
	 * @param textPosition
	 *            - position to fix
	 * @param textLength
	 *            - length of text unit
	 * @return - fixed length
	 */

	private int correctPositionText2RtbText(Ntext myText, int textPosition, int textLength)
	{
		// length in the text buffer returns length in text pane
		int nbN = 0;

		for (int i = textPosition; i < textPosition + textLength; i++)
			if (myText.buffer.charAt(i) == '\n')
				nbN++;

		// returns position in the rtbText
		return textLength + nbN;
	}

	/**
	 * Exchange color determination flags.
	 * 
	 * @param currentTextUnitIsBlack
	 *            - flag for determination of current unit color
	 */
	public void partialColorText(TextEditorShellController controller, Ntext myText, JTextPane textPane,
			TextEditorShell textShell, boolean currentTextUnitIsBlack)
	{
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			controller.setCurrentTextUnitIsBlack(currentTextUnitIsBlack);
			boolean textWasBeingColored = controller.isTextIsBeingColored();
			controller.setTextIsBeingColored(true);
			StyledDocument doc = textPane.getStyledDocument();

			if (currentTextUnitIsBlack)
			{
				// color all text'units in gray
				doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);

				// color current text unit in black
				int currentTU = (Integer) textShell.getSpinner().getValue();
				int currentAddress = myText.mft.tuAddresses[currentTU];
				int correctCurrentAddress = correctPositionText2RtbText(myText, 0, currentAddress);

				int adjuster = 0;
				for (int i = 0; i < currentAddress; i++)
				{
					if (myText.buffer.charAt(i) == '\n')
						adjuster++;
				}
				correctCurrentAddress -= adjuster;

				int currentLength = myText.mft.tuLengths[currentTU];

				doc.setCharacterAttributes(correctCurrentAddress, currentLength, textPane.getStyle("Active"), true);

			}
			else
				doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);

			paintTextInRGB();
			controller.setTextIsBeingColored(textWasBeingColored);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	/**
	 * Function implements key listener for both CustomJSpinner and JTextPane.
	 * 
	 * @param controller
	 *            - text controller
	 * @param e
	 *            - key listener
	 */
	public void keyEventFunction(TextEditorShellController controller, KeyEvent e)
	{
		JTextPane textPane = controller.getTextShell().getTextPane();

		if (textPane.isEditable())
			return;

		// delete selected annotation
		if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			controller.removeAnnotation();
			return;
		}
		else if (e.getKeyCode() == KeyEvent.VK_INSERT)
		{
			controller.addAnnotation();
			return;
		}

		// navigate text units
		if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
				&& e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT)
			return;

		TuGraph tuGraph = textShell.getTextController().getTuGraph();

		int rtbPosition = textPane.getCaretPosition();
		double selectAllAnnotationsAtPosition = 0.0;

		// scroll TAS
		if (textShell.getChckbxShowTextAnnotation().isSelected())
		{
			String rtbText = textShell.getTextPane().getText();
			if (rtbPosition >= 0 && rtbPosition < rtbText.length())
			{
				if (Language.isLetter(rtbText.charAt(rtbPosition)))
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
					g2D.fillRoundRect(thicknessOfBorders, thicknessOfBorders,
							component.getWidth() - thicknessOfBorders, component.getHeight() - thicknessOfBorders,
							thicknessOfBorders, thicknessOfBorders);

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

			// Repainting components
			textShell.invalidate();
			textShell.validate();
			textShell.repaint();
		}
	}

	// getters and setters
	public int getParagraphStart()
	{
		return paragraphStart + 1;
	}

	public void setParagraphStart(int paragraphStart)
	{
		this.paragraphStart = paragraphStart;
	}

	public void setText(String text)
	{
		this.text = text;
	}
}