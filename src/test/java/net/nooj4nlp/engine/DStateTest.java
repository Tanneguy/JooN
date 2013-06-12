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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class DStateTest
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
	 * Test method for {@link net.nooj4nlp.engine.DState#sortCharsDests()}.
	 */
	@Test
	public void testSortCharsDests()
	{
		DState dstate = new DState();

		dstate.chars = new char[5];
		dstate.chars[0] = 'f';
		dstate.chars[1] = 'a';
		dstate.chars[2] = 't';
		dstate.chars[3] = 'c';
		dstate.chars[4] = 'b';

		dstate.dests = new int[5];
		dstate.dests[0] = 3;
		dstate.dests[1] = 10;
		dstate.dests[2] = 5;
		dstate.dests[3] = 2;
		dstate.dests[4] = 1;

		dstate.sortCharsDests();

		Assert.assertEquals('a', dstate.chars[0]);
		Assert.assertEquals('t', dstate.chars[4]);
		Assert.assertEquals(10, dstate.dests[0]);
		Assert.assertEquals(5, dstate.dests[4]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.DState#replaceDest(int, int)}.
	 */
	@Test
	public void testReplaceDest()
	{
		DState dstate = new DState();

		dstate.dests = new int[5];
		dstate.dests[0] = 3;
		dstate.dests[1] = 10;
		dstate.dests[2] = 5;
		dstate.dests[3] = 2;
		dstate.dests[4] = 1;

		dstate.replaceDest(10, 15);

		Assert.assertEquals(15, dstate.dests[1]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.DState#DState()}.
	 */
	@Test
	public void testDState()
	{
		DState dstate = new DState();

		Assert.assertNotNull(dstate);
		Assert.assertEquals(0, dstate.infonb);
		Assert.assertFalse(dstate.canonical);
		Assert.assertNull(dstate.chars);
		Assert.assertNull(dstate.dests);
		Assert.assertNull(dstate.parents);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.DState#DState(int)}.
	 */
	@Test
	public void testDStateInt()
	{
		DState dstate = new DState(15);

		Assert.assertNotNull(dstate);
		Assert.assertEquals(0, dstate.infonb);
		Assert.assertFalse(dstate.canonical);
		Assert.assertNull(dstate.chars);
		Assert.assertNull(dstate.dests);

		Assert.assertNotNull(dstate.parents);
		Assert.assertEquals(15, dstate.parents[0]);
	}

}
