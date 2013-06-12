package net.nooj4nlp.controller.preferencesdialog;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.components.CustomCell;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class UpdateTablesListener
{
	private JTable lvDic;
	private JTable lvMor;
	private JList lbAvailableSyntacticResources;
	private JTable lvSGrm;
	private PreferencesDialog dialog;

	private String docDirLex, docDirSyn, prjDirLex, prjDirSyn;

	public UpdateTablesListener(JTable lvDic, JTable lvMor, JList lbAvailableSyntacticResources, JTable lvSGrm,
			PreferencesDialog dialog)
	{
		this.lvDic = lvDic;
		this.lvMor = lvMor;
		this.dialog = dialog;
		this.lbAvailableSyntacticResources = lbAvailableSyntacticResources;
		this.lvSGrm = lvSGrm;
	}

	public void GetAllResourcesFromDisk(boolean projectmode, String languagename)
	{
		lvDic = dialog.getTableDictionary();
		DefaultTableModel dicModel = (DefaultTableModel) lvDic.getModel();
		if (dicModel.getRowCount() > 0)
		{
			// remove all the elements from the table
			dicModel.getDataVector().removeAllElements();
			dicModel.fireTableDataChanged();
		}

		// Table select listener
		lvDic.addMouseListener(new LexTableMouseListener(lvDic, lvMor, this.dialog, true));

		lvMor = dialog.getTableMorphology();
		DefaultTableModel morModel = (DefaultTableModel) lvMor.getModel();
		if (morModel.getRowCount() > 0)
		{
			// remove all the elements from the table
			morModel.getDataVector().removeAllElements();
			morModel.fireTableDataChanged();
		}

		// Table select listener
		lvMor.addMouseListener(new LexTableMouseListener(lvDic, lvMor, this.dialog, false));

		lvSGrm = dialog.getTableResources();
		DefaultTableModel resModel = (DefaultTableModel) lvSGrm.getModel();

		// Table select listener
		lvSGrm.addMouseListener(new SynTableMouseListener(lvSGrm, lbAvailableSyntacticResources, this.dialog, false));

		lbAvailableSyntacticResources = dialog.getListSynResources();
		DefaultListModel listModel = (DefaultListModel) lbAvailableSyntacticResources.getModel();
		if (listModel.size() > 0)
			listModel.clear();

		// List selection listener
		lbAvailableSyntacticResources.addMouseListener(new SynTableMouseListener(lvSGrm, lbAvailableSyntacticResources,
				this.dialog, true));

		boolean loadprojectresources = false;
		if (projectmode)
		{
			String zname = FilenameUtils.concat(Paths.projectDir, languagename);
			File dir = new File(zname);
			if (dir.isDirectory())
				loadprojectresources = true;
			else
				loadprojectresources = false;
		}

		if (!loadprojectresources)
		{
			// lexical analysis
			docDirLex = Paths.docDir + System.getProperty("file.separator") + languagename
					+ System.getProperty("file.separator") + "Lexical Analysis";
			File ldir = new File(docDirLex);
			if (!ldir.isDirectory())
				ldir.mkdirs();
			else
			{
				lvDic.getColumn("Dictionary").setCellRenderer(new CellEditorRenderer());
				lvDic.getColumn("Dictionary").setCellEditor(new CellEditorRenderer());

				lvMor.getColumn("Morphology").setCellRenderer(new CellEditorRenderer());
				lvMor.getColumn("Morphology").setCellEditor(new CellEditorRenderer());

				String[] list = ldir.list();
				for (String file : list)
				{
					String ext = FilenameUtils.getExtension(file);
					if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
					{
						CustomCell value = new CustomCell();
						value.label.setText(file);
						value.label
								.setSize(value.label.getPreferredSize().width, value.label.getPreferredSize().height);

						File f = new File(docDirLex + System.getProperty("file.separator")
								+ file.substring(0, file.lastIndexOf(".")) + Constants.JNOG_EXTENSION);
						if (f.exists())
							value.setForeground(Color.RED);
						dicModel.addRow(new Object[] { value, "" });
					}
					else if (ext.equalsIgnoreCase(Constants.JNOM_EXTENSION))
					{
						CustomCell value = new CustomCell();
						value.label.setText(file);
						value.label
								.setSize(value.label.getPreferredSize().width, value.label.getPreferredSize().height);

						File f = new File(docDirLex + System.getProperty("file.separator")
								+ file.substring(0, file.lastIndexOf(".")) + Constants.JNOM_EXTENSION);
						if (f.exists())
							value.setForeground(Color.RED);
						morModel.addRow(new Object[] { value, "" });
					}
				}
			}
			docDirSyn = Paths.docDir + System.getProperty("file.separator") + languagename
					+ System.getProperty("file.separator") + "Syntactic Analysis";
			File sdir = new File(docDirSyn);
			if (!sdir.isDirectory())
				sdir.mkdirs();
			else
			{
				String[] list = sdir.list();
				for (String file : list)
				{
					String ext = FilenameUtils.getExtension(file);
					if (ext.equalsIgnoreCase(Constants.JNOG_EXTENSION))
					{
						// listSynResources
						listModel.addElement(file);
					}
				}
			}
		}
		else
		{
			// Load resources from project's directory

			lvDic.getColumn("Dictionary").setCellRenderer(new CellEditorRenderer());
			lvDic.getColumn("Dictionary").setCellEditor(new CellEditorRenderer());

			lvMor.getColumn("Morphology").setCellRenderer(new CellEditorRenderer());
			lvMor.getColumn("Morphology").setCellEditor(new CellEditorRenderer());

			// Lexical analysis
			String lexName = FilenameUtils.concat(Paths.projectDir,
					FilenameUtils.concat(languagename, "Lexical Analysis"));
			File lexDir = new File(lexName);
			if (lexDir.exists())
				prjDirLex = lexName;
			else
				prjDirLex = FilenameUtils.concat(Paths.projectDir, languagename);

			File tempDir = new File(prjDirLex);
			for (File file : tempDir.listFiles())
			{
				String fullName = file.getName();
				if (!file.exists())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROJECT_RESOURCE_FILE + fullName
							+ Constants.PROJECT_DOES_NOT_EXIST, Constants.PROJECT_FILE_INVALID,
							JOptionPane.INFORMATION_MESSAGE);
					continue;
				}

				String dName = file.getParent();
				String fName = fullName;
				String fName2 = fName.substring(2);
				String ext = FilenameUtils.getExtension(fullName);

				if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
				{
					// Is it a lexicon grammar?
					Color c = Color.black;

					String nogFileName = FilenameUtils.concat(dName,
							FilenameUtils.concat(FilenameUtils.removeExtension(fullName), Constants.JNOG_EXTENSION));
					File nogFile = new File(nogFileName);
					if (nogFile.exists())
						c = Color.red;

					CustomCell value = new CustomCell();
					value.label.setText(fName2);
					value.label.setSize(value.label.getPreferredSize().width, value.label.getPreferredSize().height);
					value.setForeground(c);

					dicModel.addRow(new Object[] { value, "" });
				}
				else if (ext.equalsIgnoreCase(Constants.JNOM_EXTENSION))
				{
					morModel.addRow(new Object[] { new JCheckBox(fName2), "" });
				}
			}

			lvSGrm.getColumn("Grammar").setCellRenderer(new CellEditorRenderer());
			lvSGrm.getColumn("Grammar").setCellEditor(new CellEditorRenderer());

			// Syntactic analysis
			String synName = FilenameUtils.concat(Paths.projectDir,
					FilenameUtils.concat(languagename, "Syntactic Analysis"));
			File synDir = new File(synName);
			if (synDir.exists())
				prjDirSyn = synName;
			else
				prjDirSyn = FilenameUtils.concat(Paths.projectDir, languagename);

			tempDir = new File(prjDirSyn);
			for (File file : tempDir.listFiles())
			{
				String fullName = file.getName();
				if (!file.exists())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.PROJECT_RESOURCE_FILE + fullName
							+ Constants.PROJECT_DOES_NOT_EXIST, Constants.PROJECT_FILE_INVALID,
							JOptionPane.INFORMATION_MESSAGE);
					continue;
				}

				
				String fName = fullName;
				String pref = fName.substring(0, 2);
				String fName2 = fName.substring(2);
				String ext = FilenameUtils.getExtension(fullName);

				if (ext.equalsIgnoreCase(Constants.JNOG_EXTENSION))
				{
					resModel.addRow(new Object[] { pref, fName2 });
					listModel.addElement(fName2);
				}
			}
		}
	}

	public void GetAllResourcesFromPref(String languagename)
	{
		// lexical analysis
		ArrayList<String> f = Launcher.preferences.ldic.get(languagename);
		if (f != null)
		{
			for (int i = 0; i < f.size(); i++)
			{
				String prefname = f.get(i);
				String ext = FilenameUtils.getExtension(prefname);
				if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
					CheckPrefResourceInLvLex(prefname, dialog.getTableDictionary());
				else if (ext.equalsIgnoreCase(Constants.JNOM_EXTENSION))
					CheckPrefResourceInLvLex(prefname, dialog.getTableMorphology());
			}
		}

		// syntactic analysis
		f = Launcher.preferences.lsyn.get(languagename);
		if (f != null)
		{
			for (int i = 0; i < f.size(); i++)
			{
				String prefName = f.get(i);
				String ext = FilenameUtils.getExtension(prefName);

				if (ext.equalsIgnoreCase(Constants.JNOG_EXTENSION))
				{
					String pref = prefName.substring(0, 2);
					String fName = prefName.substring(2);

					int prio = 0;
					prio = Integer.parseInt(pref);

					if (prio != 0)
						CheckPrefResourceInLvSyn(fName, prio, lvSGrm);
				}
			}
		}
	}

	private void CheckPrefResourceInLvLex(String prefname, JTable lv)
	{
		String fName = prefname;

		// compute priority
		String prio = "";

		if (lv == this.lvSGrm) // syntactic parsing: priority from 00 to 99
		{
			prio = fName.substring(0, 2);

			if (Character.isDigit(prio.charAt(0)))
			{
				if (Character.isDigit(prio.charAt(1)))
				{
					fName = fName.substring(1);
				}
				else
				{
					prio = prio.substring(0, 1);
					fName = fName.substring(1);
				}
			}

			if (prio.equals("0") || prio.equals("00"))
				prio = "";
		}
		else
		{
			// lexical parsing: priority from -9 (high) to 09 (low)
			int priority;
			String prefx = fName.substring(0, 2);
			try
			{
				priority = Integer.parseInt(prefx);
			}
			catch (Exception e)
			{
				priority = 0;
			}

			if (priority < 0)
				prio = "H" + String.valueOf((-priority));
			else if (priority > 0)
				prio = "L" + String.valueOf(priority);
			else
				prio = "";
			fName = fName.substring(2);
		}

		// now find and check file
		DefaultTableModel model = (DefaultTableModel) lv.getModel();
		Vector<?> dataVector = model.getDataVector();
		Object[] data = dataVector.toArray();

		for (int i = 0; i < data.length; i++)
		{
			Vector<?> file = (Vector<?>) data[i];
			CustomCell value = (CustomCell) file.get(0);
			String fname2 = value.label.getText();

			if (fname2.equalsIgnoreCase(fName))
			{
				value.checkBox.setSelected(true);
				model.setValueAt(value, i, 0);
				model.setValueAt(prio, i, 1);
				return;
			}
		}
	}

	private void CheckPrefResourceInLvSyn(String fname, int priority, JTable lvSGrm)
	{
		// add file and set its priority
		DefaultTableModel model = (DefaultTableModel) lvSGrm.getModel();
		model.addRow(new Object[] { priority, fname });
	}

	class CellEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
	{
		private static final long serialVersionUID = -7918950819212313412L;

		private CustomCell renderer = new CustomCell();
		private CustomCell editor = new CustomCell();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
		{
			renderer = (CustomCell) value;
			if (isSelected)
				renderer.setBackground(table.getSelectionBackground());
			else
				renderer.setBackground(table.getBackground());
			return renderer;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			editor = (CustomCell) value;
			editor.setBackground(table.getSelectionBackground());
			return editor;
		}

		@Override
		public Object getCellEditorValue()
		{
			return editor;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent)
		{
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent)
		{
			return true;
		}
	}
}