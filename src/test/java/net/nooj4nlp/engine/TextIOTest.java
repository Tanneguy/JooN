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
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import org.apache.poi.POIXMLException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author natalija
 * 
 */
public class TextIOTest
{

	private static final String PATH_TO_RESOURCES = "src/test/resources/TextIO/";

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
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadRawText(String filePath)}.
	 */
	@Test
	public void testLoadRawTextString()
	{
		// Valid file path - existing file
		String validFilePath = PATH_TO_RESOURCES + "01.txt";
		// Invalid file path - empty string for path
		String emptyFilePath = "";
		// Invalid file path - not existing file
		String invalidFilePath = PATH_TO_RESOURCES + "010.txt";
		// Invalid file path - path to existing directory
		String validDirectoryPath = PATH_TO_RESOURCES + "01dir";

		try
		{
			TextIO.loadRawText(validFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadRawText(emptyFilePath);
			Assert.assertTrue(false);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadRawText(invalidFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}

		try
		{
			TextIO.loadRawText(validDirectoryPath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadRawText(String filePath, String encoding)}.
	 */
	@Test
	public void testLoadRawTextStringString()
	{
		// Valid file path - existing file
		String validFilePath = PATH_TO_RESOURCES + "01.txt";
		// Valid encoding for valid file
		String validEncoding = "UTF-8";
		// Invalid file path - not existing file
		String invalidFilePath = PATH_TO_RESOURCES + "010.txt";
		// Invalid encoding for any file
		String invalidEncoding = "INVALIDENCODING";
		// Valid file path and encoding for second file
		String validFilePath2 = PATH_TO_RESOURCES + "02.txt";
		String validEncoding2 = "ISO-8859-2";

		try
		{
			TextIO.loadRawText(validFilePath, validEncoding);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadRawText(invalidFilePath, validEncoding);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}

		try
		{
			TextIO.loadRawText(validFilePath, invalidEncoding);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}

		try
		{
			TextIO.loadRawText(validFilePath2, validEncoding2);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadWordFile(String filePath)}.
	 */
	@Test
	public void testLoadWordFile()
	{
		// Valid file path - existing file with .doc extension
		String validFilePath = PATH_TO_RESOURCES + "03.doc";
		// Invalid file path - existing file, but not proper Word file (not with .doc extension)
		String invalidFilePath = PATH_TO_RESOURCES + "03.docx";

		try
		{
			TextIO.loadWordFile(validFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadWordFile(invalidFilePath);
		}
		catch (OfficeXmlFileException e)
		{
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadHtmlText(String filePath)}.
	 */
	@Test
	public void testLoadHtmlText()
	{
		// Valid file path - existing file with proper extension
		String validFilePath = PATH_TO_RESOURCES + "04.htm";
		// Invalid file path - existing file, but not proper HTML file (not with .htm extension)
		String invalidFilePath = PATH_TO_RESOURCES + "03.docx";

		try
		{
			TextIO.loadHtmlText(validFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadHtmlText(invalidFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadWord2007File(String filePath)}.
	 */
	@Test
	public void testLoadWord2007File()
	{
		// Valid file path - existing file with proper (.docx) extension
		String validFilePath = PATH_TO_RESOURCES + "03.docx";
		// Invalid file path - existing file but with .doc extension
		String invalidFilePath = PATH_TO_RESOURCES + "03.doc";

		try
		{
			TextIO.loadWord2007File(validFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadWord2007File(invalidFilePath);
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

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadRtfFile(String filePath)}.
	 */
	@Test
	public void testLoadRtfFile()
	{
		// Valid file path - existing file with proper (.rtf) extension
		String validFilePath = PATH_TO_RESOURCES + "ReadMe.rtf";
		// Invalid file path - existing file but with .doc extension
		String invalidFilePath = PATH_TO_RESOURCES + "03.doc";

		try
		{
			String result = TextIO.loadRtfFile(validFilePath);
			System.out.println(result);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadRtfFile(invalidFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadPdfFile(String filePath)}.
	 */
	@Test
	public void testLoadPdfFile()
	{
		// Valid file path - existing file with proper (.pdf) extension
		String validFilePath = PATH_TO_RESOURCES + "06.pdf";
		String validFilePath2 = PATH_TO_RESOURCES + "06a.pdf";

		try
		{
			TextIO.loadPdfFile(validFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadPdfFile(validFilePath2);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.TextIO#loadText(String filePath, int encodingtype, String
	 * encodingcode, String encodingname, ArrayList<String> chartable)} .
	 */
	@Test
	public void testLoadText()
	{
		// Valid file paths
		String validFilePath01 = PATH_TO_RESOURCES + "01.txt";
		String validFilePath02 = PATH_TO_RESOURCES + "02.txt";
		String validFilePath03 = PATH_TO_RESOURCES + "03.doc";
		String validFilePath04 = PATH_TO_RESOURCES + "03.docx";
		String validFilePath05 = PATH_TO_RESOURCES + "05.rtf";
		String validFilePath06 = PATH_TO_RESOURCES + "06.pdf";
		String validFilePath07 = PATH_TO_RESOURCES + "07.txt";
		String validFilePath08 = PATH_TO_RESOURCES + "04.htm";
		String validFilePath09 = PATH_TO_RESOURCES + "08.docx";

		// Valid and invalid encodings
		String validEncoding = "ISO-8859-2";
		String invalidEncoding = "INVALIDENCODING";

		ArrayList<String> charTable = new ArrayList<String>();
		charTable.add("a");
		charTable.add("A");
		charTable.add("m");
		charTable.add("M");

		try
		{
			TextIO.loadText(validFilePath01, 1, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			String result = TextIO.loadText(validFilePath07, 1, "", "", charTable);
			// System.out.println(result);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath02, 2, validEncoding, "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath02, 2, invalidEncoding, "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath01, 2, "INVALIDENCODING", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(true);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath05, 3, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath03, 4, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath08, 5, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath04, 6, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			String result = TextIO.loadText(validFilePath09, 6, "", "", charTable);
			// System.out.println(result);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			TextIO.loadText(validFilePath06, 7, "", "", null);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}

		try
		{
			charTable.add("s");
			charTable.add("PUF");
			String result = TextIO.loadText(validFilePath06, 7, "", "", charTable);
			// System.out.println(result);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}
	}
}
