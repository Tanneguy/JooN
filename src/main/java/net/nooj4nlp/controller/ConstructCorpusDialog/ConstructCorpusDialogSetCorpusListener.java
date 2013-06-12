package net.nooj4nlp.controller.ConstructCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class ConstructCorpusDialogSetCorpusListener implements ActionListener
{
	private JInternalFrame frame;
	private ConstructCorpusDialogController controller;

	public ConstructCorpusDialogSetCorpusListener(JInternalFrame frame, ConstructCorpusDialogController controller)
	{
		super();
		this.frame = frame;
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser jFileChooser = Launcher.getOpenCorpusChooser();

		// Sets the current directory
		File fileToBeSplit = new File(this.controller.getFldFileName().getText());
		if (fileToBeSplit.exists())
		{
			jFileChooser.setCurrentDirectory(fileToBeSplit);
		}
		else
		{
			// Default folder for JFileChooser is MyDocuments on Windows.
			File currentDir = jFileChooser.getCurrentDirectory();

			String pathToBeSet = currentDir.getAbsolutePath() + System.getProperty("file.separator")
					+ Constants.NOOJ_RESOURCES_DIRECTORY;

			File dirToBeSet = new File(pathToBeSet);
			jFileChooser.setCurrentDirectory(dirToBeSet);
		}

		int result = jFileChooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();
			String filePath = file.getPath();

			// If extension is not set, set to ".jnoc" by default
			String fileName = file.getName();
			if (!FilenameUtils.getExtension(fileName).equals(Constants.JNOC_EXTENSION))
				filePath += "." + Constants.JNOC_EXTENSION;

			this.controller.getFldCorpus().setText(filePath);
		}
	}
}
