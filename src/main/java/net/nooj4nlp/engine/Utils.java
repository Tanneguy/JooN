package net.nooj4nlp.engine;

import java.io.File;

/**
 * Helper class for util functions.
 * 
 */
public class Utils
{
	/**
	 * Deletes all files from the given directory, and the directory itself.
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}