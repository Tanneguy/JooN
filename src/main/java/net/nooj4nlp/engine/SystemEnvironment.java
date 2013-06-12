package net.nooj4nlp.engine;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;

public class SystemEnvironment
{

	public static String[] encodings = listAllEncodings();

	public static String[] listAllEncodings()
	{
		SortedMap<String, Charset> map = Charset.availableCharsets();
		Set<String> keys = map.keySet();
		String[] array = keys.toArray(new String[keys.size()]);
		Arrays.sort(array);
		return array;
	}

}
