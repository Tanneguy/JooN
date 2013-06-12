package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.gui.dialogs.DictionaryPropDefDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DictionaryEditorShell;

/**
 * 
 * ActionListener that opens an empty dictionary editor shell
 * 
 */
public class NewDictionaryActionListener implements ActionListener
{

	private JList lstLang;
	private DictionaryPropDefDialog dialog;

	/**
	 * @param lstLang
	 *            language list used to get the selected language (at actionPerformed() time)
	 */
	public NewDictionaryActionListener(JList lstLang, DictionaryPropDefDialog dialog)
	{
		this.lstLang = lstLang;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String lang = (String) lstLang.getSelectedValue();
		if (lang == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please, select language!", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String dir = org.apache.commons.io.FilenameUtils.concat(Paths.docDir, lang);
		File theDir = new File(dir);
		if (!theDir.exists())
			theDir.mkdir();
		String dir2 = org.apache.commons.io.FilenameUtils.concat(dir, "Lexical Analysis");
		File theDir2 = new File(dir2);
		if (!theDir2.exists())
			theDir2.mkdir();
		dir2 = org.apache.commons.io.FilenameUtils.concat(dir, "Syntactic Analysis");
		theDir2 = new File(dir2);
		if (!theDir2.exists())
			theDir2.mkdir();

		DictionaryEditorShell editor = new DictionaryEditorShell();
		editor.getController().initLoad(lang);
		Launcher.getDesktopPane().add(editor);
		editor.setVisible(true);
		dialog.dispose();
	}

}
