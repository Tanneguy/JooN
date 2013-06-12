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

/**
 * Class that contains all the data that represents the list of all the matches, as well as their corresponding output.
 * The output might be structured (in the case of syntactic parsing).
 * 
 * @author Silberztein Max
 * 
 */
public class TheSolutions
{
	/**
	 * Helper class - data for the match.
	 * 
	 * @author Silberztein Max
	 * 
	 */
	private static class ASolution
	{
		public int tuNb;
		public double begAddress;
		public double length;
		public ArrayList<Double> input;
		public ArrayList<String> output;

		/**
		 * Constructor.
		 * 
		 * @param tuNb
		 * @param begAddress
		 * @param length
		 * @param input
		 * @param output
		 */
		public ASolution(int tuNb, double begAddress, double length, ArrayList<Double> input, ArrayList<String> output)
		{
			this.tuNb = tuNb;
			this.begAddress = begAddress;
			this.length = length;
			this.input = new ArrayList<Double>(input);
			this.output = new ArrayList<String>(output);
		}
	}

	public ArrayList<ASolution> list;

	/**
	 * Default constructor.
	 */
	public TheSolutions()
	{
		this.list = new ArrayList<ASolution>();
	}

	/**
	 * Creates another object (ASolution) based on given parameters and adds it to list.
	 * 
	 * @param tuNb
	 * @param begAddress
	 * @param length
	 * @param input
	 * @param output
	 */
	public final void addASolution(int tuNb, double begAddress, double length, ArrayList<Double> input,
			ArrayList<String> output)
	{
		this.list.add(new ASolution(tuNb, begAddress, length, input, output));
	}

	/**
	 * Gets an order number of text unit for solution with given order number.
	 * 
	 * @param isol
	 *            - order number of solution in the list
	 * @return order number of text unit
	 */
	public final int getTuNb(int isol)
	{
		ASolution asol = this.list.get(isol);
		return asol.tuNb;
	}

	/**
	 * Gets a beginning address for solution with given order number.
	 * 
	 * @param isol
	 *            - order number of solution in the list
	 * @return beginning address
	 */
	public final double getBegAddress(int isol)
	{
		ASolution asol = this.list.get(isol);
		return asol.begAddress;
	}

	/**
	 * Gets a length of text unit for solution with given order number.
	 * 
	 * @param isol
	 *            - order number of solution in the list
	 * @return order number of text unit
	 */
	public final double getLength(int isol)
	{
		ASolution asol = this.list.get(isol);
		return asol.length;
	}

	/**
	 * Gets an array list of inputs for ASolution with given order number.
	 * 
	 * @param isol
	 *            - order number of the solution
	 * @return array list of inputs
	 */
	public final ArrayList<Double> getInput(int isol)
	{
		ASolution asol = this.list.get(isol);
		return asol.input;
	}

	/**
	 * Gets an array list of outputs for ASolution with given order number.
	 * 
	 * @param isol
	 *            - order number of the solution
	 * @return array list of outputs
	 */
	public final ArrayList<String> getOutput(int isol)
	{
		ASolution asol = this.list.get(isol);
		return asol.output;
	}
}