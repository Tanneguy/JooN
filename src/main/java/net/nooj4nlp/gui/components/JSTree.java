package net.nooj4nlp.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import net.nooj4nlp.controller.SyntacticTreeShell.SyntacticTreeShellController;

public class JSTree extends JPanel
{
	private static final long serialVersionUID = 1L;
	private SyntacticTreeShellController controller;

	public void paint(Graphics grphcs)
	{
		super.paint(grphcs);

		if (controller.getTree() == null)
		{
			grphcs.dispose();
			return;
		}

		HashMap<String, ArrayList<Integer>> bridges = new HashMap<String, ArrayList<Integer>>();
		controller.getTree().draw(grphcs, controller.getShell().getCbDisplayAll().isSelected(), bridges);
		if (bridges.size() > 0)
		{
			int nb = 1; // depth of the current bridge
			for (Object o : bridges.keySet())
			{
				String xref = (String) o;
				ArrayList<Integer> b = bridges.get(xref);
				int xlast = b.get(0);
				for (int i = 1; i < b.size(); i++)
				{
					int xcurrent = b.get(i);
					drawBridge(grphcs, Color.BLUE, xlast, xcurrent, nb);
					xlast = xcurrent;
				}
				nb++;
			}
		}

		grphcs.dispose();
	}

	private void drawBridge(Graphics g, Color color, int xsource, int xdest, int nb)
	{
		int y = controller.getShell().getPanel1().getHeight() - 2 * STree.LINE; // +height / 4;

		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke());
		graphics.drawLine(xsource, y + 10 * nb, xdest, y + 10 * nb);
		graphics.drawLine(xsource, y + 10 * nb, xsource, y);
		graphics.drawLine(xdest, y + 10 * nb, xdest, y);
	}

	public void setController(SyntacticTreeShellController controller)
	{
		this.controller = controller;
	}
}
