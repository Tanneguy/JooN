package net.nooj4nlp.gui.dialogs;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.packageconfigurationdialog.PackageConfigurationDialogController;
import net.nooj4nlp.controller.packageconfigurationdialog.RefreshActionListener;
import net.nooj4nlp.controller.packageconfigurationdialog.SaveProjectActionListener;

/**
 * 
 * Package configuration dialog
 * 
 */
public class PackageConfigurationDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1021274592244274784L;

	private JTable tableFiles;
	private JTextPane textDocumentation;
	private JTextArea textResources;

	private PackageConfigurationDialogController packageConfigurationDialogController;

	/**
	 * Creates the frame.
	 */
	public PackageConfigurationDialog()
	{
		setBounds(100, 100, 550, 620);
		getContentPane().setLayout(
				new MigLayout("insets 3", "[320!, grow][90!, grow][90!, grow]",
						"[30!][150:175:200, grow][30!][150:175:200, grow][30!][150:175:200, grow]"));

		// Frame shouldn't have the option to be minimized/maximized/closed - closing is done from menu!
		setClosable(false);
		setIconifiable(false);
		setMaximizable(false);

		JLabel lblFiles = new JLabel("Following files are to be included in Project:");
		
		getContentPane().add(lblFiles, "cell 0 0, grow");

		JButton btnRefresh = new JButton("Refresh");

		getContentPane().add(btnRefresh, "cell 1 0");

		JButton btnSave = new JButton("Save");
	
		getContentPane().add(btnSave, "cell 2 0");

		String[] columnNames = { "Type", "File", "Directory" };
		DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);
		tableFiles = new JTable(tableModel);
		tableFiles.setShowGrid(false);

		JScrollPane scrollTable = new JScrollPane(tableFiles);
	
		getContentPane().add(scrollTable, "cell 0 1, span 3, grow");

		tableFiles.setFillsViewportHeight(true);

		JLabel lblResources = new JLabel("Following Resources are to be included in Project:");
	
		getContentPane().add(lblResources, "cell 0 2, span 3");

		textResources = new JTextArea();
		textResources.setBackground(SystemColor.control);
		textResources.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JScrollPane scrollResources = new JScrollPane(textResources);
		
		getContentPane().add(scrollResources, "cell 0 3, span 3, grow");

		JLabel lblDocumentation = new JLabel("Project Documentation:");
		
		getContentPane().add(lblDocumentation, "cell 0 4, span 3");

		textDocumentation = new JTextPane();
		textDocumentation.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JScrollPane scrollDocumentation = new JScrollPane(textDocumentation);
		
		getContentPane().add(scrollDocumentation, "cell 0 5, span 3, grow");

		packageConfigurationDialogController = new PackageConfigurationDialogController(this);

		// Adding listeners
		btnRefresh.addActionListener(new RefreshActionListener(packageConfigurationDialogController));
		btnSave.addActionListener(new SaveProjectActionListener(packageConfigurationDialogController));
	}

	public PackageConfigurationDialogController getPackageConfigurationDialogController()
	{
		return packageConfigurationDialogController;
	}

	public void setPackageConfigurationDialogController(
			PackageConfigurationDialogController packageConfigurationDialogController)
	{
		this.packageConfigurationDialogController = packageConfigurationDialogController;
	}

	public JTable getTableFiles()
	{
		return tableFiles;
	}

	public void setTableFiles(JTable tableFiles)
	{
		this.tableFiles = tableFiles;
	}

	public JTextArea getTextResources()
	{
		return textResources;
	}

	public void setTextResources(JTextArea textResources)
	{
		this.textResources = textResources;
	}

	public JTextPane getTextDocumentation()
	{
		return textDocumentation;
	}

	public void setTextDocumentation(JTextPane textDocumentation)
	{
		this.textDocumentation = textDocumentation;
	}
}
