package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.gui.dialogs.DictionaryDialog;
import net.nooj4nlp.gui.shells.LabInstructionsShell;

/**
 * 
 * ActionListener that opens the dictionary lab dialog and instructions
 *
 */
public class DictionaryActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	private LabInstructionsShell lab;
	
	public DictionaryActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	   DictionaryDialog dictDialog = new DictionaryDialog();
	   lab = new LabInstructionsShell("Dictionary Lab");
	   desktopPane.add(dictDialog);
	   desktopPane.add(lab);
	   lab.setVisible(true);
	   dictDialog.setVisible(true);
	   
	// When closing the dialog, close the lab as well
	   dictDialog.addInternalFrameListener(new InternalFrameListener(){

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
