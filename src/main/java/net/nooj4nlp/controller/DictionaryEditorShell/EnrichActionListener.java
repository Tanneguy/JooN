package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.nooj4nlp.gui.main.Launcher;

public class EnrichActionListener implements ActionListener
{


	public EnrichActionListener()
	{
		super();
		
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "HAS TO BE IMPLEMENTED");
	}
}