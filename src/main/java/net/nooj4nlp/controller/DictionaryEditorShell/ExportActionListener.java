package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.gui.main.Launcher;

public class ExportActionListener implements ActionListener
{

	private DictionaryEditorShellController controller;

	public ExportActionListener(DictionaryEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FileNameExtensionFilter filterCsv = new FileNameExtensionFilter("CSV file", "csv");
		JFileChooser saveFileDialog = new JFileChooser();
		saveFileDialog.setDialogTitle("Export Table");
		saveFileDialog.setAcceptAllFileFilterUsed(false);
		saveFileDialog.addChoosableFileFilter(filterCsv);
		saveFileDialog.setFileFilter(filterCsv);

		if (saveFileDialog.showSaveDialog(controller.getShell()) != JFileChooser.APPROVE_OPTION)
			return;

		File file = saveFileDialog.getSelectedFile();
		String fileStr = file.getAbsolutePath();
		if (!fileStr.endsWith(".csv"))
			fileStr += ".csv";
		if (!controller.export(fileStr))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Could not create file " + fileStr,
					"NooJ: file problem", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		else
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Successfully created File " + fileStr, "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
	}

}
