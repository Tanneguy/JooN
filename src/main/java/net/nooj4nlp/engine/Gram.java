/*
 * This file is part of Nooj. Copyright (C) 2012 Silberztein Max
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.nooj4nlp.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Class that implements the parsing. It deals with grammar optimizations.
 * 
 * @author Silberztein Max
 */
public class Gram implements Serializable
{
	private static final long serialVersionUID = 1544877445200128280L;

	public ArrayList<State> states; 
	ArrayList<Boolean> isTerminal;
	ArrayList<String> vocab;
	transient ArrayList<String> vocabIn;
	transient ArrayList<String> vocabOut;

	/**
	 * Constructor
	 * 
	 * @param nbOfStates
	 *            - number of states for initializing array list of states
	 */
	Gram(int nbOfStates)
	{
		states = new ArrayList<State>();

		for (int i = 0; i < nbOfStates; i++)
		{
			states.add(new State());
		}

		vocab = vocabIn = vocabOut = null;
		InflectionsCommands = null;
	}

	/**
	 * Default constructor
	 */
	Gram()
	{
		states = new ArrayList<State>();
		vocab = vocabIn = vocabOut = null;
		InflectionsCommands = null;
	}

	/**
	 * Cleans up list of ID labels for every state in array list of states.
	 */
	private void cleanup()
	{
		for (State st : this.states)
		{
			st.AllIdLabels = null;
		}
	}

	/**
	 * Adds another transition (from dst to idLabel - calculated from hVocab based on given label) to state with given
	 * order number (src).
	 * 
	 * @param src
	 *            - order number of the state for which transition is given
	 * @param dst
	 *            - destination of transition
	 * @param label
	 *            - label for calculating idLabel
	 * @param aVocab
	 *            - list of labels
	 * @param hVocab
	 *            - HashMap which contains the label
	 */
	final void addTransition(int src, int dst, String label, ArrayList<String> aVocab, HashMap<String, Integer> hVocab)
	{
		ParameterCheck.mandatoryString("label", label);
		ParameterCheck.mandatoryCollection("aVocab", aVocab);

		int idLabel;
		if (hVocab.containsKey(label))
		{
			idLabel = hVocab.get(label);
		}
		else
		{
			aVocab.add(label);
			idLabel = aVocab.size() - 1;
			hVocab.put(label, idLabel);
		}

		(states.get(src)).addTrans(dst, idLabel);
	}

	/**
	 * Adds the output in all transitions for which the given output was factorized.
	 * 
	 * @param grm1
	 * @param labelOutput
	 * @param aVocab
	 * @param hVocab
	 * @return
	 */
	static Gram addOutput(Gram grm1, String labelOutput, ArrayList<String> aVocab, HashMap<String, Integer> hVocab)
	{
		ParameterCheck.mandatory("grm1", grm1);
		ParameterCheck.mandatoryString("labelOutput", labelOutput);
		ParameterCheck.mandatoryCollection("aVocab", aVocab);

		Gram grmOut = new Gram(2);

		String label = "<E>/" + labelOutput;
		// connect initial node to terminal node (grm1)
		int idLabel;
		if (hVocab.containsKey(label))
		{
			idLabel = hVocab.get(label);
		}
		else
		{
			aVocab.add(label);
			idLabel = aVocab.size() - 1;
			hVocab.put(label, idLabel);
		}

		(grmOut.states.get(0)).addTrans(1, idLabel);

		
		return grmOut.concatenation(grm1);
	}

	/**
	 * Adds another transition (from dst to idLabel) to state with given order number (src).
	 * 
	 * @param src
	 *            - order number of the state for which transition is given
	 * @param dst
	 *            - destination of transition
	 * @param idLabel
	 *            - id of label for transition
	 */
	final void addTransition(int src, int dst, int idLabel)
	{
		(states.get(src)).addTrans(dst, idLabel);
	}

	/**
	 * Adds transitions to list of states based on given gram's list of states, starting from anchor.
	 * 
	 * @param grm1
	 *            - given gram
	 * @param anchor
	 * @param inode
	 */
	final void transfer(Gram grm1, int anchor, int inode)
	{
		ParameterCheck.mandatory("grm1", grm1);

		// copy grm1 (shift state number + anchor)
		for (int istate = 0; istate < grm1.states.size(); istate++)
		{
			State st = grm1.states.get(istate);
			int nst = istate + anchor;

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				int idl = st.IdLabels.get(itrans);

				// compute new destination and vocab
				int ndst = dst + anchor;
				(this.states.get(nst)).addTrans(ndst, idl);
				(this.states.get(nst)).GraphNodeNumber = inode;
			}
		}
	}

	/**
	 * Adds new (starting) transition to token with given label (token) to a new gram.
	 * 
	 * @param token
	 * @param aVocab
	 * @param hVocab
	 * @return
	 */
	static Gram token(String token, ArrayList<String> aVocab, HashMap<String, Integer> hVocab)
	{
		ParameterCheck.mandatoryString("token", token);
		ParameterCheck.mandatoryCollection("aVocab", aVocab);

		Gram grm = new Gram(2);
		grm.addTransition(0, 1, token, aVocab, hVocab);

		return grm;
	}

	/**
	 * Concatenates given Gram to current Gram object (this).
	 * 
	 * @param grm2
	 *            - Gram to be concatenated to current Gram
	 * @return current Gram after concatenation
	 */
	final Gram concatenation(Gram grm2)
	{
		ParameterCheck.mandatory("grm2", grm2);

		int grm1InitialNbOfStates = this.states.size();

		this.states.add(0, new State());
		this.states.add(0, new State());

		// shift all state destination numbers to +2
		for (int istate = 2; istate < this.states.size(); istate++)
		{
			State st = this.states.get(istate);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				st.Dests.set(itrans, dst + 2);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + 2);
				}
			}
		}

		// insert grm2 after grm
		for (int i = 0; i < grm2.states.size(); i++)
		{
			State st = grm2.states.get(i);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
		

				// compute new destination
				int ndst = dst + grm1InitialNbOfStates + 2;
				st.Dests.set(itrans, ndst);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + grm1InitialNbOfStates + 2);
				}
			}
			this.states.add(st);
		}

		// connect initial node to initial node (grm1)
		(this.states.get(0)).addTrans(2, 0);
		// connect terminal node of grm1 to initial node of grm2
		(this.states.get(3)).addTrans(grm1InitialNbOfStates + 2, 0);
		// connect terminal node of grm2 to terminal node
		(this.states.get(grm1InitialNbOfStates + 3)).addTrans(1, 0);

		return this;
	}

	/**
	 * Makes a disjunction of current Gram (this) and given Gram.
	 * 
	 * @param grm2
	 *            - Gram for making the disjunction
	 * @return current Gram after disjunction
	 */
	final Gram disjunction(Gram grm2)
	{
		ParameterCheck.mandatory("grm2", grm2);

		int grm1InitialNbOfStates = this.states.size();

		this.states.add(0, new State());
		this.states.add(0, new State());

		// shift all state destination numbers to +2
		for (int istate = 2; istate < this.states.size(); istate++)
		{
			State st = this.states.get(istate);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				st.Dests.set(itrans, dst + 2);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + 2);
				}
			}
		}

		// insert grm2 after grm
		for (int i = 0; i < grm2.states.size(); i++)
		{
			State st = grm2.states.get(i);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				

				// compute new destination
				int ndst = dst + grm1InitialNbOfStates + 2;
				st.Dests.set(itrans, ndst);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + grm1InitialNbOfStates + 2);
				}
			}
			this.states.add(st);
		}

		// connect initial node to both initial nodes (grm1 and grm2)
		(this.states.get(0)).addTrans(2, 0);
		(this.states.get(0)).addTrans(grm1InitialNbOfStates + 2, 0);

		// connect both terminal nodes of grm1 and grm2 to terminal node
		(this.states.get(3)).addTrans(1, 0);
		(this.states.get(grm1InitialNbOfStates + 3)).addTrans(1, 0);

		return this;
	}

	/**
	 * Makes a disjunction of current Gram (this) and given Grams.
	 * 
	 * @param grms2
	 *            - list of Grams for making the disjunction
	 * @return current Gram after disjunction
	 */
	final Gram disjunctions(ArrayList<Gram> grms2)
	{
		ParameterCheck.mandatoryCollection("grms2", grms2);

		this.states.add(0, new State());
		this.states.add(0, new State());

		// shift all state destination numbers to +2
		for (int istate = 2; istate < this.states.size(); istate++)
		{
			State st = this.states.get(istate);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				st.Dests.set(itrans, dst + 2);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + 2);
				}
			}
		}

		// connect res initial node to grm1 initial node
		this.states.get(0).addTrans(2, 0);
		// connect terminal node to res terminal node
		this.states.get(3).addTrans(1, 0);

		// insert all grms2 after grm
		for (Gram grm2 : grms2)
		{
			int anchorStNb = this.states.size();

			for (int i = 0; i < grm2.states.size(); i++)
			{
				State st = grm2.states.get(i);

				for (int itrans = 0; itrans < st.Dests.size(); itrans++)
				{
					int dst = st.Dests.get(itrans);
			

					// compute new destination
					int ndst = dst + anchorStNb;
					st.Dests.set(itrans, ndst);
				}

				for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
				{
					for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
					{
						int dst = outgoingTransitions.get(itrans);
						outgoingTransitions.set(itrans, dst + anchorStNb);
					}
				}
				this.states.add(st);
			}

			// connect res initial node to new grm2 initial node
			(this.states.get(0)).addTrans(anchorStNb, 0);

			// connect both terminal nodes of grm1 and grm2 to terminal node
			(this.states.get(anchorStNb + 1)).addTrans(1, 0);
		}

		return this;
	}

	/**
	 * Concatenates given Grams to current Gram objects.
	 * 
	 * @param grms2
	 *            - list of Grams for concatenating to current Gram
	 * @return current Gram after concatenation
	 */
	final Gram concatenations(ArrayList<Gram> grms2)
	{
		this.states.add(0, new State());
		this.states.add(0, new State());

		// shift all state destination numbers to +2
		for (int istate = 2; istate < this.states.size(); istate++)
		{
			State st = this.states.get(istate);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				st.Dests.set(itrans, dst + 2);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + 2);
				}
			}
		}
		int previousTerminalNode = 3;

		// connect res initial node to grm1 initial node
		(this.states.get(0)).addTrans(2, 0);

		// insert all grms2 after grm
		int anchorStNb = this.states.size();

		for (Gram grm2 : grms2)
		{
			anchorStNb = this.states.size();

			for (int i = 0; i < grm2.states.size(); i++)
			{
				State st = grm2.states.get(i);

				for (int itrans = 0; itrans < st.Dests.size(); itrans++)
				{
					int dst = st.Dests.get(itrans);
				

					// compute new destination
					int ndst = dst + anchorStNb;
					st.Dests.set(itrans, ndst);
				}

				for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
				{
					for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
					{
						int dst = outgoingTransitions.get(itrans);
						outgoingTransitions.set(itrans, dst + anchorStNb);
					}
				}
				this.states.add(st);
			}

			// connect previous res terminal node to new grm2 initial node
			(this.states.get(previousTerminalNode)).addTrans(anchorStNb, 0);
			previousTerminalNode = anchorStNb + 1;

		}
		// connect terminal node of grm1 and grm2 to terminal node
		(this.states.get(anchorStNb + 1)).addTrans(1, 0);

		return this;
	}

	/**
	 * Constructs the automaton that corresponds to the '*' in regular expressions (Kleene operator).
	 * 
	 * @return current Gram after applying Kleene operator
	 */
	final Gram kleene()
	{


		this.states.add(0, new State());
		this.states.add(0, new State());

		// shift all state destination numbers to +2
		for (int istate = 2; istate < this.states.size(); istate++)
		{
			State st = this.states.get(istate);

			for (int itrans = 0; itrans < st.Dests.size(); itrans++)
			{
				int dst = st.Dests.get(itrans);
				st.Dests.set(itrans, dst + 2);
			}

			for (ArrayList<Integer> outgoingTransitions : st.AllIdLabels.values())
			{
				for (int itrans = 0; itrans < outgoingTransitions.size(); itrans++)
				{
					int dst = outgoingTransitions.get(itrans);
					outgoingTransitions.set(itrans, dst + 2);
				}
			}
		}

		// connect res-initial node to res-terminal node and to grm1-initial node
		(this.states.get(0)).addTrans(2, 0);
		(this.states.get(0)).addTrans(1, 0);

		// connect this-terminal node to res-terminal node
		(this.states.get(3)).addTrans(1, 0);

		// connect res-terminal node to res-intial node
		(this.states.get(1)).addTrans(0, 0);

		return this;
	}

	/**
	 * Helper method - computes vocabs (cleans them from '\' and '"')
	 */
	private void prepareVocabs()
	{
		// compute VocabIn and VocabOut
		vocabIn = new ArrayList<String>();
		vocabOut = new ArrayList<String>();

		for (int iv = 0; iv < vocab.size(); iv++)
		{
			String label = vocab.get(iv);
			int i;

			for (i = 0; i < label.length() && label.charAt(i) != '/'; i++)
			{
				if (label.charAt(i) == '\\')
				{
					i++;
				}
				else if (label.charAt(i) == '"')
				{
					for (i++; i < label.length() && label.charAt(i) != '"'; i++)
					{
						;
					}
				}
			}
			if (i < label.length())
			{
				String input = label.substring(0, i);
				String output = label.substring(i + 1);

				vocabIn.add(input);
				vocabOut.add(output);
			}
			else
			{
				vocabIn.add(label);
				vocabOut.add(null);
			}
		}
	}

	/**
	 * Prepares gram for parsing.
	 * 
	 */
	public final void prepareForParsing()
	{
		this.prepareVocabs();
	}

	/**
	 * Helper method - removes all epsilon transitions (&lt;E&gt;) from a grammar.
	 * 
	 * @param istate
	 * @param closure
	 * @return
	 */
	private boolean epsilonStateClosure(int istate, RefObject<HashMap<Integer, ArrayList<State>>> closure)
	{
		HashMap<Integer, ArrayList<Integer>> allIdLabels = (states.get(istate)).AllIdLabels;

		if (!allIdLabels.containsKey(0))
		{
			return false;
		}

		ArrayList<Integer> allDests = allIdLabels.get(0);
		boolean modified = false;

		for (int dst : allDests)
		{
			if (!closure.argvalue.containsKey(dst))
			{
				// add state #dst to closure
				// i ovde nesto ne valja
				closure.argvalue.put(dst, null);
				epsilonStateClosure(dst, closure);
				modified = true;
			}
		}

		return modified;
	}

	/**
	 * Removes all epsilon transitions (&lt;E&gt;) from a grammar.
	 * 
	 * @param aSetOfStates
	 * @return
	 */
	private HashMap<Integer, ArrayList<State>> epsilonStateClosure(HashMap<Integer, ArrayList<State>> aSetOfStates)
	{
		// Using copy-constructor instead of clone() - recommended because of unchecked class cast
		HashMap<Integer, ArrayList<State>> closure = new HashMap<Integer, ArrayList<State>>(aSetOfStates);

		boolean modified = true;

		while (modified)
		{
			modified = false;
			ArrayList<Integer> theStates = new ArrayList<Integer>(closure.keySet());

			for (int istate : theStates)
			{
				RefObject<HashMap<Integer, ArrayList<State>>> tempRef_closure = new RefObject<HashMap<Integer, ArrayList<State>>>(
						closure);
				boolean tempVar = epsilonStateClosure(istate, tempRef_closure);
				closure = tempRef_closure.argvalue;

				if (tempVar)
				{
					modified = true;
				}
			}
		}

		return closure;
	}

	/**
	 * 
	 * @param istate
	 * @param ilet
	 * @param closure
	 */
	private void stateClosure(int istate, int ilet, RefObject<HashMap<Integer, ArrayList<State>>> closure)
	{
		HashMap<Integer, ArrayList<Integer>> allIdLabels = (states.get(istate)).AllIdLabels;

		if (!allIdLabels.containsKey(ilet))
		{
			return;
		}

		ArrayList<Integer> allDests = allIdLabels.get(ilet);

		for (int dst : allDests)
		{
			if (!closure.argvalue.containsKey(dst))
			{
				// add state #dst to closure
				// i ovde nesto ne valja
				closure.argvalue.put(dst, null);
			}
		}
	}

	/**
	 * Compares two given hash tables (aSet and another set).
	 * 
	 * @param aSet
	 *            - first hash table
	 * @param anotherSet
	 *            - second hash table
	 * @return true if hash tables are the same, false otherwise
	 */
	private boolean sameSets(HashMap<Integer, ArrayList<State>> aSet, HashMap<Integer, ArrayList<State>> anotherSet)
	{
		if (aSet.size() != anotherSet.size())
		{
			return false;
		}

		for (int istate : aSet.keySet())
		{
			if (!anotherSet.containsKey(istate))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Looks for given variable in this.VocabIn.
	 * 
	 * @param varName
	 *            - variable to look for
	 * @return order number of state variable associates to (if found), or -1 (if not found).
	 */
	private final int lookForVariable(String varName)
	{
		ParameterCheck.mandatoryString("varName", varName);

		for (int istate = 0; istate < this.states.size(); istate++)
		{
			State state = this.states.get(istate);

			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int lbl = state.IdLabels.get(itrans);

				String ilabel0 = this.vocabIn.get(lbl);

				if (ilabel0.equals("$(" + varName))
				{
					return istate;
				}
			}
		}
		return -1;
	}

	/**
	 * Looks in a graph for variable's definition.
	 * 
	 * @param initialState
	 *            - order number of state for transition to be added
	 * @param stateNb
	 *            - order number of state in question
	 * @param varName
	 *            - name of variable that is searched for
	 * @param recLevel
	 *            - level of recursion for the search
	 * @param resGram
	 *            - gram to which the new transition is added
	 */
	private final void exploreForVariable(int initialState, int stateNb, String varName, int recLevel,
			RefObject<Gram> resGram)
	{
		ParameterCheck.mandatoryString("varName", varName);

		State state = this.states.get(stateNb);

		for (int itrans = 0; itrans < state.Dests.size(); itrans++)
		{
			int dst = state.Dests.get(itrans);
			int lbl = state.IdLabels.get(itrans);

			if (dst == 1)
			{
				resGram.argvalue.addTransition(initialState, 1, lbl);
			}

			String ilabel0 = this.vocabIn.get(lbl);

			if (recLevel == 0)
			{
				if (ilabel0.equals("$(" + varName))
				{
					resGram.argvalue.states.add(new State());
					int iNewState = resGram.argvalue.states.size() - 1;

					resGram.argvalue.addTransition(initialState, iNewState, 0);
					this.exploreForVariable(iNewState, dst, varName, recLevel + 1, resGram);
				}
				continue;
			}

			if (ilabel0.length() >= 2 && ilabel0.substring(0, 2).equals("$("))
			{
				resGram.argvalue.states.add(new State());
				int iNewState = resGram.argvalue.states.size() - 1;

				resGram.argvalue.addTransition(initialState, iNewState, lbl);
				this.exploreForVariable(iNewState, dst, varName, recLevel + 1, resGram);
			}
			else if (ilabel0.length() >= 2 && ilabel0.substring(0, 2).equals("$)"))
			{
				if (recLevel <= 1)
				{
					resGram.argvalue.addTransition(initialState, 1, 0);
				}
				else
				{
					resGram.argvalue.states.add(new State());
					int iNewState = resGram.argvalue.states.size() - 1;

					resGram.argvalue.addTransition(initialState, iNewState, lbl);
					this.exploreForVariable(iNewState, dst, varName, recLevel - 1, resGram);
				}
			}
			else
			{
				resGram.argvalue.states.add(new State());
				int iNewState = resGram.argvalue.states.size() - 1;

				resGram.argvalue.addTransition(initialState, iNewState, lbl);
				this.exploreForVariable(iNewState, dst, varName, recLevel, resGram);
			}
		}
	}

	/**
	 * Returns the gram which is created during exploreForVariable, with proper fields initialized.
	 * 
	 * @param varName
	 *            - name of the variable it's looked for.
	 * @return - resulting gram
	 */
	final Gram getGramFromVariableDefinition(String varName)
	{
		ParameterCheck.mandatoryString("varName", varName);

		int iStateVar = lookForVariable(varName);
		if (iStateVar == -1)
		{
			return null;
		}

		Gram resgram = new Gram();
		resgram.states.add(new State()); // initial state @ 0
		resgram.states.add(new State()); // terminal state @ 1

		RefObject<Gram> tempRef_resgram = new RefObject<Gram>(resgram);
		this.exploreForVariable(0, iStateVar, varName, 0, tempRef_resgram);
		resgram = tempRef_resgram.argvalue;

		// various inits for resgram
		resgram.vocab = this.vocab;
		resgram.vocabIn = this.vocabIn;
		resgram.vocabOut = this.vocabOut;
		resgram.isTerminal = new ArrayList<Boolean>();

		for (int i = 0; i < resgram.states.size(); i++)
		{
			resgram.isTerminal.add(false);
		}
		resgram.isTerminal.set(1, true);

		return resgram;
	}

	/**
	 * 
	 * @param commands
	 * @param ic
	 * @return
	 */
	private static int argnum(String commands, int ic)
	{
		ParameterCheck.mandatoryString("commands", commands);

		if (commands.charAt(ic) == '>')
		{
			return 1;
		}

		if (commands.charAt(ic) == 'W' && commands.charAt(ic + 1) == '>')
		{
			return -1;
		}

		int val = 0;

		for (; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
		{
			if (!Character.isDigit(commands.charAt(ic)))
			{
				return 0;
			}
			val = val * 10 + commands.charAt(ic) - '0';
		}
		return val;
	}

	/**
	 * Produces one inflected form from a lemma and a string of commands.
	 * 
	 * @param lan
	 * @param lemma
	 * @param commands
	 * @param ires
	 * @return
	 */
	public static String processInflection(Language lan, String lemma, String commands, RefObject<Integer> ires)
	{
		StringBuilder res = new StringBuilder(lemma);
		ires.argvalue = res.length();

		for (int ic = 0; ic < commands.length();)
		{
			if (commands.charAt(ic) == '\\')
			{
				res.insert(ires.argvalue, String.valueOf(commands.charAt(ic + 1)));
				ires.argvalue++;

				ic += 2;
				continue;
			}
			else if (commands.charAt(ic) == '"')
			{
				for (ic++; ic < commands.length() && commands.charAt(ic) != '"'; ic++)
				{
					res.insert(ires.argvalue, String.valueOf(commands.charAt(ic)));
					ires.argvalue++;
				}

				ic++;
				continue;
			}
			else if (commands.charAt(ic) != '<')
			{
				res.insert(ires.argvalue, String.valueOf(commands.charAt(ic)));
				ires.argvalue++;

				ic++;
				continue;
			}
			else
			{
				int val;

				if (commands.charAt(ic + 1) == 'B') // Backspace
				{
					val = argnum(commands, ic + 2);

					if (val == -1) // W => whole word
					{
						val = ires.argvalue;
					}
					else if (val == 0) // unknown argument or 0
					{
						res.insert(ires.argvalue, "<B:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else if (ires.argvalue - val < 0) // invalid argument
					{
						res.insert(ires.argvalue, "<B:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					ires.argvalue -= val;
					res.delete(ires.argvalue, ires.argvalue + val);
					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}
					ic++;
				}
				else if (commands.charAt(ic + 1) == 'C') // Change Case
				{
					val = argnum(commands, ic + 2);
					if (val != 1)
					{
						res.insert(ires.argvalue, "<C:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					if (Character.isUpperCase(res.charAt(ires.argvalue - 1)))
					{
						res.setCharAt(ires.argvalue - 1, Character.toLowerCase(res.charAt(ires.argvalue - 1)));
					}
					else if (Character.isLowerCase(res.charAt(ires.argvalue - 1)))
					{
						res.setCharAt(ires.argvalue - 1, Character.toUpperCase(res.charAt(ires.argvalue - 1)));
					}

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}

					ic++;
				}
				else if (commands.charAt(ic + 1) == 'D') // DUPLICATE
				{
					val = argnum(commands, ic + 2);
					if (val == 0 || val == -1 || ires.argvalue - val < 0)
					{
						res.insert(ires.argvalue, "<D:ERROR=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else
					{
						if (lan.isoName.equals("br") || lan.isoName.equals("hu"))
						{
							// berber and hungarian have special <D> operators
							ires.argvalue = lan.processInflection(commands, res, ic, ires.argvalue);

							if (ires.argvalue == -1)
							{
								res.insert(res.length(), "<INVALID CMD=" + commands.charAt(ic + 1) + ">");
								ires.argvalue = res.length();
								return res.toString();
							}
							else
							{
								for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
								{
									;
								}
								ic++;
							}
						}
						else
						{
							for (int i = 0; i < val; i++)
							{
								res.insert(ires.argvalue, String.valueOf(res.charAt(ires.argvalue - val)));
								ires.argvalue++;
							}
							for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
							{
								;
							}
							ic++;
						}
					}
				}
				else if (commands.charAt(ic + 1) == 'E') // empty string
				{
					val = argnum(commands, ic + 2);

					if (val != 1)
					{
						res.insert(ires.argvalue, "<E:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else
					{
						for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
						{
							;
						}
						ic++;
					}
				}
				else if (commands.charAt(ic + 1) == 'L') // LEFT
				{
					val = argnum(commands, ic + 2);

					if (val == -1) // W => whole word
					{
						ires.argvalue--;

						for (; ires.argvalue >= 0 && Language.isLetter(res.charAt(ires.argvalue)); ires.argvalue--)
						{
							;
						}

						ires.argvalue++;

						for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
						{
							;
						}

						ic++;
						continue;
					}
					else if (val == 0) // unknown argument or 0
					{
						res.insert(ires.argvalue, "<L:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else if (ires.argvalue - val < 0) // invalid argument
					{
						res.insert(ires.argvalue, "<L:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					if (lan.isoName.equals("he"))
					{
						val = Language.nbOfDagueshShinSinDotsIn(res, ires.argvalue, val); // skip shin dot and sin dot
					}

					ires.argvalue -= val;

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}

					ic++;
				}
				else if (commands.charAt(ic + 1) == 'P') // PREVIOUS WORD FORM
				{
					val = argnum(commands, ic + 2);

					if (val == -1) // W => go to the end of first word
					{
						for (ires.argvalue = 0; ires.argvalue < res.length()
								&& !Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue++;
						}

						for (; ires.argvalue < res.length() && Language.isLetter(res.charAt(ires.argvalue)); ires.argvalue++)
						{
							;
						}

						if (ires.argvalue < res.length() && ires.argvalue > 0)
						{
							for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
							{
								;
							}

							ic++;
							continue;
						}
						else
						{
							res.insert(ires.argvalue, "<P:INVALID ARG=" + commands.charAt(ic + 2) + ">");
							return res.toString();
						}
					}
					else if (val == 0) // unknown argument or 0
					{
						res.insert(ires.argvalue, "<P:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					while (val > 0)
					{
						ires.argvalue--;

						for (; ires.argvalue >= 0 && Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue--;
						}

						for (; ires.argvalue >= 0 && !Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue--;
						}

						val--;
					}

					ires.argvalue++;

					if (ires.argvalue < 0) // invalid argument
					{
						res.insert(ires.argvalue, "<P:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}

					ic++;
				}
				else if (commands.charAt(ic + 1) == 'R') // RIGHT
				{
					val = argnum(commands, ic + 2);

					if (val == -1)
					{
						for (; ires.argvalue < res.length() && Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue++;
						}

						for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
						{
							;
						}

						ic++;
						continue;
					}
					else if (val == 0)
					{
						res.insert(ires.argvalue, "<R:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else if (ires.argvalue + val > res.length())
					{
						res.insert(ires.argvalue, "<R:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					ires.argvalue += val;

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}

					ic++;
				}
				else if (commands.charAt(ic + 1) == 'N') // NEXT WORD FORM
				{
					val = argnum(commands, ic + 2);

					if (val == -1) // W => whole word
					{
						for (ires.argvalue = res.length() - 1; ires.argvalue >= 0
								&& !Language.isLetter(res.charAt(ires.argvalue)); ires.argvalue--)
						{
							;
						}

						if (ires.argvalue >= 0)
						{
							ires.argvalue++;

							for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
							{
								;
							}

							ic++;
							continue;
						}
						else
						{
							res.insert(ires.argvalue, "<N:INVALID ARG=" + commands.charAt(ic + 2) + ">");
							return res.toString();
						}
					}
					else if (val == 0) // unknown argument or 0
					{
						res.insert(ires.argvalue, "<N:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					while (val > 0)
					{
						for (; ires.argvalue < res.length() && Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue++;
						}
						for (; ires.argvalue < res.length() && !Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue++;
						}
						for (; ires.argvalue < res.length() && Language.isLetter(res.charAt(ires.argvalue));)
						{
							ires.argvalue++;
						}

						val--;
					}

					if (ires.argvalue > res.length()) // invalid argument
					{
						res.insert(ires.argvalue, "<N:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}
					ic++;
				}
				else if (commands.charAt(ic + 1) == 'S') // SUPPRESS
				{
					val = argnum(commands, ic + 2);

					if (val == -1)
					{
						val = res.length() - ires.argvalue;
					}
					else if (val == 0)
					{
						res.insert(ires.argvalue, "<S:UNKNOWN ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}
					else if (ires.argvalue + val > res.length())
					{
						res.insert(ires.argvalue, "<S:INVALID ARG=" + commands.charAt(ic + 2) + ">");
						return res.toString();
					}

					res.delete(ires.argvalue, ires.argvalue + val);

					for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
					{
						;
					}

					ic++;
				}
				else
				{
					ires.argvalue = lan.processInflection(commands, res, ic, ires.argvalue);

					if (ires.argvalue == -1)
					{
						res.insert(res.length(), "<INVALID CMD=" + commands.charAt(ic + 1) + ">");
						ires.argvalue = res.length();
						return res.toString();
					}
					else
					{
						for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
						{
							;
						}
						ic++;
					}
				}
			}
		}

		return res.toString(); 
	}

	/**
	 * Checks if newInput and newOutput are contained in given array list as pair (sols(i), sols(i+1))
	 * 
	 * @param sols
	 *            - array list to be checked
	 * @param newInput
	 * @param newOutput
	 * @return
	 */
	private boolean alreadyInSols(ArrayList<String> sols, String newInput, String newOutput)
	{
		for (int i = 0; i < sols.size(); i += 2)
		{
			String cInput = sols.get(i);
			if (!newInput.equals(cInput))
			{
				continue;
			}

			String cOutput = sols.get(i + 1);
			if (!newOutput.equals(cOutput))
			{
				continue;
			}

			return true;
		}
		return false;
	}

	/**
	 * Helper function - imitates C#'s DateTime.MaxValue
	 * 
	 * @return
	 */
	private Date getMaxDate()
	{
		Calendar c = Calendar.getInstance();
		c.set(9999, 11, 31, 23, 59, 59);
		c.set(Calendar.MILLISECOND, 999);
		Date maxDate = c.getTime();

		return maxDate;
	}

	/**
	 * Takes a sentence (e.g. "Paul loves Eva") and produces all its paraphrases (e.g. "it is Paul who loves her").
	 * 
	 * @param recLevel
	 *            - level of recursion
	 * @param allGrammars
	 * @param matchLimit
	 * @param dateLimit
	 * @param gt
	 * @param lan
	 * @param keepVariables
	 * @return
	 */
	public final String[] generateParaphrases(int recLevel, HashMap<String, Gram> allGrammars, int matchLimit,
			Date dateLimit, GramType gt, Language lan, boolean keepVariables)
	{
		ArrayList<String> sols = null;
		Stack<MTrace> stack = new Stack<MTrace>();
		stack.push(new MTrace());

		label:
		{
			while (stack.size() > 0)
			{
				MTrace curTrc = stack.pop();
				State state = states.get(curTrc.Statenb);

				for (int itrans = 0; itrans < state.Dests.size(); itrans++)
				{
					Date dt = new Date();
					Date maxDate = getMaxDate();

					if (!dateLimit.equals(maxDate) && dt.compareTo(dateLimit) > 0)
					{
						// GOTO changed with BREAK
						break label;
					}

					int dst = state.Dests.get(itrans);
					int lbl = state.IdLabels.get(itrans);

					String label = vocabIn.get(lbl);
					String labelOut = vocabOut.get(lbl);

					// compute the new trace and insert it in the stack
					MTrace newTrc = new MTrace();
					newTrc.Statenb = dst;
					newTrc.Inputs.addAll(curTrc.Inputs);
					newTrc.Outputs.addAll(curTrc.Outputs);

					if (recLevel != -1 && label.length() > 0 && label.charAt(0) == ':') // call sub graph recursively
					{
						if (allGrammars != null)
						{
							Gram rgrm = allGrammars.get(label.substring(1));

							if (rgrm != null)
							{
								if (rgrm.vocabIn == null)
								{
									rgrm.prepareForParsing();
								}

								String[] recSols = rgrm.generateParaphrases(recLevel + 1, allGrammars, matchLimit,
										dateLimit, gt, lan, keepVariables);

								if (recSols == null)
								{
									continue;
								}

								if (recSols.length == 2)
								{
									newTrc.Inputs.add(recSols[0]);
									newTrc.Outputs.add(recSols[1]);

									if (labelOut != null)
									{
										if (gt == GramType.FLX)
										{
											// add missing "+" in front of features
											if (labelOut.charAt(0) == '+')
											{
												newTrc.Outputs.add(labelOut);
											}
											else
											{
												newTrc.Outputs.add("+" + labelOut);
											}
										}
										else
										{
											newTrc.Outputs.add(labelOut);
										}
									}
									stack.push(newTrc);

									// reach terminal state
									if (dst==1)
									{
										String newInput = null;

										if (newTrc.Inputs.size() > 0)
										{
											StringBuilder totalInput = new StringBuilder(newTrc.Inputs.get(0));

											for (int i = 1; i < newTrc.Inputs.size(); i++)
											{
												if (gt == GramType.SYNTAX)
												{
													totalInput.append(' ');
												}
												totalInput.append(newTrc.Inputs.get(i));
											}
											newInput = totalInput.toString();
										}
										else
										{
											newInput = "";
										}

										String newOutput = null;
										if (newTrc.Outputs.size() > 0)
										{
											StringBuilder totalOutput = new StringBuilder(newTrc.Outputs.get(0));
											for (int i = 1; i < newTrc.Outputs.size(); i++)
											{
												totalOutput.append(newTrc.Outputs.get(i));
											}
											newOutput = totalOutput.toString();
										}
										else
										{
											newOutput = "";
										}

										if (sols == null)
										{
											sols = new ArrayList<String>();
										}

										if ((matchLimit == -1 || sols.size() < matchLimit * 2)
												&& !alreadyInSols(sols, newInput, newOutput))
										{
											sols.add(newInput);
											sols.add(newOutput);
										}

										if (recLevel < 2 && matchLimit > 0 && sols.size() > matchLimit * 2)
										{
											// GOTO changed with BREAK
											break label;
										}
									}
								}
								else if (recSols.length > 2)
								{
									for (int irec = 0; irec < recSols.length; irec += 2)
									{

										MTrace newTrc0 = new MTrace();
										newTrc0.Statenb = newTrc.Statenb;
										newTrc0.Pos = newTrc.Pos;
										newTrc0.Inputs.addAll(newTrc.Inputs);
										newTrc0.Outputs.addAll(newTrc.Outputs);

										newTrc0.Inputs.add(recSols[irec]);
										newTrc0.Outputs.add(recSols[irec + 1]);

										if (labelOut != null)
										{
											if (gt == GramType.FLX)
											{
												// add missing "+" in front of features
												if (labelOut.length() > 0 && labelOut.charAt(0) == '+')
												{
													newTrc.Outputs.add(labelOut);
												}
												else if (labelOut.length() > 0)
												{
													newTrc.Outputs.add("+" + labelOut);
												}
											}
											else if (gt == GramType.MORPHO)
											{
												// reorder the category in front of all "+" prefixed information, e.g.
												// "V+RE+FLX=AIDER" and not "+REV+FLX=AIDER"
												if (labelOut.length() > 0 && labelOut.charAt(0) == '+')
												{
													newTrc.Outputs.add(labelOut);
												}
												else
												{
													int iout = 0;

													for (; iout < newTrc.Outputs.size(); iout++)
													{
														String ilab = newTrc.Outputs.get(iout);
														if (ilab.length() > 0 && ilab.charAt(0) == '+')
														{
															break;
														}
													}
													if (iout < newTrc.Outputs.size())
													{
														newTrc.Outputs.add(iout, labelOut);
													}
													else
													{
														newTrc.Outputs.add(labelOut);
													}
												}
											}
											else
											{
												newTrc.Outputs.add(labelOut);
											}
										}
										stack.push(newTrc0);

										// reach terminal state
										if (dst==1)
										{
											String newInput = null;
											if (newTrc0.Inputs.size() > 0)
											{
												StringBuilder totalInput = new StringBuilder(newTrc0.Inputs.get(0));
												for (int i = 1; i < newTrc0.Inputs.size(); i++)
												{
													if (gt == GramType.SYNTAX)
													{
														totalInput.append(' ');
													}
													totalInput.append(newTrc0.Inputs.get(i));
												}
												newInput = totalInput.toString();
											}
											else
											{
												newInput = "";
											}

											String newOutput = null;
											if (newTrc0.Outputs.size() > 0)
											{
												StringBuilder totalOutput = new StringBuilder(newTrc0.Outputs.get(0));

												for (int i = 1; i < newTrc0.Outputs.size(); i++)
												{
													totalOutput.append(newTrc0.Outputs.get(i));
												}
												newOutput = totalOutput.toString();
											}
											else
											{
												newOutput = "";
											}

											if (sols == null)
											{
												sols = new ArrayList<String>();
											}

											if ((matchLimit == -1 || sols.size() < matchLimit * 2)
													&& !alreadyInSols(sols, newInput, newOutput))
											{
												sols.add(newInput);
												sols.add(newOutput);
											}

											if (recLevel < 2 && matchLimit > 0 && sols.size() >= matchLimit * 2)
											{
												// GOTO changed with BREAK
												break label;
											}
										}
									}
								}
							}
						}
					}
					else
					// not recursive
					{
						if (!label.equals("<E>") && label != null && !label.equals(""))
						{
							if (gt == GramType.SYNTAX)
							{
								if (label.length() >= 2 && label.charAt(0) == '$'
										&& (label.charAt(1) == '(' || label.charAt(1) == ')'))
								{
									if (keepVariables)
									{
										newTrc.Inputs.add(" " + label);
									}
								}
								else
								{
									newTrc.Inputs.add(" " + label);
								}
							}
							else
							{
								newTrc.Inputs.add(label);
							}
						}

						if (labelOut != null && labelOut.length() > 0)
						{
							if (gt == GramType.FLX)
							{
								// add missing "+" in front of features
								if (labelOut.charAt(0) == '+')
								{
									newTrc.Outputs.add(labelOut);
								}
								else
								{
									newTrc.Outputs.add("+" + labelOut);
								}
							}
							else if (gt == GramType.MORPHO)
							{
								// reorder the category in front of all "+" prefixed information, e.g. "V+RE+FLX=AIDER"
								// and not "+REV+FLX=AIDER"
								if (labelOut.length() > 0 && labelOut.charAt(0) == '+')
								{
									newTrc.Outputs.add(labelOut);
								}
								else
								{
									int iout = 0;

									for (; iout < newTrc.Outputs.size(); iout++)
									{
										String ilab = newTrc.Outputs.get(iout);
										if (ilab.length() > 0 && ilab.charAt(0) == '+')
										{
											break;
										}
									}
									if (iout < newTrc.Outputs.size())
									{
										newTrc.Outputs.add(iout, labelOut);
									}
									else
									{
										newTrc.Outputs.add(labelOut);
									}
								}
							}
							else
							{
								newTrc.Outputs.add(labelOut);
							}
						}
						stack.push(newTrc);

						// reach terminal state
						if (dst==1)
						{
							String newInput = null;
							if (newTrc.Inputs.size() > 0)
							{
								StringBuilder totalInput = new StringBuilder(newTrc.Inputs.get(0));

								for (int i = 1; i < newTrc.Inputs.size(); i++)
								{
									if (gt == GramType.SYNTAX)
									{
										totalInput.append(' ');
									}
									totalInput.append(newTrc.Inputs.get(i));
								}
								newInput = totalInput.toString();
							}
							else
							{
								newInput = "";
							}

							String newoutput = null;
							if (newTrc.Outputs.size() > 0)
							{
								StringBuilder totaloutput = new StringBuilder(newTrc.Outputs.get(0));
								for (int i = 1; i < newTrc.Outputs.size(); i++)
								{
									totaloutput.append(newTrc.Outputs.get(i));
								}
								newoutput = totaloutput.toString();
							}
							else
							{
								newoutput = "";
							}

							if (sols == null)
							{
								sols = new ArrayList<String>();
							}

							if ((matchLimit == -1 || sols.size() < matchLimit * 2)
									&& !alreadyInSols(sols, newInput, newoutput))
							{
								sols.add(newInput);
								sols.add(newoutput);
							}
							if (recLevel < 2 && matchLimit > 0 && sols.size() >= matchLimit * 2)
							{
								// GOTO changed with BREAK
								break label;
							}
						}
					}
				}
			}
		}

		// When it is exited from the loop, program continues here
		if (sols != null && sols.size() > 0)
		{
			String[] forms = sols.toArray(new String[sols.size()]);

			for (int iform = 0; iform < forms.length; iform++)
			{
				if (forms[iform] != null)
				{
					forms[iform] = forms[iform].trim();
				}
			}
			return forms;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Computes all the strings of inflectional commands that are in all the paradigms (grammars).
	 * 
	 * @param allGrammars
	 * @return
	 */
	private String[] inflect(HashMap<String, Gram> allGrammars)
	{
		// allGrammars is allowed to be null. No parameters checks here.

		ArrayList<String> sols = null;
		Stack<MTrace> stack = new Stack<MTrace>();
		stack.push(new MTrace());

		while (stack.size() > 0)
		{
			MTrace curTrc = stack.pop();
			State state = states.get(curTrc.Statenb);

			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int dst = state.Dests.get(itrans);
				int lbl = state.IdLabels.get(itrans);
				String label = vocabIn.get(lbl);
				String labelOut = vocabOut.get(lbl);

				// compute the new trace and insert it in the stack
				MTrace newTrc = new MTrace();
				newTrc.Statenb = dst;
				newTrc.Inputs.addAll(curTrc.Inputs);
				newTrc.Outputs.addAll(curTrc.Outputs);

				if (label.length() > 0 && label.charAt(0) == ':') // call sub graph recursively
				{
					if (allGrammars != null)
					{
						Gram rGrm = allGrammars.get(label.substring(1));

						if (rGrm != null)
						{
							if (rGrm.vocabIn == null)
							{
								rGrm.prepareForParsing();
							}

							String[] recSols = rGrm.inflect(allGrammars);

							if (recSols.length == 2)
							{
								newTrc.Inputs.add(recSols[0]);
								newTrc.Outputs.add(recSols[1]);

								if (labelOut != null)
								{
									if (labelOut.charAt(0) == '+')
									{
										newTrc.Outputs.add(labelOut);
									}
									else
									{
										newTrc.Outputs.add("+" + labelOut);
									}
								}
								stack.push(newTrc);

								// reach terminal state
								if (dst==1)
								{
									if (sols == null)
									{
										sols = new ArrayList<String>();
									}

									String totalInput = "";
									String totalOutput = "";

									for (int i = 0; i < newTrc.Inputs.size(); i++)
									{
										totalInput += newTrc.Inputs.get(i);
									}

									for (int i = 0; i < newTrc.Outputs.size(); i++)
									{
										totalOutput += newTrc.Outputs.get(i);
									}

									sols.add(totalInput);
									sols.add(totalOutput);
								}
							}
							else if (recSols.length > 2)
							{
								for (int irec = 0; irec < recSols.length; irec += 2)
								{
									MTrace newTrc0 = new MTrace();
									newTrc0.Statenb = newTrc.Statenb;
									newTrc0.Pos = newTrc.Pos;
									newTrc0.Inputs.addAll(newTrc.Inputs);
									newTrc0.Outputs.addAll(newTrc.Outputs);

									newTrc0.Inputs.add(recSols[irec]);
									newTrc0.Outputs.add(recSols[irec + 1]);

									if (labelOut != null)
									{
										if (labelOut.charAt(0) == '+')
										{
											newTrc0.Outputs.add(labelOut);
										}
										else
										{
											newTrc0.Outputs.add("+" + labelOut);
										}
									}
									stack.push(newTrc0);

									// reach terminal state
									if (dst==1)
									{
										if (sols == null)
										{
											sols = new ArrayList<String>();
										}

										String totalInput = "";
										String totalOutput = "";

										for (int i = 0; i < newTrc0.Inputs.size(); i++)
										{
											totalInput += newTrc0.Inputs.get(i);
										}

										for (int i = 0; i < newTrc0.Outputs.size(); i++)
										{
											totalOutput += newTrc0.Outputs.get(i);
										}

										sols.add(totalInput);
										sols.add(totalOutput);
									}
								}
							}
						}
					}
				}
				else
				// not recursive
				{
					if (!label.equals("<E>"))
					{
						newTrc.Inputs.add(label);
					}

					if (labelOut != null && labelOut.length() > 0)
					{
						if (labelOut.charAt(0) == '+')
						{
							newTrc.Outputs.add(labelOut);
						}
						else
						{
							newTrc.Outputs.add("+" + labelOut);
						}
					}
					stack.push(newTrc);

					// reach terminal state
					if (dst == 1) 
					{
						if (sols == null)
						{
							sols = new ArrayList<String>();
						}

						String totalInput = "";
						String totalOutput = "";

						for (int i = 0; i < newTrc.Inputs.size(); i++)
						{
							totalInput += newTrc.Inputs.get(i);
						}

						for (int i = 0; i < newTrc.Outputs.size(); i++)
						{
							totalOutput += newTrc.Outputs.get(i);
						}

						sols.add(totalInput);
						sols.add(totalOutput);
					}
				}
			}
		}

		if (sols != null && sols.size() > 0)
		{
			return sols.toArray(new String[sols.size()]);
		}
		else
		{
			return null;
		}
	}

	transient String[] InflectionsCommands;

	/**
	 * Inflects a lemma (word) using all the inflectional paradigms (grammars) that are loaded in a hash table.
	 * 
	 * @param lan
	 * @param word
	 * @param forms
	 * @param outputs
	 * @param allGrammars
	 */
	public final void inflect(Language lan, String word, RefObject<String[]> forms, RefObject<String[]> outputs,
			HashMap<String, Gram> allGrammars)
	{
		forms.argvalue = null;
		outputs.argvalue = null;

		if (InflectionsCommands == null)
		{
			InflectionsCommands = inflect(allGrammars);
			cleanup();
		}

		if (InflectionsCommands == null)
		{
			return;
		}

		int pos = 0;
		forms.argvalue = new String[InflectionsCommands.length / 2];
		outputs.argvalue = new String[InflectionsCommands.length / 2];

		for (int i = 0; i < InflectionsCommands.length; i += 2)
		{
			RefObject<Integer> tempRef_pos = new RefObject<Integer>(pos);
			forms.argvalue[i / 2] = processInflection(lan, word, InflectionsCommands[i], tempRef_pos);
			pos = tempRef_pos.argvalue;

			if (InflectionsCommands[i + 1].length() > 0 && InflectionsCommands[i + 1].charAt(0) == '+')
			{
				outputs.argvalue[i / 2] = InflectionsCommands[i + 1];
			}
			else
			{
				outputs.argvalue[i / 2] = "+" + InflectionsCommands[i + 1];
			}

			if (outputs.argvalue[i / 2].equals("+<E>"))
			{
				outputs.argvalue[i / 2] = "";
			}
		}
	}
}