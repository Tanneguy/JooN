package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.nooj4nlp.engine.Language;

/**
 * Class for implementation of sorting concordance's table.
 * 
 */
public class ConcordanceTableSorter extends MouseAdapter
{
	// variables
	private ConcordanceShellController controller;
	private JTable concordanceTable;
	private DefaultTableModel tableModel;

	// index of clicked column
	private int index;
	// language of corpus/text
	private Language lan;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param concordanceTable
	 *            - concordance's table
	 */

	public ConcordanceTableSorter(ConcordanceShellController controller, JTable concordanceTable)
	{
		this.controller = controller;
		this.concordanceTable = concordanceTable;
		this.tableModel = (DefaultTableModel) this.concordanceTable.getModel();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// sort colors of the table
		// Changed when removing static variables
		CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
				.getDefaultRenderer(Object.class);
		customForegroundTableRenderer.sortTableColors();
		// save the active table model. It will be necessary if we want to add items to concordance.
		controller.setTableModel(tableModel);

		// get clicked column
		JTable table = ((JTableHeader) e.getSource()).getTable();
		TableColumnModel colModel = table.getColumnModel();
		index = colModel.getColumnIndexAtX(e.getX());

		// get data vector and convert it to a list of objects
		Vector<?> dataVector = tableModel.getDataVector();
		List<Object> dataList = new ArrayList<Object>(dataVector);

		// initialize colored rows lists...sorting needs to be done by color and then by language
		// we'll create colored lists and we'll sort them individually by language, and in the end, they all will be
		// added to one list
		List<Object[]> blackList = new ArrayList<Object[]>();
		List<Object[]> redList = new ArrayList<Object[]>();
		List<Object[]> greenList = new ArrayList<Object[]>();
		List<Object[]> blueList = new ArrayList<Object[]>();

		// get predefined color map and comparing language
		Map<Color, Integer> map = controller.getColorMap();
		lan = controller.getLan();

		// iterating through data list, and put data to adequate color list depending on row's color
		for (int i = 0; i < dataList.size(); i++)
		{
			Object[] tableRow = ((Vector<?>) (dataList.get(i))).toArray();
			int color = (Integer) tableRow[4];

			if (color == map.get(Color.BLACK))
				blackList.add(tableRow);
			else if (color == map.get(Color.RED))
				redList.add(tableRow);
			else if (color == map.get(Color.GREEN))
				greenList.add(tableRow);
			else
				blueList.add(tableRow);
		}

		List<Object[]> newDataList = new ArrayList<Object[]>();

		// sort all list by language
		Collections.sort(blackList, new ConcordanceItemComparer(index, lan.locale));
		Collections.sort(redList, new ConcordanceItemComparer(index, lan.locale));
		Collections.sort(greenList, new ConcordanceItemComparer(index, lan.locale));
		Collections.sort(blueList, new ConcordanceItemComparer(index, lan.locale));

		// add lists to main data list
		newDataList.addAll(blackList);
		newDataList.addAll(redList);
		newDataList.addAll(greenList);
		newDataList.addAll(blueList);

		// create new data model
		DefaultTableModel newTableModel = new DefaultTableModel(new Object[] { "Text", "Before", "Seq.", "After",
				"Color", "Tag" }, 0);

		// add data list to the model and set the model
		for (int i = 0; i < newDataList.size(); i++)
			newTableModel.addRow(newDataList.get(i));

		table.setModel(newTableModel);

		// Removing column - it is not supposed to be seen!
		table.removeColumn(concordanceTable.getColumnModel().getColumn(5));
		table.removeColumn(concordanceTable.getColumnModel().getColumn(4));

		// set adequate widths of columns
		controller.setWidthOfTableColumn(table, newTableModel, 0);
		controller.setWidthOfTableColumn(table, newTableModel, 1);
		controller.setWidthOfTableColumn(table, newTableModel, 2);
		controller.setWidthOfTableColumn(table, newTableModel, 3);

		controller.setConcordanceTableSorted(true);
	}
}