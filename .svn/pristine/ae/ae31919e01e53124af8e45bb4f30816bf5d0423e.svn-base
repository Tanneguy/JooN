package net.nooj4nlp.controller.packageconfigurationdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.nooj4nlp.gui.main.Launcher;

public class RefreshActionListener implements ActionListener
{
	private PackageConfigurationDialogController controller;

	public RefreshActionListener(PackageConfigurationDialogController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String res;
		try
		{
			res = controller.copyNooJStatusToProject();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Error while update project");
			return;
		}

		if (res != null)
			controller.modify();
	}

}