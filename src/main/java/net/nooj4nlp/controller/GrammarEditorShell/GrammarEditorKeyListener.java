package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class GrammarEditorKeyListener implements KeyListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public GrammarEditorKeyListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) // delete selected nodes
		{
			

			// delete all selected nodes
			boolean deleted = false;
			controller.grf.nbofnodesdeleted = 0;
			controller.grf.inodedeleted = -1;
			for (int inode = 0; inode < controller.grf.label.size(); inode++)
			{
				boolean sel = controller.grf.selected.get(inode);
				if (!sel || inode == 1)
					continue;
				if (inode == 0)
					controller.grf.label.set(0, "<E>");
				else
				{
					deleted = true;
					controller.grf.inodedeleted = inode;
					controller.grf.nbofnodesdeleted++;
					controller.grf.deleteNode(inode);
					inode--;
				}
			}
			if (deleted)
			{
				if (controller.grf.nbofnodesdeleted > 1)
					controller.modify("delete " + controller.grf.nbofnodesdeleted + " nodes", false, true);
				else if (controller.grf.inodedeleted != -1)
					controller.modify("delete node #" + controller.grf.inodedeleted, false, true);
			}
			editor.pGraph.invalidate();
			editor.pGraph.repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_U)
		{
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_D)
		{
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_N)
		{
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_P)
		{
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}