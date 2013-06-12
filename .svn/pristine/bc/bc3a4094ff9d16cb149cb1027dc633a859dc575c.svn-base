package net.nooj4nlp.controller.DelaDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import net.nooj4nlp.gui.dialogs.DelaDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;

public class OKActionListener implements ActionListener
{

	private JList listLanguages;
	private JRadioButton rdbtnOther;
	private JList listFormats;
	private JRadioButton rdbtnDelas;
	private DelaDialog dialog;
	private String fullName;

	public OKActionListener(String fullname, JList listLanguages, JRadioButton rdbtnUtf, JRadioButton rdbtnOther,
			JList listFormats, JRadioButton rdbtnDelas, JRadioButton rdbtnDelaf, JRadioButton rdbtnLG, DelaDialog dialog)
	{
		super();
		this.fullName = fullname;
		this.listLanguages = listLanguages;
		this.rdbtnOther = rdbtnOther;
		this.listFormats = listFormats;
		this.rdbtnDelas = rdbtnDelas;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String lang = (String) listLanguages.getSelectedValue();
		if (lang == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please, select language!", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Charset enc = null;

		if (rdbtnOther.isSelected())
		{
			String encoding = (String) listFormats.getSelectedValue();
			if (encoding == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please select a file format", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			enc = Charset.forName(encoding.substring(0, encoding.indexOf('[')));
		}
		else
			enc = Charset.forName("UTF-8");

		DictionaryEditorShell editor = new DictionaryEditorShell();
		if (rdbtnDelas.isSelected())
			editor.getController().loadFromDl(fullName, lang, enc, true);
		else
			editor.getController().loadFromDl(fullName, lang, enc, false);
		Launcher.getDesktopPane().add(editor);
		editor.setVisible(true);

		dialog.dispose();
	}
}