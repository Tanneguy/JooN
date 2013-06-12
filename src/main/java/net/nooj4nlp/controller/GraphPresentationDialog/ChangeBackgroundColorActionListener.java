package net.nooj4nlp.controller.GraphPresentationDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import net.nooj4nlp.gui.components.UsualColoredButtonUI;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements action of changing JButton's UI background color (of Graph Presentation Dialog).
 * 
 */

public class ChangeBackgroundColorActionListener implements ActionListener
{

	/**
	 * Simple constructor.
	 */

	public ChangeBackgroundColorActionListener()
	{
		super();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Color selectedColor = JColorChooser.showDialog(Launcher.getDesktopPane(), "Color", Color.WHITE);

		// if the color was selected...
		if (selectedColor != null)
		{
			// ...get adequate button, set the color to its customized UI view, and repaint the button
			JButton button = (JButton) e.getSource();
			((UsualColoredButtonUI) button.getUI()).setBackgroundColor(selectedColor);
			button.repaint();
		}
	}
}