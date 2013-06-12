package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.DictionaryPropDefDialog.CancelActionListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.actions.shells.construct.NewDictionaryActionListener;
import net.nooj4nlp.gui.actions.shells.construct.NewPropDefActionListener;

/**
 * 
 * Dialog for creating a new dictionary or a property definition
 * 
 */
public class DictionaryPropDefDialog extends JDialog
{
	private static final long serialVersionUID = 3762020754004251438L;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Creates the dialog.
	 * 
	 * @param dictionary
	 *            true initializes a new dictionary, false creates a new property definition
	 */
	public DictionaryPropDefDialog(boolean dictionary)
	{
		if (dictionary)
		{
			setTitle("Import and INTEX-type dictionary");
		}
		else
		{
			setTitle("File");
		}
		setBounds(100, 100, 229, 297);
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

		JList listLanguages = new JList(Language.getAllLanguages());
		JScrollPane scrollPane = new JScrollPane(listLanguages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setBounds(10, 24, 99, 205);
		pnlLang.add(scrollPane);

		JButton okButton = new JButton("OK");
		if (dictionary)
			okButton.addActionListener(new NewDictionaryActionListener(listLanguages, this));
		else
			okButton.addActionListener(new NewPropDefActionListener(listLanguages, this));
		okButton.setBounds(139, 228, 64, 23);
		contentPanel.add(okButton);
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("OK");
		btnCancel.setBounds(139, 194, 64, 23);
		btnCancel.addActionListener(new CancelActionListener(this));
		contentPanel.add(btnCancel);
	}
}
