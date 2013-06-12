package net.nooj4nlp.controller.preferencesdialog;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.main.Launcher;

public class SelectDefLanguageActionListener implements ActionListener
{

	private JComboBox cbDefLanguage;
	private UpdateTablesListener tableListener;
	private JLabel lblLex;
	private JLabel lblSyn;
	private JLabel lNative;
	private JLabel lEnglish;
	private JLabel lChartVariants;

	public SelectDefLanguageActionListener(JComboBox cbDefLanguage, JLabel lblLex, JLabel lblSyn, JLabel lNative,
			JLabel lEnglish, JLabel lChartVariants, UpdateTablesListener tableListener)
	{
		this.cbDefLanguage = cbDefLanguage;
		this.tableListener = tableListener;
		this.lblLex = lblLex;
		this.lblSyn = lblSyn;
		this.lNative = lNative;
		this.lEnglish = lEnglish;
		this.lChartVariants = lChartVariants;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int index = cbDefLanguage.getSelectedIndex();
		if (index == -1)
			return;
		String isolanguagename = (String) cbDefLanguage.getSelectedItem();

		lblLex.setText("Lexical Resources for: " + isolanguagename);
		lblSyn.setText("Available Syntactic Resources for: " + isolanguagename);

		Language lan = new Language(isolanguagename);

		if (lan.rightToLeft)
		{
			lNative.setHorizontalAlignment(JLabel.TRAILING);
			lNative.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		lNative.setText(lan.natName);
		lEnglish.setText(lan.engName);

		UpdateLvsFromPref(Launcher.projectMode, isolanguagename);

		// is there a characters' variation table ? (not only for zh or vi)
		String chartname = Paths.docDir + System.getProperty("file.separator") + lan.isoName
				+ System.getProperty("file.separator") + "Lexical Analysis" + System.getProperty("file.separator")
				+ "charvariants.txt";
		File file = new File(chartname);
		if (!file.exists())
		{
			chartname = Paths.docDir + System.getProperty("file.separator") + lan.isoName
					+ System.getProperty("file.separator") + "Lexical Analysis" + System.getProperty("file.separator")
					+ "_charvariants.txt";
			File file1 = new File(chartname);
			if (!file1.exists())
			{
				lChartVariants.setText("No character variation");
				return;
			}
		}

		lChartVariants.setText("NooJ uses the following table of characters' variants: " + chartname);
	}

	private void UpdateLvsFromPref(boolean projectmode, String languagename)
	{
		

		tableListener.GetAllResourcesFromDisk(projectmode, languagename);
		tableListener.GetAllResourcesFromPref(languagename);

		try
		{
			
		}
		catch (Exception e)
		{

		}
	}
}