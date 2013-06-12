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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * 
 * @author Silberztein Max
 * 
 */
public class Regexps implements Serializable
{
	private static final String MAIN = "Main";

	private static final long serialVersionUID = -6677839946698256614L;
	public Grammar grammar;

	/**
	 * creates new Regexps object
	 * 
	 * @param ilanguage
	 * @param gramType
	 * @param gram
	 * @param engine
	 */
	public Regexps(Language ilanguage, GramType gramType, Gram gram, Engine engine)
	{
		grammar = new Grammar(ilanguage, gramType, engine);
		grammar.grams = new HashMap<String, Gram>();
		if (gramType != GramType.FLX)
		{
			grammar.grams.put(MAIN, gram);
		}
	}

	/**
	 * 
	 * @param line
	 * @param ibuffer
	 * @param rname
	 * @param expression
	 * @return
	 */
	private static int getRule(String line, int ibuffer, StringBuilder rname, RefObject<String> expression)
	{
		ParameterCheck.mandatoryString("line", line);
		ParameterCheck.mandatory("rname", rname);
		ParameterCheck.mandatory("expression", expression);

		int i, j, k, m;

		rname.delete(0, rname.length());
		expression.argvalue = null;

		i = ibuffer;
		// skip white spaces and comments
		boolean skipped = true;
		while (skipped)
		{
			skipped = false;
			if (Character.isWhitespace(line.charAt(i)))
			{
				skipped = true;
				for (i++; i < line.length() && Character.isWhitespace(line.charAt(i)); i++)
				{
					;
				}
				if (i == line.length()) // no more rule
				{
					return -1;
				}
			}
			if (line.charAt(i) == '#')
			{
				skipped = true;
				for (i++; i < line.length() && line.charAt(i) != '\n'; i++)
				{
					;
				}
				if (i == line.length()) // no more rule
				{
					return -1;
				}
			}
		}
		// get rname
		for (j = 0; i + j < line.length() && line.charAt(i + j) != '=' && !Character.isWhitespace(line.charAt(i + j)); j++)
		{
			;
		}
		if (i + j == line.length()) // problem: a rule name alone
		{
			return -2;
		}
		rname.append(line.substring(i, i + j));

		// get "="
		for (k = 0; i + j + k < line.length() && line.charAt(i + j + k) != '='
				&& Character.isWhitespace(line.charAt(i + j + k)); k++)
		{
			;
		}
		if (i + j + k == line.length() || line.charAt(i + j + k) != '=') // problem:
																			// no
																			// "="
		{
			return -2;
		}

		// get ";"
		for (m = 1; i + j + k + m < line.length() && line.charAt(i + j + k + m) != ';'; m++)
		{
			if (line.charAt(i + j + k + m) == '"')
			{
				for (m++; line.charAt(i + j + k + m) != '"'; m++)
				{
					;
				}
			}
		}
		if (i + j + k + m == line.length() || line.charAt(i + j + k + m) != ';') // problem:
																					// no
																					// ending
																					// ";"
		{
			return -2;
		}

		// get expression
		expression.argvalue = line.substring(i + j + k + 1, i + j + k + m);

		return i + j + k + m + 1;
	}

	/**
	 * Removes comments from inputBuffer
	 * 
	 * @param inputBuffer
	 * @param ilanguageName
	 * @param olanguageName
	 * @param gramType
	 * @return
	 */
	private static String removeComments(String inputBuffer, RefObject<String> ilanguageName,
			RefObject<String> olanguageName, RefObject<GramType> gramType)
	{
		ParameterCheck.mandatoryString("inputBuffer", inputBuffer);
		ParameterCheck.mandatory("ilanguageName", ilanguageName);
		ParameterCheck.mandatory("olanguageName", olanguageName);
		ParameterCheck.mandatory("gramType", gramType);

		ilanguageName.argvalue = null;
		olanguageName.argvalue = null;
		gramType.argvalue = GramType.FLX;
		StringBuilder buffer = new StringBuilder();
		boolean begofline = true;
		for (int i = 0; i < inputBuffer.length(); i++)
		{
			if (inputBuffer.charAt(i) == '"')
			{
				buffer.append(inputBuffer.charAt(i));
				i++;
				while (i < inputBuffer.length() && inputBuffer.charAt(i) != '"')
				{
					buffer.append(inputBuffer.charAt(i));
					i++;
				}
				buffer.append(inputBuffer.charAt(i));
				begofline = false;
				i++;
			}
			else if (inputBuffer.charAt(i) == '\\')
			{
				i++;
				begofline = false;
			}
			else if (inputBuffer.charAt(i) == '#' && begofline)
			{
				int j;
				for (j = 0; i + j < inputBuffer.length() && inputBuffer.charAt(i + j) != '\n'; j++)
				{
					;
				}
				begofline = true;
				String commentline = inputBuffer.substring(i, i + j);
				if (commentline.equals("# Syntactic grammar"))
				{
					gramType.argvalue = GramType.SYNTAX;
				}
				else if (commentline.equals("# Morphological grammar"))
				{
					gramType.argvalue = GramType.MORPHO;
				}

				if (commentline.length() > 15)
				{
					String pref = commentline.substring(0, 15);
					if (pref.equals("# Language is: "))
					{
						ilanguageName.argvalue = commentline.substring(15);
					}
				}
				if (commentline.length() > 21)
				{
					String pref = commentline.substring(0, 21);
					if (pref.equals("# Input Language is: "))
					{
						ilanguageName.argvalue = commentline.substring(21);
					}
				}
				if (commentline.length() > 22)
				{
					String pref = commentline.substring(0, 22);
					if (pref.equals("# Output Language is: "))
					{
						olanguageName.argvalue = commentline.substring(22);
					}
				}
				i += j;
			}
			if (i < inputBuffer.length())
			{
				buffer.append(inputBuffer.charAt(i));
				if (inputBuffer.charAt(i) == '\n' || inputBuffer.charAt(i) == ';')
				{
					begofline = true;
				}
				else
				{
					begofline = false;
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * method loads <code>Regexps</code> object from a given <code>fileName</code>
	 * 
	 * @param fileName
	 * @param errorMessage
	 * @param ilanguageName
	 * @param olanguageName
	 * @return
	 */
	static Regexps load(String fileName, RefObject<String> errorMessage, RefObject<String> ilanguageName,
			RefObject<String> olanguageName)
	{
		ParameterCheck.mandatoryString("fileName", fileName);
		ParameterCheck.mandatory("errorMessage", errorMessage);
		ParameterCheck.mandatory("ilanguageName", ilanguageName);
		ParameterCheck.mandatory("olanguageName", olanguageName);

		errorMessage.argvalue = null;
		ilanguageName.argvalue = null;
		olanguageName.argvalue = null;

		Regexps regexps = null;
		BufferedReader bufferedReader = null;
		try
		{
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));

			String header = bufferedReader.readLine();
			if (!header.equals("# NooJ V1") && !header.equals("# NooJ V2") && !header.equals("# NooJ V3")&& !header.equals("# NooJ V4"))
			{
				errorMessage.argvalue = "Header not compatible";
				Dic.writeLog(errorMessage.argvalue);
				return null;
			}

			// load the file
			// ATTENTION: cannot use sr.ReadLine because sometimes, a few \x0d are weirdly inserted in text. I Need to
			// correct that: This implementation is not the same as c# code because java doesn't have readToEnd
			String rawBuffer = "";
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				rawBuffer += line;
				// going like this line after line removes all \n from the original text which is weird and should not
				// happen
				rawBuffer += "\n";
			}
			RefObject<GramType> gramType = new RefObject<GramType>(GramType.FLX);
			String buf = removeComments(rawBuffer, ilanguageName, olanguageName, gramType);
			Language ilanguage = new Language(ilanguageName.argvalue);
			
			regexps = new Regexps(null, GramType.FLX, null, null);
			int end;
			for (int iBuffer = 0; iBuffer < buf.length(); iBuffer = end)
			{
				StringBuilder rulename = new StringBuilder();
				RefObject<String> exp = new RefObject<String>(null);
				end = getRule(buf, iBuffer, rulename, exp);
				if (end == -1) // no more rule
				{
					break;
				}
				else if (end == -2)
				{
					// syntax error in regular expression
					errorMessage.argvalue = "Cannot parse rule:" + buf.substring(iBuffer, iBuffer + 15);
					Dic.writeLog(errorMessage.argvalue);
					return null;
				}
		
				if (!regexps.grammar.grams.containsKey(rulename))
				{
					Regexp re = null;
					re = new Regexp(ilanguage, exp.argvalue, gramType.argvalue);
					
					regexps.grammar.grams.put(rulename.toString(), re.Grm);
					
				}
			}
		}
		catch (IOException e)
		{
			errorMessage.argvalue = e.getMessage();
			Dic.writeLog(errorMessage.argvalue);
			return null;
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException e)
				{
					System.out.println("Error while closing " + fileName);
				}
			}
		}
		return regexps;
	}
}