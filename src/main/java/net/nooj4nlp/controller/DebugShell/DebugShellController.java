package net.nooj4nlp.controller.DebugShell;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.Engine;
import net.nooj4nlp.engine.Gram;
import net.nooj4nlp.engine.GramType;
import net.nooj4nlp.engine.Grammar;
import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.MatchType;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.Paths;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.components.DebugJTableRenderer;
import net.nooj4nlp.gui.components.DebugJTreeRenderer;
import net.nooj4nlp.gui.main.Launcher;
import net.nooj4nlp.gui.shells.DebugShell;

/**
 * Controller class of the Debug Shell.
 */

public class DebugShellController
{
	private DebugShell debugShell;
	private GrammarEditorShellController grammarController;

	private Language lan;

	/**
	 * Constructor.
	 * 
	 * @param debugShell
	 *            - debug shell window
	 * @param grammarController
	 *            - controller of the recently opened grammar
	 */

	public DebugShellController(DebugShell debugShell, GrammarEditorShellController grammarController)
	{
		this.debugShell = debugShell;
		this.grammarController = grammarController;

		this.lan = null;
	}

	/**
	 * Function implements action taken after pressing any key of Debug's combo box.
	 * 
	 * @param e
	 *            - event to be handled
	 */

	public void comboPressedKeyEvent(KeyEvent e)
	{
		clearAllItems(this.debugShell, true, true);

		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			debug();
		else
			stopBlinking();
	}

	/**
	 * Function implements action taken when Debug's button is pressed.
	 */

	public void buttonPressedFunction()
	{
		clearAllItems(this.debugShell, true, true);

		debug();
	}

	/**
	 * Main back-end function of Debug shell.
	 */

	private void debug()
	{
		clearAllItems(this.debugShell, true, false);

		String comboText = this.debugShell.getComboExpression().getSelectedItem().toString();
		ArrayList<Object> aDebug = debugLine(comboText);

		if (aDebug != null && aDebug.size() > 1)
		{
			for (int i = 1; i < aDebug.size(); i += 3)
			{
				int lastPosition = 0;
				ArrayList<Object> aNodes = (ArrayList<Object>) aDebug.get(i);
				ArrayList<Object> aInputs = (ArrayList<Object>) aDebug.get(i + 1);
				String output = aDebug.get(i + 2) == null ? null : aDebug.get(i + 2).toString();

				StringBuilder sbI = new StringBuilder();
				StringBuilder sbO = new StringBuilder();

				for (int j = 0; j < aNodes.size(); j++)
				{
					if (aNodes.get(j) instanceof String)
						sbI.append(" (\"" + aNodes.get(j).toString() + "\" ");
					else
					{
						int nodeNb = (Integer) aNodes.get(j);

						if (nodeNb == 1)
						{
							sbI.append(" )");
							while (j + 1 < aNodes.size() && (Integer) aNodes.get(j + 1) == -1)
								j++;
						}
						else if (j < aInputs.size())
						{
							if (aInputs.get(j) instanceof String)
							{
								// morphological analysis
								String morpheme = aInputs.get(j).toString();

								if (morpheme != null && morpheme.length() > 2 && morpheme.charAt(0) == '$'
										&& morpheme.charAt(1) == '(')
									sbI.append(morpheme + " ");

								else if (morpheme == "$)")
									sbI.append(" " + morpheme + " ");

								else
									sbI.append(morpheme);
							}

							else
							{
								// syntactic analysis
								int position = ((Double) aInputs.get(j)).intValue();

								if (position > lastPosition)
								{
									// HACK
									while (position > comboText.length())
										position--;

									String tok = comboText.substring(lastPosition, position);
									sbI.append(tok);
									lastPosition = position;
								}
							}
						}
					}
				}

				if (output != null && !output.equals(""))
					sbO.append(output);

				Color colorOfARow;
				String debugString = aDebug.get(0).toString();

				if (debugString.equals("perfect"))
					// colorOfARow = Color.GREEN;
					colorOfARow = new Color(0, 128, 0);
				else if (debugString.equals("partial"))
					colorOfARow = Color.BLUE;
				else
					colorOfARow = Color.RED;

				Object[] rowObject = new Object[] { sbI.toString(), sbO.toString(), aNodes.clone() };

				JTable debugTable = this.debugShell.getTableTraces();
				DefaultTableModel tableModel = (DefaultTableModel) debugTable.getModel();
				DebugJTableRenderer renderer = this.debugShell.getCustomTableRenderer();
				renderer.getColoredRowsMap().put(tableModel.getRowCount(), colorOfARow);

				tableModel.addRow(rowObject);
			}
		}
	}

	/**
	 * Function responsible for debugging input string through whole opened Grammar.
	 * 
	 * @param currentLine
	 *            - input text of Debug's combo box
	 * @return - resulting objects of Debug as ArrayList
	 */

	private ArrayList<Object> debugLine(String currentLine)
	{
		Grammar grammar = grammarController.grammar;

		if (grammar == null)
			return null;

		if (this.lan == null)
			this.lan = grammarController.lan;

		RefObject<Language> lanRef = new RefObject<Language>(this.lan);

		Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		this.lan = lanRef.argvalue;

		String errorMessage = "";

		RefObject<String> errorMessageRef = new RefObject<String>(errorMessage);

		try
		{
			if (!engine.loadResources(Launcher.preferences.ldic.get(this.lan.isoName),
					Launcher.preferences.lsyn.get(this.lan.isoName), true, errorMessageRef))
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessageRef.argvalue,
						Constants.GRAMMAR_WARNING_CANNOT_LOAD_LINGUISTIC_RESOURCE, JOptionPane.WARNING_MESSAGE);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// Implemented compileAll instead of debugCompileAll.
		errorMessage = grammar.compileAll(engine);

		if (errorMessage != null)
		{
			Dic.writeLog(errorMessage);
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage, Constants.NOOJ_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		String line = currentLine;

		if (grammar.gramType == GramType.MORPHO)
		{
			Gram grm = grammar.grams.get("Main");

			if (grm == null || grm.states == null || grm.states.size() < 2)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + grammar.fullName
						+ " has no Main graph!", Constants.GRAMMAR_NO_MAIN_GRAPH, JOptionPane.ERROR_MESSAGE);
				return null;
			}

			RefObject<ArrayList<Integer>> solLengthsRef = new RefObject<ArrayList<Integer>>(new ArrayList<Integer>());
			RefObject<ArrayList<ArrayList<Object>>> solNodesRef = new RefObject<ArrayList<ArrayList<Object>>>(
					new ArrayList<ArrayList<Object>>());
			RefObject<ArrayList<ArrayList<String>>> solInputsRef = new RefObject<ArrayList<ArrayList<String>>>(
					new ArrayList<ArrayList<String>>());
			RefObject<ArrayList<ArrayList<String>>> solOutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
					new ArrayList<ArrayList<String>>());

			// Implemented morphoMatch instead of debugMorphoMatch.
			int da = grammar.morphoMatch("Main", line, grm, solLengthsRef, solInputsRef, solOutputsRef, solNodesRef);

			ArrayList<Integer> solLengths = solLengthsRef.argvalue;
			ArrayList<ArrayList<Object>> solNodes = solNodesRef.argvalue;
			ArrayList<ArrayList<String>> solInputs = solInputsRef.argvalue;
			ArrayList<ArrayList<String>> solOutputs = solOutputsRef.argvalue;

			if (da > 0)
			{
				boolean perfectMatch = false;
				int longestMatch = -1;
				ArrayList<Object> res = new ArrayList<Object>();

				for (int iSol = 0; iSol < solLengths.size(); iSol++)
				{
					int l = solLengths.get(iSol);

					if (l == line.length())
						perfectMatch = true;

					else if (l > longestMatch)
						longestMatch = l;
				}

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					if (perfectMatch)
					{
						int stL = solLengths.get(iSol);

						if (stL < line.length())
							continue;

						ArrayList<Object> aSolNode = solNodes.get(iSol);
						ArrayList<String> aSolTrace = solInputs.get(iSol);
						ArrayList<String> stO = solOutputs.get(iSol);

						// translate output trace into a string, and process variables
						StringBuilder oTrace = grammar.processVariablesInOutputs(aSolTrace, stO);

						// translate input trace into a string
						

						// is there any remaining lexical constraint?
						if (grammar.isComplex(oTrace)) // complex result to be checked => <$PREFer=V>
						{
							String errorMessage2 = "";
							RefObject<String> errorMessageRef2 = new RefObject<String>(errorMessage2);

							ArrayList<ArrayList<String>> cSols = grammar.processConstraints(oTrace.toString(),
									errorMessageRef2);
							errorMessage2 = errorMessageRef2.argvalue;

							if (cSols == null || cSols.size() == 0)
							{
								res.add(aSolNode);
								res.add(aSolTrace);
								res.add(errorMessage2); // "*DOES NOT CHECK* :" + otrace.ToString());
								continue;
							}

							ArrayList<ArrayList<String>> listOfSols = grammar.defactorize(cSols);

							for (int icSol = 0; icSol < listOfSols.size(); icSol++)
							{
								ArrayList<String> los = listOfSols.get(icSol);
								engine.processELCSFVariables(los, line);
							
								StringBuilder resTrace = grammar.computeInput(los);
								res.add(aSolNode);
								res.add(aSolTrace);
								res.add(resTrace.toString());
							}
						}

						else
						// simple result: => tzar,N+Hum+m+s
						{
							res.add(aSolNode);
							res.add(aSolTrace);
							res.add(oTrace.toString());
						}
					}

					else
					{
						// get only longest matches
						int stl = solLengths.get(iSol);

						if (stl < longestMatch)
							continue;

						ArrayList<Object> aSolNode = solNodes.get(iSol);
						ArrayList<String> aSolTrace = solInputs.get(iSol);
						res.add(aSolNode);
						res.add(aSolTrace); // do not get variables content because it is not a perfect match anyway
						res.add(null);
					}
				}

				if (perfectMatch)
					res.add(0, "perfect");
				else
					res.add(0, "partial");

				return res;
			}

			else
			{
				// match failure: relaunch the morphological parser in FAILURE mode

				// TODO ask Max if this is the right function.
				da = grammar.morphoMatch("Main", line, grm, solLengthsRef, solInputsRef, solOutputsRef, solNodesRef);
				solLengths = solLengthsRef.argvalue;
				solNodes = solNodesRef.argvalue;
				solInputs = solInputsRef.argvalue;
				solOutputs = solOutputsRef.argvalue;

				int longestMatch = -1;
				ArrayList<Object> res = new ArrayList<Object>();

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					ArrayList<Object> n = solNodes.get(iSol);
					int l = n.size();

					if (l > longestMatch)
						longestMatch = l;
				}

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					ArrayList<Object> aSolNode = solNodes.get(iSol);

					if (aSolNode.size() < longestMatch)
						continue;

					ArrayList<String> aSolTrace = solInputs.get(iSol);
					res.add(aSolNode);
					res.add(aSolTrace); // do not get variables content because it is not a perfect match anyway
					res.add(null);
				}

				res.add(0, "failure");
				return res;
			}
		}

		else
	
		{
			// create a Ntext with file and directory...
			Ntext myText = new Ntext(this.lan.isoName);

			myText.buffer = line;
			myText.mft = engine.delimit(myText);

			// linguistic analysis using both lexical and syntactic resources
			HashMap<String, ArrayList<String>> simpleWordCache = new HashMap<String, ArrayList<String>>();

			myText.annotations = new ArrayList<Object>();

			RefObject<String> errorMessageRef2 = new RefObject<String>(errorMessage);

			if (!engine.tokenize(null, myText, myText.annotations, simpleWordCache, errorMessageRef2))
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessageRef2.argvalue,
						Constants.NOOJ_TOKENIZER_ERROR, JOptionPane.ERROR_MESSAGE);
				myText.annotations = null;
				myText.hLexemes = null;
				myText.hUnknowns = null;
				return null;
			}

			simpleWordCache = null;
			myText.hPhrases = new HashMap<String, Integer>();

			RefObject<String> errorMessageRef3 = new RefObject<String>("");

			// first apply grammars in dictionary/grammar pairs
			if (engine.synGrms != null && engine.synGrms.size() > 0)
			{
				try
				{
					boolean applyRes = engine.applyAllGrammars(null, myText, myText.annotations, 0, errorMessageRef3);
					errorMessage = errorMessageRef3.argvalue;

					if (!applyRes && errorMessage != null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
								Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
						return null;
					}

					if (!applyRes)
					{
						myText.hPhrases = null;
						return null;
					}

					myText.cleanupBadAnnotations(myText.annotations);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return null;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}

			errorMessageRef3 = new RefObject<String>("");

			// then apply syntactic grammars
			if (engine.synGrms != null && engine.synGrms.size() > 0)
			{
				try
				{
					boolean applyRes = engine.applyAllGrammars(null, myText, myText.annotations, 1, errorMessageRef3);
					errorMessage = errorMessageRef3.argvalue;

					if (!applyRes && errorMessage != null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), errorMessage,
								Constants.NOOJ_SINT_PARSING_ERROR, JOptionPane.ERROR_MESSAGE);
						return null;
					}

					if (!applyRes)
					{
						myText.hPhrases = null;
						return null;
					}

					myText.cleanupBadAnnotations(myText.annotations);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return null;
				}
				catch (ClassNotFoundException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
							Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}

			// apply grammar
			RefObject<ArrayList<Double>> solLengthsRef = new RefObject<ArrayList<Double>>(new ArrayList<Double>());
			RefObject<ArrayList<ArrayList<Object>>> solNodesRef = new RefObject<ArrayList<ArrayList<Object>>>(
					new ArrayList<ArrayList<Object>>());
			RefObject<ArrayList<ArrayList<Double>>> solInputsRef = new RefObject<ArrayList<ArrayList<Double>>>(
					new ArrayList<ArrayList<Double>>());
			RefObject<ArrayList<ArrayList<String>>> solOutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
					new ArrayList<ArrayList<String>>());
			RefObject<ArrayList<ArrayList<String>>> solVariablesRef = new RefObject<ArrayList<ArrayList<String>>>(
					new ArrayList<ArrayList<String>>());

			Gram grm = grammar.grams.get("Main");

			if (grm == null || grm.states == null || grm.states.size() < 2)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar " + grammar.fullName
						+ " has no Main graph!", Constants.GRAMMAR_NO_MAIN_GRAPH, JOptionPane.ERROR_MESSAGE);
				return null;
			}

			ArrayList<String> recursive = new ArrayList<String>();

			// Implemented syntaxMatch instead of debugMorphoMatch.
			int da = grammar.syntaxMatch("Main", 0, line, 0.0, 1, myText.mft, myText.annotations, grm, solLengthsRef,
					solInputsRef, solVariablesRef, solOutputsRef, solNodesRef, MatchType.LONGEST, true, false,
					recursive);

			ArrayList<Double> solLengths = solLengthsRef.argvalue;
			ArrayList<ArrayList<Object>> solNodes = solNodesRef.argvalue;
			ArrayList<ArrayList<String>> solVariables = solVariablesRef.argvalue;
			ArrayList<ArrayList<Double>> solInputs = solInputsRef.argvalue;
			ArrayList<ArrayList<String>> solOutputs = solOutputsRef.argvalue;

			if (da > 0)
			{
				boolean partialMatch = false;
				ArrayList<Object> res = new ArrayList<Object>();

				for (int iSol = 0; iSol < solLengths.size(); iSol++)
				{
					double l = solLengths.get(iSol);

					if (l < line.length())
						partialMatch = true;
				}

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					ArrayList<Object> aSolNode = solNodes.get(iSol);
					ArrayList<Double> relAddresses = solInputs.get(iSol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[1]);

					int rightMargin = solLengths.get(iSol).intValue();
					boolean check = true;

					ArrayList<ArrayList<String>> resOutputs = null;
					ArrayList<HashMap<String, String>> resVariables = new ArrayList<HashMap<String, String>>();
					resVariables.add(new HashMap<String, String>());

					RefObject<ArrayList<ArrayList<String>>> resOutputsRef = new RefObject<ArrayList<ArrayList<String>>>(
							resOutputs);
					RefObject<ArrayList<HashMap<String, String>>> resVariablesRef = new RefObject<ArrayList<HashMap<String, String>>>(
							resVariables);
					RefObject<String> errMessageRef = new RefObject<String>("");

					if (Dic.isThereALexicalConstraint(solOutputs.get(iSol)))
					{
						try
						{
							check = engine.newProcessConstraints(line, myText.mft, myText.annotations, 1, grammar,
									solInputs.get(iSol), rightMargin, solVariables.get(iSol), 0.0,
									solOutputs.get(iSol), resOutputsRef, resVariablesRef, 0, errMessageRef);
						}
						catch (IOException e)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
									Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
							return null;
						}
						catch (ClassNotFoundException e)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.CANNOT_LOAD_FILE,
									Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
							return null;
						}
					}

					resOutputs = resOutputsRef.argvalue;
					resVariables = resVariablesRef.argvalue;
					errorMessage = errMessageRef.argvalue;

					if (!check)
					{
						// the input matches, but one of the constraints fails
						res.add(aSolNode);
						res.add(absAddresses);

						ArrayList<Object> seqOfAnnotations = engine.mergeIntoAnnotations(line, 1, 0.0,
								solLengths.get(iSol), absAddresses, solOutputs.get(iSol), false);

						if (seqOfAnnotations != null && seqOfAnnotations.size() > 0)
						{
							if (!check)
								res.add(errorMessage + ": " + seqOfAnnotations.get(1).toString());
							else
								res.add(seqOfAnnotations.get(1).toString());
						}
						else
							res.add(null);
					}
					else
					{
						
						if (resOutputs == null || resOutputs.size() == 0)
						{
							// no output
							res.add(aSolNode);
							res.add(absAddresses);
							res.add(null);
						}

						else
						{
							for (ArrayList<String> output2 : resOutputs)
							{
								res.add(aSolNode);
								res.add(absAddresses);
								ArrayList<Object> seqOfAnnotations = engine.mergeIntoAnnotations(line, 1, 0.0,
										solLengths.get(iSol), absAddresses, output2, false);

								if (seqOfAnnotations != null && seqOfAnnotations.size() > 0)
									res.add(seqOfAnnotations.get(1).toString());
								else
									res.add(null);
							}
						}
					}
				}

				if (partialMatch)
					res.add(0, "partial");
				else
					res.add(0, "perfect");

				return res;
			}

			else
			{
				// match failure: relaunch the syntactic parser in FAILURE mode
				recursive = new ArrayList<String>();

				// Implemented failureSyntaxMatch instead of failureDebugSyntaxMatch.
				da = grammar.failureSyntaxMatch("Main", 0, line, 0, 1, myText.mft, myText.annotations, grm,
						solLengthsRef, solInputsRef, solVariablesRef, solOutputsRef, solNodesRef, MatchType.LONGEST,
						true, false, recursive);

				if (da == 0)
					return null;

				solLengths = solLengthsRef.argvalue;
				solNodes = solNodesRef.argvalue;
				solVariables = solVariablesRef.argvalue;
				solInputs = solInputsRef.argvalue;
				solOutputs = solOutputsRef.argvalue;

				ArrayList<Object> res = new ArrayList<Object>();

				// computes the longest length
				double longestLength = 0.0;

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					ArrayList<Double> relAddresses = solInputs.get(iSol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[1]);
					double len = absAddresses.get(absAddresses.size() - 1);

					if (len > longestLength)
						longestLength = len;
				}

				for (int iSol = 0; iSol < solNodes.size(); iSol++)
				{
					ArrayList<Object> aSolNode = solNodes.get(iSol);
					ArrayList<Double> relAddresses = solInputs.get(iSol);
					ArrayList<Double> absAddresses = Engine.rel2Abs(relAddresses, myText.mft.tuAddresses[1]);
					double len = absAddresses.get(absAddresses.size() - 1);

					if (len == longestLength)
					{
						res.add(aSolNode);
						res.add(absAddresses);
						res.add(null);
					}
				}

				res.add(0, "failure");
				return res;
			}
		}
	}

	/**
	 * Function responsible for stopping of blinking path(s) to debugged grammar match.
	 */

	private void stopBlinking()
	{
		grammarController.getTimerDbg().stop();

		ArrayList<Graph> graphs = grammarController.grammar.graphs;

		for (int ig = 0; ig < graphs.size(); ig++)
		{
			Graph grf = graphs.get(ig);
			grf.stopDebug();
		}
	}

	/**
	 * Help function for clearing Debug's JTable and JTree.
	 * 
	 * @param debugShell
	 *            - Debug Shell window
	 * @param clearTableItems
	 *            - flag to determine whether JTable data should be cleared
	 * @param clearTreeItems
	 *            - flag to determine whether JTree data should be cleared
	 */

	private void clearAllItems(DebugShell debugShell, boolean clearTableItems, boolean clearTreeItems)
	{
		if (clearTableItems)
		{
			// clear renderer's data
			JTable debugTable = debugShell.getTableTraces();
			DebugJTableRenderer renderer = debugShell.getCustomTableRenderer();
			renderer.setColoredRowsMap(new HashMap<Integer, Color>());

			// remove all the elements from the table
			DefaultTableModel tableModel = (DefaultTableModel) debugTable.getModel();
			tableModel.getDataVector().removeAllElements();
			tableModel.fireTableDataChanged();
		}

		if (clearTreeItems)
		{
			DefaultTreeModel debugTreeModel = (DefaultTreeModel) debugShell.getTreeDebug().getModel();
			((DefaultMutableTreeNode) debugTreeModel.getRoot()).removeAllChildren();
			debugTreeModel.reload();
		}
	}

	/**
	 * Function implements action taken when selected index of Debug's JTable has changed.
	 */

	public void tableSelectionChangedFunction()
	{
		JTable debugTable = this.debugShell.getTableTraces();
		DefaultTableModel tableModel = (DefaultTableModel) debugTable.getModel();

		if (debugTable.getSelectedRowCount() == 0)
		{
			stopBlinking();
			return;
		}

		int iSol = debugTable.getSelectedRows()[0];
		ArrayList<Object> nodeTrace = (ArrayList<Object>) tableModel.getValueAt(iSol, 2);

		// parse nodetrace
		String graphName = nodeTrace.get(0).toString();
		parseNodeTrace(graphName, nodeTrace, 1);

		String sTrace = debugTable.getValueAt(iSol, 0).toString();
		buildSyntacticTree(sTrace);
		grammarController.getTimerDbg().start();
	}

	/**
	 * Function build JTree of Debug shell.
	 * 
	 * @param sTrace
	 *            - new node's trace
	 */

	private void buildSyntacticTree(String sTrace)
	{
		clearAllItems(this.debugShell, false, true);

		JTree treeDebug = this.debugShell.getTreeDebug();
		DefaultMutableTreeNode top = (DefaultMutableTreeNode) treeDebug.getModel().getRoot();
		DefaultTreeModel treeModel = (DefaultTreeModel) treeDebug.getModel();
		DefaultMutableTreeNode tn = new DefaultMutableTreeNode("Main");

		DebugJTreeRenderer customRenderer = this.debugShell.getCustomTreeRenderer();
		customRenderer.setBackgroundColor(Color.YELLOW);

		int ic = " (\"Main\" ".length();
		visit(sTrace, ic, tn);
		top.add(tn);
		treeModel.reload(top);

		treeDebug.expandPath(treeDebug.getPathForRow(0));
	}

	/**
	 * Function recursively adds nodes to Debug's JTree.
	 * 
	 * @param sTrace
	 *            - new node's trace
	 * @param ic
	 *            - counter of main title's length plus empty spaces
	 * @param tn
	 *            - root node on which other nodes are being recursively added
	 * @return - size of the main title's length, increased with number of empty spaces and brackets
	 */

	private int visit(String sTrace, int ic, DefaultMutableTreeNode tn)
	{
		DefaultMutableTreeNode tc;

		while (ic < sTrace.length())
		{
			char character = sTrace.charAt(ic);

			if (character == ' ')
			{
				ic++;
				continue;
			}

			else if (character == ')')
				return ic + 1;

			else if (character == '(')
			{
				String tr = sTrace.substring(ic + 2);

				if (tr.length() > 0 && tr.charAt(0) == '"')
				{
					int index = tr.indexOf('"');
					String fName = tr.substring(0, index);
					tc = new DefaultMutableTreeNode(fName);

					tn.add(tc);
					ic += index + 3;
					ic = visit(sTrace, ic, tc);
					continue;
				}
				else
					ic++;
			}

			int j = 0;

			for (; ic + j < sTrace.length() && sTrace.charAt(ic + j) != '(' && sTrace.charAt(ic + j) != ')'; j++)
				;

			String name = sTrace.substring(ic, ic + j);
			tc = new DefaultMutableTreeNode(name);

			tn.add(tc);

			ic += j;
		}

		return ic;
	}

	/**
	 * Function for parsing node's trace.
	 * 
	 * @param graphName
	 *            - name of the active graph
	 * @param nodeTrace
	 *            - node's trace
	 * @param i
	 *            - counter
	 * @return - size of node trace's depth
	 */

	private int parseNodeTrace(String graphName, ArrayList<Object> nodeTrace, int i)
	{
		ArrayList<Object> blinkingNodes = new ArrayList<Object>();

		for (; i < nodeTrace.size(); i++)
		{
			if (nodeTrace.get(i) instanceof String)
			{
				String gName = nodeTrace.get(i).toString();
				i = parseNodeTrace(gName, nodeTrace, i + 1);
			}
			else
			{
				int iNode = (Integer) nodeTrace.get(i);

				if (iNode == -1)
					continue;

				blinkingNodes.add(iNode);

				if (iNode == 1)
					break;
			}
		}

		// find graph
		Graph grf = null;
		ArrayList<Graph> graphs = grammarController.grammar.graphs;

		for (int ig = 0; ig < graphs.size(); ig++)
		{
			Graph grf0 = graphs.get(ig);

			if (grf0.name.equals(graphName))
			{
				grf = grf0;
				break;
			}
		}

		if (grf != null)
			grf.setDebug(blinkingNodes);

		return i;
	}
}