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

import java.util.ArrayList;
import java.util.HashMap;

import net.nooj4nlp.engine.helper.ParameterCheck;

/**
 * Class that works with tree representation. Consists of right and left child trees and a label.
 * 
 * @author Silberztein Max
 * 
 */
class Tree
{

	private Tree LeftChild;
	private Tree RightChild;
	private String Label;

	/**
	 * initializes empty tree
	 */
	Tree()
	{
		LeftChild = null;
		RightChild = null;
		Label = null;
	}

	/**
	 * initializes leaf tree
	 * 
	 * @param factor
	 *            - label to set for this leaf tree
	 * @return - leaf tree
	 */
	static Tree leafTree(String factor)
	{
		Tree t = new Tree();
		t.Label = factor;
		return t;
	}

	/**
	 * initializes new tree with left and right child, and a label given as parameters
	 * 
	 * @param leftChild
	 *            left child of newly created tree
	 * @param rightChild
	 *            right child of newly created tree
	 * @param label
	 *            label of newly created tree
	 * @return new binary tree
	 */
	static Tree binaryTree(Tree leftChild, Tree rightChild, String label)
	{
		Tree t = new Tree();
		t.Label = label;
		t.LeftChild = leftChild;
		t.RightChild = rightChild;
		return t;
	}

	/**
	 * explores tree and return s Gram object based on Label and aVocab and hVocab
	 * 
	 * @param aVocab
	 * @param hVocab
	 * @return Gram object
	 */
	Gram explore(ArrayList<String> aVocab, HashMap<String, Integer> hVocab)
	{
		ParameterCheck.mandatory("aVocab", aVocab);
		ParameterCheck.mandatory("hVocab", hVocab);

		Gram gram, gram1, gram2;
		
		
		if (Label.equals("*"))
		{
			if (LeftChild == null) // it is a leaf ???
			{
				System.out.println("NooJ: a '*' operator has no first argument");
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else if (RightChild != null) // it is not a leaf ???
			{
				System.out.println("NooJ: a '*' operator has a second argument");
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else
			{
				gram1 = LeftChild.explore(aVocab, hVocab);
				gram = gram1.kleene();
			}
		}
	
		else if (Label.equals("+"))
		{
			if (LeftChild == null) // it is a leaf ???
			{
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else if (RightChild == null) // it is not a Kleene operator but
											// the "*" character
			{
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else
			{
				gram1 = LeftChild.explore(aVocab, hVocab);
				ArrayList<Gram> rightGrams = new ArrayList<Gram>();
				Tree rightTree;
				for (rightTree = this.RightChild; rightTree.Label.equals("+"); rightTree = rightTree.RightChild)
				{
					if (rightTree.LeftChild == null || rightTree.RightChild == null) // it is not a
																						// concatenation
																						// but
																						// the "+"
																						// character
					{
						break;
					}
					Gram leftGram = rightTree.LeftChild.explore(aVocab, hVocab);
					rightGrams.add(leftGram);
				}
				if (rightGrams.size() > 0)
				{
					gram1.disjunctions(rightGrams);
				}
				gram2 = rightTree.explore(aVocab, hVocab);
				gram = gram1.disjunction(gram2);
			}
		}

		else if (Label.equals("."))
		{
			if (LeftChild == null) // it is a leaf ???
			{
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else if (RightChild == null) // it is a leaf ???
			{
				gram = Gram.token(Label, aVocab, hVocab);
			}
			else
			{
				gram1 = LeftChild.explore(aVocab, hVocab);
				ArrayList<Gram> rightGrams = new ArrayList<Gram>();
				Tree rightTree;
				for (rightTree = this.RightChild; rightTree.Label.equals("."); rightTree = rightTree.RightChild)
				{
					if (rightTree.LeftChild == null || rightTree.RightChild == null) // it is not a
																						// concatenation
																						// but
																						// the "."
																						// character
					{
						break;
					}
					Gram leftGram = rightTree.LeftChild.explore(aVocab, hVocab);
					rightGrams.add(leftGram);
				}
				if (rightGrams.size() > 0)
				{
					gram1.concatenations(rightGrams);
				}
				gram2 = rightTree.explore(aVocab, hVocab);
				gram = gram1.concatenation(gram2);
			}
		}
		else
		{
			gram = Gram.token(Label, aVocab, hVocab);
		}
		return gram;
	}

	/**
	 * explores tree and returns Gram object
	 * 
	 * @param output
	 * @param aVocab
	 * @param hVocab
	 * @return
	 */
	Gram explore(String output, ArrayList<String> aVocab, HashMap<String, Integer> hVocab)
	{
		ParameterCheck.mandatory("aVocab", aVocab);
		ParameterCheck.mandatory("hVocab", hVocab);

		Gram resultGram;
		if (output != null)
		{
			resultGram = Gram.addOutput(explore(aVocab, hVocab), output, aVocab, hVocab);
		}
		else
		{
			resultGram = explore(aVocab, hVocab);
		}
		return resultGram;
	}
}