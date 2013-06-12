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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MftTest
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
	 * Test method for {@link net.nooj4nlp.engine.Mft#Mft(int)}.
	 */
	@Test
	public void testMft()
	{
		Mft mft = new Mft(10);

		Assert.assertEquals(11, mft.tuAddresses.length);
		Assert.assertEquals(11, mft.tuLengths.length);
		Assert.assertEquals(11, mft.aTransitions.length);

		for (int i = 1; i < 11; i++)
		{
			Assert.assertEquals(0, mft.aTransitions[i].size());
		}

		Assert.assertEquals(0, mft.nboftransitions);
		Assert.assertEquals(100.0, mft.multiplier, 0);
		Assert.assertEquals(0, mft.nbOfTransitions);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#resetTransitions()}.
	 */

	@Test
	public void testResetTransitions()
	{
		Mft mft = new Mft(1);

		ArrayList al = new ArrayList();
		al.add("test");

		// Sets non-default values for method members
		mft.aTransitions[1] = al;
		mft.multiplier = 50.0;
		mft.nboftransitions = 1;
		mft.nbOfTransitions = 1;

		Assert.assertEquals(2, mft.aTransitions.length);
		Assert.assertEquals("test", mft.aTransitions[1].get(0));
		Assert.assertEquals(50.0, mft.multiplier, 0);
		Assert.assertEquals(1, mft.nboftransitions);
		Assert.assertEquals(1, mft.nbOfTransitions);

		// Test transitions reset
		mft.resetTransitions();

		Assert.assertEquals(2, mft.aTransitions.length);
		Assert.assertEquals(0, mft.aTransitions[1].size());
		Assert.assertEquals(100.0, mft.multiplier, 0);
		Assert.assertEquals(0, mft.nboftransitions);
		Assert.assertEquals(0, mft.nbOfTransitions);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#reSize(int)}.
	 */
	@Test
	public void testReSize()
	{
		Mft mft = new Mft(10);

		ArrayList al = new ArrayList();
		al.add("test");

		for (int i = 1; i < 11; i++)
		{
			mft.aTransitions[i] = al;
		}

		for (int i = 1; i < 11; i++)
		{
			Assert.assertEquals("test", mft.aTransitions[i].get(0));
		}

		// Resizing the mft
		mft.reSize(5);

		Assert.assertEquals(6, mft.tuAddresses.length);
		Assert.assertEquals(6, mft.tuLengths.length);
		Assert.assertEquals(6, mft.aTransitions.length);

		for (int i = 1; i < 6; i++)
		{
			Assert.assertEquals("test", mft.aTransitions[i].get(0));
		}

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#getTuGraph(int)}.
	 */
	// TODO TuGraph is a GUI class. Testing and commenting will be finished when TuGraph is committed.
	@Test
	public void testGetTuGraph()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#removeTransition(int, double, int, net.nooj4nlp.engine.RefObject)}
	 * .
	 */

	@Test
	public void testRemoveTransition()
	{
		Mft mft = new Mft(3);

		// Filling data for mft
		// First member of array of transitions
		ArrayList al1 = new ArrayList();
		al1.add(0.1);
		ArrayList al1al = new ArrayList();
		al1al.add(11);
		al1al.add(12.0);
		al1al.add(13);
		al1al.add(14.0);
		al1al.add(15);
		al1al.add(16.0);
		al1.add(al1al);

		mft.aTransitions[1] = al1;

		// Second member of array of transitions
		ArrayList al2 = new ArrayList();
		al2.add(0.2);
		ArrayList al2al = new ArrayList();
		al2al.add(21);
		al2al.add(22.0);
		al2.add(al2al);

		mft.aTransitions[2] = al2;

		// Third member of array of transitions
		ArrayList al3 = new ArrayList();
		al3.add(0.3);
		ArrayList al3al = new ArrayList();
		al3al.add(31);
		al3al.add(32.0);
		al3al.add(33);
		al3al.add(34.0);
		al3.add(al3al);

		mft.aTransitions[3] = al3;

		// Remove transition
		RefObject<Double> relEndAddress = new RefObject<Double>(0.0);

		// System.out.println(mft.aTransitions[0]);
		// System.out.println(mft.aTransitions[1]);
		// System.out.println(mft.aTransitions[2]);
		// System.out.println(mft.aTransitions[3]);

		Assert.assertTrue(mft.removeTransition(2, 0.2, 21, relEndAddress));
		Assert.assertTrue(mft.removeTransition(3, 0.3, 33, relEndAddress));
		relEndAddress = new RefObject<Double>(15.0);
		Assert.assertTrue(mft.removeTransition(1, 0.1, 13, relEndAddress));

		// System.out.println(mft.aTransitions[0]);
		// System.out.println(mft.aTransitions[1]);
		// System.out.println(mft.aTransitions[2]);
		// System.out.println(mft.aTransitions[3]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#getAllAmbiguitiesInTextUnit(int)}.
	 */
	@Test
	public void testGetAllAmbiguitiesInTextUnit()
	{
		Mft mft = createMftWithTransitions();

		// Test for first member - with ambiguities
		ArrayList ambiguities = mft.getAllAmbiguitiesInTextUnit(1);
		// for (int i = 0; i < ambiguities.size(); i++)
		// System.out.print(ambiguities.get(i) + " : ");
		Assert.assertTrue(ambiguities.size() == 3);

		ArrayList tokenIds = (ArrayList) ambiguities.get(1);
		Assert.assertTrue(tokenIds.size() == 2);

		// Test for second member - no ambiguities
		ambiguities = mft.getAllAmbiguitiesInTextUnit(2);
		Assert.assertTrue(ambiguities.size() == 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#getAllUnambiguitiesInTextUnit(int)}.
	 */
	@Test
	public void testGetAllUnambiguitiesInTextUnit()
	{
		Mft mft = new Mft(2);

		// Filling data for mft
		// First member of array of transitions
		ArrayList al1 = new ArrayList();
		al1.add(0.1);
		ArrayList al1al = new ArrayList();
		al1al.add(11);
		al1al.add(12.0);
		al1al.add(13);
		al1al.add(14.0);
		al1al.add(15);
		al1al.add(16.0);
		al1.add(al1al);

		mft.aTransitions[1] = al1;

		// Test for first member - with ambiguities
		ArrayList unambiguities = mft.getAllUnambiguitiesInTextUnit(1);
		Assert.assertTrue(unambiguities.size() == 0);

		// Second member of array of transitions
		ArrayList al2 = new ArrayList();
		al2.add(0.2);
		ArrayList al2al = new ArrayList();
		al2al.add(21);
		al2al.add(22.0);
		al2.add(al2al);

		mft.aTransitions[2] = al2;

		// Test for second member - no ambiguities
		unambiguities = mft.getAllUnambiguitiesInTextUnit(2);
		// for (int i = 0; i < unambiguities.size(); i++)
		// System.out.print(unambiguities.get(i) + " : ");
		Assert.assertTrue(unambiguities.size() == 3);

		ArrayList tokenIds = (ArrayList) unambiguities.get(1);
		Assert.assertTrue(tokenIds.size() == 1);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#getANewVirginAddress(int, double, double)}.
	 */
	@Test
	public void testGetANewVirginAddress()
	{
		Mft mft = createMftWithTransitions();

		// System.out.println(mft.getANewVirginAddress(1, 0.12, 14.0));
		Assert.assertEquals(0.13, mft.getANewVirginAddress(1, 0.12, 14.0), 0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#getOutgoingTransitions(int, double)}.
	 */
	@Test
	public void testGetOutgoingTransitions()
	{
		Mft mft = createMftWithTransitions();

		// System.out.println(mft.getOutgoingTransitions(1, 0.11));
		// System.out.println(mft.getOutgoingTransitions(1, 0.12));
		// System.out.println(mft.getOutgoingTransitions(2, 0.21));

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.11).size() == 2);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 4);
		Assert.assertTrue(mft.getOutgoingTransitions(2, 0.21).size() == 2);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#shiftAllTransitions(int[])}.
	 */
	@Test
	public void testShiftAllTransitions()
	{
		Mft mft = createMftWithTransitions();

		int[] tuAddresses = new int[2];
		tuAddresses[0] = 1;
		tuAddresses[1] = 2;

		int[] tuLengths = new int[2];
		tuLengths[0] = 3;
		tuLengths[1] = 1;

		mft.tuAddresses = tuAddresses;
		mft.tuLengths = tuLengths;

		// Preparing shift array...
		int[] shift = new int[20];
		for (int i = 0; i < 20; i++)
			shift[i] = 0;
		shift[1] = 70;
		shift[2] = 175;
		// shift[tuAddresses[0] + tuLength[0] ...
		shift[3] = 200;
		shift[4] = 100;

		// Performing shift and testing new values.
		mft.shiftAllTransitions(shift);

		Assert.assertEquals(70, mft.tuAddresses[0]);
		Assert.assertEquals(175, mft.tuAddresses[1]);

		Assert.assertEquals(30, mft.tuLengths[0]);
		Assert.assertEquals(25, mft.tuLengths[1]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#addTransition(int, double, int, double)}.
	 */
	@Test
	public void testAddTransition()
	{
		Mft mft = createMftWithTransitions();

		mft.addTransition(2, 0.21, 23, 24.0);

		ArrayList transitions = (ArrayList) mft.aTransitions[2].get(1);
		Assert.assertTrue(transitions.size() == 4);

		mft.addTransition(2, 0.25, 27, 28.0);
		// System.out.println(mft.aTransitions[2]);

		Assert.assertTrue(mft.aTransitions[2].size() == 4);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Mft#deleteNonXrefsAndAddTransition(int, double, int, java.util.ArrayList, double)}.
	 */
	@Test
	public void testDeleteNonXrefsAndAddTransition()
	{
		Mft mft = createMftWithTransitions();

		// Test - 16th member of annotations list will not be XREF -> pair (15, 16.0) will be removed from the list of
		// outgoing transitions for beginning address 0.12
		ArrayList annotations = new ArrayList(30);
		for (int i = 0; i < 30; i++)
			annotations.add("XREF " + i);

		// System.out.println(mft.aTransitions[1]);

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 4);
		// Transition added and all annotations are XREF
		mft.deleteNonXrefsAndAddTransition(1, 0.12, 17, annotations, 18.0);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 6);

		// System.out.println(mft.aTransitions[1]);

		annotations.set(15, "NONREF");

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 6);
		// Transition added and NOT all annotations are XREF
		mft.deleteNonXrefsAndAddTransition(1, 0.12, 19, annotations, 20.0);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 6);

		// System.out.println(mft.aTransitions[1]);

		annotations.set(17, "NONREF");
		annotations.set(19, "NONREF");

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 6);
		// Transition added and NOT all annotations are XREF
		mft.deleteNonXrefsAndAddTransition(1, 0.12, 21, annotations, 22.0);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 4);

		// System.out.println(mft.aTransitions[1]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#filterInconsistentXrefs(java.util.ArrayList, int)}.
	 */
	@Test
	public void testFilterInconsistentXrefs()
	{
		// TODO this method will be properly tested when there's more information about it

		Mft mft = createMftWithTransitions();

		// Test - 14th member of annotations list will not be XREF -> pair (15, 16.0) will be removed from the list of
		// outgoing transitions for beginning address 0.12, as well as pair (11, 12.0) for beginning address 0.11
		ArrayList annotations = new ArrayList(20);
		for (int i = 0; i < 20; i++)
			annotations.add("XREF=" + i + ".0" + i);

		// System.out.println(mft.aTransitions[1]);

		annotations.set(13, "NONREF");

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.11).size() == 2);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 4);

		mft.filterInconsistentXrefs(annotations, 1);

		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.11).size() == 0);
		Assert.assertTrue(mft.getOutgoingTransitions(1, 0.12).size() == 2);

		// System.out.println(mft.aTransitions[1]);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Mft#filterTransitions(java.util.ArrayList, int, double, java.lang.String, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testFilterTransitions()
	{
		Mft mft = createMftWithTransitions();

		ArrayList annotations = new ArrayList(20);
		for (int i = 0; i < 20; i++)
			annotations.add("XREF=<Tryout>");

		System.out.println(mft.aTransitions[1]);

		annotations.set(13, "<child,N+plural>children</child,N+plural>, childs, SYNTAX");
		annotations.set(14, "<child,N+plural>children</child,N+plural>, childs, SYNTAX");
		annotations.set(15, "<child,N+plural>children</child,N+plural>, childs, SYNTAX");
		annotations.set(16, "<child,N+plural>children</child,N+plural>, childs, SYNTAX");

		RefObject<Boolean> anXrefWasRemoved = new RefObject<Boolean>(false);

		mft.filterTransitions(annotations, 1, 0.12, "<child,N+plural>children</child,N+plural>, childs, SYNTAX",
				anXrefWasRemoved);

		System.out.println(mft.aTransitions[1]);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#thereAreLexs(int, double)}.
	 */
	@Test
	public void testThereAreLexs()
	{
		Mft mft = createMftWithTransitions();

		Assert.assertTrue(mft.thereAreLexs(1, 0.11));
		Assert.assertTrue(mft.thereAreLexs(1, 0.12));
		Assert.assertTrue(mft.thereAreLexs(2, 0.21));
		Assert.assertFalse(mft.thereAreLexs(2, 0.22));
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Mft#getAllLexIds(int, double, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGetAllLexIds()
	{
		Mft mft = createMftWithTransitions();

		RefObject<ArrayList> lexIds = new RefObject<ArrayList>(new ArrayList());
		RefObject<ArrayList> fLengths = new RefObject<ArrayList>(new ArrayList());

		Assert.assertEquals(2, mft.getAllLexIds(1, 0.12, lexIds, fLengths));

		ArrayList testLexIds = new ArrayList();
		testLexIds.add(13);
		testLexIds.add(15);

		ArrayList testFLengths = new ArrayList();
		testFLengths.add(13.88);
		testFLengths.add(15.88);

		for (int i = 0; i < 2; i++)
		{
			Assert.assertEquals(testLexIds.get(i), lexIds.argvalue.get(i));
			Assert.assertEquals(testFLengths.get(i), fLengths.argvalue.get(i));
		}

		System.out.println(lexIds.argvalue + " *** " + fLengths.argvalue);
	}

	/**
	 * Test method for
	 * {@link net.nooj4nlp.engine.Mft#getAllLexIdsAndContracted(int, double, net.nooj4nlp.engine.RefObject, net.nooj4nlp.engine.RefObject)}
	 * .
	 */
	@Test
	public void testGetAllLexIdsAndContracted()
	{
		Mft mft = createMftWithTransitions();

		RefObject<ArrayList> lexIds = new RefObject<ArrayList>(new ArrayList());
		RefObject<ArrayList> fLengths = new RefObject<ArrayList>(new ArrayList());

		Assert.assertTrue(mft.getAllLexIdsAndContracted(1, 0.12, lexIds, fLengths) == 2);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#beforeSaving(double)}.
	 */
	@Test
	public void testBeforeSaving()
	{
		// Since all the values that are set in tested method are private, just tests whether method finishes
		// successfully.
		Mft mft = createMftWithTransitions();
		mft.nboftransitions = 4;
		mft.beforeSaving(100.0);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#afterLoading(double)}.
	 */
	@Test
	public void testAfterLoading()
	{
		// TODO this method will be tested when there's more information about it
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Mft#showStats()}.
	 */
	@Test
	public void testShowStats()
	{
		// No need to test this method, it returns no value, changes nothing, and is not called from anywhere.
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

		System.out.println(mft.aTransitions[1]);
		System.out.println(mft.aTransitions[2]);

		return mft;
	}

}
