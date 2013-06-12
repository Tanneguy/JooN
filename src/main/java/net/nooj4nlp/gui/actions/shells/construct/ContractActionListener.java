package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ContractShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

/**
 * 
 * ActionListener that opens the Contract shell
 * 
 */
public class ContractActionListener implements ActionListener
{
	private GrammarEditorShell grammarShell;

	public ContractActionListener(GrammarEditorShell g)
	{
		grammarShell = g;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		ContractShell Contract = new ContractShell(grammarShell, grammarShell.getController().grammar,
				grammarShell.getController().lan);
		Launcher.getDesktopPane().add(Contract);
		Contract.setVisible(true);
	}
}
