package net.nooj4nlp.controller.ConcordanceShell;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Custom comparator for concordance's columns.
 * 
 */
public class ConcordanceItemComparer implements Comparator<Object[]>
{
	private int currentColumn = 1;
	private Locale locale;

	/**
	 * Constructor.
	 * 
	 * @param column
	 *            - column to be sorted
	 * @param lan
	 *            - language of a text inside of column
	 * @param table
	 *            - actual table
	 */
	public ConcordanceItemComparer(int column, Locale locale)
	{
		this.locale = locale;
		this.currentColumn = column;
	}

	/**
	 * Overridden compare function. Compares Identical via locale, or reversed sequences, Identical via locale in case
	 * of second column.
	 * 
	 * @param o1
	 *            - first comparing object
	 * @param o2
	 *            - second comparing object
	 */

	public int compare(Object[] o1, Object[] o2)
	{
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(Collator.IDENTICAL);

		String firstString = o1[currentColumn].toString();
		String secondString = o2[currentColumn].toString();

		if (currentColumn == 1)
		{
			StringBuilder tmp1 = new StringBuilder();
			StringBuilder tmp2 = new StringBuilder();

			tmp1.append(firstString).reverse();
			tmp2.append(secondString).reverse();

			return collator.compare(tmp1.toString(), tmp2.toString());
		}

		return collator.compare(firstString, secondString);
	}
}