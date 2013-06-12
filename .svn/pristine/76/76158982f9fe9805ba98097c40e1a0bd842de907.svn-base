package net.nooj4nlp.controller.PropDefEditorShell;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextDocumentListener implements DocumentListener {

	private PropDefEditorShellController controller;
	
	public TextDocumentListener(PropDefEditorShellController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		warn();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		warn();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		warn();
	}
	
	private void warn() {
		controller.modify();
	}

}
