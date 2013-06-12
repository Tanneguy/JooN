package net.nooj4nlp.controller.DictionaryDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dictionary;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.main.Launcher;

public class CompileActionListener implements ActionListener, PropertyChangeListener
{

	private DictionaryDialogController controller;

	public CompileActionListener(DictionaryDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			Launcher.getStatusBar().getProgressBar().setIndeterminate(false);
			Launcher.getStatusBar().getProgressBar().setValue(progress);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String fullName = controller.getTxtDictionaryName().getText();
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

		dictionaryCompile(fullName, resName, controller.getChckbxCheckAgreement().isSelected(), lan,
				Launcher.preferences);
	}

	private void dictionaryCompile(String fullName, String resName, boolean checkAgreement, Language lan,
			Preferences preferences)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		// desactivate all formDictionary operations
		controller.desactivateOps();
		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.progressMessage = "Compile dictionary...";
		Launcher.getStatusBar().getProgressLabel().setText("Compile dictionary...");

		if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;
			Launcher.checkAgreement = checkAgreement;

			Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.DIC_COMPILE, null, null, controller);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
		else
		{
			// mono-thread
			try
			{
				Dictionary.compile(fullName, resName, checkAgreement, lan);
				controller.reactivateOps();

				Date now = new Date();
				long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
				Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
				
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
