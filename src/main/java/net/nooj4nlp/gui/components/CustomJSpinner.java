package net.nooj4nlp.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.TuGraph;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Custom JSpinner class for Open Text Dialog. Previous and next values are reversed to obtain visual sense.
 * 
 */
public class CustomJSpinner extends JSpinner
{
	private static final long serialVersionUID = 5086608640336145412L;

	private TextEditorShell textShell;
	private int upperLimit;
	private StyledDocument doc;
	private JTextPane textPane;
	private TextEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param textShell
	 *            - shell where custom spinner is
	 * @param upperLimit
	 *            - number of text units = upper limit of JSpinner
	 */
	public CustomJSpinner(TextEditorShell textShell, int upperLimit)
	{
		this.textShell = textShell;
		this.controller = textShell.getTextController();
		this.upperLimit = upperLimit;
		this.textPane = this.textShell.getTextPane();
		// initialize editable number model
		this.setModel(new SpinnerNumberModel(1, 1, upperLimit, 1));
		((DefaultEditor) this.getEditor()).getTextField().setEditable(true);

		// get doc and delimiter
		doc = textPane.getStyledDocument();
		doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

		// styles to choose
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
	}

	/**
	 * Set custom value to CustomJSpinner.
	 * 
	 * @param value
	 *            -
	 */
	public void setCustomValue(int value)
	{
		this.setValue(value);
		controller.rtbTextUpdate(false);
		int currentAddress = controller.getMyText().mft.tuAddresses[(Integer) this.getValue()];

		UnitSelectionListener unitSelectionListener = textShell.getUnitSelectionListener();
		textPane.removeCaretListener(unitSelectionListener);
		textPane.setCaretPosition(currentAddress);
		textPane.getCaret().setVisible(true);
		textPane.addCaretListener(unitSelectionListener);

		TuGraph tuGraph = controller.getTuGraph();

		if (tuGraph == null)
			return;

		tuGraph.needToBeComputed = true;

		// scroll TAS
		scrollTAS(tuGraph);
	}

	/**
	 * Overridden function for finding previous paragraph with CustomJSpinner.
	 */
	public Object getPreviousValue()
	{
		// if current value is at maximum, just exit...
		if ((Integer) this.getValue() == upperLimit)
			return null;

		// ...otherwise, increase counter and get text
		this.setValue((Integer) this.getValue() + 1);

		setCustomValue((Integer) this.getValue());

		TuGraph tuGraph = controller.getTuGraph();

		if (tuGraph == null)
			return null;

		tuGraph.needToBeComputed = true;

		// scroll TAS
		scrollTAS(tuGraph);

		return null;
	}

	/**
	 * Overridden function for finding next paragraph with CustomJSpinner.
	 */
	@Override
	public Object getNextValue()
	{
		// if current value is at minimum, just exit...
		if ((Integer) this.getValue() == 1)
			return null;

		// ...otherwise, decrease counter and get text
		this.setValue((Integer) this.getValue() - 1);

		setCustomValue((Integer) this.getValue());

		TuGraph tuGraph = controller.getTuGraph();

		if (tuGraph == null)
			return null;

		tuGraph.needToBeComputed = true;

		// scroll TAS
		scrollTAS(tuGraph);

		return null;
	}

	private void scrollTAS(TuGraph tuGraph)
	{
		if (textShell.getChckbxShowTextAnnotation().isSelected())
		{
			

			double selectAllAnnotationsAtPosition = 0.0;
			tuGraph.currentFrameX = 0;
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
	public int getUpperLimit()
	{
		return upperLimit;
	}
}