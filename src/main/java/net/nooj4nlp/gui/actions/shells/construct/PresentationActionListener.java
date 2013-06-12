package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.GraphPresentationDialog.GraphPresentationController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.GraphPresentationDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements opening of grammar's Presentation dialog.
 */

public class PresentationActionListener implements ActionListener
{
	private GrammarEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of opened grammar
	 */
	public PresentationActionListener(GrammarEditorShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (controller.presentationDialog != null)
		{
			try
			{
				controller.presentationDialog.setSelected(true);
			}
			catch (PropertyVetoException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}

		else
		{
			GraphPresentationDialog presentationDialog = new GraphPresentationDialog(controller);
			Launcher.getDesktopPane().add(presentationDialog);
			presentationDialog.setVisible(true);
			GraphPresentationController graphController = presentationDialog.getController();
			graphController.showDialog();
			controller.presentationDialog = presentationDialog;
		}
	}
}