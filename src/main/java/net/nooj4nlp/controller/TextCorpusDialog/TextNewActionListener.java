package net.nooj4nlp.controller.TextCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.TextCorpusDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class TextNewActionListener implements ActionListener
{

	private TextCorpusDialog dialog;

	public TextNewActionListener(TextCorpusDialog dialog)
	{
		this.dialog = dialog;
	}

	public void actionPerformed(ActionEvent evt)
	{
		// input and chosen details needs to be saved
		if (TextCorpusDialog.getRdbtnPerlRegExpr().isSelected())
		{
			JComboBox comboPerl = TextCorpusDialog.getComboPerl();
			int selectedIndex = comboPerl.getSelectedIndex();
			List<String> perlList = Launcher.getRegexMemoryList();

			// if it's the case of an input text...
			if (selectedIndex == -1)
			{
				// ...select the last one and add it to the list
				selectedIndex = comboPerl.getItemCount();
				perlList.add(comboPerl.getSelectedItem().toString());
				Launcher.setRegexMemoryList(perlList);
			}

			// remember radio button and combo index that was last chosen
			Launcher.setCorpusTextRadioButtonSelectionMemory(3);
			Launcher.setRegexMemoryIndex(selectedIndex);
		}
		else if (TextCorpusDialog.getRdbtnXmlTextNodes().isSelected())
		{
			JComboBox comboXml = TextCorpusDialog.getComboXml();
			int selectedIndex = comboXml.getSelectedIndex();
			List<String> xmlList = Launcher.getXmlMemoryList();

			// if it's the case of an input text...
			if (selectedIndex == -1)
			{
				// ...select the last one and add it to the list
				selectedIndex = comboXml.getItemCount();
				xmlList.add(comboXml.getSelectedItem().toString());
				Launcher.setXmlMemoryList(xmlList);
			}

			// remember radio button and combo index that was last chosen
			Launcher.setCorpusTextRadioButtonSelectionMemory(4);
			Launcher.setXmlMemoryIndex(selectedIndex);
		}
		else if (TextCorpusDialog.getRdbtnNoDelimiterwhole().isSelected())
			Launcher.setCorpusTextRadioButtonSelectionMemory(1);
		else
			Launcher.setCorpusTextRadioButtonSelectionMemory(2);

		// set the language name
		String languageName = (String) dialog.getListLanguages().getSelectedValue();
		String dir = Paths.docDir + System.getProperty("file.separator") + languageName;
		File file = new File(dir);
		if (!file.isDirectory())
			file.mkdir();
		String dir2 = dir + System.getProperty("file.separator") + "Lexical Analysis";
		File file2 = new File(dir2);
		if (!file2.isDirectory())
			file2.mkdir();
		dir2 = dir + System.getProperty("file.separator") + "Syntactic Analysis";
		file2 = new File(dir2);
		if (!file2.isDirectory())
			file2.mkdir();

		// set the encoding for the file
		int encodingType = 1; // NooJ raw text

		String delimPattern = "";
		String[] xmlNodes = null;

		if (TextCorpusDialog.getRdbtnLineDelimiter().isSelected())
		{
			delimPattern = "\n";
			xmlNodes = null;
		}
		else if (TextCorpusDialog.getRdbtnPerlRegExpr().isSelected())
		{
			delimPattern = (String) TextCorpusDialog.getComboPerl().getSelectedItem();
		}
		else if (TextCorpusDialog.getRdbtnXmlTextNodes().isSelected())
		{
			encodingType = -encodingType;
			// get all text zones
			ArrayList<String> tmp = new ArrayList<String>();
			for (int i = 0; i < ((String) TextCorpusDialog.getComboXml().getSelectedItem()).length(); i++)
			{
				if (((String) TextCorpusDialog.getComboXml().getSelectedItem()).charAt(i) == '<')
				{
					int j = 0;
					for (j = 0; i + j < ((String) TextCorpusDialog.getComboXml().getSelectedItem()).length()
							&& ((String) TextCorpusDialog.getComboXml().getSelectedItem()).charAt(j) != '>'; j++)
						;
					tmp.add(((String) TextCorpusDialog.getComboXml().getSelectedItem()).substring(i, j + 1));
					i += j;
				}
			}
			if (tmp.size() == 0)
				xmlNodes = null;
			else
			{
				xmlNodes = new String[tmp.size()];
				for (int j = 0; j < xmlNodes.length; j++)
				{
					xmlNodes[j] = tmp.get(j);
				}
			}
		}
		Ntext myText = new Ntext(languageName, delimPattern, xmlNodes);
		// open the text editor window
		TextEditorShell editor = new TextEditorShell(null, myText, "New text", delimPattern, true);
		Launcher.getDesktopPane().add(editor);
		editor.setVisible(true);

		if (editor != null)
		{
			TextEditorShellController textController = editor.getTextController();

			// Since this is only called when creating new text, at that time fileToBeOpenedOrImported is null!
			textController.setFileToBeOpenedOrImported(null);

			textController.rtbTextUpdate(false);
			textController.updateTextPaneStats();
			textController.modify();
		}

		dialog.dispose();
	}

}
