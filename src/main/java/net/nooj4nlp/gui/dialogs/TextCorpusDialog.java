package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.TextCorpusDialog.TextNewActionListener;
import net.nooj4nlp.controller.TextEditorShell.ImportTextActionListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.SystemEnvironment;
import net.nooj4nlp.gui.actions.shells.construct.NewTextCorpusActionListener;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Dialog for creating a new text or corpus
 * 
 */
public class TextCorpusDialog extends JDialog
{
	private static final long serialVersionUID = 8534560794082502310L;

	private final JPanel contentPanel = new JPanel();
	private JList listLanguages;
	private JRadioButton rdbtnAsciiUnicode;
	private static JRadioButton rdbtnOtherRawText;
	private JRadioButton rdbtnRichTextFormat, rdbtnHtmlPage, rdbtnPdfDocument, rdbtndoc;
	private static JList listFFormats;
	private static JRadioButton rdbtnNoDelimiterwhole, rdbtnLineDelimiter, rdbtnPerlRegExpr, rdbtnXmlTextNodes;
	private static JComboBox comboPerl, comboXml;

	/**
	 * Creates the dialog.
	 * 
	 * @param corpus
	 *            - true initializes a new corpus, false creates a new text
	 * @param textImport
	 *            - true opens existing file, false creates a new one. If set to true, <b>fileToBeImported</b> must not
	 *            be null
	 * @param fileToBeImported
	 *            - file that is imported, null if it is being created
	 */
	public TextCorpusDialog(boolean corpus, boolean textImport, JInternalFrame frame, File fileToBeImported)
	{
		setBounds(100, 100, 494, 443);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel pnlLang = new JPanel();
		pnlLang.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(1) Select:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlLang.setBounds(10, 11, 119, 240);
		contentPanel.add(pnlLang);
		pnlLang.setLayout(null);

		listLanguages = new JList(Language.getAllLanguages());
		listLanguages.setSelectedValue(Launcher.preferences.deflanguage, false);
		listLanguages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listLanguages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JScrollPane scrollLanguages = new JScrollPane(listLanguages);
		scrollLanguages.setBounds(10, 24, 99, 205);
		pnlLang.add(scrollLanguages);

		JPanel pnlFileFormat = new JPanel();
		pnlFileFormat.setBorder(new TitledBorder(null, "(2) Enter file format:", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		pnlFileFormat.setBounds(139, 11, 329, 240);
		contentPanel.add(pnlFileFormat);
		pnlFileFormat.setLayout(null);

		ButtonGroup grpTextFormat = new ButtonGroup();
		rdbtnAsciiUnicode = new JRadioButton("<html>ASCII or Byte-Marked Unicode (UTF-8, UTF-16B or UTF-16L)</html>");
		rdbtnAsciiUnicode.setBounds(6, 18, 307, 37);
		pnlFileFormat.add(rdbtnAsciiUnicode);
		grpTextFormat.add(rdbtnAsciiUnicode);
		rdbtnAsciiUnicode.setSelected(true);
		rdbtnAsciiUnicode.addActionListener(new TextCorpusDialogEncodingActionListener());

		int n = SystemEnvironment.encodings.length;
		rdbtnOtherRawText = new JRadioButton("Other raw text formats(" + n + ")");
		rdbtnOtherRawText.setBounds(6, 58, 171, 23);
		pnlFileFormat.add(rdbtnOtherRawText);
		grpTextFormat.add(rdbtnOtherRawText);
		rdbtnOtherRawText.addActionListener(new TextCorpusDialogEncodingActionListener());

		DefaultListModel model = new DefaultListModel();
		listFFormats = new JList(model);
		listFFormats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listFFormats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JScrollPane scrollFileFormats = new JScrollPane(listFFormats);
		scrollFileFormats.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollFileFormats.setBounds(6, 88, 161, 141);
		pnlFileFormat.add(scrollFileFormats);
		for (int i = 0; i < n; i++)
		{
			String e = SystemEnvironment.encodings[i];
			Charset c = Charset.forName(e);
			e += c.aliases().toString();
			model.add(i, e);
		}
		listFFormats.setEnabled(false);
		scrollFileFormats.setEnabled(false);

		rdbtnRichTextFormat = new JRadioButton("Rich Text Format");
		rdbtnRichTextFormat.setBounds(183, 58, 146, 23);
		pnlFileFormat.add(rdbtnRichTextFormat);
		grpTextFormat.add(rdbtnRichTextFormat);
		rdbtnRichTextFormat.addActionListener(new TextCorpusDialogEncodingActionListener());

		rdbtnHtmlPage = new JRadioButton("HTML page");
		rdbtnHtmlPage.setBounds(183, 85, 119, 23);
		pnlFileFormat.add(rdbtnHtmlPage);
		grpTextFormat.add(rdbtnHtmlPage);
		rdbtnHtmlPage.addActionListener(new TextCorpusDialogEncodingActionListener());

		rdbtnPdfDocument = new JRadioButton("PDF document");
		rdbtnPdfDocument.setBounds(183, 111, 119, 23);
		pnlFileFormat.add(rdbtnPdfDocument);
		grpTextFormat.add(rdbtnPdfDocument);
		rdbtnPdfDocument.addActionListener(new TextCorpusDialogEncodingActionListener());

		rdbtndoc = new JRadioButton(".doc");
		rdbtndoc.setBounds(183, 137, 119, 23);
		pnlFileFormat.add(rdbtndoc);
		grpTextFormat.add(rdbtndoc);
		rdbtndoc.addActionListener(new TextCorpusDialogEncodingActionListener());

		JPanel pnlDelimiter = new JPanel();
		pnlDelimiter.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"(3) Enter Text Unit Delimiter:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlDelimiter.setBounds(10, 262, 374, 137);
		contentPanel.add(pnlDelimiter);
		pnlDelimiter.setLayout(null);

		ButtonGroup grpDelimiter = new ButtonGroup();

		rdbtnNoDelimiterwhole = new JRadioButton("No Delimiter (whole text is processed as one unit)");
		rdbtnNoDelimiterwhole.setBounds(6, 20, 270, 23);
		pnlDelimiter.add(rdbtnNoDelimiterwhole);
		grpDelimiter.add(rdbtnNoDelimiterwhole);
		if (Launcher.getCorpusTextRadioButtonSelectionMemory() == 1)
			rdbtnNoDelimiterwhole.setSelected(true);
		rdbtnNoDelimiterwhole.addActionListener(new TextCorpusDialogDelimiterActionListener());

		rdbtnLineDelimiter = new JRadioButton("Text Units are lines/paragraphs (\"\\n\")");
		rdbtnLineDelimiter.setBounds(6, 46, 270, 23);
		rdbtnLineDelimiter.setActionCommand("EOL");
		pnlDelimiter.add(rdbtnLineDelimiter);
		grpDelimiter.add(rdbtnLineDelimiter);
		if (Launcher.getCorpusTextRadioButtonSelectionMemory() == 2)
			rdbtnLineDelimiter.setSelected(true);
		rdbtnLineDelimiter.addActionListener(new TextCorpusDialogDelimiterActionListener());

		comboPerl = new JComboBox(Launcher.getRegexMemoryList().toArray());
		comboPerl.setEditable(true);
		comboPerl.setEnabled(false);
		comboPerl.setPrototypeDisplayValue("XXX");
		comboPerl.setBounds(121, 73, 243, 23);
		comboPerl.setSelectedIndex(-1);
		pnlDelimiter.add(comboPerl);

		comboXml = new JComboBox(Launcher.getXmlMemoryList().toArray());
		comboXml.setEditable(true);
		comboXml.setPrototypeDisplayValue("XXX");
		comboXml.setEnabled(false);
		comboXml.setBounds(121, 99, 243, 22);
		comboXml.setSelectedIndex(-1);
		pnlDelimiter.add(comboXml);

		rdbtnPerlRegExpr = new JRadioButton("PERL Reg. Exp.:");
		rdbtnPerlRegExpr.setBounds(6, 72, 109, 23);
		rdbtnPerlRegExpr.setActionCommand("Perl");
		pnlDelimiter.add(rdbtnPerlRegExpr);
		grpDelimiter.add(rdbtnPerlRegExpr);
		if (Launcher.getCorpusTextRadioButtonSelectionMemory() == 3)
		{
			rdbtnPerlRegExpr.setSelected(true);
			comboPerl.setEnabled(true);
			comboPerl.setSelectedIndex(Launcher.getRegexMemoryIndex());
		}
		rdbtnPerlRegExpr.addActionListener(new TextCorpusDialogDelimiterActionListener());

		rdbtnXmlTextNodes = new JRadioButton("XML Text Nodes:");
		rdbtnXmlTextNodes.setBounds(6, 98, 109, 23);
		rdbtnXmlTextNodes.setActionCommand("XML");
		pnlDelimiter.add(rdbtnXmlTextNodes);
		grpDelimiter.add(rdbtnXmlTextNodes);
		if (Launcher.getCorpusTextRadioButtonSelectionMemory() == 4)
		{
			rdbtnXmlTextNodes.setSelected(true);
			comboXml.setEnabled(true);
			comboXml.setSelectedIndex(Launcher.getXmlMemoryIndex());
		}
		rdbtnXmlTextNodes.addActionListener(new TextCorpusDialogDelimiterActionListener());

		final JButton okButton = new JButton("OK");
		okButton.setBounds(394, 361, 64, 23);
		contentPanel.add(okButton);
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);

		ActionListener actionListener;

		if (corpus)
		{
			setTitle(NewTextCorpusActionListener.getChosenFileName());

			actionListener = new ImportTextActionListener(listLanguages, this, true, fileToBeImported);
			okButton.addActionListener(actionListener);
		}
		else
		{
			if (textImport)
			{
				setTitle(NewTextCorpusActionListener.getChosenFileName());

				actionListener = new ImportTextActionListener(listLanguages, this, false, fileToBeImported);
				okButton.addActionListener(actionListener);
			}
			else
			{
				disableAllButDefaultEncoding();
				okButton.addActionListener(new TextNewActionListener(this));
			}
		}
	}

	private void disableAllButDefaultEncoding()
	{
		rdbtnAsciiUnicode.setEnabled(true);
		rdbtnAsciiUnicode.setSelected(true);

		TextCorpusDialog.rdbtnOtherRawText.setEnabled(false);
		rdbtnPdfDocument.setEnabled(false);
	
		listFFormats.setEnabled(false);

		rdbtnRichTextFormat.setEnabled(false);
		rdbtnHtmlPage.setEnabled(false);
		rdbtndoc.setEnabled(false);
	}

	// getters and setters
	public JList getListLanguages()
	{
		return listLanguages;
	}

	public JRadioButton getRdbtnAsciiUnicode()
	{
		return rdbtnAsciiUnicode;
	}

	public static JRadioButton getRdbtnOtherRawText()
	{
		return rdbtnOtherRawText;
	}

	public static JRadioButton getRdbtnNoDelimiterwhole()
	{
		return rdbtnNoDelimiterwhole;
	}

	public static JRadioButton getRdbtnLineDelimiter()
	{
		return rdbtnLineDelimiter;
	}

	public static JRadioButton getRdbtnPerlRegExpr()
	{
		return rdbtnPerlRegExpr;
	}

	public static JRadioButton getRdbtnXmlTextNodes()
	{
		return rdbtnXmlTextNodes;
	}

	public JRadioButton getRdbtndoc()
	{
		return rdbtndoc;
	}

	public JRadioButton getRdbtnRichTextFormat()
	{
		return rdbtnRichTextFormat;
	}

	public JRadioButton getRdbtnHtmlPage()
	{
		return rdbtnHtmlPage;
	}

	public JRadioButton getRdbtnPdfDocument()
	{
		return rdbtnPdfDocument;
	}

	public static JList getListFFormats()
	{
		return listFFormats;
	}

	public static JComboBox getComboPerl()
	{
		return comboPerl;
	}

	public static JComboBox getComboXml()
	{
		return comboXml;
	}
}

class TextCorpusDialogEncodingActionListener implements ActionListener
{
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == TextCorpusDialog.getRdbtnOtherRawText())
		{
			TextCorpusDialog.getListFFormats().setEnabled(true);
			TextCorpusDialog.getListFFormats().setSelectedIndex(0);
		}
		else
			TextCorpusDialog.getListFFormats().setEnabled(false);
	}
}

class TextCorpusDialogDelimiterActionListener implements ActionListener
{
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == TextCorpusDialog.getRdbtnPerlRegExpr())
		{
			TextCorpusDialog.getComboPerl().setEnabled(true);
			TextCorpusDialog.getComboXml().setEnabled(false);
		}
		else if (evt.getSource() == TextCorpusDialog.getRdbtnXmlTextNodes())
		{
			TextCorpusDialog.getComboPerl().setEnabled(false);
			TextCorpusDialog.getComboXml().setEnabled(true);
		}
		else
		{
			TextCorpusDialog.getComboPerl().setEnabled(false);
			TextCorpusDialog.getComboXml().setEnabled(false);
		}
	}
}