package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;

import net.nooj4nlp.gui.shells.AboutNooJShell;

public class AboutNooJ  implements ActionListener{
   private JDesktopPane desktopPane;
	
	public AboutNooJ (JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		AboutNooJShell AboutNooJ = new AboutNooJShell();
		    desktopPane.add(AboutNooJ);
		    AboutNooJ.setVisible(true);
		
	}

}
