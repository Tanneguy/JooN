package net.nooj4nlp.gui.shells;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.DebugShell.DebugShellController;
import net.nooj4nlp.controller.DebugShell.KeySelectionTableActionListener;
import net.nooj4nlp.controller.DebugShell.MouseSelectionTableActionListener;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.components.DebugJTableRenderer;
import net.nooj4nlp.gui.components.DebugJTreeRenderer;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements Debug Shell of a Grammar.
 */

public class DebugShell extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	// controllers
	private GrammarEditorShellController controller;
	private DebugShellController debugController;

	// components
	private JTable tableTraces;
	private JTree treeDebug;
	private JComboBox comboExpression;

	private DebugJTableRenderer customTableRenderer;
	private DebugJTreeRenderer customTreeRenderer;

	/**
	 * Constructor.
	 * 
	 * @param grammarController
	 *            - controller of currently opened grammar
	 */

	public DebugShell(GrammarEditorShellController grammarController)
	{
		this.controller = grammarController;

		// get font from preferences
		Font fontFromPreferences = Launcher.preferences.DFont;

		setMaximizable(false);
		setIconifiable(false);
		setClosable(true);
		setResizable(false);

		setTitle("Debug " + (new File(controller.getFullName())).getName());
		setBounds(51, 51, 760, 300);

		// 4x5 matrix of the main component
		getContentPane().setLayout(
				new MigLayout("ins 9", "[][grow][grow][grow][60:70:80]", "[::30][30:35:40][grow][75::]"));

		JLabel lbEnterExpression = new JLabel("Enter expression: ");
		getContentPane().add(lbEnterExpression, "cell 0 0");

		comboExpression = new JComboBox();
		comboExpression.setPrototypeDisplayValue("XXX");
		comboExpression.setEditable(true);
		comboExpression.setFont(fontFromPreferences);
		comboExpression.setSelectedItem(new String(""));
		getContentPane().add(comboExpression, "cell 1 0, gapleft 5, span 3, grow");

		JButton buttonDebug = new JButton("Debug");
		getContentPane().add(buttonDebug, "cell 4 0, alignx center");

		JLabel lbExplanation = new JLabel("Click a solution below to display the corresponding path: ");
		getContentPane().add(lbExplanation, "cell 0 1, span 2, grow");

		JLabel lbPerfect = new JLabel("Perfect");
		
		lbPerfect.setForeground(new Color(0, 128, 0));
		getContentPane().add(lbPerfect, "cell 2 1, alignx center");

		JLabel lbPartial = new JLabel("Partial");
		lbPartial.setForeground(Color.BLUE);
		getContentPane().add(lbPartial, "cell 3 1, alignx center");

		JLabel lbFailure = new JLabel("Failure");
		lbFailure.setForeground(Color.RED);
		getContentPane().add(lbFailure, "cell 4 1, alignx center");

		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Paths", "Outputs", "Tag" }, 0);

		tableTraces = new JTable(tableModel)
		{
			private static final long serialVersionUID = 1L;

			// forbid editing cells
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		// override UI so that editing cells is forbidden
		tableTraces.setUI(new BasicTableUI()
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

		tableTraces.getTableHeader().setFont(fontFromPreferences);
		tableTraces.setFont(fontFromPreferences);
		tableTraces.removeColumn(tableTraces.getColumnModel().getColumn(2));
		tableTraces.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		customTableRenderer = new DebugJTableRenderer();
		tableTraces.setDefaultRenderer(Object.class, customTableRenderer);

		JScrollPane scrollPane = new JScrollPane(tableTraces, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane, "cell 0 2, span 5, growx");

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("");
		treeDebug = new JTree(top);
		treeDebug.setRootVisible(false);
		treeDebug.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		Icon subgraphIcon = null;
		renderer.setLeafIcon(subgraphIcon);
		renderer.setClosedIcon(subgraphIcon);
		renderer.setOpenIcon(subgraphIcon);
		treeDebug.setCellRenderer(renderer);
		JScrollPane treeView = new JScrollPane(treeDebug);
		getContentPane().add(treeView, "cell 0 3, span 5, grow");

		customTreeRenderer = new DebugJTreeRenderer(Color.WHITE);
		treeDebug.setCellRenderer(customTreeRenderer);

		debugController = new DebugShellController(this, grammarController);

		// set controller-dependent listeners
		buttonDebug.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				debugController.buttonPressedFunction();
			}
		});

		comboExpression.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				debugController.comboPressedKeyEvent(e);
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});

		tableTraces.addMouseListener(new MouseSelectionTableActionListener(debugController));
		tableTraces.addKeyListener(new KeySelectionTableActionListener(debugController));

		// clear Grammar's reference to the closed Debug shell, so it could be reopened.
		addInternalFrameListener(new InternalFrameListener()
		{
			@Override
			public void internalFrameOpened(InternalFrameEvent e)
			{
			}

			@Override
			public void internalFrameIconified(InternalFrameEvent e)
			{
			}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent e)
			{
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e)
			{
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e)
			{
				controller.debugShell = null;

				controller.getTimerDbg().stop();
			}

			@Override
			public void internalFrameClosed(InternalFrameEvent e)
			{
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e)
			{
			}
		});
	}

	// getters and setters
	public JTable getTableTraces()
	{
		return tableTraces;
	}

	public JTree getTreeDebug()
	{
		return treeDebug;
	}

	public JComboBox getComboExpression()
	{
		return comboExpression;
	}

	public DebugJTableRenderer getCustomTableRenderer()
	{
		return customTableRenderer;
	}

	public void setTreeDebug(JTree treeDebug)
	{
		this.treeDebug = treeDebug;
	}

	public DebugJTreeRenderer getCustomTreeRenderer()
	{
		return customTreeRenderer;
	}
}