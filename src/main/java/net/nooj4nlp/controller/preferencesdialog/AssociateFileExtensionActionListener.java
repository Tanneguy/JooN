package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.main.Launcher;

public class AssociateFileExtensionActionListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String osName = System.getProperty("os.name");
		if (osName.equalsIgnoreCase("Windows 7"))
		{
			Runtime rt = Runtime.getRuntime();
			String executionCmd = "javaw -jar " + Paths.applicationDir.substring(1);
			try
			{
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\.jnoc\" /ve /t REG_SZ /d \"noc\" /f");
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\jnoc\\shell\\open\\command\" /ve /t REG_SZ /d " + "\""
						+ executionCmd + " %l\" " + "/f");

				rt.exec("reg add \"HKEY_CLASSES_ROOT\\.jnof\" /ve /t REG_SZ /d \"noc\" /f");
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\jnof\\shell\\open\\command\" /ve /t REG_SZ /d " + "\""
						+ executionCmd + " %l\" " + "/f");

				rt.exec("reg add \"HKEY_CLASSES_ROOT\\.nog\" /ve /t REG_SZ /d \"noc\" /f");
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\nog\\shell\\open\\command\" /ve /t REG_SZ /d " + "\""
						+ executionCmd + " %l\" " + "/f");

				rt.exec("reg add \"HKEY_CLASSES_ROOT\\.jnom\" /ve /t REG_SZ /d \"noc\" /f");
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\jnom\\shell\\open\\command\" /ve /t REG_SZ /d " + "\""
						+ executionCmd + " %l\" " + "/f");

				rt.exec("reg add \"HKEY_CLASSES_ROOT\\.jnot\" /ve /t REG_SZ /d \"noc\" /f");
				rt.exec("reg add \"HKEY_CLASSES_ROOT\\jnot\\shell\\open\\command\" /ve /t REG_SZ /d " + "\""
						+ executionCmd + " %l\" " + "/f");

			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}