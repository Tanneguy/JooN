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
 * @author Natalija
 * 
 */
public class IndexkeyTest
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
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#Indexkey()}.
	 */
	@Test
	public void testIndexkey()
	{
		Indexkey indexKey = new Indexkey();

		Assert.assertNotNull(indexKey.addresses);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#Indexkey(int, int, int)}.
	 */
	@Test
	public void testIndexkeyIntIntInt()
	{
		Indexkey ik = new Indexkey(1, 20, 30);

		Assert.assertEquals(1, ik.tokenId);
		Assert.assertEquals(20, ik.addresses.get(0));
		Assert.assertEquals(30, ik.addresses.get(1));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#Indexkey(int)}.
	 */
	@Test
	public void testIndexkeyInt()
	{
		Indexkey ik = new Indexkey(2);

		Assert.assertEquals(2, ik.tokenId);
		Assert.assertNotNull(ik.addresses);
		Assert.assertTrue(ik.addresses.isEmpty());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#Indexkey(int, int)}.
	 */
	@Test
	public void testIndexkeyIntInt()
	{
		Indexkey ik = new Indexkey(20, 30);

		Assert.assertEquals(0, ik.tokenId);
		Assert.assertEquals(20, ik.addresses.get(0));
		Assert.assertEquals(30, ik.addresses.get(1));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#copy()}.
	 */
	@Test
	public void testCopy()
	{
		Indexkey original = new Indexkey(1, 20, 30);
		Indexkey copy = original.copy();

		Assert.assertEquals(copy.tokenId, original.tokenId);
		Assert.assertEquals(copy.addresses.get(0), original.addresses.get(0));
		Assert.assertEquals(copy.addresses.get(1), original.addresses.get(1));

		Assert.assertEquals(copy.addresses, original.addresses);

		// copy.addresses.add(40);
		// copy.addresses.add(50);
		// Assert.assertEquals(copy.addresses, original.addresses);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#addOccurrence(int, int)}.
	 */
	@Test
	public void testAddOccurrence()
	{
		Indexkey ik = new Indexkey(1, 10, 20);

		ik.addOccurrence(30, 40);

		Assert.assertEquals(10, ik.addresses.get(0));
		Assert.assertEquals(20, ik.addresses.get(1));
		Assert.assertEquals(30, ik.addresses.get(2));
		Assert.assertEquals(40, ik.addresses.get(3));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Indexkey#addOccurrenceNoDup(int, int)}.
	 */
	@Test
	public void testAddOccurrenceNoDup()
	{
		Indexkey ik = new Indexkey(1, 10, 20);

		ik.addOccurrenceNoDup(30, 40);
		ik.addOccurrenceNoDup(10, 20);

		Assert.assertEquals(4, ik.addresses.size());
	}

}
