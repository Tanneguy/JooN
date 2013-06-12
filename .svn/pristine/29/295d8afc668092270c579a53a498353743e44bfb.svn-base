package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

public class LoadActionListener implements ActionListener
{

	private PreferencesDialog dialog;
	private UpdateDialogListener updateDialogListener;

	public LoadActionListener(PreferencesDialog dialog, UpdateDialogListener updateDialogListener)
	{
		this.updateDialogListener = updateDialogListener;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File sourceFile = openFileDialog();
		if (sourceFile == null)
			return;

		Preferences preferences = Preferences.Load(sourceFile.getAbsolutePath()); // load pref; do not xcopy pref files
		if (preferences == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load preference file",
					"NooJ setup problem?", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Launcher.preferences = preferences;
		updateDialogListener.updateFromFormMainPreferences();
	}

	private File openFileDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open Preferences");
		FileNameExtensionFilter filterImportLex = new FileNameExtensionFilter("Preferences|*."
				+ Constants.JNOJ_EXTENSION, Constants.JNOJ_EXTENSION);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(filterImportLex);
		fileChooser.setFileFilter(filterImportLex);
		fileChooser.showOpenDialog(dialog);

		return fileChooser.getSelectedFile();
	}

}
