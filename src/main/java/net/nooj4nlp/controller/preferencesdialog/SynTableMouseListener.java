package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class SynTableMouseListener extends MouseAdapter
{
	private boolean list;

	private JTable lvSGrm;
	private JList lbAvailableSyntacticResources;

	private PreferencesDialog preferencesDialog;

	/**
	 * 
	 * @param lvSGrm
	 * @param lbAvailableSyntacticResources
	 * @param preferencesDialog
	 * @param list
	 *            - flag that marks the table or list it is clicked on
	 */
	public SynTableMouseListener(JTable lvSGrm, JList lbAvailableSyntacticResources,
			PreferencesDialog preferencesDialog, boolean list)
	{
		this.list = list;
		this.preferencesDialog = preferencesDialog;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		lvSGrm = preferencesDialog.getTableResources();
		lbAvailableSyntacticResources = preferencesDialog.getListSynResources();

		String commentname = "";

		if (!list)
		{
			int rowIndex = lvSGrm.rowAtPoint(e.getPoint());

			// If mouse is clicked outside table, nothing should be done
			if (rowIndex != -1)
			{
				DefaultTableModel model = (DefaultTableModel) lvSGrm.getModel();

				String lname = (String) preferencesDialog.getCbDefLanguage().getSelectedItem();
				String fullname = "";

				String fname = (String) model.getValueAt(rowIndex, 1);

				fullname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Syntactic Analysis"
						+ System.getProperty("file.separator") + fname;
				commentname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Syntactic Analysis"
						+ System.getProperty("file.separator") + fname.substring(0, fname.lastIndexOf(".")) + ".txt";
				preferencesDialog.getLblSynDoc().setText(fullname);

				// Clearing out the selection
				lbAvailableSyntacticResources.getSelectionModel().clearSelection();
			}
			else
				return;
		}
		else
		{
			int rowIndex = lbAvailableSyntacticResources.locationToIndex(e.getPoint());

			// If mouse is clicked outside table, nothing should be done
			if (rowIndex != -1)
			{
				String lname = (String) preferencesDialog.getCbDefLanguage().getSelectedItem();
				String fullname = "";

				String fname = (String) lbAvailableSyntacticResources.getSelectedValue();

				fullname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Syntactic Analysis"
						+ System.getProperty("file.separator") + fname;
				commentname = Paths.docDir + System.getProperty("file.separator") + lname
						+ System.getProperty("file.separator") + "Syntactic Analysis"
						+ System.getProperty("file.separator") + fname.substring(0, fname.lastIndexOf(".")) + ".txt";
				preferencesDialog.getLblSynDoc().setText(fullname);

				// Clearing out the selection
				lvSGrm.getSelectionModel().clearSelection();
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