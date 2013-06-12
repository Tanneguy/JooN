package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.dialogs.TextCorpusDialog;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * ActionListener that opens the New Text/Corpus dialog
 * 
 */
public class NewTextCorpusActionListener implements ActionListener
{
	private boolean isCorpus;
	private TextCorpusDialog dialog;
	private JInternalFrame frame;
	private static String chosenFileName;

	/**
	 * 
	 * @param corpus
	 *            true initializes a new corpus dialog, false creates a new text dialog
	 */
	public NewTextCorpusActionListener(boolean corpus, JInternalFrame frame)
	{
		isCorpus = corpus;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (isCorpus)
		{
			JFileChooser jFileChooser = Launcher.getOpenCorpusChooser();
			jFileChooser.setMultiSelectionEnabled(false);
			jFileChooser.setAcceptAllFileFilterUsed(false);

			jFileChooser.setDialogTitle("Create New Corpus");
			int result = jFileChooser.showSaveDialog(frame);

			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selectedNOCFile = jFileChooser.getSelectedFile();
				chosenFileName = FilenameUtils.removeExtension(selectedNOCFile.getName());

				if (selectedNOCFile.exists())
				{
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int affirm = JOptionPane.showConfirmDialog(Launcher.getDesktopPane(),
							Constants.DELETE_CORPUS_MESSAGE, Constants.NOOJ_APPLICATION_NAME, dialogButton);
					if (affirm == JOptionPane.NO_OPTION)
						return;
				}
				dialog = new TextCorpusDialog(true, false, frame, selectedNOCFile);

				dialog.setModal(true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		}
		else
		{
			chosenFileName = "";
			

			dialog = new TextCorpusDialog(false, false, frame, null);

			dialog.setModal(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
	}

	public static String getChosenFileName()
	{
		return chosenFileName;
	}
}