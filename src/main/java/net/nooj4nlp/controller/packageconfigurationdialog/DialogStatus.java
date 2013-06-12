package net.nooj4nlp.controller.packageconfigurationdialog;

import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

public class DialogStatus implements Serializable
{
	private static final long serialVersionUID = 7891217418033204686L;

	private String rexp;
	private String gram;
	private String radioQuery;
	private String parentWindowName;

	public DialogStatus(String parentWindowFullName, String re, String gr, String rb)
	{
		rexp = re;
		gram = gr;
		radioQuery = rb;

		parentWindowName = FilenameUtils.getName(parentWindowFullName);
	}

	public String getRexp()
	{
		return rexp;
	}

	public String getGram()
	{
		return gram;
	}

	public String getRadioQuery()
	{
		return radioQuery;
	}

	public String getParentWindowName()
	{
		return parentWindowName;
	}
}