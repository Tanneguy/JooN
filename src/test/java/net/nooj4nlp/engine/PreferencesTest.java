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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author milos
 * 
 */
public class PreferencesTest
{

	private static final String PREFERENCES_FILE = "src/test/resources/Preferences/Preferences.noj";
	private static final String PROJECT_DIRECTORY = "src/test/resources/projectDir";

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testPreferencesString()
	{
		Preferences pref = null;
		try
		{
			pref = new Preferences(PROJECT_DIRECTORY);
			pref.Save(PREFERENCES_FILE);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			assertTrue(false);

		}
		Assert.assertNotNull(pref.languages);
		Assert.assertFalse(pref.languages.isEmpty());
	}

	@Test
	public void testPreferences()
	{
		Preferences pref = null;
		pref = new Preferences();

		Assert.assertNotNull(pref);
	}

	@Test
	public void testLoad()
	{
		Preferences pref = Preferences.Load(PREFERENCES_FILE);
		Assert.assertNotNull(pref);
		Assert.assertNotNull(pref.languages);
		Assert.assertFalse(pref.languages.isEmpty());
	}

	@Test
	public void testSave()
	{
		Preferences pref = Preferences.Load(PREFERENCES_FILE);
		try
		{
			pref.Save(PREFERENCES_FILE);
			Assert.assertTrue(true);
		}
		catch (IOException e)
		{
			assertTrue(false);
		}

	}

}
