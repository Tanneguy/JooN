package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;

import net.nooj4nlp.controller.packageconfigurationdialog.PackageConfigurationDialogController;
import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * ActionListener that opens the Package Configuration dialog
 * 
 */
public class PackageConfigurationActionListener implements ActionListener
{

	private JDesktopPane desktopPane;
	private PackageConfigurationDialogController controller;

	public PackageConfigurationActionListener(JDesktopPane dp)
	{
		this.desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		

		Launcher.setSavedPreferences(Launcher.preferences);

		PackageConfigurationDialog packageConfig = new PackageConfigurationDialog();
		controller = packageConfig.getPackageConfigurationDialogController();

		String res = controller.copyNooJStatusToProject();
		if (res != null)
		{
			packageConfig = null;
			return;
		}
		else
		{
			desktopPane.add(packageConfig);
			packageConfig.setVisible(true);
		}

		Launcher.getMntmRunProject().setText("Close Project");
		Launcher.getStatusBar().getProjectLabel().setText("PROJECT MODE");
	}
}
