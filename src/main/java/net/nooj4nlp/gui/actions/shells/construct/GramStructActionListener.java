package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.GramStructShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;

public class GramStructActionListener implements ActionListener
{

	private GramStructShell formGramStruct;
	private GrammarEditorShell editor;

	public GramStructActionListener(GrammarEditorShell grammarEditorShell)
	{
		formGramStruct = grammarEditorShell.getController().formGramStruct;
		this.editor = grammarEditorShell;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Launcher.getDesktopPane().add(formGramStruct);
		formGramStruct.setVisible(true);
		formGramStruct.setTitle("Structure of " + FilenameUtils.getName(this.editor.getTitle()));
		formGramStruct.getController().visit(formGramStruct.tvGraphs);
		formGramStruct.getController().expandAll(true);

		
	}
}