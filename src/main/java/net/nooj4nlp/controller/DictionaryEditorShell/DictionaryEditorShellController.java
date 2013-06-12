package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.engine.ComplexDictionaryParser;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.DicInvItemComparer;
import net.nooj4nlp.engine.DicItemComparer;
import net.nooj4nlp.engine.DictionaryParser;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.ParsingException;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Property;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.ErrorShell;
import java.io.FileOutputStream;

public class DictionaryEditorShellController
{

	private String fullName;
	private Language lan;
	private static JTextPane textPane;
	private HashMap<String, Boolean> prop_inf, prop_cat;
	private HashMap<String, String> properties;
	private boolean modified;
	private DictionaryEditorShell shell;
	private JLabel lblnTus;
	private ErrorShell errorShell;
	private FindReplaceDialog findReplaceDialog;
	private static DicItemComparer dicComparer = null;
	private static DicInvItemComparer dicInvComparer = null;
	private JPanel editorPane;
	private JPanel tablePane;
	private JTable table;
	private JScrollPane tableContainer;
	private static Comparator<String> comparator, comparatorInv;

	public DictionaryEditorShellController(JTextPane textPane, DictionaryEditorShell shell, JLabel lblnTus,
			JPanel editorPane, JPanel tablePane, JTable table, JScrollPane tableContainer)
	{
		super();
		DictionaryEditorShellController.textPane = textPane;
		this.shell = shell;
		this.lblnTus = lblnTus;
		errorShell = new ErrorShell();
		this.editorPane = editorPane;
		this.tablePane = tablePane;
		this.table = table;
		this.tableContainer = tableContainer;
		this.findReplaceDialog = null;

		comparator = new Comparator<String>()
		{
			@Override
			public int compare(String x, String y)
			{
				return lan.sortTexts(x, y, false);
			}
		};

		comparatorInv = new Comparator<String>()
		{
			@Override
			public int compare(String x, String y)
			{
				StringBuilder tmp1 = new StringBuilder();
				StringBuilder tmp2 = new StringBuilder();
				for (int i = x.length() - 1; i >= 0; i--)
					tmp1.append(x.charAt(i));
				for (int i = y.length() - 1; i >= 0; i--)
					tmp2.append(y.charAt(i));
				return lan.sortTexts(tmp1.toString(), tmp2.toString(), false);
			}
		};

	}

	public static DictionaryEditorShell openNooJDictionary(String fullname)
	{
		DictionaryEditorShell editor = null;

		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			editor = new DictionaryEditorShell();
			editor.getController().loadFromFile(fullname);
			Launcher.getDesktopPane().add(editor);
			editor.setVisible(true);
		}
		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}

		return editor;
	}

	public static String getLanguage()
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return null;
		javax.swing.text.Element lineElem = rootElem.getElement(3);
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
		if (line.length() >= 23)
			return line.substring(21);
		return null;
	}

	public static String buildHeader(String isoname)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("# NooJ V4\n");
		sb.append("# Dictionary\n");
		sb.append("#\n");
		sb.append("# Input Language is: " + isoname + "\n");
		sb.append("#\n");
		sb.append("# Alphabetical order is not required.\n");
		sb.append("#\n");
		sb.append("# Use inflectional & derivational paradigms' description files (.nof), e.g.:\n");
		sb.append("# Special Command: #use paradigms.nof\n");
		sb.append("#\n");
		sb.append("# Special Features: +NW (non-word) +FXC (frozen expression component) +UNAMB (unambiguous lexical entry)\n");
		sb.append("#                   +FLX= (inflectional paradigm) +DRV= (derivational paradigm)\n");
		sb.append("#\n");
		sb.append("# Special Characters: '\\' '\"' '+' ',' '#' ' '\n");
		sb.append("#\n");
		return sb.toString();
	}

	public static int sizeOfHeader()
	{
		String header = buildHeader("en");
		return header.length();
	}

	public void initLoad(String l)
	{
		textPane.setText(buildHeader(l));
		lan = new Language(l);
		lblnTus.setText("Dictionary contains " + Integer.toString(DictionaryDialogController.count(textPane))
				+ " entries");
	}

	private boolean checkHeader() // the first four lines are FIXED
	{
		javax.swing.text.Document document = textPane.getDocument();
		javax.swing.text.Element rootElem = document.getDefaultRootElement();
		if (rootElem.getElementCount() == 0)
			return false;
		for (int i = 0; i < 4; i++)
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
					if (!lineText.equals("# NooJ V1") && !lineText.equals("# NooJ V2") && !lineText.equals("# NooJ V3") &&!lineText.equals("# NooJ V4"))
						return false;
					break;
				case 1:
					if (!lineText.equals("# Dictionary"))
						return false;
					break;
				case 2:
					if (!lineText.equals("#"))
						return false;
					break;
				case 3:
					if (!lineText.substring(0, 21).equals("# Input Language is: "))
						return false;
					break;
			}
		}
		return true;
	}

	private boolean loadCategoryPropertiesFeatures(String propertydefinitionfile, RefObject<String> errmessageRef)
	{
		prop_inf = prop_cat = null;
		properties = null;
		errmessageRef.argvalue = null;

		File file = new File(propertydefinitionfile);
		if (!file.exists())
		{
			errmessageRef.argvalue = "Cannot find Property Definition File: " + propertydefinitionfile;
			Dic.writeLog(errmessageRef.argvalue);
			return false;
		}

		BufferedReader sr = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(propertydefinitionfile), "UTF8"));
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		catch (UnsupportedEncodingException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_UNSUPPORTED_ENCODING, JOptionPane.ERROR_MESSAGE);
		}
		try
		{
			String header = sr.readLine();
			if (!header.equals("# NooJ V1") && !header.equals("# NooJ V2") && !header.equals("# NooJ V3")&& !header.equals("# NooJ V4"))
			{
				errmessageRef.argvalue = "Property Definition File Format is invalid in " + propertydefinitionfile;
				Dic.writeLog(errmessageRef.argvalue);
				sr.close();
				return false;
			}

			// load the file
			StringBuilder builder = new StringBuilder();
			for (String line0 = sr.readLine(); line0 != null; line0 = sr.readLine())
				builder.append(line0 + "\n");
			String rawbuf = builder.toString();
			sr.close();
			String buf = Dic.noComment(rawbuf);

			int end;
			for (int ibuf = 0; ibuf < buf.length(); ibuf = end)
			{
				String category = null, property = null;
				String[] features = null;
				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String> propertyRef = new RefObject<String>(property);
				RefObject<String[]> featuresRef = new RefObject<String[]>(features);
				end = Dic.getRule(buf, ibuf, categoryRef, propertyRef, featuresRef, errmessageRef);
				category = categoryRef.argvalue;
				property = propertyRef.argvalue;
				features = featuresRef.argvalue;
				if (end == -1)
				{
					if (errmessageRef.argvalue != null)
					{
						sr.close();
						return false;
					}
					break;
				}

				if (category.equals("INFLECTION"))
				{
					if (prop_inf == null)
						prop_inf = new HashMap<String, Boolean>();
					for (int ifeat = 0; ifeat < features.length; ifeat++)
					{
						if (!prop_inf.containsKey(features[ifeat]))
							prop_inf.put(features[ifeat], true);
					}
				}
				else
				{
					if (prop_cat == null)
						prop_cat = new HashMap<String, Boolean>();
					if (!prop_cat.containsKey(category + "_" + property))
						prop_cat.put(category + "_" + property, true);
					for (int ifeat = 0; ifeat < features.length; ifeat++)
					{
						if (properties == null)
							properties = new HashMap<String, String>();
						String ncat = category + "_" + features[ifeat];
						if (!properties.containsKey(ncat))
							properties.put(ncat, property);
					}
				}
			}
		}
		catch (IOException e)
		{
			try
			{
				if (sr != null)
					sr.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}

			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean relevantProperty(String category, String property)
	{
		if (prop_cat == null)
			return true;

		if (prop_cat.containsKey(category + "_" + property))
			return true;
		else
			return false;
	}

	private boolean loadPropertiesDefinition()
	{
		// get current language directory
		String dname = org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
				org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis"));
		String pname = org.apache.commons.io.FilenameUtils.concat(dname, "_properties.def");
		String errmessage = null;
		RefObject<String> errmessageRef = new RefObject<String>(errmessage);
		if (!loadCategoryPropertiesFeatures(pname, errmessageRef))
		{
			errmessage = errmessageRef.argvalue;
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Warning: " + errmessage, "NooJ WARNING",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
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

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: Cannot load file " + fn, "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if (!checkHeader())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load NooJ Dictionary " + fn,
					"NooJ [.dic Header Format Error]", JOptionPane.INFORMATION_MESSAGE);
			textPane.setText("");
			return false;
		}
		String languagename = getLanguage();
		if (languagename == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load NooJ Dictionary " + fn,
					"NooJ [.dic Header Format Error]", JOptionPane.INFORMATION_MESSAGE);
			textPane.setText("");
			return false;
		}

		// initialize sorters
		lan = new Language(languagename);

		// Right to left
		if (lan.rightToLeft)
		{
			tableContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			;
			table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		modified = false;
		shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName));
		lblnTus.setText("Dictionary contains " + Integer.toString(DictionaryDialogController.count(textPane))
				+ " entries");
		textPane.select(0, 0);
		if (!check())
		{
			Launcher.getDesktopPane().add(errorShell);
			errorShell.setVisible(true);
		}
		return true;
	}

	public boolean loadFromDl(String fullname, String lang, Charset enc, boolean delas)
	{
		lan = new Language(lang);

		if (lan == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Language error", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		BufferedReader sr = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullName), enc));

			errorShell.getTxtError().setText("");
			initLoad(lang);

			StringBuilder text = new StringBuilder();
			String line;
			int nboferrors = 0;
			for (line = sr.readLine(); line != null; line = sr.readLine())
			{
				String errormessage = null;
				RefObject<String> errormessageRef = new RefObject<String>(errormessage);
				String[] lines = null;
				if (delas)
					lines = Dic.convertFromDls(line, errormessageRef);
				else
					lines = Dic.convertFromDlf(line, errormessageRef);
				errormessage = errormessageRef.argvalue;
				if (lines == null)
				{
					if (delas)
						errorShell.getTxtError().append("* DELAS FORMAT ERROR: " + line + "\n");
					else
						errorShell.getTxtError().append("* DELAF FORMAT ERROR: " + line + "\n");
					nboferrors++;
					if (nboferrors >= 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Too many format errors",
								"NooJ: format is probably not DELAS", JOptionPane.ERROR_MESSAGE);
						Launcher.getDesktopPane().add(errorShell);
						errorShell.setVisible(true);
						break;
					}
				}
				else
				{
					for (int i = 0; i < lines.length; i++)
					{
						text.append(lines[i]);
						text.append("\n");
					}
				}
			}

			textPane.setText(textPane.getText() + text.toString());

			sr.close();
		}
		catch (Exception e)
		{
			try
			{
				sr.close();
			}
			catch (IOException e1)
			{
				// Catch block does not do anything - message below should be written in each case.
			}

			
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot open dictionary file", "NooJ: file "
					+ fullname + " is protected or locked", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		String fname = org.apache.commons.io.FilenameUtils.getBaseName(fullname) + ".dic";
		String dname = org.apache.commons.io.FilenameUtils.getFullPath(fullname);
		fullName = org.apache.commons.io.FilenameUtils.concat(dname, fname);

		textPane.setFocusable(true);
		modify();
		if (!errorShell.getTxtError().getText().equals(""))
		{
			Launcher.getDesktopPane().add(errorShell);
			errorShell.setVisible(true);
		}
		return true;
	}

	public void modify()
	{
		if (Launcher.projectMode)
			return;

		if (modified)
			return;

		modified = true;
		if (fullName == null)
			shell.setTitle("Untitled [Modified]");
		else
			shell.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName) + " [Modified]");
		lblnTus.setText("");
	}

	public void save()
	{
		if (fullName == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot save dictionary",
					"NooJ: undefined file name", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		save(fullName, false);
	}

	private void save(String fullname, boolean forNooJ)
	{
		// WARNING IF FILENAME STARTS WITH "_"
		if (!forNooJ)
		{
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
			
			writer = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(fullname), "UTF8"));
			
			writer.write(textPane.getText());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: cannot save dictionary", "NooJ",
					JOptionPane.ERROR_MESSAGE);
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
		lblnTus.setText("Dictionary contains " + Integer.toString(DictionaryDialogController.count(textPane))
				+ " entries");
	}

	public void saveDictionary()
	{
		if (fullName == null)
			saveAsDictionary();
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

	public void saveAsDictionary()
	{
		File directory = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
				org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis")));
		JFileChooser chooser = Launcher.getSaveDicChooser();
		chooser.setCurrentDirectory(directory);
		if (chooser.showSaveDialog(shell) != JFileChooser.APPROVE_OPTION)
			return;

		File file = chooser.getSelectedFile();
		Launcher.getOpenDicChooser().setCurrentDirectory(file);
		chooser.setCurrentDirectory(file);

		String fileStr = file.getAbsolutePath();
		if (!fileStr.endsWith(".dic"))
			fileStr += ".dic";

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

	public void saveDictionaryForNooJ()
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

	public boolean check()
	{
		int cpt = 0;

		Document document = textPane.getDocument();
		Element rootElem = document.getDefaultRootElement();
		for (int i = 0; i < rootElem.getElementCount(); i++)
		{
			Element lineElem = rootElem.getElement(i);
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
			if (lineText.equals(""))
				continue;
			String entry = null, lemma = null, category = null;
			String[] properties = null;
			Integer[] indices = null;
			Property[] features = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			RefObject<String[]> propertiesRef = new RefObject<String[]>(properties);
			try
			{
				DictionaryParser.parse(lineText, entryRef, lemmaRef, categoryRef, propertiesRef, indices, features);

				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				properties = propertiesRef.argvalue;
			}
			catch (ParsingException ex)
			{
				String entry1 = null;
				String[] lexeme1 = null;
				Integer[] indices1 = null;
				String[] properties1 = null;
				Property[] features1 = null;
				RefObject<String> entry1Ref = new RefObject<String>(entry1);
				RefObject<String[]> lexeme1Ref = new RefObject<String[]>(lexeme1);
				RefObject<String[]> properties1Ref = new RefObject<String[]>(properties1);

				// try to parse current line
				try
				{
					if (ComplexDictionaryParser.parse(lineText, entry1Ref, lexeme1Ref, properties1Ref, indices1,
							features1))
					{
						entry1 = entry1Ref.argvalue;
						lexeme1 = lexeme1Ref.argvalue;
						properties1 = properties1Ref.argvalue;
						for (int j = 0; j < lexeme1.length; j++)
						{
							String entry2 = null, lemma2 = null, category2 = null;
							String[] properties2 = null;
							Integer[] indices2 = null;
							Property[] features2 = null;
							RefObject<String> entry2Ref = new RefObject<String>(entry2);
							RefObject<String> lemma2Ref = new RefObject<String>(lemma2);
							RefObject<String> category2Ref = new RefObject<String>(category2);
							RefObject<String[]> properties2Ref = new RefObject<String[]>(properties2);
							try
							{
								// try to parse current line
								DictionaryParser.parse(lexeme1[j], entry2Ref, lemma2Ref, category2Ref, properties2Ref,
										indices2, features2);

								entry2 = entry2Ref.argvalue;
								lemma2 = lemma2Ref.argvalue;
								category2 = category2Ref.argvalue;
								properties2 = properties2Ref.argvalue;
							}
							catch (ParsingException ex1)
							{
								cpt++;
								StringBuilder s = new StringBuilder("At character ");
								int endOfNumber = ex.message.indexOf(':');
								Integer.parseInt(ex.message.substring(13, endOfNumber));
								s.append(ex.message.substring(endOfNumber));
								errorShell.getTxtError().append(
										"Line " + ((Integer) (i + 1)).toString() + " " + ex1.message.toLowerCase()
												+ "\n");
							}
						}
					}
					else
					{
						cpt++;
						errorShell.getTxtError().append(
								"Line " + ((Integer) (i + 1)).toString() + " " + ex.message.toLowerCase() + "\n");
					}
				}
				catch (ParsingException e)
				{
					cpt++;
					errorShell.getTxtError().append(
							"Line " + ((Integer) (i + 1)).toString() + " " + e.message.toLowerCase() + "\n");
				}
			}
			if (cpt > 1000)
				return false;
		}
		if (cpt == 0)
			lblnTus.setText("Dictionary contains " + Integer.toString(DictionaryDialogController.count(textPane))
					+ " entries");
		return (cpt == 0);
	}

	public static void sortLines(ArrayList<String> lines, Language lan)
	{
		if (dicComparer == null)
			dicComparer = new DicItemComparer(lan);

		Collections.sort(lines, dicComparer);

		String previousline = "";
		for (int i = 0; i < lines.size();)
		{
			if (previousline.equals(lines.get(i)))
				lines.remove(i);
			else
			{
				previousline = lines.get(i);
				i++;
			}
		}
	}

	private static void sortInvLines(ArrayList<String> lines, Language lan)
	{
		if (dicInvComparer == null)
			dicInvComparer = new DicInvItemComparer(lan);

		Collections.sort(lines, dicInvComparer);

		String previousline = "";
		for (int i = 0; i < lines.size();)
		{
			if (previousline.equals(lines.get(i)))
				lines.remove(i);
			else
			{
				previousline = lines.get(i);
				i++;
			}
		}
	}

	public void sortDictionary(boolean standardmode)
	{
		ArrayList<String> lines = new ArrayList<String>();

		String rtbtext = textPane.getText();
		String sep = "\n";
		String[] rtblines = rtbtext.split(sep);
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
			if (standardmode)
				sortLines(lines0, this.lan); // standard mode
			else
				sortInvLines(lines0, this.lan); // inverse mode
			lines.addAll(lines0);
		}
		StringBuilder sb = new StringBuilder();

		for (int iline = 0; iline < lines.size(); iline++)
		{
			String line = lines.get(iline);
			if (iline < lines.size() - 1 || !line.equals(""))
				sb.append(line + "\n");
		}
		textPane.setText(sb.toString());
	}

	private ArrayList<String> computeColumns(String[] lexemes, RefObject<Boolean> thereisalemma)
	{
		thereisalemma.argvalue = false;

		ArrayList<String> scolumns = new ArrayList<String>();
		ArrayList<String> mcolumns = new ArrayList<String>();

		loadPropertiesDefinition();

		boolean thereisasynsem = false;
		for (int i = 0; i < lexemes.length; i++)
		{
			String lexeme = lexemes[i].trim();
			if (lexeme.equals("") || lexeme.charAt(0) == '#')
				continue;
			String entry = null, lemma = null, category = null, info = null;
			String[] features = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			RefObject<String> infoRef = new RefObject<String>(info);
			RefObject<String[]> featuresRef = new RefObject<String[]>(features);
			if (Dic.nbOfCommas(lexeme) == 2)
			{
				if (!Dic.parseDELAFFeatureArray(lexeme, entryRef, lemmaRef, categoryRef, featuresRef))
					continue;
				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				features = featuresRef.argvalue;
			}
			else
			{
				if (Dic.parseDELASFeatureArray(lexeme, entryRef, categoryRef, featuresRef))
				{
					entry = entryRef.argvalue;
					category = categoryRef.argvalue;
					features = featuresRef.argvalue;
					lemma = entry;
				}
				else if (Dic.parseContracted(lexeme, entryRef, infoRef))
				{
					entry = entryRef.argvalue;
					info = infoRef.argvalue;
					category = "CONTRACTED";
					lemma = entry;
					features = new String[1];
					features[0] = info;
				}
				else
					continue;
			}
			if (features == null || features.length == 0)
				continue;
			String feat0;
			if (lemma != null && !lemma.equals(""))
				thereisalemma.argvalue = true;
			if (features != null)
			{
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					feat0 = features[ifeat];
					if (feat0.equals(""))
						continue;
					// e.g. feat0 = "m" or "Hum"
					String propname = null, propvalue = null;
					RefObject<String> propnameRef = new RefObject<String>(propname);
					RefObject<String> propvalueRef = new RefObject<String>(propvalue);
					Dic.getProperty(feat0, category, properties, propnameRef, propvalueRef);
					propname = propnameRef.argvalue;
					propvalue = propvalueRef.argvalue;
					// e.g. prop = "Gender" or "Hum"
					int index;
					if (feat0.equals(propname))
					{
						// e.g. "Hum"
						thereisasynsem = true;
					}
					else
					{
						// e.g. "Gender"
						index = mcolumns.indexOf(propname);
						if (index == -1)
							mcolumns.add(propname);
					}
				}
			}
		}

		// first the known properties, then the other ones
		Collections.sort(scolumns, new Comparator<String>()
		{

			@Override
			public int compare(String arg0, String arg1)
			{
				return arg0.compareToIgnoreCase(arg1);
			}
		});
		Collections.sort(mcolumns, new Comparator<String>()
		{
			@Override
			public int compare(String arg0, String arg1)
			{
				return arg0.compareToIgnoreCase(arg1);
			}
		});
		scolumns.addAll(mcolumns);
		if (thereisasynsem)
			scolumns.add("SynSem");
		return scolumns;
	}

	public void fillInLvDicos()
	{
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		String[] lines = textPane.getText().split("\n");
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
		model.setColumnCount(0);

		Boolean thereisalemma = false;
		RefObject<Boolean> thereisalemmaRef = new RefObject<Boolean>(thereisalemma);
		ArrayList<String> columns = computeColumns(lines, thereisalemmaRef);
		thereisalemma = thereisalemmaRef.argvalue;

		if (lan.isoName.equals("fr"))
		{
			model.addColumn("Entr�e");
			if (thereisalemma)
				model.addColumn("S-Lemme");
			model.addColumn("Cat�gorie");
		}
		else
		{
			model.addColumn("Entry");
			if (thereisalemma)
			{
				model.addColumn("S-Lemma");
			}
			model.addColumn("Category");
		}
		for (String prop : columns)
		{
			model.addColumn(prop);
		}

		// fill in lvVoc
		for (String line : lines) // lexical entries
		{
			String lexeme = line.trim();
			if (lexeme.equals("") || lexeme.charAt(0) == '#')
				continue;

			String entry = null, lemma = null, category = null, info = null;
			String[] lexfeatures = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			RefObject<String> infoRef = new RefObject<String>(info);
			RefObject<String[]> lexfeaturesRef = new RefObject<String[]>(lexfeatures);
			if (Dic.nbOfCommas(lexeme) == 2)
			{
				if (!Dic.parseDELAFFeatureArray(lexeme, entryRef, lemmaRef, categoryRef, lexfeaturesRef))
					continue;
				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				lexfeatures = lexfeaturesRef.argvalue;
			}
			else if (Dic.parseDELASFeatureArray(lexeme, entryRef, categoryRef, lexfeaturesRef))
			{
				entry = entryRef.argvalue;
				category = categoryRef.argvalue;
				lexfeatures = lexfeaturesRef.argvalue;
				lemma = entry;
			}
			else if (Dic.parseContracted(lexeme, entryRef, infoRef))
			{
				entry = entryRef.argvalue;
				info = infoRef.argvalue;
				lemma = entry;
				category = "CONTRACTED";
				lexfeatures = new String[1];
				lexfeatures[0] = info;
			}
			else
				continue;

			if (entry == null || category == null)
				continue;
			ArrayList<String> headers = new ArrayList<String>();
			headers.add(entry);
			if (thereisalemma)
				headers.add(lemma);
			headers.add(category);
			for (String colname : columns)
			{
				String featvalue = null;
				if (lexfeatures != null)
				{
					for (String lexfeat : lexfeatures)
					{
						if (lexfeat.equals(""))
							continue;

						String lexpropname = null, lexpropvalue = null;
						RefObject<String> lexpropnameRef = new RefObject<String>(lexpropname);
						RefObject<String> lexpropvalueRef = new RefObject<String>(lexpropvalue);
						Dic.getProperty(lexfeat, category, this.properties, lexpropnameRef, lexpropvalueRef);
						lexpropname = lexpropnameRef.argvalue;
						lexpropvalue = lexpropvalueRef.argvalue;
						if (lexpropname.equals(colname))
						{
							if (featvalue == null) // the first time
							{
								featvalue = lexpropvalue;
							}
							else
							// multiple values for one property
							{
								featvalue += "+" + lexpropvalue;
							}
						}
						else if (colname.equals("SynSem") && lexpropname.equals(lexpropvalue))
						{
							if (featvalue == null) // the first time
							{
								featvalue = lexpropvalue;
							}
							else
							// multiple values for one property
							{
								featvalue += "+" + lexpropvalue;
							}
						}
					}
				}
				if (featvalue != null)
				{
					headers.add(featvalue);
				}
				else
				{
					// column property not in lexeme
					if (relevantProperty(category, colname))
						headers.add("-");
					else
						headers.add(" ");
				}
			}
			String[] sheaders = headers.toArray(new String[headers.size()]);
			model.addRow(sheaders);
		}

		int nblexs = model.getRowCount();
		lblnTus.setText(Integer.toString(nblexs) + " Lexical entries:");

		// resize columns
		for (int vColIndex = 0; vColIndex < table.getColumnCount(); vColIndex++)
		{
			DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
			TableColumn col = colModel.getColumn(vColIndex);
			int width = 0;

			// Get width of column header
			TableCellRenderer renderer = col.getHeaderRenderer();
			if (renderer == null)
			{
				renderer = table.getTableHeader().getDefaultRenderer();
			}
			Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
			width = comp.getPreferredSize().width;

			// Get maximum width of column data
			for (int r = 0; r < table.getRowCount(); r++)
			{
				renderer = table.getCellRenderer(r, vColIndex);
				comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r,
						vColIndex);
				width = Math.max(width, comp.getPreferredSize().width);
			}

			// Set the width
			col.setPreferredWidth(width + 4);
		}

		// Unchecked cast cannot be avoided unless by making a custom class that extends TableModel, and there is really
		// no need for that.
		((TableRowSorter<TableModel>) table.getRowSorter()).toggleSortOrder(0);
	}

	public boolean export(String fullname)
	{
		BufferedWriter sw = null;
		try
		{
			sw = new BufferedWriter(new FileWriter(fullname));
			sw.write(table.getColumnName(0));
			for (int i = 1; i < table.getColumnCount(); i++)
			{
				sw.write("," + table.getColumnName(i));
			}
			sw.write("\n");

			for (int i = 0; i < table.getRowCount(); i++)
			{
				sw.write((String) table.getValueAt(i, 0));
				for (int j = 1; j < table.getColumnCount(); j++)
					sw.write("," + (String) table.getValueAt(i, j));
				sw.write("\n");
			}
			sw.close();
		}
		catch (IOException e)
		{
			try
			{
				sw.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}

			return false;
		}
		return true;
	}

	public void setLblText(String s)
	{
		lblnTus.setText(s);
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}

	public String getFullName()
	{
		return fullName;
	}

	public ErrorShell getErrorShell()
	{
		return errorShell;
	}

	public boolean isModified()
	{
		return modified;
	}

	public DictionaryEditorShell getShell()
	{
		return shell;
	}

	public JPanel getEditorPane()
	{
		return editorPane;
	}

	public JPanel getTablePane()
	{
		return tablePane;
	}

	public Language getLan()
	{
		return lan;
	}

	public static Comparator<String> getComparator()
	{
		return comparator;
	}

	public static Comparator<String> getComparatorInv()
	{
		return comparatorInv;
	}

	public JTable getTable()
	{
		return table;
	}

	public JLabel getLblnTus()
	{
		return lblnTus;
	}

	public FindReplaceDialog getFindReplaceDialog()
	{
		return findReplaceDialog;
	}

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog)
	{
		this.findReplaceDialog = findReplaceDialog;
	}

	public void close()
	{
		if (modified && Launcher.projectMode)
		{
			int code = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(), "Save dictionary file?",
					"NooJ: dictionary file has not been saved", JOptionPane.YES_NO_CANCEL_OPTION);
			if (code == JOptionPane.CANCEL_OPTION)
				return;
			else if (code == JOptionPane.YES_OPTION)
			{
				if (fullName != null)
					save();
				else
					saveAsDictionary();
			}
		}
		shell.dispose();

		if (errorShell != null)
			errorShell.dispose();

		if (findReplaceDialog != null)
		{
			findReplaceDialog.dispose();
			setFindReplaceDialog(null);
		}
	}

	public static String getHeader(String sb)
	{
		StringBuilder res = new StringBuilder();
		int pos = 0;
		int i;
		StringBuilder line = null;

		do
		{
			// get one line
			line = new StringBuilder();
			for (i = 0; pos + i < sb.length() && sb.charAt(pos + i) != '\n'; i++)
				line.append(sb.charAt(pos + i));

			if (line.length() == 0 || line.charAt(0) == '#')
			{
				res.append(line + "\n");
				pos += i;
				pos++;
			}
		}

		while (line.length() == 0 || line.charAt(0) == '#');

		return res.toString();
	}
}