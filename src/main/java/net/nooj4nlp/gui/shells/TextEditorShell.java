package net.nooj4nlp.gui.shells;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.CorpusEditorShell.ExportColoredToHtmlActionListener;
import net.nooj4nlp.controller.CorpusEditorShell.ExportXmlActionListener;
import net.nooj4nlp.controller.LocateDialog.LocateDialogTextActionListener;
import net.nooj4nlp.controller.TextEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.TextEditorShell.ComputeMouseAdapterForText;
import net.nooj4nlp.controller.TextEditorShell.JMftPanel;
import net.nooj4nlp.controller.TextEditorShell.RightClickPopupMenuForText;
import net.nooj4nlp.controller.TextEditorShell.TASactionListener;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextLinguisticAnalysisActionListener;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.gui.actions.documents.CopyActionListener;
import net.nooj4nlp.gui.actions.documents.CutActionListener;
import net.nooj4nlp.gui.actions.documents.PasteActionListener;
import net.nooj4nlp.gui.actions.documents.SelectAllActionListener;
import net.nooj4nlp.gui.actions.shells.control.TextCommandInternalFrameListener;
import net.nooj4nlp.gui.actions.shells.modify.ModifyTextActionListener;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.components.CustomJSpinner;
import net.nooj4nlp.gui.dialogs.ExportXmlDialog;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.dialogs.LocateDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AlphabetDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.AmbiguitiesUnambiguitiesDialog;
import net.nooj4nlp.gui.dialogs.OpenCorpusDialog.TokensDigramsDialog;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Shell for working with text
 * 
 */
public class TextEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = -1613487531803530130L;

	private JTextPane textPane;
	private JList listOfResults;
	private CustomJSpinner spinner;
	private Ntext text;
	private JCheckBox chckbxShowTextAnnotation;
	private JLabel lblnTus;
	private TextEditorShellController textController;
	private CorpusEditorShellController corpusController;
	private boolean newText;

	private JTextArea txtInfo;
	private JScrollPane scrollList;
	private JSplitPane splitPane;
	private JScrollPane panelScrollPane;
	private JScrollPane scrollPane;

	private JMftPanel hiddenPanel;
	private MouseListener rightClickListener;

	private ExportXmlDialog exportXmlDialog;
	private LocateDialog locateDialog;
	private FindReplaceDialog findReplaceDialog;

	private AlphabetDialog alphabetDialog;
	private AmbiguitiesUnambiguitiesDialog ambiguitiesDialog;
	private AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog;
	private TokensDigramsDialog tokensDialog;
	private TokensDigramsDialog digramsDialog;

	private TextCommandInternalFrameListener textCommandInternalFrameListener;
	private ComputeMouseAdapterForText computeMouseAdapterForText;
	private UnitSelectionListener unitSelectionListener;
	private CloseInternalFrameListener closeInternalFrameListener;
	private TASactionListener tasActionListener;

	private JMenu mnText;

	/**
	 * Create the frame.
	 * 
	 * @param corpus
	 *            Corpus from which text was opened. Null if text was not opened from corpus.
	 * @param txt
	 *            Ntext object containing the text to work with
	 * @param title
	 *            window title (usually the file name)
	 * @param delimText
	 *            text unit delimiter
	 */
	public TextEditorShell(CorpusEditorShellController controller, Ntext txt, String title, String delimText,
			boolean newText)
	{
		alphabetDialog = null;
		tokensDialog = null;
		digramsDialog = null;
		ambiguitiesDialog = null;
		unAmbiguitiesDialog = null;
		exportXmlDialog = null;
		findReplaceDialog = null;

		text = txt;
		corpusController = controller;
		this.newText = newText;

		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setTitle(title);
		setBounds(100, 100, 935, 435);
		this.setMinimumSize(new Dimension(550,350));
		getContentPane().setLayout(new MigLayout("ins 7", "[230!,left][120!,right][70::, grow]", "[100][20][][fill,grow]"));

		txtInfo = new JTextArea();
		txtInfo.setBackground(new Color(227, 227, 227));
		txtInfo.setEnabled(false);
		txtInfo.setText("Language is \"" + text.getLanguage().engName + " (" + text.getLanguage().isoName + ")\".\n"
				+ delimText);
		txtInfo.setBorder(BorderFactory.createLoweredBevelBorder());
		JScrollPane scrollInfo = new JScrollPane(txtInfo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollInfo.setMinimumSize(new Dimension(70, 100));
		getContentPane().add(scrollInfo, "cell 2 0,span 0 2, alignx right, aligny top, grow");

		chckbxShowTextAnnotation = new JCheckBox("Show Text Annotation Structure");
		getContentPane().add(chckbxShowTextAnnotation, "cell 0 1, aligny bottom, alignx left");

		DefaultListModel model2 = new DefaultListModel();
		listOfResults = new JList(model2);
		listOfResults.setBorder(BorderFactory.createLoweredBevelBorder());

		scrollList = new JScrollPane(listOfResults, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollList, "cell 1 0, span 0 2, aligny top, alignx right, grow");

		textPane = new JTextPane();
		textPane.setBorder(BorderFactory.createLoweredBevelBorder());

		textController = new TextEditorShellController(this);

		// create custom spinner
		if (!newText)
		{
			spinner = new CustomJSpinner(this, text.nbOfTextUnits);
			spinner.setEnabled(true);
		}
		else
		{
			spinner = new CustomJSpinner(this, 1);
			spinner.setEnabled(false);
		}

		getContentPane().add(spinner, "flowx,cell 0 0, split 2, alignx left, aligny top");

		// catch "Enter" key event for a custom value
		final JFormattedTextField jtf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
		jtf.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String text = jtf.getText();
					// show error dialogs if custom value is not regular
					try
					{
						Integer newValue = Integer.valueOf(text);
						CustomJSpinner spinner = getSpinner();
						if (newValue > spinner.getUpperLimit() || newValue < 1)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.NOOJ_NUMBER_RANGE_INPUT_MESSAGE, Constants.NOOJ_APPLICATION_NAME
											+ " error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						spinner.setCustomValue(newValue);
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.NOOJ_NUMBER_INPUT_MESSAGE,
								Constants.NOOJ_APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					unitSelectionListener.keyEventFunction(textController, e);
			}
		});
		lblnTus = new JLabel("");
		getContentPane().add(lblnTus, "cell 0 0,alignx center,aligny top");

		scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		getContentPane().add(scrollPane, "cell 0 3, span 3, hidemode 2, grow");

		// hidden panel for TAS functionality
		hiddenPanel = new JMftPanel(textController);

		panelScrollPane = new JScrollPane(hiddenPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelScrollPane.setPreferredSize(new Dimension(550, 200));

		hiddenPanel.setParentScrollPane(panelScrollPane);

		// hidden split pane for displaying text and TAS
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splitPane.setContinuousLayout(true);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(200, 50);
		scrollPane.setMinimumSize(minimumSize);
		splitPane.setMinimumSize(minimumSize);

		// set initial and resize divider location of hidden JSplitPane
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);

		// by default TAS is hidden
		splitPane.setVisible(false);

		getContentPane().add(splitPane, "cell 0 2, span 3, hidemode 2");

		tasActionListener = new TASactionListener(getContentPane(), chckbxShowTextAnnotation, splitPane, scrollPane,
				panelScrollPane, textController);
		chckbxShowTextAnnotation.addActionListener(tasActionListener);

		if (corpusController == null)
		{
			textCommandInternalFrameListener = new TextCommandInternalFrameListener(textController);
			this.addInternalFrameListener(textCommandInternalFrameListener);
		}
		else
		{
			corpusController.setTextShell(this);
		}

		computeMouseAdapterForText = new ComputeMouseAdapterForText(textController);
		listOfResults.addMouseListener(computeMouseAdapterForText);

		closeInternalFrameListener = new CloseInternalFrameListener(textController, this, corpusController);
		this.addInternalFrameListener(closeInternalFrameListener);

		rightClickListener = new RightClickPopupMenuForText(textController);

		// right click pop up menu
		addMouseListener(rightClickListener);
		textPane.addMouseListener(rightClickListener);
		txtInfo.addMouseListener(rightClickListener);

		// If not empty, add a Text Unit (TU) selection listener
		if (text.buffer != null)
		{
			textPane.setText(text.buffer);
			textPane.moveCaretPosition(0);
			textPane.getCaret().setVisible(true);
			// Move caret to beginning of text
			unitSelectionListener = new UnitSelectionListener(textController, textPane);
			textPane.addCaretListener(unitSelectionListener);
			textPane.addKeyListener(new KeyListener()
			{
				@Override
				public void keyTyped(KeyEvent e)
				{
				}

				@Override
				public void keyReleased(KeyEvent e)
				{
				}

				@Override
				public void keyPressed(KeyEvent e)
				{
					unitSelectionListener.keyEventFunction(textController, e);
				}
			});
			textPane.setEditable(false);
		}
		else
		{
			text.buffer = "";
		}

		mnText = createTextMenu();
	}

	// Function added after static variables removal
	public void refreshListenersAndAdapters()
	{
		this.removeInternalFrameListener(textCommandInternalFrameListener);
		textCommandInternalFrameListener = new TextCommandInternalFrameListener(textController);
		this.addInternalFrameListener(textCommandInternalFrameListener);

		this.listOfResults.removeMouseListener(computeMouseAdapterForText);
		computeMouseAdapterForText = new ComputeMouseAdapterForText(textController);
		this.listOfResults.addMouseListener(computeMouseAdapterForText);

		this.textPane.removeCaretListener(unitSelectionListener);
		unitSelectionListener = new UnitSelectionListener(textController, textPane);
		this.textPane.addCaretListener(unitSelectionListener);

		this.removeInternalFrameListener(closeInternalFrameListener);
		closeInternalFrameListener = new CloseInternalFrameListener(textController, this, corpusController);
		this.addInternalFrameListener(closeInternalFrameListener);
	}

	private JMenu createTextMenu()
	{  
		Launcher.mnEdit.setVisible(true);
		
		JMenuItem mntmCut;
		JMenuItem mntmCopy;
		JMenuItem mntmPaste;
		JMenuItem mntmSelectAll;
		JMenuItem mntmLinguisticAnalysis;
		JMenuItem mntmLocate;
		JMenuItem mntmExportAnnotatedTexts;
		JMenuItem mntmExportColoredTexts;
		JMenuItem mntmModify;

		mntmCut = Launcher.getTextCommands().get("Cut");
		mntmCopy = Launcher.getTextCommands().get("Copy");
		mntmPaste = Launcher.getTextCommands().get("Paste");
		mntmSelectAll = Launcher.getTextCommands().get("Select All");

		// Add cut/copy/paste functionality to the current component
		mntmCut.addActionListener(new CutActionListener(textPane));
		mntmCopy.addActionListener(new CopyActionListener(textPane));
		mntmPaste.addActionListener(new PasteActionListener(textPane));
		mntmSelectAll.addActionListener(new SelectAllActionListener(textPane));

		mnText = new JMenu("TEXT");

		mntmLinguisticAnalysis = new JMenuItem("Linguistic Analysis");
		mnText.add(mntmLinguisticAnalysis);
		TextLinguisticAnalysisActionListener textLinguisticAnalysisActionListener = new TextLinguisticAnalysisActionListener(
				textController);
		mntmLinguisticAnalysis.addActionListener(textLinguisticAnalysisActionListener);

		mntmLocate = new JMenuItem("Locate");
		mnText.add(mntmLocate);
		mntmLocate.addActionListener(new LocateDialogTextActionListener(textController, null));

		JSeparator separator_1 = new JSeparator();
		mnText.add(separator_1);

		mntmExportColoredTexts = new JMenuItem("Export colored text as an HTML document");
		mnText.add(mntmExportColoredTexts);
		mntmExportColoredTexts.setEnabled(textController.isColored());
		mntmExportColoredTexts.addActionListener(new ExportColoredToHtmlActionListener(null, textController));

		mntmExportAnnotatedTexts = new JMenuItem("Export annotated text as an XML document");
		mnText.add(mntmExportAnnotatedTexts);
		mntmExportAnnotatedTexts.addActionListener(new ExportXmlActionListener(textController));

		JSeparator separator_2 = new JSeparator();
		mnText.add(separator_2);

		mntmModify = new JMenuItem("Modify Text");
		mnText.add(mntmModify);
		mntmModify.addActionListener(new ModifyTextActionListener(textController));

		return mnText;
	}

	public JMenu getMnText()
	{
		return mnText;
	}

	public CustomJSpinner getSpinner()
	{
		return spinner;
	}

	public JCheckBox getChckbxShowTextAnnotation()
	{
		return chckbxShowTextAnnotation;
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}

	public void setTextPane(JTextPane textPane)
	{
		this.textPane = textPane;
	}

	public Ntext getText()
	{
		return text;
	}

	public JLabel getLblnTus()
	{
		return lblnTus;
	}

	public ExportXmlDialog getExportXmlDialog()
	{
		return exportXmlDialog;
	}

	public void setExportXmlDialog(ExportXmlDialog exportXmlDialog)
	{
		this.exportXmlDialog = exportXmlDialog;
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public void setTextController(TextEditorShellController textController)
	{
		this.textController = textController;
	}

	public LocateDialog getLocateDialog()
	{
		return locateDialog;
	}

	public void setLocateDialog(LocateDialog locateDialog)
	{
		this.locateDialog = locateDialog;
	}

	public AlphabetDialog getAlphabetDialog()
	{
		return alphabetDialog;
	}

	public void setAlphabetDialog(AlphabetDialog alphabetDialog)
	{
		this.alphabetDialog = alphabetDialog;
	}

	public AmbiguitiesUnambiguitiesDialog getAmbiguitiesDialog()
	{
		return ambiguitiesDialog;
	}

	public void setAmbiguitiesDialog(AmbiguitiesUnambiguitiesDialog ambiguitiesDialog)
	{
		this.ambiguitiesDialog = ambiguitiesDialog;
	}

	public AmbiguitiesUnambiguitiesDialog getUnAmbiguitiesDialog()
	{
		return unAmbiguitiesDialog;
	}

	public void setUnAmbiguitiesDialog(AmbiguitiesUnambiguitiesDialog unAmbiguitiesDialog)
	{
		this.unAmbiguitiesDialog = unAmbiguitiesDialog;
	}

	public TokensDigramsDialog getTokensDialog()
	{
		return tokensDialog;
	}

	public void setTokensDialog(TokensDigramsDialog tokensDialog)
	{
		this.tokensDialog = tokensDialog;
	}

	public TokensDigramsDialog getDigramsDialog()
	{
		return digramsDialog;
	}

	public void setDigramsDialog(TokensDigramsDialog digramsDialog)
	{
		this.digramsDialog = digramsDialog;
	}

	public JMftPanel getHiddenPanel()
	{
		return hiddenPanel;
	}

	public MouseListener getRightClickListener()
	{
		return rightClickListener;
	}

	public JSplitPane getSplitPane()
	{
		return splitPane;
	}

	public JScrollPane getPanelScrollPane()
	{
		return panelScrollPane;
	}

	public void setPanelScrollPane(JScrollPane panelScrollPane)
	{
		this.panelScrollPane = panelScrollPane;
	}

	public JScrollPane getScrollPane()
	{
		return scrollPane;
	}

	public FindReplaceDialog getFindReplaceDialog()
	{
		return findReplaceDialog;
	}

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog)
	{
		this.findReplaceDialog = findReplaceDialog;
	}

	public UnitSelectionListener getUnitSelectionListener()
	{
		return unitSelectionListener;
	}

	public TASactionListener getTasActionListener()
	{
		return tasActionListener;
	}

	public void setTasActionListener(TASactionListener tasActionListener)
	{
		this.tasActionListener = tasActionListener;
	}

	public boolean isNewText()
	{
		return newText;
	}

	public void setSpinner(CustomJSpinner spinner)
	{
		this.spinner = spinner;
	}

	public void setUnitSelectionListener(UnitSelectionListener unitSelectionListener)
	{
		this.unitSelectionListener = unitSelectionListener;
	}

	public TextCommandInternalFrameListener getTextCommandInternalFrameListener()
	{
		return textCommandInternalFrameListener;
	}

	public JScrollPane getScrollList()
	{
		return scrollList;
	}

	public JTextArea getTxtInfo()
	{
		return txtInfo;
	}

	public JList getListOfResults()
	{
		return listOfResults;
	}
}