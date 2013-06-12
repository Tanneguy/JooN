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
 * Helper class - used for keeping id of token and list of addresses (beginning and ending addresses) where the token
 * can be found at.
 * 
 * @author Silberztein Max
 */
public class Indexkey implements Serializable
{
	private static final long serialVersionUID = -7635435434180069063L;

	// ORIGINAL LINE: internal uint tokenId; - tokenId needs to be long and not int type!
	public ArrayList<Integer> addresses; // each token has a list of beginning and ending addresses

	/**
	 * Default constructor
	 */
	Indexkey() // for tokens
	{
		this.addresses = new ArrayList<Integer>();
	}
}