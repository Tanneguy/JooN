package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.nio.charset.Charset;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.DelaDialog.CancelActionListener;
import net.nooj4nlp.controller.DelaDialog.OKActionListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.SystemEnvironment;
import net.nooj4nlp.gui.main.Launcher;

public class DelaDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public DelaDialog(String fullname)
	{
		setTitle("Import an INTEX-type dictionary");
		setBounds(100, 100, 354, 397);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel pnlLang = new JPanel();
		pnlLang.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(1) Select Language:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlLang.setBounds(10, 11, 119, 240);
		contentPanel.add(pnlLang);
		pnlLang.setLayout(null);

		JList listLanguages = new JList(Language.getAllLanguages());
		JScrollPane scrollPane = new JScrollPane(listLanguages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setBounds(10, 24, 99, 205);
		pnlLang.add(scrollPane);

		listLanguages.setSelectedValue(Launcher.preferences.deflanguage, true);

		JPanel pnlEncoding = new JPanel();
		pnlEncoding.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(2) Enter file format",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlEncoding.setBounds(131, 11, 204, 240);
		contentPanel.add(pnlEncoding);
		pnlEncoding.setLayout(null);

		ButtonGroup grpRadioBtnsEncoding = new ButtonGroup();

		JRadioButton rdbtnUtf = new JRadioButton("<html>ASCII or Byte-Marked Unicode (UTF8, UTF16B or UTF16L)</html>");
		rdbtnUtf.setBounds(11, 19, 190, 33);
		pnlEncoding.add(rdbtnUtf);

		JRadioButton rdbtnOther = new JRadioButton("Other Raw text formats (.txt):");
		rdbtnOther.setBounds(11, 55, 187, 23);
		pnlEncoding.add(rdbtnOther);

		grpRadioBtnsEncoding.add(rdbtnOther);
		grpRadioBtnsEncoding.add(rdbtnUtf);

		rdbtnOther.setSelected(true);

		DefaultListModel model = new DefaultListModel();
		JList listFormats = new JList(model);
		for (int i = 0; i < SystemEnvironment.encodings.length; i++)
		{
			String e = SystemEnvironment.encodings[i];
			Charset c = Charset.forName(e);
			e += c.aliases().toString();
			model.add(i, e);
		}
		JScrollPane scrollPane1 = new JScrollPane(listFormats, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane1.setBounds(10, 80, 183, 145);
		pnlEncoding.add(scrollPane1);

		JPanel pnlDela = new JPanel();
		pnlDela.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(3) Dictionary Type",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlDela.setBounds(10, 250, 180, 105);
		contentPanel.add(pnlDela);
		pnlDela.setLayout(null);

		ButtonGroup grpRadioBtnsDela = new ButtonGroup();

		JRadioButton rdbtnDelas = new JRadioButton("DELAS/DELAC");
		rdbtnDelas.setBounds(11, 20, 110, 23);
		pnlDela.add(rdbtnDelas);

		JRadioButton rdbtnDelaf = new JRadioButton("DELAF/DELACF");
		rdbtnDelaf.setBounds(11, 45, 110, 23);
		pnlDela.add(rdbtnDelaf);

		JRadioButton rdbtnLG = new JRadioButton("Lexicon-Grammar Table");
		rdbtnLG.setBounds(11, 70, 140, 23);
		pnlDela.add(rdbtnLG);

		grpRadioBtnsDela.add(rdbtnDelas);
		grpRadioBtnsDela.add(rdbtnDelaf);
		grpRadioBtnsDela.add(rdbtnLG);

		rdbtnDelas.setSelected(true);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKActionListener(fullname, listLanguages, rdbtnUtf, rdbtnOther, listFormats,
				rdbtnDelas, rdbtnDelaf, rdbtnLG, this));
		okButton.setBounds(229, 318, 74, 23);
		contentPanel.add(okButton);
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new CancelActionListener(this));
		btnCancel.setActionCommand("OK");
		btnCancel.setBounds(229, 284, 74, 23);
		contentPanel.add(btnCancel);
	}

}