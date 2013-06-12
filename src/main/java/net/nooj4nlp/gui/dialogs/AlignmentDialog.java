package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.AlignmentDialog.AlignmentController;
import net.nooj4nlp.controller.AlignmentDialog.AlignmentJPanel;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.gui.components.UsualColoredButtonUI;

/**
 * Class implements Alignment Dialog of currently opened Grammar.
 */

public class AlignmentDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	private AlignmentController controller;

	private JCheckBox cbUseGrid;

	/**
	 * Constructor.
	 * 
	 * @param grammarController
	 *            - controller of currently opened Grammar
	 */

	public AlignmentDialog(GrammarEditorShellController grammarController)
	{
		// turn off all window-defined buttons and forbid resizing
		setMaximizable(false);
		setIconifiable(false);
		setClosable(false);
		setResizable(false);

		setTitle("Alignment");
		setBounds(123, 123, 290, 320);

		// 3x2 matrix of window
		getContentPane().setLayout(new MigLayout("ins 4", "[grow][100:110:120]", "[100][grow][40]"));

		// horizontal panel
		AlignmentJPanel panelHorizontal = new AlignmentJPanel(true);
		panelHorizontal.setBorder(new TitledBorder(null, "Horizontal", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		// 1x3 matrix of horizontal panel
		panelHorizontal.setLayout(new MigLayout("ins 2", "[33%][34%][33%]", "[]"));
		getContentPane().add(panelHorizontal, "cell 0 0, height 90, span");

		JButton buttonTop = new JButton("Top");
		buttonTop.setPreferredSize(new Dimension(60, 25));
		buttonTop.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelHorizontal.add(buttonTop, "cell 0 0, gapleft 10, aligny bottom");

		JButton buttonHCenter = new JButton("Center");
		buttonHCenter.setPreferredSize(new Dimension(60, 41));
		buttonHCenter.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelHorizontal.add(buttonHCenter, "cell 1 0, gapleft 6, aligny center");

		JButton buttonBottom = new JButton("Bottom");
		buttonBottom.setPreferredSize(new Dimension(60, 31));
		buttonBottom.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelHorizontal.add(buttonBottom, "cell 2 0, gapright 10, gapbottom 35, aligny top");

		// horizontal panel
		AlignmentJPanel panelVertical = new AlignmentJPanel(false);
		panelVertical.setBorder(new TitledBorder(null, "Vertical", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// 3x1 matrix of vertical panel
		panelVertical.setLayout(new MigLayout("ins 2", "[grow]", "[33%][34%][33%]"));
		getContentPane().add(panelVertical, "cell 0 1, height 170, span 1 2, grow");

		JButton buttonLeft = new JButton("Left");
		buttonLeft.setPreferredSize(new Dimension(60, 31));
		buttonLeft.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelVertical.add(buttonLeft, "cell 0 0, alignx right, gapbottom 15, gapright 8");

		JButton buttonVCenter = new JButton("Center");
		buttonVCenter.setPreferredSize(new Dimension(60, 36));
		buttonVCenter.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelVertical.add(buttonVCenter, "cell 0 1, alignx center, gapbottom 8");

		JButton buttonRight = new JButton("Right");
		buttonRight.setPreferredSize(new Dimension(60, 31));
		buttonRight.setUI(new UsualColoredButtonUI(Color.GRAY, Color.BLACK));
		panelVertical.add(buttonRight, "cell 0 2, gapleft 8, gapbottom 5");

		// panel-free components
		cbUseGrid = new JCheckBox("Use Grid");
		getContentPane().add(cbUseGrid, "cell 1 1, alignx left, aligny bottom");

		JButton buttonClose = new JButton("Close");
		getContentPane().add(buttonClose, "cell 1 2, alignx left, aligny top, gapleft 2, growx");

		this.controller = new AlignmentController(this, grammarController);

		// attach listeners to dialog components
		buttonClose.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.close();
			}
		});

		buttonBottom.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.bottomAlign();
			}
		});

		buttonTop.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.topAlign();
			}
		});

		buttonHCenter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.centerHorizontalAlign();
			}
		});

		buttonLeft.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.leftAlign();
			}
		});

		buttonRight.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.rightAlign();
			}
		});

		buttonVCenter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.centerVerticalAlign();
			}
		});

		cbUseGrid.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				controller.useGrid();
			}
		});
	}

	// getters and setters
	public AlignmentController getController()
	{
		return controller;
	}

	public JCheckBox getCbUseGrid()
	{
		return cbUseGrid;
	}
}