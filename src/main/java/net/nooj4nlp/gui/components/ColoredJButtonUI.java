package net.nooj4nlp.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Implementation of custom colored JButton UI, with 2 colors for background, depending of a state, shiny line and 3D
 * shadow effect.
 * 
 */
public class ColoredJButtonUI extends BasicButtonUI
{
	// chosen colors
	private Color backgroundColor;
	private Color pressedStateColor;

	/**
	 * Constructor.
	 * 
	 * @param backgroundColor
	 *            - color for background in regular state
	 * @param pressedStateColor
	 *            - color for background in pressed/selected state
	 */

	public ColoredJButtonUI(Color backgroundColor, Color pressedStateColor)
	{
		super();
		this.backgroundColor = backgroundColor;
		this.pressedStateColor = pressedStateColor;
	}

	/**
	 * Overridden paint function
	 */
	public void paint(Graphics g, JComponent component)
	{
		// 2D graphics for anti aliasing
		Graphics2D g2D = (Graphics2D) g;

		JButton myButton = (JButton) component;

		// set custom border of a component
		myButton.setBorder(new ThreeDimensionalBorder(Color.BLACK, 200, 2));

		// set font (bold)
		Font buttonFont = new Font(myButton.getFont().getName(), Font.BOLD, myButton.getFont().getSize());
		myButton.setFont(buttonFont);

		// anti alias
		g2D.setRenderingHints(ThreeDimensionalBorder.getHints());

		// define current colors
		ButtonModel buttonModel = myButton.getModel();
		if (buttonModel.isPressed() || buttonModel.isSelected())
			g2D.setColor(pressedStateColor);
		else
			g2D.setColor(backgroundColor);

		// get fixed data from custom border class
		int strokePad = ThreeDimensionalBorder.getStrokePad();
		int thickness = ThreeDimensionalBorder.getThickness();
		int shadowPad = ThreeDimensionalBorder.getShadowPad();

		// set background color of a button
		g2D.fillRoundRect(0 + strokePad, 0 + strokePad, component.getWidth() - thickness - shadowPad - 1,
				component.getHeight() - thickness - shadowPad - 1, 5, 5);

		super.paint(g2D, component);
	}

	// getters and setters
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

}

/**
 * Class for implementation three dimensional border style for custom buttons.
 */
class ThreeDimensionalBorder extends AbstractBorder
{
	// variables
	private static final long serialVersionUID = 1L;
	private Color color;
	private static int thickness = 2;
	private int param = 8;
	private Insets insets = null;
	private BasicStroke stroke = null;
	private static int strokePad;
	private static RenderingHints hints;
	private static int shadowPad = 2;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            - desirable border color
	 * @param transparency
	 *            - transparency parameter
	 * @param shadowWidth
	 *            - width of shadows
	 */
	ThreeDimensionalBorder(Color color, int transparency, int shadowWidth)
	{
		this.color = color;
		shadowPad = shadowWidth;

		// initialize stroke and stroke Pad
		stroke = new BasicStroke(thickness);
		strokePad = thickness / 2;

		// set hints (anti aliasing)
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// create new insets for a border
		int pad = param + strokePad;
		int bottomPad = pad + strokePad + shadowPad;
		int rightPad = pad + strokePad + shadowPad;
		insets = new Insets(pad, pad, bottomPad + shadowPad, rightPad);
	}

	/**
	 * Overridden paint function. 3D effect is achieved with drawing same sized, translated, gray colored area over the
	 * button.
	 * 
	 * @param c
	 *            - component which will be painted
	 * @param g
	 *            - current graphics
	 * @param x
	 *            - width (not using)
	 * @param y
	 *            - height (not using)
	 * @param width
	 *            - width of component
	 * @param height
	 *            - height of component
	 * 
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		// 2D graphics for anti aliasing and 2D objects
		Graphics2D g2 = (Graphics2D) g;

		// calculate value of bottom line height
		int bottomLine = height - thickness - shadowPad;

		// rectangle of a button, and button area
		RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(0 + strokePad, 0 + strokePad, width - thickness
				- shadowPad - 1, bottomLine - 1, param, param);
		Area area = new Area(bubble);

		// set options and draw the button
		g2.setRenderingHints(hints);
		g2.setColor(color);
		g2.setStroke(stroke);
		g2.draw(area);

		// desirable gray color with alpha channel
		Color shadow = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);

		// set options and draw gray pattern
		g2.setRenderingHints(hints);
		g2.setColor(shadow);
		g2.fillRoundRect(3 + strokePad, 3 + strokePad, width - thickness - shadowPad, bottomLine, param, param);
	}

	// getters and setters
	@Override
	public Insets getBorderInsets(Component c)
	{
		return insets;
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		return getBorderInsets(c);
	}

	public static int getThickness()
	{
		return thickness;
	}

	public static int getStrokePad()
	{
		return strokePad;
	}

	public static int getShadowPad()
	{
		return shadowPad;
	}

	public static RenderingHints getHints()
	{
		return hints;
	}
}
