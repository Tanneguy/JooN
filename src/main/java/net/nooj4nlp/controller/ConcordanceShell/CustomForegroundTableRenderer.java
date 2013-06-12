package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.nooj4nlp.engine.Constants;

/**
 * Custom table renderer. Responsible for coloring individual rows of texts in desirable colors.
 */
public class CustomForegroundTableRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 3751665304019380449L;

	// flag for determination whether table should be rendered with sorted or unsorted color scheme
	private boolean sortedPreview = false;
	// maps with color schemes (row, text color of a row)
	private Map<Integer, Color> coloredRowsMap = new HashMap<Integer, Color>();
	private Map<Integer, Color> sortedRowsMap = new HashMap<Integer, Color>();

	/**
	 * Constructor.
	 */
	public CustomForegroundTableRenderer()
	{
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{

		// get component(cell) that needs to be rendered
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// depending of flag, set text color if it is mapped
		if (!sortedPreview)
		{
			if (coloredRowsMap.containsKey(row))
				c.setForeground(coloredRowsMap.get(row));
		}
		else
		{
			if (sortedRowsMap.containsKey(row))
				c.setForeground(sortedRowsMap.get(row));
		}

		// return rendered cell
		return c;
	}

	/**
	 * Function adds data to a colored map.
	 * 
	 * @param row
	 *            - actual row
	 * @param color
	 *            - color of a row text
	 */
	public void addColoredRowsToAMap(int row, Color color)
	{
		if (coloredRowsMap.containsKey(row))
			return;
		coloredRowsMap.put(row, color);
	}

	/**
	 * Function sorts color map scheme of rows inside concordance's table.
	 */
	public void sortTableColors()
	{
		// get map from renderer and iterate through it; count all colors found
		int blackLines = 0, redLines = 0, greenLines = 0;
		Map<Integer, Color> actualColorMap = getColoredRowsMap();
		Iterator<Entry<Integer, Color>> iter = actualColorMap.entrySet().iterator();

		while (iter.hasNext())
		{
			Entry<Integer, Color> pairs = iter.next();
			Color color = pairs.getValue();

			// button is gray colored, but we want text in black!
			if (color.equals(Color.BLACK))
				blackLines++;
			else if (color.equals(Constants.NOOJ_RED_BUTTON_COLOR))
				redLines++;
			else if (color.equals(Constants.NOOJ_GREEN_BUTTON_COLOR))
				greenLines++;
		}

		// create a new temporary map for renderer
		Map<Integer, Color> sortedColorMap = new HashMap<Integer, Color>();
		for (int i = 0; i < actualColorMap.size(); i++)
		{
			if (i < blackLines)
				sortedColorMap.put(i, Color.BLACK);
			else if (i < blackLines + redLines)
				sortedColorMap.put(i, Constants.NOOJ_RED_BUTTON_COLOR);
			else if (i < blackLines + redLines + greenLines)
				sortedColorMap.put(i, Constants.NOOJ_GREEN_BUTTON_COLOR);
			else
				sortedColorMap.put(i, Constants.NOOJ_BLUE_BUTTON_COLOR);
		}

		// set the new map and set the flag for it
		setSortedRowsMap(sortedColorMap);
		setSortedPreview(true);
	}

	/**
	 * Function responsible for updating color schemed map of concordance table in case of deleting certain row.
	 * 
	 * @param sortedIndex
	 *            - row that's going to be deleted in sorted list
	 * @param unsortedIndex
	 *            - row that's going to be deleted in unsorted list
	 */

	public void updateMapsWhenRowIsDeleted(int sortedIndex, int unsortedIndex)
	{
		setColoredRowsMap(removeDeletedItemsFromColorMaps(coloredRowsMap, unsortedIndex));
		setSortedRowsMap(removeDeletedItemsFromColorMaps(sortedRowsMap, sortedIndex));
	}

	/**
	 * Function removes desired row from color map.
	 * 
	 * @param colorMap
	 *            - map(size n) from which row will be deleted
	 * @param index
	 *            - index of row to be deleted
	 * @return - new map(size n-1) created after deleting row from the old one
	 */

	private static Map<Integer, Color> removeDeletedItemsFromColorMaps(Map<Integer, Color> colorMap, int index)
	{
		// if index is -1, return new map
		if (index == -1)
			return new HashMap<Integer, Color>();

		// if there's no such row in active map, just exit
		if (!colorMap.containsKey(index))
			return new HashMap<Integer, Color>();

		// sort a map and iterate through it
		colorMap = new TreeMap<Integer, Color>(colorMap);
		Iterator<Entry<Integer, Color>> iter = colorMap.entrySet().iterator();

		/*
		 * Deleting is done backwards, to avoid possible conflicts; do so, if a row is deleted, rows above don't need
		 * update. Rows below deleted row need to be shifted one position to top.
		 */
		int i = index;
		while (iter.hasNext())
		{
			Entry<Integer, Color> pairs = iter.next();

			// if iteration is still above the row that should be deleted, just get the next value
			if (pairs.getKey() <= index)
				continue;
			// if iteration is below the row that should be deleted, set the color of actual to previous value and
			// increase counter
			else
			{
				colorMap.put(i, pairs.getValue());
				i++;
			}
		}

		// remove the last element of the map (the result of shifting) - it's the deleted row
		colorMap.remove(colorMap.size() - 1);
		return colorMap;
	}

	// getters and setters
	public Map<Integer, Color> getColoredRowsMap()
	{
		return coloredRowsMap;
	}

	public void setSortedPreview(boolean sortedPreview)
	{
		this.sortedPreview = sortedPreview;
	}

	public void setSortedRowsMap(Map<Integer, Color> sortedRowsMap)
	{
		this.sortedRowsMap = sortedRowsMap;
	}

	public void setColoredRowsMap(Map<Integer, Color> coloredRowsMap)
	{
		this.coloredRowsMap = coloredRowsMap;
	}
}