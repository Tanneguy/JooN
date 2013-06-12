package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;

public class ViewActionListener implements ActionListener
{

	private DictionaryEditorShellController controller;

	public ViewActionListener(DictionaryEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		DictionaryEditorShell shell = controller.getShell();
		JMenuItem item = shell.getMntmView();

		if (item.getText().equals("View as Table"))
		{

			item.setText("View as List");
			shell.getMntmView().setText("View as List");
			shell.getMntmCheckFormat().setEnabled(false);
			shell.getMntmFind().setEnabled(false);
			shell.getMntmSort().setEnabled(false);
			shell.getMntmSortBackward().setEnabled(false);
			shell.getMntmExport().setEnabled(true);

			controller.fillInLvDicos();

			controller.getEditorPane().setVisible(!controller.getEditorPane().isVisible());
			controller.getTablePane().setVisible(!controller.getTablePane().isVisible());
		}
		else
		{

			item.setText("View as Table");
			shell.getMntmView().setText("View as Table");
			shell.getMntmCheckFormat().setEnabled(true);
			shell.getMntmFind().setEnabled(true);
			shell.getMntmSort().setEnabled(true);
			shell.getMntmSortBackward().setEnabled(true);
			shell.getMntmExport().setEnabled(false);

			controller.setLblText("Dictionary contains "
					+ Integer.toString(DictionaryDialogController.count(controller.getTextPane())) + " entries");

			controller.getEditorPane().setVisible(!controller.getEditorPane().isVisible());
			controller.getTablePane().setVisible(!controller.getTablePane().isVisible());
		}
	}
}