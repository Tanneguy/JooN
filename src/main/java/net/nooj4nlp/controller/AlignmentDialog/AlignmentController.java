package net.nooj4nlp.controller.AlignmentDialog;

import java.beans.PropertyVetoException;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.dialogs.AlignmentDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Controller class for Alignment Dialog of opened Grammar.
 */

public class AlignmentController
{
	private int nbOfNodesAligned;

	private GrammarEditorShellController grammarController;
	private AlignmentDialog dialog;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            - alignment dialog
	 * @param grammarController
	 *            - controller of opened grammar
	 */

	public AlignmentController(AlignmentDialog dialog, GrammarEditorShellController grammarController)
	{
		this.grammarController = grammarController;
		this.dialog = dialog;
	}

	/**
	 * Function closes Alignment Dialog.
	 */

	public void close()
	{
		this.grammarController.alignmentDialog = null;
		this.dialog.dispose();
	}

	/**
	 * Function implements bottom horizontal alignment of currently opened Grammar.
	 */
	public void bottomAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		// compute lowest node
		int lowest = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			int y = grf.posY.get(iNode);
			int line0 = y + grf.hei.get(iNode);

			if (lowest < line0)
				lowest = line0;
		}

		// set all nodes to lowest
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posY.set(iNode, lowest - grf.hei.get(iNode));
		}

		grammarController.modify("bottom-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function implements top horizontal alignment of currently opened Grammar.
	 */

	public void topAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		int top = 999999999;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			int y = grf.posY.get(iNode);
			int line0 = y - grf.hei.get(iNode);

			if (top > line0)
				top = line0;
		}

		// set all nodes to lowest
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posY.set(iNode, top + grf.hei.get(iNode));
		}

		grammarController.modify("top-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function implements central horizontal alignment of currently opened Grammar.
	 */

	public void centerHorizontalAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		// compute average y
		int average = 0;
		int nbSelected = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			int y = grf.posY.get(iNode);
			average += y;
			nbSelected++;
		}

		if (nbSelected < 2)
			return;

		average = average / nbSelected;

		// set all nodes to average
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posY.set(iNode, average);
		}

		grammarController.modify("horizontal-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function implements left vertical alignment of currently opened Grammar.
	 */

	public void leftAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		// compute lowest x
		int lowest = 999999999;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			int x = grf.posX.get(iNode);

			if (lowest > x)
				lowest = x;
		}

		// set all nodes to lowest x
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posX.set(iNode, lowest);
		}

		grammarController.modify("left-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function implements right vertical alignment of currently opened Grammar.
	 */

	public void rightAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		// compute rightest node
		int rightest = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			int x = grf.posX.get(iNode);
			int right = x + grf.wid.get(iNode);

			if (rightest < right)
				rightest = right;
		}

		// set all nodes to lowest
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posX.set(iNode, rightest - grf.wid.get(iNode));
		}

		grammarController.modify("right-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function implements center vertical alignment of currently opened Grammar.
	 */

	public void centerVerticalAlign()
	{
		if (grammarController == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		// compute average node center
		int average = 0;
		int nbSelected = 0;
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;
			nbSelected++;

			int x = grf.posX.get(iNode);
			int center = x + (int) (grf.wid.get(iNode) / 2.0F);

			average += center;
		}

		if (nbSelected < 2)
			return;

		average = average / nbSelected;

		// set all nodes to average
		nbOfNodesAligned = 0;

		for (int iNode = 0; iNode < grf.selected.size(); iNode++)
		{
			if (!grf.selected.get(iNode))
				continue;

			nbOfNodesAligned++;

			grf.posX.set(iNode, average - (int) (grf.wid.get(iNode) / 2.0F));
		}

		grammarController.modify("vertical-align " + nbOfNodesAligned + " nodes", false, false);

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function turns on or off grid view of currently opened Grammar.
	 */

	public void useGrid()
	{
		if (grammarController == null)
			return;

		Grammar grm = grammarController.grammar;

		if (grm == null)
			return;

		Graph grf = grammarController.grf;

		if (grf == null)
			return;

		grm.dispGrid = this.dialog.getCbUseGrid().isSelected();

		for (int iNode = 0; iNode < grf.posX.size(); iNode++)
		{
			grf.posX.set(iNode, (grf.posX.get(iNode) / 20) * 20);
			grf.posY.set(iNode, (grf.posY.get(iNode) / 20) * 20);
		}

		if (grm.dispGrid)
			grammarController.modify("snap to grid", false, false);

		grammarController.editor.repaint();

		try
		{
			this.dialog.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}
}