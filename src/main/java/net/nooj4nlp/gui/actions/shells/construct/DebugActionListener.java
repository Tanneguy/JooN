package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DebugShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

/**
 * Class implements action taken when "Debug" is clicked in Grammar menu.
 */

public class DebugActionListener implements ActionListener
{
	private GrammarEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of the recently opened grammar
	 */

	public DebugActionListener(GrammarEditorShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GrammarEditorShell grammarShell = this.controller.editor;
		// get preferred size and current location
		Dimension preferedDimension = grammarShell.getPreferredSize();
		Point locationPoint = grammarShell.getLocation();

		try
		{
			// if the Grammar Shell was minimized - restore it
			grammarShell.setIcon(false);
		}
		catch (PropertyVetoException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}

		// if Grammar's window size is different from preferred size, restore the preferred size
		if (grammarShell.getSize().height != preferedDimension.height
				|| grammarShell.getSize().width != preferedDimension.width)
			grammarShell.setSize(preferedDimension);

		// if Grammar's window was moved, set it to the starting position
		if (locationPoint.x != 100 || locationPoint.y != 100)
			grammarShell.setLocation(100, 100);

		// close the Gram Struct Shell if it's opened
		if (grammarShell.formGramStruct != null)
		{
			grammarShell.formGramStruct.dispose();
			grammarShell.formGramStruct = null;
		}

		// if Debug shell is already opened, just select it...
		if (controller.debugShell != null)
		{
			try
			{
				controller.debugShell.setSelected(true);
			}
			catch (PropertyVetoException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
		// ...otherwise, open a new Debug shell
		else
		{
			DebugShell debugShell = new DebugShell(this.controller);
			Launcher.getDesktopPane().add(debugShell);
			debugShell.setVisible(true);
			controller.debugShell = debugShell;
		}
	}
}