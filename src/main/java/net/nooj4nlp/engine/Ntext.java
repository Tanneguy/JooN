/*
 * This file is part of Nooj. Copyright (C) 2012 Silberztein Max
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.nooj4nlp.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * @author Silberztein Max
 */
public class Ntext implements Serializable
{
	private static final long serialVersionUID = 4556178507526248243L;

	public String LanguageName; // Name of the language
	public transient Language Lan; // Language
	public String DelimPattern; // String used as a pattern for delimiting
	public String[] XmlNodes; // Array of XML tags

	public transient Charlist charlist = null;
	public transient HashMap<String, Indexkey> hTokens = null; // Tokens
	public transient HashMap<String, ArrayList<Object>> hAmbiguities = null; // Ambiguities
	public transient HashMap<String, ArrayList<Object>> hUnambiguities = null; // Unambiguities
	public transient HashMap<String, Indexkey> hDigrams = null; // Digrams
	public transient HashMap<String, Integer> hLexemes = null; // Lexemes
	public transient HashMap<String, Integer> hUnknowns = null; // Uknowns
	public transient HashMap<String, Integer> hPhrases = null; // Phrases

	public transient ArrayList<String> listOfResources = null; // List of resources

	public String buffer;
	public Mft mft;
	public ArrayList<Object> annotations; // List of annotations

	public int nbOfTextUnits; // Number of text units in corpus
	public int nbOfChars;// Number of characters in corpus
	public int nbOfDiffChars;// Number of different characters in corpus
	public int nbOfLetters;// Number of letters in corpus
	public int nbOfDiffLetters;// Number of different letters in corpus
	public int nbOfDelimiters;// Number of delimiters in corpus
	public int nbOfDiffDelimiters;// Number of different delimiters in corpus
	public int nbOfBlanks;// Number of blanks in corpus
	public int nbOfDiffBlanks;// Number of different blanks in corpus
	public int nbOfDigits;// Number of digits in corpus
	public int nbOfDiffDigits;// Number of different digits in corpus
	public int nbOfTokens;// Number of tokens in corpus
	public int nbOfDiffTokens;// Number of different tokens in corpus
	public int nbOfWords;// Number of words in corpus
	public int nbOfDiffWords;// Number of different words in corpus

	/**
	 * Constructor - creates new Ntext object: initializes language based on given language name. If language is
	 * initialized properly, it initializes delimitPattern and xmlNodes also, as well as charlist, buffer, mft,
	 * annotations and numbers of tokens, blanks...
	 * 
	 * @param languageName
	 *            - language name
	 * @param delimPattern
	 *            - string used as a pattern for delimiting
	 * @param xmlNodes
	 *            - array of XML tags
	 */
	public Ntext(String languageName, String delimPattern, String[] xmlNodes)
	{
		ParameterCheck.mandatoryString("languageName", languageName);

		// text is not already processed => all parameters come from dialogOpenText

		this.LanguageName = languageName;
		Lan = new Language(languageName);

		if (Lan != null)
		{
			this.DelimPattern = delimPattern;
			this.XmlNodes = xmlNodes;

			charlist = null;
			buffer = null;
			mft = null;
			annotations = null;
			nbOfTextUnits = nbOfChars = nbOfDiffChars = nbOfLetters = nbOfDiffLetters = nbOfDelimiters = nbOfDiffDelimiters = nbOfBlanks = nbOfDiffBlanks = nbOfDigits = nbOfDiffDigits = nbOfTokens = nbOfDiffTokens = nbOfWords = nbOfDiffWords = -1;
		}
		else
		{
			System.out.println("Cannot construct language for languagename = " + languageName);
		}
	}

	/**
	 * Constructor - creates new Ntext object based on given corpus.
	 * 
	 * @param corpus
	 *            - corpus
	 */
	public Ntext(Corpus corpus)
	{
		ParameterCheck.mandatory("corpus", corpus);

		// text is not already processed => all parameters come from corpus

		LanguageName = corpus.languageName;
		Lan = corpus.lan;
		DelimPattern = corpus.delimPattern;
		XmlNodes = corpus.xmlNodes;

		charlist = null;
		buffer = null;
		mft = null;
		annotations = null;
		nbOfTextUnits = nbOfChars = nbOfDiffChars = nbOfLetters = nbOfDiffLetters = nbOfDelimiters = nbOfDiffDelimiters = nbOfBlanks = nbOfDiffBlanks = nbOfDigits = nbOfDiffDigits = nbOfTokens = nbOfDiffTokens = nbOfWords = nbOfDiffWords = -1;
	}

	/**
	 * Constructor - creates new Ntext object with language based on given language
	 * 
	 * @param languageName
	 */
	public Ntext(String languageName)
	{
		ParameterCheck.mandatoryString("languageName", languageName);

		this.LanguageName = languageName;
		Lan = new Language(languageName);

		if (Lan != null)
		{
			DelimPattern = null;
			XmlNodes = null;

			charlist = null;
			buffer = null;
			mft = null;
			annotations = null;
			nbOfTextUnits = nbOfChars = nbOfDiffChars = nbOfLetters = nbOfDiffLetters = nbOfDelimiters = nbOfDiffDelimiters = nbOfBlanks = nbOfDiffBlanks = nbOfDigits = nbOfDiffDigits = nbOfTokens = nbOfDiffTokens = nbOfWords = nbOfDiffWords = -1;
		}
		else
		{

		}
	}

	/**
	 * 
	 * @param corpus
	 * @param engine
	 * @param xmlNodes
	 * @param annotations
	 * @param hLexemes
	 * @param hPhrases
	 * @return
	 */
	public final String delimitXmlTextUnitsAndImportXmlTags(Corpus corpus, Engine engine, String[] xmlNodes,
			ArrayList<Object> annotations, HashMap<String, Integer> hLexemes, HashMap<String, Integer> hPhrases)
	{
		ParameterCheck.mandatory("engine", engine);
		ParameterCheck.mandatory("xmlNodes", xmlNodes);

		// annotations, hLexemes and hPhrases must be initialized beforehand
		String errMessage = "";

		RefObject<String> tempRef_errmessage = new RefObject<String>(errMessage);
		this.mft = engine.delimitXml(this, xmlNodes, tempRef_errmessage);
		errMessage = tempRef_errmessage.argvalue;

		if (this.mft == null)
		{
			return errMessage;
		}

		engine.addAllXmlAnnotations(corpus, this, annotations);
		return "";
	}

	/**
	 * 
	 * @param engine
	 * @return
	 */
	public final String delimitTextUnits(Engine engine)
	{
		ParameterCheck.mandatory("engine", engine);

		this.mft = engine.delimit(this);

		if (this.mft == null)
		{
			return "Cannot split text into text units: one text unit is larger than 65K characters";
		}

		return "";
	}

	/**
	 * 
	 * @param annotations
	 */
	public final void cleanupBadAnnotations(ArrayList<Object> annotations)
	{
		ParameterCheck.mandatoryCollection("annotations", annotations);

		// remove "NW" and "+FXC" from annotations
		HashMap<Integer, Object> foundANullAnnotation = new HashMap<Integer, Object>();

		for (int i = 0; i < annotations.size(); i++)
		{
			String lexeme = (String) annotations.get(i);

			if (lexeme == null)
			{
				foundANullAnnotation.put(i, null);
				continue;
			}

			String entry = null, lemma = null, category = null, features = null;

			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_features = new RefObject<String>(features);

			boolean tempVar = !Dic.parseDELAF(lexeme, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			features = tempRef_features.argvalue;

			if (tempVar)
			{
				continue;
			}

			if (category.equals("NW")
					|| ((features != null && !features.equals("")) && (Dic.lookFor("FXC", features) != null)))
			{
				annotations.set(i, null);

				foundANullAnnotation.put(i, null);
			}
		}

		if (foundANullAnnotation.size() > 0)
		{
			// clean up annotations and renumber all annotations' indices
			for (int itu = 1; itu < this.mft.tuAddresses.length; itu++)
			{
				ArrayList<TransitionObject> transitions = this.mft.aTransitions.get(itu);

				for (int istate = 0; istate < transitions.size(); istate++)
				{
					ArrayList<TransitionPair> state = transitions.get(istate).getOutgoings();

					for (int k = 0; k < state.size();)
					{
						int tokenId = state.get(k).getTokenId();

						if (foundANullAnnotation.containsKey(tokenId))
						{
							state.subList(k, k + 1).clear();
						}
						else
						{
							k++;
						}
					}
				}
			}
		}

		// clean up widow morphological annotations, i.e. annotations that start in the middle of a word form, but have
		// no parent
		for (int itu = 1; itu < this.mft.tuAddresses.length; itu++)
		{
			ArrayList<TransitionObject> transitions = this.mft.aTransitions.get(itu);
			HashMap<Double, Object> endings = new HashMap<Double, Object>();

			// first build the list of all endings that are floats
			for (int istate = 0; istate < transitions.size(); istate++)
			{
				ArrayList<TransitionPair> outgoings = transitions.get(istate).getOutgoings();

				for (int k = 0; k < outgoings.size(); k++)
				{
					double ending = outgoings.get(k).getRelEndAddress();

					if (!endings.containsKey(ending))
					{
						endings.put(ending, null);
					}
				}
			}

			// then remove all outgoing transitions that start at an address that is not an ending
			for (int istate = 0; istate < transitions.size();)
			{
				double beginning = transitions.get(istate).getRelBegAddress();
				int h_beg = (int) (100 * beginning);
				int h_beg2 = (int) beginning * 100;

				if (h_beg != h_beg2)
				{
					// this is inside of a word form
					if (!endings.containsKey(beginning))
					{
						// all the outgoing transitions from current state must be removed
						transitions.subList(istate, istate + 1).clear();
					}
					else
					{
						istate++;
					}
				}
				else
				{
					istate++;
				}
			}
		}
	}

	/**
	 * Helper method which removes members of given array lists lexs and lens with values bigger than given length.
	 * 
	 * @param lexs
	 *            - list from which members are removed
	 * @param lens
	 *            - list from which members are removed
	 * @param length
	 *            - limit for object removal
	 */
	private void keepOnlyInternal(RefObject<ArrayList<String>> lexs, RefObject<ArrayList<Double>> lens, int length)
	{
		ParameterCheck.mandatory("lexs", lexs);
		ParameterCheck.mandatory("lens", lens);

		for (int i = 0; i < lexs.argvalue.size();)
		{
			double la = lens.argvalue.get(i);

			if (la > length)
			{
				lexs.argvalue.remove(i);
				lens.argvalue.remove(i);
			}
			else
			{
				i++;
			}
		}
	}

	/**
	 * Helper method which removes members of given array lists lexs and lens with values bigger than or equal to given
	 * length.
	 * 
	 * @param lexs
	 *            - list from which members are removed
	 * @param lens
	 *            - list from which members are removed
	 * @param length
	 *            - limit for object removal
	 */
	private void keepOnlyInside(RefObject<ArrayList<String>> lexs, RefObject<ArrayList<Double>> lens, int length)
	{
		ParameterCheck.mandatory("lexs", lexs);
		ParameterCheck.mandatory("lens", lens);

		for (int i = 0; i < lexs.argvalue.size();)
		{
			double la = lens.argvalue.get(i);

			if (la >= length)
			{
				lexs.argvalue.remove(i);
				lens.argvalue.remove(i);
			}
			else
			{
				i++;
			}
		}
	}

	/**
	 * Helper method which finds largest value in lens array list and removes members of lens and lexs which are smaller
	 * than that value.
	 * 
	 * @param lexs
	 *            - list from which members are removed
	 * @param lens
	 *            - list from which members are removed
	 */
	private void keepOnlyLongest(RefObject<ArrayList<String>> lexs, RefObject<ArrayList<Double>> lens)
	{
		ParameterCheck.mandatory("lexs", lexs);
		ParameterCheck.mandatory("lens", lens);

		// compute maximum length
		double maxlen = 0;

		for (int i = 0; i < lexs.argvalue.size(); i++)
		{
			double li = lens.argvalue.get(i);
			if (li > maxlen)
			{
				maxlen = li;
			}
		}

		for (int i = 0; i < lexs.argvalue.size();)
		{
			double la = lens.argvalue.get(i);
			if (la < maxlen)
			{
				lexs.argvalue.remove(i);
				lens.argvalue.remove(i);
			}
			else
			{
				i++;
			}
		}
	}

	/**
	 * Cleans up opening and closing tags and adds quote marks.
	 * 
	 * @param feature
	 *            - string to be cleaned
	 * @return cleaned string
	 */
	private String cleanupAndAddQuotes(String feature)
	{
		ParameterCheck.mandatoryString("feature", feature);

		// returns empty string
		if (feature.equals(""))
		{
			return ("\"\"");
		}

		StringBuilder sb = new StringBuilder();

		// add '"' at the beginning
		if (feature.charAt(0) != '"' || feature.charAt(feature.length() - 1) != '"')
		{
			sb.append('"');
		}

		for (int i = 0; i < feature.length(); i++)
		{
			if (feature.charAt(i) != '<' && feature.charAt(i) != '>')
			{
				sb.append(feature.charAt(i));
			}
		}

		// add '"' at the end
		if (feature.charAt(0) != '"' || feature.charAt(feature.length() - 1) != '"')
		{
			sb.append('"');
		}

		return sb.toString();
	}

/**
	 * Cleans up opening and closing XML tags ('<' and '>').
	 * 
	 * @param line - string to be cleaned
	 * @return cleaned string
	 */
	private String cleanupXmlTags(String line)
	{
		ParameterCheck.mandatoryString("line", line);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < line.length(); i++)
		{
			if (line.charAt(i) != '<')
			{
				sb.append(line.charAt(i));
				continue;
			}
			for (i++; line.charAt(i) != '>'; i++)
			{
				;
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param currentLine
	 *            - line to be analyzed
	 * @param beg
	 * @param filterOut
	 *            - flag for printing
	 * @param pw
	 *            - object that prints into a text-output stream
	 * @param itu
	 * @param annotations
	 * @param xmlAnnotations
	 * @param lan
	 *            - language of the text
	 * @param insideOnly
	 *            - flag whether
	 */
	public final void buildXmlTaggedText(String currentLine, int beg, boolean filterOut, PrintWriter pw, int itu,
			ArrayList<Object> annotations, String[] xmlAnnotations, Language lan, boolean insideOnly)
	{
		ParameterCheck.mandatory("pw", pw);
		ParameterCheck.mandatoryCollection("annotations", annotations);
		ParameterCheck.mandatory("xmlAnnotations", xmlAnnotations);
		ParameterCheck.mandatory("lan", lan);

		int cpos;
		String token;

		for (int iChar = 0; iChar < currentLine.length();)
		{
			// get rid of white spaces
			if (Character.isWhitespace(currentLine.charAt(iChar)))
			{
				if (!filterOut)
				{
					pw.print(currentLine.charAt(iChar));
				}

				iChar++;
				continue;
			}

			cpos = iChar; // starting position of the token in the text unit

			if (Language.isLetter(currentLine.charAt(cpos))) // word form
			{
				for (iChar++; iChar < currentLine.length() && Language.isLetter(currentLine.charAt(iChar)); iChar++)
				{
					;
				}

				token = currentLine.substring(cpos, iChar);
			}
			else if (this.XmlNodes != null && currentLine.charAt(cpos) == '<') // get rid of xml tags
			{
				int level = 1;

				for (iChar++; iChar < currentLine.length(); iChar++)
				{
					if (currentLine.charAt(iChar) == '>')
					{
						level--;

						if (level == 0)
						{
							break;
						}
					}
					else if (currentLine.charAt(iChar) == '<')
					{
						level++;
					}
				}

				if (iChar < currentLine.length())
				{
					iChar++;
					continue;
					
				}
				else
				{
					// non closing '<' are processed as delimiters

					iChar = cpos + 1;
					token = currentLine.substring(cpos, cpos + 1);
				}
			}
			else
			{
				// delimiter

				iChar++;
				token = currentLine.substring(cpos, cpos + 1);
			}

			ArrayList<Integer> lexIds = null;
			ArrayList<Double> lens = null;
			ArrayList<String> rlexs = null;
			ArrayList<Double> rlens = null;

			RefObject<ArrayList<Integer>> tempRef_lexids = new RefObject<ArrayList<Integer>>(lexIds);
			RefObject<ArrayList<Double>> tempRef_lens = new RefObject<ArrayList<Double>>(lens);

			int da = this.mft.getAllLexIdsAndContracted(itu, beg + cpos, tempRef_lexids, tempRef_lens);
			lexIds = tempRef_lexids.argvalue;
			lens = tempRef_lens.argvalue;

			if (da > 0)
			{
				ArrayList<String> lexs = new ArrayList<String>();

				for (int i = 0; i < da; i++)
				{
					int tkId = lexIds.get(i);
					String lex = (String) annotations.get(tkId);

					if (lex == null)
					{
						continue;
					}

					lexs.add(lex);
				}

				RefObject<ArrayList<String>> tempRef_lexs = new RefObject<ArrayList<String>>(lexs);
				RefObject<ArrayList<Double>> tempRef_lens2 = new RefObject<ArrayList<Double>>(lens);

				keepOnlyInternal(tempRef_lexs, tempRef_lens2, currentLine.length() - cpos);

				lexs = tempRef_lexs.argvalue;
				lens = tempRef_lens2.argvalue;

				RefObject<ArrayList<String>> tempRef_lexs2 = new RefObject<ArrayList<String>>(lexs);
				RefObject<ArrayList<Double>> tempRef_lens3 = new RefObject<ArrayList<Double>>(lens);

				if (insideOnly && cpos == 0)
				{
					keepOnlyInside(tempRef_lexs2, tempRef_lens3, currentLine.length() - cpos);
				}
				lexs = tempRef_lexs2.argvalue;
				lens = tempRef_lens3.argvalue;

				RefObject<ArrayList<String>> tempRef_lexs3 = new RefObject<ArrayList<String>>(lexs);
				RefObject<ArrayList<Double>> tempRef_lens4 = new RefObject<ArrayList<Double>>(lens);

				keepOnlyLongest(tempRef_lexs3, tempRef_lens4);
				lexs = tempRef_lexs3.argvalue;
				lens = tempRef_lens4.argvalue;

				if (da > 0)
				{
					Grammar g = new Grammar();

					RefObject<ArrayList<String>> tempRef_rlexs = new RefObject<ArrayList<String>>(rlexs);
					RefObject<ArrayList<Double>> tempRef_rlens = new RefObject<ArrayList<Double>>(rlens);

					g.xmlFilterMatches(lexs, lens, xmlAnnotations, tempRef_rlexs, tempRef_rlens);
					rlexs = tempRef_rlexs.argvalue;
					rlens = tempRef_rlens.argvalue;
				}
			}

			if (rlexs != null && rlexs.size() > 0)
			{
				int len = 0;

				// first check for TRANS
				boolean thereIsATrans = false;

				for (int i = 0; i < rlexs.size(); i++)
				{
					String lex = rlexs.get(i);
					String entry = null, lemma = null, category = null;
					String[] features = null;

					RefObject<String> tempRef_entry = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);

					boolean tempVar = !Dic.parseDELAFFeatureArray(lex, tempRef_entry, tempRef_lemma, tempRef_category,
							tempRef_features);

					entry = tempRef_entry.argvalue;
					lemma = tempRef_lemma.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;

					if (tempVar)
					{
						continue;
					}

					if (lemma.equals("SYNTAX") && category.equals("TRANS"))
					{
						thereIsATrans = true;

						if (features != null)
						{
							// computes txtzone and propzone
							String txtzone = null, propzone = null;
							for (int ifeat = 0; ifeat < features.length; ifeat++)
							{
								String feat = features[ifeat];
								String propname = null, propvalue = null;

								RefObject<String> tempRef_propname = new RefObject<String>(propname);
								RefObject<String> tempRef_propvalue = new RefObject<String>(propvalue);

								Dic.getPropertyNameValue(feat, tempRef_propname, tempRef_propvalue);

								propname = tempRef_propname.argvalue;
								propvalue = tempRef_propvalue.argvalue;

								for (int j = 0; j < xmlAnnotations.length; j++)
								{
									if (xmlAnnotations[j].equals("<TRANS+" + propname + ">"))
									{
										propzone = propname;
										txtzone = propvalue;
									}
								}
							}

							if (propzone != null)
							{
								pw.print("<" + propzone);

								for (int ifeat = 0; ifeat < features.length; ifeat++)
								{
									String feat = features[ifeat];
									String propname = null, propvalue = null;

									RefObject<String> tempRef_propname2 = new RefObject<String>(propname);
									RefObject<String> tempRef_propvalue2 = new RefObject<String>(propvalue);

									Dic.getPropertyNameValue(feat, tempRef_propname2, tempRef_propvalue2);

									propname = tempRef_propname2.argvalue;
									propvalue = tempRef_propvalue2.argvalue;

									if (!propzone.equals(propname))
									{
										pw.print(" \"" + propname + "\"");
									}
								}
								pw.print(">");
								pw.print(txtzone + "</" + propzone + ">");
							}
							else
							{
								// no text zone: ignore TRANS

								thereIsATrans = false;
								continue;
							}
						}

						double helpDouble = rlens.get(i);
						len = (int) helpDouble;
						iChar = cpos + len;
						break;
					}
				}
				if (thereIsATrans)
				{
					continue;
				}

				if (rlexs == null || rlexs.isEmpty())
				{
					// output token
					if (!filterOut)
					{
						pw.print(token);
					}
				}
				else
				{
					boolean thereisunamb = false;

					// print headers
					for (int i = 0; i < rlexs.size(); i++)
					{
						String lex = rlexs.get(i);
						String entry = null, lemma = null, category = null;
						String[] features = null;

						RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
						RefObject<String> tempRef_category2 = new RefObject<String>(category);
						RefObject<String[]> tempRef_features2 = new RefObject<String[]>(features);

						boolean tempVar2 = !Dic.parseDELAFFeatureArray(lex, tempRef_entry2, tempRef_lemma2,
								tempRef_category2, tempRef_features2);

						entry = tempRef_entry2.argvalue;
						lemma = tempRef_lemma2.argvalue;
						category = tempRef_category2.argvalue;
						features = tempRef_features2.argvalue;

						if (tempVar2)
						{
							continue;
						}

						if (category.equals("TRANS"))
						{
							continue;
						}

						if (lemma.equals("SYNTAX"))
						{
							pw.print("<" + category);

							if (features != null)
							{
								for (int ifeat = 0; ifeat < features.length; ifeat++)
								{
									String feat = features[ifeat];
									String propname = null, propvalue = null;

									RefObject<String> tempRef_propname3 = new RefObject<String>(propname);
									RefObject<String> tempRef_propvalue3 = new RefObject<String>(propvalue);

									Dic.getPropertyNameValue(feat, tempRef_propname3, tempRef_propvalue3);

									propname = tempRef_propname3.argvalue;
									propvalue = tempRef_propvalue3.argvalue;

									if (propvalue.equals("UNAMB"))
									{
										thereisunamb = true;
									}
									else
									{
										if (propname.equals(propvalue))
										{
											pw.print(" TYPE=" + cleanupAndAddQuotes(propvalue));
										}
										else
										{
											pw.print(" " + propname + "=" + cleanupAndAddQuotes(propvalue));
										}
									}
								}
							}
							pw.print(">");
						}
						else
						{
							pw.print("<LU LEMMA=\"" + lemma + "\" CAT=\"" + category + "\"");

							if (features != null)
							{
								for (int ifeat = 0; ifeat < features.length; ifeat++)
								{
									String feat = features[ifeat];
									String propname = null, propvalue = null;

									RefObject<String> tempRef_propname4 = new RefObject<String>(propname);
									RefObject<String> tempRef_propvalue4 = new RefObject<String>(propvalue);

									Dic.getPropertyNameValue(feat, tempRef_propname4, tempRef_propvalue4);

									propname = tempRef_propname4.argvalue;
									propvalue = tempRef_propvalue4.argvalue;

									if (propvalue.equals("UNAMB"))
									{
										thereisunamb = true;
									}
									else
									{
										if (feat.equals(propname))
										{
											pw.print(" TYPE=" + cleanupAndAddQuotes(propvalue));
										}
										else
										{
											pw.print(" " + propname + "=" + cleanupAndAddQuotes(propvalue));
										}
									}
								}
							}
							pw.print(">");
						}
					}

					// print text
					double helpDouble = rlens.get(0);
					len = (int) helpDouble;

					if (len == 0) // we do not export info inside word forms
					{
						len = token.length();
					}

					String recurrentline = currentLine.substring(cpos, cpos + len);
					if (len > token.length()) 
					{
						if (thereisunamb)
						{
							pw.print(cleanupXmlTags(recurrentline));
						}
						else
						{
							buildXmlTaggedText(recurrentline, beg + cpos, filterOut, pw, itu, annotations,
									xmlAnnotations, lan, true);
						}
					}
					else
					{
						pw.print(cleanupXmlTags(recurrentline));
					}

					iChar = cpos + len;

					// print tailers
					for (int i = rlexs.size() - 1; i >= 0; i--)
					{
						String lex = rlexs.get(i);
						String entry = null, lemma = null, category = null;
						String[] features = null;

						RefObject<String> tempRef_entry3 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma3 = new RefObject<String>(lemma);
						RefObject<String> tempRef_category3 = new RefObject<String>(category);
						RefObject<String[]> tempRef_features3 = new RefObject<String[]>(features);

						boolean tempVar3 = !Dic.parseDELAFFeatureArray(lex, tempRef_entry3, tempRef_lemma3,
								tempRef_category3, tempRef_features3);

						entry = tempRef_entry3.argvalue;
						lemma = tempRef_lemma3.argvalue;
						category = tempRef_category3.argvalue;
						features = tempRef_features3.argvalue;

						if (tempVar3)
						{
							continue;
						}

						if (category.equals("TRANS"))
						{
							continue;
						}

						if (lemma.equals("SYNTAX"))
						{
							pw.print("</" + category + ">");
						}
						else
						{
							pw.print("</LU>");
						}
					}
				}
			}
			else
			{
				// output token
				if (!filterOut)
				{
					pw.print(token);
				}
			}
		}
	}

	/**
	 * Performs parsing and computes hLexemes for given Ntext object.
	 * 
	 * @param text
	 *            - Ntext which lexemes are computed
	 */
	private static void computehLexemes(Ntext text)
	{
		ParameterCheck.mandatory("text", text);

		// compute text.hLexemes and hPhrases
		text.hLexemes = new HashMap<String, Integer>();
		text.hPhrases = new HashMap<String, Integer>();
		text.hUnknowns = new HashMap<String, Integer>();

		if (text.annotations.size() > 0)
		{
			for (int i = 0; i < text.annotations.size(); i++)
			{
				String lex = (String) text.annotations.get(i);
				if (lex == null)
				{
					continue;
				}

				String entry = null, lemma = null, info = null;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
				RefObject<String> tempRef_info = new RefObject<String>(info);

				boolean tempVar = !Dic.parseDELAF(lex, tempRef_entry, tempRef_lemma, tempRef_info);

				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				info = tempRef_info.argvalue;

				if (tempVar)
				{
					Dic.writeLog("Invalid annotation: \"" + lex + "\"");
				}

				if (info.equals("UNKNOWN"))
				{
					if (!text.hUnknowns.containsKey(lex))
					{
						text.hUnknowns.put(lex, i);
					}
				}
				else if (lemma.equals("SYNTAX"))
				{
					if (!text.hPhrases.containsKey(lex))
					{
						text.hPhrases.put(lex, i);
					}
				}
				else
				{
					if (!text.hLexemes.containsKey(lex))
					{
						text.hLexemes.put(lex, i);
					}
				}
			}
		}
	}

	/**
	 * Loads Ntext object.
	 * 
	 * @param fullNamePath
	 *            - path to file where Ntext object is saved in
	 * @param languageName
	 *            - name of the language of file (not used)
	 * @param errMessage
	 *            - object containing errorMessage to be written to log
	 * @return loaded Ntext
	 * @throws IOException
	 *             if an error occurs while loading object
	 * @throws ClassNotFoundException
	 *             if an error occurs while reading object
	 */
	public static Ntext load(String fullNamePath, String languageName, RefObject<String> errMessage)
			throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatoryString("fullNamePath", fullNamePath);
		ParameterCheck.mandatoryString("languageName", languageName);
		ParameterCheck.mandatory("errMessage", errMessage);

		errMessage.argvalue = null;
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Ntext text = null;

		try
		{
			fileInputStream = new FileInputStream(fullNamePath);
			objectInputStream = new ObjectInputStream(fileInputStream);
			text = (Ntext) objectInputStream.readObject();
		}
		catch (RuntimeException ex)
		{
			if (objectInputStream != null)
				objectInputStream.close();
			if (fileInputStream != null)
				fileInputStream.close();

			errMessage.argvalue = "Cannot load text " + fullNamePath + ": " + ex.getMessage();
			Dic.writeLog(errMessage.argvalue);
			return null;
		}

		if (objectInputStream != null)
			objectInputStream.close();
		if (fileInputStream != null)
			fileInputStream.close();

		text.Lan = new Language(text.LanguageName);
		if (text.Lan == null)
		{
			return null;
		}

		double multiplier = 1.0;
		if (text.annotations != null && text.annotations.size() > 0)
		{
			if (text.annotations.get(0) != null && text.annotations.get(0).getClass() == HashMap.class)
			{
				// Unchecked cast cannot be avoided here; text.annotations is an ArrayList that contains objects of
				// different data types (Doubles, HashMaps, ArrayLists...)
				text.hTokens = (HashMap<String, Indexkey>) text.annotations.get(0);
				text.annotations.remove(0);
			}
			else
			{
				String keyword;

				do
				{
					keyword = (String) text.annotations.get(0);

					if (keyword.equals("$tokens$"))
					{
						// Unchecked cast cannot be avoided here; text.annotations is an ArrayList that contains objects
						// of different data types (Doubles, HashMaps, ArrayLists...)
						text.hTokens = (HashMap<String, Indexkey>) text.annotations.get(1);
						text.annotations.remove(0);
						text.annotations.remove(0);
					}
					else if (keyword.equals("$colors$"))
					{
						
						text.annotations.remove(0);
						text.annotations.remove(0);
					}
					else if (keyword.equals("$multiplier$"))
					{
						if (text.annotations.get(1) == null)
						{
							multiplier = 100.0;
						}
						else
						{
							multiplier = (Double) text.annotations.get(1);
						}
						text.annotations.remove(0);
						text.annotations.remove(0);
					}
					else if (keyword.equals("$resources$"))
					{
						// Unchecked cast cannot be avoided here; text.annotations is an ArrayList that contains objects
						// of different data types (Doubles, HashMaps, ArrayLists...)
						text.listOfResources = (ArrayList<String>) text.annotations.get(1);
						text.annotations.remove(0);
						text.annotations.remove(0);
					}
				}
				while (text.annotations.size() > 0
						&& (keyword.equals("$tokens$") || keyword.equals("$colors$") || keyword.equals("$multiplier$") || keyword
								.equals("$resources$")));
			}
		}

		if (text.mft != null)
		{
			text.mft.afterLoading(multiplier);
		}

		if (text.annotations != null)
		{
			computehLexemes(text);
		}

		return text;
	}

	/**
	 * Loads Ntext object for working with corpus.
	 * 
	 * @param fullNamePath
	 *            - path to file where Ntext object is saved in
	 * @param lan
	 *            - language of the corpus
	 * @param multiplier
	 *            - multiplier to be passed to mft.afterLoading
	 * @return Ntext object
	 * @throws IOException
	 *             if an error occurs while opening a stream or reading from it
	 */
	public static Ntext loadForCorpus(String fullNamePath, Language lan, double multiplier) throws IOException
	{
		ParameterCheck.mandatoryString("fullNamePath", fullNamePath);
		ParameterCheck.mandatory("lan", lan);

		FileInputStream fileInputStream = null;
		Ntext text = null;

		fileInputStream = new FileInputStream(fullNamePath);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		try
		{
			text = (Ntext) objectInputStream.readObject();
		}
		catch (Exception e)
		{
			fileInputStream.close();
			return null;
		}

		fileInputStream.close();
		text.Lan = lan;

		if (text.mft != null)
		{
			text.mft.afterLoading(multiplier);
		}

		return text;
	}

	/**
	 * Loads just buffer for corpus.
	 * 
	 * @param fullNamePath
	 *            - path to file where Ntext object is saved in
	 * @param lan
	 *            - language of the corpus
	 * @param multiplier
	 *            - multiplier to be passed to mft.afterLoading
	 * @return text of a given file
	 * @throws IOException
	 *             if an error occurs while opening a stream or reading from it
	 */
	public static Ntext loadJustBufferForCorpus(String fullNamePath, Language lan, double multiplier)
			throws IOException
	{

		ParameterCheck.mandatoryString("fullNamePath", fullNamePath);
		ParameterCheck.mandatory("lan", lan);

		FileInputStream fileInputStream = null;
		Ntext text = null;

		fileInputStream = new FileInputStream(fullNamePath);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		try
		{
			text = (Ntext) objectInputStream.readObject();
		}
		catch (Exception e)
		{
			fileInputStream.close();
			return null;
		}

		fileInputStream.close();
		text.Lan = lan;

	
		return text;
	}

	/**
	 * Saves Ntext object.
	 * 
	 * @param fullNamePath
	 *            - path to file where this object should be saved.
	 * @throws IOException
	 *             if an error occurs during opening of a stream or writing to it.
	 */
	public final void save(String fullNamePath) throws IOException
	{
		ParameterCheck.mandatoryString("fullNamePath", fullNamePath);

		int nbOfHacks = 0;

		if (this.annotations == null)
		{
			this.annotations = new ArrayList<Object>();
		}

		if (this.hTokens != null)
		{
			this.annotations.add(0, "$tokens$");
			this.annotations.add(1, this.hTokens);
			nbOfHacks++;
		}

		

		if (this.listOfResources != null)
		{
			this.annotations.add(0, "$resources$");
			this.annotations.add(1, this.listOfResources);
			nbOfHacks++;
		}

		if (this.mft != null)
		{
			this.mft.beforeSaving(this.mft.multiplier);
			this.annotations.add(0, "$multiplier$"); // multiplies all positions to store (double)stPostions as (int)
			this.annotations.add(1, null); // equivalent to Insert (1,100.0)
			nbOfHacks++;
		}

		// Serialization
		File file = new File(fullNamePath);
		if (!file.exists())
			file.createNewFile();

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		fileOutputStream.close();

		if (nbOfHacks > 0)
		{
			for (int i = 0; i < nbOfHacks; i++)
			{
				this.annotations.remove(0);
				this.annotations.remove(0);
			}
		}
	}

	/**
	 * Saves Ntext object for working with corpus.
	 * 
	 * @param fullNamePath
	 *            - path to file where this object should be saved.
	 * 
	 * @throws IOException
	 *             if an error occurs during opening of a stream or writing to it.
	 */
	public final void saveForCorpus(String fullNamePath) throws IOException
	{
		ParameterCheck.mandatoryString("fullNamePath", fullNamePath);

		if (this.mft != null)
		{
			this.mft.beforeSaving(this.mft.multiplier);
		}

		// Serialization
		File file = new File(fullNamePath);
		if (!file.exists())
			file.createNewFile();

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		fileOutputStream.close();
	}

	/**
	 * 
	 * @return
	 */
	public final boolean updateAnnotationsForText()
	{
		if (this.mft == null)
		{
			return false;
		}

		boolean[] exist = new boolean[this.annotations.size()];
		for (int i = 0; i < exist.length; i++)
		{
			exist[i] = false;
		}

		// scan mft
		for (int tuNb = 1; tuNb <= this.mft.tuAddresses.length - 1; tuNb++)
		{
			
			ArrayList<TransitionObject> transitions = this.mft.aTransitions.get(tuNb); // all the transitions in text
																						// unit

		
			for (int it = 0; it < transitions.size(); it++)
			{
				
				ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

				
				for (int io = 0; io < outgoings.size(); io++)
				{
					
					int tokenId = outgoings.get(io).getTokenId();

					if (tokenId >= exist.length)
					{
						return false;
					}
					exist[tokenId] = true;
				}
			}
		}

		// update annotations
		for (int ia = 0; ia < this.annotations.size(); ia++)
		{
			if (!exist[ia])
			{
				String label = (String) this.annotations.get(ia);
				if (label == null)
				{
					continue;
				}

				String entry = null, info = null;

				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_info = new RefObject<String>(info);

				boolean tempVar = Dic.parseDELAS(label, tempRef_entry, tempRef_info);

				entry = tempRef_entry.argvalue;
				info = tempRef_info.argvalue;

				if (tempVar)
				{
					// a lexeme does not exist anymore => delete it
					this.annotations.set(ia, null);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public Language getLanguage()
	{
		return Lan;
	}

	/**
	 * 
	 * @return
	 */
	public String getDelimPattern()
	{
		return DelimPattern;
	}

	public ArrayList<Object> getAnnotations()
	{
		return annotations;
	}

}