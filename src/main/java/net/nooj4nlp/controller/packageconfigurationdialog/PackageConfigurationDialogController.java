package net.nooj4nlp.controller.packageconfigurationdialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.controller.FlexDescEditorShell.FlexDescEditorShellController;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.PropDefEditorShell.PropDefEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.engine.Utils;
import net.nooj4nlp.engine.Zip;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.DocumentationDialog;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;
import net.nooj4nlp.gui.utilities.Helper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class PackageConfigurationDialogController
{
	private static PackageConfigurationDialog packageConfigDialog;

	private static Project project;
	private ArrayList<Object> languagesResourcesLex;
	private ArrayList<Object> languagesResourcesSyn;

	private boolean modified;
	private String fullName;
	private static String projectDir;

	private static DocumentationDialog formDocumentation;

	public PackageConfigurationDialogController(PackageConfigurationDialog packageConfigurationDialog)
	{
		PackageConfigurationDialogController.packageConfigDialog = packageConfigurationDialog;
		PackageConfigurationDialogController.packageConfigDialog.setTitle("Untitled");
		PackageConfigurationDialogController.packageConfigDialog.getTableFiles().setFont(Launcher.preferences.TFont);

		PackageConfigurationDialogController.project = new Project();
	}

	public String copyNooJStatusToProject()
	{
		RefObject<ArrayList<String>> languages = new RefObject<ArrayList<String>>(null);
		// (1) copy all opened files to lvFiles
		project.setListOfForms(new ArrayList<JInternalFrame>());

		String[] columnNames = { "Type", "File", "Directory", "" };
		DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);
		packageConfigDialog.getTableFiles().setModel(tableModel);
		packageConfigDialog.getTableFiles().removeColumn(
				packageConfigDialog.getTableFiles().getColumnModel().getColumn(3));

		packageConfigDialog.getTextResources().setText("");

		JInternalFrame[] openFrames = Launcher.getDesktopPane().getAllFrames();

		for (JInternalFrame currentForm : openFrames)
		{
			if (!currentForm.isVisible())
				continue;

			String formName = currentForm.getClass().getSimpleName();

			if (formName.equals("GrammarEditorShell"))
			{
				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				GrammarEditorShell shell = (GrammarEditorShell) currentForm;
				GrammarEditorShellController controller = shell.getController();

				if (controller.getFullName() == null || controller.isModified())
				{
					return "NooJ: first save grammar " + FilenameUtils.getName(controller.getFullName());
				}

				// We need to add all necessary language resources to produce transformation operations with
				// inflectional operations
				addALanguage(controller.grammar.iLanguage, languages);
				addALanguage(controller.grammar.oLanguage, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				File gramFile = new File(controller.getFullName());
				add("Grammar", FilenameUtils.getName(controller.getFullName()), gramFile.getParent(), wp);
			}
			else if (formName.equals("DictionaryEditorShell"))
			{
				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				DictionaryEditorShell shell = (DictionaryEditorShell) currentForm;
				DictionaryEditorShellController controller = shell.getController();

				if (controller.getFullName() == null || controller.isModified())
				{
					return "NooJ: first save dictionary " + FilenameUtils.getName(controller.getFullName());
				}

				// we need to add all necessary language resources to produce transformation operations with
				// inflectional operations
				String lname = controller.getLan().isoName;
				addALanguage(lname, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				File gramFile = new File(controller.getFullName());
				add("Dictionary", FilenameUtils.getName(controller.getFullName()), gramFile.getParent(), wp);
			}
			else if (formName.equals("TextEditorShell"))
			{
				TextEditorShell shell = (TextEditorShell) currentForm;
		
				if (shell.getCorpusController() != null)
					break;

				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				TextEditorShellController controller = shell.getTextController();
				File fileToBeOpenedOrImported = controller.getFileToBeOpenedOrImported();

				if (fileToBeOpenedOrImported.getName() == null || controller.isModified())
				{
					String absolutePath = fileToBeOpenedOrImported.getAbsolutePath();

					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.SAVE_TEXT_PROJECT_MESSAGE
							+ FilenameUtils.removeExtension(absolutePath), Constants.NOOJ_APPLICATION_NAME,
							JOptionPane.INFORMATION_MESSAGE);
					return "NooJ: first save text " + absolutePath;
				}

				// We need to add all necessary language resources to produce transformation operations with
				// inflectional operations
				addALanguage(controller.getMyText().LanguageName, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				add("Text", FilenameUtils.getName(fileToBeOpenedOrImported.getName()),
						fileToBeOpenedOrImported.getParent(), wp);

				// Locate panel
				LocateDialog locateDialog = shell.getLocateDialog();
				if (locateDialog != null)
				{
					if (locateDialog.isVisible())
					{
						ArrayList<JInternalFrame> list2 = project.getListOfForms();
						list2.add(locateDialog);
						project.setListOfForms(list2);

						wp = new WindowPosition(locateDialog.getX(), locateDialog.getY(), locateDialog.getWidth(),
								locateDialog.getHeight());

						String radioQuery = "";
						if (locateDialog.getRbStringPattern().isSelected())
							radioQuery = "string";
						else if (locateDialog.getRbPerlPattern().isSelected())
							radioQuery = "perl re";
						else if (locateDialog.getRbNooJPattern().isSelected())
							radioQuery = "nooj re";
						else if (locateDialog.getRbNooJGrammar().isSelected())
						{
							if (locateDialog.getSyntacticAnalysisCBox().isSelected())
								radioQuery = "Grammar";
							else
								radioQuery = "grammar";
						}

						DialogStatus dialogStatus = null;
						String grammarPath = locateDialog.getNooJGrammarPathCombo().getSelectedItem().toString();
						if (!grammarPath.equals(""))
						{
							String gName = locateDialog.getNooJGrammarPathCombo().getSelectedItem().toString();

							if (!isTheGrammarOpened(gName))
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + gName
										+ " is not opened", Constants.MISSING_FILE_PROJECT_CAPTION,
										JOptionPane.INFORMATION_MESSAGE);
								return "NooJ: either open grammar " + gName
										+ " or clear up the 'Locate NooJ Grammar' field";
							}

							dialogStatus = new DialogStatus(controller.getFileToBeOpenedOrImported().getAbsolutePath(),
									locateDialog.getNooJRegeXCombo().getSelectedItem().toString(),
									FilenameUtils.getName(gName), radioQuery);
						}
						else
							dialogStatus = new DialogStatus(controller.getFileToBeOpenedOrImported().getAbsolutePath(),
									locateDialog.getNooJRegeXCombo().getSelectedItem().toString(), "", radioQuery);

						addDialogLocate(dialogStatus);
					}
				}
			}
			else if (formName.equals("CorpusEditorShell"))
			{
				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				CorpusEditorShell shell = (CorpusEditorShell) currentForm;
				CorpusEditorShellController controller = shell.getController();

				// We need to add all necessary language resources to produce transformation operations with
				// inflectional operations
				addALanguage(controller.getCorpus().languageName, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				File file = new File(controller.getFullPath());

				add("Corpus", FilenameUtils.getName(controller.getFullName()), file.getParent(), wp);

				// Locate panel
				LocateDialog locateDialog = controller.getLocateDialog();
				if (locateDialog != null)
				{
					if (locateDialog.isVisible())
					{
						ArrayList<JInternalFrame> list2 = project.getListOfForms();
						list2.add(locateDialog);
						project.setListOfForms(list2);

						wp = new WindowPosition(locateDialog.getX(), locateDialog.getY(), locateDialog.getWidth(),
								locateDialog.getHeight());

						String radioQuery = "";
						if (locateDialog.getRbStringPattern().isSelected())
							radioQuery = "string";
						else if (locateDialog.getRbPerlPattern().isSelected())
							radioQuery = "perl re";
						else if (locateDialog.getRbNooJPattern().isSelected())
							radioQuery = "nooj re";
						else if (locateDialog.getRbNooJGrammar().isSelected())
							radioQuery = "grammar";

						DialogStatus dialogStatus = null;
						String grammarPath = locateDialog.getNooJGrammarPathCombo().getSelectedItem().toString();
						if (!grammarPath.equals(""))
						{
							String gName = locateDialog.getNooJGrammarPathCombo().getSelectedItem().toString();

							if (!isTheGrammarOpened(gName))
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + gName
										+ " is not opened", Constants.MISSING_FILE_PROJECT_CAPTION,
										JOptionPane.INFORMATION_MESSAGE);
								return "NooJ: either open grammar " + gName
										+ " or clear up the 'Locate NooJ Grammar' field";
							}

							dialogStatus = new DialogStatus(controller.getFullName(), locateDialog.getNooJRegeXCombo()
									.getSelectedItem().toString(), gName, radioQuery);
						}
						else
							dialogStatus = new DialogStatus(controller.getFullName(), locateDialog.getNooJRegeXCombo()
									.getSelectedItem().toString(), "", radioQuery);

						addDialogLocate(dialogStatus);
					}
				}
			}
			else if (formName.equalsIgnoreCase("FlexDescEditorShell"))
			{
				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				FlexDescEditorShell shell = (FlexDescEditorShell) currentForm;
				FlexDescEditorShellController controller = shell.getController();

				if (controller.getFullName() == null || controller.isModified())
				{
					return "NooJ: first save morphological description file "
							+ FilenameUtils.getName(controller.getFullName());
				}

				addALanguage(controller.getiLan().isoName, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				File flexFile = new File(controller.getFullName());
				add("FlexDesc", FilenameUtils.getName(controller.getFullName()), flexFile.getParent(), wp);
			}
			else if (formName.equalsIgnoreCase("PropDefEditorShell"))
			{
				ArrayList<JInternalFrame> list = project.getListOfForms();
				list.add(currentForm);
				project.setListOfForms(list);

				PropDefEditorShell shell = (PropDefEditorShell) currentForm;
				PropDefEditorShellController controller = shell.getController();

				if (controller.getFullName() == null || controller.isModified())
				{
					return "NooJ: first save properties' definition file "
							+ FilenameUtils.getName(controller.getFullName());
				}

				addALanguage(controller.getLan().isoName, languages);

				WindowPosition wp = new WindowPosition(shell.getX(), shell.getY(), shell.getWidth(), shell.getHeight());
				File dicoFile = new File(controller.getFullName());
				add("DicoDef", FilenameUtils.getName(controller.getFullName()), dicoFile.getParent(), wp);
			}
		}

		// Refreshing table
		for (int i = 0; i < packageConfigDialog.getTableFiles().getColumnCount(); i++)
			Helper.setWidthOfTableColumn(packageConfigDialog.getTableFiles(), tableModel, i);

		if (languages.argvalue == null)
			return "NooJ: no opened window; cannot find any language";

		displayCurrentLinguisticResourcesForLanguages(languages.argvalue);

		return null;
	}

	private void addALanguage(String lname, RefObject<ArrayList<String>> listOfLanguages)
	{
		boolean found = false;

		if (listOfLanguages.argvalue == null)
			listOfLanguages.argvalue = new ArrayList<String>();

		for (String l : listOfLanguages.argvalue)
		{
			if (l.equals(lname))
			{
				found = true;
				break;
			}
		}
		if (!found)
			listOfLanguages.argvalue.add(lname);
	}

	private void add(String type, String fname, String dname, WindowPosition wp)
	{
		// Add file to listview ONLY if fname is not already there
		DefaultTableModel model = (DefaultTableModel) packageConfigDialog.getTableFiles().getModel();

		for (int i = 0; i < model.getRowCount(); i++)
		{
			if (model.getValueAt(i, 1).equals(fname))
				return;
		}

		Object[] row = new Object[4];
		row[0] = type;
		row[1] = fname;
		row[2] = dname;
		row[3] = wp;

		model.addRow(row);
	}

	private void addDialogLocate(DialogStatus ds)
	{
		DefaultTableModel model = (DefaultTableModel) packageConfigDialog.getTableFiles().getModel();

		for (int i = 0; i < model.getRowCount(); i++)
		{
			if (model.getValueAt(i, 1).equals("LocateDialog"))
				return;
		}

		Object[] row = new Object[4];
		row[0] = "Locate";
		row[1] = "";
		row[2] = "";
		row[3] = ds;

		model.addRow(row);
	}

	private void displayCurrentLinguisticResourcesForLanguages(ArrayList<String> languageNames)
	{
		this.languagesResourcesLex = new ArrayList<Object>();
		this.languagesResourcesSyn = new ArrayList<Object>();

		JTextArea textResources = packageConfigDialog.getTextResources();
		textResources.setText("");

		StringBuilder sb = new StringBuilder(textResources.getText());

		for (String languageName : languageNames)
		{
			sb.append("Language: " + languageName + "\n");

			ArrayList<String> lex = Launcher.preferences.ldic.get(languageName);

			if (lex == null || lex.size() == 0)
				sb.append("No lexical resource\n");
			else
			{
				sb.append(lex.size() + " lexical resources: \n");
				for (String prefname : lex)
				{
					String fname = FilenameUtils.getName(prefname);
					String ext = FilenameUtils.getExtension(prefname);
					String prio = fname.substring(0, 2);
					fname = fname.substring(2);

					int pr = Integer.parseInt(prio);
					if (pr > 0)
					{
						sb.append("-- ");
						sb.append(fname);
						sb.append(" (Low Priority ");
						sb.append(pr);
						sb.append(")\n");
					}
					else if (pr == 0)
					{
						sb.append("-- ");
						sb.append(fname);
						sb.append("\n");
					}
					else
					{
						sb.append("-- ");
						sb.append(fname);
						sb.append(" (High Priority ");
						sb.append(Math.abs(pr));
						sb.append(")\n");
					}

					// add resource to save it in project\languagename
					this.languagesResourcesLex.add(languageName);
					this.languagesResourcesLex.add(fname);
					this.languagesResourcesLex.add(pr);

					// add grammar if there is a pair .nod/.nog
					if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
					{
						String gname = FilenameUtils.getName(fname) + Constants.JNOG_EXTENSION; 
																								// + ".nog";
						String dname = FilenameUtils.concat(Paths.docDir,
								FilenameUtils.concat(languageName, "Lexical Analysis"));
						String gfullname = FilenameUtils.concat(dname, gname);
						File f = new File(gfullname);
						if (f.exists())
						{
							if (pr > 0)
								sb.append("-- " + gname + " (Low Priority " + pr + ")\n");
							else if (pr == 0)
								sb.append("-- " + gname + "\n");
							else
								sb.append("-- " + gname + " (High Priority " + Math.abs(pr) + ")\n");

							// add resource to save it in project\languagename
							this.languagesResourcesLex.add(languageName);
							this.languagesResourcesLex.add(gname);
							this.languagesResourcesLex.add(pr);
						}
					}
				}
			}

			ArrayList<String> syn = Launcher.preferences.lsyn.get(languageName);
			if (syn == null || syn.size() == 0)
				sb.append("No syntactic resource\n");
			else
			{
				sb.append(syn.size() + " syntactic resources: \n");
				for (String prefname : syn)
				{
					String fname = FilenameUtils.getName(prefname);
					String prio = fname.substring(0, 2);
					fname = fname.substring(2);

					int pr = Integer.parseInt(prio);
					if (pr == 0)
						sb.append("-- " + fname + "\n");
					else
						sb.append("-- " + fname + " (Step #" + pr + ")\n");

					// add resource to save it in project\languagename
					this.languagesResourcesSyn.add(languageName);
					this.languagesResourcesSyn.add(fname);
					this.languagesResourcesSyn.add(pr);
				}
			}
		}

		textResources.setText(sb.toString());
	}

	private boolean isTheGrammarOpened(String gname)
	{
		boolean found = false;

		JInternalFrame[] openFrames = Launcher.getDesktopPane().getAllFrames();
		for (JInternalFrame currentForm : openFrames)
		{
			if (!currentForm.isVisible())
				continue;

			String formName = currentForm.getClass().getSimpleName();
			if (formName.equals("GrammarEditorShell"))
			{
				GrammarEditorShell shell = (GrammarEditorShell) currentForm;
				GrammarEditorShellController controller = shell.getController();

				if (controller.getFullName().equals(gname))
				{
					found = true;
					break;
				}
			}
		}

		return found;
	}

	public void modify()
	{
		modified = true;

		if (fullName == null)
			packageConfigDialog.setTitle("Untitled [Modified]");
		else
			packageConfigDialog.setTitle(FilenameUtils.getName(fullName) + "[Modified]");
	}

	private void unmodify()
	{
		modified = false;

		if (fullName == null)
			packageConfigDialog.setTitle("Untitled");
		else
			packageConfigDialog.setTitle(FilenameUtils.getName(fullName));
	}

	public void save()
	{
		save(fullName, false);
	}

	public void saveProjectForNooj()
	{
		String dirName = Paths.applicationDir + System.getProperty("file.separator") + "resources"
				+ System.getProperty("file.separator") + "initial" + Launcher.preferences.deflanguage
				+ System.getProperty("file.separator") + "Projects";

		File dir = new File(dirName);
		if (!dir.exists() || !dir.isDirectory())
		{
			dir.mkdir();
		}

		if (fullName == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROJECT_NO_NAME_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String fName = FilenameUtils.getName(fullName);
		String noojName = dirName + System.getProperty("file.separator") + fName;

		CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
		save(noojName, true);
		CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);

		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File " + noojName + " has been updated.",
				Constants.NOOJ_UPDATE_MESSAGE_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Function that performs 'Save as'
	 * 
	 * @param fullName
	 * @param forNooj
	 */
	private void save(String fullName, boolean forNooj)
	{
		if (!forNooj)
		{
			// WARNING IF FILENAME STARTS WITH "_"
			String fNameNoExt = FilenameUtils.removeExtension(fullName);
			if (fNameNoExt.charAt(0) == '_')
			{
				int answer = JOptionPane
						.showConfirmDialog(Launcher.getDesktopPane(), Constants.FILENAME_PREFIX_WARNING,
								Constants.NOOJ_PROTECTED_RESOURCE, JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.NO_OPTION)
					return;
			}

			// MANAGE MULTIPLE BACKUPS
			try
			{
				Utilities.savePreviousVersion(fullName, Launcher.preferences.multiplebackups);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}

		project.save(packageConfigDialog.getTableFiles(), packageConfigDialog.getTextDocumentation(), fullName,
				Paths.docDir, languagesResourcesLex, languagesResourcesSyn);

		if (!forNooj)
			this.fullName = fullName;

		unmodify();
	}

	public void saveProject()
	{
		SaveProjectActionListener.saveProject();
	}

	public void saveAsProject()
	{
		SaveProjectActionListener.saveAsProject();
	}

	private void updateFromProject()
	{
		// Get all files from project
		DefaultTableModel model = (DefaultTableModel) packageConfigDialog.getTableFiles().getModel();
		model.getDataVector().removeAllElements();
		model.fireTableDataChanged();

		ArrayList<Object> listOfFiles = project.getListOfFiles();

		for (int i = 0; i < listOfFiles.size(); i += 3)
		{
			String type = (String) listOfFiles.get(i); // type

			if (!type.equals("Locate"))
			{
				String fName = (String) listOfFiles.get(i + 1); // fName
				WindowPosition wp = (WindowPosition) listOfFiles.get(i + 2); // position

				add(type, fName, "--", wp);
			}
		}

		for (int i = 0; i < listOfFiles.size(); i += 3)
		{
			String type = (String) listOfFiles.get(i); // type

			if (type.equals("Locate"))
			{
				DialogStatus ds = (DialogStatus) listOfFiles.get(i + 2); // position

				addDialogLocate(ds);
			}
		}

		// *** Get all resources listed in project.preferences
		updatePreferenceFromProject();

		unmodify();
	}

	private void updatePreferenceFromProject()
	{
		packageConfigDialog.getTextDocumentation().setText("");
		languagesResourcesLex = new ArrayList<Object>();
		languagesResourcesSyn = new ArrayList<Object>();

		// Create a new preferences from the project's directory
		try
		{
			Launcher.preferences = new Preferences(fullName + Constants.DIRECTORY_SUFFIX);
			// updateFromFormMainPreferences is in PreferencesDialog constructor
			
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROJECT_CANNOT_UPDATE_PREFERENCES,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
		}

		JTextArea rtbResources = packageConfigDialog.getTextResources();
		for (String languageName : Launcher.preferences.languages)
		{
			rtbResources.append("Language '" + languageName + "': ");

			String lDirName = FilenameUtils.concat(this.fullName + Constants.DIRECTORY_SUFFIX, languageName);
			String tDirName = FilenameUtils.concat(lDirName, "Lexical Analysis");

			File tDir = new File(tDirName);
			if (!tDir.exists())
				tDirName = lDirName;

			File[] resourceFiles = tDir.listFiles();
			if (resourceFiles != null)
			{
				if (resourceFiles.length == 0)
					rtbResources.append("No lexical resource\n");
				else
				{
					rtbResources.append("Lexical resources: \n");

					for (File resource : resourceFiles)
					{
						String fName = resource.getName();
						String ext = FilenameUtils.getExtension(fName);

						if (ext.equals(Constants.JNOD_EXTENSION) || ext.equals(Constants.JNOM_EXTENSION)
								|| (ext.equals(Constants.JNOG_EXTENSION) && !tDirName.equals(lDirName)))
						{
							String prio = fName.substring(0, 2);
							String temp = fName.substring(2);
							fName = temp;

							int pr = Integer.parseInt(prio);
							if (pr > 0)
								rtbResources.append("-- " + fName + " (Low Priority " + pr + ")\n");
							else if (pr == 0)
								rtbResources.append("-- " + fName + "\n");
							else
								rtbResources.append("-- " + fName + " (High Priority " + Math.abs(pr) + ")\n");

							// add resource to save it in project\languagename
							this.languagesResourcesLex.add(languageName);
							this.languagesResourcesLex.add(fName);
							this.languagesResourcesLex.add(pr);
						}
					}
				}
			}

			tDirName = FilenameUtils.concat(lDirName, "Syntactic Analysis");

			tDir = new File(tDirName);
			if (!tDir.exists())
				tDirName = lDirName;

			resourceFiles = tDir.listFiles();
			if (resourceFiles != null)
			{
				if (resourceFiles.length == 0)
					rtbResources.append("No syntactic resource\n");
				else
				{
					rtbResources.append("Syntactic resources: \n");

					for (File resource : resourceFiles)
					{
						String fName = resource.getName();
						String ext = FilenameUtils.getExtension(fName);

						if (ext.equals(Constants.JNOG_EXTENSION))
						{
							String prio = fName.substring(0, 2);
							String temp = fName.substring(2);
							fName = temp;

							int pr = Integer.parseInt(prio);
							rtbResources.append("-- " + fName + " (Step #" + pr + ")\n");

							// add resource to save it in project\languagename
							this.languagesResourcesSyn.add(languageName);
							this.languagesResourcesSyn.add(fName);
							this.languagesResourcesSyn.add(pr);
						}
					}
				}
			}
		}
	}

	public void setPackageConfigDialog(PackageConfigurationDialog packageConfigDialog)
	{
		PackageConfigurationDialogController.packageConfigDialog = packageConfigDialog;
	}

	private boolean openAllFiles(String projName)
	{
		String prjDir = projName + Constants.DIRECTORY_SUFFIX;

		ArrayList<Object> listOfFiles = project.getListOfFiles();

		for (int i = 0; i < listOfFiles.size(); i += 3)
		{
			String type = (String) listOfFiles.get(i); // type

			if (!type.equals("Locate")) // FIRST OPEN ALL FILES
			{
				String fName = (String) listOfFiles.get(i + 1); // fName
				WindowPosition wp = (WindowPosition) listOfFiles.get(i + 2); // position

				String fullName = FilenameUtils.concat(prjDir, fName);
				if (!openFile(type, fullName, wp))
					return false;
			}
		}

		for (int i = 0; i < listOfFiles.size(); i += 3)
		{
			String type = (String) listOfFiles.get(i); // type

			if (type.equals("Locate")) // THEN OPEN ALL DIALOGS
			{
				
				DialogStatus ds = (DialogStatus) listOfFiles.get(i + 2); // dialog's data
				LocateDialog dl = null;
				

				// We need to get the corresponding formCorpus or the formText
				JInternalFrame[] allFrames = Launcher.getDesktopPane().getAllFrames();
				for (JInternalFrame currentForm : allFrames)
				{
					if (FilenameUtils.getName(currentForm.getTitle()).equals(ds.getParentWindowName()))
					{
						if (currentForm.toString().contains("TextEditorShell"))
						{
							TextEditorShell shell = (TextEditorShell) currentForm;
							dl = shell.getLocateDialog();
						}
						else
						{
							CorpusEditorShell shell = (CorpusEditorShell) currentForm;
							dl = shell.getController().getLocateDialog();
						}
						break;
					}
				}

				if (dl != null)
				{
				    dl.getNooJRegeXCombo().setSelectedItem(ds.getRexp());
					String gram = ds.getGram();
					dl.getNooJGrammarPathCombo().setSelectedItem(gram);
					String radioQuery = ds.getRadioQuery();
					dl.getSyntacticAnalysisCBox().setSelected(radioQuery.charAt(0) == 'G');

					if (!gram.equals(""))
					{
						String grmName = FilenameUtils.concat(prjDir, gram);
						File gramFile = new File(grmName);

						if (gramFile.exists())
							dl.getNooJGrammarPathCombo().setSelectedItem(grmName);
						else
							dl.getNooJGrammarPathCombo().setSelectedItem("CANNOT FIND " + gram);
					}

					if (radioQuery.equals("string"))
						dl.getRbStringPattern().setSelected(true);
					else if (radioQuery.equals("perl re"))
						dl.getRbPerlPattern().setSelected(true);
					else if (radioQuery.equals("nooj re"))
						dl.getRbNooJPattern().setSelected(true);
					else if (radioQuery.equals("grammar"))
						dl.getRbNooJGrammar().setSelected(true);
					// Syntactic analysis
					else if (radioQuery.equals("Grammar"))
					{
						dl.getRbNooJGrammar().setSelected(true);
						dl.getSyntacticAnalysisCBox().setSelected(true);
					}

					dl.show();
				}
			}
		}

		// load project documentation AFTER all other files (so that the window stands in front)
		String docFilePath = FilenameUtils.concat(prjDir, "ReadMe.rtf");
		File docFile = new File(docFilePath);
		if (docFile.exists())
		{
			packageConfigDialog.getTextDocumentation().setText("TODO"); // for the (hidden) project form

			if (Launcher.projectMode && !packageConfigDialog.getTextDocumentation().getText().equals(""))
			{
				if (formDocumentation == null)
					try
					{
						formDocumentation = new DocumentationDialog(docFilePath);
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage() + "2",
								Constants.PROJECT_CANNOT_LOAD_DOC, JOptionPane.ERROR_MESSAGE);
						return false;
					}
					catch (BadLocationException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage() + "2",
								Constants.PROJECT_CANNOT_LOAD_DOC, JOptionPane.ERROR_MESSAGE);
						return false;
					}
				Launcher.getDesktopPane().add(formDocumentation);
				formDocumentation.setVisible(true);
			}
		}

		return true;
	}

	private boolean openFile(String type, String fullName, WindowPosition wp)
	{
		File file = new File(fullName);

		if (type.equals("Text"))
		{
			TextEditorShellController controller = new TextEditorShellController(file);
			TextEditorShell shell = controller.openText(file);

			UnitSelectionListener unitSelectionListener = shell.getUnitSelectionListener();
			JTextPane textPane = shell.getTextPane();
			textPane.removeCaretListener(unitSelectionListener);
			shell.getTextController().rtbTextUpdate(true);
			textPane.addCaretListener(unitSelectionListener);

			controller.updateTextPaneStats();

			shell.setBounds(wp.getPosX(), wp.getPosY(), wp.getWidth(), wp.getHeight());
			shell.show();

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);
		}
		else if (type.equals("Corpus"))
		{
			CorpusEditorShellController controller = new CorpusEditorShellController(null, null, null, null);
			CorpusEditorShell shell = controller.openNoojCorpus(file, false);
			controller.openNoojEngine();

			if (controller == null || controller.getCorpus() == null)
				return false;

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);

			shell.setBounds(wp.getPosX(), wp.getPosY(), wp.getWidth(), wp.getHeight());
			shell.show();
		}
		else if (type.equals("Dictionary"))
		{
			DictionaryEditorShell shell = DictionaryEditorShellController.openNooJDictionary(fullName);
			DictionaryEditorShellController controller = shell.getController();

			if (controller == null)
				return false;

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);

			shell.setBounds(wp.getPosX(), wp.getPosY(), wp.getWidth(), wp.getHeight());
			shell.show();
		}
		else if (type.equals("FlexDesc"))
		{
			FlexDescEditorShell shell = new FlexDescEditorShell();
			FlexDescEditorShellController controller = shell.getController();

			if (controller == null)
				return false;

			controller.loadFromFile(fullName);

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);

			shell.setBounds(wp.getPosX(), wp.getPosY(), wp.getWidth(), wp.getHeight());
			shell.show();
		}
		else if (type.equals("DicoDef"))
		{
			PropDefEditorShell shell = new PropDefEditorShell();
			PropDefEditorShellController controller = shell.getController();

			if (controller == null)
				return false;

			controller.loadFromFile(fullName);

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);

			shell.setBounds(wp.getPosX(), wp.getPosY(), wp.getWidth(), wp.getHeight());
			shell.show();
		}
		else if (type.equals("Grammar"))
		{
			
			
			
			
			GrammarEditorShell shell = new GrammarEditorShell(fullName);
		    GrammarEditorShellController controller = shell.getController();
			if (controller == null)
				return false;

			// TODO consult Uros about this.
		    controller.LoadGrammar(fullName, false);

			ArrayList<JInternalFrame> list = project.getListOfForms();
			list.add(shell);
			project.setListOfForms(list);
			Launcher.getDesktopPane().add(shell);
			shell.setVisible(true);
			
		}

		return true;
	}

	public void openProject(String fullName)
	{
		projectDir = fullName + Constants.DIRECTORY_SUFFIX;
		File prDir = new File(projectDir);

		// Moved from UseProjectActionListener.actionPerformed. This is the right place for this call.
		Launcher.setOpenDirectories();

		try
		{
			if (prDir.exists())
				Utils.deleteDir(prDir);

			Zip.uncompressDir(projectDir, fullName);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(), Constants.PROJECT_FILE_CORRUPTED,
					JOptionPane.ERROR_MESSAGE);

			if (prDir.exists())
			{
				boolean success = Utils.deleteDir(prDir);
				if (!success)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.CANNOT_DELETE_TEMP_DIR_CAPTION + projectDir, JOptionPane.ERROR_MESSAGE);
				}
			}

			Launcher.projectMode = false;

			return;
		}

		// Old methods: there were embedded language resources' zip files that needed to be unzipped
		boolean oldMethod = false;

		File[] filesInProjectDir = prDir.listFiles();

		for (File fileInProjectDir : filesInProjectDir)
		{
			String zFullName = fileInProjectDir.getAbsolutePath();
			if (FilenameUtils.getExtension(zFullName).equals("zip"))
			{
				String zName = fileInProjectDir.getName();
				String lName = FilenameUtils.removeExtension(zName);
				String dFullName = FilenameUtils.concat(projectDir, lName);

				try
				{
					Zip.uncompressDir(dFullName, zFullName);

					oldMethod = true;

					File dir = new File(dFullName);
					if (!dir.exists())
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PROJECT_CANNOT_LOAD_LINGUISTIC_MESSAGE + lName, Constants.PROJECT_WARNING,
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.PROJECT_CANNOT_LOAD_LINGUISTIC_MESSAGE + lName, Constants.PROJECT_WARNING,
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				String zLex = FilenameUtils.concat(dFullName, "Lexical Analysis.zip");
				String lex = FilenameUtils.concat(projectDir, FilenameUtils.concat(lName, "Lexical Analysis"));

				File lexFile = new File(zLex);
				if (lexFile.exists())
				{
					try
					{
						Zip.uncompressDir(lex, zLex);

						lexFile = new File(lex);
						if (!lexFile.exists())
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.PROJECT_CANNOT_LOAD_LEXICAL_MESSAGE + lName, Constants.PROJECT_WARNING,
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PROJECT_CANNOT_LOAD_LEXICAL_MESSAGE + lName, Constants.PROJECT_WARNING,
								JOptionPane.INFORMATION_MESSAGE);
					}
				}

				String zSyn = FilenameUtils.concat(dFullName, "Syntactic Analysis.zip");
				String syn = FilenameUtils.concat(projectDir, FilenameUtils.concat(lName, "Syntactic Analysis"));

				File synFile = new File(zSyn);
				if (synFile.exists())
				{
					try
					{
						Zip.uncompressDir(syn, zSyn);

						synFile = new File(syn);
						if (!synFile.exists())
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.PROJECT_CANNOT_LOAD_SYNTACTIC_MESSAGE + lName, Constants.PROJECT_WARNING,
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								Constants.PROJECT_CANNOT_LOAD_SYNTACTIC_MESSAGE + lName, Constants.PROJECT_WARNING,
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}

		if (!oldMethod)
		{
			// New method: there are no embedded zip files
			for (File fileInProjectDir : filesInProjectDir)
			{
				String zFullName = fileInProjectDir.getAbsolutePath();
				String zName = fileInProjectDir.getName();

				if (zName.length() < 7 || zName.charAt(0) != '-' || zName.charAt(3) != '-')
					continue; // not a resource

				// Zname looks like "-en-LA-" or "-en-SA"
				String lName = zName.substring(1, 3);

				if (!Language.isALanguage(lName))
					continue;

				String dFullName = FilenameUtils.concat(projectDir, lName);
				File dir = new File(dFullName);
				if (!dir.exists())
					dir.mkdir();

				String dla = FilenameUtils.concat(dFullName, "Lexical Analysis");
				File lexDir = new File(dla);
				if (!lexDir.exists())
					lexDir.mkdir();

				String dsa = FilenameUtils.concat(dFullName, "Syntactic Analysis");
				File synDir = new File(dsa);
				if (!synDir.exists())
					synDir.mkdir();

				if (zName.substring(4).equals("_properties.def"))
				{
					File srcFile = new File(zFullName);
					File destFile = new File(FilenameUtils.concat(dla, "_properties.def"));
					try
					{
						FileUtils.moveFile(srcFile, destFile);
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					}
					continue;
				}

				if (zName.length() < 7 || zName.charAt(6) != '-')
					continue;

				String tName = zName.substring(7);
				String typeOfResource = zName.substring(4, 6);

				if (typeOfResource.equals("LA"))
				{
					File srcFile = new File(zFullName);
					File destFile = new File(FilenameUtils.concat(dla, tName));

					try
					{
						FileUtils.moveFile(srcFile, destFile);
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					}
				}
				else if (typeOfResource.equals("SA"))
				{
					File srcFile = new File(zFullName);
					File destFile = new File(FilenameUtils.concat(dsa, tName));

					try
					{
						FileUtils.moveFile(srcFile, destFile);
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		// ***
		PackageConfigurationDialogController.packageConfigDialog.setTitle(FilenameUtils.getName(fullName));
		PackageConfigurationDialogController.packageConfigDialog.getTableFiles().setFont(Launcher.preferences.TFont);

		this.modified = false;
		this.fullName = fullName;

		PackageConfigurationDialogController.project = Project.load(this.fullName + Constants.DIRECTORY_SUFFIX);

		if (project != null)
		{
			// set NooJ Status
			updateFromProject(); // display all files & set Languages
			// open all windows
			if (!openAllFiles(this.fullName))
				project = null;
			else
				unmodify();
		}
		// ***

		if (project == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_PROJECT + fullName,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Dic.writeLog("Open Project " + FilenameUtils.getName(fullName));
	}

	public static void closeProject()
	{
		Launcher.getMntmRunProject().setText("Run Project");
		Launcher.getMntmNewProject().setEnabled(true);

		if (project == null)
			return;

		if (formDocumentation != null)
		{
			formDocumentation.dispose();
			formDocumentation = null;
		}

		for (JInternalFrame form : project.getListOfForms())
		{
			if (form.isVisible())
				form.dispose();
		}

		if (packageConfigDialog != null)
			packageConfigDialog.dispose();

		String prjDir = projectDir;
		if (projectDir != null)
		{
			File dir = new File(prjDir);
			if (dir.exists())
			{
				Utils.deleteDir(dir);
			}
		}

		project = null;

		Launcher.getStatusBar().getProjectLabel().setText("");
		Launcher.projectMode = false;
		projectDir = null;

		// this.miTextLinguisticAnalysis.Visible = true;
		// this.miCorpusLinguisticAnalysis.Visible = true;
		Launcher.preferences = Launcher.savedPreferences;
		// updateFromFormMainPreferences is in PreferencesDialog constructor
		// this.formMain.dialogPreferences.UpdateFromFormMainPreferences();

		Launcher.setOpenDirectories();
	}

	public PackageConfigurationDialog getPackageConfigDialog()
	{
		return packageConfigDialog;
	}

	public static Project getProject()
	{
		return project;
	}

	public boolean isModified()
	{
		return modified;
	}

	public void setModified(boolean modified)
	{
		this.modified = modified;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public static String getProjectDir()
	{
		return projectDir;
	}

	public static void setProjectDir(String projectDir)
	{
		PackageConfigurationDialogController.projectDir = projectDir;
	}
}