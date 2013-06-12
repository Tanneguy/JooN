package net.nooj4nlp.controller.ConstructCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;

import net.nooj4nlp.gui.main.Launcher;

public class ConstructCorpusDialogSetFolderListener implements ActionListener
{
	private JInternalFrame frame;
	private ConstructCorpusDialogController controller;

	public ConstructCorpusDialogSetFolderListener(JInternalFrame frame, ConstructCorpusDialogController controller)
	{
		super();
		this.frame = frame;
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser jFileChooser = Launcher.getOpenFolderChooser();

		int result = jFileChooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();
			this.controller.getFldFolder().setText(file.getPath());
		}
	}
}
