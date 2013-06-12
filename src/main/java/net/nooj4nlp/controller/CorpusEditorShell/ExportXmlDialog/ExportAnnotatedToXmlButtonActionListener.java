package net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.ExportXmlDialog;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class ExportAnnotatedToXmlButtonActionListener implements ActionListener
{
	private ExportXmlDialog exportXmlDialog;

	public ExportAnnotatedToXmlButtonActionListener(ExportXmlDialog exportXmlDialog)
	{
		super();
		this.exportXmlDialog = exportXmlDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String[] xmlAnnotations;

		if (exportXmlDialog.getRdbtnAllSyntax().isSelected())
		{
			xmlAnnotations = new String[1];
			xmlAnnotations[0] = "<SYNTAX>";
		}
		else
		{
			// Get only first 3 annotations
			int nbOfAnnotations = 0;
			ArrayList<String> tmp = new ArrayList<String>();

			String cbSelectedText = exportXmlDialog.getCobxXmlAnnotations().getSelectedItem().toString();
			for (int i = 0; i < cbSelectedText.length(); i++)
			{
				if (cbSelectedText.substring(i, i + 1).equals('<'))
				{
					int j;

					for (j = 0; i + j < cbSelectedText.length()
							&& !cbSelectedText.substring(i + j, i + j + 1).equals('>'); j++)
						;

					tmp.add(cbSelectedText.substring(i, i + j + 1));
					nbOfAnnotations++;

					if (nbOfAnnotations >= 3)
						break;
					i += j;
				}
			}

			if (tmp.size() == 0)
				xmlAnnotations = null;
			else
			{
				xmlAnnotations = new String[tmp.size()];
				for (int i = 0; i < tmp.size(); i++)
					xmlAnnotations[i] = tmp.get(i);
			}

			if (xmlAnnotations == null || xmlAnnotations.length == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ENTER_ANNOT_MESSAGE,
						Constants.ENTER_ANNOT_CAPTION, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			for (int i = 0; i < xmlAnnotations.length; i++)
			{
				if (xmlAnnotations[i].equals("<TRANS>"))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.TRANS_ANNOT_MESSAGE,
							Constants.TRANS_ANNOT_CAPTION, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}

		// Different procedure in case this is opened from corpus context or from text context.
		if (exportXmlDialog.isCorpus())
		{
			CorpusEditorShellController controller = exportXmlDialog.getCorpusController();

			// Compute Lan
			Language lan = controller.getCorpus().lan;

			String corpusDirName = controller.getFullPath() + Constants.DIRECTORY_SUFFIX;
			String dName;

			if (Launcher.projectMode)
			{
				File dir = new File(Paths.projectDir);
				dName = dir.getParent(); // Outside of the project .nop file
			}
			else
			{
				File dir = new File(controller.getFullPath());
				dName = dir.getParent(); // Beside the corpus .noc file
			}

			for (String fName : controller.getCorpus().listOfFileTexts)
			{
				String fullName = corpusDirName + System.getProperty("file.separator") + fName;
				try
				{
					Ntext myText = Ntext.loadForCorpus(fullName, lan, controller.getCorpus().multiplier);
					if (myText == null)
						continue;

					String xmlFileName = dName + System.getProperty("file.separator")
							+ FilenameUtils.removeExtension(fName) + ".xml.txt";
					exportXmlAnnotations(xmlFileName, myText, controller.getCorpus().annotations, xmlAnnotations,
							false, exportXmlDialog.getChbxOnly().isSelected(), lan);
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ANNOT_SUCCESS_MESSAGE + dName,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

			// Close export dialog window
			exportXmlDialog.dispose();
		}
		else
		{
			TextEditorShellController textController = exportXmlDialog.getTextController();

			String xmlFileName = null;

			if (Launcher.projectMode)
			{
				File dir = new File(Paths.projectDir);
				String dName = dir.getParent(); // Outside of the project .nop file
				xmlFileName = dName + System.getProperty("file.separator")
						+ FilenameUtils.removeExtension(textController.getTextName()) + ".xml.txt";
			}
			else
			{
				xmlFileName = FilenameUtils.removeExtension(textController.getTextName()) + ".xml.txt";
			}

			// Compute Lan
			Ntext text = textController.getMyText();
			Language lan = text.getLanguage();
			exportXmlAnnotations(xmlFileName, text, text.annotations, xmlAnnotations, true, exportXmlDialog
					.getChbxOnly().isSelected(), lan);
		}
	}

	private void exportXmlAnnotations(String xmlFileName, Ntext text, ArrayList<Object> annotations,
			String[] xmlAnnotations, boolean interactive, boolean filterOut, Language lan)
	{
		// Main loop: parse each text unit
		PrintWriter pw = null;
		try
		{
			File xmlFile = new File(xmlFileName);
			xmlFile.createNewFile();

			pw = new PrintWriter(xmlFileName);

			int currentAddress = 0;
			for (int itu = 1; itu <= text.nbOfTextUnits; itu++)
			{
				if (currentAddress < text.mft.tuAddresses[itu])
				{
					if (!filterOut)
						pw.write(text.buffer.substring(currentAddress, text.mft.tuAddresses[itu]));
				}

				String currentLine = text.buffer.substring(text.mft.tuAddresses[itu], text.mft.tuLengths[itu]
						+ text.mft.tuAddresses[itu]);
				currentAddress = text.mft.tuLengths[itu] + text.mft.tuAddresses[itu];

				text.buildXmlTaggedText(currentLine, 0, filterOut, pw, itu, annotations, xmlAnnotations, lan, false);
			}

			if (!filterOut)
				pw.write(text.buffer.substring(currentAddress));
			pw.close();

			if (interactive)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.EXPORT_SUCCESS_MESSAGE + xmlFileName, Constants.NOOJ_APPLICATION_NAME,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.NOOJ_CANNOT_CREATE
					+ xmlFileName, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_EXPORT_CORPUS_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}
