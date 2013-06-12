package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class AboutNooJShell extends JInternalFrame {
	private static final long serialVersionUID = -3830956614479934675L;
	/**
	 * Launch the application.
	 */
	
	
	/**
	 * Create the frame.
	 */
	public AboutNooJShell() {
		setIconifiable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 539, 327);
		setTitle("BETA version v0.1");

		JTextArea txtConsole = new JTextArea();
		txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtConsole.setForeground(Color.BLACK);
		txtConsole.setBackground(UIManager.getColor("Button.background"));
		JScrollPane scrollPane = new JScrollPane(txtConsole);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		

		txtConsole.setText("NooJ is Open Source (GPL Affero) freeware.\r\n\r\nGo to www.nooj4nlp to download source files.\r\n\r\nThank you for your interest in NooJ!\r\n\r\nDo not hesitate to ask for enhancements in any area: software / linguistic / educational content, etc.\r\n\r\nPlease report any software bug, linguistic error or interface glitch to max.silberztein@univ-fcomte.fr.\r\n\r\nDo not use any of its resources without their author's permission and proper citation.");

	}

}
