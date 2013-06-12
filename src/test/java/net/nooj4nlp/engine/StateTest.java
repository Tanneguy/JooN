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

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Natalija
 * 
 */
public class StateTest
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
	 * Test method for {@link net.nooj4nlp.engine.State#State()}.
	 */
	@Test
	public void testState()
	{
		State s = new State();

		Assert.assertEquals(0, s.Dests.size());
		Assert.assertEquals(0, s.IdLabels.size());
		Assert.assertEquals(false, s.OnlyWordForms);
		Assert.assertEquals(null, s.hWordForms);
		Assert.assertEquals(-1, s.GraphNodeNumber);
		Assert.assertEquals(0, s.AllIdLabels.size());
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.State#addTrans(int dest, int idLabel)}.
	 */
	@Test
	public void testAddTrans()
	{
		State s = new State();

		Assert.assertEquals(0, s.Dests.size());
		Assert.assertEquals(0, s.IdLabels.size());
		Assert.assertEquals(false, s.OnlyWordForms);
		Assert.assertEquals(null, s.hWordForms);
		Assert.assertEquals(-1, s.GraphNodeNumber);
		Assert.assertEquals(0, s.AllIdLabels.size());

		// Adding a transition to empty state
		s.addTrans(1, 2);
		Assert.assertEquals(1, s.Dests.size());
		Assert.assertEquals(1, s.IdLabels.size());
		Assert.assertEquals(1, s.AllIdLabels.size());

		int dest = s.Dests.get(0);
		Assert.assertEquals(1, dest);
		int idLabel = s.IdLabels.get(0);
		Assert.assertEquals(2, idLabel);

		Set<Integer> keySet = s.AllIdLabels.keySet();
		for (Integer key : keySet)
		{
			Assert.assertEquals(1, s.AllIdLabels.get(key).size());
		}

		// Adding a new transition - new IdLabel
		s.addTrans(3, 4);
		Assert.assertEquals(2, s.Dests.size());
		Assert.assertEquals(2, s.IdLabels.size());
		Assert.assertEquals(2, s.AllIdLabels.size());

		dest = s.Dests.get(1);
		Assert.assertEquals(3, dest);
		idLabel = s.IdLabels.get(1);
		Assert.assertEquals(4, idLabel);

		keySet = s.AllIdLabels.keySet();
		for (Integer key : keySet)
		{
			Assert.assertEquals(1, s.AllIdLabels.get(key).size());
		}

		// Adding a new transition - existing IdLabel
		s.addTrans(5, 4);
		Assert.assertEquals(3, s.Dests.size());
		Assert.assertEquals(3, s.IdLabels.size());
		// Since entry for id label 4 already exists in HashMap AllIdLabels, its size should be 2.
		Assert.assertEquals(2, s.AllIdLabels.size());

		dest = s.Dests.get(2);
		Assert.assertEquals(5, dest);
		idLabel = s.IdLabels.get(2);
		Assert.assertEquals(4, idLabel);

		Assert.assertEquals(2, s.AllIdLabels.get(4).size());
	}

}
