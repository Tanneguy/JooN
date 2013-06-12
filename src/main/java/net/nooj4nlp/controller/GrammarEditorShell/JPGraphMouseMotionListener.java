package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class JPGraphMouseMotionListener implements MouseMotionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public JPGraphMouseMotionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		Graph grf = controller.grf;

		if (grf == null)
			return;
		if (!controller.mouseM) // first time mouseMove is called
		{
			controller.mouseM = true;
			// use controller.mouseX instead of controller.mouseOX because move/resize is incremental for each mouseMove
			controller.mouseX = controller.mouseOX;
			controller.mouseY = controller.mouseOY;
			controller.mouseR = controller.getMark();
			if (controller.mouseR == -1) // not a resize area node, get node
			{
				controller.getMouseN();
			}
			return;
		}

		if (controller.mouseR != -1)
		{
			// resize area node controller.mouseR
			controller.grf.posY.set(controller.mouseR, grf.posY.get(controller.mouseR)
					+ (int) ((e.getY() - controller.mouseY) / grf.scale) / 2);
			controller.grf.wid.set(controller.mouseR, grf.wid.get(controller.mouseR)
					+ (int) ((e.getX() - controller.mouseX) / grf.scale));
			controller.grf.hei.set(controller.mouseR, grf.hei.get(controller.mouseR)
					+ (int) ((e.getY() - controller.mouseY) / grf.scale) / 2);
			editor.pGraph.invalidate();
			controller.mouseX = e.getX();
			controller.mouseY = e.getY();
		}

		if (controller.mouseN != -1) // move controller.mouseN and all selected nodes
		{
			
			controller.grf.nbofnodesmoved = 0;
			controller.grf.inodemoved = -1;
			for (int inode = 0; inode < controller.grf.label.size(); inode++)
			{
				boolean sel = controller.grf.selected.get(inode);
				if (inode != controller.mouseEdited && (sel || inode == controller.mouseN))
				{
					// Move the node
					controller.grf.posX.set(inode, controller.grf.posX.get(inode)
							+ (int) ((e.getX() - controller.mouseX) / controller.grf.scale));
					controller.grf.posY.set(inode, controller.grf.posY.get(inode)
							+ (int) ((e.getY() - controller.mouseY) / controller.grf.scale));
					controller.grf.nbofnodesmoved++;
					controller.grf.inodemoved = inode;
				}
			}
			controller.mouseX = e.getX();
			controller.mouseY = e.getY();
			controller.modify(null, false, false);
		}

		else
		// build a selection rectangle
		{
			controller.SelectionRectangle.x = controller.mouseOX;
			controller.SelectionRectangle.y = controller.mouseOY;
			controller.SelectionRectangle.width = e.getX() - controller.mouseOX;
			controller.SelectionRectangle.height = e.getY() - controller.mouseOY;
			controller.getMouseNodes();
		}
		editor.pGraph.invalidate();
		editor.pGraph.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

}
