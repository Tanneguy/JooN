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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
public class DicTest
{
	private static final String PATH_TO_RESOURCES = "src/test/resources/Dic/";

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
	 * Test method for {@link net.nooj4nlp.engine.Dic#initLoad(java.io.OutputStreamWriter, java.lang.String)}.
	 */
	@Test
	public void testInitLoad()
	{
		String fileName = PATH_TO_RESOURCES + "init.txt";

		OutputStreamWriter osw = null;
		try
		{
			osw = new OutputStreamWriter(new FileOutputStream(fileName, true));
			try
			{
				Dic.initLoad(osw, "en");
			}
			catch (IOException e)
			{
				Assert.assertFalse(true);
			}
		}
		catch (FileNotFoundException e1)
		{
			Assert.assertTrue(false);
		}
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseFactorizedInfo(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseFactorizedInfo()
	{
		String line0 = "lemma=<info>";
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> info = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[1]);

		Assert.assertFalse(Dic.parseFactorizedInfo(line0, lemma, info, category, features));

		System.out.println(lemma.argvalue + " : " + info.argvalue);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#isALexicalConstraint(java.lang.String)}.
	 */
	@Test
	public void testIsALexicalConstraint()
	{
		String s = null;
		Assert.assertFalse(Dic.isALexicalConstraint(s));

		s = "<tables=table,N>";
		Assert.assertTrue(Dic.isALexicalConstraint(s));

		s = "<tables,table,N+class=Conc+f+p>";
		Assert.assertFalse(Dic.isALexicalConstraint(s));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#isALexicalSymbol(java.lang.String)}.
	 */
	@Test
	public void testIsALexicalSymbol()
	{
		String s = null;

		try
		{
			// In case of a null string, exception is thrown
			Dic.isALexicalSymbol(s);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(true);
		}

		s = "<tables=table,N>";

		Assert.assertFalse(Dic.isALexicalSymbol(s));

		s = "<tables,table,N+class=Conc+f+p>";

		Assert.assertTrue(Dic.isALexicalSymbol(s));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#parseSequenceOfSymbols(java.lang.String)}.
	 */
	@Test
	public void testParseSequenceOfSymbols()
	{
		String sequence = "first sequence";
		Assert.assertTrue(Dic.parseSequenceOfSymbols(sequence).length != 0);

		sequence = "<second sequence>";
		Assert.assertTrue(Dic.parseSequenceOfSymbols(sequence).length == 1);

		sequence += " <third sequence>";
		Assert.assertTrue(Dic.parseSequenceOfSymbols(sequence).length == 2);

		for (int i = 0; i < Dic.parseSequenceOfSymbols(sequence).length; i++)
			System.out.println(Dic.parseSequenceOfSymbols(sequence)[i]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#isThereALexicalConstraint(java.util.ArrayList)}.
	 */
	@Test
	public void testIsThereALexicalConstraint()
	{
		ArrayList<String> symbols = new ArrayList<String>();

		symbols.add("<tables=table,N>");
		symbols.add("<places=place,N>");

		Assert.assertTrue(Dic.isThereALexicalConstraint(symbols));

		symbols.set(0, "<tables,table,N+class=Conc+f+p>");
		symbols.set(1, "<places,place,N+Conc+f+p>");

		Assert.assertFalse(Dic.isThereALexicalConstraint(symbols));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#noComment(java.lang.String)}.
	 */
	@Test
	public void testNoComment()
	{
		String lineWithComments = "This is text with comments.\n#First line\n# Second line\n Next line without comments.";

		System.out.println(lineWithComments);
		System.out.println(Dic.noComment(lineWithComments));

		Assert.assertNotSame(lineWithComments, Dic.noComment(lineWithComments));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#getRule(java.lang.String, int, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGetRule()
	{
		String line = "N_Distribution = Abst | Anl | AnlColl | Coll | Conc | Hum | HumColl | PR | Ntime | Ntps | Unit;";
		int ibuf = 0;
		RefObject<String> category = new RefObject<String>("");
		RefObject<String> property = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[10]);
		RefObject<String> errMessage = new RefObject<String>("");

		Assert.assertTrue(Dic.getRule(line, ibuf, category, property, features, errMessage) != -1);
		Assert.assertNull(errMessage.argvalue);
		// for (int i = 0; i < features.argvalue.length; i++)
		// System.out.println(i + ". " + features.argvalue[i]);
		// System.out.println(category.argvalue + " " + property.argvalue);

		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		line = "Something unexpected!";
		Assert.assertTrue(Dic.getRule(line, ibuf, category, property, features, errMessage) == -1);
		Assert.assertNotNull(errMessage.argvalue);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#getProperty(java.lang.String, java.lang.String, java.util.HashMap, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGetProperty()
	{
		String feature = "prop=value";
		String category = "cat";
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("cat_prop2", "value");

		RefObject<String> propertyName = new RefObject<String>(new String());
		RefObject<String> propertyValue = new RefObject<String>(new String());

		Assert.assertTrue(Dic.getProperty(feature, category, properties, propertyName, propertyValue));
		// System.out.println("Prop name: " + propertyName.argvalue + ", property value: " + propertyValue.argvalue);

		feature = "prop2";
		Assert.assertTrue(Dic.getProperty(feature, category, properties, propertyName, propertyValue));
		// System.out.println("Prop name: " + propertyName.argvalue + ", property value: " + propertyValue.argvalue);

		Assert.assertFalse(Dic.getProperty(feature, category, null, propertyName, propertyValue));

		feature = "prop3";
		Assert.assertFalse(Dic.getProperty(feature, category, properties, propertyName, propertyValue));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#normalizeInformation(java.lang.String, java.lang.String[], java.util.HashMap)}.
	 */
	@Test
	public void testNormalizeInformation()
	{
		String category = "the,DET";
		String[] features = new String[0];
		HashMap<String, String> properties = new HashMap<String, String>();

		ArrayList<String> infos = Dic.normalizeInformation(category, features, properties);
		Assert.assertTrue(infos.size() == 1);
		Assert.assertTrue(infos.get(0).equals(category));

		features = new String[1];
		category = "";
		features[0] = "table,N+singular";
		properties.put("+singular", "+Number=singular");

		infos = Dic.normalizeInformation(category, features, properties);
		Assert.assertTrue(infos.size() == 1);
		System.out.println(infos.get(0));

		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#normalizeLexicalEntry(java.lang.String, net.nooj4nlp.engine.Engine, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNormalizeLexicalEntry()
	{
		String line = "do,do,V+PR+1+2+s";

		Engine engine = new Engine(new Language("en"));
		engine.properties = new HashMap<String, String>();
		engine.properties.put("V_Pers", "1 | 2 | 3");
		engine.properties.put("V_Nb", "s | p");

		RefObject<ArrayList<String>> lines = new RefObject<ArrayList<String>>(new ArrayList<String>());

		Assert.assertTrue(Dic.normalizeLexicalEntry(line, engine, lines));
		// System.out.println(lines.argvalue.size());
		// TODO
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#normalizeLexemeSymbol(java.lang.String, net.nooj4nlp.engine.Engine, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testNormalizeLexemeSymbol()
	{
		// Valid line
		String lexeme = "<zz=has,have,V+Pr+Aux+3+s>";
		Engine engine = new Engine(new Language("en"));
		RefObject<ArrayList<String>> lexemes = new RefObject<ArrayList<String>>(new ArrayList<String>());

		Assert.assertTrue(Dic.normalizeLexemeSymbol(lexeme, engine, lexemes));
		// System.out.println(lexemes.argvalue.get(0));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getPropertyValue(java.lang.String)}.
	 */
	@Test
	public void testGetPropertyValueString()
	{
		String feature = "property=value";
		Assert.assertEquals("value", Dic.getPropertyValue(feature));

		feature = "another feature";
		Assert.assertEquals(feature, Dic.getPropertyValue(feature));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getPropertyValue(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetPropertyValueStringString()
	{
		String propertyName = "name";
		String information = "+try=test";

		Assert.assertNull(Dic.getPropertyValue(propertyName, information));

		information = "try+name=value";
		Assert.assertNotNull(Dic.getPropertyValue(propertyName, information));
		Assert.assertEquals("value", Dic.getPropertyValue(propertyName, information));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#cleanUpDoubleQuotes(java.lang.String)}.
	 */
	@Test
	public void testCleanUpDoubleQuotes()
	{
		String info = "potato,N+FR=\"pomme\" de \"terre\"";
		System.out.println(Dic.cleanUpDoubleQuotes(info));

		info = "+Prop=ba\"bla\"bla\"bla";
		System.out.println(Dic.cleanUpDoubleQuotes(info));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#protectComma(java.lang.String)}.
	 */
	@Test
	public void testProtectComma()
	{
		String entry = "Comma one, comma two, comma three, dot.";
		System.out.println(Dic.protectComma(entry));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELAS(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELASStringRefObjectOfStringRefObjectOfString()
	{
		// Valid line for testing
		String line = "artist,N+FLX=TABLE+Hum";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> info = new RefObject<String>("");

		Assert.assertTrue(Dic.parseDELAS(line, entry, info));
		Assert.assertEquals("artist", entry.argvalue);
		Assert.assertEquals("N+FLX=TABLE+Hum", info.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELAS(line, entry, info));

		// Invalid line - only entry, without info
		line = "artist";
		Assert.assertFalse(Dic.parseDELAS(line, entry, info));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELAS(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELASStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		// Valid line for testing
		String line = "tsar,N+Hum+FLX=TSAR";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String> features = new RefObject<String>("");

		Assert.assertTrue(Dic.parseDELAS(line, entry, category, features));
		Assert.assertEquals("tsar", entry.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("+Hum+FLX=TSAR", features.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELAS(line, entry, category, features));

		// Invalid line - only entry, without info
		line = "tsar";
		Assert.assertFalse(Dic.parseDELAS(line, entry, category, features));

		// Invalid line - entry, category not valid
		line = "tsar,n+Hum+FLX=TSAR";
		Assert.assertFalse(Dic.parseDELAS(line, entry, category, features));

		// Invalid line - entry, category, with features starting without '+'
		line = "tsar,N-Hum";
		Assert.assertFalse(Dic.parseDELAS(line, entry, category, features));

		// Valid line - entry, category, without features
		line = "tsar,N";
		Assert.assertTrue(Dic.parseDELAS(line, entry, category, features));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELASFeatureArray(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELASFeatureArray()
	{
		// Valid line for testing
		String line = "tsar,N+Hum+FLX=TSAR";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[2]);

		Assert.assertTrue(Dic.parseDELASFeatureArray(line, entry, category, features));
		Assert.assertEquals("tsar", entry.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("Hum", features.argvalue[0]);
		Assert.assertEquals("FLX=TSAR", features.argvalue[1]);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELASFeatureArray(line, entry, category, features));

		// Invalid line - entry, category not valid
		line = "tsar,n+Hum+FLX=TSAR";
		Assert.assertFalse(Dic.parseDELASFeatureArray(line, entry, category, features));

		// Invalid line - entry, category, with features starting without '+'
		line = "tsar,N-Hum";
		Assert.assertFalse(Dic.parseDELASFeatureArray(line, entry, category, features));

		// Valid line - entry, category, without features
		line = "tsar,N";
		Assert.assertTrue(Dic.parseDELASFeatureArray(line, entry, category, features));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseContracted(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseContracted()
	{
		// Valid line for testing
		String line = "I'm,<I,I,PRO+1+s><am,be,V+PR+1+s>+UNAMB";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> info = new RefObject<String>("");

		Assert.assertTrue(Dic.parseContracted(line, entry, info));
		Assert.assertEquals("I'm", entry.argvalue);
		Assert.assertEquals("<I,I,PRO+1+s><am,be,V+PR+1+s>+UNAMB", info.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseContracted(line, entry, info));

		// Invalid line - just entry
		line = "I'm";
		Assert.assertFalse(Dic.parseContracted(line, entry, info));

		// Invalid line - entry, info not starting with '<'
		line = "tsar,N";
		Assert.assertFalse(Dic.parseContracted(line, entry, info));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELAF(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELAFStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		// Valid line for testing
		String line = "csar,tsar,N+Hum+FLX=TSAR";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> info = new RefObject<String>("");

		Assert.assertTrue(Dic.parseDELAF(line, entry, lemma, info));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("tsar", lemma.argvalue);
		Assert.assertEquals("N+Hum+FLX=TSAR", info.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, info));

		// Invalid line - just entry, without lemma
		line = "csar";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, info));

		// Valid line - just entry and lemma
		line = "csar, tsar";
		Assert.assertTrue(Dic.parseDELAF(line, entry, lemma, info));

		// Invalid line - entry and lemma, with comma - missing info
		line = "csar, tsar,";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, info));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELAF(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELAFStringRefObjectOfStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		// Valid line for testing
		String line = "csar,tsar,N+Hum+FLX=TSAR";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String> features = new RefObject<String>("");

		Assert.assertTrue(Dic.parseDELAF(line, entry, lemma, category, features));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("tsar", lemma.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("+Hum+FLX=TSAR", features.argvalue);

		// Valid line - when lemma is missing, it's the same as entry
		line = "csar,N+Hum+FLX=TSAR";

		Assert.assertTrue(Dic.parseDELAF(line, entry, lemma, category, features));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("csar", lemma.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("+Hum+FLX=TSAR", features.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));

		// Invalid line - just entry, without lemma
		line = "csar";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));

		// Invalid line - entry with missing lemma and closing apostrophes missing in features
		line = "doesn't, N+Dom=\"cou";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));

		// Invalid line - entry with missing lemma and closing brackets missing in features
		line = "doesn't, <do,V><not,ADV";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));

		// Invalid line - no category
		line = "csar,tsar,Hum+FLX=TSAR";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));

		// Valid line - missing features
		line = "csar,tsar,N";
		Assert.assertTrue(Dic.parseDELAF(line, entry, lemma, category, features));

		// Invalid line - features not starting with '+'
		line = "csar,tsar,N,";
		Assert.assertFalse(Dic.parseDELAF(line, entry, lemma, category, features));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseDELAFFeatureArray(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseDELAFFeatureArray()
	{
		// Valid line for testing
		String line = "csar,tsar,N+Hum+FLX=TSAR";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[2]);

		Assert.assertTrue(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("tsar", lemma.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("Hum", features.argvalue[0]);
		Assert.assertEquals("FLX=TSAR", features.argvalue[1]);

		// Valid line - when lemma is missing, it's the same as entry
		line = "csar,N+Hum+FLX=TSAR";

		Assert.assertTrue(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("csar", lemma.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertEquals("Hum", features.argvalue[0]);
		Assert.assertEquals("FLX=TSAR", features.argvalue[1]);

		// Valid line - with features missing
		line = "csar,tsar,N";

		Assert.assertTrue(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));
		Assert.assertEquals("csar", entry.argvalue);
		Assert.assertEquals("tsar", lemma.argvalue);
		Assert.assertEquals("N", category.argvalue);
		Assert.assertNull(features.argvalue);

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Invalid line - just entry, without lemma
		line = "csar";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Invalid line - entry with missing lemma and closing apostrophes missing in features
		line = "doesn't, N+Dom=\"cou";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Invalid line - entry with missing lemma and closing brackets missing in features
		line = "doesn't, <do,V><not,ADV";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Invalid line - no category
		line = "csar,tsar,Hum+FLX=TSAR";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Valid line - missing features
		line = "csar,tsar,N";
		Assert.assertTrue(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));

		// Invalid line - features not starting with '+'
		line = "csar,tsar,N,";
		Assert.assertFalse(Dic.parseDELAFFeatureArray(line, entry, lemma, category, features));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#isALexemeSymbol(java.lang.String)}.
	 */
	@Test
	public void testIsALexemeSymbol()
	{
		// Valid line
		String lexeme = "<tables,table,N+plural>";
		Assert.assertTrue(Dic.isALexemeSymbol(lexeme));

		// Invalid line
		lexeme = "<tables,table,N+plural";
		Assert.assertFalse(Dic.isALexemeSymbol(lexeme));

		// Invalid line - nothing to be parsed
		lexeme = "<>";
		Assert.assertFalse(Dic.isALexemeSymbol(lexeme));

		// Invalid line - containing entry only
		lexeme = "<tables>";
		Assert.assertFalse(Dic.isALexemeSymbol(lexeme));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#isALexicalAnnotation(java.lang.String)}.
	 */
	@Test
	public void testIsALexicalAnnotation()
	{
		// Valid line for testing
		String line = "csar,tsar,N+Hum+FLX=TSAR";
		Assert.assertTrue(Dic.isALexicalAnnotation(line));

		// Invalid line - nothing to be parsed
		line = "";
		Assert.assertFalse(Dic.isALexicalAnnotation(line));

		// Invalid line - just entry, without lemma
		line = "csar";
		Assert.assertFalse(Dic.isALexicalAnnotation(line));

		// Valid line - just entry and lemma
		line = "csar, tsar";
		Assert.assertTrue(Dic.isALexicalAnnotation(line));

		// Invalid line - entry and lemma, with comma - missing info
		line = "csar, tsar,";
		Assert.assertFalse(Dic.isALexicalAnnotation(line));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseXmlInfo(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseXmlInfo()
	{
		// Test for valid parsing
		String info = "LEMMA=table+CAT=N+CASE+NOM+Conc+f+p";
		RefObject<String> entry = new RefObject<String>("");
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String> features = new RefObject<String>("");

		// This throws a NullException
		// String[] splitted = Dic.splitAllFeaturesWithPlus(info);
		// System.out.println(splitted.length);

		Assert.assertTrue(Dic.parseXmlInfo(info, entry, lemma, category, features));
		Assert.assertNull(entry.argvalue);
		Assert.assertNotNull(lemma.argvalue);
		Assert.assertNotNull(category.argvalue);
		Assert.assertNotNull(features.argvalue);
		// System.out.println("entry: " + entry.argvalue + "\nlemma: " + lemma.argvalue + "\ncategory: "
		// + category.argvalue + "\nfeatures: " + features.argvalue);

		// Name of the log file needed to add error messages to it
		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		// Test for invalid parsing - lemma will be invalid
		info = "LEMMA=";
		entry = new RefObject<String>("");
		lemma = new RefObject<String>("");
		category = new RefObject<String>("");
		features = new RefObject<String>("");

		Assert.assertFalse(Dic.parseXmlInfo(info, entry, lemma, category, features));
		Assert.assertNull(entry.argvalue);
		Assert.assertEquals(lemma.argvalue, "INVALIDLEMMA");
		Assert.assertNull(category.argvalue);
		Assert.assertNull(features.argvalue);
		// System.out.println("entry: " + entry.argvalue + "\nlemma: " + lemma.argvalue + "\ncategory: "
		// + category.argvalue + "\nfeatures: " + features.argvalue);

		// Test for invalid parsing - category will be invalid
		info = "LEMMA=table+CAT=N22+CASE+NOM+Conc+f+p";
		entry = new RefObject<String>("");
		lemma = new RefObject<String>("");
		category = new RefObject<String>("");
		features = new RefObject<String>("");

		Assert.assertTrue(Dic.parseXmlInfo(info, entry, lemma, category, features));
		Assert.assertNull(entry.argvalue);
		Assert.assertNotNull(lemma.argvalue);
		Assert.assertEquals(category.argvalue, "INVALIDCAT");
		Assert.assertNotNull(features.argvalue);
		// System.out.println("entry: " + entry.argvalue + "\nlemma: " + lemma.argvalue + "\ncategory: "
		// + category.argvalue + "\nfeatures: " + features.argvalue);

		// Test for invalid parsing - entry will be invalid
		info = "LEMMA=table+CAT=N+ENT=+CASE+NOM+Conc+f+p";
		entry = new RefObject<String>("");
		lemma = new RefObject<String>("");
		category = new RefObject<String>("");
		features = new RefObject<String>("");

		Assert.assertTrue(Dic.parseXmlInfo(info, entry, lemma, category, features));
		Assert.assertEquals(entry.argvalue, "INVALIDENTRY");
		Assert.assertNotNull(lemma.argvalue);
		Assert.assertNotNull(category.argvalue);
		Assert.assertNotNull(features.argvalue);
		// System.out.println("entry: " + entry.argvalue + "\nlemma: " + lemma.argvalue + "\ncategory: "
		// + category.argvalue + "\nfeatures: " + features.argvalue);

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#cleanupEntry(java.lang.String, boolean)}.
	 */
	@Test
	public void testCleanupEntry()
	{
		String text = "With commas, with commas, no more.";

		Assert.assertTrue(text.contains(","));
		Assert.assertFalse(Dic.cleanupEntry(text, false).contains(","));

		text = "<With commas, without commas...>";
		Assert.assertTrue(text.contains(","));
		Assert.assertTrue(text.contains("<"));
		Assert.assertFalse(Dic.cleanupEntry(text, true).contains(","));
		Assert.assertFalse(Dic.cleanupEntry(text, true).contains("<"));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#cleanupXmlEntry(java.lang.String)}.
	 */
	@Test
	public void testCleanupXmlEntry()
	{
		String text = "<With commas, without commas...><another one, yet>";

		Assert.assertTrue(text.contains(","));
		Assert.assertTrue(text.contains("<"));
		Assert.assertFalse(Dic.cleanupXmlEntry(text).contains(","));
		Assert.assertFalse(Dic.cleanupXmlEntry(text).contains("<"));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#cleanupXmlInfo(java.lang.String, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testCleanupXmlInfo()
	{
		String info = "Test line";
		RefObject<String> category = new RefObject<String>("CAT");

		Assert.assertNotNull(Dic.cleanupXmlInfo(info, category));
		Assert.assertEquals("TEST", category.argvalue);
		Assert.assertEquals("+line", Dic.cleanupXmlInfo(info, category));

		info = "Test2 line";
		Assert.assertNotNull(Dic.cleanupXmlInfo(info, category));
		Assert.assertEquals("TESTX", category.argvalue);
		Assert.assertEquals("+line", Dic.cleanupXmlInfo(info, category));

		info = "Testtt";
		Assert.assertNotNull(Dic.cleanupXmlInfo(info, category));
		Assert.assertEquals("TESTTT", category.argvalue);
		Assert.assertEquals("", Dic.cleanupXmlInfo(info, category));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#getFullVariableName(java.lang.String, int, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testGetFullVariableName()
	{
		String text = "Some text with name of variable: $var=BLA";
		int currentPosition = 33;
		RefObject<Integer> newPosition = new RefObject<Integer>(-1);

		String fullVariableName = Dic.getFullVariableName(text, currentPosition, newPosition);
		Assert.assertNotNull(fullVariableName);
		Assert.assertEquals(Integer.valueOf(37), newPosition.argvalue);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexicalConstraint(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexicalConstraintStringRefObjectOfStringRefObjectOfStringRefObjectOfStringRefObjectOfStringRefObjectOfStringRefObjectOfBoolean()
	{
		RefObject<String> left = new RefObject<String>("");
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[0]);
		RefObject<String> op = new RefObject<String>("");
		RefObject<Boolean> negation = new RefObject<Boolean>(false);

		// Testing invalid symbol - missing brackets
		String symbol = "not valid";
		Assert.assertFalse(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));

		// Testing invalid symbol - missing a bracket
		symbol = "<not valid also";
		Assert.assertFalse(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));

		// Testing invalid symbol - missing operator
		symbol = "<zz>";
		Assert.assertFalse(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));

		// Testing valid symbol - without negation, category, features...
		symbol = "<zz=has,have,V+Pr+Aux+3+s>";

		Assert.assertTrue(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));
		Assert.assertEquals("zz", left.argvalue);
		Assert.assertEquals("=", op.argvalue);
		Assert.assertEquals(Boolean.valueOf(false), negation.argvalue);

		// Testing valid symbol - with negation
		symbol = "<!zz=has,have,V+Pr+Aux+3+s>";

		Assert.assertTrue(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));
		Assert.assertEquals("zz", left.argvalue);
		Assert.assertEquals("=", op.argvalue);
		Assert.assertEquals(Boolean.valueOf(true), negation.argvalue);

		// TODO - find example with category

		// symbol = "<xxx=V+tr-PR>";
		// Assert.assertTrue(Dic.parseLexicalConstraint(symbol, left, lemma, category, features, op, negation));
		// Assert.assertEquals("V", category.argvalue);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexicalConstraintRightSide(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexicalConstraintRightSide()
	{
		RefObject<String> lemma = new RefObject<String>("");
		RefObject<String> category = new RefObject<String>("");
		RefObject<String[]> features = new RefObject<String[]>(new String[0]);
		RefObject<Boolean> negation = new RefObject<Boolean>(false);

		// Test invalid symbol
		String right = "!";

		Assert.assertFalse(Dic.parseLexicalConstraintRightSide(right, lemma, category, features, negation));

		// Test valid symbol - with category and features
		right = "V+tr-PR";
		Assert.assertTrue(Dic.parseLexicalConstraintRightSide(right, lemma, category, features, negation));
		Assert.assertEquals("V", category.argvalue);
		Assert.assertNotNull(features.argvalue);
		Assert.assertTrue(features.argvalue.length == 2);
		Assert.assertEquals(Boolean.valueOf(false), negation.argvalue);

		// System.out.println("lemma: " + lemma.argvalue + "\ncategory: " + category.argvalue + "\nnegation: "
		// + negation.argvalue);

		// Test valid symbol - with lemma and features
		right = "avoir+3+s";
		Assert.assertTrue(Dic.parseLexicalConstraintRightSide(right, lemma, category, features, negation));
		Assert.assertNotNull(lemma.argvalue);
		Assert.assertNotNull(features.argvalue);
		Assert.assertTrue(features.argvalue.length == 2);

		// Test valid symbol - no category or lemma
		right = "+hum";
		Assert.assertFalse(Dic.parseLexicalConstraintRightSide(right, lemma, category, features, negation));
		Assert.assertNull(lemma.argvalue);
		Assert.assertNull(category.argvalue);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexicalConstraint(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexicalConstraintStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseSymbolFeatureArray(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseSymbolFeatureArray()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseSymbol(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseSymbol()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexicalUnit(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexicalUnit()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexemeSymbol(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexemeSymbolStringRefObjectOfStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#parseLexemeSymbol(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testParseLexemeSymbolStringRefObjectOfStringRefObjectOfStringRefObjectOfString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getRightToLeft(java.lang.String)}.
	 */
	@Test
	public void testGetRightToLeft()
	{
		Assert.assertTrue(Dic.getRightToLeft("fa"));
		Assert.assertTrue(Dic.getRightToLeft("ar"));
		Assert.assertFalse(Dic.getRightToLeft("en"));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#getPropertyNameValue(java.lang.String, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGetPropertyNameValue()
	{
		String feature = "name=value";
		RefObject<String> propertyName = new RefObject<String>(new String());
		RefObject<String> propertyValue = new RefObject<String>(new String());

		Assert.assertTrue(Dic.getPropertyNameValue(feature, propertyName, propertyValue));
		Assert.assertEquals("name", propertyName.argvalue);
		Assert.assertEquals("value", propertyValue.argvalue);

		feature = "false";

		Assert.assertFalse(Dic.getPropertyNameValue(feature, propertyName, propertyValue));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getAllFeaturesWithPlusOrMinus(java.lang.String)}.
	 */
	@Test
	public void testGetAllFeaturesWithPlusOrMinus()
	{
		String info = "N-Hum+f+p+Pol";

		Assert.assertTrue(Dic.getAllFeaturesWithPlusOrMinus(info).length != 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getAllFeaturesWithoutPlus(java.lang.String)}.
	 */
	@Test
	public void testGetAllFeaturesWithoutPlus()
	{
		String info = "N+Hum+f+p+Pol";

		Assert.assertTrue(Dic.getAllFeaturesWithoutPlus(info).length != 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#sortInfos(java.lang.String)}.
	 */
	@Test
	public void testSortInfos()
	{
		// Test string without duplicates
		String info = "N+Hum+f+p+Pol";
		String newInfo = "+N+Hum+f+p+Pol";

		String result = Dic.sortInfos(info);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, newInfo);

		// With duplicates
		info = "N+Hum+N+f+p+Pol";
		result = Dic.sortInfos(info);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, newInfo);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#splitAllFeaturesWithPlus(java.lang.String)}.
	 */
	@Test
	public void testSplitAllFeaturesWithPlus()
	{
		// Testing valid allFeatures strings
		String allFeatures = "+Hum+s+p+Pol";
		String[] featureArray = new String[4];
		featureArray[0] = "Hum";
		featureArray[1] = "s";
		featureArray[2] = "p";
		featureArray[3] = "Pol";

		String[] result = Dic.splitAllFeaturesWithPlus(allFeatures);
		Assert.assertNotNull(result);
		for (int i = 0; i < result.length; i++)
			Assert.assertEquals(featureArray[i], result[i]);

		allFeatures = "+<Hum>+s+p+Pol";
		result = Dic.splitAllFeaturesWithPlus(allFeatures);
		Assert.assertNotNull(result);

		// Testing invalid allFeatures strings
		allFeatures = null;
		Assert.assertNull(Dic.splitAllFeaturesWithPlus(allFeatures));

		allFeatures = "Hum+s+p+Pol";
		Assert.assertNull(Dic.splitAllFeaturesWithPlus(allFeatures));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#splitAllFeaturesWithPlusOrMinus(java.lang.String)}.
	 */
	@Test
	public void testSplitAllFeaturesWithPlusOrMinus()
	{
		// Testing valid allFeatures strings
		String allFeatures = "-Hum+s+p-Pol";
		String[] featureArray = new String[4];
		featureArray[0] = "-Hum";
		featureArray[1] = "+s";
		featureArray[2] = "+p";
		featureArray[3] = "-Pol";

		String[] result = Dic.splitAllFeaturesWithPlusOrMinus(allFeatures);
		Assert.assertNotNull(result);
		for (int i = 0; i < result.length; i++)
			Assert.assertEquals(featureArray[i], result[i]);

		allFeatures = "+<Hum>+s+p-Pol";
		result = Dic.splitAllFeaturesWithPlusOrMinus(allFeatures);
		Assert.assertNotNull(result);

		// Testing invalid allFeatures strings
		allFeatures = null;
		Assert.assertNull(Dic.splitAllFeaturesWithPlusOrMinus(allFeatures));

		allFeatures = "Hum+s+p-Pol";
		Assert.assertNull(Dic.splitAllFeaturesWithPlusOrMinus(allFeatures));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getRidOfSpecialFeatures(java.lang.String[])}.
	 */
	@Test
	public void testGetRidOfSpecialFeaturesStringArray()
	{
		// Testing null or empty array of features
		String[] features = null;
		String result = Dic.getRidOfSpecialFeatures(features);

		Assert.assertEquals("", result);

		features = new String[0];
		result = Dic.getRidOfSpecialFeatures(features);

		Assert.assertEquals("", result);

		// Testing array of features with special features
		features = new String[5];
		features[0] = "UNAMB";
		features[1] = "FLX=something";
		features[2] = "OK";
		features[3] = "DRV=something else";
		features[4] = "COLOR=blue";

		result = Dic.getRidOfSpecialFeatures(features);

		Assert.assertNotNull(result);
		Assert.assertEquals(result, "+OK");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getRidOfSpecialFeaturesPlus(java.lang.String[])}.
	 */
	@Test
	public void testGetRidOfSpecialFeaturesPlus()
	{
		// Testing null or empty array of features
		String[] features = null;
		String result = Dic.getRidOfSpecialFeaturesPlus(features);

		Assert.assertEquals("", result);

		features = new String[0];
		result = Dic.getRidOfSpecialFeaturesPlus(features);

		Assert.assertEquals("", result);

		// Testing array of features with special features
		features = new String[8];
		features[0] = "UNAMB";
		features[1] = "FLX=something";
		features[2] = "OK";
		features[3] = "DRV=something else";
		features[4] = "COLOR=blue";
		features[5] = "XREF=something also";
		features[6] = "HIDDEN field";
		features[7] = "also ok";

		result = Dic.getRidOfSpecialFeaturesPlus(features);

		Assert.assertNotNull(result);
		Assert.assertEquals(result, "+OK+also ok");

		// Testing array of features with special features - if HIDDEN is not followed by anything, it is added to
		// resulting string
		features = new String[4];
		features[0] = "UNAMB";
		features[1] = "XREF=something";
		features[2] = "HIDDEN";
		features[3] = "ok";

		result = Dic.getRidOfSpecialFeaturesPlus(features);

		Assert.assertNotNull(result);
		Assert.assertEquals(result, "+HIDDEN+ok");
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Dic#getRidOfInflectionalFeatures(java.lang.String[], java.util.HashMap)}.
	 */
	@Test
	public void testGetRidOfInflectionalFeatures()
	{
		String[] features = null;
		HashMap<String, Boolean> inflectionalProperties = new HashMap<String, Boolean>();
		inflectionalProperties.put("blue", true);
		inflectionalProperties.put("noDerive", false);

		// Testing null or empty array of features
		String[] result = Dic.getRidOfInflectionalFeatures(features, inflectionalProperties);

		Assert.assertNull(result);

		features = new String[0];
		result = Dic.getRidOfInflectionalFeatures(features, inflectionalProperties);

		Assert.assertNull(result);

		// Testing array of features
		features = new String[4];
		features[0] = "COLOR=blue";
		features[1] = "DRV=noDerive";
		features[2] = "feature not in the list";
		features[3] = "also=true";

		result = Dic.getRidOfInflectionalFeatures(features, inflectionalProperties);

		Assert.assertNotNull(result);
		Assert.assertEquals(features[2], result[0]);
		Assert.assertEquals(features[3], result[1]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#getRidOfSpecialFeatures(java.lang.String)}.
	 */
	@Test
	public void testGetRidOfSpecialFeaturesString()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#lookForAtBeg(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLookForAtBeg()
	{
		String feature0 = "XREF";
		String info = "XREF=14+Hum+f";

		Assert.assertEquals("XREF=14", Dic.lookForAtBeg(feature0, info));
		Assert.assertNull(Dic.lookForAtBeg("test", info));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#lookFor(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLookForStringString()
	{
		String feature0 = "Hum";
		String info = "N+Hum+m+p";

		// Simple look for
		Assert.assertEquals(feature0, Dic.lookFor(feature0, info));
		Assert.assertNull(Dic.lookFor("test", info));

		// Complex look for
		info = "<friends=N>{friends,friend,N+Hum+p}";
		Assert.assertEquals(feature0, Dic.lookFor(feature0, info));
		Assert.assertNull(Dic.lookFor("test", info));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#lookFor(java.lang.String, java.lang.String[])}.
	 */
	@Test
	public void testLookForStringStringArray()
	{
		String feature0 = "FLX";
		String[] infos = { "FLX=V7", "Salut" };

		Assert.assertEquals(infos[0], Dic.lookFor(feature0, infos));
		Assert.assertNull(Dic.lookFor("test", infos));

		try
		{
			Dic.lookFor(null, infos);
		}
		catch (IllegalArgumentException e)
		{
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#lookForAll(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLookForAllStringString()
	{
		String feature0 = "DRV";
		String info = "N+DRV=XxX+DRV=YYY+DRV=ZZZ+m+p";

		// Simple lookForAll
		Assert.assertTrue(Dic.lookForAll(feature0, info).length != 0);
		Assert.assertNull(Dic.lookForAll("PRG", info));

		// for (int i = 0; i < Dic.lookForAll(feature0, info).length; i++)
		// System.out.println(Dic.lookForAll(feature0, info)[i]);

		feature0 = "Hum";
		info = "<friends=N>{friends,friend,N+Hum+p}";

		// Complex lookForAll
		Assert.assertTrue(Dic.lookForAll(feature0, info).length != 0);
		Assert.assertNull(Dic.lookForAll("PRG", info));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#lookForAll(java.lang.String, java.lang.String[])}.
	 */
	@Test
	public void testLookForAllStringStringArray()
	{
		String feature0 = "DRV";
		String[] features = { "DRV+X=XxX+DRV", "YYY+DRV=ZZZ+m+p", "Salut" };

		Assert.assertTrue(Dic.lookForAll(feature0, features).length == 1);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#removeFeature(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRemoveFeature()
	{
		String feature0 = "Hum";
		String info = "N+Hum+s+f";

		Assert.assertNotNull(Dic.removeFeature(feature0, info));

		// System.out.println(Dic.removeFeature(feature0, info));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#unCompressSimpleLemma(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUnCompressSimpleLemma()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#unCompressCompoundLemma(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUnCompressCompoundLemma()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#nbOfCommas(java.lang.String)}.
	 */
	@Test
	public void testNbOfCommas()
	{
		String line = "Line to be tested, which contains commas, but without commas between brackets and double quotes. For example, <commas in here, are not counted,>, and commas \"in here, also, not counted\".";

		Assert.assertEquals(4, Dic.nbOfCommas(line));
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#compressSimpleLemma(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCompressSimpleLemma()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#compressCompoundLemma(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCompressCompoundLemma()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#convertFromDls(java.lang.String, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testConvertFromDls()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#convertFromDlf(java.lang.String, net.nooj4nlp.engine.RefObject)}.
	 */
	@Test
	public void testConvertFromDlf()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#writeLog(java.lang.String)}.
	 */
	@Test
	public void testWriteLog()
	{
		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		String message = "This is the first line to be added to log file.";
		Dic.writeLog(message);

		message = "This is the second line.";
		Dic.writeLog(message);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Dic#writeLogInit(java.lang.String)}.
	 */
	@Test
	public void testWriteLogInit()
	{
		Dic d = new Dic();
		d.LogFileName = PATH_TO_RESOURCES + "log.txt";

		String message = "This is the third line to be added to log file.";
		Dic.writeLogInit(message);

		message = "This is the fourth line.";
		Dic.writeLogInit(message);
	}

}
