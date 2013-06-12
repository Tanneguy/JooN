package net.nooj4nlp.gui.shells;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.StatsShell.DrawingJPanel;
import net.nooj4nlp.controller.StatsShell.StatsShellController;
import net.nooj4nlp.controller.StatsShell.TableJPanel;
import net.nooj4nlp.engine.Constants;

public class StatsShell extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	// controller
	private StatsShellController statsController;

	// radio buttons
	private JRadioButton rbFrequencies;
	private JRadioButton rbStandardScore;
	private JRadioButton rbTfIDf;
	private JRadioButton rbDistances;

	// (custom) panels
	private JPanel displayPanel;
	private DrawingJPanel panelOfFrequencies;
	private DrawingJPanel panelOfStandardScore;
	private TableJPanel panelOfRelevances;
	private TableJPanel panelOfSimilarities;

	public StatsShell()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);

		setBounds(200, 100, 850, 700);

		// 2x5 - line with buttons and panels below
		getContentPane().setLayout(new MigLayout("insets 5", "[grow][grow][grow][grow][grow]", "[::50][grow]"));

		// attach changing panel action to each radio button
		rbFrequencies = new JRadioButton("Frequencies");
		getContentPane().add(rbFrequencies, "cell  0 0, alignx left");
		rbFrequencies.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statsController.changePanel(displayPanel, Constants.FREQUENCIES);
			}
		});

		rbStandardScore = new JRadioButton("Standard Score");
		getContentPane().add(rbStandardScore, "cell 1 0, alignx left");
		rbStandardScore.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statsController.changePanel(displayPanel, Constants.STANDARD_SCORE);
			}
		});

		rbTfIDf = new JRadioButton("Relevance");
		getContentPane().add(rbTfIDf, "cell 2 0, alignx left");
		rbTfIDf.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statsController.changePanel(displayPanel, Constants.RELEVANCES);
			}
		});

		rbDistances = new JRadioButton("Similarity");
		getContentPane().add(rbDistances, "cell 3 0, alignx left");
		rbDistances.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statsController.changePanel(displayPanel, Constants.SIMILARITY);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(rbFrequencies);
		group.add(rbStandardScore);
		group.add(rbTfIDf);
		group.add(rbDistances);

		JButton exportButton = new JButton("Export Report");
		getContentPane().add(exportButton, "cell 4 0, alignx right");
		exportButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statsController.exportStatistics();
			}
		});

		displayPanel = new JPanel(new CardLayout());
		getContentPane().add(displayPanel, "cell 0 1, span 5, grow");

		panelOfFrequencies = new DrawingJPanel(this, true);
		panelOfStandardScore = new DrawingJPanel(this, false);
		panelOfRelevances = new TableJPanel(true);
		panelOfSimilarities = new TableJPanel(false);
	}

	// getters and setters
	public void setStatsController(StatsShellController statsController)
	{
		this.statsController = statsController;
	}

	public StatsShellController getStatsController()
	{
		return statsController;
	}

	public JRadioButton getRbFrequencies()
	{
		return rbFrequencies;
	}

	public JPanel getDisplayPanel()
	{
		return displayPanel;
	}

	public DrawingJPanel getPanelOfFrequencies()
	{
		return panelOfFrequencies;
	}

	public DrawingJPanel getPanelOfStandardScore()
	{
		return panelOfStandardScore;
	}

	public TableJPanel getPanelOfRelevances()
	{
		return panelOfRelevances;
	}

	public TableJPanel getPanelOfSimilarities()
	{
		return panelOfSimilarities;
	}

	public JRadioButton getRbStandardScore()
	{
		return rbStandardScore;
	}

	public JRadioButton getRbTfIDf()
	{
		return rbTfIDf;
	}

	public JRadioButton getRbDistances()
	{
		return rbDistances;
	}
}