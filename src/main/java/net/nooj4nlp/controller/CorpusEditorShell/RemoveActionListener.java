package net.nooj4nlp.controller.CorpusEditorShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Corpus;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class for implementation of removing *.jnot files from corpus.
 * 
 */

public class RemoveActionListener implements ActionListener
{
	private CorpusEditorShellController controller;
	private Corpus corpus;
	private String dirPath;

	public RemoveActionListener(CorpusEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// number of table row results of corpus
		int sizeOfListedFiles = controller.getTableTexts().getModel().getRowCount();
		// actual number of selected files to remove
		int[] indices = controller.getTableTexts().getSelectedRows();

		// convert to row model if sorted
		for (int k = 0; k < indices.length; k++)
			indices[k] = controller.getTableTexts().convertRowIndexToModel(indices[k]);
		Arrays.sort(indices);

		if (sizeOfListedFiles > 0 && indices.length > 0)
		{
			dirPath = controller.getFullPath() + Constants.DIRECTORY_SUFFIX;
			corpus = controller.getCorpus();
			// finding model values of table...
			TableModel model = controller.getTableTexts().getModel();
			int length = indices.length;
			// nested for loops; iterating through list of files until we find selected one
			// iterating is done backwards to avoid possible conflicts
			for (int j = sizeOfListedFiles - 1; j > -1; j--)
			{
				for (int i = length - 1; i > -1; i--)
				{
					if (indices[i] == j)
					{
						// if succeeded, delete the file from system, remove from list of files and break inner for loop
						File notFile = new File(dirPath + System.getProperty("file.separator")
								+ ((DefaultTableModel) model).getValueAt(j, 0).toString() + "."
								+ Constants.JNOT_EXTENSION);
						try
						{
							notFile.delete();
							((DefaultTableModel) model).removeRow(j);
							corpus.listOfFileTexts.remove(j);
						}
						catch (SecurityException f)
						{
							f.printStackTrace();
						}
						break;
					}
				}
			}
			// update changes
			updateEditCorpusWindow();
			corpus.nbOfChars = 0;
			try
			{
				corpus.saveIn(dirPath);
			}
			catch (IOException f)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_SAVE_CORPUS,
						Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			controller.setModified(true);
			corpus.annotations = null;
		}
	}

	/**
	 * Function for updating "Edit Corpus" window.
	 * 
	 * @param c
	 *            - controller from which we'll extract shell
	 * 
	 */
	private void updateEditCorpusWindow()
	{
		controller.updateTextPaneStats();
		// if corpus was already updated, but not saved, don't add "modified" tag again
		controller.updateTitle();
	}
}
