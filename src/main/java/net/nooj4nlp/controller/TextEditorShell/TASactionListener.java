package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.nooj4nlp.gui.components.CursorChangeEffect;

/**
 * Function shows/hides text annotation structure (TAS) in Text Editor Shell.
 * 
 */

public class TASactionListener implements ActionListener
{
	// components from Text Editor Shell
	private JCheckBox tasCheckBox;
	private JScrollPane textScroll;
	private JScrollPane tasScroll;
	private Container container;
	private JSplitPane splitPane;

	private TextEditorShellController textController;

	// variable responsible for remembering last divider position
	private int dividerActualPosition = 0;

	/**
	 * Constructor.
	 * 
	 * @param container
	 *            - container of Text Editor Shell
	 * @param tasCheckBox
	 *            - check box of Text Editor Shell
	 * @param splitPane
	 *            - TAS split pane
	 * @param textScroll
	 *            - text scroll pane of Text Editor Shell
	 * @param tasScroll
	 *            - TAS scroll pane of Text Editor Shell
	 */

	public TASactionListener(Container container, JCheckBox tasCheckBox, JSplitPane splitPane, JScrollPane textScroll,
			JScrollPane tasScroll, TextEditorShellController textController)
	{
		this.tasCheckBox = tasCheckBox;
		this.textScroll = textScroll;
		this.tasScroll = tasScroll;
		this.container = container;
		this.splitPane = splitPane;
		this.textController = textController;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		start(container, tasCheckBox, splitPane, textScroll, tasScroll, textController);
	}

	public void start(Container container, JCheckBox tasCheckBox, JSplitPane splitPane, JScrollPane textScroll,
			JScrollPane tasScroll, TextEditorShellController textController)
	{
		// if TAS needs to be shown...
		if (tasCheckBox.isSelected())
		{
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

				// hide the text and remove it from the container
				textScroll.setVisible(false);
				container.remove(textScroll);

				// set split pane's data
				splitPane.setTopComponent(textScroll);
				splitPane.setBottomComponent(tasScroll);

				// show text and split pane, and if it's not the first click on the check box, remember
				// actual divider position
				textScroll.setVisible(true);
				splitPane.setVisible(true);
				if (dividerActualPosition != 0)
					splitPane.setDividerLocation(dividerActualPosition);

				// Set up tuGraph
				textController.setTuGraph((Integer) textController.getTextShell().getSpinner().getValue());
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}

		// if TAS needs to be hidden
		else
		{
			// get dividers actual position
			dividerActualPosition = splitPane.getDividerLocation();

			// hide the text and remove split pane's data
			textScroll.setVisible(false);
			splitPane.remove(textScroll);
			splitPane.remove(tasScroll);

			// hide split pane, but show the text and add it to the container
			splitPane.setVisible(false);
			textScroll.setVisible(true);
			container.add(textScroll, "cell 0 3, span 3, grow, hidemode 2");
		}

		// revalidate changes and repaint
		container.invalidate();
		container.validate();
		container.repaint();
	}
}