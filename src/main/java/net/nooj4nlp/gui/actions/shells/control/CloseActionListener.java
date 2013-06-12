package net.nooj4nlp.gui.actions.shells.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;

/**
 * 
 * ActionListener that closes the active internal shell
 */
public class CloseActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	public CloseActionListener(JDesktopPane dp)
	{
		desktopPane = dp;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

		if (selectedFrame == null)
			return;

		String cls = selectedFrame.getClass().getSimpleName();

		if (cls.equals("DictionaryEditorShell"))
		{
			((DictionaryEditorShell) selectedFrame).getController().close();
		}
		else if (cls.equals("PropDefEditorShell"))
		{
			((PropDefEditorShell) selectedFrame).getController().close();
		}
		else if (cls.equals("FlexDescEditorShell"))
		{
			((FlexDescEditorShell) selectedFrame).getController().close();
		}
	}
}