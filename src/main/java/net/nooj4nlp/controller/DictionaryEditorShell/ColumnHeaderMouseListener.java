package net.nooj4nlp.controller.DictionaryEditorShell;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ColumnHeaderMouseListener implements MouseListener
{

	private DictionaryEditorShellController controller;
	private boolean ent_asc;

	public ColumnHeaderMouseListener(DictionaryEditorShellController controller)
	{
		super();
		this.controller = controller;
		this.ent_asc = true;
	}

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		JTable table = ((JTableHeader) evt.getSource()).getTable();
		TableColumnModel colModel = table.getColumnModel();

		// The index of the column whose header was clicked
		int vColIndex = colModel.getColumnIndexAtX(evt.getX());

		// Return if not clicked on first column header
		if (vColIndex != 0)
		{
			return;
		}

		// Unchecked cast cannot be avoided unless by making a custom class that extends TableModel, and there is really
		// no need for that.
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) controller.getTable().getRowSorter();
		if (ent_asc)
			sorter.setComparator(0, DictionaryEditorShellController.getComparatorInv());
		else
			sorter.setComparator(0, DictionaryEditorShellController.getComparator());

		sorter.toggleSortOrder(0);
		ent_asc = !ent_asc;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
}
