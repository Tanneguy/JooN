package net.nooj4nlp.gui.dialogs.OpenCorpusDialog;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.AmbiguitiesUnambiguitiesActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.components.ColoredJButtonUI;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class for creating Ambiguities and Unambiguous Dialog
 * 
 */

public class AmbiguitiesUnambiguitiesDialog extends JInternalFrame implements PropertyChangeListener
{
	private static final long serialVersionUID = -8021962323484527458L;
	// components
	private JTable tableAmbigUnambig;
	private JLabel counterLabel;
	private JButton btnLocateR, btnLocateG, btnLocateB, btnLocateW;
	private JComboBox comboPattern;
	private JCheckBox cBoxDisplayCategories;
	// variables
	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;
	private boolean isACorpus;
	private int nbOfAmb;
	private boolean areAmbiguities;

	private Language language;
	private boolean reversedSortingActive = false;

	public AmbiguitiesUnambiguitiesDialog(CorpusEditorShellController corpusController,
			TextEditorShellController textController, boolean areAmbiguities)
	{
		super();
		this.textController = textController;
		this.corpusController = corpusController;
		this.areAmbiguities = areAmbiguities;

		if (corpusController != null && corpusController.getShell() != null)
		{
			this.isACorpus = true;
			language = corpusController.getCorpus().lan;
		}
		else
		{
			this.isACorpus = false;
			language = textController.getMyText().Lan;
		}

		// adequate title
		if (areAmbiguities)
			setTitle("Ambiguities");
		else
			setTitle("Unambiguous Words");

		setBounds(170, 170, 500, 450);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		// setting up a window with 4 rows and 2 columns; first column is fixed (150px)
		getContentPane().setLayout(
				new MigLayout("", "[150px][100:170:220][grow]", "[20!, grow][20!, grow][grow][35!, grow]"));

		JLabel selectAnalysisLabel = new JLabel("Select analysis: ");
		getContentPane().add(selectAnalysisLabel, "cell 0 0, alignx left, aligny top");
		// show adequate components
		if (!areAmbiguities)
			selectAnalysisLabel.setVisible(false);

		comboPattern = new JComboBox();
		getContentPane().add(comboPattern, "cell 1 0, alignx center, span 2, aligny top, wmin 170");
		// show adequate components
		if (!areAmbiguities)
			comboPattern.setVisible(false);
		comboPattern.setSelectedItem("");
		//
		counterLabel = new JLabel(":)");
		getContentPane().add(counterLabel, "cell 0 1, alignx left, aligny top, span 2");

		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Freq", "Annotation", "Positions" }, 0);
		tableAmbigUnambig = new JTable(tableModel)
		{
			private static final long serialVersionUID = 1L;

			// forbid editing cells
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		// override UI so that editing cells is forbidden
		tableAmbigUnambig.setUI(new BasicTableUI()
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
		;

		if (areAmbiguities)
			tableAmbigUnambig.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		else
			tableAmbigUnambig.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Removing column - it is not supposed to be seen!
		tableAmbigUnambig.removeColumn(tableAmbigUnambig.getColumnModel().getColumn(2));

		// if it's Ambiguities Dialog, click on table row fills comboBox with values!
		if (areAmbiguities)
		{
			tableAmbigUnambig.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					JTable target = (JTable) e.getSource();
					// if table is empty or if mouse click is pointless do nothing
					if (target.getModel().getRowCount() == 0)
						return;
					int row = target.getSelectedRow();
					if (row == -1)
						return;

					row = tableAmbigUnambig.convertRowIndexToModel(row);

					// get clicked row and get it's parsed value
					String lexs = target.getModel().getValueAt(row, 1).toString();
					String[] alexs = Dic.parseSequenceOfSymbols(lexs);
					// clear combo and fill it with parsed values
					comboPattern.removeAllItems();
					for (String s : alexs)
						comboPattern.addItem(s);
					comboPattern.setSelectedIndex(0);
				}
			});
		}

		JScrollPane scrollPane1 = new JScrollPane(tableAmbigUnambig, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane1, "cell 0 2, grow, span 3");

		tableAmbigUnambig.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tableAmbigUnambig.setAutoCreateRowSorter(false);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableAmbigUnambig.getModel());
		sorter.setSortable(0, false);
		sorter.setSortable(1, false);
		tableAmbigUnambig.setRowSorter(sorter);

		cBoxDisplayCategories = new JCheckBox("Display Only Categories");
		getContentPane().add(cBoxDisplayCategories, "cell 0 3, aligny bottom, alignx left, gapright 80, grow");
		// if it's Ambiguities Dialog, tie action listener to checkBox
		if (areAmbiguities)
			cBoxDisplayCategories.addActionListener(new AmbiguitiesUnambiguitiesActionListener(corpusController,
					textController, this, areAmbiguities, false));
		else
			// show adequate components
			cBoxDisplayCategories.setVisible(false);

		// nooj buttons
		btnLocateR = new JButton("N");
		btnLocateR
				.setUI(new ColoredJButtonUI(Constants.NOOJ_RED_BUTTON_COLOR, Constants.NOOJ_PRESSED_RED_BUTTON_COLOR));
		getContentPane().add(btnLocateR, "cell 1 3, alignx left, grow, split 4");

		btnLocateG = new JButton("o");
		btnLocateG.setUI(new ColoredJButtonUI(Constants.NOOJ_GREEN_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_GREEN_BUTTON_COLOR));
		getContentPane().add(btnLocateG, "cell 1 3, alignx left, grow");

		btnLocateB = new JButton("o");
		btnLocateB.setUI(new ColoredJButtonUI(Constants.NOOJ_BLUE_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_BLUE_BUTTON_COLOR));
		getContentPane().add(btnLocateB, "cell 1 3, alignx left, grow");

		btnLocateW = new JButton("J");
		btnLocateW.setUI(new ColoredJButtonUI(Constants.NOOJ_GRAY_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_GRAY_BUTTON_COLOR));
		getContentPane().add(btnLocateW, "cell 1 3, alignx left, grow");

		// Setting listeners
		this.addInternalFrameListener(new CloseInternalFrameListener(corpusController, this));
		AmbiguitiesUnambiguitiesActionListener auListener = new AmbiguitiesUnambiguitiesActionListener(
				corpusController, textController, this, areAmbiguities, true);

		btnLocateR.addActionListener(auListener);
		btnLocateG.addActionListener(auListener);
		btnLocateB.addActionListener(auListener);
		btnLocateW.addActionListener(auListener);

		InversiveSortActionListener inversiveSort = new InversiveSortActionListener(this, reversedSortingActive,
				language);
		tableAmbigUnambig.getTableHeader().addMouseListener(inversiveSort);

		// before closing, clear outside controllers
		addInternalFrameListener(new AmbigUnambigOnCloseEvents(this, inversiveSort));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			Launcher.getStatusBar().getProgressBar().setIndeterminate(false);
			Launcher.getStatusBar().getProgressBar().setValue(progress);
		}
	}

	/**
	 * Function responsible for getting data and updating table of (Un)Ambiguities Dialog and table of results.
	 * 
	 */
	public void fillInTheData()
	{
		computeAmbiguities(areAmbiguities);
	}

	private void computeAmbiguities(boolean areAmbiguities)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		if (this.isVisible())
			this.hide();

		// desactivate all formText/formCorpus operations
		if (isACorpus)
			corpusController.desactivateOps();
		else
			textController.desactivateOps();

		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		if (areAmbiguities)
		{
			Launcher.progressMessage = "Compute ambiguities...";
			Launcher.getStatusBar().getProgressLabel().setText("Compute ambiguities...");
		}
		else
		{
			Launcher.progressMessage = "Look for Unambiguous words...";
			Launcher.getStatusBar().getProgressLabel().setText("Look for Unambiguous words...");
		}

		if (isACorpus)
		{
			if (areAmbiguities)
			{
//				if (Launcher.multithread)
				{
					// multi-thread
					Launcher.backgroundWorking = true;

					Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.CORPUS_AMBIGUITIES, null,
							corpusController, null);
					Launcher.backgroundWorker.addPropertyChangeListener(this);
					Launcher.backgroundWorker.execute();
				}
	//			else
				{
					// mono-thread
					corpusController.computeAmbiguities(true);
					fillInTheTable();
					corpusController.reactivateOps();
					corpusController.updateTextPaneStats();
					corpusController.updateResults();

					this.show();

					Date now = new Date();
					long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
					Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
					
				}
			}
			else
			{
	//			if (Launcher.multithread)
				{
					// multi-thread
					Launcher.backgroundWorking = true;

					Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.CORPUS_UNAMBIGUITIES, null,
							corpusController, null);
					Launcher.backgroundWorker.addPropertyChangeListener(this);
					Launcher.backgroundWorker.execute();
				}
			//	else
				{
					// mono-thread
					corpusController.computeAmbiguities(false);
					fillInTheTable();
					corpusController.reactivateOps();
					corpusController.updateTextPaneStats();
					corpusController.updateResults();

					this.show();

					Date now = new Date();
					long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
					Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
				}
			}
		}
		else
		{
			if (areAmbiguities)
			{
		//		if (Launcher.multithread)
				{
					// multi-thread
					Launcher.backgroundWorking = true;

					Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.TEXT_AMBIGUITIES, textController,
							null, null);
					Launcher.backgroundWorker.addPropertyChangeListener(this);
					Launcher.backgroundWorker.execute();
				}
			//	else
				{
					// mono-thread
					textController.computeAmbiguities(true);
					fillInTheTable();
					textController.reactivateOps();
					textController.updateTextPaneStats();

					this.show();

					Date now = new Date();
					long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
					Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
					// this.progressLabel.Text = (sec < 5) ? "" : sec.ToString() + " sec";
				}
			}
			else
			{
//				if (Launcher.multithread)
				{
					// multi-thread
					Launcher.backgroundWorking = true;

					Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.TEXT_UNAMBIGUITIES,
							textController, null, null);
					Launcher.backgroundWorker.addPropertyChangeListener(this);
					Launcher.backgroundWorker.execute();
				}
		//		else
				{
					// mono-thread
					textController.computeAmbiguities(false);
					fillInTheTable();
					textController.reactivateOps();
					textController.updateTextPaneStats();

					this.show();

					Date now = new Date();
					long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
					Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
					// this.progressLabel.Text = (sec < 5) ? "" : sec.ToString() + " sec";
				}
			}
		}
	}

	/**
	 * Function responsible for updating table and adequate counter label of (Un)Ambiguities Dialog.
	 * 
	 */
	public void fillInTheTable()
	{
		HashMap<String, ArrayList<Object>> ambigHashTable;
		if (isACorpus && areAmbiguities)
		{
			ambigHashTable = corpusController.getCorpus().hAmbiguities;
			fillInDataHelpFunction(ambigHashTable, tableAmbigUnambig);
			nbOfAmb = ambigHashTable.size();
		}

		else if (isACorpus && !areAmbiguities)
		{
			ambigHashTable = corpusController.getCorpus().hUnambiguities;
			fillInDataHelpFunction(ambigHashTable, tableAmbigUnambig);
			nbOfAmb = ambigHashTable.size();
		}

		else
		{
			if (areAmbiguities)
			{
				ambigHashTable = textController.getMyText().hAmbiguities;
				fillInDataHelpFunction(ambigHashTable, tableAmbigUnambig);
				nbOfAmb = ambigHashTable.size();
			}
			else
			{
				ambigHashTable = textController.getMyText().hUnambiguities;
				fillInDataHelpFunction(ambigHashTable, tableAmbigUnambig);
				nbOfAmb = ambigHashTable.size();
			}
		}

		// set adequate label
		if (areAmbiguities)
			counterLabel.setText(nbOfAmb + " different types of ambiguities!");
		else
			counterLabel.setText(nbOfAmb + " unambiguous linguistic units!");
	}

	/**
	 * Function responsible for creating and updating table and adequate counter label of (Un)Ambiguities Dialog.
	 * 
	 * @param ambigHashTable
	 *            - hash table with (un)ambiguities values
	 * @param previewTable
	 *            - table that serves for preview of results
	 * 
	 */
	private void fillInDataHelpFunction(HashMap<String, ArrayList<Object>> ambigHashTable, JTable previewTable)
	{
		// clear the table
		DefaultTableModel tableModel = (DefaultTableModel) previewTable.getModel();
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();
		previewTable.setModel(tableModel);

		AmbiguitiesUnambiguitiesActionListener.fillTheTable(corpusController, textController, this, areAmbiguities);
	}

	// getters and setters
	public JTable getTableAmbigUnambig()
	{
		return tableAmbigUnambig;
	}

	public void setTableAmbigUnambig(JTable tableAmbigUnambig)
	{
		this.tableAmbigUnambig = tableAmbigUnambig;
	}

	public boolean isACorpus()
	{
		return isACorpus;
	}

	public JCheckBox getcBoxDisplayCategories()
	{
		return cBoxDisplayCategories;
	}

	public JComboBox getComboPattern()
	{
		return comboPattern;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public boolean isReversedSortingActive()
	{
		return reversedSortingActive;
	}

	public void setReversedSortingActive(boolean reversedSortingActive)
	{
		this.reversedSortingActive = reversedSortingActive;
	}

	public boolean isAreAmbiguities()
	{
		return areAmbiguities;
	}
}