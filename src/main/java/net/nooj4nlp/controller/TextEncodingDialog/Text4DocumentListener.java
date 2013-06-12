package net.nooj4nlp.controller.TextEncodingDialog;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.nooj4nlp.engine.BasicConversion;
import net.nooj4nlp.gui.dialogs.TextEncodingDialog;

public class Text4DocumentListener implements DocumentListener {
	private JTextField fldHexadecimal, fldDecimal, fldBinary, textField_3;

	public Text4DocumentListener(JTextField fldHexadecimal,
			JTextField fldDecimal, JTextField fldBinary, JTextField textField_3) {
		super();
		this.fldHexadecimal = fldHexadecimal;
		this.fldDecimal = fldDecimal;
		this.fldBinary = fldBinary;
		this.textField_3 = textField_3;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		warn();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		warn();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		warn();
	}
	
	private void warn() {
		fldBinary.getDocument().removeDocumentListener(TextEncodingDialog.listener1);
		fldDecimal.getDocument().removeDocumentListener(TextEncodingDialog.listener2);
		fldHexadecimal.getDocument().removeDocumentListener(TextEncodingDialog.listener3);
		
		String text = textField_3.getText();
        if (text.isEmpty()) {
        	fldHexadecimal.setText("");
        	fldBinary.setText("");
        	fldDecimal.setText("");        	
        }
        else {
        	char c = text.charAt(0);
        	int nb = c;
        
        	fldHexadecimal.setText(BasicConversion.dectohex(nb));
        	fldBinary.setText(BasicConversion.dectobin(nb));
        	fldDecimal.setText(Integer.toString(nb));
        }
		
		fldBinary.getDocument().addDocumentListener(TextEncodingDialog.listener1);
		fldDecimal.getDocument().addDocumentListener(TextEncodingDialog.listener2);
		fldHexadecimal.getDocument().addDocumentListener(TextEncodingDialog.listener3);
	}

}
