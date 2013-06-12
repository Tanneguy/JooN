package net.nooj4nlp.gui.actions.documents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class implements calling adequate action event of Find/Replace dialog.
 * 
 */
public class FindReplaceActionListener implements ActionListener
{
	private FindReplaceEvents listener;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 *            - listener of Find Replace
	 */

	public FindReplaceActionListener(FindReplaceEvents listener)
	{
		super();
		this.listener = listener;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command.equals("Find"))
			listener.find();

		else if (command.equals("Next"))
			listener.next();

		else if (command.equals("Replace"))
			listener.replace();

		else if (command.equals("ReplaceAll"))
			listener.replaceAll();

		else if (command.equals("Extract"))
			listener.extract();

		else if (command.equals("Filter"))
			listener.filter();

		else if (command.equals("Count"))
			listener.count();

		else if (command.equals("ReplaceAllFrom"))
			listener.replaceAllFrom();
	}
}