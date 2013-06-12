package net.nooj4nlp.gui.actions.shells.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * 
 * ActionListener that exits the application
 */
public class ExitActionListener implements ActionListener
{

	private JFrame mainFrame;

	public ExitActionListener(JFrame f)
	{
		mainFrame = f;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		mainFrame.dispose();
	}
}
