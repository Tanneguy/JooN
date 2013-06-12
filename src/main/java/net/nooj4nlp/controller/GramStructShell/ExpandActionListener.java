package net.nooj4nlp.controller.GramStructShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExpandActionListener implements ActionListener
{

	private GramStructShellController controller;

	public ExpandActionListener(GramStructShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		controller.expandAll(true);
	}

}
