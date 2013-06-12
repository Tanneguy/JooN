package net.nooj4nlp.gui.shells;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.ConcordanceShell.ConcordanceFilterSelectionActionListener;
import net.nooj4nlp.controller.ConcordanceShell.ConcordanceMouseActionListener;
import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.ConcordanceShell.ConcordanceTableSorter;
import net.nooj4nlp.controller.ConcordanceShell.CustomForegroundTableRenderer;
import net.nooj4nlp.controller.ConcordanceShell.ExportConcordanceActionListener;
import net.nooj4nlp.controller.ConcordanceShell.ExtractConcordanceActionListener;
import net.nooj4nlp.controller.ConcordanceShell.RightClickPopupMenuForConcordance;
import net.nooj4nlp.controller.ConcordanceShell.TextBoxConcordanceActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.gui.actions.shells.construct.OpenStatsActionListener;
import net.nooj4nlp.gui.actions.shells.construct.SyntacticTreeActionListener;
import net.nooj4nlp.gui.actions.shells.control.ConcordanceCommandInternalFrameListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.utilities.Helper;

/**
 * Class for implementation of concordance shell.
 */

public class ConcordanceShell extends JInternalFrame
{
	private static final long serialVersionUID = 4359582798272725945L;

	// controllers
	private ConcordanceShellController controller;
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;

	// components
	private JRadioButton rbCharacters;
	private JRadioButton rbWordForms;
	private JTable concordanceTable;
	private JCheckBox cbMatches;
	private JCheckBox cbOutputs;
	private JLabel entriesNBLabel;
	private JTextField beforeTF;
	private JTextField afterTF;
	private JLabel queryNameLabel;

	private boolean rbCharactersIsSelected = false;
	private JMenu mnConcordance;
	private JMenuItem mntmAddRemoveAnnotations;
	private JMenuItem mntmDisplaySyntacticAnalysis;

	private CustomForegroundTableRenderer customForegroundTableRenderer;

	/**
	 * Constructor.
	 * 
	 * @param corpusController
	 *            - corpus from which context this concordance was open
	 * @param textController
	 *            - text from which context this concordance was open
	 */
	public ConcordanceShell(CorpusEditorShellController corpusController, TextEditorShellController textController)
	{
		this.corpusController = corpusController;
		this.textController = textController;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);

		// set adequate title
		if (corpusController != null && corpusController.getShell() != null)
			setTitle("Concordance for Corpus " + corpusController.getFullName());

		else if (corpusController == null && textController != null)
		{
			setTitle("Concordance for Text " + textController.getTextName());

			// if Concordance was opened from text context, text should be right below the Concordance
			// to reflect the changes of text being colored
			Helper.putDialogOnTheTop(textController.getTextShell());
			Helper.putDialogOnTheTop(this);
		}

		else
			setTitle("Concordance");

		setBounds(50, 50, 750, 420);

		// 3x1 with 9 split cells in the first row, and one panel with, again, 2x1 layout in its 4th cell
		// 2 split cells in third row
		getContentPane().setLayout(new MigLayout("insets 5", "[500::, grow]", "[::30, center][grow][::20]"));

		JButton resetButton = new JButton("Reset");
		getContentPane().add(resetButton, "cell 0 0, align left, split 9, gapright 20");

		JLabel displayLabel = new JLabel("Display:");
		getContentPane().add(displayLabel, "cell 0 0, align left");

		beforeTF = new JTextField(4);
		beforeTF.setText("5");
		beforeTF.setHorizontalAlignment(JTextField.RIGHT);
		getContentPane().add(beforeTF, "cell 0 0, align left, wrap 30, wmin 30");

		JPanel rbPanel = new JPanel();
		getContentPane().add(rbPanel, "cell 0 0, gapright 10");
		rbPanel.setLayout(new MigLayout("insets 0", "[fill]", "[::15][::15]"));

		rbCharacters = new JRadioButton("characters");
		rbPanel.add(rbCharacters, "cell 0 0, gaptop 1");

		rbWordForms = new JRadioButton("word forms");
		rbPanel.add(rbWordForms, "cell 0 1, gapbottom 5");
		rbWordForms.setSelected(true);

		ButtonGroup rbGroup = new ButtonGroup();
		rbGroup.add(rbCharacters);
		rbGroup.add(rbWordForms);

		JLabel beforeAndLabel = new JLabel(" before, and ");
		getContentPane().add(beforeAndLabel, "cell 0 0");

		afterTF = new JTextField(4);
		afterTF.setText("5");
		afterTF.setHorizontalAlignment(JTextField.RIGHT);
		getContentPane().add(afterTF, "cell 0 0, align left, wrap 30, wmin 30");

		if (ConcordanceShellController.getBefore() != 0)
		{
			beforeTF.setText(Integer.toString(ConcordanceShellController.getBefore()));
			afterTF.setText(Integer.toString(ConcordanceShellController.getAfter()));
		}

		JLabel afterDisplayLabel = new JLabel(" after. Display:");
		getContentPane().add(afterDisplayLabel, "cell 0 0, align right");

		cbMatches = new JCheckBox("Matches");
		getContentPane().add(cbMatches, "cell 0 0, align right");
		cbMatches.setSelected(true);

		cbOutputs = new JCheckBox("Outputs");
		getContentPane().add(cbOutputs, "cell 0 0, align right");

		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Text", "Before", "Seq.", "After", "Color",
				"Tag" }, 0);
		concordanceTable = new JTable(tableModel)
		{
			private static final long serialVersionUID = 7039783882968364592L;

			// forbid editing cells
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		// override UI so that editing cells is forbidden
		concordanceTable.setUI(new BasicTableUI()
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

		// Removing column - it is not supposed to be seen!
		concordanceTable.removeColumn(concordanceTable.getColumnModel().getColumn(5));
		concordanceTable.removeColumn(concordanceTable.getColumnModel().getColumn(4));

		// set custom renderer and turn off auto sort of columns
		customForegroundTableRenderer = new CustomForegroundTableRenderer();
		concordanceTable.setDefaultRenderer(Object.class, customForegroundTableRenderer);
		concordanceTable.setAutoCreateRowSorter(false);

		JScrollPane scrollPane = new JScrollPane(concordanceTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane, "cell 0 1, grow");

		concordanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setSortable(0, false);
		sorter.setSortable(1, false);
		sorter.setSortable(2, false);
		sorter.setSortable(3, false);

		concordanceTable.setRowSorter(sorter);

		queryNameLabel = new JLabel("Query");
		getContentPane().add(queryNameLabel, "cell 0 2, split 2, gapright 250");

		entriesNBLabel = new JLabel("New Label2");
		getContentPane().add(entriesNBLabel, "cell 0 2");

		controller = new ConcordanceShellController(this);

		if (controller.isRbCharactersIsPressed())
			rbCharacters.setSelected(true);
		else
			rbCharacters.setSelected(false);

		if (controller.isCbMatchesIsPressed())
			cbMatches.setSelected(true);
		else
			cbMatches.setSelected(false);

		if (controller.isCbOutputsIsPressed())
			cbOutputs.setSelected(true);
		else
			cbOutputs.setSelected(false);

		// parse adequate values from text box
		beforeTF.addKeyListener(new TextBoxConcordanceActionListener(beforeTF, afterTF, controller));
		afterTF.addKeyListener(new TextBoxConcordanceActionListener(beforeTF, afterTF, controller));

		// overridden column click event
		concordanceTable.getTableHeader().addMouseListener(new ConcordanceTableSorter(controller, concordanceTable));

		// set the listener for "onSelectedIndexChanged" of JTable
		ListSelectionModel rowSelectionModel = concordanceTable.getSelectionModel();
		rowSelectionModel.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int[] selectedRows = concordanceTable.getSelectedRows();
				DefaultTableModel tableModel = (DefaultTableModel) concordanceTable.getModel();

				if (selectedRows.length > 0)
				{
					List<Object> theItems = controller.getTheItems();
					for (int i = 0; i < theItems.size(); i += 4)
					{
						Object[] item = (Object[]) theItems.get(i + 1);
						if (!tableModel.getValueAt(selectedRows[0], 0).equals(item[0])
								|| !tableModel.getValueAt(selectedRows[0], 1).equals(item[1])
								|| !tableModel.getValueAt(selectedRows[0], 2).equals(item[2])
								|| !tableModel.getValueAt(selectedRows[0], 3).equals(item[3])
								|| !tableModel.getValueAt(selectedRows[0], 4).equals(item[4])
								|| !tableModel.getValueAt(selectedRows[0], 5).equals(item[5]))
							continue;

						queryNameLabel.setText(theItems.get(i).toString());
						break;
					}
				}
			}
		});

		// double click mouse listener and add menu
		concordanceTable.addMouseListener(new ConcordanceMouseActionListener(controller));
		this.addInternalFrameListener(new ConcordanceCommandInternalFrameListener(controller));

		addMouseListener(new RightClickPopupMenuForConcordance(controller));
		concordanceTable.addMouseListener(new RightClickPopupMenuForConcordance(controller));

		// before closing, clear variables from custom renderer and also, items list
		addInternalFrameListener(new CustomInternalFrameListener(corpusController, textController, controller,
				customForegroundTableRenderer));

		// Adding listeners moved because of rearranging the code concerning 'static'...
		resetButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				controller.reset();
			}
		});

		rbCharacters.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!rbCharactersIsSelected)
				{
					beforeTF.setText(Integer.toString(ConcordanceShellController.getBefore() * 10));
					afterTF.setText(Integer.toString(ConcordanceShellController.getAfter() * 10));
					controller.radioButtonEvent(true);
				}

				rbCharactersIsSelected = true;
			}
		});

		rbWordForms.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (rbCharactersIsSelected)
				{
					int before = ConcordanceShellController.getBefore();
					int after = ConcordanceShellController.getAfter();
					beforeTF.setText(Integer.toString((before - (before % 10)) / 10));
					afterTF.setText(Integer.toString((after - (after % 10)) / 10));
					controller.radioButtonEvent(false);
				}

				rbCharactersIsSelected = false;
			}
		});

		cbMatches.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.refreshConcordance();
				if (!cbMatches.isSelected())
					controller.setCbMatchesIsPressed(false);
				else
					controller.setCbMatchesIsPressed(true);
				controller.setWidthOfTableColumn(concordanceTable, (DefaultTableModel) concordanceTable.getModel(), 2);
			}
		});

		cbOutputs.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.refreshConcordance();
			}
		});

		mnConcordance = createConcordanceMenu();
	}

	private JMenu createConcordanceMenu()
	{
		JMenu mnConcordance = new JMenu("CONCORDANCE");

		JMenuItem mntmSelectAll = new JMenuItem("Select all");
		mnConcordance.add(mntmSelectAll);
		mntmSelectAll.addActionListener(new ConcordanceFilterSelectionActionListener(controller, true, false, false));

		JMenuItem mntmUnselectAll = new JMenuItem("Unselect all");
		mnConcordance.add(mntmUnselectAll);
		mntmUnselectAll
				.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false, false, false));

		JMenuItem mntmFilterSelectedLines = new JMenuItem("Filter out selected lines");
		mnConcordance.add(mntmFilterSelectedLines);
		mntmFilterSelectedLines.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false, true,
				true));

		JMenuItem mntmFilterUnselectedLines = new JMenuItem("Filter out unselected lines");
		mnConcordance.add(mntmFilterUnselectedLines);
		mntmFilterUnselectedLines.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false,
				true, false));

		JMenuItem mntmRepeatSegmentsHideHapaxes = new JMenuItem("Repeted segments only / Hide hapaxes");
		mnConcordance.add(mntmRepeatSegmentsHideHapaxes);
		mntmRepeatSegmentsHideHapaxes.addActionListener(new ConcordanceFilterSelectionActionListener(controller));

		JSeparator separator_1 = new JSeparator();
		mnConcordance.add(separator_1);

		mntmAddRemoveAnnotations = new JMenuItem("Annotate Text (add/remove annotations)");
		mnConcordance.add(mntmAddRemoveAnnotations);
		mntmAddRemoveAnnotations.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
					controller.annotate();
					
				}

				finally
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				}
			}
		});

		mntmDisplaySyntacticAnalysis = new JMenuItem("Display Syntactic Analysis");
		mnConcordance.add(mntmDisplaySyntacticAnalysis);
		mntmDisplaySyntacticAnalysis.addActionListener(new SyntacticTreeActionListener(controller));

		JSeparator separator_2 = new JSeparator();
		mnConcordance.add(separator_2);

		JMenuItem mntmExportConcordanceAsText = new JMenuItem("Export Concordance As TXT");
		mnConcordance.add(mntmExportConcordanceAsText);
		mntmExportConcordanceAsText.addActionListener(new ExportConcordanceActionListener(controller, 1));

		JMenuItem mntmExportConcordanceAsWeb = new JMenuItem("Export Concordance As Web Page");
		mnConcordance.add(mntmExportConcordanceAsWeb);
		mntmExportConcordanceAsWeb.addActionListener(new ExportConcordanceActionListener(controller, 2));

		JMenuItem mntmExportIndex = new JMenuItem("Export Index");
		mnConcordance.add(mntmExportIndex);
		mntmExportIndex.addActionListener(new ExportConcordanceActionListener(controller, 3));

		JMenuItem mntmExtractMatchingTU = new JMenuItem("Extract Matching Text Units");
		mnConcordance.add(mntmExtractMatchingTU);
		mntmExtractMatchingTU.addActionListener(new ExtractConcordanceActionListener(controller, true));

		JMenuItem mntmExtractNonMatchingTU = new JMenuItem("Extract Non Matching Text Units");
		mnConcordance.add(mntmExtractNonMatchingTU);
		mntmExtractNonMatchingTU.addActionListener(new ExtractConcordanceActionListener(controller, false));

		JMenuItem mntmStatisticalAnalyses = new JMenuItem("Statistical Analyses");
		mnConcordance.add(mntmStatisticalAnalyses);
		mntmStatisticalAnalyses.addActionListener(new OpenStatsActionListener(controller.getCorpusController(),
				controller.getTextController(), controller));

		return mnConcordance;
	}

	// getters and setters
	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public JRadioButton getRbCharacters()
	{
		return rbCharacters;
	}

	public JRadioButton getRbWordForms()
	{
		return rbWordForms;
	}

	public JTable getConcordanceTable()
	{
		return concordanceTable;
	}

	public JCheckBox getCbMatches()
	{
		return cbMatches;
	}

	public JCheckBox getCbOutputs()
	{
		return cbOutputs;
	}

	public JLabel getEntriesNBLabel()
	{
		return entriesNBLabel;
	}

	public ConcordanceShellController getController()
	{
		return controller;
	}

	public JTextField getBeforeTF()
	{
		return beforeTF;
	}

	public JTextField getAfterTF()
	{
		return afterTF;
	}

	public CustomForegroundTableRenderer getCustomForegroundTableRenderer()
	{
		return customForegroundTableRenderer;
	}

	public void setCustomForegroundTableRenderer(CustomForegroundTableRenderer customForegroundTableRenderer)
	{
		this.customForegroundTableRenderer = customForegroundTableRenderer;
	}

	public JMenu getMnConcordance()
	{
		return mnConcordance;
	}

	public JMenuItem getMntmAddRemoveAnnotations()
	{
		return mntmAddRemoveAnnotations;
	}

	public JMenuItem getMntmDisplaySyntacticAnalysis()
	{
		return mntmDisplaySyntacticAnalysis;
	}
}

class CustomInternalFrameListener implements InternalFrameListener
{
	private CorpusEditorShellController corpusController;
	private ConcordanceShellController controller;
	private TextEditorShellController textController;
	private CustomForegroundTableRenderer customForegroundTableRenderer;

	CustomInternalFrameListener(CorpusEditorShellController corpusController, TextEditorShellController textController,
			ConcordanceShellController controller, CustomForegroundTableRenderer customForegroundTableRenderer)
	{
		this.textController = textController;
		this.corpusController = corpusController;
		this.controller = controller;
		this.customForegroundTableRenderer = customForegroundTableRenderer;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		controller.setTheItems(new ArrayList<Object>());
		controller.setConcordanceTableSorted(false);
		controller.setTableModel(null);
		if (textController != null)
			textController.setConcordanceController(null);
		if (corpusController != null && corpusController.getShell() != null)
			corpusController.setConcordanceController(null);

		customForegroundTableRenderer.setSortedRowsMap(new HashMap<Integer, Color>());
		customForegroundTableRenderer.setColoredRowsMap(new HashMap<Integer, Color>());
		customForegroundTableRenderer.setSortedPreview(false);

		if (controller.getSyntacticTreeShell() != null)
		{
			controller.getSyntacticTreeShell().dispose();
			controller.setSyntacticTreeShell(null);
		}
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e)
	{
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e)
	{
	}
}