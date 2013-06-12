package net.nooj4nlp.gui.actions.documents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.undo.UndoManager;

public class UndoActionListener implements ActionListener {

	private UndoManager manager;
	
	public UndoActionListener(UndoManager m) {
		manager = m;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		manager.undo();
	}

}
