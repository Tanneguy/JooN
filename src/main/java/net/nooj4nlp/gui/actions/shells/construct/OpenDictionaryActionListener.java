package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.gui.dialogs.DelaDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * ActionListener that opens a NooJ dictionary
 * 
 */
public class OpenDictionaryActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	public OpenDictionaryActionListener(JDesktopPane dp)
	{
		desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser chooser = Launcher.getOpenDicChooser();

		int code = chooser.showOpenDialog(desktopPane);
		if (code == JFileChooser.APPROVE_OPTION)
		{
			// NooJ dictionary
			if (chooser.getFileFilter().getClass().getSimpleName().equals("FileNameExtensionFilter"))
				DictionaryEditorShellController.openNooJDictionary(chooser.getSelectedFile().getAbsolutePath());
			// DELAS, DELAF, DELAC, DELACF
			if (chooser.getFileFilter().getClass().getSimpleName().equals("AcceptAllFileFilter"))
			{
				DelaDialog dialog = new DelaDialog(chooser.getSelectedFile().getAbsolutePath());
				dialog.setModal(true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		}
	}
}
