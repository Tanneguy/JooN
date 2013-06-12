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

import java.util.HashMap;

import net.nooj4nlp.engine.helper.BackgroundWorker;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class EngineTest
{
	private static final String PATH_TO_RESOURCES = "src/test/resources/Engine/";

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
	 * {@link net.nooj4nlp.engine.Engine#loadNodResources(java.util.ArrayList, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testLoadNodResources()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#loadResources(java.util.ArrayList, java.util.ArrayList, boolean, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testLoadResources()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#loadCategoryPropertiesFeatures(net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testLoadCategoryPropertiesFeatures()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#Engine(net.nooj4nlp.engine.RefObject, java.lang.String, java.lang.String, java.lang.String, boolean, net.nooj4nlp.engine.Preferences, boolean, net.nooj4nlp.engine.helper.BackgroundWorker)}
	 * .
	 */
	@Test
	public void testEngineRefObjectOfLanguageStringStringStringBooleanPreferencesBooleanBackgroundWorker()
	{
		RefObject<Language> lan = new RefObject<Language>(new Language("en"));
		String appDir = "testAppDir";
		String docDir = "testDocDir";
		String projDir = "testProjDir";
		boolean projectMode = true;
		Preferences pref = new Preferences();
		boolean backgroundWorking = true;
		BackgroundWorker backgroundWorker = new BackgroundWorker();

		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		Engine e = new Engine(lan, appDir, docDir, projDir, projectMode, pref, backgroundWorking, backgroundWorker);

		Assert.assertEquals(lan.argvalue, e.Lan);
		Assert.assertEquals(docDir, e.docDir);
		Assert.assertEquals(pref, e.preferences);
		Assert.assertEquals(backgroundWorking, e.BackgroundWorking);
		Assert.assertEquals(backgroundWorker, e.backgroundWorker);

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#Engine(net.nooj4nlp.engine.Language)}.
	 */
	@Test
	public void testEngineLanguage()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Assert.assertEquals(lan, e.Lan);
		Assert.assertNull(e.docDir);
		Assert.assertNull(e.backgroundWorker);
		Assert.assertNull(e.recursiveMorphology);

		Assert.assertFalse(e.BackgroundWorking);
		Assert.assertFalse(e.ResourcesLoaded);

		Assert.assertTrue(e.lexBins.isEmpty());
		Assert.assertTrue(e.synGrms.isEmpty());

		Assert.assertTrue(e.paradigms.isEmpty());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#delimit(net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testDelimit()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Ntext text = new Ntext("en");

		text.buffer = "<Node1>Trying to delimit text\nBased on tags</Node1><Node2>\n\nTryout once more</Node2>";

		// text.DelimPattern == null
		Mft mft = e.delimit(text);

		Assert.assertNotNull(mft);
		Assert.assertEquals(1, text.nbOfTextUnits);
		Assert.assertEquals(2, mft.tuAddresses.length);
		Assert.assertEquals(2, mft.tuLengths.length);

		// text.DelimPattern != null - pattern exists in buffer
		text.DelimPattern = "\n";

		Mft mft2 = e.delimit(text);

		Assert.assertEquals(3, text.nbOfTextUnits);
		Assert.assertEquals(4, mft2.tuAddresses.length);
		Assert.assertEquals(4, mft2.tuLengths.length);

		// text.DelimPattern != null - pattern doesn't exist in buffer
		text.DelimPattern = "BLA";

		Mft mft3 = e.delimit(text);

		Assert.assertEquals(1, text.nbOfTextUnits);
		Assert.assertEquals(2, mft3.tuAddresses.length);
		Assert.assertEquals(2, mft3.tuLengths.length);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#delimitTextUnits(net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testDelimitTextUnits()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Corpus c = createCorpus();
		Ntext text = new Ntext(c);

		text.buffer = "<Node1>Trying to delimit text\nBased on tags</Node1><Node2>\n\nTryout once more</Node2>";

		Assert.assertNull(e.delimitTextUnits(text));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#delimitXml(net.nooj4nlp.engine.Ntext, java.lang.String[], net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testDelimitXml()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#delimitXmlTextUnitsAndImportXmlTags(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext)}
	 * .
	 */
	@Test
	public void testDelimitXmlTextUnitsAndImportXmlTags()
	{
		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Corpus corpus = createCorpus();

		Ntext text = new Ntext("en");

		String[] xmlNodes = new String[2];
		xmlNodes[0] = "<Node1>";
		xmlNodes[1] = "<Node2>";

		// Buffer mustn't be null
		text.buffer = "<Node1>Trying to delimit text\nBased on tags</Node1><Node2>\n\nTryout once more</Node2>";

		// text.XmlNodes == null
		String result = e.delimitXmlTextUnitsAndImportXmlTags(corpus, text);
		Assert.assertNotNull(result);
		Assert.assertEquals("no XML tag for text unit?", result);

		// text.XmlNodes != null
		result = e.delimitXmlTextUnitsAndImportXmlTags(corpus, text);
		Assert.assertNotNull(result);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#countChars(net.nooj4nlp.engine.Ntext, java.util.HashMap)}.
	 */
	@Test
	public void testCountChars()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Ntext text = new Ntext("en");

		HashMap<Character, Integer> theChars = new HashMap<Character, Integer>();

		// Buffer mustn't be null
		text.buffer = "We are going to count characters here.";

		e.countChars(text, theChars);
		Assert.assertTrue(!theChars.isEmpty());

		text.buffer = "";
		theChars.clear();

		e.countChars(text, theChars);
		Assert.assertTrue(theChars.isEmpty());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#computeAlphabet(net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testComputeAlphabetNtext()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Ntext text = new Ntext("en");

		// Buffer mustn't be null
		text.buffer = "We are going to count characters, delimiters, and other stuff here, 123.";

		// Before computing
		Assert.assertEquals(text.nbOfChars, -1);
		Assert.assertEquals(text.nbOfLetters, -1);
		Assert.assertEquals(text.nbOfDigits, -1);
		Assert.assertEquals(text.nbOfDelimiters, -1);
		Assert.assertEquals(text.nbOfBlanks, -1);
		Assert.assertNull(text.charlist);

		e.computeAlphabet(text);

		// After computing
		Assert.assertEquals(text.nbOfChars, 72);
		Assert.assertEquals(text.nbOfLetters, 54);
		Assert.assertEquals(text.nbOfDigits, 3);
		Assert.assertNotSame(text.nbOfDelimiters, -1);
		Assert.assertEquals(text.nbOfBlanks, 11);
		Assert.assertNotNull(text.charlist);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#computeAlphabet(net.nooj4nlp.engine.Corpus, java.util.HashMap)}.
	 */
	@Test
	public void testComputeAlphabetCorpusHashMapOfCharacterInteger()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		Ntext text = new Ntext("en");
		// Buffer mustn't be null
		text.buffer = "We are going to count characters, delimiters, and other stuff here, 123.";

		Corpus corpus = createCorpus();

		HashMap<Character, Integer> theChars = new HashMap<Character, Integer>();
		e.countChars(text, theChars);

		// Before computing
		Assert.assertEquals(corpus.nbOfChars, -1);
		Assert.assertEquals(corpus.nbOfLetters, -1);
		Assert.assertEquals(corpus.nbOfDigits, -1);
		Assert.assertEquals(corpus.nbOfDelimiters, -1);
		Assert.assertEquals(corpus.nbOfBlanks, -1);
		Assert.assertNull(corpus.charlist);

		e.computeAlphabet(corpus, theChars);

		// After computing
		Assert.assertEquals(corpus.nbOfChars, 72);
		Assert.assertEquals(corpus.nbOfLetters, 54);
		Assert.assertEquals(corpus.nbOfDigits, 3);
		Assert.assertNotSame(corpus.nbOfDelimiters, -1);
		Assert.assertEquals(corpus.nbOfBlanks, 11);
		Assert.assertNotNull(corpus.charlist);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addToken(java.util.HashMap, java.util.HashMap, java.lang.String, int, int)}
	 * .
	 */
	@Test
	public void testAddToken()
	{
		Language lan = new Language("en");
		Engine e = new Engine(lan);

		// hTextTokens not empty
		HashMap<String, Indexkey> hTextTokens = new HashMap<String, Indexkey>();

		hTextTokens.put("a", new Indexkey(0, 1));
		hTextTokens.put("b", new Indexkey(1, 2));
		hTextTokens.put("c", new Indexkey(2, 3));

		HashMap<String, Integer> hCorpusTokens = new HashMap<String, Integer>();

		String token = "a";
		int begaddress = 4;
		int endaddress = 5;

		// Before function call
		Assert.assertEquals(hTextTokens.get("a").addresses.size(), 2);
		Assert.assertEquals(hTextTokens.get("a").addresses.get(0), 0);
		Assert.assertEquals(hTextTokens.get("a").addresses.get(1), 1);

		e.addToken(hCorpusTokens, hTextTokens, token, begaddress, endaddress);

		// After function call
		Assert.assertEquals(hTextTokens.get("a").addresses.size(), 4);
		Assert.assertEquals(hTextTokens.get("a").addresses.get(2), 4);
		Assert.assertEquals(hTextTokens.get("a").addresses.get(3), 5);

		Assert.assertEquals(hCorpusTokens.size(), 1);
		Assert.assertEquals(hCorpusTokens.get("a").intValue(), 1);

		token = "d";
		begaddress = 6;
		endaddress = 7;

		e.addToken(hCorpusTokens, hTextTokens, token, begaddress, endaddress);

		// After function call
		Assert.assertEquals(hTextTokens.get("d").addresses.size(), 2);
		Assert.assertEquals(hTextTokens.get("d").addresses.get(0), 6);
		Assert.assertEquals(hTextTokens.get("d").addresses.get(1), 7);

		Assert.assertEquals(hCorpusTokens.size(), 2);
		Assert.assertEquals(hCorpusTokens.get("d").intValue(), 1);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addLexemeToText(java.util.ArrayList, java.util.HashMap, java.lang.String, net.nooj4nlp.engine.Mft, int, double, double)}
	 * .
	 */
	@Test
	public void testAddLexemeToText()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addLexemeToCorpus(java.util.ArrayList, java.util.HashMap, java.lang.String, net.nooj4nlp.engine.Mft, int, double, double)}
	 * .
	 */
	@Test
	public void testAddLexemeToCorpus()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addSyntaxToText(java.util.ArrayList, java.util.ArrayList, java.util.HashMap, java.lang.String, net.nooj4nlp.engine.Mft, int, double, double, boolean)}
	 * .
	 */
	@Test
	public void testAddSyntaxToText()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addSyntaxToCorpus(java.util.ArrayList, java.util.ArrayList, java.util.HashMap, java.lang.String, net.nooj4nlp.engine.Mft, int, double, double, boolean)}
	 * .
	 */
	@Test
	public void testAddSyntaxToCorpus()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addUnknown(java.util.ArrayList, java.util.HashMap, java.util.HashMap, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddUnknown()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#lookupAllLexsForCompounds(int, java.lang.String)}.
	 */
	@Test
	public void testLookupAllLexsForCompoundsIntString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#lookupAllCompoundsAndSimpleLexs(java.lang.String)}.
	 */
	@Test
	public void testLookupAllCompoundsAndSimpleLexs()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#lookupAllLexsAndMorphsForSimples(java.lang.String, boolean, java.lang.String, int)}
	 * .
	 */
	@Test
	public void testLookupAllLexsAndMorphsForSimples()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#lookupAllLexsForCompounds(java.lang.String, boolean, java.lang.String, int)}.
	 */
	@Test
	public void testLookupAllLexsForCompoundsStringBooleanStringInt()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#lookupAllLexsAndHighMorphsForSimples(java.lang.String, boolean)}.
	 */
	@Test
	public void testLookupAllLexsAndHighMorphsForSimples()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#getRidOfLUsAndConstraints(java.util.ArrayList)}.
	 */
	@Test
	public void testGetRidOfLUsAndConstraints()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#getRidOfConstraints(java.util.ArrayList)}.
	 */
	@Test
	public void testGetRidOfConstraints()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#getRidOfAngles(java.util.ArrayList)}.
	 */
	@Test
	public void testGetRidOfAngles()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#lookupAndAnalyzeSimpleOrCompound(java.lang.String, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testLookupAndAnalyzeSimpleOrCompound()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#lookupAndAnalyzeSimpleNoComplex(java.lang.String)}.
	 */
	@Test
	public void testLookupAndAnalyzeSimpleNoComplex()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#lookupAllSDics(java.lang.String)}.
	 */
	@Test
	public void testLookupAllSDics()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#filterUnamb(java.util.ArrayList)}.
	 */
	@Test
	public void testFilterUnamb()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#filterFILTEROUT(java.util.ArrayList)}.
	 */
	@Test
	public void testFilterFILTEROUT()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#filterNonWords(java.util.ArrayList)}.
	 */
	@Test
	public void testFilterNonWords()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#isComplex(java.lang.String)}.
	 */
	@Test
	public void testIsComplexString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#isComplex(java.lang.String, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testIsComplexStringRefObjectOfBoolean()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#isASyntacticFeature(java.lang.String)}.
	 */
	@Test
	public void testIsASyntacticFeature()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#isAninflectionalFeature(java.lang.String)}.
	 */
	@Test
	public void testIsAninflectionalFeature()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#processELCSFVariables(java.util.ArrayList, java.lang.String)}.
	 */
	@Test
	public void testProcessELCSFVariables()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#processDollarZero(java.util.ArrayList, java.lang.String)}.
	 */
	@Test
	public void testProcessDollarZero()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#tokenize(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext, java.util.ArrayList, java.util.HashMap, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testTokenize()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#computeAmbiguities(net.nooj4nlp.engine.Corpus, java.lang.String, net.nooj4nlp.engine.Ntext, java.util.ArrayList, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testComputeAmbiguities()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#computeUnambiguities(net.nooj4nlp.engine.Corpus, java.lang.String, net.nooj4nlp.engine.Ntext, java.util.ArrayList, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testComputeUnambiguities()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#enrichDictionary(java.lang.String[])}.
	 */
	@Test
	public void testEnrichDictionary()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#computeTokens(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testComputeTokens()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#computeDigrams(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext)}.
	 */
	@Test
	public void testComputeDigrams()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#processVariableInLabel(java.lang.String, net.nooj4nlp.engine.Mft, java.util.ArrayList, int, java.lang.String, java.util.ArrayList, int, java.util.ArrayList, double, int)}
	 * .
	 */
	@Test
	public void testProcessVariableInLabel()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#processVariableInMorphoLabel(java.lang.String, java.util.ArrayList)}.
	 */
	@Test
	public void testProcessVariableInMorphoLabel()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#thereIsAVariableInLabel(java.lang.String)}.
	 */
	@Test
	public void testThereIsAVariableInLabel()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#processVariablesInOneSingleOutput(java.lang.String, net.nooj4nlp.engine.Mft, java.util.ArrayList, int, int, java.util.ArrayList, int, java.util.ArrayList, double, java.lang.String, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testProcessVariablesInOneSingleOutput()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#processConstraint(java.lang.String, net.nooj4nlp.engine.Mft, int, java.util.ArrayList, net.nooj4nlp.engine.Grammar, java.util.ArrayList, int, java.util.ArrayList, double, int, java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testProcessConstraint()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessVariablesInString(java.lang.String, double, int, net.nooj4nlp.engine.Mft, int, java.util.ArrayList, int, java.util.ArrayList, java.util.ArrayList, java.lang.String, net.nooj4nlp.engine.Grammar, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, int)}
	 * .
	 */
	@Test
	public void testNewProcessVariablesInStringStringDoubleIntMftIntArrayListIntArrayListArrayListStringGrammarRefObjectOfArrayListRefObjectOfArrayListInt()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessVariablesInString(java.lang.String, java.util.HashMap, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNewProcessVariablesInStringStringHashMapIntRefObjectOfArrayList()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessConstraintsInString(java.lang.String, double, int, net.nooj4nlp.engine.Mft, int, java.util.ArrayList, int, java.util.ArrayList, java.util.ArrayList, java.lang.String, net.nooj4nlp.engine.Grammar, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNewProcessConstraintsInString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessConstraintsInStringForTransformed(java.lang.String, java.util.HashMap, int, net.nooj4nlp.engine.Grammar, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNewProcessConstraintsInStringForTransformed()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessVariables(java.lang.String, net.nooj4nlp.engine.Mft, java.util.ArrayList, int, java.util.ArrayList, int, java.util.ArrayList, net.nooj4nlp.engine.Grammar, double, java.util.ArrayList, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, int)}
	 * .
	 */
	@Test
	public void testNewProcessVariables()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newProcessConstraints(java.lang.String, net.nooj4nlp.engine.Mft, java.util.ArrayList, int, net.nooj4nlp.engine.Grammar, java.util.ArrayList, int, java.util.ArrayList, double, java.util.ArrayList, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNewProcessConstraints()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#moveBegAddressOufOfSpaces(java.lang.String, boolean, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testMoveBegAddressOufOfSpaces()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#newSyntacticParsing(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext, java.util.ArrayList, net.nooj4nlp.engine.Grammar, char, int, boolean, boolean, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNewSyntacticParsing()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#rel2Abs(java.util.ArrayList, long)}.
	 */
	@Test
	public void testRel2Abs()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#applyAllGrammars(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext, java.util.ArrayList, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testApplyAllGrammars()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#inflectSolutions(java.util.ArrayList)}.
	 */
	@Test
	public void testInflectSolutions()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Engine#computeDerivations(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testComputeDerivations()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#mergeIntoAnnotations(java.lang.String, int, double, double, java.util.ArrayList, java.util.ArrayList, boolean)}
	 * .
	 */
	@Test
	public void testMergeIntoAnnotations()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Engine#addAllXmlAnnotations(net.nooj4nlp.engine.Corpus, net.nooj4nlp.engine.Ntext, java.util.ArrayList)}
	 * .
	 */
	@Test
	public void testAddAllXmlAnnotations()
	{
		fail("Not yet implemented");
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

}
