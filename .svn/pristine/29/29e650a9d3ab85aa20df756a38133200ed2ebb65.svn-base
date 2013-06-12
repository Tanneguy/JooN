package net.nooj4nlp.controller.preferencesdialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import net.nooj4nlp.gui.dialogs.JFontChooser;
import net.nooj4nlp.gui.dialogs.PreferencesDialog;

public class DictionariesActionListener implements ActionListener{

	private PreferencesDialog dialog = null;
	private JLabel label = null;	
	
	public DictionariesActionListener(PreferencesDialog dialog, JLabel label){
		this.dialog = dialog;
		this.label = label;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFontChooser fontChooser = new JFontChooser();
		int result = fontChooser.showDialog(dialog.getParent());
		if (result == JFontChooser.OK_OPTION){
			Font f = fontChooser.getSelectedFont();			
			label.setFont(f);
			label.setText(f.getName() + ", " + f.getSize());
			if (f.isBold())
				label.setText(label.getText() + ", Bold");
			if (f.isItalic())
				label.setText(label.getText() + ", Italic");
			dialog.setDFont(new Font(f.getName(), f.getStyle(), f.getSize()));
		}
	}
}
