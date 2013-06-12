package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * Console shell
 * 
 */
public class ConsoleShell extends JInternalFrame
{
	private static final long serialVersionUID = -3830956614479934675L;

	/**
	 * Create the frame.
	 */
	public ConsoleShell()
	{
		setIconifiable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 539, 327);
		setTitle("Console");

		JTextArea txtConsole = new JTextArea();
		txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtConsole.setForeground(Color.GREEN);
		txtConsole.setBackground(Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(txtConsole);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, hh:mm:ss");
		Date date = new Date();

		txtConsole.setText("NooJ Java BETA version v0.1, " + dateFormat.format(date));
	}
}