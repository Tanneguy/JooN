package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.MorphologyDialog.ButtonListener;
import net.nooj4nlp.controller.MorphologyDialog.ListListener;
import net.nooj4nlp.controller.MorphologyDialog.RadioListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Morphology lab dialog
 * 
 */
public class MorphologyDialog extends JInternalFrame
{
	private static final long serialVersionUID = -7265615929726721451L;

	/**
	 * Creates the frame.
	 */
	public MorphologyDialog()
	{
		setTitle("Morphology");
		setClosable(true);
		setIconifiable(true);
		setBounds(400, 100, 602, 608);
		getContentPane().setLayout(null);

		JPanel pnlLanguage = new JPanel();
		pnlLanguage.setBorder(new TitledBorder(null, "Select Language:", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		pnlLanguage.setBounds(10, 11, 135, 233);
		getContentPane().add(pnlLanguage);
		pnlLanguage.setLayout(null);

		JLabel lblLanguage = new JLabel("language");
		lblLanguage.setBounds(10, 21, 115, 57);
		pnlLanguage.add(lblLanguage);

		DefaultListModel model = new DefaultListModel();
		String[] languages = Language.getAllLanguages();
		for (int i = 0; i < languages.length; i++)
			model.add(i, languages[i]);
		JList listLanguage = new JList(model);

		JScrollPane scrollPane = new JScrollPane(listLanguage, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setBounds(10, 89, 115, 123);
		pnlLanguage.add(scrollPane);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(155, 11, 411, 233);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JRadioButton rdbtnWordCommand = new JRadioButton("Enter one simple or compound word and one Command:");
		rdbtnWordCommand.setBounds(6, 7, 390, 23);
		panel_1.add(rdbtnWordCommand);

		JLabel lblWordRoot = new JLabel("Word/Root:");
		lblWordRoot.setBounds(83, 35, 79, 14);
		panel_1.add(lblWordRoot);

		JComboBox comboWordRoot = new JComboBox();
		comboWordRoot.setEditable(true);
		comboWordRoot.setBounds(159, 31, 227, 23);
		panel_1.add(comboWordRoot);

		JLabel lblCommandSuffix = new JLabel("Command/Suffix:");
		lblCommandSuffix.setBounds(42, 70, 120, 14);
		panel_1.add(lblCommandSuffix);

		JComboBox comboCommandSuffix = new JComboBox();
		comboCommandSuffix.setEditable(true);
		comboCommandSuffix.setBounds(159, 66, 227, 23);
		panel_1.add(comboCommandSuffix);

		JRadioButton rdbtnLemmaExpression = new JRadioButton(
				"Enter a lemma and an inflectional/derivational expression");
		rdbtnLemmaExpression.setBounds(6, 95, 400, 23);
		panel_1.add(rdbtnLemmaExpression);

		JLabel lblLemma = new JLabel("Lemma:");
		lblLemma.setBounds(108, 125, 64, 14);
		panel_1.add(lblLemma);

		JComboBox comboLemma = new JComboBox();
		comboLemma.setEditable(true);
		comboLemma.setBounds(159, 122, 227, 23);
		panel_1.add(comboLemma);

		JLabel lblExpression = new JLabel("Expression:");
		lblExpression.setBounds(84, 158, 78, 14);
		panel_1.add(lblExpression);

		JComboBox comboExpression = new JComboBox();
		comboExpression.setEditable(true);
		comboExpression.setBounds(159, 156, 227, 23);
		panel_1.add(comboExpression);

		JRadioButton rdbtnLookup = new JRadioButton("Lookup a word:");
		rdbtnLookup.setBounds(6, 194, 129, 23);
		panel_1.add(rdbtnLookup);

		ButtonGroup grpMorphology = new ButtonGroup();
		grpMorphology.add(rdbtnLemmaExpression);
		grpMorphology.add(rdbtnLookup);
		grpMorphology.add(rdbtnWordCommand);

		JComboBox comboLookup = new JComboBox();
		comboLookup.setEditable(true);
		comboLookup.setBounds(159, 195, 227, 23);
		panel_1.add(comboLookup);

		JButton btnInflectderive = new JButton("Inflect/Derive");
		btnInflectderive.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnInflectderive.setOpaque(true);
		btnInflectderive.setBackground(Color.GREEN);
		btnInflectderive.setBounds(10, 255, 556, 34);
		getContentPane().add(btnInflectderive);

		JPanel pnlResult = new JPanel();
		pnlResult.setBorder(new TitledBorder(null, "Result:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlResult.setBounds(10, 300, 556, 260);
		getContentPane().add(pnlResult);
		pnlResult.setLayout(new BorderLayout(0, 0));

		DefaultListModel resultModel = new DefaultListModel();
		JList listResult = new JList(resultModel);
		JScrollPane scrollListPane = new JScrollPane(listResult, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlResult.add(scrollListPane, BorderLayout.CENTER);
		listResult.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// adding listeners
		listLanguage.addListSelectionListener(new ListListener(listLanguage, comboWordRoot, comboCommandSuffix,
				comboLemma, comboExpression, comboLookup, lblLanguage));
		RadioListener listener1 = new RadioListener(comboWordRoot, comboCommandSuffix, comboLemma, comboExpression,
				comboLookup, rdbtnWordCommand, rdbtnLemmaExpression, rdbtnLookup);
		rdbtnWordCommand.addActionListener(listener1);
		rdbtnLemmaExpression.addActionListener(listener1);
		rdbtnLookup.addActionListener(listener1);
		btnInflectderive.addActionListener(new ButtonListener(listLanguage, comboWordRoot, comboCommandSuffix,
				comboLemma, comboExpression, comboLookup, rdbtnWordCommand, rdbtnLemmaExpression, rdbtnLookup,
				resultModel, pnlResult));

		Language lan = new Language(Launcher.preferences.deflanguage);
		listLanguage.setSelectedValue(lan.isoName, true);

		rdbtnWordCommand.setSelected(true);
		comboLemma.setEnabled(false);
		comboExpression.setEnabled(false);
		comboLookup.setEnabled(false);
	}
}
