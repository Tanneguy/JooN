package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.PropDefEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.PropDefEditorShell.PropDefEditorShellController;
import net.nooj4nlp.controller.PropDefEditorShell.TextDocumentListener;
import net.nooj4nlp.gui.components.TextLineNumber;

public class PropDefEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = 8333659233288899598L;

	private JTextPane textPane;
	private PropDefEditorShellController controller;

	public PropDefEditorShell()
	{
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 750, 500);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("ins 7", "[grow,fill,left][50::,right]", "10![]10![grow, fill]"));

		JLabel lblLineNo = new JLabel("Ln n");
		panel.add(lblLineNo, "skip");

		JLabel lblColNo = new JLabel("Col n");
		panel.add(lblColNo, "gapleft 30, wrap");

		textPane = new JTextPane();
		textPane.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(textPane);
		JScrollPane scrollPane = new JScrollPane(noWrapPanel);
		panel.add(scrollPane, "span");
	

		getContentPane().add(panel);

		controller = new PropDefEditorShellController(textPane, this);

		textPane.getDocument().addDocumentListener(new TextDocumentListener(controller));
		this.addInternalFrameListener(new CloseInternalFrameListener(controller));
	}

	public PropDefEditorShellController getController()
	{
		return controller;
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}
}