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
public class GramTypeTest
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
	 * Test method for {@link net.nooj4nlp.engine.GramType#getValue()}.
	 */
	@Test
	public void testGetValue()
	{
		GramType gt = GramType.FLX;
		Assert.assertEquals(0, gt.getValue());

		gt = GramType.SYNTAX;
		Assert.assertEquals(1, gt.getValue());

		gt = GramType.MORPHO;
		Assert.assertEquals(2, gt.getValue());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.GramType#forValue(int)}.
	 */
	@Test
	public void testForValue()
	{
		GramType gt = GramType.FLX;
		Assert.assertEquals(gt, GramType.forValue(0));

		gt = GramType.SYNTAX;
		Assert.assertEquals(gt, GramType.forValue(1));

		gt = GramType.MORPHO;
		Assert.assertEquals(gt, GramType.forValue(2));
	}

}
