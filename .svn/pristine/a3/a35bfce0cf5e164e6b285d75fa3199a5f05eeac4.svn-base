package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.gui.components.CustomCell;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class CheckAllActionListener implements ActionListener
{

	private int tableID = 0;
	private boolean checked;
	private PreferencesDialog dialog;

	public CheckAllActionListener(PreferencesDialog dialog, int id, boolean checked)
	{
		this.dialog = dialog;
		this.checked = checked;
		this.tableID = id;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JTable table;
		if (tableID == 0)
			table = dialog.getTableDictionary();
		else
			table = dialog.getTableMorphology();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Vector<?> dataVector = model.getDataVector();
		Object[] data = dataVector.toArray();
		for (int i = 0; i < data.length; i++)
		{
			Vector<?> file = (Vector<?>) data[i];
			CustomCell value = (CustomCell) file.get(0);
			value.checkBox.setSelected(checked);
			model.setValueAt(value, i, 0);
		}
	}
}