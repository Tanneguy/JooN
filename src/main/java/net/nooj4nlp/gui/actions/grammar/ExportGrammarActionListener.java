package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;

public class ExportGrammarActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public ExportGrammarActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser saveGraphChooser = new JFileChooser();
		saveGraphChooser.setDialogTitle("Save as");
		saveGraphChooser.setAcceptAllFileFilterUsed(false);

		String currentDirToBeSetPath = FilenameUtils.concat(Paths.docDir,
				org.apache.commons.io.FilenameUtils.concat(controller.lan.isoName, "Syntactic Analysis"));
		saveGraphChooser.setCurrentDirectory(new File(currentDirToBeSetPath));

		FileNameExtensionFilter filterGrammar = new FileNameExtensionFilter("Graph (*.grf)", "grf");
		saveGraphChooser.addChoosableFileFilter(filterGrammar);
		saveGraphChooser.setFileFilter(filterGrammar);

		if (saveGraphChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
			return;

		File selectedFile = saveGraphChooser.getSelectedFile();
		String fileName = FilenameUtils.removeExtension(selectedFile.getName()) + "." + Constants.GRF_EXTENSION;
		String parentPath = selectedFile.getParent();
		String filePath = parentPath + System.getProperty("file.separator") + fileName;
		selectedFile = new File(filePath);

		if (selectedFile.exists())
		{
			int value = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), fileName + " already exists."
					+ " \n Do you want to replace it?", Constants.CONFIRM_SAVE_AS, JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, null, null);
			if (value == JOptionPane.NO_OPTION || value == JOptionPane.CLOSED_OPTION)
				return;

			selectedFile.delete();
		}

		controller.SaveGraph(filePath);
	}
}