package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

public class ApplyActionListener implements ActionListener
{

	private PreferencesDialog dialog;
	private CopyToPreferences copyToPreferences;

	public ApplyActionListener(PreferencesDialog dialog, CopyToPreferences copyToPreferences)
	{
		this.dialog = dialog;
		this.copyToPreferences = copyToPreferences;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// Update preference
		copyToPreferences.CopyToPref();

		if (!dialog.getOldLan().equals(Launcher.preferences.deflanguage))
			Launcher.setOpenDirectories();

		dialog.dispose();
	}
}