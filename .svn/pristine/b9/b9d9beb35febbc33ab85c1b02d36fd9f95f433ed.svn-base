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
public class MTraceTest
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
	 * Test method for {@link net.nooj4nlp.engine.MTrace#MTrace()}.
	 */
	@Test
	public void testMTrace()
	{
		MTrace mt = new MTrace();

		Assert.assertEquals(0, mt.Pos);
		Assert.assertEquals(0, mt.Statenb);
		Assert.assertEquals(0, mt.Inputs.size());
		Assert.assertEquals(0, mt.Outputs.size());
		Assert.assertEquals(0, mt.Nodes.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.MTrace#MTrace(int pos)}.
	 */
	@Test
	public void testMTraceInt()
	{
		MTrace mt = new MTrace(2);

		Assert.assertNotSame(0, mt.Pos);

		Assert.assertEquals(0, mt.Statenb);
		Assert.assertEquals(0, mt.Inputs.size());
		Assert.assertEquals(0, mt.Outputs.size());
		Assert.assertEquals(0, mt.Nodes.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.MTrace#MTrace(int pos, String graphName)}.
	 */
	@Test
	public void testMTraceIntString()
	{
		MTrace mt = new MTrace(2, "testNode");

		Assert.assertNotSame(0, mt.Pos);

		Assert.assertEquals(0, mt.Statenb);
		Assert.assertEquals(0, mt.Inputs.size());
		Assert.assertEquals(0, mt.Outputs.size());

		Assert.assertNotSame(0, mt.Nodes.size());
		Assert.assertEquals("testNode", mt.Nodes.get(0));
	}

}
