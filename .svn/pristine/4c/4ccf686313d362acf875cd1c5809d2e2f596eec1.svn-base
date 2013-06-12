package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.gui.dialogs.PreferencesDialog;

import org.apache.commons.io.FilenameUtils;

public class DeleteLexActionListener implements ActionListener
{

	private PreferencesDialog dialog = null;
	private JButton bDelete = null;
	private JButton bSDelete = null;
	private JLabel lLexDoc = null;
	private JLabel lSynDoc = null;

	public DeleteLexActionListener(PreferencesDialog dialog, JLabel lLexDoc, JLabel lSynDoc, JButton bDelete,
			JButton bSDelete)
	{
		this.dialog = dialog;
		this.lLexDoc = lLexDoc;
		this.bDelete = bDelete;
		this.bSDelete = bSDelete;
		this.lSynDoc = lSynDoc;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String fullname = null;
		JTable activeTable = null;
		JList activeList = null;

		if (e.getSource() == this.bDelete)
		{
			fullname = this.lLexDoc.getText();
			JTable dicTable = this.dialog.getTableDictionary();
			JTable morphTable = this.dialog.getTableMorphology();
			activeTable = dicTable.getSelectedRow() != -1 ? dicTable : morphTable;

			if (activeTable.getSelectedRow() == -1)
				return;
		}
		else if (e.getSource() == this.bSDelete)
		{
			fullname = this.lSynDoc.getText();
			activeList = this.dialog.getListSynResources();
		}

		if (fullname == null || fullname == "")
			return;

		File f = new File(fullname);

		if (!f.exists())
			return;

		String fname = FilenameUtils.getName(fullname);

		if (JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete " + fname + "?", "Nooj delete?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			f.delete();
		}

		if (activeTable != null)
		{
			int index = activeTable.convertRowIndexToModel(activeTable.getSelectedRow());
			((DefaultTableModel) activeTable.getModel()).removeRow(index);
		}
		else
		{
			int index = activeList.getSelectedIndex();
			((DefaultListModel) activeList.getModel()).removeElementAt(index);
		}
	}
}