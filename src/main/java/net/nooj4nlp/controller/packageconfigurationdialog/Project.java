package net.nooj4nlp.controller.packageconfigurationdialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Utils;
import net.nooj4nlp.engine.Zip;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Project implements Serializable
{
	private static final long serialVersionUID = -6147896220161601998L;

	private ArrayList<Object> listOfFiles; // list of text files
	private transient ArrayList<JInternalFrame> listOfForms; // list of all windows //NON serialized

	public Project()
	{
		listOfForms = new ArrayList<JInternalFrame>();
	}

	static public Project load(String prjdir)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Project prj = null;

		try
		{
			
			fis = new FileInputStream(FilenameUtils.concat(prjdir, "project.xml"));
			ois = new ObjectInputStream(fis);

			prj = (Project) ois.readObject();

			fis.close();
		}
		catch (FileNotFoundException e)
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (IOException e1)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM_DEFAULT, JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException e)
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (IOException e1)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (IOException e1)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
		}

		

		prj.listOfForms = new ArrayList<JInternalFrame>();

		return prj;
	}

	public void save(JTable table, JTextPane rtbText, String projname, String docdir,
			ArrayList<Object> languagesResourcesLex, ArrayList<Object> languagesResourcesSyn)
	{
		// Items
		String prjdir = projname + "_dir";
		File f = new File(prjdir);
		if (!f.exists())
			f.mkdir();

		// Save languages resources
		ArrayList<String> languages = new ArrayList<String>();
		for (int ilr = 0; ilr < languagesResourcesLex.size(); ilr += 3)
		{
			String lname = (String) languagesResourcesLex.get(ilr); // ilr => language name

			// Add language to project
			boolean found = false;
			for (String l : languages)
			{
				if (l.equalsIgnoreCase(lname))
				{
					found = true;
					break;
				}
			}

			if (!found)
				languages.add(lname);

			String fname = (String) languagesResourcesLex.get(ilr + 1); // ilr+1 => file name
			int priority = (Integer) languagesResourcesLex.get(ilr + 2); // ilr+2 => priority level

			String sdirname;
			sdirname = FilenameUtils.concat(docdir, FilenameUtils.concat(lname, "Lexical Analysis"));
			String sname = FilenameUtils.concat(sdirname, fname);

			String tname = null;
			if (priority < 0)
				tname = FilenameUtils.concat(prjdir, "-" + lname + "-LA-" + String.format("%1d", priority) + fname);
			else
				tname = FilenameUtils.concat(prjdir, "-" + lname + "-LA-" + String.format("%02d", priority) + fname);

			File f1 = new File(sname);
			File f2 = new File(tname);
			if (f1.exists() && !f2.exists())
			{
				try
				{
					FileUtils.copyFile(f1, f2);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		for (int ilr = 0; ilr < languagesResourcesSyn.size(); ilr += 3)
		{
			String lname = (String) languagesResourcesSyn.get(ilr);

			// Aadd language to project
			boolean found = false;
			for (String l : languages)
			{
				if (l.equalsIgnoreCase(lname))
				{
					found = true;
					break;
				}
			}

			if (!found)
				languages.add(lname);

			String fname = (String) languagesResourcesSyn.get(ilr + 1);
			int priority = (Integer) languagesResourcesSyn.get(ilr + 2);

			String sdirname;
			sdirname = FilenameUtils.concat(docdir, FilenameUtils.concat(lname, "Syntactic Analysis"));
			String sname = FilenameUtils.concat(sdirname, fname);

			String tname = null;
			tname = FilenameUtils.concat(prjdir, "-" + lname + "-SA-" + String.format("%02d", priority) + fname);

			File f1 = new File(sname);
			File f2 = new File(tname);
			if (f1.exists() && !f2.exists())
			{
				try
				{
					FileUtils.copyFile(f1, f2);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// save _properties.def for each language
		for (String lname : languages)
		{
			String sdirname = FilenameUtils.concat(docdir, FilenameUtils.concat(lname, "Lexical Analysis"));
			String sname = FilenameUtils.concat(sdirname, "_properties.def");
			String tname = FilenameUtils.concat(prjdir, "-" + lname + "-" + "_properties.def");

			File f1 = new File(sname);
			File f2 = new File(tname);
			if (f1.exists() && !f2.exists())
			{
				try
				{
					FileUtils.copyFile(f1, f2);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// Save documentation
		StyledDocument doc = (StyledDocument) rtbText.getDocument();
		HTMLEditorKit kit = new HTMLEditorKit();

		String rtfFilePath = FilenameUtils.concat(prjdir, "ReadMe.rtf");
		BufferedWriter writer = null;
		try
		{
			File rtfFile = new File(rtfFilePath);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(rtfFile));
			writer = new BufferedWriter(outputStreamWriter);

			kit.write(outputStreamWriter, doc, 0, doc.getLength());

			writer.close();
		}
		catch (FileNotFoundException e1)
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
					+ rtfFilePath, Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e1)
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
					+ rtfFilePath, Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (BadLocationException e)
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e2)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_WRITE_TO_FILE_MESSAGE
					+ rtfFilePath, Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Save all open file windows that are in items
		listOfFiles = new ArrayList<Object>();

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < model.getRowCount(); i++)
		{
			String type = (String) model.getValueAt(i, 0);
			if (type.equals("Locate"))
			{
				DialogStatus ds = (DialogStatus) model.getValueAt(i, 3);

				listOfFiles.add(type);
				listOfFiles.add("");
				listOfFiles.add(ds);
			}
			else
			{
				String fName = (String) model.getValueAt(i, 1);
				String dName = (String) model.getValueAt(i, 2);

				WindowPosition wp = (WindowPosition) model.getValueAt(i, 3);

				listOfFiles.add(type);
				listOfFiles.add(fName);
				listOfFiles.add(wp);

				// Copy file
				String fullName = FilenameUtils.concat(dName, fName);
				String resName = FilenameUtils.concat(prjdir, fName);

				if (fullName.equals(resName))
					continue;

				File src = new File(fullName);
				if (!src.exists())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_FIND_FILE + fullName,
							Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}

				File dest = new File(resName);
				try
				{
					// Overwrite is default for this method!
					FileUtils.copyFile(src, dest);
					
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// Save project file
		FileOutputStream fos = null;
		try
		{
			// TODO this should be SOAP serialization!
			fos = new FileOutputStream(FilenameUtils.concat(prjdir, "project.xml"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();
			fos.close();
		}
		catch (IOException e)
		{
			try
			{
				if (fos != null)
					fos.close();
			}
			catch (IOException e1)
			{
			}

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}

		File projDir = new File(projname);
		if (projDir.exists())
			Utils.deleteDir(projDir);

		try
		{
			Zip.compressDir(prjdir, projname);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public ArrayList<Object> getListOfFiles()
	{
		return listOfFiles;
	}

	public ArrayList<JInternalFrame> getListOfForms()
	{
		return listOfForms;
	}

	public void setListOfForms(ArrayList<JInternalFrame> listOfForms)
	{
		this.listOfForms = listOfForms;
	}
}