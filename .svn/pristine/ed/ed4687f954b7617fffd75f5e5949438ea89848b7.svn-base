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

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class GrammarFirstTest
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
	 * Test method for {@link net.nooj4nlp.engine.GrammarFirst#GrammarFirst(boolean)}.
	 */
	@Test
	public void testGrammarFirst()
	{
		GrammarFirst gf = new GrammarFirst(true);

		Assert.assertNotNull(gf);
		Assert.assertNull(gf.first);
		Assert.assertFalse(gf.first_HasBeg);
		Assert.assertTrue(gf.nullable);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.GrammarFirst#match(java.lang.String, boolean)}.
	 */
	@Test
	public void testMatch()
	{
		GrammarFirst gf = new GrammarFirst(true);

		String token = "SEARCH";

		try
		{
			// gf.first is null -> NullPointerException
			gf.match(token, false);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		gf.first = new HashMap<String, String>();
		gf.first.put("SEARCH", "true");

		Assert.assertTrue(gf.match(token, false));

		token = "FOUND";

		Assert.assertFalse(gf.match(token, false));

		gf.first_HasSpecial = true;
		gf.first_HasUpp = true;

		Assert.assertTrue(gf.match(token, true));
	}
}
