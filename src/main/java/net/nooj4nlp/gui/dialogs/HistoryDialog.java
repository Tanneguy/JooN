package net.nooj4nlp.gui.dialogs;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTree;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.HistoryDialog.BackActionListener;
import net.nooj4nlp.controller.HistoryDialog.EndActionListener;
import net.nooj4nlp.controller.HistoryDialog.ForwardActionListener;
import net.nooj4nlp.controller.HistoryDialog.HistoryDialogController;
import net.nooj4nlp.controller.HistoryDialog.SelectionListener;
import net.nooj4nlp.controller.HistoryDialog.StartActionListener;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class HistoryDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1L;
	public JTree tv;
	private HistoryDialogController controller;

	public HistoryDialog(GrammarEditorShell shell)
	{
		setTitle("History");
		setBounds(100, 100, 280, 440);
		setClosable(true);
		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[][grow,fill]"));
		JPanel header = new JPanel();
		header.setLayout(new MigLayout("ins 7", "[][][][]", "[]"));

		getContentPane().add(header, "cell 0 0");

		JButton buttonStart = new JButton("|<");
		header.add(buttonStart, "cell 0 0");

		JButton buttonBack = new JButton("<");
		header.add(buttonBack, "cell 1 0");

		JButton buttonNext = new JButton(">");
		header.add(buttonNext, "cell 2 0");

		JButton buttonEnd = new JButton(">|");
		header.add(buttonEnd, "cell 3 0");

		tv = new JTree();
		tv.setRootVisible(false);
		getContentPane().add(tv, "cell 0 1,grow");

		controller = new HistoryDialogController(this);

		buttonStart.addActionListener(new StartActionListener(shell, controller));
		buttonBack.addActionListener(new BackActionListener(shell, controller));
		buttonNext.addActionListener(new ForwardActionListener(shell, controller));
		buttonEnd.addActionListener(new EndActionListener(shell, controller));
		tv.addTreeSelectionListener(new SelectionListener(shell, controller));
	}

	public HistoryDialogController getController()
	{
		return controller;
	}

}