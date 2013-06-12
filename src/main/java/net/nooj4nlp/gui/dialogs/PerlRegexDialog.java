package net.nooj4nlp.gui.dialogs;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.PerlRegexDialog.ClearActionListener;
import net.nooj4nlp.controller.PerlRegexDialog.LocateActionListener;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.ColoredJButtonUI;

/**
 * 
 * Perl regular expression lab dialog
 * 
 */
public class PerlRegexDialog extends JInternalFrame
{
	private static final long serialVersionUID = 5008254469853137656L;

	/**
	 * Creates the frame.
	 */
	public PerlRegexDialog(JDesktopPane desktopPane)
	{
		setTitle("Perl Lab");
		setClosable(true);
		setIconifiable(true);
		getContentPane().setFont(new Font("Tahoma", Font.BOLD, 18));
		setBounds(400, 100, 480, 550);
		getContentPane().setLayout(new MigLayout("insets 3", "[grow][80!][30!][30!][30!][30!]", "[145!][310!][25!]"));

		JPanel pnlPattern = new JPanel();
		pnlPattern.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Pattern is:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(pnlPattern, "cell 0 0, span 6");
		pnlPattern.setLayout(new MigLayout("insets 5", "[415!]", "[25!][25!][40!]"));

		ButtonGroup grpPattern = new ButtonGroup();

		JRadioButton rdbtnString = new JRadioButton("a string of characters:");
		pnlPattern.add(rdbtnString, "cell 0 0");

		JRadioButton rdbtnPerl = new JRadioButton("a PERL-style regular expression:");
		pnlPattern.add(rdbtnPerl, "cell 0 1");

		grpPattern.add(rdbtnPerl);
		grpPattern.add(rdbtnString);

		JComboBox comboPattern = new JComboBox();
		comboPattern.setEditable(true);
		pnlPattern.add(comboPattern, "cell 0 2, wmin 400, alignx right");

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Look for Pattern in Text:", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		getContentPane().add(panel_1, "cell 0 1, span 6");
		panel_1.setLayout(new MigLayout("insets 5", "[415!]", "[270!]"));

		JTextPane textPane = new JTextPane();
		textPane.setBorder(BorderFactory.createLoweredBevelBorder());

		JScrollPane scrollPane = new JScrollPane(textPane);
		panel_1.add(scrollPane, "cell 0 0, grow");

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ClearActionListener(textPane));
		getContentPane().add(btnClear, "cell 1 2, hmin 25");

		JButton btnN = new JButton("N");
		btnN.setName("btnN");
		btnN.setUI(new ColoredJButtonUI(Constants.NOOJ_RED_BUTTON_COLOR, Constants.NOOJ_PRESSED_RED_BUTTON_COLOR));
		btnN.addActionListener(new LocateActionListener(textPane, btnN, rdbtnPerl, rdbtnString, comboPattern,
				desktopPane));
		getContentPane().add(btnN, "cell 2 2");

		JButton btnO = new JButton("O");
		btnO.setName("btnO");
		btnO.setUI(new ColoredJButtonUI(Constants.NOOJ_GREEN_BUTTON_COLOR, Constants.NOOJ_PRESSED_GREEN_BUTTON_COLOR));
		btnO.addActionListener(new LocateActionListener(textPane, btnO, rdbtnPerl, rdbtnString, comboPattern,
				desktopPane));
		getContentPane().add(btnO, "cell 3 2");

		JButton btnO_1 = new JButton("O");
		btnO_1.setName("btnO_1");
		btnO_1.setUI(new ColoredJButtonUI(Constants.NOOJ_MAGENTA_BUTTON_COLOR,
				Constants.NOOJ_PRESSED_MAGENTA_BUTTON_COLOR));
		btnO_1.addActionListener(new LocateActionListener(textPane, btnO_1, rdbtnPerl, rdbtnString, comboPattern,
				desktopPane));
		getContentPane().add(btnO_1, "cell 4 2");

		JButton btnJ = new JButton("J");
		btnJ.setName("btnJ");
		btnJ.setUI(new ColoredJButtonUI(Constants.NOOJ_BLUE_BUTTON_COLOR, Constants.NOOJ_PRESSED_BLUE_BUTTON_COLOR));
		btnJ.addActionListener(new LocateActionListener(textPane, btnJ, rdbtnPerl, rdbtnString, comboPattern,
				desktopPane));
		getContentPane().add(btnJ, "cell 5 2, wmin 30");
	}
}
