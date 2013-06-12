package net.nooj4nlp.controller.PerlRegexDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClearActionListener implements ActionListener {

	private JTextPane pane;
	
	public ClearActionListener(JTextPane p) {
		pane = p;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		StyledDocument doc = pane.getStyledDocument();
        Style style = pane.addStyle("Color", null);
     	StyleConstants.setForeground(style, Color.black);
     	StyleConstants.setBold(style, false);
     	doc.setCharacterAttributes(0, pane.getText().length(), pane.getStyle("Color"), true);
		pane.setCaretPosition(0);
	}

}
