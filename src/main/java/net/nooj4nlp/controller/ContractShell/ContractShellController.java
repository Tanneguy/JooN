package net.nooj4nlp.controller.ContractShell;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Gram;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.MatchType;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ContractShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class ContractShellController
{

	public Grammar grammar;
	public Language lan;
	public JTextPane txtConsole;

	private StyledDocument doc;
	private Font okFont, failureFont, commentFont;
	private GrammarEditorShell grammarEditor;
	private boolean colored;
	private ContractShell shell;

	public ContractShellController(GrammarEditorShell gShell, Grammar g, Language l, JTextPane tc, ContractShell shell)
	{
		grammarEditor = gShell;
		txtConsole = tc;
		grammar = g;
		lan = l;

		this.shell = shell;
		this.shell.setTitle("Contract for "
				+ (grammarEditor.getTitle().equals("Untitled") ? "" : grammarEditor.getTitle()));

		if (grammar != null && grammar.checkText != null && grammar.checkText != "")
			txtConsole.setText(grammar.checkText);

		// fonts
		commentFont = txtConsole.getFont();
		okFont = new Font(txtConsole.getFont().getFontName(), Font.BOLD, txtConsole.getFont().getSize());

		// Unchecked cast cannot be avoided here - a custom class that extends unknown type (?) should be defined, and
		// there is no need for that.
		Map<TextAttribute, Boolean> attributes = (Map<TextAttribute, Boolean>) okFont.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		failureFont = new Font(attributes);
	}

	public int checkContract() throws ClassNotFoundException, IOException
	{
		int grammarchecksok = 1;

		txtConsole.requestFocus();

		grammar = grammarEditor.getController().grammar;

		boolean projectMode = Launcher.projectMode;
		boolean backgroundWorking = Launcher.backgroundWorking;
		BackgroundWorker backgroundWorker = Launcher.backgroundWorker;

		if (grammar == null)
			return -1;

		RefObject<Language> lanRef = new RefObject<Language>(lan);
		Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, projectMode,
				Launcher.preferences, backgroundWorking, backgroundWorker);
		lan = lanRef.argvalue;

		String errmessage = null;
		RefObject<String> errmessageRef = new RefObject<String>(errmessage);
		if (!engine.loadResources(Launcher.preferences.ldic.get(lan.isoName),
				Launcher.preferences.lsyn.get(lan.isoName), true, errmessageRef))
		{
			errmessage = errmessageRef.argvalue;
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
					"WARNING: cannot load linguistic resources for " + lan.isoName, 2);
		}

		errmessage = grammar.compileAll(engine);
		if (errmessage != null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage, "NooJ Grammar Compilation Error",
					JOptionPane.ERROR_MESSAGE);

			return 0;
		}

		doc = txtConsole.getStyledDocument();

		Style comment = txtConsole.addStyle("Comment", null);
		StyleConstants.setForeground(comment, Color.black);
		StyleConstants.setFontFamily(comment, commentFont.getFamily());

		int lineLength = 0;

		for (int start = 0; start < txtConsole.getText().length(); start += lineLength + 1)
		{
			int i;
			for (i = 0; start + i < txtConsole.getText().length() && txtConsole.getText().charAt(start + i) != '\n'; i++)
				;
			String line = txtConsole.getText().substring(start, start + i);
			lineLength = line.length();

			// get rid of comments and spaces
			for (i = 0; i < line.length() && line.charAt(i) != '#'; i++)
				if (line.charAt(i) == '\\')
					i++;
			if (i < line.length())
				line = line.substring(0, i);
			line = line.trim();
			if (line.length() == 0)
			{
				doc.setCharacterAttributes(start, lineLength, txtConsole.getStyle("Comment"), true);
				continue;
			}

			boolean failure = false;

			if (line.charAt(0) == '*')
			{
				// Counter example: should not match
				line = line.substring(1);

				if (grammar.gramType == GramType.MORPHO)
				{
					ArrayList<String> sols = grammar.matchWord(line, engine, true, line, 0);
					if (sols != null)
						Engine.filterNonWords(sols); // filter out all non words +NW
					failure = (sols != null && sols.size() > 0);
				}
				else
				
				{
					// create a Ntext with file and directory...
					Ntext MyText = new Ntext(lan.isoName);
					MyText.buffer = line;
					MyText.mft = engine.delimit(MyText);

					// linguistic analysis using both lexical and syntactic resources
					HashMap<String, ArrayList<String>> simpleWordCache = new HashMap<String, ArrayList<String>>();
					MyText.annotations = new ArrayList<Object>();
					errmessageRef = new RefObject<String>(errmessage);
					if (!engine.tokenize(null, MyText, MyText.annotations, simpleWordCache, errmessageRef))
					{
						errmessage = errmessageRef.argvalue;
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage, "NooJ Tokenizer Error",
								JOptionPane.ERROR_MESSAGE);

						MyText.annotations = null;
						MyText.hLexemes = null;
						MyText.hUnknowns = null;
						return 0;
					}
					simpleWordCache = null;
					MyText.hPhrases = new HashMap<String, Integer>();
					

					// first apply grammars in dictionary/grammar pairs
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						errmessageRef = new RefObject<String>(errmessage);
						boolean applyres = engine.applyAllGrammars(null, MyText, MyText.annotations, 0, errmessageRef);
						errmessage = errmessageRef.argvalue;
						if (!applyres && errmessage != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
									"NooJ Syntactic Parsing Error", JOptionPane.ERROR_MESSAGE);
							return 0;
						}
						if (!applyres)
						{
							MyText.hPhrases = null;
							
							return 0;
						}
						MyText.cleanupBadAnnotations(MyText.annotations);
					}

					// then apply syntactic grammars
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						errmessageRef = new RefObject<String>(errmessage);
						boolean applyres = engine.applyAllGrammars(null, MyText, MyText.annotations, 1, errmessageRef);
						errmessage = errmessageRef.argvalue;
						if (!applyres && errmessage != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
									"NooJ Syntactic Parsing Error", JOptionPane.ERROR_MESSAGE);
							return 0;
						}
						if (!applyres)
						{
							MyText.hPhrases = null;
							
							return 0;
						}
					}

					// apply grammar
					ArrayList<Double> sollengths = null;
					ArrayList<ArrayList<String>> soloutputs = null;
					ArrayList<ArrayList<String>> solvariables = null;
					ArrayList<ArrayList<Double>> solinputs = null;
					ArrayList<ArrayList<Object>> solnodes = null;

					double cpos;

					Gram grm = grammar.grams.get("Main");
					if (grm == null || grm.states == null || grm.states.size() < 2)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + grammar.fullName
								+ " has no Main graph", "NooJ: no main graph in grammar", 2);
						return -1;
					}
					RefObject<ArrayList<Double>> sollengthsRef = new RefObject<ArrayList<Double>>(sollengths);
					RefObject<ArrayList<ArrayList<Double>>> solinputsRef = new RefObject<ArrayList<ArrayList<Double>>>(
							solinputs);
					RefObject<ArrayList<ArrayList<String>>> soloutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
							soloutputs);
					RefObject<ArrayList<ArrayList<Object>>> solnodesRef = new RefObject<ArrayList<ArrayList<Object>>>(
							solnodes);
					RefObject<ArrayList<ArrayList<String>>> solvariablesRef = new RefObject<ArrayList<ArrayList<String>>>(
							solvariables);

					ArrayList<String> recursiveCalls = new ArrayList<String>();

					int da = grammar.syntaxMatch(grammar.fullName, -1, line, 0.0, 1, MyText.mft, MyText.annotations,
							grm, sollengthsRef, solinputsRef, solvariablesRef, soloutputsRef, solnodesRef,
							MatchType.LONGEST, true, false, recursiveCalls);

					sollengths = sollengthsRef.argvalue;
					solinputs = solinputsRef.argvalue;
					soloutputs = soloutputsRef.argvalue;
					solnodes = solnodesRef.argvalue;
					solvariables = solvariablesRef.argvalue;

					if (da > 0)
					{
						failure = false;
						for (int isol = 0; isol < sollengths.size(); isol++)
						{
							int len = sollengths.get(isol).intValue();
							if (len < line.length())
								continue;
							if (Dic.isThereALexicalConstraint(soloutputs.get(isol)))
							{
								cpos = 0.0;
								errmessageRef = new RefObject<String>(errmessage);
								boolean check = engine.processConstraints(line, MyText.mft, 1, MyText.annotations,
										grammar, solinputs.get(isol), len, solvariables.get(isol), cpos,
										soloutputs.get(isol), errmessageRef);
								errmessage = errmessageRef.argvalue;

								if (errmessage != null)
								{
									JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
											"NooJ Constraint Processing Error", JOptionPane.ERROR_MESSAGE);
									return -1;
								}
								if (check)
								{
									failure = true; // I got a match => *test fails
									break;
								}
							}
							else
							{
								failure = true; // I got a match => *test fails
								break;
							}
						}
					}
				}
			}
			else
			{
				// example: should match
				if (grammar.gramType == GramType.MORPHO)
				{
					ArrayList<String> sols = grammar.matchWord(line, engine, true, line, 0);
					failure = (sols == null || sols.size() == 0);
				}
				else
			
				{
					// create a Ntext with file and directory...
					Ntext MyText = new Ntext(lan.isoName);
					MyText.buffer = line;
					MyText.mft = engine.delimit(MyText);

					// linguistic analysis using both lexical and syntactic resources
					HashMap<String, ArrayList<String>> simpleWordCache = new HashMap<String, ArrayList<String>>();
					MyText.annotations = new ArrayList<Object>();
					errmessageRef = new RefObject<String>(errmessage);
					if (!engine.tokenize(null, MyText, MyText.annotations, simpleWordCache, errmessageRef))
					{
						errmessage = errmessageRef.argvalue;
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage, "NooJ Tokenizer Error",
								JOptionPane.ERROR_MESSAGE);
						MyText.annotations = null;
						MyText.hLexemes = null;
						MyText.hUnknowns = null;
						return 0;
					}
					simpleWordCache = null;
					MyText.hPhrases = new HashMap<String, Integer>();
					

					// first apply grammars in dictionary/grammar pairs
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						errmessageRef = new RefObject<String>(errmessage);
						boolean applyres = engine.applyAllGrammars(null, MyText, MyText.annotations, 0, errmessageRef);
						errmessage = errmessageRef.argvalue;
						if (!applyres && errmessage != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
									"NooJ Syntactic Parsing Error", JOptionPane.ERROR_MESSAGE);
							return 0;
						}
						if (!applyres)
						{
							MyText.hPhrases = null;
							
							return 0;
						}
						MyText.cleanupBadAnnotations(MyText.annotations);
					}
					// then apply syntactic grammars
					if (engine.synGrms != null && engine.synGrms.size() > 0)
					{
						errmessageRef = new RefObject<String>(errmessage);
						boolean applyres = engine.applyAllGrammars(null, MyText, MyText.annotations, 1, errmessageRef);
						errmessage = errmessageRef.argvalue;
						if (!applyres && errmessage != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
									"NooJ Syntactic Parsing Error", JOptionPane.ERROR_MESSAGE);
							return 0;
						}
						if (!applyres)
						{
							MyText.hPhrases = null;
							
							return 0;
						}
					}

					// apply grammar
					ArrayList<Double> sollengths = null;
					ArrayList<ArrayList<String>> soloutputs = null;
					ArrayList<ArrayList<String>> solvariables = null;
					ArrayList<ArrayList<Double>> solinputs = null;
					ArrayList<ArrayList<Object>> solnodes = null;

					double cpos;
					Gram grm = grammar.grams.get("Main");
					if (grm == null || grm.states == null || grm.states.size() < 2)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + grammar.fullName
								+ " has no Main graph", "NooJ: no main graph in grammar", 2);

						return -1;
					}
					RefObject<ArrayList<Double>> sollengthsRef = new RefObject<ArrayList<Double>>(sollengths);
					RefObject<ArrayList<ArrayList<Double>>> solinputsRef = new RefObject<ArrayList<ArrayList<Double>>>(
							solinputs);
					RefObject<ArrayList<ArrayList<String>>> soloutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
							soloutputs);
					RefObject<ArrayList<ArrayList<Object>>> solnodesRef = new RefObject<ArrayList<ArrayList<Object>>>(
							solnodes);
					RefObject<ArrayList<ArrayList<String>>> solvariablesRef = new RefObject<ArrayList<ArrayList<String>>>(
							solvariables);

					ArrayList<String> recursiveCalls = new ArrayList<String>();

					int da = grammar.syntaxMatch(grammar.fullName, -1, line, 0.0, 1, MyText.mft, MyText.annotations,
							grm, sollengthsRef, solinputsRef, solvariablesRef, soloutputsRef, solnodesRef,
							MatchType.LONGEST, true, false, recursiveCalls);

					sollengths = sollengthsRef.argvalue;
					solinputs = solinputsRef.argvalue;
					soloutputs = soloutputsRef.argvalue;
					solvariables = solvariablesRef.argvalue;

					if (da > 0)
					{
						for (int isol = 0; isol < sollengths.size();)
						{
							int len = sollengths.get(isol).intValue();
							if (len < line.length())
							{
								sollengths.remove(isol);
								solinputs.remove(isol);
								soloutputs.remove(isol);
								continue;
							}
							else
							{
								if (Dic.isThereALexicalConstraint(soloutputs.get(isol)))
								{
									cpos = 0.0;
									errmessageRef = new RefObject<String>(errmessage);
									boolean check = engine.processConstraints(line, MyText.mft, 1, MyText.annotations,
											grammar, solinputs.get(isol), len, solvariables.get(isol), cpos,
											soloutputs.get(isol), errmessageRef);
									errmessage = errmessageRef.argvalue;
									if (errmessage != null)
									{
										JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
												"NooJ Constraint Processing Error", JOptionPane.ERROR_MESSAGE);
										return -1;
									}
									if (!check)
									{
										sollengths.remove(isol);
										solinputs.remove(isol);
										solvariables.remove(isol);
										soloutputs.remove(isol);
										continue;
									}
								}
							}
							isol++;
						}
						da = sollengths.size();
					}
					failure = (da == 0);
				}
			}

			Style fail = txtConsole.addStyle("Fail", null);
			StyleConstants.setForeground(fail, Color.red);
			StyleConstants.setFontFamily(fail, failureFont.getFamily());

			Style ok = txtConsole.addStyle("OK", null);
			StyleConstants.setForeground(ok, Color.green);
			StyleConstants.setFontFamily(ok, okFont.getFamily());

			if (failure)
			{
				grammarchecksok = 0;
				doc.setCharacterAttributes(start, lineLength, txtConsole.getStyle("Fail"), true);
			}
			else
			{
				doc.setCharacterAttributes(start, lineLength, txtConsole.getStyle("OK"), true);
			}
			colored = true;
		}

		return grammarchecksok;
	}

	public void UncolorCheckText()
	{
		if (!colored)
			return;
		doc = txtConsole.getStyledDocument();
		Style clean = txtConsole.addStyle("Clean", null);
		StyleConstants.setForeground(clean, Color.black);
		doc.setCharacterAttributes(0, txtConsole.getDocument().getLength(), txtConsole.getStyle("Clean"), true);
		colored = false;
	}
}
