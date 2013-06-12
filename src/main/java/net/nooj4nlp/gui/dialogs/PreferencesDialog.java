package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.nooj4nlp.controller.preferencesdialog.AddGrmActionListener;
import net.nooj4nlp.controller.preferencesdialog.ApplyActionListener;
import net.nooj4nlp.controller.preferencesdialog.AssociateFileExtensionActionListener;
import net.nooj4nlp.controller.preferencesdialog.CancelActionListener;
import net.nooj4nlp.controller.preferencesdialog.CheckAllActionListener;
import net.nooj4nlp.controller.preferencesdialog.CopyToPreferences;
import net.nooj4nlp.controller.preferencesdialog.DefaultActionListener;
import net.nooj4nlp.controller.preferencesdialog.DeleteLexActionListener;
import net.nooj4nlp.controller.preferencesdialog.DictionariesActionListener;
import net.nooj4nlp.controller.preferencesdialog.EditLexActionListener;
import net.nooj4nlp.controller.preferencesdialog.ImportLexActionListener;
import net.nooj4nlp.controller.preferencesdialog.LoadActionListener;
import net.nooj4nlp.controller.preferencesdialog.PreferencesIntegerComparator;
import net.nooj4nlp.controller.preferencesdialog.PriorityLexActionListener;
import net.nooj4nlp.controller.preferencesdialog.PrioritySynActionListener;
import net.nooj4nlp.controller.preferencesdialog.RefreshActionListener;
import net.nooj4nlp.controller.preferencesdialog.SaveActionListener;
import net.nooj4nlp.controller.preferencesdialog.SaveAsDefaultActionListener;
import net.nooj4nlp.controller.preferencesdialog.SelectDefLanguageActionListener;
import net.nooj4nlp.controller.preferencesdialog.TextsActionListener;
import net.nooj4nlp.controller.preferencesdialog.UpdateDialogListener;
import net.nooj4nlp.controller.preferencesdialog.UpdateTablesListener;
import net.nooj4nlp.gui.components.CustomCell;
import net.nooj4nlp.gui.components.NooJTableSorter;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * NooJ preferences dialog
 * 
 */
public class PreferencesDialog extends JDialog
{
	private static final long serialVersionUID = -1594043405334641461L;

	private String oldLan;

	private JComboBox cbDefLanguage;
	private Font DFont = null;
	private Font TFont = null;
	private JTable tableMorphology;
	private JTable tableDictionary;
	private JTable tableResources;
	private JList listSynResources;
	private JCheckBox chckbxNoojManagesMultiple;
	private JButton btnImportFileLex;
	private JButton btnImportFileSyn;

	private JButton btnDeleteFileLex;
	private JButton btnDeleteFileSyn;
	private JButton btnHigh;
	private JButton btnLow;
	private JButton btnEditLex;
	private JLabel lblLexDoc;
	private JLabel lblSynDoc;
	private JTextArea txtFileInfoLex;
	private JTextArea txtFileInfoSyn;

	private UpdateDialogListener updateDialogListener;
	private SelectDefLanguageActionListener selectDefLanguageListener;

	/**
	 * Creates the dialog.
	 */
	public PreferencesDialog()
	{
		oldLan = Launcher.preferences.deflanguage;
		

		// enable / disable functionalities in regular / project mode
		boolean enable = !Launcher.projectMode;

		if (enable)
			setTitle("Preferences");
		else
			setTitle("Preferences are frozen in project mode");
		setBounds(100, 100, 550, 581);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(10, 465, 535, 102);
		getContentPane().add(panel);

		JButton btnApply = new JButton("Apply");
		btnApply.setBounds(406, 11, 89, 23);
		panel.add(btnApply);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(406, 45, 89, 23);
		panel.add(btnCancel);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(293, 11, 89, 23);
		btnRefresh.setEnabled(enable);
		panel.add(btnRefresh);

		JButton btnSaveAsDefault = new JButton("Save As Default");
		btnSaveAsDefault.setBounds(0, 11, 136, 23);
		btnSaveAsDefault.setEnabled(enable);
		panel.add(btnSaveAsDefault);

		JButton btnSave = new JButton("Save");
		btnSave.setBounds(146, 11, 89, 23);
		btnSave.setEnabled(enable);
		panel.add(btnSave);

		JButton btnReset = new JButton("Reset to Default");
		btnReset.setBounds(0, 45, 136, 23);
		btnReset.setEnabled(enable);
		panel.add(btnReset);

		JButton btnLoad = new JButton("Load");
		btnLoad.setEnabled(enable);
		btnLoad.setBounds(146, 45, 89, 23);
		panel.add(btnLoad);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 525, 459);
		getContentPane().add(tabbedPane);

		JPanel pnlGeneral = new JPanel();
		tabbedPane.addTab("General", null, pnlGeneral, null);
		pnlGeneral.setLayout(null);

		JLabel lblIsoLanguage = new JLabel("ISO 629-1 Language Name:");
		lblIsoLanguage.setBounds(10, 11, 182, 14);
		pnlGeneral.add(lblIsoLanguage);

		cbDefLanguage = new JComboBox();
		cbDefLanguage.setBounds(222, 8, 96, 20);
		pnlGeneral.add(cbDefLanguage);

		JLabel lblLanguagecountry = new JLabel("Language (Country)");
		lblLanguagecountry.setBounds(10, 40, 154, 14);
		pnlGeneral.add(lblLanguagecountry);

		JLabel lblLanguage = new JLabel("Language");
		lblLanguage.setBounds(10, 65, 154, 14);
		pnlGeneral.add(lblLanguage);

		JLabel lblCharacterVariation = new JLabel("Character variation");
		lblCharacterVariation.setBounds(10, 90, 154, 14);
		pnlGeneral.add(lblCharacterVariation);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Fonts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(10, 136, 304, 95);
		pnlGeneral.add(panel_1);
		panel_1.setLayout(null);

		JButton btnDictionaries = new JButton("Dictionaries");
		btnDictionaries.setBounds(10, 56, 109, 23);
		btnDictionaries.setEnabled(enable);
		panel_1.add(btnDictionaries);

		JButton btnTexts = new JButton("Texts");
		btnTexts.setBounds(10, 24, 109, 23);
		btnTexts.setEnabled(enable);
		panel_1.add(btnTexts);

		JLabel lblTextFont = new JLabel("Text font");
		lblTextFont.setBounds(129, 28, 165, 19);
		panel_1.add(lblTextFont);

		JLabel lblDictionaryFont = new JLabel("Dictionary font");
		lblDictionaryFont.setBounds(129, 60, 165, 19);
		panel_1.add(lblDictionaryFont);

		chckbxNoojManagesMultiple = new JCheckBox("NooJ manages multiple backups");
		chckbxNoojManagesMultiple.setBounds(10, 244, 263, 20);
		chckbxNoojManagesMultiple.setEnabled(enable);
		pnlGeneral.add(chckbxNoojManagesMultiple);

		JButton btnAssociateFileExtensions = new JButton("Associate File Extensions");
		btnAssociateFileExtensions.setBounds(10, 292, 189, 23);
		pnlGeneral.add(btnAssociateFileExtensions);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.append("Associate files of type extensions: .jnoc .jnof .nog .jnom .jnot with NooJ."
				+ "\nThis allows NooJ to be launched when any of these files is double-clicked."
				+ "\nYou need to run NooJ as Administrator (Right-Click on NooJ.exe, then select \"Run As Administrator\")");
		textArea.setBackground(SystemColor.control);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(209, 292, 291, 89);
		pnlGeneral.add(scrollPane);

		JPanel pnlLexAnalysis = new JPanel();
		tabbedPane.addTab("Lexical Analysis", null, pnlLexAnalysis, null);
		pnlLexAnalysis.setLayout(null);

		JLabel lblLexicalResources = new JLabel("Lexical Resources for: <lang>");
		lblLexicalResources.setBounds(10, 11, 159, 14);
		pnlLexAnalysis.add(lblLexicalResources);

		JLabel lblPriorityLevel = new JLabel("Priority Level:");
		lblPriorityLevel.setBounds(190, 11, 89, 14);
		pnlLexAnalysis.add(lblPriorityLevel);

		btnHigh = new JButton("High");
		btnHigh.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnHigh.setForeground(Color.RED);
		btnHigh.setBounds(281, 7, 59, 23);
		pnlLexAnalysis.add(btnHigh);

		JButton btnRegular = new JButton("Regular");
		btnRegular.setForeground(Color.RED);
		btnRegular.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRegular.setBounds(350, 7, 81, 23);
		pnlLexAnalysis.add(btnRegular);

		btnLow = new JButton("Low");
		btnLow.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnLow.setForeground(Color.RED);
		btnLow.setBounds(441, 7, 59, 23);
		pnlLexAnalysis.add(btnLow);

		String[] columnNames = { "Dictionary", "Priority" };
		DefaultTableModel dicModel = new DefaultTableModel(null, columnNames);
		tableDictionary = new JTable(dicModel);
		tableDictionary.setName("DictionaryTable");
		tableDictionary.setRowHeight(22);

		JScrollPane scrollDictionary = new JScrollPane(tableDictionary);
		scrollDictionary.setBounds(10, 36, 226, 171);
		pnlLexAnalysis.add(scrollDictionary);

		// Center priorities
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < tableDictionary.getColumnCount(); i++)
		{
			tableDictionary.setDefaultRenderer(tableDictionary.getColumnClass(i), renderer);
		}

		String[] columnNames1 = { "Morphology", "Priority" };
		DefaultTableModel morModel = new DefaultTableModel(null, columnNames1);
		tableMorphology = new JTable(morModel);
		tableMorphology.setName("MorphologyTable");
		tableMorphology.setRowHeight(22);

		JScrollPane scrollMorphology = new JScrollPane(tableMorphology);
		scrollMorphology.setBounds(246, 36, 234, 171);
		pnlLexAnalysis.add(scrollMorphology);

		// sorting tables
		tableDictionary.setAutoCreateRowSorter(false);
		tableMorphology.setAutoCreateRowSorter(false);
		tableDictionary.getColumnModel().getColumn(1).setMaxWidth(50);
		tableMorphology.getColumnModel().getColumn(1).setMaxWidth(50);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableDictionary.getModel());
		sorter.setSortable(0, false);
		sorter.setSortable(1, false);
		tableDictionary.setRowSorter(sorter);

		TableRowSorter<TableModel> sorter2 = new TableRowSorter<TableModel>(tableMorphology.getModel());
		sorter2.setSortable(0, false);
		sorter2.setSortable(1, false);
		tableMorphology.setRowSorter(sorter2);

		PreferenceTableSorter dicSorter = new PreferenceTableSorter(tableDictionary);
		PreferenceTableSorter morphSorter = new PreferenceTableSorter(tableMorphology);
		tableDictionary.getTableHeader().addMouseListener(dicSorter);
		tableMorphology.getTableHeader().addMouseListener(morphSorter);
		tableDictionary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Center priorities
		for (int i = 0; i < tableMorphology.getColumnCount(); i++)
		{
			// table1.setDefaultRenderer(table.getColumnModel().getColumn(i), renderer);
		}

		tableDictionary.setFillsViewportHeight(true);
		tableMorphology.setFillsViewportHeight(true);

		JButton btnUncheckAllDic = new JButton("Uncheck All");
		btnUncheckAllDic.setBounds(10, 213, 109, 23);
		pnlLexAnalysis.add(btnUncheckAllDic);

		JButton btnCheckAllDic = new JButton("Check All");
		btnCheckAllDic.setBounds(147, 213, 89, 23);
		pnlLexAnalysis.add(btnCheckAllDic);

		JButton btnUncheckAllMorph = new JButton("Uncheck All");
		btnUncheckAllMorph.setBounds(246, 213, 109, 23);
		pnlLexAnalysis.add(btnUncheckAllMorph);

		JButton btnCheckAllMorph = new JButton("CheckAll");
		btnCheckAllMorph.setBounds(391, 213, 89, 23);
		pnlLexAnalysis.add(btnCheckAllMorph);

		lblLexDoc = new JLabel("Selected file");
		lblLexDoc.setBounds(10, 240, 470, 20);
		pnlLexAnalysis.add(lblLexDoc);

		txtFileInfoLex = new JTextArea();
		txtFileInfoLex.setBounds(10, 263, 470, 96);
		txtFileInfoLex.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlLexAnalysis.add(txtFileInfoLex);

		btnEditLex = new JButton("Edit");
		btnEditLex.setBounds(411, 385, 89, 23);
		pnlLexAnalysis.add(btnEditLex);

		btnImportFileLex = new JButton("Import File");
		btnImportFileLex.setBounds(302, 385, 99, 23);
		pnlLexAnalysis.add(btnImportFileLex);

		btnDeleteFileLex = new JButton("Delete File");
		btnDeleteFileLex.setBounds(193, 385, 99, 23);
		pnlLexAnalysis.add(btnDeleteFileLex);

		JPanel pnlSynAnalysis = new JPanel();
		tabbedPane.addTab("Syntactic Analysis", null, pnlSynAnalysis, null);
		pnlSynAnalysis.setLayout(null);

		JLabel lblAvailableSyntacticResource = new JLabel("Available Syntactic Resources for: <lang>");
		lblAvailableSyntacticResource.setBounds(10, 11, 236, 14);
		pnlSynAnalysis.add(lblAvailableSyntacticResource);

		JLabel lblApplyFollowingResources = new JLabel("Apply Following Resources:");
		lblApplyFollowingResources.setBounds(245, 11, 197, 14);
		pnlSynAnalysis.add(lblApplyFollowingResources);

		txtFileInfoSyn = new JTextArea();
		txtFileInfoSyn.setBounds(10, 263, 470, 96);
		txtFileInfoSyn.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlSynAnalysis.add(txtFileInfoSyn);

		btnDeleteFileSyn = new JButton("Delete File");
		btnDeleteFileSyn.setBounds(193, 385, 99, 23);
		pnlSynAnalysis.add(btnDeleteFileSyn);

		btnImportFileSyn = new JButton("Import File");
		btnImportFileSyn.setBounds(302, 385, 99, 23);
		pnlSynAnalysis.add(btnImportFileSyn);

		JButton btnEditSyn = new JButton("Edit");
		btnEditSyn.setBounds(411, 385, 89, 23);
		pnlSynAnalysis.add(btnEditSyn);

		DefaultListModel listModel = new DefaultListModel();
		listSynResources = new JList(listModel);

		JScrollPane scrollSynResources = new JScrollPane(listSynResources);
		scrollSynResources.setBounds(10, 37, 185, 181);
		pnlSynAnalysis.add(scrollSynResources);

		String[] columnNamesResources = { "Order", "Grammar" };
		DefaultTableModel resModel = new DefaultTableModel(null, columnNamesResources);
		tableResources = new JTable(resModel);
		tableResources.setName("ResourcesTable");

		tableResources.setAutoCreateRowSorter(false);
		tableResources.setFillsViewportHeight(true);
		tableResources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollResources = new JScrollPane(tableResources);
		scrollResources.setBounds(243, 37, 194, 181);
		pnlSynAnalysis.add(scrollResources);

		JButton buttonTransfer = new JButton(">");
		buttonTransfer.setFont(new Font("Tahoma", Font.BOLD, 11));
		buttonTransfer.setForeground(Color.RED);
		buttonTransfer.setBounds(199, 37, 41, 34);
		pnlSynAnalysis.add(buttonTransfer);

		JButton btnH = new JButton("H");
		btnH.setForeground(Color.RED);
		btnH.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnH.setBounds(442, 36, 40, 34);
		pnlSynAnalysis.add(btnH);

		JButton btnL = new JButton("L");
		btnL.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnL.setForeground(Color.RED);
		btnL.setBounds(442, 81, 40, 34);
		pnlSynAnalysis.add(btnL);

		JButton btnX = new JButton("X");
		btnX.setForeground(Color.RED);
		btnX.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnX.setBounds(442, 184, 40, 34);
		pnlSynAnalysis.add(btnX);

		lblSynDoc = new JLabel("File location");
		lblSynDoc.setBounds(10, 229, 389, 14);
		pnlSynAnalysis.add(lblSynDoc);

		// Adding listeners
		CopyToPreferences copyToPreferences = new CopyToPreferences(this);

		updateDialogListener = new UpdateDialogListener(this, cbDefLanguage, TFont, DFont, lblTextFont,
				lblDictionaryFont, chckbxNoojManagesMultiple);
		// Texts button
		btnTexts.addActionListener(new TextsActionListener(this, lblTextFont));
		// Dictionaries
		btnDictionaries.addActionListener(new DictionariesActionListener(this, lblDictionaryFont));
		// Associate file extension
		btnAssociateFileExtensions.addActionListener(new AssociateFileExtensionActionListener());
		// Reset to default
		btnReset.addActionListener(new DefaultActionListener(updateDialogListener));

		// Load
		btnLoad.addActionListener(new LoadActionListener(this, updateDialogListener));
		// Refresh
		btnRefresh.addActionListener(new RefreshActionListener(this, updateDialogListener));

		// Cancel
		btnCancel.addActionListener(new CancelActionListener(this));
		// Import lexeme
		btnImportFileLex.addActionListener(new ImportLexActionListener(this, updateDialogListener));
		btnImportFileSyn.addActionListener(new ImportLexActionListener(this, updateDialogListener));
		// Table Update listener
		UpdateTablesListener tableListener = new UpdateTablesListener(tableDictionary, tableMorphology,
				listSynResources, tableResources, this);
		// Select language
		selectDefLanguageListener = new SelectDefLanguageActionListener(cbDefLanguage, lblLexicalResources,
				lblAvailableSyntacticResource, lblLanguagecountry, lblLanguage, lblCharacterVariation, tableListener);
		cbDefLanguage.addActionListener(selectDefLanguageListener);
		// Check all Dic
		btnCheckAllDic.addActionListener(new CheckAllActionListener(this, 0, true));
		// Uncheck all Dic
		btnUncheckAllDic.addActionListener(new CheckAllActionListener(this, 0, false));
		// Check all morphology
		btnCheckAllMorph.addActionListener(new CheckAllActionListener(this, 1, true));
		// Uncheck all mor
		btnUncheckAllMorph.addActionListener(new CheckAllActionListener(this, 1, false));

		// High priority button
		btnHigh.addActionListener(new PriorityLexActionListener(1, lblLexDoc, this));
		// Low priority button
		btnLow.addActionListener(new PriorityLexActionListener(-1, lblLexDoc, this));
		// Regular priority button
		btnRegular.addActionListener(new PriorityLexActionListener(0, lblLexDoc, this));

		// Button transfer
		buttonTransfer.addActionListener(new AddGrmActionListener(this));
		// Button High priority syn
		btnH.addActionListener(new PrioritySynActionListener(-1, this));
		// Button Low priority syn
		btnL.addActionListener(new PrioritySynActionListener(1, this));
		// Button X priority syn
		btnX.addActionListener(new PrioritySynActionListener(0, this));

		btnDeleteFileLex.addActionListener(new DeleteLexActionListener(this, lblLexDoc, lblSynDoc, btnDeleteFileLex,
				btnDeleteFileSyn));

		btnDeleteFileSyn.addActionListener(new DeleteLexActionListener(this, lblLexDoc, lblSynDoc, btnDeleteFileLex,
				btnDeleteFileSyn));

		btnEditLex.addActionListener(new EditLexActionListener(this, lblLexDoc, lblSynDoc, true));

		btnEditSyn.addActionListener(new EditLexActionListener(this, lblLexDoc, lblSynDoc, false));

		updateDialogListener.updateFromFormMainPreferences();

		// Apply
		btnApply.addActionListener(new ApplyActionListener(this, copyToPreferences));
		// Save
		btnSave.addActionListener(new SaveActionListener(this, copyToPreferences));
		// Save as default
		btnSaveAsDefault.addActionListener(new SaveAsDefaultActionListener(copyToPreferences));

		// Listeners for header actions
		tableResources.getTableHeader().addMouseListener(new PreferenceResourcesTableSorter(tableResources));

	

		// Update preference
		copyToPreferences.CopyToPref();
		if (oldLan != Launcher.preferences.deflanguage)
			Launcher.setOpenDirectories();
	}

	public JComboBox getCbDefLanguage()
	{
		return cbDefLanguage;
	}

	public void setCbDefLanguage(JComboBox cbDefLanguage)
	{
		this.cbDefLanguage = cbDefLanguage;
	}

	public JCheckBox getChckbxNoojManagesMultiple()
	{
		return chckbxNoojManagesMultiple;
	}

	public void setChckbxNoojManagesMultiple(JCheckBox chckbxNoojManagesMultiple)
	{
		this.chckbxNoojManagesMultiple = chckbxNoojManagesMultiple;
	}

	public Font getDFont()
	{
		return DFont;
	}

	public void setDFont(Font dFont)
	{
		DFont = dFont;
	}

	public Font getTFont()
	{
		return TFont;
	}

	public void setTFont(Font tFont)
	{
		TFont = tFont;
	}

	public JTable getTableMorphology()
	{
		return tableMorphology;
	}

	public void setTableMorphology(JTable tableMorphology)
	{
		this.tableMorphology = tableMorphology;
	}

	public JTable getTableDictionary()
	{
		return tableDictionary;
	}

	public void setTableDictionary(JTable tableDictionary)
	{
		this.tableDictionary = tableDictionary;
	}

	public JTable getTableResources()
	{
		return tableResources;
	}

	public void setTableResources(JTable tableResources)
	{
		this.tableResources = tableResources;
	}

	public JList getListSynResources()
	{
		return listSynResources;
	}

	public void setListSynResources(JList listSynResources)
	{
		this.listSynResources = listSynResources;
	}

	public JButton getBtnDeleteFileLex()
	{
		return btnDeleteFileLex;
	}

	public JButton getBtnHigh()
	{
		return btnHigh;
	}

	public JButton getBtnLow()
	{
		return btnLow;
	}

	public JButton getBtnEditLex()
	{
		return btnEditLex;
	}

	public JLabel getLblLexDoc()
	{
		return lblLexDoc;
	}

	public JLabel getLblSynDoc()
	{
		return lblSynDoc;
	}

	public JTextArea getTxtFileInfoLex()
	{
		return txtFileInfoLex;
	}

	public void setTxtFileInfoLex(JTextArea txtFileInfoLex)
	{
		this.txtFileInfoLex = txtFileInfoLex;
	}

	public UpdateDialogListener getUpdateDialogListener()
	{
		return updateDialogListener;
	}

	public void setUpdateDialogListener(UpdateDialogListener updateDialogListener)
	{
		this.updateDialogListener = updateDialogListener;
	}

	public JButton getBtnImportFileLex()
	{
		return btnImportFileLex;
	}

	public JButton getBtnImportFileSyn()
	{
		return btnImportFileSyn;
	}

	public JTextArea getTxtFileInfoSyn()
	{
		return txtFileInfoSyn;
	}

	public SelectDefLanguageActionListener getSelectDefLanguageListener()
	{
		return selectDefLanguageListener;
	}

	public String getOldLan()
	{
		return oldLan;
	}
}

class PreferenceResourcesTableSorter extends MouseAdapter
{
	// table to sort
	private JTable table;

	public PreferenceResourcesTableSorter(JTable table)
	{
		super();
		this.table = table;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() % 2 == 1)
		{
			// get clicked column and table model
			JTable table = this.table;
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
			{
				Collections.sort(newDataList, new PreferencesIntegerComparator(0));
			}
			// otherwise, it's a string sorting
			else
				Collections.sort(newDataList, new NooJTableSorter(1, false, false, null));

			// add data list to the model and set the model
			for (int i = 0; i < newDataList.size(); i++)
				tableModel.addRow(newDataList.get(i));

			table.setModel(tableModel);
		}
	}
}

/**
 * Class implements sorting of preferance's tables.
 */
class PreferenceTableSorter extends MouseAdapter
{
	// table to sort
	private JTable table;

	/**
	 * Constructor.
	 * 
	 * @param tableToSort
	 *            - desired table to sort
	 */
	public PreferenceTableSorter(JTable tableToSort)
	{
		super();
		this.table = tableToSort;
	}

	public void mouseClicked(MouseEvent e)
	{
		// get clicked column and table model
		JTable table = this.table;
		TableColumnModel colModel = table.getColumnModel();
		int index = colModel.getColumnIndexAtX(e.getX());

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

		// get data vector and convert it to a list of objects and get help lists
		Vector<?> dataVector = tableModel.getDataVector();
		List<Object> dataList = new ArrayList<Object>(dataVector);
		List<Object[]> newDataList = new ArrayList<Object[]>();
		List<String> listOfCheckedFiles = new ArrayList<String>();
		List<String> listOfFiles = new ArrayList<String>();

		for (int i = 0; i < dataList.size(); i++)
		{
			Object[] tableRow = ((Vector<?>) (dataList.get(i))).toArray();
			if (((CustomCell) tableRow[0]).getCheckBox().isSelected())
				listOfCheckedFiles.add(((CustomCell) tableRow[0]).getLabel().getText());
			newDataList.add(tableRow);
			listOfFiles.add(((CustomCell) tableRow[0]).getLabel().getText());
		}

		// clear old table model
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();

		// if the first column was clicked...
		if (index == 0)
		{
			// sort all file names
			Collections.sort(listOfFiles);

			// set temp list for iteration, and clear the old one
			List<Object[]> tempDataList = new ArrayList<Object[]>(newDataList);
			newDataList.clear();

			for (int i = 0; i < listOfFiles.size(); i++)
			{
				String file = listOfFiles.get(i);
				for (int j = 0; j < tempDataList.size(); j++)
				{
					/*
					 * if name from the sorted list matches the name of the file from data list, get the index, put that
					 * line into newDataList, and break, while removing the index from the temp. This way, checked state
					 * and priority is saved.
					 */
					if (((CustomCell) tempDataList.get(j)[0]).getLabel().getText().equals(file))
					{
						newDataList.add(tempDataList.get(j));
						tempDataList.remove(j);
						break;
					}
				}
			}
		}
		// otherwise, just sort by already made custom sort
		else
			Collections.sort(newDataList, new NooJTableSorter(index, null));

		// add data list to the model and set the model
		for (int i = 0; i < newDataList.size(); i++)
			tableModel.addRow(newDataList.get(i));
	}
}