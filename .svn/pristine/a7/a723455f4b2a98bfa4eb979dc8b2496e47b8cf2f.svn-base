package net.nooj4nlp.controller.StatsShell;

import java.awt.Graphics;

import javax.swing.JPanel;

import net.nooj4nlp.gui.shells.StatsShell;

/**
 * Panel class which implements drawing frequency or standard score of concordance's sequences.
 */

public class DrawingJPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private StatsShell statsShell;
	private boolean frequencyContext;

	/**
	 * Constructor.
	 * 
	 * @param statsShell
	 *            - window shell of Statistics
	 */

	public DrawingJPanel(StatsShell statsShell, boolean frequencyContext)
	{
		super();
		this.statsShell = statsShell;
		this.frequencyContext = frequencyContext;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		StatsShellController statsController = statsShell.getStatsController();

		if (frequencyContext)
			statsController.paintFrequency(g);
		else
			statsController.paintStandardScore(g);
	}
}