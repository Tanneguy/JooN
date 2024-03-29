package net.nooj4nlp.controller.packageconfigurationdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.main.Launcher;

public class UseProjectActionListener implements ActionListener
{
	private JDesktopPane desktopPane;

	public UseProjectActionListener(JDesktopPane dp)
	{
		this.desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (Launcher.getMntmRunProject().getText().equals("Close Project"))
		{
			PackageConfigurationDialogController.closeProject();

			Dic.writeLog("Close Project.");

			Launcher.preferences = Launcher.savedPreferences;
			Launcher.setOpenDirectories();

			return;
		}

		Launcher.savedPreferences = Launcher.preferences;

		if (Launcher.getMntmRunProject().getText().equals("Run Project"))
		{
			// Open a project file
			JFileChooser openProjectChooser = Launcher.getOpenProjectChooser();
			if (openProjectChooser.showOpenDialog(Launcher.getDesktopPane()) != JFileChooser.APPROVE_OPTION)
				return;

			String fullPath = openProjectChooser.getSelectedFile().getAbsolutePath();

			Launcher.projectMode = true; // this instruction must be BEFORE OpenProject in order to load the
											// project's documentation
			PackageConfigurationDialog packageConfigDialog = new PackageConfigurationDialog();
			PackageConfigurationDialogController controller = new PackageConfigurationDialogController(
					packageConfigDialog);
			controller.openProject(fullPath);

			if (controller != null && PackageConfigurationDialogController.getProject() != null)
			{
				Launcher.getStatusBar().getProjectLabel().setText("PROJECT MODE");
				Launcher.getMntmRunProject().setText("Close Project");
				Launcher.getMntmNewProject().setEnabled(false);

				PackageConfigurationDialogController.setProjectDir(fullPath + Constants.DIRECTORY_SUFFIX);
			}
			else
			{
				Launcher.getStatusBar().getProjectLabel().setText("");
				Launcher.projectMode = false;

				PackageConfigurationDialogController.setProjectDir(null);

				JOptionPane.showMessageDialog(desktopPane, Constants.PROJECT_CANNOT_RUN + fullPath,
						Constants.PROJECT_CORRUPTED_FILE, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
