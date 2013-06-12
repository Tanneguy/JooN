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

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Silberztein Max
 * 
 */
public class ZipTest
{

	private String dirPath = "src/test/resources/Zip/sourceDir";
	private String zipFilePath = "src/test/resources/Zip/zipFile.zip";

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
	 * Test method for {@link net.nooj4nlp.engine.Zip#compressDir(java.lang.String, java.lang.String)} .
	 */
	@Test
	public void testCompressDir()
	{
		try
		{
			Zip.compressDir(dirPath, zipFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}

	/**
	 * Test method for {@link net.nooj4nlp.engine.Zip#uncompressDir(java.lang.String, java.lang.String)} .
	 */
	@Test
	public void testUncompressDir()
	{
		try
		{
			Zip.uncompressDir(dirPath, zipFilePath);
		}
		catch (IOException e)
		{
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}

}
