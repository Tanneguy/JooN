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

/**
 * Helper class - serves for storing data (about characters, their frequencies and types) during analysis of text files
 * as well as corpus.
 * 
 * @author Silberztein Max
 * 
 */
public class Charlist implements Serializable
{
	private static final long serialVersionUID = 5894478664372879672L;

	ArrayList<Character> chars;
	ArrayList<Integer> freqs;
	ArrayList<String> types;

	/**
	 * Default constructor
	 */
	Charlist()
	{
		chars = new ArrayList<Character>();
		freqs = new ArrayList<Integer>();
		types = new ArrayList<String>();
	}

	public ArrayList<Character> getChars()
	{
		return chars;
	}

	public ArrayList<Integer> getFreqs()
	{
		return freqs;
	}

	public ArrayList<String> getTypes()
	{
		return types;
	}

}
