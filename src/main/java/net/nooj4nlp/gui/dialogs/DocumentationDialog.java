package net.nooj4nlp.gui.dialogs;

import java.awt.Font;
import java.io.IOException;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class DocumentationDialog extends JInternalFrame
{
	private static final long serialVersionUID = -5718053523008388705L;

	private JTextArea textDocumentation;

	public DocumentationDialog(String rtfPath) throws IOException, BadLocationException
	{
		setBounds(100, 100, 453, 402);
		getContentPane().setLayout(null);

		// Frame shouldn't have the option to be minimized/maximized/closed - closing is done from menu!
		setClosable(true);
		setIconifiable(false);
		setMaximizable(false);

		setTitle("Documentation");

		textDocumentation = new JTextArea();
		textDocumentation.setFont(new Font("Courier New", Font.BOLD, 10));

		String readText = net.nooj4nlp.engine.TextIO.loadRtfFile(rtfPath);
		textDocumentation.setText(readText);
		textDocumentation.setEditable(true);

		JScrollPane scrollDocumentation = new JScrollPane(textDocumentation);
		scrollDocumentation.setBounds(10, 10, 420, 350);
		getContentPane().add(scrollDocumentation);
	}
}
