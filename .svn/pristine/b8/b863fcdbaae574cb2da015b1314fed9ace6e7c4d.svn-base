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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class TheSolutionsTest
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
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#TheSolutions()}.
	 */
	@Test
	public void testTheSolutions()
	{
		TheSolutions s = new TheSolutions();

		Assert.assertNotNull(s.list);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.TheSolutions#addASolution(int, double, double, java.util.ArrayList, java.util.ArrayList)}
	 * .
	 */
	@Test
	public void testAddASolution()
	{
		TheSolutions s = new TheSolutions();

		Assert.assertTrue(s.list.isEmpty());

		int tuNb = 2;
		double begAddress = 0.02;
		int length = 10;
		ArrayList input = new ArrayList();
		ArrayList output = new ArrayList();
		s.addASolution(tuNb, begAddress, length, input, output);

		Assert.assertFalse(s.list.isEmpty());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#getTunb(int)}.
	 */
	@Test
	public void testGetTunb()
	{
		TheSolutions s = createCoupleOfSolutions();

		Assert.assertEquals(5, s.getTuNb(0));
		Assert.assertEquals(10, s.getTuNb(1));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#getBegAddress(int)}.
	 */
	@Test
	public void testGetBegAddress()
	{
		TheSolutions s = createCoupleOfSolutions();

		Assert.assertEquals(0.02, s.getBegAddress(0), 0);
		Assert.assertEquals(10.03, s.getBegAddress(1), 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#getLength(int)}.
	 */
	@Test
	public void testGetLength()
	{
		TheSolutions s = createCoupleOfSolutions();

		Assert.assertEquals(10, s.getLength(0), 0);
		Assert.assertEquals(15, s.getLength(1), 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#getInput(int)}.
	 */
	@Test
	public void testGetInput()
	{
		TheSolutions s = createCoupleOfSolutions();

		Assert.assertTrue(s.getInput(0).size() == 2);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TheSolutions#getOutput(int)}.
	 */
	@Test
	public void testGetOutput()
	{
		TheSolutions s = createCoupleOfSolutions();

		Assert.assertTrue(s.getOutput(0).size() == 2);
	}

	private TheSolutions createCoupleOfSolutions()
	{
		TheSolutions s = new TheSolutions();

		int tuNb = 5;
		double begAddress = 0.02;
		int length = 10;
		ArrayList input = new ArrayList();
		input.add(55);
		input.add(110);
		ArrayList output = new ArrayList();
		output.add(44);
		output.add(88);
		s.addASolution(tuNb, begAddress, length, input, output);
		s.addASolution(10, 10.03, 15, input, output);

		return s;
	}

}
