package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.nio.charset.Charset;

import javax.swing.DefaultListModel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.TextEncodingDialog.Text1DocumentListener;
import net.nooj4nlp.controller.TextEncodingDialog.Text2DocumentListener;
import net.nooj4nlp.controller.TextEncodingDialog.Text3DocumentListener;
import net.nooj4nlp.controller.TextEncodingDialog.Text4DocumentListener;
import net.nooj4nlp.controller.TextEncodingDialog.TextDocumentListener;
import net.nooj4nlp.engine.SystemEnvironment;
import net.nooj4nlp.gui.components.JBinaryTextField;
import net.nooj4nlp.gui.components.JDecimalTextField;
import net.nooj4nlp.gui.components.JHexdecimalTextField;

/**
 * 
 * Text encoding lab dialog
 * 
 */
public class TextEncodingDialog extends JInternalFrame
{
	private static final long serialVersionUID = -2984243350288280598L;

	private JHexdecimalTextField fldHexadecimal;
	private JDecimalTextField fldDecimal;
	private JBinaryTextField fldBinary;
	private JTextField textField_3;
	private JTextField fldText;
	private JScrollPane scrollPane;
	public static Text1DocumentListener listener1;
	public static Text2DocumentListener listener2;
	public static Text3DocumentListener listener3;
	public static Text4DocumentListener listener4;

	/**
	 * Creates the frame.
	 */
	public TextEncodingDialog()
	{
		setTitle("Text Encoding");
		setClosable(true);
		setIconifiable(true);
		getContentPane().setFont(new Font("Tahoma", Font.BOLD, 18));
		setBounds(400, 100, 750, 400);
		getContentPane().setLayout(new MigLayout("insets 5", "[300!][410!]", "[100!][15!][40!][15!][50!][15!][grow][5]"));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Base conversion & encoding:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
	
		getContentPane().add(panel, "cell 0 0, span, growx");

		panel.setLayout(new MigLayout("insets 3", "[130!][85!][95!][325!][45!]", "[20!][25!][5]"));

		JLabel lblDecimalValue = new JLabel("Decimal value:");
		
		panel.add(lblDecimalValue, "cell 0 0");

		fldDecimal = new JDecimalTextField();
		
		panel.add(fldDecimal, "cell 1 0");
		fldDecimal.setColumns(10);

		JLabel lblHexadecimalValue = new JLabel("Hexadecimal value:");
	
		panel.add(lblHexadecimalValue, "cell 0 1");

		fldHexadecimal = new JHexdecimalTextField();
		
		panel.add(fldHexadecimal, "cell 1 1");
		fldHexadecimal.setColumns(10);

		JLabel lblBinaryValue = new JLabel("Binary value:");
		
		panel.add(lblBinaryValue, "cell 2 0, gapleft 10");

		fldBinary = new JBinaryTextField();
	
		panel.add(fldBinary, "cell 3 0, span 2, growx");
		fldBinary.setColumns(10);

		JLabel lblInUnicodeie = new JLabel("In Unicode (i.e. UTF32 Big Endian), this value encodes a character:");
	
		panel.add(lblInUnicodeie, "cell 2 1, span 2, gapleft 10");

		textField_3 = new JTextField();
		textField_3.setFont(new Font("Tahoma", Font.BOLD, 18));
		textField_3.setForeground(Color.RED);
		
		panel.add(textField_3, "cell 4 1, hmin 20");
		textField_3.setColumns(10);

		// Adding listeners
		listener1 = new Text1DocumentListener(fldHexadecimal, fldDecimal, fldBinary, textField_3);
		listener2 = new Text2DocumentListener(fldHexadecimal, fldDecimal, fldBinary, textField_3);
		listener3 = new Text3DocumentListener(fldHexadecimal, fldDecimal, fldBinary, textField_3);
		listener4 = new Text4DocumentListener(fldHexadecimal, fldDecimal, fldBinary, textField_3);
		fldBinary.getDocument().addDocumentListener(listener1);
		fldDecimal.getDocument().addDocumentListener(listener2);
		fldHexadecimal.getDocument().addDocumentListener(listener3);
		textField_3.getDocument().addDocumentListener(listener4);

		JLabel lblChooseA = new JLabel("(1) Choose a text encoding (among " + SystemEnvironment.encodings.length + "):");
		lblChooseA.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		getContentPane().add(lblChooseA, "cell 0 1");

		DefaultListModel model = new DefaultListModel();
		JList listEncoding = new JList(model);
		for (int i = 0; i < SystemEnvironment.encodings.length; i++)
		{
			String e = SystemEnvironment.encodings[i];
			Charset c = Charset.forName(e);
			e += c.aliases().toString();
			model.add(i, e);
		}
		scrollPane = new JScrollPane(listEncoding, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		getContentPane().add(scrollPane, "cell 0 2, span 1 5, grow 1");

		JLabel lblEnterA = new JLabel("(2) Enter a text:");
		lblEnterA.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		getContentPane().add(lblEnterA, "cell 1 1");

		fldText = new JTextField();
		
		getContentPane().add(fldText, "cell 1 2, grow");
		fldText.setColumns(10);

		JLabel lblValuedecimal = new JLabel("Value (decimal):");
		
		getContentPane().add(lblValuedecimal, "cell 1 3");

		JTextArea fldValDecimal = new JTextArea();
		fldValDecimal.setBackground(new Color(227, 227, 227));
		fldValDecimal.setForeground(Color.RED);
		
		fldValDecimal.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		fldValDecimal.setLineWrap(true);
		fldValDecimal.setWrapStyleWord(true);
		getContentPane().add(fldValDecimal, "cell 1 4, grow");

		JLabel lblValuehexadecimal = new JLabel("Value (hexadecimal):");
	
		getContentPane().add(lblValuehexadecimal, "cell 1 5");

		JTextArea fldValHexadecimal = new JTextArea();
		fldValHexadecimal.setForeground(Color.RED);
		
		fldValHexadecimal.setLineWrap(true);
		fldValHexadecimal.setWrapStyleWord(true);
		getContentPane().add(fldValHexadecimal, "cell 1 6, grow");
		fldValHexadecimal.setBackground(new Color(227, 227, 227));
		fldValHexadecimal.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		fldText.getDocument().addDocumentListener(
				new TextDocumentListener(listEncoding, fldText, fldValDecimal, fldValHexadecimal));

	}
}
