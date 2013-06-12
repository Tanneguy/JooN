package net.nooj4nlp.gui.dialogs.OpenCorpusDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.components.NooJTableSorter;

/**
 * Class implements sorting table of TokensDigrams and AmbiguitiesUnambiguities dialogs.
 */

public class InversiveSortActionListener extends MouseAdapter
{
	// active dialogs (one per instance)
	private TokensDigramsDialog tokensDigramsDialog = null;
	private AmbiguitiesUnambiguitiesDialog ambigUnambigDialog = null;

	// language and flag for reversed order or not
	private boolean reversedSortingActive;
	private Language lan;

	/**
	 * Constructor of a TokensDigrams context.
	 * 
	 * @param tokensDigramsDialog
	 *            - dialog which table needs to be sorted
	 * @param reversedSortingActive
	 *            - flag to determine whether sorting should be from right to left or not
	 * @param lan
	 *            - sorting language
	 */

	public InversiveSortActionListener(TokensDigramsDialog tokensDigramsDialog, boolean reversedSortingActive,
			Language lan)
	{
		this.tokensDigramsDialog = tokensDigramsDialog;
		this.reversedSortingActive = reversedSortingActive;
		this.lan = lan;
	}

	/**
	 * Constructor of a AmbiguitiesUnambiguities context.
	 * 
	 * @param ambigUnambigDialog
	 *            - dialog which table needs to be sorted
	 * @param reversedSortingActive
	 *            - flag to determine whether sorting should be from right to left or not
	 * @param lan
	 *            - sorting language
	 */

	public InversiveSortActionListener(AmbiguitiesUnambiguitiesDialog ambigUnambigDialog,
			boolean reversedSortingActive, Language lan)
	{
		this.ambigUnambigDialog = ambigUnambigDialog;
		this.reversedSortingActive = reversedSortingActive;
		this.lan = lan;
	}

	public void mouseClicked(MouseEvent e)
	{
		// get clicked column and table model
		JTable table = ((JTableHeader) e.getSource()).getTable();
		TableColumnModel colModel = table.getColumnModel();
		int index = colModel.getColumnIndexAtX(e.getX());

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

		// get data vector and convert it to a list of objects
		Vector<?> dataVector = tableModel.getDataVector();
		List<Object> dataList = new ArrayList<Object>(dataVector);
		List<Object[]> newDataList = new ArrayList<Object[]>();

		for (int i = 0; i < dataList.size(); i++)
		{
			Object[] tableRow = ((Vector<?>) (dataList.get(i))).toArray();
			newDataList.add(tableRow);
		}

		// clear old table model
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();

		// if it's sorting by first column - it's numbers sorting case
		if (index == 0)
			Collections.sort(newDataList, new NooJTableSorter(0, reversedSortingActive, true, lan));

		// otherwise, it's a string sorting
		else
		{
			// if sorting is called from tokens/digrams context, change the flag
			if (this.tokensDigramsDialog != null)
			{
				Collections.sort(newDataList, new NooJTableSorter(1, reversedSortingActive, false, lan));
				tokensDigramsDialog.setReversedSortingActive(!reversedSortingActive);
			}

			// if sorting is called from ambig/unambig context, change the flag
			else if (this.ambigUnambigDialog != null)
			{
				Collections.sort(newDataList, new NooJTableSorter(1, reversedSortingActive, false, lan));
				ambigUnambigDialog.setReversedSortingActive(!reversedSortingActive);
			}
		}

		// add data list to the model and set the model
		for (int i = 0; i < newDataList.size(); i++)
			tableModel.addRow(newDataList.get(i));

		table.setModel(tableModel);
		this.reversedSortingActive = !this.reversedSortingActive;
	}

	public void setTokensDigramsDialog(TokensDigramsDialog tokensDigramsDialog)
	{
		this.tokensDigramsDialog = tokensDigramsDialog;
	}

	public void setAmbigUnambigDialog(AmbiguitiesUnambiguitiesDialog ambigUnambigDialog)
	{
		this.ambigUnambigDialog = ambigUnambigDialog;
	}
}