package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class PrioritySynActionListener implements ActionListener
{
	private JTable lvSGrm;
	private int inc;
	private PreferencesDialog preferencesDialog;

	public PrioritySynActionListener(int inc, PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		this.inc = inc;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		lvSGrm = preferencesDialog.getTableResources();

		// look for item in this.lvSGrm
		int id = lvSGrm.getSelectedRow();
		if (id < 0)
			return;

		DefaultTableModel model = (DefaultTableModel) lvSGrm.getModel();

		
		String citem = (String) (model.getValueAt(id, 1));
		int cpriority = (Integer) (model.getValueAt(id, 0));

		if (inc == 0)
		{
			String nextItem = null;

			int indexForSelection = 0;

			for (int i = 0; i < model.getRowCount(); i++)
			{
				String item = (String) (model.getValueAt(i, 1));
				int priority = (Integer) model.getValueAt(i, 0);

				if (!item.equalsIgnoreCase(citem) && nextItem == null)
				{
					indexForSelection = i;
				}
				if (priority > cpriority)
				{
					if (nextItem == null)
					{
						nextItem = item;
						indexForSelection = i;
					}
					priority--;

					model.setValueAt(priority, i, 0);
				}
			}

			lvSGrm.getSelectionModel().setSelectionInterval(indexForSelection, indexForSelection);

			model.removeRow(id);

			// get data vector and convert it to a list of objects before sorting it
			Vector<?> dataVector = model.getDataVector();
			List<Object> dataList = new ArrayList<Object>(dataVector);

			List<Object[]> newDataList = new ArrayList<Object[]>();

			for (int i = 0; i < dataList.size(); i++)
			{
				Object[] tableRow = ((Vector<?>) (dataList.get(i))).toArray();
				newDataList.add(tableRow);
			}

			Collections.sort(newDataList, new PreferencesIntegerComparator(0));
			lvSGrm.repaint();
		}
		else
		{
			cpriority += inc;
			if (cpriority <= 0)
				cpriority = 1;
			if (cpriority > lvSGrm.getRowCount())
				cpriority = lvSGrm.getRowCount();

			model.setValueAt(cpriority, id, 0);

			for (int i = 0; i < model.getRowCount(); i++)
			{
				String item = (String) (model.getValueAt(i, 1));
				int priority = (Integer) (model.getValueAt(i, 0));

				if (!item.equalsIgnoreCase(citem) && (priority == cpriority))
				{
					priority -= inc;
					model.setValueAt(priority, i, 0);

					lvSGrm.repaint();
				}
			}
		}
	}
}