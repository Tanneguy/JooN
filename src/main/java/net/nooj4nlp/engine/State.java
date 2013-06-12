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

/**
 * Holds the state in the grammar that the parser is currently in while parsing the text.
 * 
 * @author Silberztein Max
 */
public class State implements Serializable
{
	private static final long serialVersionUID = 6602692226545485530L;

	ArrayList<Integer> Dests;
	ArrayList<Integer> IdLabels;
	
	transient int GraphNodeNumber;
	transient HashMap<Integer, ArrayList<Integer>> AllIdLabels; // entries of type (idLabel, destinations)

	/**
	 * Default constructor.
	 */
	State()
	{
		Dests = new ArrayList<Integer>();
		IdLabels = new ArrayList<Integer>();
		AllIdLabels = new HashMap<Integer, ArrayList<Integer>>();
		
		GraphNodeNumber = -1;
	}

	/**
	 * Function that adds a transition given with pair (destination, idLabel) to this state.
	 * 
	 * @param dest
	 *            - destination of transition to be added
	 * @param idLabel
	 *            - id label of transition to be added
	 */
	final void addTrans(int dest, int idLabel)
	{
		this.Dests.add(dest);
		this.IdLabels.add(idLabel);

		if (this.AllIdLabels.containsKey(idLabel))
		{
			ArrayList<Integer> listofdests = this.AllIdLabels.get(idLabel);
			listofdests.add(dest);
		}
		else
		{
			ArrayList<Integer> listofdests = new ArrayList<Integer>();
			listofdests.add(dest);
			this.AllIdLabels.put(idLabel, listofdests);
		}
	}
}