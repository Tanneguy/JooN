package net.nooj4nlp.controller.AlignmentDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Class implements custom JPanel for purpose of Alignment Dialog. Panel is regular JPanel with gray rectangle which is
 * drawn through the center of JPanel, horizontally or vertically.
 */

public class AlignmentJPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private boolean horizontalAlign;

	// configured constants
	private static final int THICKNESS = 10;
	private static final int INSET = 5;
	private static final Color RECTANGLE_COLOR = Color.DARK_GRAY;

	/**
	 * Constructor.
	 * 
	 * @param horizontalAlign
	 *            - flag to determine whether rectangle should be drawn vertically or horizontally
	 */

	public AlignmentJPanel(boolean horizontalAlign)
	{
		super();
		this.horizontalAlign = horizontalAlign;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		// get dimension of panel
		Dimension dim = getSize();

		// set color of rectangle
		g.setColor(RECTANGLE_COLOR);

		// draw the rectangle depending on flag
		if (horizontalAlign)
			g.fillRect(10 + INSET, 6 + (dim.height - THICKNESS) / 2, dim.width - INSET * 2 - 20, THICKNESS);
		else
			g.fillRect((dim.width - THICKNESS) / 2, 10 + INSET, THICKNESS, dim.height - INSET * 2 - 20);
	}
}