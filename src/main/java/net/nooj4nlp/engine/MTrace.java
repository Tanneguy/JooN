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

import java.util.ArrayList;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Holds the trace of the current morphological parsing, i.e. the path that the parser is already considering, with the
 * input it has visited and the output it is producing.
 * 
 * @author Silberztein Max
 */
public class MTrace
{
	int Statenb; // current state in Gram
	int Pos; // current position in word
	ArrayList<String> Inputs; // trace of the input
	ArrayList<String> Outputs; // trace of the output
	ArrayList<Object> Nodes; // trace of the graph nodes

	/**
	 * Default constructor - sets current state in Gram and current position in word to 0, and initializes Inputs,
	 * Outputs and Nodes to empty array lists.
	 */
	MTrace()
	{
		Statenb = 0;
		Pos = 0;
		Inputs = new ArrayList<String>();
		Outputs = new ArrayList<String>();
		Nodes = new ArrayList<Object>();
	}

	/**
	 * Constructor that sets current position to given position, and adds graph with the given name to list of nodes.
	 * 
	 * @param pos
	 *            - position to be set
	 * @param graphName
	 *            - name of the graph to be added to trace of nodes
	 */
	MTrace(int pos, String graphName)
	{
		ParameterCheck.mandatoryString("graphName", graphName);

		Statenb = 0;
		Pos = pos;
		Inputs = new ArrayList<String>();
		Outputs = new ArrayList<String>();
		Nodes = new ArrayList<Object>();
		Nodes.add(graphName);
	}
}