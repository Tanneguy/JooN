package net.nooj4nlp.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.AddActionListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.SystemEnvironment;
import net.nooj4nlp.gui.actions.grammar.ImportGrammarActionListener;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Dialog for encoding and language selection of imported grammar.
 * 
 */
public class OpenGrammarDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private ImportGrammarActionListener importGrammarListener;

	private int indexOfDefaultEncoding;

	// data lists
	private JList fileFormatList;
	private JList inputLanguageList;
	private JList outputLanguageList;

	// buttons
	private JButton inflectionalGrammarButton;
	private JButton morphologicalGrammarButton;
	private JButton syntacticGrammarButton;

	/**
	 * Constructor.
	 * 
	 * @param importGrammarListener
	 *            - listener from which context this function was called
	 * @param selectedIndexOfFileFormats
	 *            - index for file formats list
	 */
	public OpenGrammarDialog(ImportGrammarActionListener importGrammarListener, int selectedIndexOfFileFormats)
	{
		this.importGrammarListener = importGrammarListener;
		this.indexOfDefaultEncoding = Launcher.getIndexOfDefaultEncoding();

		setTitle("Import a graph and its embedded graphs");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setBounds(50, 50, 550, 280);

		// 3x3
		getContentPane().setLayout(new MigLayout("insets 5", "[][][]", "[::40][grow][::50]"));

		JLabel inputLanguageLabel = new JLabel("Input Language:");
		getContentPane().add(inputLanguageLabel, "cell 0 0, gaptop 20, align center");

		JLabel outputLanguageLabel = new JLabel("Output Language:");
		getContentPane().add(outputLanguageLabel, "cell 1 0, gaptop 20, align center");

		JLabel fileFormatLabel = new JLabel("File Format:");
		getContentPane().add(fileFormatLabel, "cell 2 0, gaptop 20, align left");

		DefaultListModel inputModel = new DefaultListModel();
		DefaultListModel outputModel = new DefaultListModel();
		inputLanguageList = new JList(inputModel);
		outputLanguageList = new JList(outputModel);

		// fill in languages
		for (String language : Language.getAllLanguages())
		{
			inputModel.addElement(language);
			outputModel.addElement(language);
		}

		inputLanguageList.setSelectedValue(Launcher.preferences.deflanguage, true);
		inputLanguageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		inputLanguageList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JScrollPane scrollInputLanguages = new JScrollPane(inputLanguageList);
		scrollInputLanguages.setPreferredSize(new Dimension(50, 150));
		getContentPane().add(scrollInputLanguages, "cell 0 1, align center");
		inputLanguageList.ensureIndexIsVisible(inputLanguageList.getSelectedIndex());

		outputLanguageList.setSelectedValue(Launcher.getIndexOfDefaultEncoding(), true);
		outputLanguageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		outputLanguageList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JScrollPane scrollOutputLanguages = new JScrollPane(outputLanguageList);
		scrollOutputLanguages.setPreferredSize(new Dimension(50, 150));
		getContentPane().add(scrollOutputLanguages, "cell 1 1, align center");
		outputLanguageList.ensureIndexIsVisible(outputLanguageList.getSelectedIndex());

		DefaultListModel model = new DefaultListModel();
		fileFormatList = new JList(model);
		fileFormatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileFormatList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JScrollPane scrollFileFormat = new JScrollPane(fileFormatList);
		scrollFileFormat.setPreferredSize(new Dimension(250, 150));
		getContentPane().add(scrollFileFormat, "cell 2 1, align center");

		int n = SystemEnvironment.encodings.length;

		for (int i = 0; i < n; i++)
		{
			String e = SystemEnvironment.encodings[i];
			if (e.startsWith("UTF-8") && indexOfDefaultEncoding == -1)
				indexOfDefaultEncoding = i;
			Charset c = Charset.forName(e);
			e += c.aliases().toString();
			model.add(i, e);
		}

		fileFormatList.setSelectedIndex(indexOfDefaultEncoding);
		fileFormatList.ensureIndexIsVisible(indexOfDefaultEncoding);

		OpenGrammarButtonActionListener openGrammarListener = new OpenGrammarButtonActionListener(this,
				this.importGrammarListener);

		inflectionalGrammarButton = new JButton("Inflectional Grammar");
		getContentPane().add(inflectionalGrammarButton, "cell 0 2, align center");
		inflectionalGrammarButton.setActionCommand("Inflectic");
		inflectionalGrammarButton.addActionListener(openGrammarListener);

		morphologicalGrammarButton = new JButton("Morphological Grammar");
		getContentPane().add(morphologicalGrammarButton, "cell 1 2, align center");
		morphologicalGrammarButton.setActionCommand("Morphologic");
		morphologicalGrammarButton.addActionListener(openGrammarListener);

		// No action listeners in C# version for those two buttons! Third button event and onClose event are,
		// practically, the same one at the moment.

		syntacticGrammarButton = new JButton("Syntactic Grammar");
		getContentPane().add(syntacticGrammarButton, "cell 2 2, align center");
		syntacticGrammarButton.setActionCommand("Syntactic");
		syntacticGrammarButton.addActionListener(openGrammarListener);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				Launcher.setIndexOfDefaultEncoding(fileFormatList.getSelectedIndex());
			}
		});
	}

	// getters and setters
	public JList getInputLanguageList()
	{
		return inputLanguageList;
	}

	public JList getOutputLanguageList()
	{
		return outputLanguageList;
	}

	public JList getFileFormatList()
	{
		return fileFormatList;
	}

	public JButton getInflectionalGrammarButton()
	{
		return inflectionalGrammarButton;
	}

	public JButton getMorphologicalGrammarButton()
	{
		return morphologicalGrammarButton;
	}

	public JButton getSyntacticGrammarButton()
	{
		return syntacticGrammarButton;
	}
}

/**
 * Class implements action listeners for button events of Open Grammar Dialog
 */
class OpenGrammarButtonActionListener implements ActionListener
{
	private ImportGrammarActionListener importGrammarListener;
	private OpenGrammarDialog dialog;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            - Open grammar dialog
	 * @param importGrammarListener
	 *            - class from which context open grammar dialog was called
	 */
	public OpenGrammarButtonActionListener(OpenGrammarDialog dialog, ImportGrammarActionListener importGrammarListener)
	{
		this.importGrammarListener = importGrammarListener;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.importGrammarListener.setClosedWithXButton(false);

		// if button is Syntactic, set encoding, and remember it - otherwise, just close the window
		if (((JButton) e.getSource()).getActionCommand().equals("Syntactic"))
		{
			int selectedIndexOfFileFormats = this.dialog.getFileFormatList().getSelectedIndex();
			Launcher.setIndexOfDefaultEncoding(selectedIndexOfFileFormats);
			String fields = SystemEnvironment.listAllEncodings()[this.dialog.getFileFormatList().getSelectedIndex()];
			int index = fields.indexOf('[');
			Charset enc;
			if (index != -1)
				enc = Charset.forName(fields.substring(0, index));
			else
				enc = Charset.forName(fields);
			Launcher.setEncodingCodeOfOpenGrammar(enc.name());
		}

		AddActionListener.closeDialogWindow((JButton) e.getSource());
	}
}