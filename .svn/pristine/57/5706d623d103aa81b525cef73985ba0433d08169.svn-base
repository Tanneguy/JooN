package net.nooj4nlp.controller.TextEncodingDialog;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.nooj4nlp.engine.BasicConversion;
import net.nooj4nlp.gui.dialogs.TextEncodingDialog;

public class Text2DocumentListener implements
		DocumentListener {

	private JTextField fldHexadecimal, fldDecimal, fldBinary, textField_3;

	public Text2DocumentListener(JTextField fldHexadecimal,
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
		fldHexadecimal.getDocument().removeDocumentListener(TextEncodingDialog.listener3);
		textField_3.getDocument().removeDocumentListener(TextEncodingDialog.listener4);
		
		String decval = fldDecimal.getText();
		if (decval.isEmpty()) {
			textField_3.setText("");
			fldBinary.setText("");
			fldHexadecimal.setText("");
		}
		else {
			int val = 0;
			try {
				val = Integer.parseInt(fldDecimal.getText());

			} catch (NumberFormatException e) {
				textField_3.setText("");
				fldBinary.setText("");
				fldHexadecimal.setText("");
			}

			textField_3.setText(Character.toString((char) val));
			fldBinary.setText(BasicConversion.dectobin(val));
			fldHexadecimal.setText(BasicConversion.dectohex(val));
		}
		
		fldBinary.getDocument().addDocumentListener(TextEncodingDialog.listener1);
		fldHexadecimal.getDocument().addDocumentListener(TextEncodingDialog.listener3);
		textField_3.getDocument().addDocumentListener(TextEncodingDialog.listener4);
	}

}
