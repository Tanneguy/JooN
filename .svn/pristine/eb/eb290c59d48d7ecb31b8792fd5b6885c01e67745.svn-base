package net.nooj4nlp.gui.shells;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

/**
 * 
 * Shell containing lab-specific instructions
 * 
 */
public class LabInstructionsShell extends JInternalFrame
{
	private static final long serialVersionUID = 2207198069870629997L;

	/**
	 * Create the frame.
	 */
	public LabInstructionsShell(String instructions)
	{
		setBounds(100, 100, 450, 459);
		setTitle(instructions);
		setResizable(true);
		setIconifiable(true);

		HTMLEditorKit html = new HTMLEditorKit();
		JEditorPane editor = new JEditorPane();
		editor.setEditorKit(html);
		editor.setBackground(Color.white);
		editor.setEditable(false);
		getContentPane().add(editor, BorderLayout.NORTH);
		HTMLDocument doc = (HTMLDocument) editor.getDocument();
		doc.putProperty("IgnoreCharsetDirective", new Boolean(true));

		JScrollPane scrollPane = new JScrollPane(editor);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		FileInputStream fInput = null;

		Reader fin = null;

		// Load appropriate instruction file into the editor area
		try
		{
			switch (instructions.charAt(0))
			{
				case 'T':
					fInput = new FileInputStream("_Misc/TextEncoding.htm");
					break;
				case 'M':
					fInput = new FileInputStream("_Misc/Morphology.htm");
					break;
				case 'C':
					fInput = new FileInputStream("_Misc/CorpusConstruction.htm");
					break;
				case 'D':
					fInput = new FileInputStream("_Misc/Dictionary.htm");
					break;
				case 'P':
					fInput = new FileInputStream("_Misc/Perl.htm");
					break;
				case 'L':
					fInput = new FileInputStream("_Misc/LanguageSpecifics.htm");
					break;
				default:
					System.out.println("NONE!");
			}
			// FIXME(?): Some unicode characters are not being decoded correctly
			// when loaded as RTF. BufferedReader workaround works for HTML, but
			// often causes the GUI to freeze when loading an instruction file
			fin = new BufferedReader(new InputStreamReader(fInput, Charset.forName("UTF-8")));
			html.read(fin, doc, 0);

			fin.close();
			fInput.close();
		}
		catch (FileNotFoundException e)
		{
			try
			{
				if (fin != null)
					fin.close();
				if (fInput != null)
					fInput.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (IOException e)
		{
			try
			{
				if (fin != null)
					fin.close();
				if (fInput != null)
					fInput.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (BadLocationException e)
		{
			try
			{
				if (fin != null)
					fin.close();
				if (fInput != null)
					fInput.close();
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}