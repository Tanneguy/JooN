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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Silberztein Max
 * 
 */
public class TreeTest
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
	 * Test method for {@link net.nooj4nlp.engine.Tree#Tree()}.
	 */
	@Test
	public void testTree()
	{
		Tree tree = new Tree();
		assertNotNull(tree);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Tree#leafTree(java.lang.String)}.
	 */
	@Test
	public void testLeafTree()
	{

		Tree leafTree = Tree.leafTree(null);
		assertNotNull(leafTree);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Tree#binaryTree(net.nooj4nlp.engine.Tree, net.nooj4nlp.engine.Tree, java.lang.String)}
	 * .
	 */
	@Test
	public void testBinaryTree()
	{
		Tree binaryTree = Tree.binaryTree(null, null, null);
		assertNotNull(binaryTree);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Tree#explore(java.util.ArrayList, java.util.HashMap)}. TODO :
	 * depends on Gram class needs to be done after it has been tested
	 */
	@Test
	public void testExploreArrayListOfStringHashMapOfStringInteger()
	{
		Tree t = Tree.binaryTree(null, null, " ");
		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add(" ");
		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put(" ", 1);
		Gram explore = t.explore(aVocab, hVocab);
		Assert.assertNotNull(explore);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Tree#explore(java.lang.String, java.util.ArrayList, java.util.HashMap)}.TODO :
	 * depends on Gram class needs to be done after it has been tested
	 */
	@Test
	public void testExploreStringArrayListOfStringHashMapOfStringInteger()
	{
		Tree t = Tree.binaryTree(null, null, " ");
		ArrayList<String> aVocab = new ArrayList<String>();
		aVocab.add(" ");
		HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
		hVocab.put(" ", 1);
		Gram explore = t.explore(" ", aVocab, hVocab);
		Assert.assertNotNull(explore);
	}

}
