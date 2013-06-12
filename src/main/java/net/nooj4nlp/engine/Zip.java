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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * The class provides static methods for compressing and uncompressing directory content
 * 
 * @author Silberztein Max
 * 
 */
public class Zip
{

	/**
	 * The method compresses given directory to compressed file.
	 * 
	 * @param sourceDirPath
	 *            - path of a directory which content should be compressed
	 * @param destinationZipFilePath
	 *            - path of compressed file created during directory compression
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static void compressDir(String sourceDirPath, String destinationZipFilePath) throws IOException
	{
		ParameterCheck.mandatoryString("sourceDirPath", sourceDirPath);
		ParameterCheck.mandatoryString("destinationZipFilePath", destinationZipFilePath);

		File directory = new File(sourceDirPath);

		File[] sourceDirFiles = directory.listFiles();

		CRC32 crc = new CRC32();

		File zipFile = new File(destinationZipFilePath);
		if (!zipFile.createNewFile())
		{
			zipFile.delete();
			zipFile.createNewFile();
		}
		// inicialization of streams, so that they can be closed
		FileInputStream fileInputStream = null;
		OutputStream fileOutputStream = null;
		ZipOutputStream zipOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(zipFile);

			zipOutputStream = new ZipOutputStream(fileOutputStream);

			zipOutputStream.setLevel(6); // 0 - store only to 9 - means best
											// compression

			for (File sourceDirFile : sourceDirFiles)
			{
				try
				{
					fileInputStream = new FileInputStream(sourceDirFile);
					byte[] dataBuffer = null;

					dataBuffer = new byte[(int) sourceDirFile.length()];

					fileInputStream.read(dataBuffer, 0, dataBuffer.length);

					String sourceDirFileName = sourceDirFile.getName();
					ZipEntry zipEntry = new ZipEntry(sourceDirFileName);
					zipEntry.setTime(new Date().getTime());
					zipEntry.setSize(sourceDirFile.length());

					fileInputStream.close();
					crc.reset();
					crc.update(dataBuffer);

					zipEntry.setCrc(crc.getValue());

					zipOutputStream.putNextEntry(zipEntry);

					zipOutputStream.write(dataBuffer, 0, dataBuffer.length);

				}
				catch (RuntimeException e)
				{
					System.out.println(e.getMessage() + "\n\nNooJ: cannot store file " + sourceDirFile);
				}
			}

			zipOutputStream.finish();
			zipOutputStream.close();
		}
		finally
		{
			// close streams if any is still open
			if (fileInputStream != null)
				fileInputStream.close();
			if (fileOutputStream != null)
				fileOutputStream.close();
			if (zipOutputStream != null)
				zipOutputStream.close();
		}
	}

	/**
	 * The method uncompresses given compressed file to the directory
	 * 
	 * @param destinationDirPath
	 *            - path to the directory to which the content of compressed file should be uncompressed
	 * @param sourceZipFilePath
	 *            - path of compressed file which content is uncompressed
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static void uncompressDir(String destinationDirPath, String sourceZipFilePath) throws IOException
	{
		ParameterCheck.mandatoryString("destinationDirPath", destinationDirPath);
		ParameterCheck.mandatoryString("sourceZipFilePath", sourceZipFilePath);

		// initialization of streams, so that they can be closed
		InputStream zipInputStream = null;
		FileOutputStream fileOutputStream = null;
		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(sourceZipFilePath);
			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();)
			{

				ZipEntry zipEntry = e.nextElement();
				String zipEntryFileName = zipEntry.getName();
				zipInputStream = zipFile.getInputStream(zipEntry);
				new File(destinationDirPath).mkdir();

				if (!zipEntryFileName.equals(""))
				{
					File file = new File(destinationDirPath, zipEntryFileName);
					if (!file.createNewFile())
					{
						file.delete();
						file.createNewFile();
					}
					fileOutputStream = new FileOutputStream(file);

					int bufferSize = 2048;
					byte[] dataBuffer = new byte[2048];
					while (true)
					{
						bufferSize = zipInputStream.read(dataBuffer, 0, dataBuffer.length);
						if (bufferSize > 0)
						{
							fileOutputStream.write(dataBuffer, 0, bufferSize);
						}
						else
						{
							break;
						}
					}
					fileOutputStream.close();
				}
			}
			zipInputStream.close();
		}
		finally
		{
			// close streams if any is still open
			if (zipInputStream != null)
				zipInputStream.close();
			if (zipFile != null)
				zipFile.close();
			if (fileOutputStream != null)
				fileOutputStream.close();
		}
	}
}
