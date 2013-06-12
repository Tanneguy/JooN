package net.nooj4nlp.controller.preferencesdialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.components.CustomCell;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImportLexActionListener implements ActionListener
{

	private PreferencesDialog dialog = null;
	private UpdateDialogListener updateDialogListener;

	public ImportLexActionListener(PreferencesDialog dialog, UpdateDialogListener updateDialogListener)
	{
		this.dialog = dialog;
		this.updateDialogListener = updateDialogListener;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File sourceFile;

		if (e.getSource() == this.dialog.getBtnImportFileLex())
			sourceFile = openFileDialog(true);
		else
			sourceFile = openFileDialog(false);

		if (sourceFile == null || !sourceFile.exists())
			return;

		String extension = FilenameUtils.getExtension(sourceFile.getName());
		String pathToSave = Constants.LEXICAL_ANALYSIS_PATH;

		if (extension.equals(Constants.JNOG_EXTENSION))
			pathToSave = Constants.SYNTACTIC_ANALYSIS_PATH;

		String language = (String) dialog.getCbDefLanguage().getSelectedItem();
		String to = Paths.docDir + System.getProperty("file.separator") + language
				+ System.getProperty("file.separator") + pathToSave + System.getProperty("file.separator")
				+ sourceFile.getName();

		File targetFile = new File(to);

		if (targetFile.exists())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File + " + sourceFile.getAbsolutePath()
					+ " - exists. Delete it first!", Constants.NOOJ_INVALID_OPERATION, JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			FileUtils.copyFile(sourceFile, targetFile);
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
		}

		CustomCell cell = new CustomCell();
		cell.getLabel().setText(sourceFile.getName());
		cell.getLabel().setSize(cell.getLabel().getPreferredSize().width, cell.getLabel().getPreferredSize().height);
		cell.setForeground(Color.BLACK);

		String fileExtension = FilenameUtils.getExtension(sourceFile.getName());
		if (fileExtension.equals(Constants.JNOD_EXTENSION))
			((DefaultTableModel) dialog.getTableDictionary().getModel()).addRow(new Object[] { cell, "" });
		else if (fileExtension.equals(Constants.JNOM_EXTENSION))
			((DefaultTableModel) dialog.getTableMorphology().getModel()).addRow(new Object[] { cell, "" });
		else
			((DefaultListModel) dialog.getListSynResources().getModel()).addElement(sourceFile.getName());

		updateDialogListener.updateFromFormMainPreferences();
	}

	private File openFileDialog(boolean lexPageActive)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open a grammar");
		FileNameExtensionFilter filter;
		FileNameExtensionFilter additionalFilter = null;

		if (lexPageActive)
		{
			filter = new FileNameExtensionFilter("Dictionaries (*.jnod)", Constants.JNOD_EXTENSION, "jnod");
			additionalFilter = new FileNameExtensionFilter("Morphology (*.jnom)", Constants.JNOM_EXTENSION, "jnom");
		}
		else
			filter = new FileNameExtensionFilter("Syntactic grammar (*.nog)", Constants.JNOG_EXTENSION, "nog");

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(filter);
		if (lexPageActive)
			fileChooser.addChoosableFileFilter(additionalFilter);
		fileChooser.setFileFilter(filter);
		fileChooser.showOpenDialog(dialog);

		return fileChooser.getSelectedFile();
	}
}