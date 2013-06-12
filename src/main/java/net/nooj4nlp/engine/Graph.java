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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.nooj4nlp.engine.helper.PenAttributes;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

public class Graph implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public Dimension size; // size of graph
	public int fits; // 1: fits form; else scale (100 => 1.0)
	private int[] arrHei, arrWid, arrPosX, arrPosY; // all of the above
	private String[] arrLabel;
	private int[][] arrChild;
	private ArrayList<ArrayList<Object>> extraParams; // for the future
	private HashMap<String, PenAttributes<Color, Float>> pen;

	transient public Grammar wholeGrammar; // graph belongs to a whole grammar (not saved)
	transient public ArrayList<Integer> hei; // Heigth of each node (& area node)
	transient public ArrayList<Integer> wid; // Width of each node (& area node)
	transient public ArrayList<String> label; // label of each node
	transient public ArrayList<Integer> posX; // Position of each node
	transient public ArrayList<Integer> posY; // Position of each node
	transient public ArrayList<ArrayList<Integer>> child; // list of children for each node
	transient public ArrayList<Boolean> selected; // is each node selected (true) or not (false)
	transient public ArrayList<Object> history;
	transient public ArrayList<Integer> widB; // Width of each node
	transient public int iHistory;

	// Unused code
	// transient private Color bColor;
	transient private Color aColor, cColor, fColor, vColor;
	transient private int e5, e10, e20; // e7
	transient public int interline;
	transient private int vinterline;
	transient private int x, y;
	transient public float scale;
	transient public Font cfont;
	transient public Font ffont, ofont, vfont, ifont;
	transient public int epsilonHei;
	transient public int epsilonWid;
	transient public int epsilonwidB;
	transient public Color tColor; // selected node color during flashing
	// Unused code
	// transient private boolean noAuxiliaryTerm;

	transient public int nbofnodesmoved = 0, inodemoved = 0;
	transient public int nbofnodesdeleted = 0, inodedeleted = 0;
	transient public int nbofnodesconnected = 0;
	transient public int inodeconnected = 0;
	transient public boolean oneconnectionatleast = false, onedisconnectionatleast = false;

	transient private ArrayList<Boolean> inDebugNode;
	transient private ArrayList<Integer> inDebugConnection;
	transient public boolean debugInvisible = false;

	public Graph()
	{
		name = null;
		size = new Dimension(1047, 763);

		fits = 100;
		scale = 1.0F;
		cfont = ffont = ifont = ofont = vfont = null;
		tColor = Color.BLACK;

		// new's
		label = new ArrayList<String>();
		selected = new ArrayList<Boolean>();
		posX = new ArrayList<Integer>();
		posY = new ArrayList<Integer>();
		hei = new ArrayList<Integer>();
		wid = new ArrayList<Integer>();
		widB = new ArrayList<Integer>();
		child = new ArrayList<ArrayList<Integer>>();
		pen = new HashMap<String, PenAttributes<Color, Float>>();

		history = new ArrayList<Object>();
		iHistory = 0; // index of the last state (here no last state)
		inodemoved = -1;
		inodeconnected = -1;
	}

	public Graph(Dimension givenSize)
	{
		name = null;
		size = givenSize;

		fits = 100;
		scale = 1.0F;
		cfont = ffont = ifont = ofont = vfont = null;
		tColor = Color.BLACK;

		// new's
		label = new ArrayList<String>();
		selected = new ArrayList<Boolean>();
		posX = new ArrayList<Integer>();
		posY = new ArrayList<Integer>();
		hei = new ArrayList<Integer>();
		wid = new ArrayList<Integer>();
		widB = new ArrayList<Integer>();
		child = new ArrayList<ArrayList<Integer>>();
		pen = new HashMap<String, PenAttributes<Color, Float>>();

		history = new ArrayList<Object>();
		iHistory = 0; // index of the last state (here no last state)
		inodemoved = -1;
		inodeconnected = -1;
	}

	/**
	 * Function loads INTEX grammar.
	 * 
	 * @param grm
	 *            - parent grammar
	 * @param fullPath
	 *            - path of grammar to be loaded
	 * @param encodingCode
	 *            - code of encoding type (default: UTF-8)
	 * @param gt
	 *            - type of grammar
	 * @return
	 */
	public static Graph loadIntex(Grammar grm, String fullPath, String encodingCode, GramType gt)
	{
		File graphFile = new File(fullPath);

		if (!graphFile.exists())
			return null;

		BufferedReader reader = null;
		Graph grf = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath), encodingCode));

			String line = "";

			line = reader.readLine();

			String sep = " ";
			String sep2 = ":";
			String[] fields = line.split(sep);

			if (fields[0].charAt(0) != '#') // "#FSGraph" or else...
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
							Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
					return null;
				}

				return null;
			}

			// INTEX FORMAT: header
			grf = new Graph(); // argument is not used
			grf.name = FilenameUtils.removeExtension(graphFile.getName());
			grf.wholeGrammar = grm;

			line = reader.readLine();

			while (true)
			{
				if (line.length() == 0)
				{
					line = reader.readLine();
					continue;
				}

				if (line.charAt(0) == '#')
					break;

				fields = line.split(sep);
				String fontName, fontType;

				if ("SIZE".equals(fields[0]))
					grf.size = new Dimension(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));

				else if ("FONT".equals(fields[0]))
				{
					line = line.substring(fields[0].length() + 1);
					fields = line.split(sep2);
					fontName = fields[0].trim();
					fontType = fields[1].trim();
					grm.iFontName = fontName;

					if (fontType.charAt(0) == 'B')
					{
						grm.iFontStyle = Font.BOLD;
						grm.iFontSize = Float.parseFloat(fontType.substring(1));
					}
					else if (fontType.charAt(0) == 'I')
					{
						grm.iFontStyle = Font.ITALIC;
						grm.iFontSize = Float.parseFloat(fontType.substring(1));
					}
					else
					{
						grm.iFontStyle = Font.PLAIN;
						grm.iFontSize = Float.parseFloat(fontType);
					}
				}

				else if ("OFONT".equals(fields[0]))
				{
					line = line.substring(fields[0].length() + 1);
					fields = line.split(sep2);
					fontName = fields[0].trim();
					fontType = fields[1].trim();
					grm.oFontName = fontName;

					if (fontType.charAt(0) == 'B')
					{
						grm.oFontStyle = Font.BOLD;
						grm.oFontSize = Float.parseFloat(fontType.substring(1));
					}
					else if (fontType.charAt(0) == 'I')
					{
						grm.oFontStyle = Font.ITALIC;
						grm.oFontSize = Float.parseFloat(fontType.substring(1));
					}
					else
					{
						grm.oFontStyle = Font.PLAIN;
						grm.oFontSize = Float.parseFloat(fontType);
					}
				}

				else if ("ACOLOR".equals(fields[0]))
					grm.aColor = convertColor(Integer.parseInt(fields[1]));

				else if ("BCOLOR".equals(fields[0]))
					grm.bColor = convertColor(Integer.parseInt(fields[1]));

				else if ("CCOLOR".equals(fields[0]))
					grm.cColor = convertColor(Integer.parseInt(fields[1]));

				else if ("FCOLOR".equals(fields[0]))
					grm.fColor = convertColor(Integer.parseInt(fields[1]));

				else if ("SCOLOR".equals(fields[0]))
					grm.sColor = convertColor(Integer.parseInt(fields[1]));

				else if ("DBOXES".equals(fields[0]))
					grm.dispBox = (fields[1].charAt(0) == 'y');

				else if ("DFRAME".equals(fields[0]))
					grm.dispFrame = (fields[1].charAt(0) == 'y');

				else if ("DDATE".equals(fields[0]))
					grm.dispDate = (fields[1].charAt(0) == 'y');

				else if ("DDIR".equals(fields[0]))
					grm.dispDir = (fields[1].charAt(0) == 'y');

				else if ("DFILE".equals(fields[0]))
					grm.dispFile = (fields[1].charAt(0) == 'y');

				else if ("DRIG".equals(fields[0]))
					; 

				else if ("DRST".equals(fields[0]))
					grm.dispState = (fields[1].charAt(0) == 'y');

				else if ("FITS".equals(fields[0]))
				{
					grf.fits = Integer.parseInt(fields[1]);

					if (grf.fits == 2)
						grf.fits = 1; // no more screen fit
				}

				else if ("PORIENT".equals(fields[0]))
					; 

				else
					return null;

				line = reader.readLine();
			}

			// new-in-NooJ parameters
			grm.cFontName = grm.iFontName;
			grm.cFontSize = grm.iFontSize;
			grm.cFontStyle = grm.iFontStyle;
			grm.vColor = Color.RED;

			// graph network
			line = reader.readLine();
			int nbOfNodes = Integer.parseInt(line);

			for (int iNode = 0; iNode < nbOfNodes; iNode++)
			{
				line = reader.readLine();

				// label
				StringBuilder label = new StringBuilder();
				int isTrt, length;

				for (isTrt = 0; line.charAt(isTrt) != '"'; isTrt++)
					;

				isTrt++;

				for (length = 0; isTrt + length < line.length() && line.charAt(isTrt + length) != '"';)
				{
					if (line.charAt(isTrt + length) == '\\')
					{
						char character = line.charAt(isTrt + length + 1);

						if (character == '+' || character == ':' || character == '<' || character == '/'
								|| character == '\\')
						{
							label.append('\\');
							label.append(line.charAt(isTrt + length + 1));
							length += 2;
						}

						else
						// double quote or... forget the backslash!
						{
							label.append(line.charAt(isTrt + length + 1));
							length += 2;
						}
					}

					else
					{
						label.append(line.charAt(isTrt + length));
						length++;
					}
				}

				if (isTrt + length >= line.length())
					return null;

				// conversion
				StringBuilder labelNooJ = convert(label, gt);
				grf.label.add(labelNooJ.toString());
				grf.selected.add(false);
				length++;

				// connections
				while (Character.isWhitespace(line.charAt(isTrt + length)))
					length++;

				String connections = line.substring(isTrt + length);
				fields = connections.split(sep);

				// fields[0] is x, fields[1] is y, fields[2] is nb of children
				int x = 0, y = 0, n = 0;

				try
				{
					x = Integer.parseInt(fields[0]);
				}
				catch (NumberFormatException e)
				{
				}

				try
				{
					y = Integer.parseInt(fields[1]);
				}
				catch (NumberFormatException e)
				{
				}

				try
				{
					n = Integer.parseInt(fields[2]);
				}
				catch (NumberFormatException e)
				{
				}

				grf.posX.add(x);
				grf.posY.add(y);
				grf.hei.add(-1);
				grf.wid.add(-1);
				grf.widB.add(-1);

				int nbOfChildren = n;
				grf.child.add(new ArrayList<Integer>());

				for (int iChild = 0; iChild < nbOfChildren; iChild++)
				{
					n = 0;

					try
					{
						n = Integer.parseInt(fields[iChild + 3]);
					}
					catch (NumberFormatException e)
					{
					}

					(grf.child.get(iNode)).add(n);
				}
			}

		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return null;
		}

		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}

		return grf;
	}

	public boolean commentNode(int inode)
	{
		if (inode < 2)
			return false; // cannot be a comment node
		String lbl = label.get(inode);
		if (lbl.length() == 0 || lbl.charAt(0) == ':')
			return false; // area node, not comment node

		ArrayList<Integer> children = child.get(inode);
		if (children.size() == 0)
			return true; // no outgoing transition: comment node

		// is there any incoming connection?
		for (int jnode = 0; jnode < child.size(); jnode++)
		{
			children = child.get(jnode);
			for (int ic = 0; ic < children.size(); ic++)
				if (children.get(ic) == inode)
					return false;
		}
		return true;
	}

	public boolean areaNode(int inode)
	{
		if (inode < 2)
			return false;
		String lbl = label.get(inode);
		if (lbl.length() == 0 || lbl.charAt(0) != ':')
			return false; // not an area node

		ArrayList<Integer> children = child.get(inode);
		if (children.size() > 0)
			return false; // no outgoing transition for area nodes

		// is there any incoming connection?
		for (int jnode = 0; jnode < child.size(); jnode++)
		{
			children = child.get(jnode);
			for (int ic = 0; ic < children.size(); ic++)
				if (children.get(ic) == inode)
					return false;
		}
		return true;
	}

	private boolean variableNode(int inode)
	{
		String lbl = label.get(inode);
		if (lbl == null || lbl.length() < 2)
			return false;
		if (lbl.charAt(0) != '$')
			return false;
		if (lbl.charAt(1) == '(' || lbl.charAt(1) == ')')
			return true;
		return false;
	}

	private void drawConnection(int inode, int ilength, int iheight, int jheight, int jnode, Graphics2D g)
	{
		if (inDebugConnection != null && debugInvisible && inode < inDebugConnection.size())
		{
			int destination = inDebugConnection.get(inode);
			if (jnode == destination)
				return;
		}

		int xi = x + ilength;
		int yi = y;

		int xj = (int) ((posX.get(jnode)) * scale);
		int yj = (int) ((posY.get(jnode)) * scale);

		g.setColor(pen.get("penC").color);
		g.setStroke(new BasicStroke(pen.get("penC").stroke));
		if (xi <= xj) // node i is left of node j
		{
			g.drawLine(xi, yi, xj, yj);
			return;
		}

		if (yi - iheight >= yj + jheight) // node i is below node j
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi + e10, yi - iheight - e10);
			pt[2] = new Point(xj - e10, yj + iheight + e10);
			pt[3] = new Point(xj, yj);
			// No cardinal splines in Java!
			

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}
		else if (yi + iheight <= yj - jheight)// node i is above node j
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi + e10, yi + iheight + e10);
			pt[2] = new Point(xj - e10, yj - iheight - e10);
			pt[3] = new Point(xj, yj);
			

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}
		else
		// nodes i and j are roughly alined
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi + e10, yi - iheight - e10);
			pt[2] = new Point(xj - e10, yj - iheight - e10);
			pt[3] = new Point(xj, yj);
			

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}

	}

	private void drawLoop(int inode, Graphics2D g)
	{
		int xi = x + wid.get(inode);
		int radius = hei.get(inode) / 2 + e5;

		g.setColor(pen.get("pen").color);
		g.setStroke(new BasicStroke(pen.get("pen").stroke));
		// TODO: Check coordinates (in C#, x and y refer to the top left corner
		// of the outer rectangle; in Java, x and y are the coordinates of the
		// outer rectangle's center)
		g.drawArc(xi - radius, y - 2 * radius, 2 * radius, 2 * radius, 270, 180);
		g.drawLine(x, y - 2 * radius, xi, y - 2 * radius);
		g.drawArc(x - radius, y - 2 * radius, 2 * radius, 2 * radius, 90, 180);
	}

	
	private void ParseLabel(String label, StringBuilder sb, StringBuilder sbo, Engine engine)
	{
		
		
		
		int i = 0;
		while ((i < label.length()) && (label.charAt(i) != '/'))
		{
			if (label.charAt(i) == '\\')
			{
				sb.append(label.charAt(i));
				i++;
				sb.append(label.charAt(i));
			}
			else if (label.charAt(i) == '"')
			{
				sb.append(label.charAt(i));
				i++;
				while ( i < label.length() && label.charAt(i) != '"')
					{
					   sb.append(label.charAt(i));
					   i++;
					}
				sb.append(label.charAt(i));
			}
			
			else if (label.charAt(i) == '<')
			{String symbol="<";
				  int num2 = 1;
		            int num3 = 1;
		            while (((i + num3) < label.length()) && (label.charAt(i + num3) != '/'))
		            {
		                if (label.charAt(i + num3) == '<')
		                {
		                    num2++;
		                }
		                else if (label.charAt(i + num3) == '>')
		                { symbol=symbol+label.charAt(i+num3);
		                    num2--;
		                    if (num2 == 0)
		                    {
		                        break;
		                    }
		                }
		                symbol=symbol+label.charAt(i+num3);
		                num3++;
		                

				}
				

				// here we have either a lexical symbol <V> or a lexeme <eat,V+PR+3+s>
				String entry = null, lemma = null, category = null;
				String[] features = null;
				RefObject<String> entryRef = new RefObject<String>(entry), lemmaRef = new RefObject<String>(lemma), categoryRef = new RefObject<String>(
						category);
				RefObject<String[]> featuresRef = new RefObject<String[]>(features);
				boolean parseLexemeSymbol = Dic.parseLexemeSymbol(symbol, entryRef, lemmaRef, categoryRef, featuresRef);
				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				features = featuresRef.argvalue;
				if (parseLexemeSymbol)
				{
					// just a regular symbol, e.g. <V+PR>
					sb.append(symbol);
				}
				else
				{
					// this is a lexeme symbol: we need to normalize its information
					ArrayList<String> infos = Dic.normalizeInformation(category, features, engine.properties);
					if (infos == null || infos.size() == 0)
					{
						sb.append(symbol);
					}
					else
					{
						if (lemma == null)
							lemma = entry;
						sb.append("<" + entry + "," + lemma + "," + infos.get(0) + ">");
						for (int ii = 1; ii < infos.size(); ii++)
						{
							sb.append("\n" + "<" + entry + "," + lemma + "," + infos.get(ii) + ">");
						}
					}
				}
				i += num3;
				
			}
			else
			{
				// just any regular character
				sb.append(label.charAt(i));
				
			}
			i++;
		}
	
		if (i < label.length())
		{
			// slash found at position i
			// remove all \n from output
		
			for (int io = i + 1; io < label.length(); io++)
				if (label.charAt(io) != '\n' && label.charAt(io) != '\r' && label.charAt(io) != '\t')
					sbo.append(label.charAt(io));
		
		}
	
		
	}

	public Gram compile(Language lan, GramType grmType, ArrayList<String> aVocab, HashMap<String, Integer> hVocab,
			Engine engine)
	{
		// compile the grm for each node
		int totalnbofstates = 0;
		int NbOfNodes = label.size();
		if (NbOfNodes == 0)
			return null;

		ArrayList<Gram> grms = new ArrayList<Gram>();
		for (int inode = 0; inode < NbOfNodes; inode++)
		{
			String label = this.label.get(inode);
			if (inode == 1 || label.length() == 0 || commentNode(inode) || areaNode(inode))
				label = "<E>";
			Regexp r;

			String labelin=null,labelout=null ;
			
			
			StringBuilder sb=new StringBuilder();
			StringBuilder sbo=new StringBuilder();
			ParseLabel(label, sb, sbo, engine);
			labelin=sb.toString();
			labelout=sbo.toString();
			if(labelout.equals(""))
				labelout=null;
		if (labelout != null)
			{
				r = new Regexp(lan, labelin, labelout, grmType, aVocab, hVocab);
			}
			else
			{
				r = new Regexp(lan, label, null, grmType, aVocab, hVocab);
			}
			
			
			
			grms.add(r.Grm);
			if (r.Grm != null)
				totalnbofstates += r.Grm.states.size();
			
			
		}

		// transfer all nodes' grammars in one grm
		Gram grm = new Gram(totalnbofstates + 2);

		int[] anchor = new int[NbOfNodes];

		// transfer inode 0 into grm
		anchor[0] = 2;
		Gram g = grms.get(0);
		if (g != null)
			grm.transfer(g, 2, 0);

		// transfer all other inodes

		for (int inode = 1; inode < NbOfNodes; inode++)
		{
			if (grms.get(inode - 1) == null)
				anchor[inode] = anchor[inode - 1];
			else
				anchor[inode] = anchor[inode - 1] + (grms.get(inode - 1).states.size());
			g = grms.get(inode);
			if (g != null)
				grm.transfer(g, anchor[inode], inode);
		}

		// connect grm states to all grms
		grm.addTransition(0, 2, 0);
		for (int inode = 0; inode < NbOfNodes; inode++)
		{
			for (int ichild = 0; ichild < (child.get(inode)).size(); ichild++)
			{
				int idest = (child.get(inode)).get(ichild);
				if (idest == 1) // terminal node
					grm.addTransition(anchor[inode] + 1, 1, 0);
				else
					grm.addTransition(anchor[inode] + 1, anchor[idest], 0);
			}
		}
		return grm;
	}

	private void getDataFromSerialization0(Grammar wholegrammar)
	{
		wholeGrammar = wholegrammar;

		hei = convertInts(arrHei);
		wid = convertInts(arrWid);
		label = new ArrayList<String>(Arrays.asList(arrLabel));
		posX = convertInts(arrPosX);
		posY = convertInts(arrPosY);
		arrHei = arrWid = arrPosX = arrPosY = null;
		arrLabel = null;

		selected = new ArrayList<Boolean>();
		for (int i = 0; i < label.size(); i++)
			selected.add(false);

		child = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < arrChild.length; i++)
		{
			int[] c = arrChild[i];
			child.add(convertInts(c));
		}
		arrChild = null;
	}

	public void getDataFromSerialization(Grammar gram)
	{
		this.wholeGrammar = gram;

		hei = convertInts(arrHei);
		wid = convertInts(arrWid);
		label = new ArrayList<String>(Arrays.asList(arrLabel));
		posX = convertInts(arrPosX);
		posY = convertInts(arrPosY);
		arrHei = arrWid = arrPosX = arrPosY = null;
		arrLabel = null;

		selected = new ArrayList<Boolean>();
		for (int i = 0; i < label.size(); i++)
			selected.add(false);

		child = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < arrChild.length; i++)
		{
			int[] c = arrChild[i];
			child.add(convertInts(c));
		}
		arrChild = null;

		// HISTORY
		if (extraParams != null && extraParams.size() > 0)
		{
			history = extraParams.get(0);
			iHistory = 0;
			if (history != null)
			{
				iHistory = history.size(); 
				try
				{
					for (int ih = 0; ih < history.size(); ih += 2)
					{
						Graph hgrf = (Graph) history.get(ih + 1);
						hgrf.getDataFromSerialization0(wholeGrammar);
						hgrf.history = this.history;
						hgrf.iHistory = this.iHistory;
					}
				}
				finally
				{
					history = null;
					iHistory = 0;
				}
			}
			inodemoved = -1;
			inodeconnected = -1;
		}
	}

	public void storeDataForSerialization0()
	{
		arrHei = convertIntegers(hei);
		arrWid = convertIntegers(wid);
		arrLabel = label.toArray(new String[label.size()]);
		;
		arrPosX = convertIntegers(posX);
		arrPosY = convertIntegers(posY);
		arrChild = new int[child.size()][];
		for (int i = 0; i < child.size(); i++)
		{
			ArrayList<Integer> c = this.child.get(i);
			arrChild[i] = convertIntegers(c);
		}

		extraParams = new ArrayList<ArrayList<Object>>();
	}

	/**
	 * Function converts data of grammar.
	 * 
	 * @param intexLabel
	 *            - data to be converted
	 * @param gt
	 *            - grammar type
	 * @return - converted data
	 */
	private static StringBuilder convert(StringBuilder intexLabel, GramType gt)
	{
		StringBuilder input = new StringBuilder();
		int iInput;

		for (iInput = 0; iInput < intexLabel.length() && intexLabel.charAt(iInput) != '/'; iInput++)
		{
			char character = intexLabel.charAt(iInput);

			if (character == '\\')
			{
				if (intexLabel.charAt(iInput + 1) != '+') // '\+' does no longer need to be protected
					input.append(character);

				iInput++;
				input.append(intexLabel.charAt(iInput));
			}

			else if (character == '+')
				input.append('\n');

			else if (character == '"')
			{
				input.append(character);

				for (iInput++; iInput < intexLabel.length() && intexLabel.charAt(iInput) != '"'; iInput++)
					input.append(intexLabel.charAt(iInput));

				if (iInput < intexLabel.length())
					input.append(intexLabel.charAt(iInput));
				else
					input.append('"');
			}

			else if (character == '<')
			{
				input.append(character);

				for (iInput++; iInput < intexLabel.length() && intexLabel.charAt(iInput) != '>'; iInput++)
					input.append(intexLabel.charAt(iInput));

				if (iInput < intexLabel.length())
					input.append(intexLabel.charAt(iInput));
				else
					input.append('>');
			}

			else
				input.append(character);
		}

		StringBuilder res;

		if (gt == GramType.FLX) // inflectional graph
		{
			if (input.length() > 0 && input.charAt(0) == ':')
				res = input;
			else
			{
				// conversion of the commands
				res = new StringBuilder();

				for (int i = 0; i < input.length(); i++)
				{
					char character = input.charAt(i);

					if ("C".equals(character))
						res.append("<R>");
					else if ("D".equals(character))
						res.append("<D>");
					else if ("L".equals(character))
						res.append("<B>");
					else if ("R".equals(character))
						res.append("<R>");
					else
					{
						if (Character.isDigit(character))
						{
							int nb = Character.digit(character, Character.MAX_RADIX);

							for (i++; i < input.length() && Character.isDigit(input.charAt(i)); i++)
							{
								nb *= 10;
								int helpNb = Character.digit(input.charAt(i), Character.MAX_RADIX);
								nb += helpNb;
							}

							i--;
							res.append("<B" + nb + ">");
						}

						else
							res.append(input.charAt(i));
					}
				}

				if (iInput < intexLabel.length())
				{
					// each inflectional property prefixed by a "+"
					res.append('/');

					for (int i = 1; iInput + i < intexLabel.length() && intexLabel.charAt(iInput + i) != ':'; i++)
					{
						if (i > 1)
							res.append('+');

						res.append(intexLabel.charAt(iInput + i));
					}
				}
			}
		}

		else if (gt == GramType.MORPHO) // morphological graph
		{
			if (input.length() > 0 && input.charAt(0) == ':')
				res = input;
			else
			{
				res = new StringBuilder();

				// replace all "." with ","
				for (int i = 0; i < input.length(); i++)
				{
					if (input.charAt(i) == '.')
						res.append(',');
					else
						res.append(input.charAt(i));
				}
			}

			if (iInput < intexLabel.length())
			{
				res.append('/');

				for (int i = 1; iInput + i < intexLabel.length(); i++)
				{
					if (intexLabel.charAt(iInput + i) == '.')
						res.append(',');
					else
						res.append(intexLabel.charAt(iInput + i));
				}
			}
		}

		else
		// syntactic graph
		{
			if (input.length() > 0 && input.charAt(0) == ':')
				res = input;
			else
			{
				res = new StringBuilder();

				for (int i = 0; i < input.length(); i++)
				{
					char character = input.charAt(i);

					if (character == '.')
						res.append(','); // replace all . with ,

					else if (character == '\\') // replace all \: with ":"
					{
						res.append("\\" + input.charAt(i + 1));
						i++;
					}

					else if (character == '<') // <MOT>=><WRD>, <MIN>=><LOW>, <MAJ>=><UPP>, <PRE>=><CAP>
					{
						if (input.charAt(i + 1) == 'M' && input.charAt(i + 2) == 'O' && input.charAt(i + 3) == 'T'
								&& input.charAt(i + 4) == '>')
						{
							res.append("<WF>");
							i += 4;
							continue;
						}
						else if (input.charAt(i + 1) == 'M' && input.charAt(i + 2) == 'I' && input.charAt(i + 3) == 'N'
								&& input.charAt(i + 4) == '>')
						{
							res.append("<LOW>");
							i += 4;
							continue;
						}
						else if (input.charAt(i + 1) == 'M' && input.charAt(i + 2) == 'A' && input.charAt(i + 3) == 'J'
								&& input.charAt(i + 4) == '>')
						{
							res.append("<UPP>");
							i += 4;
							continue;
						}
						else if (input.charAt(i + 1) == 'P' && input.charAt(i + 2) == 'R' && input.charAt(i + 3) == 'E'
								&& input.charAt(i + 4) == '>')
						{
							res.append("<CAP>");
							i += 4;
							continue;
						}
						else if (input.charAt(i + 1) == 'P' && input.charAt(i + 2) == 'N' && input.charAt(i + 3) == 'C'
								&& input.charAt(i + 4) == '>')
						{
							res.append("<P>");
							i += 4;
							continue;
						}

						else
							res.append('<');
					}

					else
						res.append(character);
				}
			}

			if (iInput < intexLabel.length())
				;
			{
				res.append('/');

				for (int i = 1; iInput + i < intexLabel.length(); i++)
				{
					char character = intexLabel.charAt(iInput + i);
					if (character == '.')
						res.append(',');
					else
						res.append(character);
				}
			}
		}

		return res;
	}

	private static int[] convertIntegers(ArrayList<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	private static ArrayList<Integer> convertInts(int[] oldArray)
	{
		Integer[] newArray = new Integer[oldArray.length];
		int i = 0;
		for (int value : oldArray)
		{
			newArray[i++] = Integer.valueOf(value);
		}
		return new ArrayList<Integer>(Arrays.asList(newArray));
	}

	/**
	 * Function converts integer representative of the Color to Color type.
	 * 
	 * @param n
	 *            - number representing color (blue from 0-7 bytes, green 8-15, red 16-24)
	 * @return - actual color
	 */
	public static Color convertColor(int n)
	{
		Color c = new Color(n);
		int r = c.getRed();
		int b = c.getBlue();
		int g = c.getGreen();

		Color c2 = new Color(r, g, b, 255);
		return c2;
	}

	/**
	 * Function converts color into long type number.
	 * 
	 * @param color
	 *            - color to be converted
	 * @return - long type correspondence
	 */

	public static long convertColor(Color color)
	{
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		long res;

		res = b;
		res += (long) g * 256;
		res += (long) r * 256 * 256;

		return res;
	}

	public int addNode(String lbl, int x, int y)
	{
		label.add(lbl);
		int inode = label.size() - 1;
		selected.add(false);
		posX.add(x);
		posY.add(y);
		hei.add(-1);
		wid.add(-1);
		widB.add(-1);
		child.add(new ArrayList<Integer>());
		return inode;
	}

	public void deleteNode(int inode)
	{
		label.remove(inode);
		selected.remove(inode);
		posX.remove(inode);
		posY.remove(inode);
		hei.remove(inode);
		wid.remove(inode);
		widB.remove(inode);
		child.set(inode, null);
		child.remove(inode);

		// delete all children == inode && decremente all nodes' children >
		// inode
		for (int i = 0; i < child.size(); i++)
		{
			ArrayList<Integer> children = child.get(i);
			for (int ic = 0; ic < children.size(); ic++)
			{
				int dnode = children.get(ic);
				if (dnode == inode)
				{
					children.remove(ic);
					ic--;
				}
				else if (dnode > inode)
				{
					children.set(ic, dnode - 1);
				}
			}
		}
	}

	/**
	 * Function saves special characters while saving string to the file.
	 * 
	 * @param label
	 *            - string with special characters to be saved
	 * @return - new String with saved special characters
	 */

	/**
	 * Save function for Export Graph of Grammars.
	 * 
	 * @param fullName
	 *            - full path of file where graph is being exported
	 * @param NoojRightToLeft
	 *            - flag to determine whether text should be read from right to left and vice versa
	 */

	public void saveIntex(String fullName, boolean NoojRightToLeft) // Save INTEX Graph
	{
		String fStyle;
		long color;

		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(fullName); // Windows encoding is default

			pw.write("#FSGraph 4.0\n");
			pw.write("SIZE " + size.width + " " + size.height + "\n");

			fStyle = "";

			if (this.ifont.isBold())
				fStyle = "B";
			if (this.ifont.isItalic())
				fStyle += "I";

			if (fStyle == "")
				fStyle = " ";

			pw.write("FONT " + wholeGrammar.iFontName + ":" + fStyle + " " + wholeGrammar.iFontSize + "\n");

			fStyle = "";

			if (this.ofont.isBold())
				fStyle = "B";
			if (this.ofont.isItalic())
				fStyle += "I";

			if (fStyle == "")
				fStyle = " ";

			pw.write("OFONT " + wholeGrammar.oFontName + ":" + fStyle + " " + wholeGrammar.oFontSize + "\n");

			color = convertColor(wholeGrammar.aColor);
			pw.write("ACOLOR " + color + "\n");
			color = convertColor(wholeGrammar.bColor);
			pw.write("BCOLOR " + color + "\n");
			color = convertColor(wholeGrammar.cColor);
			pw.write("CCOLOR " + color + "\n");
			color = convertColor(wholeGrammar.fColor);
			pw.write("FCOLOR " + color + "\n");
			color = convertColor(wholeGrammar.sColor);
			pw.write("SCOLOR " + color + "\n");

			pw.write("DBOXES y\n");
			pw.write("DFRAME " + (wholeGrammar.dispFrame ? "y" : "n") + "\n");
			pw.write("DDATE " + (wholeGrammar.dispDate ? "y" : "n") + "\n");
			pw.write("DFILE " + (wholeGrammar.dispFile ? "y" : "n") + "\n");
			pw.write("DDIR " + (wholeGrammar.dispDir ? "y" : "n") + "\n");
			pw.write("DRIG " + (NoojRightToLeft ? "y" : "n") + "\n");
			pw.write("DRST " + (wholeGrammar.dispState ? "y" : "n") + "\n");

			pw.write("FITS " + fits + "\n");
			pw.write("PORIENT " + (size.width > size.height ? "L" : "P") + "\n");
			pw.write("#" + "\n");

			// graph network
			pw.write(label.size() + "\n");

			for (int iNode = 0; iNode < label.size(); iNode++)
			{
				pw.write("\"");
				pw.write(protectChars(label.get(iNode)));
				pw.write("\" ");

				pw.write((posX.get(iNode)) + " ");
				pw.write((posY.get(iNode)) + " ");

				// connections
				ArrayList<Integer> lc = child.get(iNode);
				pw.write(lc.size() + " ");

				for (int iChild = 0; iChild < lc.size(); iChild++)
				{
					int c = lc.get(iChild);
					pw.write(c + " ");
				}

				pw.write("\n");
			}
		}

		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM_DEFAULT, JOptionPane.ERROR_MESSAGE);
		}

		finally
		{
			pw.close();
		}
	}

	public void saveCurrentGraphInHistory(String modification)
	{
		if (history == null)
			history = new ArrayList<Object>();
		if (history.size() > iHistory)
			history.subList(iHistory, iHistory + (history.size() - iHistory)).clear();
		history.add(modification);
		history.add(this.clone());
		iHistory += 2; // index of the last state just saved
	}

	public void purgeHistory()
	{
		if (iHistory >= 2)
		{
			history.subList(0, iHistory - 2);
			iHistory = 2;
			history.set(0, "Initial");
			Graph cgrf = (Graph) history.get(iHistory - 1);
			getGraphFromHistory(cgrf);
			if (history.size() > 2)
				history.subList(2, history.size());
		}
		else
		{
			history = new ArrayList<Object>();
			history.add("Initial");
			history.add(this);
			iHistory = 2;
		}
	}

	public void undo()
	{
		// get previous state
		if (history.size() == 0)
			return;
		if (iHistory == 2)
			return;

		iHistory -= 2;
		Graph cgrf = (Graph) history.get(iHistory - 1);
		getGraphFromHistory(cgrf);
	}

	public void reset()
	{
		// get previous state
		if (history.size() == 0)
			return;
		if (iHistory == 2)
			return;

		iHistory = 2;
		Graph cgrf = (Graph) history.get(1);
		getGraphFromHistory(cgrf);
	}

	public void redo()
	{
		// get previous state
		if (history.size() == 0)
			return;
		if (iHistory >= history.size())
			return;

		iHistory += 2;
		Graph cgrf = (Graph) history.get(iHistory - 1);
		getGraphFromHistory(cgrf);
	}

	public void redoEnd()
	{
		// get previous state
		if (history.size() == 0)
			return;
		if (iHistory >= history.size())
			return;

		iHistory = history.size();
		Graph cgrf = (Graph) history.get(iHistory - 1);
		getGraphFromHistory(cgrf);
	}

	public void Do(int ihistory)
	{
		iHistory = ihistory;
		Graph cgrf = (Graph) history.get(iHistory - 1);
		getGraphFromHistory(cgrf);
	}

	private void getGraphFromHistory(Graph cgrf)
	{
		this.wholeGrammar = cgrf.wholeGrammar;

		this.hei = new ArrayList<Integer>();
		for (int i = 0; i < cgrf.hei.size(); i++)
			this.hei.add(cgrf.hei.get(i));

		this.wid = new ArrayList<Integer>();
		for (int i = 0; i < cgrf.wid.size(); i++)
			this.wid.add(cgrf.wid.get(i));

		this.widB = new ArrayList<Integer>(); // get WidB from Wid, *NOT* WidB which can be null
		for (int i = 0; i < cgrf.wid.size(); i++)
			this.widB.add(cgrf.wid.get(i));

		this.label = new ArrayList<String>();
		for (int i = 0; i < cgrf.label.size(); i++)
			this.label.add(cgrf.label.get(i));

		this.selected = new ArrayList<Boolean>();
		for (int i = 0; i < cgrf.selected.size(); i++)
			this.selected.add(cgrf.selected.get(i));

		this.posX = new ArrayList<Integer>();
		for (int i = 0; i < cgrf.posX.size(); i++)
			this.posX.add(cgrf.posX.get(i));

		this.posY = new ArrayList<Integer>();
		for (int i = 0; i < cgrf.posY.size(); i++)
			this.posY.add(cgrf.posY.get(i));

		this.child = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < cgrf.child.size(); i++)
		{
			ArrayList<Integer> children = cgrf.child.get(i);
			ArrayList<Integer> cchildren = new ArrayList<Integer>();
			for (int j = 0; j < children.size(); j++)
				cchildren.add(children.get(j));
			this.child.add(cchildren);
		}
	}

	@Override
	public Graph clone()
	{
		Graph grf = new Graph();
		grf.wholeGrammar = this.wholeGrammar;

		grf.hei = new ArrayList<Integer>();
		for (int i = 0; i < this.hei.size(); i++)
			grf.hei.add(this.hei.get(i));

		grf.wid = new ArrayList<Integer>();
		for (int i = 0; i < this.wid.size(); i++)
			grf.wid.add(this.wid.get(i));

		grf.widB = new ArrayList<Integer>(); // get widB from wid, *NOT* Widb which can be null...
		for (int i = 0; i < this.wid.size(); i++)
			grf.widB.add(this.wid.get(i));

		grf.label = new ArrayList<String>();
		for (int i = 0; i < this.label.size(); i++)
			grf.label.add(this.label.get(i));

		grf.selected = new ArrayList<Boolean>();
		for (int i = 0; i < this.selected.size(); i++)
			grf.selected.add(this.selected.get(i));

		grf.posX = new ArrayList<Integer>();
		for (int i = 0; i < this.posX.size(); i++)
			grf.posX.add(this.posX.get(i));

		grf.posY = new ArrayList<Integer>();
		for (int i = 0; i < this.posY.size(); i++)
			grf.posY.add(this.posY.get(i));

		grf.child = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < this.child.size(); i++)
		{
			ArrayList<Integer> children = this.child.get(i);
			ArrayList<Integer> cchildren = new ArrayList<Integer>();
			for (int j = 0; j < children.size(); j++)
				cchildren.add(children.get(j));
			grf.child.add(cchildren);
		}
		return grf;
	}

	public static ArrayList<String> embGraphsLabel(String label)
	{
		String output = null;
		ArrayList<String> embedded = null;

		RefObject<String> outputRef = new RefObject<String>(output);
		String[] terms = inLineLabel(label, outputRef);
		output = outputRef.argvalue;
		for (int i = 0; i < terms.length; i++)
			if (terms[i].length() > 1 && terms[i].charAt(0) == ':')
			{
				if (embedded == null)
					embedded = new ArrayList<String>();
				embedded.add(terms[i].substring(1));
			}
		return embedded;
	}

	public static String[] inLineLabel(String label, RefObject<String> output)
	{
		// look for '/'
		int i;
		String input;
		for (i = 0; i < label.length() && label.charAt(i) != '/'; i++)
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
		}
		if (i > label.length())
		{
			// label ends with an unfinished @"\"
			input = "ERROR";
			output.argvalue = null;
		}
		else if (i == label.length())
		{
			input = label;
			output.argvalue = null;
		}
		else
		{
			input = label.substring(0, i);
			output.argvalue = label.substring(i + 1);
		}

		// parse input: look for terms
		ArrayList<String> aterms = new ArrayList<String>(); // array of array of
															// String

		for (int iterm = 0; iterm < input.length();)
		{
			int len;
			for (len = 0; iterm + len < input.length() && input.charAt(iterm + len) != '\n';)
			{
				if (input.charAt(iterm + len) == '\\')
					len += 2;
				else if (input.charAt(iterm + len) == '"')
				{
					for (len++; iterm + len < input.length() && input.charAt(iterm + len) != '"'; len++)
						if (input.charAt(iterm + len) == '\\')
							len++;
					if (iterm + len < input.length())
						len++;
				}
				else if (input.charAt(iterm + len) == '<')
				{
					for (len++; iterm + len < input.length() && input.charAt(iterm + len) != '>'; len++)
						if (input.charAt(iterm + len) == '\\')
							len++;
					if (iterm + len < input.length())
						len++;
				}
				else
					len++;
			}

			// we have a term from iterm, length len
			String cterm = input.substring(iterm, iterm + len);

			// remove all '\\' for display
			String noslash = "";
			for (int ict = 0; ict < cterm.length(); ict++)
			{
				if (cterm.charAt(ict) != '\\')
					noslash = noslash + cterm.charAt(ict);
			}
			aterms.add(noslash);
			iterm += len;
			if (iterm < input.length() && input.charAt(iterm) == '\n')
				iterm++;
		}
		String[] terms = aterms.toArray(new String[aterms.size()]);
		return terms;
	}

	public void paint(JPanel p, Graphics g, boolean moving)
	{
		// non serialized
		// TODO: What to do about StringFormat sformat?
		// NOTE: Measuring trailing spaces might not be necessary in Java (needs
		// testing)
		
		widB = new ArrayList<Integer>();
	
		for (int i = 0; i < label.size(); i++)
		{
			widB.add(-1);
			
		}
	

		// computes colors
		aColor = wholeGrammar.aColor;
		cColor = wholeGrammar.cColor;
		fColor = wholeGrammar.fColor;
		vColor = wholeGrammar.vColor;

		Graphics2D graphics = (Graphics2D) g;
		p.setBackground(wholeGrammar.bColor);

		// scale
		e5 = (int) (5.0F * scale);
		e10 = (int) (10.0F * scale);
		e20 = (int) (20.0F * scale);

		FontMetrics iMetrics = graphics.getFontMetrics(ifont);
		interline = iMetrics.getHeight();

		FontMetrics vMetrics = graphics.getFontMetrics(vfont);
		vinterline = vMetrics.getHeight();
		epsilonHei = interline / 2;
		epsilonWid = iMetrics.stringWidth("<E>");
		epsilonwidB = epsilonWid;

		// Frame
		pen.put("pen", new PenAttributes<Color, Float>(wholeGrammar.fColor, 1)); // default ink
		pen.put("penC", new PenAttributes<Color, Float>(wholeGrammar.fColor, 1)); // ink for connections
		pen.put("penS", new PenAttributes<Color, Float>(tColor, 2)); // ink for selection marks
		pen.put("penF", new PenAttributes<Color, Float>(wholeGrammar.fColor, 2)); // ink for Frame
		pen.put("penD", new PenAttributes<Color, Float>(wholeGrammar.cColor, 1)); // ink for dashed comments
		graphics.setColor(wholeGrammar.bColor);
		graphics.fillRect(0, 0, (int) (size.width * scale), (int) (size.height * scale));

		if (name != "Main")
		{
			graphics.setFont(ffont);
			graphics.setColor(wholeGrammar.fColor);
			graphics.drawString(name, e20, e20);
		}
		if (wholeGrammar.dispFrame)
		{
			graphics.setColor(pen.get("penF").color);
			graphics.setStroke(new BasicStroke(pen.get("penF").stroke));
			graphics.drawRect(e10, e10, (int) (size.width * scale) - e20, (int) (size.height * scale) - e20);
		}

		int line = 2;
		if (wholeGrammar.dispDate)
		{
			if (wholeGrammar.fullName != null)
			{
				File file = new File(wholeGrammar.fullName);
				if (file.exists())
				{
					Date dt = new Date(file.lastModified());
					DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, hh:mm:ss");
					graphics.setFont(ffont);
					graphics.setColor(wholeGrammar.fColor);
					graphics.drawString(dateFormat.format(dt), e20, (int) (size.height * scale) - e20 * (line++));
				}
			}
		}
		if (wholeGrammar.dispFile)
		{
			graphics.setFont(ffont);
			graphics.setColor(wholeGrammar.fColor);
			if (wholeGrammar.fullName != null)
			{
				if (wholeGrammar.dispDir)
				{
					graphics.drawString(wholeGrammar.fullName, e20, (int) (size.height * scale) - e20 * (line++));
				}
				else
					graphics.drawString(FilenameUtils.removeExtension(new File(wholeGrammar.fullName).getName()), e20,
							(int) (size.height * scale) - e20 * (line++));
			}
		}
		if (wholeGrammar.dispInstitution && wholeGrammar.institution != null)
		{
			String institution = wholeGrammar.institution;
			if (institution.equals(""))
				institution = "Unknown (set Preferences)";
			{
				graphics.setFont(ffont);
				graphics.setColor(wholeGrammar.fColor);
				graphics.drawString(institution, e20, (int) (size.height * scale) - e20 * (line++));
			}
		}
		if (wholeGrammar.dispAuthor && wholeGrammar.author != null)
		{
			String author = wholeGrammar.author;
			if (author.equals(""))
				author = "Unknown (set Preferences)";
			graphics.setFont(ffont);
			graphics.setColor(wholeGrammar.fColor);
			graphics.drawString(author, e20, (int) (size.height * scale) - e20 * (line++));
		}

		if (wholeGrammar.dispGrid)
		{
			int GridSize = 20;
			for (int i = GridSize; i < size.width; i += GridSize)
				for (int j = GridSize; j < size.height; j += GridSize)
				{
					graphics.setColor(pen.get("penF").color);
					graphics.setStroke(new BasicStroke(pen.get("penF").stroke));
					graphics.drawLine((int) (i * scale), (int) (j * scale), i + 1, j + 1);
				}
		}

		// Paint each node
		int NbOfNodes = label.size();
		// Unused code
	
		if (p.getComponentOrientation() == ComponentOrientation.LEFT_TO_RIGHT) // ENGLISH,
																				// FRENCH,
																				// etc.
		{
			for (int inode = 0; inode < NbOfNodes; inode++)
			{
				if (inode == 1)
					paintTerminalNode(inode, graphics);
				else if (variableNode(inode))
					paintVariableNode(inode, graphics);
				else if (commentNode(inode))
					paintCommentNode(inode, graphics);
				else if (areaNode(inode))
					paintAreaNode(inode, graphics);
				else
					paintRegularNode(inode, graphics);
			}
		}
		else
		// ARABIC, HEBREW
		{
			for (int inode = 0; inode < NbOfNodes; inode++)
			{
				if (inode == 1)
					paintTerminalNode(inode, graphics);
				else if (variableNode(inode))
					paintVariableNode2(inode, graphics);
				else if (commentNode(inode))
					paintCommentNode2(inode, graphics);
				else if (areaNode(inode))
					paintAreaNode(inode, graphics);
				else
					paintRegularNode2(inode, graphics);
			}
		}
	}

	private void paintRegularNode2(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		Color b = fColor;

		FontMetrics iMetrics = g.getFontMetrics(ifont);
		FontMetrics oMetrics = g.getFontMetrics(ofont);

		if (terms.length == 1 && terms[0].equals("<E>")) // epsilon node
		{
			int maxlen = iMetrics.stringWidth("E");
			wid.set(inode, maxlen + e5);
			hei.set(inode, interline / 2);

			g.setColor(pen.get("pen").color);
			g.setStroke(new BasicStroke(pen.get("pen").stroke));
			if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
					|| (!(Boolean) inDebugNode.get(inode)))
			{
				g.drawLine(x, y, x + maxlen, y);
			}
			g.drawLine(x - e5, y, x, y - interline / 2);
			g.drawLine(x - e5, y, x, y + interline / 2);
			g.drawLine(x, y - interline / 2, x, y + interline / 2);

			if (output != null)
			{
				g.setFont(ofont);
				g.setColor(b);
				System.out.println(g.getColor());
				g.drawString(output, x, y + hei.get(inode) * 2);
				if (oMetrics.stringWidth("/" + output) > maxlen)
					widB.set(inode, oMetrics.stringWidth("/" + output));
				else
					widB.set(inode, maxlen);
			}
			else
				widB.set(inode, maxlen);

			// add a tail to the initial node
			if (inode == 0)
			{
				if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
						|| (!(Boolean) inDebugNode.get(inode)))
					g.setColor(pen.get("penF").color);
				g.setStroke(new BasicStroke(pen.get("penF").stroke));
				g.drawLine(x + maxlen, y, x + maxlen + e10, y);
			}

		}
		else
		// one or more terms
		{
			hei.set(inode, interline * terms.length / 2);
			int line0 = y - hei.get(inode);
			int maxlen = 0;
			for (int i = 0; i < terms.length; i++)
			{
				if (terms[i].length() > 0 && terms[i].charAt(0) == ':') // auxiliary
																		// term
				{
					// Unused code
			

					g.setColor(aColor);
					g.fillRect(x, line0 + interline * i, iMetrics.stringWidth(terms[i].substring(1)), interline);
					g.setFont(ifont);
					g.setColor(b);
					g.drawString(terms[i].substring(1), x, line0 + interline * (i + 1));
					if (iMetrics.stringWidth(terms[i].substring(1)) > maxlen)
						maxlen = iMetrics.stringWidth(terms[i].substring(1));
				}
				else
				// regular term
				{
					int w;
					g.setFont(ifont);
					g.setColor(b);
					g.drawString(terms[i], x, line0 + interline * (i + 1));
					w = iMetrics.stringWidth(terms[i]);
					if (w > maxlen)
						maxlen = w;
				}
			}
			maxlen += e5;
			wid.set(inode, maxlen + e5);
			if (output != null)
			{
				g.setFont(ofont);
				g.setColor(b);
				g.drawString(output, x, line0 + interline * (terms.length + 1));
				if (oMetrics.stringWidth("/" + output) > maxlen)
					widB.set(inode, oMetrics.stringWidth("/" + output));
				else
					widB.set(inode, maxlen);
			}
			else
				widB.set(inode, maxlen);

			g.setColor(pen.get("pen").color);
			g.setStroke(new BasicStroke(pen.get("pen").stroke));
			if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
					|| (!(Boolean) inDebugNode.get(inode)))
				g.drawRect(x, line0, maxlen, interline * terms.length);
			// arrow
			g.drawLine(x - e5, y, x, line0);
			g.drawLine(x - e5, y, x, line0 + interline * terms.length);

			// add a tail to the initial node
			if (inode == 0)
			{
				if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
						|| (!(Boolean) inDebugNode.get(inode)))
				{
					g.setColor(pen.get("penF").color);
					g.setStroke(new BasicStroke(pen.get("penF").stroke));
					g.drawLine(x + maxlen, y, x + maxlen + e10, y);
				}
			}

		}

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
				drawConnection2(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
		}
	}

	private void drawConnection2(int inode, int ilength, int iheight, int jheight, int jnode, Graphics2D g)
	{
		if (inDebugConnection != null && debugInvisible && inode < inDebugConnection.size())
		{
			int destination = inDebugConnection.get(inode);
			if (jnode == destination)
				return;
		}

		int xi = x - e5;
		int yi = y;

		int xj = (int) ((posX.get(jnode)) * scale) + wid.get(jnode) - e5;
		if (jnode == 1)
			xj += e5;

		int yj = (int) ((posY.get(jnode)) * scale);

		g.setColor(pen.get("penC").color);
		g.setStroke(new BasicStroke(pen.get("penC").stroke));
		if (xi >= xj) // node i is left of node j
		{
			g.drawLine(xi, yi, xj, yj);
			return;
		}

		if (yi - iheight >= yj + jheight) // node i is below node j
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi - e10, yi - iheight - e10);
			pt[2] = new Point(xj + e10, yj + iheight + e10);
			pt[3] = new Point(xj, yj);
			// No cardinal splines in Java!
		

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}
		else if (yi + iheight <= yj - jheight)// node i is above node j
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi - e10, yi + iheight + e10);
			pt[2] = new Point(xj + e10, yj - iheight - e10);
			pt[3] = new Point(xj, yj);
		

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}
		else
		// nodes i and j are roughly alined
		{
			Point[] pt = new Point[4];
			pt[0] = new Point(xi, yi);
			pt[1] = new Point(xi - e10, yi - iheight - e10);
			pt[2] = new Point(xj + e10, yj - iheight - e10);
			pt[3] = new Point(xj, yj);
		

			g.drawLine(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
			g.drawLine(pt[1].x, pt[1].y, pt[2].x, pt[2].y);
			g.drawLine(pt[2].x, pt[2].y, pt[3].x, pt[3].y);
		}
	}

	private void paintVariableNode2(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);
		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		FontMetrics oMetrics = g.getFontMetrics(ofont);
		FontMetrics vMetrics = g.getFontMetrics(vfont);

		String parenthesis = terms[0].substring(1, 2);
		g.setFont(vfont);
		g.setColor(vColor);
		if (parenthesis.equals("("))
		{
			g.drawString(")", x, y - vinterline / 2);
		}
		else if (parenthesis.equals(")"))
			g.drawString("(", x, y - vinterline / 2);
		else
			g.drawString(terms[0].substring(1, 2), x, y - vinterline / 2);
		if (terms[0].length() > 2)
		{
			g.setFont(ofont);
			g.drawString(terms[0].substring(2), x + e5, y + vinterline / 2);
		}
		int maxlen = vMetrics.stringWidth("(");
		wid.set(inode, maxlen + e5);
		widB.set(inode, maxlen + oMetrics.stringWidth(terms[0].substring(2)));
		hei.set(inode, vinterline / 2);

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// add a tail if initial node
		if (inode == 0)
		{
			g.setColor(pen.get("penF").color);
			g.setStroke(new BasicStroke(pen.get("penF").stroke));
			g.drawLine(x - e10, y, x, y);
		}

		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
				drawConnection2(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
		}
	}

	private void paintRegularNode(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		Color b = fColor;

		FontMetrics iMetrics = g.getFontMetrics(ifont);
		FontMetrics oMetrics = g.getFontMetrics(ofont);

		if (terms.length == 1 && terms[0].equals("<E>")) // epsilon node
		{
			int maxlen = iMetrics.stringWidth("<E>");
			wid.set(inode, maxlen + e5);
			hei.set(inode, interline / 2);
			g.setColor(pen.get("pen").color);
			g.setStroke(new BasicStroke(pen.get("pen").stroke));
			if (inDebugNode == null || !debugInvisible || inode >= inDebugNode.size()
					|| (!(Boolean) inDebugNode.get(inode)))
			{
				g.drawLine(x, y, x + maxlen, y);
			}
			g.drawLine(x + maxlen + e5, y, x + maxlen, y - interline / 2);
			g.drawLine(x + maxlen + e5, y, x + maxlen, y + interline / 2);
			g.drawLine(x + maxlen, y - interline / 2, x + maxlen, y + interline / 2);

			if (output != null)
			{
				g.setFont(ofont);
				g.setColor(b);
				g.drawString(output, x, y + hei.get(inode) * 2);
				if (oMetrics.stringWidth("/" + output) > maxlen)
					widB.set(inode, oMetrics.stringWidth("/" + output));
				else
					widB.set(inode, maxlen);
			}
			else
				widB.set(inode, maxlen);
		}
		else
		// one or more terms
		{
			hei.set(inode, interline * terms.length / 2);
			int line0 = y - hei.get(inode);
			int maxlen = 0;
			for (int i = 0; i < terms.length; i++)
			{
				if (terms[i].length() > 0 && terms[i].charAt(0) == ':') // auxiliary
																		// term
				{
					// Unused code
				
					g.setColor(aColor);
					g.fillRect(x, line0 + interline * i, iMetrics.stringWidth(terms[i].substring(1)), interline);

					g.setColor(b);
					g.setFont(ifont);
					g.drawString(terms[i].substring(1), x, line0 + interline * (i + 1));

					if ((iMetrics.stringWidth(terms[i].substring(1))) > maxlen)
						maxlen = iMetrics.stringWidth(terms[i].substring(1));
				}
				else if (terms[i].length() > 0 & terms[i].charAt(0) == '$') // variable
																			// term
				{
					int w;
					g.setFont(ofont);
					g.setColor(vColor);
					g.drawString(terms[i].substring(1), x, line0 + interline * i);
					w = oMetrics.stringWidth(terms[i].substring(1));
					if (w > maxlen)
						maxlen = w;
				}
				else
				// regular term
				{
					int w;
					g.setFont(ifont);
					g.setColor(b);
					g.drawString(terms[i], x, line0 + interline * (i + 1));
					w = iMetrics.stringWidth(terms[i]);
					if (w > maxlen)
						maxlen = w;
				}
			}
			maxlen += e5;
			wid.set(inode, maxlen + e5);
			if (output != null)
			{
				g.setFont(ofont);
				g.setColor(b);
				g.drawString(output, x, line0 + interline * (terms.length + 1));
				if (oMetrics.stringWidth("/" + output) > maxlen)
					widB.set(inode, oMetrics.stringWidth("/" + output));
				else
					widB.set(inode, maxlen);
			}
			else
				widB.set(inode, maxlen);

			g.setColor(pen.get("pen").color);
			g.setStroke(new BasicStroke(pen.get("pen").stroke));
			if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
					|| (!(Boolean) inDebugNode.get(inode)))
			{
				g.drawRect(x, line0, maxlen, interline * terms.length);
			}
			g.drawLine(x + maxlen + e5, y, x + maxlen, line0);
			g.drawLine(x + maxlen + e5, y, x + maxlen, line0 + interline * terms.length);
		}

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			// The center of the arc is the center of the rectangle whose origin
			// is (x, y)
			g.drawArc(rect1.x - rect1.width / 2, rect1.y, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// add a tail to the initial node
		if (inode == 0)
		{
			if (inDebugNode == null || !this.debugInvisible || inode >= inDebugNode.size()
					|| (!(Boolean) inDebugNode.get(inode)))
			{
				g.setColor(pen.get("penF").color);
				g.setStroke(new BasicStroke(pen.get("penF").stroke));
				g.drawLine(x - e10, y, x, y);
			}
		}

		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
				drawConnection(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
		}
	}

	private void paintAreaNode(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		Color b = cColor;

		FontMetrics oMetrics = g.getFontMetrics(ofont);
		FontMetrics cMetrics = g.getFontMetrics(cfont);

		// height
		if ((int) (hei.get(inode) * scale) < interline * terms.length / 2)
			hei.set(inode, ((int) (interline * terms.length / 2 / scale)));
		int h = (int) (hei.get(inode) * scale);

		// width
		int line0 = y - (int) (hei.get(inode) * scale);
		int maxlen = 0;
		for (int i = 0; i < terms.length; i++)
		{
			if (terms[i].length() > 0)
			{
				g.setFont(cfont);
				g.setColor(b);
				g.drawString(terms[i].substring(1), x, line0 + interline * (i + 1));
				if (cMetrics.stringWidth(terms[i].substring(1)) > maxlen)
					maxlen = cMetrics.stringWidth(terms[i].substring(1));
			}
		}
		maxlen += e5;
		if (output != null)
		{
			g.setFont(ofont);
			g.setColor(b);
			g.drawString(output, x, line0 + interline * (terms.length + 1));
			if (oMetrics.stringWidth("/" + output) > maxlen)
				widB.set(inode, oMetrics.stringWidth("/" + output));
			else
				widB.set(inode, maxlen);
		}
		else
			widB.set(inode, maxlen);
		if ((int) (wid.get(inode) * scale) < widB.get(inode))
		{
			wid.set(inode, (int) (widB.get(inode) / scale));
		}
		int w = (int) (wid.get(inode) * scale);

	

		Rectangle rect = new Rectangle(x, line0 + interline, w, 2 * (h - interline / 2));
		g.setColor(pen.get("penD").color);

		float dash1[] = { 10.0f };
		BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
		g.setStroke(dashed);
		g.draw(rect);

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - h - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + h, 10, 10);
			Rectangle rect3 = new Rectangle(x + w, y - h - 10, 10, 10);
		

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);

			g.drawLine(x + w, y + h, x + w + 10, y + h);
			g.drawLine(x + w, y + h, x + w, y + h + 10);
			g.drawLine(x + w, y + h + 10, x + w + 10, y + h);
			// reset color
			g.setColor(pen.get("pen").color);
		}
	}

	private void paintCommentNode(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		Color b = cColor;

		FontMetrics oMetrics = g.getFontMetrics(ofont);
		FontMetrics cMetrics = g.getFontMetrics(cfont);

		hei.set(inode, interline * terms.length / 2);
		int line0 = y - hei.get(inode);
		int maxlen = 0;
		for (int i = 0; i < terms.length; i++)
		{
			int w;
			g.setFont(cfont);
			g.setColor(b);
			g.drawString(terms[i], x, line0 + interline * (i + 1));
			w = cMetrics.stringWidth(terms[i]);
			if (w > maxlen)
				maxlen = w;
		}
		maxlen += e5;
		wid.set(inode, maxlen + e5);
		if (output != null)
		{
			g.setFont(ofont);
			g.setColor(b);
			g.drawString(output, x, line0 + interline * (terms.length + 1));
			if (oMetrics.stringWidth("/" + output) > maxlen)
				widB.set(inode, oMetrics.stringWidth("/" + output));
			else
				widB.set(inode, maxlen);
		}
		else
			widB.set(inode, maxlen);

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// still have to connect to outgoing nodes if necessary
		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
				drawConnection(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
		}
	}

	private void paintVariableNode(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		FontMetrics oMetrics = g.getFontMetrics(ofont);
		FontMetrics vMetrics = g.getFontMetrics(vfont);

		g.setColor(vColor);
		g.setFont(vfont);
		g.drawString(terms[0].substring(1, 2), x, y + vinterline / 2);
		if (terms[0].length() > 2)
		{
			g.setFont(ofont);
			g.drawString(terms[0].substring(2), x + e5, y + vinterline / 2);
		}
		int maxlen = vMetrics.stringWidth("(");
		wid.set(inode, maxlen + e5);
		widB.set(inode, maxlen + oMetrics.stringWidth(terms[0].substring(2)));
		hei.set(inode, vinterline / 2);

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);
			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			// The center of the arc is the center of the rectangle whose origin
			// is (x, y)
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// add a tail if initial node
		if (inode == 0)
		{
			g.setColor(pen.get("penF").color);
			g.setStroke(new BasicStroke(pen.get("penF").stroke));
			g.drawLine(x - e10, y, x, y);
		}

		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
				drawConnection(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
		}
	}

	private void paintCommentNode2(int inode, Graphics2D g)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);

		String[] terms = inLineLabel(label.get(inode), outputRef);
		output = outputRef.argvalue;
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		Color b = cColor;

		FontMetrics oMetrics = g.getFontMetrics(ofont);
		FontMetrics cMetrics = g.getFontMetrics(cfont);

		hei.set(inode, interline * terms.length / 2);
		int line0 = y - hei.get(inode);
		int maxlen = 0;
		for (int i = 0; i < terms.length; i++)
		{
			int w;
			g.setColor(b);
			g.setFont(cfont);
			g.drawString(terms[i], x, line0 + interline * (i + 1));
			w = cMetrics.stringWidth(terms[i]);
			if (w > maxlen)
				maxlen = w;
		}
		maxlen += e5;
		wid.set(inode, maxlen + e5);
		if (output != null)
		{
			g.setFont(ofont);
			g.setColor(b);
			g.drawString(output, x, line0 + interline * (terms.length + 1));
			if (oMetrics.stringWidth("/" + output) > maxlen)
				widB.set(inode, oMetrics.stringWidth("/" + output));
			else
				widB.set(inode, maxlen);
		}
		else
			widB.set(inode, maxlen);

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);

			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			// The center of the arc is the center of the rectangle whose origin
			// is (x, y)
			g.drawArc(rect1.x - rect1.width / 2, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x - rect2.width / 2, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x - rect3.width / 2, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x - rect4.width / 2, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}

		// still have to connect to outgoing nodes if necessary
		// connections
		for (int i = 0; i < (child.get(inode)).size(); i++)
		{
			int jnode = (child.get(inode)).get(i);
			if (inode == jnode)
				drawLoop(inode, g);
			else
			{
				drawConnection2(inode, wid.get(inode), hei.get(inode), (hei.get(jnode)), jnode, g);
			}
		}
	}

	private void paintTerminalNode(int inode, Graphics2D g)
	{
	
		x = (int) ((posX.get(inode)) * scale);
		y = (int) ((posY.get(inode)) * scale);

		g.setColor(pen.get("pen").color);
		g.setStroke(new BasicStroke(pen.get("pen").stroke));

		if (inDebugNode == null || !debugInvisible || inode >= inDebugNode.size()
				|| (!(Boolean) inDebugNode.get(inode)))
		{
			g.draw(new Ellipse2D.Double(x, y - interline / 2, interline, interline));
		}
		g.drawLine(x, y, x + interline, y);
		g.drawLine(x + interline / 2, y - interline / 2, x + interline / 2, y + interline);
		g.drawLine((int) (x + interline * .30F), y + interline, (int) (x + interline * .70F), y + interline);
		hei.set(inode, interline / 2);
		wid.set(inode, interline);
		widB.set(inode, -1); // never used

		// add marks if node is selected
		if (selected != null && selected.get(inode))
		{
			Rectangle rect1 = new Rectangle(x - 10, y - hei.get(inode) - 10, 10, 10);
			Rectangle rect2 = new Rectangle(x - 10, y + hei.get(inode), 10, 10);
			Rectangle rect3 = new Rectangle(x + wid.get(inode), y - hei.get(inode) - 10, 10, 10);
			Rectangle rect4 = new Rectangle(x + wid.get(inode), y + hei.get(inode), 10, 10);
			g.setColor(pen.get("penS").color);
			g.setStroke(new BasicStroke(pen.get("penS").stroke));
			g.drawArc(rect1.x, rect1.y + rect1.height / 2, rect1.width, rect1.height, 0, 360);
			g.drawArc(rect2.x, rect2.y + rect2.height / 2, rect2.width, rect2.height, 0, 360);
			g.drawArc(rect3.x, rect3.y + rect3.height / 2, rect3.width, rect3.height, 0, 360);
			g.drawArc(rect4.x, rect4.y + rect4.height / 2, rect4.width, rect4.height, 0, 360);
			// reset color
			g.setColor(pen.get("pen").color);
		}
	}

	public void renameEmbeddedGraphInLabel(int inode, String oldname, String newname)
	{
		String output = null;
		RefObject<String> outputRef = new RefObject<String>(output);
		String lbl = label.get(inode);
		String[] terms = inLineLabel(lbl, outputRef);
		output = outputRef.argvalue;

		boolean modif = false;
		for (int i = 0; i < terms.length; i++)
			if (terms[i].charAt(0) == ':')
			{
				if (terms[i].substring(1) == oldname)
				{
					terms[i] = ":" + newname;
					modif = true;
				}
			}
		if (modif)
			label.set(inode, outLineLabel(terms, output));
	}

	private String outLineLabel(String[] terms, String output)
	{
		StringBuilder res = new StringBuilder();
		res.append(terms[0]);
		for (int i = 1; i < terms.length; i++)
			res.append('\n' + terms[i]);
		return res.toString();
	}

	/**
	 * Function clears debugging lists of current Graph.
	 */

	public void stopDebug()
	{
		inDebugNode = null;
		inDebugConnection = null;
	}

	/**
	 * Function sets debugging lists of current Graph.
	 * 
	 * @param trace
	 *            - graph's trace
	 */

	public void setDebug(ArrayList<Object> trace)
	{
		// set InDebugNode
		inDebugNode = new ArrayList<Boolean>();

		for (int i = 0; i < selected.size(); i++)
			inDebugNode.add(false);

		for (int i = 0; i < trace.size(); i++)
		{
			if (trace.get(i) instanceof String)
				continue;

			int iNode = (Integer) trace.get(i);

			if (iNode == -1)
				continue;

			inDebugNode.set(iNode, true);
		}

		// set InDebugConnection
		inDebugConnection = new ArrayList<Integer>();

		for (int i = 0; i < selected.size(); i++)
			inDebugConnection.add(-1);

		int lastTrace = 0;

		for (int i = 0; i < trace.size(); i++)
		{
			if (trace.get(i) instanceof String)
				continue;

			int iNode = (Integer) trace.get(i);

			if (iNode == -1)
				continue;

			inDebugConnection.set(lastTrace, iNode);
			lastTrace = iNode;
		}
	}

	private String protectChars(String label)
	{
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < label.length(); i++)
		{
			char characterLabel = label.charAt(i);

			if (characterLabel == '"')
				res.append("\\\"");

			else if (characterLabel == '+')
				res.append("\\+");
			else if (characterLabel == '\n')
			{
				if (i + 1 < label.length() && label.charAt(i + 1) == '/')
				{
					res.append("/");
					i++;
					continue;
				}
				else res.append("+");
			}
			else res.append(characterLabel);
		}
		return res.toString();
	}

	private String unProtectChars(StringBuilder label)
	{
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < label.length(); i++)
		{
			char characterLabel = label.charAt(i);

			if (characterLabel == '+')
				res.append("\n");
			else if (characterLabel == '/')
				res.append("\n/");
			else if (characterLabel == '\\')
			{
				if (i + 1 < label.length() && label.charAt(i + 1) == '"')
				{
					res.append("\"");
					i++;
					continue;
				}
				else if (i + 1 < label.length() && label.charAt(i + 1) == '+')
				{
					res.append("+");
					i++;
					continue;
				}
			}
			else res.append(characterLabel);
		}
		return res.toString();
	}

	public void saveONooJGraph(String fullname, BufferedWriter writer) // Save Open Source NooJ Graph
	{
		try
		{
			writer.write("#");
			writer.write('\n');
			writer.write(name);
			writer.write('\n');

			// number of nodes
			writer.write(Integer.toString(label.size()));
			writer.write('\n');

			for (int inode = 0; inode < label.size(); inode++)
			{
				writer.write("\"");
				writer.write(protectChars(label.get(inode)));
				writer.write("\" ");

				writer.write((posX.get(inode)).toString() + " ");
				writer.write((posY.get(inode)).toString() + " ");

				// connections
				ArrayList<Integer> lc = child.get(inode);
				writer.write(Integer.toString(lc.size()) + " ");
				for (int ichild = 0; ichild < lc.size(); ichild++)
				{
					int c = lc.get(ichild);
					writer.write(Integer.toString(c) + " ");
				}

				writer.write('\n');
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public boolean loadONooJ(BufferedReader reader) // Load OpenNooJ Graph
	{
		// Header
		String line = "";
		try
		{
			line = reader.readLine();
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (line == "" || line.charAt(0) != '#')
		{
			return false;
		}

		// Name
		try
		{
			line = reader.readLine();
			this.name = line;
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// Number of nodes
		try
		{
			line = reader.readLine();
			int NbOfNodes = Integer.parseInt(line);

			// graph network
			String sep = " ";
			for (int inode = 0; inode < NbOfNodes; inode++)
			{
				line = reader.readLine();

				// label
				StringBuilder label = new StringBuilder();
				int istrt, len;
				for (istrt = 0; line.charAt(istrt) != '"'; istrt++)
					;
				istrt++;
				for (len = 0; istrt + len < line.length() && line.charAt(istrt + len) != '"';)
				{
					if (line.charAt(istrt + len) == '\\')
					{
						label.append('\\');
						label.append(line.charAt(istrt + len + 1));
						len += 2;
					}
					else
					{
						label.append(line.charAt(istrt + len));
						len ++;
					}
				}

				if (istrt + len >= line.length())
				{
					// Debug.WriteLine("NooJ: File format Error: cannot parse line " + line);
					return false;
				}

				// conversion
				this.label.add(unProtectChars(label));
				len++;

				this.selected.add(false);

				// connections
				while (Character.isWhitespace(line.charAt(istrt + len)))
					len++;
				String connections = line.substring(istrt + len);
				String[] fields = connections.split(sep);

				// fields[0] is x, fields[1] is y, fields[2] is nb of children
				int x = 0, y = 0, n = 0;
				try
				{
					x = Integer.parseInt(fields[0]);
				}
				catch (NumberFormatException e)
				{
				}

				try
				{
					y = Integer.parseInt(fields[1]);
				}
				catch (NumberFormatException e)
				{
				}
				try
				{
					n = Integer.parseInt(fields[2]);
				}
				catch (NumberFormatException e)
				{
				}

				this.posX.add(x);
				this.posY.add(y);
				this.hei.add(-1);
				this.wid.add(-1);
				this.widB.add(-1);
				int NbOfChildren = n;
				this.child.add(new ArrayList<Integer>());

				for (int ichild = 0; ichild < NbOfChildren; ichild++)
				{
					n = 0;
					try
					{
						n = Integer.parseInt(fields[ichild + 3]);
					}
					catch (NumberFormatException e)
					{
					}
					this.child.get(inode).add(n);
				}
			}
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	public static String convert(StringBuilder label)
	{
		StringBuilder input = new StringBuilder();
		int iinput;
		for (iinput = 0; iinput < label.length() && label.charAt(iinput) != '/'; iinput++)
		{
			if (label.charAt(iinput) == '\\')
			{
				if (label.charAt(iinput + 1) != '+') // '\+' does no longer need to be protected
					input.append(label.charAt(iinput));
				iinput++;
				input.append(label.charAt(iinput));
			}
			else if (label.charAt(iinput) == '+')
			{
				input.append('\n');
			}
			else if (label.charAt(iinput) == '"')
			{
				input.append('"');
				for (iinput++; iinput < label.length() && label.charAt(iinput) != '"'; iinput++)
					input.append(label.charAt(iinput));
				if (iinput < label.length())
					input.append(label.charAt(iinput));
				else
					input.append('"');
			}
			else if (label.charAt(iinput) == '<')
			{
				// the + and " in the < ... > are not meta-characters
				input.append(label.charAt(iinput));
				for (iinput++; iinput < label.length() && label.charAt(iinput) != '>'; iinput++)
					input.append(label.charAt(iinput));
				if (iinput < label.length())
					input.append(label.charAt(iinput));
				else
					input.append('>');
			}
			else
				input.append(label.charAt(iinput));
		}

		return input.toString();
	}

	public int[] getArrHei()
	{
		return arrHei;
	}

	public int[] getArrWid()
	{
		return arrWid;
	}

	public int[] getArrPosX()
	{
		return arrPosX;
	}

	public int[] getArrPosY()
	{
		return arrPosY;
	}

	public String[] getArrLabel()
	{
		return arrLabel;
	}

	public int[][] getArrChild()
	{
		return arrChild;
	}

	public ArrayList<ArrayList<Object>> getExtraParams()
	{
		return extraParams;
	}

	public void setArrHei(int[] arrHei)
	{
		this.arrHei = arrHei;
	}

	public void setArrWid(int[] arrWid)
	{
		this.arrWid = arrWid;
	}

	public void setArrPosX(int[] arrPosX)
	{
		this.arrPosX = arrPosX;
	}

	public void setArrPosY(int[] arrPosY)
	{
		this.arrPosY = arrPosY;
	}

	public void setArrLabel(String[] arrLabel)
	{
		this.arrLabel = arrLabel;
	}

	public void setArrChild(int[][] arrChild)
	{
		this.arrChild = arrChild;
	}

	public void setExtraParams(ArrayList<ArrayList<Object>> extraParams)
	{
		this.extraParams = extraParams;
	}
}