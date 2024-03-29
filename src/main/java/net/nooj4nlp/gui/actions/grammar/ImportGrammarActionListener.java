package net.nooj4nlp.gui.actions.grammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.OpenGrammarDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;

/**
 * Class implements importing grammar or graph to opened grammar.
 * 
 */
public class ImportGrammarActionListener implements ActionListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;
	private boolean closedWithXButton = true;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            - opened grammar shell
	 */
	public ImportGrammarActionListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser openGraphChooser = new JFileChooser();
		openGraphChooser.setDialogTitle("Open");
		openGraphChooser.setAcceptAllFileFilterUsed(false);
		openGraphChooser.setMultiSelectionEnabled(true);

		String currentDirToBeSetPath = FilenameUtils.concat(Paths.docDir,
				FilenameUtils.concat(controller.lan.isoName, "Syntactic Analysis"));
		openGraphChooser.setCurrentDirectory(new File(currentDirToBeSetPath));

		FileNameExtensionFilter fileFilter;

		String extension = "";

		boolean knownType = true;

		if (controller.grammar.gramType == GramType.SYNTAX)
			extension = "nog";
		else if (controller.grammar.gramType == GramType.FLX)
			extension = "nof";
		else if (controller.grammar.gramType == GramType.MORPHO)
			extension = "nom";
		else
		{
			knownType = false;
			openGraphChooser.setAcceptAllFileFilterUsed(true);
		}

		if (knownType)
		{
			fileFilter = new FileNameExtensionFilter("Nooj Grammar (*." + extension + ")", extension);
			FileNameExtensionFilter filterGrammar = new FileNameExtensionFilter("INTEX Graphs (*.grf)", "grf");
			openGraphChooser.addChoosableFileFilter(fileFilter);
			openGraphChooser.addChoosableFileFilter(filterGrammar);
			openGraphChooser.setFileFilter(fileFilter);
		}

		if (openGraphChooser.showOpenDialog(Launcher.getDesktopPane()) == JFileChooser.CANCEL_OPTION)
			return;

		GramType gramType = controller.grammar.gramType;

		if (openGraphChooser.getFileFilter().getDescription().startsWith("INTEX"))
		{
			// disable language selection and grammar type
			OpenGrammarDialog openGrammarDialog = new OpenGrammarDialog(this, Launcher.getIndexOfDefaultEncoding());

			JList inputLanguageList = openGrammarDialog.getInputLanguageList();
			int index = ((DefaultListModel) inputLanguageList.getModel()).indexOf(controller.grammar.iLanguage);

			if (index != -1)
			{
				inputLanguageList.setSelectedIndex(index);
				inputLanguageList.ensureIndexIsVisible(index);
			}

			inputLanguageList.setEnabled(false);

			JList outputLanguageList = openGrammarDialog.getOutputLanguageList();
			index = ((DefaultListModel) outputLanguageList.getModel()).indexOf(controller.grammar.oLanguage);

			if (index != -1)
			{
				outputLanguageList.setSelectedIndex(index);
				outputLanguageList.ensureIndexIsVisible(index);
			}

			outputLanguageList.setEnabled(false);

			JButton buttonFlex = openGrammarDialog.getInflectionalGrammarButton();
			JButton buttonMorpho = openGrammarDialog.getMorphologicalGrammarButton();
			JButton buttonSyntax = openGrammarDialog.getSyntacticGrammarButton();

			if (gramType == GramType.FLX)
			{
				buttonFlex.setEnabled(true);
				buttonMorpho.setEnabled(false);
				buttonSyntax.setEnabled(false);
			}
			else if (gramType == GramType.MORPHO)
			{
				buttonFlex.setEnabled(false);
				buttonMorpho.setEnabled(true);
				buttonSyntax.setEnabled(false);
			}
			else
			// syntax
			{
				buttonFlex.setEnabled(false);
				buttonMorpho.setEnabled(false);
				buttonSyntax.setEnabled(true);
			}

			openGrammarDialog.setModal(true);
			openGrammarDialog.setVisible(true);

			// import morpho graph: select languages

			if (closedWithXButton)
				return;

			String encodingCode = Launcher.getEncodingCodeOfOpenGrammar();

			if (gramType == GramType.FLX)
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_INTEX_CONVERSION_FLX,
						Constants.NOOJ_INTEX_CONVERSION, JOptionPane.INFORMATION_MESSAGE);

			else if (gramType == GramType.MORPHO)
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_INTEX_CONVERSION_MORPH,
						Constants.NOOJ_INTEX_CONVERSION, JOptionPane.INFORMATION_MESSAGE);

			else
				
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_INTEX_CONVERSION_SYNTAX,
						Constants.NOOJ_INTEX_CONVERSION, JOptionPane.INFORMATION_MESSAGE);

			for (File file : openGraphChooser.getSelectedFiles())
			{
				// look if the graph is already there
				String fileName = FilenameUtils.removeExtension(file.getName());
				boolean found = false;

				do
				{
					found = false;

					for (int iG = 0; iG < controller.grammar.graphs.size(); iG++)
					{
						Graph g = controller.grammar.graphs.get(iG);

						if (g.name.equals(fileName))
						{
							found = true;
							break;
						}
					}

					if (found)
						fileName += "'";
				}

				while (found);

				String filePath = file.getAbsolutePath();

				if (!controller.addIntexGraph(filePath, fileName, encodingCode, gramType))
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_IMPORT_GRAPH + filePath,
							Constants.NOOJ_INTEX_CONVERSION_FAILURE, JOptionPane.ERROR_MESSAGE);
				else
					controller.modify("import intex graph", true, false);
			}
		}

		else
		{
			for (File file : openGraphChooser.getSelectedFiles())
			{
				// look if the graph is already there
				String fileName = FilenameUtils.removeExtension(file.getName());
				boolean found = false;

				do
				{
					found = false;

					for (int iG = 0; iG < controller.grammar.graphs.size(); iG++)
					{
						Graph g = controller.grammar.graphs.get(iG);

						if (g.name.equals(fileName))
						{
							found = true;
							break;
						}
					}

					if (found)
						fileName += "'";
				}

				while (found);

				controller.addNoojGrammar(file.getAbsolutePath(), fileName, gramType);
				controller.modify("import nooj grammar", true, false);
				// HACK!
				if (controller.editor.formGramStruct != null)
					controller.editor.formGramStruct.getController().expandAll(true);
			}
		}

		controller.updateFormHeader();
		controller.editor.invalidate();
		controller.editor.repaint();
	}

	public void setClosedWithXButton(boolean closedWithXButton)
	{
		this.closedWithXButton = closedWithXButton;
	}
}