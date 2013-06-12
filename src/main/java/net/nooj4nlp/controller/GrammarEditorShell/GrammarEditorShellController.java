package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.nooj4nlp.controller.DebugShell.DbgTimerActionListener;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.Preferences;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.Utilities;
import net.nooj4nlp.gui.dialogs.AlignmentDialog;
import net.nooj4nlp.gui.dialogs.FindReplaceDialog;
import net.nooj4nlp.gui.dialogs.GraphPresentationDialog;
import net.nooj4nlp.gui.dialogs.HistoryDialog;
import net.nooj4nlp.gui.dialogs.ProduceParaphrasesDialog;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.ContractShell;
import net.nooj4nlp.gui.shells.DebugShell;
import net.nooj4nlp.gui.shells.GramStructShell;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

import org.apache.commons.io.FilenameUtils;

public class GrammarEditorShellController
{

	public boolean modified;
	public int current; // graph index
	public DefaultMutableTreeNode currentNode;
	private String fullName;
	public Language lan;
	private Language lan2;

	public ArrayList<Object> visitHistory;
	public Font iFont;
	public Font oFont;
	public Font cFont;
	public Graph grf;
	public Grammar grammar;
	public String originallabel;
	public int graphnum = 0;

	public GrammarEditorShell editor;
	private FindReplaceDialog findReplaceDialog;
	public GramStructShell formGramStruct;
	public AlignmentDialog alignmentDialog;
	public GraphPresentationDialog presentationDialog;
	public DebugShell debugShell;

	private JLabel label;

	public boolean first_paint = true;

	public int mouseOX, mouseOY, mouseX, mouseY; // coordinates
	boolean mouseD; // is the mouse down? is the mouse moving?
	public boolean mouseM;
	public int mouseN; // mouse node
	public int mouseEdited;
	public int mouseR;
	public Rectangle SelectionRectangle;
	public int lasteditednode = 0;
	public int nbofcreatednodes = 1;

	public ContractShell contractShell;
	public ProduceParaphrasesDialog transformationDialog;
	public HistoryDialog dialogHistory;

	private int timerSelCount;
	private Timer timerSel;
	private Timer timerDbg;

	public GrammarEditorShellController(GrammarEditorShell shell, Language l, Language l2)
	{
		findReplaceDialog = null;
		editor = shell;
		label = editor.info;
		label.setText("TEST");
		lan = l;
		lan2 = l2;
		contractShell = new ContractShell(editor, grammar, lan);
		formGramStruct = new GramStructShell(editor);
		dialogHistory = new HistoryDialog(editor);
		visitHistory = new ArrayList<Object>();

		timerSel = new Timer(100, new TimerActionListener(editor));
		timerSel.start();

		timerDbg = new Timer(100, new DbgTimerActionListener(this));

		SelectionRectangle = new Rectangle();
		resetMouseStatus();
	}

	public GrammarEditorShellController(GrammarEditorShell shell, String fname)
	{
		findReplaceDialog = null;
		editor = shell;
		label = editor.info;
		label.setText("TEST");
		lan = lan2 = null;
		fullName = fname;
		contractShell = new ContractShell(editor, grammar, lan);
		formGramStruct = new GramStructShell(editor);
		dialogHistory = new HistoryDialog(editor);
		visitHistory = new ArrayList<Object>();

		timerSelCount = 0;
		timerSel = new Timer(100, new TimerActionListener(editor));
		timerSel.start();

		timerDbg = new Timer(100, new DbgTimerActionListener(this));

		SelectionRectangle = new Rectangle();
		resetMouseStatus();
	}

	public void updateFormHeader()
	{
		label.setText(lan.engName);
		if (grammar.gramType == GramType.SYNTAX && lan2 != null)
			label.setText(label.getText() + "/" + lan2.engName);

		File directory;
		JFileChooser chooser = Launcher.getSaveGramChooser();

		// Type of grammar; NOTE:
		// Original code had the following condition for each grammar type:
	
		if (grammar.gramType == GramType.FLX)
		{
			label.setText(label.getText() + " inflectional grammar");
			directory = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis")));
			chooser.setCurrentDirectory(directory);
		}
		else if (grammar.gramType == GramType.MORPHO)
		{
			label.setText(label.getText() + " morphological grammar");
			directory = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis")));
			chooser.setCurrentDirectory(directory);
		}
		else
		{
			label.setText(label.getText() + " syntactic grammar");
			directory = new File(org.apache.commons.io.FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Syntactic Analysis")));
			chooser.setCurrentDirectory(directory);
		}

		editor.getMntmDebug()
				.setEnabled((grammar != null && grammar.gramType != GramType.FLX && grammar.lockType == 0));
		editor.getMntmShowStructure().setEnabled((grammar != null && grammar.lockType == 0));

		if (grammar.graphs.size() > 1)
			label.setText(label.getText() + " consists of " + grammar.graphs.size() + " graphs");

		if (grammar.lockType == 1)
			label.setText(label.getText() + " (protected)");
		else if (grammar.lockType == 2)
			label.setText(label.getText() + " (Community protected)"); // Community
																		// Edition
																		// Only

		this.label.setText(label.getText() + ".");
	}

	public void ModifyExamples()
	{
		modified = true;
		if (fullName == null)
			editor.setTitle("Untitled [Modified]");
		else
			editor.setTitle(org.apache.commons.io.FilenameUtils.getName(fullName) + " [Modified]");

		// TODO
		// formGramExample.UncolorCheckText();
		if (dialogHistory != null && dialogHistory.isVisible())
			dialogHistory.getController().updateCurrentFor(editor);
	}

	public void newGrammar(GramType gt, String author, String institution, String password, short locktype,
			String ilanguage, String olanguage, Preferences preferences)
	{
		grammar = new Grammar(gt, author, institution, password, locktype, ilanguage, olanguage, preferences);

		iFont = new Font(grammar.iFontName, grammar.iFontStyle, (int) grammar.iFontSize);
		oFont = new Font(grammar.oFontName, grammar.oFontStyle, (int) grammar.oFontSize);
		cFont = new Font(grammar.cFontName, grammar.cFontStyle, (int) grammar.cFontSize);

		lan = new Language(ilanguage);
		if (lan == null)
			return;
		if (olanguage == null)
			lan2 = new Language(ilanguage);
		lan2 = new Language(olanguage);
		if (lan2 == null)
			return;

		if (lan.rightToLeft)
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
			contractShell.getController().txtConsole
					.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}
		else
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			contractShell.getController().txtConsole
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}

		

		Graph g = new Graph();
		g.name = "Main";
		g.wholeGrammar = grammar;
		g.addNode("<E>", 30, 30);
		g.addNode("", 50, 60);
		g.saveCurrentGraphInHistory("New Grammar");
		grammar.graphs.add(g);

		resetTvGraphs();
		modified = false;
		updateFormHeader();

		editor.setTitle("Untitled");
	}

	private void resetTvGraphs()
	{
		current = 0;
		grf = grammar.graphs.get(current);

		if (dialogHistory != null && dialogHistory.isVisible())
			dialogHistory.getController().updateNewFor(editor);

		if (formGramStruct.tvGraphs.isEnabled())
		{
			formGramStruct.getController().visit(formGramStruct.tvGraphs);
			// HACK BUG FIX!
			this.editor.formGramStruct = formGramStruct;
			currentNode = (DefaultMutableTreeNode) formGramStruct.tvGraphs.getModel().getRoot();
		}
	}

	public void removeCurrentGraph()
	{
		grammar.graphs.remove(grf);
		updateFormHeader();
	}

	public void loadIntexGrammar(String fullname, GramType gt, String ilanguage, String olanguage, String encodingcode,
			Preferences preferences)
	{
		grammar = Grammar.importWithAllEmbeddedGraphs(fullname, gt, ilanguage, olanguage, encodingcode, preferences);
		if (grammar == null)
			return;
		iFont = new Font(grammar.iFontName, grammar.iFontStyle, (int) grammar.iFontSize);
		oFont = new Font(grammar.oFontName, grammar.oFontStyle, (int) grammar.oFontSize);
		cFont = new Font(grammar.cFontName, grammar.cFontStyle, (int) grammar.cFontSize);

		// init all graphs' histories
		for (Object o : grammar.graphs)
		{
			Graph g = (Graph) o;
			if (g != null)
			{
				g.history = new ArrayList<Object>();
				g.iHistory = 0;
				g.inodeconnected = -1;
				g.inodemoved = -1;
				g.saveCurrentGraphInHistory("import INTEX graph");
			}
		}

		grammar.author = null;
		grammar.institution = null;

		lan = new Language(ilanguage);
		if (lan == null)
			return;
		lan2 = new Language(olanguage);
		if (lan2 == null)
			return;

		contractShell.getController().lan = lan;

		if (lan.rightToLeft)
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}
		else
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}

		grammar.gramType = gt;
		editor.getMntmShowContract().setEnabled((gt != GramType.FLX));

		resetTvGraphs();
		modified = false;
		editor.setTitle("Untitled [Modified]");
		updateFormHeader();

		fullName = null;
	}

	public void SaveGraph(String fullname)
	{
		boolean righttoleft = lan.rightToLeft;
		grf.saveIntex(fullname, righttoleft);
	}

	public boolean addIntexGraph(String fullgname, String graphname, String encodingcode, GramType gt)
	{
		File f = new File(fullgname);
		if (!f.exists())
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find file:\n" + fullgname,
					"NooJ: graph file is not found", 2);
			return false;
		}
		Graph grf0 = Graph.loadIntex(grammar, fullgname, encodingcode, gt);
		if (grf0 == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load graph:\n" + fullgname,
					"NooJ: INTEX graph format error", 2);
			return false;
		}
		grf0.name = graphname;
		if (grf0 != null)
		{
			// make sure the new graph is not already there
			boolean alreadythere = false;
			for (int jgrf = 0; jgrf < grammar.graphs.size(); jgrf++)
			{
				Graph grf1 = grammar.graphs.get(jgrf);
				if (grf1.name == grf0.name)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Graph " + grf0.name + " already exists.",
							"NooJ: duplicate graph will not be imported", 2);
					alreadythere = true;
					break;
				}
			}
			if (!alreadythere)
			{
				grammar.graphs.add(grf0);
				ArrayList<String> recGraph = new ArrayList<String>();
				recGraph.add(graphname);
				if (!grammar.importEmbeddedGraphs(f.getParentFile().getName(), grf0, recGraph, encodingcode, gt))
				{
					return false;
				}
			}
		}
		updateFormHeader();
		return true;
	}

	public void addNoojGrammar(String fullname, String graphname, GramType gt)
	{
		
		Grammar grm0 = Grammar.loadONooJGrammar(fullname);

		if (grm0 == null)
			return;

		for (int igrf = 0; igrf < grm0.graphs.size(); igrf++)
		{
			Graph grf0 = grm0.graphs.get(igrf);
			if (igrf == 0)
				grf0.name = FilenameUtils.removeExtension((new File(fullname)).getName());
			// test to see if the graph is already there
			boolean alreadythere = false;
			for (int jgrf = 0; jgrf < grammar.graphs.size(); jgrf++)
			{
				Graph grf1 = grammar.graphs.get(jgrf);
				if (grf1.name.equals(grf0.name))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Graph " + grf0.name + " already exists!",
							"NooJ: duplicate graph will not be imported", 2);
					alreadythere = true;
					break;
				}
			}
			if (!alreadythere)
				grammar.graphs.add(grf0);
		}

		resetTvGraphs();
		editor.pGraph.invalidate();
		updateFormHeader();
	}

	public void LoadGrammar(String fullname, boolean fromCSharp)
	{
		
		grammar = Grammar.loadONooJGrammar(fullname);
		if (grammar == null)
		{
			return;
		}
		if (grammar.iFontSize == 0.0)
			grammar.iFontSize = 12.0F;
		iFont = new Font(grammar.iFontName, grammar.iFontStyle, (int) grammar.iFontSize);
		if (grammar.oFontSize == 0.0)
			grammar.oFontSize = 12.0F;
		oFont = new Font(grammar.oFontName, grammar.oFontStyle, (int) grammar.oFontSize);
		if (grammar.cFontSize == 0.0)
			grammar.cFontSize = 12.0F;
		cFont = new Font(grammar.cFontName, grammar.cFontStyle, (int) grammar.cFontSize);

		HashMap<String, String> alreadythere = new HashMap<String, String>();
		for (int ig = 0; ig < grammar.graphs.size();)
		{
			Graph g = grammar.graphs.get(ig);
			if (g == null)
			{
				grammar.graphs.remove(ig);
				continue;
			}
			String gname = g.name;
			if (gname == null || gname.equals("") || alreadythere.containsKey(gname))
			{
				grammar.graphs.remove(ig);
				continue;
			}
			alreadythere.put(gname, null);
			if (g.history == null)
			{
				// init each graph's history
				g.history = new ArrayList<Object>();
				g.iHistory = 0;
				g.inodeconnected = -1;
				g.inodemoved = -1;
				g.saveCurrentGraphInHistory("load grammar");
			}
			if (g.name.equals("Main") && ig != 0)
			{
				// switch current Main graph to first
				Graph tmp = grammar.graphs.get(0);
				grammar.graphs.set(0, g);
				grammar.graphs.set(ig, tmp);
			}
			ig++;
		}

		if (grammar.lockType != 0)
		{
			contractShell.getController().txtConsole.setEditable(false);
		}

		// correct language names
		lan = new Language(grammar.iLanguage);
		if (lan == null)
			return;
		if (lan.isoName != grammar.iLanguage)
		{
			grammar.iLanguage = lan.isoName;
			lan = new Language(grammar.iLanguage);
		}
		lan2 = new Language(grammar.oLanguage);
		if (lan2 == null)
			return;
		if (lan2.isoName != grammar.oLanguage)
		{
			grammar.oLanguage = lan2.isoName;
			lan2 = new Language(grammar.oLanguage);
		}

		if (lan.rightToLeft)
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}
		else
		{
			editor.pGraph.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}

		if (grammar.windowHeight != 0 && grammar.windowWidth != 0)
			editor.setSize(new Dimension(grammar.windowWidth, grammar.windowHeight));

		

		contractShell.getController().lan = lan;
		contractShell.getController().grammar = grammar;
		contractShell.getController().txtConsole.setText(grammar.checkText);
		if (lan.rightToLeft)
		{
			contractShell.getController().txtConsole
					.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}
		else
		{
			contractShell.getController().txtConsole
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}

		
		resetTvGraphs();
		modified = false;
		editor.setTitle((new File(fullname)).getName());
		updateFormHeader();

	}

	private void save()
	{
		save(fullName, false);
	}

	/**
	 * 
	 * Save the grammar but DO NOT use FullName
	 * 
	 */
	private void save(String fullname, boolean forNooJ)
	{
		if (fullname == null || fullname.equals(""))
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot save grammar",
					"NooJ: undefined file name", 2);
			return;
		}

		if (!forNooJ)
		{
			// WARNING IF FILENAME STARTS WITH "_"
			String fnamenoext = FilenameUtils.removeExtension((new File(fullname)).getName());
			if (fnamenoext.charAt(0) == '_')
			{
				int dr = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
						"WARNING: file name starts with \"_\". Are you sure you want to save it with this prefix?",
						"NooJ: protected resource", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						null, null);
				if (dr == JOptionPane.NO_OPTION)
					return;
			}

			// MANAGE MULTIPLE BACKUPS
			try
			{
				Utilities.savePreviousVersion(fullname, Launcher.preferences.multiplebackups);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		grammar.windowHeight = editor.getHeight();
		grammar.windowWidth = editor.getWidth();

		if (contractShell == null)
		{
			contractShell = new ContractShell(editor, grammar, lan);
		}
		grammar.checkText = contractShell.getController().txtConsole.getText();

		if (this.grammar.gramType != GramType.FLX)
		{

			try
			{
				int grammarschecksok = contractShell.getController().checkContract();
				if (grammarschecksok == 0) // grammar is broken
				{
					int dr = JOptionPane.showOptionDialog(Launcher.getDesktopPane(),
							"Grammar is broken. Do you want to save it anyway?", "NooJ: Contract failed",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (dr == JOptionPane.NO_OPTION)
						return;
				}
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}

		try
		{
			
			grammar.saveONooJGrammar(fullname);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "NooJ: cannot save grammar", "Error", 2);
			return;
		}

		modified = false;
		if (!forNooJ)
			fullName = fullname;
		editor.setTitle((new File(fullname)).getName());
	}

	public void revert()
	{
		LoadGrammar(fullName, false);
		editor.rtBox.setSize(0, 0);
		editor.rtBox.setLocation(new Point(0, 0));
		if (!this.timerSel.isRunning())
			this.timerSel.start();
		editor.invalidate();
		editor.repaint();
	}

	private void resetMouseStatus()
	{
		mouseEdited = mouseN = -1;
		mouseD = mouseM = false;
		mouseN = mouseEdited = -1;
	}

	public int getMark()
	{
		for (int inode = 2; inode < grf.hei.size(); inode++)
		{
			if (inode == mouseEdited)
				continue;
			if (!grf.areaNode(inode))
				continue;
			boolean sel = grf.selected.get(inode);
			if (!sel)
				continue;

			int x = grf.posX.get(inode);
			int y = grf.posY.get(inode);
			int w = grf.wid.get(inode);
			int h = grf.hei.get(inode);
			Rectangle rect = new Rectangle((int) ((x + w) * grf.scale), (int) ((y + h) * grf.scale), 10, 10);
			if (rect.contains(mouseX, mouseY))
				return inode;
		}
		return -1;
	}

	public void getMouseN()
	{
		float scale = grf.scale;
		mouseN = -1;

		for (int inode = 0; inode < grf.hei.size(); inode++)
		{
			if (inode == mouseEdited)
				continue;
			if (grf.areaNode(inode))
				continue; // need to look first for non area nodes

			int xleft = (int) ((grf.posX.get(inode)) * scale);
			int xright = xleft + grf.wid.get(inode);
			int ytop = (int) ((grf.posY.get(inode)) * scale) - grf.hei.get(inode);
			int ybottom = (int) ((grf.posY.get(inode)) * scale) + grf.hei.get(inode);

			if (mouseOX >= xleft && mouseOX <= xright && mouseOY >= ytop && mouseOY <= ybottom)
			{
				mouseN = inode;
				return;
			}
		}

		// same loop for area nodes
		for (int inode = 0; inode < grf.hei.size(); inode++)
		{
			if (inode == mouseEdited)
				continue;
			if (!grf.areaNode(inode))
				continue; // already done that

			int xleft = (int) ((grf.posX.get(inode)) * scale);
			int xright = xleft + (int) (grf.wid.get(inode) * scale);
			int ytop = (int) ((grf.posY.get(inode)) * scale) - (int) (grf.hei.get(inode) * scale);
			int ybottom = (int) ((grf.posY.get(inode)) * scale) + (int) (grf.hei.get(inode) * scale);

			if (mouseOX >= xleft && mouseOX <= xright && mouseOY >= ytop && mouseOY <= ybottom)
			{
				mouseN = inode;
				return;
			}
		}
	}

	public void getMouseNodes()
	{
		float scale = grf.scale;

		Rectangle SelRectangle = computeRectangle(SelectionRectangle);
		int xr = SelRectangle.x;
		int yr = SelRectangle.y;
		int x2r = xr + SelRectangle.width;
		int y2r = yr + SelRectangle.height;

		for (int inode = 0; inode < grf.hei.size(); inode++)
		{
			if (inode == mouseEdited || grf.areaNode(inode))
			{
				grf.selected.set(inode, false);
				continue;
			}

			int xleft = (int) ((grf.posX.get(inode)) * scale);
			int xright = xleft + grf.wid.get(inode);
			int ytop = (int) ((grf.posY.get(inode)) * scale) - grf.hei.get(inode);
			int ybottom = (int) ((grf.posY.get(inode)) * scale) + grf.hei.get(inode);

			if (xr <= xleft && x2r >= xleft && yr <= ytop && y2r >= ytop)
				grf.selected.set(inode, true);
			else if (xr <= xleft && x2r >= xleft && yr <= ybottom && y2r >= ybottom)
				grf.selected.set(inode, true);
			else if (xr <= xright && x2r >= xright && yr <= ytop && y2r >= ytop)
				grf.selected.set(inode, true);
			else if (xr <= xright && x2r >= xright && yr <= ybottom && y2r >= ybottom)
				grf.selected.set(inode, true);
			else
				grf.selected.set(inode, false);
		}
	}

	public String cleanup(String label)
	{
		// first: put the first "/" at the beginning of a line
		boolean found = false;
		for (int i = 0; i < label.length(); i++)
		{
			if (label.charAt(i) == '\\')
				i++;
			else if (label.charAt(i) == '"')
			{
				for (i++; i < label.length() && label.charAt(i) != '"'; i++)
					;
			}
			else if (label.charAt(i) == '<')
			{
				for (i++; i < label.length() && label.charAt(i) != '>'; i++)
					;
			}
			else
			{
				if (label.charAt(i) == '/' && !found)
				{
					found = true;
					if (i == 0)
					{
						// label == "/foo", to be replaced with "<E>\n/foo"
						label = "<E>\n" + label;
						break;
					}
					else
					{
						if (label.charAt(i - 1) != '\n')
						{
							label = label.substring(0, i) + "\n" + label.substring(i);
							break;
						}
					}
				}
			}
		}
		int nbterms = 0;
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < label.length();)
		{
			int j;
			for (j = 0; i + j < label.length() && label.charAt(i + j) != '\n'; j++)
				;
			if (j < label.length())
			{
				// we have a term from i to i+j
				if (j > 0)
				{
					// only if the term is not empty...
					String term = label.substring(i, i + j);
					if (term.charAt(0) != '/')
						term = term.trim();
					if (term.length() > 0)
					{
						nbterms++;
						if (nbterms > 1)
							res.append('\n');
						res.append(term);
					}
					i += j;
				}
				i++;
			}
			else
			{
				// we have a term from i
				String term = label.substring(i);
				if (term.length() > 0 && term.charAt(0) != '/')
					term = term.trim();
				if (term.length() > 0)
				{
					nbterms++;
					if (nbterms > 1)
						res.append('\n');
					res.append(term);
				}
				i += j;
			}
		}
		return res.toString();
	}

	private boolean hasOutput(String label)
	{
		for (int i = 0; i < label.length(); i++)
		{
			if (label.charAt(i) == '/')
				return true;
			else if (label.charAt(i) == '\\')
				i++;
			else if (label.charAt(i) == '"')
			{
				for (i++; i < label.length() && label.charAt(i) != '"'; i++)
					;
			}
			else if (label.charAt(i) == '<')
			{
				for (i++; i < label.length() && label.charAt(i) != '>'; i++)
					;
			}
		}
		return false;
	}

	public void displayRtbox(int inode)
	{
		timerSel.stop();
		grf.tColor = new Color(255, 215, 0); // Color.Gold

		int rx = (int) ((grf.posX.get(inode)) * grf.scale);
		int ry = (int) ((grf.posY.get(inode)) * grf.scale);

		
		originallabel = grf.label.get(inode);
		editor.rtBox.setText(originallabel);

		boolean hasOutput = hasOutput(editor.rtBox.getText());

		if (grf.commentNode(inode))
		{
			editor.rtBox.setFont(grf.cfont);
			editor.rtBox.setForeground(grammar.cColor);

			int height = grf.interline * editor.rtBox.getLineCount() + 10;
			int width = grf.widB.get(inode);
			if (editor.rtBox.getWidth() < 50)
				width = 50;
			editor.rtBox.setSize(width, height);

			if (!hasOutput)
				editor.rtBox.setLocation(new Point(rx, ry - editor.rtBox.getHeight() / 2));
			else
				editor.rtBox.setLocation(new Point(rx, ry - (grf.interline * (editor.rtBox.getLineCount() - 1) + 10)
						/ 2));
		}
		else if (grf.areaNode(inode))
		{
			editor.rtBox.setFont(grf.cfont);
			editor.rtBox.setForeground(grammar.cColor);

			if (!hasOutput)
				editor.rtBox.setLocation(new Point(rx, ry - editor.rtBox.getHeight() / 2));
			else
				editor.rtBox.setLocation(new Point(rx, ry - (grf.interline * (editor.rtBox.getLineCount() - 1) + 10)
						/ 2));
		}
		else
		{
			editor.rtBox.setFont(grf.ifont);
			editor.rtBox.setForeground(grammar.fColor);
			int width = grf.widB.get(inode);
			if (editor.rtBox.getWidth() < 50)
				width = 50;
			int height = grf.interline * editor.rtBox.getLineCount() + 10;
			editor.rtBox.setSize(width, height);

			if (!hasOutput)
				editor.rtBox.setLocation(new Point(rx, ry - editor.rtBox.getHeight() / 2));
			else
				editor.rtBox.setLocation(new Point(rx, ry - (grf.interline * (editor.rtBox.getLineCount() - 1) + 10)
						/ 2));
		}
		mouseEdited = inode;

		editor.rtBox.requestFocusInWindow();
		editor.rtBox.selectAll();
		editor.pGraph.invalidate();
		editor.pGraph.repaint();
	}

	public void hideRtbox()
	{
		this.timerSel.start();
		resetMouseStatus();
		editor.rtBox.setSize(0, 0); // why not setVisible(false)?
		editor.rtBox.setLocation(new Point(0, 0));
		
		mouseEdited = -1;

		editor.requestFocusInWindow();
		editor.pGraph.invalidate();
		editor.pGraph.repaint();
	}

	public Rectangle computeRectangle(Rectangle selectionrectangle)
	{
		Rectangle drawRectangle = new Rectangle();

		if (selectionrectangle.width < 0)
		{
			drawRectangle.x = selectionrectangle.x + selectionrectangle.width;
			drawRectangle.width = -selectionrectangle.width;
		}
		else
		{
			drawRectangle.x = selectionrectangle.x;
			drawRectangle.width = selectionrectangle.width;
		}

		if (selectionrectangle.height < 0)
		{
			drawRectangle.y = selectionrectangle.y + selectionrectangle.height;
			drawRectangle.height = -selectionrectangle.height;
		}
		else
		{
			drawRectangle.y = selectionrectangle.y;
			drawRectangle.height = selectionrectangle.height;
		}
		return drawRectangle;
	}

	public void modify(String modification, boolean needtovisit, boolean invalidate)
	{
		grf.debugInvisible = false;
		timerDbg.stop();

		modified = true;
		if (fullName == null)
			editor.setTitle("Untitled [Modified]");
		else
			editor.setTitle((new File(fullName)).getName() + " [Modified]");

		if (mouseD) // in the middle of a move
			return;

		if (contractShell.isVisible())
			contractShell.getController().UncolorCheckText();

		if (invalidate)
		{
			formGramStruct.tvGraphs.setEnabled(false);
		}
		else if (needtovisit)
		{
			// compute the full path of the current graph in the tree node structure
			ArrayList<String> fullpath = new ArrayList<String>();
			fullpath.add(currentNode.toString());

			currentNode = (DefaultMutableTreeNode) currentNode.getParent();

			// workaround for JTree; root node is ""
			while (currentNode != null && !currentNode.toString().equals(""))
			{
				fullpath.add(currentNode.toString());
				if (currentNode.getParent() != null)
				{
					currentNode = (DefaultMutableTreeNode) currentNode.getParent();
				}
				else
				{
					break;
				}
			}

			// revisit the tree view
			formGramStruct.getController().visit(formGramStruct.tvGraphs);

			// retrieve current node's ancestor
			Collections.reverse(fullpath);
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) ((DefaultTreeModel) formGramStruct.tvGraphs
					.getModel()).getRoot()).getFirstChild();

			for (int ic = 0; ic < root.getChildCount(); ic++)
			{
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode) root.getChildAt(ic);
				if (tn.toString() == fullpath.get(0))
				{
					currentNode = tn;
					break;
				}
			}

			// go down to current node's
			for (int i = 1; i < fullpath.size(); i++)
			{
				String label = fullpath.get(i);
				for (int ic = 0; ic < currentNode.getChildCount(); ic++)
				{
					DefaultMutableTreeNode tn = (DefaultMutableTreeNode) currentNode.getChildAt(ic);
					if (tn.toString() == label)
					{
						currentNode = tn;
						break;
					}
				}
			}

			if (currentNode == null)
				currentNode = root;

		
			formGramStruct.tvGraphs.expandPath(new TreePath(currentNode.getPath()));
		}

		// update History
		if (grf != null)
			grf.saveCurrentGraphInHistory(modification);
		if (dialogHistory != null && dialogHistory.isVisible())
			dialogHistory.getController().updateNewFor(editor);

		// update graph
		try
		{
			editor.setSelected(true);
		}
		catch (PropertyVetoException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_PROPERTY_VETO,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		editor.invalidate();
		editor.repaint();
	}

	private int findGraph(String gname)
	{
		int index = -1;
		for (int ig = 0; ig < grammar.graphs.size(); ig++)
		{
			Graph grf2 = grammar.graphs.get(ig);
			if (grf2 != null && grf2.name.equals(gname))
			{
				index = ig;
				break;
			}
		}
		return index;
	}

	public void findAndLoadGraph(String gname)
	{
		int index = findGraph(gname);
		if (index == -1)
		{
			int dr = JOptionPane.showConfirmDialog(editor, "Graph " + gname + " not found. Create it?",
					"NooJ: graph does not exist", JOptionPane.YES_NO_OPTION); // default button title

			if (dr == JOptionPane.NO_OPTION)
				return;
			Graph ngrf = new Graph();
			ngrf.wholeGrammar = grammar;
			ngrf.name = gname;
			ngrf.addNode("<E>", 30, 30);
			ngrf.addNode("", 50, 60);

			grammar.graphs.add(ngrf);
			current = grammar.graphs.size();
		}
		else
		{
			current = index;
		}
		this.grf = grammar.graphs.get(current);
		updateGraphStructure();

		if (this.dialogHistory != null && dialogHistory.isVisible())
			dialogHistory.getController().updateNewFor(editor);
	}

	public boolean findPattern(String pattern, int startnode, RefObject<Integer> graph_nb, RefObject<Integer> node_nb,
			RefObject<Integer> position, RefObject<Integer> length)
	{
		node_nb.argvalue = position.argvalue = length.argvalue = 0;
		Graph fgrf = grammar.graphs.get(graph_nb.argvalue);
		for (int inode = 0; inode < fgrf.label.size(); inode++)
		{
			if (inode == 1)
				continue; // no need to look in terminal node
			String buf = grf.label.get(inode);
			int pos = buf.indexOf(pattern, 0);
			if (pos == -1)
				continue;

			// I found the node
			node_nb.argvalue = inode;
			position.argvalue = pos;
			length.argvalue = pattern.length();
			return true;
		}

		// the pattern was not found in current graph: let's loop over all the other graphs
		for (int igrf = 0; igrf < grammar.graphs.size(); igrf++)
		{
			if (igrf == graph_nb.argvalue)
				continue;
			fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				int pos = buf.indexOf(pattern, 0);
				if (pos == -1)
					continue;

				// I found the node
				graph_nb.argvalue = igrf;
				node_nb.argvalue = inode;
				position.argvalue = pos;
				length.argvalue = pattern.length();
				return true;
			}
		}
		return false;
	}

	public boolean findNextPattern(String pattern, int startnode, RefObject<Integer> graph_nb,
			RefObject<Integer> node_nb, RefObject<Integer> position, RefObject<Integer> length)
	{
		Graph fgrf = grammar.graphs.get(graph_nb.argvalue);
		for (int inode = node_nb.argvalue; inode < fgrf.label.size(); inode++)
		{
			if (inode == 1)
				continue; // no need to look in terminal node
			String buf = this.grf.label.get(inode);
			int pos;
			if (inode == node_nb.argvalue)
				pos = buf.indexOf(pattern, position.argvalue + length.argvalue);
			else
				pos = buf.indexOf(pattern, 0);
			if (pos == -1)
				continue;

			// I found the node
			node_nb.argvalue = inode;
			position.argvalue = pos;
			length.argvalue = pattern.length();
			return true;
		}

		// the pattern was not found in current graph: let's loop over all the other graphs
		for (int igrf = graph_nb.argvalue + 1; igrf < grammar.graphs.size(); igrf++)
		{
			fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				int pos = buf.indexOf(pattern, 0);
				if (pos == -1)
					continue;

				// I found the node
				graph_nb.argvalue = igrf;
				node_nb.argvalue = inode;
				position.argvalue = pos;
				length.argvalue = pattern.length();
				return true;
			}
		}
		return false;
	}

	public int replaceAll(String pattern, String replacement)
	{
		int count = 0;
		for (int igrf = 0; igrf < grammar.graphs.size(); igrf++)
		{
			Graph fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				StringBuilder res = new StringBuilder();

				int start = 0;
				boolean found = false;
				int pos = 0;
				while (pos != -1)
				{
					pos = buf.indexOf(pattern, start);
					if (pos == -1)
						break;
					found = true;
					count++;
					res.append(buf.substring(start, start + pos - start));
					res.append(replacement);
					start = pos + pattern.length();
				}
				if (found)
				{
					res.append(buf.substring(start));
					fgrf.label.set(inode, res.toString());
				}
			}
		}
		if (count > 0)
			this.modify("replacements", true, true);
		return count;
	}

	public int replaceAllPerl(Pattern rexp, String replacement)
	{
		int count = 0;
		for (int igrf = 0; igrf < grammar.graphs.size(); igrf++)
		{
			Graph fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				Matcher matchCollection = rexp.matcher(buf);
				int matchCount = 0;
				while (matchCollection.find())
				{
					matchCount++;
				}

				if (matchCount == 0)
					continue;
				count += matchCount;
				StringBuilder res = new StringBuilder();
				int start = 0;
				// reset the matcher (position affected by the counter above)
				matchCollection.reset();
				for (int i = 0; i < matchCount; i++)
				{
					// get the next match;
					matchCollection.find();
					int pos = matchCollection.start();
					int length = matchCollection.group().length();
					res.append(buf.substring(start, start + pos - start));
					res.append(replacement);
					start = pos + length;
				}
				res.append(buf.substring(start));
				fgrf.label.set(inode, res.toString());
			}
		}
		if (count > 0)
			this.modify("replacements", true, true);
		return count;
	}

	public boolean findPattern(Pattern rexp, int startnode, RefObject<Integer> graph_nb, RefObject<Integer> node_nb,
			RefObject<Integer> position, RefObject<Integer> length)
	{
		node_nb.argvalue = position.argvalue = length.argvalue = 0;
		Graph fgrf = grammar.graphs.get(graph_nb.argvalue);
		for (int inode = 0; inode < fgrf.label.size(); inode++)
		{
			if (inode == 1)
				continue; // no need to look in terminal node
			String buf = this.grf.label.get(inode);
			Matcher matchCollection = rexp.matcher(buf);
			int matchCount = 0;
			while (matchCollection.find())
			{
				matchCount++;
			}
			if (matchCount == 0)
				continue;

			matchCollection.reset();

			// I found the node
			matchCollection.find();
			node_nb.argvalue = inode;
			position.argvalue = matchCollection.start();
			length.argvalue = matchCollection.group().length();
			return true;
		}

		// the pattern was not found in current graph: let's loop over all the other graphs
		for (int igrf = 0; igrf < grammar.graphs.size(); igrf++)
		{
			if (igrf == graph_nb.argvalue)
				continue;
			fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				Matcher matchCollection = rexp.matcher(buf);
				int matchCount = 0;
				while (matchCollection.find())
				{
					matchCount++;
				}
				if (matchCount == 0)
					continue;

				// I found the node
				matchCollection.find();
				graph_nb.argvalue = igrf;
				node_nb.argvalue = inode;
				position.argvalue = matchCollection.start();
				length.argvalue = matchCollection.group().length();

				return true;
			}
		}
		return false;
	}

	public boolean findNextPattern(Pattern rexp, int startnode, RefObject<Integer> graph_nb,
			RefObject<Integer> node_nb, RefObject<Integer> position, RefObject<Integer> length)
	{
		Graph fgrf = grammar.graphs.get(graph_nb.argvalue);
		for (int inode = node_nb.argvalue; inode < fgrf.label.size(); inode++)
		{
			if (inode == 1)
				continue; // no need to look in terminal node
			String buf = this.grf.label.get(inode);
			Matcher matchCollection = rexp.matcher(buf);
			if (inode == node_nb.argvalue)
				matchCollection.region(position.argvalue + length.argvalue, buf.length());
			int matchCount = 0;
			while (matchCollection.find())
			{
				matchCount++;
			}
			if (matchCount == 0)
				continue;

			matchCollection.reset();
			if (inode == node_nb.argvalue)
				matchCollection.region(position.argvalue + length.argvalue, buf.length());

			// I found the node
			matchCollection.find();
			node_nb.argvalue = inode;
			position.argvalue = matchCollection.start();
			length.argvalue = matchCollection.group().length();
			return true;
		}

		// the pattern was not found in current graph: let's loop over all the other graphs
		for (int igrf = graph_nb.argvalue + 1; igrf < grammar.graphs.size(); igrf++)
		{
			fgrf = grammar.graphs.get(igrf);
			for (int inode = 0; inode < fgrf.label.size(); inode++)
			{
				if (inode == 1)
					continue; // no need to look in terminal node
				String buf = fgrf.label.get(inode);
				Matcher matchCollection = rexp.matcher(buf);
				int matchCount = 0;
				while (matchCollection.find())
				{
					matchCount++;
				}

				if (matchCount == 0)
					continue;

				matchCollection.reset();

				// I found the node
				matchCollection.find();
				graph_nb.argvalue = igrf;
				node_nb.argvalue = inode;
				position.argvalue = matchCollection.start();
				length.argvalue = matchCollection.group().length();
				return true;
			}
		}
		return false;
	}

	public boolean UpdateGraphStructureFromTop(TreeNode root)
	{
		while (root != null)
		{
			if (root.toString().equals(grf.name))
			{
				currentNode = (DefaultMutableTreeNode) root;
				formGramStruct.tvGraphs.grabFocus();
				if (formGramStruct.isVisible())
				{
					formGramStruct.tvGraphs.setSelectionPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
					((DefaultTreeModel) formGramStruct.tvGraphs.getModel()).reload();
					return true;
				}
			}
			// look up children
			if (((DefaultMutableTreeNode) root).getChildCount() != 0)
			{
				if (UpdateGraphStructureFromTop(((DefaultMutableTreeNode) root).getFirstChild()) == true)
					return true;
			}
			root = ((DefaultMutableTreeNode) root).getNextSibling();
		}
		return false;
	}

	private void updateGraphStructure()
	{
		// find corresponding node in the structure
		if (currentNode.getChildCount() != 0)
		{
			TreeNode node = currentNode.getFirstChild();
			while (node != null && !node.toString().equals(grf.name))
			{
				node = ((DefaultMutableTreeNode) node).getNextSibling();
			}
			if (node != null)
			{
				currentNode = (DefaultMutableTreeNode) node;
				((DefaultTreeModel) formGramStruct.tvGraphs.getModel()).reload();
				formGramStruct.tvGraphs.setSelectionPath(new TreePath(((DefaultMutableTreeNode) node).getPath()));
			}
		}
		editor.rtBox.grabFocus();
	}

	public void saveGrammar()
	{
		if (fullName == null)
			saveAsGrammar();
		else
		{
			editor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			save();
			editor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void saveAsGrammar()
	{
		FileNameExtensionFilter filterGrammar = null;
		if (grammar.gramType == GramType.MORPHO)
		{
			String currentDirToBeSetPath = FilenameUtils.concat(Paths.docDir,
					org.apache.commons.io.FilenameUtils.concat(lan.isoName, "Lexical Analysis"));
			Launcher.getSaveGramChooser().setCurrentDirectory(new File(currentDirToBeSetPath));

			filterGrammar = new FileNameExtensionFilter("NooJ Morphology (*.nom)", "nom");
			Launcher.getSaveGramChooser().addChoosableFileFilter(filterGrammar);
			Launcher.getSaveGramChooser().setFileFilter(filterGrammar);
		}
		else if (grammar.gramType == GramType.FLX)
		{
			filterGrammar = new FileNameExtensionFilter("NooJ Inflection (*.nof)", "nof");
			Launcher.getSaveGramChooser().addChoosableFileFilter(filterGrammar);
			Launcher.getSaveGramChooser().setFileFilter(filterGrammar);

		}
		else if (grammar.gramType == GramType.SYNTAX)
		{
			filterGrammar = new FileNameExtensionFilter("NooJ Grammar (*.nog)", "nog");
			Launcher.getSaveGramChooser().addChoosableFileFilter(filterGrammar);
			Launcher.getSaveGramChooser().setFileFilter(filterGrammar);
		}

		if (Launcher.getSaveGramChooser().showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
			return;

		editor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		String extension = Launcher.getSaveGramChooser().getFileFilter().getDescription();
		String ext = "";
		if (extension == "NooJ Inflection (*.nof)")
		{
			ext = ".nof";
		}
		else if (extension == "NooJ Morphology (*.nom)")
		{
			ext = ".nom";
		}
		else if (extension == "NooJ Grammar (*.nog)")
		{
			ext = ".nog";
		}
		fullName = Launcher.getSaveGramChooser().getSelectedFile().getAbsolutePath() + ext;
		if (fullName == null || fullName.equals(""))
		{
			JOptionPane.showMessageDialog(editor, "Cannot set file name to '" + fullName + "'");
			editor.getLastCursor();
			return;
		}
		save();
		editor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void saveGrammarForNooJ()
	{
		String languagename = lan.isoName;
		String dirname;
		if (grammar.gramType == GramType.SYNTAX)
			dirname = FilenameUtils.concat(
					FilenameUtils.concat(Paths.applicationDir, "resources"),
					FilenameUtils.concat("initial",
							org.apache.commons.io.FilenameUtils.concat(languagename, "Syntactic Analysis")));
		else
			dirname = FilenameUtils.concat(FilenameUtils.concat(Paths.applicationDir, "resources"),
					FilenameUtils.concat("initial", FilenameUtils.concat(languagename, "Lexical Analysis")));

		if (fullName == null)
		{
			JOptionPane.showMessageDialog(editor, "Name this grammar first", "NooJ: Grammar has no name",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String fname = new File(fullName).getName();
		String noojname = dirname + System.getProperty("file.separator") + fname;

		editor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		save(noojname, true);
		editor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		JOptionPane.showMessageDialog(editor, "File " + noojname + " has been updated", "NooJ Update",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Function zooms in/out Grammar in desired percentage.
	 * 
	 * @param zoomPercentage
	 *            - desired zooming percent
	 */

	public void zoom(int zoomPercentage)
	{
		this.grf.fits = zoomPercentage;
		editor.repaint();
	}

	/**
	 * Function deletes recursively active graph and its children.
	 */

	public void deleteGraphAndItsChildren()
	{
		if (this.currentNode == null)
			return;

		String gName = grf.name;

		if (this.grammar.graphs.size() > 1)
		{
			// The fix! Modify needs to be done before recursive delete! Otherwise, modify modifies nonexistent grammar
			// structure which causes major bug.
			modify("recursively delete graph " + gName, true, false);
			this.formGramStruct.getController().recursiveDelete(this.currentNode, this.current);
		}

		this.updateFormHeader();
	}

	public FindReplaceDialog getFindReplaceDialog()
	{
		return findReplaceDialog;
	}

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog)
	{
		this.findReplaceDialog = findReplaceDialog;
	}

	public String getFullName()
	{
		return fullName;
	}

	public boolean isModified()
	{
		return modified;
	}

	public Timer getTimerDbg()
	{
		return timerDbg;
	}
}