package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ErrorShell;

public class CheckActionListener implements ActionListener
{

	private DictionaryEditorShellController controller;

	public CheckActionListener(DictionaryEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (controller.check())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "No problem found", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			ErrorShell shell = controller.getErrorShell();
			JDesktopPane pane = Launcher.getDesktopPane();
			if (pane.getIndexOf(shell) < 0)
			{
				pane.add(controller.getErrorShell());
				shell.setVisible(true);
			}
		}
		controller.setLblText("Dictionary contains "
				+ Integer.toString(DictionaryDialogController.count(controller.getTextPane())) + " entries");
	}

}
