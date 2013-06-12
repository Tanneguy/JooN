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

import java.io.IOException;

import org.apache.poi.POIXMLException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
public class DocxToTextTest
{

	private static final String PATH_TO_RESOURCES = "src/test/resources/DocxToText/";

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
	 * Test method for {@link net.nooj4nlp.engine.DocxToText#DocxToText(java.lang.String)}.
	 */
	@Test
	public void testDocxToText()
	{
		// Valid file path - existing file with proper (.docx) extension
		String validFilePath = PATH_TO_RESOURCES + "03.docx";
		// Invalid file path - empty string
		String invalidFilePath = "";

		try
		{
			DocxToText docxToText = new DocxToText(validFilePath);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(false);
		}

		// Creating docxToText object without proper parameter should result in throwing an IllegalArgumentException
		try
		{
			DocxToText docxToText = new DocxToText(invalidFilePath);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(true);
		}

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.DocxToText#extractText()}.
	 */
	@Test
	public void testExtractText()
	{
		// Valid file path - existing file with proper (.docx) extension
		String validFilePath = PATH_TO_RESOURCES + "03.docx";
		// Invalid file path - existing file but with .doc extension
		String invalidFilePath = PATH_TO_RESOURCES + "03.doc";
		// Invalid file path - existing file but with .txt extension
		String invalidFilePath2 = PATH_TO_RESOURCES + "01.txt";

		try
		{
			DocxToText docxToText = new DocxToText(validFilePath);
			docxToText.extractText();
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(false);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		// Extracting text from .doc file should result in throwing POIXMLException
		try
		{
			DocxToText docxToText = new DocxToText(invalidFilePath);
			docxToText.extractText();
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(false);
		}
		catch (POIXMLException e)
		{
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		// Extracting text from .txt file should result in throwing POIXMLException
		try
		{
			DocxToText docxToText = new DocxToText(invalidFilePath2);
			docxToText.extractText();
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(false);
		}
		catch (POIXMLException e)
		{
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

}
