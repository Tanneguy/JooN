package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Constants;

public class OpenCorpusActionListener implements ActionListener
{
	private JDesktopPane desktopPane;

	public OpenCorpusActionListener(JDesktopPane desktopPane)
	{
		this.desktopPane = desktopPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter(".jnoc files", Constants.JNOC_EXTENSION);
		chooser.setFileFilter(filter);

		try
		{
			chooser.setDialogTitle("Open a corpus");
			int code = chooser.showOpenDialog(desktopPane);
			if (code == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = chooser.getSelectedFile();

				CorpusEditorShellController controller = new CorpusEditorShellController(null, null, null, null);
				controller.openNoojCorpus(selectedFile, false);
				controller.openNoojEngine();

				// DialogLocate...
				// Comparers...
			}
		}
		catch (Exception f)
		{
			f.printStackTrace();
		}
	}
}
