package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.AlignmentDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements action taken when user clicks on 'Alignment' menu item of Grammar menu.
 */

public class AlignmentActionListener implements ActionListener
{
	private GrammarEditorShellController grammarController;

	/**
	 * Constructor.
	 * 
	 * @param grammarController
	 *            - controller of currently opened grammar
	 */
	public AlignmentActionListener(GrammarEditorShellController grammarController)
	{
		super();

		this.grammarController = grammarController;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// if Alignment dialog is already opened, put it on top
		if (grammarController.alignmentDialog != null)
		{
			try
			{
				grammarController.alignmentDialog.setSelected(true);
			}
			catch (PropertyVetoException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}

		// ...otherwise, create a new one
		else
		{
			AlignmentDialog alignDialog = new AlignmentDialog(grammarController);
			Launcher.getDesktopPane().add(alignDialog);
			alignDialog.setVisible(true);
			grammarController.alignmentDialog = alignDialog;
		}
	}
}