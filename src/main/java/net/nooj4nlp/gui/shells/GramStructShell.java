package net.nooj4nlp.gui.shells;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.GramStructShell.CollapseActionListener;
import net.nooj4nlp.controller.GramStructShell.ExpandActionListener;
import net.nooj4nlp.controller.GramStructShell.GramStructShellController;
import net.nooj4nlp.controller.GramStructShell.RefreshActionListener;
import net.nooj4nlp.controller.GramStructShell.SelectionListener;

public class GramStructShell extends JInternalFrame
{
	private static final long serialVersionUID = 1L;
	public JTree tvGraphs;
	private GramStructShellController controller;
	private GrammarEditorShell formGrammar;

	public GramStructShell(GrammarEditorShell shell)
	{

		formGrammar = shell;
		setIconifiable(false);
		setResizable(true);
		setClosable(true);
		setBounds(100, 50, 330, 600);
		setTitle("Structure of " + org.apache.commons.io.FilenameUtils.getName(formGrammar.getTitle()));
		setMaximizable(false);

		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[][grow,fill]"));

		JPanel header = new JPanel();
		header.setLayout(new MigLayout("ins 7", "[][][]", "[]"));

		getContentPane().add(header, "wrap");

		JButton btnExpand = new JButton("Expand");
		header.add(btnExpand, "");

		JButton btnCollapse = new JButton("Collapse");
		header.add(btnCollapse, "");

		JButton btnRefresh = new JButton("Refresh");
		header.add(btnRefresh, "");

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("");
	
		tvGraphs = new JTree(top);
		tvGraphs.setRootVisible(false);
		tvGraphs.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		Icon subgraphIcon = null;
		renderer.setLeafIcon(subgraphIcon);
		renderer.setClosedIcon(subgraphIcon);
		renderer.setOpenIcon(subgraphIcon);
		tvGraphs.setCellRenderer(renderer);
		JScrollPane treeView = new JScrollPane(tvGraphs);
		getContentPane().add(treeView);

		controller = new GramStructShellController(this, shell);

		btnExpand.addActionListener(new ExpandActionListener(controller));
		btnCollapse.addActionListener(new CollapseActionListener(controller));
		btnRefresh.addActionListener(new RefreshActionListener(controller));
		tvGraphs.addTreeSelectionListener(new SelectionListener(controller));
	}

	public GramStructShellController getController()
	{
		return controller;
	}
}
