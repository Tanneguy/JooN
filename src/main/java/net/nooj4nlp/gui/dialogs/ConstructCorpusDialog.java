package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.nio.charset.Charset;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogBuildCorpusListener;
import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogController;
import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogFileFormatListener;
import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogSetCorpusListener;
import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogSetFolderListener;
import net.nooj4nlp.controller.ConstructCorpusDialog.ConstructCorpusDialogSetSourceFileNameListener;
import net.nooj4nlp.engine.SystemEnvironment;

/**
 * 
 * Corpus construction lab dialog
 * 
 */
public class ConstructCorpusDialog extends JInternalFrame
{
	private static final long serialVersionUID = -7224857705084943005L;

	private JTextField fldFileName;
	private JTextField fldPerlExpr;
	private JTextField fldBaseName;
	private JTextField fldFileNumber;
	private JTextField fldCorpus;
	private JTextField fldFolder;

	/**
	 * Creates the frame.
	 */
	public ConstructCorpusDialog()
	{
		setTitle("Construct A Corpus");
		setClosable(true);
		setIconifiable(true);
		getContentPane().setFont(new Font("Tahoma", Font.BOLD, 18));
		setBounds(400, 100, 900, 415);
		getContentPane().setLayout(null);

		JPanel pnlFileName = new JPanel();
		pnlFileName.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"(1) Enter Source File Name:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlFileName.setBounds(10, 11, 435, 62);
		getContentPane().add(pnlFileName);
		pnlFileName.setLayout(null);

		fldFileName = new JTextField();
		fldFileName.setBounds(10, 22, 340, 20);
		pnlFileName.add(fldFileName);
		fldFileName.setColumns(10);

		JButton btnSet = new JButton("Set");
		btnSet.setBounds(360, 21, 65, 23);
		pnlFileName.add(btnSet);

		JPanel pnlFileFormat = new JPanel();
		pnlFileFormat.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(2) Enter file format:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlFileFormat.setBounds(10, 85, 435, 272);
		getContentPane().add(pnlFileFormat);
		pnlFileFormat.setLayout(null);

		JRadioButton rdbtnAsciiOrBytemarked = new JRadioButton(
				"ASCII or Byte-Marked Unicode (UTF-8, UTF-16B or UTF-16L)");
		rdbtnAsciiOrBytemarked.setBounds(6, 20, 415, 23);
		// Selected by default
		rdbtnAsciiOrBytemarked.setSelected(true);
		pnlFileFormat.add(rdbtnAsciiOrBytemarked);

		DefaultListModel model = new DefaultListModel();
		JList listTextFormats = new JList(model);
		int n = SystemEnvironment.encodings.length;
		for (int i = 0; i < n; i++)
		{
			String e = SystemEnvironment.encodings[i];
			Charset c = Charset.forName(e);
			e += c.aliases().toString();
			model.add(i, e);
		}
		// List text formats will be disabled until Other raw text formats radio button is clicked
		listTextFormats.setEnabled(false);

		// n is dynamic
		JRadioButton rdbtnOtherRawText = new JRadioButton("Other raw text formats (" + n + ")");
		rdbtnOtherRawText.setBounds(6, 46, 415, 23);
		pnlFileFormat.add(rdbtnOtherRawText);

		JScrollPane scrollPane = new JScrollPane(listTextFormats, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setBounds(12, 76, 413, 158);
		pnlFileFormat.add(scrollPane);

		JRadioButton rdbtnPdf = new JRadioButton("A PDF Document");
		rdbtnPdf.setBounds(6, 242, 415, 23);
		pnlFileFormat.add(rdbtnPdf);

		ButtonGroup grpFileFormat = new ButtonGroup();
		grpFileFormat.add(rdbtnAsciiOrBytemarked);
		grpFileFormat.add(rdbtnOtherRawText);
		grpFileFormat.add(rdbtnPdf);

		JPanel pnlPerlExpr = new JPanel();
		pnlPerlExpr.setBorder(new TitledBorder(null, "(3) Enter a Perl expression for the corpus delimiter",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlPerlExpr.setBounds(452, 11, 420, 62);
		getContentPane().add(pnlPerlExpr);
		pnlPerlExpr.setLayout(null);

		fldPerlExpr = new JTextField();
		fldPerlExpr.setBounds(10, 22, 400, 20);
		pnlPerlExpr.add(fldPerlExpr);
		fldPerlExpr.setColumns(10);

		JPanel pnlResult = new JPanel();
		pnlResult.setBorder(new TitledBorder(null, "(4) Result:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlResult.setBounds(455, 85, 419, 214);
		getContentPane().add(pnlResult);
		pnlResult.setLayout(null);

		JLabel lblBaseName = new JLabel("Base Name for each file:");
		lblBaseName.setBounds(10, 28, 157, 14);
		pnlResult.add(lblBaseName);

		fldBaseName = new JTextField();
		fldBaseName.setBounds(172, 25, 237, 20);
		pnlResult.add(fldBaseName);
		fldBaseName.setColumns(10);

		JLabel lblFirstPagesectionchapterFile = new JLabel("First page/section/chapter/... file number:");
		lblFirstPagesectionchapterFile.setBounds(10, 64, 300, 14);
		pnlResult.add(lblFirstPagesectionchapterFile);

		fldFileNumber = new JTextField();
		fldFileNumber.setText("1");
		fldFileNumber.setBounds(315, 61, 80, 20);
		pnlResult.add(fldFileNumber);
		fldFileNumber.setHorizontalAlignment(JTextField.RIGHT);
		fldFileNumber.setColumns(10);

		JRadioButton rdbtnBuildACorpus = new JRadioButton("Build a corpus for language \"en\":");
		rdbtnBuildACorpus.setBounds(10, 93, 400, 23);
		rdbtnBuildACorpus.setSelected(true);
		pnlResult.add(rdbtnBuildACorpus);

		fldCorpus = new JTextField();
		fldCorpus.setBounds(20, 123, 314, 20);
		pnlResult.add(fldCorpus);
		fldCorpus.setColumns(10);

		JButton btnSetCorpusDir = new JButton("Set");
		btnSetCorpusDir.setBounds(344, 122, 65, 23);
		pnlResult.add(btnSetCorpusDir);

		fldFolder = new JTextField();
		fldFolder.setColumns(10);
		fldFolder.setBounds(20, 170, 314, 20);
		pnlResult.add(fldFolder);

		JButton btnSetFolder = new JButton("Set");
		btnSetFolder.setBounds(344, 169, 65, 23);
		pnlResult.add(btnSetFolder);

		JRadioButton rdbtnStoreAllFiles = new JRadioButton("Store all files in Folder:");
		rdbtnStoreAllFiles.setBounds(10, 145, 190, 20);
		pnlResult.add(rdbtnStoreAllFiles);

		ButtonGroup grpResult = new ButtonGroup();
		grpResult.add(rdbtnBuildACorpus);
		grpResult.add(rdbtnStoreAllFiles);

		JButton btnNewButton = new JButton("Build Corpus");
		btnNewButton.setOpaque(true);
		btnNewButton.setBackground(Color.GREEN);
		btnNewButton.setBounds(455, 310, 419, 47);
		getContentPane().add(btnNewButton);

		// Adding listeners
		ConstructCorpusDialogController controller = new ConstructCorpusDialogController(fldFileName,
				rdbtnAsciiOrBytemarked, rdbtnOtherRawText, rdbtnPdf, listTextFormats, fldCorpus, fldFolder,
				rdbtnBuildACorpus, rdbtnStoreAllFiles, fldPerlExpr, pnlPerlExpr, fldFileNumber, fldBaseName);
		btnSet.addActionListener(new ConstructCorpusDialogSetSourceFileNameListener(this, controller));
		ConstructCorpusDialogFileFormatListener fileFormatListener = new ConstructCorpusDialogFileFormatListener(
				controller);
		rdbtnAsciiOrBytemarked.addActionListener(fileFormatListener);
		rdbtnOtherRawText.addActionListener(fileFormatListener);
		rdbtnPdf.addActionListener(fileFormatListener);
		btnSetCorpusDir.addActionListener(new ConstructCorpusDialogSetCorpusListener(this, controller));
		btnSetFolder.addActionListener(new ConstructCorpusDialogSetFolderListener(this, controller));
		btnNewButton.addActionListener(new ConstructCorpusDialogBuildCorpusListener(controller));
	}
}
