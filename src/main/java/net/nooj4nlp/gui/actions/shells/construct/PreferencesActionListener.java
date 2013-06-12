package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;

/**
 * 
 * ActionListener that opens the Preferences dialog
 * 
 */
public class PreferencesActionListener implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		PreferencesDialog dialog = new PreferencesDialog();
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
}
