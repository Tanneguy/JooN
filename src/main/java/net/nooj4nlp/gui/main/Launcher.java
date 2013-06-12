package net.nooj4nlp.gui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog.XmlAnnotationsTextPaneResources;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.controller.packageconfigurationdialog.PackageConfigurationDialogController;
import net.nooj4nlp.controller.packageconfigurationdialog.UseProjectActionListener;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.actions.documents.CopyActionListener;
import net.nooj4nlp.gui.actions.documents.RedoActionListener;
import net.nooj4nlp.gui.actions.documents.SaveActionListener;
import net.nooj4nlp.gui.actions.documents.SaveForNooJActionListener;
import net.nooj4nlp.gui.actions.documents.UndoActionListener;
import net.nooj4nlp.gui.actions.grammar.CutActionListener;
import net.nooj4nlp.gui.actions.shells.construct.AboutNooJ;
import net.nooj4nlp.gui.actions.shells.construct.ConsoleActionListener;
import net.nooj4nlp.gui.actions.shells.construct.ConstructCorpusActionListener;
import net.nooj4nlp.gui.actions.shells.construct.DictionaryActionListener;
import net.nooj4nlp.gui.actions.shells.construct.FindReplaceActionListener;
import net.nooj4nlp.gui.actions.shells.construct.LanguageSpecificsActionListener;
import net.nooj4nlp.gui.actions.shells.construct.MorphologyActionListener;
import net.nooj4nlp.gui.actions.shells.construct.NewDictionaryPropDefActionListener;
import net.nooj4nlp.gui.actions.shells.construct.NewGrammarActionListener;
import net.nooj4nlp.gui.actions.shells.construct.NewTextCorpusActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenConcordanceActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenCorpusActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenDictionaryActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenGrammarActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenPropDefActionListener;
import net.nooj4nlp.gui.actions.shells.construct.OpenTextActionListener;
import net.nooj4nlp.gui.actions.shells.construct.PackageConfigurationActionListener;
import net.nooj4nlp.gui.actions.shells.construct.PerlRegexActionListener;
import net.nooj4nlp.gui.actions.shells.construct.PreferencesActionListener;
import net.nooj4nlp.gui.actions.shells.construct.TextEncodingActionListener;
import net.nooj4nlp.gui.actions.shells.control.ArrangeIconsActionListener;
import net.nooj4nlp.gui.actions.shells.control.CascadeWindowsActionListener;
import net.nooj4nlp.gui.actions.shells.control.CloseActionListener;
import net.nooj4nlp.gui.actions.shells.control.ExitActionListener;
import net.nooj4nlp.gui.actions.shells.control.MinimizeAllWindowsActionListener;
import net.nooj4nlp.gui.actions.shells.control.ResizeActionListener;
import net.nooj4nlp.gui.actions.shells.control.TileWindowsActionListener;
import net.nooj4nlp.gui.components.CustomStatusBar;
import net.nooj4nlp.gui.dialogs.PackageConfigurationDialog;
import net.nooj4nlp.gui.shells.ConcordanceShell;
import net.nooj4nlp.gui.shells.CorpusEditorShell;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

import org.apache.commons.io.FilenameUtils;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import  net.nooj4nlp.gui.shells.GrammarEditorShell;




public class Launcher
{
	
	public static JMenu mnEdit;
	
	//public boolean visible=false; 
	private static JFrame frame;
	private static JMenuBar menuBar;
	private static JDesktopPane desktopPane;
	public static JInternalFrame lastActive;
	private UndoManager doManager;
	private static JMenuItem mntmCopy;
	private static JMenuItem mntmCut;
	private static JMenuItem mntmPaste;
	private static JMenuItem mntmSelectAll;
	private static JMenuItem mntmRunProject;
	private static JMenuItem mntmNewProject;


	private static JFileChooser openDicChooser;
	private static JFileChooser saveDicChooser;
	private static JFileChooser openGramChooser;
	private static JFileChooser saveGramChooser;
	private static JFileChooser openDefDialogChooser;
	private static JFileChooser saveDefDialogChooser;
	private static JFileChooser openSourceChooser;
	private static JFileChooser openCorpusChooser;
	private static JFileChooser saveCorpusChooser;
	private static JFileChooser openTextChooser;
	private static JFileChooser saveTextChooser;
	private static JFileChooser openFolderChooser;
	private static JFileChooser saveConcordanceChooser;
	private static JFileChooser openConcordanceChooser;
	private static JFileChooser openProjectChooser;
	private static JFileChooser saveProjectChooser;

	private static CustomStatusBar statusBar;

	public static String nooJVersion = "v3.1 b0109";

	public static Preferences preferences;
	public static Preferences savedPreferences = null;
	public static Graph graphClipboard;
	public static int iGraphClipboard;
	public static ArrayList<Integer> nGraphClipboard;
	public static boolean projectMode;
	private static XmlAnnotationsTextPaneResources xmlAnnotationsTextPaneResources;

	private static String[] Arguments;

	// Variables needed for multithreading
	public static boolean multithread = true; // If 'true', this application works in multithreading mode.

	public static boolean backgroundWorking = false;
	public static BackgroundWorker backgroundWorker;
	public static Date initialDate;

	public static String processName;
	public static int progressPercentage;
	public static String progressMessage;

	public static boolean checkAgreement;

	private static List<String> regexMemoryList = new ArrayList<String>();
	private static List<String> xmlMemoryList = new ArrayList<String>();
	private static int corpusTextRadioButtonSelectionMemory = 2;
	private static int regexMemoryIndex = -1;
	private static int xmlMemoryIndex = -1;
	private static int indexOfDefaultEncoding = -1;
	private static String encodingCodeOfOpenGrammar = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		Arguments = args;
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// handle exception
		}
		catch (ClassNotFoundException e)
		{
			// handle exception
		}
		catch (InstantiationException e)
		{
			// handle exception
		}
		catch (IllegalAccessException e)
		{
			// handle exception
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			@SuppressWarnings("static-access")
			public void run()
			{
				try
				{
					Launcher window = new Launcher();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Process arguments
	 */
	private static void processTheArguments()
	{
		if (Arguments == null)
			return;
		if (Arguments.length == 0)
			return;

		for (String arg : Arguments)
		{
			int mid = arg.lastIndexOf(".");
			String ext = arg.substring(mid, arg.length());

			if (ext.equalsIgnoreCase("DIC"))
			{
				// TODO form dictionary
			}
			else if (ext.equalsIgnoreCase("FLX"))
			{
				// TODO form flex
			}
			else if (ext.equalsIgnoreCase("DEF"))
			{
				// TODO form dico def
			}
			else if (ext.equalsIgnoreCase(Constants.JNOC_EXTENSION))
			{
				File file = new File(arg);

				CorpusEditorShellController controller = new CorpusEditorShellController(null, null, null, null);
				CorpusEditorShell shell = controller.openNoojCorpus(file, false);
				controller.openNoojEngine();

				if (controller != null && controller.getCorpus() != null)
				{
					shell.show();
				}
			}
			else if (ext.equalsIgnoreCase(Constants.JNOF_EXTENSION) || ext.equalsIgnoreCase(Constants.JNOG_EXTENSION)
					|| ext.equalsIgnoreCase("NOM"))
			{
				// TODO
			}
			else if (ext.equalsIgnoreCase("NOP"))
			{
				PackageConfigurationDialog packageConfigDialog = new PackageConfigurationDialog();
				PackageConfigurationDialogController controller = new PackageConfigurationDialogController(
						packageConfigDialog);
				controller.openProject(arg);

				if (controller == null || PackageConfigurationDialogController.getProject() == null)
					return;

				statusBar.getProjectLabel().setText("PROJECT MODE");
				Arguments = null;
				return;
			}
			else if (ext.equalsIgnoreCase(Constants.JNOT_EXTENSION))
			{
				File file = new File(arg);

				TextEditorShellController controller = new TextEditorShellController(file);
				TextEditorShell shell = controller.openText(file);

				if (controller != null)
				{
					controller.rtbTextUpdate(false);
					controller.updateTextPaneStats();

					shell.show();
				}
			}
		}
		Arguments = null;
	}

	/**
	 * Create the application.
	 */
	public Launcher()
	{
		initialize();

		Paths.InitializePaths();

		createFileChoosers();

		init();

		loadXmlAnnotationsTextPaneResources();
	}

	private void init()
	{
	
		String updatefile = FilenameUtils.concat(Paths.docDir, "update.txt");
		File file = new File(updatefile);
	
		{
			// xcopy projects texts corpus grammars
			try
			{
				Utilities.initAllDiskResources();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}

			// create update file
			PrintWriter sw = null;
			try
			{
				sw = new PrintWriter(file);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			sw.write("Hello NooJ User");
			sw.close();
		}

		// preferences
		loadDefaultPreference();

		setOpenDirectories();

		processTheArguments();
	}

	public static void setOpenDirectories()
	{
		if (projectMode)
		{
			
			String prjdir = PackageConfigurationDialogController.getProjectDir();
			preferences.openTextDir = prjdir;
			preferences.openDicDir = prjdir;
			preferences.openGrammarDir = prjdir;
			preferences.openProjDir = null;
			
			Paths.projectDir = prjdir;
		}
		else
		{
			preferences.openProjDir = FilenameUtils.concat(Paths.docDir,
					FilenameUtils.concat(Launcher.preferences.deflanguage, "Projects"));
			File directory = new File(preferences.openProjDir);
			if (!directory.exists())
				directory.mkdir();

			preferences.openTextDir = FilenameUtils.concat(Paths.docDir,
					FilenameUtils.concat(Launcher.preferences.deflanguage, "Projects"));
			preferences.openDicDir = FilenameUtils.concat(Paths.docDir,
					FilenameUtils.concat(Launcher.preferences.deflanguage, "Lexical Analysis"));
			directory = new File(preferences.openDicDir);
			if (!directory.exists())
				directory.mkdir();

			preferences.openGrammarDir = FilenameUtils.concat(Paths.docDir,
					FilenameUtils.concat(Launcher.preferences.deflanguage, "Syntactic Analysis"));
			directory = new File(preferences.openGrammarDir);
			if (!directory.exists())
				directory.mkdir();
		}

		// set openFiledialogs
		Launcher.openDicChooser.setCurrentDirectory(new File(preferences.openDicDir));
		Launcher.saveDicChooser.setCurrentDirectory(new File(preferences.openDicDir));
		Launcher.openDefDialogChooser.setCurrentDirectory(new File(preferences.openDicDir)); // same as dictionaries
		Launcher.saveDefDialogChooser.setCurrentDirectory(new File(preferences.openDicDir));
		Launcher.openGramChooser.setCurrentDirectory(new File(preferences.openGrammarDir));
		Launcher.saveGramChooser.setCurrentDirectory(new File(preferences.openGrammarDir));
		Launcher.openTextChooser.setCurrentDirectory(new File(preferences.openTextDir));
		Launcher.saveTextChooser.setCurrentDirectory(new File(preferences.openTextDir));
		Launcher.openCorpusChooser.setCurrentDirectory(new File(preferences.openTextDir));
		Launcher.saveCorpusChooser.setCurrentDirectory(new File(preferences.openTextDir));
		// Workaround
		if (preferences.openProjDir != null)
		{
			Launcher.openProjectChooser.setCurrentDirectory(new File(preferences.openProjDir));
			Launcher.saveProjectChooser.setCurrentDirectory(new File(preferences.openProjDir));
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Launcher.class.getResource("/net/nooj4nlp/gui/components/NooJ.jpg")));
		frame.setTitle("NooJ");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(640, 480));

		desktopPane = new JDesktopPane();
		frame.getContentPane().add(desktopPane, BorderLayout.CENTER);
		desktopPane.setBackground(new Color(171, 171, 171));

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		doManager = new UndoManager();

		createMenuContents();

		statusBar = new CustomStatusBar(frame);
		statusBar.getBtnCancel().setEnabled(false);

		backgroundWorker = new BackgroundWorker();

		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		regexMemoryList.add("^[A-Z ][A-Z ]*");
		xmlMemoryList.add("<title> <head> <p>");
		xmlMemoryList.add("<s>");
	}

	private void loadXmlAnnotationsTextPaneResources()
	{
		Launcher.xmlAnnotationsTextPaneResources = new XmlAnnotationsTextPaneResources();
	}

	/**
	 * Load preferences
	 */
	private void loadDefaultPreference()
	{
		String prefname = FilenameUtils.concat(Paths.docDir, "Preferences." + Constants.JNOJ_EXTENSION);
		Launcher.preferences = Preferences.Load(prefname); // load pref; do not xcopy pref files
		if (Launcher.preferences == null)
		{
			JOptionPane.showMessageDialog(getDesktopPane(), "Cannot load preference file " + prefname,
					"NooJ setup problem?", JOptionPane.ERROR_MESSAGE);
			prefname = Paths.applicationDir + "Preferences." + Constants.JNOJ_EXTENSION;
			Launcher.preferences = Preferences.Load(prefname); // load pref; do not xcopy pref files

			if (Launcher.preferences == null)
			{
				JOptionPane.showMessageDialog(getDesktopPane(), "Cannot load preference file " + prefname,
						"NooJ setup problem?", JOptionPane.ERROR_MESSAGE);
				// Create new preferences
				Launcher.preferences = new Preferences();
			}
		}
	}

	public static void modifyAllFonts()
	{
		JInternalFrame[] allFrames = Launcher.getDesktopPane().getAllFrames();

		for (JInternalFrame currentForm : allFrames)
		{
			String cls = currentForm.getClass().getSimpleName();

			if (cls.equals("DictionaryEditorShell"))
			{
				DictionaryEditorShell dictionaryEditorShell = (DictionaryEditorShell) currentForm;
				dictionaryEditorShell.modifyFont(preferences.DFont.getFamily(), preferences.DFont.getSize());
			}
			else if (cls.equals("TextEditorShell"))
			{
				TextEditorShell textEditorShell = ((TextEditorShell) currentForm);
				textEditorShell.getTextController().modifyTextFont(preferences.TFont.getFamily(),
						preferences.TFont.getSize());
			}
			else if (cls.equals("ConcordanceShell"))
			{
				((ConcordanceShell) currentForm).getConcordanceTable().setFont(preferences.TFont);
			}
			else if (cls.equals("FlexDescEditorShell"))
			{
				((FlexDescEditorShell) currentForm).modifyFont(preferences.DFont.getFamily(),
						preferences.DFont.getSize());
			}
		}
	}

	private void createFileChoosers()
	{
		FileNameExtensionFilter filterDic = new FileNameExtensionFilter("NooJ Dictionary (*.dic)", "dic");
		FileNameExtensionFilter filterGram = new FileNameExtensionFilter("NooJ Grammar (*.nog;*.nom;*.nof)",
				Constants.JNOG_EXTENSION,"nom","nof");
		FileNameExtensionFilter filterCSharpGram = new FileNameExtensionFilter("C# NooJ Grammar (*.xml)", "xml");
		FileNameExtensionFilter filterDef = new FileNameExtensionFilter("Properties\' definition (*.def)", "def");

		openDicChooser = new JFileChooser();
		openDicChooser.setDialogTitle("Open a dictionary");
		openDicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openDicChooser.addChoosableFileFilter(filterDic);
		openDicChooser.setFileFilter(filterDic);

		saveDicChooser = new JFileChooser();
		saveDicChooser.setDialogTitle("Save a dictionary");
		saveDicChooser.addChoosableFileFilter(filterDic);
		saveDicChooser.setFileFilter(filterDic);
		saveDicChooser.setAcceptAllFileFilterUsed(false);

		openGramChooser = new JFileChooser();
		openGramChooser.setDialogTitle("Open a grammar");
		openGramChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openGramChooser.addChoosableFileFilter(filterGram);
		openGramChooser.addChoosableFileFilter(filterCSharpGram);
		openGramChooser.setFileFilter(filterGram);

		saveGramChooser = new JFileChooser();
		saveGramChooser.setDialogTitle("Save a grammar");
		saveGramChooser.addChoosableFileFilter(filterGram);
		saveGramChooser.setFileFilter(filterGram);
		saveGramChooser.setAcceptAllFileFilterUsed(false);

		openDefDialogChooser = new JFileChooser();
		openDefDialogChooser.setDialogTitle("Open Properties\' definition");
		openDefDialogChooser.setAcceptAllFileFilterUsed(false);
		openDefDialogChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openDefDialogChooser.addChoosableFileFilter(filterDef);
		openDefDialogChooser.setFileFilter(filterDef);

		saveDefDialogChooser = new JFileChooser();
		saveDefDialogChooser.setDialogTitle("Save Properties\' definition");
		saveDefDialogChooser.addChoosableFileFilter(filterDef);
		saveDefDialogChooser.setFileFilter(filterDef);
		saveDefDialogChooser.setAcceptAllFileFilterUsed(false);

		openSourceChooser = new JFileChooser();
		openSourceChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		openCorpusChooser = new JFileChooser();
		FileNameExtensionFilter filterCorpus = new FileNameExtensionFilter("NooJ Corpus (*.jnoc)",
				Constants.JNOC_EXTENSION);
		openCorpusChooser.addChoosableFileFilter(filterCorpus);
		openCorpusChooser.setFileFilter(filterCorpus);

		saveCorpusChooser = new JFileChooser();
		FileNameExtensionFilter filterSaveCorpus = new FileNameExtensionFilter("NooJ-formated Corpus (*.jnoc)",
				Constants.JNOC_EXTENSION);
		saveCorpusChooser.addChoosableFileFilter(filterSaveCorpus);
		saveCorpusChooser.setFileFilter(filterSaveCorpus);

		openTextChooser = new JFileChooser();
		FileNameExtensionFilter filterOpenText1 = new FileNameExtensionFilter("NooJ-formated Text (*.jnot)",
				Constants.JNOT_EXTENSION);
		openTextChooser.addChoosableFileFilter(filterOpenText1);
		openTextChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		saveTextChooser = new JFileChooser();
		FileNameExtensionFilter filterSaveText = new FileNameExtensionFilter("Nooj-formated Text (*.jnot)",
				Constants.JNOT_EXTENSION);
		saveTextChooser.addChoosableFileFilter(filterSaveText);

		openFolderChooser = new JFileChooser();
		openFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		saveConcordanceChooser = new JFileChooser();
		saveConcordanceChooser.setDialogTitle("Save concordance");
		FileNameExtensionFilter filterSaveConcordance = new FileNameExtensionFilter("NooJ concordance (*.jncc)",
				Constants.JNCC_EXTENSION);
		saveConcordanceChooser.addChoosableFileFilter(filterSaveConcordance);

		openConcordanceChooser = new JFileChooser();
		openConcordanceChooser.setDialogTitle("Open a concordance");
		FileNameExtensionFilter filterOpenConcordance = new FileNameExtensionFilter("NooJ concordance (*.jncc)",
				Constants.JNCC_EXTENSION);
		openConcordanceChooser.addChoosableFileFilter(filterOpenConcordance);

		saveProjectChooser = new JFileChooser();
		saveProjectChooser.setDialogTitle("Save a project");
		FileNameExtensionFilter filterSaveProject = new FileNameExtensionFilter("Project (*.jnop)",
				Constants.JNOP_EXTENSION);
		saveProjectChooser.addChoosableFileFilter(filterSaveProject);

		openProjectChooser = new JFileChooser();
		openProjectChooser.setDialogTitle("Open a project");
		openProjectChooser.addChoosableFileFilter(filterSaveProject);
	}

	
	private void createMenuContents()
	{
		JMenu mnFile = new JMenu("File");
		mnFile.setVisible(true);
		menuBar.add(mnFile);

		JMenu mnNew = new JMenu("New");
		mnFile.add(mnNew);

		JMenuItem mntmText = new JMenuItem("Text");
		mnNew.add(mntmText);
		mntmText.addActionListener(new NewTextCorpusActionListener(false, lastActive));

		JMenuItem mntmCorpus = new JMenuItem("Corpus");
		mnNew.add(mntmCorpus);
		mntmCorpus.addActionListener(new NewTextCorpusActionListener(true, lastActive));

		JSeparator separator_1 = new JSeparator();
		mnNew.add(separator_1);

		JMenuItem mntmNewDictionary = new JMenuItem("Dictionary");
		mnNew.add(mntmNewDictionary);
		mntmNewDictionary.addActionListener(new NewDictionaryPropDefActionListener(true));

		JMenuItem mntmPropertiesDefinition = new JMenuItem("Properties' definition");
		mnNew.add(mntmPropertiesDefinition);
		mntmPropertiesDefinition.addActionListener(new NewDictionaryPropDefActionListener(false));

		JSeparator separator_2 = new JSeparator();
		mnNew.add(separator_2);

		JMenuItem mntmGrammar = new JMenuItem("Grammar");
		mnNew.add(mntmGrammar);
		mntmGrammar.addActionListener(new NewGrammarActionListener());

		JMenu mnOpen = new JMenu("Open");
		mnFile.add(mnOpen);

		JMenuItem mntmText_1 = new JMenuItem("Text");
		mnOpen.add(mntmText_1);
		mntmText_1.addActionListener(new OpenTextActionListener(desktopPane));

		JMenuItem mntmCorpus_1 = new JMenuItem("Corpus");
		mnOpen.add(mntmCorpus_1);
		mntmCorpus_1.addActionListener(new OpenCorpusActionListener(desktopPane));

		JMenuItem mntmConcordance = new JMenuItem("Concordance");
		mnOpen.add(mntmConcordance);
		mntmConcordance.addActionListener(new OpenConcordanceActionListener(desktopPane));

		JSeparator separator_3 = new JSeparator();
		mnOpen.add(separator_3);

		JMenuItem mntmOpenDictionary = new JMenuItem("Dictionary");
		mnOpen.add(mntmOpenDictionary);
		mntmOpenDictionary.addActionListener(new OpenDictionaryActionListener(desktopPane));

		JMenuItem mntmPropertiesDefinition_1 = new JMenuItem("Properties' definition");
		mnOpen.add(mntmPropertiesDefinition_1);
		mntmPropertiesDefinition_1.addActionListener(new OpenPropDefActionListener(desktopPane));

		JSeparator separator_4 = new JSeparator();
		mnOpen.add(separator_4);

		JMenuItem mntmGrammar_1 = new JMenuItem("Grammar");
		mntmGrammar_1.addActionListener(new OpenGrammarActionListener(desktopPane));
		mnOpen.add(mntmGrammar_1);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mnFile.add(mntmSave);
		mntmSave.addActionListener(new SaveActionListener(false));

		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		
		mnFile.add(mntmSaveAs);
		mntmSaveAs.addActionListener(new SaveActionListener(true));

		

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		mntmClose.addActionListener(new CloseActionListener(desktopPane));

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.addActionListener(new ExitActionListener(frame));

		
		//JMenu mnEdit = new JMenu("Edit");
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		
		

		mntmCopy = new JMenuItem("Copy");
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mnEdit.add(mntmCopy);
		

		mntmCut = new JMenuItem("Cut");
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		mnEdit.add(mntmCut);

		mntmPaste = new JMenuItem("Paste");
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mnEdit.add(mntmPaste);

		mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mnEdit.add(mntmSelectAll);
		
		
		

		JSeparator separator_5 = new JSeparator();
		mnEdit.add(separator_5);

		JMenuItem mntmFindReplace = new JMenuItem("Find & Replace");
		mntmFindReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		mnEdit.add(mntmFindReplace);
		mntmFindReplace.addActionListener(new FindReplaceActionListener(desktopPane));

		JSeparator separator_6 = new JSeparator();
		mnEdit.add(separator_6);

		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		mnEdit.add(mntmUndo);
		mntmUndo.addActionListener(new UndoActionListener(doManager));

		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		mnEdit.add(mntmRedo);
		mntmRedo.addActionListener(new RedoActionListener(doManager));

		JMenu mnLab = new JMenu("Lab");
		menuBar.add(mnLab);

		JMenuItem mntmTextEncoding = new JMenuItem("Text Encoding");
		mnLab.add(mntmTextEncoding);
		mntmTextEncoding.addActionListener(new TextEncodingActionListener(desktopPane));

		JSeparator separator_8 = new JSeparator();
		mnLab.add(separator_8);

		JMenuItem mntmPerlRegExpr = new JMenuItem("PERL Reg. Expr.");
		mnLab.add(mntmPerlRegExpr);
		mntmPerlRegExpr.addActionListener(new PerlRegexActionListener(desktopPane));

		JMenuItem mntmConstructACorpus = new JMenuItem("Construct a Corpus");
		mnLab.add(mntmConstructACorpus);
		mntmConstructACorpus.addActionListener(new ConstructCorpusActionListener(desktopPane));

		JSeparator separator_7 = new JSeparator();
		mnLab.add(separator_7);

		JMenuItem mntmLanguageSpecifics = new JMenuItem("Language Specifics");
		mnLab.add(mntmLanguageSpecifics);
		mntmLanguageSpecifics.addActionListener(new LanguageSpecificsActionListener(desktopPane));

		JMenuItem mntmMorphology = new JMenuItem("Morphology");
		mnLab.add(mntmMorphology);
		mntmMorphology.addActionListener(new MorphologyActionListener(desktopPane));

		JMenuItem mntmDictionary = new JMenuItem("Dictionary");
		mnLab.add(mntmDictionary);
		mntmDictionary.addActionListener(new DictionaryActionListener(desktopPane));

		JMenu mnProject = new JMenu("Project");
		menuBar.add(mnProject);

		mntmRunProject = new JMenuItem("Run Project");
		mnProject.add(mntmRunProject);
		mntmRunProject.addActionListener(new UseProjectActionListener(desktopPane));

		JSeparator separator_9 = new JSeparator();
		mnProject.add(separator_9);

		mntmNewProject = new JMenuItem("Package Configuration");
		mnProject.add(mntmNewProject);
		mntmNewProject.addActionListener(new PackageConfigurationActionListener(desktopPane));

		JMenu mnWindows = new JMenu("Windows");
		menuBar.add(mnWindows);

		JMenuItem mntmResizeTo800 = new JMenuItem("Resize to 800x600");
		mnWindows.add(mntmResizeTo800);
		mntmResizeTo800.addActionListener(new ResizeActionListener(frame, 1));

		JMenuItem mntmResizeTo1024 = new JMenuItem("Resize to 1024x768");
		mnWindows.add(mntmResizeTo1024);
		mntmResizeTo1024.addActionListener(new ResizeActionListener(frame, 2));

		JSeparator separator_10 = new JSeparator();
		mnWindows.add(separator_10);

		JMenuItem mntmCascade = new JMenuItem("Cascade");
		mnWindows.add(mntmCascade);
		mntmCascade.addActionListener(new CascadeWindowsActionListener(desktopPane));

		JMenuItem mntmTileHorizontally = new JMenuItem("Tile Horizontally");
		mnWindows.add(mntmTileHorizontally);
		mntmTileHorizontally.addActionListener(new TileWindowsActionListener(desktopPane, true));

		JMenuItem mntmTileVertically = new JMenuItem("Tile Vertically");
		mnWindows.add(mntmTileVertically);
		mntmTileVertically.addActionListener(new TileWindowsActionListener(desktopPane, false));

		JMenuItem mntmArrangeIcons = new JMenuItem("Arrange Icons");
		mnWindows.add(mntmArrangeIcons);
		mntmArrangeIcons.addActionListener(new ArrangeIconsActionListener(desktopPane));

		JMenuItem mntmMinimizeAllWindows = new JMenuItem("Minimize All Windows");
		mnWindows.add(mntmMinimizeAllWindows);
		mntmMinimizeAllWindows.addActionListener(new MinimizeAllWindowsActionListener(desktopPane, false));

		JMenuItem mntmMinimizeAllOther = new JMenuItem("Minimize All Other Windows");
		mnWindows.add(mntmMinimizeAllOther);
		mntmMinimizeAllOther.addActionListener(new MinimizeAllWindowsActionListener(desktopPane, true));

		JMenu mnInfo = new JMenu("Info");
		menuBar.add(mnInfo);

		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mnInfo.add(mntmPreferences);
		mntmPreferences.addActionListener(new PreferencesActionListener());

		JMenuItem mntmConsole = new JMenuItem("Console");
		mnInfo.add(mntmConsole);
		mntmConsole.addActionListener(new ConsoleActionListener(desktopPane));

		JSeparator separator_11 = new JSeparator();
		mnInfo.add(separator_11);

		JMenuItem mntmAboutNooj = new JMenuItem("About NooJ");
		mnInfo.add(mntmAboutNooj);
		mntmAboutNooj.addActionListener(new AboutNooJ(desktopPane));
	}

	
	
	
	public static HashMap<String, JMenuItem> getTextCommands()
	{
		HashMap<String, JMenuItem> commands = new HashMap<String, JMenuItem>();
		commands.put("Cut", mntmCut);
		commands.put("Copy", mntmCopy);
		commands.put("Paste", mntmPaste);
		commands.put("Select All", mntmSelectAll);
		return commands;
	}
	

	public static JDesktopPane getDesktopPane()
	{
		return desktopPane;
	}

	public static JMenuBar getMenuBar()
	{
		return menuBar;
	}

	public static JFileChooser getOpenDicChooser()
	{
		return openDicChooser;
	}

	public static JFileChooser getSaveDicChooser()
	{
		return saveDicChooser;
	}

	public static JFileChooser getOpenGramChooser()
	{
		return openGramChooser;
	}

	public static JFileChooser getSaveGramChooser()
	{
		return saveGramChooser;
	}

	public static JFileChooser getOpenDefDialogChooser()
	{
		return openDefDialogChooser;
	}

	public static JFileChooser getSaveDefDialogChooser()
	{
		return saveDefDialogChooser;
	}

	public static JFileChooser getOpenCorpusChooser()
	{
		return openCorpusChooser;
	}

	public static JFileChooser getSaveCorpusChooser()
	{
		return saveCorpusChooser;
	}

	public static JFileChooser getOpenTextChooser()
	{
		return openTextChooser;
	}

	public static JFileChooser getSaveTextChooser()
	{
		return saveTextChooser;
	}

	public static JFileChooser getOpenSourceChooser()
	{
		return openSourceChooser;
	}

	public static JFileChooser getOpenFolderChooser()
	{
		return openFolderChooser;
	}

	public static JFileChooser getSaveConcordanceChooser()
	{
		return saveConcordanceChooser;
	}

	public static JFileChooser getOpenConcordanceChooser()
	{
		return openConcordanceChooser;
	}

	public static JFileChooser getOpenProjectChooser()
	{
		return openProjectChooser;
	}

	public static JFileChooser getSaveProjectChooser()
	{
		return saveProjectChooser;
	}

	public static CustomStatusBar getStatusBar()
	{
		return statusBar;
	}

	public static JMenuItem getMntmRunProject()
	{
		return mntmRunProject;
	}

	public static JMenuItem getMntmNewProject()
	{
		return mntmNewProject;
	}

	public static XmlAnnotationsTextPaneResources getXmlAnnotationsTextPaneResources()
	{
		return xmlAnnotationsTextPaneResources;
	}

	public static void setSavedPreferences(Preferences savedPreferences)
	{
		Launcher.savedPreferences = savedPreferences;
	}

	public static List<String> getRegexMemoryList()
	{
		return regexMemoryList;
	}

	public static void setRegexMemoryList(List<String> regexMemoryList)
	{
		Launcher.regexMemoryList = regexMemoryList;
	}

	public static List<String> getXmlMemoryList()
	{
		return xmlMemoryList;
	}

	public static void setXmlMemoryList(List<String> xmlMemoryList)
	{
		Launcher.xmlMemoryList = xmlMemoryList;
	}

	public static int getCorpusTextRadioButtonSelectionMemory()
	{
		return corpusTextRadioButtonSelectionMemory;
	}

	public static void setCorpusTextRadioButtonSelectionMemory(int corpusTextRadioButtonSelectionMemory)
	{
		Launcher.corpusTextRadioButtonSelectionMemory = corpusTextRadioButtonSelectionMemory;
	}

	public static int getRegexMemoryIndex()
	{
		return regexMemoryIndex;
	}

	public static void setRegexMemoryIndex(int regexMemoryIndex)
	{
		Launcher.regexMemoryIndex = regexMemoryIndex;
	}

	public static int getXmlMemoryIndex()
	{
		return xmlMemoryIndex;
	}

	public static void setXmlMemoryIndex(int xmlMemoryIndex)
	{
		Launcher.xmlMemoryIndex = xmlMemoryIndex;
	}

	public static int getIndexOfDefaultEncoding()
	{
		return indexOfDefaultEncoding;
	}

	public static void setIndexOfDefaultEncoding(int indexOfDefaultEncoding)
	{
		Launcher.indexOfDefaultEncoding = indexOfDefaultEncoding;
	}

	public static String getEncodingCodeOfOpenGrammar()
	{
		return encodingCodeOfOpenGrammar;
	}

	public static void setEncodingCodeOfOpenGrammar(String encodingCodeOfOpenGrammar)
	{
		Launcher.encodingCodeOfOpenGrammar = encodingCodeOfOpenGrammar;
	}
}