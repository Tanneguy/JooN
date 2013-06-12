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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
public class CharlistTest
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Charlist#Charlist()}
	 */
	@Test
	public void test()
	{
		// Creation of new object
		Charlist charlist = new Charlist();

		// Initialization of charlist's arraylists
		charlist.chars.add('a');
		charlist.chars.add('b');
		charlist.freqs.add(10);
		charlist.freqs.add(5);

		Assert.assertEquals(2, charlist.chars.size());
		Assert.assertEquals(2, charlist.freqs.size());
		Assert.assertEquals(0, charlist.types.size());
	}

}
