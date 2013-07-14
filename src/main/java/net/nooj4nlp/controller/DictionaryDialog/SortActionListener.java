package net.nooj4nlp.controller.DictionaryDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

public class SortActionListener implements ActionListener
{

	private DictionaryDialogController controller;

	public SortActionListener(DictionaryDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Language lan = new Language(Launcher.preferences.deflanguage);
		ArrayList<String> lines = new ArrayList<String>();

		String rtbtext = DictionaryDialogController.getDictionaryContent(controller.getTxtDictionaryName().getText());
		if (rtbtext == null)
			return;

		String[] rtblines = rtbtext.split("\n");

		for (int iline = 0; iline < rtblines.length;)
		{
			String line = rtblines[iline];
			for (; line.length() == 0 || line.charAt(0) == '#';)
			{
				lines.add(line);
				iline++;
				if (iline >= rtblines.length)
					break;
				line = rtblines[iline];
			}

			ArrayList<String> lines0 = new ArrayList<String>();
			for (; line.length() > 0 && line.charAt(0) != '#';)
			{
				lines0.add(line);
				iline++;
				if (iline >= rtblines.length)
					break;
				line = rtblines[iline];
			}

			DictionaryEditorShellController.sortLines(lines0, lan);

			lines.addAll(lines0);
		}
		StringBuilder sb = new StringBuilder();

		for (int iline = 0; iline < lines.size(); iline++)
		{
			String line = lines.get(iline);
			if (iline < lines.size() - 1 || !line.equals(""))
				sb.append(line + "\n");
		}
		rtblines = null;
		String resultingdictionary = sb.toString();
		sb = null;

		String tmp = controller.getTxtDictionaryName().getText();
		String fname = org.apache.commons.io.FilenameUtils.removeExtension(org.apache.commons.io.FilenameUtils
				.getName(tmp));
		String ext = org.apache.commons.io.FilenameUtils.getExtension(tmp);
		String dname = org.apache.commons.io.FilenameUtils.getFullPath(tmp);

		String fullname2 = dname + fname + "-s." + ext;
		try
		{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullname2), "UTF8"));
			out.write(resultingdictionary);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resultingdictionary = null;

		if (JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Do you want to work with new created dictionary "
				+ fullname2 + "?", "NooJ", JOptionPane.YES_NO_OPTION) == 0)
		{
			controller.getTxtDictionaryName().setText(fullname2);
			if (controller.loadLines(0, 500))
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display "
								+ org.apache.commons.io.FilenameUtils.getName(controller.getTxtDictionaryName()
										.getText()), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			else
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display beginning of "
								+ org.apache.commons.io.FilenameUtils.getName(controller.getTxtDictionaryName()
										.getText()), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
	}

}
