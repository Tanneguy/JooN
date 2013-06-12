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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NtextTest
{
	private static final String PATH_TO_RESOURCES = "src/test/resources/Ntext/";

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
	 * {@link net.nooj4nlp.engine.Ntext#Ntext(java.lang.String languageName, java.lang.String delimitPattern, java.lang.String[] xmlNodes)}
	 * .
	 */
	@Test
	public void testNtextStringStringStringArray()
	{
		String languageName = "en";
		String delimitPattern = "\n";
		String[] xmlNodes = new String[2];
		xmlNodes[0] = "<Node1>";
		xmlNodes[1] = "</Node1>";

		Ntext ntext = new Ntext(languageName, delimitPattern, xmlNodes);

		Assert.assertNotNull(ntext);

		Assert.assertEquals("en", ntext.Lan.locale.getLanguage());
		Assert.assertEquals("US", ntext.Lan.locale.getCountry());

		Assert.assertEquals(-1, ntext.nbOfBlanks);
		Assert.assertEquals(-1, ntext.nbOfWords);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#Ntext(net.nooj4nlp.engine.Corpus)}.
	 */
	@Test
	public void testNtextCorpus()
	{
		Corpus corpus = createCorpus();

		Ntext ntext = new Ntext(corpus);

		Assert.assertNotNull(ntext);

		Assert.assertEquals(corpus.languageName, ntext.LanguageName);
		Assert.assertEquals(corpus.delimPattern, ntext.DelimPattern);

		Assert.assertNull(ntext.mft);
		Assert.assertNull(ntext.annotations);
		Assert.assertNull(ntext.buffer);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#Ntext(java.lang.String languageName)}.
	 */
	@Test
	public void testNtextString()
	{
		String languageName = "en";

		Ntext ntext = new Ntext(languageName);

		Assert.assertEquals(languageName, ntext.Lan.isoName);
		Assert.assertNull(ntext.annotations);
		Assert.assertNull(ntext.buffer);
		Assert.assertEquals(-1, ntext.nbOfBlanks);
		Assert.assertEquals(-1, ntext.nbOfWords);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Ntext#delimitXmlTextUnitsAndImportXmlTags(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Engine, java.lang.String[], java.util.ArrayList, java.util.HashMap, java.util.HashMap)}
	 * .
	 */
	@Test
	public void testDelimitXmlTextUnitsAndImportXmlTags()
	{
		Corpus corpus = createCorpus();
		Engine engine = new Engine(corpus.lan);

		String[] xmlNodes = new String[2];
		xmlNodes[0] = "<Node1>";
		xmlNodes[1] = "<Node2>";

		ArrayList annotations = new ArrayList();

		Ntext ntext = new Ntext(corpus);

		// We expect IllegalArgumentException, because annotations are not set
		try
		{
			ntext.delimitXmlTextUnitsAndImportXmlTags(corpus, engine, xmlNodes, annotations, null, null);
			Assert.assertTrue(false);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(true);
		}

		annotations.add("ann1");
		annotations.add("ann2");

		// Buffer mustn't be null
		ntext.buffer = "<Node1>Trying to delimit text\nBased on tags</Node1><Node2>\n\nTryout once more</Node2>";

		ntext.delimitXmlTextUnitsAndImportXmlTags(corpus, engine, xmlNodes, annotations, null, null);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#delimitTextUnits(net.nooj4nlp.engine.Engine)}.
	 */
	@Test
	public void testDelimitTextUnits()
	{
		Corpus c = createCorpus();
		Engine engine = new Engine(c.lan);

		Ntext ntext = new Ntext(c);

		ntext.buffer = "<Node1>Trying to delimit text\nBased on tags</Node1><Node2>\n\nTryout once more</Node2>";

		Assert.assertNull(ntext.delimitTextUnits(engine));
		Assert.assertNotNull(ntext.mft);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#cleanupBadAnnotations(java.util.ArrayList)}.
	 */
	@Test
	public void testCleanupBadAnnotations()
	{
		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Ntext#buildXmlTaggedText(java.lang.String, int, boolean, java.io.PrintWriter, int, java.util.ArrayList, java.lang.String[], net.nooj4nlp.engine.Language, boolean)}
	 * .
	 */
	@Test
	public void testBuildXmlTaggedText()
	{
		String currentLine = "This is the current line of text.";

		Corpus c = createCorpus();
		Engine engine = new Engine(c.lan);

		Ntext ntext = new Ntext(c);

		// TODO
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#computehLexemes(net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testComputehLexemes()
	{
		Corpus c = createCorpus();
		Engine engine = new Engine(c.lan);

		Ntext ntext = new Ntext(c);

		ntext.annotations = new ArrayList();
		ntext.annotations.add("<child,N+plural>children</child,N+plural>, childs, SYNTAX");

		Ntext.computehLexemes(ntext);
		Assert.assertTrue(true);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Ntext#load(java.lang.String, java.lang.String, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testLoad()
	{
		String fullNamePath = PATH_TO_RESOURCES + "file2.not";

		Mft mft = createMftWithTransitions();

		RefObject<String> errMessage = new RefObject<String>("");

		try
		{
			Ntext ntext = Ntext.load(fullNamePath, "en", errMessage);
			Assert.assertTrue(true);
		}
		catch (ClassNotFoundException e)
		{
			Assert.assertTrue(false);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Ntext#loadForCorpus(java.lang.String, net.nooj4nlp.engine.Language, double)}.
	 */
	@Test
	public void testLoadForCorpus()
	{
		String fullNamePath = PATH_TO_RESOURCES + "file.not";

		Mft mft = createMftWithTransitions();

		Language lan = new Language("en");
		double multiplier = 100.0;

		try
		{
			Ntext ntext = Ntext.loadForCorpus(fullNamePath, lan, multiplier);

			Assert.assertEquals(lan, ntext.Lan);
			Assert.assertEquals(mft.multiplier, ntext.mft.multiplier, 0.0);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Ntext#loadJustBufferForCorpus(java.lang.String, net.nooj4nlp.engine.Language, double)}
	 * .
	 */
	@Test
	public void testLoadJustBufferForCorpus()
	{
		String fullNamePath = PATH_TO_RESOURCES + "file.not";

		Mft mft = createMftWithTransitions();

		Language lan = new Language("en");
		double multiplier = 100.0;

		try
		{
			Ntext ntext = Ntext.loadJustBufferForCorpus(fullNamePath, lan, multiplier);

			Assert.assertEquals(lan, ntext.Lan);

		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#save(java.lang.String)}.
	 */
	@Test
	public void testSave()
	{
		String fullNamePath = PATH_TO_RESOURCES + "file2.not";
		Corpus corpus = createCorpus();

		Ntext ntext = new Ntext(corpus);
		ntext.mft = createMftWithTransitions();

		ntext.annotations = new ArrayList();
		ntext.annotations.add("<child,N+plural>children</child,N+plural>, childs, SYNTAX");

		try
		{
			ntext.save(fullNamePath);
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#saveForCorpus(java.lang.String)}.
	 * 
	 */
	@Test
	public void testSaveForCorpus()
	{
		String fullNamePath = PATH_TO_RESOURCES + "file.not";
		Corpus corpus = createCorpus();

		Ntext ntext = new Ntext(corpus);
		ntext.mft = createMftWithTransitions();

		try
		{
			ntext.saveForCorpus(fullNamePath);
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Ntext#updateAnnotationsForText()}.
	 */
	@Test
	public void testUpdateAnnotationsForText()
	{
		Corpus corpus = createCorpus();

		Ntext ntext = new Ntext(corpus);
		ntext.mft = createMftWithTransitions();

		ntext.annotations = new ArrayList();
		for (int i = 0; i < 20; i++)
			ntext.annotations.add("<child,N+plural>children</child,N+plural>, childs, SYNTAX");

		ntext.updateAnnotationsForText();
	}

	private Corpus createCorpus()
	{
		String delimPattern = "\n";

		String[] xmlNodes = null;

		int encodingType = 2; // raw text with encoding
		String encodingCode = "UTF-8";
		// Since format is known in this example (raw text), encodingName can be null
		String encodingName = null;
		String languageName = "en";

		return new Corpus(delimPattern, xmlNodes, encodingType, encodingCode, encodingName, languageName);
	}

	private Mft createMftWithTransitions()
	{
		Mft mft = new Mft(2);

		// Filling data for mft
		// First member of array of transitions
		ArrayList al1 = new ArrayList();
		al1.add(0.11);
		ArrayList al1al = new ArrayList();
		al1al.add(11);
		al1al.add(12.0);
		al1.add(al1al);

		al1.add(0.12);
		ArrayList al2al = new ArrayList();
		al2al.add(13);
		al2al.add(14.0);
		al2al.add(15);
		al2al.add(16.0);
		al1.add(al2al);

		mft.aTransitions[1] = al1;

		// Second member of array of transitions
		ArrayList al2 = new ArrayList();
		al2.add(0.21);
		ArrayList al3al = new ArrayList();
		al3al.add(21);
		al3al.add(22.0);
		al2.add(al3al);

		mft.aTransitions[2] = al2;

		// System.out.println(mft.aTransitions[1]);
		// System.out.println(mft.aTransitions[2]);

		mft.nboftransitions = 4;

		mft.multiplier = 100.0;

		return mft;
	}

}
