package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.components.CustomCell;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class LexTableMouseListener extends MouseAdapter
{
	private boolean dic;

	private JTable lvDic;
	private JTable lvMor;

	private PreferencesDialog preferencesDialog;

	public LexTableMouseListener(JTable lvDic, JTable lvMor, PreferencesDialog preferencesDialog, boolean dic)
	{
		this.dic = dic;
		this.preferencesDialog = preferencesDialog;
	}

	public void mouseClicked(MouseEvent e)
	{
		lvDic = preferencesDialog.getTableDictionary();
		lvMor = preferencesDialog.getTableMorphology();

		String commentname = "";

		if (dic)
		{
			int rowIndex = lvDic.rowAtPoint(e.getPoint());

			// If mouse is clicked outside table, nothing should be done
			if (rowIndex != -1)
			{
				DefaultTableModel model = (DefaultTableModel) lvDic.getModel();
				Vector<?> data = model.getDataVector();
				Vector<?> cell = (Vector<?>) data.get(rowIndex);

				String lname = (String) preferencesDialog.getCbDefLanguage().getSelectedItem();
				String fullname = "";

				String fname = ((CustomCell) cell.get(0)).label.getText();

				fullname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Lexical Analysis"
						+ System.getProperty("file.separator") + fname;
				commentname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Lexical Analysis"
						+ System.getProperty("file.separator") + fname.substring(0, fname.lastIndexOf(".")) + ".txt";
				preferencesDialog.getLblLexDoc().setText(fullname);

				if (lvMor.getSelectedRow() != -1)
				{
					ListSelectionModel sm = lvMor.getSelectionModel();

					int indexMor = lvMor.convertRowIndexToModel(lvMor.getSelectedRow());
					CustomCell cc = (CustomCell) lvMor.getModel().getValueAt(indexMor, 0);
					cc.setBackground(lvMor.getBackground());
					lvMor.getModel().setValueAt(cc, indexMor, 0);

					sm.clearSelection();
				}
			}
			else
				return;
		}
		else
		{
			int rowIndex = lvMor.rowAtPoint(e.getPoint());

			// If mouse is clicked outside table, nothing should be done
			if (rowIndex != -1)
			{
				DefaultTableModel model = (DefaultTableModel) lvMor.getModel();
				Vector<?> data = model.getDataVector();
				Vector<?> cell = (Vector<?>) data.get(rowIndex);

				String lname = (String) preferencesDialog.getCbDefLanguage().getSelectedItem();
				String fullname = "";

				String fname = ((CustomCell) cell.get(0)).label.getText();

				fullname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Lexical Analysis"
						+ System.getProperty("file.separator") + fname;
				commentname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Lexical Analysis"
						+ System.getProperty("file.separator") + fname.substring(0, fname.lastIndexOf(".")) + ".txt";
				preferencesDialog.getLblLexDoc().setText(fullname);

				if (lvDic.getSelectedRow() != -1)
				{
					ListSelectionModel sm = lvDic.getSelectionModel();
					int indexDic = lvDic.convertRowIndexToModel(lvDic.getSelectedRow());
					CustomCell cc = (CustomCell) lvDic.getModel().getValueAt(indexDic, 0);
					cc.setBackground(lvDic.getBackground());
					lvDic.getModel().setValueAt(cc, indexDic, 0);
					sm.clearSelection();
				}
			}
			else
				return;
		}

		preferencesDialog.getTxtFileInfoLex().setText("");
		File commentFile = new File(commentname);
		if (commentFile.exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commentFile), "UTF8"));

				try
				{
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();

					while (line != null)
					{
						sb.append(line);
						sb.append("\n");
						line = br.readLine();
					}
					String everything = sb.toString();
					preferencesDialog.getTxtFileInfoLex().append(everything);
				}
				finally
				{
					br.close();
				}
			}
			catch (Exception e1)
			{

			}
		}
	}
}