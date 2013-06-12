package net.nooj4nlp.controller.ProduceParaphrasesController;

import java.awt.ComponentOrientation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
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
import net.nooj4nlp.gui.dialogs.ProduceParaphrasesDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class ProduceParaphrasesController
{
	private ProduceParaphrasesDialog dialog;

	private Engine engine;
	private GrammarEditorShellController grammarController;

	public ProduceParaphrasesController(ProduceParaphrasesDialog dialog, GrammarEditorShell gShell)
	{
		this.grammarController = gShell.getController();
		this.dialog = dialog;
	}

	public void setLanguage(Language lan)
	{
		dialog.getTextField().setComponentOrientation(
				lan.rightToLeft ? ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT);
	}

	private boolean isASingleAnnotation(String info)
	{
		if (info == null || info.equals(""))
			return false;
		if (info.charAt(0) != '<')
			return false;
		if (info.charAt(info.length() - 1) != '>')
			return false;
		info = info.substring(1, info.length() - 1);
		int level = 1;
		for (int i = 0; i < info.length(); i++)
		{
			if (info.charAt(i) == '<')
				level++;
			else if (info.charAt(i) == '>')
				level--;
			if (level == 0)
				return false;
		}
		if (level != 1)
			return false;
		return true;
	}

	public String storeVariablesInHash(ArrayList<String> solvariables, int i, boolean topmost,
			RefObject<Integer> ifollow, RefObject<HashMap<String, String>> hvar)
	{
		hvar = new RefObject<HashMap<String, String>>(new HashMap<String, String>());
		ifollow.argvalue = i;

		String value = "";
		for (; i < solvariables.size();)
		{
			String svar = solvariables.get(i);
			if (svar == null)
			{
				i++;
				continue;
			}
			if (svar.length() < 2)
			{
				i++;
				continue;
			}
			if (svar.charAt(0) != '$')
			{
				i++;
				continue;
			}
			if (svar.charAt(1) != '(')
			{
				i++;
				continue;
			}
			String varname = svar.substring(2);

			// get value
			if (i + 1 >= solvariables.size())
			{
				i++;
				continue;
			}
			for (i++; i < solvariables.size();)
			{
				String solval = solvariables.get(i);
				if (solval == null || solval.equals(""))
				{
					i++;
					continue;
				}
				if (solval.charAt(0) == ':')
				{
					i++;
					continue;
				}
				if (solval.equals("$)"))
				{
					i++;
					if (topmost)
						break; // there might be more variables defined in
					// solvariables
					ifollow.argvalue = i;
					return value;
				}

				String val2 = null;

				if (solval.length() > 2 && solval.substring(0, 1).equals("$("))
				{
					Integer ifollow2 = null;
					HashMap<String, String> hvar2 = null;
					RefObject<Integer> ifollow2Ref = new RefObject<Integer>(ifollow2);
					RefObject<HashMap<String, String>> hvar2Ref = new RefObject<HashMap<String, String>>(hvar2);

					val2 = storeVariablesInHash(solvariables, i, false, ifollow2Ref, hvar2Ref);

					ifollow2 = ifollow2Ref.argvalue;
					hvar2 = hvar2Ref.argvalue;

					String key = null;
					
					for (String key1 : hvar2.keySet())
					{
						
						key = key1;
						if (!hvar.argvalue.containsKey(key))
						{
							String val = hvar2.get(key);
							hvar.argvalue.put(key, val);
						}
					}
					if (!hvar.argvalue.containsKey(varname))
						hvar.argvalue.put(varname, val2);
					else
					{
						String old = hvar.argvalue.get(varname);
						hvar.argvalue.put(varname, old + " " + val2);
					}
					value += val2;
					i = ifollow2;
					continue;
				}

				String ent, lemma, info;
				ent = lemma = info = null;
				RefObject<String> lemmaRef = new RefObject<String>(lemma);
				RefObject<String> entRef = new RefObject<String>(ent);
				RefObject<String> infoRef = new RefObject<String>(info);

				boolean parseDelaf = Dic.parseDELAF(solval, entRef, lemmaRef, infoRef);

				lemma = lemmaRef.argvalue;
				ent = entRef.argvalue;
				info = infoRef.argvalue;

				if (parseDelaf)
					val2 = "<" + solval + ">";
				else
					val2 = solval;
				if (!hvar.argvalue.containsKey(varname))
					hvar.argvalue.put(varname, val2);
				else
				{
					String old = hvar.argvalue.get(varname);
					hvar.argvalue.put(varname, old + " " + val2);
				}
				i++;
			}
		}
		ifollow.argvalue = i;
		return value;
	}

	// Unused code
	private ArrayList<String> replaceVariablesWithValuesInInput(String phrase, int ipos,
			HashMap<String, String> hvariables, RefObject<ArrayList<HashMap<String, String>>> listofhvars,
			boolean getLexemes)
	{
		if (phrase == null)
		{
			listofhvars = null;
			return null;
		}
		if (ipos >= phrase.length())
		{
			ArrayList<String> newres = new ArrayList<String>();
			newres.add("");
			listofhvars.argvalue = new ArrayList<HashMap<String, String>>();
			listofhvars.argvalue.add(hvariables);
			return newres;
		}

		// compute res
		ArrayList<String> reslist = null;
		ArrayList<String> follows = null;
		String res = "";
		int nextipos = 0;
		if (phrase.charAt(ipos) == '\\')
		{
			res = phrase.substring(ipos, ipos + 2);
			nextipos = ipos + 2;
		}
		else if (phrase.charAt(ipos) == '<')
		{
			// a lexical unit in the grammar
			int i;
			for (i = 0; ipos + i < phrase.length() && phrase.charAt(i + ipos) != '>'; i++)
				;
			if (ipos + i >= phrase.length())
			{
				// a '<' is not closed
				res = "*Lexeme is not closed*";
				nextipos = ipos + i;
			}
			else
			{
				String lexeme = phrase.substring(ipos, ipos + i + 1);
				String lexentry, lemma, info;
				lexentry = lemma = info = null;
				RefObject<String> lemmaRef = new RefObject<String>(lemma);
				RefObject<String> lexentryRef = new RefObject<String>(lexentry);
				RefObject<String> infoRef = new RefObject<String>(info);

				boolean parseLexemeSymbol = Dic.parseLexemeSymbol(lexeme, lexentryRef, lemmaRef, infoRef);

				lemma = lemmaRef.argvalue;
				lexentry = lexentryRef.argvalue;
				info = infoRef.argvalue;
				if (!parseLexemeSymbol)
				{
					res = "*Invalid Lexeme: " + lexeme + "*";
				}
				else
				{
					if (getLexemes)
					{
						lexeme = "<" + lexentry + "," + lemma + "," + info + ">";
					}
					else
						lexeme = lexentry;
					if (res.equals(""))
						res = lexeme;
					else
						res += lexeme;
				}
				nextipos = ipos + i + 1;
			}
		}
		else if (phrase.charAt(ipos) != '$')
		{
			res = phrase.substring(ipos, ipos + 1);
			nextipos = ipos + 1;
		}
		else if (phrase.charAt(ipos + 1) == '(')
		{
			// a variable, e.g. $(N1 => get variable's name
			int i = ipos + 2;

			ArrayList<String> listOfVariablesToUpdate = new ArrayList<String>();
			int j;
			for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != ' '; j++)
				;
			String varname = phrase.substring(i, i + j);
			listOfVariablesToUpdate.add(varname);
			i += j;
			String seqofconstraints = "";

			// get variable's lexical constraint, e.g. <V+3+s>
			int reclevel = 1;

			while (i < phrase.length())
			{
				for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != '<' && phrase.charAt(i + j) != '$'; j++)
					;
				if (i + j >= phrase.length())
				{
					seqofconstraints += phrase.substring(i);
					i = phrase.length();
					break;
				}
				else if (j > 0)
				{
					seqofconstraints += phrase.substring(i, i + j);
					i += j;
					continue;
				}
				else if (phrase.charAt(i) == '<')
				{
					// get lexeme
					for (j = 1; i + j < phrase.length() && phrase.charAt(i + j) != '>'; j++)
					{
						if (phrase.charAt(i + j) == '<')
						{
							// embedded <
							for (j++; phrase.charAt(i + j) != '>'; j++)
								;
						}
						else if (phrase.charAt(i + j) == '"')
						{
							// embedded "
							for (j++; phrase.charAt(i + j) != '"'; j++)
								;
						}
					}
					j++;
					seqofconstraints += phrase.substring(i, i + j);
					i += j;
				}
				else if (phrase.charAt(i) == '$')
				{
					if (phrase.charAt(i + 1) == ')')
					{
						reclevel--;
						for (i += 2; i < phrase.length() && phrase.charAt(i) != ' '; i++)
							;
						if (reclevel == 0)
							break;
					}
					else if (phrase.charAt(i + 1) == '(')
					{
						// there is an embedded variable to update
						int kk = 0;
						for (; i + 2 + kk < phrase.length() && phrase.charAt(i + 1) != ' '; kk++)
							;
						String embeddedvarname = phrase.substring(i + 2, i + 2 + kk);
						listOfVariablesToUpdate.add(embeddedvarname);

						reclevel++;
						for (i += 2; i < phrase.length() && phrase.charAt(i) != ' '; i++)
							;
					}
				}
			}
			String[] grammarsymbols = Dic.parseSequenceOfSymbols(seqofconstraints);
			// Using labeled break statements to break out of block of code (if(true)),
			// to simulate C#'s goto statement
			NEXT1: if (true)
			{
				if (grammarsymbols == null || grammarsymbols.length == 0)
				{
					res = "*Invalid constraint " + seqofconstraints + "*";

					break NEXT1;
				}

				String seqoflexemes = hvariables.get(varname);
				String[] lexemes = null;
				if (seqoflexemes == null)
				{
					// variable has no value
					// if the grammar contains a sequence of lexemes, we take them
					// otherwise, if there is one lexical constraint, we stop

					boolean onlylexemes = true;
					for (int ilex = 0; ilex < grammarsymbols.length; ilex++)
					{
						if (!Dic.isALexemeSymbol(grammarsymbols[ilex]))
						{
							onlylexemes = false;
							break;
						}
					}
					if (onlylexemes)
					{
						lexemes = grammarsymbols;
						if (grammarsymbols.length == 1)
						{
							for (Object v : listOfVariablesToUpdate)
							{
								if (hvariables.get(v) == null)
									hvariables.put((String) v, grammarsymbols[0]);
							}
						}
					}
					else
					{
						res = "*Variable $" + varname + " is undefined*";
						break NEXT1;
					}
				}
				else
					lexemes = Dic.parseSequenceOfSymbols(seqoflexemes);
				if (lexemes == null || lexemes.length == 0)
				{
					res = "*Invalid lexeme " + seqoflexemes + "*";
					break NEXT1;
				}
				if (lexemes.length != grammarsymbols.length)
				{
					listofhvars = null;
					return null; // the lexemes in $Var cannot parse the grammar
					// symbols in $(Var ... $)
				}

				if (lexemes.length > 1) // the value of $Var is a sequence of
				// lexemes
				{
					// do not perform any morphological generation: just copy each
					// compatible lexeme
					for (int icon = 0; icon < lexemes.length; icon++)
					{
						String lexeme = lexemes[icon];
						if (lexeme == null || lexeme.equals(""))
						{
							res = "*Invalid lexeme*";
							break NEXT1;
						}
						String constraint = grammarsymbols[icon];
						if (constraint == null || constraint.equals(""))
						{
							res = "*Invalid constraint*";
							break NEXT1;
						}

						String lexentry, lexlemma, lexcategory;
						lexentry = lexlemma = lexcategory = null;
						String[] lexfeatures = null;
						Boolean neg = null; // information in the lexicon

						RefObject<String> lexcategoryRef = new RefObject<String>(lexcategory);
						RefObject<String> lexentryRef = new RefObject<String>(lexentry);
						RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
						RefObject<String[]> lexfeaturesRef = new RefObject<String[]>(lexfeatures);
						RefObject<Boolean> negRef = new RefObject<Boolean>(neg);

						Dic.parseSymbolFeatureArray(lexeme, lexentryRef, lexlemmaRef, lexcategoryRef, lexfeaturesRef,
								negRef);

						lexcategory = lexcategoryRef.argvalue;
						lexentry = lexentryRef.argvalue;
						lexlemma = lexlemmaRef.argvalue;
						lexfeatures = lexfeaturesRef.argvalue;
						neg = negRef.argvalue;
						if (grammarController.grammar.matchLexeme(lexentry, lexlemma, lexcategory, lexfeatures,
								constraint))
						{
							if (getLexemes)
							{
								if (res.equals(""))
									res = lexeme;
								else
									res += " " + lexeme;
							}
							else
							{
								if (res.equals(""))
									res = lexentry;
								else
									res += " " + lexentry;
							}
						}
						else
						// the lexeme is not compatible with the constraint:
						// => return null
						{
							listofhvars = null;
							return null;
						}
					}
				}
				else
				{
					// there is only one lexeme in $Var and one constraint in $(Var
					// ... $): get the lexeme and generate all its inflected/derived
					// forms that are compatible with the constraint
					int icon = 0;
					String lexeme = lexemes[icon];
					if (lexeme == null || lexeme.equals(""))
					{
						res = "*Invalid lexeme*";
						break NEXT1;
					}
					String constraint = grammarsymbols[icon];
					if (constraint == null || constraint.equals(""))
					{
						res = "*Invalid constraint*";
						break NEXT1;
					}

					// compute nextipos
					nextipos = i;

					String lexentry, lexlemma, lexcategory;
					lexentry = lexlemma = lexcategory = null;
					String[] lexfeatures = null;
					Boolean neg = null; // information in the lexicon

					RefObject<String> lexcategoryRef = new RefObject<String>(lexcategory);
					RefObject<String> lexentryRef = new RefObject<String>(lexentry);
					RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
					RefObject<String[]> lexfeaturesRef = new RefObject<String[]>(lexfeatures);
					RefObject<Boolean> negRef = new RefObject<Boolean>(neg);

					Dic.parseSymbolFeatureArray(lexeme, lexentryRef, lexlemmaRef, lexcategoryRef, lexfeaturesRef,
							negRef);

					lexcategory = lexcategoryRef.argvalue;
					lexentry = lexentryRef.argvalue;
					lexlemma = lexlemmaRef.argvalue;
					lexfeatures = lexfeaturesRef.argvalue;
					neg = negRef.argvalue;

					if (grammarController.grammar.matchLexeme(lexentry, lexlemma, lexcategory, lexfeatures, constraint))
					{
						if (getLexemes)
						{
							if (res.equals(""))
								res = lexeme;
							else
								res += " " + lexeme;
						}
						else
						{
							if (res.equals(""))
								res = lexentry;
							else
								res += " " + lexentry;
						}
					}
					else if (Dic.isALexemeSymbol(constraint))
					{
						// a lexeme in the grammar does not match the current lexeme
						// => failure
						res = "*No Match*";
						break NEXT1;
					}
					else
					{
						// need to perform morphological generation
						Language lan = grammarController.lan;
						if (engine == null || engine.Lan != lan)
						{
							boolean projectMode = true;
							boolean backgroundWorking = Launcher.backgroundWorking;
							BackgroundWorker backgroundWorker = Launcher.backgroundWorker;

							engine = new Engine(new RefObject<Language>(lan), Paths.applicationDir, Paths.docDir,
									Paths.projectDir, projectMode, Launcher.preferences, backgroundWorking,
									backgroundWorker);
							engine.preferences = Launcher.preferences;
							engine.docDir = Paths.docDir;
						}

						// inflect/derive lexeme to match constraint
						String[] forms = null;
						try
						{
							forms = engine.computeDerivations(lexeme, constraint.substring(1, constraint.length() - 1));
						}
						catch (ClassNotFoundException e1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_COMPUTING_DERIVATIONS, JOptionPane.ERROR_MESSAGE);
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
						}
						if (forms == null || forms.length == 0)
						{
							res = "*Cannot compute " + constraint + "*";
							break NEXT1;
						}

						// multiple solutions: need to recursively addup each of
						// them to their followups
						reslist = new ArrayList<String>();
						listofhvars.argvalue = new ArrayList<HashMap<String, String>>();

						nextipos = i;
						for (String aform : forms)
						{
							String alexentry, alemma, aninfo;
							alexentry = alemma = aninfo = null;
							RefObject<String> alexentryRef = new RefObject<String>(alexentry);
							RefObject<String> alemmaRef = new RefObject<String>(alemma);
							RefObject<String> aninfoRef = new RefObject<String>(aninfo);
							Dic.parseLexemeSymbol(aform, alexentryRef, alemmaRef, aninfoRef);
							alexentry = alexentryRef.argvalue;
							alemma = alemmaRef.argvalue;
							aninfo = aninfoRef.argvalue;

							// copy hvariables to currenthvariables and replace old
							// match with generated match
							HashMap<String, String> currenthvariables = new HashMap<String, String>();
							
							for (Object key1 : hvariables.keySet())
							{
								
								String key = (String) key1;
								boolean found = false;
								for (Object varToUpdate : listOfVariablesToUpdate)
								{
									if (key.equals(varToUpdate))
									{
										currenthvariables.put(key, aform);
										found = true;
										break;
									}
								}
								if (!found)
									currenthvariables.put(key, hvariables.get(key));
							}

							ArrayList<HashMap<String, String>> taillistofhvars = null;
							RefObject<ArrayList<HashMap<String, String>>> taillistofhvarsRef = new RefObject<ArrayList<HashMap<String, String>>>(
									taillistofhvars);

							follows = replaceVariablesWithValuesInInput(phrase, nextipos, currenthvariables,
									taillistofhvarsRef, getLexemes);
							taillistofhvars = taillistofhvarsRef.argvalue;
							if (follows == null)
								continue;

							StringBuilder newres;
							while (follows.iterator().hasNext())
							{
								String sbfollow = follows.iterator().next();
								if (getLexemes)
									newres = new StringBuilder(aform);
								else
									newres = new StringBuilder(alexentry);
								newres.append(sbfollow);
								reslist.add(newres.toString());
							}
							while (taillistofhvars.iterator().hasNext())
								listofhvars.argvalue.add(taillistofhvars.iterator().next());
						}
						return reslist;
					}
				}
			}
			nextipos = i;
		}
		else
		{
			// variable, e.g. $N1
			int i = ipos + 1;
			int j;
			for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != ' ' && phrase.charAt(i + j) != '#'
					&& phrase.charAt(i + j) != '=' && phrase.charAt(i + j) != '!'; j++)
				;
			String varname = phrase.substring(i, i + j);
			i += j;

			String seqoflexemes = hvariables.get(varname);
			if (seqoflexemes == null)
			{
				// variable does not have any value in hvariables
				// is the variable a compound variable, e.g. $Pred$Noun ?
				boolean compoundvariable = false;
				String varname2 = null;
				String fieldname = null;
				int index1 = varname.indexOf('_');
				int index2 = varname.indexOf('$', 1);
				int index = index1;
				if (index == -1)
					index = index2;
				else if (index2 == -1)
					index = index1;
				else if (index2 < index)
					index = index2;
				if (index == -1)
				{
					index = varname.indexOf('$');
					if (index == -1)
						varname2 = varname;
					else
					{
						varname2 = varname.substring(0, index);
						fieldname = varname.substring(index + 1);
					}
				}
				else
				{
					varname2 = varname.substring(0, index);
					fieldname = varname.substring(index + 1);
				}
				if (varname2 != varname)
					compoundvariable = true;

				if (!compoundvariable)
				{
					// if this is not a compound variable, then FAILURE to find
					// the variable's value
					res = "*Variable " + varname + " is undedined*";
				}
				else
				{
					// if this is a compound variable, then (a) get the
					// variable's lexeme value, (b) compute the field
					seqoflexemes = hvariables.get(varname2);
					if (seqoflexemes == null)
					{
						res = "*Variable " + varname + " is undedined*";
					}
					else if (fieldname != null)
					{
						String entry, lemma, info;
						entry = lemma = info = null;
						RefObject<String> entryRef = new RefObject<String>(entry);
						RefObject<String> lemmaRef = new RefObject<String>(lemma);
						RefObject<String> infoRef = new RefObject<String>(info);
						Dic.parseLexemeSymbol(seqoflexemes, entryRef, lemmaRef, infoRef);
						entry = entryRef.argvalue;
						lemma = lemmaRef.argvalue;
						info = infoRef.argvalue;
						res = Dic.getPropertyValue(fieldname, info);
						if (res == null || res.equals(""))
							res = "*Variable " + varname + " is undedined*";
						else if (res.charAt(0) == '<' && res.charAt(res.length() - 1) == '>')
						{
							// field value is a lexeme
							if (!getLexemes)
							{
								String entry2, lemma2, info2;
								entry2 = lemma2 = info2 = null;
								RefObject<String> entry2Ref = new RefObject<String>(entry2);
								RefObject<String> lemma2Ref = new RefObject<String>(lemma2);
								RefObject<String> info2Ref = new RefObject<String>(info2);
								if (Dic.parseLexemeSymbol(res, entry2Ref, lemma2Ref, info2Ref))
									entry2 = entry2Ref.argvalue;
								lemma2 = lemma2Ref.argvalue;
								info2 = info2Ref.argvalue;
								res = entry2;
							}
						}
					}
				}
			}
			else
			{
				if (getLexemes)
					res = seqoflexemes;
				else
				{
					res = null;
					// need to parse the sequence of lexeme to produce the
					// sequence of lexentries
					String[] lexs = Dic.parseSequenceOfSymbols(seqoflexemes);
					for (String lex : lexs)
					{
						String lexentry, lexlemma, lexinfo;
						lexentry = lexlemma = lexinfo = null;
						RefObject<String> lexentryRef = new RefObject<String>(lexentry);
						RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
						RefObject<String> lexinfoRef = new RefObject<String>(lexinfo);
						Dic.parseLexemeSymbol(lex, lexentryRef, lexlemmaRef, lexinfoRef);
						lexentry = lexentryRef.argvalue;
						lexlemma = lexlemmaRef.argvalue;
						lexinfo = lexinfoRef.argvalue;

						if (res == null)
							res = lexentry;
						else
							res += " " + lexentry;
					}
				}
			}
			nextipos = i;
		}

		// add res and each follow up
		follows = replaceVariablesWithValuesInInput(phrase, nextipos, hvariables, listofhvars, getLexemes);
		if (follows != null)
		{
			reslist = new ArrayList<String>();
			for (Object sbfollow : follows)
			{
				StringBuilder newres = new StringBuilder(res);
				newres.append((String) sbfollow);
				reslist.add(newres.toString());
			}
			return reslist;
		}
		else
			return null;
	}

	private ArrayList<String> replaceVariablesWithValuesInOutput(String phrase, int ipos,
			HashMap<String, String> hvariables, boolean getLexemes)
	{
		if (phrase == null || phrase.equals(""))
			return null;
		if (ipos >= phrase.length())
			return null;

		// compute res
		ArrayList<String> reslist = null;
		ArrayList<String> follows = null;
		String res = "";
		int nextipos = 0;
		if (phrase.charAt(ipos) == '\\')
		{
			res = phrase.substring(ipos, ipos + 2);
			nextipos = ipos + 2;
		}
		
		else if (phrase.charAt(ipos) != '$')
		{
			res = phrase.substring(ipos, ipos + 1);
			nextipos = ipos + 1;
		}
		else if (phrase.charAt(ipos + 1) == '(')
		{
			// a variable, e.g. $(N1 => get variable's name
			int i = ipos + 2;

			int j;
			for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != ' '; j++)
				;
			String varname = phrase.substring(i, i + j);
			i += j;
			String seqofconstraints = "";

			// get variable's lexical constraint, e.g. <V+3+s>
			int reclevel = 1;
			while (i < phrase.length())
			{
				for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != '<' && phrase.charAt(i + j) != '$'; j++)
					;
				if (i + j >= phrase.length())
				{
					seqofconstraints += phrase.substring(i);
					i = phrase.length();
					break;
				}
				else if (j > 0)
				{
					seqofconstraints += phrase.substring(i, i + j);
					i += j;
					continue;
				}
				else if (phrase.charAt(i) == '<')
				{
					// get lexeme
					for (j = 1; i + j < phrase.length() && phrase.charAt(i + j) != '>'; j++)
					{
						if (phrase.charAt(i + j) == '<')
						{
							// embedded <
							for (j++; phrase.charAt(i + j) != '>'; j++)
								;
						}
						else if (phrase.charAt(i + j) == '"')
						{
							// embedded "
							for (j++; phrase.charAt(i + j) != '"'; j++)
								;
						}
					}
					j++;
					seqofconstraints += phrase.substring(i, i + j);
					i += j;
				}
				else if (phrase.charAt(i) == '$')
				{
					if (phrase.charAt(i + 1) == ')')
					{
						reclevel--;
						for (i += 2; i < phrase.length() && phrase.charAt(i) != ' '; i++)
							;
						if (reclevel == 0)
							break;
					}
					else if (phrase.charAt(i + 1) == '(')
					{
						reclevel++;
						for (i += 2; i < phrase.length() && phrase.charAt(i) != ' '; i++)
							;
					}
				}
			}
			String[] constraints = Dic.parseSequenceOfSymbols(seqofconstraints);
			// Using labeled break statements to break out of block of code (if(true)),
			// to simulate C#'s goto statement
			NEXT2: if (true)
			{
				if (constraints == null || constraints.length == 0)
				{
					res = "*Invalid constraint " + seqofconstraints + "*";
					break NEXT2;
				}

				String seqoflexemes = hvariables.get(varname);
				if (seqoflexemes == null)
				{
					// variable has no value
					res = "*Variable $" + varname + " is undefined*";
					break NEXT2;
				}
				String[] lexemes = Dic.parseSequenceOfSymbols(seqoflexemes);
				if (lexemes == null || lexemes.length == 0)
				{
					res = "*Invalid lexeme " + seqoflexemes + "*";
					break NEXT2;
				}
				if (lexemes.length != constraints.length)
					return null;

				if (lexemes.length > 1)
				{
					// do not perform any morphological generation: just copy each
					// compatible lexeme
					for (int icon = 0; icon < lexemes.length; icon++)
					{
						String lexeme = lexemes[icon];
						if (lexeme == null || lexeme.equals(""))
						{
							res = "*Invalid lexeme*";
							break NEXT2;
						}
						String constraint = constraints[icon];
						if (constraint == null || constraint.equals(""))
						{
							res = "*Invalid constraint*";
							break NEXT2;
						}

						String lexentry, lexlemma, lexcategory;
						lexentry = lexlemma = lexcategory = null;
						String[] lexfeatures = null;
						Boolean neg = null; // information in the lexicon
						RefObject<String> lexentryRef = new RefObject<String>(lexentry);
						RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
						RefObject<String> lexcategoryRef = new RefObject<String>(lexcategory);
						RefObject<String[]> lexfeaturesRef = new RefObject<String[]>(lexfeatures);
						RefObject<Boolean> negRef = new RefObject<Boolean>(neg);

						Dic.parseSymbolFeatureArray(lexeme, lexentryRef, lexlemmaRef, lexcategoryRef, lexfeaturesRef,
								negRef);

						lexentry = lexentryRef.argvalue;
						lexlemma = lexlemmaRef.argvalue;
						lexcategory = lexcategoryRef.argvalue;
						lexfeatures = lexfeaturesRef.argvalue;
						neg = negRef.argvalue;

						if (grammarController.grammar.matchLexeme(lexentry, lexlemma, lexcategory, lexfeatures,
								constraint))
						{
							if (getLexemes)
							{
								if (res.equals(""))
									res = lexeme;
								else
									res += lexeme;
							}
							else
							{
								if (res.equals(""))
									res = lexentry;
								else
									res += " " + lexentry;
							}
						}
						else
							// the lexeme is not compatible with the constraint: =>
							// return null
							return null;
					}
				}
				else
				{
					// there is only one lexeme/constraint: get the lexeme if
					// compatible with constraint, otherwise generate it
					int icon = 0;
					String lexeme = lexemes[icon];
					if (lexeme == null || lexeme.equals(""))
					{
						res = "*Invalid lexeme*";
						break NEXT2;
					}
					String constraint = constraints[icon];
					if (constraint == null || constraint.equals(""))
					{
						res = "*Invalid constraint*";
						break NEXT2;
					}

					String lexentry, lexlemma, lexcategory;
					lexentry = lexlemma = lexcategory = null;
					String[] lexfeatures = null;
					Boolean neg = null; // information in the lexicon

					RefObject<String> lexentryRef = new RefObject<String>(lexentry);
					RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
					RefObject<String> lexcategoryRef = new RefObject<String>(lexcategory);
					RefObject<String[]> lexfeaturesRef = new RefObject<String[]>(lexfeatures);
					RefObject<Boolean> negRef = new RefObject<Boolean>(neg);

					Dic.parseSymbolFeatureArray(lexeme, lexentryRef, lexlemmaRef, lexcategoryRef, lexfeaturesRef,
							negRef);

					lexentry = lexentryRef.argvalue;
					lexlemma = lexlemmaRef.argvalue;
					lexcategory = lexcategoryRef.argvalue;
					lexfeatures = lexfeaturesRef.argvalue;
					neg = negRef.argvalue;

					if (grammarController.grammar.matchLexeme(lexentry, lexlemma, lexcategory, lexfeatures, constraint))
					{
						if (getLexemes)
						{
							if (res.equals(""))
								res = lexeme;
							else
								res += lexeme;
						}
						else
						{
							if (res.equals(""))
								res = lexentry;
							else
								res += " " + lexentry;
						}
					}
					else
					// need to perform morphological generation
					{
						Language lan = grammarController.lan;
						if (engine == null || engine.Lan != lan)
						{
							boolean projectMode = true;

							RefObject<Language> lanRef = new RefObject<Language>(lan);

							engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir,
									projectMode, Launcher.preferences, Launcher.backgroundWorking,
									Launcher.backgroundWorker);

							lan = lanRef.argvalue;
							engine.preferences = Launcher.preferences;
							engine.docDir = Paths.docDir;
						}
						// inflect/derive lexeme to match constraint
						String[] forms = null;
						try
						{
							forms = engine.computeDerivations(lexeme, constraint.substring(1, constraint.length() - 1));
						}
						catch (ClassNotFoundException e)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_COMPUTING_DERIVATIONS, JOptionPane.ERROR_MESSAGE);
						}
						catch (IOException e)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
						}
						if (forms == null || forms.length == 0)
						{
							res = "*Cannot derive lexeme " + lexeme + "*";
							break NEXT2;
						}
						// multiple solutions: need to recursively addup each of
						// them to their followups

						// compute nextipos
						nextipos = i + 1;

						reslist = new ArrayList<String>();
						follows = replaceVariablesWithValuesInOutput(phrase, nextipos, hvariables, getLexemes);
						for (String aform : forms)
						{
							StringBuilder newres;
							if (getLexemes)
								newres = new StringBuilder(aform);
							else
							{
								// String lexentry, lexlemma, lexcategory; String[]
								// lexfeatures; bool neg; // information in the
								// lexicon

								RefObject<String> lexentryRef2 = new RefObject<String>(lexentry);
								RefObject<String> lexlemmaRef2 = new RefObject<String>(lexlemma);
								RefObject<String> lexcategoryRef2 = new RefObject<String>(lexcategory);
								RefObject<String[]> lexfeaturesRef2 = new RefObject<String[]>(lexfeatures);
								RefObject<Boolean> negRef2 = new RefObject<Boolean>(neg);

								Dic.parseSymbolFeatureArray(aform, lexentryRef2, lexlemmaRef2, lexcategoryRef2,
										lexfeaturesRef2, negRef2);
								lexentry = lexentryRef2.argvalue;
								lexlemma = lexlemmaRef2.argvalue;
								lexcategory = lexcategoryRef2.argvalue;
								lexfeatures = lexfeaturesRef2.argvalue;
								neg = negRef2.argvalue;

								newres = new StringBuilder(lexentry);
							}

							if (follows == null)
							{
								if (getLexemes)
									reslist.add(aform);
								else
								{
									
									// information in the lexicon
									RefObject<String> lexentryRef3 = new RefObject<String>(lexentry);
									RefObject<String> lexlemmaRef3 = new RefObject<String>(lexlemma);
									RefObject<String> lexcategoryRef3 = new RefObject<String>(lexcategory);
									RefObject<String[]> lexfeaturesRef3 = new RefObject<String[]>(lexfeatures);
									RefObject<Boolean> negRef3 = new RefObject<Boolean>(neg);

									Dic.parseSymbolFeatureArray(lexeme, lexentryRef3, lexlemmaRef3, lexcategoryRef3,
											lexfeaturesRef3, negRef3);
									lexentry = lexentryRef3.argvalue;
									lexlemma = lexlemmaRef3.argvalue;
									lexcategory = lexcategoryRef3.argvalue;
									lexfeatures = lexfeaturesRef3.argvalue;
									neg = negRef3.argvalue;
									reslist.add(lexentry);
								}
							}
							else
							{
								for (Object sbfollow : follows)
								{
									newres.append((String) sbfollow);
									reslist.add(newres.toString());
								}
							}
						}
						return reslist;
					}
				}
			}
			nextipos = i;
		}
		else
		{
			// variable, e.g. $N1
			int i = ipos + 1;
			int j;
			for (j = 0; i + j < phrase.length() && phrase.charAt(i + j) != ' ' && phrase.charAt(i + j) != '#'
					&& phrase.charAt(i + j) != '=' && phrase.charAt(i + j) != '!'; j++)
				;
			String varname = phrase.substring(i, i + j);
			i += j;

			String seqoflexemes = hvariables.get(varname);
			if (seqoflexemes == null)
			{
				// variable does not have any value
				res = "";
			}
			else if (getLexemes)
				res = seqoflexemes;
			else
			{
				String[] lexemes = Dic.parseSequenceOfSymbols(seqoflexemes);
				if (lexemes[0] != null && lexemes[0] != "")
				{
					if (lexemes[0].charAt(0) == '<' && lexemes[0].charAt(lexemes[0].length() - 1) == '>')
					{
						String delafline = lexemes[0].substring(1, lexemes[0].length() - 1);
						String lexentry, lexlemma, lexinfo; // information in
						// the lexicon
						lexentry = lexlemma = lexinfo = null;
						RefObject<String> lexentryRef = new RefObject<String>(lexentry);
						RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
						RefObject<String> lexinfoRef = new RefObject<String>(lexinfo);
						boolean parseDelaf = Dic.parseDELAF(delafline, lexentryRef, lexlemmaRef, lexinfoRef);
						lexentry = lexentryRef.argvalue;
						lexlemma = lexlemmaRef.argvalue;
						lexinfo = lexinfoRef.argvalue;
						if (parseDelaf)
							res = lexentry;
						else
							res = lexemes[0];

					}
				}
				else
					res = "";
				for (int k = 1; k < lexemes.length; k++)
				{
					if (lexemes[k] != null && lexemes[k] != "")
					{
						if (lexemes[k].charAt(0) == '<' && lexemes[k].charAt(lexemes[k].length() - 1) == '>')
						{
							String delafline = lexemes[k].substring(1, lexemes[k].length() - 1);
							String lexentry, lexlemma, lexinfo; // information
							// in the
							// lexicon
							lexentry = lexlemma = lexinfo = null;
							RefObject<String> lexentryRef = new RefObject<String>(lexentry);
							RefObject<String> lexlemmaRef = new RefObject<String>(lexlemma);
							RefObject<String> lexinfoRef = new RefObject<String>(lexinfo);
							boolean parseDelaf = Dic.parseDELAF(delafline, lexentryRef, lexlemmaRef, lexinfoRef);
							lexentry = lexentryRef.argvalue;
							lexlemma = lexlemmaRef.argvalue;
							lexinfo = lexinfoRef.argvalue;
							if (parseDelaf)
								res += " " + lexentry;
							else
								res += " " + lexemes[k];

						}
					}
					else
						res = "";
				}
			}
			nextipos = i;
		}

		// add res and each follow up
		reslist = new ArrayList<String>();
		follows = replaceVariablesWithValuesInOutput(phrase, nextipos, hvariables, getLexemes);
		if (follows == null)
		{
			reslist.add(res);
		}
		else
			for (Object sbfollow : follows)
			{
				StringBuilder newres = new StringBuilder(res);
				newres.append((String) sbfollow);
				reslist.add(newres.toString());
			}
		return reslist;
	}

	boolean areFeaturesCompatible(String grammaroutput, String[] constraints)
	{
		for (String constraint : constraints)
		{
			boolean plus = (constraint.charAt(0) == '+');
			boolean found = (grammaroutput.indexOf(constraint.substring(1), 0) != -1);
			if (found && plus)
			{
				continue; // found the +constraint
			}
			else if (!found && !plus)
				continue; // -constraint was not found
			else
				return false;
		}
		return true;
	}

	public void produceParaphrases()
	{
		Grammar grammar = grammarController.grammar;

		if (grammar == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_GRAMMAR_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String[] filters = null;

		if (dialog.getRdbtnNameATransformation().isSelected())
		{
			filters = Dic.splitAllFeaturesWithPlusOrMinus(dialog.getTxtpassiveneg().getText());

			if (filters == null || filters.length == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.GRAMMAR_INVALID_LIST_OF_TRANSFORMED_PHRASES, Constants.NOOJ_APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		Language lan = grammarController.lan;

		RefObject<Language> refLan = new RefObject<Language>(lan);
		Engine engine = new Engine(refLan, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);

		lan = refLan.argvalue;

		String errorMessage = "";
		RefObject<String> refError = new RefObject<String>(errorMessage);

		try
		{
			if (!engine.loadResources(Launcher.preferences.ldic.get(lan.isoName),
					Launcher.preferences.lsyn.get(lan.isoName), true, refError))
			{
				errorMessage = refError.argvalue;

				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
						Constants.GRAMMAR_CANNOT_LOAD_LINGUISTIC_RESOURCE, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		errorMessage = grammar.compileAndComputeFirst(engine);

		if (errorMessage != null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.GRAMMAR_COMPILATION_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String line = dialog.getTextField().getText();
		line = line.trim();

		if (line.length() == 0)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.GRAMMAR_CANNOT_READ_PHRASE_TO_PARSE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (grammar.gramType == GramType.SYNTAX)
		{
			// create a Ntext with file and directory...
			Ntext myText = new Ntext(lan.isoName);
			myText.buffer = line;
			myText.mft = engine.delimit(myText);

			// linguistic analysis using both lexical and syntactic resources
			myText.annotations = new ArrayList<Object>();

			RefObject<String> refErrorMessage = new RefObject<String>(errorMessage);

			if (!engine.tokenize(null, myText, myText.annotations, new HashMap<String, ArrayList<String>>(),
					refErrorMessage))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), refErrorMessage.argvalue,
						Constants.NOOJ_TOKENIZER_ERROR, JOptionPane.ERROR_MESSAGE);

				myText.annotations = null;
				myText.hLexemes = null;
				myText.hUnknowns = null;
				return;
			}

			myText.hPhrases = new HashMap<String, Integer>();

			// first apply grammars in dictionary/grammar pairs
			if (engine.synGrms != null && engine.synGrms.size() > 0)
			{
				RefObject<String> refErrorMessage2 = new RefObject<String>(errorMessage);
				boolean applyRes = false;

				try
				{
					applyRes = engine.applyAllGrammars(null, myText, myText.annotations, 0, refErrorMessage2);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!applyRes && !refErrorMessage2.argvalue.equals(""))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), refErrorMessage.argvalue,
							Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!applyRes)
				{
					myText.hPhrases = null;
					return;
				}

				myText.cleanupBadAnnotations(myText.annotations);
			}

			// then apply syntactic grammars
			if (engine.synGrms != null && engine.synGrms.size() > 0)
			{
				RefObject<String> refErrorMessage2 = new RefObject<String>(errorMessage);
				boolean applyRes = false;

				try
				{
					applyRes = engine.applyAllGrammars(null, myText, myText.annotations, 1, refErrorMessage2);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!applyRes && !refErrorMessage2.argvalue.equals(""))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), refErrorMessage.argvalue,
							Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!applyRes)
				{
					myText.hPhrases = null;
					return;
				}
			}

			// apply grammar in parsing mode
			double cPos;

			Gram grm = grammar.grams.get("Main");

			if (grm == null || grm.states == null || grm.states.size() < 2)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + grammar.fullName
						+ " has no Main graph!", Constants.GRAMMAR_NO_MAIN_GRAPH, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// apply the grammar and store the result
			ArrayList<Double> solLengths = null;
			ArrayList<ArrayList<Double>> solInputs = null;
			ArrayList<ArrayList<String>> solOutputs = null;
			ArrayList<ArrayList<Object>> solNodes = null;
			ArrayList<ArrayList<String>> solVariables = null;
			RefObject<ArrayList<Double>> tempRef_sollengths = new RefObject<ArrayList<Double>>(solLengths);
			RefObject<ArrayList<ArrayList<Double>>> tempRef_solinputs = new RefObject<ArrayList<ArrayList<Double>>>(
					solInputs);
			RefObject<ArrayList<ArrayList<String>>> tempRef_solvariables = new RefObject<ArrayList<ArrayList<String>>>(
					solVariables);
			RefObject<ArrayList<ArrayList<String>>> tempRef_soloutputs = new RefObject<ArrayList<ArrayList<String>>>(
					solOutputs);
			RefObject<ArrayList<ArrayList<Object>>> tempRef_solnodes = new RefObject<ArrayList<ArrayList<Object>>>(
					solNodes);
			ArrayList<String> recursiveCalls = new ArrayList<String>();

			int da = grammar.syntaxMatch(grammar.fullName, -1, line, 0.0, 1, myText.mft, myText.annotations, grm,
					tempRef_sollengths, tempRef_solinputs, tempRef_solvariables, tempRef_soloutputs, tempRef_solnodes,
					MatchType.ALL, true, false, recursiveCalls);

			solLengths = tempRef_sollengths.argvalue;
			solInputs = tempRef_solinputs.argvalue;
			solVariables = tempRef_solvariables.argvalue;
			solOutputs = tempRef_soloutputs.argvalue;

			if (da == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.GRAMMAR_SEQUENCE_DOES_NOT_MATCH_GRAMMAR, Constants.NOOJ_APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (da > 0)
			{
				for (int iSol = 0; iSol < solLengths.size();)
				{
					int len = (solLengths.get(iSol)).intValue();

					if (len < line.length())
					{
						solLengths.remove(iSol);
						solInputs.remove(iSol);
						solOutputs.remove(iSol);
						continue;
					}
					else
					{
						if (Dic.isThereALexicalConstraint(solOutputs.get(iSol)))
						{
							cPos = 0.0;
							ArrayList<ArrayList<String>> resOutputs = null;
							ArrayList<HashMap<String, String>> resVariables = new ArrayList<HashMap<String, String>>();
							resVariables.add(new HashMap<String, String>());

							RefObject<ArrayList<ArrayList<String>>> resOutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
									resOutputs);
							RefObject<ArrayList<HashMap<String, String>>> resVariablesRef = new RefObject<ArrayList<HashMap<String, String>>>(
									resVariables);
							RefObject<String> refErrorMessage2 = new RefObject<String>(errorMessage);
							boolean check = false;

							try
							{
								check = engine.newProcessConstraints(line, myText.mft, myText.annotations, 1, grammar,
										solInputs.get(iSol), len, solVariables.get(iSol), cPos, solOutputs.get(iSol),
										resOutputsRef, resVariablesRef, 1, refErrorMessage2);
							}
							catch (IOException e)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
										Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
								return;
							}
							catch (ClassNotFoundException e)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
										Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
								return;
							}

							if (!check)
							{
								solLengths.remove(iSol);
								solInputs.remove(iSol);
								solOutputs.remove(iSol);
								solVariables.remove(iSol);
								continue;
							}
						}
					}

					iSol++;
				}

				da = solLengths.size();
			}

			if (da == 0)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
						Constants.GRAMMAR_SEQUENCE_DOES_NOT_MATCH_GRAMMAR_CONSTRAINTS, Constants.NOOJ_APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
			{
				// now we generate all the paraphrases
				boolean excludeErrors = !dialog.getChckbxDisplayConstraintFailures().isSelected();

				String[] results = grm.generateParaphrases(0, grammar.grams, 1000000, new Date(Long.MAX_VALUE),
						GramType.SYNTAX, lan, true);

				if (results == null)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.GRAMMAR_CANNOT_GENERATE_LANGUAGE, Constants.NOOJ_APPLICATION_NAME,
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Now process variables for each solution in results
				ArrayList<String> res = new ArrayList<String>();

				for (ArrayList<String> soltrace : solVariables)
				{
					int iFollow = 0;
					HashMap<String, String> hVars0 = new HashMap<String, String>(), hVars = new HashMap<String, String>();
					RefObject<Integer> iFollowRef = new RefObject<Integer>(iFollow);
					RefObject<HashMap<String, String>> hVars0Ref = new RefObject<HashMap<String, String>>(hVars0);
					storeVariablesInHash(soltrace, 0, true, iFollowRef, hVars0Ref);
					iFollow = iFollowRef.argvalue;
					hVars0 = hVars0Ref.argvalue;

					for (int iRes = 0; iRes < results.length; iRes += 2)
					{
						String result = results[iRes];
						String output = results[iRes + 1];
						ArrayList<HashMap<String, String>> listOfHVars = new ArrayList<HashMap<String, String>>();
						hVars = new HashMap<String, String>(hVars0);
						listOfHVars.add(hVars);

						RefObject<ArrayList<HashMap<String, String>>> tempRef_listOfHVars = new RefObject<ArrayList<HashMap<String, String>>>(
								listOfHVars);

						// replace in result all variables with the ones set in soltrace
						ArrayList<String> newResults = replaceVariablesWithValuesInInput(result, 0, hVars,
								tempRef_listOfHVars, dialog.getChckbxDisplayLexemes().isSelected());
						if (newResults == null)
							continue;

						listOfHVars = tempRef_listOfHVars.argvalue;
						ArrayList<String> listOfNewOutputs = new ArrayList<String>();
						RefObject<ArrayList<String>> tempRef_listOfNewOutputs = new RefObject<ArrayList<String>>(
								listOfNewOutputs);

						for (int iNewRes = 0; iNewRes < newResults.size(); iNewRes++)
						{
							String rResults = newResults.get(iNewRes);
							HashMap<String, String> rhvars = listOfHVars.get(iNewRes);
							RefObject<ArrayList<ArrayList<String>>> tempRef_resoutputs;
							String errmessage = "";
							RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
							try
							{
								if (engine.newProcessConstraintsInStringForTransformed(output, rhvars, 0, grammar,
										tempRef_listOfNewOutputs, tempRef_errmessage))
								{
									listOfNewOutputs = tempRef_listOfNewOutputs.argvalue;
									for (String newOutput : listOfNewOutputs)
									{
										ArrayList<String> newOutputs = replaceVariablesWithValuesInOutput(newOutput, 0,
												rhvars, dialog.getChckbxDisplayLexemes().isSelected());

										if (newOutputs == null)
										{
											res.add(rResults);
											res.add("");
										}
										else
										{
											for (String newOutpt : newOutputs)
											{
												if (!excludeErrors || rResults.indexOf("*") == -1)
												{
													if (filters != null && !areFeaturesCompatible(newOutpt, filters))
														continue;

													res.add(rResults);
													res.add(newOutpt);
												}
											}
										}
									}
								}
								else if (dialog.getChckbxDisplayConstraintFailures().isSelected())
								{
									if (rResults.indexOf("*") != -1)
									{
										// there is already a failure in the input, no need to display erroneous output
										res.add("# " + rResults);
										res.add("");
									}
									else
									{
										// add output error at the beginning
										res.add("# " + errorMessage + " in: " + rResults);
										res.add("");
									}
								}
							}
							catch (ClassNotFoundException e)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
										Constants.GRAMMAR_CANNOT_GENERATE_LANGUAGE + e.getMessage(),
										Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
								return;
							}
							catch (IOException e)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
										Constants.GRAMMAR_CANNOT_GENERATE_LANGUAGE + e.getMessage(),
										Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
					}
				}

				// build a dictionary
				DictionaryEditorShell editor = new DictionaryEditorShell();
				editor.getController().initLoad(grammarController.grammar.iLanguage);

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < res.size(); i += 2)
				{
					String result = res.get(i);
					result = result.trim();

					for (int ir = 0; ir < result.length(); ir++)
					{
						char character = result.charAt(ir);

						if (character == ',' || character == '"')
							sb.append("\\");

						else if (Character.isWhitespace(character))
						{
							if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
								continue; // skip multiple spaces in output

							sb.append(" ");
							continue;
						}

						sb.append(character);
					}

					if (result.charAt(0) == '#')
					{
						sb.append("\n");
						continue;
					}

					String analysis = res.get(i + 1);

					if (analysis == null || analysis.equals(""))
						sb.append(",NOANALYSIS\n");
					else
					{
						sb.append(",");

						if (isASingleAnnotation(analysis))
						{
							String info = analysis.substring(1, analysis.length() - 1);
							sb.append(info);
						}
						else
							sb.append(analysis);

						sb.append("\n");
					}
				}

				String paneText = editor.getTextPane().getText();
				paneText += "#\n";
				paneText += "# Dictionary generated automatically\n";
				paneText += "#\n";
				paneText += sb.toString();
				editor.getTextPane().setText(paneText);

				editor.getController().sortDictionary(true);
				Launcher.getDesktopPane().add(editor);
				editor.setVisible(true);
			}
		}
	}
}