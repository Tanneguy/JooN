package net.nooj4nlp.controller.DictionaryPropDefDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.DictionaryPropDefDialog;

public class CancelActionListener implements ActionListener
{

	private DictionaryPropDefDialog dialog;

	public CancelActionListener(DictionaryPropDefDialog dialog)
	{
		super();
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		dialog.dispose();
	}

}
