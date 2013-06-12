package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicButtonUI;

import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.TuGraph;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class JMftPanelMouseAdapter extends MouseAdapter
{
	private TextEditorShellController textController;

	public JMftPanelMouseAdapter(TextEditorShellController textController)
	{
		this.textController = textController;
	}

	public void mouseClicked(MouseEvent e)
	{
		TuGraph tuGraph = textController.getTuGraph();

		if (tuGraph != null)
		{
			TextEditorShell textShell = textController.getTextShell();
			JMftPanel mftPanel = textShell.getHiddenPanel();

			Double selectAllAnnotationsAtPosition = textController.getSelectAllAnnotationsAtPosition();
			RefObject<Double> refSelectAllAnnotationsAtPosition = new RefObject<Double>(selectAllAnnotationsAtPosition);
			int x = e.getX();
			int y = e.getY();

			if (tuGraph.getSelectedAnnotation(x, y, mftPanel, refSelectAllAnnotationsAtPosition))
			{
				selectAllAnnotationsAtPosition = refSelectAllAnnotationsAtPosition.argvalue;
				tuGraph.computeXYcoord(mftPanel, selectAllAnnotationsAtPosition, y);

				// Adding the 'button'
				JButton test = new JButton(Integer.toString((selectAllAnnotationsAtPosition.intValue())));
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
						Font buttonFont = new Font(myButton.getFont().getName(), Font.BOLD, myButton.getFont()
								.getSize());
						myButton.setFont(buttonFont);

						myButton.setBorder(BorderFactory.createLineBorder(Color.RED, thicknessOfBorders));
						g2D.setColor(Color.WHITE);

						// set background color of a button
						g2D.fillRoundRect(thicknessOfBorders, thicknessOfBorders, component.getWidth()
								- thicknessOfBorders, component.getHeight() - thicknessOfBorders, thicknessOfBorders,
								thicknessOfBorders);

						super.paint(g2D, component);
					}
				});
				test.setMinimumSize(new Dimension(60, 20));

				textShell.getHiddenPanel().removeAll();
				textShell.getHiddenPanel().add(test);

				// Scroll mftpanel to selected annotations
				JScrollPane scroll = textShell.getPanelScrollPane();
				scroll.getVerticalScrollBar().setValue(0);
				scroll.getHorizontalScrollBar().setValue(tuGraph.xCoord);

				// Refreshing caret position
				textShell.getHiddenPanel().setSelectAllAnnotationsAtPosition(selectAllAnnotationsAtPosition);
				textShell.getTextPane().removeCaretListener(textShell.getUnitSelectionListener());

				int currentUnit = (Integer) textShell.getSpinner().getValue();
				Double newCaretPosition = selectAllAnnotationsAtPosition
						+ textController.getMyText().mft.tuAddresses[currentUnit];

				textShell.getTextPane().setCaretPosition(newCaretPosition.intValue());
				textShell.getTextPane().getCaret().setVisible(true);
				textShell.getTextPane().addCaretListener(textShell.getUnitSelectionListener());

				// Repainting components
				textShell.invalidate();
				textShell.validate();
				textShell.repaint();
			}
		}
	}
}
