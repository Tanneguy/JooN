package net.nooj4nlp.controller.GramStructShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshActionListener implements ActionListener
{

	private GramStructShellController controller;

	public RefreshActionListener(GramStructShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// revisit the tree view
		controller.visit(controller.tvGraphs);
		controller.tvGraphs.update(controller.tvGraphs.getGraphics());

	
	}

}