package net.nooj4nlp.controller.StatsShell;

import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

/**
 * Panel class which implements drawing relevance or similarity of concordance's sequences.
 */

public class TableJPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	// tables
	private JTable tableOfRelevances;
	private JTable tableOfDistances;
	// flag to determine whether call was made from relevance context or not
	private boolean relevancesActive;

	/**
	 * Constructor.
	 */

	public TableJPanel(boolean relevancesActive)
	{
		// create the table with only one column (others will be added dynamically) and place it on the panel
		super();

		this.relevancesActive = relevancesActive;

		this.setLayout(new MigLayout("insets 5", "[grow]", "[grow]"));
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Term" }, 0);

		// if TableJPanel was called from context of relevances
		JTable table = new JTable(tableModel)
		{
			private static final long serialVersionUID = 1L;

			// forbid editing cells
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		// override UI so that editing cells is forbidden
		table.setUI(new BasicTableUI()
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

		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// set custom renderer and turn off auto sort of columns
		CustomCellRenderer customCellRenderer = new CustomCellRenderer();
		table.setDefaultRenderer(Object.class, customCellRenderer);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer(table));
		this.add(scrollPane, "cell 0 0, grow, align left");

		// set the actual table
		if (this.relevancesActive)
			tableOfRelevances = table;
		else
			tableOfDistances = table;
	}

	// getters and setters
	public JTable getTableOfRelevances()
	{
		return tableOfRelevances;
	}

	public JTable getTableOfDistances()
	{
		return tableOfDistances;
	}
}