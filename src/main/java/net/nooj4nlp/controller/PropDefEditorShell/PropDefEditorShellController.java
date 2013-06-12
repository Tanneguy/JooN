package net.nooj4nlp.controller.PropDefEditorShell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.PropDefEditorShell;

public class PropDefEditorShellController
{

	private String fullName;
	private JTextPane textPane;
	private boolean modified;
	private Language lan;
	private PropDefEditorShell shell;
	private FindReplaceDialog findReplaceDialog;

	public PropDefEditorShellController(JTextPane textPane, PropDefEditorShell shell)
	{
		super();
		this.textPane = textPane;
		this.shell = shell;
		this.findReplaceDialog = null;
	}

	private boolean checkHeader()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return false;
		for (int i = 0; i < 5; i++)
		{
			javax.swing.text.Element lineElem = rootElem.getElement(i);
			if (lineElem == null)
				return false;
			int lineStart = lineElem.getStartOffset();
			int lineEnd = lineElem.getEndOffset();
			String lineText;
			try
			{
				lineText = document.getText(lineStart, lineEnd - lineStart).trim();
			}
			catch (BadLocationException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return false;
			}
			switch (i)
			{
				case 0:
					if (!lineText.equals("# NooJ V1") && !lineText.equals("# NooJ V2") && !lineText.equals("# NooJ V3") && !lineText.equals("# NooJ V4"))
						return false;
					break;
				case 1:
					if (!lineText.equals("# Dictionary properties' definition"))
						return false;
					break;
				case 2:
					if (!lineText.equals("#"))
						return false;
					break;
				case 3:
					if (!lineText.substring(0, 15).equals("# Language is: "))
						return false;
					break;
				case 4:
					if (!lineText.equals("#"))
						return false;
					break;
			}
		}
		return true;
	}

	private String getLanguage()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() < 5)
			return null;
		javax.swing.text.Element lineElem = rootElem.getElement(3);
		if (lineElem == null)
			return null;
		int lineStart = lineElem.getStartOffset();
		int lineEnd = lineElem.getEndOffset();
		String lineText;
		try
		{
			lineText = document.getText(lineStart, lineEnd - lineStart).trim();
		}
		catch (BadLocationException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (lineText.length() < 16)
			return null;

		return lineText.substring(15);
	}

	public boolean loadFromFile(String fn)
	{
		fullName = fn;
		BufferedReader sr = null;
		StringBuilder builder = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(fn), "UTF8"));
		}
		catch (FileNotFoundException e)
		{
			if (fn.equals(""))
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Filename cannot be empty!", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File \"" + fn + "\" not found!", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);

			return false;
		}
		catch (UnsupportedEncodingException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_UNSUPPORTED_ENCODING, JOptionPane.ERROR_MESSAGE);
		}
		try
		{
			// load the file
			builder = new StringBuilder();
			for (String line0 = sr.readLine(); line0 != null; line0 = sr.readLine())
			{
				builder.append(line0);
				builder.append("\n");
			}
			textPane.setText(builder.toString());
			sr.close();
		}
		catch (IOException e)
		{
			try
			{
				sr.close();
			}
			catch (IOException e1)
			{
				// Catch block does not do anything - message below should be written in each case.
			}

			JOptionPane
					.showMessageDialog(null, "NooJ: Cannot load file " + fn, "NooJ", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if (!checkHeader())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary properties' definitions format error",
					"NooJ: Header for .def file is invalid", JOptionPane.INFORMATION_MESSAGE);
			textPane.setText("");
			return false;
		}

		lan = new Language(getLanguage());
		if (lan == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot process language in file " + fn,
					"NooJ: Header for .def file is invalid", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		modified = false;
		shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName));
		textPane.setCaretPosition(0);
		return true;
	}

	public void saveDicoDef()
	{
		if (fullName == null)
			saveAsDicoDef();
		else
		{
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
				save();
			}

			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}
		}
	}

	public void saveDicoDefForNooJ()
	{
		String languagename = lan.isoName;
		String dirname = org.apache.commons.io.FilenameUtils.concat(
				org.apache.commons.io.FilenameUtils.concat(Paths.applicationDir, "resources"),
				org.apache.commons.io.FilenameUtils.concat("initial",
						org.apache.commons.io.FilenameUtils.concat(languagename, "Lexical Analysis")));
		String fname = org.apache.commons.io.FilenameUtils.getName(fullName);
		String noojname = org.apache.commons.io.FilenameUtils.concat(dirname, fname);

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			save(noojname, true);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File " + noojname + " has been updated.",
				"NooJ Update", JOptionPane.INFORMATION_MESSAGE);
	}

	public void saveAsDicoDef()
	{
		File directory = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
				org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis")));
		JFileChooser chooser = Launcher.getSaveDefDialogChooser();
		chooser.setCurrentDirectory(directory);
		if (chooser.showSaveDialog(shell) != JFileChooser.APPROVE_OPTION)
			return;

		File file = chooser.getSelectedFile();
		Launcher.getOpenDicChooser().setCurrentDirectory(file);
		Launcher.getSaveDicChooser().setCurrentDirectory(file);
		Launcher.getOpenDefDialogChooser().setCurrentDirectory(file);
		chooser.setCurrentDirectory(file);

		String fileStr = file.getAbsolutePath();
		if (!fileStr.endsWith(".def"))
			fileStr += ".def";

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
			save(fileStr, false);
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}

	public boolean save()
	{
		if (fullName == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					"Cannot save dictionary's properties' definitions", "NooJ: undefined file name",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		save(fullName, false);
		return true;
	}

	private void save(String fullname, boolean forNooJ)
	{
		if (!forNooJ)
		{
			// WARNING IF FILENAME STARTS WITH "_"
			String fname = org.apache.commons.io.FilenameUtils.getName(fullname);
			if (fname.charAt(0) == '_')
			{
				if (JOptionPane.showConfirmDialog(Launcher.getDesktopPane(),
						"WARNING: file name starts with \"_\". Are you sure you want to save it with this prefix?",
						"NooJ: protected resource", JOptionPane.YES_NO_OPTION) != 0)
					return;
			}

			// MANAGE MULTIPLE BACKUPS
			try
			{
				Utilities.savePreviousVersion(fullname, Launcher.preferences.multiplebackups);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: cannot save previous version of file",
						"NooJ", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(fullname));
			writer.write(textPane.getText());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					"NooJ: cannot save dictionary properties' definitions", "NooJ", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
			}
		}

		if (!forNooJ)
			fullName = fullname;
		modified = false;
		shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName));
	}

	public void initLoad(String language)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("# NooJ V4\n");
		builder.append("# Dictionary properties' definition\n");
		builder.append("#\n");
		builder.append("# Language is: " + language + "\n");
		builder.append("#\n");
		builder.append("# Special Characters: '=' '+' '#' ' '\n");
		builder.append("#\n");
		builder.append("# Starting with V3, there is only ONE Dictionary Properties' Definition File per language;\n");
		builder.append("# it should always be named _properties.def and be saved in the \"Lexical Analysis\" folder\n");
		builder.append("#\n");
		builder.append("# List categories and properties associated with features\n");
		builder.append("#   Example: N_Number = m + f;\n");
		builder.append("# Special KEYWORD: INFLECTION lists all inflectional features (used by variables $xF)\n");
		builder.append("#   Example: INFLECTION = m + f + Present + Futur;\n");
		textPane.setText(builder.toString());

		lan = new Language(language);
	}

	public void modify()
	{
		if (modified)
			return;
		modified = true;
		if (fullName == null)
			shell.setTitle("Untitled [Modified]");
		else
			shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName) + " [Modified]");
	}

	public PropDefEditorShell getShell()
	{
		return shell;
	}

	public String getFullName()
	{
		return fullName;
	}

	public FindReplaceDialog getFindReplaceDialog()
	{
		return findReplaceDialog;
	}

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog)
	{
		this.findReplaceDialog = findReplaceDialog;
	}

	public boolean isModified()
	{
		return modified;
	}

	public void close()
	{
		if (findReplaceDialog != null)
		{
			findReplaceDialog.dispose();
			setFindReplaceDialog(null);
		}

		if (modified && Launcher.projectMode)
		{
			int code = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Save inflectional description file?",
					"NooJ: properties' description file has not been not saved", JOptionPane.YES_NO_CANCEL_OPTION);
			if (code == JOptionPane.CANCEL_OPTION)
				return;
			if (code == JOptionPane.YES_OPTION)
				saveDicoDef();
		}

		shell.dispose();
	}

	public Language getLan()
	{
		return lan;
	}

}
