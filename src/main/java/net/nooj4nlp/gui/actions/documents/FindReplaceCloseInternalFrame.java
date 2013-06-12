package net.nooj4nlp.gui.actions.documents;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.controller.FlexDescEditorShell.FlexDescEditorShellController;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.PropDefEditorShell.PropDefEditorShellController;
import net.nooj4nlp.gui.dialogs.DictionaryDialog;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * Class implements On Close events of Find/Replace dialog.
 * 
 */
public class FindReplaceCloseInternalFrame implements InternalFrameListener
{
	private JInternalFrame activeFrame;

	/**
	 * Constructor.
	 * 
	 * @param activeFrame
	 *            - frame in which we are searching desired pattern
	 */
	public FindReplaceCloseInternalFrame(JInternalFrame activeFrame)
	{
		this.activeFrame = activeFrame;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		// remove pointer in active frame
		String cls = activeFrame.getClass().getSimpleName();

		if (cls.equals("TextEditorShell"))
		{
			TextEditorShell textShell = (TextEditorShell) activeFrame;
			textShell.setFindReplaceDialog(null);
		}
		else if (cls.equals("DictionaryEditorShell"))
		{
			DictionaryEditorShellController dicController = ((DictionaryEditorShell) activeFrame).getController();
			dicController.setFindReplaceDialog(null);
		}
		else if (cls.equals("FlexDescEditorShell"))
		{
			FlexDescEditorShellController flexDescController = ((FlexDescEditorShell) activeFrame).getController();
			flexDescController.setFindReplaceDialog(null);
		}
		else if (cls.equals("PropDefEditorShell"))
		{
			PropDefEditorShellController propDefController = ((PropDefEditorShell) activeFrame).getController();
			propDefController.setFindReplaceDialog(null);
		}
		else if (cls.equals("GrammarEditorShell"))
		{
			GrammarEditorShellController grammarController = ((GrammarEditorShell) activeFrame).getController();
			grammarController.setFindReplaceDialog(null);
		}
		else if (cls.equals("DictionaryDialog"))
		{
			DictionaryDialogController dicDialogController = ((DictionaryDialog) activeFrame).getController();
			dicDialogController.setFindReplaceDialog(null);
		}
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
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