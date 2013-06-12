package net.nooj4nlp.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.GraphPresentationDialog.ChangeBackgroundColorActionListener;
import net.nooj4nlp.controller.GraphPresentationDialog.ChangeFontActionListener;
import net.nooj4nlp.controller.GraphPresentationDialog.GraphPresentationController;

/**
 * Class implements Presentation Dialog of opened Grammar.
 * 
 */
public class GraphPresentationDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	private GraphPresentationController controller;

	// components
	private JCheckBox cbFileName;
	private JCheckBox cbFrame;
	private JCheckBox cbDirName;
	private JCheckBox cbAuthor;
	private JCheckBox cbDate;
	private JCheckBox cbInstitution;
	private JCheckBox cbCircleEmptyNodes;

	private JLabel labelInput;
	private JLabel labelOutput;
	private JLabel labelCom;

	private JButton buttonBackground;
	private JButton buttonVariables;
	private JButton buttonForeground;
	private JButton buttonAuxiliary;
	private JButton buttonSelection;
	private JButton buttonComments;

	/**
	 * Constructor.
	 * 
	 * @param grammarController
	 *            - controller of currently opened grammar
	 */

	public GraphPresentationDialog(GrammarEditorShellController grammarController)
	{
		// turn off all window-defined buttons
		setMaximizable(false);
		setIconifiable(false);
		setClosable(false);
		setResizable(true);

		setTitle("Graph Presentation");
		setBounds(75, 75, 540, 400);

		// 5x2 matrix of the main component
		getContentPane().setLayout(new MigLayout("ins 4", "[260:280:300][grow]", "[100][][][][]"));

		// TEXT panel
		JPanel panelText = new JPanel();
		panelText.setBorder(new TitledBorder(null, "Text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// 3x2 matrix of text panel
		panelText.setLayout(new MigLayout("ins 4", "[][]", "[][][]"));
		getContentPane().add(panelText, "cell 0 0, grow, height 90");

		Dimension panelButtonSize = new Dimension(25, 15);

		JButton buttonInput = new JButton("Input");
		buttonInput.setPreferredSize(panelButtonSize);
		buttonInput.setActionCommand("Input");
		panelText.add(buttonInput, "cell 0 0, gaptop 5, grow");

		labelInput = new JLabel("Font");
		panelText.add(labelInput, "cell 1 0, alignx left, gaptop 5, gapleft 5, grow");

		JButton buttonOutput = new JButton("Output");
		buttonOutput.setPreferredSize(panelButtonSize);
		buttonOutput.setActionCommand("Output");
		panelText.add(buttonOutput, "cell 0 1, gaptop 5, grow");

		labelOutput = new JLabel("Font");
		panelText.add(labelOutput, "cell 1 1, alignx left, gaptop 5, gapleft 5, grow");

		JButton buttonCom = new JButton("Com.");
		buttonCom.setPreferredSize(panelButtonSize);
		buttonCom.setActionCommand("Comment");
		panelText.add(buttonCom, "cell 0 2, gaptop 5, grow");

		labelCom = new JLabel("Font");
		panelText.add(labelCom, "cell 1 2, alignx left, gaptop 5, gapleft 5, grow");

		// COLORS panel
		JPanel panelColors = new JPanel();
		panelColors.setBorder(new TitledBorder(null, "Colors", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// 3x2 matrix of color panel with equal columns
		panelColors.setLayout(new MigLayout("ins 3", "[grow][grow]", "[][][]"));
		getContentPane().add(panelColors, "cell 1 0, gapleft 3, grow");

		buttonBackground = new JButton("Background");
		panelColors.add(buttonBackground, "cell 0 0, height 28::, grow");

		buttonVariables = new JButton("Variables");
		panelColors.add(buttonVariables, "cell 1 0, height 28::, gapleft 5, grow");

		buttonForeground = new JButton("Foreground");
		panelColors.add(buttonForeground, "cell 0 1, height 28::, gaptop 5, grow");

		buttonAuxiliary = new JButton("Auxiliary terms");
		panelColors.add(buttonAuxiliary, "cell 1 1, height 28::, gapleft 5, gaptop 5, grow");

		buttonSelection = new JButton("Selection");
		panelColors.add(buttonSelection, "cell 0 2, height 28::, gaptop 5, grow");

		buttonComments = new JButton("Comments");
		panelColors.add(buttonComments, "cell 1 2, height 28::, gaptop 5, gapleft 5, grow");

		// DISPLAY panel
		JPanel panelDisplay = new JPanel();
		panelDisplay.setBorder(new TitledBorder(null, "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// 5x2 matrix of text panel
		panelDisplay.setLayout(new MigLayout("ins 3", "[grow][120:130:140]", "[fill][fill][fill][fill][fill]"));
		getContentPane().add(panelDisplay, "cell 0 1, grow, span 1 4, gaptop 3");

		cbFileName = new JCheckBox("File name");
		panelDisplay.add(cbFileName, "cell 0 0, alignx left");

		cbFrame = new JCheckBox("Frame");
		panelDisplay.add(cbFrame, "cell 1 0, alignx left");

		cbDirName = new JCheckBox("Directory name");
		panelDisplay.add(cbDirName, "cell 0 1, alignx left");

		cbAuthor = new JCheckBox("Author");
		panelDisplay.add(cbAuthor, "cell 1 1, alignx left");

		cbDate = new JCheckBox("Date");
		panelDisplay.add(cbDate, "cell 0 2, alignx left");

		cbInstitution = new JCheckBox("Institution");
		panelDisplay.add(cbInstitution, "cell 1 2, alignx left");

		cbCircleEmptyNodes = new JCheckBox("Circle Empty nodes");
		panelDisplay.add(cbCircleEmptyNodes, "cell 0 3, alignx left");

		JCheckBox cbMaxDisplTerms = new JCheckBox("Max. Displayed terms");
		panelDisplay.add(cbMaxDisplTerms, "cell 0 4, alignx left");

		JTextField tfTerms = new JTextField("4");
		tfTerms.setColumns(4);
		tfTerms.setHorizontalAlignment(JTextField.RIGHT);
		tfTerms.setEnabled(true);
		panelDisplay.add(tfTerms, "cell 1 4, alignx left, gapleft 30");

		// panel-free buttons

		Dimension buttonSize = new Dimension(140, 24);

		JButton buttonGetDefault = new JButton("Get Default");
		buttonGetDefault.setPreferredSize(buttonSize);
		getContentPane().add(buttonGetDefault, "cell 1 1, gaptop 13, gapright 7, alignx right");

		JButton buttonSetDefault = new JButton("Set Default");
		buttonSetDefault.setPreferredSize(buttonSize);
		getContentPane().add(buttonSetDefault, "cell 1 2, gaptop 13, gapright 7, alignx right");

		JButton buttonApply = new JButton("Apply");
		buttonApply.setPreferredSize(buttonSize);
		getContentPane().add(buttonApply, "cell 1 3, gaptop 13, gapright 7, alignx right");

		JButton buttonClose = new JButton("Close");
		buttonClose.setPreferredSize(buttonSize);
		getContentPane().add(buttonClose, "cell 1 4, gaptop 11, gapright 7, alignx right");

		// attach listeners to buttons
		ActionListener changeButtonBackgroundColorListener = new ChangeBackgroundColorActionListener();

		buttonAuxiliary.addActionListener(changeButtonBackgroundColorListener);
		buttonBackground.addActionListener(changeButtonBackgroundColorListener);
		buttonComments.addActionListener(changeButtonBackgroundColorListener);
		buttonForeground.addActionListener(changeButtonBackgroundColorListener);
		buttonSelection.addActionListener(changeButtonBackgroundColorListener);
		buttonVariables.addActionListener(changeButtonBackgroundColorListener);

		// attach controller-dependent listeners to buttons
		controller = new GraphPresentationController(this, grammarController);
		ActionListener changeFontListener = new ChangeFontActionListener(controller);

		buttonInput.addActionListener(changeFontListener);
		buttonOutput.addActionListener(changeFontListener);
		buttonCom.addActionListener(changeFontListener);

		buttonApply.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.apply();
			}
		});

		buttonClose.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.close();
			}
		});

		buttonGetDefault.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.reset();
			}
		});

		buttonSetDefault.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.setDefault();
			}
		});
	}

	// getters and setters
	public GraphPresentationController getController()
	{
		return controller;
	}

	public JCheckBox getCbFileName()
	{
		return cbFileName;
	}

	public JCheckBox getCbFrame()
	{
		return cbFrame;
	}

	public JCheckBox getCbDirName()
	{
		return cbDirName;
	}

	public JCheckBox getCbAuthor()
	{
		return cbAuthor;
	}

	public JCheckBox getCbDate()
	{
		return cbDate;
	}

	public JCheckBox getCbInstitution()
	{
		return cbInstitution;
	}

	public JCheckBox getCbCircleEmptyNodes()
	{
		return cbCircleEmptyNodes;
	}

	public JLabel getLabelInput()
	{
		return labelInput;
	}

	public JLabel getLabelOutput()
	{
		return labelOutput;
	}

	public JLabel getLabelCom()
	{
		return labelCom;
	}

	public JButton getButtonBackground()
	{
		return buttonBackground;
	}

	public JButton getButtonVariables()
	{
		return buttonVariables;
	}

	public JButton getButtonForeground()
	{
		return buttonForeground;
	}

	public JButton getButtonAuxiliary()
	{
		return buttonAuxiliary;
	}

	public JButton getButtonSelection()
	{
		return buttonSelection;
	}

	public JButton getButtonComments()
	{
		return buttonComments;
	}
}