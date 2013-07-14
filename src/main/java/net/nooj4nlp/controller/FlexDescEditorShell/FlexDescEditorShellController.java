package net.nooj4nlp.controller.FlexDescEditorShell;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;

public class FlexDescEditorShellController
{

	private JTextPane textPane;
	private FlexDescEditorShell shell;
	private String fullName;
	private boolean modified;
	private GramType grammartype;
	private Language iLan, oLan;

	private ArrayList<Object> coloredSequences;
	private boolean coloring, colored;

	private FindReplaceDialog findReplaceDialog;

	public FlexDescEditorShellController(JTextPane textPane, FlexDescEditorShell flexDescEditorShell)
	{
		this.textPane = textPane;
		this.shell = flexDescEditorShell;
		modified = false;
		fullName = null;
		shell.setTitle("Untitled");
		findReplaceDialog = null;
	}

	public boolean loadFromFile(String fullname)
	{
		fullName = fullname;
		BufferedReader sr = null;
		StringBuilder builder = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));
		}
		catch (FileNotFoundException e)
		{
			if (fullname.equals(""))
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Filename cannot be empty!", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File \"" + fullname + "\" not found!",
						"NooJ", JOptionPane.INFORMATION_MESSAGE);

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

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: Cannot load file " + fullname, "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if (!checkHeader())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Header file is invalid",
					"Grammar file is corrupted", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		String gtname = getGrammarTypeName();
		if (gtname == null)
		{
			String ext = org.apache.commons.io.FilenameUtils.getExtension(fullname);
			if (ext.equals("flx") || ext.equals("FLX"))
				gtname = "FLX";
			else
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot process grammar type ." + ext,
						"Grammar file extension error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		if (gtname.equals("FLX"))
			this.grammartype = GramType.FLX;
		else if (gtname.equals("MORPHO"))
			this.grammartype = GramType.MORPHO;
		else
			this.grammartype = GramType.SYNTAX;
		if (grammartype == GramType.FLX)
		{
			iLan = new Language(getLanguage());
			if (iLan == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot process language in file " + fullname,
						"Grammar file is corrupted", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			oLan = null;
		}
		else
		{
			iLan = new Language(getILanguage());
			if (iLan == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot process language in file " + fullname,
						"Grammar file is corrupted", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			oLan = new Language(getOLanguage());
			if (oLan == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot process output language in file "
						+ fullname, "Grammar file is corrupted", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		modified = false;
		shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName));
		textPane.setCaretPosition(0);
		
		return true;
	}

	public void saveFlexDesc()
	{
		if (fullName == null)
			saveAsFlexDesc();
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

	public void saveAsFlexDesc()
	{
		JFileChooser chooser = Launcher.getSaveGramChooser();
		if (fullName != null)
			chooser.setSelectedFile(new File(fullName));
		else
			chooser.setSelectedFile(new File(""));

		String ext = null;
		if (grammartype == GramType.SYNTAX)
		{
			File file = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(iLan.isoName, "Syntactic Analysis")));
			chooser.setCurrentDirectory(file);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Syntactic Grammar (*.nog)",
					Constants.JNOG_EXTENSION);
			chooser.addChoosableFileFilter(filter);
			chooser.setFileFilter(filter);
			ext = "." + Constants.JNOG_EXTENSION;
		}
		else if (grammartype == GramType.MORPHO)
		{
			File file = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(iLan.isoName, "Lexical Analysis")));
			chooser.setCurrentDirectory(file);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Morphological Grammar (*.nom)", "nom");
			chooser.addChoosableFileFilter(filter);
			chooser.setFileFilter(filter);
			ext = "."+ Constants.JNOM_EXTENSION;
		}
		else
		{
			File file = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(iLan.isoName, "Lexical Analysis")));
			chooser.setCurrentDirectory(file);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Inflect/Deriv. Grammar (*.nof)",
					Constants.JNOF_EXTENSION);
			chooser.addChoosableFileFilter(filter);
			chooser.setFileFilter(filter);
			ext = "." + Constants.JNOF_EXTENSION;
		}

		if (chooser.showSaveDialog(shell) != JFileChooser.APPROVE_OPTION)
			return;

		String fileStr = chooser.getSelectedFile().getAbsolutePath();
		if (!fileStr.endsWith(ext))
			fileStr += ext;

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

	public void saveFlexDescForNooJ()
	{
		String languagename = iLan.isoName;

		String dirname = null;
		if (grammartype == GramType.SYNTAX)
			dirname = org.apache.commons.io.FilenameUtils.concat(
					org.apache.commons.io.FilenameUtils.concat(Paths.applicationDir, "resources"),
					org.apache.commons.io.FilenameUtils.concat("initial",
							org.apache.commons.io.FilenameUtils.concat(languagename, "Syntactic Analysis")));
		else
			dirname = org.apache.commons.io.FilenameUtils.concat(
					org.apache.commons.io.FilenameUtils.concat(Paths.applicationDir, "resources"),
					org.apache.commons.io.FilenameUtils.concat(languagename, "Lexical Analysis"));
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

		JOptionPane.showMessageDialog(shell, "File " + noojname + " has been updated.", "NooJ Update",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void save()
	{
		if (fullName == null)
			saveAsFlexDesc();
		else
			save(fullName, false);
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
			writer=new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(fullname), "UTF8"));
			writer.write(textPane.getText());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: cannot save inflectional description",
					"NooJ", JOptionPane.ERROR_MESSAGE);
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

	public void initLoad(GramType grammartype, Language iLan, Language oLan, String author, String institution)
	{
		this.grammartype = grammartype;
		this.iLan = iLan;
		this.oLan = oLan;

		textPane.setText("");
		StringBuilder builder = new StringBuilder();

		builder.append("# NooJ V4\n");
		builder.append("#\n");

		if (this.grammartype == GramType.FLX)
			builder.append("# Inflectional/Derivational description\n");
		else if (this.grammartype == GramType.MORPHO)
			builder.append("# Morphological grammar\n");
		else
			builder.append("# Syntactic grammar\n");

		if (author != null && !author.equals(""))
			builder.append("# Author: " + author + "\n");
		if (institution != null && !institution.equals(""))
			builder.append("# Institution: " + institution + "\n");
		builder.append("#\n");

		if (this.grammartype == GramType.FLX)
			builder.append("# Language is: " + iLan.isoName + "\n");
		else
		{
			builder.append("# Input Language is: " + iLan.isoName + "\n");
			builder.append("# Output Language is: " + oLan.isoName + "\n");
		}
		builder.append("#\n");
		builder.append("# Special Characters: '=' '<' '>' '\\' '\"' ':' '+' '/' '#' ';'\n");
		builder.append("#\n");

		if (this.grammartype == GramType.FLX)
		{
			builder.append("# Generic Commands:\n");
			builder.append("# <B>: keyboard Backspace\n");
			builder.append("# <C>: change Case\n");
			builder.append("# <D>: Duplicate current char\n");
			builder.append("# <E>: Empty string\n");
			builder.append("# <L>: keyboard Left arrow\n");
			builder.append("# <N>: go to end of Next word form\n");
			builder.append("# <P>: go to end of Previous word form\n");
			builder.append("# <R>: keyboard Right arrow\n");
			builder.append("# <S>: delete/Suppress current char\n");
			builder.append("# Arguments for commands <B>, <L>, <N>, <P>, <R>, <S>:\n");
			builder.append("# xx number: repeat xx times\n");
			builder.append("# W: whole word\n");
			builder.append("# Examples\n");
			builder.append("# <R3>: go right 3 times\n");
			builder.append("# <LW>: go to beg. of word\n");
			builder.append("#\n");
			builder.append("# Language-Specific Commands:\n");
			builder.append(iLan.inflectionCommands() + "\n");
		}
		else
		{
			builder.append("# Special Start Rule: Main\n");
			builder.append("#\n\n");
			builder.append("Main = ");
		}

		textPane.setText(builder.toString());
	}

	private boolean checkHeader()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return false;
		javax.swing.text.Element lineElem = rootElem.getElement(0);
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
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (!lineText.equals("# NooJ V1") && !lineText.equals("# NooJ V2") && !lineText.equals("# NooJ V3")&& !lineText.equals("# NooJ V4"))
			return false;

		return true;
	}

	private String getGrammarTypeName()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() < 3)
			return null;

		// compatibility with NooJ V1
		javax.swing.text.Element lineElem = rootElem.getElement(1);
		if (lineElem == null)
			return null;
		int lineStart = lineElem.getStartOffset();
		int lineEnd = lineElem.getEndOffset();
		String line;
		try
		{
			line = document.getText(lineStart, lineEnd - lineStart).trim();
		}
		catch (BadLocationException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (line.equals("# Inflectional/Derivational Description"))
			return "FLX";

		// NooJ V2 and higher
		lineElem = rootElem.getElement(2);
		if (lineElem == null)
			return null;
		lineStart = lineElem.getStartOffset();
		lineEnd = lineElem.getEndOffset();
		try
		{
			line = document.getText(lineStart, lineEnd - lineStart).trim();
		}
		catch (BadLocationException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (line.equals("# Inflectional/Derivational description"))
			return "FLX";
		if (line.equals("# Morphological grammar"))
			return "MORPHO";
		if (line.equals("# Syntactic grammar"))
			return "SYNTAX";
		return null;
	}

	private String getLanguage()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return null;
		for (int i = 0; true; i++)
		{
			javax.swing.text.Element lineElem = rootElem.getElement(i);
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
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return null;
			}
			if (lineText.length() < 17)
				continue;
			String pref = lineText.substring(0, 15);
			if (pref.equals("# Language is: "))
				return lineText.substring(15);
		}
	}

	private String getILanguage()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return null;
		for (int i = 0; true; i++)
		{
			javax.swing.text.Element lineElem = rootElem.getElement(i);
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
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return null;
			}
			if (lineText.length() < 23)
				continue;
			String pref = lineText.substring(0, 21);
			if (pref.equals("# Input Language is: "))
				return lineText.substring(21);
		}
	}

	private String getOLanguage()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return null;
		for (int i = 0; true; i++)
		{
			javax.swing.text.Element lineElem = rootElem.getElement(i);
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
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return null;
			}
			if (lineText.length() < 24)
				continue;
			String pref = lineText.substring(0, 22);
			if (pref.equals("# Output Language is: "))
				return lineText.substring(22);
		}
	}

	public void modify()
	{
		if (coloring)
			return;

		if (colored)
		{
			colored = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					StyledDocument doc = textPane.getStyledDocument();
					doc.setCharacterAttributes(0, textPane.getText().length(), textPane.getStyle("black"), true);
					int x = textPane.getCaretPosition();
					textPane.setCaretPosition(0);
					textPane.setCaretPosition(x);
				}
			});
		}
		modified = true;
		if (fullName == null)
			shell.setTitle("Untitled [Modified]");
		else
			shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName) + " [Modified]");
	}

	public void close()
	{
		if (findReplaceDialog != null)
		{
			findReplaceDialog.dispose();
			setFindReplaceDialog(null);
		}

		if (this.modified && Launcher.projectMode)
		{
			int code;
			if (this.grammartype == GramType.FLX)
				code = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(),
						"Save inflectional/derivational description?",
						"NooJ: inflectional description file has not been not saved", JOptionPane.YES_NO_CANCEL_OPTION);
			else if (this.grammartype == GramType.MORPHO)
				code = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Save morphological grammar?",
						"NooJ: morphological grammar has not been not saved", JOptionPane.YES_NO_CANCEL_OPTION);
			else
				code = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Save syntactic grammar?",
						"NooJ: syntactic grammar has not been not saved", JOptionPane.YES_NO_CANCEL_OPTION);
			if (code == JOptionPane.CANCEL_OPTION)
				return;
			if (code == JOptionPane.YES_OPTION)
				this.save();
		}
		shell.dispose();
	}

	public boolean computeColoring()
	{
		coloredSequences = new ArrayList<Object>();

		if (!checkHeader())
			return false;

		colorrules();

		return true;
	}

	private void colorrules()
	{
		int i, j;
		String buf = textPane.getText();
		i = 0;

		while (true)
		{
			// skip white spaces and comments
			for (; i < buf.length() && Character.isWhitespace(buf.charAt(i)); i++)
				;
			if (i == buf.length())
				return; // no more rule
			while (buf.charAt(i) == '#')
			{
				i = colorcomment(i);
				for (; i < buf.length() && Character.isWhitespace(buf.charAt(i)); i++)
					;
				if (i == buf.length())
					return; // no more rule
			}

			// get rule name
			for (j = 0; i + j < buf.length() && buf.charAt(i + j) != '=' && buf.charAt(i + j) != '#'; j++)
				;
			// misplaced spaces?
			int k1, k2;
			for (k1 = i; buf.charAt(k1) == ' '; k1++)
				;
			for (k2 = i + j - 1; buf.charAt(k2) == ' '; k2--)
				;
			String buf2 = buf.substring(k1, k2);
			if (buf2.indexOf(' ') != -1)
			{
				// problem: no rule name before '='
				coloredSequences.add(i);
				j = buf.length() - i;
				coloredSequences.add(j);
				coloredSequences.add(Color.RED);
				JOptionPane.showMessageDialog(shell, "There is a space character in rule name",
						"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			if (j == 0)
			{
				// problem: no rule name before '='
				coloredSequences.add(i);
				j = buf.length() - i;
				coloredSequences.add(j);
				coloredSequences.add(Color.RED);
				JOptionPane.showMessageDialog(shell, "There is no name for rule definition", "NooJ rule syntax error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (i + j == buf.length())
			{
				// problem: a rule name with no '='
				coloredSequences.add(i);
				j = buf.length() - i;
				coloredSequences.add(j);
				coloredSequences.add(Color.RED);
				JOptionPane.showMessageDialog(shell, "There is no '=' in rule definition", "NooJ rule syntax error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (buf.charAt(i + j) == '#')
			{
				// problem: a comment inside a rule name
				coloredSequences.add(i);
				j = buf.length() - i;
				coloredSequences.add(j);
				coloredSequences.add(Color.RED);
				JOptionPane.showMessageDialog(shell, "There is a comment inside a rule name", "NooJ rule syntax error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			coloredSequences.add(i);
			coloredSequences.add(j + 1);
			coloredSequences.add(Color.BLUE);

			i += j + 1;

			// parse right side; get ";"
			for (j = 0; i + j < buf.length() && buf.charAt(i + j) != ';'; j++)
			{
				if (buf.charAt(i + j) == '"')
				{
					for (j++; i + j < buf.length() && buf.charAt(i + j) != '"'; j++)
						;
					if (i + j == buf.length())
					{
						// problem: no closing '"'
						coloredSequences.add(i);
						coloredSequences.add(j);
						coloredSequences.add(Color.RED);
						JOptionPane.showMessageDialog(shell, "There is no closing \" in rule",
								"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					continue;
				}
				else if (buf.charAt(i + j) == '\\')
				{
					j++;
					continue;
				}
				else if (buf.charAt(i + j) == ':')
				{
					int k;
					for (k = 1; i + j + k < buf.length() && buf.charAt(i + j + k) != ' '
							&& buf.charAt(i + j + k) != '/' && buf.charAt(i + j + k) != '+'
							&& buf.charAt(i + j + k) != '|' && buf.charAt(i + j + k) != ')'
							&& buf.charAt(i + j + k) != ';' && buf.charAt(i + j + k) != '#'; k++)
						;
					if (i + j == buf.length())
					{
						// problem: unexpected ending
						coloredSequences.add(i);
						coloredSequences.add(buf.length() - i);
						coloredSequences.add(Color.RED);
						JOptionPane.showMessageDialog(shell, "There is no ending ';' in rule",
								"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					coloredSequences.add(i + j);
					coloredSequences.add(k);
					coloredSequences.add(Color.ORANGE);
					j += k - 1;
					continue;
				}
				else if (buf.charAt(i + j) == '/')
				{
					int k;
					if (buf.charAt(i + j + 1) == '"')
					{
						for (k = 2; i + j + k < buf.length() && buf.charAt(i + j + k) != '"'; k++)
							;
						if (i + j + k == buf.length())
						{
							// problem: no closing '"'
							coloredSequences.add(i);
							coloredSequences.add(buf.length() - i);
							coloredSequences.add(Color.RED);
							JOptionPane.showMessageDialog(shell, "There is no closing \" in rule",
									"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						k++;
					}
					else
					{
						for (k = 1; i + j + k < buf.length() && buf.charAt(i + j + k) != ' '
								&& buf.charAt(i + j + k) != '|' && buf.charAt(i + j + k) != ')'
								&& buf.charAt(i + j + k) != ';'; k++)
							;
					}
					if (i + j + k == buf.length())
					{
						// problem: unexpected ending
						coloredSequences.add(i);
						coloredSequences.add(buf.length() - i);
						coloredSequences.add(Color.RED);
						JOptionPane.showMessageDialog(shell, "Unexpected ending: no ';' in rule",
								"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					coloredSequences.add(i + j);
					coloredSequences.add(k);
					coloredSequences.add(Color.DARK_GRAY);
					j += k - 1;
					continue;
				}
				else if (buf.charAt(i + j) == '$' && buf.length() > i + j + 1
						&& (buf.charAt(i + j + 1) == '(' || buf.charAt(i + j + 1) == ')'))
				{
					int k;
					for (k = 2; i + j + k < buf.length() && buf.charAt(i + j + k) != ' '
							&& buf.charAt(i + j + k) != '|' && buf.charAt(i + j + k) != ')'
							&& buf.charAt(i + j + k) != ';'; k++)
						;
					if (i + j + k == buf.length())
					{
						// problem: unexpected ending
						coloredSequences.add(i);
						coloredSequences.add(buf.length() - i);
						coloredSequences.add(Color.RED);
						JOptionPane.showMessageDialog(shell, "Unexpected ending: no ';' in rule",
								"NooJ rule syntax error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					coloredSequences.add(i + j);
					coloredSequences.add(k);
					coloredSequences.add(Color.RED);
					j += k - 1;
					continue;
				}
				// no comment in rule: '#' is used in syntactic grammars (meaning "no space")
			
				if (buf.charAt(i + j) == '=')
				{
					// problem: no ending ";": we are reading into next rule
					coloredSequences.add(i);
					j = buf.length() - i;
					coloredSequences.add(j);
					coloredSequences.add(Color.RED);
					JOptionPane.showMessageDialog(shell, "There is no ending ';' in rule", "NooJ rule syntax error",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			if (i + j == buf.length())
			{
				// problem: no ending ";"
				coloredSequences.add(i);
				coloredSequences.add(j - 1);
				coloredSequences.add(Color.RED);
				JOptionPane.showMessageDialog(shell, "There is no ending ';' in rule", "NooJ rule syntax error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			else
			{
				// found the ending ";"
				coloredSequences.add(i + j);
				coloredSequences.add(1);
				coloredSequences.add(Color.BLUE);
			}
			i += j + 1;
		}
	}

	private int colorcomment(int ibuf)
	{
		String buf = textPane.getText();

		int i;
		for (i = 1; ibuf + i < buf.length() && buf.charAt(ibuf + i) != '\n'; i++)
			;
		String commentline = buf.substring(ibuf, ibuf + i);
		if (commentline.length() == 17)
		{
			String pref = commentline.substring(0, 15);
			if (pref.equals("# Language is: "))
			{
				
				coloredSequences.add(ibuf);
				coloredSequences.add(17);
				coloredSequences.add(Color.GREEN);
				return ibuf + i;
			}
		}
		if (commentline.length() == 23)
		{
			String pref = commentline.substring(0, 21);
			if (pref.equals("# Input Language is: "))
			{
				
				coloredSequences.add(ibuf);
				coloredSequences.add(23);
				coloredSequences.add(Color.GREEN);
				return ibuf + i;
			}
		}
		if (commentline.length() == 24)
		{
			String pref = commentline.substring(0, 22);
			if (pref.equals("# Output Language is: "))
			{
				
				coloredSequences.add(ibuf);
				coloredSequences.add(24);
				coloredSequences.add(Color.GREEN);
				return ibuf + i;
			}
		}
		if (commentline.indexOf("# Author: ") == 0)
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(commentline.length());
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else if (commentline.indexOf("# Institution: ") == 0)
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(commentline.length());
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else if (commentline.equals("# NooJ V1") || commentline.equals("# NooJ V2") || commentline.equals("# NooJ V3") || commentline.equals("# NooJ V4"))
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(19);
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else if (commentline.equals("# Syntactic grammar"))
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(19);
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else if (commentline.equals("# Morphological grammar"))
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(23);
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else if (commentline.equals("# Inflectional/Derivational description"))
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(23);
			coloredSequences.add(Color.GREEN);
			return ibuf + i;
		}
		else
		{
			coloredSequences.add(ibuf);
			coloredSequences.add(i);
			coloredSequences.add(Color.GREEN);
		}
		return ibuf + i;
	}

	public void totalColoring()
	{
		if (coloredSequences == null || coloredSequences.size() == 0)
			return;
		coloring = true;

		StyledDocument doc = textPane.getStyledDocument();
		doc.setCharacterAttributes(0, textPane.getText().length(), textPane.getStyle("red"), true);

		for (int i = 0; i < coloredSequences.size(); i += 3)
		{
			int s = (Integer) coloredSequences.get(i);
			int l = (Integer) coloredSequences.get(i + 1);
			Color c = (Color) coloredSequences.get(i + 2);
			doc.setCharacterAttributes(s, l, textPane.getStyle("black"), true);
			if (c == Color.RED)
				doc.setCharacterAttributes(s, l, textPane.getStyle("red"), true);
			else if (c == Color.GREEN)
				doc.setCharacterAttributes(s, l, textPane.getStyle("green"), true);
			else if (c == Color.BLUE)
				doc.setCharacterAttributes(s, l, textPane.getStyle("blue"), true);
			else if (c == Color.ORANGE)
				doc.setCharacterAttributes(s, l, textPane.getStyle("orange"), true);
			else if (c == Color.DARK_GRAY)
				doc.setCharacterAttributes(s, l, textPane.getStyle("gray"), true);
		}

		coloring = false;
		colored = true;
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

	public Language getiLan()
	{
		return iLan;
	}
}