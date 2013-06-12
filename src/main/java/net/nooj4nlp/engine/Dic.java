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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import net.nooj4nlp.engine.helper.DotNetToJavaStringHelper;
import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * @author Silberztein Max
 */
public class Dic
{
	private static int LEMMA_PREFIX_LENGTH = "LEMMA".length();
	private static int CAT_PREFIX_LENGTH = "CAT".length();
	private static int ENT_PREFIX_LENTH = "ENT".length();

	/**
	 * Writes text that contains data about Nooj and language to given writer.
	 * 
	 * @param writer
	 *            - writer to write the text in
	 * @param languageName
	 *            - name of the language
	 * @throws IOException
	 *             if an error occurs while writing text to writer
	 */
	static void initLoad(OutputStreamWriter writer, String languageName) throws IOException
	{
		ParameterCheck.mandatory("writer", writer);

		writer.write("# NooJ V4\n");
		writer.write("# Dictionary\n");
		writer.write("#\n");
		writer.write("# Input Language is: " + languageName + "\n");
		writer.write("#\n");
		writer.write("# Alphabetical order is not required.\n");
		writer.write("#\n");
		writer.write("# Use any number of inflectional/derivational description files (.nof), e.g.:\n");
		writer.write("# Special Command: #use description.nof\n");
		writer.write("# INFLECTIONAL AND DERIVATIONAL DESCRIPTION FILES MUST BE STORED IN SAME DIRECTORY AS THE DICTIONARY\n");
		writer.write("#\n");
		writer.write("# Special Features: +NW (non-word) +FXC (frozen expression component) +UNAMB (unambiguous lexical entry)\n");
		writer.write("#                   +FLX= (inflectional paradigm) +DRV (derivational paradigm)\n");
		writer.write("#\n");
		writer.write("# Special Characters: '\\' '\"' '+' ',' '#' ' '\n");
		writer.write("#\n");
	}

	/**
	 * Helper method -parses factorized information (lexical information is factorized in compressed dictionaries - i.e.
	 * if a word can have more than one analysis, analysis are put together in one factorized information string).
	 * 
	 * @param line0
	 *            - string to be parsed
	 * @param lemma
	 *            - object to hold lemma extracted from line0
	 * @param info
	 *            - object to hold info extracted from line0
	 * @return true if no lemma is found or if "line0" == "lemma=info", false otherwise
	 */
	private static boolean parseFactorizedInfo(String line0, RefObject<String> lemma, RefObject<String> info)
	{
		lemma.argvalue = info.argvalue = null;

		int i;

		// look for a comment
		int index = -1;
		for (int ii = 0; ii < line0.length(); ii++)
		{
			if (line0.charAt(ii) == '\\')
			{
				ii++;
			}
			else if (line0.charAt(ii) == '<')
			{
				for (ii++; ii < line0.length() && line0.charAt(ii) != '>'; ii++)
				{
					;
				}
			}
			else if (line0.charAt(ii) == '{')
			{
				for (ii++; ii < line0.length() && line0.charAt(ii) != '}'; ii++)
				{
					;
				}
			}
			else if (line0.charAt(ii) == '#') // comment
			{
				index = ii;
				break;
			}
		}

		String line;
		if (index == -1)
		{
			line = line0;
		}
		else
		{
			line = line0.substring(0, index);
		}

		if (line.length() == 0)
		{
			return false;
		}

		// get lemma
		StringBuilder sb = new StringBuilder();
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
				sb.append(line.charAt(i));
			}
			else if (line.charAt(i) == '<')
			{
				sb.append(line.charAt(i));

				for (i++; i < line.length() && line.charAt(i) != '>'; i++)
				{
					sb.append(line.charAt(i));
				}
				sb.append(line.charAt(i));
			}
			else if (line.charAt(i) == '{')
			{
				sb.append(line.charAt(i));

				for (i++; i < line.length() && line.charAt(i) != '}'; i++)
				{
					sb.append(line.charAt(i));
				}
				sb.append(line.charAt(i));
			}
			else
			{
				sb.append(line.charAt(i));
			}
		}

		if (i == line.length()) // did not find any "," => no lemma
		{
			info.argvalue = line;
			return true;
		}

		lemma.argvalue = sb.toString();
		info.argvalue = line.substring(i + 1);
		Character[] charactersToTrim = null;
		info.argvalue = DotNetToJavaStringHelper.trimEnd(info.argvalue, charactersToTrim);

		if (info.argvalue.equals("")) // nothing after the ","
		{
			return false;
		}

		return true;
	}

	/**
	 * Parses factorized information (lexical information is factorized in compressed dictionaries - i.e. if a word can
	 * have more than one analysis, analysis are put together in one factorized information string).
	 * 
	 * @param line0
	 *            - string to be parsed
	 * @param lemma
	 *            - object to hold lemma extracted from line0
	 * @param info
	 *            - object to hold info extracted from line0
	 * @param category
	 *            - object to hold category extracted from info
	 * @param features
	 *            - object to hold features list extracted from info
	 * @return false if line0 doesn't have proper format, if category is "" or null, true otherwise
	 */
	static boolean parseFactorizedInfo(String line0, RefObject<String> lemma, RefObject<String> info,
			RefObject<String> category, RefObject<String[]> features)
	{
		ParameterCheck.mandatory("line0", line0);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("info", info);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		if (!parseFactorizedInfo(line0, lemma, info))
		{
			return false;
		}

		category.argvalue = null;
		features.argvalue = null;

		// get category
		int k = 0;
		for (; k < info.argvalue.length() && Character.isUpperCase(info.argvalue.charAt(k)); k++)
		{
			;
		}

		category.argvalue = info.argvalue.substring(0, k);
		if (category.argvalue.equals("") || category.argvalue == null)
		{
			return false;
		}

		// get features
		String allfeatures = info.argvalue.substring(k);
		features.argvalue = splitAllFeaturesWithPlus(allfeatures);

		return true;
	}

	/**
	 * Checks if given string is a lexical constraint.
	 * 
	 * @param symbol
	 *            - string to be checked
	 * @return true if given string is a lexical constraint, false otherwise
	 */
	static boolean isALexicalConstraint(String symbol)
	{
		// symbol = "<tables=N>" or "<tables=table,N>" => yes
		// symbol = "<tables=:table,N+Conc+f+p>" or "<tables,table,N+class=Conc+f+p>" => no
		if (symbol == null || symbol.length() == 0)
		{
			return false;
		}

		if (symbol.charAt(0) != '<')
		{
			return false;
		}

		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}

		for (int i = 1; i < symbol.length(); i++)
		{
			if (symbol.charAt(i) == '\\')
			{
				i++;
				continue;
			}
			if (symbol.charAt(i) == ',' || symbol.charAt(i) == '<') // if I find a comma first => no
			{
				return false;
			}
			else if (symbol.charAt(i) == '=') // if I find a "=" first => yes
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if given string is a lexical symbol.
	 * 
	 * @param symbol
	 *            - string to be checked
	 * @return true if given string is a lexical symbol, false otherwise
	 */
	public static boolean isALexicalSymbol(String symbol)
	{
		ParameterCheck.mandatoryString("symbol", symbol);

		// symbol = "<tables=N>" or "<tables=table,N>" => no
		// symbol = "<tables,table,N+Conc+f+p>" or "<tables,table,N+class=Conc+f+p>" => yes
		if (symbol.charAt(0) != '<')
		{
			return false;
		}

		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}

		for (int i = 1; i < symbol.length() - 1; i++)
		{
			if (symbol.charAt(i) == '=') // if I find a "=" first => no
			{
				return false;
			}
			if (symbol.charAt(i) == ',') // if I find a "," first => yes
			{
				return true;
			}
		}

		return false; // no comma => return false;
	}

	/**
	 * Parses a sequence of symbols into an array of symbols.
	 * 
	 * @param sequence
	 *            - string to be parsed
	 * @return array of resulting strings
	 */
	public static String[] parseSequenceOfSymbols(String sequence)
	{
		ParameterCheck.mandatory("sequence", sequence);

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < sequence.length();)
		{
			int j;
			if (sequence.charAt(i) == '<')
			{
				int embeddedAngle = 0;
				for (j = 1; (i + j) < sequence.length()
						&& (sequence.charAt(i + j) != '>' || (sequence.charAt(i + j) == '>' && embeddedAngle != 0)); j++)
				{
					if (sequence.charAt(i + j) == '<')
					{
						embeddedAngle++;
					}
					else if (sequence.charAt(i + j) == '>')
					{
						embeddedAngle--;
					}
				}

				if (i + j < sequence.length())
				{
					j++;
					result.add(sequence.substring(i, i + j));
				}
				else
				{
					result.add(sequence.substring(i));
				}

				i += j;
			}
			else if (Character.isWhitespace(sequence.charAt(i)))
			{
				i++;
			}
			else if (sequence.charAt(i) == '"')
			{
				for (j = 1; (i + j) < sequence.length() && sequence.charAt(i + j) != '"'; j++)
				{
					;
				}

				j++;
				i += j;
			}
			else
			{
				result.add(String.valueOf(sequence.charAt(i)));
				i++;
			}
		}

		String[] res = result.toArray(new String[result.size()]);

		return res;
	}

	/**
	 * Checks if there is a lexical constraint in array list of strings.
	 * 
	 * @param symbols
	 *            - array list of strings to be checked
	 * @return true if there is a lexical constraint, false otherwise
	 */
	public static boolean isThereALexicalConstraint(ArrayList<String> symbols)
	{
		ParameterCheck.mandatory("symbols", symbols);

		for (int i = 0; i < symbols.size(); i++)
		{
			String so = symbols.get(i);

			if (so == null || so.equals(""))
			{
				continue;
			}

			for (int iso = 0; iso < so.length();)
			{
				if (so.charAt(iso) != '<')
				{
					iso++;
					continue;
				}

				int len = -1;

				for (len = 1; iso + len < so.length(); len++)
				{
					if (so.charAt(iso + len) == '>')
					{
						break;
					}
				}

				if (iso + len < so.length())
				{
					String so2 = so.substring(iso, iso + len + 1);
					if (isALexicalConstraint(so2))
					{
						return true;
					}
				}

				iso += len + 1;
			}
		}

		return false;
	}

	/**
	 * Removes comments (lines starting with '#') from given string.
	 * 
	 * @param buf
	 *            - string to remove comments from
	 * @return string without comments
	 */
	public static String noComment(String buf)
	{
		ParameterCheck.mandatory("buf", buf);

		StringBuilder buffer = new StringBuilder();
		int i;

		for (i = 0; i < buf.length(); i++)
		{
			if (buf.charAt(i) == '#')
			{
				while (i < buf.length() && buf.charAt(i) != '\n')
				{
					i++;
				}
			}
			char c = buf.charAt(i);
			buffer.append(c);
		}

		return buffer.toString();
	}

	/**
	 * Method that is used to read each rule in the properties definition file (_properties.def)
	 * 
	 * @param line
	 *            - string to be checked
	 * @param ibuf
	 *            - index of starting character
	 * @param category
	 *            - category in the rule
	 * @param property
	 *            - property in the rule
	 * @param features
	 *            - features of the rule
	 * @param errMessage
	 *            - message to write to log file in case of error
	 * @return -1 in an error occurs
	 */
	public static int getRule(String line, int ibuf, RefObject<String> category, RefObject<String> property,
			RefObject<String[]> features, RefObject<String> errMessage)
	{
		ParameterCheck.mandatory("line", line);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("property", property);
		ParameterCheck.mandatory("features", features);
		ParameterCheck.mandatory("errmessage", errMessage);

		int i, j, k, m;
		category.argvalue = property.argvalue = errMessage.argvalue = null;
		features.argvalue = null;

		// skip white spaces
		for (i = ibuf; i < line.length() && Character.isWhitespace(line.charAt(i)); i++)
		{
			;
		}

		if (i == line.length())
		{
			return -1;
		}

		// get category and property
		for (j = 0; i + j < line.length() && line.charAt(i + j) != '=' && !Character.isWhitespace(line.charAt(i + j)); j++)
		{
			;
		}

		if (i + j == line.length())
		{
			return -1;
		}

		category.argvalue = line.substring(i, i + j);
		int index = category.argvalue.indexOf('_');

		if (index != -1) // for INFLECTION and SYNTAX
		{
			property.argvalue = category.argvalue.substring(index + 1);
			category.argvalue = category.argvalue.substring(0, index);
		}

		// get "="
		for (k = 0; i + j + k < line.length() && line.charAt(i + j + k) != '='
				&& Character.isWhitespace(line.charAt(i + j + k)); k++)
		{
			;
		}

		if (i + j + k == line.length() || line.charAt(i + j + k) != '=')
		{
			errMessage.argvalue = "Syntax Error: '=' is missing";
			Dic.writeLog(errMessage.argvalue);
			return -1;
		}

		// get ";"
		for (m = 1; i + j + k + m < line.length() && line.charAt(i + j + k + m) != ';'; m++)
		{
			;
		}

		if (i + j + k + m == line.length() || line.charAt(i + j + k + m) != ';')
		{
			errMessage.argvalue = "Syntax Error: ';' is missing";
			Dic.writeLog(errMessage.argvalue);
			return -1;
		}

		// get features
		String exp = line.substring(i + j + k + 1, i + j + k + 1 + m - 1);
		features.argvalue = exp.split("[+|]", -1);

		for (int ifeat = 0; ifeat < features.argvalue.length; ifeat++)
		{
			features.argvalue[ifeat] = features.argvalue[ifeat].trim();
		}

		return i + j + k + m + 1;
	}

	/**
	 * Checks if feature contains a property, or if properties contain the proper value (that corresponds to
	 * 'category_feature').
	 * 
	 * @param feature
	 *            - string to be checked or used for checks
	 * @param category
	 *            - string to be used for checks
	 * @param properties
	 *            - hash table to be checked for property
	 * @param propertyName
	 *            - name of the found property
	 * @param propertyValue
	 *            - value of the found property
	 * @return true if property is found, false otherwise
	 */
	public static boolean getProperty(String feature, String category, HashMap<String, String> properties,
			RefObject<String> propertyName, RefObject<String> propertyValue)
	{
		ParameterCheck.mandatory("feature", feature);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("propertyName", propertyName);
		ParameterCheck.mandatory("propertyValue", propertyValue);

		propertyName.argvalue = propertyValue.argvalue = feature;
		int index = feature.indexOf('=');
		if (index != -1)
		{
			propertyName.argvalue = feature.substring(0, index);
			propertyValue.argvalue = feature.substring(index + 1);
			return true;
		}
		if (properties == null)
		{
			return false;
		}

		// make property pair implicit
		String property = properties.get(category + "_" + feature);
		if (property == null)
		{
			return false;
		}
		propertyName.argvalue = property;
		propertyValue.argvalue = feature;
		return true;
	}

	/**
	 * Function is used to normalize (= uncompress) the lexical information that is stored in a compressed dictionary.
	 * 
	 * @param category
	 *            - category to be added to resulting array list
	 * @param features
	 *            -
	 * @param properties
	 * @return
	 */
	static ArrayList<String> normalizeInformation(String category, String[] features, HashMap<String, String> properties)
	{
		if (features == null || features.length == 0)
		{
			// e.g. "the,DET"
			ArrayList<String> infos = new ArrayList<String>();
			infos.add(category);
			return infos;
		}

		// if a lexeme looks like "N+Number=s+Number=p" then make it two lexemes: "N+Number=s" + "N+Number=p"
		ArrayList<String> lfeatures = new ArrayList<String>(features.length);
		Collections.addAll(lfeatures, features);

		// CHECK i j k indexes
		ArrayList<String> res = new ArrayList<String>();
		res.add(category);

		for (int i = 0; i < lfeatures.size(); i++)
		{
			String ifeat = lfeatures.get(i);
			String ipropname = null, ipropvalue = null;
			RefObject<String> tempRef_ipropname = new RefObject<String>(ipropname);
			RefObject<String> tempRef_ipropvalue = new RefObject<String>(ipropvalue);

			Dic.getProperty(ifeat, category, properties, tempRef_ipropname, tempRef_ipropvalue);

			ipropname = tempRef_ipropname.argvalue;
			ipropvalue = tempRef_ipropvalue.argvalue;

			ArrayList<String> res2 = new ArrayList<String>();

			for (int j = 0; j < res.size(); j++)
			{
				String info = res.get(j);
				if (ipropname.equals(ipropvalue))
				{
					res2.add(info + "+" + ipropname);
				}
				else
				{
					res2.add(info + "+" + ipropname + "=" + ipropvalue);
				}
			}

			// do not defactorize DRVs because one lexeme might have more than one derivation
			if (!ipropname.equals("DRV") && i + 1 < lfeatures.size())
			{
				for (int j = i + 1; j < lfeatures.size();)
				{
					String jfeat = lfeatures.get(j);
					String jpropname = null, jpropvalue = null;
					RefObject<String> tempRef_jpropname = new RefObject<String>(jpropname);
					RefObject<String> tempRef_jpropvalue = new RefObject<String>(jpropvalue);

					Dic.getProperty(jfeat, category, properties, tempRef_jpropname, tempRef_jpropvalue);

					jpropname = tempRef_jpropname.argvalue;
					jpropvalue = tempRef_jpropvalue.argvalue;

					if (ipropname.equals(jpropname))
					{
						for (int k = 0; k < res.size(); k++)
						{
							String info = res.get(k);
							if (jpropname.equals(jpropvalue))
							{
								res2.add(info + "+" + jpropname);
							}
							else
							{
								res2.add(info + "+" + jpropname + "=" + jpropvalue);
							}
						}
						lfeatures.remove(j);
					}
					else
					{
						j++;
					}
				}
			}
			res = res2;
		}
		return res;
	}

	/**
	 * Function is used to normalize (= uncompress) the lexical entry that is stored in a compressed dictionary.
	 * 
	 * @param line
	 *            - lexical entry to be uncompressed
	 * @param engine
	 *            - engine used to perform the normalization
	 * @param lines
	 *            - object that holds uncompressed entries
	 * @return true if uncompressing was successful, false otherwise
	 */
	static boolean normalizeLexicalEntry(String line, Engine engine, RefObject<ArrayList<String>> lines)
	{
		ParameterCheck.mandatory("engine", engine);
		ParameterCheck.mandatory("lines", lines);

		// line = "do,do,V+PR+1+2+s" => lines = {"do,do,V+Tense=PR+Pers=1+Nb=s","do,do,V+Tense=PR+Pers=2+Nb=s"}
		String entry = null, lemma = null, category = null;
		String[] features = null;

		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);

		boolean tempVar = !Dic.parseDELAFFeatureArray(line, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;

		if (tempVar)
		{
			return false;
		}

		ArrayList<String> infos = Dic.normalizeInformation(category, features, engine.properties);

		if (infos == null || infos.isEmpty())
		{
			return false;
		}

		for (int i = 0; i < infos.size(); i++)
		{
			String info = infos.get(i);
			String line0 = entry + "," + lemma + "," + info;
			if (lines.argvalue == null)
			{
				lines.argvalue = new ArrayList<String>();
			}
			lines.argvalue.add(line0);
		}

		return true;
	}

	/**
	 * Function is used to normalize (= uncompress) the lexeme symbol that is stored in a compressed dictionary.
	 * 
	 * 
	 * @param lexeme
	 *            - lexeme symbol to be uncompressed
	 * @param engine
	 *            - engine used to perform the normalization
	 * @param lines
	 *            - object that holds uncompressed lexeme symbols
	 * @return true if uncompressing was successful, false otherwise
	 */
	static boolean normalizeLexemeSymbol(String lexeme, Engine engine, RefObject<ArrayList<String>> lexemes)
	{
		ParameterCheck.mandatoryString("lexeme", lexeme);
		ParameterCheck.mandatory("lexemes", lexemes);

		String line = lexeme.substring(1, 1 + lexeme.length() - 2);
		ArrayList<String> lines = null;
		RefObject<ArrayList<String>> tempRef_lines = new RefObject<ArrayList<String>>(lines);

		boolean tempVar = !normalizeLexicalEntry(line, engine, tempRef_lines);
		lines = tempRef_lines.argvalue;

		if (tempVar)
		{
			return false;
		}

		for (int i = 0; i < lines.size(); i++)
		{
			String line0 = lines.get(i);

			if (lexemes.argvalue == null)
			{
				lexemes.argvalue = new ArrayList<String>();
			}
			lexemes.argvalue.add("<" + line0 + ">");
		}

		return true;
	}

	/**
	 * Returns value of property ('feature' -> 'property=value').
	 * 
	 * @param feature
	 *            - string to be parsed for property
	 * @return value of the property, if feature is of the right kind, or whole feature, if it's not
	 */
	static String getPropertyValue(String feature)
	{
		ParameterCheck.mandatory("feature", feature);

		int index = feature.indexOf('=');
		if (index == -1)
		{
			return feature;
		}
		else
		{
			return feature.substring(index + 1);
		}
	}

	/**
	 * Returns value of property, if it is contained in information string.
	 * 
	 * @param propertyName
	 *            - name of the property to be checked for
	 * @param information
	 *            - string to be checked for property
	 * @return value of the property, if found, null otherwise
	 */
	public static String getPropertyValue(String propertyName, String information)
	{
		ParameterCheck.mandatory("propertyName", propertyName);
		ParameterCheck.mandatory("information", information);

		int index = information.indexOf('+');
		if (index == -1)
		{
			return null;
		}

		String info = information.substring(index);
		String[] features = splitAllFeaturesWithPlus(info);

		for (String feat : features)
		{
			String name = null, value = null;

			RefObject<String> tempRef_name = new RefObject<String>(name);
			RefObject<String> tempRef_value = new RefObject<String>(value);

			Dic.getPropertyNameValue(feat, tempRef_name, tempRef_value);

			name = tempRef_name.argvalue;
			value = tempRef_value.argvalue;

			if (propertyName.equals(name))
			{
				return value;
			}
		}
		return null;
	}

	// Parse Dictionary entries

	/**
	 * Cleans up double quotes inside property values, e.g. potato,N+FR="pomme" de "terre" =>
	 * potato,N+FR="pomme de terre" if there is no double quotes, then do not add any, e.g. potato,N+FR=patate =>
	 * potato,N+FR=patate does not change any other double quotes inside entry or lemma
	 * 
	 * @param info
	 *            - string to be cleaned up
	 * @return string without double quotes
	 */
	public static String cleanUpDoubleQuotes(String info)
	{
		ParameterCheck.mandatory("info", info);

		StringBuilder sb = new StringBuilder();
		int i = 0;

		for (i = 0; i < info.length();)
		{
			if (info.charAt(i) == '\\')
			{
				sb.append('\\');
				i++;
				sb.append(info.charAt(i));
				i++;
				continue;
			}
			else if (info.charAt(i) == '"')
			{
				sb.append('"');
				for (i++; i < info.length() && info.charAt(i) != '"'; i++)
				{
					sb.append(info.charAt(i));
				}
				if (i < info.length())
				{
					sb.append('"');
				}
				i++;
				continue;
			}
			else if (info.charAt(i) != '=')
			{
				sb.append(info.charAt(i));
				i++;
				continue;
			}

			sb.append('=');

			// there is a +Prop=ba"bla"bla"bla => remove all internal " and add outside ones
			StringBuilder sval = new StringBuilder();
			boolean thereIsDoubleQuote = false;

			for (i++; i < info.length() && info.charAt(i) != '+';)
			{
				if (info.charAt(i) == '\\')
				{
					sval.append('\\');
					i++;
					sval.append(info.charAt(i));
					i++;
					continue;
				}
				else if (info.charAt(i) != '"')
				{
					sval.append(info.charAt(i));
					i++;
					continue;
				}
				else
				{
					thereIsDoubleQuote = true;
					for (i++; i < info.length() && info.charAt(i) != '"'; i++)
					{
						sval.append(info.charAt(i));
					}
					i++;
				}
				continue;
			}
			if (thereIsDoubleQuote)
			{
				sb.append('"');
				sb.append(sval);
				sb.append('"');
			}
			else
			{
				sb.append(sval);
			}
		}
		return sb.toString();
	}

	/**
	 * Adds a backslash in front of a comma in given string.
	 * 
	 * @param entry
	 *            - string to be transformed
	 * @return resulting string
	 */
	public static String protectComma(String entry)
	{
		ParameterCheck.mandatory("entry", entry);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < entry.length(); i++)
		{
			if (entry.charAt(i) == ',')
			{
				sb.append('\\');
			}
			sb.append(entry.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * @param line
	 * @param entry
	 * @param info
	 * @return
	 */
	public static boolean parseDELAS(String line, RefObject<String> entry, RefObject<String> info)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("info", info);
		entry.argvalue = info.argvalue = null;
		int i;
		line = cleanUpDoubleQuotes(line);
		// get entry
		StringBuilder sb = new StringBuilder();
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				sb.append('\\');
				i++;
				sb.append(line.charAt(i));
				continue;
			}
			else if (line.charAt(i) == '"')
			{
				sb.append('"');
				for (i++; i < line.length() && line.charAt(i) != '"'; i++)
				{
					sb.append(line.charAt(i));
				}
				sb.append('"');
				continue;
			}
			else
			{
				sb.append(line.charAt(i));
			}
		}
		if (i == line.length()) // did not find any ","
		{
			return false;
		}
		entry.argvalue = sb.toString();
		info.argvalue = line.substring(i + 1);
		if (info.argvalue.equals("")) // nothing after the ","
		{
			return false;
		}

		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param category
	 * @param features
	 * @return
	 */
	public static boolean parseDELAS(String line, RefObject<String> entry, RefObject<String> category,
			RefObject<String> features)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		entry.argvalue = category.argvalue = features.argvalue = null;
		String info = null;
		RefObject<String> tempRef_info = new RefObject<String>(info);
		boolean tempVar = !Dic.parseDELAS(line, entry, tempRef_info);
		info = tempRef_info.argvalue;
		if (tempVar)
		{
			return false;
		}
		if (info.length() == 0) // there must be some lexical information
		{
			return false;
		}
		if (info.charAt(0) < 'A' || info.charAt(0) > 'Z') // the category must be in UPPERCASE
		{
			return false;
		}
		int i;
		for (i = 0; i < info.length() && info.charAt(i) >= 'A' && info.charAt(i) <= 'Z'; i++)
		{
			;
		}
		if (i >= info.length())
		{
			category.argvalue = info;
		}
		else
		{
			category.argvalue = info.substring(0, i);
			features.argvalue = info.substring(i);
			if (features.argvalue.charAt(0) != '+')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param category
	 * @param features
	 * @return
	 */
	public static boolean parseDELASFeatureArray(String line, RefObject<String> entry, RefObject<String> category,
			RefObject<String[]> features)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		entry.argvalue = category.argvalue = null;
		features.argvalue = null;
		String allFeatures = null;
		RefObject<String> tempRef_allFeatures = new RefObject<String>(allFeatures);
		boolean tempVar = !Dic.parseDELAS(line, entry, category, tempRef_allFeatures);
		allFeatures = tempRef_allFeatures.argvalue;
		if (tempVar)
		{
			return false;
		}
		features.argvalue = splitAllFeaturesWithPlus(allFeatures);
		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param info
	 * @return
	 */
	public static boolean parseContracted(String line, RefObject<String> entry, RefObject<String> info)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("info", info);

		entry.argvalue = info.argvalue = null;
		if (!Dic.parseDELAS(line, entry, info))
		{
			return false;
		}
		if (info.argvalue.length() == 0) // there must be some lexical information
		{
			return false;
		}
		if (info.argvalue.charAt(0) != '<') // the comma must be followed by a lexeme
		{
			return false;
		}
		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param lemma
	 * @param info
	 * @return
	 */
	public static boolean parseDELAF(String line, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> info)
	{
		ParameterCheck.mandatory("line", line);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("info", info);

		int i, j;
		entry.argvalue = lemma.argvalue = info.argvalue = null;

		if (line.length() == 0)
		{
			return false;
		}
		line = cleanUpDoubleQuotes(line);
		// get entry
		StringBuilder sb = new StringBuilder();
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
				sb.append(line.charAt(i));
			}
			else if (line.charAt(i) == '<')
			{
				sb.append(line.charAt(i));
				for (i++; i < line.length() && line.charAt(i) != '>'; i++)
				{
					sb.append(line.charAt(i));
				}
				sb.append(line.charAt(i));
			}
			else if (line.charAt(i) == '"')
			{
				sb.append(line.charAt(i));
				for (i++; i < line.length() && line.charAt(i) != '"'; i++)
				{
					sb.append(line.charAt(i));
				}
				sb.append(line.charAt(i));
			}
			else
			{
				sb.append(line.charAt(i));
			}
		}
		if (i == line.length()) // never found the ","
		{
			return false;
		}

		entry.argvalue = sb.toString();

		// get lemma
		sb = new StringBuilder();
		for (j = i + 1; j < line.length() && line.charAt(j) != ','; j++)
		{
			if (line.charAt(j) == '\\')
			{
				j++;
				sb.append(line.charAt(j));
			}
			else if (line.charAt(j) == '<')
			{
				sb.append(line.charAt(j));
				for (j++; j < line.length() && line.charAt(j) != '>'; j++)
				{
					sb.append(line.charAt(j));
				}
				if (j == line.length())
				{
					return false;
				}
				sb.append(line.charAt(j));
			}
			else
			{
				sb.append(line.charAt(j));
			}
		}
		if (j == line.length())
		{
			lemma.argvalue = null;
			info.argvalue = line.substring(i + 1, i + 1 + j - i - 1).trim();
			return true;
		}
		lemma.argvalue = sb.toString();

		// get info
		info.argvalue = line.substring(j + 1);
		if (info.argvalue.equals(""))
		{
			return false;
		}
		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param features
	 * @return
	 */
	public static boolean parseDELAF(String line, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String> features)
	{
		ParameterCheck.mandatory("line", line);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		int i, j, k;
		entry.argvalue = lemma.argvalue = category.argvalue = features.argvalue = null;

		if (line.length() == 0)
		{
			return false;
		}
		line = cleanUpDoubleQuotes(line);
		StringBuilder sb = new StringBuilder();

		// get entry
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
			sb.append(line.charAt(i));
		}
		entry.argvalue = sb.toString();
		if (i == line.length())
		{			
			return true;
		}

		// get lemma
		sb = new StringBuilder();
		for (j = i + 1; j < line.length() && line.charAt(j) != ','; j++)
		{
			// I need to check if there is a "..." or "< ... >" because they could be in the information field
			if (line.charAt(j) == '\\')
			{
				j++;
			}
			else if (line.charAt(j) == '"')
			{
				for (j++; j < line.length() && line.charAt(j) != '"'; j++)
				{
					sb.append(line.charAt(j));
				}
				if (j == line.length())
				{
					// no closing " in lexeme, e.g. doen't,N+Dom="cou
					
					return false;
				}
				continue;
			}
			else if (line.charAt(j) == '<')
			{
				sb.append('<');
				for (j++; j < line.length() && line.charAt(j) != '>'; j++)
				{
					sb.append(line.charAt(j));
				}
				if (j == line.length())
				{
					// no closing ">" in lexeme, e.g. doen't,<do,V><not,ADV
					
					return false;
				}
				sb.append('>');
				continue;
			}
			sb.append(line.charAt(j));
		}
		if (j >= line.length())
		{
			// did not find any "," => no lemma: lexeme is like "often,ADV" => lemma = entry
			lemma.argvalue = entry.argvalue;
			j = i;
		}
		else
		{
			lemma.argvalue = sb.toString();
			if (lemma.argvalue.equals(""))
			{
				lemma.argvalue = entry.argvalue;
			}
		}

		// get category
		for (k = j + 1; k < line.length() && Character.isUpperCase(line.charAt(k)); k++)
		{
			;
		}
		category.argvalue = line.substring(j + 1, j + 1 + k - j - 1);
		if (category.argvalue.equals("") || category.argvalue == null)
		{
			
			return false;
		}

		// get features
		if (k == line.length())
		{
			features.argvalue = "";
		}
		else if (line.charAt(k) != '+' && line.charAt(k) != '=')
		{
			
			return false; // a valid lexeme would be <in gioco,XREF=1.2>
		}
		features.argvalue = line.substring(k);
		return true;
	}

	/**
	 * @param line
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param features
	 * @return
	 */
	public static boolean parseDELAFFeatureArray(String line, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String[]> features)
	{
		ParameterCheck.mandatory("features", features);

		String allFeatures = null;
		features.argvalue = null;

		RefObject<String> tempRef_allFeatures = new RefObject<String>(allFeatures);
		boolean tempVar = !parseDELAF(line, entry, lemma, category, tempRef_allFeatures);
		allFeatures = tempRef_allFeatures.argvalue;
		if (tempVar)
		{
			return false;
		}
		// get features
		features.argvalue = splitAllFeaturesWithPlus(allFeatures);
		return true;
	}

	/**
	 * @param lexeme
	 * @return
	 */
	public static boolean isALexemeSymbol(String lexeme)
	{
		ParameterCheck.mandatory("lexeme", lexeme);

		// eg "<table,N+plural>" or "<tables,table,N+plural>"
		if (lexeme.length() < 2 || lexeme.charAt(0) != '<' || lexeme.charAt(lexeme.length() - 1) != '>')
		{
			return false;
		}
		String entry = null, lemma = null, category = null, info = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String> tempRef_info = new RefObject<String>(info);
		boolean tempVar = Dic.parseDELAF(lexeme.substring(1, 1 + lexeme.length() - 2), tempRef_entry, tempRef_lemma,
				tempRef_category, tempRef_info);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		info = tempRef_info.argvalue;
		return tempVar;
	}

	/**
	 * 
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isALexicalAnnotation(String lexeme)
	{
		// eg "table,N+plural" or "tables,table,N+plural"
		String entry = null, lemma = null, info = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_info = new RefObject<String>(info);

		boolean tempVar = Dic.parseDELAF(lexeme, tempRef_entry, tempRef_lemma, tempRef_info);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		info = tempRef_info.argvalue;

		return tempVar;
	}

	/**
	 * Checks if given category is invalid (if there is a character which is not a letter).
	 * 
	 * @param cat
	 *            - string to be checked
	 * @return true if cat is invalid, false if it is valid
	 */
	private static boolean invalidCategory(String cat)
	{
		ParameterCheck.mandatory("cat", cat);

		for (int i = 0; i < cat.length(); i++)
		{
			if (cat.charAt(i) < 'A' || cat.charAt(i) > 'Z')
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Parses XML info.
	 * 
	 * @param info
	 *            - string to be parsed
	 * @param entry
	 * @param lemma
	 *            - lemma parsed from info
	 * @param category
	 *            - category parsed from info
	 * @param features
	 *            - features parsed from info
	 * @return true if category is returned, false otherwise
	 */
	static boolean parseXmlInfo(String info, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String> features)
	{
		if (info == null || info.length() == 0)
		{
			return false;
		}

		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		// info == "LEMMA=table+CAT=N+CASE=NOM+Conc+f+p" => lemma="table", category="N", features="CASE+NOM+Conc+f+p"
		entry.argvalue = lemma.argvalue = category.argvalue = features.argvalue = null;

		// get feats
		// TODO check if it's split or get
		
		String[] feats = getAllFeaturesWithoutPlus(info);
		for (int i = 0; i < feats.length; i++)
		{
			String feat = feats[i];
			if (feat.equals(""))
			{
				continue;
			}
			if (feat.length() >= LEMMA_PREFIX_LENGTH
					&& (feat.substring(0, LEMMA_PREFIX_LENGTH).equals("LEMMA") || feat
							.substring(0, LEMMA_PREFIX_LENGTH).equals("lemma")))
			{
				lemma.argvalue = feat.substring(LEMMA_PREFIX_LENGTH + 1);
				if (lemma.argvalue.length() == 0)
				{
					lemma.argvalue = "INVALIDLEMMA";
				}
				else
				{
					if (lemma.argvalue.charAt(0) == '"')
					{
						lemma.argvalue = lemma.argvalue.substring(1);
					}
					if (lemma.argvalue.charAt(lemma.argvalue.length() - 1) == '"')
					{
						lemma.argvalue = lemma.argvalue.substring(0, lemma.argvalue.length() - 1);
					}
				}
			}
			else if (feat.length() >= CAT_PREFIX_LENGTH
					&& (feat.substring(0, CAT_PREFIX_LENGTH).equals("CAT") || feat.substring(0, CAT_PREFIX_LENGTH)
							.equals("cat")))
			{
				category.argvalue = feat.substring(CAT_PREFIX_LENGTH + 1);
				if (invalidCategory(category.argvalue))
				{
					Dic.writeLog("Category '" + category.argvalue + "' is invalid");
					category.argvalue = "INVALIDCAT";
				}
				else
				{
					if (category.argvalue.charAt(0) == '"')
					{
						category.argvalue = category.argvalue.substring(1);
					}
					if (category.argvalue.charAt(category.argvalue.length() - 1) == '"')
					{
						category.argvalue = category.argvalue.substring(0, category.argvalue.length() - 1);
					}
				}
			}
			else if (feat.length() >= ENT_PREFIX_LENTH
					&& (feat.substring(0, ENT_PREFIX_LENTH).equals("ENT") || feat.substring(0, ENT_PREFIX_LENTH)
							.equals("ent")))
			{
				entry.argvalue = feat.substring((new String("ENT")).length() + 1);
				if (entry.argvalue.length() == 0)
				{
					entry.argvalue = "INVALIDENTRY";
				}
				else
				{
					if (entry.argvalue.charAt(0) == '"')
					{
						entry.argvalue = entry.argvalue.substring(1);
					}
					if (entry.argvalue.charAt(entry.argvalue.length() - 1) == '"')
					{
						entry.argvalue = entry.argvalue.substring(0, entry.argvalue.length() - 1);
					}
				}
			}
			else
			{
				if (features.argvalue == null)
				{
					features.argvalue = feat;
				}
				else
				{
					features.argvalue += "+" + feat;
				}
			}
		}
		return category.argvalue != null;
	}

	/**
	 * Cleans up given entry from commas, white spaces or ISO control characters, or angle brackets (if xmlText is
	 * true).
	 * 
	 * @param text
	 *            - entry to be cleaned
	 * @param xmlText
	 *            - flag that marks that given entry is XML text
	 * @return cleaned string
	 */
	public static String cleanupEntry(String text, boolean xmlText)
	{
		ParameterCheck.mandatory("text", text);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ',' || Character.isWhitespace(text.charAt(i))
					|| Character.isISOControl(text.charAt(i)))
			{
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
				{
					sb.append(' ');
				}
			}
			else if (xmlText && text.charAt(i) == '<')
			{
				int j = i + 1;
				for (; j < text.length() && text.charAt(j) != '>'; j++)
				{
					;
				}
				i = j;
			}
			else
			{
				sb.append(text.charAt(i));
			}
		}
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		while (sb.length() > 0 && sb.charAt(0) == ' ')
		{
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Cleans up given XML entry from commas, white spaces ISO control characters, or angle brackets.
	 * 
	 * @param text
	 *            - entry to be cleaned
	 * @return cleaned string
	 */
	static String cleanupXmlEntry(String text)
	{
		ParameterCheck.mandatory("text", text);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ',' || text.charAt(i) == '<' || text.charAt(i) == '>' || text.charAt(i) == '"'
					|| Character.isWhitespace(text.charAt(i)) || Character.isISOControl(text.charAt(i)))
			{
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
				{
					sb.append(' ');
				}
			}
			else
			{
				sb.append(text.charAt(i));
			}
		}
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		while (sb.length() > 0 && sb.charAt(0) == ' ')
		{
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Cleans up XML info (transforms lower case to upper case, replaces ' ' with '+'...).
	 * 
	 * @param info
	 *            - string to be cleaned up
	 * @param xmlCategory
	 *            - category to be set
	 * @return cleaned up string
	 */
	static String cleanupXmlInfo(String info, RefObject<String> xmlCategory)
	{
		ParameterCheck.mandatory("info", info);
		ParameterCheck.mandatory("xmlCategory", xmlCategory);

		xmlCategory.argvalue = null;
		StringBuilder sb = new StringBuilder();
		int i;

		// get XML tag category
		for (i = 0; i < info.length() && info.charAt(i) != ' '; i++)
		{
			if (info.charAt(i) >= 'A' && info.charAt(i) <= 'Z')
			{
				sb.append(info.charAt(i));
			}
			else if (info.charAt(i) >= 'a' && info.charAt(i) <= 'z')
			{
				sb.append(Character.toUpperCase(info.charAt(i)));
			}
			else
			{
				sb.append('X');
			}
		}
		xmlCategory.argvalue = sb.toString();

		// get information and replace " " with "+"
		sb = new StringBuilder();
		if (i < info.length())
		{
			for (; i < info.length(); i++)
			{
				if (info.charAt(i) == '"')
				{
					
					for (i++; i < info.length() && info.charAt(i) != '"'; i++)
					{
						sb.append(info.charAt(i));
					}
					
				}
				else if (info.charAt(i) == ' ')
				{
					sb.append("+");
					for (i++; i < info.length() && info.charAt(i) == ' '; i++)
					{
						;
					}
					i--;
				}
				else if (info.charAt(i) != '<' && info.charAt(i) != '>')
				{
					sb.append(info.charAt(i));
				}
			}
		}
		return sb.toString();
	}

	// Parse variable name and lexeme symbols and Lexical Constraint

	/**
	 * Returns full name of the variable.
	 * 
	 * @param text
	 *            - string to be checked for variable name
	 * @param currentPosition
	 *            - current position of '$'
	 * @param newPosition
	 *            - new position of '$'
	 * @return full name of the variable
	 */
	static String getFullVariableName(String text, int currentPosition, RefObject<Integer> newPosition)
	{
		ParameterCheck.mandatory("text", text);
		ParameterCheck.mandatory("newPosition", newPosition);

		// at currentposition is the "$"
		int i = currentPosition + 1;
		newPosition.argvalue = -1;
		int j;

		for (j = 0; i + j < text.length() && !Character.isWhitespace(text.charAt(i + j)) && text.charAt(i + j) != '#'
				&& text.charAt(i + j) != '=' && text.charAt(i + j) != ',' && text.charAt(i + j) != '>'
				&& text.charAt(i + j) != '}'; j++)
		{
			;
		}

		String varname = text.toString().substring(i, i + j);
		j += i;

		if (j < text.length() && text.charAt(j) == '#')
		{
			j++;
		}

		newPosition.argvalue = j;
		return varname;
	}

	/**
	 * Parses logical constraint.
	 * 
	 * @param symbol
	 *            - string to be parsed
	 * @param left
	 *            - left side of the symbol
	 * @param lemma
	 *            - lemma in the symbol
	 * @param category
	 *            - category in the symbol
	 * @param features
	 *            - features in the symbol
	 * @param op
	 *            - operator (if it exists in symbol)
	 * @param negation
	 *            - flag that marks whether symbol is a negation or not
	 * @return
	 */
	static boolean parseLexicalConstraint(String symbol, RefObject<String> left, RefObject<String> lemma,
			RefObject<String> category, RefObject<String[]> features, RefObject<String> op, RefObject<Boolean> negation)
	{
		// symbol = "<zz=V>" or <zz=have> or "<zz=have,V+Pr+Aux+3+s>" or "<zz=has,have,V+Pr+Aux+3+s>"
		// symbol = "<!zz=V>" or <!zz=have> or "<!zz=have,V+Pr+Aux+3+s>" or "<!zz=has,have,V+Pr+Aux+3+s>"

		ParameterCheck.mandatory("symbol", symbol);
		ParameterCheck.mandatory("left", left);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);
		ParameterCheck.mandatory("op", op);
		ParameterCheck.mandatory("negation", negation);

		left.argvalue = lemma.argvalue = category.argvalue = op.argvalue = null;
		features.argvalue = null;
		negation.argvalue = false;

		if (symbol.charAt(0) != '<')
		{
			return false;
		}

		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}

		String line;
		if (symbol.charAt(1) == '!')
		{
			line = symbol.substring(2, 2 + symbol.length() - 3);
			negation.argvalue = true;
		}
		else
		{
			line = symbol.substring(1, 1 + symbol.length() - 2);
		}

		if (line.length() == 0)
		{
			return false;
		}

		// get left side
		int i0;
		for (i0 = 0; i0 < line.length() && line.charAt(i0) != '='; i0++)
		{
			if (line.charAt(i0) == '\\')
			{
				i0++;
			}
		}

		if (i0 == line.length()) // no operator
		{
			return false;
		}

		// get the operator
		if (line.charAt(i0 + 1) != ':')
		{
			if (line.charAt(i0 + 1) == '!') // no negation after operator =
			{
				return false;
			}

			if (i0 > 0 && line.charAt(i0 - 1) == '!')
			{
				op.argvalue = "!=";
				left.argvalue = line.substring(0, i0 - 1);
				lemma.argvalue = line.substring(i0 + 1);
				return true;
			}
			else
			{
				op.argvalue = "=";
				left.argvalue = line.substring(0, i0);
				lemma.argvalue = line.substring(i0 + 1);
				return true;
			}
		}

		// a lexical constraint <xxx=:V+tr-PR>
		if (line.charAt(i0 + 1) == '!') // no negation!
		{
			return false;
		}

		op.argvalue = "=:";
		left.argvalue = line.substring(0, i0);
		line = line.substring(i0 + 2);

		int i, j, k;
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}

		if (i == line.length()) // no comma
		{
			// no comma : <xxx=avoir> or <xxx=:V-tr+Aux> or <xxx=:être+PR-p>
			boolean cat = true;
			int ii = 0;

			for (; ii < line.length() && line.charAt(ii) != '+' && line.charAt(ii) != '-'; ii++)
			{
				if (!Character.isUpperCase(line.charAt(ii)))
				{
					cat = false;
				}
			}

			if (ii == 0) // no category nor lemma <xxx=+hum>
			{
				return false;
			}

			if (cat == true) // <xxx=V> or <xxx=V+aux> or <xxx=N-Hum>
			{
				if (ii == line.length()) // <V>
				{
					category.argvalue = line;
					return true;
				}
				else
				{
					category.argvalue = line.substring(0, ii);
				}
			}
			else
			// <avoir> or <avoir+3+s> or <avoir-PR>
			{
				if (ii == line.length()) // <avoir>
				{
					lemma.argvalue = line;
					return true;
				}
				else
				{
					lemma.argvalue = line.substring(0, ii);
				}
			}

			if (line.charAt(ii) == '+' || line.charAt(ii) == '-')
			{
				features.argvalue = splitAllFeaturesWithPlusOrMinus(line.substring(ii));
			}

			return true;
		}

		// constraint with a comma
		// <xxx=:être,V+Aux> or <xxx=:être,to be,V+Aux+3+s>
		lemma.argvalue = line.substring(0, i);

		int lastcomma = i;
		for (j = i + 1; j < line.length(); j++)
		{
			if (line.charAt(j) == '\\')
			{
				j++;
				continue;
			}
			else if (line.charAt(j) == ',')
			{
				lastcomma = j;
			}
		}

		// get category
		for (k = lastcomma + 1; k < line.length() && Character.isUpperCase(line.charAt(k)); k++)
		{
			;
		}

		category.argvalue = line.substring(lastcomma + 1, lastcomma + 1 + k - lastcomma - 1);

		if (category.argvalue.equals("") || category.argvalue == null)
		{
			return false;
		}

		// get features
		String allfeatures = line.substring(k);
		if (allfeatures.equals(""))
		{
			return true;
		}
		if (allfeatures.charAt(0) == '+' || allfeatures.charAt(0) == '-')
		{
			features.argvalue = splitAllFeaturesWithPlusOrMinus(allfeatures);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Parses right side of a lexical constraint.
	 * 
	 * @param right
	 *            - string to be parsed
	 * @param lemma
	 *            - lemma parsed from the string
	 * @param category
	 *            - category parsed from the string
	 * @param features
	 *            - array of features (strings) parsed from the string
	 * @param negation
	 *            - flag that marks whether symbol is a negation or not
	 * @return true if given string is a lexical constraint, false otherwise
	 */
	static boolean parseLexicalConstraintRightSide(String right, RefObject<String> lemma, RefObject<String> category,
			RefObject<String[]> features, RefObject<Boolean> negation)
	{
		// symbol = "<zz=V>" or <zz=have> or "<zz=have,V+Pr+Aux+3+s>" or "<zz=has,have,V+Pr+Aux+3+s>"
		// symbol = "<!zz=V>" or <!zz=have> or "<!zz=have,V+Pr+Aux+3+s>" or "<zz=has,have,V+Pr+Aux+3+s>"

		ParameterCheck.mandatory("right", right);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);
		ParameterCheck.mandatory("negation", negation);

		lemma.argvalue = category.argvalue = null;
		features.argvalue = null;
		negation.argvalue = false;

		String line;
		if (right.charAt(0) == '!')
		{
			line = right.substring(1);
			negation.argvalue = true;
		}
		else
		{
			line = right;
		}

		if (line.length() == 0)
		{
			return false;
		}

		// a lexical constraint "V+tr-PR"
		int i, j, k;

		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}

		if (i == line.length()) // no comma
		{
			// no comma : <xxx=avoir> or <xxx=:V-tr+Aux> or <xxx=:être+PR-p>
			boolean cat = true;
			int ii = 0;

			for (; ii < line.length() && line.charAt(ii) != '+' && line.charAt(ii) != '-'; ii++)
			{
				if (!Character.isUpperCase(line.charAt(ii)))
				{
					cat = false;
				}
			}

			if (ii == 0) // no category nor lemma <xxx=+hum>
			{
				return false;
			}

			if (cat == true) // <xxx=:V> or <xxx=:V+aux> or <xxx=:N-Hum>
			{
				if (ii == line.length()) // <V>
				{
					category.argvalue = line;
					return true;
				}
				else
				{
					category.argvalue = line.substring(0, ii);
				}
			}
			else
			// <avoir> or <avoir+3+s> or <avoir-PR>
			{
				if (ii == line.length()) // <avoir>
				{
					lemma.argvalue = line;
					return true;
				}
				else
				{
					lemma.argvalue = line.substring(0, ii);
				}
			}

			if (line.charAt(ii) == '+' || line.charAt(ii) == '-')
			{
				features.argvalue = splitAllFeaturesWithPlusOrMinus(line.substring(ii));
			}

			return true;
		}

		// constraint with a comma
		// <xxx=:être,V+Aux> or <xxx=:être,to be,V+Aux+3+s>
		lemma.argvalue = line.substring(0, i);

		int lastcomma = i;
		for (j = i + 1; j < line.length(); j++)
		{
			if (line.charAt(j) == '\\')
			{
				j++;
				continue;
			}
			else if (line.charAt(j) == ',')
			{
				lastcomma = j;
			}
		}

		// get category
		for (k = lastcomma + 1; k < line.length() && Character.isUpperCase(line.charAt(k)); k++)
		{
			;
		}

		category.argvalue = line.substring(lastcomma + 1, lastcomma + 1 + k - lastcomma - 1);

		if (category.argvalue.equals("") || category.argvalue == null)
		{
			return false;
		}

		// get features
		String allFeatures = line.substring(k);
		if (allFeatures.equals(""))
		{
			return true;
		}

		if (allFeatures.charAt(0) == '+' || allFeatures.charAt(0) == '-')
		{
			features.argvalue = splitAllFeaturesWithPlusOrMinus(allFeatures);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param constraint
	 * @param left
	 * @param op
	 * @param right
	 * @return
	 */
	static boolean parseLexicalConstraint(String constraint, RefObject<String> left, RefObject<String> op,
			RefObject<String> right)
	{
		// symbol = "<zz=V>" or <zz=have> or "<zz=have,V+Pr+Aux+3+s>" or "<zz=has,have,V+Pr+Aux+3+s>"
		// symbol = "<!zz=V>" or <!zz=have> or "<!zz=have,V+Pr+Aux+3+s>" or "<zz=has,have,V+Pr+Aux+3+s>"

		ParameterCheck.mandatory("constraint", constraint);
		ParameterCheck.mandatory("left", left);
		ParameterCheck.mandatory("op", op);
		ParameterCheck.mandatory("right", right);

		left.argvalue = right.argvalue = op.argvalue = null;

		if (constraint.charAt(0) != '<')
		{
			return false;
		}

		if (constraint.charAt(constraint.length() - 1) != '>')
		{
			return false;
		}

		String line = constraint.substring(1, 1 + constraint.length() - 2);

		if (line.length() == 0)
		{
			return false;
		}

		// get left side
		int i0;
		for (i0 = 0; i0 < line.length() && line.charAt(i0) != '='; i0++)
		{
			if (line.charAt(i0) == '\\')
			{
				i0++;
			}
		}
		if (i0 == line.length()) // no operator
		{
			return false;
		}

		// get the operator
		if (line.charAt(i0 + 1) != ':')
		{
			if (line.charAt(i0 + 1) == '!') // no negation after operator =
			{
				return false;
			}
			if (i0 > 0 && line.charAt(i0 - 1) == '!')
			{
				op.argvalue = "!=";
				left.argvalue = line.substring(0, i0 - 1);
				right.argvalue = line.substring(i0 + 1);
				return true;
			}
			else
			{
				op.argvalue = "=";
				left.argvalue = line.substring(0, i0);
				right.argvalue = line.substring(i0 + 1);
				return true;
			}
		}

		// a lexical constraint <xxx=:V+tr-PR>
		if (line.charAt(i0 + 1) == '!') // no negation!
		{
			return false;
		}

		op.argvalue = "=:";
		left.argvalue = line.substring(0, i0);
		right.argvalue = line.substring(i0 + 2);

		return true;
	}

	/**
	 * @param symbol
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param features
	 * @param negation
	 * @return
	 */
	public static boolean parseSymbolFeatureArray(String symbol, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String[]> features, RefObject<Boolean> negation)
	{
		// symbol = "<V>" or <be> or "<be,V+PT+Aux+1+p>" or "<be,to be,ser,zzz,V+Fut+Aux+1+p>"

		ParameterCheck.mandatory("symbol", symbol);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);
		ParameterCheck.mandatory("negation", negation);

		entry.argvalue = lemma.argvalue = category.argvalue = null;
		features.argvalue = null;
		negation.argvalue = false;

		if (symbol.charAt(0) != '<')
		{
			return false;
		}
		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}

		String line;
		if (symbol.charAt(1) == '!')
		{
			line = symbol.substring(2, 2 + symbol.length() - 3);
			negation.argvalue = true;
		}
		else
		{
			line = symbol.substring(1, 1 + symbol.length() - 2);
		}
		if (line.length() == 0)
		{
			return false;
		}

		int i, j, k;

		// get entry
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}
		if (i == line.length()) // no comma
		{
			// no comma : <V+Aux> or <être+P> or "<pomme de terre>"
			boolean cat = true;
			int ii = 0;
			for (; ii < line.length() && line.charAt(ii) != '+' && line.charAt(ii) != '-'; ii++)
			{
				if (!Character.isUpperCase(line.charAt(ii)))
				{
					cat = false;
				}
			}
			if (cat == true) // <V> or <V+aux> or <N-Hum>
			{
				if (ii == line.length()) // <V>
				{
					category.argvalue = line;
					return true;
				}
				else
				{
					category.argvalue = line.substring(0, ii);
				}
			}
			else
			// <avoir> or <avoir+3+s> or <avoir-P>
			{
				if (ii == line.length()) // <avoir>
				{
					lemma.argvalue = line;
					return true;
				}
				else
				{
					lemma.argvalue = line.substring(0, ii);
				}
			}
			if (line.charAt(ii) == '+' || line.charAt(ii) == '-')
			{
				features.argvalue = splitAllFeaturesWithPlusOrMinus(line.substring(ii));
			}
			return true;
		}

		// <have,V+Aux> or <had,have,V+Aux+PRET>
		lemma.argvalue = line.substring(0, i);
		int follow = i + 1;

		for (j = 0; follow + j < line.length() && line.charAt(follow + j) != ','; j++)
		{
			if (line.charAt(follow + j) == '\\')
			{
				j++;
			}
		}

		if (follow + j < line.length() && line.charAt(follow + j) == ',')
		{
			// <had,have,V+Aux+PRET>
			entry.argvalue = lemma.argvalue;
			lemma.argvalue = line.substring(follow, follow + j);
			follow = i + j + 2;
		}

		// get category
		for (k = follow; k < line.length() && Character.isUpperCase(line.charAt(k)); k++)
		{
			;
		}
		category.argvalue = line.substring(follow, k);
		if (category.argvalue.equals("") || category.argvalue == null)
		{
			return false;
		}

		// get features
		String allfeatures = line.substring(k);
		if (allfeatures.equals(""))
		{
			return true;
		}
		if (allfeatures.charAt(0) == '+' || allfeatures.charAt(0) == '-')
		{
			features.argvalue = splitAllFeaturesWithPlusOrMinus(allfeatures);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param lexeme
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param features
	 * @return
	 */
	static boolean parseLexicalUnit(String lexeme, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String[]> features)
	{
		// lexeme = "<LU=helped,help,V+tr+FLX=HELP>"

		ParameterCheck.mandatory("lexeme", lexeme);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		entry.argvalue = lemma.argvalue = category.argvalue = null;
		features.argvalue = null;

		String line = lexeme.substring(4, 4 + lexeme.length() - 5);
		if (line.length() == 0)
		{
			return false;
		}

		// get entry
		int i0;
		for (i0 = 0; i0 < line.length() && line.charAt(i0) != ','; i0++)
		{
			if (line.charAt(i0) == '\\')
			{
				i0++;
			}
		}
		if (i0 == line.length()) // no comma?
		{
			return false;
		}

		// a lexical unit <LU=helped,help,V+tr>
		entry.argvalue = line.substring(0, i0);
		line = line.substring(i0 + 1);

		int i, k;
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}
		if (i == line.length()) // no comma?
		{
			return false;
		}
		lemma.argvalue = line.substring(0, i);

		// get category
		for (k = i + 1; k < line.length() && Character.isUpperCase(line.charAt(k)); k++)
		{
			;
		}
		category.argvalue = line.substring(i + 1, i + 1 + k - i - 1);
		if (category.argvalue.equals("") || category.argvalue == null)
		{
			return false;
		}

		// get features
		String allfeatures = line.substring(k);
		if (allfeatures.equals(""))
		{
			return true;
		}
		if (allfeatures.charAt(0) == '+')
		{
			features.argvalue = splitAllFeaturesWithPlus(allfeatures);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param symbol
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param features
	 * @return
	 */
	public static boolean parseLexemeSymbol(String symbol, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> category, RefObject<String[]> features)
	{
		// lexeme = "<helped,help,V+tr+FLX=HELP>"

		ParameterCheck.mandatory("symbol", symbol);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("category", category);
		ParameterCheck.mandatory("features", features);

		entry.argvalue = lemma.argvalue = category.argvalue = null;
		features.argvalue = null;

		if (symbol.charAt(0) != '<')
		{
			return false;
		}
		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}
		String line = symbol.substring(1, 1+symbol.length() - 2);
		if (line.length() == 0)
		{
			return false;
		}

		return Dic.parseDELAFFeatureArray(line, entry, lemma, category, features);
	}

	/**
	 * @param symbol
	 * @param entry
	 * @param lemma
	 * @param info
	 * @return
	 */
	public static boolean parseLexemeSymbol(String symbol, RefObject<String> entry, RefObject<String> lemma,
			RefObject<String> info)
	{
		// symbol = "<helped,help,V+tr+FLX=HELP>"

		ParameterCheck.mandatory("symbol", symbol);
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);
		ParameterCheck.mandatory("info", info);

		entry.argvalue = lemma.argvalue = info.argvalue = null;

		if (symbol.charAt(0) != '<')
		{
			return false;
		}
		if (symbol.charAt(symbol.length() - 1) != '>')
		{
			return false;
		}
		String line = symbol.substring(1, symbol.length() - 2);
		if (line.length() == 0)
		{
			return false;
		}

		return Dic.parseDELAF(line, entry, lemma, info);
	}

	// look for features in a lexical unit's information

	/**
	 * Checks whether feature contains property and value and sets them in propertyName and propertyValue if found.
	 * 
	 * @param feature
	 *            - string to be checked for property
	 * @param propertyName
	 *            - name of the found property
	 * @param propertyValue
	 *            - value of the found property
	 * @return true if property is found, false otherwise
	 */
	public static boolean getPropertyNameValue(String feature, RefObject<String> propertyName,
			RefObject<String> propertyValue)
	{
		ParameterCheck.mandatory("feature", feature);
		ParameterCheck.mandatory("propertyName", propertyName);
		ParameterCheck.mandatory("propertyValue", propertyValue);

		propertyName.argvalue = propertyValue.argvalue = feature;
		int index = feature.indexOf('=');
		if (index == -1)
		{
			return false;
		}

		propertyName.argvalue = feature.substring(0, index);
		propertyValue.argvalue = feature.substring(index + 1);
		if (propertyValue.argvalue != null && propertyValue.argvalue.length() > 2
				&& propertyValue.argvalue.charAt(0) == '"'
				&& propertyValue.argvalue.charAt(propertyValue.argvalue.length() - 1) == '"')
		{
			propertyValue.argvalue = propertyValue.argvalue.substring(1, 1 + propertyValue.argvalue.length() - 2);
		}
		return true;
	}

	/**
	 * Splits info (starting without '+') to array of strings without '+' between them.
	 * 
	 * @param info
	 *            - string to be split
	 * @return array of strings without '+'
	 */
	public static String[] getAllFeaturesWithoutPlus(String info)
	{
		// info == "N+Hum+f+p+Pol" => {"N","Hum","f","p","Pol"}

		ParameterCheck.mandatory("info", info);

		ArrayList<String> allfields = new ArrayList<String>();
		int i = 0;
		for (; i < info.length() && info.charAt(i) != '+'; i++)
		{
			;
		}
		if (i > 0) // add category
		{
			allfields.add(info.substring(0, i));
		}
		if (i < info.length())
		{
			String[] following = splitAllFeaturesWithPlus(info.substring(i));
			if (following != null)
			{
				allfields.addAll(Arrays.asList(following));
			}
		}

		return allfields.toArray(new String[allfields.size()]);
	}

	/**
	 * Sorts info (removes duplicates).
	 * 
	 * @param info
	 *            - string to be processed
	 * @return processed string
	 */
	public static String sortInfos(String info)
	{
		String[] fields = getAllFeaturesWithoutPlus(info);
		for (int i = 0; i < fields.length; i++)
		{
			if (fields[i] == null)
			{
				continue;
			}
			for (int j = i + 1; j < fields.length; j++)
			{
				if (fields[i].equals(fields[j]))
				{
					fields[j] = null;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String fd : fields)
		{
			if (fd != null)
			{
				sb.append("+" + fd);
			}
		}
		return sb.toString();
	}

	/**
	 * Splits allFeatures (starting with '+') to array of strings without '+' between them.
	 * 
	 * @param allFeatures
	 *            - string to be split
	 * @return array of strings without '+'
	 */
	private static String[] splitAllFeaturesWithPlus(String allFeatures)
	{
		// features="+Hum+s+p+Pol" => {"Hum","s","p","Pol"}

		if (allFeatures == null || allFeatures.equals(""))
		{
			return null;
		}

		if (allFeatures.charAt(0) != '+')
		{
			return null;
		}

		ArrayList<String> feats = new ArrayList<String>();
		int len;

		for (int i = 0; i < allFeatures.length();)
		{
			for (len = 1; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '+'; len++)
			{
				if (allFeatures.charAt(i + len) == '\\')
				{
					len++;
				}
				else if (allFeatures.charAt(i + len) == '"')
				{
					for (len++; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '"'; len++)
					{
						;
					}
				}
				else if (allFeatures.charAt(i + len) == '<')
				{
					for (len++; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '>'; len++)
					{
						;
					}
				}
			}

			if (i + len < allFeatures.length())
			{
				feats.add(allFeatures.substring(i + 1, i + 1 + len - 1)); // get rid of the "+"
				i += len;
			}
			else
			{
				feats.add(allFeatures.substring(i + 1));
				break;
			}
		}

		return feats.toArray(new String[feats.size()]);
	}

	/**
	 * Splits allFeatures (starting with '+' or '-') to array of strings.
	 * 
	 * @param allFeatures
	 *            - string to be split
	 * @return array of strings
	 */
	public static String[] splitAllFeaturesWithPlusOrMinus(String allFeatures)
	{
		// features="-Hum+s+p-Pol" => {"-Hum","+s","+p","-Pol"}

		if (allFeatures == null || allFeatures.equals(""))
		{
			return null;
		}

		if (allFeatures.charAt(0) != '+' && allFeatures.charAt(0) != '-')
		{
			return null;
		}

		ArrayList<String> feats = new ArrayList<String>();
		int len;

		for (int i = 0; i < allFeatures.length();)
		{
			for (len = 1; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '+'
					&& allFeatures.charAt(i + len) != '-'; len++)
			{
				if (allFeatures.charAt(i + len) == '\\')
				{
					len++;
				}
				else if (allFeatures.charAt(i + len) == '"')
				{
					for (len++; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '"'; len++)
					{
						;
					}
				}
				else if (allFeatures.charAt(i + len) == '<')
				{
					for (len++; i + len < allFeatures.length() && allFeatures.charAt(i + len) != '>'; len++)
					{
						;
					}
				}
			}

			if (i + len < allFeatures.length())
			{
				feats.add(allFeatures.substring(i, i + len));
				i += len;
			}
			else
			{
				feats.add(allFeatures.substring(i));
				break;
			}
		}

		return feats.toArray(new String[feats.size()]);
	}

	/**
	 * Gets rid of special features ("UNAMB", "FLX=", "DRV=", "COLOR=").
	 * 
	 * @param features
	 *            - array of features (strings) to be cleaned from special features
	 * @return resulting string (features prefixed with '+')
	 */
	public static String getRidOfSpecialFeatures(String[] features)
	{
		if (features == null || features.length == 0)
		{
			return "";
		}

		StringBuilder res = new StringBuilder();

		for (String feat : features)
		{
			if (feat.equals("UNAMB"))
			{
				;
			}
			else if (feat.length() > 4 && (feat.substring(0, 4).equals("FLX=") || feat.substring(0, 4).equals("DRV=")))
			{
				;
			}
			
			else if (feat.length() > 6 && feat.substring(0, 6).equals("COLOR="))
			{
				;
			}
			else
			{
				res.append("+" + feat);
			}
		}
		return res.toString();
	}

	/**
	 * Gets rid of special features ("UNAMB", "FLX=", "DRV=", "XREF=", "HIDDEN", "COLOR=").
	 * 
	 * @param features
	 *            - array of features (strings) to be cleaned from special features
	 * @return resulting string (features prefixed with '+')
	 */
	public static String getRidOfSpecialFeaturesPlus(String[] features)
	{
		if (features == null || features.length == 0)
		{
			return "";
		}

		StringBuilder res = new StringBuilder();

		for (String feat : features)
		{
			if (feat.equals("UNAMB"))
			{
				;
			}
			else if (feat.length() > 4 && (feat.substring(0, 4).equals("FLX=") || feat.substring(0, 4).equals("DRV=")))
			{
				;
			}
			else if (feat.length() > 5 && feat.substring(0, 5).equals("XREF="))
			{
				;
			}
			// "HIDDEN" needs to be followed with something in order for it to be removed from resulting string!
			else if (feat.length() > 6 && feat.substring(0, 6).equals("HIDDEN"))
			{
				;
			}
			else if (feat.length() > 6 && feat.substring(0, 6).equals("COLOR="))
			{
				;
			}
			else
			{
				res.append("+" + feat);
			}
		}
		return res.toString();
	}

	/**
	 * 
	 * 
	 * @param features
	 * @param inflectionalProperties
	 * @return
	 */
	static String[] getRidOfInflectionalFeatures(String[] features, HashMap<String, Boolean> inflectionalProperties)
	{
		if (features == null || features.length == 0)
		{
			return null;
		}

		ArrayList<String> myFeatures = new ArrayList<String>();

		for (String feat : features)
		{
			if (inflectionalProperties != null && inflectionalProperties.size() > 0)
			{
				// get property value
				int i = feat.indexOf('=');
				String value;

				if (i != -1)
				{
					value = feat.substring(i + 1);
				}
				else
				{
					value = feat;
				}

				if (!inflectionalProperties.containsKey(value))
				{
					myFeatures.add(feat);
				}
			}
			else
			{
				myFeatures.add(feat);
			}
		}

		if (myFeatures.isEmpty())
		{
			return null;
		}

		return myFeatures.toArray(new String[myFeatures.size()]);
	}

	/**
	 * @param lexeme
	 * @return
	 */
	static String getRidOfSpecialFeatures(String lexeme)
	{
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			return null;
		}
		String newinfos = Dic.getRidOfSpecialFeatures(features);
		return entry + "," + lemma + "," + category + newinfos;
	}

	/**
	 * Looks for string feature in string info.
	 * 
	 * @param feature0
	 *            - string to be looked for
	 * @param info
	 *            - string in which to be looked for
	 * @return - feature0 string - if found, empty string - otherwise
	 */
	private static String simpleLookFor(String feature0, String info)
	{
		// feature0 = "Hum", info = "N+Hum+m+p"
		// return "Hum" or null

		ParameterCheck.mandatory("feature0", feature0);
		ParameterCheck.mandatory("info", info);

		// Original:
		
		int i = info.indexOf("+" + feature0, 0);

		if (i == -1)
		{
			return null;
		}
		int j;
		for (j = 1, i++; i + j < info.length() && info.charAt(i + j) != '+'; j++)
		{
			;
		}
		return info.substring(i, i + j);
	}

	/**
	 * Looks for feature0 in the beginning of info.
	 * 
	 * @param feature0
	 *            - string to be looked for
	 * @param info
	 *            - string in which it is looked for
	 * @return beginning of info until '+' (if info is of type 'feature0=first_word+...') or null, if feature0 is not at
	 *         the beginning of info
	 */
	static String lookForAtBeg(String feature0, String info)
	{
		// feature0 = "XREF", info = "XREF=14+Hum+f"
		// return "XREF=14" or null

		ParameterCheck.mandatory("feature0", feature0);
		ParameterCheck.mandatory("info", info);

		// Original:
	
		int i = info.indexOf(feature0, 0);
		if (i != 0)
		{
			return null;
		}
		int j;
		for (j = 1; j < info.length() && info.charAt(j) != '+'; j++)
		{
			;
		}
		return info.substring(0, j);
	}

	/**
	 * Looks for feature0 in info, where info is complex.
	 * 
	 * @param feature0
	 *            - string to look for
	 * @param info
	 *            - string in which it is looked
	 * @return feature0 string - if found, empty string - otherwise
	 */
	private static String complexLookFor(String feature0, String info)
	{
		// feature0 = "Hum", info = "<friends=N>{friends,friend,N+Hum+p}"
		// => return "Hum" or null

		ParameterCheck.mandatory("info", info);

		int j;
		for (int i = 0; i < info.length();)
		{
			String simpleLex;
			if (info.charAt(i) == '<')
			{
				int recLevel = 0;
				for (j = 0; i + j < info.length(); j++)
				{
					if (info.charAt(i + j) == '<')
					{
						recLevel++;
					}
					else if (info.charAt(i + j) == '>')
					{
						recLevel--;
						if (recLevel <= 0)
						{
							break;
						}
					}
				}
				simpleLex = info.substring(i + 1, i + 1 + j - 1);
			}
			else if (info.charAt(i) == '{')
			{
				for (j = 0; i + j < info.length() && info.charAt(i + j) != '}'; j++)
				{
					;
				}
				simpleLex = info.substring(i + 1, i + 1 + j - 1);
			}
			else
			{
				return null;
			}
			String simpleEntry = null, simpleLemma = null, simpleInfo = null;
			if (simpleLex.startsWith("INFO"))
			{
				// {INFO+Hum+p}
				String looked = simpleLookFor(feature0, simpleLex);
				if (looked != null)
				{
					return looked;
				}
			}
			else
			{
				RefObject<String> tempRef_simpleentry = new RefObject<String>(simpleEntry);
				RefObject<String> tempRef_simplelemma = new RefObject<String>(simpleLemma);
				RefObject<String> tempRef_simpleinfo = new RefObject<String>(simpleInfo);
				boolean tempVar = Dic.parseDELAF(simpleLex, tempRef_simpleentry, tempRef_simplelemma,
						tempRef_simpleinfo);
				simpleEntry = tempRef_simpleentry.argvalue;
				simpleLemma = tempRef_simplelemma.argvalue;
				simpleInfo = tempRef_simpleinfo.argvalue;
				if (tempVar)
				{
					// {friends,friend,N+Hum+p}
					String looked = simpleLookFor(feature0, simpleInfo);
					if (looked != null)
					{
						return looked;
					}
				}
			}
			i += j + 1;
		}
		return null;
	}

	/**
	 * Looks for feature0 in info.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param info
	 *            - string in which it is searched
	 * @return - feature0 string - if found, empty string - otherwise
	 */
	public static String lookFor(String feature0, String info)
	{
		// feature0 = "FLX" info = "N+FLX+Salut"
		if (info == null || info.length() == 0)
		{
			return null;
		}
		if (info.charAt(0) == '<' || info.charAt(0) == '{')
		{
			return complexLookFor(feature0, info);
		}
		else
		{
			return simpleLookFor(feature0, info);
		}
	}

	/**
	 * Looks for feature0 in array of infos.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param infos
	 *            - array of strings in which it is searched
	 * @return string where feature0 is found - if found, null - otherwise
	 */
	static String lookFor(String feature0, String[] infos)
	{
		// feature0 = "FLX" infos = {"FLX=V7","Salut"}
		// returns "FLX=V7" or null
		ParameterCheck.mandatory("feature0", feature0);

		if (infos == null || infos.length == 0)
		{
			return null;
		}
		for (String info : infos)
		{
			if (info.length() < feature0.length())
			{
				continue;
			}
			int index = info.indexOf('=');
			if (index != -1)
			{
				if (feature0.equals(info.substring(0, index)))
				{
					return info;
				}
				else
				{
					continue;
				}
			}
			else
			{
				if (feature0.equals(info))
				{
					return info;
				}
				else
				{
					continue;
				}
			}
		}
		return null;
	}

	/**
	 * Looks for all appearances of feature0 in info.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param info
	 *            - string in which it is searched
	 * @return array of found strings - if found, null - otherwise
	 */
	private static String[] simpleLookForAll(String feature0, String info)
	{
		// feature0 = "DRV", info = "N+DRV=XxX+DRV=YYY+DRV=ZZZ+m+p"

		ParameterCheck.mandatory("feature0", feature0);
		ParameterCheck.mandatory("info", info);

		
	
		int i = info.indexOf("+" + feature0, 0);

		if (i == -1)
		{
			return null;
		}
		ArrayList<String> ares = new ArrayList<String>();
		while (i != -1)
		{
			int j;
			for (j = 1, i++; i + j < info.length() && info.charAt(i + j) != '+'; j++)
			{
				;
			}
			ares.add(info.substring(i, i + j));

			
		
			i = info.indexOf("+" + feature0, i + j);
		}

		return ares.toArray(new String[ares.size()]);
	}

	/**
	 * Looks for all appearances of feature0 in info, where info is complex.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param info
	 *            - string in which it is searched
	 * @return array of found strings - if found, null - otherwise
	 */
	private static String[] complexLookForAll(String feature0, String info)
	{
		// feature0 = "Hum", info = "<friends=N>{friends,friend,N+Hum+p}"
		// return

		ParameterCheck.mandatory("info", info);

		ArrayList<String> ares = new ArrayList<String>();
		int j;
		for (int i = 0; i < info.length();)
		{
			String simplelex;

			if (info.charAt(i) == '<') // <friend=N>
			{
				for (j = 0; i + j < info.length() && info.charAt(i + j) != '>'; j++)
				{
					;
				}
				simplelex = info.substring(i + 1, i + 1 + j - 2);
			}
			else if (info.charAt(i) == '{') // result: look for feature
			{
				for (j = 0; i + j < info.length() && info.charAt(i + j) != '}'; j++)
				{
					;
				}
				simplelex = info.substring(i + 1, i + 1 + j - 2);
			}
			else
			{
				return null;
			}
			if (simplelex.startsWith("INFO"))
			{
				// {INFO+Hum+p}
				String looked = simpleLookFor(feature0, simplelex);
				if (looked != null)
				{
					ares.add(looked);
				}
			}
			else if (simplelex.indexOf('=') == -1)
			{
				// {friends,friend,N+Hum+p}
				String simpleentry = null, simplelemma = null, simpleinfo = null;
				RefObject<String> tempRef_simpleentry = new RefObject<String>(simpleentry);
				RefObject<String> tempRef_simplelemma = new RefObject<String>(simplelemma);
				RefObject<String> tempRef_simpleinfo = new RefObject<String>(simpleinfo);
				boolean tempVar = Dic.parseDELAF(simplelex, tempRef_simpleentry, tempRef_simplelemma,
						tempRef_simpleinfo);
				simpleentry = tempRef_simpleentry.argvalue;
				simplelemma = tempRef_simplelemma.argvalue;
				simpleinfo = tempRef_simpleinfo.argvalue;
				if (tempVar)
				{
					String looked = simpleLookFor(feature0, simpleinfo);
					if (looked != null)
					{
						ares.add(looked);
					}
				}
			}
			i += j + 1;
		}
		if (ares.isEmpty())
		{
			return null;
		}

		return ares.toArray(new String[ares.size()]);
	}

	/**
	 * Looks for all appearances of feature0 in info.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param info
	 *            - string in which it is searched
	 * @return array of found strings - if found, null - otherwise
	 */
	static String[] lookForAll(String feature0, String info)
	{
		if (info == null || info.length() == 0)
		{
			return null;
		}
		if (info.charAt(0) == '<' || info.charAt(0) == '{')
		{
			return complexLookForAll(feature0, info);
		}
		else
		{
			return simpleLookForAll(feature0, info);
		}
	}

	/**
	 * Looks for all appearances of feature0 in array of strings (features), but only from the beginning of strings in
	 * features.
	 * 
	 * @param feature0
	 *            - string to be searched for
	 * @param features
	 *            - array of strings in which it is searched
	 * @return array of found strings - if found, null - otherwise
	 */
	static String[] lookForAll(String feature0, String[] features)
	{
		ParameterCheck.mandatory("feature0", feature0);

		if (features == null || features.length == 0)
		{
			return null;
		}
		ArrayList<String> resfeatures = new ArrayList<String>();
		for (String feat : features)
		{
			if (feat.length() > feature0.length() && feature0.equals(feat.substring(0, feature0.length())))
			{
				resfeatures.add(feat);
			}
		}
		if (resfeatures.isEmpty())
		{
			return null;
		}
		return resfeatures.toArray(new String[resfeatures.size()]);
	}

	/**
	 * Removes feature0 from info, together with appropriate '+' sign.
	 * 
	 * @param feature0
	 *            - string to be removed
	 * @param info
	 *            - string from which it is removed
	 * @return string after removal
	 */
	public static String removeFeature(String feature0, String info)
	{
		// feature0="Hum" info="N+Hum+s+f" => info="N+s+f"

		ParameterCheck.mandatory("feature0", feature0);

		if (info == null || info.length() == 0)
		{
			return null;
		}
		String[] infos = getAllFeaturesWithoutPlus(info);
		if (infos == null)
		{
			return null;
		}
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < infos.length; i++)
		{
			if (infos[i].length() >= feature0.length() && feature0.equals(infos[i].substring(0, feature0.length())))
			{
				continue;
			}
			if (info.charAt(0) == '+' || res.length() > 0)
			{
				res.append("+");
			}
			res.append(infos[i]);
		}
		return res.toString();
	}

	/**
	 * @param entry
	 * @param clemma
	 * @return
	 */
	static String unCompressSimpleLemma(String entry, String clemma)
	{
		if (clemma == null || clemma.equals(""))
		{
			return entry;
		}
		ParameterCheck.mandatory("entry", entry);

		if (clemma.charAt(0) == '0')
		{
			return unCompressSimpleLemma(entry.substring(clemma.charAt(1) - '0'), clemma.substring(2));
		}
		int i, val;
		// read the number
		for (i = val = 0; i < clemma.length() && Character.isDigit(clemma.charAt(i)); i++)
		{
			val = val * 10 + (clemma.charAt(i) - '0');
		}

		if (entry.length() < val)
		{
			return entry;
		}
		else
		{
			return entry.substring(0, entry.length() - val) + clemma.substring(i);
		}
	}

	/**
	 * @param entry
	 * @param clemma
	 * @return
	 */
	static String unCompressCompoundLemma(String entry, String clemma)
	{
		ParameterCheck.mandatory("clemma", clemma);

		if (clemma.equals(""))
		{
			return entry;
		}

		// compute lemma for compounds
		StringBuilder res = new StringBuilder();
		String[] entries = splitInSimpleWords(entry);
		String[] clemmas = splitInSimpleWords(clemma);
		int i;
		for (i = 0; i < entries.length && i < clemmas.length; i++)
		{
			if (Character.isLetter(entries[i].charAt(0)))
			{
				int ichar = 0;
				// get numerical value of clemmas[i]
				if (Character.isDigit(clemmas[i].charAt(0)))
				{
					for (ichar = 0; ichar < clemmas[i].length() && Character.isDigit(clemmas[i].charAt(ichar)); ichar++)
					{
						;
					}
					int val = Integer.parseInt(clemmas[i].substring(0, ichar));
					res.append(entries[i].substring(0, entries[i].length() - val));
				}
				else
				{
					res.append(entries[i]);
				}
				res.append(clemmas[i].substring(ichar));
			}
			else
			{
				res.append(Dic.protectComma(clemmas[i]));
			}
		}
		if (entries.length < clemmas.length)
		{
			for (; i < clemmas.length; i++)
			{
				res.append(Dic.protectComma(clemmas[i]));
			}
		}
		return res.toString();
	}

	/**
	 * Counts the commas in given line, without counting commas between double quotes and angle brackets.
	 * 
	 * @param line
	 *            - string for which the commas are counted
	 * @return number of commas in given line
	 */
	public static int nbOfCommas(String line)
	{
		ParameterCheck.mandatory("line", line);

		int nbofcommas = 0;
		boolean ininfo = false;
		for (int i = 0; i < line.length(); i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
				continue;
			}
			else if (ininfo && line.charAt(i) == '<')
			{
				// do not count the commas in info
				for (i++; i < line.length() && line.charAt(i) != '>'; i++)
				{
					;
				}
			}
			else if (ininfo && line.charAt(i) == '"')
			{
				// do not count the commas between double quotes
				for (i++; i < line.length() && line.charAt(i) != '"'; i++)
				{
					;
				}
			}
			else if (line.charAt(i) == '#')
			{
				return nbofcommas;
			}
			else if (line.charAt(i) == ',')
			{
				ininfo = true;
				nbofcommas++;
			}
		}
		return nbofcommas;
	}

	/**
	 * Splits text in simple words.
	 * 
	 * @param text
	 *            - text to be split
	 * @return array of simple words.
	 */
	private static String[] splitInSimpleWords(String text)
	{
		ParameterCheck.mandatory("text", text);

		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < text.length();)
		{
			int j;
			if (Character.isLetter(text.charAt(i)) || Character.isDigit(text.charAt(i)))
			{
				for (j = 1; i + j < text.length()
						&& (Character.isLetter(text.charAt(i + j)) || Character.isDigit(text.charAt(i + j))); j++)
				{
					;
				}
			}
			else
			{
				for (j = 1; i + j < text.length() && !Character.isLetter(text.charAt(i + j))
						&& !Character.isDigit(text.charAt(i + j)); j++)
				{
					;
				}
			}
			res.add(text.substring(i, i + j));
			i += j;
		}
		return res.toArray(new String[res.size()]);
	}

	/**
	 * @param entry
	 * @param lemma
	 * @return
	 */
	static String compressSimpleLemma(String entry, String lemma)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);

		if (entry.equals(lemma))
		{
			return "";
		}

		// compute command to get lemma from an inflected form (for simple words)
		int ichar;
		for (ichar = 0; ichar < entry.length() && ichar < lemma.length() && entry.charAt(ichar) == lemma.charAt(ichar); ichar++)
		{
			;
		}

		if (ichar == entry.length()) // entry is a prefix of lemma, e.g. "mange,manger" => "r"
		{
			return lemma.substring(ichar);
		}
		else if (ichar == lemma.length()) // lemma is a prefix of entry, e.g. "mangerons,manger" => "3"
		{
			return String.valueOf(entry.length() - ichar);
		}
		else if (ichar == 0) // entry and lemma differ from the beginning, e.g. "relevera,lever" => anchor position 2
								// "l"; => "<LW><B2><RW><B>" => "021"
		{
			// Slim a toi de jouer. Utilise une autre methode pour calculer l'affixe commun pour ne pas surcharger cette
			// fonction

			if (lemma.length() >= 2 && entry.length() >= 2)
			{
				int jchar;
				for (jchar = 1; jchar < 10 && jchar < entry.length() - 1
						&& (entry.charAt(jchar) != lemma.charAt(0) || entry.charAt(jchar + 1) != lemma.charAt(1)); jchar++)
				{
					;
				}
				if (jchar < 10 && jchar < entry.length())
				{
					return "0" + (new Integer(jchar)).toString() + compressSimpleLemma(entry.substring(jchar), lemma);
				}
			}
			return String.valueOf(entry.length() - ichar) + lemma.substring(ichar);
		}
		else
		// entry and lemma differ at i, e.g. "mangeons,manger" => "3r"
		{
			return String.valueOf(entry.length() - ichar) + lemma.substring(ichar);
		}
	}

	/**
	 * @param entry
	 * @param lemma
	 * @return
	 */
	static String compressCompoundLemma(String entry, String lemma)
	{
		ParameterCheck.mandatory("entry", entry);
		ParameterCheck.mandatory("lemma", lemma);

		if (entry.equals(lemma))
		{
			return "";
		}

		// compute lemma for compounds
		StringBuilder res = new StringBuilder();
		String[] entries = splitInSimpleWords(entry);
		String[] lemmas = splitInSimpleWords(lemma);
		int i;
		for (i = 0; i < entries.length && i < lemmas.length; i++)
		{
			if (Character.isLetter(entries[i].charAt(0)) && Character.isLetter(lemmas[i].charAt(0)))
			{
				if (entries[i].equals(lemmas[i]))
				{
					res.append("0");
				}
				else
				{
					int ichar;
					for (ichar = 0; ichar < entries[i].length() && ichar < lemmas[i].length(); ichar++)
					{
						if (entries[i].charAt(ichar) != lemmas[i].charAt(ichar))
						{
							break;
						}
					}
					if (ichar == entries[i].length())
					{
						res.append(lemmas[i].substring(ichar));
					}
					else if (ichar == lemmas[i].length())
					{
						res.append(String.valueOf(entries[i].length() - ichar));
					}
					else
					{
						res.append(String.valueOf(entries[i].length() - ichar) + lemmas[i].substring(ichar));
					}
				}
			}
			else if (i < lemmas.length)
			{
				for (int ii = 0; ii < lemmas[i].length(); ii++)
				{
					if (lemmas[i].charAt(ii) == ',')
					{
						res.append('\\');
					}
					res.append(lemmas[i].charAt(ii));
				}
			}
		}
		if (entries.length > lemmas.length)
		{
			// more entries than lemmas, e.g. "pomme de terre,patate,N" => "4atate"
			;
		}
		else if (lemmas.length > entries.length)
		{
			// more lemmas than entries, e.g. "patate,pomme de terre,N" => "5omme de terre"
			for (; i < lemmas.length; i++)
			{
				for (int ii = 0; ii < lemmas[i].length(); ii++)
				{
					if (lemmas[i].charAt(ii) == ',')
					{
						res.append('\\');
					}
					res.append(lemmas[i].charAt(ii));
				}
			}
		}
		return res.toString();
	}

	// Convert from INTEX DELA and Lexicon-Grammar tables

	/**
	 * @param line
	 * @param errMessage
	 * @return
	 */
	public static String[] convertFromDls(String line, RefObject<String> errMessage)
	{
		ParameterCheck.mandatory("line", line);
		ParameterCheck.mandatory("errMessage", errMessage);

		errMessage.argvalue = null;
		int i, j;
		String rline = null;
		String[] res = null;

		// delas entry
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}
		if (i >= line.length())
		{
			errMessage.argvalue = "cannot find comma in DLS line: " + line;
			Dic.writeLog(errMessage.argvalue);
			return null;
		}
		rline += line.substring(0, i);
		i++;

		// delas category
		for (j = 0; i + j < line.length() && line.charAt(i + j) != '.' && line.charAt(i + j) != ':'
				&& line.charAt(i + j) != ' ' && line.charAt(i + j) != '/'; j++)
		{
			if (line.charAt(i + j) == '\\')
			{
				j++;
			}
		}
		if (j == 0)
		{
			errMessage.argvalue = "cannot find category in DLS line: " + line;
			Dic.writeLog(errMessage.argvalue);
			return null;
		}
		if (i + j < line.length() && line.charAt(i + j) == ':')
		{
			errMessage.argvalue = "invalid character ':' in DLS line: " + line;
			Dic.writeLog(errMessage.argvalue);
			return null;
		}
		else if (i + j < line.length() && line.charAt(i + j) == '.')
		{
			errMessage.argvalue = "invalid character '.' in DLS line: " + line;
			Dic.writeLog(errMessage.argvalue);
			return null;
		}

		if (i + j == line.length())
		{
			rline += "," + line.substring(i, i + j);
			res = new String[1];
			res[0] = rline;
			return res;
		}
		else if (line.charAt(i + j) == ' ' || line.charAt(i + j) == '/')
		{
			rline += "," + line.substring(i, i + j) + '#' + line.substring(i + j + 1);
			res = new String[1];
			res[0] = rline;
			return res;
		}
		else
		{
			errMessage.argvalue = "unknown error";
			return null;
		}
	}

	/**
	 * @param line
	 * @param errMessage
	 * @return
	 */
	public static String[] convertFromDlf(String line, RefObject<String> errMessage)
	{
		ParameterCheck.mandatory("line", line);
		ParameterCheck.mandatory("errMessage", errMessage);

		errMessage.argvalue = null;
		int i, j, k;
		ArrayList<String> list = null;
		String rline = null;
		String[] res = null;

		// delaf entry
		for (i = 0; i < line.length() && line.charAt(i) != ','; i++)
		{
			if (line.charAt(i) == '\\')
			{
				i++;
			}
		}
		if (i >= line.length())
		{
			errMessage.argvalue = "no comma";
			return null;
		}
		rline += line.substring(0, i);
		i++;

		// delaf lemma
		for (j = 0; i + j < line.length() && line.charAt(i + j) != '.'; j++)
		{
			if (line.charAt(i + j) == '\\')
			{
				j++;
			}
		}
		if (i + j == line.length())
		{
			errMessage.argvalue = "no dot";
			return null;
		}
		rline += "," + line.substring(i, i + j);
		j++;

		// delaf syntactic category & semantic Features
		for (k = 0; i + j + k < line.length() && line.charAt(i + j + k) != ':' && line.charAt(i + j + k) != ' '
				&& line.charAt(i + j + k) != '/'; k++)
		{
			;
		}
		rline += "," + line.substring(i + j, i + j + k);
		if (i + j + k == line.length())
		{
			res = new String[1];
			res[0] = rline;
			return res;
		}
		if (line.charAt(i + j + k) != ':')
		{
			rline += '#' + line.substring(i + j + k + 1);
			res = new String[1];
			res[0] = rline;
			return res;
		}

		// now we have a ':' delaf inflectional info
		list = new ArrayList<String>();
		String rline0;
		while (i + j + k < line.length() && line.charAt(i + j + k) == ':')
		{
			rline0 = rline;
			for (k++; i + j + k < line.length() && line.charAt(i + j + k) != ':' && line.charAt(i + j + k) != ' '
					&& line.charAt(i + j + k) != '/'; k++)
			{
				rline0 += "+" + String.valueOf(line.charAt(i + j + k));
			}
			list.add(rline0);
		}
		return list.toArray(new String[list.size()]);
	}

	// Log

	public static String LogFileName; // Name of the log file

	/**
	 * Writes the given string to log file.
	 * 
	 * @param message
	 *            - string to be written to log file
	 */
	public static void writeLog(String message)
	{

		OutputStreamWriter sw = null;
		try
		{
			sw = new OutputStreamWriter(new FileOutputStream(LogFileName, true));
			sw.write(message);
			sw.write('\n'); // This is necessary, because StreamWriter.WriteLine terminates the line.
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (sw != null)
			{
				try
				{
					sw.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	/**
	 * Writes the given string to log file, with an empty line after it.
	 * 
	 * @param message
	 *            - string to be written to log file
	 */
	public static void writeLogInit(String message)
	{
		OutputStreamWriter sw = null;
		try
		{
			sw = new OutputStreamWriter(new FileOutputStream(LogFileName, false));
			sw.write(message);
			sw.write('\n'); // This is necessary, because StreamWriter.WriteLine terminates the line.
			sw.write("");
			sw.write('\n'); // This is necessary, because StreamWriter.WriteLine terminates the line.
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (sw != null)
			{
				try
				{
					sw.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
}