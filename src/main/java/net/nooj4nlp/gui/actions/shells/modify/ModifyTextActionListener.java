package net.nooj4nlp.gui.actions.shells.modify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;
import javax.swing.event.CaretListener;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.components.CustomJSpinner;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * 
 * ActionListener that enables/disables text input and makes text units selectable
 * 
 */
public class ModifyTextActionListener implements ActionListener
{
	// controller and components
	private TextEditorShellController controller;
	private JTextPane textPane;
	private TextEditorShell textShell;

	// loaded Ntext and text
	private String currentBufferedText;
	private Ntext myText;

	/**
	 * 
	 * @param tp
	 *            text pane to enable/disable
	 */
	public ModifyTextActionListener(TextEditorShellController controller)
	{
		this.controller = controller;
		
		this.textShell = this.controller.getTextShell();
		this.textPane = this.textShell.getTextPane();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// remove action listener if it has been added
		CaretListener[] listeners = textPane.getCaretListeners();
		for (CaretListener listener : listeners)
			textPane.removeCaretListener(listener);

		// get Ntext
		this.myText = controller.getMyText();

		// enable text editing and set loaded text
		textPane.setEditable(true);
		currentBufferedText = this.controller.getMyText().buffer;
		textPane.setText(currentBufferedText);

		// if spinner is enabled, disable it and set custom value for it
		CustomJSpinner spinner = textShell.getSpinner();
		if (spinner.isEnabled())
			spinner.setEnabled(false);

		// clear mft an annotations
		myText.mft = null;
		myText.annotations = null;
		controller.textHasJustBeenEdited();
	}
}