package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.main.Launcher;

public class SaveAsDefaultActionListener implements ActionListener
{

	private CopyToPreferences copyToPreferences;

	public SaveAsDefaultActionListener(CopyToPreferences copyToPreferences)
	{
		this.copyToPreferences = copyToPreferences;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		copyToPreferences.CopyToPref();
		if (!Launcher.projectMode)
		{
			try
			{
				Launcher.preferences.Save(Paths.docDir + System.getProperty("file.separator") + "Preferences."
						+ Constants.JNOJ_EXTENSION);
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}

			int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), Constants.NOOJ_UPDATE_APP_DEFAULT,
					Constants.SAVE_FOR_NOOJ, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
				return;

			try
			{
				Launcher.preferences.Save(Paths.applicationDir + System.getProperty("file.separator") + "Preferences."
						+ Constants.JNOJ_EXTENSION);
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}