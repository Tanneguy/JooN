package net.nooj4nlp.controller.preferencesdialog;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

public class UpdateDialogListener
{
	private JComboBox cbDefLanguage;
	private Font TFont;
	private Font DFont;
	private JLabel fText;
	private JLabel fDic;
	private JCheckBox chckbxNoojManagesMultiple;
	private PreferencesDialog dialog;

	public UpdateDialogListener(PreferencesDialog dialog, JComboBox cbDefLanguage, Font TFont, Font DFont,
			JLabel fText, JLabel fDic, JCheckBox chckbxNoojManagesMultiple)
	{
		this.cbDefLanguage = cbDefLanguage;
		this.TFont = TFont;
		this.DFont = DFont;
		this.fDic = fDic;
		this.fText = fText;
		this.chckbxNoojManagesMultiple = chckbxNoojManagesMultiple;
		this.dialog = dialog;
	}

	public void updateFromFormMainPreferences()
	{
		// Update all language lists
		cbDefLanguage.removeAllItems();
		if (Launcher.preferences.deflanguage == null)
		{
			
			return;
		}

		if (Launcher.projectMode)
		{
			for (String lan : Launcher.preferences.languages)
			{
				cbDefLanguage.addItem(lan);
			}
			cbDefLanguage.setSelectedIndex(0);
		}
		else
		{
			SelectDefLanguageActionListener selectDefLangListener = this.dialog.getSelectDefLanguageListener();
			cbDefLanguage.removeActionListener(selectDefLangListener);

			for (String lan : Language.getAllLanguages())
			{
				cbDefLanguage.addItem(lan);
			}

			cbDefLanguage.addActionListener(selectDefLangListener);

			// set default language
			int index = -1;
			for (int i = 0; i < cbDefLanguage.getItemCount(); i++)
			{
				if (Launcher.preferences.deflanguage.equalsIgnoreCase((String) cbDefLanguage.getItemAt(i)))
				{
					index = i;
					break;
				}
			}
			if (index != -1)
			{
				cbDefLanguage.setSelectedIndex(index);
			}
		}

		// update misc
		chckbxNoojManagesMultiple.setSelected(Launcher.preferences.multiplebackups);

		// update Fonts
		Font f = Launcher.preferences.TFont;
		TFont = new Font(f.getName(), f.getStyle(), f.getSize());
		fText.setText(TFont.getName() + ", " + TFont.getSize()); // input font
		if (TFont.isBold())
			fText.setText(fText.getText() + ", Bold");
		if (TFont.isItalic())
			fText.setText(fText.getText() + ", Italic");
		f = Launcher.preferences.DFont;
		DFont = new Font(f.getName(), f.getStyle(), f.getSize());
		fDic.setText(DFont.getName() + ", " + DFont.getSize()); // output font
		if (DFont.isBold())
			fDic.setText(fDic.getText() + ", Bold");
		if (DFont.isItalic())
			fDic.setText(fDic.getText() + ", Italic");
		fText.setFont(TFont);
		fDic.setFont(DFont);
	}
}