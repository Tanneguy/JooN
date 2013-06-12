package net.nooj4nlp.controller.ConstructCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;

import net.nooj4nlp.gui.main.Launcher;

public class ConstructCorpusDialogSetSourceFileNameListener implements ActionListener
{
	private JInternalFrame frame;
	private ConstructCorpusDialogController controller;

	public ConstructCorpusDialogSetSourceFileNameListener(JInternalFrame frame,
			ConstructCorpusDialogController controller)
	{
		super();
		this.frame = frame;
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser jFileChooser = Launcher.getOpenSourceChooser();

		int result = jFileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();

			this.controller.getFldFileName().setText(file.getPath());
		}
	}
}
