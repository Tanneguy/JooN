package net.nooj4nlp.gui.dialogs.OpenCorpusDialog;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.CorpusEditorShell.CloseInternalFrameListener;
import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.controller.TextEditorShell.TextEditorShellController;
import net.nooj4nlp.engine.Charlist;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.main.Launcher;

public class AlphabetDialog extends JInternalFrame implements PropertyChangeListener
{
	private static final long serialVersionUID = 178125551413285417L;

	private JLabel label;
	private JTable tableChars;

	private CorpusEditorShellController corpusController;
	private TextEditorShellController textController;

	public AlphabetDialog(CorpusEditorShellController corpusController, TextEditorShellController textController)
	{
		super();
		this.textController = textController;
		this.corpusController = corpusController;

		if (corpusController != null && corpusController.getShell() != null)
			setTitle("Characters in " + corpusController.getFullName());
		else
			setTitle("Characters in " + (textController.getTextName() == null ? "" : textController.getTextName()));

		setBounds(120, 120, 248, 373);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		getContentPane().setLayout(new MigLayout("", "[232px]", "[20!, grow][grow]"));

		label = new JLabel("New label");
		getContentPane().add(label, "cell 0 0,alignx left,aligny top");

		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Freq", "Char", "Type", "Unicode" }, 0);
		tableChars = new JTable(tableModel);

		JScrollPane scrollPane1 = new JScrollPane(tableChars);
		scrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(scrollPane1, "cell 0 1,grow");

		this.addInternalFrameListener(new CloseInternalFrameListener(corpusController, this));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			Launcher.getStatusBar().getProgressBar().setIndeterminate(false);
			Launcher.getStatusBar().getProgressBar().setValue(progress);
		}
	}

	public void fillInTheAlphabet(Charlist charlist)
	{
		// Fill in new data
		if (charlist == null)
		{
			// Clear previous data
			tableChars.setModel(new DefaultTableModel(new Object[] { "Freq", "Char", "Type", "Unicode" }, 0));
			return;
		}
		else
		{
			ArrayList<Character> chars = charlist.getChars();
			ArrayList<Integer> freqs = charlist.getFreqs();

			// Clear previous data
			tableChars.setModel(new DefaultTableModel(new Object[] { "Freq", "Char", "Type", "Unicode" }, 0));
			DefaultTableModel tableCharsModel = (DefaultTableModel) tableChars.getModel();

			for (int i = 0; i < chars.size(); i++)
			{
				char c = chars.get(i);
				int f = freqs.get(i);

				int code = c;

				String scode = String.format("%04X", code);

				String t;
				if (Language.isLetter(c))
					t = "Let";
				else if (Character.isDigit(c))
					t = "Dig";
				else if (Character.isWhitespace(c))
					t = "Blk";
				else
					t = "Del";

				Object[] row = new Object[4];

				row[0] = f;
				row[1] = c;
				row[2] = t;
				row[3] = scode;

				tableCharsModel.addRow(row);
			}

			// First column needs to be sorted as Integer. Others have default sorting.
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableCharsModel);
			sorter.setComparator(0, new Comparator<Integer>()
			{
				@Override
				public int compare(Integer o1, Integer o2)
				{
					if (o1 < o2)
						return -1;
					else if (o1 > o2)
						return 1;
					else
						return 0;
				}
			});
			tableChars.setRowSorter(sorter);

			label.setText("Text has " + chars.size() + " characters.");
		}
	}

	public void fillInTheData(boolean isACorpus)
	{
		if (isACorpus)
		{
			alphabetisation(corpusController);
		}
		else
		{
			alphabetisation(textController);
		}
	}

	private void alphabetisation(CorpusEditorShellController corpusController)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		// if (fc.formAlphabet != null && fc.formAlphabet.Visible) fc.formAlphabet.Hide();
		if (this.isVisible())
			this.hide();

		// desactivate all formText operations
		corpusController.desactivateOps();
		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.progressMessage = "Alphabetisation...";
		Launcher.getStatusBar().getProgressLabel().setText("Alphabetisation...");

		//if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;

			Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.CORPUS_ALPHABETISATION, null,
					corpusController, null);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
//		else
		{
			// mono-thread
			corpusController.computeAlphabet();
			fillInTheAlphabet(corpusController.getCorpus().charlist);
			corpusController.reactivateOps();
			corpusController.updateTextPaneStats();
			corpusController.updateResults();

			this.show();

			Date now = new Date();
			long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
			Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
			// this.progressLabel.Text = (sec < 5) ? "" : sec.ToString() + " sec";
		}
	}

	private void alphabetisation(TextEditorShellController textController)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		// if (ft.formAlphabet!= null && ft.formAlphabet.Visible) ft.formAlphabet.Hide();
		if (this.isVisible())
			this.hide();

		// desactivate all formText operations
		textController.desactivateOps();
		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.getStatusBar().getProgressLabel().setText("Alphabetisation...");

	//	if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;

			Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.TEXT_ALPHABETISATION, textController,
					null, null);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
//		else
		{
			// mono-thread
			textController.computeAlphabet();
			fillInTheAlphabet(textController.getMyText().charlist);
			textController.reactivateOps();
			textController.updateTextPaneStats();
			textController.rtbTextUpdate(false);

			this.show();

			Date now = new Date();
			long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
			Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
			// this.progressLabel.Text = (sec < 5) ? "" : sec.ToString() + " sec";
		}
	}

	public CorpusEditorShellController getCorpusController()
	{
		return corpusController;
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}
}