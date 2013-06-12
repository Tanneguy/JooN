package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * Class for parsing values from Text Boxes and passing them to the controller.
 * 
 */
public class TextBoxConcordanceActionListener extends KeyAdapter
{
	private JTextField beforeTF;
	private JTextField afterTF;
	private ConcordanceShellController controller;

	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			reset(beforeTF, afterTF);
	}

	/**
	 * Constructor.
	 * 
	 * @param beforeTF
	 *            - text box before
	 * @param afterTF
	 *            - text box after
	 */
	public TextBoxConcordanceActionListener(JTextField beforeTF, JTextField afterTF,
			ConcordanceShellController controller)
	{
		this.beforeTF = beforeTF;
		this.afterTF = afterTF;
		this.controller = controller;
	}

	/**
	 * Function resets values of concordance's text fields and sets their value in controller.
	 * 
	 * @param beforeTF
	 *            - text field "before"
	 * @param afterTF
	 *            - text field "after"
	 */
	public void reset(JTextField beforeTF, JTextField afterTF)
	{
		try
		{
			ConcordanceShellController.setBefore(Integer.valueOf(beforeTF.getText()));
		}
		catch (Exception f)
		{
			ConcordanceShellController.setBefore(20);
			beforeTF.setText(Integer.toString(20));
		}

		try
		{
			ConcordanceShellController.setAfter(Integer.valueOf(afterTF.getText()));
		}
		catch (Exception f)
		{
			ConcordanceShellController.setAfter(60);
			afterTF.setText(Integer.toString(60));
		}

		controller.refreshConcordance();
		
	}
}
