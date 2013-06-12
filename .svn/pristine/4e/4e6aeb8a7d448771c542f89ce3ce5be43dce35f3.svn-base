package net.nooj4nlp.controller.MorphologyDialog;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Regexp;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;

public class ButtonListener implements ActionListener
{

	private JList listLanguage;
	private JComboBox comboWordRoot, comboCommandSuffix, comboLemma, comboExpression, comboLookup;
	private JRadioButton rdbtnWordCommand, rdbtnLemmaExpression, rdbtnLookup;
	private DefaultListModel resultModel;
	private JPanel pnlResult;

	private String word;
	private String command;
	private int icommand;
	private Regexp exp;

	public ButtonListener(JList listLanguage, JComboBox comboWordRoot, JComboBox comboCommandSuffix,
			JComboBox comboLemma, JComboBox comboExpression, JComboBox comboLookup, JRadioButton rdbtnWordCommand,
			JRadioButton rdbtnLemmaExpression, JRadioButton rdbtnLookup, DefaultListModel resultModel, JPanel pnlResult)
	{
		super();
		this.listLanguage = listLanguage;
		this.comboWordRoot = comboWordRoot;
		this.comboCommandSuffix = comboCommandSuffix;
		this.comboLemma = comboLemma;
		this.comboExpression = comboExpression;
		this.comboLookup = comboLookup;
		this.rdbtnWordCommand = rdbtnWordCommand;
		this.rdbtnLemmaExpression = rdbtnLemmaExpression;
		this.rdbtnLookup = rdbtnLookup;
		this.resultModel = resultModel;
		this.pnlResult = pnlResult;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		resultModel.removeAllElements();

		Language lan = new Language((String) listLanguage.getSelectedValue());

		if (rdbtnWordCommand.isSelected())
		{

			if (comboWordRoot.getSelectedItem() == null || ((String) comboWordRoot.getSelectedItem()).equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please type in a word form", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			word = (String) comboWordRoot.getSelectedItem();
			if (word.charAt(0) == '[')
			{
				int index = word.indexOf("] ");
				if (index != -1)
					word = word.substring(index + 2);
			}

			if (comboCommandSuffix.getSelectedItem() == null
					|| ((String) comboCommandSuffix.getSelectedItem()).equals(""))
			{
				JOptionPane
						.showMessageDialog(null, "Please type in a command", "NooJ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			command = (String) comboCommandSuffix.getSelectedItem();
			if (command.charAt(0) == '[')
			{
				int index = command.indexOf("] ");
				if (index != -1)
					command = command.substring(index + 2);
			}

			icommand = 0;
			try
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

				resultModel.addElement(word + "│ " + command);

				Timer timer = new Timer();
				AnimationTask task = new AnimationTask(resultModel, word, command, icommand, lan, timer);
				timer.scheduleAtFixedRate(task, 1000, 1000);
			}
			finally
			{
				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
			}

		}
		else if (rdbtnLemmaExpression.isSelected())
		{

			if (comboLemma.getSelectedItem() == null || ((String) comboLemma.getSelectedItem()).equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please type in a lemma", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			word = (String) comboLemma.getSelectedItem();
			if (word.charAt(0) == '[')
			{
				int index = word.indexOf("] ");
				if (index != -1)
					word = word.substring(index + 2);
			}

			if (comboExpression.getSelectedItem() == null || ((String) comboExpression.getSelectedItem()).equals(""))
			{
				JOptionPane
						.showMessageDialog(null, "Please type in a command", "NooJ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			command = (String) comboExpression.getSelectedItem();
			if (command.charAt(0) == '[')
			{
				int index = command.indexOf("] ");
				if (index != -1)
					command = command.substring(index + 2);
			}

			exp = new Regexp(command);
			exp.Grm.prepareForParsing();

			String[] forms = null, outputs = null;
			RefObject<String[]> formsRef = new RefObject<String[]>(forms);
			RefObject<String[]> outputsRef = new RefObject<String[]>(outputs);
			exp.Grm.inflect(lan, word, formsRef, outputsRef, null);
			forms = formsRef.argvalue;
			outputs = outputsRef.argvalue;

			if (forms == null)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot inflect lemma \"" + word + "\"",
						"NooJ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			pnlResult.setBorder(new TitledBorder(null, "Lemma '" + word + "' has " + forms.length + " forms:",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			for (int i = 0; i < forms.length; i++)
				resultModel.addElement(outputs[i] + " => " + forms[i]);
		}
		else if (rdbtnLookup.isSelected())
		{

			if (comboLookup.getSelectedItem() == null || ((String) comboLookup.getSelectedItem()).equals(""))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please type in a word", "NooJ",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			word = (String) comboLookup.getSelectedItem();
			if (word.charAt(0) == '[')
			{
				int index = word.indexOf("] ");
				if (index != -1)
					word = word.substring(index + 2);
			}

			// lookup dictionaries to get lexical entries
			RefObject<Language> lanRef = new RefObject<Language>(lan);
			Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir,
					Launcher.projectMode, Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
			lan = lanRef.argvalue;

			String errorMessage = "";
			RefObject<String> errmessageRef = new RefObject<String>(errorMessage);

			try
			{
				if (!engine.loadResources(Launcher.preferences.ldic.get(lan.isoName), null, true, errmessageRef))
				{
					errorMessage = errmessageRef.argvalue;
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
							"NooJ: cannot load linguistic resources", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			catch (HeadlessException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_HEADLESS, JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
						Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			ArrayList<String> sols = engine.lookupAllSDics((String) comboLookup.getSelectedItem());

			if (sols == null || sols.size() == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find any lexical entry for word "
						+ (String) comboLookup.getSelectedItem(), "NooJ", JOptionPane.ERROR_MESSAGE);
				return;
			}

			resultModel.removeAllElements();
			ArrayList<String> res = engine.inflectSolutions(sols);

			for (int i = 0; i < res.size(); i += 2)
				resultModel.addElement((String) res.get(i + 1) + " => " + (String) res.get(i));
		}
	}
}