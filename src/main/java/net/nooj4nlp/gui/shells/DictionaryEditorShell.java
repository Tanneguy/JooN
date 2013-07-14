package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.DictionaryEditorShell.CheckActionListener;
import net.nooj4nlp.controller.DictionaryEditorShell.ColumnHeaderMouseListener;
import net.nooj4nlp.controller.DictionaryEditorShell.CompileActionListener;
import net.nooj4nlp.controller.DictionaryEditorShell.DictionaryEditorShellController;
import net.nooj4nlp.controller.DictionaryEditorShell.EnrichActionListener;
import net.nooj4nlp.controller.DictionaryEditorShell.ExportActionListener;
import net.nooj4nlp.controller.DictionaryEditorShell.SortActionListener;
import net.nooj4nlp.controller.DictionaryEditorShell.TextDocumentListener;
import net.nooj4nlp.controller.DictionaryEditorShell.ViewActionListener;
import net.nooj4nlp.gui.actions.documents.CopyActionListener;
import net.nooj4nlp.gui.actions.documents.CutActionListener;
import net.nooj4nlp.gui.actions.documents.PasteActionListener;
import net.nooj4nlp.gui.actions.documents.SelectAllActionListener;
import net.nooj4nlp.gui.actions.shells.construct.FindReplaceActionListener;
import net.nooj4nlp.gui.actions.shells.control.DictionaryCommandInternalFrameListener;
import net.nooj4nlp.gui.actions.shells.modify.ContextMenuMouseListener;
import net.nooj4nlp.gui.components.TextLineNumber;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Shell for editing dictionaries
 * 
 */
public class DictionaryEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = 6264526005284711686L;

	private JTextPane textPane;
	private JPopupMenu popText;
	private JPanel editorPane;
	private JPanel tablePane;
	private JLabel lblnTus;
	private JTable table;
	private JScrollPane tableContainer;

	private JMenuItem mntmCheckFormat;
	private JMenuItem mntmFind;
	private JMenuItem mntmSort;
	private JMenuItem mntmSortBackward;
	private JMenuItem mntmView;
	private JMenuItem mntmEnrich;
	private JMenuItem mntmExport;
	//private JMenuItem mntmCompile;

	private JMenu mnDictionary;

	private DictionaryEditorShellController controller;

	public DictionaryEditorShell()
	{

		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);

		setBounds(100, 100, 750, 550);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

		// We need two additional panes, so we can show/hide them when switching between editor and table views
		editorPane = new JPanel();
		tablePane = new JPanel();
		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[][grow,fill]"));
		editorPane.setLayout(new MigLayout("ins 7", "[grow,fill,left][200::,right]", "[0!][][grow]"));
		tablePane.setLayout(new MigLayout("ins 7", "[grow,fill]", "[grow,fill]"));
		tablePane.setVisible(false);

		lblnTus = new JLabel("");
		getContentPane().add(lblnTus, "wrap,cell 0 0,alignx left,aligny top,growy 0");

		getContentPane().add(editorPane, "hidemode 3,alignx left,aligny top");
		getContentPane().add(tablePane, "hidemode 3,pushy,alignx left,aligny top");

		popText = createPopupMenu();

		createEditorPane();
		createTablePane();

		getContentPane().addMouseListener(new ContextMenuMouseListener(popText));

		controller = new DictionaryEditorShellController(textPane, this, lblnTus, editorPane, tablePane, table,
				tableContainer);

		this.addInternalFrameListener(new DictionaryCommandInternalFrameListener(controller));

		textPane.getDocument().addDocumentListener(new TextDocumentListener(controller));
		mntmCheckFormat.addActionListener(new CheckActionListener(controller));
		mntmFind.addActionListener(new FindReplaceActionListener(Launcher.getDesktopPane()));
		mntmSort.addActionListener(new SortActionListener(controller, false));
		mntmSortBackward.addActionListener(new SortActionListener(controller, true));
		mntmView.addActionListener(new ViewActionListener(controller));
		mntmEnrich.addActionListener(new EnrichActionListener());
		mntmExport.addActionListener(new ExportActionListener(controller));
		//mntmCompile.addActionListener(new CompileActionListener(controller));
		table.getTableHeader().addMouseListener(new ColumnHeaderMouseListener(controller));

		setSorters();

		mnDictionary = createDictionaryMenu();
	}

	/**
	 * 
	 * Creates the editor pane content
	 * 
	 */
	private void createEditorPane()
	{
		JLabel lblLineNo = new JLabel("Ln n");
		lblLineNo.setHorizontalAlignment(SwingConstants.RIGHT);
		editorPane.add(lblLineNo, "flowx,cell 2 1");

		JLabel lblColNo = new JLabel("Col n");
		lblColNo.setHorizontalAlignment(SwingConstants.RIGHT);
		editorPane.add(lblColNo, "cell 2 1,gapleft 30");

		textPane = new JTextPane();
		textPane.setBorder(BorderFactory.createLoweredBevelBorder());
		
		initializeDefaultFont();

		// A workaround to avoid word wrapping
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(textPane);

		JScrollPane scrollPane = new JScrollPane(noWrapPanel);
		editorPane.add(scrollPane, "cell 0 2,span 3,grow");
		

		textPane.addMouseListener(new ContextMenuMouseListener(popText));
	}

	/**
	 * Creates the table pane/view content
	 */
	private void createTablePane()
	{
		table = new JTable(new DefaultTableModel())
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -5784086340191589971L;

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		tableContainer = new JScrollPane(table);

		table.addMouseListener(new ContextMenuMouseListener(popText));
		tableContainer.addMouseListener(new ContextMenuMouseListener(popText));

		tablePane.add(tableContainer, "grow");
	}

	private void setSorters()
	{
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());

		for (int i = 0; i < table.getColumnCount(); i++)
			sorter.setComparator(i, DictionaryEditorShellController.getComparator());

		table.setRowSorter(sorter);
	}

	/**
	 * Create a right-click menu containing dictionary-related options
	 * 
	 * @return pop-up menu containing dictionary-related options
	 */
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu pop = new JPopupMenu();
		mntmCheckFormat = new JMenuItem("Check Format");
		pop.add(mntmCheckFormat);

		mntmFind = new JMenuItem("Find//Replace//Extract//Count");
		pop.add(mntmFind);

		mntmSort = new JMenuItem("Sort");
		pop.add(mntmSort);

		mntmSortBackward = new JMenuItem("Sort Backward");
		pop.add(mntmSortBackward);

		JSeparator separator_1 = new JSeparator();
		pop.add(separator_1);

		mntmView = new JMenuItem("View as Table");
		
		pop.add(mntmView);

		mntmEnrich = new JMenuItem("Enrich with NooJ's Lexical Information");
		pop.add(mntmEnrich);

		mntmExport = new JMenuItem("Export table as CSV file");
		pop.add(mntmExport);
		mntmExport.setEnabled(false);

		//mntmCompile = new JMenuItem("Compile Dictionary");
		//pop.add(mntmCompile);

		return pop;
	}

	private JMenu createDictionaryMenu()
	{
		Launcher.mnEdit.setVisible(true);
		JMenuItem mntmCut;
		JMenuItem mntmCopy;
		JMenuItem mntmPaste;
		JMenuItem mntmSelectAll;
		
		mntmCut = Launcher.getTextCommands().get("Cut");
		mntmCopy = Launcher.getTextCommands().get("Copy");
		mntmPaste = Launcher.getTextCommands().get("Paste");
		mntmSelectAll = Launcher.getTextCommands().get("Select All");

		// Add cut/copy/paste functionality to the current component
		mntmCut.addActionListener(new CutActionListener(textPane));
		mntmCopy.addActionListener(new CopyActionListener(textPane));
		mntmPaste.addActionListener(new PasteActionListener(textPane));
		mntmSelectAll.addActionListener(new SelectAllActionListener(textPane));
	
		JMenu mnDictionary;

		mnDictionary = new JMenu("DICTIONARY");

		mntmCheckFormat = new JMenuItem("Check Format");
		mnDictionary.add(mntmCheckFormat);
		mntmCheckFormat.addActionListener(new CheckActionListener(controller));

		mntmFind = new JMenuItem("Find//Replace//Extract//Count");
		mnDictionary.add(mntmFind);
		mntmFind.addActionListener(new FindReplaceActionListener(Launcher.getDesktopPane()));

		mntmSort = new JMenuItem("Sort");
		mnDictionary.add(mntmSort);
		mntmSort.addActionListener(new SortActionListener(controller, false));

		mntmSortBackward = new JMenuItem("Sort Backward");
		mnDictionary.add(mntmSortBackward);
		mntmSortBackward.addActionListener(new SortActionListener(controller, true));

		JSeparator separator_1 = new JSeparator();
		mnDictionary.add(separator_1);

		mntmView = new JMenuItem("View as Table");
		mnDictionary.add(mntmView);
		mntmView.addActionListener(new ViewActionListener(controller));

		JMenuItem mntmEnrich = new JMenuItem("Enrich with NooJ's Lexical Information");
		mnDictionary.add(mntmEnrich);
		mntmEnrich.addActionListener(new EnrichActionListener());

		mntmExport = new JMenuItem("Export table as CSV file");
		mnDictionary.add(mntmExport);
		mntmExport.setEnabled(false);
		mntmExport.addActionListener(new ExportActionListener(controller));

		//JMenuItem mntmCompile = new JMenuItem("Compile Dictionary");
		//mnDictionary.add(mntmCompile);
		//mntmCompile.addActionListener(new CompileActionListener(controller));

		return mnDictionary;
	}

	/**
	 * setFont causes the GUI to freeze for 30+ sec, so the default font is initialized using the text pane attribute
	 * set instead
	 */
	private void initializeDefaultFont()
	{
		modifyFont("Courier New", 14);
	}

	public void modifyFont(String fontFamily, int fontSize)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();

		StyleConstants.setFontFamily(attr, fontFamily);
		StyleConstants.setFontSize(attr, fontSize);
		StyleConstants.setForeground(attr, Color.darkGray);

		int textLength = textPane.getText().length();
		textPane.getStyledDocument().setParagraphAttributes(0, textLength, attr, false);

		textPane.repaint();
	}

	public DictionaryEditorShellController getController()
	{
		return controller;
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}

	public JMenuItem getMntmView()
	{
		return mntmView;
	}

	public JMenuItem getMntmCheckFormat()
	{
		return mntmCheckFormat;
	}

	public JMenuItem getMntmFind()
	{
		return mntmFind;
	}

	public JMenuItem getMntmSort()
	{
		return mntmSort;
	}

	public JMenuItem getMntmSortBackward()
	{
		return mntmSortBackward;
	}

	public JMenuItem getMntmExport()
	{
		return mntmExport;
	}

	public JLabel getLblnTus()
	{
		return lblnTus;
	}

	public JMenu getMnDictionary()
	{
		return mnDictionary;
	}
}