package net.nooj4nlp.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Implementation of custom colored JButton UI, with 2 colors: for background and foreground, onClick sets darker or
 * brighter color.
 */
public class UsualColoredButtonUI extends BasicButtonUI
{
	// chosen colors
	private Color backgroundColor;
	private Color foregroundColor;

	/**
	 * Constructor.
	 * 
	 * @param backgroundColor
	 *            - color for background in regular state
	 * @param foregroundColor
	 *            - button text color
	 */

	public UsualColoredButtonUI(Color backgroundColor, Color foregroundColor)
	{
		super();
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Overridden paint function
	 */
	public void paint(Graphics g, JComponent component)
	{
		// 2D graphics for anti aliasing
		Graphics2D g2D = (Graphics2D) g;

		JButton myButton = (JButton) component;

		// set font and border
		Font buttonFont = new Font(myButton.getFont().getName(), Font.PLAIN, myButton.getFont().getSize());
		myButton.setFont(buttonFont);
		myButton.setBorder(new LineBorder(Color.BLACK, 1));

		// if chosen color is too dark, change the foreground color of button to white
		if (Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), null)[2] < 0.5)
			myButton.setForeground(Color.WHITE);
		else
			myButton.setForeground(foregroundColor);

		// anti alias
		g2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

		ButtonModel buttonModel = myButton.getModel();

		/*
		 * If a button is in the pressed state, change its background color to darker, by default. If the background
		 * color is too dark, set the brighter color for the background (onClick). If the background color is black, set
		 * it to dark gray in pressed state (brighter() does not work for black color!).
		 */
		if (buttonModel.isPressed() || buttonModel.isSelected())
		{
			Color changedColor;

			if (Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), null)[2] < 0.5)
			{
				if (backgroundColor.equals(Color.BLACK))
					changedColor = Color.DARK_GRAY;
				else
					changedColor = backgroundColor.brighter();

			}
			else
				changedColor = backgroundColor.darker();

			g2D.setColor(changedColor);
		}

		else
			g2D.setColor(backgroundColor);

		// set background color of a button
		g2D.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), 3, 3);

		super.paint(g2D, component);
	}

	// getters and setters
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
}