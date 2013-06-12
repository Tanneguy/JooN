package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.lang3.SystemUtils;

public class JPGraphMouseListener implements MouseListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public JPGraphMouseListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (controller.grammar.lockType != 0)
				return; // cannot touch a locked grammar
			// find the node
			controller.getMouseN();
			int mouseN = controller.mouseN;
			if (mouseN == -1) // mouse is not in a node
			{
				if (e.isControlDown() || e.isAltDown())
				{
					// create a new node
					int rx = (int) (controller.mouseOX / controller.grf.scale) - controller.grf.epsilonWid / 2;
					int ry = (int) (controller.mouseOY / controller.grf.scale);
					int snode = controller.grf.addNode("<E>", rx, ry);
					controller.grf.hei.set(snode, controller.grf.epsilonHei);
					controller.grf.wid.set(snode, controller.grf.epsilonWid);
					controller.grf.widB.set(snode, controller.grf.epsilonwidB);

					controller.modify("create node #" + snode, false, false);

					// connect all selected nodes to new node
					controller.grf.inodeconnected = -1;
					controller.grf.nbofnodesconnected = 0;
					for (int inode = 0; inode < controller.grf.label.size(); inode++)
					{
						boolean sel = controller.grf.selected.get(inode);
						if (!sel || inode == 1)
							continue;
						controller.grf.inodeconnected = inode;
						controller.grf.nbofnodesconnected++;

						ArrayList<Integer> children = controller.grf.child.get(inode);
						children.add(snode);

						controller.grf.selected.set(inode, false); // unselect all
																	// nodes
					}
					if (controller.grf.nbofnodesconnected == 1)
						controller.modify("connect node #" + controller.grf.inodeconnected + " to node #" + snode,
								false, true);
					if (controller.grf.nbofnodesconnected > 1)
						controller.modify("connect " + controller.grf.nbofnodesconnected + " nodes to node #" + snode,
								false, true);

					controller.grf.selected.set(snode, true); // DO select new node
					// edit the new node
					controller.displayRtbox(snode);
				}
				else
				{
					if (controller.mouseEdited != -1) // cancel editing
					{
						controller.grf.label.set(controller.mouseEdited, controller.originallabel);
						controller.hideRtbox();
					}

					// unselect everything
					for (int inode = 0; inode < controller.grf.label.size(); inode++)
						controller.grf.selected.set(inode, false);
				}
			}
			else
			// mouse click in a node
			{
				if (mouseN != controller.lasteditednode)
				{
					controller.lasteditednode = mouseN;
					controller.nbofcreatednodes = 1;
				}
				if (controller.mouseEdited != -1) // cancel current editing
				{
					controller.grf.label.set(controller.mouseEdited, controller.originallabel);
					controller.hideRtbox();
					return;
				}
				if (e.isShiftDown()) // either unselect node or select
				{

					if (controller.grammar.lockType != 0) // cannot
															// controller.modify a
															// locked grammar
						return;

					boolean sel = controller.grf.selected.get(mouseN);
					controller.grf.selected.set(mouseN, !sel);
				}
				else if (e.isControlDown()) // edit a label
				{

					if (controller.grammar.lockType != 0) // cannot
															// controller.modify a
															// locked grammar
						return;

					controller.grf.selected.set(mouseN, false); // unselect node
					controller.displayRtbox(mouseN);
				}
				else if (e.isAltDown() && controller.grf.interline != 0) // explore
																			// an
																			// embedded
																			// graph
				{
					int y0 = (int) (controller.grf.posY.get(mouseN) * controller.grf.scale)
							- controller.grf.hei.get(mouseN);
					int line = (controller.mouseOY - y0) / controller.grf.interline;
					String label = controller.grf.label.get(mouseN);
					String output = null;
					RefObject<String> outputRef = new RefObject<String>(output);
					String[] terms = Graph.inLineLabel(label, outputRef);
					output = outputRef.argvalue;
					if (line < terms.length && terms[line].length() > 0 && terms[line].charAt(0) == ':')
						controller.findAndLoadGraph(terms[line].substring(1));
					else
					{
						// mac-friendly: alt-click to edit the node if there is no
						// embedded graph
						controller.grf.selected.set(mouseN, false); // unselect node
						controller.displayRtbox(mouseN);
					}
				}
				else
				// select mouseN or connect/disconnect all selected nodes to
				// mouseN
				{

					if (controller.grammar.lockType != 0)
						return; // cannot .modify a locked grammar

					boolean connection = false;
					controller.grf.nbofnodesconnected = 0;
					controller.grf.inodeconnected = -1;
					controller.grf.oneconnectionatleast = controller.grf.onedisconnectionatleast = false;
					for (int inode = 0; inode < controller.grf.label.size(); inode++)
					{
						boolean sel = controller.grf.selected.get(inode);
						if (!sel || inode == 1)
							continue;
						connection = true; // at least one node was selected:
											// add/remove a connection

						ArrayList<Integer> children = controller.grf.child.get(inode);
						int ichild;
						for (ichild = 0; ichild < children.size(); ichild++)
						{
							if (children.get(ichild) == mouseN)
								break;
						}

						controller.grf.inodeconnected = inode;
						controller.grf.nbofnodesconnected++;
						if (ichild < children.size()) // already connected
						{
							children.remove(ichild);
							controller.grf.onedisconnectionatleast = true;
						}
						else
						{
							controller.grf.oneconnectionatleast = true;
							children.add(mouseN);
						}

						if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown())
							controller.grf.selected.set(inode, false);
					}
					if (!connection) // no node was previously selected: select
										// mouseN
					{
						controller.grf.selected.set(mouseN, true);
					}
					else
					{
						if (controller.grf.nbofnodesconnected == 1)
						{
							// only two possibilities: connect or disconnect
							if (controller.grf.oneconnectionatleast && !controller.grf.onedisconnectionatleast)
								controller.modify("connect node #" + controller.grf.inodeconnected + " to node #"
										+ mouseN, false, true);
							else if (!controller.grf.oneconnectionatleast && controller.grf.onedisconnectionatleast)
								controller.modify("disconnect node #" + controller.grf.inodeconnected + " from node #"
										+ mouseN, false, false);
						}
						else if (controller.grf.nbofnodesconnected > 1)
						{
							// three possibilities: connect all, disconnect all,
							// connect some and disconnect others
							if (controller.grf.oneconnectionatleast && !controller.grf.onedisconnectionatleast)
								controller.modify("connect " + controller.grf.nbofnodesconnected + " nodes to node #"
										+ mouseN, false, true);
							else if (!controller.grf.oneconnectionatleast && controller.grf.onedisconnectionatleast)
								controller.modify("disconnect " + controller.grf.nbofnodesconnected
										+ " nodes from node #" + mouseN, false, false);
							else if (controller.grf.oneconnectionatleast && controller.grf.onedisconnectionatleast)
								controller.modify("(dis)connect " + controller.grf.nbofnodesconnected
										+ " nodes (from)to node #" + mouseN, false, true);
						}
					}
				}
			}
			editor.pGraph.invalidate();
			editor.pGraph.repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
		if (!SwingUtilities.isLeftMouseButton(e))
			return;

		controller.mouseOX = e.getX();
		controller.mouseOY = e.getY();
		controller.mouseM = false;
		controller.mouseD = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		

		if (controller.grf == null)
			return;
		if (SwingUtilities.isRightMouseButton(e))
		{
			// create right-click menu
			if (e.isPopupTrigger() || !SystemUtils.IS_OS_WINDOWS)
			{
				editor.popText.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			else
			{
				return;
			}
		}

		controller.mouseD = false;

		if (!controller.mouseM || e.isControlDown() || e.isAltDown())
		{
			// a plain click (no move)
			controller.SelectionRectangle.width = controller.SelectionRectangle.height = 0; // erase
																							// the
																							// selection
																							// rectangle
			return;
		}
		if (controller.SelectionRectangle.width != 0 && controller.SelectionRectangle.height != 0)
		{
			controller.mouseOX = -1;
			controller.mouseOY = -1;
			// just drew a rectangle: select all nodes inside
			controller.getMouseNodes();
			controller.SelectionRectangle.width = controller.SelectionRectangle.height = 0;
			controller.mouseM = false;
			controller.grf.inodemoved = -1;
			controller.grf.nbofnodesmoved = 0;
			editor.pGraph.invalidate();
			editor.pGraph.repaint();
			return;
		}

		// finishing moving nodes
		controller.mouseOX = -1;
		controller.mouseOY = -1;
		if (controller.grammar.dispGrid && controller.grf != null)
		{
			for (int inode = 0; inode < controller.grf.posX.size(); inode++)
			{
				controller.grf.posX.set(inode, (controller.grf.posX.get(inode) / 20) * 20);
				controller.grf.posY.set(inode, (controller.grf.posY.get(inode) / 20) * 20);
			}
		}
		if (controller.grf.nbofnodesmoved > 1)
			controller.modify("move " + controller.grf.nbofnodesmoved + " nodes", false, false);
		else if (controller.grf.inodemoved != -1)
			controller.modify("move node #" + controller.grf.inodemoved, false, false);

		controller.SelectionRectangle.width = controller.SelectionRectangle.height = 0;
		controller.mouseM = false;
		controller.grf.inodemoved = -1;
		controller.grf.nbofnodesmoved = 0;
		editor.pGraph.invalidate();
		editor.pGraph.repaint();
	}

}
