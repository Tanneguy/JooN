package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JOptionPane;

import net.nooj4nlp.gui.dialogs.DictionaryPropDefDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.PropDefEditorShell;

public class NewPropDefActionListener implements ActionListener
{

	private DictionaryPropDefDialog dialog;
	private JList lstLang;

	public NewPropDefActionListener(JList lstLang, DictionaryPropDefDialog dialog)
	{
		super();
		this.lstLang = lstLang;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String lang = (String) lstLang.getSelectedValue();
		if (lang == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Please, select language!", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		PropDefEditorShell editor = new PropDefEditorShell();
		editor.getController().initLoad(lang);
		Launcher.getDesktopPane().add(editor);
		editor.setVisible(true);
		dialog.dispose();
	}

}
