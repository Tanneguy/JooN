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

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * It is almost impossible to test this class and it's method the usual way. It has to be done simultaneously with other
 * classes
 * 
 * @author milos
 * 
 */
public class FSDicTest
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
	 * Test method for {@link net.nooj4nlp.engine.FSDic#FSDic(java.lang.String)}.
	 */
	@Test
	public void testFSDic()
	{
		FSDic fsDic = new FSDic("ar");
		Assert.assertNotNull(fsDic);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#unCompactInfo(java.lang.String)}.
	 */
	@Test
	public void testUnCompactInfo()
	{
		FSDic fsDic = new FSDic("sr");
		String compactInfo = fsDic.compactInfo("run", "runs,running,run,runable", "runs,running,run,runable", fsDic);
		fsDic.toBinaryAlphabetInfobitstable();
		ArrayList<String> unCompactInfo = fsDic.unCompactInfo(compactInfo);
		for (String string : unCompactInfo)
		{
			System.out.println(string);
		}
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#compactInfo(java.lang.String, java.lang.String, net.nooj4nlp.engine.FSDic)}.
	 */
	@Test
	public void testCompactInfoStringStringFSDic()
	{
		FSDic fsDic = new FSDic("en");
		String compactInfo = fsDic.compactInfo("run", "runs,running,run,runable", fsDic);
		Assert.assertNotNull(compactInfo);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#compactInfo(java.lang.String, java.lang.String, java.lang.String, net.nooj4nlp.engine.FSDic)}
	 * .
	 */
	@Test
	public void testCompactInfoStringStringStringFSDic()
	{
		FSDic fsDic = new FSDic("en");
		String compactInfo = fsDic.compactInfo("run", "runs,running,run,runable", "runs,running,run,runable", fsDic);
		Assert.assertNotNull(compactInfo);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#cleanupInflectionCommands()}.
	 */
	@Test
	public void testCleanupInflectionCommands()
	{
		FSDic fsDic = new FSDic("en");
		fsDic.cleanupInflectionCommands();
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#minimize(int)}.
	 */
	@Test
	public void testMinimizeInt()
	{
		FSDic fsDic = new FSDic("en");
		fsDic.minimize(1000);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#addLexLineToDic(java.lang.String, java.lang.String, java.lang.String, java.lang.String, net.nooj4nlp.engine.FSDic, net.nooj4nlp.engine.FSDic, java.util.HashMap, java.util.ArrayList, boolean)}
	 * .
	 */
	@Test
	public void testAddLexLineToDic()
	{
		FSDic fsDic = new FSDic("en");
		// fsDic.addLexLineToDic(entry, lemma, infolemma, infoflex, dics0, dics, hDicInfos, aDicInfos, isacompound)
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#toStatesString()}.
	 */
	@Test
	public void testToStatesString()
	{
		FSDic fsDic = new FSDic("en");
		String statesString = fsDic.toStatesString();
		Assert.assertNotNull(statesString);
		System.out.println(statesString);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#toDicString()}.
	 */
	@Test
	public void testToDicString()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#writeToBuf(int, byte[], int, int)}.
	 */
	@Test
	public void testWriteToBuf()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#readFromBuf(byte[], int, int)}.
	 */
	@Test
	public void testReadFromBuf()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#stateToBinary(net.nooj4nlp.engine.DState, byte[], int, int, int, int, net.nooj4nlp.engine.FSDic)}
	 * .
	 */
	@Test
	public void testStateToBinary()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#guessNeededSizeLog(int, int)}.
	 */
	@Test
	public void testGuessNeededSizeLog()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#computeAllLogs(net.nooj4nlp.engine.FSDic, net.nooj4nlp.engine.FSDic, net.nooj4nlp.engine.FSDic, net.nooj4nlp.engine.FSDic)}
	 * .
	 */
	@Test
	public void testComputeAllLogs()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#toBinaryAlphabetInfobitstable()}.
	 */
	@Test
	public void testToBinaryAlphabetInfobitstable()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#toBinary(net.nooj4nlp.engine.FSDic)}.
	 */
	@Test
	public void testToBinary()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#load(java.lang.String, net.nooj4nlp.engine.Engine, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testLoad()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#save(java.lang.String)}.
	 */
	@Test
	public void testSave()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#saveComments(java.lang.String, int, int, int, int, int)}.
	 */
	@Test
	public void testSaveComments()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#readTrans(byte[], int, int, int, int, int, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testReadTrans()
	{
		// TODO:
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.FSDic#lookUpSimple(java.lang.String, int, net.nooj4nlp.engine.Engine)}
	 * .
	 */
	@Test
	public void testLookUpSimple()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#lookUpCompound(java.lang.String, int, net.nooj4nlp.engine.Engine)}.
	 */
	@Test
	public void testLookUpCompound()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#lookUpCompoundSemitic(java.lang.String, int, net.nooj4nlp.engine.Engine)}.
	 */
	@Test
	public void testLookUpCompoundSemitic()
	{
		// TODO:
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.FSDic#lookUpSimpleSemitic(java.lang.String, int, net.nooj4nlp.engine.Engine)}.
	 */
	@Test
	public void testLookUpSimpleSemitic()
	{
		// TODO:
	}

}
