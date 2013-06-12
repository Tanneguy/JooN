package net.nooj4nlp.engine;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testIsItTextual()
	{
		boolean isItTextual = Grammar.isItTextual("c:\\nooj.txt");
		Assert.assertEquals(true, isItTextual);
		isItTextual = Grammar.isItTextual("c:\\save.txt");
		Assert.assertEquals(false, isItTextual);
	}

	@Test
	public void testGrammar()
	{
		Grammar g = new Grammar();
		Assert.assertNotNull(g);
	}

	@Test
	public void testGrammarLanguageGramTypeEngine()
	{
		Language l = new Language("en");
		Grammar g = new Grammar(l, GramType.FLX, new Engine(l));
		Assert.assertNotNull(g);
	}

	@Test
	public void testGrammarGramTypeStringStringStringIntStringString()
	{
		Grammar g = new Grammar(GramType.FLX, "String", "String", "String", 0, "String", "String");
		Assert.assertNotNull(g);
	}

	@Test
	public void testGrammarGramTypeStringStringStringIntStringStringPreferences()
	{
		Grammar g = new Grammar(GramType.FLX, "String", "String", "String", 0, "String", "String", null);
		Assert.assertNotNull(g);
	}

	@Test
	// N
	public void testImportWithAllEmbeddedGraphsStringGramTypeStringStringInt()
	{
		// NAPOMENA Graph uvek vraca null jer nije implementiran
		// Funkcija nikad ne prodje tu proveru
		Grammar.importWithAllEmbeddedGraphs("c:\\_sample.nod", GramType.FLX, "en", "en", 1);
	}

	@Test
	// N
	public void testImportWithAllEmbeddedGraphsStringGramTypeStringStringIntPreferences()
	{
		// NAPOMENA Graph uvek vraca null jer nije implementiran
		// Funkcija nikad ne prodje tu proveru
		Grammar.importWithAllEmbeddedGraphs("c:\\_phrasal_verbs.nog", GramType.MORPHO, "en", "en", 0, null);
	}

	@Test
	public void testImportEmbeddedGraphs()
	{
		// NAPOMENA Graph uvek vraca null jer nije implementiran
	}

	@Test
	public void testGetDataFromSerialization()
	{
		// Empty function
	}

	@Test
	public void testStoreDataForSerialization()
	{
		// Empty function
	}

	@Test
	// N
	public void testLoadTextual()
	{
		String txt = null;
		RefObject<String> tmp = new RefObject<String>(txt);
		try
		{
			// Grammar g = Grammar.loadTextual("c:\\_sample.dic", GramType.MORPHO, tmp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	// N
	public void testLoadGraphical()
	{
		String txt = null;
		RefObject<String> tmp = new RefObject<String>(txt);
		try
		{
			// Grammar g = Grammar.loadGraphical("c:\\_sample.dic");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testSave()
	{
		Grammar g = new Grammar(GramType.FLX, "String", "String", "String", 0, "String", "String", null);
		g.save("c:\\save.txt");
	}

	@Test
	public void testAddGrams()
	{
		Grammar g = new Grammar();
		Grammar g1 = new Grammar();
		Assert.assertNull(g.addGrams(g1));
	}

	@Test
	// N
	public void testCompileAndComputeFirst()
	{
		try
		{
			Language l = new Language("en");
			Engine e = new Engine(l);
			Grammar g = new Grammar(l, GramType.FLX, e);
			// g.compileAndComputeFirst(e, false));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	// N
	public void testDebugCompileAll()
	{
		try
		{
			Language l = new Language("en");
			Engine e = new Engine(l);
			Grammar g = new Grammar(l, GramType.FLX, e);
			// g.debugCompileAll(e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	// N
	public void testProcessVariablesInOutputs()
	{

	}

	@Test
	public void testComputeInput()
	{
		ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
		{
			input.add(String.valueOf(i));
		}
		input.add("<E>");
		input.add("");
		input.add("$(");
		for (int i = 0; i < 5; i++)
		{
			input.add(String.valueOf(i));
		}
		Assert.assertEquals("012345678901234", Grammar.computeInput(input).toString());
	}

	@Test
	// N
	public void testMorphoMatch()
	{

	}

	@Test
	// N
	public void testDebugMorphoMatch()
	{

	}

	@Test
	// N
	public void testFailureDebugMorphoMatch()
	{

	}

	@Test
	// N
	public void testMatchWord()
	{

	}

	@Test
	// N
	public void testDebugMatchWord()
	{

	}

	@Test
	public void testIsComplex()
	{
		StringBuilder s = new StringBuilder();
		s.append("\\n");
		s.append("{");
		s.append("<");
		Assert.assertEquals(true, Grammar.isComplex(s));
		StringBuilder d = new StringBuilder();
		d.append("\\nneki");
		Assert.assertEquals(false, Grammar.isComplex(d));
	}

	@Test
	public void testHasMoreThanOneWord()
	{
		StringBuilder s = new StringBuilder();
		s.append("Some string");
		Assert.assertEquals(false, Grammar.hasMoreThanOneWord(s));
		s.append("{Word} {Word}");
		Assert.assertEquals(true, Grammar.hasMoreThanOneWord(s));
	}

	@Test
	// N
	public void testFilterConstraint()
	{

	}

	@Test
	public void testAddBracketsAround()
	{
		ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
		{
			input.add(String.valueOf(i));
		}
		ArrayList output = Grammar.addBracketsAround(input);
		for (int i = 0; i < 10; i++)
		{
			Assert.assertEquals("{" + String.valueOf(i) + "}", output.get(i));
		}
	}

	@Test
	public void testTransformConstraintIntoLU()
	{
		ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
		{
			input.add("<" + String.valueOf(i));
		}
		ArrayList output = Grammar.transformConstraintIntoLU(input);
		for (int i = 0; i < 10; i++)
		{
			Assert.assertEquals("<LU=NA,null,nullnull>", output.get(i));
		}
	}

	@Test
	public void testTransformConstraintIntoLUNoLU()
	{
		ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
		{
			input.add("<" + String.valueOf(i));
		}
		ArrayList output = Grammar.transformConstraintIntoLUNoLU(input);
		for (int i = 0; i < 10; i++)
		{
			Assert.assertEquals("<NA,null,nullnull>", output.get(i));
		}
	}

	@Test
	// N
	public void testProcessConstraints()
	{

	}

	@Test
	// N
	public void testDefactorize()
	{
		fail("Not yet implemented");
	}

	@Test
	// N
	public void testXmlFilterMatches()
	{

	}

	@Test
	// N
	public void testMatchLexeme()
	{

	}

	@Test
	// N
	public void testFilterLexemesRefObjectOfArrayListRefObjectOfArrayListStringStringStringArrayBoolean()
	{

	}

	@Test
	// N
	public void testFilterLexemesRefObjectOfArrayListStringStringStringArrayBoolean()
	{

	}

	@Test
	// N
	public void testFilterNonMatches()
	{

	}

	@Test
	// N
	public void testSkipSpaces()
	{

	}

	@Test
	// N
	public void testSyntaxMatch()
	{

	}

	@Test
	// N
	public void testDebugSyntaxMatch()
	{

	}

	@Test
	// N
	public void testFailureDebugSyntaxMatch()
	{

	}

	@Test
	// N
	public void testCheckSyntaxMatch()
	{

	}

}
