package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.gui.main.Launcher;

public class DefaultActionListener implements ActionListener
{

	private UpdateDialogListener updateDialogListener;

	public DefaultActionListener(UpdateDialogListener updateDialogListener)
	{
		this.updateDialogListener = updateDialogListener;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Launcher.preferences = Preferences.Load(Paths.docDir + System.getProperty("file.separator") + "Preferences."
				+ Constants.JNOJ_EXTENSION);
		updateDialogListener.updateFromFormMainPreferences();
	}
}