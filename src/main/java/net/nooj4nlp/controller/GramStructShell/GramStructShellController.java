package net.nooj4nlp.controller.GramStructShell;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.gui.shells.GramStructShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class GramStructShellController
{

	public JTree tvGraphs;
	public GrammarEditorShell formGrammar;
	public GramStructShell structShell;

	public GramStructShellController(GramStructShell s, GrammarEditorShell shell)
	{
		this.structShell = s;
		this.tvGraphs = s.tvGraphs;
		formGrammar = shell;
	}

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree (except root).
	public void expandAll(boolean expand)
	{
		TreeNode root = (TreeNode) tvGraphs.getModel().getRoot();

		// Traverse tree from root
		expandAll(tvGraphs, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand)
	{
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0)
		{
			for (Enumeration<?> e = node.children(); e.hasMoreElements();)
			{
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		if (parent.getPathCount() == 1)
			return;

		// Expansion or collapse must be done bottom-up
		if (expand)
		{
			tree.expandPath(parent);
		}
		else
		{
			tree.collapsePath(parent);
		}
	}

	// region grammar tree

	private boolean[] visited;

	public int findGraph(String gname)
	{
		int index = -1;
		for (int ig = 0; ig < formGrammar.getController().grammar.graphs.size(); ig++)
		{
			Graph grf2 = formGrammar.getController().grammar.graphs.get(ig);
			if (grf2 != null && grf2.name.equals(gname))
			{
				index = ig;
				break;
			}
		}
		return index;
	}

	String getNameFromTreeNodeLabel(String text)
	{
		String label = text;
		int le = label.length();
		if (le > 4 && label.substring(le - 4, le - 2).equals(" (") && label.substring(le - 1, le).equals(")"))
			label = label.substring(0, le - 4);
		return label;
	}

	boolean isRecursive(String gname, TreeNode node)
	{
		node = node.getParent();
		while (node != null)
		{
			String label = getNameFromTreeNodeLabel(node.toString());
			if (label.equals(gname))
				return true;
			node = node.getParent();
		}
		return false;
	}

	private boolean isChild(String gname, TreeNode node)
	{
		for (int i = 0; i < node.getChildCount(); i++)
		{
			if (node.getChildAt(i).toString().equals(gname))
				return true;
		}
		return false;
	}

	private boolean hack_clearing;

	public void visit(JTree tv)
	{
		hack_clearing = true;
		((DefaultMutableTreeNode) tv.getModel().getRoot()).removeAllChildren();
		((DefaultTreeModel) tv.getModel()).reload();
		hack_clearing = false;
		visited = new boolean[formGrammar.getController().grammar.graphs.size()];
		for (int i = 0; i < formGrammar.getController().grammar.graphs.size(); i++)
			visited[i] = false;

		DefaultMutableTreeNode top = (DefaultMutableTreeNode) tv.getModel().getRoot();

		// first find and visit "Main" if it exists
		int index = -1;
		for (int i = 0; i < formGrammar.getController().grammar.graphs.size(); i++)
		{
			Graph grf = formGrammar.getController().grammar.graphs.get(i);
			if (grf == null)
				continue;
			String gname = grf.name;
			if (gname.equals("Main"))
			{
				index = i;
				break;
			}
		}

		if (index != -1)
		{
			visited[index] = true;
			Graph grf = formGrammar.getController().grammar.graphs.get(index);
			if (grf != null)
			{
				String gname = grf.name; // "Main"
				DefaultMutableTreeNode tn = new DefaultMutableTreeNode(gname);
				top.add(tn);
				visitEmbeddedGraphs(grf, tn);
			}
		}
		for (int i = 0; i < formGrammar.getController().grammar.graphs.size(); i++)
		{
			if (visited[i])
				continue;
			visited[i] = true;
			Graph grf = formGrammar.getController().grammar.graphs.get(i);
			if (grf != null)
			{
				String gname = grf.name;
				DefaultMutableTreeNode tn = new DefaultMutableTreeNode(gname);
				top.add(tn);
				visitEmbeddedGraphs(grf, tn);
			}
		}
		((DefaultTreeModel) tv.getModel()).reload();
		tv.setEnabled(true);
		tv.update(tvGraphs.getGraphics());
	}

	public void recursiveDelete(TreeNode currentNode, int current)
	{
		hack_clearing = true;
		JTree tv = tvGraphs;
		((DefaultMutableTreeNode) tv.getModel().getRoot()).removeAllChildren();
		((DefaultTreeModel) tv.getModel()).reload();
		hack_clearing = false;
		visited = new boolean[formGrammar.getController().grammar.graphs.size()];
		for (int i = 0; i < formGrammar.getController().grammar.graphs.size(); i++)
			visited[i] = false;

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("");
		((DefaultTreeModel) tv.getModel()).setRoot(top);

		visited[current] = true;
		Graph grf = formGrammar.getController().grammar.graphs.get(current);
		if (grf != null)
		{
			String gname = grf.name;
			DefaultMutableTreeNode tn = new DefaultMutableTreeNode(gname);
			top.add(tn);
			visitEmbeddedGraphs(grf, tn);
		}

		for (int ig = 0; ig < formGrammar.getController().grammar.graphs.size(); ig++)
		{
			if (visited[ig])
				formGrammar.getController().grammar.graphs.set(ig, null);
		}

		for (int ig = 0; ig < formGrammar.getController().grammar.graphs.size();)
		{
			Graph g = formGrammar.getController().grammar.graphs.get(ig);
			if (g == null)
				formGrammar.getController().grammar.graphs.remove(ig);
			else
				ig++;
		}
		tvGraphs.update(tvGraphs.getGraphics());
	}

	private void renameNodes(DefaultMutableTreeNode tn, String oldname, String newname)
	{
		String label = tn.toString();
		if (label.length() > 4 && label.substring(label.length() - 4).equals(" (R)"))
		{
			label = label.substring(0, label.length() - 4);
			if (label.equals(oldname))
				tn = new DefaultMutableTreeNode(newname + " (R)");
			return;
		}
		else
		{
			if (tn.toString().equals(oldname))
				tn = new DefaultMutableTreeNode(newname);
			if (tn.getChildCount() == 0)
				return;
			for (int i = 0; i < tn.getChildCount(); i++)
			{
				DefaultMutableTreeNode t = (DefaultMutableTreeNode) tn.getChildAt(i);
				if (t.toString().equals(oldname))
					t = new DefaultMutableTreeNode(newname);
				if (!isRecursive(t.toString(), t))
					renameNodes(t, oldname, newname);
			}
		}
	}

	void rename(String oldname, String newname)
	{
		// rename graph name in all graphs' labels
		for (int i = 0; i < formGrammar.getController().grammar.graphs.size(); i++)
		{
			Graph grf = formGrammar.getController().grammar.graphs.get(i);
			if (grf == null)
				continue;

			if (grf.name.equals(oldname))
				grf.name = newname;

			for (int inode = 0; inode < grf.label.size(); inode++)
			{
				if (inode == 1)
					continue;
				if (grf.commentNode(inode) || grf.areaNode(inode))
					continue;
				ArrayList<String> recterms = Graph.embGraphsLabel(grf.label.get(inode));
				if (recterms == null)
					continue;
				grf.renameEmbeddedGraphInLabel(inode, oldname, newname);
			}
		}

		// rename all graphs' nodes
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode) tvGraphs.getModel().getRoot();
		renameNodes(tn, oldname, newname);
		tvGraphs.update(tvGraphs.getGraphics());
	}

	private void visitEmbeddedGraphs(Graph grf, DefaultMutableTreeNode tn)
	{
		// import all embedded graphs
		int nbofnodes = grf.label.size();
		for (int inode = 0; inode < nbofnodes; inode++)
		{
			if (inode == 1)
				continue;
			if (grf.commentNode(inode) || grf.areaNode(inode))
				continue;

			ArrayList<String> recterms = Graph.embGraphsLabel(grf.label.get(inode));
			if (recterms == null)
				continue;
			for (int iterm = 0; iterm < recterms.size(); iterm++)
			{
				String gname = recterms.get(iterm);
				if (isChild(gname, tn))
					continue; // gname has already been added
				DefaultMutableTreeNode ntn;
				int index;
				if (isRecursive(gname, tn)) // gname is an already visited parent
				{
					index = -1;
					for (int i = 0; i < tn.getChildCount(); i++)
						if (tn.getChildAt(i).toString().equals(gname + " (R)"))
						{
							index = i;
							break;
						}
					if (index == -1)
					{
						ntn = new DefaultMutableTreeNode(gname + " (R)");
						// for this purpose, custom TreeCellRenderer is needed, but not implemented!)
						
						tn.add(ntn);
						// fix for bug in painting (onSelect event of Structure Dialog).
						// If it spoils other functionalities it should be reconsidered.
						
					}
					continue;
				}

				// look for gname in grammar
				index = findGraph(gname);
				if (index != -1) // found gname in grammar
				{
					visited[index] = true;
					ntn = new DefaultMutableTreeNode(gname);
					tn.add(ntn);
					Graph grf2 = formGrammar.getController().grammar.graphs.get(index);
					visitEmbeddedGraphs(grf2, ntn);
					tvGraphs.update(tvGraphs.getGraphics());
				}
				else
				// gname does not exist in grammar
				{
					// look for gname in tn
					index = -1;
					for (int i = 0; i < tn.getChildCount(); i++)
						if (tn.getChildAt(i).toString().equals(gname + " (X)"))
						{
							index = i;
							break;
						}
					if (index == -1)
					{
						ntn = new DefaultMutableTreeNode(gname + " (X)");
						// a custom TreeCellRenderer is needed for this purpose!
						
						tn.add(ntn);
					}
					tvGraphs.update(tvGraphs.getGraphics());
					continue;
				}
			}
		}
	}

	public void loadParent()
	{
		if (formGrammar.getController().currentNode.getParent() != null)
			formGrammar.getController().currentNode = (DefaultMutableTreeNode) formGrammar.getController().currentNode
					.getParent();
		if (tvGraphs.isVisible())
		{
			
			tvGraphs.setSelectionPath(new TreePath(formGrammar.getController().currentNode));
		}
		else
		{
			int c = findGraph(formGrammar.getController().currentNode.toString());
			if (c != -1)
			{
				formGrammar.getController().current = c;
				formGrammar.getController().grf = formGrammar.getController().grammar.graphs.get(formGrammar
						.getController().current);

				if (formGrammar.getController().dialogHistory != null
						&& formGrammar.getController().dialogHistory.isVisible())
					formGrammar.getController().dialogHistory.getController().updateNewFor(formGrammar);
			}
		}
		formGrammar.getController().editor.pGraph.invalidate();
		formGrammar.getController().editor.pGraph.repaint();
	}

	public void loadFirstChild()
	{
		if (formGrammar.getController().currentNode.getChildAt(0) != null)
			formGrammar.getController().currentNode = (DefaultMutableTreeNode) formGrammar.getController().currentNode
					.getChildAt(0);
		if (tvGraphs.isVisible())
		{
			
			tvGraphs.setSelectionPath(new TreePath(formGrammar.getController().currentNode));
		}
		else
		{
			int c = findGraph(formGrammar.getController().currentNode.toString());
			if (c != -1)
			{
				formGrammar.getController().current = c;
				formGrammar.getController().grf = formGrammar.getController().grammar.graphs.get(formGrammar
						.getController().current);

				if (formGrammar.getController().dialogHistory != null
						&& formGrammar.getController().dialogHistory.isVisible())
					formGrammar.getController().dialogHistory.getController().updateNewFor(formGrammar);
			}
		}
		formGrammar.getController().editor.pGraph.invalidate();
		formGrammar.getController().editor.pGraph.repaint();
	}

	public void loadNextChild()
	{
		if (formGrammar.getController().currentNode.getNextNode() != null)
			formGrammar.getController().currentNode = formGrammar.getController().currentNode.getNextNode();
		if (tvGraphs.isVisible())
		{
			// JTree was not customized for this purpose!
			
		}
		else
		{
			int index = findGraph(formGrammar.getController().currentNode.toString());
			if (index != -1)
			{
				formGrammar.getController().current = index;
				formGrammar.getController().grf = formGrammar.getController().grammar.graphs.get(formGrammar
						.getController().current);
				if (formGrammar.getController().dialogHistory != null
						&& formGrammar.getController().dialogHistory.isVisible())
					formGrammar.getController().dialogHistory.getController().updateNewFor(formGrammar);
			}
		}
		formGrammar.pGraph.invalidate();
		formGrammar.pGraph.repaint();
	}

	public void loadPreviousChild()
	{
		if (formGrammar.getController().currentNode.getPreviousNode() != null)
			formGrammar.getController().currentNode = formGrammar.getController().currentNode.getPreviousNode();
		if (tvGraphs.isVisible())
		{
			// JTree was not customized for this purpose!
		
		}
		else
		{
			int index = findGraph(formGrammar.getController().currentNode.toString());
			if (index != -1)
			{
				formGrammar.getController().current = index;
				formGrammar.getController().grf = formGrammar.getController().grammar.graphs.get(formGrammar
						.getController().current);
				if (formGrammar.getController().dialogHistory != null
						&& formGrammar.getController().dialogHistory.isVisible())
					formGrammar.getController().dialogHistory.getController().updateNewFor(formGrammar);
			}
		}
		formGrammar.pGraph.invalidate();
		formGrammar.pGraph.repaint();
	}

	public void select()
	{
		if (isHack_clearing())
			return;

		// Compute Current and CurrentNode
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tvGraphs.getLastSelectedPathComponent();
		if (node == null)
			return;
		String label = getNameFromTreeNodeLabel(node.toString());
		int index = findGraph(label);
		if (index != -1)
		{
			// found graph
			getFormGrammar().current = index;
			getFormGrammar().grf = getFormGrammar().grammar.graphs.get(getFormGrammar().current);

			if (getFormGrammar().dialogHistory != null)
				getFormGrammar().dialogHistory.getController().updateNewFor(formGrammar);

			getFormGrammar().currentNode = node;
			if (isRecursive(label, node)) // is it a recursive call?
			{
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getParent();
				while (n != null)
				{
					if (label == getNameFromTreeNodeLabel(n.toString()))
					{
						getFormGrammar().currentNode = n;
						break;
					}
					n = (DefaultMutableTreeNode) n.getParent();
				}
			}
		}
		else
		// non-existing graph
		{
			if (JOptionPane.showConfirmDialog(null, "Create New Graph?", "NooJ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				Graph ngrf = new Graph();
				ngrf.wholeGrammar = getFormGrammar().grammar;
				ngrf.name = getNameFromTreeNodeLabel(node.toString());
				ngrf.addNode("<E>", 30, 30);
				ngrf.addNode("", 50, 60);

				getFormGrammar().grammar.graphs.add(ngrf);
				getFormGrammar().current = getFormGrammar().grammar.graphs.size() - 1;
				getFormGrammar().grf = getFormGrammar().grammar.graphs.get(getFormGrammar().current);

				if (getFormGrammar().dialogHistory != null)
					getFormGrammar().dialogHistory.getController().updateNewFor(getFormGrammar().editor);

				getFormGrammar().currentNode = node;
				tvGraphs.getModel().valueForPathChanged(tvGraphs.getSelectionPath(), ngrf.name);
			
			}
			else
			{
				// do not change Current
				getFormGrammar().currentNode = (DefaultMutableTreeNode) node.getParent();
			}
		}
		tvGraphs.setSelectionPath(new TreePath(getFormGrammar().currentNode));
		tvGraphs.expandPath(new TreePath(getFormGrammar().currentNode));
		getFormGrammar().visitHistory.add(getFormGrammar().currentNode);

		formGrammar.invalidate();
		formGrammar.repaint();
	}

	public boolean isHack_clearing()
	{
		return hack_clearing;
	}

	public GrammarEditorShellController getFormGrammar()
	{
		return formGrammar.getController();
	}
}
