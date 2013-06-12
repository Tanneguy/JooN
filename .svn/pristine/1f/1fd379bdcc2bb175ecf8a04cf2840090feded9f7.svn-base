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

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Class that represents a state in a deterministic automaton.
 * 
 * @author Silberztein Max
 * 
 */
class DState
{
	public boolean canonical;
	public int infonb; // index of information (0 if none)
	public char[] chars;
	public int[] dests;
	public int[] parents;

	/**
	 * Sorts characters from lower to higher (and proper destinations as well).
	 */
	void sortCharsDests()
	{
		// bubble sort
		boolean modified = true;
		while (modified)
		{
			modified = false;
			for (int i = 0; i < chars.length - 1; i++)
			{
				if (chars[i] > chars[i + 1])
				{
					char tempChar = chars[i];
					chars[i] = chars[i + 1];
					chars[i + 1] = tempChar;
					int tempDest = dests[i];
					dests[i] = dests[i + 1];
					dests[i + 1] = tempDest;
					modified = true;
				}
			}
		}
	}

	/**
	 * Replaces destinationCharacter with newCharacter.
	 * 
	 * @param destinationCharacter
	 * @param newCharacter
	 */
	void replaceDest(int destinationCharacter, int newCharacter)
	{
		ParameterCheck.mandatory("destinationCharacter", destinationCharacter);
		ParameterCheck.mandatory("newCharacter", newCharacter);

		for (int i = 0; i < dests.length; i++)
		{
			if (dests[i] == destinationCharacter)
			{
				dests[i] = newCharacter;
			}
		}
	}

	/**
	 * Default constructor.
	 */
	DState()
	{
		infonb = 0;
		canonical = false;
		chars = null;
		dests = null;
		parents = null;
	}

	/**
	 * Constructor based on one parent.
	 * 
	 * @param parent
	 */
	DState(int parent)
	{
		infonb = 0;
		canonical = false;
		chars = null;
		dests = null;
		parents = new int[1];
		parents[0] = parent;
	}
}