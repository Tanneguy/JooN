package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class TimerActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private int timerSelCount;

	public TimerActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		timerSelCount = 0;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (editor.getController().grf == null)
			return;

		if (timerSelCount > 9)
			timerSelCount = 0;
		else
			timerSelCount++;

		if (timerSelCount >= 8)
			editor.getController().grf.tColor = new Color(255, 215, 0); // Color.Gold
		else if (timerSelCount < 2)
			editor.getController().grf.tColor = editor.getController().grammar.bColor;
		else
			editor.getController().grf.tColor = editor.getController().grammar.sColor;

		editor.pGraph.invalidate();
		editor.pGraph.repaint();
	}
}
