package net.nooj4nlp.gui.actions.documents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.text.JTextComponent;

public class CopyActionListener implements ActionListener {

	private JTextComponent text;
	
	
	public CopyActionListener(JTextComponent tc)
	{
		text=tc;
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		text.copy();
		
	}

}