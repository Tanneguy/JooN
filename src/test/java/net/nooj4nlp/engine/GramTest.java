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

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class GramTest
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
	 * Test method for {@link net.nooj4nlp.engine.Gram#Gram(int)}.
	 */
	@Test
	public void testGramInt()
	{
		int n = 18;

		Gram g = new Gram(n);

		Assert.assertEquals(n, g.states.size());
		Assert.assertNull(g.vocab);
		Assert.assertNull(g.vocabIn);
		Assert.assertNull(g.vocabOut);
		Assert.assertNull(g.InflectionsCommands);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#Gram()}.
	 */
	@Test
	public void testGram()
	{
		Gram g = new Gram();

		Assert.assertNotNull(g);

		Assert.assertTrue(g.states.isEmpty());
		Assert.assertNull(g.vocab);
		Assert.assertNull(g.vocabIn);
		Assert.assertNull(g.vocabOut);
		Assert.assertNull(g.InflectionsCommands);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#toMyString()}.
	 */
	@Test
	public void testToMyString()
	{
		Gram g = createGram();
		System.out.println(g.toMyString());
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#addTransition(int, int, java.lang.String, java.util.ArrayList, java.util.HashMap)}
	 * .
	 */
	@Test
	public void testAddTransitionIntIntStringArrayListOfStringHashMapOfStringInteger()
	{
		Gram g = createGram();

		int src = 2;
		int dst = 100;
		String label = "newLabel";

		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add("firstLabel");
		aVocab.add("secondLabel");
		aVocab.add("thirdLabel");
		aVocab.add("fourthLabel");

		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put("firstLabel", 1);
		hVocab.put("secondLabel", 2);
		hVocab.put("thirdLabel", 3);
		hVocab.put("fourthLabel", 4);
		hVocab.put("newLabel", 5);

		Assert.assertEquals(4, g.states.get(src).IdLabels.size());
		Assert.assertEquals(4, g.states.get(src).Dests.size());

		// System.out.println(g.states.get(src).IdLabels);
		// System.out.println(g.states.get(src).Dests);

		g.addTransition(src, dst, label, aVocab, hVocab);

		Assert.assertEquals(5, g.states.get(src).IdLabels.size());
		Assert.assertEquals(5, g.states.get(src).Dests.size());

		// System.out.println(g.states.get(src).IdLabels);
		// System.out.println(g.states.get(src).Dests);

		String otherLabel = "otherLabel";

		g.addTransition(src, dst, otherLabel, aVocab, hVocab);

		Assert.assertEquals(6, g.states.get(src).IdLabels.size());
		Assert.assertEquals(6, g.states.get(src).Dests.size());

		Assert.assertEquals(5, aVocab.size());

		// System.out.println(g.states.get(src).IdLabels);
		// System.out.println(g.states.get(src).Dests);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#addOutput(net.nooj4nlp.engine.Gram, java.lang.String, java.util.ArrayList, java.util.HashMap)}
	 * .
	 */
	@Test
	public void testAddOutput()
	{
		Gram g = createGram();

		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add("firstLabel");
		aVocab.add("secondLabel");
		aVocab.add("thirdLabel");
		aVocab.add("fourthLabel");

		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put("<E>firstLabel", 1);
		hVocab.put("secondLabel", 2);
		hVocab.put("thirdLabel", 3);
		hVocab.put("fourthLabel", 4);
		hVocab.put("newLabel", 5);

		String labelOutput = "firstLabel";

		Gram out = Gram.addOutput(g, labelOutput, aVocab, hVocab);
		Assert.assertNotNull(out);
		Assert.assertEquals(g.states.size() + 4, out.states.size());

		// System.out.println(out.states.get(5).IdLabels);
		Assert.assertEquals(5, out.states.get(5).IdLabels.size());

		labelOutput = "secondLabel";

		out = Gram.addOutput(g, labelOutput, aVocab, hVocab);
		Assert.assertNotNull(out);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#addTransition(int, int, int)}.
	 */
	@Test
	public void testAddTransitionIntIntInt()
	{
		Gram g = createGram();

		int src = 2;
		int dst = 100;
		int idLabel = 30;

		// System.out.println(g.states.get(src).IdLabels);
		// System.out.println(g.states.get(src).Dests);

		Assert.assertEquals(4, g.states.get(src).IdLabels.size());
		Assert.assertEquals(4, g.states.get(src).Dests.size());

		g.addTransition(src, dst, idLabel);

		Assert.assertEquals(5, g.states.get(src).IdLabels.size());
		Assert.assertEquals(5, g.states.get(src).Dests.size());

		// System.out.println(g.states.get(src).IdLabels);
		// System.out.println(g.states.get(src).Dests);

		// For given order number higher than size of states, exception must be thrown
		try
		{
			g.addTransition(100, dst, idLabel);
			Assert.assertTrue(false);
		}
		catch (IndexOutOfBoundsException e)
		{
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#transfer(net.nooj4nlp.engine.Gram, int, int)}.
	 */
	@Test
	public void testTransfer()
	{
		Gram g = createGram();

		Gram g2 = new Gram(2);
		State first = g2.states.get(0);
		ArrayList<Integer> dests = new ArrayList<Integer>(5);
		ArrayList<Integer> idLabels = new ArrayList<Integer>(5);
		for (int i = 1; i < 6; i++)
		{
			dests.add(i);
			idLabels.add(i * 2);
		}
		first.Dests = dests;
		first.IdLabels = idLabels;

		ArrayList<Integer> secondDests = new ArrayList<Integer>(5);
		ArrayList<Integer> secondLabels = new ArrayList<Integer>(5);
		State second = g2.states.get(1);
		for (int i = 6; i < 11; i++)
		{
			secondDests.add(i);
			secondLabels.add(i * 2);
		}
		second.Dests = secondDests;
		second.IdLabels = secondLabels;

		int anchor = 2;
		int inode = 200;

		// for (int i = 0; i < 5; i++)
		// {
		// System.out.println(" i = " + i + " " + g.states.get(i).IdLabels);
		// System.out.println(" i = " + i + " " + g.states.get(i).Dests);
		// }

		Assert.assertEquals(4, g.states.get(2).IdLabels.size());
		Assert.assertEquals(4, g.states.get(2).Dests.size());

		g.transfer(g2, anchor, inode);

		Assert.assertEquals(9, g.states.get(2).IdLabels.size());
		Assert.assertEquals(9, g.states.get(2).Dests.size());

		// for (int i = 0; i < 5; i++)
		// {
		// System.out.println(" i = " + i + " " + g.states.get(i).IdLabels);
		// System.out.println(" i = " + i + " " + g.states.get(i).Dests);
		// }
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#token(java.lang.String, java.util.ArrayList, java.util.HashMap)}.
	 */
	@Test
	public void testToken()
	{
		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add("firstLabel");
		aVocab.add("secondLabel");
		aVocab.add("thirdLabel");
		aVocab.add("fourthLabel");

		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put("firstLabel", 1);
		hVocab.put("secondLabel", 2);
		hVocab.put("thirdLabel", 3);
		hVocab.put("fourthLabel", 4);
		hVocab.put("newLabel", 5);

		String token = "firstLabel";

		Gram g = Gram.token(token, aVocab, hVocab);

		Assert.assertNotNull(g);

		State s = g.states.get(0);
		Assert.assertTrue(s.IdLabels.contains(hVocab.get(token)));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#concatenation(net.nooj4nlp.engine.Gram)}.
	 */
	@Test
	public void testConcatenation()
	{
		Gram g1 = createGram();
		int s1 = g1.states.size();
		Gram g2 = createGram();
		int s2 = g2.states.size();

		g1.concatenation(g2);

		Assert.assertEquals(s1 + s2 + 2, g1.states.size());
		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#disjunction(net.nooj4nlp.engine.Gram)}.
	 */
	@Test
	public void testDisjunction()
	{
		Gram g1 = createGram();
		int s1 = g1.states.size();
		Gram g2 = createGram();
		int s2 = g2.states.size();

		g1.disjunction(g2);

		Assert.assertEquals(s1 + s2 + 2, g1.states.size());
		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#disjunctions(java.util.ArrayList)}.
	 */
	@Test
	public void testDisjunctions()
	{
		Gram g1 = createGram();
		int s1 = g1.states.size();

		ArrayList<Gram> grams = new ArrayList<Gram>(3);

		Gram g2 = createGram();
		int s2 = g2.states.size();

		grams.add(g2);
		grams.add(g2);
		grams.add(g2);

		g1.disjunctions(grams);

		Assert.assertNotNull(g1);
		Assert.assertEquals(s1 + 3 * s2 + 2, g1.states.size());
		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#concatenations(java.util.ArrayList)}.
	 */
	@Test
	public void testConcatenations()
	{
		Gram g1 = createGram();
		int s1 = g1.states.size();

		ArrayList<Gram> grams = new ArrayList<Gram>(3);

		Gram g2 = createGram();
		int s2 = g2.states.size();

		grams.add(g2);
		grams.add(g2);
		grams.add(g2);

		g1.concatenations(grams);
		Assert.assertNotNull(g1);
		Assert.assertEquals(s1 + 3 * s2 + 2, g1.states.size());
		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#kleene()}.
	 */
	@Test
	public void testKleene()
	{
		Gram g = createGram();
		int s = g.states.size();

		g.kleene();
		Assert.assertNotNull(g);
		Assert.assertEquals(s + 2, g.states.size());
		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#prepareForParsing()}.
	 */
	@Test
	public void testPrepareForParsing()
	{
		Gram g = createGram();

		g.prepareForParsing();
		Assert.assertNotNull(g);

		// TODO make a better test
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#debugPrepareForParsing()}.
	 */
	@Test
	public void testDebugPrepareForParsing()
	{
		Gram g = createGram();

		Assert.assertNotNull(g.vocab);

		g.debugPrepareForParsing();
		Assert.assertNotNull(g);

		// System.out.println(g.VocabIn);
		// System.out.println(g.VocabOut);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#prepareForParsingNoDeterm()}.
	 */
	@Test
	public void testPrepareForParsingNoDeterm()
	{
		Gram g = createGram();

		Assert.assertNotNull(g.vocab);

		g.prepareForParsingNoDeterm();
		Assert.assertNotNull(g);

		// System.out.println(g.VocabIn);
		// System.out.println(g.VocabOut);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#determ()}.
	 */
	@Test
	public void testDeterm()
	{
		Gram g = createGram();

		g.determ();
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#lookForVariable(java.lang.String)}.
	 */
	@Test
	public void testLookForVariable()
	{
		Gram g = createGram();

		String var = "nataly";

		try
		{
			// This will throw an exception because g.VocabIn is null
			g.lookForVariable(var);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		g.vocabIn = new ArrayList<String>();
		g.vocabIn.add("one");
		g.vocabIn.add("two");
		g.vocabIn.add("three");
		g.vocabIn.add("four");
		g.vocabIn.add("five");
		g.vocabIn.add("six");
		g.vocabIn.add("seven");
		Assert.assertTrue(g.lookForVariable(var) == -1);

		g.vocabIn.set(2, "$(nataly");
		Assert.assertTrue(g.lookForVariable(var) != -1);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#exploreForVariable(int, int, java.lang.String, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testExploreForVariable()
	{
		Gram g = createGram();

		int initialState = 0;
		int stateNb = 2;
		String varName = "nataly";
		int recLevel = 0;
		RefObject<Gram> resGram = new RefObject<Gram>(new Gram());

		try
		{
			// This will throw NullPointerException because g.VocabIn is null
			g.exploreForVariable(initialState, stateNb, varName, recLevel, resGram);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		g.vocabIn = new ArrayList<String>();
		g.vocabIn.add("one");
		g.vocabIn.add("two");
		g.vocabIn.add("three");
		g.vocabIn.add("four");
		g.vocabIn.add("five");
		g.vocabIn.add("six");
		g.vocabIn.add("seven");

		try
		{
			// This will throw IndexOutOfBoundsException because resGram.states is empty
			g.exploreForVariable(initialState, stateNb, varName, recLevel, resGram);
		}
		catch (IndexOutOfBoundsException e)
		{
			Assert.assertTrue(true);
		}

		resGram.argvalue.states.add(new State());

		Assert.assertEquals(0, resGram.argvalue.states.get(0).AllIdLabels.size());

		g.exploreForVariable(initialState, stateNb, varName, recLevel, resGram);
		Assert.assertNotNull(resGram.argvalue);

		Assert.assertEquals(1, resGram.argvalue.states.get(0).AllIdLabels.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#getGramFromVariableDefinition(java.lang.String)}.
	 */
	@Test
	public void testGetGramFromVariableDefinition()
	{
		Gram g = createGram();

		String varName = "nataly";
		try
		{
			// This will throw NullPointerException because g.VocabIn is null
			g.getGramFromVariableDefinition(varName);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		g.vocabIn = new ArrayList<String>();
		g.vocabIn.add("one");
		g.vocabIn.add("two");
		g.vocabIn.add("three");
		g.vocabIn.add("four");
		g.vocabIn.add("five");
		g.vocabIn.add("six");
		g.vocabIn.add("seven");

		// Since varName is not found, resulting gram is null
		Assert.assertNull(g.getGramFromVariableDefinition(varName));

		// TODO make a better test
		// g.VocabIn.set(2, "$(nataly");
		// Assert.assertNotNull(g.getGramFromVariableDefinition(varName));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Gram#nullable(net.nooj4nlp.engine.Grammar)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNullable()
	{
		Gram g = createGram();

		Grammar grammar = new Grammar();
		grammar.grams.put("Main", g);

		Assert.assertFalse(g.nullable(grammar));

		grammar.grams.remove("Main");
		try
		{
			// This will throw an exception because g.IsTerminal is null
			g.nullable(grammar);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		g.isTerminal = new ArrayList<Boolean>();
		Assert.assertFalse(g.nullable(grammar));

		g.isTerminal.add(true);
		Assert.assertTrue(g.nullable(grammar));

		g.isTerminal.set(0, false);
		try
		{
			// This will throw an exception because g.VocabIn is null
			g.nullable(grammar);
		}
		catch (NullPointerException e)
		{
			Assert.assertTrue(true);
		}

		g.vocabIn = new ArrayList<String>();
		try
		{
			// This will throw an exception because g.VocabIn is empty
			g.nullable(grammar);
		}
		catch (IndexOutOfBoundsException e)
		{
			Assert.assertTrue(true);
		}

		g.vocabIn.add("one");
		g.vocabIn.add("two");
		g.vocabIn.add("three");
		g.vocabIn.add("four");
		g.vocabIn.add("five");
		g.vocabIn.add("six");
		g.vocabIn.add("seven");
		Assert.assertFalse(g.nullable(grammar));

		g.vocabIn.set(2, "<E>");
		g.isTerminal.add(false);
		g.isTerminal.add(false);
		g.isTerminal.add(true);
		Assert.assertTrue(g.nullable(grammar));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#computeFirst(net.nooj4nlp.engine.Engine, net.nooj4nlp.engine.Grammar)}.
	 */
	@Test
	public void testComputeFirst()
	{
		fail("Not yet implemented");
		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#processInflection(net.nooj4nlp.engine.Language, java.lang.String, java.lang.String, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testProcessInflection()
	{
		Language lan = new Language("en");
		String lemma = "This is a test lemma.";

		// Some of the supported commands:
		// <B> delete last character, <D> duplicate last character, <L> go left, <R> go right,
		// <N> go to the end of next word form, <P> go to the end of previous word form, <S> delete next character
		String commands = "<B>";
		RefObject<Integer> ires = new RefObject<Integer>(0);

		Assert.assertEquals(lemma.substring(0, lemma.length() - 1), Gram.processInflection(lan, lemma, commands, ires));
		Assert.assertEquals(lemma.length() - 1, ires.argvalue.intValue());

		commands = "<B><B><B>";

		Assert.assertEquals(lemma.substring(0, lemma.length() - 3), Gram.processInflection(lan, lemma, commands, ires));
		Assert.assertEquals(lemma.length() - 3, ires.argvalue.intValue());

		commands = "<D>";

		Assert.assertEquals(lemma.concat("."), Gram.processInflection(lan, lemma, commands, ires));
		Assert.assertEquals(lemma.length() + 1, ires.argvalue.intValue());
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#generateParaphrases(int, java.util.HashMap, int, java.util.Date, net.nooj4nlp.engine.GramType, net.nooj4nlp.engine.Language, boolean)}
	 * .
	 */
	@Test
	public void testGenerateParaphrasesIntHashMapOfStringGramIntDateGramTypeLanguageBoolean()
	{
		fail("Not yet implemented");
		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#generateParaphrases(int, java.util.HashMap, int, java.util.Date, net.nooj4nlp.engine.GramType, net.nooj4nlp.engine.Language, boolean, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGenerateParaphrasesIntHashMapOfStringGramIntDateGramTypeLanguageBooleanRefObjectOfArrayListRefObjectOfArrayList()
	{
		fail("Not yet implemented");
		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Gram#inflect(net.nooj4nlp.engine.Language, java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, java.util.HashMap)}
	 * .
	 */
	@Test
	public void testInflect()
	{
		fail("Not yet implemented");
		// TODO
	}

	private Gram createGram()
	{
		Gram g = new Gram();

		g.vocab = new ArrayList<String>(15);
		g.vocab.add("A/1");
		g.vocab.add("E");
		g.vocab.add("I");
		g.vocab.add("O");
		g.vocab.add("U");
		g.vocab.add("AA");
		g.vocab.add("EE");
		g.vocab.add("II");
		g.vocab.add("OO");
		g.vocab.add("UU");
		g.vocab.add("AAA");
		g.vocab.add("EEE");
		g.vocab.add("III");
		g.vocab.add("OOO/2");
		g.vocab.add("UUU/3");

		ArrayList<Integer> idLabels = new ArrayList<Integer>();
		idLabels.add(0);
		idLabels.add(1);
		idLabels.add(2);
		idLabels.add(3);

		g.states = new ArrayList<State>(5);
		for (int i = 0; i < 5; i++)
		{
			g.states.add(new State());
			State s = g.states.get(i);
			s.IdLabels = new ArrayList<Integer>(4);
			s.Dests = new ArrayList<Integer>(4);
			for (int j = 0; j < 4; j++)
			{
				s.IdLabels.add(idLabels.get(j) * 2 / (i + 1));
				s.Dests.add(idLabels.get(j) * 3 / (i + 1));
			}
			// System.out.println(s.IdLabels);
			// System.out.println(s.Dests);
		}

		return g;
	}
}
