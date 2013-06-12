package net.nooj4nlp.controller.ConcordanceShell;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.nooj4nlp.engine.Dic;

public class ConcordanceData implements Serializable
{
	private static final long serialVersionUID = 3056471410015278329L;

	private List<Object> theItems;
	private ArrayList<ArrayList<Object>> theTags;

	public ConcordanceData(String corpusFullName, List<Object> theItems)
	{
		this.theItems = theItems;
		this.theTags = new ArrayList<ArrayList<Object>>();

		for (int i = 0; i < theItems.size(); i += 4)
		{
			Object[] item = (Object[]) theItems.get(i + 1);
			// Unchecked cast - cannot be avoided here, because theItems is a list of objects of different types
			// (ArrayList<Object>, Object[], Boolean...)
			ArrayList<Object> theTag = (ArrayList<Object>) item[5];
			theTags.add(theTag);
		}
	}

	public void save(String fullName) throws IOException
	{
		// By default, serialization in Java is binary
		FileOutputStream fileOutputStream = new FileOutputStream(fullName);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();

		objectOutputStream.close();
		fileOutputStream.close();
	}

	public static ConcordanceData load(String fullName) throws IOException
	{
		ConcordanceData cd = null;
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;

		try
		{
			fileInputStream = new FileInputStream(fullName);
			objectInputStream = new ObjectInputStream(fileInputStream);
			cd = (ConcordanceData) objectInputStream.readObject();
		}
		catch (Exception ex)
		{
			if (objectInputStream != null)
				objectInputStream.close();

			if (fileInputStream != null)
				fileInputStream.close();

			String errMessage = "Cannot load text " + fullName + ": " + ex.getMessage();
			Dic.writeLog(errMessage);

			return null;
		}

		if (objectInputStream != null)
			objectInputStream.close();

		if (fileInputStream != null)
			fileInputStream.close();

		for (int i = 0; i < cd.theTags.size(); i++)
		{
			ArrayList<Object> theTag = cd.theTags.get(i);

			Object[] item = (Object[]) cd.theItems.get(i * 4 + 1);
			item[5] = theTag;
		}

		return cd;
	}

	public List<Object> getTheItems()
	{
		return theItems;
	}
}