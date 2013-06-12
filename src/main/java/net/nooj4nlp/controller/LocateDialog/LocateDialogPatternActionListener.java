package net.nooj4nlp.controller.LocateDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Action listener for Locate Dialog and its radio button, pattern group.
 * 
 */
public class LocateDialogPatternActionListener implements ActionListener
{
	private JInternalFrame frame;
	private List<String> listOfFilesToBeSaved = new ArrayList<String>();

	private LocateDialog locateDialog;

	/**
	 * Constructor for opened text.
	 * 
	 * @param frame
	 *            - the last opened window
	 * @param controller
	 *            - text controller of current text
	 */
	public LocateDialogPatternActionListener(JInternalFrame frame, TextEditorShellController controller,
			LocateDialog locateDialog)
	{
		this.listOfFilesToBeSaved = controller.getLocateGrammarMemoryList();
		this.frame = frame;

		this.locateDialog = locateDialog;
	}

	/**
	 * Constructor for opened corpus.
	 * 
	 * @param frame
	 *            - the last opened window
	 * @param controller
	 *            - corpus controller of current corpus
	 */
	public LocateDialogPatternActionListener(JInternalFrame frame, CorpusEditorShellController controller,
			LocateDialog locateDialog)
	{
		this.frame = frame;
		this.listOfFilesToBeSaved = controller.getLocateGrammarMemoryList();

		this.locateDialog = locateDialog;
	}

	/**
	 * Empty constructor.
	 */
	public LocateDialogPatternActionListener(LocateDialog locateDialog)
	{
		this.locateDialog = locateDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == locateDialog.getRbNooJPattern())
		{
			locateDialog.getNooJRegeXCombo().setEnabled(true);
			locateDialog.getSyntacticAnalysisCBox().setSelected(false);
			disableGrammarComponents();
			enableIndexRadioButtons();
		}
		else if (e.getSource() == locateDialog.getRbNooJGrammar())
		{
			locateDialog.getNooJRegeXCombo().setEnabled(false);
			enableIndexRadioButtons();
			enableGrammarComponents();
			if (locateDialog.getNooJGrammarPathCombo().getSelectedItem().toString().equals(""))
				openFileChooserWindow();
		}

		else if (e.getSource() == locateDialog.getSetButton())
			openFileChooserWindow();
		else
		{
			locateDialog.getNooJRegeXCombo().setEnabled(true);
			locateDialog.getSyntacticAnalysisCBox().setSelected(false);
			disableGrammarComponents();
			disableIndexRadioButtons();
		}
	}

	/**
	 * Function for opening file chooser window in Locate Dialog.
	 */
	private void openFileChooserWindow()
	{
		// set the start directory
		String defaultPath = Paths.docDir + System.getProperty("file.separator") + Launcher.preferences.deflanguage
				+ System.getProperty("file.separator") + Constants.SYNTACTIC_ANALYSIS_PATH;
		File defaultFile = new File(defaultPath);

		// only grammar format is on
		FileNameExtensionFilter filterGram = new FileNameExtensionFilter("Nooj Grammar (*.nog)",
				Constants.JNOG_EXTENSION);
		// TODO: File Chooser should have default directory? Open Gram File Chooser should have default
		// directory? Other approach to launcher's file choosers (there's no default directory anywhere)?

		JFileChooser jFileChooser = new JFileChooser(defaultFile);
		jFileChooser.setDialogTitle("Open a grammar");
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.addChoosableFileFilter(filterGram);
		jFileChooser.setFileFilter(filterGram);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setAcceptAllFileFilterUsed(false);
		int result = jFileChooser.showOpenDialog(frame);

		// if a file is selected, get path and write it to the combo box and its memory list
		if (result == JFileChooser.APPROVE_OPTION)
		{
			JComboBox combo = locateDialog.getNooJGrammarPathCombo();
			File selectedFile = jFileChooser.getSelectedFile();
			String filePath = selectedFile.getAbsolutePath();
			combo.setSelectedItem(filePath);
			// do not resize combo box if size of text changes
			combo.setPrototypeDisplayValue("XXX");
			combo.addItem(filePath);
			listOfFilesToBeSaved.add(filePath);
		}
	}

	// enable/disable functions of graphic components
	private void enableGrammarComponents()
	{
		locateDialog.getSetButton().setEnabled(true);
		locateDialog.getSyntacticAnalysisCBox().setEnabled(true);
		locateDialog.getNooJGrammarPathCombo().setEnabled(true);
	}

	private void disableGrammarComponents()
	{
		locateDialog.getSetButton().setEnabled(false);
		locateDialog.getSyntacticAnalysisCBox().setEnabled(false);
		locateDialog.getNooJGrammarPathCombo().setEnabled(false);
	}

	private void enableIndexRadioButtons()
	{
		locateDialog.getRbShortestMatches().setEnabled(true);
		locateDialog.getRbAllIndexMatches().setEnabled(true);
		locateDialog.getRbLongestMatches().setEnabled(true);
	}

	private void disableIndexRadioButtons()
	{
		locateDialog.getRbShortestMatches().setEnabled(false);
		locateDialog.getRbAllIndexMatches().setEnabled(false);
		locateDialog.getRbLongestMatches().setEnabled(false);
	}
}