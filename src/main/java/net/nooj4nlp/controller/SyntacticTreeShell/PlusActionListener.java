package net.nooj4nlp.controller.SyntacticTreeShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlusActionListener implements ActionListener
{

	private SyntacticTreeShellController controller;

	public PlusActionListener(SyntacticTreeShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		controller.setConcordanceIndex(controller.getConcordanceIndex() + 1);
		controller.getShell().repaint();
	}

}
