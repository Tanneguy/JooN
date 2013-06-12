package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.gui.dialogs.LanguageSpecificsDialog;
import net.nooj4nlp.gui.shells.LabInstructionsShell;

/**
 * 
 * ActionListener that opens the language specifics lab dialog and instructions
 *
 */
public class LanguageSpecificsActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	private LabInstructionsShell lab;
	
	public LanguageSpecificsActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	   LanguageSpecificsDialog langSpecDialog = new LanguageSpecificsDialog();
	   lab = new LabInstructionsShell("Language Specifics Lab");
	   desktopPane.add(langSpecDialog);
	   desktopPane.add(lab);
	   lab.setVisible(true);
	   langSpecDialog.setVisible(true);
	   
	   // When closing the dialog, close the lab as well
	   langSpecDialog.addInternalFrameListener(new InternalFrameListener(){

			@Override
			public void internalFrameActivated(InternalFrameEvent arg0) {}

			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
				lab.dispose();
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent arg0) {}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}

			@Override
			public void internalFrameIconified(InternalFrameEvent arg0) {}

			@Override
			public void internalFrameOpened(InternalFrameEvent arg0) {}
			   
	   });
	}
}
