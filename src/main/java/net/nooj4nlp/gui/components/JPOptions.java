package net.nooj4nlp.gui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import net.nooj4nlp.controller.SyntacticTreeShell.SyntacticTreeShellController;

public class JPOptions extends JPanel
{
	private static final long serialVersionUID = 1L;
	private SyntacticTreeShellController controller;

	public JPOptions()
	{
		super();
	}

	public void setController(SyntacticTreeShellController controller)
	{
		this.controller = controller;
	}

	@Override
	public void paint(Graphics grphcs)
	{
		super.paint(grphcs);
		Graphics2D g = (Graphics2D) grphcs;

		controller.resetEntry(grphcs);
		if (controller.getShell() != null)
			controller.getShell().getPanel1().repaint();

		g.dispose();
	}
}