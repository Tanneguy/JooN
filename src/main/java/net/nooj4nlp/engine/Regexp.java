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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Class that works with regular expressions
 * 
 * @author Silberztein Max
 * 
 */
public class Regexp implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7660746903663279727L;
	private Language Lan;
	private GramType Type; // MORPHO, FLX or SYNTAX
	private String Text; // expression to be parsed

	transient private int Position;
	transient private char LookAhead;

	/**
	 * method checks whether character c matches that on <code>Position</code> in <code>Text</code>. If matched it
	 * increments Position and updates LookAhead.
	 * 
	 * @param character
	 *            to match against Text at Position
	 * @return true if character matches and false otherwise
	 */
	private boolean match(char character)
	{
		ParameterCheck.mandatory("character", character);
		if (Position < Text.length() && Text.charAt(Position) == character)
		{
			Position++;
			if (Position == Text.length())
			{
				LookAhead = '\0';
			}
			else
			{
				LookAhead = Text.charAt(Position);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * method checks whether character is white space or bracket.
	 * 
	 * @param character
	 *            to check
	 * @return true if character is white space or bracket, false otherwise
	 */
	private boolean delim(char character)
	{
		ParameterCheck.mandatory("character", character);

		if (Character.isWhitespace(character))
		{
			return true;
		}
		if (character == '(')
		{
			return true;
		}
		if (character == ')')
		{
			return true;
		}
		return false;
	}

	/**
	 * parses regular expression returning Tree object
	 * 
	 * @return Tree object
	 */
	private Tree parseExp()
	{
		Tree tree1, tree2, resultTree;

		while (Character.isWhitespace(LookAhead))
		{
			match(LookAhead);
		}
		tree1 = parseTerm();
		while (Character.isWhitespace(LookAhead))
		{
			match(LookAhead);
		}
		if (Position == Text.length())
		{
			return tree1;
		}

		if (LookAhead == '+' | LookAhead == '|')
		{
			match(LookAhead);
			while (Character.isWhitespace(LookAhead))
			{
				match(LookAhead);
			}
			tree2 = parseExp();
			resultTree = Tree.binaryTree(tree1, tree2, "+");
			return resultTree;
		}
		else
		{
			return tree1;
		}
	}

	/**
	 * returns Tree object
	 * 
	 * @return Tree object
	 */
	private Tree parseTerm()
	{
		Tree tree1, tree2, resultTree;

		tree1 = parseSubTerm();
		while (Character.isWhitespace(LookAhead))
		{
			match(LookAhead);
		}
		if (Position == Text.length())
		{
			return tree1;
		}

		if (LookAhead != ')' && LookAhead != '+' && LookAhead != '|')
		{
			tree2 = parseTerm();
			resultTree = Tree.binaryTree(tree1, tree2, ".");
			return resultTree;
		}
		else
		{
			return tree1;
		}
	}

	/**
	 * returns Tree object
	 * 
	 * @return Tree object
	 */
	private Tree parseSubTerm()
	{
		Tree tree1, resultTree;

		tree1 = parseFactor();
		if (LookAhead == '*')
		{
			match('*');
			resultTree = Tree.binaryTree(tree1, null, "*");
			return resultTree;
		}
		else
		{
			return tree1;
		}
	}

	/**
	 * 
	 * @param stringBuilder
	 */
	private void processOutput(StringBuilder stringBuilder)
	{

		ParameterCheck.mandatory("stringBuilder", stringBuilder);
		if (LookAhead == '"')
		{
		
			match(LookAhead);
			while (LookAhead != '\0' && LookAhead != '"')
			{
				stringBuilder.append(LookAhead);
				match(LookAhead);
			}
			
			match(LookAhead);
		}
		else
		{
			for (; LookAhead != '\0' && !delim(LookAhead) && LookAhead != '|';)
			{
				stringBuilder.append(LookAhead);
				match(LookAhead);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private Tree parseFactor()
	{
		Tree tree;
		StringBuilder stringBuilder;

		switch (LookAhead)
		{
			case '(':
				match('(');
				tree = parseExp();
				match(')');
				break;
			case '<':
				stringBuilder = new StringBuilder();
				for (; LookAhead != '>';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				match(LookAhead);
				stringBuilder.append('>');
				if (LookAhead == '/')
				{
					match(LookAhead);
					stringBuilder.append('/');
					processOutput(stringBuilder);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case '"':
				stringBuilder = new StringBuilder();
				stringBuilder.append('"');
				match(LookAhead);
				for (; LookAhead != '\0' && LookAhead != '"';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				match(LookAhead);
				stringBuilder.append('"');
				if (LookAhead == '/')
				{
					match(LookAhead);
					stringBuilder.append('/');
					processOutput(stringBuilder);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case ':':
				stringBuilder = new StringBuilder();
				stringBuilder.append(':');
				match(LookAhead);
				for (; LookAhead != '\0' && !delim(LookAhead) && LookAhead != '|';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				if (LookAhead == '/')
				{
					match(LookAhead);
					stringBuilder.append('/');
					processOutput(stringBuilder);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case '$':
				stringBuilder = new StringBuilder();
				stringBuilder.append('$');
				match(LookAhead);
				if (LookAhead == '(' || LookAhead == ')')
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
					for (; LookAhead != '\0' && !delim(LookAhead) && LookAhead != '|';)
					{
						stringBuilder.append(LookAhead);
						match(LookAhead);
					}
				}
				else
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
					for (; LookAhead != '\0' && !delim(LookAhead) && LookAhead != '|';)
					{
						stringBuilder.append(LookAhead);
						match(LookAhead);
					}
				}
				if (LookAhead == '/')
				{
					match(LookAhead);
					stringBuilder.append('/');
					processOutput(stringBuilder);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			default:
				stringBuilder = new StringBuilder();
				if (Type == GramType.MORPHO)
				{
					if (LookAhead == '\\')
					{
						stringBuilder.append(LookAhead);
						match(LookAhead);
					}
					stringBuilder.append(LookAhead);
					match(LookAhead);
					if (LookAhead == '/')
					{
						match(LookAhead);
						stringBuilder.append('/');
						processOutput(stringBuilder);
					}
				}
				else
				// syntax
				{
					if (Language.isLetter(LookAhead) && !Lan.asianTokenizer)
					{
						stringBuilder.append(LookAhead);
						match(LookAhead);
						while (Language.isLetter(LookAhead))
						{
							stringBuilder.append(LookAhead);
							match(LookAhead);
						}
					}
					else
					// delimiter
					{
						if (LookAhead == '\\')
						{
							stringBuilder.append(LookAhead);
							match(LookAhead);
						}
						stringBuilder.append(LookAhead);
						match(LookAhead);
					}
					if (LookAhead == '/')
					{
						match(LookAhead);
						stringBuilder.append('/');
						processOutput(stringBuilder);
					}
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
		}
		return tree;
	}

	/**
	 * checks whether this character is whitespace (but not new line character)
	 * 
	 * @param character
	 *            to check
	 * @return true if its whitespace and not new line character, false otherwise.
	 */
	private static boolean gIsWhiteSpace(char character)
	{
		ParameterCheck.mandatory("character", character);
		return Character.isWhitespace(character) && (character != '\n');
	}

	/**
	 * 
	 * @return
	 */
	private Tree gParseExp()
	{
		Tree tree1, tree2, resultTree;

		while (gIsWhiteSpace(LookAhead))
		{
			match(LookAhead);
		}
		tree1 = gParseTerm();
		while (gIsWhiteSpace(LookAhead))
		{
			match(LookAhead);
		}
		if (Position == Text.length())
		{
			return tree1;
		}

		if (LookAhead == '\n')
		{
			match('\n');
			while (gIsWhiteSpace(LookAhead))
			{
				match(LookAhead);
			}
			tree2 = gParseExp();
			resultTree = Tree.binaryTree(tree1, tree2, "+");
			return resultTree;
		}
		else
		{
			return tree1;
		}
	}

	/**
	 * 
	 * @return
	 */
	private Tree gParseTerm()
	{
		Tree tree1, tree2, resultTree;

		tree1 = gParseFactor();
		while (gIsWhiteSpace(LookAhead))
		{
			match(LookAhead);
		}
		if (Position == Text.length())
		{
			return tree1;
		}

		if (LookAhead != '\n')
		{
			tree2 = gParseTerm();
			resultTree = Tree.binaryTree(tree1, tree2, ".");
			return resultTree;
		}
		else
		{
			return tree1;
		}
	}

	/**
	 * 
	 * @return
	 */
	private Tree gParseFactor()
	{
		Tree tree;
		StringBuilder stringBuilder;

		switch (LookAhead)
		{
			case '<':
				stringBuilder = new StringBuilder();
				for (; LookAhead != '>';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				match(LookAhead);
				stringBuilder.append('>');
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case '\\':
				stringBuilder = new StringBuilder();
				stringBuilder.append(LookAhead);
				match(LookAhead);
				stringBuilder.append(LookAhead);
				match(LookAhead);
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case '"':
				stringBuilder = new StringBuilder();
				stringBuilder.append('"');
				match(LookAhead);
				for (; LookAhead != '"';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				match(LookAhead);
				stringBuilder.append('"');
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case ':':
				stringBuilder = new StringBuilder();
				stringBuilder.append(':');
				match(LookAhead);
				for (; LookAhead != '\0' && LookAhead != '\n';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			case '$':
				stringBuilder = new StringBuilder();
				stringBuilder.append('$');
				match(LookAhead);
				for (; LookAhead != '\0' && LookAhead != '\n';)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
			default:
				stringBuilder = new StringBuilder();
				if (Type == GramType.MORPHO)
				{
					stringBuilder.append(LookAhead);
					match(LookAhead);
				}
				else
				// syntax
				{
					if (Language.isLetter(LookAhead) && !Lan.asianTokenizer)
					{
						stringBuilder.append(LookAhead);
						match(LookAhead);
						while (Language.isLetter(LookAhead))
						{
							stringBuilder.append(LookAhead);
							match(LookAhead);
						}
					}
					else
					// delimiter
					{
						if (LookAhead == '\\')
						{
							stringBuilder.append(LookAhead);
							match(LookAhead);
						}
						stringBuilder.append(LookAhead);
						match(LookAhead);
					}
				}
				tree = Tree.leafTree(stringBuilder.toString());
				break;
		}
		return tree;
	}

	public transient Gram Grm; // resulting Gram

	/**
	 * constructs new regexp object
	 * 
	 * @param language
	 * @param regularExpression
	 * @param gramType
	 */
	public Regexp(Language language, String regularExpression, GramType gramType) // a
	// regular
	// expression
	{
		ParameterCheck.mandatoryString("regularExpression", regularExpression);

		Lan = language;
		Text = regularExpression;
		Type = gramType;
		Position = 0;
		if (Text.equals(""))
		{
			
			return;
		}
		LookAhead = Text.charAt(0);
		while (Character.isWhitespace(LookAhead))
		{
			match(LookAhead);
		}
		Tree T = parseExp();
		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add("<E>");
		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put("<E>", 0);
		Grm = T.explore(aVocab, hVocab);
		Grm.vocab = aVocab;
		hVocab = null;
		Text = null;
	}

	/**
	 * constructs new Regexp object
	 * 
	 * @param inflectionalExpression
	 */
	public Regexp(String inflectionalExpression) // an inflectional expression
	{
		try
		{
			Lan = new Language("en");
			if (Lan == null)
			{
				System.out.println("Cannot construct language for isoname = en");
			}
		}
		catch (RuntimeException e)
		{
			System.out.println("Cannot construct language: " + e.getMessage());
		}
		if (Lan != null)
		{
			Type = GramType.FLX;
			Text = inflectionalExpression;
			Position = 0;
			if (Text.length() > 0)
			{
				LookAhead = Text.charAt(0);
				while (Character.isWhitespace(LookAhead))
				{
					match(LookAhead);
				}
				Tree T = parseExp();
				ArrayList<String> aVocab = new ArrayList<String>();
				aVocab.add("<E>");
				HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
				hVocab.put("<E>", 0);
				Grm = T.explore(aVocab, hVocab);
				Grm.vocab = aVocab;
				hVocab = null;
				Text = null;
			}
			else
			{
				Grm = null;
			}
		}
	}

	/**
	 * constructs new Regexp object
	 * 
	 * @param language
	 * @param labelIn
	 * @param labelOut
	 * @param gramType
	 * @param aVocab
	 * @param hVocab
	 */
	Regexp(Language language, String labelIn, String labelOut, GramType gramType, ArrayList<String> aVocab,
			HashMap<String, Integer> hVocab) // a graph node label
	{
		ParameterCheck.mandatoryString("labelIn", labelIn);
		
		ParameterCheck.mandatory("aVocab", aVocab);
		ParameterCheck.mandatory("hVocab", hVocab);

		Lan = language;
		Text = labelIn;
		Type = gramType;
		Position = 0;
		LookAhead = Text.charAt(0);
		while (gIsWhiteSpace(LookAhead))
		{
			match(LookAhead);
		}
		Tree T = gParseExp();
		Grm = T.explore(labelOut, aVocab, hVocab);
		Grm.vocab = aVocab;
		hVocab = null;
		Text = null;
	}
}