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
public class STraceTest
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
	 * Test method for {@link net.nooj4nlp.engine.STrace#STrace()}.
	 */
	@Test
	public void testSTrace()
	{
		STrace st = new STrace();

		Assert.assertEquals(0.0, st.Pos, 0);

		Assert.assertEquals(0, st.Statenb);
		Assert.assertEquals(0, st.Inputs.size());
		Assert.assertEquals(0, st.Variables.size());
		Assert.assertEquals(0, st.Outputs.size());
		Assert.assertEquals(0, st.Nodes.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.STrace#STrace(double pos)}.
	 */
	@Test
	public void testSTraceDouble()
	{
		STrace st = new STrace(2.0);

		Assert.assertNotSame(0.0, st.Pos);
		Assert.assertEquals(2.0, st.Pos, 0.1);

		Assert.assertEquals(0, st.Statenb);
		Assert.assertEquals(0, st.Inputs.size());
		Assert.assertEquals(0, st.Variables.size());
		Assert.assertEquals(0, st.Outputs.size());
		Assert.assertEquals(0, st.Nodes.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.STrace#STrace(double pos, String graphName)}.
	 */
	@Test
	public void testSTraceDoubleString()
	{
		STrace st = new STrace(2.0, "testNode");

		Assert.assertNotSame(0, st.Pos);
		Assert.assertEquals(0, st.Statenb);

		Assert.assertEquals(1, st.Inputs.size());
		Assert.assertEquals(1, st.Variables.size());
		Assert.assertEquals(1, st.Outputs.size());

		Assert.assertNotSame(0, st.Nodes.size());
		Assert.assertEquals("testNode", st.Nodes.get(0));
	}

}
