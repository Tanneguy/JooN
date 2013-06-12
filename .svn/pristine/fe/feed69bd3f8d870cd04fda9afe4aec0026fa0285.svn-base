package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;

/**
 * Class implements Action Listener for calling recursive delete of the active grammar graph and all of its children.
 */

public class DeleteGraphAndChildrenActionListener implements ActionListener
{
	private GrammarEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of the opened grammar
	 */
	public DeleteGraphAndChildrenActionListener(GrammarEditorShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		controller.deleteGraphAndItsChildren();
	}
}