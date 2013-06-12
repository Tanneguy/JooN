package net.nooj4nlp.controller.DictionaryDialog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Dictionary;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ErrorShell;

public class DictionaryDialogController
{
	private FindReplaceDialog findReplaceDialog;
	private JTextField txtDictionaryName;
	private JCheckBox chckbxCheckAgreement;
	private JPanel pnlDisplayDictionary;
	private JTextPane txtDictionary;
	private JButton btnNewButton, btnEdit, btnInflect, btnCompile;
	private TitledBorder titledBorder;

	public Language lan = null;

	public DictionaryDialogController(JTextField txtDictionaryName, JCheckBox chckbxCheckAgreement,
			JPanel pnlDisplayDictionary, JTextPane txtDictionary, JButton btnNewButton, JButton btnEdit,
			JButton btnInflect, JButton btnCompile, TitledBorder titledBorder)
	{
		super();
		this.txtDictionaryName = txtDictionaryName;
		this.chckbxCheckAgreement = chckbxCheckAgreement;
		this.pnlDisplayDictionary = pnlDisplayDictionary;
		this.txtDictionary = txtDictionary;
		this.btnNewButton = btnNewButton;
		this.btnEdit = btnEdit;
		this.btnInflect = btnInflect;
		this.btnCompile = btnCompile;
		this.titledBorder = titledBorder;
	}

	public JTextField getTxtDictionaryName()
	{
		return txtDictionaryName;
	}

	public JCheckBox getChckbxCheckAgreement()
	{
		return chckbxCheckAgreement;
	}

	public JPanel getPnlDisplayDictionary()
	{
		return pnlDisplayDictionary;
	}

	public TitledBorder getTitledBorder()
	{
		return titledBorder;
	}

	public JTextPane getTxtDictionary()
	{
		return txtDictionary;
	}

	static public int count(String fullname)
	{
		BufferedReader bufferedReader = null;
		int itu = 0;
		try
		{
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));

			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				if (line.equals("") || line.charAt(0) == '#')
				{
					continue;
				}
				itu++;
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bufferedReader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return itu;
	}

	static public int count(JTextPane rtbText)
	{
		int nbofentries = 0;
		Document document = rtbText.getDocument();
		Element rootElem = document.getDefaultRootElement();
		for (int j = 0; j < rootElem.getElementCount(); j++)
		{
			Element lineElem = rootElem.getElement(j);
			int lineStart = lineElem.getStartOffset();
			int lineEnd = lineElem.getEndOffset();
			String lineText = null;
			try
			{
				lineText = document.getText(lineStart, lineEnd - lineStart).trim();
			}
			catch (BadLocationException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			}
			if (lineText.equals(""))
				continue;

			// remove comments
			int i;
			for (i = 0; i < lineText.length() && lineText.charAt(i) != '#'; i++)
				if (lineText.charAt(i) == '\\')
				{
					i++;
					continue;
				}
			String line;
			if (i < lineText.length() && lineText.charAt(i) == '#')
				line = lineText.substring(0, i);
			else
				line = lineText;
			if (line.equals(""))
				continue;

			nbofentries++;
		}
		return nbofentries;
	}

	public boolean loadLines(int firstline, int nboflines)
	{
		txtDictionary.setText("");
		BufferedReader bufferedReader = null;
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(txtDictionaryName.getText()),
					"UTF8"));

			int iline = 0;
			StringBuilder builder = new StringBuilder();
			for (String line = bufferedReader.readLine(); line != null && iline < firstline + nboflines; line = bufferedReader
					.readLine(), iline++)
			{
				if (iline >= firstline)
					builder.append(line + "\n");
			}
			bufferedReader.close();
			txtDictionary.setText(builder.toString());

			if (iline >= firstline + nboflines)
			{
				txtDictionary.setText(txtDictionary.getText() + "...");
				txtDictionary.select(0, 0);
				return false;
			}
			else
			{
				txtDictionary.select(0, 0);
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bufferedReader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
		return false;
	}

	public static String getDictionaryContent(String fullname)
	{
		BufferedReader bufferedReader = null;
		StringBuilder builder = null;
		try
		{
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));

			builder = new StringBuilder();
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				builder.append(line + "\n");
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			if (fullname.equals(""))
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Filename cannot be empty!", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Error occurred while reading file \""
						+ fullname + "\"!", "NooJ", JOptionPane.INFORMATION_MESSAGE);

			try
			{
				bufferedReader.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				return null;
			}

			return null;
		}

		try
		{
			bufferedReader.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return builder.toString();
	}

	public void desactivateOps()
	{
		btnNewButton.setEnabled(false);
		btnEdit.setEnabled(false);
		btnInflect.setEnabled(false);
		btnCompile.setEnabled(false);
		txtDictionaryName.setEnabled(false);
	}

	public void reactivateOps()
	{
		btnNewButton.setEnabled(true);
		btnEdit.setEnabled(true);
		btnInflect.setEnabled(true);
		btnCompile.setEnabled(true);
		txtDictionaryName.setEnabled(true);

		if (Dictionary.errMessage != null && Dictionary.errMessage.toString().length() > 0)
		{
			String errorMessage = Dictionary.errMessage.toString();
			Dic.writeLog(errorMessage);

			ErrorShell errorShell = new ErrorShell();
			errorShell.getTxtError().setText(errorShell.getTxtError().getText() + errorMessage);
			Launcher.getDesktopPane().add(errorShell);
			errorShell.setVisible(true);
		}
	}

	public FindReplaceDialog getFindReplaceDialog()
	{
		return findReplaceDialog;
	}

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog)
	{
		this.findReplaceDialog = findReplaceDialog;
	}
}