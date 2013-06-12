package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import net.nooj4nlp.gui.dialogs.DictionaryPropDefDialog;

/**
 * 
 * ActionListener that creates a new Dictionary / Property definition dialog
 *
 */
public class NewDictionaryPropDefActionListener implements ActionListener {

	private boolean isDictionary;
	private DictionaryPropDefDialog dialog;
	
	/**
	 * 
	 * @param dictionary
	 * 				true initializes a new dictionary, false creates a new property definition
	 */
	public NewDictionaryPropDefActionListener(boolean dictionary){
		isDictionary = dictionary;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(isDictionary){
			dialog = new DictionaryPropDefDialog(true);
		}
		else{
			dialog = new DictionaryPropDefDialog(false);
		}
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

}
