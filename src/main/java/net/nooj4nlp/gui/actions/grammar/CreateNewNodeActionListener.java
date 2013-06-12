package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class CreateNewNodeActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public CreateNewNodeActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		
		// create a new node
		int rx = controller.grf.posX.get(controller.lasteditednode) + controller.nbofcreatednodes * 15;
		int ry = controller.grf.posY.get(controller.lasteditednode) + controller.nbofcreatednodes * 15;
		controller.nbofcreatednodes++;

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
			controller.modify("connect node #" + controller.grf.inodeconnected + " to node #" + snode, false, true);
		if (controller.grf.nbofnodesconnected > 1)
			controller.modify("connect " + controller.grf.nbofnodesconnected + " nodes to node #" + snode, false, true);

		controller.grf.selected.set(snode, true); // DO select new node
		// edit the new node
		controller.displayRtbox(snode);
	}

}