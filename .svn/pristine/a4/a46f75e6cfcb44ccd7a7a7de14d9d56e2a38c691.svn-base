package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class RefreshActionListener implements ActionListener
{
	private UpdateDialogListener updateDialogListener;
	private PreferencesDialog dialog;

	public RefreshActionListener(PreferencesDialog dialog, UpdateDialogListener updateDialogListener)
	{
		this.updateDialogListener = updateDialogListener;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		updateDialogListener.updateFromFormMainPreferences();
		dialog.getLblLexDoc().setText("Selected file");
		dialog.getLblSynDoc().setText("File location");
		dialog.getTxtFileInfoLex().setText("");
		dialog.getTxtFileInfoSyn().setText("");
	}
}