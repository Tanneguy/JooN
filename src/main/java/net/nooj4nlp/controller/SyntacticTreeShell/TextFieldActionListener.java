package net.nooj4nlp.controller.SyntacticTreeShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextFieldActionListener implements ActionListener {

	private SyntacticTreeShellController controller;
	
	public TextFieldActionListener(SyntacticTreeShellController controller) {
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int index = Integer.parseInt(controller.getShell().getTbUnitNumber().getText());
		index--;
		controller.setConcordanceIndex(index);
		controller.getShell().repaint();
	}

}
