package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

public class SaveActionListener implements ActionListener
{

	private PreferencesDialog dialog;
	private CopyToPreferences copyToPreferences;

	public SaveActionListener(PreferencesDialog dialog, CopyToPreferences copyToPreferences)
	{
		this.copyToPreferences = copyToPreferences;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		copyToPreferences.CopyToPref();
		File file = saveFileDialog();
		
		if (file != null)
		{
			try
			{
				Launcher.preferences.Save(file.getAbsolutePath());
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private File saveFileDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Preferences");
		FileNameExtensionFilter filterImportLex = new FileNameExtensionFilter("Preferences|*."
				+ Constants.JNOJ_EXTENSION, Constants.JNOJ_EXTENSION);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(filterImportLex);
		fileChooser.setFileFilter(filterImportLex);
		fileChooser.showSaveDialog(dialog);

		return fileChooser.getSelectedFile();
	}

}