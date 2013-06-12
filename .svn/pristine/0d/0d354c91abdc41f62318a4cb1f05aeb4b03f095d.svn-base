package net.nooj4nlp.gui.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JDecimalTextField extends JTextField {
	
	private static final long serialVersionUID = 1L;

	public JDecimalTextField() {
        super();
    }
    
    protected Document createDefaultModel() {
    	return new DecimalDocument();
    }

    static class DecimalDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a) 
            throws BadLocationException {

            if (str == null) {
                return;
            }
            str = str.replaceAll("[^0-9]", ""); 
            char[] s = str.toCharArray();
            super.insertString(offs, new String(s), a);
        }
    }
}