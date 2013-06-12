package net.nooj4nlp.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class implements renderer of Debug Shell's JTable.
 */

public class DebugJTableRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	// map storing the data of all colored rows
	private HashMap<Integer, Color> coloredRowsMap = new HashMap<Integer, Color>();

	/**
	 * Simple constructor.
	 */

	public DebugJTableRenderer()
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
		if (coloredRowsMap.containsKey(row))
			c.setForeground(coloredRowsMap.get(row));

		// return rendered cell
		return c;
	}

	// getters and setters
	public HashMap<Integer, Color> getColoredRowsMap()
	{
		return coloredRowsMap;
	}

	public void setColoredRowsMap(HashMap<Integer, Color> coloredRowsMap)
	{
		this.coloredRowsMap = coloredRowsMap;
	}
}