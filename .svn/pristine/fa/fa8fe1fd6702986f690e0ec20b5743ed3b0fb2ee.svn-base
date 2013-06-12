package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.NooJTableSorter;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements sorting of corpus.
 * 
 */
public class TableSorterActionListener extends MouseAdapter
{
	// index of clicked column
	private int index = 0;
	// actual table model
	private DefaultTableModel tableModel;

	/**
	 * Empty constructor.
	 */
	public TableSorterActionListener()
	{
		super();
	}

	public void mouseClicked(MouseEvent e)
	{
		// get clicked column
		JTable table = ((JTableHeader) e.getSource()).getTable();
		TableColumnModel colModel = table.getColumnModel();
		index = colModel.getColumnIndexAtX(e.getX());

		// set model
		tableModel = (DefaultTableModel) table.getModel();

		sortTheTable(table, tableModel, index);
	}

	/**
	 * Function sorts table of corpus.
	 * 
	 * @param table
	 *            - corpus table to be sorted
	 * @param tableModel
	 *            - actual table model
	 * @param index
	 *            - sorting by column given in this index
	 */
	public void sortTheTable(JTable table, DefaultTableModel tableModel, int index)
	{
		// get data vector and convert it to a list of objects
		Vector<?> dataVector = tableModel.getDataVector();
		List<Object> dataList = new ArrayList<Object>(dataVector);
		List<Object[]> newDataList = new ArrayList<Object[]>();

		for (int i = 0; i < dataList.size(); i++)
		{
			Object[] tableRow = ((Vector<?>) (dataList.get(i))).toArray();
			newDataList.add(tableRow);
		}

		// if it's the first column, it's regular sorting of file names
		if (index == 0)
			Collections.sort(newDataList, new NooJTableSorter(index, null));

		// if it's the second column, then it's sorting by length of files
		else if (index == 1)
		{
			List<Long> listOfLength = new ArrayList<Long>();

			// add to list all lengths of files in corpus
			for (int j = 0; j < newDataList.size(); j++)
				listOfLength.add((Long) newDataList.get(j)[1]);

			// sort the list
			Collections.sort(listOfLength);
			List<Object[]> helpList = new ArrayList<Object[]>();

			// get the matching from sorted list and sort data list on the same way
			for (int k = 0; k < listOfLength.size(); k++)
			{
				for (int l = 0; l < newDataList.size(); l++)
				{
					if (listOfLength.get(k) == (Long) newDataList.get(l)[1])
					{
						helpList.add(newDataList.get(l));
						newDataList.remove(l);
						break;
					}
				}
			}

			newDataList = helpList;
		}

		// if it's the third column, then it's sorting of modifying dates
		else
		{
			// set written date format
			List<Date> listOfDates = new ArrayList<Date>();
			String dateFormatString = "dd/MM/yyyy";
			SimpleDateFormat format = new SimpleDateFormat(dateFormatString);

			// parse all dates and insert them into a list
			for (int j = 0; j < newDataList.size(); j++)
			{
				String date = newDataList.get(j)[2].toString();

				Date newDate = null;

				try
				{
					newDate = format.parse(date);
				}
				catch (ParseException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_DATE_PARSE, JOptionPane.ERROR_MESSAGE);
					return;
				}

				listOfDates.add(newDate);
			}

			// sort the dates
			Collections.sort(listOfDates);

			List<Object[]> helpList = new ArrayList<Object[]>();

			// get the matching from sorted list and sort data list on the same way
			for (int k = 0; k < listOfDates.size(); k++)
			{
				for (int l = 0; l < newDataList.size(); l++)
				{
					Date newDate = null;

					try
					{
						newDate = format.parse(newDataList.get(l)[2].toString());
					}
					catch (ParseException e1)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
								Constants.ERROR_MESSAGE_TITLE_DATE_PARSE, JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (listOfDates.get(k).equals(newDate))
					{
						helpList.add(newDataList.get(l));
						newDataList.remove(l);
						break;
					}
				}
			}

			newDataList = helpList;
		}

		// clear old table model
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();

		// add new, sorted data list to the model and set the model
		for (int i = 0; i < newDataList.size(); i++)
			tableModel.addRow(newDataList.get(i));

		table.setModel(tableModel);
	}

	public int getIndex()
	{
		return index;
	}
}
