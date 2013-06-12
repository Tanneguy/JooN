package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dictionary;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

public class CompileActionListener implements ActionListener
{

	private DictionaryEditorShellController controller;

	public CompileActionListener(DictionaryEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String fullName = controller.getFullName();
		if (fullName.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Filename should not be empty", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		File file = new File(fullName);
		if (!file.exists())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find file " + fullName,
					"NooJ: cannot find dictionary file", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String fName = org.apache.commons.io.FilenameUtils.removeExtension(org.apache.commons.io.FilenameUtils
				.getName(fullName));
		String dirName = org.apache.commons.io.FilenameUtils.getFullPath(fullName);
		String resName = dirName + fName + "." + Constants.JNOD_EXTENSION;

		String languagename;
		try
		{
			languagename = Dictionary.getLanguage(fullName);
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary has not a valid format.",
					"NooJ: cannot read dictionary language", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Language lan = new Language(languagename);

		try
		{
			Dictionary.compile(fullName, resName, true, lan);
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}
}