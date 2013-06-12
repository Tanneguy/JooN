package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Gram;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.gui.dialogs.GenerateDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;

/**
 * Action listener for 'Explore' button of Generate dialog.
 * 
 */
public class GenerateActionListener implements ActionListener
{
	private GenerateDialog genDialog;
	private GrammarEditorShellController grammarController;

	/**
	 * Constructor.
	 * 
	 * @param genDialog
	 *            - generate dialog from which the button was clicked
	 * @param grammarController
	 *            - grammar controller of the opened grammar
	 */
	public GenerateActionListener(GenerateDialog genDialog, GrammarEditorShellController grammarController)
	{
		this.genDialog = genDialog;
		this.grammarController = grammarController;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String currentGraph = grammarController.currentNode.toString();
		if (currentGraph.equals(""))
			currentGraph = grammarController.currentNode.getFirstChild().toString();
		genDialog.setTitle("Explore graph " + currentGraph + " in grammar "
				+ (new File(grammarController.getFullName())).getName());

		// maximal number of matches
		int matchLimit = -1;

		if (genDialog.getCbStopAfter().isSelected() && genDialog.getRbSequences().isSelected())
		{
			try
			{
				matchLimit = Integer.parseInt(genDialog.getTfSequences().getText());
			}
			catch (NumberFormatException e1)
			{
				// exception is not handled - so set the default values
				matchLimit = 100;
				genDialog.getTfSequences().setText("100");
			}
		}

		// maximal number of matches
		Date dt = new Date(Long.MAX_VALUE);
		int delay = -1;

		if (genDialog.getCbStopAfter().isSelected() && genDialog.getRbSeconds().isSelected())
		{
			try
			{
				delay = Integer.parseInt(genDialog.getTfSeconds().getText());
			}
			catch (NumberFormatException e1)
			{
				// exception is not handled - so set the default values
				delay = 5;
				genDialog.getTfSeconds().setText("5");
			}

			dt = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			calendar.add(Calendar.SECOND, delay);
			dt = calendar.getTime();
		}

		if (genDialog.getCbExploreEmbeddedGraphs().isSelected())
		{
			
		}

		// compile grammar
		Grammar grammar = grammarController.grammar;

		if (grammar == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_GRAMMAR_MESSAGE + "= gname",
					Constants.CANNOT_LOAD_GRAMMAR_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String errorMessage = grammar.compileAndComputeFirst(null);

		if (errorMessage != null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
					Constants.CANNOT_LOAD_GRAMMAR_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Gram grm = grammar.grams.get(currentGraph);

		if (grm == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.GRAMMAR_CANNOT_FIND_MAIN_GRAPH,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Language lan = new Language (grammar.iLanguage);
		String[] results = grm.generateParaphrases(1,grammar.grams,matchLimit,dt,grammar.gramType,lan,false);
		if (results == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.GRAMMAR_CANNOT_GENERATE_LANGUAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		DictionaryEditorShell shell = new DictionaryEditorShell();
		shell.getController().initLoad(grammar.iLanguage);

		StringBuilder sb = new StringBuilder();
		int resNb = results.length / 2;

		if (matchLimit != -1 && resNb > matchLimit)
			resNb = matchLimit;

		for (int i = 0; i < resNb; i++)
		{
			int i2 = i * 2;
			int j2 = i2 + 1;
			sb.append(results[i2]);

			if (results[j2] == null || results[j2].equals(""))
				sb.append(",NOINFO\n");
			else
			{
				sb.append(",");

				if (isASingleAnnotation(results[j2]))
				{
					String info = results[j2].substring(1, results[j2].length() - 2);
					sb.append(info);
				}
				else
					sb.append(results[j2]);

				sb.append("\n");
			}
		}

		String text = "# NooJ V4\n";
		text +="# Dictionary\n";
		text +="#\n";
		text +="# Input Language is: " + grammar.iLanguage + "\n";
		text +="#\n";
		text +="# Alphabetical order is not required.\n";
		text +="#\n";
		text +="# Use inflectional & derivational paradigms' description files (.nof), e.g.:\n";
		text +="# Special Command: #use paradigms.nof\n";
		text +="#\n";
		text +="# Special Features: +NW (non-word) +FXC (frozen expression component) +UNAMB (unambiguous lexical entry)\n";
		text +="#                   +FLX= (inflectional paradigm) +DRV= (derivational paradigm)\n";
		text +="#\n";
		text +="# Special Characters: '\\' '\"' '+' ',' '#' ' '\n";
		text +="#\n";
		text += "# Dictionary was generated automatically: " + resNb + " entries.\n";
		text += "#\n";
		text += sb.toString();
		shell.getTextPane().setText(text);

		Launcher.getDesktopPane().add(shell);
		shell.setVisible(true);
	}

	private boolean isASingleAnnotation(String info)
	{
		if (info == null || "".equals(info))
			return false;

		if (info.charAt(0) != '<')
			return false;

		if (info.charAt(info.length() - 1) != '>')
			return false;

		info = info.substring(1, info.length() - 2);
		int level = 1;

		for (int i = 0; i < info.length(); i++)
		{
			char character = info.charAt(i);

			if (character == '<')
				level++;
			else if (character == '>')
				level--;

			if (level == 0)
				return false;
		}

		if (level != 1)
			return false;

		return true;
	}
}