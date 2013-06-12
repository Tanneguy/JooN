package net.nooj4nlp.controller.SyntacticTreeShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MinusActionListener implements ActionListener
{

	private SyntacticTreeShellController controller;

	public MinusActionListener(SyntacticTreeShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		controller.setConcordanceIndex(controller.getConcordanceIndex() - 1);
		controller.getShell().repaint();
	}

}
