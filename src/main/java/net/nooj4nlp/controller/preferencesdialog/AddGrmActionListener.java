package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class AddGrmActionListener implements ActionListener
{
	private JList listSynResources;
	private JTable lvSGrm;

	private PreferencesDialog preferencesDialog;

	public AddGrmActionListener(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		listSynResources = preferencesDialog.getListSynResources();
		lvSGrm = preferencesDialog.getTableResources();

		if (listSynResources.getSelectedValue() == null)
			return;

		DefaultTableModel model = (DefaultTableModel) lvSGrm.getModel();

		for (int id : listSynResources.getSelectedIndices())
		{
			String fname = (String) listSynResources.getModel().getElementAt(id);
			boolean alreadythere = false;

			for (int i = 0; i < model.getRowCount(); i++)
			{
				String cname = (String) model.getValueAt(i, 1);
				if (cname.equalsIgnoreCase(fname))
				{
					alreadythere = true;
					break;
				}
			}

			if (!alreadythere)
			{
				int priority = model.getRowCount() + 1;
				model.addRow(new Object[] { priority, fname });

				lvSGrm.repaint();
			}
		}
	}
}