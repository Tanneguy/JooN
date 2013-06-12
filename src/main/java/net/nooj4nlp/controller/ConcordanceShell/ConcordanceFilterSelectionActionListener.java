package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Class implements (de)selection, filtering (de)selection and hapaxes for table of concordance window.
 */
public class ConcordanceFilterSelectionActionListener implements ActionListener
{
	// controller
	private ConcordanceShellController controller;

	private JTable concordanceTable;

	// flag to determine if all rows needs to be selected
	private boolean selectAll;
	// flag to determine if the filtering mode is on
	private boolean isFilteringActive = false;
	// flag to determine if the filtering mode is for selected lines or not
	private boolean filterSelectedLines;
	// flag to determine if hapax should be hidden
	private boolean isHapaxActive = false;

	/**
	 * Constructor for case of (de)selection and filtering.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param selectAll
	 *            - flag to determine if all rows needs to be selected
	 * @param isFilteringActive
	 *            - flag to determine if the filtering mode is on
	 * @param filterSelectedLines
	 *            - flag to determine if the filtering mode is for selected lines or not
	 */
	public ConcordanceFilterSelectionActionListener(ConcordanceShellController controller, boolean selectAll,
			boolean isFilteringActive, boolean filterSelectedLines)
	{
		this.controller = controller;
		this.concordanceTable = this.controller.getConcordanceTable();
		this.selectAll = selectAll;
		this.filterSelectedLines = filterSelectedLines;
		this.isFilteringActive = isFilteringActive;
		this.isHapaxActive = false;
	}

	/**
	 * Constructor for case of hiding hapaxes.
	 * 
	 * @param controller
	 *            - concordance controller
	 */
	public ConcordanceFilterSelectionActionListener(ConcordanceShellController controller)
	{
		this.controller = controller;
		this.concordanceTable = this.controller.getConcordanceTable();
		this.isHapaxActive = true;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// if function was called for filtering...
		if (isFilteringActive)
		{
			// get selected rows, sort them, reverse iteration to avoid conflicts, and remove selected rows and
			// their
			// color mapping
			DefaultTableModel model = (DefaultTableModel) concordanceTable.getModel();
			int[] selectedRows = concordanceTable.getSelectedRows();
			int selectedRowsLength = selectedRows.length;
			if (selectedRowsLength == 0)
				return;
			Arrays.sort(selectedRows);

			List<Object[]> listOfObjects = new ArrayList<Object[]>();

			// if selected rows should be filtered
			if (filterSelectedLines)
			{
				// go through model backwards...
				for (int i = model.getRowCount() - 1; i > -1; i--)
				{
					// through selected rows too
					for (int j = selectedRowsLength - 1; j > -1; j--)
					{
						// if matched
						if (i == selectedRows[j])
						{
							DefaultTableModel backModel = controller.getTableModel();

							// if concordance is sorted...
							if (backModel != null)
							{
								// compare color and annotations; if they match, remove them from
								// the back model and color map and break
								Object color = model.getValueAt(i, 4);
								Object annotations = model.getValueAt(i, 5);
								for (int k = backModel.getRowCount() - 1; k > -1; k--)
								{
									if (color.equals(backModel.getValueAt(k, 4))
											&& annotations.equals(backModel.getValueAt(k, 5)))
									{
										backModel.removeRow(k);
										// Changed when removing static variables
										CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
												.getDefaultRenderer(Object.class);
										customForegroundTableRenderer.updateMapsWhenRowIsDeleted(i, k);
										// CustomForegroundTableRenderer.updateMapsWhenRowIsDeleted(i, k);
										break;
									}
								}
							}
							// if concordance isn't sorted, just remove the line from the color map
							else
							{
								// Changed when removing static variables
								CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
										.getDefaultRenderer(Object.class);
								customForegroundTableRenderer.updateMapsWhenRowIsDeleted(-1, i);
							}
							break;
						}

						// if not matched and ending index, then row needs to be saved (add to list)
						if (j == 0)
						{
							Object[] object = new Object[6];
							object[0] = model.getValueAt(i, 0);
							object[1] = model.getValueAt(i, 1);
							object[2] = model.getValueAt(i, 2);
							object[3] = model.getValueAt(i, 3);
							object[4] = model.getValueAt(i, 4);
							object[5] = model.getValueAt(i, 5);
							listOfObjects.add(object);
						}
					}
				}
			}
			// if rows that weren't selected should be filtered...
			else
			{
				// go through model backwards...
				for (int i = model.getRowCount() - 1; i > -1; i--)
				{
					// through selected rows too
					for (int j = selectedRowsLength - 1; j > -1; j--)
					{
						// if matched, add row to save list
						if (i == selectedRows[j])
						{
							Object[] object = new Object[6];
							object[0] = model.getValueAt(i, 0);
							object[1] = model.getValueAt(i, 1);
							object[2] = model.getValueAt(i, 2);
							object[3] = model.getValueAt(i, 3);
							object[4] = model.getValueAt(i, 4);
							object[5] = model.getValueAt(i, 5);
							listOfObjects.add(object);

							break;
						}

						// if not matched and ending index
						if (j == 0)
						{
							DefaultTableModel backModel = controller.getTableModel();
							// if concordance is sorted...
							if (backModel != null)
							{
								// ...compare color and annotations; if they match, remove them from
								// the back model and color map and break
								Object color = model.getValueAt(i, 4);
								Object annotations = model.getValueAt(i, 5);
								for (int k = backModel.getRowCount() - 1; k > -1; k--)
								{
									if (color.equals(backModel.getValueAt(k, 4))
											&& annotations.equals(backModel.getValueAt(k, 5)))
									{
										backModel.removeRow(k);
										// Changed when removing static variables
										CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
												.getDefaultRenderer(Object.class);
										customForegroundTableRenderer.updateMapsWhenRowIsDeleted(i, k);
										break;
									}
								}
							}

							// if concordance isn't sorted, just remove the line from the color map
							else
							{
								// Changed when removing static variables
								CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
										.getDefaultRenderer(Object.class);
								customForegroundTableRenderer.updateMapsWhenRowIsDeleted(-1, i);
							}
						}
					}
				}
			}

			// remove all elements from model
			model.getDataVector().removeAllElements();
			model.fireTableDataChanged();

			// reverse the save list (because of backward tracking)
			Collections.reverse(listOfObjects);
			// add list to the model and set table's model
			for (int k = 0; k < listOfObjects.size(); k++)
				model.addRow(listOfObjects.get(k));

			concordanceTable.setModel(model);

			// notify table data about filtering
			filterHelpFunction(controller, model);

			// exit to avoid entering next if clause
			return;
		}
		// if function wasn't called from hapax context, it should do the (de)selection
		if (!isHapaxActive)
		{
			if (selectAll)
				concordanceTable.setRowSelectionInterval(0, concordanceTable.getRowCount() - 1);
			else
				concordanceTable.getSelectionModel().clearSelection();
		}

		// removing hapaxes...the same process as above
		else
		{
			DefaultTableModel model = (DefaultTableModel) concordanceTable.getModel();
			for (int i = model.getRowCount() - 1; i > -1; i--)
			{
				if (onlyOne(model, i))
				{
					DefaultTableModel backModel = controller.getTableModel();
					if (backModel != null)
					{
						Object color = model.getValueAt(i, 4);
						Object annotations = model.getValueAt(i, 5);
						for (int k = backModel.getRowCount() - 1; k > -1; k--)
						{
							if (color.equals(backModel.getValueAt(k, 4))
									&& annotations.equals(backModel.getValueAt(k, 5)))
							{
								backModel.removeRow(k);
								// Changed when removing static variables
								CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
										.getDefaultRenderer(Object.class);
								customForegroundTableRenderer.updateMapsWhenRowIsDeleted(i, k);
								break;
							}
						}
					}
					else
					{
						// Changed when removing static variables
						CustomForegroundTableRenderer customForegroundTableRenderer = (CustomForegroundTableRenderer) concordanceTable
								.getDefaultRenderer(Object.class);
						customForegroundTableRenderer.updateMapsWhenRowIsDeleted(-1, i);
					}

					model.removeRow(i);
					concordanceTable.getSelectionModel().clearSelection();
				}
			}
			filterHelpFunction(controller, model);
		}
	}

	/**
	 * Filter/Selection of concordance, help function. Notifies concordance table data of new changes of model.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param model
	 *            - concordance table model
	 */

	private void filterHelpFunction(ConcordanceShellController controller, DefaultTableModel model)
	{
		List<Object> theItems = controller.getTheItems();
		// first unselect everything
		for (int j = 0; j < theItems.size(); j += 4)
			theItems.set(j + 2, false);

		// then select only the remaining lines of the concordance
		for (int k = 0; k < model.getRowCount(); k++)
		{
			for (int l = 0; l < theItems.size(); l += 4)
			{
				Object[] item = (Object[]) theItems.get(l + 1);
				if (!item[5].equals(model.getValueAt(k, 5)))
					continue;
				theItems.set(l + 2, true);
				break;
			}
		}
		controller.setTheItems(theItems);

		if (controller.getSyntacticTreeShell() != null)
		{
			controller.getSyntacticTreeShell().dispose();
			controller.setSyntacticTreeShell(null);
		}

		controller.getConcordanceShell().getEntriesNBLabel().setText(model.getRowCount() + "/" + (theItems.size() / 4));
	}

	/**
	 * Function determines whether should be only one sequence in concordance table or not.
	 * 
	 * @param model
	 *            - model of concordance table
	 * @param index
	 *            - index of current row
	 * @return - flag to tell whether data from current row should be deleted or not
	 */
	private boolean onlyOne(DefaultTableModel model, int index)
	{
		String seq = model.getValueAt(index, 2).toString();
		for (int i = model.getRowCount() - 1; i > -1; i--)
		{
			if (model.getValueAt(index, 0).equals(model.getValueAt(i, 0))
					&& model.getValueAt(index, 1).equals(model.getValueAt(i, 1))
					&& model.getValueAt(index, 2).equals(model.getValueAt(i, 2))
					&& model.getValueAt(index, 3).equals(model.getValueAt(i, 3))
					&& model.getValueAt(index, 4).equals(model.getValueAt(i, 4))
					&& model.getValueAt(index, 5).equals(model.getValueAt(i, 5)))
				continue;

			String seqc = model.getValueAt(i, 2).toString();

			if (seq.equals(seqc))
				return false;
		}

		return true;
	}
}