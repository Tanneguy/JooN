package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.dialogs.GenerateDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Action listener responsible for opening of Generate Dialog.
 * 
 */
public class GenerateLanguageActionListener implements ActionListener
{
	private GrammarEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of opened grammar
	 */

	public GenerateLanguageActionListener(GrammarEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GenerateDialog genDialog = new GenerateDialog(controller);
		Launcher.getDesktopPane().add(genDialog);
		genDialog.setVisible(true);
	}
}