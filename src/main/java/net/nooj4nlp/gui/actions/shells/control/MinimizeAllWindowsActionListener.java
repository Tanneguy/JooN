package net.nooj4nlp.gui.actions.shells.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

public class MinimizeAllWindowsActionListener implements ActionListener
{

	private JDesktopPane desktopPane;
	private boolean leaveActive;

	public MinimizeAllWindowsActionListener(JDesktopPane p, boolean lactive)
	{
		desktopPane = p;
		leaveActive = lactive;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		for (JInternalFrame frame : desktopPane.getAllFrames())
		{
			if (!frame.isIcon())
			{
				if (leaveActive)
				{
					if (!frame.isSelected())
					{
						try
						{
							frame.setIcon(true);
						}
						catch (PropertyVetoException e1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO, Constants.NOOJ_ERROR,
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				else
				{
					try
					{
						frame.setIcon(true);
					}
					catch (PropertyVetoException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO, Constants.NOOJ_ERROR,
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
}