package net.nooj4nlp.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class Paths
{
	private static String userDir;
	public static String docDir;
	public static String applicationDir;
	public static String projectDir;

	public static void InitializePaths()
	{
		try
		{
			applicationDir = FilenameUtils.getPath(FilenameUtils.getFullPathNoEndSeparator(Launcher.class
					.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0)
				applicationDir = "/" + applicationDir;
		}
		catch (URISyntaxException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_URL_FORMATTING, JOptionPane.ERROR_MESSAGE);
		}

		userDir = System.getProperty("user.home");

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
		{
			if (System.getProperty("os.name").equals("Windows XP"))
			{
				userDir += "\\Application Data";
			}
			else
			{
				userDir += "\\AppData\\Roaming";
			}
		}
		else
		{
			userDir += "/.config";
		}

		JFileChooser fr = new javax.swing.JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
		{
			docDir = FilenameUtils.concat(fw.getDefaultDirectory().getAbsolutePath(), "ONooJ");
		}
		else
		{
			String rootHomeNooJDirectory = null;
			String noojPathFolder = FilenameUtils.concat(userDir, "ONooJ");
			File tmp = new File(noojPathFolder);
			if (!tmp.exists())
				tmp.mkdir();
			String noojPath = FilenameUtils.concat(noojPathFolder, "path.txt");
			File file = new File(noojPath);
			if (file.exists())
			{
				try
				{
					BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(noojPath)));
					rootHomeNooJDirectory = sr.readLine().trim();
					sr.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("Choose a folder which will contain the root of the home NooJ directory");
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					rootHomeNooJDirectory = chooser.getSelectedFile().getAbsolutePath();
					PrintWriter sw = null;
					try
					{
						sw = new PrintWriter(file);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					sw.write(rootHomeNooJDirectory + "\n");
					sw.close();
				}
				else
				{
					System.exit(1);
				}
			}
			docDir = FilenameUtils.concat(rootHomeNooJDirectory, "ONooJ");
		}
		File directory = new File(docDir);
		if (!directory.exists())
			directory.mkdir();

		Dic.LogFileName = FilenameUtils.concat(FilenameUtils.concat(userDir, "NooJ"), "log.txt");
		File file = new File(Dic.LogFileName);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
		Date today = new Date();
		Dic.writeLogInit("NooJ "
				+ Launcher.nooJVersion
				+ ", "
				+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, java.util.Locale.ENGLISH).format(
						today));
	}
}