package net.nooj4nlp.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class JPGraph extends JPanel
{
	private static final long serialVersionUID = 1L;
	private Graph grf;
	private GrammarEditorShell editor;

	public JPGraph(GrammarEditorShell shell)
	{
		editor = shell;
	}

	public void paintComponent(Graphics grphcs)
	{
		Graphics2D g = (Graphics2D) grphcs;

		ArrayList<Graph> graphs = editor.getController().grammar.graphs;

		Font iFont = editor.getController().iFont;
		Font oFont = editor.getController().oFont;
		Font cFont = editor.getController().cFont;

		// this hack solves the bug in C# version, related to deletion of graph and its children
		if (graphs == null || graphs.size() == 0)
		{
			displayEmptyGraph(g, iFont);
			return;
		}

		grf = graphs.get(editor.getController().current);

		if (grf == null)
		{
			displayEmptyGraph(g, iFont);
			return;
		}
		
		if (editor.getController().grammar.lockType == 1)
		{
			g.setStroke(new BasicStroke(2.0F));
			// set color to light blue
			g.setColor(new Color(173, 216, 230));
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.RED);
			g.setFont(iFont);

			if (editor.getController().grammar.lockType == 1)
				g.drawString(
						"Grammar is locked  (you can display its structure and its contract, and apply it to texts)",
						20, 20);
			else
				g.drawString(
						"Community Grammar is locked (you can display its structure and its contract, and apply it to texts)",
						20, 20);
			return;
		}

		// computes the scale
		float scale;
		// string zoom;
		if (grf.fits == 1) // fit in window
		{
			// zoom = "Fit";
			float ratiograph = 1.0F * grf.size.width / grf.size.height;
			float ratioform = 1.0F * editor.pBackGraph.getWidth() / editor.pBackGraph.getHeight();
			if (ratiograph <= ratioform)
				scale = (1.0F * editor.pBackGraph.getHeight()) / grf.size.height;
			else
				scale = (1.0F * editor.pBackGraph.getWidth()) / grf.size.width;
		}
		else
		{
			scale = grf.fits / 100.0F;
			
		}

		// Java's font constructor expects an integer instead of a float as the
		// size argument
		grf.ifont = new Font(iFont.getFamily(), iFont.getStyle(), Math.round(iFont.getSize() * scale));
		grf.scale = grf.ifont.getSize2D() / iFont.getSize2D();
		grf.cfont = new Font(cFont.getFamily(), cFont.getStyle(), Math.round(cFont.getSize() * grf.scale));

		// other fonts than ifont and cfont (ifont has already been set in
		// ForGrammar)
		grf.ffont = new Font(iFont.getFamily(), iFont.getStyle(), Math.round(iFont.getSize() * scale));
		grf.ofont = new Font(oFont.getFamily(), oFont.getStyle(), Math.round(oFont.getSize() * scale));
		grf.vfont = new Font(iFont.getFamily(), iFont.getStyle(), Math.round(iFont.getSize() * scale * 2));

		// size transform etc.
		this.setSize(new Dimension((int) (grf.size.width * grf.scale), (int) (grf.size.height * grf.scale)));
		// TODO: pBackGraph.AutoScrollMinSize = new Size
		

		grf.paint(this, g, editor.getController().mouseM);

		if (editor.getController().SelectionRectangle.width != 0
				&& editor.getController().SelectionRectangle.height != 0)
		{
			g.setColor(editor.getController().grammar.sColor);

			float dash1[] = { 10.0f };
			BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

			g.setStroke(dashed);

			Rectangle drawRectangle = editor.getController()
					.computeRectangle(editor.getController().SelectionRectangle);
			g.draw(drawRectangle);
		}

		if (editor.getController().first_paint)
		{
			this.requestFocusInWindow();
			;
			editor.getController().first_paint = false;
		}

		this.paintComponents(g);
		g.dispose();
	}

	/**
	 * Help function for drawing an empty graph.
	 * 
	 * @param g
	 *            - drawing graphics
	 * @param iFont
	 *            - grammar font
	 */

	private void displayEmptyGraph(Graphics2D g, Font iFont)
	{
		// background color needs to be set due to rendering problems
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setStroke(new BasicStroke(2.0F));
		// set color to light blue
		g.setColor(new Color(173, 216, 230));
		g.fillRect(0, 0, 800, 600);
		g.setColor(Color.RED);
		g.drawLine(0, 0, 800, 600);
		g.drawLine(800, 0, 0, 600);
		g.setFont(iFont);
		g.drawString("Graph does not exist", 20, 20);
	}
}