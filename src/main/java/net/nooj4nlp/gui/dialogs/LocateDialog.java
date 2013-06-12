package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.LocateDialog.CloseInternalFrameListener;
import net.nooj4nlp.controller.LocateDialog.ConcordanceLocateActionListener;
import net.nooj4nlp.controller.LocateDialog.LocateDialogPatternActionListener;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.ColoredJButtonUI;

/**
 * Locate dialog
 */
public class LocateDialog extends JInternalFrame
{
	private static final long serialVersionUID = -5687237998263649977L;

	// controllers, depending on context of a call
	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;

	// flag determinator (text/corpus)
	private boolean isCorpus;

	// components
	private JRadioButton rbStringPattern, rbPerlPattern, rbNooJPattern, rbNooJGrammar;
	private JRadioButton rbShortestMatches, rbLongestMatches, rbAllIndexMatches;
	private JRadioButton rbAllOccurrences, rbOnly;
	private JComboBox nooJRegeXCombo, nooJGrammarPathCombo;
	private JTextField txtNumberOfOccurrences;
	private JButton setButton;
	private JCheckBox syntacticAnalysisCBox, oneOccPerMatchCBox, resetConcordanceCBox;

	private ConcordanceLocateActionListener concordanceLocateActionListener;

	/**
	 * Constructor. One controller is always null, depending on a flag.
	 * 
	 * @param textController
	 *            - text controller if such exists
	 * @param corpusController
	 *            - corpus controller if such exists
	 * @param corpus
	 *            - corpus/text flag.
	 */
	public LocateDialog(TextEditorShellController textController, CorpusEditorShellController corpusController,
			boolean corpus)
	{
		this.textController = textController;
		this.corpusController = corpusController;
		this.isCorpus = corpus;

		// no resize or maxi/minimize
		setIconifiable(false);
		setMaximizable(false);
		setResizable(false);
		setClosable(true);
		if (!corpus)
		{
			if (this.textController.getFileToBeOpenedOrImported() != null)
				setTitle("Locate a pattern in " + this.textController.getFileToBeOpenedOrImported().getName());
			else
				setTitle("Locate a pattern in ");
		}
		else
			setTitle("Locate a pattern in " + this.corpusController.getFullName());
		setBounds(50, 50, 400, 420);

		// 3x2, plus new layouts inside existing
		getContentPane().setLayout(
				new MigLayout("insets 5", "[::150,grow,left][::220,grow,fill]", "[200::,grow][50::,grow,fill][20]"));

		JPanel patternPanel = new JPanel();
		patternPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Pattern is:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(patternPanel, "cell 0 0, span 2, grow");

		// 7x2 in first layout
		patternPanel
				.setLayout(new MigLayout("insets 3", "[250!][30!, grow, fill]",
						"[20::, grow, fill][20::, grow, fill][20::, grow, fill][20, fill][20::][20, fill][20::, grow, center]"));

		// radio buttons pattern group
		rbStringPattern = new JRadioButton("a string of characters:");
		patternPanel.add(rbStringPattern, "cell 0 0, span 2, align left");
		rbStringPattern.addActionListener(new LocateDialogPatternActionListener(this));

		rbPerlPattern = new JRadioButton("a PERL regular expression:");
		patternPanel.add(rbPerlPattern, "cell 0 1, span 2, align left");
		rbPerlPattern.addActionListener(new LocateDialogPatternActionListener(this));

		rbNooJPattern = new JRadioButton("a NooJ regular expression:");
		rbNooJPattern.setSelected(true);
		patternPanel.add(rbNooJPattern, "cell 0 2, span 2, align left");
		rbNooJPattern.addActionListener(new LocateDialogPatternActionListener(this));

		List<String> inputRegexList;
		// load saved, past inputs of combo box if such exists
		if (isCorpus)
			inputRegexList = corpusController.getLocateRegexMemoryList();
		else
			inputRegexList = textController.getLocateRegexMemoryList();

		if (inputRegexList != null && inputRegexList.size() > 0)
			nooJRegeXCombo = new JComboBox(inputRegexList.toArray());
		else
			nooJRegeXCombo = new JComboBox();
		// do not resize combo box if size of text changes
		nooJRegeXCombo.setPrototypeDisplayValue("XXX");
		nooJRegeXCombo.setEditable(true);
		nooJRegeXCombo.setSelectedItem(new String(""));
		nooJRegeXCombo.setMaximumSize(new Dimension(280, 20));
		patternPanel.add(nooJRegeXCombo, "cell 0 3, span 2, gapleft 20, width 280, height 20");

		if (isCorpus)
		{
			if (corpusController.getCorpus().lan.rightToLeft)
				nooJRegeXCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			else
				nooJRegeXCombo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		else
		{
			// Alignment is based on text direction
			if (textController.isNooJRightToLeft())
				nooJRegeXCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			else
				nooJRegeXCombo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}

		rbNooJGrammar = new JRadioButton("a NooJ grammar:");
		patternPanel.add(rbNooJGrammar, "cell 0 4, span 2, align left");
		if (isCorpus)
			rbNooJGrammar.addActionListener(new LocateDialogPatternActionListener(this, corpusController, this));
		else
			rbNooJGrammar.addActionListener(new LocateDialogPatternActionListener(this, textController, this));

		List<String> inputGrammarList;
		// load saved, past inputs of combo box if such exists
		if (isCorpus)
			inputGrammarList = corpusController.getLocateGrammarMemoryList();
		else
			inputGrammarList = textController.getLocateGrammarMemoryList();

		if (inputGrammarList != null && inputGrammarList.size() > 0)
			nooJGrammarPathCombo = new JComboBox(inputGrammarList.toArray());
		else
			nooJGrammarPathCombo = new JComboBox();
		// do not resize combo box if size of text changes
		nooJGrammarPathCombo.setPrototypeDisplayValue("XXX");
		nooJGrammarPathCombo.setEditable(true);
		nooJGrammarPathCombo.setSelectedItem(new String(""));
		nooJGrammarPathCombo.setMaximumSize(new Dimension(250, 20));
		patternPanel.add(nooJGrammarPathCombo, "cell 0 5, align left, gapleft 20, width 250, height 20");
		nooJGrammarPathCombo.setEnabled(false);

		// button for file chooser dialog
		setButton = new JButton("Set");
		patternPanel.add(setButton, "cell 1 5, gapleft 10, align right, wrap");
		setButton.setEnabled(false);
		if (isCorpus)
			setButton.addActionListener(new LocateDialogPatternActionListener(this, corpusController, this));
		else
			setButton.addActionListener(new LocateDialogPatternActionListener(this, textController, this));

		syntacticAnalysisCBox = new JCheckBox("Syntactic Analysis");
		patternPanel.add(syntacticAnalysisCBox, "cell 0 6, gapleft 50");
		syntacticAnalysisCBox.setEnabled(false);

		ButtonGroup rbPatternGroup = new ButtonGroup();
		rbPatternGroup.add(rbStringPattern);
		rbPatternGroup.add(rbPerlPattern);
		rbPatternGroup.add(rbNooJPattern);
		rbPatternGroup.add(rbNooJGrammar);

		resetConcordanceCBox = new JCheckBox("Reset Concordance");
		getContentPane().add(resetConcordanceCBox, "cell 0 2, align left");
		resetConcordanceCBox.setSelected(true);

		// NooJ colored buttons
		JButton actionButtonR = new JButton("N");
		actionButtonR.setUI(new ColoredJButtonUI(Constants.NOOJ_RED_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_RED_BUTTON_COLOR));
		getContentPane().add(actionButtonR, "cell 1 2, split 4, align right");

		JButton actionButtonG = new JButton("o");
		actionButtonG.setUI(new ColoredJButtonUI(Constants.NOOJ_GREEN_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_GREEN_BUTTON_COLOR));
		getContentPane().add(actionButtonG, "cell 1 2, align right");

		JButton actionButtonB = new JButton("o");
		actionButtonB.setUI(new ColoredJButtonUI(Constants.NOOJ_BLUE_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_BLUE_BUTTON_COLOR));
		getContentPane().add(actionButtonB, "cell 1 2, align right");

		JButton actionButtonC = new JButton("J");
		actionButtonC.setUI(new ColoredJButtonUI(Constants.NOOJ_GRAY_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_GRAY_BUTTON_COLOR));
		getContentPane().add(actionButtonC, "cell 1 2, align right");

		// Adding listeners
		concordanceLocateActionListener = new ConcordanceLocateActionListener(isCorpus, textController,
				corpusController, this);
		actionButtonR.addActionListener(concordanceLocateActionListener);
		actionButtonG.addActionListener(concordanceLocateActionListener);
		actionButtonB.addActionListener(concordanceLocateActionListener);
		actionButtonC.addActionListener(concordanceLocateActionListener);

		// 3x1 second layout
		JPanel indexPanel = new JPanel();
		indexPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Index",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(indexPanel, "cell 0 1, align left, grow");
		indexPanel.setLayout(new MigLayout("insets 3", "[100::, grow, fill]",
				"[20::, grow, fill][20!, grow, fill][20::, grow, fill]"));

		// radio button index group
		rbShortestMatches = new JRadioButton("Shortest matches");
		indexPanel.add(rbShortestMatches, "cell 0 0, align left");

		rbLongestMatches = new JRadioButton("Longest matches");
		rbLongestMatches.setSelected(true);
		indexPanel.add(rbLongestMatches, "cell 0 1, align left");

		rbAllIndexMatches = new JRadioButton("All matches");
		indexPanel.add(rbAllIndexMatches, "cell 0 2, align left");

		ButtonGroup rbIndexGroup = new ButtonGroup();
		rbIndexGroup.add(rbShortestMatches);
		rbIndexGroup.add(rbLongestMatches);
		rbIndexGroup.add(rbAllIndexMatches);

		// 3x3 third layout
		JPanel limitationPanel = new JPanel();
		limitationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Limitation",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(limitationPanel, "cell 1 1, align right, grow");
		limitationPanel.setLayout(new MigLayout("insets 3", "[50::, grow, fill][50::, grow, fill][50::, grow, fill]",
				"[20::, grow, fill][20!, grow, fill][20::, grow, fill]"));

		// radio button limitation group
		rbAllOccurrences = new JRadioButton("All occurrences");
		limitationPanel.add(rbAllOccurrences, "cell 0 0, span 3, align left");

		rbOnly = new JRadioButton("Only: ");
		rbOnly.setSelected(true);
		limitationPanel.add(rbOnly, "cell 0 1, align left");

		txtNumberOfOccurrences = new JTextField();
		limitationPanel.add(txtNumberOfOccurrences, "cell 1 1, align center");
		txtNumberOfOccurrences.setText("100");
		rbAllOccurrences.addActionListener(new LocateDialogLimitationActionListener(txtNumberOfOccurrences, rbOnly));
		rbOnly.addActionListener(new LocateDialogLimitationActionListener(txtNumberOfOccurrences, rbOnly));

		JLabel occurrencesLabel = new JLabel("occ.");
		limitationPanel.add(occurrencesLabel, "cell 2 1, align left");

		oneOccPerMatchCBox = new JCheckBox("1 occ. per match");
		oneOccPerMatchCBox.setSelected(false);
		limitationPanel.add(oneOccPerMatchCBox, "cell 0 2, span 3, align left");

		ButtonGroup rbLimitationGroup = new ButtonGroup();
		rbLimitationGroup.add(rbAllOccurrences);
		rbLimitationGroup.add(rbOnly);

		addInternalFrameListener(new CloseInternalFrameListener(corpusController, textController));
	}

	public ConcordanceLocateActionListener getConcordanceLocateActionListener()
	{
		return concordanceLocateActionListener;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public JRadioButton getRbStringPattern()
	{
		return rbStringPattern;
	}

	public void setRbStringPattern(JRadioButton rbStringPattern)
	{
		this.rbStringPattern = rbStringPattern;
	}

	public JRadioButton getRbPerlPattern()
	{
		return rbPerlPattern;
	}

	public void setRbPerlPattern(JRadioButton rbPerlPattern)
	{
		this.rbPerlPattern = rbPerlPattern;
	}

	public JRadioButton getRbNooJPattern()
	{
		return rbNooJPattern;
	}

	public void setRbNooJPattern(JRadioButton rbNooJPattern)
	{
		this.rbNooJPattern = rbNooJPattern;
	}

	public JRadioButton getRbNooJGrammar()
	{
		return rbNooJGrammar;
	}

	public void setRbNooJGrammar(JRadioButton rbNooJGrammar)
	{
		this.rbNooJGrammar = rbNooJGrammar;
	}

	public JCheckBox getSyntacticAnalysisCBox()
	{
		return syntacticAnalysisCBox;
	}

	public void setSyntacticAnalysisCBox(JCheckBox syntacticAnalysisCBox)
	{
		this.syntacticAnalysisCBox = syntacticAnalysisCBox;
	}

	public JComboBox getNooJGrammarPathCombo()
	{
		return nooJGrammarPathCombo;
	}

	public void setNooJGrammarPathCombo(JComboBox nooJGrammarPathCombo)
	{
		this.nooJGrammarPathCombo = nooJGrammarPathCombo;
	}

	public JComboBox getNooJRegeXCombo()
	{
		return nooJRegeXCombo;
	}

	public void setNooJRegeXCombo(JComboBox nooJRegeXCombo)
	{
		this.nooJRegeXCombo = nooJRegeXCombo;
	}

	public JCheckBox getOneOccPerMatchCBox()
	{
		return oneOccPerMatchCBox;
	}

	public JCheckBox getResetConcordanceCBox()
	{
		return resetConcordanceCBox;
	}

	public JButton getSetButton()
	{
		return setButton;
	}

	public JRadioButton getRbShortestMatches()
	{
		return rbShortestMatches;
	}

	public JRadioButton getRbLongestMatches()
	{
		return rbLongestMatches;
	}

	public JRadioButton getRbAllIndexMatches()
	{
		return rbAllIndexMatches;
	}

	public JRadioButton getRbOnly()
	{
		return rbOnly;
	}

	public JTextField getTxtNumberOfOccurrences()
	{
		return txtNumberOfOccurrences;
	}
}

/**
 * Action listener class for radio button group "limitation".
 * 
 */
class LocateDialogLimitationActionListener implements ActionListener
{
	private JTextField txtNumberOfOccurrences;
	private JRadioButton rbOnly;

	public LocateDialogLimitationActionListener(JTextField txtNumberOfOccurrences, JRadioButton rbOnly)
	{
		this.txtNumberOfOccurrences = txtNumberOfOccurrences;
		this.rbOnly = rbOnly;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == rbOnly)
			txtNumberOfOccurrences.setEnabled(true);
		else
			txtNumberOfOccurrences.setEnabled(false);
	}
}