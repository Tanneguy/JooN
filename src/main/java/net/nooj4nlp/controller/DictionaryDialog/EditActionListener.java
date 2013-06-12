package net.nooj4nlp.controller.DictionaryDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.gui.components.CursorChangeEffect;

public class EditActionListener implements ActionListener
{

	private DictionaryDialogController controller;

	public EditActionListener(DictionaryDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			DictionaryEditorShellController.openNooJDictionary(controller.getTxtDictionaryName().getText());
		}
		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}
}
