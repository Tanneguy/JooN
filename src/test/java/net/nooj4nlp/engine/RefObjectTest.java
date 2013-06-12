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

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
public class RefObjectTest
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
	 * Test method for {@link net.nooj4nlp.engine.RefObject#RefObject(java.lang.Object)}.
	 */
	@Test
	public void testRefObject()
	{
		RefObject<Double> rod = new RefObject<Double>(3.15);
		Assert.assertTrue(rod.argvalue.equals(3.15));

		RefObject<String> ros = new RefObject<String>("test string");
		Assert.assertEquals("test string", ros.argvalue);

		Date today = new Date();
		RefObject<Date> roda = new RefObject<Date>(today);
		Assert.assertEquals(today, roda.argvalue);
	}

}
