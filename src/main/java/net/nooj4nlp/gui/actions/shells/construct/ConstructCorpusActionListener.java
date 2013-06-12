package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.gui.dialogs.ConstructCorpusDialog;
import net.nooj4nlp.gui.shells.LabInstructionsShell;

/**
 * 
 * ActionListener that opens the corpus construction lab dialog and instructions
 *
 */
public class ConstructCorpusActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	private LabInstructionsShell lab;
	
	public ConstructCorpusActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {		
	   ConstructCorpusDialog corpusDialog = new ConstructCorpusDialog();
	   lab = new LabInstructionsShell("Corpus Construction Lab");
	   desktopPane.add(corpusDialog);
	   desktopPane.add(lab);
	   lab.setVisible(true);
	   corpusDialog.setVisible(true);
	   
	   // When closing the dialog, close the lab as well
	   corpusDialog.addInternalFrameListener(new InternalFrameListener(){

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
