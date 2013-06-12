package net.nooj4nlp.controller.StatsShell;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer. Responsible for coloring individual cells of table in desired color.
 */

public class CustomCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 3751665304019380449L;

	// flag for determination whether table has or has not colored cells
	private boolean tableHasColoredCells = false;
	// cell matrix for determination whether certain cell needs to be colored
	private boolean[][] cellMatrix;

	/**
	 * Constructor.
	 */

	public CustomCellRenderer()
	{
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		// get component(cell) that needs to be rendered
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// if cell matrix has been set...
		if (tableHasColoredCells)
		{
			if (cellMatrix[column][row] == true)
			{
				c.setBackground(Color.YELLOW);
				c.setForeground(Color.RED);
			}
			else
			{
				c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);
			}
		}

		// set alignment
		if (column != 0)
			setHorizontalAlignment(RIGHT);
		else
			setHorizontalAlignment(LEFT);

		// return rendered cell
		return c;
	}

	public void setTableHasColoredCells(boolean tableHasColoredCells)
	{
		this.tableHasColoredCells = tableHasColoredCells;
	}

	public void setCellMatrix(boolean[][] cellMatrix)
	{
		this.cellMatrix = cellMatrix;
	}

	public boolean[][] getCellMatrix()
	{
		return cellMatrix;
	}
}