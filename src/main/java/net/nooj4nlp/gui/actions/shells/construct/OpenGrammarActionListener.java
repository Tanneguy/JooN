package net.nooj4nlp.gui.actions.shells.construct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.gui.components.CursorChangeEffect;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.FlexDescEditorShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class OpenGrammarActionListener implements ActionListener
{

	private JDesktopPane desktopPane;

	public OpenGrammarActionListener(JDesktopPane desktopPane)
	{
		super();
		this.desktopPane = desktopPane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser chooser = Launcher.getOpenGramChooser();

		int code = chooser.showOpenDialog(desktopPane);
		if (code == JFileChooser.APPROVE_OPTION)
		{

			GrammarEditorShell formGram = null;
			FlexDescEditorShell formFlex = null;

			File file = chooser.getSelectedFile();
			String fullname = file.getAbsolutePath();

			Launcher.getOpenGramChooser().setCurrentDirectory(file);
			Launcher.getSaveGramChooser().setCurrentDirectory(file);

			String fname = org.apache.commons.io.FilenameUtils.getName(fullname);
			String ext = org.apache.commons.io.FilenameUtils.getExtension(fname);

			if (ext.equals("grf") || ext.equals("GRF"))
			{
				
			}
			else if (ext.equals("flx") || ext.equals("FLX"))
			{
				formFlex = new FlexDescEditorShell();

				try
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
					formFlex.getController().loadFromFile(fullname);
				}

				finally
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				}
			}
			else
			{
				boolean istextual = Grammar.isItTextual(fullname);
				if (istextual)
				{
					formFlex = new FlexDescEditorShell();

					try
					{
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
						boolean success = formFlex.getController().loadFromFile(fullname);
						if (!success)
							formFlex = null;
					}

					finally
					{
						CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
					}
				}
				else
				{
					if (ext.equals("xml") || ext.equals("XML"))
					{
						formGram = loadNooJGrammar(fullname, true);
					}
					else
					{
						formGram = loadNooJGrammar(fullname, false);
					}
				}
			}

			if (formFlex != null)
			{
				Launcher.getDesktopPane().add(formFlex);
				formFlex.setVisible(true);
			}
			else if (formGram != null)
			{
				if (formGram.getController().grammar == null)
					formGram.dispose();
				else
				{
					Launcher.getDesktopPane().add(formGram);
					formGram.setVisible(true);
				}
			}
		}
	}

	private GrammarEditorShell loadNooJGrammar(String fullname, boolean fromCSharp)
	{
		GrammarEditorShell formGram = new GrammarEditorShell(fullname);

		formGram.getController().LoadGrammar(fullname, fromCSharp);
		if (formGram.getController().grammar == null)
		{
			JOptionPane.showMessageDialog(formGram, "Cannot load grammar " + fullname,
					"NooJ: Grammar file is corrupted", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return formGram;
	}
}