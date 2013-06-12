package net.nooj4nlp.controller.packageconfigurationdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class SaveProjectActionListener implements ActionListener
{
	private static PackageConfigurationDialogController controller;

	public SaveProjectActionListener(PackageConfigurationDialogController controller)
	{
		SaveProjectActionListener.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (controller.getPackageConfigDialog().getTextDocumentation().getText().equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROJECT_NO_DOCUMENTATION_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		saveAsProject();
	}

	public static void saveAsProject()
	{
		JFileChooser jFileChooser = Launcher.getSaveProjectChooser();

		// Sets the current directory
		File currentDir = new File(Launcher.preferences.openProjDir);
		jFileChooser.setCurrentDirectory(currentDir);

		int result = jFileChooser.showSaveDialog(controller.getPackageConfigDialog());
		if (result != JFileChooser.APPROVE_OPTION)
			return;

		String fullName = jFileChooser.getSelectedFile().getAbsolutePath();
		if (!FilenameUtils.getExtension(fullName).equals(Constants.JNOP_EXTENSION))
			fullName += "." + Constants.JNOP_EXTENSION;

		CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
		controller.setFullName(fullName);
		controller.save();
		CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
	}

	public static void saveProject()
	{
		String txt = controller.getFullName();
		if (txt == null)
			saveAsProject();
		else
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			controller.save();
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}
}