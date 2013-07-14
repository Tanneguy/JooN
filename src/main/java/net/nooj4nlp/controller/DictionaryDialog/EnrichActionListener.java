package net.nooj4nlp.controller.DictionaryDialog;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class EnrichActionListener implements ActionListener
{
	private DictionaryDialogController controller;

	public EnrichActionListener(DictionaryDialogController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (controller.lan == null)
			controller.lan = new Language(Launcher.preferences.deflanguage);

		RefObject<Language> lanRef = new RefObject<Language>(controller.lan);

		Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		controller.lan = lanRef.argvalue;

		String errmessage = null;
		RefObject<String> errmessageRef = new RefObject<String>(errmessage);

		try
		{
			if (!engine.loadResources(Launcher.preferences.ldic.get(controller.lan.isoName),
					Launcher.preferences.lsyn.get(controller.lan.isoName), true, errmessageRef))
			{
				errmessage = errmessageRef.argvalue;
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errmessage,
						"NooJ: cannot load linguistic resources", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		catch (HeadlessException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_HEADLESS, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (ClassNotFoundException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String buffer = DictionaryDialogController.getDictionaryContent(controller.getTxtDictionaryName().getText());
		if (buffer == null)
			return;
		String[] lines = null;
		String resultingdictionary = null;

		lines = buffer.split("\n");
		resultingdictionary = engine.enrichDictionary(lines);

		String tmp = controller.getTxtDictionaryName().getText();
		String fname = FilenameUtils.removeExtension(FilenameUtils.getName(tmp));
		String ext = FilenameUtils.getExtension(tmp);
		String dname = FilenameUtils.getFullPath(tmp);

		String fullname2 = dname + fname + "-en." + ext;

		try
		{
			
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(fullname2), "UTF8"));
			out.write(resultingdictionary);
			out.close();
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e1.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}
		resultingdictionary = null;

		if (JOptionPane.showConfirmDialog(null, "Do you want to work with new created dictionary " + fullname2 + "?",
				"NooJ", JOptionPane.YES_NO_OPTION) == 0)
		{
			controller.getTxtDictionaryName().setText(fullname2);
			if (controller.loadLines(0, 500))
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display "
								+ FilenameUtils.getName(controller.getTxtDictionaryName().getText()),
								TitledBorder.LEADING, TitledBorder.TOP, null, null));
			else
				controller.getPnlDisplayDictionary().setBorder(
						new TitledBorder(null, "Display beginning of "
								+ FilenameUtils.getName(controller.getTxtDictionaryName().getText()),
								TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
	}
}