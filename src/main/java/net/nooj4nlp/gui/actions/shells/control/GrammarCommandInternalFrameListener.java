package net.nooj4nlp.gui.actions.shells.control;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * InternalFrameListener that adds a GRAMMAR menu to the menu bar each time a grammar editor is activated, and removes
 * it whenever the editor loses focus
 * 
 */
public class GrammarCommandInternalFrameListener implements InternalFrameListener
{
	private JMenuBar menuBar;

	private GrammarEditorShellController controller;

	public GrammarCommandInternalFrameListener(GrammarEditorShellController ctrl)
	{
		controller = ctrl;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
		menuBar = Launcher.getMenuBar();
		controller.editor.grammarMenu.setVisible(true);
		menuBar.add(controller.editor.grammarMenu);
		Launcher.mnEdit.setVisible(false);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
		Launcher.mnEdit.setVisible(true);
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		FindReplaceDialog findReplaceDialog = controller.getFindReplaceDialog();
		if (findReplaceDialog != null)
		{
			findReplaceDialog.dispose();
			controller.setFindReplaceDialog(null);
		}

		if (controller.dialogHistory != null)
		{
			controller.dialogHistory.dispose();
		}
		if (controller.contractShell != null)
		{
			controller.contractShell.dispose();
		}
		if (controller.transformationDialog != null)
		{
			controller.transformationDialog.dispose();
		}
		if (controller.formGramStruct != null)
		{
			controller.formGramStruct.dispose();
		}
		if (controller.alignmentDialog != null)
			controller.alignmentDialog.dispose();

		if (controller.presentationDialog != null)
			controller.presentationDialog.dispose();

		if (controller.debugShell != null)
			controller.debugShell.dispose();

		if (controller.modified && !Launcher.projectMode)
		{
			int dr = JOptionPane.showOptionDialog(Launcher.getDesktopPane(), "Save grammar file?",
					"NooJ: grammar file has not been saved", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, null, null);
			if (dr == JOptionPane.YES_OPTION)
				controller.saveGrammar();
		}
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
		controller.editor.grammarMenu.setVisible(false);
		menuBar.repaint();
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}
}