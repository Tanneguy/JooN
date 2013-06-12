package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.gui.dialogs.PerlRegexDialog;
import net.nooj4nlp.gui.shells.LabInstructionsShell;

/**
 * 
 * ActionListener that opens the Perl lab dialog and instructions
 *
 */
public class PerlRegexActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	private LabInstructionsShell lab;
	
	public PerlRegexActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {		
	   PerlRegexDialog regexDialog = new PerlRegexDialog(desktopPane);
	   lab = new LabInstructionsShell("Perl Lab");
	   desktopPane.add(regexDialog);
	   desktopPane.add(lab);
	   lab.setVisible(true);
	   regexDialog.setVisible(true);
	   
	   regexDialog.addInternalFrameListener(new InternalFrameListener(){

			@Override
			public void internalFrameActivated(InternalFrameEvent arg0){}

			@Override
			public void internalFrameClosed(InternalFrameEvent arg0){
				lab.dispose();
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent arg0){}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent arg0){}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent arg0){}

			@Override
			public void internalFrameIconified(InternalFrameEvent arg0){}

			@Override
			public void internalFrameOpened(InternalFrameEvent arg0){}
			   
	   });
	}
}
