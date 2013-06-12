package net.nooj4nlp.gui.dialogs;

import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.GrammarEditorShell.GenerateActionListener;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;

/**
 * Class implements window shown after calling the grammar function - 'Generate language'
 * 
 */
public class GenerateDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	private GrammarEditorShellController grammarController;

	// components
	private JCheckBox cbStopAfter;
	private JCheckBox cbExploreEmbeddedGraphs;
	private JRadioButton rbSequences;
	private JRadioButton rbSeconds;
	private JTextField tfSequences;
	private JTextField tfSeconds;

	/**
	 * Constructor.
	 * 
	 * @param grammarController
	 *            - controller of opened grammar
	 */

	public GenerateDialog(GrammarEditorShellController grammarController)
	{
		this.grammarController = grammarController;

		// window should not be resized!
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setTitle("Generation " + (new File(grammarController.getFullName())).getName());
		setBounds(150, 150, 355, 205);
		// 4x4 matrix
		getContentPane().setLayout(new MigLayout("ins 7", "[40!][60!][60!][100::]", "[40!,grow][20!][fill][fill]"));

		cbExploreEmbeddedGraphs = new JCheckBox("Explore Embedded Graphs");
		cbExploreEmbeddedGraphs.setSelected(true);
		getContentPane().add(cbExploreEmbeddedGraphs, "cell 0 0, span 3, alignx left, aligny top");

		cbStopAfter = new JCheckBox("Stop after:");
		cbStopAfter.setSelected(false);
		getContentPane().add(cbStopAfter, "cell 0 1, span 3, alignx left");

		rbSeconds = new JRadioButton();
		rbSeconds.setEnabled(false);
		getContentPane().add(rbSeconds, "cell 0 2, alignx right");

		tfSeconds = new JTextField("5");
		tfSeconds.setColumns(6);
		tfSeconds.setHorizontalAlignment(JTextField.RIGHT);
		tfSeconds.setEnabled(false);
		getContentPane().add(tfSeconds, "cell 1 2, alignx center");

		final JLabel lbSeconds = new JLabel("seconds");
		lbSeconds.setEnabled(false);
		getContentPane().add(lbSeconds, "cell 2 2, alignx left");

		rbSequences = new JRadioButton();
		rbSequences.setEnabled(false);
		rbSequences.setSelected(true);
		getContentPane().add(rbSequences, "cell 0 3, alignx right");

		tfSequences = new JTextField("100");
		tfSequences.setColumns(6);
		tfSequences.setHorizontalAlignment(JTextField.RIGHT);
		tfSequences.setEnabled(false);
		getContentPane().add(tfSequences, "cell 1 3, alignx center");

		final JLabel lbSequences = new JLabel("sequences");
		lbSequences.setEnabled(false);
		getContentPane().add(lbSequences, "cell 2 3, alignx left");

		JButton btnExploreAllPaths = new JButton("Explore all paths");
		getContentPane().add(btnExploreAllPaths, "cell 3 2, span 1 2");

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rbSeconds);
		buttonGroup.add(rbSequences);

		cbStopAfter.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (cbStopAfter.isSelected())
				{
					rbSeconds.setEnabled(true);
					tfSeconds.setEnabled(true);
					lbSeconds.setEnabled(true);

					rbSequences.setEnabled(true);
					tfSequences.setEnabled(true);
					lbSequences.setEnabled(true);
				}
				else
				{
					rbSeconds.setEnabled(false);
					tfSeconds.setEnabled(false);
					lbSeconds.setEnabled(false);

					rbSequences.setEnabled(false);
					tfSequences.setEnabled(false);
					lbSequences.setEnabled(false);
				}
			}
		});

		btnExploreAllPaths.addActionListener(new GenerateActionListener(this, this.grammarController));
	}

	// getters and setters
	public JCheckBox getCbStopAfter()
	{
		return cbStopAfter;
	}

	public JRadioButton getRbSequences()
	{
		return rbSequences;
	}

	public JTextField getTfSequences()
	{
		return tfSequences;
	}

	public JRadioButton getRbSeconds()
	{
		return rbSeconds;
	}

	public JTextField getTfSeconds()
	{
		return tfSeconds;
	}

	public JCheckBox getCbExploreEmbeddedGraphs()
	{
		return cbExploreEmbeddedGraphs;
	}
}