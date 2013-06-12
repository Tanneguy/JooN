package net.nooj4nlp.gui.actions.shells.control;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class TileWindowsActionListener implements ActionListener
{

	private JDesktopPane desktopPane;
	private boolean horizontal;

	public TileWindowsActionListener(JDesktopPane p, boolean h)
	{
		desktopPane = p;
		horizontal = h;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JInternalFrame[] frames = desktopPane.getAllFrames();
		if (frames.length == 0)
			return;
		tileWindows();
	}

	private void tileWindows()
	{
		// Divide up space among open frames
		JInternalFrame frames[] = desktopPane.getAllFrames();
		Dimension frameSize = new Dimension(desktopPane.getSize());
		int xShift = 0, yShift = 0;

		if (frames.length > 0)
		{
			// Horizontal or vertical tile?
			if (horizontal)
			{
				frameSize.height /= frames.length;
				yShift = frameSize.height;
			}
			else
			{
				frameSize.width /= frames.length;
				xShift = frameSize.width;
			}
		}

		// Set size of each frame
		int x = 0, y = 0;
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i].isMaximum())
			{
				try
				{
					frames[i].setMaximum(false);
				}
				catch (PropertyVetoException pve)
				{
				}
			}
			frames[i].setSize(frameSize);
			frames[i].setLocation(x, y);
			x += xShift;
			y += yShift;
		}
	}

}
