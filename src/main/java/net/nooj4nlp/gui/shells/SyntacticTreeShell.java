package net.nooj4nlp.gui.shells;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.ConcordanceShell.ConcordanceShellController;
import net.nooj4nlp.controller.SyntacticTreeShell.MinusActionListener;
import net.nooj4nlp.controller.SyntacticTreeShell.OptionsActionListner;
import net.nooj4nlp.controller.SyntacticTreeShell.PlusActionListener;
import net.nooj4nlp.controller.SyntacticTreeShell.SyntacticTreeShellController;
import net.nooj4nlp.controller.SyntacticTreeShell.TextFieldActionListener;
import net.nooj4nlp.gui.components.JPOptions;
import net.nooj4nlp.gui.components.JSTree;

public class SyntacticTreeShell extends JInternalFrame
{
	private static final long serialVersionUID = 1L;
	private JTextField tbUnitNumber;
	private SyntacticTreeShellController controller;
	private JSTree panel1;
	private ConcordanceShellController concordanceShellController;
	private JLabel lConcEntries;
	private JLabel label1;
	private JRadioButton rbDerivationTree;
	private JCheckBox cbDisplayAll;
	private JPOptions pOptions;

	public SyntacticTreeShell(ConcordanceShellController concordanceController)
	{
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setResizable(true);
		setTitle("Syntactic Analysis");
		setBounds(100, 100, 717, 530);
		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[120!,grow][grow,fill]"));

		pOptions = new JPOptions();
		pOptions.setBackground(SystemColor.control);
		getContentPane().add(pOptions, "grow, wrap");
		pOptions.setLayout(null);

		JButton btnPrev = new JButton("-");
		btnPrev.setBounds(10, 10, 40, 23);
		pOptions.add(btnPrev);

		JButton btnNext = new JButton("+");
		btnNext.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNext.setBounds(107, 10, 40, 23);
		pOptions.add(btnNext);

		lConcEntries = new JLabel("Concordance Entry");
		lConcEntries.setBounds(157, 14, 114, 14);
		pOptions.add(lConcEntries);

		JLabel lblNewLabel_1 = new JLabel("Display:");
		lblNewLabel_1.setBounds(20, 44, 46, 14);
		pOptions.add(lblNewLabel_1);

		JRadioButton rbStructuralTree = new JRadioButton("Structural Tree");
		rbStructuralTree.setSelected(true);
		rbStructuralTree.setBounds(82, 40, 109, 23);
		pOptions.add(rbStructuralTree);

		rbDerivationTree = new JRadioButton("Derivation Tree");
		rbDerivationTree.setBounds(193, 40, 109, 23);
		pOptions.add(rbDerivationTree);

		ButtonGroup group = new ButtonGroup();
		group.add(rbStructuralTree);
		group.add(rbDerivationTree);

		cbDisplayAll = new JCheckBox("Lexeme, Lemma, Linguistic Information");
		cbDisplayAll.setSelected(true);
		cbDisplayAll.setBounds(82, 66, 220, 23);
		pOptions.add(cbDisplayAll);

		label1 = new JLabel("New label");
		label1.setBackground(SystemColor.activeCaptionText);
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setBounds(10, 95, 364, 35);
		label1.setOpaque(true);
		pOptions.add(label1);

		tbUnitNumber = new JTextField();
		tbUnitNumber.setText("number");
		tbUnitNumber.setBounds(60, 11, 37, 20);
		pOptions.add(tbUnitNumber);
		tbUnitNumber.setColumns(10);
		JPanel pBackground = new JPanel();
		getContentPane().add(pBackground, "grow");
		pBackground.setLayout(null);

		panel1 = new JSTree();
		panel1.setBounds(10, 0, 10, 10);
		pBackground.add(panel1);

		controller = new SyntacticTreeShellController(this);
		pOptions.setController(controller);
		panel1.setController(controller);
		btnPrev.addActionListener(new MinusActionListener(controller));
		btnNext.addActionListener(new PlusActionListener(controller));
		rbStructuralTree.addActionListener(new OptionsActionListner(controller));
		rbDerivationTree.addActionListener(new OptionsActionListner(controller));
		cbDisplayAll.addActionListener(new OptionsActionListner(controller));
		tbUnitNumber.addActionListener(new TextFieldActionListener(controller));
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
				concordanceShellController.setSyntacticTreeShell(null);
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

	public SyntacticTreeShellController getController()
	{
		return controller;
	}

	public JSTree getPanel1()
	{
		return panel1;
	}

	public JTextField getTbUnitNumber()
	{
		return tbUnitNumber;
	}

	public JLabel getlConcEntries()
	{
		return lConcEntries;
	}

	public ConcordanceShellController getConcordanceShellController()
	{
		return concordanceShellController;
	}

	public JLabel getLabel1()
	{
		return label1;
	}

	public JRadioButton getRbDerivationTree()
	{
		return rbDerivationTree;
	}

	public JCheckBox getCbDisplayAll()
	{
		return cbDisplayAll;
	}
}