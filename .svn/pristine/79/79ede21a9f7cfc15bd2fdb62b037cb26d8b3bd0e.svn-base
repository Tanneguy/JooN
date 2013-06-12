package net.nooj4nlp.controller.TextEncodingDialog;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.nooj4nlp.engine.BasicConversion;
import net.nooj4nlp.gui.dialogs.TextEncodingDialog;

public class Text1DocumentListener implements
		DocumentListener {
	
	private JTextField fldHexadecimal, fldDecimal, fldBinary, textField_3;

	public Text1DocumentListener(JTextField fldHexadecimal,
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
		fldDecimal.getDocument().removeDocumentListener(TextEncodingDialog.listener2);
		fldHexadecimal.getDocument().removeDocumentListener(TextEncodingDialog.listener3);
		textField_3.getDocument().removeDocumentListener(TextEncodingDialog.listener4);
		
		String binval = fldBinary.getText();
        if (binval.isEmpty()) {
            fldDecimal.setText("");
            fldHexadecimal.setText("");
            textField_3.setText("");        	
        }
        else {
        	fldDecimal.setText(BasicConversion.bintodec(fldBinary.getText()));
        	fldHexadecimal.setText(BasicConversion.bintohex(fldBinary.getText()));
        	textField_3.setText(Character.toString((char)Integer.parseInt(fldDecimal.getText())));
        }
        
		fldDecimal.getDocument().addDocumentListener(TextEncodingDialog.listener2);
		fldHexadecimal.getDocument().addDocumentListener(TextEncodingDialog.listener3);
		textField_3.getDocument().addDocumentListener(TextEncodingDialog.listener4);

	}

}
