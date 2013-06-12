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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author milos
 * 
 */
public class RegexpsTest
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
	 * Test method for
	 * {@link net.nooj4nlp.engine.Regexps#Regexps(net.nooj4nlp.engine.Language, net.nooj4nlp.engine.GramType, net.nooj4nlp.engine.Gram, net.nooj4nlp.engine.Engine)}
	 * .
	 */
	@Test
	public void testRegexps()
	{
		Language ilanguage = new Language(" ");
		Regexps regexps = new Regexps(ilanguage, GramType.MORPHO, new Gram(), new Engine(ilanguage));
		Assert.assertNotNull(regexps);
		regexps = new Regexps(ilanguage, GramType.FLX, new Gram(), new Engine(ilanguage));
		Assert.assertNotNull(regexps);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Regexps#removeComments(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testRemoveComments()
	{
		String inputBuffer = "\\ \n gfcjhgfchgcf \n# Syntactic grammar\n \n\\ \n gfcjhgfchgcf \n# Language is: ar\n \n# Output Language is: estonian";
		RefObject<String> ilanguageName = new RefObject<String>(null);
		RefObject<String> olanguageName = new RefObject<String>(null);
		RefObject<GramType> gramType = new RefObject<GramType>(null);
		String removeComments = Regexps.removeComments(inputBuffer, ilanguageName, olanguageName, gramType);
		Assert.assertNotNull(removeComments);
		Assert.assertNotNull(ilanguageName.argvalue);
		Assert.assertEquals(ilanguageName.argvalue, "ar");
		Assert.assertNotNull(olanguageName.argvalue);
		Assert.assertEquals(olanguageName.argvalue, "estonian");
		Assert.assertNotNull(gramType.argvalue);
		Assert.assertEquals(gramType.argvalue, GramType.SYNTAX);
		inputBuffer = "\\ \n gfcjhgfchgcf \n# Morphological grammar\n \n\\ \n gfcjhgfchgcf \n# Input Language is: estonian";
		removeComments = Regexps.removeComments(inputBuffer, ilanguageName, olanguageName, gramType);
		Assert.assertNotNull(removeComments);
		Assert.assertNotNull(ilanguageName.argvalue);
		Assert.assertEquals(ilanguageName.argvalue, "estonian");
		Assert.assertNotNull(gramType.argvalue);
		Assert.assertEquals(gramType.argvalue, GramType.MORPHO);
		inputBuffer = "\\ \n gfcjhgfchgcf \\ \n gfcjhgfchgcf \n# Input Language is: estonian";
		removeComments = Regexps.removeComments(inputBuffer, ilanguageName, olanguageName, gramType);
		Assert.assertNotNull(removeComments);
		Assert.assertNotNull(gramType.argvalue);
		Assert.assertEquals(gramType.argvalue, GramType.FLX);
		System.out.println(removeComments);

	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Regexps#load(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testLoad()
	{
		String inputBuffer = "# NooJ V1\n# Syntactic grammar\n# Language is: ar\n \n# Output Language is: estonian";

		String fileName = "src/test/resources/Regexps/regexps";
		FileOutputStream out;
		try
		{
			out = new FileOutputStream(new File(fileName));

			OutputStreamWriter osw = new OutputStreamWriter(out);
			osw.write(inputBuffer);
			osw.close();
		}
		catch (FileNotFoundException e)
		{
			Assert.assertFalse(true);
		}
		catch (IOException e)
		{
			Assert.assertFalse(true);
		}
		RefObject<String> ilanguageName = new RefObject<String>(null);
		RefObject<String> olanguageName = new RefObject<String>(null);
		RefObject<String> errorMessage = new RefObject<String>(null);
		Regexps load = Regexps.load(fileName, errorMessage, ilanguageName, olanguageName);
		Assert.assertNotNull(load);
		Assert.assertNotNull(ilanguageName.argvalue);
		Assert.assertEquals(ilanguageName.argvalue, "ar");
		Assert.assertNotNull(olanguageName.argvalue);
		Assert.assertEquals(olanguageName.argvalue, "estonian");
	}
}
