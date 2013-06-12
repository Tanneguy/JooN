package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretListener;
import javax.swing.text.StyledDocument;

import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.gui.actions.shells.modify.UnitSelectionListener;
import net.nooj4nlp.gui.components.CustomJSpinner;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class TextLinguisticAnalysisActionListener implements ActionListener, PropertyChangeListener
{
	private TextEditorShellController controller;
	private TextEditorShell textShell;
	private JTextPane textPane;

	public TextLinguisticAnalysisActionListener(TextEditorShellController controller)
	{
		super();
		this.controller = controller;
		this.textShell = this.controller.getTextShell();
		this.textPane = this.textShell.getTextPane();
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

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (Launcher.backgroundWorking)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ONE_PROCESS_RUNNING_MESSAGE,
					Constants.ONE_PROCESS_ONLY_CAPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Launcher.initialDate = new Date();

		JCheckBox cbTAS = textShell.getChckbxShowTextAnnotation();
		if (cbTAS.isSelected())
		{
			Container container = textShell.getContentPane();
			cbTAS.setSelected(false);
			JSplitPane splitPane = textShell.getSplitPane();
			JScrollPane textScroll = textShell.getScrollPane();
			JScrollPane panelScroll = textShell.getPanelScrollPane();

			textShell.getTasActionListener().start(container, cbTAS, splitPane, textScroll, panelScroll, controller);
		}

		// desactivate all formText operations
		controller.desactivateOps();

		Launcher.getStatusBar().getBtnCancel().setEnabled(true);
		Launcher.getStatusBar().getBtnCancel().setForeground(Color.red);
		Launcher.progressMessage = "Linguistic Analysis...";
		Launcher.getStatusBar().getProgressLabel().setText("Linguistic Analysis...");

		if (Launcher.multithread)
		{
			// multi-thread
			Launcher.backgroundWorking = true;

			Launcher.backgroundWorker = new BackgroundWorker(BackgroundWorker.TEXT_LING_ANALYSIS, controller, null,
					null);
			Launcher.backgroundWorker.addPropertyChangeListener(this);
			Launcher.backgroundWorker.execute();
		}
		else
		{
			// mono-thread
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.MONO_THREAD_MESSAGE,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

			controller.linguisticAnalysis();

			linguisticAnalysisForNewText(textPane, textShell);

			controller.reactivateOps();
			controller.updateTextPaneStats();
			controller.rtbTextUpdate(false);
			controller.modify();

			Date now = new Date();
			long sec = (now.getTime() - Launcher.initialDate.getTime()) / 1000;
			Launcher.getStatusBar().getProgressLabel().setText(Long.toString(sec) + " sec");
			

			undoModifyChanges();
		}
	}

	private void linguisticAnalysisForNewText(JTextPane textPane, TextEditorShell textShell)
	{
		int numberOfTU = controller.getMyText().nbOfTextUnits;
		final CustomJSpinner spinner = new CustomJSpinner(textShell, numberOfTU);
		StyledDocument doc = textPane.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);

		// if linguistic analysis is being performed on a new text...
		if (textShell.isNewText())
		{
			// get container of a text shell, total number of text units
			Container contentPane = textShell.getContentPane();

			JLabel labelTU = textShell.getLblnTus();

			// remove old spinner and label from GUI and replace them with a new ones
			contentPane.remove(textShell.getSpinner());
			// catch "Enter" key event for a custom value
			final JFormattedTextField jtf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
			jtf.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						String text = jtf.getText();
						// show error dialogs if custom value is not regular
						try
						{
							Integer newValue = Integer.valueOf(text);

							if (newValue > spinner.getUpperLimit() || newValue < 1)
							{
								JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
										Constants.NOOJ_NUMBER_RANGE_INPUT_MESSAGE, Constants.NOOJ_APPLICATION_NAME
												+ " error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							spinner.setCustomValue(newValue);
						}
						catch (NumberFormatException ex)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									Constants.NOOJ_NUMBER_INPUT_MESSAGE, Constants.NOOJ_APPLICATION_NAME + " error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});

			textShell.setSpinner(spinner);
			textShell.getSpinner().setEnabled(true);

			contentPane.remove(labelTU);
			contentPane.add(spinner, "flowx, cell 0 0, alignx left, aligny top");
			contentPane.add(labelTU, "cell 0 0, alignx center, aligny top");
			labelTU.setText("/ " + numberOfTU);
			textShell.revalidate();
			textShell.repaint();

			// add unit selection listener
			UnitSelectionListener tuListener = new UnitSelectionListener(controller, textPane);
			textPane.addCaretListener(tuListener);
			textShell.setUnitSelectionListener(tuListener);
			textPane.setCaretPosition(0);
			doc.setCharacterAttributes(0, doc.getLength(), textPane.getStyle("Inactive"), true);
			spinner.setCustomValue(1);
		}
	}

	/**
	 * Reverse function for enabling components, and reactivating behavior of textPane, which were disabled after
	 * Text/Modify action has been done.
	 */
	private void undoModifyChanges()
	{
		// if there's no listener for textPane, add it
		CaretListener[] listeners = textPane.getCaretListeners();
		if (listeners.length == 0)
		{
			textPane.moveCaretPosition(0);
			textPane.getCaret().setVisible(true);
			// Move caret to beginning of text
			textPane.addCaretListener(new UnitSelectionListener(controller, textPane));
			textPane.setEditable(false);
		}

		// remove edit-ability
		textPane.setEditable(false);

		CustomJSpinner spinner = textShell.getSpinner();
		JCheckBox cbShowAnnottation = textShell.getChckbxShowTextAnnotation();

		// enable spinner and checkbox
		if (!cbShowAnnottation.isEnabled())
			cbShowAnnottation.setEnabled(true);
		if (!spinner.isEnabled())
		{
			spinner.setEnabled(true);
			spinner.setCustomValue(0);
		}
	}
}