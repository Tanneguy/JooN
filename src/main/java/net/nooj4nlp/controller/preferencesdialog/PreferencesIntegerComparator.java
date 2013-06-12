package net.nooj4nlp.controller.preferencesdialog;

import java.util.Comparator;

public class PreferencesIntegerComparator implements Comparator<Object[]>
{
	private int currentColumn;

	public PreferencesIntegerComparator(int currentColumn)
	{
		this.currentColumn = currentColumn;
	}

	@Override
	public int compare(Object[] o1, Object[] o2)
	{
		// get numbers and sort them descending
		int firstNumber = (Integer) o1[currentColumn];
		int secondNumber = (Integer) o2[currentColumn];

		if (firstNumber < secondNumber)
			return -1;
		else if (firstNumber > secondNumber)
			return 1;
		else
			return 0;
	}
}