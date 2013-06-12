package net.nooj4nlp.gui.components;

import java.text.Collator;
import java.util.Comparator;

import net.nooj4nlp.engine.Language;

/**
 * 
 * Custom comparator of data objects array in JTable's rows.
 * 
 */
public class NooJTableSorter implements Comparator<Object[]>
{
	// sorting column
	private int currentColumn;
	// given language
	private Language language = null;

	// boolean to determine whether sorting should be from right to left (for Asian languages) or not
	private boolean reversedSort = false;
	// boolean to determine if we are sorting numbers or not
	private boolean sortingIntegers = false;

	/**
	 * Default constructor.
	 * 
	 * @param currentColumn
	 *            - column to be sorted by
	 * @param language
	 *            - language of corpus/text
	 */

	public NooJTableSorter(int currentColumn, Language language)
	{
		this.language = language;
		this.currentColumn = currentColumn;
	}

	/**
	 * Constructor to be called when reverse sorting and/or sorting of numbers is needed.
	 * 
	 * @param currentColumn
	 *            - column to be sorted by
	 * @param reversedSort
	 *            - boolean to determine whether sorting should be from right to left (for Asian languages) or not
	 * @param sortingIntegers
	 *            - boolean to determine if we are sorting numbers or not
	 * @param language
	 *            - language of corpus/text
	 */
	public NooJTableSorter(int currentColumn, boolean reversedSort, boolean sortingIntegers, Language language)
	{
		this.currentColumn = currentColumn;
		this.reversedSort = reversedSort;
		this.sortingIntegers = sortingIntegers;
		this.language = language;
	}

	/**
	 * Overridden function compares two arrays of objects
	 * 
	 * @param o1
	 *            - data from the first row
	 * @param o2
	 *            - data from the second row
	 */

	public int compare(Object[] o1, Object[] o2)
	{
		// if it's a case of string sorting
		if (!sortingIntegers)
		{
			Collator collator;

			// if language is null, get default instance of collator
			if (language != null)
				collator = Collator.getInstance(language.locale);
			else
				collator = Collator.getInstance();

			collator.setStrength(Collator.IDENTICAL);

			// get two strings
			String firstString = o1[currentColumn].toString();
			String secondString = o2[currentColumn].toString();

			// if it's a regular sorting, just compare and exit...
			if (!reversedSort)
				return collator.compare(firstString, secondString);

			// ...otherwise revert strings and compare them
			else
			{
				StringBuilder tmp1 = new StringBuilder();
				StringBuilder tmp2 = new StringBuilder();

				tmp1.append(firstString).reverse();
				tmp2.append(secondString).reverse();

				return collator.compare(tmp1.toString(), tmp2.toString());
			}
		}

		// if it's a case of sorting numbers
		else
		{
			// get numbers and sort them descending
			int firstNumber = (Integer) o1[currentColumn];
			int secondNumber = (Integer) o2[currentColumn];

			if (firstNumber < secondNumber)
				return 1;
			else if (firstNumber > secondNumber)
				return -1;
			else
				return 0;
		}
	}
}