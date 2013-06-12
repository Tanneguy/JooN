package net.nooj4nlp.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.nooj4nlp.controller.DictionaryDialog.CompileActionListener;
import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.controller.DictionaryDialog.EditActionListener;
import net.nooj4nlp.controller.DictionaryDialog.EnrichActionListener;
import net.nooj4nlp.controller.DictionaryDialog.InflectActionListener;
import net.nooj4nlp.controller.DictionaryDialog.SetActionListener;
import net.nooj4nlp.controller.DictionaryDialog.SortActionListener;
import net.nooj4nlp.gui.actions.shells.construct.FindReplaceActionListener;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Dictionary lab dialog
 * 
 */
public class DictionaryDialog extends JInternalFrame
{
	private static final long serialVersionUID = -2540842726385399575L;

	private DictionaryDialogController controller;

	/**
	 * Creates the frame.
	 */
	public DictionaryDialog()
	{
		setClosable(true);
		setIconifiable(true);
		setBounds(400, 100, 830, 500);
		getContentPane().setLayout(null);

		JPanel pnlDictionaryFile = new JPanel();
		pnlDictionaryFile.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlDictionaryFile.setBounds(10, 11, 794, 105);
		getContentPane().add(pnlDictionaryFile);
		pnlDictionaryFile.setLayout(null);

		JLabel lblEnterANooj = new JLabel("Enter a Nooj Dictionary file (*.dic):");
		lblEnterANooj.setBounds(10, 11, 232, 14);
		pnlDictionaryFile.add(lblEnterANooj);

		JTextField txtDictionaryName = new JTextField();
		txtDictionaryName.setBounds(10, 35, 676, 23);
		txtDictionaryName.setFont(new Font(txtDictionaryName.getFont().getName(), txtDictionaryName.getFont()
				.getStyle(), 11));
		pnlDictionaryFile.add(txtDictionaryName);

		JButton btnNewButton = new JButton("Set");
		btnNewButton.setBounds(716, 35, 58, 23);
		pnlDictionaryFile.add(btnNewButton);

		JButton btnEdit = new JButton("Edit");
		btnEdit.setBounds(10, 69, 58, 23);
		pnlDictionaryFile.add(btnEdit);

		JButton btnSort = new JButton("Sort");
		btnSort.setBounds(98, 69, 58, 23);
		pnlDictionaryFile.add(btnSort);

		JButton btnReplace = new JButton("Replace...");
		btnReplace.setBounds(166, 69, 103, 23);
		btnReplace.setActionCommand("LabDicoReplace");
		pnlDictionaryFile.add(btnReplace);

		JButton btnExtract = new JButton("Extract...");
		btnExtract.setBounds(279, 69, 79, 23);
		btnExtract.setActionCommand("LabDicoExtract");
		pnlDictionaryFile.add(btnExtract);

		JButton btnEnrich = new JButton("Enrich");
		btnEnrich.setBounds(387, 69, 68, 23);
		pnlDictionaryFile.add(btnEnrich);

		JButton btnInflect = new JButton("Inflect");
		btnInflect.setFont(new Font("Tahoma", Font.ITALIC, 11));
		btnInflect.setBounds(465, 69, 79, 23);
		pnlDictionaryFile.add(btnInflect);

		JButton btnCompile = new JButton("Compile");
		btnCompile.setBounds(554, 69, 79, 23);
		pnlDictionaryFile.add(btnCompile);

		JCheckBox chckbxCheckAgreement = new JCheckBox("Check Agreement");
		chckbxCheckAgreement.setBounds(638, 69, 156, 23);
		chckbxCheckAgreement.setSelected(true);
		pnlDictionaryFile.add(chckbxCheckAgreement);

		JPanel pnlDisplayDictionary = new JPanel();
		TitledBorder titledBorder = new TitledBorder(null, "Display Dictionary:", TitledBorder.LEADING,
				TitledBorder.TOP, null, null);
		pnlDisplayDictionary.setBorder(titledBorder);
		pnlDisplayDictionary.setBounds(10, 127, 794, 312);
		getContentPane().add(pnlDisplayDictionary);
		pnlDisplayDictionary.setLayout(new BorderLayout(0, 0));

		JTextPane txtDictionary = new JTextPane();
		txtDictionary.setBackground(SystemColor.control);
		txtDictionary.setEditable(false);

		// A workaround to avoid word wrapping
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(txtDictionary);

		JScrollPane scrollPane = new JScrollPane(noWrapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlDisplayDictionary.add(scrollPane, BorderLayout.CENTER);

		controller = new DictionaryDialogController(txtDictionaryName, chckbxCheckAgreement, pnlDisplayDictionary,
				txtDictionary, btnNewButton, btnEdit, btnInflect, btnCompile, titledBorder);
		btnNewButton.addActionListener(new SetActionListener(this, controller));
		btnEdit.addActionListener(new EditActionListener(controller));
		btnSort.addActionListener(new SortActionListener(controller));
		btnReplace.addActionListener(new FindReplaceActionListener(Launcher.getDesktopPane()));
		btnExtract.addActionListener(new FindReplaceActionListener(Launcher.getDesktopPane()));
		btnEnrich.addActionListener(new EnrichActionListener(controller));
		btnInflect.addActionListener(new InflectActionListener(controller));
		btnCompile.addActionListener(new CompileActionListener(controller));

		this.addInternalFrameListener(new InternalFrameListener()
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
				((DictionaryDialog) e.getInternalFrame()).getController().setFindReplaceDialog(null);
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

	public DictionaryDialogController getController()
	{
		return controller;
	}
}