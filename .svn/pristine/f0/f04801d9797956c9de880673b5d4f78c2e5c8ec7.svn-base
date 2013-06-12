package net.nooj4nlp.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.controller.ProduceParaphrasesController.ProduceParaphrasesController;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class ProduceParaphrasesDialog extends JInternalFrame
{
	private static final long serialVersionUID = 8188599698282082251L;

	private JTextField textField;
	private JTextField txtpassiveneg;
	private boolean txtpassivenegEnabled;
	private JRadioButton rdbtnNameATransformation;
	private JCheckBox chckbxDisplayConstraintFailures;
	private JCheckBox chckbxDisplayLexemes;
	private ProduceParaphrasesController controller;

	public ProduceParaphrasesDialog(GrammarEditorShell gShell)
	{
		setBounds(100, 100, 430, 296);
		setClosable(true);

		setTitle("Transformation for grammar: " + (new File(gShell.getController().getFullName())).getName());

		getContentPane().setLayout(null);

		JLabel lblEnterAPhrase = new JLabel("Enter a phrase to parse:");
		lblEnterAPhrase.setBounds(10, 11, 152, 14);
		getContentPane().add(lblEnterAPhrase);

		textField = new JTextField();
		textField.setBounds(10, 31, 354, 20);
		getContentPane().add(textField);
		textField.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Transformations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 62, 356, 116);
		getContentPane().add(panel);
		panel.setLayout(null);

		ButtonGroup transformationGroup = new ButtonGroup();

		rdbtnNameATransformation = new JRadioButton("Name a transformation");
		rdbtnNameATransformation.setBounds(6, 20, 191, 23);
		transformationGroup.add(rdbtnNameATransformation);
		panel.add(rdbtnNameATransformation);

		txtpassiveneg = new JTextField();
		txtpassiveneg.setEnabled(false);
		txtpassiveneg.setText("+Passive-Neg");
		txtpassiveneg.setBounds(27, 50, 300, 23);
		panel.add(txtpassiveneg);
		txtpassiveneg.setColumns(10);

		txtpassivenegEnabled = false;

		JRadioButton rdbtnPerformAllTransformations = new JRadioButton("Perform All Transformations");
		rdbtnPerformAllTransformations.setBounds(6, 80, 211, 23);
		rdbtnPerformAllTransformations.setSelected(true);
		transformationGroup.add(rdbtnPerformAllTransformations);
		panel.add(rdbtnPerformAllTransformations);

		rdbtnNameATransformation.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				txtpassivenegEnabled = !txtpassivenegEnabled;
				txtpassiveneg.setEnabled(txtpassivenegEnabled);

				if (txtpassivenegEnabled)
				{
					txtpassiveneg.grabFocus();
					txtpassiveneg.selectAll();
				}
			}
		});

		chckbxDisplayLexemes = new JCheckBox("display lexemes");
		chckbxDisplayLexemes.setBounds(10, 185, 146, 23);
		getContentPane().add(chckbxDisplayLexemes);

		chckbxDisplayConstraintFailures = new JCheckBox("display constraint failures");
		chckbxDisplayConstraintFailures.setBounds(10, 211, 204, 23);
		getContentPane().add(chckbxDisplayConstraintFailures);

		JButton btnProduce = new JButton("Produce Paraphrases");
		btnProduce.setBounds(229, 189, 155, 45);
		getContentPane().add(btnProduce);

		controller = new ProduceParaphrasesController(this, gShell);

		btnProduce.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.produceParaphrases();
			}
		});

		textField.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					controller.produceParaphrases();
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});
	}

	public ProduceParaphrasesController getController()
	{
		return controller;
	}

	public JTextField getTextField()
	{
		return textField;
	}

	public JRadioButton getRdbtnNameATransformation()
	{
		return rdbtnNameATransformation;
	}

	public JTextField getTxtpassiveneg()
	{
		return txtpassiveneg;
	}

	public JCheckBox getChckbxDisplayConstraintFailures()
	{
		return chckbxDisplayConstraintFailures;
	}

	public JCheckBox getChckbxDisplayLexemes()
	{
		return chckbxDisplayLexemes;
	}
}