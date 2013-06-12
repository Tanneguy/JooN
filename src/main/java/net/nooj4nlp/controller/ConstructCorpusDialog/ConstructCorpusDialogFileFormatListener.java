package net.nooj4nlp.controller.ConstructCorpusDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConstructCorpusDialogFileFormatListener implements ActionListener
{
	private ConstructCorpusDialogController controller;

	public ConstructCorpusDialogFileFormatListener(ConstructCorpusDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// List of formats is enabled only if Other raw text formats radio button is clicked
		if (this.controller.getRdbtnOtherRawText().isSelected())
		{
			this.controller.getListTextFormats().setEnabled(true);
			this.controller.getFldPerlExpr().setEnabled(true);
		}
		else
		{
			if (this.controller.getRdbtnPdf().isSelected())
			{
				this.controller.getPnlPerlExpr().setEnabled(false);
				this.controller.getFldPerlExpr().setEnabled(false);
			}
			else
			{
				this.controller.getPnlPerlExpr().setEnabled(true);
				this.controller.getFldPerlExpr().setEnabled(true);
			}

			this.controller.getListTextFormats().setEnabled(false);
			this.controller.getListTextFormats().clearSelection();
		}
	}
}
