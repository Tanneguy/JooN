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

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.helper.ParameterCheck;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Used for storing dictionary in a finite state transducer
 * 
 * @author Silberztein Max
 * 
 */
public class FSDic implements Serializable
{

	private static final long serialVersionUID = -4795046378802556971L;

	transient ArrayList<DState> states;
	transient private HashMap<String, Integer> he = null; // hashtable for equivalent name => state number
	transient private ArrayList<String> ae = null; // reverse hashtable for state number => equivalent names
	private transient HashMap<String, Integer> hi; // hashtable for each piece of info (lemma,infolemma,infoflex)
	transient private HashMap<Character, Integer> hc; // hashtable for the compacted alphabet
	transient private int charnb; // nb of characters in input
	transient private int[] stateaddress; // for ToBinary

	transient Language Lan;
	private transient String Author;
	private transient String Institution;
	private transient int Protection;

	private transient int alphabetlog;
	private transient int infolog;
	private transient int buflog, buflogc;
	private transient char[] alphabet; // array that stores the compacted alphabet
	transient String[] infos; // array that stores the compacted infos
	private transient String[] infobitstable; // array that stores the compacted info bits

	transient UnsignedByte[] buffer; // the whole FSA for simple word forms
	transient UnsignedByte[] bufferc; // the whole FSA for compound word forms
	transient UnsignedByte[] bufferl; // the whole FSA for simple word forms' lemma
	transient UnsignedByte[] buffercl; // the whole FSA for compound word forms' lemma

	transient HashMap<String, Gram> paradigms; // for Inflection

	private HashMap<String, Object> Params; // all of the above

	/**
	 * constructs new FSDic
	 * 
	 * @param isoname
	 */
	FSDic(String isoname)
	{
		ParameterCheck.mandatory("isoname", isoname);
		Lan = new Language(isoname);
		if (Lan != null)
		{
			states = new ArrayList<DState>();
			createState();

			infos = null; // index => information

			he = null;
			hc = new HashMap<Character, Integer>(); // alphabet
			charnb = 1;

			hi = new HashMap<String, Integer>();
		}
	}

	/**
	 * Uncompacts information
	 * 
	 * @param information
	 * @return uncompacted information as an array of strings
	 */
	private ArrayList<String> unCompactInfo(String information)
	{
		ParameterCheck.mandatoryString("information", information);
		ArrayList<String> info = new ArrayList<String>();

		if (information.length() > 8 && information.substring(0, 8).equals("4 bytes:"))
		{
			for (int i = 8; i < information.length();)
			{
				String inf = "";
				for (; i < information.length(); i += 2)
				{
					char h = information.charAt(i);
					char l = information.charAt(i + 1);
					if (h == '\0' && l == '\0')
					{
						break;
					}
					int infnb = h * 65536 + l;
					inf += this.infobitstable[infnb];
				}
				info.add(inf);
				if (i < information.length()) // two characters for each information code
				{
					i += 2;
				}
			}
		}
		else
		{
			for (int i = 0; i < information.length();)
			{
				String inf = "";
				for (; i < information.length() && information.charAt(i) != '\0'; i++)
				{
					int infonb = information.charAt(i);
					if (infonb < this.infobitstable.length)
					{
						inf += this.infobitstable[infonb];
					}
					else
					{
						inf += "+ERROR";
					}
				}
				info.add(inf);
				if (i < information.length())
				{
					i++;
				}
			}
		}
		return info;
	}

	/**
	 * Compacts informationlemma and informationflex
	 * 
	 * @param informationlemma
	 * @param informationflex
	 * @param dics
	 * @return
	 */
	private String compactInfo(String informationlemma, String informationflex, FSDic dics)
	{
		ArrayList<Integer> comp = new ArrayList<Integer>();

		String[] fields2 = new String[2];
		fields2[0] = informationlemma;
		fields2[1] = informationflex;
		for (int i = 0; i < fields2.length; i++)
		{
			String fd2;
			if (i == 0)
			{
				if (comp.size() > 0)
				{
					fd2 = "," + fields2[i];
				}
				else
				{
					fd2 = fields2[i];
				}
			}
			else
			{
				fd2 = "+" + fields2[i];
			}
			if (!dics.hi.containsKey(fd2))
			{
				int encoder = dics.hi.size() + 1;
				dics.hi.put(fd2, encoder);
			}
			comp.add(dics.hi.get(fd2));
		}

		// do we need four bytes to encode the information index?
		boolean needfourbytes = false;
		for (int i = 0; i < comp.size(); i++)
		{
			int c = comp.get(i);
			if (c >= Character.MAX_VALUE)
			{
				needfourbytes = true;
				break;
			}
		}

		StringBuilder result = new StringBuilder();
		if (!needfourbytes)
		{
			for (int i = 0; i < comp.size(); i++)
			{
				result.append(comp.get(i));
			}
		}
		else
		{
			result.append("4 bytes:");
			for (int i = 0; i < comp.size(); i++)
			{
				int c = comp.get(i);
				char h = (char) (c / 65536);
				char l = (char) (c % 65536);
				result.append(h);
				result.append(l);
			}
		}
		return result.toString();
	}

	/**
	 * Compacts lemma informationlemma and informationflex
	 * 
	 * @param lemma
	 * @param informationlemma
	 * @param informationflex
	 * @param dics
	 * @return
	 */
	private String compactInfo(String lemma, String informationlemma, String informationflex, FSDic dics)
	{
		ArrayList<Integer> comp = new ArrayList<Integer>();

		if (!dics.hi.containsKey(lemma))
		{
			int encoder = dics.hi.size() + 1;
			dics.hi.put(lemma, encoder);
		}
		comp.add(dics.hi.get(lemma));

		String info0 = "," + informationlemma;
		if (!dics.hi.containsKey(info0))
		{
			int encoder = dics.hi.size() + 1;
			dics.hi.put(info0, encoder);
		}
		comp.add(dics.hi.get(info0));

		if (!dics.hi.containsKey(informationflex))
		{
			int encoder = dics.hi.size() + 1;
			dics.hi.put(informationflex, encoder);
		}
		comp.add(dics.hi.get(informationflex));

		// do we need four bytes to encode the information index?
		boolean needfourbytes = false;
		for (int i = 0; i < comp.size(); i++)
		{
			int c = comp.get(i);
			if (c >= Character.MAX_VALUE)
			{
				needfourbytes = true;
				break;
			}
		}

		StringBuilder result = new StringBuilder();
		if (!needfourbytes)
		{
			for (int i = 0; i < comp.size(); i++)
			{
				int c = comp.get(i);
				result.append((char) c);
			}
		}
		else
		{
			result.append("4 bytes:");
			for (int i = 0; i < comp.size(); i++)
			{
				int c = comp.get(i);
				char h = (char) (c / 65536);
				char l = (char) (c % 65536);
				result.append(h);
				result.append(l);
			}
		}
		return result.toString();
	}

	transient private ArrayList<Integer> FreeStates = null;

	/**
	 * 
	 * @param index
	 */
	private void deleteState(int index)
	{
		states.set(index, null);
		ae.set(index, null);
		if (FreeStates == null)
		{
			FreeStates = new ArrayList<Integer>();
		}
		FreeStates.add(index);
	}

	/**
	 * 
	 * @param iparent
	 * @return
	 */
	private int createState(int iparent)
	{
		DState nstate = new DState(iparent);
		int index = -1;
		if (FreeStates != null)
		{
			int c = FreeStates.size();
			if (c > 0)
			{
				index = FreeStates.get(c - 1);
				FreeStates.remove(c - 1);
			}
		}
		if (index != -1)
		{
			states.set(index, nstate);
			ae.set(index, null);
		}
		else
		{
			states.add(nstate);
			index = states.size() - 1;
			if (ae == null)
			{
				ae = new ArrayList<String>();
			}
			ae.add(null);
		}

		return index;
	}

	/**
	 * 
	 * @return
	 */
	private int createState()
	{
		DState nstate = new DState();
		int index = -1;
		if (FreeStates != null)
		{
			int c = FreeStates.size();
			if (c > 0)
			{
				index = FreeStates.get(c - 1);
				FreeStates.remove(c - 1);
			}
		}
		if (index != -1)
		{
			states.set(index, nstate);
			ae.set(index, null);
		}
		else
		{
			states.add(nstate);
			index = states.size() - 1;
			if (ae == null)
			{
				ae = new ArrayList<String>();
			}
			ae.add(null);
		}
		return index;
	}

	/**
	 * 
	 * @param istate
	 * @param iparent
	 */
	private void addParent(int istate, int iparent)
	{
		DState state = states.get(istate);
		for (int i = 0; i < state.parents.length; i++)
		{
			if (state.parents[i] == iparent)
			{
				return;
			}
		}
		int[] nparents = new int[state.parents.length + 1];
		for (int i = 0; i < state.parents.length; i++)
		{
			nparents[i] = state.parents[i];
		}
		nparents[nparents.length - 1] = iparent;
		state.parents = nparents;
	}

	/**
	 * 
	 * @param istate
	 * @param iparent
	 */
	private void removeParent(int istate, int iparent)
	{
		DState state = states.get(istate);
		int index = -1;
		for (int i = 0; i < state.parents.length; i++)
		{
			if (state.parents[i] == iparent)
			{
				index = i;
				break;
			}
		}
		if (index != -1)
		{
			int[] nparents = new int[state.parents.length - 1];
			for (int inew = 0; inew < index; inew++)
			{
				nparents[inew] = state.parents[inew];
			}
			for (int inew = index + 1; inew < state.parents.length; inew++)
			{
				nparents[inew - 1] = state.parents[inew];
			}
			state.parents = nparents;
		}
	}

	/**
	 * CLEANUP the Inflection Commands (text REs) for all grammars
	 */
	void cleanupInflectionCommands()
	{
		// CLEANUP the Inflection Commands (text REs) for all grammars
		if (this.paradigms != null)
		{
			for (Gram grm : this.paradigms.values())
			{
				grm.InflectionsCommands = null;
			}
		}
	}

	/**
	 * 
	 * @param istate
	 * @return
	 */
	private int setEqName(int istate)
	{
		DState cstate = states.get(istate);
		if (cstate.canonical)
		{
			return istate;
		}

		StringBuilder eqname = new StringBuilder();
		int eqstate;
		if (cstate.infonb != 0)
		{
			if (NbOfInfosForEq < Character.MAX_VALUE) 
			{
				char stateinfonb = (char) cstate.infonb;
				eqname.append(stateinfonb);
			}
			else
			{
				eqname.append(cstate.infonb);
			}
		}
		if (cstate.chars != null)
		{
			char cnboftrans = (char) cstate.chars.length;
			eqname.append(cnboftrans);
			if (cstate.chars.length > 1)
			{
				cstate.sortCharsDests();
			}
			for (int it = 0; it < cstate.chars.length; it++)
			{
				char character = cstate.chars[it];
				eqname.append(character);
				int dest = cstate.dests[it];
				int eqdest = setEqName(dest);
				eqname.append(eqdest);
			}
		}
		String eqn = eqname.toString();
		if (he != null && he.containsKey(eqn))
		{
			eqstate = he.get(eqn);
			if (eqstate != istate) // cstate is useless
			{
				if (verbose)
				{
					checkConsistency();
				}

			

				// replace cstate with estate in all cstate's parents Dests
				for (int iparent : cstate.parents)
				{
					DState pstate = states.get(iparent);
					pstate.replaceDest(istate, eqstate);

					// add iparent (no dupplicate) to estate.Parents
					addParent(eqstate, iparent);
				}

				// replace cstate with estate in all cstate's childrens' Parents
				if (cstate.dests != null)
				{
					for (int it = 0; it < cstate.dests.length; it++)
					{
						int ichildstate = cstate.dests[it];
						removeParent(ichildstate, istate);
						addParent(ichildstate, eqstate);
					}
				}
				String se = ae.get(istate);
				if (se != null)
				{
					he.remove(se);
				}
				deleteState(istate);
				if (verbose)
				{
					checkConsistency();
				}
			}
		}
		else
		{
			if (he == null)
			{
				he = new HashMap<String, Integer>();
			}
			he.put(eqn, istate);
			ae.set(istate, eqn);
			eqstate = istate;
			cstate.canonical = true;
		}
		return eqstate;
	}

	transient private int NbOfInfosForEq;

	/**
 * 
 */
	private void invalidateAllStates()
	{
		he = new HashMap<String, Integer>();
		ae = new ArrayList<String>();
		for (int istate = 0; istate < states.size(); istate++)
		{
			ae.add(null);
			DState cstate = states.get(istate);
			cstate.canonical = false;
		}
	}

	/**
	 * 
	 * @param istate
	 */
	private void invalidateStates(int istate)
	{
		DState s = states.get(istate);
		if (s.canonical)
		{
			s.canonical = false;
			String se = ae.get(istate);
			if (se != null)
			{
				he.remove(se);
			}
			ae.set(istate, null);
		}
		if (s.parents != null)
		{
			for (int iparent : s.parents)
			{
				invalidateStates(iparent);
			}
		}
	}

	/**
	 * 
	 * @param nbofinfos
	 */
	void minimize(int nbofinfos)
	{
		NbOfInfosForEq = nbofinfos;

		
		setEqName(0);
	}

	/**
	 * 
	 * @param istate
	 */
	private void cloneState(int istate)
	{
		DState cstate = states.get(istate);

		// create a new node for each parent's transition going into istate
		for (int ip = 0; ip < cstate.parents.length; ip++)
		{
			int iparent = cstate.parents[ip];
			DState pstate = states.get(iparent);

			if (pstate.dests == null)
			{
				continue;
			}

			for (int it = 0; it < pstate.dests.length; it++)
			{
				int dest = pstate.dests[it];
				if (dest != istate)
				{
					continue;
				}

				// clone cstate
				int instate = createState(iparent);
				DState nstate = states.get(instate);
				nstate.infonb = cstate.infonb;
				

				if (cstate.chars != null)
				{
					nstate.chars = cstate.chars.clone();
					nstate.dests = cstate.dests.clone();

					// make consistent nstate.Dests and each target's list of parents
					for (int jt = 0; jt < nstate.dests.length; jt++)
					{
						int id = nstate.dests[jt];
					
						addParent(id, instate);
						removeParent(id, istate); // actually: this should be useful only once
					}
				}

				// connect parent to the new node
				pstate.dests[it] = instate; // instead of previous istate
				// need to invalidate parent state
				invalidateStates(iparent);
			}
		}

		// delete cstate
		String se = ae.get(istate);
		if (se != null)
		{
			he.remove(se);
		}
		deleteState(istate);
	}

	/**
	 * 
	 * @param statestrace
	 * @return
	 */
	private boolean cloneTrace(ArrayList<Integer> statestrace)
	{
		ParameterCheck.mandatory("statestrace", statestrace);
		// clone states in trace
		boolean cloned = false;
		for (int ist : statestrace)
		{
			DState cstate = states.get(ist);
			if (cstate.parents != null && cstate.parents.length > 1)
			{
				// need to clone cstate
				{
					cloneState(ist);
					cloned = true;
					break;
				}
			}
		}
		return cloned;
	}

	/**
	 * 
	 * @return
	 */
	private int countStates()
	{
		if (this.FreeStates == null)
		{
			return this.states.size();
		}
		else
		{
			return (this.states.size() - this.FreeStates.size());
		}
		
	}

	/**
 * 
 */
	private void checkConsistency()
	{
		for (int istate = 0; istate < states.size(); istate++)
		{
			DState state = states.get(istate);
			if (state == null)
			{
				continue;
			}

			// check that each parent exists
			for (int iparent : state.parents)
			{
				DState pstate = states.get(iparent);
				if (pstate == null)
				{
					return;
				}
				// check that the parent has it as a child
				for (int ic : pstate.dests)
				{
					if (ic == istate)
					{
						break;
					}
				}
			}

			// check that each child has it as a parent
			if (state.dests != null)
			{
				for (int idest : state.dests)
				{
					DState child = states.get(idest);

					for (int ip : child.parents)
					{
						if (ip == istate)
						{
							break;
						}
					}
				}
			}
		}
	}

	transient private int counter;
	transient private boolean verbose = false;

	/**
	 * builds up the transducer incrementally by adding the new states needed to store the current word in a
	 * deterministic way (to insure speed) as well as minimized way (to insure that the number of states does not
	 * explode). The basic algorithm used is based on Daciuk's thesis
	 * 
	 * @param entry
	 * @param lemma
	 * @param infolemma
	 * @param infoflex
	 * @param dics0
	 * @param dics
	 * @param hDicInfos
	 * @param aDicInfos
	 * @param isacompound
	 */
	void addLexLineToDic(String entry, String lemma, String infolemma, String infoflex, FSDic dics0, FSDic dics,
			HashMap<String, Integer> hDicInfos, ArrayList<String> aDicInfos, boolean isacompound)
	{
		ParameterCheck.mandatoryString("entry", entry);
		ParameterCheck.mandatory("dics", dics);
		ParameterCheck.mandatory("hDicInfos", hDicInfos);
		ParameterCheck.mandatory("aDicInfos", aDicInfos);

		counter++;
		if (counter % 1000000 == 0)
		{
			Dic.writeLog(" > " + counter + " forms; " + countStates() + " states.");
		}
		if (he == null)
		{
			this.invalidateAllStates();
		}
		String newlemma;
		if (isacompound || this.Lan.asianTokenizer)
		{
			newlemma = Dic.compressCompoundLemma(entry, lemma);
		}
		else
		{
			newlemma = Dic.compressSimpleLemma(entry, lemma);
		}
		int i, istate;
		DState state; 

		char[] newchars;
		int[] newdests;

	
		while (true)
		{
			ArrayList<Integer> statestrace = new ArrayList<Integer>();
			boolean breakedFor = false;
			for (i = istate = 0; i < entry.length(); i++)
			{
				statestrace.add(istate);
				char c = entry.charAt(i);
				if (!dics.hc.containsKey(c))
				{
					dics.hc.put(c, dics.charnb++);
				}
				state = states.get(istate);
				int index = -1;
				if (state.chars != null)
				{
					for (int it = 0; it < state.chars.length; it++)
					{
						char character = state.chars[it];
						if (character == c)
						{
							index = it;
							break;
						}
					}
				}
				if (index != -1) // found destination => move forward
				{
					istate = state.dests[index];
				}
				else
				// a new state need to be created
				{
					// need to clone a state?
					if (cloneTrace(statestrace))
					{
						// there was goto RESTART here ... so break from this for loop and continue on while loop makes
						// us go back to the beginning of while loop which is the same as goto RESTART

						// original line goto RESTART
						breakedFor = true;
						break;
					}

					int dest = createState(istate);

					if (state.chars == null)
					{
						newchars = new char[1];
						newchars[0] = c;
						newdests = new int[1];
						newdests[0] = dest;
					}
					else
					{
						newchars = Arrays.copyOf(state.chars, state.chars.length + 1);
						newchars[state.chars.length] = c;
						newdests = Arrays.copyOf(state.dests, state.dests.length + 1);
						newdests[state.dests.length] = dest;
					}
					state.chars = newchars;
					state.dests = newdests;

					istate = dest;
				}
			}
			// when code comes to this point breakedFor either has value true which means that line goto RESTART was
			// reached. In that case for loop breaks and all left to do is continue on while loop so that code comes to
			// the RESTART label which is now while loop beginning

			// if breakedFOr has value false then goto line was never reached so for loop terminated successfully and
			// all that is left to be done is ... nothing ... go to the next statement
			if (breakedFor)
			{
				continue;
			}
			state = states.get(istate);
			statestrace.add(istate);

			// need to clone a state?
			if (cloneTrace(statestrace))
			{
				// original line was goto RESTART

				// so now code continues and comes to the RESTART point which is now while loops beginning
				continue;
			}
			// this point secures that the code exits while loop if no goto RESTART point was reached
			break;
		}

		String compactedinfo;
		if (newlemma == null) // ATTENTION: only two fields, eg: "du,<de,PREP><le,DET>"
		{
			compactedinfo = compactInfo(infolemma, infoflex, dics0); // dics0 could be SuperDico
		}
		else
		{
			compactedinfo = compactInfo(newlemma, infolemma, infoflex, dics0);
		}

		String infostring = null;
		if (state.infonb == 0)
		{
			infostring = compactedinfo;
		}
		else if (!compactedinfo.equals(infostring))
		{
			infostring = aDicInfos.get(state.infonb);
			if (compactedinfo.length() >= 8 && compactedinfo.substring(0, 8).equals("4 bytes:"))
			{
				if (infostring.length() >= 8 && infostring.substring(0, 8).equals("4 bytes:"))
				{
					// before 4 bytes ; add 4 bytes
					infostring = compactedinfo + "\0\0" + infostring.substring(8);
				}
				else
				{
					// before 2 bytes ; add 4 bytes
					String tmp = compactedinfo + "\0\0";
					for (int iinfo = 0; iinfo < infostring.length(); iinfo++)
					{
						tmp += '\0';
						tmp += infostring.charAt(iinfo);
					}
					infostring = tmp;
				}
			}
			else
			{
				if (infostring.length() >= 8 && infostring.substring(0, 8).equals("4 bytes:"))
				{
					// before 4 bytes; add 2 bytes
					String tmp = infostring + "\0\0";
					for (int iinfo = 0; iinfo < compactedinfo.length(); iinfo++)
					{
						tmp += '\0';
						tmp += compactedinfo.charAt(iinfo);
					}
					infostring = tmp;
				}
				else
				{
					// before 2 bytes; add 2 bytes;
					infostring = infostring + "\0" + compactedinfo;
				}
			}
		}
		if (hDicInfos.containsKey(infostring))
		{
			state.infonb = hDicInfos.get(infostring);
		}
		else
		{
			aDicInfos.add(infostring);
			state.infonb = aDicInfos.size() - 1;
			hDicInfos.put(infostring, state.infonb);
		}

		
		invalidateStates(istate);
		if (counter % 1000000 == 0)
		{
			this.minimize(aDicInfos.size());
		}

		
	}

	/**
	 * Writes info to buffer.
	 * 
	 * @param info
	 * @param buf
	 * @param bufindex
	 * @param sizelog
	 */
	private static void writeToBuf(int info, UnsignedByte[] buf, int bufindex, int sizelog)
	{
		ParameterCheck.mandatory("buf", buf);
		switch (sizelog)
		{
			case 1:
				buf[bufindex].setB((byte) (info % 256L));
				break;
			case 2:
				buf[bufindex].setB((byte) ((info / 256L) % 256L));
				buf[bufindex + 1].setB((byte) (info % 256L));
				break;
			case 3:
				buf[bufindex].setB((byte) ((info / 256L / 256L) % 256L));
				buf[bufindex + 1].setB((byte) ((info / 256L) % 256L));
				buf[bufindex + 2].setB((byte) (info % 256L));
				break;
			case 4:
				buf[bufindex].setB((byte) ((info / 256L / 256L / 256L) % 256L));
				buf[bufindex + 1].setB((byte) ((info / 256L / 256L) % 256L));
				buf[bufindex + 2].setB((byte) ((info / 256L) % 256L));
				buf[bufindex + 3].setB((byte) (info % 256L));
				break;
		}
	}

	/**
	 * Reads info from buffer
	 * 
	 * @param buf
	 * @param ib
	 * @param sizelog
	 * @return
	 */
	private static int readFromBuf(UnsignedByte[] buf, int ib, int sizelog)
	{
		ParameterCheck.mandatory("buf", buf);
		int info = 0;
		switch (sizelog)
		{
			case 1:
				info = buf[ib].getInt();
				break;
			case 2:
				info = 256 * buf[ib].getInt() + buf[ib + 1].getInt();
				break;
			case 3:
				info = 65536 * buf[ib].getInt() + 256 * buf[ib + 1].getInt() + buf[ib + 2].getInt();
				break;
			case 4:
				info = 65536 * 256 * buf[ib].getInt() + 65536 * buf[ib + 1].getInt() + 256 * buf[ib + 2].getInt()
						+ buf[ib + 3].getInt();
				break;
		}
		return info;
	}

	/**
	 * writes state to buffer
	 * 
	 * @param state
	 * @param buf
	 * @param bufindex
	 * @param alphabetlog
	 * @param infolog
	 * @param buflog
	 * @param dics
	 */
	private final void stateToBinary(DState state, UnsignedByte[] buf, int bufindex, int alphabetlog, int infolog,
			int buflog, FSDic dics)
	{
		ParameterCheck.mandatory("state", state);
		ParameterCheck.mandatory("dics", dics);
		int ib = bufindex;

		// information
		writeToBuf(state.infonb, buf, ib, infolog);
		
		ib += infolog;

		// nb of transitions
		if (state.chars == null)
		{
			writeToBuf(0, buf, ib, alphabetlog);
			ib += alphabetlog;
			return;
		}
		writeToBuf(state.chars.length, buf, ib, alphabetlog);

		ib += alphabetlog;

		for (int it = 0; it < state.dests.length; it++)
		{
			char character = state.chars[it];
			int charindex = dics.hc.get(character);
			writeToBuf(charindex, buf, ib, alphabetlog);
			ib += alphabetlog;

			// destination address
			int idest = state.dests[it];
		
			int destaddr = this.stateaddress[idest];
			writeToBuf(destaddr, buf, ib, buflog);
			ib += buflog;
		}
		state.chars = null;
		state.dests = null;
	}

	/**
	 * 
	 * @param try_alphabetlog
	 * @param try_infolog
	 * @return
	 */
	private int guessNeededSizeLog(int try_alphabetlog, int try_infolog)
	{
		int try_buflog = 2;
		int address = 0;
		boolean notenough = false;
		for (int istate = 0; istate < this.states.size(); istate++)
		{
			DState state = this.states.get(istate);
			if (state == null)
			{
				continue;
			}

			int size;
			if (state.chars == null)
			{
				size = try_infolog + try_alphabetlog + 0; // infonb + nb of transitions
			}
			else
			{
				size = try_infolog + try_alphabetlog + state.chars.length * try_alphabetlog + state.dests.length
						* try_buflog; // infonb + nb of transitions + characters + destinations
			}
			address += size;
			if (address >= 65536)
			{
				notenough = true;
				break;
			}
		}
		if (notenough)
		{
			try_buflog = 3;
			address = 0;
			notenough = false;
			for (int istate = 0; istate < states.size(); istate++)
			{
				DState state = this.states.get(istate);
				if (state == null)
				{
					continue;
				}

				int size;
				if (state.chars == null)
				{
					size = try_infolog + try_alphabetlog + 0; // infonb + nb of transitions
				}
				else
				{
					size = try_infolog + try_alphabetlog + state.chars.length * try_alphabetlog + state.dests.length
							* try_buflog; // infonb + nb of transitions + characters + destinations
				}
				address += size;
				if (address >= 65536 * 256)
				{
					notenough = true;
					break;
				}
			}
			if (notenough)
			{
				try_buflog = 4;
				address = 0;
				for (int istate = 0; istate < states.size(); istate++)
				{
					DState state = this.states.get(istate);
					if (state == null)
					{
						continue;
					}

					int size;
					if (state.chars == null)
					{
						size = try_infolog + try_alphabetlog + 0; // infonb + nb of transitions
					}
					else
					{
						size = try_infolog + try_alphabetlog + state.chars.length * try_alphabetlog
								+ state.dests.length * try_buflog; // infonb + nb of transitions + characters +
																	// destinations
					}
					address += size;
				}
			}
		}
		return try_buflog;
	}

	/**
	 * 
	 * @param d_alphabetlog
	 * @param d_infolog
	 * @param d_buflog
	 * @return
	 */
	private int computeNeededSize(int d_alphabetlog, int d_infolog, int d_buflog)
	{
		this.stateaddress = new int[this.states.size()];
		int address = 0;
		for (int istate = 0; istate < this.states.size(); istate++)
		{
			DState state = this.states.get(istate);
			if (state == null)
			{
				continue;
			}
			this.stateaddress[istate] = address;

			int size;
			if (state.chars == null)
			{
				size = d_infolog + d_alphabetlog + 0; // infonb + nb of transitions
			}
			else
			{
				size = d_infolog + d_alphabetlog + state.chars.length * d_alphabetlog + state.dests.length * d_buflog; // infonb
																														// +
																														// nb
																														// of
																														// transitions
																														// +
																														// characters
																														// +
																														// destinations
			}
			address += size;
		}
		return address;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	private static int getLargest(int a, int b, int c, int d)
	{
		int res = a;
		if (b > res)
		{
			res = b;
		}
		if (c > res)
		{
			res = c;
		}
		if (d > res)
		{
			res = d;
		}
		return res;
	}

	/**
	 * guesses needed size log for every FSDic and the largest computed value sets as dics argument buflog
	 * 
	 * @param dics
	 * @param dicc
	 * @param dicsLemma
	 * @param diccLemma
	 */
	public static void computeAllLogs(FSDic dics, FSDic dicc, FSDic dicsLemma, FSDic diccLemma)
	{
		ParameterCheck.mandatory("dics", dics);
		ParameterCheck.mandatory("dicc", dicc);
		ParameterCheck.mandatory("dicsLemma", dicsLemma);
		ParameterCheck.mandatory("diccLemma", diccLemma);
		// compute the nb of bytes to store characters and infos
		if (dics.charnb <= UnsignedByte.MAX_VALUE)
		{
			dics.alphabetlog = 1;
		}
		else if (dics.charnb <= UnsignedShort.MAX_VALUE)
		{
			dics.alphabetlog = 2;
		}
		else
		{
			dics.alphabetlog = 4;
		}

		if ((dics.infos.length) <= UnsignedByte.MAX_VALUE)
		{
			dics.infolog = 1;
		}
		else if ((dics.infos.length) <= UnsignedShort.MAX_VALUE)
		{
			dics.infolog = 2;
		}
		else
		{
			dics.infolog = 4;
		}

		// compute the address of each canonical state and the total amount of memory needed
		int buflog_dics = dics.guessNeededSizeLog(dics.alphabetlog, dics.infolog);
		int buflog_dicc = dicc.guessNeededSizeLog(dics.alphabetlog, dics.infolog);
		int buflog_dicsLemma = dicsLemma.guessNeededSizeLog(dics.alphabetlog, dics.infolog);
		int buflog_diccLemma = diccLemma.guessNeededSizeLog(dics.alphabetlog, dics.infolog);
		dics.buflog = getLargest(buflog_dics, buflog_dicc, buflog_dicsLemma, buflog_diccLemma);
	}

	/**
	 * transfer all characters (hc) in the dic's alphabet and all info bits (hi) in the dic's infobitstable
	 */
	public void toBinaryAlphabetInfobitstable()
	{
		// transfer all characters (hc) in the dic's alphabet
		this.alphabet = new char[charnb];
		for (Character cc : hc.keySet())
		{
			this.alphabet[hc.get(cc)] = cc;
		}
		hc = null;

		// transfer all info bits (hi) in the dic's infobitstable
		this.infobitstable = new String[hi.size() + 1];
		for (String s : hi.keySet())
		{
			int c = hi.get(s); // c encodes string s
			this.infobitstable[c] = s;
		}
	}

	/**
	 * creates binary representation of dics object
	 * 
	 * @param dics
	 * @return
	 */
	public UnsignedByte[] toBinary(FSDic dics)
	{
		ParameterCheck.mandatory("dics", dics);
		// transfer each canonical state's buf to dics.bufferl
		int address = this.computeNeededSize(dics.alphabetlog, dics.infolog, dics.buflog);
	    
		
		 UnsignedByte[]buf = new UnsignedByte[address];		
	 
		 	try
		 	{
		       for (int i = 0; i < address; i++)
		
				  buf[i] = new UnsignedByte();
				
			 
		
		
		int iad = 0;
		for (int istate = 0; istate < this.states.size(); istate++)
		{
			DState state = this.states.get(istate);
			if (state == null)
			{
				continue;
			}
			int size;
			if (state.chars == null)
			{
				size = dics.infolog + dics.alphabetlog + 0; // infonb + nb of transitions
			}
			else
			{
				size = dics.infolog + dics.alphabetlog + state.chars.length * dics.alphabetlog + state.dests.length
						* dics.buflog; // infonb + nb of transitions + characters + destinations
			}
			stateToBinary(state, buf, iad, dics.alphabetlog, dics.infolog, dics.buflog, dics);
			iad += size;
		}
		this.stateaddress = null;

		return buf;
			}
		catch (OutOfMemoryError E) {
	    	JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					"Compilation Stopped.. Insufficient Memory!!!", "NooJ",
					JOptionPane.ERROR_MESSAGE);
	    	
	    	return null;
	    	}
	}

	/**
	 * deserializes previously serialized state of FSDic object
	 * 
	 * @param fullname
	 * @param engine
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	static FSDic[] load(String fullname, Engine engine, RefObject<String> errmessage) throws IOException,
			ClassNotFoundException
	{
		ParameterCheck.mandatoryString("fullname", fullname);
		ParameterCheck.mandatory("engine", engine);
		ParameterCheck.mandatory("errmessage", errmessage);

		FSDic dic = null;
		errmessage.argvalue = null;

		try
		{
			
			FileInputStream fs = new FileInputStream(fullname);
			ObjectInputStream serializer = new ObjectInputStream(fs);
			dic = (FSDic) serializer.readObject();

			if (serializer != null)
				serializer.close();
			if (fs != null)
				fs.close();
		}
		catch (RuntimeException ex)
		{
			errmessage.argvalue = "Error: cannot load binary dictionary " + fullname + "\r" + ex.getMessage();
			Dic.writeLog(errmessage.argvalue);
			return null;
		}

		String lname = (String) dic.Params.get("LanguageName");
		if (engine.Lan.isoName.equals(lname))
		{
			dic.Lan = engine.Lan;
		}
		else
		{
			dic.Lan = new Language(lname);
		}
		if (dic.Lan == null)
		{
			return null;
		}

		dic.Author = (String) dic.Params.get("Author");
		dic.Institution = (String) dic.Params.get("Institution");
		dic.Protection = (Integer) dic.Params.get("Protection");
		if (dic.Params.containsKey("Paradigms"))
		{
			// Unchecked cast cannot be avoided here; dic.Params contains of different data types (Strings, Integers,
			// HashMaps, ArrayLists, String arrays - String[]...)
			dic.paradigms = (HashMap<String, Gram>) dic.Params.get("Paradigms");
		}

		if (dic.Params.get("Infos").getClass().getName().equals("ArrayList"))
		{
			// Unchecked cast cannot be avoided here; dic.Params contains of different data types (Strings, Integers,
			// HashMaps, ArrayLists, String arrays - String[]...)
			ArrayList<String> infos = (ArrayList<String>) dic.Params.get("Infos");

			dic.infos = infos.toArray(new String[infos.size()]);
		}
		else
		{
			dic.infos = (String[]) dic.Params.get("Infos");
		}
		dic.infobitstable = (String[]) dic.Params.get("Infobitstable");
		dic.infolog = (Integer) dic.Params.get("Infolog");

		dic.alphabet = (char[]) dic.Params.get("Alphabet");
		dic.alphabetlog = (Integer) dic.Params.get("Alphabetlog");

		// a regular dictionary
		dic.buffer = (UnsignedByte[]) dic.Params.get("Buffer");
		dic.bufferc = (UnsignedByte[]) dic.Params.get("Bufferc");
		if (dic.Params.containsKey("BufferL"))
		{
			dic.bufferl = (UnsignedByte[]) dic.Params.get("BufferL");
		}
		if (dic.Params.containsKey("BuffercL"))
		{
			dic.buffercl = (UnsignedByte[]) dic.Params.get("BuffercL");
		}

		if (dic.Params.containsKey("Properties"))
		{
			
		}

		if (dic.Params.containsKey("Buflog"))
		{
			dic.buflogc = dic.buflog = (Integer) dic.Params.get("Buflog");
		}
		else
		{
			if (!dic.Params.containsKey("version"))
			{
				dic.buflog = dic.buflogc = 3;
			}
			else
			{
				// compatibility with NooJ v1.x
				if (dic.buffer.length < 65536)
				{
					dic.buflog = 2;
				}
				else if (dic.buffer.length < 65536 * 256)
				{
					dic.buflog = 3;
				}
				else
				{
					dic.buflog = 4;
				}

				if (dic.bufferc != null)
				{
					if (dic.bufferc.length < 65536)
					{
						dic.buflogc = 2;
					}
					else if (dic.bufferc.length < 65536 * 256)
					{
						dic.buflogc = 3;
					}
					else
					{
						dic.buflogc = 4;
					}
				}
			}
		}

		FSDic[] dics = new FSDic[1];
		dics[0] = dic;
		return dics;
	}

	/**
	 * serializes state of FSDic object to given file
	 * 
	 * @param fname
	 * @throws IOException
	 */
	final void save(String fname) throws IOException
	{		

		ParameterCheck.mandatoryString("fname", fname);
		// store everything in Params
		Params = new HashMap<String, Object>(17);

		Params.put("LanguageName", this.Lan.isoName);
		
		Params.put("Author", Author);
		Params.put("Institution", Institution);
		Params.put("Protection", Protection);
		Params.put("Paradigms", paradigms);

		Params.put("Alphabet", alphabet);
		Params.put("Alphabetlog", alphabetlog);
		Params.put("Infos", infos);
		Params.put("Infolog", infolog);
		Params.put("Infobitstable", infobitstable);

	
		Params.put("Buffer", this.buffer);
		Params.put("Bufferc", this.bufferc);
		Params.put("BufferL", this.bufferl);
		Params.put("BuffercL", this.buffercl);
		Params.put("Buflog", this.buflog);
		Params.put("version", 2.00F);
		
		FileOutputStream fs = new FileOutputStream(fname);
		ObjectOutputStream serializer = new ObjectOutputStream(fs);
		
		serializer.writeObject(this);

		if (serializer != null)
			serializer.close();
		
			
		
	}

	transient String Comments = null;

	/**
	 * saves comments to file fullname
	 * 
	 * @param fullname
	 * @param nbofinflectedforms
	 * @param dics_nbofstates
	 * @param dicc_nbofstates
	 * @param dicsl_nbofstates
	 * @param diccl_nbofstates
	 * @throws FileNotFoundException
	 */
	final void saveComments(String fullname, int nbofinflectedforms, int dics_nbofstates, int dicc_nbofstates,
			int dicsl_nbofstates, int diccl_nbofstates) throws FileNotFoundException
	{
		ParameterCheck.mandatoryString("fullname", fullname);
		PrintWriter sw = null;
		try
		{
			sw = new PrintWriter(fullname);

			sw.println("NooJ v4.x Compiled Dictionary");
			sw.println("recognizes " + nbofinflectedforms + " word forms.");

			if (dics_nbofstates > dicsl_nbofstates)
			{
				sw.print((new Integer(dics_nbofstates)).toString() + "/");
			}
			else
			{
				sw.print((new Integer(dicsl_nbofstates)).toString() + "/");
			}

			if (dicc_nbofstates > diccl_nbofstates)
			{
				sw.print(dicc_nbofstates);
			}
			else
			{
				sw.print(diccl_nbofstates);
			}
			sw.println(" states, " + this.infos.length + " different analyses.");

			sw.println();
			if (Comments != null)
			{
				sw.print(Comments);
			}
		}
		finally
		{
			if (sw != null)
				sw.close();
		}
	}

	/**
	 * 
	 * @param buf
	 * @param stateadr
	 * @param infolog
	 * @param alphabetlog
	 * @param buflog
	 * @param it
	 * @param charindex
	 * @param dest
	 */
	private void readTrans(UnsignedByte[] buf, int stateadr, int infolog, int alphabetlog, int buflog, int it,
			RefObject<Integer> charindex, RefObject<Integer> dest)
	{
		ParameterCheck.mandatory("buf", buf);
		ParameterCheck.mandatory("charindex", charindex);
		ParameterCheck.mandatory("dest", dest);
		charindex.argvalue = readFromBuf(buf, stateadr + infolog + alphabetlog + it * (buflog + alphabetlog),
				alphabetlog);
		dest.argvalue = readFromBuf(buf, stateadr + infolog + alphabetlog + it * (buflog + alphabetlog) + alphabetlog,
				buflog);
	}

	/**
	 * 
	 * @author milos
	 * 
	 */
	private class Trace
	{
		public int txtpos;
		public int stateadr;
		public String trace;

		public Trace()
		{
			txtpos = 0;
			stateadr = 0;
			trace = "";
		}
	}

	/**
	 * 
	 * @param text
	 * @param position
	 * @param buffer
	 * @param engine
	 * @return
	 */
	private ArrayList<String> lookUpSimple0(String text, int position, UnsignedByte[] buffer, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("buffer", buffer);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> info = null;
		int stateadr = 0;
		int infonb;
		int transnb, charindex = 0, dest = 0;

		Stack<Trace> stack = new Stack<Trace>();
		stack.push(new Trace());

		while (stack.size() > 0)
		{
			Trace curtrc = stack.pop();

			stateadr = curtrc.stateadr;
			int ipos = curtrc.txtpos;

			infonb = readFromBuf(buffer, stateadr, infolog);
			if (infonb > infos.length)
			{
				System.out.println("**********");
				System.out.println("Corruption in simple .jnod file: I read infonb = " + infonb);
				System.out.println("In state " + stateadr + " infolog = " + infolog);
				System.out.println("but infos.Length = " + infos.length);
				return null;
			}
			if (infonb > 0 && (position + ipos == text.length() || !Language.isLetter(text.charAt(position + ipos)))) // end
																														// of
																														// simple
																														// word
																														// -
																														// end
																														// of
																														// text
			{
				ArrayList<String> infs = unCompactInfo(infos[infonb]);
				if (info == null)
				{
					info = new ArrayList<String>();
				}
				for (int i = 0; i < infs.size(); i++)
				{
					String diclemma = null, category = null, dicinfo = null;
					String[] features = null;
					String lexinfo = infs.get(i);
					String trace;

					trace = curtrc.trace;
					if (lexinfo.charAt(0) == ',' && lexinfo.charAt(1) == '<')
					{
						// this is a special entry, e.g. I'm,<I,PRO><am,be,V+PR+1+s>
						info.add(trace + "," + trace + lexinfo);
						continue;
					}
					RefObject<String> tempRef_diclemma = new RefObject<String>(diclemma);
					RefObject<String> tempRef_dicinfo = new RefObject<String>(dicinfo);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					Dic.parseFactorizedInfo(lexinfo, tempRef_diclemma, tempRef_dicinfo, tempRef_category,
							tempRef_features);
					diclemma = tempRef_diclemma.argvalue;
					dicinfo = tempRef_dicinfo.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					ArrayList<String> dicinfos = Dic.normalizeInformation(category, features, engine.properties);
					if (dicinfos == null)
					{
						if (Lan.isACompound(trace) || Lan.asianTokenizer)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo);
						}
						else
						{
							info.add(trace + "," + Dic.unCompressSimpleLemma(trace, diclemma) + "," + dicinfo);
						}
					}
					else if (diclemma != null)
					{
						for (String dicinfo0 : dicinfos)
						{
							if (Lan.isACompound(trace) || Lan.asianTokenizer)
							{
								info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo0);
							}
							else
							{
								info.add(trace + "," + Dic.unCompressSimpleLemma(trace, diclemma) + "," + dicinfo0);
							}
						}
					}
					else
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + dicinfo0);
						}
					}
				}
			}

			if (text == null || position + ipos > text.length())
			{
				continue;
			}

			transnb = readFromBuf(buffer, stateadr + infolog, alphabetlog);
			for (int it = 0; it < transnb; it++)
			{
				RefObject<Integer> tempRef_charindex = new RefObject<Integer>(charindex);
				RefObject<Integer> tempRef_dest = new RefObject<Integer>(dest);
				readTrans(buffer, stateadr, infolog, alphabetlog, buflog, it, tempRef_charindex, tempRef_dest);
				charindex = tempRef_charindex.argvalue;
				dest = tempRef_dest.argvalue;
				if (charindex > alphabet.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in simple .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (dest > buffer.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in simple .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (position + ipos < text.length())
				{
					if (Lan.doLettersMatch(text.charAt(position + ipos), alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos + 1;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
					else if (Lan.isoName.equals("ru") && alphabet[charindex] == '\u0341')
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
				}
			}
		}
		return info;
	}

	/**
	 * 
	 * @param results
	 * @param aresult
	 */
	private void addNoDuplicate(RefObject<ArrayList<String>> results, String aresult)
	{
		ParameterCheck.mandatory("results", results);
		ParameterCheck.mandatory("aresult", aresult);
		if (results.argvalue == null)
		{
			results.argvalue = new ArrayList<String>();
		}
		for (String res : results.argvalue)
		{
			if (aresult.equals(res))
			{
				return;
			}
		}
		results.argvalue.add(aresult);
	}

	/**
	 * 
	 * @param results
	 * @param aresult
	 * @return
	 */
	private boolean alreadyThereInMultiwordResults(ArrayList<String> results, String aresult)
	{
		ParameterCheck.mandatory("results", results);
		ParameterCheck.mandatory("aresult", aresult);
		if (results == null)
		{
			results = new ArrayList<String>();
		}
		for (int i = 0; i < results.size(); i += 2)
		{
			String res = results.get(i);
			if (aresult.equals(res))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * nooj starts at the initial state of the transducer, reads each letter in the word and goes to the corresponding
	 * state in the transduced. When the word is read, NooJ sees if the current state is terminal; if so, the word is
	 * recognized and the transducer produces its output (i.e. the information). If not the word is not recognized.
	 * 
	 * @param text
	 * @param position
	 * @param engine
	 * @return
	 */
	ArrayList<String> lookUpSimple(String text, int position, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> res0 = lookUpSimple0(text, position, buffer, engine);
		if (this.bufferl == null)
		{
			return res0;
		}
		if (res0 == null)
		{
			return null;
		}

		ArrayList<String> res = new ArrayList<String>();
		for (String res0i : res0)
		{
			String entry = null, lemma = null, category = null, info = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			boolean tempVar = !Dic.parseDELAF(res0i, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_info);
			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			info = tempRef_info.argvalue;
			if (tempVar)
			{
				// could be a contracted entry such as du,<de,PREP><le,DET>
				RefObject<ArrayList<String>> tempRef_res = new RefObject<ArrayList<String>>(res);
				addNoDuplicate(tempRef_res, res0i);
				res = tempRef_res.argvalue;
				continue;
			}
			// process inflection
			String myfeatureflx = Dic.lookFor("FLX", info);
			String[] myfeaturesdrv = Dic.lookForAll("DRV", info);

			if (myfeatureflx == null && myfeaturesdrv == null)
			{
				// the word form was entered as is, without any FLX nor DRV command
				RefObject<ArrayList<String>> tempRef_res2 = new RefObject<ArrayList<String>>(res);
				addNoDuplicate(tempRef_res2, res0i);
				res = tempRef_res2.argvalue;
				continue;
			}
			else
			{
				// the word form was inflected: add the info of each potential lemma to the word form's info
				ArrayList<String> infolemma = lookUpSimple0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					// get the info from the simple-word lemma
					for (String lex : infolemma)
					{
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo = new RefObject<String>(lexinfo);
						boolean tempVar2 = !Dic.parseDELAF(lex, tempRef_lexentry, tempRef_lexlemma,
								tempRef_lexcategory, tempRef_lexinfo);
						lexentry = tempRef_lexentry.argvalue;
						lexlemma = tempRef_lexlemma.argvalue;
						lexcategory = tempRef_lexcategory.argvalue;
						lexinfo = tempRef_lexinfo.argvalue;
						if (tempVar2)
						{
							continue;
						}
						if (category.equals(lexcategory))
						{
							String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
							if ((myfeatureflx == null && lexfeatureflx == null)
									|| (myfeatureflx != null && myfeatureflx.equals(lexfeatureflx)))
							{
								RefObject<ArrayList<String>> tempRef_res3 = new RefObject<ArrayList<String>>(res);
								addNoDuplicate(tempRef_res3, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
								res = tempRef_res3.argvalue;
							}
						}
					}
				}
				else
				{
					// no info in the simple-word dictionary: get the info from the multi-word lemma
					infolemma = lookUpCompound0(lemma, 0, buffercl, engine);
					// get the info from the lemma
					if (infolemma != null)
					{
						for (int ilex = 0; ilex < infolemma.size(); ilex += 2) // need to skip lengths stored in
																				// multiword units lookup results
						{
							String lex = infolemma.get(ilex);

							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry2 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma2 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory2 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo2 = new RefObject<String>(lexinfo);
							boolean tempVar3 = !Dic.parseDELAF(lex, tempRef_lexentry2, tempRef_lexlemma2,
									tempRef_lexcategory2, tempRef_lexinfo2);
							lexentry = tempRef_lexentry2.argvalue;
							lexlemma = tempRef_lexlemma2.argvalue;
							lexcategory = tempRef_lexcategory2.argvalue;
							lexinfo = tempRef_lexinfo2.argvalue;
							if (tempVar3)
							{
								continue;
							}
							if (category.equals(lexcategory))
							{
								String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
								if (myfeatureflx.equals(lexfeatureflx))
								{
									RefObject<ArrayList<String>> tempRef_res4 = new RefObject<ArrayList<String>>(res);
									addNoDuplicate(tempRef_res4,
											res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
									res = tempRef_res4.argvalue;
								}
							}
						}
					}
					else
					{
						// the word form was not inflected from its lemma: it is a variant, e.g.
						// czar,tsar,N+Hum+FLX=TABLE
						RefObject<ArrayList<String>> tempRef_res5 = new RefObject<ArrayList<String>>(res);
						addNoDuplicate(tempRef_res5, res0i);
						res = tempRef_res5.argvalue;
					}
				}
			}

			// process derivation
			if (myfeaturesdrv != null && myfeaturesdrv.length > 0)
			{
				// the word form was generated from a DRV command: add the info of each potential lemma with the form's
				// info
				// get the info from a simple-word dictionary
				ArrayList<String> infolemma = lookUpSimple0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					for (String lex : infolemma)
					{
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry3 = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma3 = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory3 = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo3 = new RefObject<String>(lexinfo);
						boolean tempVar4 = !Dic.parseDELAF(lex, tempRef_lexentry3, tempRef_lexlemma3,
								tempRef_lexcategory3, tempRef_lexinfo3);
						lexentry = tempRef_lexentry3.argvalue;
						lexlemma = tempRef_lexlemma3.argvalue;
						lexcategory = tempRef_lexcategory3.argvalue;
						lexinfo = tempRef_lexinfo3.argvalue;
						if (tempVar4)
						{
							continue;
						}
						String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
						if (lexfeaturesdrv == null) // the form does not come from this lexical entry
						{
							continue;
						}
						boolean founddrv = false;
						for (String lexfeaturedrv : lexfeaturesdrv)
						{
							if (myfeaturesdrv[0].equals(lexfeaturedrv))
							{
								founddrv = true;
								break; // no need to continue to search
							}
						}
						if (founddrv)
						{
							RefObject<ArrayList<String>> tempRef_res6 = new RefObject<ArrayList<String>>(res);
							addNoDuplicate(tempRef_res6, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
							res = tempRef_res6.argvalue;
						}

					}
				}
				else
				{
					// no info in the simple-word dictionary: get the info from the multi-word lemma
					infolemma = lookUpCompound0(lemma, 0, buffercl, engine);
					if (infolemma != null)
					{
						for (int ilex = 0; ilex < infolemma.size(); ilex += 2) // need to skip lengths stored in
																				// multiword units lookup results
						{
							String lex = infolemma.get(ilex);

							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry4 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma4 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory4 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo4 = new RefObject<String>(lexinfo);
							boolean tempVar5 = !Dic.parseDELAF(lex, tempRef_lexentry4, tempRef_lexlemma4,
									tempRef_lexcategory4, tempRef_lexinfo4);
							lexentry = tempRef_lexentry4.argvalue;
							lexlemma = tempRef_lexlemma4.argvalue;
							lexcategory = tempRef_lexcategory4.argvalue;
							lexinfo = tempRef_lexinfo4.argvalue;
							if (tempVar5)
							{
								continue;
							}
							String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
							if (lexfeaturesdrv == null) // the form does not come from this lexical entry
							{
								continue;
							}
							boolean founddrv = false;
							for (String lexfeaturedrv : lexfeaturesdrv)
							{
								if (myfeaturesdrv[0].equals(lexfeaturedrv))
								{
									founddrv = true;
									break; // no need to continue to search
								}
							}
							if (founddrv)
							{
								RefObject<ArrayList<String>> tempRef_res7 = new RefObject<ArrayList<String>>(res);
								addNoDuplicate(tempRef_res7, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
								res = tempRef_res7.argvalue;
							}

						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param text
	 * @param position
	 * @param bufferc
	 * @param engine
	 * @return
	 */
	private ArrayList<String> lookUpCompound0(String text, int position, UnsignedByte[] bufferc, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("bufferc", bufferc);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> info = null;
		int stateadr = 0;
		int infonb;
		int transnb, charindex = 0, dest = 0;

		Stack<Trace> stack = new Stack<Trace>();
		stack.push(new Trace());

		while (stack.size() > 0)
		{
			Trace curtrc = stack.pop();

			stateadr = curtrc.stateadr;
			int ipos = curtrc.txtpos;

			infonb = readFromBuf(bufferc, stateadr, infolog);
			if (infonb > infos.length)
			{
				System.out.println("**********");
				System.out.println("Corruption in compound .jnod file: I read infonb = " + infonb);
				System.out.println("In state " + stateadr + " infolog = " + infolog);
				System.out.println("but infos.Length = " + infos.length);
				return null;
			}
			if (stateadr > 0
					&& (infonb > 0 && (position + ipos == text.length()
							|| !Language.isLetter(text.charAt(position + ipos)) || this.Lan.asianTokenizer || (ipos > 0 && !Language
							.isLetter(text.charAt(position + ipos - 1))))))
			// end of a compound that ends with a delimiter - Asian tokenizer - end of simple word - end of text - TODO:
			// it looks like there is a bug for compound dictionaries' compilation: state 0 is terminal!!!
			{
				ArrayList<String> infs = unCompactInfo(infos[infonb]);
				if (info == null)
				{
					info = new ArrayList<String>();
				}
				for (int i = 0; i < infs.size(); i++)
				{
					String diclemma = null, category = null, dicinfo = null;
					String lexinfo = infs.get(i);
					String[] features = null;

					String trace = curtrc.trace; // $0
					if (lexinfo.charAt(0) == ',' && lexinfo.charAt(1) == '<')
					{
						// this is a special entry, e.g. I'm,<I,PRO><am,be,V+PR+1+s>
						info.add(trace + "," + trace + lexinfo);
						info.add(Integer.toString(ipos));
						continue;
					}

					RefObject<String> tempRef_diclemma = new RefObject<String>(diclemma);
					RefObject<String> tempRef_dicinfo = new RefObject<String>(dicinfo);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					Dic.parseFactorizedInfo(lexinfo, tempRef_diclemma, tempRef_dicinfo, tempRef_category,
							tempRef_features);
					diclemma = tempRef_diclemma.argvalue;
					dicinfo = tempRef_dicinfo.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					ArrayList<String> dicinfos = Dic.normalizeInformation(category, features, engine.properties);
					if (dicinfos == null)
					{
						if (diclemma != null)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo);
						}
						else
						{
							info.add(trace + "," + dicinfo);
						}
						info.add(Integer.toString(ipos));
					}
					else if (diclemma != null)
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo0);
							info.add(Integer.toString(ipos));
						}
					}
					else
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + dicinfo0);
							info.add(Integer.toString(ipos));
						}
					}
				}
			}

			if (text == null || position + ipos > text.length())
			{
				continue;
			}

			transnb = readFromBuf(bufferc, stateadr + infolog, alphabetlog);
			for (int it = 0; it < transnb; it++)
			{
				RefObject<Integer> tempRef_charindex = new RefObject<Integer>(charindex);
				RefObject<Integer> tempRef_dest = new RefObject<Integer>(dest);
				readTrans(bufferc, stateadr, infolog, alphabetlog, buflogc, it, tempRef_charindex, tempRef_dest);
				charindex = tempRef_charindex.argvalue;
				dest = tempRef_dest.argvalue;
				if (charindex > alphabet.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in compound .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (dest > bufferc.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in compound .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (position + ipos < text.length())
				{
					if (alphabet[charindex] == ' ')
					{
						boolean amatch = false;
						while (position + ipos < text.length())
						{
							if (text.charAt(position + ipos) == '<')
							{
								int i2 = 0;
								while (position + ipos + i2 < text.length() && text.charAt(position + ipos + i2) != '>')
								{
									i2++;
								}
								if (position + ipos + i2 == text.length()) // a '<' that does not follow with '>'
								{
									break;
								}
								ipos += i2 + 1;
								amatch = true;
							}
							else if (Character.isWhitespace(text.charAt(position + ipos)))
							{
								amatch = true;
								while (position + ipos < text.length()
										&& Character.isWhitespace(text.charAt(position + ipos)))
								{
									ipos++;
								}
							}
							else
							{
								break;
							}
						}
						if (amatch)
						{
							Trace newtrc = new Trace();
							newtrc.stateadr = dest;
							newtrc.txtpos = ipos;
							newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
							stack.push(newtrc);
						}
					}
					else if (Lan.doLettersMatch(text.charAt(position + ipos), alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos + 1;
						if (alphabet[charindex] == ',')
						{
							newtrc.trace = curtrc.trace + "\\,";
						}
						else
						{
							newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						}
						stack.push(newtrc);
					}
				}
			}
		}
		return info;
	}

	/**
	 * 
	 * @param text
	 * @param position
	 * @param bufferc
	 * @param engine
	 * @return
	 */
	private ArrayList<String> lookUpCompoundSemitic0(String text, int position, UnsignedByte[] bufferc, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("bufferc", bufferc);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> info = null;
		int stateadr = 0;
		int infonb;
		int transnb, charindex = 0, dest = 0;

		Stack<Trace> stack = new Stack<Trace>();
		stack.push(new Trace());

		while (stack.size() > 0)
		{
			Trace curtrc = stack.pop();

			stateadr = curtrc.stateadr;
			int ipos = curtrc.txtpos;

			infonb = readFromBuf(bufferc, stateadr, infolog);
			if (infonb > infos.length)
			{
				System.out.println("**********");
				System.out.println("Corruption in compound .jnod file: I read infonb = " + infonb);
				System.out.println("In state " + stateadr + " infolog = " + infolog);
				System.out.println("but infos.Length = " + infos.length);
				return null;
			}
			if (stateadr > 0
					&& (infonb > 0 && (position + ipos == text.length()
							|| !Language.isLetter(text.charAt(position + ipos)) || this.Lan.asianTokenizer || (ipos > 0 && !Language
							.isLetter(text.charAt(position + ipos - 1))))))
			// end of a compound that ends with a delimiter - Asian tokenizer - end of simple word - end of text - TODO:
			// it looks like there is a bug for compound dictionaries' compilation: state 0 is terminal!!!
			{
				ArrayList<String> infs = unCompactInfo(infos[infonb]);
				if (info == null)
				{
					info = new ArrayList<String>();
				}
				for (int i = 0; i < infs.size(); i++)
				{
					String diclemma = null, dicinfo = null, category = null;
					String lexinfo = infs.get(i);
					String[] features = null;

					String trace = curtrc.trace; // $0

					if (lexinfo.charAt(0) == ',' && lexinfo.charAt(1) == '<')
					{
						// this is a special entry, e.g. I'm,<I,PRO><am,be,V+PR+1+s>
						info.add(trace + "," + trace + lexinfo);
						info.add(Integer.toString(ipos));
						continue;
					}
					RefObject<String> tempRef_diclemma = new RefObject<String>(diclemma);
					RefObject<String> tempRef_dicinfo = new RefObject<String>(dicinfo);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					Dic.parseFactorizedInfo(lexinfo, tempRef_diclemma, tempRef_dicinfo, tempRef_category,
							tempRef_features);
					diclemma = tempRef_diclemma.argvalue;
					dicinfo = tempRef_dicinfo.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					ArrayList<String> dicinfos = Dic.normalizeInformation(category, features, engine.properties);
					if (dicinfos == null)
					{
						if (diclemma != null)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo);
						}
						else
						{
							info.add(trace + "," + dicinfo);
						}
						info.add(Integer.toString(ipos));
					}
					else if (diclemma != null)
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo0);
							info.add(Integer.toString(ipos));
						}
					}
					else
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + dicinfo0);
							info.add(Integer.toString(ipos));
						}
					}
				}
			}

			if (text == null || position + ipos > text.length())
			{
				continue;
			}
			if (position + ipos < text.length() && text.charAt(position + ipos) == '\u0640') // kashida
			{
				Trace newtrc = new Trace();
				newtrc.stateadr = stateadr;
				newtrc.txtpos = ipos + 1;
				newtrc.trace = curtrc.trace; // +'\x640';
				stack.push(newtrc);
				continue;
			}

			transnb = readFromBuf(bufferc, stateadr + infolog, alphabetlog);
			for (int it = 0; it < transnb; it++)
			{
				RefObject<Integer> tempRef_charindex = new RefObject<Integer>(charindex);
				RefObject<Integer> tempRef_dest = new RefObject<Integer>(dest);
				readTrans(bufferc, stateadr, infolog, alphabetlog, buflogc, it, tempRef_charindex, tempRef_dest);
				charindex = tempRef_charindex.argvalue;
				dest = tempRef_dest.argvalue;
				if (charindex > alphabet.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in compound .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (dest > bufferc.length)
				{
					System.out.println("*********");
					System.out.println("Corruption Problem in compound .jnod dictionary at state: " + stateadr);
					System.out.println("infolog = " + infolog + ", alphabetlog = " + alphabetlog);
					System.out.println("charindex = " + charindex + ", dest = " + dest);
					System.out.println("Nb of transitions = " + transnb);
					System.out.println("alphabet.Length = " + alphabet.length);
					return null;
				}
				if (position + ipos < text.length())
				{
					if (alphabet[charindex] == ' ')
					{
						boolean amatch = false;
						while (position + ipos < text.length())
						{
							if (text.charAt(position + ipos) == '<')
							{
								int i2 = 0;
								while (position + ipos + i2 < text.length() && text.charAt(position + ipos + i2) != '>')
								{
									i2++;
								}
								if (position + ipos + i2 == text.length()) // a '<' that does not follow with '>'
								{
									break;
								}
								ipos += i2 + 1;
								amatch = true;
							}
							if (Character.isWhitespace(text.charAt(position + ipos)))
							{
								amatch = true;
								while (position + ipos < text.length()
										&& Character.isWhitespace(text.charAt(position + ipos)))
								{
									ipos++;
								}
							}
							else
							{
								break;
							}
						}
						if (amatch)
						{
							Trace newtrc = new Trace();
							newtrc.stateadr = dest;
							newtrc.txtpos = ipos;
							newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
							stack.push(newtrc);
						}
					}
					else if (Lan.doLettersMatch(text.charAt(position + ipos), alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos + 1;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
					else if (Language.isVowel(alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
				}
				else if (Language.isVowel(alphabet[charindex]))
				{
					Trace newtrc = new Trace();
					newtrc.stateadr = dest;
					newtrc.txtpos = ipos;
					newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
					stack.push(newtrc);
				}
			}
		}
		return info;
	}

	/**
	 * nooj starts at the initial state of the transducer, reads each letter in the word and goes to the corresponding
	 * state in the transduced. When the word is read, NooJ sees if the current state is terminal; if so, the word is
	 * recognized and the transducer produces its output (i.e. the information). If not the word is not recognized.
	 * 
	 * @param text
	 * @param position
	 * @param engine
	 * @return
	 */
	ArrayList<String> lookUpCompound(String text, int position, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> res0 = lookUpCompound0(text, position, bufferc, engine);
		if (this.buffercl == null)
		{
			return res0;
		}
		if (res0 == null)
		{
			return null;
		}
		ArrayList<String> res = new ArrayList<String>();
		for (int ires0 = 0; ires0 < res0.size(); ires0 += 2)
		{
			String res0i = res0.get(ires0);
			String entry = null, lemma = null, category = null, info = null;

			// do not test for parsing error, as the lexical entry might be of form I'll,<I,PRO><will,V>+UNAMB
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			Dic.parseDELAF(res0i, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_info);
			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			info = tempRef_info.argvalue;
			// process inflection
			String myfeatureflx = Dic.lookFor("FLX", info);
			if (myfeatureflx == null)
			{
				// the form was not inflected: keep the basic info
				if (!alreadyThereInMultiwordResults(res, res0i))
				{
					res.add(res0i);
					res.add(res0.get(ires0 + 1));
				}
			}
			else
			{
				// the form was inflected: lookup the lemma and add the basic info of the lemma to each word form's info
				ArrayList<String> infolemma = lookUpCompound0(lemma, 0, buffercl, engine);
				if (infolemma != null)
				{
					// get the info from the multi-word lemma
					for (int jres = 0; jres < infolemma.size(); jres += 2)
					{
						String lex = infolemma.get(jres);
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo = new RefObject<String>(lexinfo);
						boolean tempVar = !Dic.parseDELAF(lex, tempRef_lexentry, tempRef_lexlemma, tempRef_lexcategory,
								tempRef_lexinfo);
						lexentry = tempRef_lexentry.argvalue;
						lexlemma = tempRef_lexlemma.argvalue;
						lexcategory = tempRef_lexcategory.argvalue;
						lexinfo = tempRef_lexinfo.argvalue;
						if (tempVar)
						{
							continue;
						}
						String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
						if (category.equals(lexcategory) && myfeatureflx.equals(lexfeatureflx))
						{
							String r = res0i + Dic.removeFeature("FLX", lexinfo);
							if (!alreadyThereInMultiwordResults(res, r))
							{
								res.add(r);
								res.add(res0.get(ires0 + 1));
							}
						}
					}
				}
				else
				{
					// get the info from the simple-word lemma
					infolemma = lookUpSimple0(lemma, 0, bufferl, engine);
					if (infolemma != null)
					{
						for (String lex : infolemma)
						{
							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry2 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma2 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory2 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo2 = new RefObject<String>(lexinfo);
							boolean tempVar2 = !Dic.parseDELAF(lex, tempRef_lexentry2, tempRef_lexlemma2,
									tempRef_lexcategory2, tempRef_lexinfo2);
							lexentry = tempRef_lexentry2.argvalue;
							lexlemma = tempRef_lexlemma2.argvalue;
							lexcategory = tempRef_lexcategory2.argvalue;
							lexinfo = tempRef_lexinfo2.argvalue;
							if (tempVar2)
							{
								continue;
							}
							String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
							if (category.equals(lexcategory) && myfeatureflx.equals(lexfeatureflx))
							{
								String r = res0i + Dic.removeFeature("FLX", lexinfo);
								if (!alreadyThereInMultiwordResults(res, r))
								{
									res.add(r);
									res.add(res0.get(ires0 + 1));
								}
							}
						}
					}
					else
					{
						// the word form was inflected, but not from its lemma (this happens if the lemma is a spelling
						// variant)
						if (!alreadyThereInMultiwordResults(res, res0i))
						{
							res.add(res0i);
							res.add(res0.get(ires0 + 1));
						}
					}
				}
			}

			// process derivation
			String[] myfeaturesdrv = Dic.lookForAll("DRV", info);
			if (myfeaturesdrv != null && myfeaturesdrv.length > 0)
			{
				ArrayList<String> infolemma = lookUpSimple0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					// get the info from the simple-word lemma
					for (int jres = 0; jres < infolemma.size(); jres++)
					{
						String lex = infolemma.get(jres);
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry3 = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma3 = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory3 = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo3 = new RefObject<String>(lexinfo);
						boolean tempVar3 = !Dic.parseDELAF(lex, tempRef_lexentry3, tempRef_lexlemma3,
								tempRef_lexcategory3, tempRef_lexinfo3);
						lexentry = tempRef_lexentry3.argvalue;
						lexlemma = tempRef_lexlemma3.argvalue;
						lexcategory = tempRef_lexcategory3.argvalue;
						lexinfo = tempRef_lexinfo3.argvalue;
						if (tempVar3)
						{
							continue;
						}
						String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
						if (lexfeaturesdrv != null)
						{
							// look for one matching DRV in the lemma and in the derived form
							boolean founddrv = false;
							for (String lexfeaturedrv : lexfeaturesdrv)
							{
								for (String mydrv : myfeaturesdrv)
								{
									if (lexfeaturedrv.equals(mydrv))
									{
										founddrv = true;
										break;
									}
								}
								if (founddrv)
								{
									break;
								}
							}
							if (founddrv)
							{
								String r = res0i + Dic.removeFeature("FLX", lexinfo);
								if (!alreadyThereInMultiwordResults(res, r))
								{
									res.add(r);
									res.add(res0.get(ires0 + 1));
								}
							}
						}
					}
				}
				else
				{
					infolemma = lookUpCompound0(lemma, 0, buffercl, engine);
					if (infolemma != null)
					{
						// get the info from the multi-word lemma
						for (int jres = 0; jres < infolemma.size(); jres += 2)
						{
							String lex = infolemma.get(jres);
							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry4 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma4 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory4 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo4 = new RefObject<String>(lexinfo);
							boolean tempVar4 = !Dic.parseDELAF(lex, tempRef_lexentry4, tempRef_lexlemma4,
									tempRef_lexcategory4, tempRef_lexinfo4);
							lexentry = tempRef_lexentry4.argvalue;
							lexlemma = tempRef_lexlemma4.argvalue;
							lexcategory = tempRef_lexcategory4.argvalue;
							lexinfo = tempRef_lexinfo4.argvalue;
							if (tempVar4)
							{
								continue;
							}
							String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
							if (lexfeaturesdrv != null)
							{
								// look for one matching DRV in lemma and in derived form
								boolean founddrv = false;
								for (String lexfeaturedrv : lexfeaturesdrv)
								{
									for (String mydrv : myfeaturesdrv)
									{
										if (lexfeaturedrv.equals(mydrv))
										{
											founddrv = true;
											break;
										}
									}
									if (founddrv)
									{
										break;
									}
								}
								if (founddrv)
								{
									String r = res0i + Dic.removeFeature("FLX", lexinfo);
									if (!alreadyThereInMultiwordResults(res, r))
									{
										res.add(r);
										res.add(res0.get(ires0 + 1));
									}
								}
							}
						}
					}
					else
					{
						// the word form was not inflected from its lemma: it is a variant, e.g.
						// czar,tsar,N+Hum+FLX=TABLE
						RefObject<ArrayList<String>> tempRef_res = new RefObject<ArrayList<String>>(res);
						addNoDuplicate(tempRef_res, res0i);
						res = tempRef_res.argvalue;
					}
				}
			}
		}
		return res;
	}

	/**
	 * nooj starts at the initial state of the transducer, reads each letter in the word and goes to the corresponding
	 * state in the transduced. When the word is read, NooJ sees if the current state is terminal; if so, the word is
	 * recognized and the transducer produces its output (i.e. the information). If not the word is not recognized.
	 * 
	 * @param text
	 * @param position
	 * @param engine
	 * @return
	 */
	ArrayList<String> lookUpCompoundSemitic(String text, int position, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> res0 = lookUpCompoundSemitic0(text, position, bufferc, engine);
		if (this.buffercl == null)
		{
			return res0;
		}
		if (res0 == null)
		{
			return null;
		}
		ArrayList<String> res = new ArrayList<String>();
		for (int ires0 = 0; ires0 < res0.size(); ires0 += 2)
		{
			String res0i = res0.get(ires0);
			String entry = null, lemma = null, category = null, info = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			boolean tempVar = !Dic.parseDELAF(res0i, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_info);
			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			info = tempRef_info.argvalue;
			if (tempVar)
			{
				continue;
			}

			// process inflection
			String myfeatureflx = Dic.lookFor("FLX", info);
			if (myfeatureflx == null)
			{
				// the form was not inflected: keep the basic info
				if (!alreadyThereInMultiwordResults(res, res0i))
				{
					res.add(res0i);
					res.add(res0.get(ires0 + 1));
				}
			}
			else
			{
				// the form was inflected: lookup the lemma and add the basic info of the lemma to each word form's info
				ArrayList<String> infolemma = lookUpCompoundSemitic0(lemma, 0, buffercl, engine);
				if (infolemma != null)
				{
					// get the info from the multi-word lemma
					for (int jres = 0; jres < infolemma.size(); jres += 2)
					{
						String lex = infolemma.get(jres);
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo = new RefObject<String>(lexinfo);
						boolean tempVar2 = !Dic.parseDELAF(lex, tempRef_lexentry, tempRef_lexlemma,
								tempRef_lexcategory, tempRef_lexinfo);
						lexentry = tempRef_lexentry.argvalue;
						lexlemma = tempRef_lexlemma.argvalue;
						lexcategory = tempRef_lexcategory.argvalue;
						lexinfo = tempRef_lexinfo.argvalue;
						if (tempVar2)
						{
							continue;
						}
						String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
						if (category.equals(lexcategory) && myfeatureflx.equals(lexfeatureflx))
						{
							String r = res0i + Dic.removeFeature("FLX", lexinfo);
							if (!alreadyThereInMultiwordResults(res, r))
							{
								res.add(r);
								res.add(res0.get(ires0 + 1));
							}
						}
					}
				}
				else
				{
					// get the info from the simple-word lemma
					infolemma = lookUpSimpleSemitic0(lemma, 0, bufferl, engine);
					for (String lex : infolemma)
					{
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry2 = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma2 = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory2 = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo2 = new RefObject<String>(lexinfo);
						boolean tempVar3 = !Dic.parseDELAF(lex, tempRef_lexentry2, tempRef_lexlemma2,
								tempRef_lexcategory2, tempRef_lexinfo2);
						lexentry = tempRef_lexentry2.argvalue;
						lexlemma = tempRef_lexlemma2.argvalue;
						lexcategory = tempRef_lexcategory2.argvalue;
						lexinfo = tempRef_lexinfo2.argvalue;
						if (tempVar3)
						{
							continue;
						}
						String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
						if (category.equals(lexcategory) && myfeatureflx.equals(lexfeatureflx))
						{
							String r = res0i + Dic.removeFeature("FLX", lexinfo);
							if (!alreadyThereInMultiwordResults(res, r))
							{
								res.add(r);
								res.add(res0.get(ires0 + 1));
							}
						}
					}
				}
			}

			// process derivation
			String[] myfeaturesdrv = Dic.lookForAll("DRV", info);
			if (myfeaturesdrv != null && myfeaturesdrv.length > 0)
			{
				ArrayList<String> infolemma = lookUpSimpleSemitic0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					// get the info from the simple-word lemma
					for (int jres = 0; jres < infolemma.size(); jres++)
					{
						String lex = infolemma.get(jres);
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry3 = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma3 = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory3 = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo3 = new RefObject<String>(lexinfo);
						boolean tempVar4 = !Dic.parseDELAF(lex, tempRef_lexentry3, tempRef_lexlemma3,
								tempRef_lexcategory3, tempRef_lexinfo3);
						lexentry = tempRef_lexentry3.argvalue;
						lexlemma = tempRef_lexlemma3.argvalue;
						lexcategory = tempRef_lexcategory3.argvalue;
						lexinfo = tempRef_lexinfo3.argvalue;
						if (tempVar4)
						{
							continue;
						}
						String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
						if (lexfeaturesdrv != null)
						{
							// look for one matching DRV in the lemma and in the derived form
							boolean founddrv = false;
							for (String lexfeaturedrv : lexfeaturesdrv)
							{
								for (String mydrv : myfeaturesdrv)
								{
									if (lexfeaturedrv.equals(mydrv))
									{
										founddrv = true;
										break;
									}
								}
								if (founddrv)
								{
									break;
								}
							}
							if (founddrv)
							{
								String r = res0i + Dic.removeFeature("FLX", lexinfo);
								if (!alreadyThereInMultiwordResults(res, r))
								{
									res.add(r);
									res.add(res0.get(ires0 + 1));
								}
							}
						}
					}
				}
				else
				{
					infolemma = lookUpCompoundSemitic0(lemma, 0, buffercl, engine);
					if (infolemma != null)
					{
						// get the info from the multi-word lemma
						for (int jres = 0; jres < infolemma.size(); jres += 2)
						{
							String lex = infolemma.get(jres);
							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry4 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma4 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory4 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo4 = new RefObject<String>(lexinfo);
							boolean tempVar5 = !Dic.parseDELAF(lex, tempRef_lexentry4, tempRef_lexlemma4,
									tempRef_lexcategory4, tempRef_lexinfo4);
							lexentry = tempRef_lexentry4.argvalue;
							lexlemma = tempRef_lexlemma4.argvalue;
							lexcategory = tempRef_lexcategory4.argvalue;
							lexinfo = tempRef_lexinfo4.argvalue;
							if (tempVar5)
							{
								continue;
							}
							String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
							if (lexfeaturesdrv != null)
							{
								// look for one matching DRV in lemma and in derived form
								boolean founddrv = false;
								for (String lexfeaturedrv : lexfeaturesdrv)
								{
									for (String mydrv : myfeaturesdrv)
									{
										if (lexfeaturedrv.equals(mydrv))
										{
											founddrv = true;
											break;
										}
									}
									if (founddrv)
									{
										break;
									}
								}
								if (founddrv)
								{
									String r = res0i + Dic.removeFeature("FLX", lexinfo);
									if (!alreadyThereInMultiwordResults(res, r))
									{
										res.add(r);
										res.add(res0.get(ires0 + 1));
									}
								}
							}
						}
					}
					else
					{
						// the word form was not inflected from its lemma: it is a variant, e.g.
						// czar,tsar,N+Hum+FLX=TABLE
						RefObject<ArrayList<String>> tempRef_res = new RefObject<ArrayList<String>>(res);
						addNoDuplicate(tempRef_res, res0i);
						res = tempRef_res.argvalue;
					}
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param text
	 * @param position
	 * @param buffer
	 * @param engine
	 * @return
	 */
	private ArrayList<String> lookUpSimpleSemitic0(String text, int position, UnsignedByte[] buffer, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("bufferc", bufferc);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> info = null;
		int stateadr = 0;
		int infonb;
		int transnb, charindex = 0, dest = 0;

		Stack<Trace> stack = new Stack<Trace>();
		stack.push(new Trace());

		while (stack.size() > 0)
		{
			Trace curtrc = stack.pop();

			stateadr = curtrc.stateadr;
			int ipos = curtrc.txtpos;

			infonb = readFromBuf(buffer, stateadr, infolog);
			if (infonb > 0 && (position + ipos == text.length() || !Language.isLetter(text.charAt(position + ipos)))) // end
																														// of
																														// simple
																														// word
																														// -
																														// end
																														// of
																														// text
			{
				assert infonb < infos.length;
				ArrayList<String> infs = unCompactInfo(infos[infonb]);
				if (info == null)
				{
					info = new ArrayList<String>();
				}
				for (int i = 0; i < infs.size(); i++)
				{
					String diclemma = null, category = null, dicinfo = null;
					String[] features = null;
					String lexinfo = infs.get(i);
					String trace = curtrc.trace;
					if (lexinfo.charAt(0) == ',' && lexinfo.charAt(1) == '<')
					{
						// this is a special entry, e.g. I'm,<I,PRO><am,be,V+PR+1+s>
						info.add(trace + "," + trace + lexinfo);
						continue;
					}
					RefObject<String> tempRef_diclemma = new RefObject<String>(diclemma);
					RefObject<String> tempRef_dicinfo = new RefObject<String>(dicinfo);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					Dic.parseFactorizedInfo(lexinfo, tempRef_diclemma, tempRef_dicinfo, tempRef_category,
							tempRef_features);
					diclemma = tempRef_diclemma.argvalue;
					dicinfo = tempRef_dicinfo.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					ArrayList<String> dicinfos = Dic.normalizeInformation(category, features, engine.properties);
					if (dicinfos == null)
					{
						if (Lan.isACompound(trace) || Lan.asianTokenizer)
						{
							info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo);
						}
						else
						{
							info.add(trace + "," + Dic.unCompressSimpleLemma(trace, diclemma) + "," + dicinfo);
						}
					}
					else if (diclemma != null)
					{
						for (String dicinfo0 : dicinfos)
						{
							if (Lan.isACompound(trace) || Lan.asianTokenizer)
							{
								info.add(trace + "," + Dic.unCompressCompoundLemma(trace, diclemma) + "," + dicinfo0);
							}
							else
							{
								info.add(trace + "," + Dic.unCompressSimpleLemma(trace, diclemma) + "," + dicinfo0);
							}
						}
					}
					else
					{
						for (String dicinfo0 : dicinfos)
						{
							info.add(trace + "," + dicinfo0);
						}
					}

				}
			}

			if (text == null || position + ipos > text.length())
			{
				continue;
			}
			if (position + ipos < text.length() && text.charAt(position + ipos) == '\u0640') // kashida
			{
				Trace newtrc = new Trace();
				newtrc.stateadr = stateadr;
				newtrc.txtpos = ipos + 1;
				newtrc.trace = curtrc.trace; // +'\x640';
				stack.push(newtrc);
				continue;
			}
			transnb = readFromBuf(buffer, stateadr + infolog, alphabetlog);
			for (int it = 0; it < transnb; it++)
			{
				RefObject<Integer> tempRef_charindex = new RefObject<Integer>(charindex);
				RefObject<Integer> tempRef_dest = new RefObject<Integer>(dest);
				readTrans(buffer, stateadr, infolog, alphabetlog, buflog, it, tempRef_charindex, tempRef_dest);
				charindex = tempRef_charindex.argvalue;
				dest = tempRef_dest.argvalue;
				if (position + ipos < text.length())
				{
					if (Lan.doLettersMatch(text.charAt(position + ipos), alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos + 1;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
					else if (Language.isVowel(alphabet[charindex]))
					{
						Trace newtrc = new Trace();
						newtrc.stateadr = dest;
						newtrc.txtpos = ipos;
						newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
						stack.push(newtrc);
					}
				}
				else if (Language.isVowel(alphabet[charindex]))
				{
					Trace newtrc = new Trace();
					newtrc.stateadr = dest;
					newtrc.txtpos = ipos;
					newtrc.trace = curtrc.trace + (new Character(alphabet[charindex])).toString();
					stack.push(newtrc);
				}
			}
		}
		return info;
	}

	/**
	 * nooj starts at the initial state of the transducer, reads each letter in the word and goes to the corresponding
	 * state in the transduced. When the word is read, NooJ sees if the current state is terminal; if so, the word is
	 * recognized and the transducer produces its output (i.e. the information). If not the word is not recognized.
	 * 
	 * @param text
	 * @param position
	 * @param engine
	 * @return
	 */
	ArrayList<String> lookUpSimpleSemitic(String text, int position, Engine engine)
	{
		ParameterCheck.mandatoryString("text", text);
		ParameterCheck.mandatory("engine", engine);
		ArrayList<String> res0 = lookUpSimpleSemitic0(text, position, buffer, engine);
		if (this.bufferl == null)
		{
			return res0;
		}
		if (res0 == null)
		{
			return null;
		}

		ArrayList<String> res = new ArrayList<String>();
		for (String res0i : res0)
		{
			String entry = null, lemma = null, category = null, info = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			boolean tempVar = !Dic.parseDELAF(res0i, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_info);
			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			info = tempRef_info.argvalue;
			if (tempVar)
			{
				continue;
			}

			// process inflection
			String myfeatureflx = Dic.lookFor("FLX", info);
			if (myfeatureflx == null)
			{
				// the word form was entered as is, without any FLX nor DRV command
				RefObject<ArrayList<String>> tempRef_res = new RefObject<ArrayList<String>>(res);
				addNoDuplicate(tempRef_res, res0i);
				res = tempRef_res.argvalue;
			}
			else
			{
				// the word form was inflected: add the info of each potential lemma to the word form's info
				ArrayList<String> infolemma = lookUpSimpleSemitic0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					// get info from the simple-word lemma
					for (String lex : infolemma)
					{
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo = new RefObject<String>(lexinfo);
						Dic.parseDELAF(lex, tempRef_lexentry, tempRef_lexlemma, tempRef_lexcategory, tempRef_lexinfo);
						lexentry = tempRef_lexentry.argvalue;
						lexlemma = tempRef_lexlemma.argvalue;
						lexcategory = tempRef_lexcategory.argvalue;
						lexinfo = tempRef_lexinfo.argvalue;
						if (category.equals(lexcategory))
						{
							String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
							if (myfeatureflx.equals(lexfeatureflx))
							{
								RefObject<ArrayList<String>> tempRef_res2 = new RefObject<ArrayList<String>>(res);
								addNoDuplicate(tempRef_res2, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
								res = tempRef_res2.argvalue;
							}
						}
					}
				}
				else
				{
					// no info in the simple-word dictionary: get the info from the multi-word lemma
					infolemma = lookUpCompoundSemitic0(lemma, 0, buffercl, engine);
					// get the info from the lemma
					if (infolemma != null)
					{
						for (int ilex = 0; ilex < infolemma.size(); ilex += 2) // need to skip lengths stored in
																				// multiword units lookup results
						{
							String lex = infolemma.get(ilex);

							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry2 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma2 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory2 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo2 = new RefObject<String>(lexinfo);
							Dic.parseDELAF(lex, tempRef_lexentry2, tempRef_lexlemma2, tempRef_lexcategory2,
									tempRef_lexinfo2);
							lexentry = tempRef_lexentry2.argvalue;
							lexlemma = tempRef_lexlemma2.argvalue;
							lexcategory = tempRef_lexcategory2.argvalue;
							lexinfo = tempRef_lexinfo2.argvalue;
							if (category.equals(lexcategory))
							{
								String lexfeatureflx = Dic.lookFor("FLX", lexinfo);
								if (myfeatureflx.equals(lexfeatureflx))
								{
									RefObject<ArrayList<String>> tempRef_res3 = new RefObject<ArrayList<String>>(res);
									addNoDuplicate(tempRef_res3,
											res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
									res = tempRef_res3.argvalue;
								}
							}
						}
					}
					else
					{
						// the word form was not inflected from its lemma: it is a variant, e.g.
						// czar,tsar,N+Hum+FLX=TABLE
						RefObject<ArrayList<String>> tempRef_res4 = new RefObject<ArrayList<String>>(res);
						addNoDuplicate(tempRef_res4, res0i);
						res = tempRef_res4.argvalue;
					}
				}
			}

			// process derivation
			String[] myfeaturesdrv = Dic.lookForAll("DRV", info);
			if (myfeaturesdrv != null && myfeaturesdrv.length > 0)
			{
				// the word form was generated from a DRV command: add the info of each potential lemma with the form's
				// info
				// get the info from a simple-word dictionary
				ArrayList<String> infolemma = lookUpSimpleSemitic0(lemma, 0, bufferl, engine);
				if (infolemma != null)
				{
					for (String lex : infolemma)
					{
						String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
						RefObject<String> tempRef_lexentry3 = new RefObject<String>(lexentry);
						RefObject<String> tempRef_lexlemma3 = new RefObject<String>(lexlemma);
						RefObject<String> tempRef_lexcategory3 = new RefObject<String>(lexcategory);
						RefObject<String> tempRef_lexinfo3 = new RefObject<String>(lexinfo);
						Dic.parseDELAF(lex, tempRef_lexentry3, tempRef_lexlemma3, tempRef_lexcategory3,
								tempRef_lexinfo3);
						lexentry = tempRef_lexentry3.argvalue;
						lexlemma = tempRef_lexlemma3.argvalue;
						lexcategory = tempRef_lexcategory3.argvalue;
						lexinfo = tempRef_lexinfo3.argvalue;
						String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
						if (lexfeaturesdrv == null) // the form does not come from this lexical entry
						{
							continue;
						}
						boolean founddrv = false;
						for (String lexfeaturedrv : lexfeaturesdrv)
						{
							if (myfeaturesdrv[0].equals(lexfeaturedrv))
							{
								founddrv = true;
								break; // no need to continue to search
							}
						}
						if (founddrv)
						{
							RefObject<ArrayList<String>> tempRef_res5 = new RefObject<ArrayList<String>>(res);
							addNoDuplicate(tempRef_res5, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
							res = tempRef_res5.argvalue;
						}
					}
				}
				else
				{
					// no info in the simple-word dictionary: get the info from the multi-word lemma
					infolemma = lookUpCompoundSemitic0(lemma, 0, buffercl, engine);
					if (infolemma != null)
					{
						for (int ilex = 0; ilex < infolemma.size(); ilex += 2) // need to skip lengths stored in
																				// multiword units lookup results
						{
							String lex = infolemma.get(ilex);

							String lexentry = null, lexlemma = null, lexcategory = null, lexinfo = null;
							RefObject<String> tempRef_lexentry4 = new RefObject<String>(lexentry);
							RefObject<String> tempRef_lexlemma4 = new RefObject<String>(lexlemma);
							RefObject<String> tempRef_lexcategory4 = new RefObject<String>(lexcategory);
							RefObject<String> tempRef_lexinfo4 = new RefObject<String>(lexinfo);
							Dic.parseDELAF(lex, tempRef_lexentry4, tempRef_lexlemma4, tempRef_lexcategory4,
									tempRef_lexinfo4);
							lexentry = tempRef_lexentry4.argvalue;
							lexlemma = tempRef_lexlemma4.argvalue;
							lexcategory = tempRef_lexcategory4.argvalue;
							lexinfo = tempRef_lexinfo4.argvalue;
							String[] lexfeaturesdrv = Dic.lookForAll("DRV", lexinfo);
							if (lexfeaturesdrv == null) // the form does not come from this lexical entry
							{
								continue;
							}
							boolean founddrv = false;
							for (String lexfeaturedrv : lexfeaturesdrv)
							{
								if (myfeaturesdrv[0].equals(lexfeaturedrv))
								{
									founddrv = true;
									break; // no need to continue to search
								}
							}
							if (founddrv)
							{
								RefObject<ArrayList<String>> tempRef_res6 = new RefObject<ArrayList<String>>(res);
								addNoDuplicate(tempRef_res6, res0i + Dic.sortInfos(Dic.removeFeature("FLX", lexinfo)));
								res = tempRef_res6.argvalue;
							}
						}
					}
				}
			}
		}
		return res;
	}

}