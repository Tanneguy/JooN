package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;

import org.apache.commons.io.FilenameUtils;

public class PriorityLexActionListener implements ActionListener
{

	private JLabel lblLexDoc;
	private int inc = 0;
	private PreferencesDialog dialog;

	public PriorityLexActionListener(int inc, JLabel lblLexDoc, PreferencesDialog dialog)
	{
		this.lblLexDoc = lblLexDoc;
		this.dialog = dialog;
		this.inc = inc;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String fullname = lblLexDoc.getText();
		
		String ext = FilenameUtils.getExtension(fullname);
		JTable lv = null;
		if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
		{
			lv = dialog.getTableDictionary();
		}
		else
		{
			lv = dialog.getTableMorphology();
		}

		if (lv.getSelectedRowCount() <= 0)
			return;

		int id = lv.getSelectedRow();

		Vector<?> data = ((DefaultTableModel) lv.getModel()).getDataVector();
		Vector<?> row = (Vector<?>) data.get(id);

		String cprio = (String) row.get(1);
		int cpriority = 0;
		if (cprio.length() == 2 && cprio.substring(0, 1).equalsIgnoreCase("H"))
			cpriority = Integer.parseInt(cprio.substring(1));
		else if (cprio.length() == 2 && cprio.substring(0, 1).equalsIgnoreCase("L"))
			cpriority = -Integer.parseInt(cprio.substring(1));

		String value = "";
		if (inc != 0)
		{
			cpriority += inc;
			if (cpriority >= 10)
				cpriority = 9;
			if (cpriority < -9)
				cpriority = -9;
			if (cpriority > 0)
				value = "H" + String.valueOf(cpriority);
			else if (cpriority == 0)
				value = "";
			else
				value = "L" + String.valueOf(Math.abs(cpriority));
		}

		DefaultTableModel model = (DefaultTableModel) lv.getModel();
		model.setValueAt(value, id, 1);
	}

}