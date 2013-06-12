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

import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author IMPCS
 * 
 */
public class LanguageTest
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
	 * Test method for {@link net.nooj4nlp.engine.Language#Language(java.lang.String)}.
	 */
	@Test
	public void testLanguage()
	{
		Language lan1 = new Language("");
		Locale defaultLocale = new Locale("en", "US");

		Assert.assertEquals("", lan1.isoName);
		Assert.assertEquals(defaultLocale, lan1.locale);
		Assert.assertEquals("Unknown", lan1.engName);
		Assert.assertEquals("Unknown", lan1.natName);
		Assert.assertFalse(lan1.rightToLeft);

		Language lan2 = new Language("br");

		Assert.assertEquals("br", lan2.isoName);
		Assert.assertEquals(defaultLocale, lan2.locale);
		Assert.assertEquals("Berber", lan2.engName);
		Assert.assertEquals("Berber", lan2.natName);

		Language lan3 = new Language("sw");

		Assert.assertEquals("sw", lan3.isoName);
		defaultLocale = new Locale("sw", "KE");
		Assert.assertEquals(defaultLocale, lan3.locale);
		Assert.assertEquals("Kiswahili", lan3.engName);
		Assert.assertEquals("Kiswahili", lan3.natName);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#getAllLanguages()}.
	 */
	@Test
	public void testGetAllLanguages()
	{
		String[] allLanguages = Language.getAllLanguages();

		Assert.assertEquals("ac", allLanguages[0]);
		Assert.assertEquals("zh", allLanguages[allLanguages.length - 1]);

		// System.out.println(allLanguages.length);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isALanguage(java.lang.String)}.
	 */
	@Test
	public void testIsALanguage()
	{
		Assert.assertTrue(Language.isALanguage("ac"));
		Assert.assertFalse(Language.isALanguage("aa"));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isVowel(char)}.
	 */
	@Test
	public void testIsVowel()
	{
		Assert.assertTrue(Language.isVowel('A'));
		Assert.assertFalse(Language.isVowel('B'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isLetter(char)}.
	 */
	@Test
	public void testIsLetter()
	{
		Assert.assertTrue(Language.isLetter('R'));
		Assert.assertFalse(Language.isLetter('9'));
		Assert.assertTrue(Language.isLetter('\u064C'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isNotAccented(char)}.
	 */
	@Test
	public void testIsNotAccented()
	{
		Assert.assertTrue(Language.isNotAccented('a'));
		Assert.assertTrue(Language.isNotAccented('\u0391'));
		Assert.assertFalse(Language.isNotAccented('\u0390'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isAccented(char)}.
	 */
	@Test
	public void testIsAccented()
	{
		Assert.assertTrue(Language.isAccented('\u00D3'));
		Assert.assertFalse(Language.isAccented('O'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isKatakana(char)}.
	 */
	@Test
	public void testIsKatakana()
	{
		Assert.assertTrue(Language.isKatakana('\u30A2'));
		Assert.assertTrue(Language.isKatakana('\u30FF'));
		Assert.assertFalse(Language.isKatakana('\u31A2'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isUniHan(char)}.
	 */
	@Test
	public void testIsUniHan()
	{
		Assert.assertTrue(Language.isUniHan('\u4E00'));
		Assert.assertFalse(Language.isUniHan('\u3300'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isHiragana(char)}.
	 */
	@Test
	public void testIsHiragana()
	{
		Assert.assertTrue(Language.isHiragana('\u3040'));
		Assert.assertFalse(Language.isHiragana('\u3039'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#finalize(char)}.
	 */
	@Test
	public void testFinalizeChar()
	{
		Assert.assertEquals('\u05da', Language.finalize('\u05db'));
		Assert.assertEquals('\u05de', Language.finalize('\u05dd'));
		Assert.assertEquals('R', Language.finalize('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toNoAccent(char)}.
	 */
	@Test
	public void testToNoAccent()
	{
		Assert.assertEquals('A', Language.toNoAccent('\u00c2'));
		Assert.assertEquals('o', Language.toNoAccent('\u00f6'));
		Assert.assertEquals('R', Language.toNoAccent('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toAcute(char)}.
	 */
	@Test
	public void testToAcute()
	{
		Language l = new Language("en");

		Assert.assertEquals('\u00cd', l.toAcute('I'));
		Assert.assertEquals('\u0388', l.toAcute('\u0395'));
		Assert.assertEquals('R', l.toAcute('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toGrave(char)}.
	 */
	@Test
	public void testToGrave()
	{
		Language l = new Language("en");

		Assert.assertEquals('\u00cc', l.toGrave('I'));
		Assert.assertEquals('\u1fc8', l.toGrave('\u0395'));
		Assert.assertEquals('R', l.toGrave('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toCircumflex(char)}.
	 */
	@Test
	public void testToCircumflex()
	{
		Language l = new Language("en");

		Assert.assertEquals('\u00ce', l.toCircumflex('I'));
		Assert.assertEquals('\u1fc6', l.toCircumflex('\u03b7'));
		Assert.assertEquals('R', l.toCircumflex('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toTrema(char)}.
	 */
	@Test
	public void testToTrema()
	{
		Language l = new Language("en");

		Assert.assertEquals('\u00cf', l.toTrema('I'));
		Assert.assertEquals('\u1f18', l.toTrema('\u0395'));
		Assert.assertEquals('R', l.toTrema('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toIota(char)}.
	 */
	@Test
	public void testToIota()
	{
		Language l = new Language("el");

		Assert.assertEquals('\u1f85', l.toIota('\u1f05'));
		Assert.assertEquals('\u1f9e', l.toIota('\u1f2e'));
		Assert.assertEquals('R', l.toIota('R'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isLower(java.lang.String)}.
	 */
	@Test
	public void testIsLower()
	{
		String word = "alllower";
		Assert.assertTrue(Language.isLower(word));

		word = "Not all lower";
		Assert.assertFalse(Language.isLower(word));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isUpper(java.lang.String)}.
	 */
	@Test
	public void testIsUpper()
	{
		String word = "ALLUPPER";
		Assert.assertTrue(Language.isUpper(word));

		word = "Not all upper";
		Assert.assertFalse(Language.isUpper(word));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isCapital(java.lang.String)}.
	 */
	@Test
	public void testIsCapital()
	{
		String word = "Capitalized";
		Assert.assertTrue(Language.isCapital(word));

		word = "ALL UPPER - NOT CAPITALIZED";
		Assert.assertFalse(Language.isCapital(word));

		word = "not capitalized";
		Assert.assertFalse(Language.isCapital(word));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#isACompound(java.lang.String)}.
	 */
	@Test
	public void testIsACompound()
	{
		Language l = new Language("en");

		String word = "A compound";
		Assert.assertTrue(l.isACompound(word));

		word = "Notacompound";
		Assert.assertFalse(l.isACompound(word));

		word = "Compound2?";
		Assert.assertTrue(l.isACompound(word));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#toUpper(java.lang.String)}.
	 */
	@Test
	public void testToUpper()
	{
		String word = "all lower";
		String[] wordUpper = Language.toUpper(word);

		Assert.assertEquals("All lower", wordUpper[0]);
		Assert.assertEquals("ALL LOWER", wordUpper[1]);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Language#loadCharacterVariants(java.lang.String, java.lang.StringBuilder)}.
	 */
	@Test
	public void testLoadCharacterVariants()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#doLettersMatch(char, char)}.
	 */
	@Test
	public void testDoLettersMatch()
	{
		Language l = new Language("en");

		Assert.assertTrue(l.doLettersMatch('b', 'b'));
		Assert.assertTrue(l.doLettersMatch('B', 'b'));

		Assert.assertTrue(l.doLettersMatch('A', '\u00c2'));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#doWordFormsMatch(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDoWordFormsMatch()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#parseSequenceOfTokens(java.lang.String)}.
	 */
	@Test
	public void testParseSequenceOfTokens()
	{
		Language l = new Language("en");

		String forParsing = "This string is to be parsed";
		String[] afterParsing = l.parseSequenceOfTokens(forParsing);

		Assert.assertTrue(afterParsing.length != 0);
		Assert.assertEquals(6, afterParsing.length);

		// for (int i = 0; i < afterParsing.length; i++)
		// System.out.println(afterParsing[i]);

		forParsing = "What about this string? <B> Bolded </B>";
		afterParsing = null;
		afterParsing = l.parseSequenceOfTokens(forParsing);

		Assert.assertTrue(afterParsing.length != 0);
		Assert.assertEquals(8, afterParsing.length);

		// for (int i = 0; i < afterParsing.length; i++)
		// System.out.println(afterParsing[i]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#parseSequenceOfTokensAndMetaNodes(java.lang.String)}.
	 */
	@Test
	public void testParseSequenceOfTokensAndMetaNodes()
	{
		Language l = new Language("en");

		String forParsing = "This string is to be parsed";
		String[] afterParsing = l.parseSequenceOfTokensAndMetaNodes(forParsing);

		Assert.assertTrue(afterParsing.length != 0);
		Assert.assertEquals(6, afterParsing.length);

		// for (int i = 0; i < afterParsing.length; i++)
		// System.out.println(afterParsing[i]);

		forParsing = "What about this string? <B> \\B Bolded ($try#)</B>";
		afterParsing = null;
		afterParsing = l.parseSequenceOfTokensAndMetaNodes(forParsing);

		Assert.assertTrue(afterParsing.length != 0);
		Assert.assertEquals(12, afterParsing.length);

		// for (int i = 0; i < afterParsing.length; i++)
		// System.out.println(afterParsing[i]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#nbOfDagueshShinSinDotsIn(java.lang.StringBuilder, int, int)}.
	 */
	@Test
	public void testNbOfDagueshShinSinDotsIn()
	{
		StringBuilder sb = new StringBuilder("Text without shin sin dots.");
		int ipos = 15;
		int val = 4;

		System.out.println(Language.nbOfDagueshShinSinDotsIn(sb, ipos, val));

		sb = new StringBuilder("Text \u05C1 with shin sin dots \u05C1.");

		System.out.println(Language.nbOfDagueshShinSinDotsIn(sb, ipos, val));

		// TODO ask about this method
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Language#processInflection(java.lang.String, java.lang.StringBuilder, int, int)}.
	 */
	@Test
	public void testProcessInflection()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#inflectionCommands()}.
	 */
	@Test
	public void testInflectionCommands()
	{
		Language l = new Language("en");
		System.out.println(l.inflectionCommands());

		l = new Language("hu");
		System.out.println(l.inflectionCommands());

		l = new Language("de");
		System.out.println(l.inflectionCommands());

		l = new Language("el");
		System.out.println(l.inflectionCommands());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Language#sortTexts(java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSortTexts()
	{
		Language l = new Language("en");

		String text1 = "First text";
		String text2 = "Second text";

		Assert.assertTrue(l.sortTexts(text1, text2, true) < 0);
		Assert.assertTrue(l.sortTexts(text2, text1, true) > 0);

		text2 = null;
		text2 = "First text";
		Assert.assertTrue(l.sortTexts(text1, text2, true) == 0);

		text2 = null;
		text2 = "FIRST TEXT";
		Assert.assertTrue(l.sortTexts(text1, text2, true) == 0);
		Assert.assertTrue(l.sortTexts(text1, text2, false) < 0);
	}

}
