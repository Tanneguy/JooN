package net.nooj4nlp.controller.StatsShell;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Custom header renderer. Responsible for aligning header text as desired.
 */

public class CustomHeaderRenderer implements TableCellRenderer
{
	private DefaultTableCellRenderer renderer;

	/**
	 * Constructor.
	 * 
	 * @param table
	 *            - table which headers will be alligned
	 */

	public CustomHeaderRenderer(JTable table)
	{
		super();
		renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		// column with terms needs to be alligned left, others - right
		if (column != 0)
			renderer.setHorizontalAlignment(JLabel.RIGHT);
		else
			renderer.setHorizontalAlignment(JLabel.LEFT);

		return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}