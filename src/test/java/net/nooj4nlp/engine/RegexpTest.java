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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author milos
 * 
 */
public class RegexpTest
{
	private String fileName = "src/test/resources/Regexp/regexp";

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
	 * Test method for {@link net.nooj4nlp.engine.Regexp#gIsWhiteSpace(char)}.
	 */
	@Test
	public void testGIsWhiteSpace()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Regexp#Regexp(net.nooj4nlp.engine.Language, java.lang.String, net.nooj4nlp.engine.GramType)}
	 * .
	 */
	@Test
	public void testRegexpLanguageStringGramType()
	{
		String string = " ";
		Regexp regexp = new Regexp(new Language(string), string, GramType.SYNTAX);
		Assert.assertNotNull(regexp);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Regexp#Regexp(java.lang.String)}.
	 */
	@Test
	public void testRegexpString()
	{
		String string = "";
		Regexp regexp = new Regexp(string);
		Assert.assertNotNull(regexp);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Regexp#Regexp(net.nooj4nlp.engine.Language, java.lang.String, java.lang.String, net.nooj4nlp.engine.GramType, java.util.ArrayList, java.util.HashMap)}
	 * .
	 */
	@Test
	public void testRegexpLanguageStringStringGramTypeArrayListOfStringHashMapOfStringInteger()
	{
		String string = " ";
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(string);
		Regexp regexp = new Regexp(new Language(string), string, string, GramType.SYNTAX, arrayList,
				new HashMap<String, Integer>());
		Assert.assertNotNull(regexp);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Regexp#load(java.lang.String)}.
	 */
	@Test
	public void testLoad()
	{
		Regexp load;
		try
		{
			load = Regexp.load(fileName);
			Assert.assertNotNull(load);
		}
		catch (IOException e)
		{
			Assert.assertFalse(true);
		}
		catch (ClassNotFoundException e)
		{
			Assert.assertFalse(true);
		}

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Regexp#save(java.lang.String)}.
	 */
	@Test
	public void testSave()
	{
		String string = "";
		Regexp regexp = new Regexp(string);
		try
		{
			regexp.save(fileName);
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertFalse(true);
		}
	}

}
