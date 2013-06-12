package net.nooj4nlp.controller.SyntacticTreeShell;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.gui.components.STree;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.SyntacticTreeShell;

public class SyntacticTreeShellController
{

	public SyntacticTreeShellController(SyntacticTreeShell shell)
	{
		this.shell = shell;
	}

	private SyntacticTreeShell shell;

	private int concordanceIndex;
	private STree tree;
	private String[] tokens;
	private int itokens;

	private STree parse()
	{
		String token = tokens[itokens];
		STree t = new STree(token.substring(1), shell.getPanel1());
		for (itokens++; itokens < tokens.length && tokens[itokens] != ">"; itokens++)
		{
			STree c;
			if (tokens[itokens].equals(""))
				continue;
			else if (Dic.isALexicalSymbol(tokens[itokens]))
			{
				c = new STree(tokens[itokens], shell.getPanel1());
			}
			else if (tokens[itokens].charAt(0) == '<')
			{
				c = parse();
			}
			else if (tokens[itokens].charAt(0) == '$')
			{
				c = new STree(tokens[itokens], shell.getPanel1());
			}
			else
			{
				// this is a set of properties for the current parent node
				t.label += tokens[itokens];
				continue;
			}
			t.addChild(c);
		}
		return t;
	}

	private STree parseSyntacticAnalysis(String buffer, boolean derivationtree)
	{
		String sep = "[#]";
		tokens = buffer.split(sep);
		if ((tokens == null || tokens.length < 2) && buffer.charAt(buffer.length() - 1) != '#')
			return null;

		ArrayList<String> nodes = new ArrayList<String>();
		if (derivationtree)
		{
			// just keep the :xx
			nodes.add("<Main");
			for (String tok : tokens)
			{
				if (tok.equals(":"))
					nodes.add(">");
				else if (tok.length() > 1 && tok.charAt(0) == ':')
					nodes.add("<" + tok.substring(1));
				else if (tok.length() > 2 && tok.charAt(0) == '<' && tok.charAt(tok.length() - 1) == '>')
					nodes.add(tok);
			
			}
			nodes.add(">");
		}
		else
		{
			// keep everything but the :xx
			for (String tok : tokens)
			{
				if (tok.length() == 0 || tok.charAt(0) == ':')
					continue;
				else
					nodes.add(tok);
			}
		}
		String roottok = nodes.get(0);
		if (roottok != null && !roottok.equals("") && roottok.charAt(0) != '<')
		{
			nodes.add(0, "<NOSTRUCTURE");
			nodes.add(">");
		}
		tokens = nodes.toArray(new String[nodes.size()]);
		itokens = 0;
		STree t = parse();
		return t;
	}

	public void resetEntry(Graphics grphcs)
	{
		Graphics2D g = (Graphics2D) grphcs;

		if (shell == null)
			return;

		shell.getlConcEntries().setText(
				"Concordance Entry / " + shell.getConcordanceShellController().getTheItems().size() / 4);
		shell.getTbUnitNumber().setText(Integer.toString(concordanceIndex + 1)); // [1,n] instead of [0,n-1]
		Object[] citem = (Object[]) shell.getConcordanceShellController().getTheItems().get(concordanceIndex * 4 + 1);

		ArrayList<ArrayList<?>> ctag = (ArrayList<ArrayList<?>>) citem[5];
		ArrayList<?> annotation = ctag.get(1);
		String input = (String) citem[2];
		if (input.indexOf('/') != -1)
			input = input.substring(0, input.indexOf('/'));
		String output = (String) annotation.get(1);

		shell.getLabel1().setFont(Launcher.preferences.TFont);
		shell.getLabel1().setText(input);

		tree = parseSyntacticAnalysis(output, shell.getRbDerivationTree().isSelected());
		if (tree == null)
		{
			shell.dispose();
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					"There is no structural information in the concordance", "NooJ", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		shell.getPanel1().setFont(Launcher.preferences.DFont);

		FontMetrics metrics = g.getFontMetrics(shell.getPanel1().getFont());

		// compute height of panel1
		int depth = tree.computeDepth();
		STree.LINE = metrics.getHeight();
		int panelheight = depth * STree.LINE * 5 + STree.LINE;
		tree.computeYPosition(panelheight, STree.LINE * 5);

		// compute width of panel1
		int margin = metrics.stringWidth("MMMM");
		STree.currentleftposition = margin;
		tree.computeWidth(grphcs, shell.getPanel1().getFont(), shell.getCbDisplayAll().isSelected());
		tree.computeXPosition(margin);
		int panelwidth = STree.currentleftposition;

		shell.getPanel1().setSize(new Dimension(panelwidth, panelheight));
		shell.getLabel1().setSize(new Dimension(panelwidth, shell.getLabel1().getHeight()));
		
	}

	public SyntacticTreeShell getShell()
	{
		return shell;
	}

	public STree getTree()
	{
		return tree;
	}

	public int getConcordanceIndex()
	{
		return concordanceIndex;
	}

	public void setConcordanceIndex(int concordanceIndex)
	{
		if (concordanceIndex >= shell.getConcordanceShellController().getTheItems().size() / 4)
			concordanceIndex = shell.getConcordanceShellController().getTheItems().size() / 4 - 1;
		if (concordanceIndex < 0)
			concordanceIndex = 0;
		this.concordanceIndex = concordanceIndex;
	}
}