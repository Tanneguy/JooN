package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.gui.dialogs.TextEncodingDialog;
import net.nooj4nlp.gui.shells.LabInstructionsShell;

/**
 * 
 * ActionListener that opens the text encoding lab dialog and instructions
 *
 */
public class TextEncodingActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	private LabInstructionsShell lab;
	
	public TextEncodingActionListener(JDesktopPane dp) {
		desktopPane = dp;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {		
	   TextEncodingDialog encodingDialog = new TextEncodingDialog();
	   lab = new LabInstructionsShell("Text Encoding Lab");
	   desktopPane.add(encodingDialog);
	   desktopPane.add(lab);
	   lab.setVisible(true);
	   encodingDialog.setVisible(true);
	   
	   encodingDialog.addInternalFrameListener(new InternalFrameListener(){

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
