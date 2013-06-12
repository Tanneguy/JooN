package net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.ExportXmlDialog;

public class XmlAnnotationsListener implements ActionListener
{
	private ExportXmlDialog exportXmlDialog;

	public XmlAnnotationsListener(ExportXmlDialog exportXmlDialog)
	{
		super();
		this.exportXmlDialog = exportXmlDialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (exportXmlDialog.getRdbtnAllSyntax().isSelected())
		{
			exportXmlDialog.getCobxXmlAnnotations().setEnabled(false);
		}
		else if (exportXmlDialog.getRdbtnList().isSelected())
		{
			exportXmlDialog.getCobxXmlAnnotations().setEnabled(true);
		}
	}
}
