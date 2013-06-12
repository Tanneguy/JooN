package net.nooj4nlp.controller.DictionaryDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.gui.main.Launcher;

public class SetActionListener implements ActionListener
{

	private JInternalFrame frame;
	private DictionaryDialogController controller;

	public SetActionListener(JInternalFrame frame, DictionaryDialogController controller)
	{
		super();
		this.frame = frame;
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser chooser = Launcher.getOpenDicChooser();

		int code = chooser.showOpenDialog(frame);
		if (code == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = chooser.getSelectedFile();
			chooser.setCurrentDirectory(selectedFile);

			controller.getTxtDictionaryName().setText(selectedFile.getAbsolutePath());
			if (controller.loadLines(0, 500))
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display "
								+ org.apache.commons.io.FilenameUtils.getName(controller.getTxtDictionaryName()
										.getText()), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			else
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display beginning of "
								+ org.apache.commons.io.FilenameUtils.getName(controller.getTxtDictionaryName()
										.getText()), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
	}
}