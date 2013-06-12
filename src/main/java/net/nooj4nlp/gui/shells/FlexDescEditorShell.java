package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.FlexDescEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.FlexDescEditorShell.FlexDescEditorShellController;
import net.nooj4nlp.controller.FlexDescEditorShell.SyntaxColorActionListener;
import net.nooj4nlp.controller.FlexDescEditorShell.TextDocumentListener;
import net.nooj4nlp.gui.actions.shells.modify.ContextMenuMouseListener;
import net.nooj4nlp.gui.components.TextLineNumber;

public class FlexDescEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = -8122529763446499249L;

	private JTextPane textPane;
	private JPopupMenu popText;
	private JMenuItem menuItem;

	private FlexDescEditorShellController controller;

	public FlexDescEditorShell()
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

		popText = new JPopupMenu();
		menuItem = new JMenuItem("Syntax Color");
		popText.add(menuItem);

		Style style = textPane.addStyle("black", null);
		StyleConstants.setForeground(style, Color.black);
		Style style1 = textPane.addStyle("red", null);
		StyleConstants.setForeground(style1, Color.red);
		Style style2 = textPane.addStyle("green", null);
		StyleConstants.setForeground(style2, Color.green);
		Style style3 = textPane.addStyle("blue", null);
		StyleConstants.setForeground(style3, Color.blue);
		Style style4 = textPane.addStyle("orange", null);
		StyleConstants.setForeground(style4, Color.orange);
		Style style5 = textPane.addStyle("gray", null);
		StyleConstants.setForeground(style5, Color.darkGray);

		getContentPane().addMouseListener(new ContextMenuMouseListener(popText));
		textPane.addMouseListener(new ContextMenuMouseListener(popText));

		controller = new FlexDescEditorShellController(textPane, this);

		textPane.getDocument().addDocumentListener(new TextDocumentListener(controller));
		this.addInternalFrameListener(new CloseInternalFrameListener(controller));
		menuItem.addActionListener(new SyntaxColorActionListener(controller));
	}

	public void modifyFont(String fontFamily, int fontSize)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();

		StyleConstants.setFontFamily(attr, fontFamily);
		StyleConstants.setFontSize(attr, fontSize);
		StyleConstants.setForeground(attr, Color.darkGray);

		int textLength = textPane.getText().length();
		textPane.getStyledDocument().setParagraphAttributes(0, textLength, attr, false);

		textPane.repaint();
	}

	public FlexDescEditorShellController getController()
	{
		return controller;
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}
}