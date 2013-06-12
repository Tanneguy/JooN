package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;

import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlActionListener;
import net.nooj4nlp.controller.LocateDialog.LocateDialogTextActionListener;
import net.nooj4nlp.gui.actions.shells.modify.ModifyTextActionListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;

/**
 * Pop up menu for right click event in Open Text Dialog.
 */
public class RightClickPopupMenuForText extends MouseAdapter
{
	private TextEditorShellController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of a text shell
	 */
	public RightClickPopupMenuForText(TextEditorShellController controller)
	{
		this.controller = controller;
	}

	/**
	 * If the right mouse button is clicked, show pop up menu.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			PopUpText menu = new PopUpText(controller);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Set text cursor for text pane.
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		Component component = e.getComponent();
		if (component.getClass().getName().equals(JTextPane.class.getName()))
			CursorChangeEffect.setIBeamCursor(component);
	}
}

/**
 * Help class for creating pop up menu that triggers when right mouse button is clicked in Open text dialog.
 * 
 */
class PopUpText extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	public PopUpText(TextEditorShellController controller)
	{
		// create the same items that are already in TEXT menu
		JMenuItem mntmLingAnalysis = new JMenuItem("Linguistic Analysis");
		add(mntmLingAnalysis);
		mntmLingAnalysis.addActionListener(new TextLinguisticAnalysisActionListener(controller));

		JMenuItem mntmLocate = new JMenuItem("Locate");
		add(mntmLocate);
		mntmLocate.addActionListener(new LocateDialogTextActionListener(controller, null));

		JSeparator separator_1 = new JSeparator();
		add(separator_1);

		JMenuItem mntmExportColored = new JMenuItem("Export colored text as an RTF document");
		add(mntmExportColored);

		JMenuItem mntmExportAnnotated = new JMenuItem("Export annotated text as an XML document");
		add(mntmExportAnnotated);
		mntmExportAnnotated.addActionListener(new ExportXmlActionListener(controller));

		JSeparator separator_2 = new JSeparator();
		add(separator_2);

		JMenuItem mntmModify = new JMenuItem("Modify Text");
		add(mntmModify);

		mntmModify.addActionListener(new ModifyTextActionListener(controller));
	}
}
