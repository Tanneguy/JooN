package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * Console shell
 * 
 */
public class ErrorShell extends JInternalFrame
{
	private static final long serialVersionUID = 4797367697324585929L;

	private JTextArea txtError;

	/**
	 * Create the frame.
	 */
	public ErrorShell()
	{
		setIconifiable(true);
		setResizable(true);
		setMaximizable(true);
		setClosable(false);
		setBounds(100, 100, 539, 327);
		setTitle("Errors");

		txtError = new JTextArea();
		txtError.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtError.setForeground(Color.RED);
		
		JScrollPane scrollPane = new JScrollPane(txtError);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public JTextArea getTxtError()
	{
		return txtError;
	}
}