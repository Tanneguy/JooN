package net.nooj4nlp.controller.DebugShell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class implements action listener for mouse events of Debug's JTable.
 */

public class MouseSelectionTableActionListener extends MouseAdapter
{
	private DebugShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of Debug Shell
	 */

	public MouseSelectionTableActionListener(DebugShellController controller)
	{
		this.controller = controller;
	}

	public void mouseClicked(MouseEvent e)
	{
		controller.tableSelectionChangedFunction();
	}
}