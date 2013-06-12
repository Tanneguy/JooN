package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * ActionListener that opens a NooJ text file
 * 
 */
public class OpenTextActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	public OpenTextActionListener(JDesktopPane dp)
	{
		desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser chooser = Launcher.getOpenTextChooser();

		try
		{
			int code = chooser.showOpenDialog(desktopPane);
			if (code == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = chooser.getSelectedFile();
				if (FilenameUtils.getExtension(selectedFile.getName()).equals(Constants.JNOT_EXTENSION))
				{
					TextEditorShellController textController = new TextEditorShellController(selectedFile);
					textController.openText(selectedFile);
				}
				else
				{
					TextEditorShellController textController = new TextEditorShellController(selectedFile);
					textController.importText(selectedFile);
				}
			}
		}
		catch (Exception f)
		{
			f.printStackTrace();
		}
	}

}
