package net.nooj4nlp.controller.preferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;



public class EditLexActionListener implements ActionListener
{
	
	
	
	

	private PreferencesDialog dialog = null;
	private JLabel lLexDoc = null;
	private JLabel lSynDoc = null;
	private boolean lexicalContext;

	public EditLexActionListener(PreferencesDialog dialog, JLabel lLexDoc, JLabel lSynDoc, boolean lexicalContext)
	{
		this.dialog = dialog;
		this.lLexDoc = lLexDoc;
		this.lSynDoc = lSynDoc;
		this.lexicalContext = lexicalContext;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String fullname = "";

		if (lexicalContext)
			fullname = lLexDoc.getText();
		else
			fullname = lSynDoc.getText();

		if (fullname == "")
			return;

		File f = new File(fullname);

		if (!f.exists())
			return;

		String ext = FilenameUtils.getExtension(fullname);

		try
		{
			if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION) || ext.equalsIgnoreCase(".bin"))
			{
				dialog.setVisible(false);
				String fullname2 = FilenameUtils.getFullPath(fullname) + System.getProperty("file.separator")
						+ FilenameUtils.getBaseName(fullname) + ".dic";
				File f1 = new File(fullname2);
				if (!f1.exists())
				{
					JOptionPane.showMessageDialog(dialog, "Cannot find dictionary source file " + fullname2);
					dialog.setVisible(true);
					return;
				}

				JOptionPane.showMessageDialog(dialog,
						"Remember to recompile the dictionary in Lab after having edited it");

				CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
				String lg = FilenameUtils.getPath(fullname) + FilenameUtils.getBaseName(fullname) + "."
						+ Constants.JNOG_EXTENSION;
				File f2 = new File(lg);
				if (f2.exists())
				{
					DictionaryEditorShell formDictionary = new DictionaryEditorShell();
					boolean success = formDictionary.getController().loadFromFile(fullname2);
					if (!success)
					{
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
						JOptionPane.showMessageDialog(dialog, "Dictionary file corrupted? Cannot load it");
						return;
					}
					Launcher.getDesktopPane().add(formDictionary);
					formDictionary.setVisible(true);
					dialog.setVisible(false);

					GrammarEditorShell gEditor = new GrammarEditorShell(lg);
					Launcher.getDesktopPane().add(gEditor);
					gEditor.getController().LoadGrammar(lg, false);
					if (gEditor.getController().grammar == null)
					{
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
						dialog.setVisible(true);
						JOptionPane.showMessageDialog(dialog, "Grammar file corrupted? Cannot load it");
						gEditor.setVisible(true);
						
						
						
						
					}
				}
				else
				{
					DictionaryEditorShell formDictionary = new DictionaryEditorShell();
					boolean success = formDictionary.getController().loadFromFile(fullname2);
					if (!success)
					{
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
						JOptionPane.showMessageDialog(dialog, "Dictionary file corrupted? Cannot load it");
						return;
					}
					Launcher.getDesktopPane().add(formDictionary);
					formDictionary.setVisible(true);
					dialog.setVisible(false);
				}
			}
			else if (ext.equals(Constants.JNOM_EXTENSION) || ext.equals(".grm"))
			{
				String lg = FilenameUtils.getPath(fullname) + FilenameUtils.getBaseName(fullname) + "."
						+ Constants.JNOG_EXTENSION;
				File f2 = new File(lg);
				if (f2.exists())
				{
					dialog.setVisible(false);
					GrammarEditorShell gEditor = new GrammarEditorShell(lg);
					Launcher.getDesktopPane().add(gEditor);
					gEditor.getController().LoadGrammar(lg, false);
					if (gEditor.getController().grammar == null)
					{
						dialog.setVisible(true);
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
						return;
					}
					gEditor.setVisible(true);
					dialog.setVisible(false);
					
				}
			}

			else if (ext.equals(Constants.JNOG_EXTENSION) || ext.equals(".grm"))
			{
				dialog.setVisible(false);
				GrammarEditorShell gEditor = new GrammarEditorShell(fullname);
				Launcher.getDesktopPane().add(gEditor);
				gEditor.getController().LoadGrammar(fullname, false);
				if (gEditor.getController().grammar == null)
				{
					dialog.setVisible(true);
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
					return;
				}
				gEditor.setVisible(true);
				dialog.setVisible(false);
				
				
				
				
			}
		}

		finally
		{
			CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
		}
	}
}