package net.nooj4nlp.controller.TextEncodingDialog;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.nooj4nlp.engine.BasicConversion;
import net.nooj4nlp.gui.main.Launcher;

public class TextDocumentListener implements DocumentListener
{

	private JList listEncoding;
	private JTextField fldText;
	private JTextArea fldValDecimal, fldValHexadecimal;

	public TextDocumentListener(JList listEncoding, JTextField fldText, JTextArea fldValDecimal,
			JTextArea fldValHexadecimal)
	{
		this.listEncoding = listEncoding;
		this.fldText = fldText;
		this.fldValDecimal = fldValDecimal;
		this.fldValHexadecimal = fldValHexadecimal;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0)
	{
		warn();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0)
	{
		warn();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0)
	{
		warn();
	}

	private void warn()
	{
		String encoding = (String) listEncoding.getSelectedValue();
		if (encoding == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please select an encoding", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			fldText.setText("");
			return;
		}
		Charset enc = Charset.forName(encoding.substring(0, encoding.indexOf('[')));
		String text = fldText.getText();
		fldValDecimal.setText("");
		fldValHexadecimal.setText("");

		for (int it = 0; it < text.length(); it++)
		{
			String cars = Character.toString(text.charAt(it));
			ByteBuffer byteBuf = enc.encode(cars);
			byte[] buf = byteBuf.array();

			for (int ib = 0; ib < buf.length; ib++)
			{
				int x = (buf[ib]) & 0xff;
				String dec = Integer.toString(x);
				fldValDecimal.append(dec + " ");

				String hex = BasicConversion.dectohex(x);
				fldValHexadecimal.append(hex + " ");
			}
			if (it < text.length() - 1)
			{
				fldValDecimal.append("- ");
				fldValHexadecimal.append("- ");
			}
		}
	}
}
