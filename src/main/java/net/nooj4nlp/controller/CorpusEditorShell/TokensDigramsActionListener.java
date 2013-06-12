package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;

public class TokensDigramsActionListener implements ActionListener
{
	private TokensDigramsDialog tokensDigramsDialog;

	public TokensDigramsActionListener(TokensDigramsDialog tokensDigramsDialog, boolean isToken)
	{
		super();

		this.tokensDigramsDialog = tokensDigramsDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);

			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("Clear"))
			{
				tokensDigramsDialog.fillInTheTokensDigramsTable(tokensDigramsDialog.isACorpus());
			}
			else if (actionCommand.equals("SelectAll"))
			{
				JTable tokensTable = tokensDigramsDialog.getTableTokensDigrams();
				tokensTable.setRowSelectionAllowed(true);
				int rowCount = tokensTable.getRowCount();
				tokensTable.setRowSelectionInterval(0, rowCount - 1);
			}
			else if (actionCommand.equals("Filter"))
			{
				JTable tokensTable = tokensDigramsDialog.getTableTokensDigrams();
				DefaultTableModel tableModel = (DefaultTableModel) tokensTable.getModel();
				int[] selectedRowNumbers = tokensTable.getSelectedRows();
				int selectedRowsNumber = selectedRowNumbers.length;

				for (int k = 0; k < selectedRowsNumber; k++)
					selectedRowNumbers[k] = tokensTable.convertRowIndexToModel(selectedRowNumbers[k]);

				if (selectedRowsNumber == 0)
					return;

				List<Object[]> listOfSelectedItems = new ArrayList<Object[]>();

				for (int i = 0; i < selectedRowsNumber; i++)
				{
					Object[] obj = new Object[2];
					obj[0] = tableModel.getValueAt(selectedRowNumbers[i], 0);
					obj[1] = tableModel.getValueAt(selectedRowNumbers[i], 1);

					listOfSelectedItems.add(obj);
				}
				// remove all the elements from the table
				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();

				for (Object[] selectedTokenDigram : listOfSelectedItems)
					tableModel.addRow(selectedTokenDigram);
			}
			else if (actionCommand.equals("Export"))
			{
				tokensDigramsDialog.fillTheDictionary();
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}
}