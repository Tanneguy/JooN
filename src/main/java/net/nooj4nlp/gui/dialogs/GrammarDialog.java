package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.GrammarDialog.ButtonListener;
import net.nooj4nlp.controller.GrammarDialog.RuleGraphicalRadioListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Dialog for creating a new grammar
 * 
 */
public class GrammarDialog extends JDialog
{
	private static final long serialVersionUID = -1202831967070061505L;

	private final JPanel contentPanel = new JPanel();
	private JTextField fldAuthor;
	private JTextField fldInsitution;
	private JPasswordField fldPassword;

	/**
	 * Creates the dialog.
	 */
	public GrammarDialog()
	{
		setTitle("Create a new Grammar");
		setBounds(100, 100, 519, 423);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel pnlLang = new JPanel();
		pnlLang.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "(1) Select Languages:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlLang.setBounds(10, 11, 240, 220);
		contentPanel.add(pnlLang);
		pnlLang.setLayout(null);

		DefaultListModel model = new DefaultListModel();
		String[] languages = Language.getAllLanguages();
		for (int i = 0; i < languages.length; i++)
			model.add(i, languages[i]);

		JList listInputLang = new JList(model);
		JScrollPane scrollPane1 = new JScrollPane(listInputLang, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane1.setBounds(10, 54, 99, 154);
		pnlLang.add(scrollPane1);

		JList listOutputLang = new JList(model);
		JScrollPane scrollPane2 = new JScrollPane(listOutputLang, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane2.setBounds(130, 54, 99, 154);
		pnlLang.add(scrollPane2);

		listInputLang.setSelectedValue(Launcher.preferences.deflanguage, true);
		listOutputLang.setSelectedValue(Launcher.preferences.deflanguage, true);

		JLabel lblInput = new JLabel("Input:");
		lblInput.setBounds(10, 29, 46, 14);
		pnlLang.add(lblInput);

		JLabel lblOutput = new JLabel("Output:");
		lblOutput.setBounds(132, 29, 46, 14);
		pnlLang.add(lblOutput);

		JPanel pnlOptionalInfo = new JPanel();
		pnlOptionalInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"(2) Optional Information:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlOptionalInfo.setBounds(261, 11, 233, 220);
		contentPanel.add(pnlOptionalInfo);
		pnlOptionalInfo.setLayout(null);

		JLabel lblAuthor = new JLabel("Author:");
		lblAuthor.setBounds(10, 29, 46, 14);
		pnlOptionalInfo.add(lblAuthor);

		fldAuthor = new JTextField();
		fldAuthor.setBounds(52, 26, 171, 20);
		pnlOptionalInfo.add(fldAuthor);
		fldAuthor.setColumns(10);

		JLabel lblInstitution = new JLabel("Institution:");
		lblInstitution.setBounds(10, 60, 62, 14);
		pnlOptionalInfo.add(lblInstitution);

		fldInsitution = new JTextField();
		fldInsitution.setBounds(66, 57, 157, 20);
		pnlOptionalInfo.add(fldInsitution);
		fldInsitution.setColumns(10);

		JCheckBox chckbxLockGrammar = new JCheckBox("Lock Grammar:");
		chckbxLockGrammar.setBounds(10, 128, 97, 23);
		pnlOptionalInfo.add(chckbxLockGrammar);

		JRadioButton rdbtnNoDisplay = new JRadioButton("No display");
		rdbtnNoDisplay.setEnabled(false);
		rdbtnNoDisplay.setBounds(130, 128, 83, 23);
		rdbtnNoDisplay.setSelected(true);
		pnlOptionalInfo.add(rdbtnNoDisplay);

		JRadioButton rdbtnCommunity = new JRadioButton("Community");
		rdbtnCommunity.setEnabled(false);
		rdbtnCommunity.setBounds(130, 154, 83, 23);
		pnlOptionalInfo.add(rdbtnCommunity);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 191, 62, 14);
		pnlOptionalInfo.add(lblPassword);

		fldPassword = new JPasswordField();
		fldPassword.setBounds(66, 188, 157, 20);
		pnlOptionalInfo.add(fldPassword);
		fldPassword.setColumns(10);
		fldPassword.setEnabled(false);

		JPanel pnlGrammarType = new JPanel();
		pnlGrammarType.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"(3) Select Grammar Type:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlGrammarType.setBounds(10, 242, 484, 137);
		contentPanel.add(pnlGrammarType);
		pnlGrammarType.setLayout(null);

		JLabel lblNewLabel = new JLabel(
				"<html>Morphological Graphs recognize sequences of letters. Syntactic Graphs recognize sequences of words</html>");
		lblNewLabel.setBounds(20, 23, 443, 31);
		pnlGrammarType.add(lblNewLabel);

		ButtonGroup grpGrammarType = new ButtonGroup();

		JRadioButton rdbtnGraphicalEditor = new JRadioButton("graphical editor");
		rdbtnGraphicalEditor.setBounds(20, 61, 109, 23);
		pnlGrammarType.add(rdbtnGraphicalEditor);
		grpGrammarType.add(rdbtnGraphicalEditor);

		JRadioButton rdbtnRuleEditor = new JRadioButton("rule editor");
		rdbtnRuleEditor.setBounds(131, 61, 109, 23);
		pnlGrammarType.add(rdbtnRuleEditor);
		grpGrammarType.add(rdbtnRuleEditor);

		JButton btnInflectionDerivation = new JButton("Inflection & Derivation");
		btnInflectionDerivation.setBounds(20, 91, 149, 35);
		pnlGrammarType.add(btnInflectionDerivation);

		JButton btnProductiveMorphology = new JButton("Productive Morphology");
		btnProductiveMorphology.setBounds(183, 91, 149, 35);
		pnlGrammarType.add(btnProductiveMorphology);

		JButton btnSyntax = new JButton("Syntax");
		btnSyntax.setBounds(342, 91, 132, 35);
		pnlGrammarType.add(btnSyntax);

		rdbtnGraphicalEditor.setSelected(true);
		RuleGraphicalRadioListener listener = new RuleGraphicalRadioListener(rdbtnRuleEditor, chckbxLockGrammar,
				rdbtnNoDisplay, rdbtnCommunity, fldPassword, lblPassword);
		rdbtnGraphicalEditor.addActionListener(listener);
		rdbtnRuleEditor.addActionListener(listener);
		ButtonListener btnListener = new ButtonListener(this, listInputLang, listOutputLang, fldAuthor, fldInsitution,
				rdbtnRuleEditor);
		btnInflectionDerivation.addActionListener(btnListener);
		btnProductiveMorphology.addActionListener(btnListener);
		btnSyntax.addActionListener(btnListener);
	}
}
