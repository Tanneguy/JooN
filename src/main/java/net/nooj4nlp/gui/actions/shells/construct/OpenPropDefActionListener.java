package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.PropDefEditorShell;

public class OpenPropDefActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	public OpenPropDefActionListener(JDesktopPane desktopPane)
	{
		super();
		this.desktopPane = desktopPane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser chooser = Launcher.getOpenDefDialogChooser();

		int code = chooser.showOpenDialog(desktopPane);
		if (code == JFileChooser.APPROVE_OPTION)
		{
			PropDefEditorShell editor = new PropDefEditorShell();
			editor.getController().loadFromFile(chooser.getSelectedFile().getAbsolutePath());
			Launcher.getDesktopPane().add(editor);
			editor.setVisible(true);
		}
	}

}
