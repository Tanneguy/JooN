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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author milos
 * 
 */
public class UtilitiesTest
{
	private static final String PROJECT_DIRECTORY = "src/test/resources/projectDir";

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
	 * Test method for {@link net.nooj4nlp.engine.Utilities#xcopy(java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testXcopy()
	{
		String sourceDirPath = "src/test/resources/Utilities/sourceDir";
		String targetDirPath = "src/test/resources/Utilities/targetDir";
		try
		{
			if (new File(targetDirPath).isDirectory())
			{
				FileUtils.deleteDirectory(new File(targetDirPath));
			}
			Assert.assertFalse(new File(targetDirPath).isDirectory());
			Utilities.xcopy(sourceDirPath, targetDirPath, false);
			Assert.assertTrue(new File(targetDirPath).isDirectory());

		}
		catch (IOException e)
		{
			Assert.assertFalse(true);
		}

	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Utilities#initAllDiskResources(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInitAllDiskResources()
	{
		String docDirPath = "src/test/resources/Utilities/doc";
		new File(docDirPath).mkdir();
		try
		{
			Utilities.initAllDiskResources(PROJECT_DIRECTORY, docDirPath);
		}
		catch (IOException e)
		{
			Assert.assertFalse(true);
		}
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Utilities#savePreviousVersion(java.lang.String, boolean)}.
	 */
	@Test
	public void testSavePreviousVersion()
	{
		String filePath = "src/test/resources/Utilities/doc/Preferences.noj";
		try
		{
			FileUtils.deleteDirectory(new File("src/test/resources/Utilities/doc/Backup"));
			Utilities.savePreviousVersion(filePath, false);
			Assert.assertTrue(new File("src/test/resources/Utilities/doc/Backup/Preferences_backup.noj").exists());
			Utilities.savePreviousVersion(filePath, true);
			Assert.assertTrue(new File("src/test/resources/Utilities/doc/Backup/Preferences_001.noj").exists());
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
	}

}
