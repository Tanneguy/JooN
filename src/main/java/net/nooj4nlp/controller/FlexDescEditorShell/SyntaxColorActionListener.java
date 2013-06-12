package net.nooj4nlp.controller.FlexDescEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SyntaxColorActionListener implements ActionListener
{

	private FlexDescEditorShellController controller;

	public SyntaxColorActionListener(FlexDescEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (controller.computeColoring())
			controller.totalColoring();
	}

}
