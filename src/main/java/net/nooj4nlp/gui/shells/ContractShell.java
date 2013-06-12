package net.nooj4nlp.gui.shells;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.ContractShell.ContractShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Contract shell, a.k.a. FormGramExample
 * 
 */
public class ContractShell extends JInternalFrame
{
	private static final long serialVersionUID = -3686247306949987334L;

	private ContractShellController controller;

	/**
	 * Create the frame.
	 */
	public ContractShell(GrammarEditorShell editor, Grammar g, Language l)
	{
		setIconifiable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 539, 327);
		setTitle("Contract");

		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[][grow,fill]"));

		JPanel header = new JPanel();
		header.setLayout(new MigLayout("ins 7", "[100::][]", "[]"));

		getContentPane().add(header, "wrap");

		JButton btnCheck = new JButton("Check");
		header.add(btnCheck, "");

		JTextPane txtConsole = new JTextPane();
		txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(txtConsole);
		getContentPane().add(scrollPane, "grow");

		txtConsole.setText("# NooJ Java v1.0\n# Enter examples, *counter examples and #comments\n#");

		controller = new ContractShellController(editor, g, l, txtConsole, this);

		btnCheck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					controller.checkContract();
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				}
				catch (ClassNotFoundException e1)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public ContractShellController getController()
	{
		return controller;
	}
}