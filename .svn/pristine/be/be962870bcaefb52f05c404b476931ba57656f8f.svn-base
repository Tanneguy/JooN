package net.nooj4nlp.gui.utilities;

import java.awt.Component;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

public class Helper
{
	private static final int MINIMUM_COLUMN_WIDTH = 50;

	/**
	 * Function for dynamically auto sorting of column widths. Takes care of small columns, too.
	 * 
	 * @param table
	 *            - table whose columns widths needs to be fixed
	 * @param tableModel
	 *            - model of a table
	 * @param column
	 *            - actual column that needs to be sorted
	 */
	public static void setWidthOfTableColumn(JTable table, DefaultTableModel tableModel, int column)
	{
		int width = 0;

		// for every row, calculate preferred size of the width, and set maximum
		for (int row = 0; row < tableModel.getRowCount(); row++)
		{
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component comp = table.prepareRenderer(renderer, row, column);
			width = Math.max(comp.getPreferredSize().width, width);
		}

		int tableWidth = table.getWidth();

		// Part that takes care of small columns; if it's first column and needs to be wider...
		if (column == 0 && width < MINIMUM_COLUMN_WIDTH)
			width = MINIMUM_COLUMN_WIDTH;
		// If a column is less than third of a table width
		else if (width < (tableWidth / 3))
			width = (tableWidth / 3);

		// Set max and preffered width (increased for 5 pixels to avoid the dots) to column of a table
		table.getColumnModel().getColumn(column).setMaxWidth(width + 5);
		table.getColumnModel().getColumn(column).setPreferredWidth(width + 5);
	}

	/**
	 * Function puts window on top of all opened windows and makes it selected.
	 * 
	 * @param window
	 *            - future top window
	 */

	public static void putDialogOnTheTop(JInternalFrame window)
	{
		try
		{
			window.setSelected(true);
		}
		catch (PropertyVetoException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_WINDOW_ON_TOP,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}
}