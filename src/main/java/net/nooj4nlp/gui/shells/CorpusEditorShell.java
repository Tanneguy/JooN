package net.nooj4nlp.gui.shells;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.AddActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.CorpusEditorShell.ComputeMouseAdapter;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.CorpusEditorShell.ExportColoredToHtmlActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.LinguisticAnalysisActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.RemoveActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.RightClickPopupMenuForCorpus;
import net.nooj4nlp.controller.CorpusEditorShell.TableSorterActionListener;
import net.nooj4nlp.controller.LocateDialog.LocateDialogTextActionListener;
import net.nooj4nlp.controller.TextEditorShell.OpenTextFromCorpusActionListener;
import net.nooj4nlp.gui.actions.shells.control.CorpusCommandInternalFrameListener;
import net.nooj4nlp.gui.dialogs.ExportXmlDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AlphabetDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AmbiguitiesUnambiguitiesDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;
import net.nooj4nlp.gui.main.Launcher;

public class CorpusEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = 8330102707204048318L;

	private JTextPane textPaneStats;
	private JTable tableTexts;
	private JList listResults;
	private JButton btnRemove;
	private JButton btnAdd;

	private AlphabetDialog alphabetDialog;
	private TokensDigramsDialog tokensDialog;
	private TokensDigramsDialog digramsDialog;
	private AmbiguitiesUnambiguitiesDialog ambiguitiesDialog;
	private AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog;

	private TextEditorShell textEditorShell;
	private ExportXmlDialog exportXmlDialog;
	private CorpusEditorShellController controller;
	private TableSorterActionListener tableSorterActionListener;

	private JMenu mnCorpus;

	public CorpusEditorShell()
	{
		alphabetDialog = null;
		tokensDialog = null;
		digramsDialog = null;
		ambiguitiesDialog = null;
		unAmbiguitiesDialog = null;
		exportXmlDialog = null;

		textPaneStats = new JTextPane();

		// Change JList with JTable
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "FileName", "Size", "Modified", "NText" },
				0);
		tableTexts = new JTable(tableModel)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		tableTexts.setUI(new BasicTableUI()
		{
			// Create the mouse listener for the JTable.
			protected MouseInputListener createMouseInputListener()
			{
				return new MouseInputHandler()
				{
					// Display frame on double-click
					public void mouseClicked(MouseEvent e)
					{
						if (e.getClickCount() > 1)
						{
						}
					}
				};
			}
		});

		// Removing Ntext column - it is not supposed to be seen!
	

		DefaultListModel model2 = new DefaultListModel();
		listResults = new JList(model2);

		controller = new CorpusEditorShellController(this, this.textPaneStats, this.tableTexts, this.listResults);
		controller.updateTitle();
		controller.reactivateOps();

		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 600, 300);
		getContentPane().setLayout(new MigLayout("insets 5", "[300!,grow][100::,grow]", "[100!, grow][grow][]"));

		JScrollPane scrollPane1 = new JScrollPane(textPaneStats);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane1, "cell 0 0");

		JScrollPane scrollPane2 = new JScrollPane(listResults);
		getContentPane().add(scrollPane2, "cell 1 0");

		JScrollPane scrollPane3 = new JScrollPane(tableTexts);
		scrollPane3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane3, "cell 0 1, span 2, grow");

		btnRemove = new JButton("Remove");
		getContentPane().add(btnRemove, "cell 1 2, split 2");

		btnAdd = new JButton("Add");
		getContentPane().add(btnAdd);

		// Adding listeners
		btnRemove.addActionListener(new RemoveActionListener(controller));
		btnAdd.addActionListener(new AddActionListener(controller, this));
		listResults.addMouseListener(new ComputeMouseAdapter(controller, listResults));
		tableTexts.addMouseListener(new OpenTextFromCorpusActionListener(controller, tableTexts));

		MouseListener rightClickListener = new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					RightClickPopupMenuForCorpus menu = new RightClickPopupMenuForCorpus(controller);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};

		addMouseListener(rightClickListener);
		tableTexts.addMouseListener(rightClickListener);
		listResults.addMouseListener(rightClickListener);
		textPaneStats.addMouseListener(rightClickListener);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setSortable(0, false);
		sorter.setSortable(1, false);
		sorter.setSortable(2, false);

		tableTexts.setRowSorter(sorter);

		this.addInternalFrameListener(new CloseInternalFrameListener(controller, this));
		this.addInternalFrameListener(new CorpusCommandInternalFrameListener(this.controller));

		controller.updateTitle();
		controller.resetLv();
		controller.updateTextPaneStats();
		controller.updateResults();

		// overridden column click event
		tableSorterActionListener = new TableSorterActionListener();
		tableTexts.getTableHeader().addMouseListener(tableSorterActionListener);
	}

	public CorpusEditorShell(final CorpusEditorShellController c)
	{
		alphabetDialog = null;
		tokensDialog = null;
		digramsDialog = null;
		ambiguitiesDialog = null;
		unAmbiguitiesDialog = null;
		exportXmlDialog = null;

		textPaneStats = new JTextPane();

		// Change JList with JTable
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "FileName", "Size", "Modified", "NText" },
				0);
		tableTexts = new JTable(tableModel)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		tableTexts.setUI(new BasicTableUI()
		{
			// Create the mouse listener for the JTable.
			protected MouseInputListener createMouseInputListener()
			{
				return new MouseInputHandler()
				{
					// Display frame on double-click
					public void mouseClicked(MouseEvent e)
					{
						if (e.getClickCount() > 1)
						{
						}
					}
				};
			}
		});

		// Removing Ntext column - it is not supposed to be seen!
		

		DefaultListModel model2 = new DefaultListModel();
		listResults = new JList(model2);

	
		controller = c;
		controller.setShell(this);
		controller.setTextPaneStats(textPaneStats);
		controller.setTableTexts(tableTexts);
		controller.setListResults(listResults);

		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 600, 300);
		getContentPane().setLayout(new MigLayout("insets 5", "[300!,grow][100::,grow]", "[100!, grow][grow][]"));

		JScrollPane scrollPane1 = new JScrollPane(textPaneStats);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane1, "cell 0 0");

		JScrollPane scrollPane2 = new JScrollPane(listResults);
		getContentPane().add(scrollPane2, "cell 1 0, grow");

		JScrollPane scrollPane3 = new JScrollPane(tableTexts);
		scrollPane3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane3, "cell 0 1, span 2, grow");

		btnRemove = new JButton("Remove");
		getContentPane().add(btnRemove, "cell 1 2, split 2, grow");

		btnAdd = new JButton("Add");
		getContentPane().add(btnAdd, "cell 1 2, grow");

		// Adding listeners
		btnRemove.addActionListener(new RemoveActionListener(controller));
		btnAdd.addActionListener(new AddActionListener(controller, this));
		listResults.addMouseListener(new ComputeMouseAdapter(controller, listResults));
		tableTexts.addMouseListener(new OpenTextFromCorpusActionListener(controller, tableTexts));

		MouseListener rightClickListener = new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					RightClickPopupMenuForCorpus menu = new RightClickPopupMenuForCorpus(controller);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};

		addMouseListener(rightClickListener);
		tableTexts.addMouseListener(rightClickListener);
		listResults.addMouseListener(rightClickListener);
		textPaneStats.addMouseListener(rightClickListener);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setSortable(0, false);
		sorter.setSortable(1, false);
		sorter.setSortable(2, false);

		tableTexts.setRowSorter(sorter);

		this.addInternalFrameListener(new CloseInternalFrameListener(controller, this));
		this.addInternalFrameListener(new CorpusCommandInternalFrameListener(controller));

		controller.updateTitle();
		controller.resetLv();
		controller.updateTextPaneStats();
		controller.updateResults();

		tableSorterActionListener = new TableSorterActionListener();
		// overridden column click event
		tableTexts.getTableHeader().addMouseListener(tableSorterActionListener);

		mnCorpus = createCorpusMenu();
	}

	private JMenu createCorpusMenu()
	{
		Launcher.mnEdit.setVisible(true);
		JMenuItem mntmLinguisticAnalysis;
		JMenuItem mntmLocate;
		JMenuItem mntmExportAnnotatedTexts;
		JMenuItem mntmExportColoredTexts;
		JMenu mnCorpus;

		mnCorpus = new JMenu("CORPUS");

		mntmLinguisticAnalysis = new JMenuItem("Linguistic analysis");
		mnCorpus.add(mntmLinguisticAnalysis);
		mntmLinguisticAnalysis.addActionListener(new LinguisticAnalysisActionListener(controller));

		mntmLocate = new JMenuItem("Locate");
		mnCorpus.add(mntmLocate);
		mntmLocate.addActionListener(new LocateDialogTextActionListener(null, controller));

		JSeparator separator_12 = new JSeparator();
		mnCorpus.add(separator_12);

		mntmExportColoredTexts = new JMenuItem("Export colored texts as HTML documents");
		mnCorpus.add(mntmExportColoredTexts);
		mntmExportColoredTexts.setEnabled(controller.isColored());
		mntmExportColoredTexts.addActionListener(new ExportColoredToHtmlActionListener(controller, null));

		mntmExportAnnotatedTexts = new JMenuItem("Export annotated texts as XML documents");
		mnCorpus.add(mntmExportAnnotatedTexts);
		mntmExportAnnotatedTexts.addActionListener(new ExportXmlActionListener(controller));

		return mnCorpus;
	}

	public CorpusEditorShellController getController()
	{
		return controller;
	}

	public AlphabetDialog getAlphabetDialog()
	{
		return alphabetDialog;
	}

	public void setAlphabetDialog(AlphabetDialog alphabetDialog)
	{
		this.alphabetDialog = alphabetDialog;
	}

	public TokensDigramsDialog getTokensDialog()
	{
		return tokensDialog;
	}

	public void setTokensDialog(TokensDigramsDialog tokensDialog)
	{
		this.tokensDialog = tokensDialog;
	}

	public TokensDigramsDialog getDigramsDialog()
	{
		return digramsDialog;
	}

	public void setDigramsDialog(TokensDigramsDialog digramsDialog)
	{
		this.digramsDialog = digramsDialog;
	}

	public AmbiguitiesUnambiguitiesDialog getAmbiguitiesDialog()
	{
		return ambiguitiesDialog;
	}

	public void setAmbiguitiesDialog(AmbiguitiesUnambiguitiesDialog ambiguitiesDialog)
	{
		this.ambiguitiesDialog = ambiguitiesDialog;
	}

	public AmbiguitiesUnambiguitiesDialog getUnAmbiguitiesDialog()
	{
		return unAmbiguitiesDialog;
	}

	public void setUnAmbiguitiesDialog(AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog)
	{
		this.unAmbiguitiesDialog = unAmbiguitiesDialog;
	}

	public ExportXmlDialog getExportXmlDialog()
	{
		return exportXmlDialog;
	}

	public void setExportXmlDialog(ExportXmlDialog exportXmlDialog)
	{
		this.exportXmlDialog = exportXmlDialog;
	}

	public TextEditorShell getTextEditorShell()
	{
		return textEditorShell;
	}

	public void setTextEditorShell(TextEditorShell textEditorShell)
	{
		this.textEditorShell = textEditorShell;
	}

	public JTable getTableTexts()
	{
		return tableTexts;
	}

	public JButton getBtnRemove()
	{
		return btnRemove;
	}

	public JButton getBtnAdd()
	{
		return btnAdd;
	}

	public TableSorterActionListener getTableSorterActionListener()
	{
		return tableSorterActionListener;
	}

	public void setTableSorterActionListener(TableSorterActionListener tableSorterActionListener)
	{
		this.tableSorterActionListener = tableSorterActionListener;
	}

	public JMenu getMnCorpus()
	{
		return mnCorpus;
	}
}