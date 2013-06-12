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
 * @author IMPCS
 * 
 */
public class MatchTypeTest
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
	 * Test method for {@link net.nooj4nlp.engine.MatchType#getValue()}.
	 */
	@Test
	public void testGetValue()
	{
		MatchType mt = MatchType.ALL;
		Assert.assertEquals(0, mt.getValue());

		mt = MatchType.SHORTEST;
		Assert.assertEquals(1, mt.getValue());

		mt = MatchType.LONGEST;
		Assert.assertEquals(2, mt.getValue());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.MatchType#forValue(int)}.
	 */
	@Test
	public void testForValue()
	{
		MatchType mt = MatchType.ALL;
		Assert.assertEquals(mt, MatchType.forValue(0));

		mt = MatchType.SHORTEST;
		Assert.assertEquals(mt, MatchType.forValue(1));

		mt = MatchType.LONGEST;
		Assert.assertEquals(mt, MatchType.forValue(2));
	}

}
