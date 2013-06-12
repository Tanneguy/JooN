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

/**
 * Enumeration that represents type of gram.
 * 
 * @author Silberztein Max
 * 
 */
public enum GramType
{
	FLX, SYNTAX, MORPHO;

	/**
	 * Returns proper integer value for given enum.
	 * 
	 * @return
	 */
	public int getValue()
	{
		return this.ordinal();
	}

	/**
	 * Returns proper enum for given integer value.
	 * 
	 * @param value
	 * @return
	 */
	public static GramType forValue(int value)
	{
		return values()[value];
	}
}