package net.nooj4nlp.gui.dialogs;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog.ExportAnnotatedToXmlButtonActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog.XmlAnnotationsListener;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.main.Launcher;

public class ExportXmlDialog extends JInternalFrame
{
	private static final long serialVersionUID = -4146892505648063492L;

	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;

	private boolean isCorpus;

	private JTextPane textPane;
	private JRadioButton rdbtnAllSyntax;
	private JRadioButton rdbtnList;
	private JComboBox cobxXmlAnnotations;
	private JCheckBox chbxOnly;
	private JButton btnExport;

	public ExportXmlDialog(CorpusEditorShellController corpusController, TextEditorShellController textController,
			boolean isCorpus)
	{
		super();

		this.corpusController = corpusController;
		this.textController = textController;
		this.isCorpus = isCorpus;

		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setBounds(140, 140, 450, 300);
		getContentPane().setLayout(
				new MigLayout("", "[grow][100!]", "[150!,grow][20!,grow][20!,grow][20!,grow][20!,grow]"));

		setTitle("Export " + (isCorpus ? corpusController.getFullName() : textController.getTextName()));

		textPane = new JTextPane();
		textPane.setText(Launcher.getXmlAnnotationsTextPaneResources().getText().toString());
		JScrollPane scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		textPane.setCaretPosition(0);

		getContentPane().add(scrollPane, "cell 0 0,grow, span 2");

		rdbtnAllSyntax = new JRadioButton("Tag all syntactic annotations");
		rdbtnAllSyntax.setSelected(true);
		getContentPane().add(rdbtnAllSyntax, "cell 0 1, span 2");

		rdbtnList = new JRadioButton("Tag only following annotations (click arrow for examples)");
		getContentPane().add(rdbtnList, "cell 0 2, span 2");

		ButtonGroup rdbtnGroup = new ButtonGroup();
		rdbtnGroup.add(rdbtnAllSyntax);
		rdbtnGroup.add(rdbtnList);

		// Adding needed listeners
		XmlAnnotationsListener xal = new XmlAnnotationsListener(this);
		rdbtnAllSyntax.addActionListener(xal);
		rdbtnList.addActionListener(xal);

		String[] cobxStrings = { "<header> <title> <p>", "<N>", "<SENTENCE> <NP>", "<TIMEX> <ENAMEX>", "", "<TRANS+FR>" };
		cobxXmlAnnotations = new JComboBox(cobxStrings);
		cobxXmlAnnotations.setSelectedIndex(4);
		cobxXmlAnnotations.setFont(Launcher.preferences.DFont);
		getContentPane().add(cobxXmlAnnotations, "cell 0 3,growx, span 2");

		chbxOnly = new JCheckBox("Export Annotated Text Only (ignore non-annotated text)");
		getContentPane().add(chbxOnly, "cell 0 4");

		btnExport = new JButton("Export");
		getContentPane().add(btnExport, "cell 1 4, alignx right");
		btnExport.addActionListener(new ExportAnnotatedToXmlButtonActionListener(this));
	}

	public JRadioButton getRdbtnAllSyntax()
	{
		return rdbtnAllSyntax;
	}

	public JRadioButton getRdbtnList()
	{
		return rdbtnList;
	}

	public JComboBox getCobxXmlAnnotations()
	{
		return cobxXmlAnnotations;
	}

	public JCheckBox getChbxOnly()
	{
		return chbxOnly;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public boolean isCorpus()
	{
		return isCorpus;
	}
}
