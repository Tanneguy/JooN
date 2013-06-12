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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import net.nooj4nlp.engine.TuGraph.TransCell;
import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Class that implements storage and access to the Text Annotation Structure. i.e. in the Mft all the annotations are
 * stored.
 * 
 * @author Silberztein Max
 */
public class Mft implements Serializable
{
	private static final long serialVersionUID = -7338281112909884406L;

	// Internal -> package
	public int[] tuAddresses; // starting address of each TU
	public transient int[] tuLengths; // length of each TU
	public transient ArrayList<ArrayList<TransitionObject>> aTransitions; // each text unit has an ArrayList of
																			// transitions
	public transient double multiplier;
	public transient int nbOfTransitions;
	private int maxRelBegAddress, maxLenAddress, maxTokenId;
	private int nboftransitions;

	/**
	 * Constructor - initializes arrays of tuAddresses, tuLengths, aTransitions to empty arrays with the length of
	 * nbOfTus + 1 (aTransition as an array of empty ArrayLists), initializes other class members to their default
	 * values.
	 * 
	 * @param nbOfTus
	 *            - number of text units
	 */
	public Mft(int nbOfTus)
	{
		tuAddresses = new int[nbOfTus + 1];
		tuLengths = new int[nbOfTus + 1];

		aTransitions = new ArrayList<ArrayList<TransitionObject>>();
		aTransitions.add(null);

		for (int itu = 1; itu < nbOfTus + 1; itu++)
		{
			aTransitions.add(new ArrayList<TransitionObject>());
		}

		maxRelBegAddress = maxLenAddress = nboftransitions = maxTokenId = 0;
		multiplier = 100.0;
		nbOfTransitions = 0;
	}

	/**
	 * Sets empty array lists as members of transitions array.
	 */
	public final void resetTransitions()
	{
		// Iterating through array members starting from second member?
		for (int itu = 1; itu < aTransitions.size(); itu++)
		{
			aTransitions.set(itu, new ArrayList<TransitionObject>());
		}
		nboftransitions = maxTokenId = 0;
		multiplier = 100.0;
		nbOfTransitions = 0;
	}

	/**
	 * Resizes this object - sets empty tuAddresses, tuLengths and aTransitions, if given number is smaller than size of
	 * tuAddresses array. If not, it does nothing.
	 * 
	 * @param newNbOfTus
	 *            - new length of above mentioned arrays.
	 */
	final void reSize(int newNbOfTus)
	{
		if (newNbOfTus > tuAddresses.length - 1)
		{
			return;
		}

		int[] newTuAddresses = new int[newNbOfTus + 1];
		for (int i = 1; i <= newNbOfTus; i++)
		{
			newTuAddresses[i] = tuAddresses[i];
		}
		tuAddresses = newTuAddresses;

		int[] newtuLengths = new int[newNbOfTus + 1];
		for (int i = 1; i <= newNbOfTus; i++)
		{
			newtuLengths[i] = tuLengths[i];
		}
		tuLengths = newtuLengths;

		ArrayList<ArrayList<TransitionObject>> newaTransitions = new ArrayList<ArrayList<TransitionObject>>(
				newNbOfTus + 1);
		newaTransitions.add(null);
		for (int i = 1; i <= newNbOfTus; i++)
		{
			newaTransitions.add(aTransitions.get(i));
		}
		aTransitions = newaTransitions;
	}

	/**
	 * Creates TuGraph object based on aTransitions.
	 * 
	 * 
	 * @param tuNb
	 *            - number of text unit for which TuGraph object is created
	 * @return
	 */
	public final TuGraph getTuGraph(int tuNb, TuGraph tuGraphToBeUpdated)
	{
		if (tuGraphToBeUpdated == null)
			tuGraphToBeUpdated = new TuGraph();
		else
		{
			tuGraphToBeUpdated.transitions.clear();
			tuGraphToBeUpdated.setTransitions(new ArrayList<ArrayList<TransCell>>());
			tuGraphToBeUpdated.stPositions.clear();
		}

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb);
		for (int it = 0; it < transitions.size(); it++)
		{
			double relbegaddress = transitions.get(it).getRelBegAddress();
			ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

			for (int io = 0; io < outgoings.size(); io++)
			{
				int tokenId = outgoings.get(io).getTokenId();
				double relEndAddress = outgoings.get(io).getRelEndAddress();

				tuGraphToBeUpdated.addTransition(relbegaddress, tokenId, relEndAddress);
			}
		}
		return tuGraphToBeUpdated;
	}

	/**
	 * For text unit with given number (tuNb) finds the transitions for given beginning address (theRelBegAddress) for
	 * which token id is equal to given one (theTokenId) and removes them from the list of transitions.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @param theRelBegAddress
	 *            - relative beginning address of TU
	 * @param theTokenId
	 *            - id of the token
	 * @param relEndAddress
	 *            - relative ending address of TU
	 * @return true if removal succeeds, false otherwise
	 */
	public final boolean removeTransition(int tuNb, double theRelBegAddress, int theTokenId,
			RefObject<Double> relEndAddress)
	{
		ParameterCheck.mandatory("relEndAddress", relEndAddress);

		relEndAddress.argvalue = 0.0;

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb);

		for (int it = 0; it < transitions.size(); it++)
		{
			double relBegAddress = transitions.get(it).getRelBegAddress();
			if (relBegAddress != theRelBegAddress)
			{
				continue;
			}

			ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

			for (int io = 0; io < outgoings.size(); io++)
			{
				int tokenId = outgoings.get(io).getTokenId();

				if (tokenId == theTokenId)
				{
					Double newRelEndAddressValue = outgoings.get(io).getRelEndAddress();
					relEndAddress.argvalue = newRelEndAddressValue;
					outgoings.remove(io);
					nbOfTransitions--;

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Finds all ambiguities of text unit marked with the given number.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @return list of ambiguities
	 */
	final ArrayList<AmbiguitiesUnambiguitiesObject> getAllAmbiguitiesInTextUnit(int tuNb)
	{
		// ambiguities[3*i] = relBegAddress, ambiguities[3*i+1] = {tokenId1, tokenId2, ...},
		// ambiguities[3*i+2] = relEndAddress
		ArrayList<AmbiguitiesUnambiguitiesObject> ambiguities = new ArrayList<AmbiguitiesUnambiguitiesObject>();

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb);

		for (int it = 0; it < transitions.size(); it++)
		{
			double relBegAddress = transitions.get(it).getRelBegAddress();
			double relEndAddress = relBegAddress;
			ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

			// If there is more than one pair of type (tokenId, relEndAddress) in list of transitions for the given
			// TU...
			if (outgoings.size() > 1)
			{
				ArrayList<Integer> tokenIds = new ArrayList<Integer>();

				// ...all the id's are added to proper list and ending address to proper variable...
				for (int io = 0; io < outgoings.size(); io++)
				{
					int tokenId = outgoings.get(io).getTokenId();
					tokenIds.add(tokenId);
					double end = outgoings.get(io).getRelEndAddress();
					if (relEndAddress < end)
					{
						relEndAddress = end;
					}
				}
				// ...and they are added to list of ambiguities.
				Collections.sort(tokenIds);

				ambiguities.add(new AmbiguitiesUnambiguitiesObject(relBegAddress, tokenIds, relEndAddress));
			}
		}
		return ambiguities;
	}

	/**
	 * Finds all unambiguities of the text unit marked with the given number.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @return list of unambiguities
	 */
	final ArrayList<AmbiguitiesUnambiguitiesObject> getAllUnambiguitiesInTextUnit(int tuNb)
	{
		ArrayList<AmbiguitiesUnambiguitiesObject> unambiguities = new ArrayList<AmbiguitiesUnambiguitiesObject>();

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb);

		for (int it = 0; it < transitions.size(); it++)
		{
			double relBegAddress = transitions.get(it).getRelBegAddress();
			double relEndAddress = relBegAddress;

			ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

			// If there is exactly one pair of type (tokenId, relEndAddress) in list of transitions for the given TU...
			if (outgoings.size() == 1)
			{
				ArrayList<Integer> tokenIds = new ArrayList<Integer>();

				// ...all the id's are added to proper list and ending address to proper variable...
				for (int io = 0; io < outgoings.size(); io++)
				{
					int tokenId = outgoings.get(io).getTokenId();
					tokenIds.add(tokenId);
					double end = outgoings.get(io).getRelEndAddress();
					if (relEndAddress < end)
					{
						relEndAddress = end;
					}
				}

				// ...and they are added to list of ambiguities.
				Collections.sort(tokenIds);

				unambiguities.add(new AmbiguitiesUnambiguitiesObject(relBegAddress, tokenIds, relEndAddress));
			}
		}
		return unambiguities;
	}

	/**
	 * Finds the next address which is available for unit with the given number, that is between given beginning and
	 * ending addresses.
	 * 
	 * @param tuNb
	 *            - number of the text unit
	 * @param relBegAddress
	 *            - beginning address from which transitions are taken into count
	 * @param relEndAddress
	 *            - ending address till which transitions are taken into count
	 * @return - address
	 */
	final double getANewVirginAddress(int tuNb, double relBegAddress, double relEndAddress)
	{
		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit

		// get the outgoing transitions between relBegAddress and relEndAddress
		double largest = relBegAddress;

		for (int i = 0; i < transitions.size(); i++)
		{
			double current = transitions.get(i).getRelBegAddress();
			if (current >= relBegAddress && current < relEndAddress)
			{
				if (current > largest)
				{
					largest = current;
				}
			}
		}

		int zz = (int) ((largest + 0.005) * 100) + 1;
		largest = zz / 100.0;
		return largest;
	}

	/**
	 * Finds all the outgoing transitions for the text unit with given number which begin at the given address.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @param relBegAddress
	 *            - beginning address for transitions
	 * @return list of outgoing transitions
	 */
	final ArrayList<TransitionPair> getOutgoingTransitions(int tuNb, double relBegAddress)
	{
		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit

		// get the outgoing transitions for relbegaddress
		int index = -1;
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				index = i;
				break;
			}
		}

		if (index == -1)
		{
			return null;
		}

		return transitions.get(index).getOutgoings();
	}

	/**
	 * Shifts all transitions - calculates new addresses, lengths and transitions for all text units.
	 * 
	 * @param shift
	 *            - array of shifts
	 */
	final void shiftAllTransitions(int[] shift)
	{
		ParameterCheck.mandatory("shift", shift);

		// first compute new text units' addresses
		int[] newAddresses = new int[this.tuAddresses.length];
		int[] newLengths = new int[this.tuLengths.length];

		for (int itu = 0; itu < this.tuAddresses.length; itu++)
		{
			int tuAddress = this.tuAddresses[itu];
			int newAddress = shift[tuAddress];

			int tuLength = this.tuLengths[itu];
			int end = tuAddress + tuLength;
			int newend = shift[end];
			int newLength = newend - newAddress;

			newAddresses[itu] = newAddress;
			newLengths[itu] = newLength;
		}

		// then shift all transitions
		for (int itu = 0; itu < this.tuAddresses.length; itu++)
		{
			
			ArrayList<TransitionObject> transitions = this.aTransitions.get(itu);
			if (transitions == null)
			{
				continue;
			}

			int startAdd = this.tuAddresses[itu];
			for (int i = 0; i < transitions.size(); i++)
			{
				double relBeg = transitions.get(i).getRelBegAddress();
				double beg = relBeg + startAdd;
				int iBeg = (int) beg;
				double rBeg = beg - iBeg;
				double newBeg = shift[iBeg] + rBeg;

				transitions.get(i).setRelBegAddress(newBeg - newAddresses[itu]);

				ArrayList<TransitionPair> outgoings = transitions.get(i).getOutgoings();

				for (int j = 0; j < outgoings.size(); j++)
				{
					double relEnd = outgoings.get(j).getRelEndAddress();
					double end = relEnd + startAdd;
					int iEnd = (int) end;
					double rEnd = end - iEnd;
					double newEnd = shift[iEnd] + rEnd;

					outgoings.get(j).setRelEndAddress(newEnd - newAddresses[itu]);
				}
			}
		}

		// finally shift all text units
		for (int itu = 0; itu < this.tuAddresses.length; itu++)
		{
			this.tuAddresses[itu] = newAddresses[itu];
			this.tuLengths[itu] = newLengths[itu];
		}
	}

	/**
	 * Adds transition to text unit with given number, that begins at given address, ends at given address and has given
	 * token id.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @param relBegAddress
	 *            - beginning address
	 * @param tokenId
	 *            - id of token to be added to list
	 * @param relEndAddress
	 *            - ending address to be added to the list
	 */
	public final void addTransition(int tuNb, double relBegAddress, int tokenId, double relEndAddress)
	{
		if (relBegAddress > maxRelBegAddress)
		{
			maxRelBegAddress = (int) relBegAddress;
		}

		if (relEndAddress - relBegAddress > maxLenAddress)
		{
			maxLenAddress = (int) (relEndAddress - relBegAddress);
		}

		if (tokenId > maxTokenId)
		{
			maxTokenId = tokenId;
		}

		nboftransitions++;

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit

		// get the outgoing transitions for relbegaddress
		int index = -1;
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				index = i;
				break;
			}
		}

		if (index == -1)
		{
			index = transitions.size();

			transitions.add(new TransitionObject(relBegAddress, new ArrayList<TransitionPair>()));

			ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();
			outgoings.add(new TransitionPair(tokenId, relEndAddress));
			transitions.get(index).setOutgoings(outgoings);

			nbOfTransitions++;
		}
		else
		{
			ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();
			boolean alreadyThere = false;

			for (int io = 0; io < outgoings.size(); io++)
			{
				int tid = outgoings.get(io).getTokenId();
				double rel = outgoings.get(io).getRelEndAddress();

				if (tokenId == tid && relEndAddress == rel)
				{
					alreadyThere = true;
					break;
				}
			}

			if (!alreadyThere)
			{
				outgoings.add(new TransitionPair(tokenId, relEndAddress));

				nbOfTransitions++;
			}
		}
	}

	/**
	 * Helper method - from given list of transitions for given beginning address removes pair (tokenId, relEndAddress),
	 * if in given list of annotations appropriate lexeme does not contain "XREF".
	 * 
	 * @param transitions
	 *            - list of transitions
	 * @param annotations
	 *            - list of annotations
	 * @param begAddress
	 *            - beginning address, from which removal starts
	 * @return number of removed transitions
	 */
	private int filterOutNonXrefsTransitions(ArrayList<TransitionObject> transitions, ArrayList<Object> annotations,
			double begAddress)
	{
		ParameterCheck.mandatoryCollection("transitions", transitions);
		ParameterCheck.mandatoryCollection("annotations", annotations);

		int nbOfRemoved = 0;
		// delete all outgoing transitions from any decimal address (e.g. 32.1, 32.2, 32.3) inside int address (e.g. 32)
		for (int index = 0; index < transitions.size(); index++)
		{
			if (transitions.get(index).getRelBegAddress() == begAddress)
			{
				// there are outgoing transitions inside the word
				ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();

				// filter out all non XREFs transitions starting at the same address
				for (int io = 0; io < outgoings.size();)
				{
					int tk = outgoings.get(io).getTokenId();
					String lexeme = (String) annotations.get(tk);

					if (lexeme.indexOf("XREF") == -1)
					{
						outgoings.remove(io);

						nbOfRemoved++;
					}
					else
					{
						io++;
					}
				}
			}
		}
		return nbOfRemoved;
	}

	/**
	 * Adds an outgoing transition (tokenId, relEndAddress) to list of transitions starting with relBegAddress, if
	 * transitions do not already exist, or adds the mentioned transition and performs filtering of non-XREFs contained
	 * in annotations list, if transitions already exist.
	 * 
	 * @param tuNb
	 *            - number of text unit
	 * @param relBegAddress
	 *            - beginning address
	 * @param tokenId
	 *            - id of token
	 * @param annotations
	 *            -
	 * @param relEndAddress
	 *            - ending address
	 */
	final void deleteNonXrefsAndAddTransition(int tuNb, double relBegAddress, int tokenId,
			ArrayList<Object> annotations, double relEndAddress)
	{
		ParameterCheck.mandatoryCollection("annotations", annotations);

		if (relBegAddress > maxRelBegAddress)
		{
			maxRelBegAddress = (int) relBegAddress;
		}

		if (relEndAddress - relBegAddress > maxLenAddress)
		{
			maxLenAddress = (int) (relEndAddress - relBegAddress);
		}

		if (tokenId > maxTokenId)
		{
			maxTokenId = tokenId;
		}

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit # tunb

		// get the list of all outgoing transitions at relbegaddress
		int index = -1;
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				index = i;
				break;
			}
		}

		if (index == -1)
		{
			// no outgoing transition: just add the transition
			index = transitions.size();

			transitions.add(new TransitionObject(relBegAddress, new ArrayList<TransitionPair>()));

			ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();
			outgoings.add(new TransitionPair(tokenId, relEndAddress));
			transitions.get(index).setOutgoings(outgoings);

			nbOfTransitions++;
		}
		else
		{
			// there are outgoing transitions
			ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();

			outgoings.add(new TransitionPair(tokenId, relEndAddress));
			nbOfTransitions++;
			nbOfTransitions -= filterOutNonXrefsTransitions(transitions, annotations, relBegAddress);
		}
	}

	/**
	 * Deletes inconsistent XREFs.
	 * 
	 * @param annotations
	 * @param tuNb
	 */
	public final void filterInconsistentXrefs(ArrayList<Object> annotations, int tuNb)
	{
		ParameterCheck.mandatoryCollection("annotations", annotations);

		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit

		// get the outgoing transitions for relbegaddress
		HashMap<String, ArrayList<Integer>> hc = new HashMap<String, ArrayList<Integer>>();

		for (int it = 0; it < transitions.size(); it++)
		{
			ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

			for (int io = 0; io < outgoings.size(); io++)
			{
				int tkId = outgoings.get(io).getTokenId();

				String lbl = (String) annotations.get(tkId);
				if (lbl == null)
				{
					continue;
				}

				int index = lbl.indexOf("XREF=");
				if (index != -1)
				{
					int i;
					StringBuilder sb = new StringBuilder();
					for (i = index + (new String("XREF=")).length(); i < lbl.length()
							&& (Character.isDigit(lbl.charAt(i)) || lbl.charAt(i) == '.'); i++)
					{
						sb.append(lbl.charAt(i));
					}
					String xrefnb = sb.toString();

					// we know that there is a "XREF=24.3" => we need 3-1 more occurrences of XREF=24.3
					ArrayList<Integer> al = null;
					if (!hc.containsKey(xrefnb))
					{
						al = new ArrayList<Integer>();
						al.add(it);
						al.add(io);
						hc.put(xrefnb, al);
					}
					else
					{
						al = hc.get(xrefnb);
						al.add(it);
						al.add(io);
					}
				}
			}
		}

		// now check the consistency of each xrefnb
		boolean thereIsAnInconsistency = false;
		Set<String> hcKeySet = hc.keySet();
		for (String xrefnb : hcKeySet)
		{
			int index = xrefnb.indexOf('.');
			String scount = xrefnb.substring(index + 1);
			int count = Integer.parseInt(scount);
			ArrayList<Integer> al = hc.get(xrefnb);
			if (al.size() != count * 2)
			{
				// there is a consistency problem: number of annotations with XREF=24.3 is not equal to 3
				for (int ian = 0; ian < al.size(); ian += 3)
				{
					int it = al.get(ian);
					ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();

					int io = al.get(ian + 1);

					outgoings.get(io).setTokenId(-1);
					thereIsAnInconsistency = true;
				}
			}
		}

		if (thereIsAnInconsistency)
		{
			// cleanup -1 in annotations
			for (int it = 0; it < transitions.size(); it++)
			{
				ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();
				for (int io = 0; io < outgoings.size();)
				{
					int tkId = outgoings.get(io).getTokenId();
					if (tkId == -1)
					{
						outgoings.remove(io);
					}
					else
					{
						io++;
					}
				}
			}
		}
	}

	/**
	 * Removes transition from list of outgoing transitions for text unit with given number, starting from
	 * relBegAddress, which is parsed using given symbol...
	 * 
	 * @param annotations
	 *            - list of annotations from which label for parsing is acquired
	 * @param tuNb
	 *            - number of text unit
	 * @param relBegAddress
	 *            - beginning address
	 * @param symbol
	 *            - symbol for filtering
	 * @param anXrefWasRemoved
	 *            - flag
	 * @return number of removed transitions
	 */
	public final int filterTransitions(ArrayList<Object> annotations, int tuNb, double relBegAddress, String symbol,
			RefObject<Boolean> anXrefWasRemoved)
	{
		ParameterCheck.mandatoryCollection("annotations", annotations);
		ParameterCheck.mandatoryString("symbol", symbol);
		ParameterCheck.mandatory("anXrefWasRemoved", anXrefWasRemoved);

		anXrefWasRemoved.argvalue = false;
		int nbOfRemoved = 0;
		String entry = null, lemma = null, category = null;
		String[] features = null;
		boolean negation = false;

		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);

		boolean tempVar = !Dic.parseSymbolFeatureArray(symbol, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features, tempRef_negation);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		negation = tempRef_negation.argvalue;
		if (tempVar)
		{
			return nbOfRemoved;
		}

		
		ArrayList<TransitionObject> transitions = aTransitions.get(tuNb); // all the transitions in text unit

		// get the outgoing transitions for relBegAddress
		int index = -1;
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				index = i;
				break;
			}
		}

		// If no transitions are found for given beginning address, 0 is returned.
		if (index == -1)
		{
			return nbOfRemoved;
		}

		ArrayList<TransitionPair> initialTrans = transitions.get(index).getOutgoings();
		if (initialTrans.isEmpty())
		{
			return nbOfRemoved;
		}

		// Using copy-constructor instead of clone() - recommended because of unchecked class cast
		ArrayList<TransitionPair> outgoings = new ArrayList<TransitionPair>(initialTrans);
		for (int io = 0; io < outgoings.size(); io++)
		{
			boolean anxref = false;
			int tkId = outgoings.get(io).getTokenId();
			String lbl = (String) annotations.get(tkId);
			if (lbl == null)
			{
				continue;
			}

			String lEntry = null, lLemma = null, lCategory = null;
			String[] lFeatures = null;
			RefObject<String> tempRef_lEntry = new RefObject<String>(lEntry);
			RefObject<String> tempRef_lLemma = new RefObject<String>(lLemma);
			RefObject<String> tempRef_lCategory = new RefObject<String>(lCategory);
			RefObject<String[]> tempRef_lfeatures = new RefObject<String[]>(lFeatures);

			boolean tempVar2 = !Dic.parseDELAFFeatureArray(lbl, tempRef_lEntry, tempRef_lLemma, tempRef_lCategory,
					tempRef_lfeatures);

			lEntry = tempRef_lEntry.argvalue;
			lLemma = tempRef_lLemma.argvalue;
			lCategory = tempRef_lCategory.argvalue;
			lFeatures = tempRef_lfeatures.argvalue;
			if (tempVar2)
			{
				continue;
			}

			if (lCategory.equals("XREF") || Dic.lookFor("XREF", lFeatures) != null)
			{
				anxref = true;
			}

			if (!negation)
			{
				if (entry != null)
				{
					if (!entry.equals(lEntry))
					{
						outgoings.remove(io);
						this.nbOfTransitions--;

						if (anxref)
						{
							anXrefWasRemoved.argvalue = true;
						}
						nbOfRemoved++;

						io--;
						continue;
					}
				}

				if (lemma != null)
				{
					if (!lemma.equals(lLemma))
					{
						outgoings.remove(io);
						this.nbOfTransitions--;

						if (anxref)
						{
							anXrefWasRemoved.argvalue = true;
						}
						nbOfRemoved++;

						io--;
						continue;
					}
				}

				if (category != null)
				{
					if (!category.equals(lCategory) && !category.equals("DIC")) // added support for <DIC> in output
					{
						outgoings.remove(io);
						this.nbOfTransitions--;

						if (anxref)
						{
							anXrefWasRemoved.argvalue = true;
						}
						nbOfRemoved++;

						io--;
						continue;
					}
				}

				// match features against lfeatures
				if (features != null)
				{
					boolean match = false;
					for (String feature : features)
					{
						if (feature.charAt(0) == '+')
						{
							match = false;
							if (lFeatures != null)
							{
								for (String lfeature : lFeatures)
								{
									if (lfeature.equals(feature.substring(1)))
									{
										match = true;
										break;
									}
									else if (lfeature.indexOf('=') != -1)
									{
										String val = lfeature.substring(lfeature.indexOf('=') + 1);
										if (val.equals(feature.substring(1)))
										{
											match = true;
											break;
										}
									}
								}
							}
							if (!match)
							{
								break;
							}
						}
						else
						{
							match = true;
							if (lFeatures != null)
							{
								for (String lfeature : lFeatures)
								{
									if (lfeature.equals(feature.substring(1)))
									{
										match = false;
										break;
									}
									else if (lfeature.indexOf('=') != -1)
									{
										String val = lfeature.substring(lfeature.indexOf('=') + 1);
										if (val.equals(feature.substring(1)))
										{
											match = false;
											break;
										}
									}
								}
							}
							if (!match)
							{
								break;
							}
						}
					}

					if (!match)
					{
						outgoings.remove(io);
						this.nbOfTransitions--;

						if (anxref)
						{
							anXrefWasRemoved.argvalue = true;
						}
						nbOfRemoved++;

						io--;
						continue;
					}
				}
			}
			else
			// there is a negation
			{
				if (entry != null)
				{
					if (!entry.equals(lEntry))
					{
						continue;
					}
				}
				else if (lemma != null)
				{
					if (!lemma.equals(lLemma))
					{
						continue;
					}
				}
				else if (category != null)
				{
					if (!category.equals(lCategory))
					{
						continue;
					}
				}

				// lemma or category match
				if (features == null)
				{
					outgoings.remove(io);
					this.nbOfTransitions--;

					if (anxref)
					{
						anXrefWasRemoved.argvalue = true;
					}
					nbOfRemoved++;

					io--;
					continue;
				}

				// match features against lFeatures
				boolean match = false;
				for (String feature : features)
				{
					if (feature.charAt(0) == '+')
					{
						match = false;
						if (lFeatures != null)
						{
							for (String lfeature : lFeatures)
							{
								if (lfeature.equals(feature.substring(1)))
								{
									match = true;
									break;
								}
								else if (lfeature.indexOf('=') != -1)
								{
									String val = lfeature.substring(lfeature.indexOf('=') + 1);
									if (val.equals(feature.substring(1)))
									{
										match = true;
										break;
									}
								}
							}
						}
						if (!match)
						{
							break;
						}
					}
					else
					{
						match = true;
						if (lFeatures != null)
						{
							for (String lfeature : lFeatures)
							{
								if (lfeature.equals(feature.substring(1)))
								{
									match = false;
									break;
								}
								else if (lfeature.indexOf('=') != -1)
								{
									String val = lfeature.substring(lfeature.indexOf('=') + 1);
									if (val.equals(feature.substring(1)))
									{
										match = false;
										break;
									}
								}
							}
						}
						if (!match)
						{
							break;
						}
					}
				}

				if (match)
				{
					outgoings.remove(io);
					this.nbOfTransitions--;

					if (anxref)
					{
						anXrefWasRemoved.argvalue = true;
					}
					nbOfRemoved++;

					io--;
					continue;
				}
			}
		}

		if (outgoings.size() > 0)
		{
			transitions.get(index).setOutgoings(outgoings);
		}
		return nbOfRemoved;
	}

	/**
	 * Checks if the list of outgoing transitions for given text unit number contains given relative beginning address.
	 * 
	 * @param tuNb
	 *            - text unit number
	 * @param relBegAddress
	 *            - relative beginning address to be found in list of transitions
	 * @return true if the address is found, false otherwise
	 */
	final boolean thereAreLexs(int tuNb, double relBegAddress)
	{
		ArrayList<TransitionObject> transitions = this.aTransitions.get(tuNb); // all the transitions in text unit

		// get the list of outgoing transitions for relBegAddress
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * For text unit with given number and transitions starting from relBegAddress, fills array lists with ids of tokens
	 * and lengths of transitions (relEndAddress - re
	 * 
	 * @param tuNb
	 * @param relBegAddress
	 * @param lexIds
	 * @param fLengths
	 * @return
	 * 
	 */
	final int getAllLexIds(int tuNb, double relBegAddress, RefObject<ArrayList<Integer>> lexIds,
			RefObject<ArrayList<Double>> fLengths)
	{
		ParameterCheck.mandatory("lexIds", lexIds);
		ParameterCheck.mandatory("fLengths", fLengths);

		lexIds.argvalue = null;
		fLengths.argvalue = null;

		// Reuse of what was already written instead of the following commented segment of the code.
		ArrayList<TransitionPair> outgoings = getOutgoingTransitions(tuNb, relBegAddress);
		if (outgoings == null)
			return 0;

		for (int io = 0; io < outgoings.size(); io++)
		{
			if (lexIds.argvalue == null)
			{
				lexIds.argvalue = new ArrayList<Integer>();
				fLengths.argvalue = new ArrayList<Double>();
			}

			lexIds.argvalue.add(outgoings.get(io).getTokenId());

			long hund_len = (long) (100 * (outgoings.get(io).getRelEndAddress())) - (long) (100 * relBegAddress);
			double len = hund_len / 100.0;
			fLengths.argvalue.add(len);
		}

		if (lexIds.argvalue == null)
		{
			return 0;
		}

		// remove duplicates
		for (int iLex = 1; iLex < lexIds.argvalue.size();)
		{
			int lexId = lexIds.argvalue.get(iLex);
			double len = fLengths.argvalue.get(iLex);

			boolean found = false;
			for (int jLex = 0; jLex < iLex; jLex++)
			{
				int jLexId = lexIds.argvalue.get(jLex);
				double jlen = fLengths.argvalue.get(jLex);
				if (lexId == jLexId && len == jlen)
				{
					found = true;
					break;
				}
			}

			if (found)
			{
				lexIds.argvalue.remove(iLex);
				fLengths.argvalue.remove(iLex);
			}
			else
			{
				iLex++;
			}
		}
		return lexIds.argvalue.size();
	}

	final int getAllLexIdsAndContracted(int tuNb, double relBegAddress, RefObject<ArrayList<Integer>> lexIds,
			RefObject<ArrayList<Double>> fLengths)
	{
		ParameterCheck.mandatory("lexIds", lexIds);
		ParameterCheck.mandatory("fLengths", fLengths);

		lexIds.argvalue = null;
		fLengths.argvalue = null;

		ArrayList<TransitionObject> transitions = this.aTransitions.get(tuNb); // all the transitions in text unit

		// get the list of outgoing transitions for relBegAddress
		int index = -1;
		for (int i = 0; i < transitions.size(); i++)
		{
			if (transitions.get(i).getRelBegAddress() == relBegAddress)
			{
				index = i;
				break;
			}
		}

		if (index == -1)
		{
			return 0;
		}

		ArrayList<TransitionPair> outgoings = transitions.get(index).getOutgoings();

		for (int io = 0; io < outgoings.size(); io++)
		{
			if (lexIds.argvalue == null)
			{
				lexIds.argvalue = new ArrayList<Integer>();
				fLengths.argvalue = new ArrayList<Double>();
			}

			lexIds.argvalue.add(outgoings.get(io).getTokenId());

			double len = (outgoings.get(io).getRelEndAddress()) - relBegAddress;
			fLengths.argvalue.add(len);
		}

		if (lexIds.argvalue == null)
		{
			return 0;
		}

		double relBegAddress2 = relBegAddress;
		do
		{
			relBegAddress2 += 0.01;
			// get the list of outgoing transitions for relBegAddress
			index = -1;
			for (int i = 0; i < transitions.size(); i++)
			{
				if (transitions.get(i).getRelBegAddress() == relBegAddress2)
				{
					index = i;
					break;
				}
			}

			if (index != -1)
			{
				outgoings = transitions.get(index).getOutgoings();
				for (int io = 0; io < outgoings.size(); io++)
				{
					lexIds.argvalue.add(outgoings.get(io).getTokenId());

					double len = outgoings.get(io).getRelEndAddress() - relBegAddress;
					fLengths.argvalue.add(len);
				}
			}
		}
		while (index != -1);

		// remove duplicates
		for (int iLex = 1; iLex < lexIds.argvalue.size();)
		{
			int lexId = lexIds.argvalue.get(iLex);
			double len = fLengths.argvalue.get(iLex);

			boolean found = false;
			for (int jLex = 0; jLex < iLex; jLex++)
			{
				int jlexid = lexIds.argvalue.get(jLex);
				double jlen = fLengths.argvalue.get(jLex);
				if (lexId == jlexid && len == jlen)
				{
					found = true;
					break;
				}
			}

			if (found)
			{
				lexIds.argvalue.remove(iLex);
				fLengths.argvalue.remove(iLex);
			}
			else
			{
				iLex++;
			}
		}
		return lexIds.argvalue.size();
	}

	private int[] nbOfTransPerTu;
	// Following are uints and ushorts
	private short[] trans_beg_s;
	private int[] trans_beg_i;
	private byte[] trans_end_b;
	private short[] trans_end_s;
	private int[] trans_end_i;
	private short[] trans_tok_s;
	private int[] trans_tok_i;
	private short[] lengths_s;
	private int[] lengths_i;

	/**
	 * Function computes values of mft's variables before saving is called.
	 * 
	 * @param multiplier
	 */
	public final void beforeSaving(double multiplier)
	{
		// compute text unit maximum length
		lengths_s = null;
		lengths_i = null;
		int maxLength = 0;

		for (int i = 0; i < tuLengths.length; i++)
		{
			if (tuLengths[i] > maxLength)
			{
				maxLength = tuLengths[i];
			}
		}

		if (maxLength < Short.MAX_VALUE)
		{
			lengths_s = new short[tuLengths.length];
			for (int i = 0; i < tuLengths.length; i++)
			{
				lengths_s[i] = (short) tuLengths[i];
			}
		}
		else
		{
			lengths_i = new int[tuLengths.length];
			for (int i = 0; i < tuLengths.length; i++)
			{
				lengths_i[i] = tuLengths[i];
			}
		}

		nbOfTransPerTu = new int[aTransitions.size()];

		// save all begaddresses
		trans_beg_s = null;
		trans_beg_i = null;
		int ibeg = 0;

		if (maxRelBegAddress * multiplier < Short.MAX_VALUE)
		{
			trans_beg_s = new short[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				nbOfTransPerTu[itu] = 0;

				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					short relbegaddress = (short) (trans.get(j).getRelBegAddress() * multiplier);
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						trans_beg_s[ibeg++] = relbegaddress;
						nbOfTransPerTu[itu]++;
					}
				}
			}
		}
		else
		{
			trans_beg_i = new int[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				nbOfTransPerTu[itu] = 0;
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					int relBegAddress = (int) (trans.get(j).getRelBegAddress() * multiplier);
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						trans_beg_i[ibeg++] = relBegAddress;
						nbOfTransPerTu[itu]++;
					}
				}
			}
		}

		// save all endaddresses as lengths
		trans_end_b = null;
		trans_end_s = null;
		trans_end_i = null;
		int iend = 0;

		if (maxLenAddress * multiplier < Byte.MAX_VALUE)
		{
			trans_end_b = new byte[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					int relbegaddress = (int) (multiplier * trans.get(j).getRelBegAddress());
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						int relendaddress = (int) (multiplier * state.get(k).getRelEndAddress());
						int len = relendaddress - relbegaddress;
						trans_end_b[iend++] = (byte) len;
					}
				}
			}
		}
		else if (maxLenAddress < Short.MAX_VALUE)
		{
			trans_end_s = new short[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					int relbegaddress = (int) (multiplier * trans.get(j).getRelBegAddress());
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						int relEndAddress = (int) (multiplier * state.get(k).getRelEndAddress());
						int len = relEndAddress - relbegaddress;
						trans_end_s[iend++] = (short) len;
					}
				}
			}
		}
		else
		{
			trans_end_i = new int[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					int relbegaddress = (int) (multiplier * trans.get(j).getRelBegAddress());
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						int relendaddress = (int) (multiplier * state.get(k).getRelEndAddress());
						int len = relendaddress - relbegaddress;
						trans_end_i[iend++] = len;
					}
				}
			}
		}

		// save all token ids
		trans_tok_s = null;
		trans_tok_i = null;
		int itok = 0;

		if (maxTokenId < Short.MAX_VALUE)
		{
			trans_tok_s = new short[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();
					for (int k = 0; k < state.size(); k++)
					{
						int tokenId = state.get(k).getTokenId();
						trans_tok_s[itok++] = (short) tokenId;
					}
				}
			}
		}
		else
		{
			trans_tok_i = new int[nboftransitions];
			for (int itu = 1; itu < aTransitions.size(); itu++)
			{
				ArrayList<TransitionObject> trans = aTransitions.get(itu); // transitions for the text unit

				for (int j = 0; j < trans.size(); j++)
				{
					ArrayList<TransitionPair> state = trans.get(j).getOutgoings();

					for (int k = 0; k < state.size(); k++)
					{
						int tokenId = state.get(k).getTokenId();
						trans_tok_i[itok++] = tokenId;
					}
				}
			}
		}
	}

	/**
	 * Function computes values of mft's variables after loading.
	 * 
	 * @param divider
	 */
	final void afterLoading(double divider)
	{
		if (divider == 0.0)
		{
			divider = 10.0;
		}
		this.multiplier = divider;

		// compute text units lengths
		if (lengths_s != null)
		{
			tuLengths = new int[lengths_s.length];
			for (int i = 0; i < lengths_s.length; i++)
			{
				tuLengths[i] = lengths_s[i];
			}
			lengths_s = null;
		}
		else
		{
			tuLengths = new int[lengths_i.length];
			for (int i = 0; i < lengths_i.length; i++)
			{
				tuLengths[i] = lengths_i[i];
			}
			lengths_i = null;
		}

		aTransitions = new ArrayList<ArrayList<TransitionObject>>();

		int originalNbOfTransitions = nboftransitions;

		if (originalNbOfTransitions == 0)
		{
			aTransitions.add(null);
			for (int itu = 1; itu < tuAddresses.length; itu++)
			{
				aTransitions.add(new ArrayList<TransitionObject>());
			}
		}
		else
		{
			int iTrans = 0;
			int tokenId;
			double relBegAddress, relEndAddress;

			if (trans_beg_s != null)
			{
				relBegAddress = trans_beg_s[0] / divider;
			}
			else
			{
				relBegAddress = trans_beg_i[0] / divider;
			}

			if (trans_end_b != null)
			{
				relEndAddress = relBegAddress + trans_end_b[0] / divider;
			}
			else if (trans_end_s != null)
			{
				relEndAddress = relBegAddress + trans_end_s[0] / divider;
			}
			else
			{
				relEndAddress = relBegAddress + trans_end_i[0] / divider;
			}

			if (trans_tok_s != null)
			{
				tokenId = trans_tok_s[0];
			}
			else
			{
				tokenId = trans_tok_i[0];
			}

			aTransitions.add(null);
			for (int itu = 1; itu < tuAddresses.length; itu++)
			{
				aTransitions.add(new ArrayList<TransitionObject>());

				for (int jtrans = 0; jtrans < nbOfTransPerTu[itu]; jtrans++)
				{
					addTransition(itu, relBegAddress, tokenId, relEndAddress);
					iTrans++;

					if (iTrans >= originalNbOfTransitions)
					{
						break;
					}

					if (trans_beg_s != null)
					{
						relBegAddress = trans_beg_s[iTrans] / divider;
					}
					else
					{
						relBegAddress = trans_beg_i[iTrans] / divider;
					}

					if (trans_end_b != null)
					{
						relEndAddress = relBegAddress + trans_end_b[iTrans] / divider;
					}
					else if (trans_end_s != null)
					{
						relEndAddress = relBegAddress + trans_end_s[iTrans] / divider;
					}
					else
					{
						relEndAddress = relBegAddress + trans_end_i[iTrans] / divider;
					}

					if (trans_tok_s != null)
					{
						tokenId = trans_tok_s[iTrans];
					}
					else
					{
						tokenId = trans_tok_i[iTrans];
					}
				}
			}
		}

		trans_beg_s = null;
		trans_beg_i = null;
		trans_end_b = null;
		trans_end_s = null;
		trans_end_i = null;
		trans_tok_s = null;
		trans_tok_i = null;
	}
}