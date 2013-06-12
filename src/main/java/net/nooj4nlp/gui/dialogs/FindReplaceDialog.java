package net.nooj4nlp.gui.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.nooj4nlp.gui.actions.documents.FindReplaceActionListener;
import net.nooj4nlp.gui.actions.documents.FindReplaceCloseInternalFrame;
import net.nooj4nlp.gui.actions.documents.FindReplaceEvents;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.PropDefEditorShell;
import net.nooj4nlp.gui.shells.TextEditorShell;

/**
 * 
 * Dialog to find/replace patterns in text
 * 
 */
public class FindReplaceDialog extends JInternalFrame
{
	private static final long serialVersionUID = -6173419966017392181L;

	private JTextPane rtb;
	private FindReplaceEvents listener;
	private int labDicoContext;
	private JInternalFrame activeFrame;
	private JComboBox comboReplacePattern;
	private JComboBox comboFindPattern;
	private JRadioButton rdbtnExactPattern;
	private JButton btnNext;
	private JButton btnReplaceThenNext;

	/**
	 * Creates the frame.
	 * 
	 * @param activeFrame
	 *            currently selected frame (i.e. text to search)
	 * @param title
	 *            - title of dialog
	 */
	public FindReplaceDialog(JInternalFrame activeFrame, String title, int labDicoContext)
	{
		this.activeFrame = activeFrame;
		this.labDicoContext = labDicoContext;

		setClosable(true);
		setIconifiable(true);
		setBounds(100, 100, 558, 202);
		getContentPane().setLayout(null);
		setTitle(title);

		ButtonGroup grpPattern = new ButtonGroup();

		rdbtnExactPattern = new JRadioButton("Exact pattern");
		rdbtnExactPattern.setBounds(43, 7, 109, 23);
		rdbtnExactPattern.setActionCommand("Exact");
		rdbtnExactPattern.setSelected(true);
		getContentPane().add(rdbtnExactPattern);
		grpPattern.add(rdbtnExactPattern);

		JRadioButton rdbtnPerlPattern = new JRadioButton("PERL pattern");
		rdbtnPerlPattern.setBounds(154, 7, 109, 23);
		rdbtnPerlPattern.setActionCommand("Regex");
		getContentPane().add(rdbtnPerlPattern);
		grpPattern.add(rdbtnPerlPattern);

		JLabel lblFind = new JLabel("Find:");
		lblFind.setBounds(10, 41, 34, 14);
		getContentPane().add(lblFind);

		comboFindPattern = new JComboBox();
		comboFindPattern.setEditable(true);
		comboFindPattern.setBounds(43, 37, 488, 23);
		comboFindPattern.setSelectedItem("");
		JTextComponent comboTextField = (JTextComponent) comboFindPattern.getEditor().getEditorComponent();
		comboTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}
		});
		comboTextField.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					listener.find();
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});
		getContentPane().add(comboFindPattern);

		JButton btnFindFirst = new JButton("Find 1st occurrence");
		btnFindFirst.setBounds(10, 72, 127, 23);
		btnFindFirst.setActionCommand("Find");
		getContentPane().add(btnFindFirst);

		btnNext = new JButton("Next");
		btnNext.setBounds(147, 72, 55, 23);
		btnNext.setEnabled(false);
		btnNext.setActionCommand("Next");
		getContentPane().add(btnNext);

		JButton btnCountLines = new JButton("Count Lines");
		btnCountLines.setBounds(212, 72, 89, 23);
		btnCountLines.setToolTipText("Count all entries that match the pattern");
		btnCountLines.setActionCommand("Count");
		getContentPane().add(btnCountLines);

		JButton btnExtractLines = new JButton("Extract Lines");
		btnExtractLines.setBounds(311, 72, 101, 23);
		btnExtractLines.setToolTipText("Extract all entries that match the pattern");
		btnExtractLines.setActionCommand("Extract");
		getContentPane().add(btnExtractLines);

		JButton btnFilterOutLines = new JButton("Filter Out Lines");
		btnFilterOutLines.setBounds(422, 72, 109, 23);
		btnFilterOutLines.setToolTipText("Extract all entries that do not match the pattern");
		btnFilterOutLines.setActionCommand("Filter");
		getContentPane().add(btnFilterOutLines);

		JLabel lblReplaceWith = new JLabel("Replace with:");
		lblReplaceWith.setBounds(10, 106, 72, 14);
		getContentPane().add(lblReplaceWith);

		comboReplacePattern = new JComboBox();
		comboReplacePattern.setEditable(true);
		comboReplacePattern.setBounds(81, 102, 451, 23);
		comboReplacePattern.setSelectedItem("");
		JTextComponent comboReplaceField = (JTextComponent) comboReplacePattern.getEditor().getEditorComponent();
		comboReplaceField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				btnNext.setEnabled(false);
				btnReplaceThenNext.setEnabled(false);
			}
		});
		getContentPane().add(comboReplacePattern);

		btnReplaceThenNext = new JButton("Replace, then Next");
		btnReplaceThenNext.setBounds(10, 136, 165, 23);
		btnReplaceThenNext.setEnabled(false);
		btnReplaceThenNext.setActionCommand("Replace");
		getContentPane().add(btnReplaceThenNext);

		JButton btnReplaceAll = new JButton("Replace All");
		btnReplaceAll.setBounds(185, 136, 116, 23);
		btnReplaceAll.setActionCommand("ReplaceAll");
		getContentPane().add(btnReplaceAll);

		JButton btnReplaceAllFrom = new JButton("Replace All from rules in File");
		btnReplaceAllFrom.setBounds(311, 136, 220, 23);
		btnReplaceAllFrom.setToolTipText("Find,Replace rules are stored in a .dic file");
		btnReplaceAllFrom.setActionCommand("ReplaceAllFrom");
		getContentPane().add(btnReplaceAllFrom);

		String cls = activeFrame.getClass().getSimpleName();

		if (cls.equals("TextEditorShell"))
		{
			TextEditorShell textShell = (TextEditorShell) activeFrame;
			this.rtb = textShell.getTextPane();
		}
		else if (cls.equals("DictionaryEditorShell"))
		{
			DictionaryEditorShell dictionaryShell = (DictionaryEditorShell) activeFrame;
			this.rtb = dictionaryShell.getTextPane();
			btnCountLines.setVisible(true);
			btnExtractLines.setVisible(true);
			btnFilterOutLines.setVisible(true);
		}
		else if (cls.equals("FlexDescEditorShell"))
		{
			FlexDescEditorShell flexDescShell = (FlexDescEditorShell) activeFrame;
			this.rtb = flexDescShell.getTextPane();
			btnCountLines.setVisible(false);
			btnExtractLines.setVisible(false);
			btnFilterOutLines.setVisible(false);
		}
		else if (cls.equals("PropDefEditorShell"))
		{
			PropDefEditorShell propDefShell = (PropDefEditorShell) activeFrame;
			this.rtb = propDefShell.getTextPane();
			btnCountLines.setVisible(false);
			btnExtractLines.setVisible(false);
			btnFilterOutLines.setVisible(false);
		}
		else if (cls.equals("GrammarEditorShell"))
		{
			this.rtb = null;
			btnCountLines.setVisible(false);
			btnExtractLines.setVisible(false);
			btnFilterOutLines.setVisible(false);
		}

		if (labDicoContext > 0)
		{
			btnReplaceThenNext.setVisible(false);
			btnReplaceAllFrom.setVisible(false);
			btnFindFirst.setVisible(false);
			btnCountLines.setVisible(false);
			btnNext.setVisible(false);

			if (labDicoContext == 1) // replace
			{
				btnExtractLines.setVisible(false);
				btnFilterOutLines.setVisible(false);
			}
			else if (labDicoContext == 2) // extract
			{
				comboReplacePattern.setVisible(false);
				lblReplaceWith.setVisible(false);
				btnReplaceAll.setVisible(false);
			}
		}

		this.listener = new FindReplaceEvents(this);

		FindReplaceActionListener actionListener = new FindReplaceActionListener(listener);
		btnFindFirst.addActionListener(actionListener);
		btnNext.addActionListener(actionListener);
		btnReplaceThenNext.addActionListener(actionListener);
		btnReplaceAll.addActionListener(actionListener);
		btnExtractLines.addActionListener(actionListener);
		btnFilterOutLines.addActionListener(actionListener);
		btnCountLines.addActionListener(actionListener);
		btnReplaceAllFrom.addActionListener(actionListener);

		this.addInternalFrameListener(new FindReplaceCloseInternalFrame(activeFrame));
	}

	public JTextPane getRtb()
	{
		return rtb;
	}

	public void setRtb(JTextPane rtb)
	{
		this.rtb = rtb;
	}

	public JInternalFrame getActiveFrame()
	{
		return activeFrame;
	}

	public JComboBox getComboReplacePattern()
	{
		return comboReplacePattern;
	}

	public JComboBox getComboFindPattern()
	{
		return comboFindPattern;
	}

	public JRadioButton getRdbtnExactPattern()
	{
		return rdbtnExactPattern;
	}

	public JButton getBtnNext()
	{
		return btnNext;
	}

	public JButton getBtnReplaceThenNext()
	{
		return btnReplaceThenNext;
	}

	public int getLabDicoContext()
	{
		return labDicoContext;
	}

	public FindReplaceEvents getListener()
	{
		return listener;
	}
}