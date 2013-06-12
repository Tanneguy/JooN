package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.gui.main.Launcher;

public class SortActionListener implements ActionListener
{

	private DictionaryEditorShellController controller;
	private boolean backward;

	public SortActionListener(DictionaryEditorShellController controller, boolean inverse)
	{
		super();
		this.controller = controller;
		this.backward = inverse;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		controller.sortDictionary(!backward);
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary sorted", "NooJ: sort OK",
				JOptionPane.INFORMATION_MESSAGE);
		controller.setLblText("Dictionary contains "
				+ Integer.toString(DictionaryDialogController.count(controller.getTextPane())) + " entries");
	}
}