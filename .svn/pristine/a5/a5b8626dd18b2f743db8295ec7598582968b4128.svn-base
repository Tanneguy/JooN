package net.nooj4nlp.controller.DebugShell;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Class implements action listener for key events of Debug's JTable.
 */

public class KeySelectionTableActionListener extends KeyAdapter
{
	private DebugShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of Debug Shell
	 */

	public KeySelectionTableActionListener(DebugShellController controller)
	{
		this.controller = controller;
	}

	public void keyReleased(KeyEvent e)
	{
		controller.tableSelectionChangedFunction();
	}
}