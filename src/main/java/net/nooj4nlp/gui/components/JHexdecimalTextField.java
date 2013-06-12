package net.nooj4nlp.gui.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JHexdecimalTextField extends JTextField {
	
	private static final long serialVersionUID = 1L;

	public JHexdecimalTextField() {
        super();
    }
    
    protected Document createDefaultModel() {
    	return new HexdecimalDocument();
    }

    static class HexdecimalDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a) 
            throws BadLocationException {

            if (str == null) {
                return;
            }
            str = str.replaceAll("[^0-9a-fA-F]", ""); 
            char[] s = str.toCharArray();
            super.insertString(offs, new String(s), a);
        }
    }
}