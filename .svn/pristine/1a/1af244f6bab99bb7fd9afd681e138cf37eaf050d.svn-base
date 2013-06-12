package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;

import net.nooj4nlp.gui.shells.ConsoleShell;

/**
 * 
 * ActionListener that opens the Console shell
 *
 */
public class ConsoleActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	
	public ConsoleActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	    ConsoleShell console = new ConsoleShell();
	    desktopPane.add(console);
	    console.setVisible(true);
	}
}