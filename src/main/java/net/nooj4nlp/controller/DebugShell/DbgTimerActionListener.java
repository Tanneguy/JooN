package net.nooj4nlp.controller.DebugShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Graph;

/**
 * Action listener implements action of Grammar's dbg timer.
 */

public class DbgTimerActionListener implements ActionListener
{
	private int timerDbgCount;

	private GrammarEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of recently opened grammar
	 */

	public DbgTimerActionListener(GrammarEditorShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Graph grf = controller.grf;

		if (grf == null)
			return;

		if (timerDbgCount > 9)
			timerDbgCount = 0;
		else
			timerDbgCount++;

		if (timerDbgCount >= 3)
			grf.debugInvisible = false;
		else
			grf.debugInvisible = true;

		controller.editor.invalidate();
		controller.editor.validate();
		controller.editor.repaint();
	}
}