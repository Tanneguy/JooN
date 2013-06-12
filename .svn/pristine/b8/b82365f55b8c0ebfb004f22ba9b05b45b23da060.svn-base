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

import javax.swing.text.BadLocationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
public class CorpusTest
{

	private static final String PATH_TO_RESOURCES = "src/test/resources/Corpus/";

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
	 * {@link net.nooj4nlp.engine.Corpus#Corpus(java.lang.String, java.lang.String[], int, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCorpus()
	{
		String delimPattern = "\n";

		String[] xmlNodes = new String[5];
		for (int i = 0; i < 5; i++)
			xmlNodes[i] = "<TAG" + i + ">";

		int encodingType = 2; // raw text with encoding
		String encodingCode = "UTF-8";
		// Since format is known in this example (raw text), encodingName can be null
		String encodingName = null;
		String languageName = "br";

		Corpus corpus = new Corpus(delimPattern, xmlNodes, encodingType, encodingCode, encodingName, languageName);

		// Test whether constructor initialized the values of class members properly
		Assert.assertTrue(corpus.lan.engName.equals("Berber"));
		Assert.assertTrue(corpus.lan.natName.equals("Berber"));
		Assert.assertTrue(corpus.lan.locale.getLanguage().equals("en"));
		Assert.assertTrue(corpus.lan.locale.getCountry().equals("US"));
		Assert.assertTrue(corpus.lan.rightToLeft == false);

		Assert.assertTrue(corpus.annotations.isEmpty());
		Assert.assertTrue(corpus.hLexemes.isEmpty());
		Assert.assertTrue(corpus.hPhrases.isEmpty());
		Assert.assertTrue(corpus.nbOfBlanks == -1);
		Assert.assertTrue(corpus.nbOfWords == -1);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Corpus#addTextFile(java.lang.String, java.lang.String, net.nooj4nlp.engine.Engine)}.
	 */
	@Test
	public void testAddTextFile()
	{
		String corpusDirName = PATH_TO_RESOURCES + "testCorpus.noc";

		try
		{
			Corpus c = Corpus.load(corpusDirName, "en");
			Assert.assertTrue(true);

			String corpusFullDirName = PATH_TO_RESOURCES + "testCorpus.noc_dir";
			String textFullPath = PATH_TO_RESOURCES + "forcorpus.xml";

			// Corpus and engine must be defined before calling tested method
			Engine engine = new Engine(c.lan);

			// Annotations must not be empty because test fails (Dic.writeLog fails at some point)
			c.annotations.add("prva");

			c.addTextFile(corpusFullDirName, textFullPath, engine);

			String newFileName = PATH_TO_RESOURCES + "forcorpus.not";
			Assert.assertEquals(newFileName, c.listOfFileTexts.get(0));

			// TODO this will be tested later
			// c.saveIn(corpusFullDirName);
			// Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (ClassNotFoundException e)
		{
			Assert.assertTrue(false);
		}
		catch (BadLocationException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Corpus#saveIn(java.lang.String)}.
	 */
	@Test
	public void testSaveIn()
	{
		String resDirPath = PATH_TO_RESOURCES + "testCorpus.noc_dir";

		String delimPattern = "\n";

		String[] xmlNodes = null;

		int encodingType = 2; // raw text with encoding
		String encodingCode = "UTF-8";
		// Since format is known in this example (raw text), encodingName can be null
		String encodingName = null;
		String languageName = "en";

		Corpus corpus = new Corpus(delimPattern, xmlNodes, encodingType, encodingCode, encodingName, languageName);

		try
		{
			corpus.saveIn(resDirPath);
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Corpus#load(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLoad()
	{
		String corpusDirName = PATH_TO_RESOURCES + "testCorpus.noc";

		try
		{
			Corpus c = Corpus.load(corpusDirName, "en");
			Assert.assertTrue(true);

			// TODO once the Dic.parseDELAF is explained better, this will be tested
			// Assert.assertTrue(c.listOfFileTexts.size() != 0);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		catch (ClassNotFoundException e)
		{
			Assert.assertTrue(false);
		}
	}

}
