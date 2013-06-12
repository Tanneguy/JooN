package net.nooj4nlp.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.gui.main.Launcher;

public class CustomStatusBar extends JPanel
{
	private static final long serialVersionUID = 8062992743359577052L;

	private static int STATUS_BAR_HEIGHT = 27;

	private JFrame parent;

	private JLabel progressLabel;
	private JButton btnCancel;
	private JProgressBar progressBar;
	private JLabel projectLabel;

	public CustomStatusBar(JFrame parent)
	{
		this.parent = parent;

		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(this.parent.getWidth(), STATUS_BAR_HEIGHT));

		setLayout(new MigLayout("insets 3", "[][][][]", "[27!]"));

		progressLabel = new JLabel("");
		progressLabel.setSize(180, STATUS_BAR_HEIGHT);
		progressLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 10));
		progressLabel.setForeground(Color.RED);
		progressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(progressLabel, "gapleft 130, gaptop 3, alignx left, aligny top");

		btnCancel = new JButton("Cancel");
		btnCancel.setSize(62, STATUS_BAR_HEIGHT);
		btnCancel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 10));
		btnCancel.setEnabled(false);
		add(btnCancel, "alignx left, aligny top");

		progressBar = new JProgressBar(0, 20);
		progressBar.setSize(260, STATUS_BAR_HEIGHT);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		add(progressBar, "gaptop 1, alignx left, aligny top");

		projectLabel = new JLabel("");
		projectLabel.setSize(425, STATUS_BAR_HEIGHT);
		projectLabel.setFont(new Font("Courier New", Font.BOLD, 12));
		projectLabel.setForeground(Color.RED);
		projectLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(projectLabel, "gaptop 3, alignx left, aligny top");

		// Adding action listener
		btnCancel.addActionListener(new CancelButtonActionListener());
	}

	private class CancelButtonActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Launcher.backgroundWorker.setCancellationPending(true);
			Launcher.backgroundWorker.cancel(true);

			btnCancel.setForeground(Color.black);
			btnCancel.setEnabled(false);

			progressBar.setValue(0);

			Dic.writeLog("Process canceled");
		}
	};

	public JLabel getProgressLabel()
	{
		return progressLabel;
	}

	public JButton getBtnCancel()
	{
		return btnCancel;
	}

	public JProgressBar getProgressBar()
	{
		return progressBar;
	}

	public JLabel getProjectLabel()
	{
		return projectLabel;
	}
}