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

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.helper.ParameterCheck;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

import com.itextpdf.text.Font.FontStyle;

/**
 * 
 * @author Silberztein Max
 * 
 */
public class Grammar implements Serializable
{
	private static final long serialVersionUID = 2937834300602472387L;

	public ArrayList<Graph> graphs; // list of graphs
	transient public HashMap<String, Gram> grams; // (string) name => (Gram) compiled graph
	transient public String fullName; // only used for graph drawing and error messages during lexical/syntactic
										// analysis
	transient private Engine engine; // only used for graph drawing and error messages during lexical/syntactic analysis
	transient private boolean isTextual;

	public int windowHeight = 0, windowWidth = 0;
	public GramType gramType = net.nooj4nlp.engine.GramType.forValue(0);;
	public String author;
	public String institution;
	public int lockType;
	public String checkText = null;
	public String iLanguage, oLanguage; // input/output language

	transient private Language iLan; // default input language (used for lots of matching functions to avoid recreating
										// a new Lan from ILanguage)
	private ArrayList<Object> extraParams; // for the future

	public String iFontName;
	public float iFontSize;
	public int iFontStyle;
	public String oFontName;
	public float oFontSize;
	public int oFontStyle;
	public String cFontName;
	public float cFontSize;
	public int cFontStyle;

	public Color cColor;
	public Color aColor, bColor, fColor, sColor, vColor; // auxiliary/background/Comment/Frame/Selection/Variable
															// node color
	public boolean dispFrame; // display frame or not
	public boolean dispFile; // display file name
	public boolean dispDir; // display file name
	public boolean dispDate; // display last modification date
	public boolean dispBox = false; // display a box for each node
	public boolean dispState = false; // display circles (true) or arrows (false) for <E> nodes
	public boolean dispAuthor = false; // display Author
	public boolean dispInstitution = false; // display Institution
	public boolean dispGrid = false;

	public static boolean isItTextual(String filePath)
	{
		ParameterCheck.mandatoryString("filePath", filePath);

		BufferedReader reader = null;
		String firstLine;

		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
			

			// read header and get language
			firstLine = reader.readLine();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							Constants.ERROR_MESSAGE_TITLE_CLOSE_READER_FILE, Constants.NOOJ_ERROR,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		return (firstLine.length() == 9 && firstLine.substring(0, 6).equals("# NooJ"));
	}

	// Constructors

	public Grammar()
	{
		this.graphs = new ArrayList<Graph>();
		this.grams = new HashMap<String, Gram>();
	}

	Grammar(Language ilan, GramType gt, Engine eng)
	{
		this.iLan = ilan;
		this.gramType = gt;
		this.engine = eng;
	}

	public Grammar(GramType gt, String author, String institution, String password, int locktype, String ilanguage,
			String olanguage, Preferences preferences)
	{
		this.gramType = gt;
		this.author = author;
		this.institution = institution;
		this.lockType = locktype;
		this.iLanguage = ilanguage;
		this.iLan = new Language(iLanguage);
		this.oLanguage = olanguage;
		this.graphs = new ArrayList<Graph>();
		this.grams = null;

		if (preferences != null) // could be null if called from tokenizer
		{
			this.iFontName = preferences.IFont.getName();
			this.iFontSize = preferences.IFont.getSize2D();
			this.iFontStyle = preferences.IFont.getStyle();

			this.oFontName = preferences.OFont.getName();
			this.oFontSize = preferences.OFont.getSize2D();
			this.oFontStyle = preferences.OFont.getStyle();

			this.cFontName = preferences.CFont.getName();
			this.cFontSize = preferences.CFont.getSize2D();
			this.cFontStyle = preferences.CFont.getStyle();

			this.aColor = preferences.AColor;
			this.bColor = preferences.BColor;
			this.cColor = preferences.CColor;
			this.fColor = preferences.FColor;
			this.sColor = preferences.SColor;
			this.vColor = preferences.VColor;

			this.dispFrame = preferences.DispFrame;
			this.dispFile = preferences.DispFile;
			this.dispDir = preferences.DispDir;
			this.dispDate = preferences.DispDate;
		}
	}

	// Import INTEX Graphs
	public static Grammar importWithAllEmbeddedGraphs(String fullname, GramType gt, String ilanguage, String olanguage,
			String encodingcode, Preferences preferences)
	{
		Grammar grm = new Grammar(gt, null, null, null, 0, ilanguage, olanguage, preferences);
		grm.fullName = fullname;
		Graph grf = Graph.loadIntex(grm, fullname, encodingcode, gt);
		if (grf == null)
		{
			return null;
		}

		grf.wholeGrammar = grm;
		grf.name = FilenameUtils.removeExtension(fullname);
		grm.graphs.add(grf);
		ArrayList<String> recGraph = new ArrayList<String>();
		recGraph.add(grf.name);

		File f = new File(fullname);
		String dname = f.getParentFile().getAbsolutePath();
		if (!grm.importEmbeddedGraphs(dname, grf, recGraph, encodingcode, gt))
		{
			return null;
		}
		grm.gramType = gt;
		return grm;
	}

	public final boolean importEmbeddedGraphs(String dname, Graph grf, ArrayList<String> recGraph, String encodingcode,
			GramType gt)
	{
		// import all embedded graphs
		int nbofnodes = grf.label.size();
		ArrayList<String> embeddedGraphs = new ArrayList<String>();
		for (int inode = 0; inode < nbofnodes; inode++)
		{
			if (inode == 1)
			{
				continue;
			}
			if (grf.commentNode(inode) || grf.areaNode(inode))
			{
				continue;
			}
			ArrayList<Integer> child = grf.child.get(inode);
			if (child.isEmpty())
			{
				continue;
			}

			String output = null;
			RefObject<String> tempRef_output = new RefObject<String>(output);
			String[] terms = Graph.inLineLabel(grf.label.get(inode), tempRef_output);
			output = tempRef_output.argvalue;
			for (int iterm = 0; iterm < terms.length; iterm++)
			{
				if (terms[iterm].length() > 1 && terms[iterm].charAt(0) == ':')
				{
					String gname = terms[iterm].substring(1);
					int index = embeddedGraphs.indexOf(gname);
					if (index != -1) // already present in this graph
					{
						continue;
					}
					embeddedGraphs.add(gname);
					index = recGraph.indexOf(gname);
					if (index != -1) // (recursive call) already present among the ancestors
					{
						continue;
					}
					recGraph.add(gname);
					index = -1;
					Graph grf2;
					for (int ig = 0; ig < graphs.size(); ig++)
					{
						grf2 = graphs.get(ig);
						if (grf2 != null && grf2.name.equals(gname))
						{
							index = ig;
							break;
						}
					}
					if (index != -1) // already present in the grammar
					{
						continue;
					}
					File targetFile = new File(dname, gname + ".grf");
					String fullgname = targetFile.getPath();
					if (!targetFile.isFile())
					{

					}
					else
					{
						grf2 = Graph.loadIntex(this, fullgname, encodingcode, gt);
						if (grf2 == null)
						{

						}
						else
						{
							if (!importEmbeddedGraphs(dname, grf2, recGraph, encodingcode, gt))
							{
								return false;
							}
							graphs.add(grf2);
						}
					}
				}
			}
		}
		return true;
	}

	// Serialization

	private final void getDataFromSerialization()
	{
		
	}

	private final void storeDataForSerialization()
	{
		extraParams = null; // NooJ V1
	}

	public static Grammar loadTextual(String fullname, GramType gt, RefObject<String> errmessage)
	{
		Grammar grammar = null;

		errmessage.argvalue = null;
		String ilanguagename = null, olanguagename = null;
		RefObject<String> tempRef_ilanguagename = new RefObject<String>(ilanguagename);
		RefObject<String> tempRef_olanguagename = new RefObject<String>(olanguagename);

		Regexps rs = Regexps.load(fullname, errmessage, tempRef_ilanguagename, tempRef_olanguagename);
		ilanguagename = tempRef_ilanguagename.argvalue;
		olanguagename = tempRef_olanguagename.argvalue;
		if (rs == null)
		{
			errmessage.argvalue = "Invalid statements:\n" + errmessage.argvalue;
			Dic.writeLog(errmessage.argvalue);
			return null;
		}

		if (rs.grammar.grams.isEmpty())
		{
			errmessage.argvalue = "Grammar is empty: " + errmessage.argvalue;
			Dic.writeLog(errmessage.argvalue);
			return null;
		}
		Gram grm;

		Set<String> keySet = rs.grammar.grams.keySet();
		for (String expname : keySet)
		{
			grm = rs.grammar.grams.get(expname);
			if (grm == null)
			{
				errmessage.argvalue = "Error in grammar rule " + expname;
				Dic.writeLog(errmessage.argvalue);
				return null;
			}
			if (grm.vocabIn == null)
			{
				grm.prepareForParsing();
			}
		}
		grammar = rs.grammar;
		if (grammar != null)
		{
			grammar.gramType = gt;
			grammar.isTextual = true;
			grammar.iLanguage = ilanguagename;
			grammar.iLan = new Language(ilanguagename);
			grammar.oLanguage = olanguagename;
		}
		return grammar;
	}


	public final void save(String fullname)
	{
		this.fullName = fullname;
		// serialization
		storeDataForSerialization();
		for (int i = 0; i < graphs.size();)
		{
			Graph grf = graphs.get(i);
			if (grf == null || grf.name == null || grf.name.equals(""))
			{
				graphs.remove(i);
			}
			else
			{
				grf.storeDataForSerialization0();
				i++;
			}
		}
		FileOutputStream fs = null;
		try
		{

			File file = new File(fullname);
			// Check if the directory path exists - if not, create it
			String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
			File filePath = new File(path);
			if (!filePath.exists())
			{
				filePath.mkdirs();
			}
			fs = new FileOutputStream(fullname);
			ObjectOutputStream serializer = new ObjectOutputStream(fs);
			serializer.writeObject(this);
			serializer.close();
		}
		catch (Exception e)
		{
			if (fs != null)
			{
				try
				{
					fs.close();
				}
				catch (IOException e1)
				{
				}
			}
		}
	}

	private String compileAllGraphical(Engine eng)
	{
		grams = new HashMap<String, Gram>();
		Language Lan = new Language(iLanguage);

		this.engine = eng;

		for (int ig = 0; ig < graphs.size(); ig++)
		{
			Graph grf = graphs.get(ig);

			grf.wholeGrammar = this;
			ArrayList<String> aVocab = new ArrayList<String>();
			aVocab.add("<E>");
			HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
			hVocab.put("<E>", 0);
			Gram grm = grf.compile(Lan, gramType, aVocab, hVocab, this.engine);
			if (grm != null)
			{
				grm.vocab = aVocab;
				hVocab = null;
				grm.prepareForParsing();
				if (grams.containsKey(grf.name))
				{
					continue;
				}
				else
				{
					grams.put(grf.name, grm);
				}
			}
		}
		return null;
	}

	private String compileAllTextual(Engine eng)
	{
		this.engine = eng;

		Set<String> keySet = this.grams.keySet();
		for (String expname : keySet)
		{
			Gram grm = this.grams.get(expname);
			if (grm == null)
			{
				return ("Error in rule " + expname);
			}
			if (grm.vocabIn == null)
			{
				grm.prepareForParsing();
			}
		}
		return null;
	}

	/**
	 * Function calls responsible compiling function.
	 * 
	 * @param engine
	 *            - active engine
	 * @return - error message, if any
	 */

	public String compileAndComputeFirst(Engine engine)
	{
		if (this.isTextual)
			return compileAllTextual(engine);
		else
			return compileAllGraphical(engine);
	}

	final String addGrams(Grammar cgrammar)
	{
		Set<String> keySet = cgrammar.grams.keySet();
		for (String expname : keySet)
		{
			Gram grm = cgrammar.grams.get(expname);
			if (this.grams.containsKey(expname))
			{
				return "Paradigm " + expname + " is defined more than once.";
			}
			this.grams.put(expname, grm);
		}
		return null;
	}

	public final String compileAll(Engine eng)
	{
		if (this.isTextual)
		{
			return compileAllTextual(eng);
		}
		else
		{
			return compileAllGraphical(eng);
		}
	}

	transient private static String epsilon; // "<E>"
	transient private static ArrayList<String> lepsilon; // {"<E>"}
	transient private static ArrayList<Object> lnull; // {null}
	transient private static ArrayList<Integer> l0; // {0}
	transient private static ArrayList<Integer> l1; // {1}
	transient private static HashMap<String, String[]> hlemmas; // lemmas expansion

	private static void setConstants()
	{
		// several constants
		epsilon = "<E>";
		lepsilon = new ArrayList<String>();
		lepsilon.add(epsilon);
		lnull = new ArrayList<Object>();
		lnull.add(null);
		l0 = new ArrayList<Integer>();
		l0.add(0);
		l1 = new ArrayList<Integer>();
		l1.add(1);

		// various initializations
		hlemmas = null;
	}

	// Morphological Parsing

	private StringBuilder getValue(String varname, ArrayList<String> input, int ipos)
	{
		// scan the input to get the value of the variable
		int index = -1;
		for (int iinput = ipos; iinput >= 0; iinput--)
		{
			// look for $(varname
			String si = input.get(iinput);
			if (si == null || si.length() == 0)
			{
				continue;
			}
			if (si.equals("$(" + varname))
			{
				index = iinput;
				break;
			}
		}
		if (index == -1)
		{
			return null;
		}

		StringBuilder res = new StringBuilder();

		// look for corresponding $)
		int reclevel = 0;
		String sj = null;
		for (int jinput = index + 1; jinput < input.size(); jinput++)
		{
			sj = input.get(jinput);
			if (sj == null || sj.length() == 0)
			{
				continue;
			}
			if (sj.length() >= 2 && sj.charAt(0) == '$' && sj.charAt(1) == '(')
			{
				reclevel++;
				continue;
			}
			else if (sj.length() >= 2 && sj.charAt(0) == '$' && sj.charAt(1) == ')')
			{
				if (reclevel == 0)
				{
					break;
				}
				reclevel--;
			}
			else
			{
				res.append(sj);
			}
		}
		if (sj.length() > 2)
		{
			res = new StringBuilder(sj.substring(2));
		}
		return res;
	}

	public final StringBuilder processVariablesInOutputs(ArrayList<String> input, ArrayList<String> output)
	{
		StringBuilder result = new StringBuilder();
		for (int ipos = 0; ipos < output.size(); ipos++)
		{
			String so = output.get(ipos);
			if (so == null || so.equals(""))
			{
				continue;
			}

			// replace all variable calls in output with their content found in input
			StringBuilder res = new StringBuilder();
			for (int i = 0; i < so.length();)
			{
				if (so.charAt(i) == '\\')
				{
					res.append(so.charAt(i));
					res.append(so.charAt(i + 1));
					i += 2;
					continue;
				}
				else if (so.charAt(i) != '$')
				{
					res.append(so.charAt(i));
					i++;
					continue;
				}

				// get variable name
				int j;
				for (i++, j = 0; i + j < so.length() && !Character.isWhitespace(so.charAt(i + j))
						&& so.charAt(i + j) != '#' && so.charAt(i + j) != '=' && so.charAt(i + j) != '+'
						&& so.charAt(i + j) != ',' && so.charAt(i + j) != '$' && so.charAt(i + j) != '>'
						&& so.charAt(i + j) != '}'; j++)
				{
					;
				}
				String varname = so.toString().substring(i, i + j);
				if (i + j < so.length() && so.charAt(i + j) == '#')
				{
					j++;
				}

				StringBuilder val = getValue(varname, input, ipos);
				if (val == null)
				{
					res.append("$" + varname);
				}
				else
				{
					res.append(val);
				}
				i += j;
			}
			// get rid of all '#'
			for (int i = 0; i < res.length(); i++)
			{
				if (res.charAt(i) != '#')
				{
					result.append(res.charAt(i));
				}
			}
		}
		return result;
	}

	private boolean morphoMatchTermLabel(String ilabel, String token, int ipos, RefObject<Integer> length)
	{
		length.argvalue = 0;
		if (ilabel.equals("<E>"))
		{
			return true;
		}
		if (ilabel.charAt(0) == '$'
				&& ((ilabel.length() > 2 && ilabel.charAt(1) == '(') || (ilabel.length() >= 2 && ilabel.charAt(1) == ')')))
		{
			return true;
		}
		if (ipos >= token.length())
		{
			return false;
		}
		boolean match = false;
		if (ilabel.charAt(0) == '<')
		{
			if (ilabel.equals("<L>")) // any letter
			{
				match = Language.isLetter(token.charAt(ipos));
			}
			else if (ilabel.equals("<U>")) // uppercase letter
			{
				match = Character.isUpperCase(token.charAt(ipos));
			}
			else if (ilabel.equals("<W>")) // lowercase letter
			{
				match = Character.isLowerCase(token.charAt(ipos));
			}
			else if (ilabel.equals("<A>")) // accented letter
			{
				match = Language.isAccented(token.charAt(ipos));
			}
			else if (ilabel.equals("<N>")) // unaccented letter
			{
				match = !Language.isAccented(token.charAt(ipos));
			}
			else if (ilabel.equals("<C>")) // consonantic letter
			{
				match = !Language.isVowel(token.charAt(ipos));
			}
			else if (ilabel.equals("<V>")) // vowel letter
			{
				match = Language.isVowel(token.charAt(ipos));
			}
			else
			// TODO: each language should be able to add their own symbols - C#'s
			{
			}
			length.argvalue = 1;
			return match;
		}
		else if (ilabel.charAt(0) == '"') // protected string
		{
			for (int i = 1; i < ilabel.length() - 1; i++)
			{
				if (token.charAt(ipos + i - 1) != ilabel.charAt(i))
				{
					return false;
				}
			}
			length.argvalue = ilabel.length() - 2;
			return true;
		}
		else if (ilabel.length() > 1) // string
		{
			if (token.length() - ipos < ilabel.length())
			{
				return false;
			}
			for (int i = 0; i < ilabel.length(); i++)
			{
				if (token.charAt(ipos + i) != ilabel.charAt(i))
				{
					return false;
				}
			}
			length.argvalue = ilabel.length();
			return true;
		}
		else
		// character
		{
			// TODO: match with uppercase, unaccented, etc. - C#'s
			match = iLan.doLettersMatch(token.charAt(ipos), ilabel.charAt(0));
			length.argvalue = 1;
			return match;
		}
	}

	private int morphoMatchLabel(String ilabel, String olabel, String text, int ipos,
			RefObject<ArrayList<Integer>> lengths, RefObject<ArrayList<ArrayList<String>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> outputs)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		outputs.argvalue = null;

		if (ilabel.charAt(0) == ':') // recursive call
		{
			Gram grm = grams.get(ilabel.substring(1));
			if (grm == null) // not compiled yet
			{
				Graph grf = null;
				for (int i = 0; i < this.graphs.size(); i++)
				{
					grf = this.graphs.get(i);
					if (grf != null && grf.name.equals(ilabel.substring(1)))
					{
						break;
					}
				}
				if (grf != null)
				{
					ArrayList<String> aVocab = new ArrayList<String>();
					aVocab.add("<E>");
					HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
					hVocab.put("<E>", 0);
					grm = grf.compile(iLan, this.gramType, aVocab, hVocab, engine);
					if (grm != null)
					{
						grm.vocab = aVocab;
						hVocab = null;
						grm.prepareForParsing();
					}
					grams.put(grf.name, grm);
				}
				else
				{
					return 0;
				}
			}
			int da = morphoMatch(text.substring(ipos), grm, false, lengths, inputs, outputs);
			// TODO: if the auxiliary node has an output: add <E>/output to each trace - C#'s
			return da;
		}
		else
		// simple terminal match
		{
			int l = 0;
			RefObject<Integer> tempRef_l = new RefObject<Integer>(l);
			boolean tempVar = morphoMatchTermLabel(ilabel, text, ipos, tempRef_l);
			l = tempRef_l.argvalue;
			if (tempVar)
			{
				lengths.argvalue = new ArrayList<Integer>();
				lengths.argvalue.add(l);

				// compute the input trace
				ArrayList<String> i = new ArrayList<String>();
				if (ilabel.charAt(0) == '$' && (ilabel.charAt(1) == '(' || ilabel.charAt(1) == ')'))
				{
					// insert the variable marker
					i.add(ilabel);
				}
				else
				// insert the input text
				{
					i.add(text.substring(ipos, ipos + l));
				}
				inputs.argvalue = new ArrayList<ArrayList<String>>();
				inputs.argvalue.add(i);
				ArrayList<String> o = new ArrayList<String>();
				o.add(olabel);
				outputs.argvalue = new ArrayList<ArrayList<String>>();
				outputs.argvalue.add(o);
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

	private int morphoMatchLabel(int graphnode, String ilabel, String olabel, String text, int ipos,
			RefObject<ArrayList<Integer>> lengths, RefObject<ArrayList<ArrayList<String>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> outputs, RefObject<ArrayList<ArrayList<Object>>> nodes)
	{
		lengths.argvalue = null;
		inputs.argvalue = outputs.argvalue = null;
		nodes.argvalue = null;

		if (ilabel.charAt(0) == ':') // recursive call
		{
			String graphname = ilabel.substring(1);
			Gram grm = grams.get(graphname);
			if (grm == null) // not compiled yet
			{
				Graph grf = null;
				for (int i = 0; i < this.graphs.size(); i++)
				{
					grf = this.graphs.get(i);
					if (grf != null && grf.name.equals(graphname))
					{
						break;
					}
				}
				if (grf != null)
				{
					ArrayList<String> aVocab = new ArrayList<String>();
					aVocab.add("<E>");
					HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
					hVocab.put("<E>", 0);
					grm = grf.compile(iLan, this.gramType, aVocab, hVocab, engine);
					if (grm != null)
					{
						grm.vocab = aVocab;
						hVocab = null;
						grm.prepareForParsing();
					}
					grams.put(grf.name, grm);
				}
				else
				{
					return 0;
				}
			}
			int da = morphoMatch(graphname, text.substring(ipos), grm, lengths, inputs, outputs, nodes);
			if (da > 0)
			{
				for (int isol = 0; isol < da; isol++)
				{
					// do not touch length
					ArrayList<String> i2 = inputs.argvalue.get(isol);
					i2.add(0, "");
					ArrayList<String> o2 = outputs.argvalue.get(isol);
					o2.add(0, olabel);
					ArrayList<Object> n2 = nodes.argvalue.get(isol);
					n2.add(0, graphnode);
				}
			}
			return da;
		}
		else
		// simple terminal match
		{
			int l = 0;
			RefObject<Integer> tempRef_l = new RefObject<Integer>(l);
			boolean tempVar = morphoMatchTermLabel(ilabel, text, ipos, tempRef_l);
			l = tempRef_l.argvalue;
			if (tempVar)
			{
				lengths.argvalue = new ArrayList<Integer>();
				lengths.argvalue.add(l);

				// compute the input trace
				ArrayList<String> i = new ArrayList<String>();
				if (ilabel.charAt(0) == '$' && (ilabel.charAt(1) == '(' || ilabel.charAt(1) == ')'))
				{
					// insert the variable marker
					i.add(ilabel);
				}
				else
				// insert the input text
				{
					i.add(text.substring(ipos, ipos + l));
				}
				inputs.argvalue = new ArrayList<ArrayList<String>>();
				inputs.argvalue.add(i);

				ArrayList<String> o = new ArrayList<String>();
				o.add(olabel);
				outputs.argvalue = new ArrayList<ArrayList<String>>();
				outputs.argvalue.add(o);

				ArrayList<Object> n = new ArrayList<Object>();
				n.add(graphnode);
				nodes.argvalue = new ArrayList<ArrayList<Object>>();
				nodes.argvalue.add(n);
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

	public static StringBuilder computeInput(ArrayList<String> inputs)
	{
		StringBuilder itrace = new StringBuilder();
		for (int i = 0; i < inputs.size(); i++)
		{
			String si = inputs.get(i);
			if (si != null && !si.equals("") && !si.equals("<E>") && !si.equals("$)")
					&& !(si.charAt(0) == '$' && si.charAt(1) == '('))
			{
				itrace.append(si);
			}
		}
		return itrace;
	}

	private final int morphoMatch(String text, Gram grm, boolean totheend, RefObject<ArrayList<Integer>> sollengths,
			RefObject<ArrayList<ArrayList<String>>> solinputs, RefObject<ArrayList<ArrayList<String>>> soloutputs)
	{
		if (epsilon==null)
		{
			setConstants();
		}

		sollengths.argvalue = null;
		solinputs.argvalue = soloutputs.argvalue = null;

		Stack<MTrace> stack = new Stack<MTrace>();
		stack.push(new MTrace());
		while (stack.size() > 0)
		{
			MTrace curtrc = stack.pop();

			State state = grm.states.get(curtrc.Statenb);
			int ipos = curtrc.Pos;
			ArrayList<String> inputs = curtrc.Inputs;
			ArrayList<String> outputs = curtrc.Outputs;

			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int dst = state.Dests.get(itrans);
				int lbl = state.IdLabels.get(itrans);
				String ilabel0 = grm.vocabIn.get(lbl);
				String olabel = grm.vocabOut.get(lbl);

				// check for variable in input
				String ilabel;
				if (engine.thereIsAVariableInLabel(ilabel0))
				{
					ilabel = engine.processVariableInMorphoLabel(ilabel0, inputs);
				}
				else
				{
					ilabel = ilabel0;
				}

				ArrayList<ArrayList<String>> i2 = null;
				ArrayList<ArrayList<String>> o2 = null;
				ArrayList<Integer> l2 = null;
				RefObject<ArrayList<Integer>> tempRef_l2 = new RefObject<ArrayList<Integer>>(l2);
				RefObject<ArrayList<ArrayList<String>>> tempRef_i2 = new RefObject<ArrayList<ArrayList<String>>>(i2);
				RefObject<ArrayList<ArrayList<String>>> tempRef_o2 = new RefObject<ArrayList<ArrayList<String>>>(o2);

				int da = morphoMatchLabel(ilabel, olabel, text, ipos, tempRef_l2, tempRef_i2, tempRef_o2);

				l2 = tempRef_l2.argvalue;
				i2 = tempRef_i2.argvalue;
				o2 = tempRef_o2.argvalue;
				for (int iamb = 0; iamb < da; iamb++)
				{
					int newpos = ipos + l2.get(iamb);

					// compute the new trace and insert it in the stack
					MTrace newtrc = new MTrace();
					newtrc.Statenb = dst;
					newtrc.Pos = newpos;
					newtrc.Inputs.addAll(inputs);
					newtrc.Inputs.addAll(i2.get(iamb));
					newtrc.Outputs.addAll(outputs);
					newtrc.Outputs.addAll(o2.get(iamb));

					if (newtrc.Inputs == null || newtrc.Inputs.size() < 1000)
					{
						stack.push(newtrc);
					}

					// reach terminal state ?
					if ((dst==1) 
							&& (!totheend || (newpos >= text.length()) || !Language.isLetter(text.charAt(newpos))))
					{
						if (sollengths.argvalue == null)
						{
							sollengths.argvalue = new ArrayList<Integer>();
							solinputs.argvalue = new ArrayList<ArrayList<String>>();
							soloutputs.argvalue = new ArrayList<ArrayList<String>>();
						}
						sollengths.argvalue.add(newpos);
						solinputs.argvalue.add(newtrc.Inputs);
						soloutputs.argvalue.add(newtrc.Outputs);
					}
				}
			}
		}
		if (sollengths.argvalue == null)
		{
			return 0;
		}
		else
		{
			return sollengths.argvalue.size();
		}
	}

	public final int morphoMatch(String graphname, String text, Gram grm, RefObject<ArrayList<Integer>> sollengths,
			RefObject<ArrayList<ArrayList<String>>> solinputs, RefObject<ArrayList<ArrayList<String>>> soloutputs,
			RefObject<ArrayList<ArrayList<Object>>> solnodes)
	{
		if (epsilon == null)
		{
			setConstants();
		}

		sollengths.argvalue = null;
		solinputs.argvalue = soloutputs.argvalue = null;
		solnodes.argvalue = null;

		Stack<MTrace> stack = new Stack<MTrace>();
		stack.push(new MTrace(0, graphname));
		while (stack.size() > 0)
		{
			MTrace curtrc = stack.pop();

			State state = grm.states.get(curtrc.Statenb);
			int ipos = curtrc.Pos;
			int graphnode = state.GraphNodeNumber;
			ArrayList<String> inputs = curtrc.Inputs;
			ArrayList<String> outputs = curtrc.Outputs;
			ArrayList<Object> nodes = curtrc.Nodes;

			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int dst = state.Dests.get(itrans);
				int lbl = state.IdLabels.get(itrans);
				String ilabel0 = grm.vocabIn.get(lbl);
				String olabel = grm.vocabOut.get(lbl);

				// check for variable in input
				String ilabel;
				if (engine.thereIsAVariableInLabel(ilabel0))
				{
					ilabel = engine.processVariableInMorphoLabel(ilabel0, inputs);
				}
				else
				{
					ilabel = ilabel0;
				}

				ArrayList<Integer> l2 = null;
				ArrayList<ArrayList<String>> i2 = null, o2 = null;
				ArrayList<ArrayList<Object>> n2 = null;
				RefObject<ArrayList<Integer>> tempRef_l2 = new RefObject<ArrayList<Integer>>(l2);
				RefObject<ArrayList<ArrayList<String>>> tempRef_i2 = new RefObject<ArrayList<ArrayList<String>>>(i2);
				RefObject<ArrayList<ArrayList<String>>> tempRef_o2 = new RefObject<ArrayList<ArrayList<String>>>(o2);
				RefObject<ArrayList<ArrayList<Object>>> tempRef_n2 = new RefObject<ArrayList<ArrayList<Object>>>(n2);

				int da = morphoMatchLabel(graphnode, ilabel, olabel, text, ipos, tempRef_l2, tempRef_i2, tempRef_o2,
						tempRef_n2);

				l2 = tempRef_l2.argvalue;
				i2 = tempRef_i2.argvalue;
				o2 = tempRef_o2.argvalue;
				n2 = tempRef_n2.argvalue;

				for (int iamb = 0; iamb < da; iamb++)
				{
					int newpos = ipos + l2.get(iamb);

					// compute the new trace and insert it in the stack
					MTrace newtrc = new MTrace();
					newtrc.Statenb = dst;
					newtrc.Pos = newpos;
					newtrc.Inputs.addAll(inputs);
					newtrc.Inputs.addAll(i2.get(iamb));
					newtrc.Outputs.addAll(outputs);
					newtrc.Outputs.addAll(o2.get(iamb));
					newtrc.Nodes.addAll(nodes);
					newtrc.Nodes.addAll(n2.get(iamb));

					if (newtrc.Inputs == null || newtrc.Inputs.size() < 1000)
					{
						stack.push(newtrc);
					}

					// reach terminal state ?
					if (dst == 1)
					{
						// add terminal state to nodes and sync with other arrays
						newtrc.Nodes.add(1);
						newtrc.Inputs.add("");
						newtrc.Outputs.add(null);

						if (sollengths.argvalue == null)
						{
							sollengths.argvalue = new ArrayList<Integer>();
							solinputs.argvalue = new ArrayList<ArrayList<String>>();
							soloutputs.argvalue = new ArrayList<ArrayList<String>>();
							solnodes.argvalue = new ArrayList<ArrayList<Object>>();
						}
						sollengths.argvalue.add(newpos);
						solinputs.argvalue.add(newtrc.Inputs);
						soloutputs.argvalue.add(newtrc.Outputs);
						solnodes.argvalue.add(newtrc.Nodes);
					}
				}
			}
		}
		if (sollengths.argvalue == null)
		{
			return 0;
		}
		else
		{
			return sollengths.argvalue.size();
		}
	}

	public final ArrayList<String> matchWord(String text, Engine eng, boolean morethanoneword, String currentline,
			int cpos)
	{
		engine = eng;
		if (epsilon == null)
		{
			setConstants();
		}

		Gram grm = grams.get("Main");
		if (grm == null)
		{
			return null;
		}
		if (engine.BackgroundWorking)
		{
			if (engine.backgroundWorker.isCancellationPending())
			{
				return null;
			}
		}

		ArrayList<Integer> slengths = null;
		ArrayList<ArrayList<String>> sinputs = null, soutputs = null;
		int da;

		RefObject<ArrayList<Integer>> tempRef_slengths2 = null;
		RefObject<ArrayList<ArrayList<String>>> tempRef_sinputs2 = null;
		RefObject<ArrayList<ArrayList<String>>> tempRef_soutputs2 = null;

		if (engine.Lan.isoName.equals("ar") || engine.Lan.isoName.equals("he") || engine.Lan.isoName.equals("vi"))
		{
			String text0 = currentline.substring(cpos);
			RefObject<ArrayList<Integer>> tempRef_slengths = new RefObject<ArrayList<Integer>>(slengths);
			RefObject<ArrayList<ArrayList<String>>> tempRef_sinputs = new RefObject<ArrayList<ArrayList<String>>>(
					sinputs);
			RefObject<ArrayList<ArrayList<String>>> tempRef_soutputs = new RefObject<ArrayList<ArrayList<String>>>(
					soutputs);

			da = morphoMatch(text0, grm, true, tempRef_slengths, tempRef_sinputs, tempRef_soutputs);

			slengths = tempRef_slengths.argvalue;
			sinputs = tempRef_sinputs.argvalue;
			soutputs = tempRef_soutputs.argvalue;
		}
		else
		{
			tempRef_slengths2 = new RefObject<ArrayList<Integer>>(slengths);
			tempRef_sinputs2 = new RefObject<ArrayList<ArrayList<String>>>(sinputs);
			tempRef_soutputs2 = new RefObject<ArrayList<ArrayList<String>>>(soutputs);

			da = morphoMatch(text, grm, true, tempRef_slengths2, tempRef_sinputs2, tempRef_soutputs2);
		}
		slengths = tempRef_slengths2.argvalue;
		sinputs = tempRef_sinputs2.argvalue;
		soutputs = tempRef_soutputs2.argvalue;

		if (da == 0)
		{
			return null;
		}

		ArrayList<String> sols = new ArrayList<String>();
		for (int isol = 0; isol < da; isol++)
		{
			int stl = slengths.get(isol);
			ArrayList<String> sti = sinputs.get(isol);
			ArrayList<String> sto = soutputs.get(isol);

			// translate output trace into a string, and process variables
			StringBuilder otrace = processVariablesInOutputs(sti, sto);

			// translate input trace into a string
			StringBuilder itrace = computeInput(sti);

			// is there any remaining lexical constraint ?
			String sol;
			if (isComplex(otrace))
			{
				if (!morethanoneword && hasMoreThanOneWord(otrace))
				{
					continue;
				}
				String errmessage = null;
				RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
				ArrayList<ArrayList<String>> csols = processConstraints(otrace.toString(), tempRef_errmessage);
				errmessage = tempRef_errmessage.argvalue;
				if (csols == null || csols.isEmpty())
				{
					continue;
				}
				ArrayList<ArrayList<String>> listofsols = defactorize(csols);
				for (int icsol = 0; icsol < listofsols.size(); icsol++)
				{
					ArrayList<String> asol = listofsols.get(icsol);
					engine.processELCSFVariables(asol, text);
					ArrayList<String> asol2 = engine.getRidOfConstraints(asol);
					StringBuilder restrace = computeInput(asol2);
					sol = text + "," + restrace.toString();
					if (stl > text.length())
					{
						sols.add(Integer.toString(stl));
					}
					sols.add(sol);
				}
			}
			else
			{
				int count = 0;
				for (int i = 0; i < otrace.length(); i++)
				{
					if (otrace.charAt(i) == '\\')
					{
						i++;
					}
					else if (otrace.charAt(i) == ',')
					{
						count++;
					}
				}
				if (count == 0) // graph produces only info
				{
					sol = text.substring(0, stl) + "," + itrace.toString() + "," + otrace.toString();
				}
				else
				// graph produces lemma and info
				{
					sol = itrace.toString() + "," + otrace.toString();
				}
				sols.add(sol);
			}
		}
		return sols;
	}

	// Lexical constraints

	public static boolean isComplex(StringBuilder output)
	{
		for (int i = 0; i < output.length(); i++)
		{
			if (output.charAt(i) == '\\')
			{
				i++;
			}
			else if (output.charAt(i) == '<' || output.charAt(i) == '{')
			{
				return true;
			}
		}
		return false;
	}

	private static boolean hasMoreThanOneWord(StringBuilder output)
	{
		int cpt = 0;
		for (int i = 0; i < output.length(); i++)
		{
			if (output.charAt(i) == '\\')
			{
				i++;
			}
			else if (output.charAt(i) == '{')
			{
				cpt++;
			}
			else if (output.charAt(i) == '<')
			{
				int j;
				for (j = 0; i + j < output.length() && output.charAt(j) != '>'; j++)
				{
					;
				}
				String lexeme = output.toString().substring(i, i + j);
				String entry = null, lemma = null, category = null;
				String[] features = null;
				boolean negation = false;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
				RefObject<String> tempRef_category = new RefObject<String>(category);
				RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
				RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);
				boolean tempVar = Dic.parseSymbolFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
						tempRef_features, tempRef_negation);
				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				category = tempRef_category.argvalue;
				features = tempRef_features.argvalue;
				negation = tempRef_negation.argvalue;
				if (tempVar)
				{
					cpt++;
					i += j;
				}
			}
		}
		return cpt > 1;
	}

	transient private static HashMap<String, Pattern> perlpatterns;

	private static boolean perlMatch(String wordform, String pattern)
	{
		String rpat;
		if (pattern.charAt(0) == '"' && pattern.charAt(pattern.length() - 1) == '"')
		{
			rpat = pattern.substring(1, 1 + pattern.length() - 2);
		}
		else if (pattern.charAt(0) == '"' || pattern.charAt(pattern.length() - 1) == '"')
		{
			return false;
		}
		else
		{
			rpat = pattern;
		}

		Pattern p;

		if (perlpatterns == null)
		{
			perlpatterns = new HashMap<String, Pattern>();
			p = Pattern.compile(rpat);
			perlpatterns.put(rpat, p);
		}
		else if (perlpatterns.containsKey(rpat))
		{
			p = perlpatterns.get(rpat);
		}
		else
		{
			p = Pattern.compile(rpat);
			perlpatterns.put(rpat, p);
		}

		Matcher m = p.matcher(wordform);

		return m.find();
	}

	private static String[] extractPerlQueries(String[] features)
	{
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < features.length; i++)
		{
			if (features[i].length() > 4
					&& (features[i].substring(0, 4).equals("+MP=") || features[i].substring(0, 4).equals("-MP=")))
			{
				if (res == null)
				{
					res = new ArrayList<String>();
				}
				res.add(features[i]);
			}
		}
		return res.toArray(new String[res.size()]);
	}

	private static String[] removePerlQueries(String[] features)
	{
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < features.length; i++)
		{
			if (features[i].length() < 4
					|| (!features[i].substring(0, 4).equals("+MP=") && !features[i].substring(0, 4).equals("-MP=")))
			{
				if (res == null)
				{
					res = new ArrayList<String>();
				}
				res.add(features[i]);
			}
		}
		return res.toArray(new String[res.size()]);
	}

	static void filterConstraint(ArrayList<String> sols, String entry, String lemma, String category,
			String[] features, boolean negation)
	{
		String lentry = null, llemma = null, lcategory = null; // information in the lexicon
		String[] lfeatures = null;

		for (int i = 0; i < sols.size();)
		{
			RefObject<String> tempRef_lentry = new RefObject<String>(lentry);
			RefObject<String> tempRef_llemma = new RefObject<String>(llemma);
			RefObject<String> tempRef_lcategory = new RefObject<String>(lcategory);
			RefObject<String[]> tempRef_lfeatures = new RefObject<String[]>(lfeatures);
			Dic.parseDELAFFeatureArray(sols.get(i), tempRef_lentry, tempRef_llemma, tempRef_lcategory,
					tempRef_lfeatures);
			lentry = tempRef_lentry.argvalue;
			llemma = tempRef_llemma.argvalue;
			lcategory = tempRef_lcategory.argvalue;
			lfeatures = tempRef_lfeatures.argvalue;

			if (!negation)
			{
				if (category != null && !category.equals("DIC") && !category.equals(lcategory))
				{
					sols.subList(i, 1 + i).clear();
					continue;
				}
				if (lemma != null && !lemma.equals(llemma))
				{
					sols.subList(i, 1 + i).clear();
					continue;
				}

				// categories or lemmas have matched: now match features
				if (features == null || features.length == 0)
				{
					i++;
					continue;
				}
				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = false;
						if (lfeatures == null)
						{
							break;
						}
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = true;
								break;
							}
							else
							{
								int index = lfeatures[jfeat].indexOf('=');
								if (index != -1)
								{
									if (features[ifeat].substring(1) == lfeatures[jfeat].substring(index + 1))
									{
										compatible = true;
										break;
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
					
					{
						compatible = true;
						if (lfeatures == null)
						{
							break;
						}
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = false;
								break;
							}
							else if (features[ifeat].length() > 5 && features[ifeat].substring(0, 4).equals("-MP="))
							{
								compatible = !perlMatch(entry, features[ifeat].substring(4));
							}
							else
							{
								int index = lfeatures[jfeat].indexOf('=');
								if (index != -1)
								{
									if (features[ifeat].substring(1) == lfeatures[jfeat].substring(index + 1))
									{
										compatible = false;
										break;
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (!compatible)
				{
					sols.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
			else
			// negation
			{
				if (category != null && !category.equals("DIC") && !category.equals(lcategory))
				{
					i++;
					continue;
				}
				if (lemma != null && !lemma.equals(llemma))
				{
					i++;
					continue;
				}

				// categories or lemmas match: now match features
				if (features == null || features.length == 0)
				{
					
					sols.subList(i, 1 + i).clear();
					continue;
				}
				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						compatible = false;
						if (lfeatures == null)
						{
							break;
						}
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = true;
								break;
							}
							else if (features[ifeat].length() > 5 && features[ifeat].substring(0, 4).equals("+MP="))
							{
								compatible = perlMatch(entry, features[ifeat].substring(4));
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
				
					{
						compatible = true;
						if (lfeatures == null)
						{
							break;
						}
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = false;
								break;
							}
							else if (features[ifeat].length() > 5 && features[ifeat].substring(0, 4).equals("-MP="))
							{
								compatible = !perlMatch(entry, features[ifeat].substring(4));
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (compatible)
				{
					
					sols.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
		}
	}

	static ArrayList<String> addBracketsAround(ArrayList<String> sols)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < sols.size(); i++)
		{
			result.add("{" + sols.get(i) + "}");
		}
		return result;
	}

	private static ArrayList<String> transformConstraintIntoLU(ArrayList<String> sols)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < sols.size(); i++)
		{
			String sol = sols.get(i);
			if (sol.charAt(0) == '<' || sol.charAt(0) == '{')
			{
				sol = sol.substring(1, 1 + sol.length() - 2);
			}
			String entry = null, lemma = null, category = null, features = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_features = new RefObject<String>(features);

			Dic.parseDELAF(sol, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			features = tempRef_features.argvalue;
			if (entry == null || entry.equals(""))
			{
				entry = "NA";
			}
			result.add("<LU=" + entry + "," + lemma + "," + category + features + ">");
		}
		return result;
	}

	static ArrayList<String> transformConstraintIntoLUNoLU(ArrayList<String> sols)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < sols.size(); i++)
		{
			String sol = sols.get(i);
			if (sol.charAt(0) == '<' || sol.charAt(0) == '{')
			{
				sol = sol.substring(1, 1 + sol.length() - 2);
			}

			String entry = null, lemma = null, category = null, features = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_features = new RefObject<String>(features);

			Dic.parseDELAF(sol, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			features = tempRef_features.argvalue;
			if (entry == null || entry.equals(""))
			{
				entry = "NA";
			}
			result.add("<" + entry + "," + lemma + "," + category + features + ">");
		}
		return result;
	}

	public final ArrayList<ArrayList<String>> processConstraints(String tokenSequence, RefObject<String> errmessage)
	{
		String[] tokens;
		ArrayList<ArrayList<String>> result = null;

		tokens = iLan.parseSequenceOfTokens(tokenSequence);
		if (tokens == null)
		{
			errmessage.argvalue = "Invalid syntax in info '" + tokenSequence + "'";
			Dic.writeLog(errmessage.argvalue);
			return null;
		}
		for (int i = 0; i < tokens.length; i++)
		{
			String entry = null, lemma = null, category = null;
			String[] features = null;
			String op = null;
			boolean negation = false;
			ArrayList<String> csols;

			if (Dic.isALexicalConstraint(tokens[i]))
			{
				// a lexical constraint
				ArrayList<String> csols0 = null;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
				RefObject<String> tempRef_category = new RefObject<String>(category);
				RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
				RefObject<String> tempRef_op = new RefObject<String>(op);
				RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);

				boolean tempVar = !Dic.parseLexicalConstraint(tokens[i], tempRef_entry, tempRef_lemma,
						tempRef_category, tempRef_features, tempRef_op, tempRef_negation);

				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				category = tempRef_category.argvalue;
				features = tempRef_features.argvalue;
				op = tempRef_op.argvalue;
				negation = tempRef_negation.argvalue;
				if (tempVar)
				{
					continue;
				}
				if (!op.equals("=:"))
				{
					errmessage.argvalue = "Morphological parser cannot process operator '" + op + "' in " + tokens[i];
					Dic.writeLog(errmessage.argvalue);
					return null;
				}
				if (category != null && category.length() >= 1) // recursive constraint e.g. <form=:V>
				{
					
					if (engine.recursiveMorphology == null)
					{
						engine.recursiveMorphology = new HashMap<String, String>();
					}
					else if (engine.recursiveMorphology.containsKey(entry))
					{
						errmessage.argvalue = "Infinite recursivity while parsing '" + entry + "' for constraint '"
								+ tokens[i] + "'";
						Dic.writeLog(errmessage.argvalue);
						return null;
					}
					engine.recursiveMorphology.put(entry, null);
					// WARNNG: agglutinative language might have a compound
					csols0 = engine.lookupAllLexsAndMorphsForSimples(entry, false, entry, 0);
					engine.recursiveMorphology.remove(entry);
				}
				else if (lemma != null && lemma.length() >= 1) // recursive constraint e.g. <form=:eat>
				{
					
					if (engine.recursiveMorphology == null)
					{
						engine.recursiveMorphology = new HashMap<String, String>();
					}
					else if (engine.recursiveMorphology.containsKey(entry))
					{
						errmessage.argvalue = "Infinite recursivity while parsing '" + entry + "' for constraint '"
								+ tokens[i] + "'";
						Dic.writeLog(errmessage.argvalue);
						return null;
					}

					engine.recursiveMorphology.put(entry, null);
					csols0 = engine.lookupAllLexsAndMorphsForSimples(entry, false, entry, 0); // IF WE WANT IT TO BE
																								// RECURSIVE =>
					// engine.LookupAndAnalyzeSimpleNoComplex(entry);
					engine.recursiveMorphology.remove(entry);
				}
				else
				{
					// constraint =: has no lemma nor category???
					errmessage.argvalue = "Constraint '" + tokens[i] + "' has no lemma nor category?";
					Dic.writeLog(errmessage.argvalue);
					return null;
				}
				if (csols0 == null || csols0.isEmpty())
				{
					errmessage.argvalue = "Cannot find lexical entry '" + entry + "' for constraint '" + tokens[i]
							+ "'";
					Dic.writeLog(errmessage.argvalue);
					return null;
				}
				// Using copy-constructor instead of clone() - recommended because of unchecked class cast
				ArrayList<String> csols2 = new ArrayList<String>(csols0);
				if (lemma != null || category != null)
				{
					filterConstraint(csols2, entry, lemma, category, features, negation);
				}
				if (csols2.isEmpty())
				{
					errmessage.argvalue = "Constraint '" + tokens[i] + "' is not satisfied";
					Dic.writeLog(errmessage.argvalue);
					return null;
				}
				csols = transformConstraintIntoLU(csols2); // replace the contraint with a list of lexemes
			}
			else if (tokens[i].charAt(0) == '<' || tokens[i].charAt(0) == '{') // a lexical entry
			{
				csols = new ArrayList<String>();
				csols.add(tokens[i]);
			}
			else
			// a token
			{
				csols = new ArrayList<String>();
				csols.add(tokens[i]);
			}
			if (result == null)
			{
				result = new ArrayList<ArrayList<String>>();
			}
			result.add(csols);
		}
		errmessage.argvalue = null;
		return result;
	}

	private void recDefact(ArrayList<String> prefix, ArrayList<ArrayList<String>> exp, ArrayList<ArrayList<String>> res)
	{
		if (exp.isEmpty())
		{
			// Using copy-constructor instead of clone() - recommended because of unchecked class cast
			res.add(new ArrayList<String>(prefix));
		}
		else
		{
			ArrayList<String> fact = exp.get(0);
			for (int iterm = 0; iterm < fact.size(); iterm++)
			{
				// Using copy-constructor instead of clone() - recommended because of unchecked class cast
				ArrayList<String> tmp = new ArrayList<String>(prefix);
				tmp.add(fact.get(iterm));

				ArrayList<ArrayList<String>> expSublist = new ArrayList<ArrayList<String>>();
				if (exp.size() == 1)
					expSublist = new ArrayList<ArrayList<String>>();
				else if (exp.size() == 2)
					expSublist.add(exp.get(1));
				else
					expSublist = (ArrayList<ArrayList<String>>) exp.subList(1, exp.size() - 1);
				recDefact(tmp, expSublist, res);
			}
		}
	}

	public final ArrayList<ArrayList<String>> defactorize(ArrayList<ArrayList<String>> expression)
	{
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		recDefact(new ArrayList<String>(), expression, res);
		return res;
	}

	// Syntactic Parsing

	final void xmlFilterMatches(ArrayList<String> lexs, ArrayList<Double> lengths, String[] symbols,
			RefObject<ArrayList<String>> reslex, RefObject<ArrayList<Double>> reslen)
	{
		reslex.argvalue = new ArrayList<String>();
		reslen.argvalue = new ArrayList<Double>();

		for (String symbol : symbols)
		{
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
				continue;
			}

			// Using copy-constructor instead of clone() - recommended because of unchecked class cast
			ArrayList<String> sols = new ArrayList<String>(lexs);
			ArrayList<Double> lens = new ArrayList<Double>(lengths);
			RefObject<ArrayList<String>> tempRef_sols = new RefObject<ArrayList<String>>(sols);
			RefObject<ArrayList<Double>> tempRef_lens = new RefObject<ArrayList<Double>>(lens);

			filterLexemes(tempRef_sols, tempRef_lens, lemma, category, features, negation);

			sols = tempRef_sols.argvalue;
			lens = tempRef_lens.argvalue;
			if (sols.size() > 0)
			{
				reslex.argvalue.addAll(sols);
				reslen.argvalue.addAll(lens);
			}
		}
	}

	public final boolean matchLexeme(String lexentry, String lexlemma, String lexcategory, String[] lexfeatures,
			String constraint)
	{
		String consentry = null, conslemma = null, conscategory = null;
		String[] consfeatures = null;
		boolean consnegation = false;
		RefObject<String> tempRef_consentry = new RefObject<String>(consentry);
		RefObject<String> tempRef_conslemma = new RefObject<String>(conslemma);
		RefObject<String> tempRef_conscategory = new RefObject<String>(conscategory);
		RefObject<String[]> tempRef_consfeatures = new RefObject<String[]>(consfeatures);
		RefObject<Boolean> tempRef_consnegation = new RefObject<Boolean>(consnegation);
		Dic.parseSymbolFeatureArray(constraint, tempRef_consentry, tempRef_conslemma, tempRef_conscategory,
				tempRef_consfeatures, tempRef_consnegation);
		consentry = tempRef_consentry.argvalue;
		conslemma = tempRef_conslemma.argvalue;
		conscategory = tempRef_conscategory.argvalue;
		consfeatures = tempRef_consfeatures.argvalue;
		consnegation = tempRef_consnegation.argvalue;

		if (!consnegation)
		{
			if (consentry != null)
			{
				if (!lexentry.equals(consentry))
				{
					return false;
				}
			}
			if (conslemma != null)
			{
				if (!lexlemma.equals(conslemma))
				{
					return false;
				}
			}
			if (conscategory != null)
			{
				if (!lexcategory.equals(conscategory))
				{
					return false;
				}
			}
			if (consfeatures != null)
			{
				boolean compatible = true;
				for (int ifeat = 0; ifeat < consfeatures.length; ifeat++)
				{
					if (consfeatures[ifeat].charAt(0) == '+')
					{
						if (consfeatures[ifeat].length() > 4 && consfeatures[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lexentry, consfeatures[ifeat].substring(4));
							if (!compatible)
							{
								return false;
							}
							continue;
						}
						compatible = false;
						if (lexfeatures != null)
						{
							for (int jfeat = 0; jfeat < lexfeatures.length; jfeat++)
							{
								if (lexfeatures[jfeat].equals(consfeatures[ifeat]))
								{
									compatible = true;
									break;
								}
								else
								{
									int index = lexfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if (consfeatures[ifeat].substring(1) == lexfeatures[jfeat].substring(index + 1))
										{
											compatible = true;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							return false;
						}
					}
					else
				
					{
						if (consfeatures[ifeat].length() > 4 && consfeatures[ifeat].substring(0, 4).equals("-MP="))
						{
							compatible = !perlMatch(lexentry, consfeatures[ifeat].substring(4));
							if (!compatible)
							{
								return false;
							}
							continue;
						}
						compatible = true;
						if (lexfeatures != null)
						{
							for (int jfeat = 0; jfeat < lexfeatures.length; jfeat++)
							{
								if (consfeatures[ifeat].substring(1) == lexfeatures[jfeat].substring(1))
								{
									compatible = false;
									return false;
								}
								else
								{
									int index = lexfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if (consfeatures[ifeat].substring(1) == lexfeatures[jfeat].substring(index + 1))
										{
											compatible = false;
											return false;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							return false;
						}
					}
				}
				if (!compatible)
				{
					return false;
				}
			}
		}
		return true;
	}

	private final void filterLexemes(RefObject<ArrayList<String>> sols, RefObject<ArrayList<Double>> lens,
			String lemma, String category, String[] features, boolean negation)
	{
		String lentry = null, llemma = null, lcategory = null; // information in the lexicon
		String[] lfeatures = null;
		String[] lemmas = null;

		// superlemma
		if (lemma != null)
		{
			// replace lemma with lemmas
			if (hlemmas == null)
			{
				hlemmas = new HashMap<String, String[]>();
			}
			if (hlemmas.containsKey(lemma))
			{
				lemmas = hlemmas.get(lemma);
			}
			else
			{
				if (lemma.equals("SYNTAX"))
				{
					lemmas = new String[1];
					lemmas[0] = "SYNTAX";
					if (lemmas != null)
					{
						hlemmas.put(lemma, lemmas.clone());
					}
				}
			}
		}

		for (int i = 0; i < sols.argvalue.size();)
		{
			RefObject<String> tempRef_lentry = new RefObject<String>(lentry);
			RefObject<String> tempRef_llemma = new RefObject<String>(llemma);
			RefObject<String> tempRef_lcategory = new RefObject<String>(lcategory);
			RefObject<String[]> tempRef_lfeatures = new RefObject<String[]>(lfeatures);

			Dic.parseDELAFFeatureArray(sols.argvalue.get(i), tempRef_lentry, tempRef_llemma, tempRef_lcategory,
					tempRef_lfeatures);

			lentry = tempRef_lentry.argvalue;
			llemma = tempRef_llemma.argvalue;
			lcategory = tempRef_lcategory.argvalue;
			lfeatures = tempRef_lfeatures.argvalue;

			if (!negation)
			{
				if (lemma != null)
				{
					boolean foundsuperlemma = false;
					if (lemmas != null)
					{
						for (int ilemma = 0; ilemma < lemmas.length; ilemma++)
						{
							if (llemma.equals(lemmas[ilemma]))
							{
								foundsuperlemma = true;
								break;
							}
						}
					}
					if (!foundsuperlemma)
					{
						sols.argvalue.subList(i, 1 + i).clear();
						lens.argvalue.subList(i, 1 + i).clear();
						continue;
					}
				}

				if (category != null && category.equals("SYNTAX") && llemma != null && llemma.equals("SYNTAX"))
				{
					i++;
					continue;
				}

				if (category != null)
				{
					if (!category.equals("DIC") && !category.equals(lcategory))
					{
						sols.argvalue.subList(i, 1 + i).clear();
						lens.argvalue.subList(i, 1 + i).clear();
						continue;
					}
				}

				// lemmas or/and categories matched: now match features
				if (features == null)
				{
					i++;
					continue;
				}

				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = false;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									// <N+plural> vs lexeme tables,table+plural
									compatible = true;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)) .equals(lfeatures[jfeat].substring(index + 1)))
										{
											// <N+plural> vs lexeme tables,table+Nb=plural
											compatible = true;
											break;
										}
										else if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											// <N+FR> vs lexeme eat,V+FR=manger
											compatible = true;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
					
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("-MP="))
						{
							compatible = !perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = true;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									// symbol <N-ZZ> vs lexeme table,N+ZZ
									compatible = false;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											// symbol <N-ZZ> against vs table,N+ZZ="hello"
											compatible = false;
											break;
										}
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(index + 1)))
										{
											// symbol <N-plural> vs lexeme table,N+Number=plural
											compatible = false;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (!compatible)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					lens.argvalue.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
			else
			{
				// negation
				if (lemma != null)
				{
					boolean foundsuperlemma = false;
					if (lemmas != null)
					{
						for (int ilemma = 0; ilemma < lemmas.length; ilemma++)
						{
							if (llemma.equals(lemmas[ilemma]))
							{
								foundsuperlemma = true;
								break;
							}
						}
					}
					if (!foundsuperlemma)
					{
						i++;
						continue;
					}
				}

				if (category != null)
				{
					if (!category.equals("DIC") && !category.equals(lcategory))
					{
						i++;
						continue;
					}
				}

				// lemmas or/and categories matched: now match features
				if (features == null)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					lens.argvalue.subList(i, 1 + i).clear();
					continue;
				}

				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = false;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = true;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											compatible = true;
											break;
										}
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(index + 1)))
										{
											compatible = true;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
				
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("-MP="))
						{
							compatible = !perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = true;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = false;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											compatible = false;
											break;
										}
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(index + 1)))
										{
											compatible = false;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (compatible)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					lens.argvalue.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
		}
	}

	// identical to latter, but no lens
	final void filterLexemes(RefObject<ArrayList<String>> sols, String lemma, String category, String[] features,
			boolean negation)
	{
		String lentry = null, llemma = null, lcategory = null; // information in the lexicon
		String[] lfeatures = null;
		String[] lemmas = null;

		// superlemma
		if (lemma != null)
		{
			// replace lemma with lemmas
			if (hlemmas == null)
			{
				hlemmas = new HashMap<String, String[]>();
			}
			if (hlemmas.containsKey(lemma))
			{
				lemmas = hlemmas.get(lemma);
			}
		}

		for (int i = 0; i < sols.argvalue.size();)
		{
			RefObject<String> tempRef_lentry = new RefObject<String>(lentry);
			RefObject<String> tempRef_llemma = new RefObject<String>(llemma);
			RefObject<String> tempRef_lcategory = new RefObject<String>(lcategory);
			RefObject<String[]> tempRef_lfeatures = new RefObject<String[]>(lfeatures);

			Dic.parseDELAFFeatureArray(sols.argvalue.get(i), tempRef_lentry, tempRef_llemma, tempRef_lcategory,
					tempRef_lfeatures);

			lentry = tempRef_lentry.argvalue;
			llemma = tempRef_llemma.argvalue;
			lcategory = tempRef_lcategory.argvalue;
			lfeatures = tempRef_lfeatures.argvalue;

			if (!negation)
			{
				if (lemma != null)
				{
					boolean foundsuperlemma = false;
					if (lemmas != null)
					{
						for (int ilemma = 0; ilemma < lemmas.length; ilemma++)
						{
							if (llemma.equals(lemmas[ilemma]))
							{
								foundsuperlemma = true;
								break;
							}
						}
					}
					if (!foundsuperlemma)
					{
						sols.argvalue.subList(i, 1 + i).clear();
						continue;
					}
				}

				if (category != null)
				{
					if (!category.equals("DIC") && !category.equals(lcategory))
					{
						sols.argvalue.subList(i, 1 + i).clear();
						continue;
					}
				}

				// lemmas or/and categories matched: now match features
				if (features == null)
				{
					i++;
					continue;
				}

				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = false;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = true;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											compatible = true;
											break;
										}
										if (features[ifeat].substring(1) == lfeatures[jfeat].substring(index + 1))
										{
											compatible = true;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
				
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("-MP="))
						{
							compatible = !perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = true;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = false;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)) .equals(lfeatures[jfeat].substring(0, index)))
										{
											compatible = false;
											break;
										}
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(index + 1)))
										{
											compatible = false;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (!compatible)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
			else
			{
				// negation
				if (lemma != null)
				{
					boolean foundsuperlemma = false;
					if (lemmas != null)
					{
						for (int ilemma = 0; ilemma < lemmas.length; ilemma++)
						{
							if (llemma.equals(lemmas[ilemma]))
							{
								foundsuperlemma = true;
								break;
							}
						}
					}
					if (!foundsuperlemma)
					{
						i++;
						continue;
					}
				}

				if (category != null)
				{
					if (!category.equals("DIC") && !category.equals(lcategory))
					{
						i++;
						continue;
					}
				}

				// lemmas or/and categories matched: now match features
				if (features == null)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					continue;
				}

				boolean compatible = true;
				for (int ifeat = 0; ifeat < features.length; ifeat++)
				{
					if (features[ifeat].charAt(0) == '+')
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("+MP="))
						{
							compatible = perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = false;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = true;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(0, index)))
										{
											compatible = true;
											break;
										}
										if ((features[ifeat].substring(1)).equals(lfeatures[jfeat].substring(index + 1)))
										{
											compatible = true;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
					else
					
					{
						if (features[ifeat].length() > 4 && features[ifeat].substring(0, 4).equals("-MP="))
						{
							compatible = !perlMatch(lentry, features[ifeat].substring(4));
							if (!compatible)
							{
								break;
							}
							continue;
						}
						compatible = true;
						if (lfeatures != null)
						{
							for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
							{
								if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
								{
									compatible = false;
									break;
								}
								else
								{
									int index = lfeatures[jfeat].indexOf('=');
									if (index != -1)
									{
										if ((features[ifeat].substring(1)).equals( lfeatures[jfeat].substring(0, index)))
										{
											compatible = false;
											break;
										}
										if ((features[ifeat].substring(1)).equals( lfeatures[jfeat].substring(index + 1)))
										{
											compatible = false;
											break;
										}
									}
								}
							}
						}
						if (!compatible) // not found
						{
							break;
						}
					}
				}
				if (compatible)
				{
					sols.argvalue.subList(i, 1 + i).clear();
					continue;
				}
				i++;
			}
		}
	}

	private final void filterNonMatches(RefObject<ArrayList<String>> sols, RefObject<ArrayList<Double>> lens,
			String lemma, String category, String[] features)
	{
		String lentry = null, llemma = null, lcategory = null; // information in the lexicon
		String[] lfeatures = null;

		for (int i = 0; i < sols.argvalue.size();)
		{
			RefObject<String> tempRef_lentry = new RefObject<String>(lentry);
			RefObject<String> tempRef_llemma = new RefObject<String>(llemma);
			RefObject<String> tempRef_lcategory = new RefObject<String>(lcategory);
			RefObject<String[]> tempRef_lfeatures = new RefObject<String[]>(lfeatures);

			Dic.parseDELAFFeatureArray(sols.argvalue.get(i), tempRef_lentry, tempRef_llemma, tempRef_lcategory,
					tempRef_lfeatures);

			lentry = tempRef_lentry.argvalue;
			llemma = tempRef_llemma.argvalue;
			lcategory = tempRef_lcategory.argvalue;
			lfeatures = tempRef_lfeatures.argvalue;
			if (lemma != null)
			{
				if (!lemma.equals(llemma))
				{
					// NEGATION
					i++;
					continue;
				}
			}

			if (category != null)
			{
				if (category.equals("UNK"))
				{
					// NEGATION
					i++;
					continue;
				}
				else if (!category.equals("DIC") && !category.equals(lcategory))
				{
					// NEGATION
					i++;
					continue;
				}
			}

			// lemmas or/and categories matched: now match features
			if (features == null)
			{
				// HARD NEGATION: remove everything
				sols.argvalue = null;
				lens.argvalue = null;
				return;
			
			}

			boolean compatible = true;
			for (int ifeat = 0; ifeat < features.length; ifeat++)
			{
				if (features[ifeat].charAt(0) == '+')
				{
					compatible = false;
					if (lfeatures != null)
					{
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = true;
								break;
							}
							else if (features[ifeat].length() > 5 && features[ifeat].substring(0, 4).equals("+MP="))
							{
								compatible = perlMatch(lentry, features[ifeat].substring(4));
							}
						}
					}
					if (!compatible) // not found
					{
						break;
					}
				}
				else
			
				{
					compatible = true;
					if (lfeatures != null)
					{
						for (int jfeat = 0; jfeat < lfeatures.length; jfeat++)
						{
							if (lfeatures[jfeat].equals(features[ifeat].substring(1)))
							{
								compatible = false;
								break;
							}
							else if (features[ifeat].length() > 5 && features[ifeat].substring(0, 4).equals("-MP="))
							{
								compatible = !perlMatch(lentry, features[ifeat].substring(4));
							}
						}
					}
					if (!compatible) // not found
					{
						break;
					}
				}
			}
			if (!compatible)
			{
				i++;
				continue;
			}
			else
			{
				// hard negation: remove everything
				sols.argvalue = null;
				lens.argvalue = null;
				return;
			
			}
		}
	}

	// take care of the +UNAMB or -UNAMB constraints in symbol
	private boolean thereIsPlusUnambIn(String[] features)
	{
		if (features == null)
		{
			return false;
		}
		for (int i = 0; i < features.length; i++)
		{
			if (features[i].equals("+UNAMB"))
			{
				return true;
			}
		}
		return false;
	}

	private boolean thereIsMinusUnambIn(String[] features)
	{
		if (features == null)
		{
			return false;
		}
		for (int i = 0; i < features.length; i++)
		{
			if (features[i].equals("-UNAMB"))
			{
				return true;
			}
		}
		return false;
	}

	private String[] removeUnamb(String[] features)
	{
		ArrayList<String> ares = new ArrayList<String>();
		for (int i = 0; i < features.length; i++)
		{
			if (!features[i].equals("-UNAMB") && !features[i].equals("+UNAMB"))
			{
				ares.add(features[i]);
			}
		}
		if (ares.size() > 0)
		{
			return ares.toArray(new String[ares.size()]);
		}
		else
		{
			return null;
		}
	}

	private int matchlexeme(String lemma, String category, String[] features, String text, double cpos, int tunb,
			Mft textmft, ArrayList<Object> annotations, RefObject<ArrayList<Double>> lengths,
			RefObject<ArrayList<ArrayList<Double>>> inputs, RefObject<ArrayList<ArrayList<String>>> variables)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;

		ArrayList<Double> lens = null;
		ArrayList<Integer> lexids = null;

		RefObject<ArrayList<Integer>> tempRef_lexids = new RefObject<ArrayList<Integer>>(lexids);
		RefObject<ArrayList<Double>> tempRef_lens = new RefObject<ArrayList<Double>>(lens);

		int da = textmft.getAllLexIds(tunb, cpos, tempRef_lexids, tempRef_lens);

		lexids = tempRef_lexids.argvalue;
		lens = tempRef_lens.argvalue;

		if (da == 0)
		{
			if (category != null && category.equals("UNK"))
			{
				double length;
				for (length = 0.0; cpos + (int) length < text.length()
						&& Language.isLetter(text.charAt((int) (cpos + length))); length++)
				{
					;
				}
				if (length == 0.0)
				{
					return 0;
				}

				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				ArrayList<Double> input = new ArrayList<Double>();
				input.add(cpos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				ArrayList<String> variable = new ArrayList<String>();
				variable.add(null);
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			return 0;
		}

		// take care of +UNAMB or -UNAMB in symbol
		if (thereIsPlusUnambIn(features))
		{
			// if the symbol has +UNAMB, there must be at most one lexeme
			if (da > 1)
			{
				return 0;
			}
			features = removeUnamb(features);
		}
		else if (thereIsMinusUnambIn(features))
		{
			// if the symbol has +UNAMB, there must be at most one lexeme
			if (da == 1)
			{
				return 0;
			}
			features = removeUnamb(features);
		}

		ArrayList<String> sols = new ArrayList<String>();
		for (int i = 0; i < da; i++)
		{
			int tkid = lexids.get(i);
			if (tkid >= annotations.size())
			{
				continue;
			}
			String lex = (String) annotations.get(tkid);
			if (lex == null)
			{
				continue;
			}
			sols.add(lex);
		}
		RefObject<ArrayList<String>> tempRef_sols = null;
		RefObject<ArrayList<Double>> tempRef_lens2 = null;
		if (da > 0)
		{
			tempRef_sols = new RefObject<ArrayList<String>>(sols);
			tempRef_lens2 = new RefObject<ArrayList<Double>>(lens);

			filterLexemes(tempRef_sols, tempRef_lens2, lemma, category, features, false);
		}
		sols = tempRef_sols.argvalue;
		lens = tempRef_lens2.argvalue;
		if (sols == null)
		{
			return 0;
		}
		if (sols.size() > 0)
		{
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			variables.argvalue = new ArrayList<ArrayList<String>>();
			lengths.argvalue = new ArrayList<Double>();
			ArrayList<Double> input;
			ArrayList<String> variable;
			for (int i = 0; i < sols.size(); i++)
			{
				input = new ArrayList<Double>();
				input.add(cpos);
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(sols.get(i));
				variables.argvalue.add(variable);
				long hund_len = (long) (100 * (lens.get(i)));
				double len = hund_len / 100.0;
				lengths.argvalue.add(len);
			}
		}
		return sols.size();
	}

	private int dontMatchlexeme(String lemma, String category, String[] features, String text, double cpos,
			int startaddr, Mft textmft, ArrayList<Object> annotations, RefObject<ArrayList<Double>> lengths,
			RefObject<ArrayList<ArrayList<Double>>> inputs, RefObject<ArrayList<ArrayList<String>>> variables)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;

		ArrayList<Double> lens = null;
		ArrayList<Integer> lexids = null;

		double ipos = cpos;
		RefObject<ArrayList<Integer>> tempRef_lexids = new RefObject<ArrayList<Integer>>(lexids);
		RefObject<ArrayList<Double>> tempRef_lens = new RefObject<ArrayList<Double>>(lens);

		int da = textmft.getAllLexIds(startaddr, ipos, tempRef_lexids, tempRef_lens);

		lexids = tempRef_lexids.argvalue;
		lens = tempRef_lens.argvalue;
		if (da == 0 && !category.equals("UNK"))
		{
			double length;
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			if (length == 0)
			{
				return 0;
			}

			
			length += ipos - cpos;
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			ArrayList<Double> input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			ArrayList<String> variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}

		// take care of +UNAMB or -UNAMB in symbol
		// TODO I probably need to inverse these tests - C#'s
		if (thereIsPlusUnambIn(features))
		{
			// if the symbol has +UNAMB, there must be at most one lexeme
			if (da > 1)
			{
				return 0;
			}
			features = removeUnamb(features);
		}
		else if (thereIsMinusUnambIn(features))
		{
			// if the symbol has +UNAMB, there must be at most one lexeme
			if (da == 1)
			{
				return 0;
			}
			features = removeUnamb(features);
		}

		ArrayList<String> sols = new ArrayList<String>();
		for (int i = 0; i < da; i++)
		{
			int tkid = lexids.get(i);
			String lex = (String) annotations.get(tkid);
			if (lex == null)
			{
				continue;
			}
			sols.add(lex);
		}

		RefObject<ArrayList<String>> tempRef_sols = null;
		RefObject<ArrayList<Double>> tempRef_lens2 = null;
		if (da > 0)
		{
			tempRef_sols = new RefObject<ArrayList<String>>(sols);
			tempRef_lens2 = new RefObject<ArrayList<Double>>(lens);
			filterNonMatches(tempRef_sols, tempRef_lens2, lemma, category, features);
		}
		sols = tempRef_sols.argvalue;
		lens = tempRef_lens2.argvalue;
		if (sols == null)
		{
			return 0;
		}
		if (sols.size() > 0)
		{
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			variables.argvalue = new ArrayList<ArrayList<String>>();
			lengths.argvalue = new ArrayList<Double>();
			ArrayList<Double> input;
			ArrayList<String> variable;
			for (int i = 0; i < sols.size(); i++)
			{
				input = new ArrayList<Double>();
				input.add(cpos);
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(sols.get(i));
				variables.argvalue.add(variable);
				lengths.argvalue.add(lens.get(i));
			}
		}
		return sols.size();
	}

	static boolean skipSpaces(String text, RefObject<Double> dpos, boolean xmltext)
	{
		int ipos = (int) ((double) dpos.argvalue);
		if (xmltext)
		{
			while (ipos < text.length() && (Character.isWhitespace(text.charAt(ipos)) || text.charAt(ipos) == '<'))
			{
				if (Character.isWhitespace(text.charAt(ipos)))
				{
					for (; ipos < text.length() && Character.isWhitespace(text.charAt(ipos)); ipos++)
					{
						;
					}
				}
				if (ipos < text.length() && text.charAt(ipos) == '<')
				{
					for (; ipos < text.length() && text.charAt(ipos) != '>'; ipos++)
					{
						;
					}
					ipos++;
				}
			}
		}
		else
		{
			while (ipos < text.length() && (Character.isWhitespace(text.charAt(ipos))))
			{
				if (Character.isWhitespace(text.charAt(ipos)))
				{
					for (; ipos < text.length() && Character.isWhitespace(text.charAt(ipos)); ipos++)
					{
						;
					}
				}
			}
		}
		if (ipos > (int) ((double) dpos.argvalue))
		{
			dpos.argvalue = (double) ipos;
		}
		return ipos < text.length();
	}

	private int syntaxMatchSymbol(String ilabel, String text, double cpos, int tunb, Mft textmft,
			ArrayList<Object> annotations, RefObject<ArrayList<Double>> lengths,
			RefObject<ArrayList<ArrayList<Double>>> inputs, RefObject<ArrayList<ArrayList<String>>> variables,
			boolean xmltext)
	{
		double length;
		String token;
		ArrayList<Double> input;
		ArrayList<String> variable;
		int match = 0;

		double ipos = cpos;
		RefObject<Double> tempRef_ipos = new RefObject<Double>(ipos);
		boolean tempVar = !skipSpaces(text, tempRef_ipos, xmltext);
		ipos = tempRef_ipos.argvalue;
		if (tempVar)
		{
			inputs.argvalue = null;
			variables.argvalue = null;
			lengths.argvalue = null;
			return 0;
		}

		String entry = null, lemma = null, category = null;
		String[] features = null;
		boolean negation = false;

		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);

		boolean tempVar2 = !Dic.parseSymbolFeatureArray(ilabel, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features, tempRef_negation);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		negation = tempRef_negation.argvalue;
		if (tempVar2)
		{
			inputs.argvalue = null;
			variables.argvalue = null;
			lengths.argvalue = null;
			return 0;
		}
		if (lemma != null && (lemma.equals("^") || lemma.equals("$")) && category == null)
		{
			category = lemma;
		}
		String[] pfeatures = null;
		if (features != null)
		{
			pfeatures = extractPerlQueries(features);
		}
		if (pfeatures != null && pfeatures.length > 0)
		{
			// precompute the tok just for PERL
			int plen = 0;
			if (iLan.asianTokenizer)
			{
				plen = 1;
			}
			else if (Character.isDigit(text.charAt((int) ipos)) && category.equals("NB"))
			{
				for (plen = 0; ipos + plen < text.length() && Character.isDigit(text.charAt((int) ipos + plen)); plen++)
				{
					;
				}
			}
			else if (Language.isLetter(text.charAt((int) ipos)))
			{
				for (plen = 0; ipos + plen < text.length() && Language.isLetter(text.charAt((int) ipos + plen)); plen++)
				{
					;
				}
			}
			else
			{
				plen = 1;
			}
		
			String tok = text.substring((int) ipos, (int) ipos + plen);
			for (int iperl = 0; iperl < pfeatures.length; iperl++)
			{
				boolean pm = perlMatch(tok, pfeatures[iperl].substring(4));
				if (pfeatures[iperl].charAt(0) == '+' && !pm)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
				else if (pfeatures[iperl].charAt(0) == '-' && pm)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
			}
			features = removePerlQueries(features);
		}

		if (category != null && category.equals("WF")) // any word form: WORKS ALSO ON ASIAN LANGUAGES
		{
			if (this.iLan.asianTokenizer)
			{
				if (Language.isLetter(text.charAt((int) ipos)))
				{
					token = text.substring((int) ipos, (int) ipos + 1);
					length = 1;
				}
				else
				{
					token = "";
					length = 0;
				}
			}
			else
			{
				for (length = 0; ipos + (int) length < text.length()
						&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
				{
					;
				}
				token = text.substring((int) ipos, (int) ipos + (int) length);
			}
			if (length == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
			length += ipos - cpos;
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "WF");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("^")) // beginning of text unit
		{
			for (int k = (int) ipos - 1; k > 0; k--)
			{
				if (!Character.isWhitespace(text.charAt(k)))
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(0.0);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("$")) // end of text unit
		{
			for (int k = (int) ipos; k < text.length(); k++)
			{
				if (!Character.isWhitespace(text.charAt(k)))
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(0.0);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("UPP")) // uppercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (!Language.isUpper(token))
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "UPP");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("U")) // one-letter uppercase word form
		{
			if (this.iLan.isoName.equals("ja"))
			{
				if (Language.isUniHan(text.charAt((int) ipos)))
				{
					token = text.substring((int) ipos, (int) ipos + 1);
					length = 1;
				}
				else
				{
					token = "";
					length = 0;
				}
				length += ipos - cpos;
				if (token.length() != 1)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}

				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				input = new ArrayList<Double>();
				input.add(ipos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + "," + token + "," + "L");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			else
			{
				for (length = 0; ipos + (int) length < text.length()
						&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
				{
					;
				}
				token = text.substring((int) ipos, (int) ipos + (int) length);
				length += ipos - cpos;
				if (!Language.isUpper(token) || token.length() != 1)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}

				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				input = new ArrayList<Double>();
				input.add(ipos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + "," + token + "," + "U");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
		}
		else if (category != null && category.equals("H")) // one-letter Hiragana word form: WORKS ONLY ON JAPANESE
		{
			if (this.iLan.isoName.equals("ja"))
			{
				if (Language.isHiragana(text.charAt((int) ipos)))
				{
					token = text.substring((int) ipos, (int) ipos + 1);
					length = 1;
				}
				else
				{
					token = "";
					length = 0;
				}
				length += ipos - cpos;
				if (token.length() != 1)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}

				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				input = new ArrayList<Double>();
				input.add(ipos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + "," + token + "," + "L");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			else
			{
				match = matchlexeme(lemma, category, features, text, ipos, tunb, textmft, annotations, lengths, inputs,
						variables);
				if (match == 0)
				{
					return 0;
				}
				for (int isol = 0; isol < lengths.argvalue.size(); isol++)
				{
					double len = lengths.argvalue.get(isol);
					lengths.argvalue.set(isol, len + ipos - cpos);
				}
				return match;
			}
		}
		else if (category != null && category.equals("K")) // one-letter Katakana word form: WORKS ONLY ON JAPANESE
		{
			if (this.iLan.isoName.equals("ja"))
			{
				if (Language.isKatakana(text.charAt((int) ipos)))
				{
					token = text.substring((int) ipos, (int) ipos + 1);
					length = 1;
				}
				else
				{
					token = "";
					length = 0;
				}
				length += ipos - cpos;
				if (token.length() != 1)
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}

				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				input = new ArrayList<Double>();
				input.add(ipos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + "," + token + "," + "L");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			else
			{
				match = matchlexeme(lemma, category, features, text, ipos, tunb, textmft, annotations, lengths, inputs,
						variables);
				if (match == 0)
				{
					return 0;
				}
				for (int isol = 0; isol < lengths.argvalue.size(); isol++)
				{
					double len = lengths.argvalue.get(isol);
					lengths.argvalue.set(isol, len + ipos - cpos);
				}
				return match;
			}
		}
		else if (category != null && category.equals("L")) // one-letter word form: WORKS ALSO ON ASIAN LANGUAGES
		{
			if (this.iLan.asianTokenizer)
			{
				if (Language.isLetter(text.charAt((int) ipos)))
				{
					token = text.substring((int) ipos, (int) ipos + 1);
					length = 1;
				}
				else
				{
					token = "";
					length = 0;
				}
			}
			else
			{
				for (length = 0; ipos + (int) length < text.length()
						&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
				{
					;
				}
				token = text.substring((int) ipos, (int) ipos + (int) length);
			}
			length += ipos - cpos;
			if (token.length() != 1)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "L");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("W")) // one-letter lowercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (!Language.isLower(token) || token.length() != 1)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "W");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("LOW")) // lowercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (!Language.isLower(token))
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "LOW");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("CAP")) // first letter is uppercase
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (!Language.isCapital(token))
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "CAP");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("P")) // any delimiter
		{
			match = ipos < text.length() && !Language.isLetter(text.charAt((int) ipos))
					&& !Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			token = text.substring((int) ipos, (int) ipos + 1);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "P");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("D")) // any digit
		{
			match = ipos < text.length() && Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			token = text.substring((int) ipos, (int) ipos + 1);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "D");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category != null && category.equals("NB")) // any sequence of digits
		{
			match = ipos < text.length() && Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}

			for (length = 0; ipos + (int) length < text.length()
					&& Character.isDigit(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + "," + token + "," + "NB");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else
		// lexeme
		{
			match = matchlexeme(lemma, category, features, text, ipos, tunb, textmft, annotations, lengths, inputs,
					variables);
			if (match == 0)
			{
				return 0;
			}
			for (int isol = 0; isol < lengths.argvalue.size(); isol++)
			{
				double len = lengths.argvalue.get(isol);
				lengths.argvalue.set(isol, len + ipos - cpos);
			}
			return match;
		}
	}

	private int syntaxDontMatchSymbol(String ilabel, String text, double cpos, int startaddr, Mft textmft,
			ArrayList<Object> annotations, RefObject<ArrayList<Double>> lengths,
			RefObject<ArrayList<ArrayList<Double>>> inputs, RefObject<ArrayList<ArrayList<String>>> variables,
			boolean xmltext)
	{
		double length;
		String token;
		ArrayList<Double> input;
		ArrayList<String> variable;
		int match = 0;
		inputs.argvalue = null;
		variables.argvalue = null;
		lengths.argvalue = null;

		double ipos = cpos;
		RefObject<Double> tempRef_ipos = new RefObject<Double>(ipos);
		boolean tempVar = !skipSpaces(text, tempRef_ipos, xmltext);
		ipos = tempRef_ipos.argvalue;
		if (tempVar)
		{
			return 0;
		}

		String entry = null, lemma = null, category = null;
		String[] features = null;
		boolean negation = false;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);

		boolean tempVar2 = !Dic.parseSymbolFeatureArray(ilabel, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features, tempRef_negation);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		negation = tempRef_negation.argvalue;
		if (tempVar2)
		{
			return 0;
		}
		String[] pfeatures = null;
		if (features != null)
		{
			pfeatures = extractPerlQueries(features);
		}
		if (pfeatures != null && pfeatures.length > 0)
		{
			// precompute the tok just for PERL
			int plen;
			if (iLan.asianTokenizer)
			{
				plen = 1;
			}
			else if (Character.isDigit(text.charAt((int) ipos)) && category.equals("NB"))
			{
				for (plen = 0; ipos + plen < text.length() && Character.isDigit(text.charAt((int) ipos + plen)); plen++)
				{
					;
				}
			}
			else if (Language.isLetter(text.charAt((int) ipos)))
			{
				for (plen = 0; ipos + plen < text.length() && Language.isLetter(text.charAt((int) ipos + plen)); plen++)
				{
					;
				}
			}
			else
			{
				plen = 1;
			}

			String tok = text.substring((int) ipos, (int) ipos + plen);
			for (int iperl = 0; iperl < pfeatures.length; iperl++)
			{
				boolean pm = perlMatch(tok, pfeatures[iperl].substring(4));
				if (features[iperl].charAt(0) == '+' && pm) // NEGATION
				{
					return 0;
				}
				else if (features[iperl].charAt(0) == '-' && !pm) // NEGATION
				{
					return 0;
				}
			}

			features = removePerlQueries(features);
		}
		boolean foundanonspace;

		if (category.equals("WF")) // any word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			if (length > 0) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("^")) // beginning of text unit
		{
			foundanonspace = false;
			for (int k = (int) ipos - 1; k > 0; k--)
			{
				if (!Character.isWhitespace(text.charAt(k)))
				{
					foundanonspace = true;
					break;
				}
			}
			if (!foundanonspace)
			{
				return 0;
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(0.0);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("$")) // end of text unit
		{
			foundanonspace = false;
			for (int k = (int) ipos; k < text.length(); k++)
			{
				if (!Character.isWhitespace(text.charAt(k)))
				{
					foundanonspace = true;
					break;
				}
			}
			if (!foundanonspace)
			{
				return 0;
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(0.0);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("UPP")) // uppercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (Language.isUpper(token)) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("U")) // one-letter uppercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (Language.isUpper(token) && token.length() == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("L")) // one-letter word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (token.length() == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("W")) // one-letter lowercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (Language.isLower(token) && token.length() == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("LOW")) // lowercase word form
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (Language.isLower(token)) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("CAP")) // first letter is uppercase
		{
			for (length = 0; ipos + (int) length < text.length()
					&& Language.isLetter(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			length += ipos - cpos;
			if (Language.isCapital(token)) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("P")) // any delimiter
		{
			match = ipos < text.length() && !Language.isLetter(text.charAt((int) ipos))
					&& !Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			token = text.substring((int) ipos, (int) ipos + 1);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("D")) // any digit
		{
			match = ipos < text.length() && Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			token = text.substring((int) ipos, (int) ipos + 1);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (category.equals("NB")) // any sequence of digits
		{
			match = ipos < text.length() && Character.isDigit(text.charAt((int) ipos)) ? 1 : 0;
			if (match == 1) // NEGATION
			{
				if (pfeatures == null || pfeatures.length == 0)
				{
					return 0;
				}
			}

			for (length = 0; ipos + (int) length < text.length()
					&& Character.isDigit(text.charAt((int) ipos + (int) length)); length++)
			{
				;
			}
			token = text.substring((int) ipos, (int) ipos + (int) length);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length + ipos - cpos);
			input = new ArrayList<Double>();
			input.add(ipos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(null);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else
		// lexeme
		{
			match = dontMatchlexeme(lemma, category, features, text, ipos, startaddr, textmft, annotations, lengths,
					inputs, variables);
			if (match > 0)
			{
				for (int isol = 0; isol < lengths.argvalue.size(); isol++)
				{
					double len = lengths.argvalue.get(isol);
					lengths.argvalue.set(isol, len + ipos - cpos);
				}
			}
			return match;
		}
	}

	private int syntaxMatchTermLabel(String ilabel, String text, double cpos, double cposafterspaces, int tunb,
			Mft textmft, ArrayList<Object> annotations, RefObject<ArrayList<Double>> lengths,
			RefObject<ArrayList<ArrayList<Double>>> inputs, RefObject<ArrayList<ArrayList<String>>> variables,
			boolean xmltext)
	{
		double length;
		String token;
		ArrayList<Double> input;
		ArrayList<String> variable;
		int match;

		if (ilabel.charAt(0) == '<')
		{
			if (ilabel.length() == 3 && ilabel.charAt(1) == 'E' && ilabel.charAt(2) == '>')
			{
				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(0.0);
				input = new ArrayList<Double>();
				input.add(cpos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(null);
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
		}
		else if (ilabel.charAt(0) == '$' && ilabel.length() >= 2
				&& (ilabel.charAt(1) == '(' || ilabel.charAt(1) == ')'))
		{
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(0.0);
			input = new ArrayList<Double>();
			input.add(cpos);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(ilabel);
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}

		if (cpos >= text.length())
		{
			if (ilabel.equals("<$>"))
			{
				lengths.argvalue = new ArrayList<Double>(); // go beyond text's end in order to make sure we stop
				lengths.argvalue.add(1.0);
				input = new ArrayList<Double>();
				input.add(cpos);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add("END,WF");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			else
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
		}
		if (ilabel.charAt(0) == '#' && ilabel.length() == 1)
		{
			inputs.argvalue = null;
			variables.argvalue = null;
			lengths.argvalue = null;
			if (Character.isWhitespace(text.charAt((int) cpos)))
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}

		if (ilabel.charAt(0) == '<')
		{
			if (ilabel.charAt(1) == '!')
			{
				return syntaxDontMatchSymbol(ilabel, text, cpos, tunb, textmft, annotations, lengths, inputs,
						variables, xmltext);
			}
			else
			{
				return syntaxMatchSymbol(ilabel, text, cpos, tunb, textmft, annotations, lengths, inputs, variables,
						xmltext);
			}
		}
		else if (Language.isLetter(ilabel.charAt(0))) // wordform
		{
			token = ilabel;
			if (cposafterspaces >= text.length()) 
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
			if (iLan.asianTokenizer)
			{
				length = 1.0;
				match = (text.charAt((int) cposafterspaces) == ilabel.charAt(0)) ? 1 : 0;
			}
			else
			{
				for (length = 0; cposafterspaces + (int) length < text.length()
						&& Language.isLetter(text.charAt((int) cposafterspaces + (int) length)); length++)
				{
					;
				}
				token = text.substring((int) cposafterspaces, (int) cposafterspaces + (int) length);
				match = iLan.doWordFormsMatch(token, ilabel) ? 1 : 0;
			}
			length += cposafterspaces - cpos;

			if (match != 0)
			{
				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length);
				input = new ArrayList<Double>();
				input.add(cposafterspaces);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + ",WF");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
			}
			else
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
			}
			return match;
		}
		else if (ilabel.charAt(0) == '"') // protected sequence
		{
			StringBuilder sb = new StringBuilder();
			for (int i2 = 0; i2 < ilabel.length(); i2++)
			{
				if (ilabel.charAt(i2) == ',')
				{
					sb.append("\\,");
				}
				else
				{
					sb.append(ilabel.charAt(i2));
				}
			}
			token = sb.toString();

			if (Character.isWhitespace(ilabel.charAt(1)))
			{
				if (cpos >= text.length()) // (!SkipSpaces (text,ref ipos,xmltext))
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
				int i;
				for (i = 0; i < ilabel.length() - 2; i++)
				{
					if (cpos + i >= text.length())
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
					if (text.charAt((int) cpos + i) != ilabel.charAt(i + 1))
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
				}
				length = (int) (ilabel.length() - 2 + cposafterspaces - cpos);
				lengths.argvalue = new ArrayList<Double>();
				lengths.argvalue.add(length + cposafterspaces - cpos);
				input = new ArrayList<Double>();
				input.add(cposafterspaces);
				inputs.argvalue = new ArrayList<ArrayList<Double>>();
				inputs.argvalue.add(input);
				variable = new ArrayList<String>();
				variable.add(token + ",WF");
				variables.argvalue = new ArrayList<ArrayList<String>>();
				variables.argvalue.add(variable);
				return 1;
			}
			else if (Character.isLetter(ilabel.charAt(1)) && Character.isLetter(ilabel.charAt(ilabel.length() - 2))) // e.g.
																														// ilabel
																														// ==
																														// "\"the\""
			{
				// make sure that the match is complete
				if (cposafterspaces + ilabel.length() - 2 < text.length()
						&& Character.isLetter(text.charAt((int) cposafterspaces + ilabel.length() - 2)))
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
				for (int i = 0; i < ilabel.length() - 2; i++)
				{
					if (cposafterspaces + i >= text.length())
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
					if (text.charAt((int) cposafterspaces + i) != ilabel.charAt(i + 1))
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
				}
			}
			else if (!Character.isWhitespace(ilabel.charAt(1)))
			{
				if (cposafterspaces >= text.length()) 
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
				for (int i = 0; i < ilabel.length() - 2; i++)
				{
					if (cposafterspaces + i >= text.length())
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
					if (text.charAt((int) cposafterspaces + i) != ilabel.charAt(i + 1))
					{
						inputs.argvalue = null;
						variables.argvalue = null;
						lengths.argvalue = null;
						return 0;
					}
				}
			}
			length = (int) (ilabel.length() - 2 + cposafterspaces - cpos);
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(length + cposafterspaces - cpos);
			input = new ArrayList<Double>();
			input.add(cposafterspaces);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + ",WF");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return 1;
		}
		else if (ilabel.charAt(0) == '\\') // protected char
		{
			token = ilabel;
			if (!Character.isWhitespace(ilabel.charAt(1)))
			{
				if (cposafterspaces >= text.length()) 
				{
					inputs.argvalue = null;
					variables.argvalue = null;
					lengths.argvalue = null;
					return 0;
				}
			}
			match = (text.charAt((int) cposafterspaces) == ilabel.charAt(1)) ? 1 : 0;
			if (match == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + cposafterspaces - cpos);
			input = new ArrayList<Double>();
			input.add(cposafterspaces);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + ",WF");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return match;
		}
		else
		// delimiter
		{
			if (ilabel.equals(","))
			{
				token = "\\,";
			}
			else
			{
				token = ilabel;
			}
			if (cposafterspaces >= text.length()) 
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
			match = (text.charAt((int) cposafterspaces) == ilabel.charAt(0)) ? 1 : 0;
			if (match == 0)
			{
				inputs.argvalue = null;
				variables.argvalue = null;
				lengths.argvalue = null;
				return 0;
			}
			lengths.argvalue = new ArrayList<Double>();
			lengths.argvalue.add(1.0 + cposafterspaces - cpos);
			input = new ArrayList<Double>();
			input.add(cposafterspaces);
			inputs.argvalue = new ArrayList<ArrayList<Double>>();
			inputs.argvalue.add(input);
			variable = new ArrayList<String>();
			variable.add(token + ",WF");
			variables.argvalue = new ArrayList<ArrayList<String>>();
			variables.argvalue.add(variable);
			return match;
		}
	}


	private int syntaxMatchLabel(int graphnode, String ilabel, String olabel, String text, double cpos,
			double cposafterspaces, int tunb, Mft textmft, ArrayList<Object> annotations,
			RefObject<ArrayList<Double>> lengths, RefObject<ArrayList<ArrayList<Double>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> variables, RefObject<ArrayList<ArrayList<String>>> outputs,
			RefObject<ArrayList<ArrayList<Object>>> nodes, boolean xmltext, ArrayList<String> recursiveCalls,
			Gram currentgram)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;
		outputs.argvalue = null;
		nodes.argvalue = null;

		if ((ilabel.charAt(0) == ':'
				|| (ilabel.charAt(0) == '$' && ilabel.length() >= 2 && ilabel.charAt(1) != '(' && ilabel.charAt(1) != ')'))) // recursive
																															// call
		{
			String graphname = ilabel.substring(1);
			Gram grm = null;
			if (!grams.containsKey(graphname))
			{
				// gram :graphname or $graphname does not exist in the grammar: maybe in a variable definition $(var ...
				// $)
				grm = currentgram.getGramFromVariableDefinition(graphname);
				grams.put(graphname, grm);
			}
			else
			{
				grm = grams.get(graphname);
			}

			if (nbOfRecursiveCalls(ilabel, recursiveCalls) > 10)
			{
				return 0;
			}

			if (grm == null) // not compiled yet
			{
				Graph grf = null;
				if (this.graphs != null)
				{
					for (int i = 0; i < this.graphs.size(); i++)
					{
						Graph cgrf = this.graphs.get(i);
						if (cgrf != null && cgrf.name.equals(graphname))
						{
							grf = cgrf;
							break;
						}
					}
				}
				if (grf != null)
				{
					ArrayList<String> aVocab = new ArrayList<String>();
					aVocab.add("<E>");
					HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
					hVocab.put("<E>", 0);
					grm = grf.compile(iLan, this.gramType, aVocab, hVocab, engine);
					if (grm != null)
					{
						grm.vocab = aVocab;
						hVocab = null;
						grm.prepareForParsing();
					}
					grams.put(grf.name, grm);
				}
				else
				{
					return 0;
				}
			}

			if (nbOfRecursiveCalls(ilabel, recursiveCalls) > 3)
			{
				return 0;
			}
			recursiveCalls.add(ilabel);
			int da = syntaxMatch(graphname, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths, inputs,
					variables, outputs, nodes, MatchType.ALL, false, xmltext, recursiveCalls);
			recursiveCalls.remove(recursiveCalls.size() - 1);

			if (da > 0)
			{
				for (int isol = 0; isol < da; isol++)
				{
					// do not touch lengths[isol]
					ArrayList<Double> i2 = inputs.argvalue.get(isol);
					i2.add(0, cpos);
					ArrayList<String> v2 = variables.argvalue.get(isol);
					v2.add(0, null);
					ArrayList<String> o2 = outputs.argvalue.get(isol);
					o2.add(0, olabel);
					ArrayList<Object> n2 = nodes.argvalue.get(isol);
					n2.add(0, graphnode);
				}
			}
			return da;
		}
		else
		// simple terminal match
		{
			int da = syntaxMatchTermLabel(ilabel, text, cpos, cposafterspaces, tunb, textmft, annotations, lengths,
					inputs, variables, xmltext);
			if (da > 0)
			{
				// I have computed inputs & variables: I need to add outputs and nodes for sync
				outputs.argvalue = new ArrayList<ArrayList<String>>();
				ArrayList<String> o2 = new ArrayList<String>();
				o2.add(olabel);
				nodes.argvalue = new ArrayList<ArrayList<Object>>();
				ArrayList<Object> n2 = new ArrayList<Object>();
				n2.add(graphnode);
				for (int isol = 0; isol < da; isol++)
				{
					outputs.argvalue.add(o2);
					nodes.argvalue.add(n2);
				}
			}
			return da;
		}
	}

	private int syntaxMatchSequenceOfLabels(int graphnode, String ilabel, String[] ilabels, String olabel, String text,
			double cpos, double cposafterspaces, int tunb, Mft textmft, ArrayList<Object> annotations,
			RefObject<ArrayList<Double>> lengths, RefObject<ArrayList<ArrayList<Double>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> variables, RefObject<ArrayList<ArrayList<String>>> outputs,
			RefObject<ArrayList<ArrayList<Object>>> nodes, boolean xmltext, ArrayList<String> recursiveCalls)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;
		outputs.argvalue = null;
		nodes.argvalue = null;

		if (!grams.containsKey(ilabel))
		{
			// we need to create a grammar that represents the sequence of ilabels
			Gram grm0 = null;
			ArrayList<String> aVocab = new ArrayList<String>();
			aVocab.add("<E>");
			HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
			hVocab.put("<E>", 0);
			if (ilabels.length == 1)
			{
				grm0 = new Gram(2);
				grm0.addTransition(0, 1, ilabels[ilabels.length - 1], aVocab, hVocab);
			}
			else
			{
				grm0 = new Gram(ilabels.length + 1);
				int src = 0;
				int dst = 2;
				for (int itok = 0; itok < ilabels.length - 1; itok++)
				{
					grm0.addTransition(src, dst, ilabels[itok], aVocab, hVocab);
					src = dst;
					dst++;
				}
				grm0.addTransition(src, 1, ilabels[ilabels.length - 1], aVocab, hVocab);
			}
			grm0.vocab = aVocab;
			grm0.prepareForParsing();
			grams.put(ilabel, grm0);
		}
		Gram grm = grams.get(ilabel);
		if (grm == null)
		{
			// gram did not compile
			return 0;
		}

		if (nbOfRecursiveCalls(ilabel, recursiveCalls) > 3)
		{
			return 0;
		}
		recursiveCalls.add(ilabel);
		int da = syntaxMatch(ilabel, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths, inputs,
				variables, outputs, nodes, MatchType.ALL, false, xmltext, recursiveCalls);
		recursiveCalls.remove(recursiveCalls.size() - 1);

		if (da > 0)
		{
			for (int isol = 0; isol < da; isol++)
			{
				// do not touch lengths[isol]
				ArrayList<Double> i2 = inputs.argvalue.get(isol);
				i2.add(0, cpos);
				ArrayList<String> v2 = variables.argvalue.get(isol);
				v2.add(0, null);
				ArrayList<String> o2 = outputs.argvalue.get(isol);
				o2.add(0, olabel);
				ArrayList<Object> n2 = nodes.argvalue.get(isol);
				n2.add(0, graphnode);
			}
		}
		return da;
	}

	private int syntaxFailureMatchLabel(int graphnode, String ilabel, String olabel, String text, double cpos,
			double cposafterspaces, int tunb, Mft textmft, ArrayList<Object> annotations,
			RefObject<ArrayList<Double>> lengths, RefObject<ArrayList<ArrayList<Double>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> variables, RefObject<ArrayList<ArrayList<String>>> outputs,
			RefObject<ArrayList<ArrayList<Object>>> nodes, boolean xmltext, ArrayList<String> recursiveCalls,
			Gram currentgram)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;
		outputs.argvalue = null;
		nodes.argvalue = null;

		if (ilabel.charAt(0) == ':'
				|| (ilabel.charAt(0) == '$' && ilabel.length() >= 2 && ilabel.charAt(1) != '(' && ilabel.charAt(1) != ')')) // recursive
																															// call
		{
			String graphname = ilabel.substring(1);
			Gram grm = null;
			if (!grams.containsKey(graphname))
			{
				// gram :graphname or $graphname does not exist in the grammar: maybe in a variable definition $(var ...
				// $)
				grm = currentgram.getGramFromVariableDefinition(graphname);
				grams.put(graphname, grm);
			}
			else
			{
				grm = grams.get(graphname);
			}
			if (grm == null) // not compiled yet
			{
				Graph grf = null;
				if (this.graphs != null)
				{
					for (int i = 0; i < this.graphs.size(); i++)
					{
						Graph cgrf = this.graphs.get(i);
						if (cgrf != null && cgrf.name.equals(graphname))
						{
							grf = cgrf;
							break;
						}
					}
				}
				if (grf != null)
				{
					ArrayList<String> aVocab = new ArrayList<String>();
					aVocab.add("<E>");
					HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
					hVocab.put("<E>", 0);
					grm = grf.compile(iLan, this.gramType, aVocab, hVocab, engine);
					if (grm != null)
					{
						grm.vocab = aVocab;
						hVocab = null;
						grm.prepareForParsing();
					}
					grams.put(grf.name, grm);
				}
				else
				// grf does not even exist
				{
					return 0;
				}
			}

			if (nbOfRecursiveCalls(ilabel, recursiveCalls) > 3)
			{
				return 0;
			}
			recursiveCalls.add(ilabel);
			int da = syntaxMatch(graphname, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths, inputs,
					variables, outputs, nodes, MatchType.ALL, false, xmltext, recursiveCalls);
			recursiveCalls.remove(recursiveCalls.size() - 1);

			if (da == 0)
			{
				recursiveCalls.add(ilabel);
				da = failureSyntaxMatch(graphname, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths,
						inputs, variables, outputs, nodes, MatchType.LONGEST, false, xmltext, recursiveCalls);
				da = (-1) * da;
				recursiveCalls.remove(recursiveCalls.size() - 1);
			}
			if (da != 0) // attention da can be negative
			{
				for (int isol = 0; isol < Math.abs(da); isol++)
				{
					// do not touch lengths[isol]
					ArrayList<Double> i2 = inputs.argvalue.get(isol);
					i2.add(0, cpos);
					ArrayList<String> v2 = variables.argvalue.get(isol);
					v2.add(0, null);
					ArrayList<String> o2 = outputs.argvalue.get(isol);
					o2.add(0, olabel);
					ArrayList<Object> n2 = nodes.argvalue.get(isol);
					n2.add(0, graphnode);
				}
			}
			return da;
		}
		else
		// simple terminal match
		{
			int da = syntaxMatchTermLabel(ilabel, text, cpos, cposafterspaces, tunb, textmft, annotations, lengths,
					inputs, variables, xmltext);
			if (da > 0)
			{
				outputs.argvalue = new ArrayList<ArrayList<String>>();
				ArrayList<String> o2 = new ArrayList<String>();
				o2.add(olabel);
				nodes.argvalue = new ArrayList<ArrayList<Object>>();
				ArrayList<Object> n2 = new ArrayList<Object>();
				n2.add(graphnode);
				for (int isol = 0; isol < da; isol++)
				{
					outputs.argvalue.add(o2);
					nodes.argvalue.add(n2);
				}
			}
			return da;
		}
	}

	private int syntaxFailureMatchSequenceOfLabels(int graphnode, String ilabel, String[] ilabels, String olabel,
			String text, double cpos, double cposafterspaces, int tunb, Mft textmft, ArrayList<Object> annotations,
			RefObject<ArrayList<Double>> lengths, RefObject<ArrayList<ArrayList<Double>>> inputs,
			RefObject<ArrayList<ArrayList<String>>> variables, RefObject<ArrayList<ArrayList<String>>> outputs,
			RefObject<ArrayList<ArrayList<Object>>> nodes, boolean xmltext, ArrayList<String> recursiveCalls)
	{
		lengths.argvalue = null;
		inputs.argvalue = null;
		variables.argvalue = null;
		outputs.argvalue = null;
		nodes.argvalue = null;

		if (!grams.containsKey(ilabel))
		{
			// we need to create a grammar that represents the sequence of ilabels
			Gram grm0 = null;
			ArrayList<String> aVocab = new ArrayList<String>();
			aVocab.add("<E>");
			HashMap<String, Integer> hVocab = new HashMap<String, Integer>();
			hVocab.put("<E>", 0);
			if (ilabels.length == 1)
			{
				grm0 = new Gram(2);
				grm0.addTransition(0, 1, ilabels[ilabels.length - 1], aVocab, hVocab);
			}
			else
			{
				grm0 = new Gram(ilabels.length + 1);
				int src = 0;
				int dst = 2;
				for (int itok = 0; itok < ilabels.length - 1; itok++)
				{
					grm0.addTransition(src, dst, ilabels[itok], aVocab, hVocab);
					src = dst;
					dst++;
				}
				grm0.addTransition(src, 1, ilabels[ilabels.length - 1], aVocab, hVocab);
			}
			grm0.vocab = aVocab;
			grm0.prepareForParsing();
			grams.put(ilabel, grm0);
		}
		Gram grm = grams.get(ilabel);
		if (grm == null)
		{
			// gram did not compile
			return 0;
		}

		recursiveCalls.add(ilabel);
		int da = syntaxMatch(ilabel, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths, inputs,
				variables, outputs, nodes, MatchType.ALL, false, xmltext, recursiveCalls);
		recursiveCalls.remove(recursiveCalls.size() - 1);

		if (da == 0)
		{
			recursiveCalls.add(ilabel);
			da = failureSyntaxMatch(ilabel, graphnode, text, cpos, tunb, textmft, annotations, grm, lengths, inputs,
					variables, outputs, nodes, MatchType.LONGEST, false, xmltext, recursiveCalls);
			da = (-1) * da;
			recursiveCalls.remove(recursiveCalls.size() - 1);
		}
		if (da != 0) // attention da can be negative
		{
			for (int isol = 0; isol < Math.abs(da); isol++)
			{
				// do not touch lengths[isol]
				ArrayList<Double> i2 = inputs.argvalue.get(isol);
				i2.add(0, cpos);
				ArrayList<String> v2 = variables.argvalue.get(isol);
				v2.add(0, null);
				ArrayList<String> o2 = outputs.argvalue.get(isol);
				o2.add(0, olabel);
				ArrayList<Object> n2 = nodes.argvalue.get(isol);
				n2.add(0, graphnode);
			}
		}
		return da;
	}

	private int nbOfRecursiveCalls(String gname, ArrayList<String> recursivecalls)
	{
		int nb = 0;

		for (String cgname : recursivecalls)
		{
			if (gname.equals(cgname))
			{
				nb++;
			}
		}
		return nb;
	}

	private boolean alreadyThereFailure(ArrayList<Double> lengths, double length,
			ArrayList<ArrayList<Double>> positions, ArrayList<Double> position, ArrayList<ArrayList<String>> variables,
			ArrayList<String> variable, ArrayList<ArrayList<String>> outputs, ArrayList<String> output)
	{
		if (lengths == null || lengths.isEmpty())
		{
			return false;
		}
		for (int i = 0; i < lengths.size(); i++)
		{
			double l = lengths.get(i);
			if (length != l)
			{
				continue;
			}

			ArrayList<Double> p = positions.get(i);
			if ((p == null && position != null) || (p != null && position == null))
			{
				continue;
			}
			if (position != null)
			{
				if (position.size() != p.size())
				{
					continue;
				}
				boolean identical = true;
				for (int j = 0; j < position.size(); j++)
				{
					double cp = p.get(j);
					double cposition = position.get(j);
					if (cposition != cp)
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}

			ArrayList<String> v = variables.get(i);
			if ((v == null && variable != null) || (v != null && variable == null))
			{
				continue;
			}
			if (variable != null)
			{
				if (variable.size() != v.size())
				{
					continue;
				}
				boolean identical = true;
				for (int j = 0; j < variable.size(); j++)
				{
					String cv = v.get(j);
					String cvariable = variable.get(j);
					if (!cv.equals(cvariable))
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}

			ArrayList<String> o = outputs.get(i);
			if ((o == null && output != null) || (o != null && output == null))
			{
				continue;
			}
			if (output != null)
			{
				if (output.size() != o.size())
				{
					continue;
				}
				boolean identical = true;
				for (int j = 0; j < output.size(); j++)
				{
					String co = o.get(j);
					String coutput = output.get(j);
					if (!co.equals(coutput))
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}
			return true;
		}
		return false;
	}

	private boolean alreadyThere(ArrayList<Double> lengths, double length, ArrayList<ArrayList<Double>> positions,
			ArrayList<Double> position, ArrayList<ArrayList<String>> variables, ArrayList<String> variable,
			ArrayList<ArrayList<String>> outputs, ArrayList<String> output, ArrayList<ArrayList<Object>> nodes,
			ArrayList<Object> node)
	{
		if (lengths == null || lengths.isEmpty())
		{
			return false;
		}
		for (int i = 0; i < lengths.size(); i++)
		{
			double l = lengths.get(i);
			if (length != l)
			{
				continue;
			}

			ArrayList<Double> p = positions.get(i);
			if ((p == null && position != null) || (p != null && position == null))
			{
				continue;
			}
			if (position != null)
			{
				if (position.size() != p.size())
				{
					continue;
				}
				boolean identical = true;
				for (int j = 0; j < position.size(); j++)
				{
					double cp = p.get(j);
					double cposition = position.get(j);
					if (cposition != cp)
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}

			ArrayList<String> v = variables.get(i);
			if ((v == null && variable != null) || (v != null && variable == null))
			{
				continue;
			}
			if (variable != null)
			{
				if (variable.size() != v.size())
				{
					continue;
				}
				boolean identical = true;

				for (int j = 0; j < variable.size(); j++)
				{
					String cv = v.get(j);
					String cvariable = variable.get(j);

					// Possible null values comparison avoided.
					if ((cv != null && cvariable == null) || (cv == null && cvariable != null)
							|| (cv != null && cvariable != null && !cv.equals(cvariable)))
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}

			ArrayList<String> o = outputs.get(i);
			if ((o == null && output != null) || (o != null && output == null))
			{
				continue;
			}
			if (output != null)
			{
				if (output.size() != o.size())
				{
					continue;
				}
				boolean identical = true;

				for (int j = 0; j < output.size(); j++)
				{
					String co = o.get(j);
					String coutput = output.get(j);

					// Possible null values comparison avoided.
					if ((co != null && coutput == null) || (co == null && coutput != null)
							|| (co != null && coutput != null && !co.equals(coutput)))
					{
						identical = false;
						break;
					}
				}
				if (!identical)
				{
					continue;
				}
			}

			ArrayList<Object> n = nodes.get(i);
			if ((n == null && node != null) || (n != null && node == null))
			{
				continue;
			}
			if (node != null)
			{
				if (node.size() != n.size())
				{
					continue;
				}
				boolean identical = true;
				for (int j = 0; j < node.size(); j++)
				{
					if (n.get(j) instanceof Integer)
					{
						int cn = (Integer) n.get(j);
						int cnode = (Integer) node.get(j);
						if (cnode != cn)
						{
							identical = false;
							break;
						}
					}
					else
					{
						String cn = (String) n.get(j);
						if (node.get(j) instanceof Integer)
						{
							identical = false;
							break;
						}
						String cnode = (String) node.get(j);

						// Possible null values comparison avoided.
						if ((cn != null && cnode == null) || (cn == null && cnode != null)
								|| (cn != null && cnode != null && !cn.equals(cnode)))
						{
							identical = false;
							break;
						}
					}
				}
				if (!identical)
				{
					continue;
				}
			}
			return true;
		}
		return false;
	}

	// syntactic parser

	public final int syntaxMatch(String graphname, int graphnode, String text, double currentpos, int tunb,
			Mft textmft, ArrayList<Object> annotations, Gram grm, RefObject<ArrayList<Double>> sollengths,
			RefObject<ArrayList<ArrayList<Double>>> solpositions, RefObject<ArrayList<ArrayList<String>>> solvariables,
			RefObject<ArrayList<ArrayList<String>>> soloutputs, RefObject<ArrayList<ArrayList<Object>>> solnodes,
			MatchType typeofmatch, boolean topcall, boolean xmltext, ArrayList<String> recursiveCalls)
	{
		if (epsilon == null)
		{
			setConstants();
		}

		sollengths.argvalue = null;
		solpositions.argvalue = null;
		solvariables.argvalue = null;
		soloutputs.argvalue = null;
		solnodes.argvalue = null;

		Stack<STrace> stack = new Stack<STrace>();
		stack.push(new STrace(currentpos, graphname));

		while (stack.size() > 0)
		{
			STrace curtrc = stack.pop();

			State state = grm.states.get(curtrc.Statenb);
			double cpos = curtrc.Pos;
			if (cpos > text.length()) // might happen if after <$>
			{
				continue;
			}
			ArrayList<Double> inputs = curtrc.Inputs;
			ArrayList<String> outputs = curtrc.Outputs;
			ArrayList<Object> nodes = curtrc.Nodes;
			ArrayList<String> variables = curtrc.Variables;

			double cposafterspaces = cpos;
			RefObject<Double> tempRef_cposafterspaces = new RefObject<Double>(cposafterspaces);
			skipSpaces(text, tempRef_cposafterspaces, xmltext);
			cposafterspaces = tempRef_cposafterspaces.argvalue;
			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int dst = state.Dests.get(itrans);
				int lbl = state.IdLabels.get(itrans);

				// check for <ONCE> in output
				String olabel = grm.vocabOut.get(lbl);
				if (olabel != null && olabel.length() > 5 && olabel.substring(0, 5).equals("<ONCE"))
				{
					boolean foundalready = false;
					for (String oz : outputs)
					{
						if (olabel.equals(oz))
						{
							foundalready = true;
							break;
						}
					}
					if (foundalready)
					{
						continue;
					}
				}

				String ilabel0 = grm.vocabIn.get(lbl);
				String[] ilabel;
				boolean avariable = false;
				ilabel = new String[1];
				ilabel[0] = ilabel0;
				
				for (int ilbl = 0; ilbl < ilabel.length; ilbl++)
				{
					String[] seqlabels = this.iLan.parseSequenceOfTokensAndMetaNodes(ilabel[ilbl]);
					if (seqlabels == null)
					{
						// invalid node complex syntax => skip current label
						continue;
					}

					ArrayList<Double> l2 = null;
					ArrayList<ArrayList<Double>> i2 = null;
					ArrayList<ArrayList<String>> v2 = null;
					ArrayList<ArrayList<String>> o2 = null;
					ArrayList<ArrayList<Object>> n2 = null;
					int da;

					if (seqlabels == null || !avariable)
					{
						RefObject<ArrayList<Double>> tempRef_l2 = new RefObject<ArrayList<Double>>(l2);
						RefObject<ArrayList<ArrayList<Double>>> tempRef_i2 = new RefObject<ArrayList<ArrayList<Double>>>(
								i2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_v2 = new RefObject<ArrayList<ArrayList<String>>>(
								v2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_o2 = new RefObject<ArrayList<ArrayList<String>>>(
								o2);
						RefObject<ArrayList<ArrayList<Object>>> tempRef_n2 = new RefObject<ArrayList<ArrayList<Object>>>(
								n2);

						da = syntaxMatchLabel(state.GraphNodeNumber, ilabel[ilbl], olabel, text, cpos, cposafterspaces,
								tunb, textmft, annotations, tempRef_l2, tempRef_i2, tempRef_v2, tempRef_o2, tempRef_n2,
								xmltext, recursiveCalls, grm);

						l2 = tempRef_l2.argvalue;
						i2 = tempRef_i2.argvalue;
						v2 = tempRef_v2.argvalue;
						o2 = tempRef_o2.argvalue;
						n2 = tempRef_n2.argvalue;
					}
					else
					{
						RefObject<ArrayList<Double>> tempRef_l22 = new RefObject<ArrayList<Double>>(l2);
						RefObject<ArrayList<ArrayList<Double>>> tempRef_i22 = new RefObject<ArrayList<ArrayList<Double>>>(
								i2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_v22 = new RefObject<ArrayList<ArrayList<String>>>(
								v2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_o22 = new RefObject<ArrayList<ArrayList<String>>>(
								o2);
						RefObject<ArrayList<ArrayList<Object>>> tempRef_n22 = new RefObject<ArrayList<ArrayList<Object>>>(
								n2);

						da = syntaxMatchSequenceOfLabels(state.GraphNodeNumber, ilabel[ilbl], seqlabels, olabel, text,
								cpos, cposafterspaces, tunb, textmft, annotations, tempRef_l22, tempRef_i22,
								tempRef_v22, tempRef_o22, tempRef_n22, xmltext, recursiveCalls);

						l2 = tempRef_l22.argvalue;
						i2 = tempRef_i22.argvalue;
						v2 = tempRef_v22.argvalue;
						o2 = tempRef_o22.argvalue;
						n2 = tempRef_n22.argvalue;
					}
					for (int iamb = 0; iamb < da; iamb++)
					{
						double newpos = cpos;
						if (l2 != null)
						{
							newpos += l2.get(iamb);
						}

						// compute the new trace and insert it in the stack
						STrace newtrc = new STrace();
						newtrc.Statenb = dst;
						newtrc.Pos = newpos;
						newtrc.Inputs.addAll(inputs);
						newtrc.Variables.addAll(variables);
						newtrc.Outputs.addAll(outputs);
						newtrc.Nodes.addAll(nodes);

						if (i2 != null)
						{
							newtrc.Inputs.addAll(i2.get(iamb));
							newtrc.Variables.addAll(v2.get(iamb));
							newtrc.Outputs.addAll(o2.get(iamb));
							newtrc.Nodes.addAll(n2.get(iamb));
						}

						if (newtrc.Inputs == null || newtrc.Inputs.size() < 1000)
						{
							stack.push(newtrc);
						}
						if (dst == 1)
						{
							double sollength = newpos - currentpos;

							// add terminal state 1 to Nodes and sync the other arrays
							newtrc.Inputs.add(0.0);
							newtrc.Variables.add(null);
							newtrc.Outputs.add(null);
							newtrc.Nodes.add(1); // add terminal state to nodes

							if (sollengths.argvalue == null)
							{
								sollengths.argvalue = new ArrayList<Double>();
								solpositions.argvalue = new ArrayList<ArrayList<Double>>();
								solvariables.argvalue = new ArrayList<ArrayList<String>>();
								soloutputs.argvalue = new ArrayList<ArrayList<String>>();
								solnodes.argvalue = new ArrayList<ArrayList<Object>>();

								sollengths.argvalue.add(sollength);
								solpositions.argvalue.add(newtrc.Inputs);
								solvariables.argvalue.add(newtrc.Variables);
								soloutputs.argvalue.add(newtrc.Outputs);
								solnodes.argvalue.add(newtrc.Nodes);
							}
							else if (topcall && typeofmatch == MatchType.LONGEST) // longest
							{
								if (sollength > sollengths.argvalue.get(0))
								{
									sollengths.argvalue.set(0, sollength);
									solpositions.argvalue.set(0, newtrc.Inputs);
									solvariables.argvalue.set(0, newtrc.Variables);
									soloutputs.argvalue.set(0, newtrc.Outputs);
									solnodes.argvalue.set(0, newtrc.Nodes);
									int size = sollengths.argvalue.size() - 1;
									if (size > 0)
									{
										sollengths.argvalue.subList(1, size + 1).clear();
										solpositions.argvalue.subList(1, size + 1).clear();
										solvariables.argvalue.subList(1, size + 1).clear();
										soloutputs.argvalue.subList(1, size + 1).clear();
										solnodes.argvalue.subList(1, size + 1).clear();
									}
								}
								else if (sollength == sollengths.argvalue.get(0))
								{
									sollengths.argvalue.add(sollength);
									solpositions.argvalue.add(newtrc.Inputs);
									solvariables.argvalue.add(newtrc.Variables);
									soloutputs.argvalue.add(newtrc.Outputs);
									solnodes.argvalue.add(newtrc.Nodes);
								}
								continue;
							}
							else
							// all matches
							{
								
								{
									sollengths.argvalue.add(sollength);
									solpositions.argvalue.add(newtrc.Inputs);
									solvariables.argvalue.add(newtrc.Variables);
									soloutputs.argvalue.add(newtrc.Outputs);
									solnodes.argvalue.add(newtrc.Nodes);
								}
							}
						}
					}
				}
			}
		}
		if (sollengths.argvalue == null)
		{
			return 0;
		}
		else
		{
			return sollengths.argvalue.size();
		}
	}

	public final int failureSyntaxMatch(String graphname, int graphnode, String text, double currentpos, int tunb,
			Mft textmft, ArrayList<Object> annotations, Gram grm, RefObject<ArrayList<Double>> sollengths,
			RefObject<ArrayList<ArrayList<Double>>> solpositions, RefObject<ArrayList<ArrayList<String>>> solvariables,
			RefObject<ArrayList<ArrayList<String>>> soloutputs, RefObject<ArrayList<ArrayList<Object>>> solnodes,
			MatchType typeofmatch, boolean topcall, boolean xmltext, ArrayList<String> recursiveCalls)
	{
		if (epsilon == null)
		{
			setConstants();
		}

		sollengths.argvalue = null;
		solpositions.argvalue = null;
		solvariables.argvalue = null;
		soloutputs.argvalue = null;
		solnodes.argvalue = null;

		Stack<STrace> stack = new Stack<STrace>();
		stack.push(new STrace(currentpos, graphname));
		while (stack.size() > 0)
		{
			STrace curtrc = stack.pop();

			State state = grm.states.get(curtrc.Statenb);
			double cpos = curtrc.Pos;
			ArrayList<Double> inputs = curtrc.Inputs;
			ArrayList<String> outputs = curtrc.Outputs;
			ArrayList<Object> nodes = curtrc.Nodes;
			ArrayList<String> variables = curtrc.Variables;

			double cposafterspaces = cpos;
			RefObject<Double> tempRef_cposafterspaces = new RefObject<Double>(cposafterspaces);
			skipSpaces(text, tempRef_cposafterspaces, xmltext);
			cposafterspaces = tempRef_cposafterspaces.argvalue;
			for (int itrans = 0; itrans < state.Dests.size(); itrans++)
			{
				int dst = state.Dests.get(itrans);
				int lbl = state.IdLabels.get(itrans);

				// check for <ONCE> in output
				String olabel = grm.vocabOut.get(lbl);
				if (olabel != null && olabel.length() > 5 && olabel.substring(0, 5).equals("<ONCE"))
				{
					boolean foundalready = false;
					for (String oz : outputs)
					{
						if (olabel.equals(oz))
						{
							foundalready = true;
							break;
						}
					}
					if (foundalready)
					{
						continue;
					}
				}

				// check for variable in input, e.g. to recognize <ADV> $ADV in "very very pretty"
				String ilabel0 = grm.vocabIn.get(lbl);
				String[] ilabel;
				boolean avariable = false;
				ilabel = new String[1];
				ilabel[0] = ilabel0;

				for (int ilbl = 0; ilbl < ilabel.length; ilbl++)
				{
					String[] seqlabels = this.iLan.parseSequenceOfTokensAndMetaNodes(ilabel[ilbl]);
					if (seqlabels == null)
					{
						// invalid node complex syntax => skip
						continue;
					}

					ArrayList<Double> l2 = null;
					ArrayList<ArrayList<Double>> i2 = null;
					ArrayList<ArrayList<String>> v2 = null;
					ArrayList<ArrayList<String>> o2 = null;
					ArrayList<ArrayList<Object>> n2 = null;
					int da;

					if (seqlabels == null || !avariable)
					{
						RefObject<ArrayList<Double>> tempRef_l2 = new RefObject<ArrayList<Double>>(l2);
						RefObject<ArrayList<ArrayList<Double>>> tempRef_i2 = new RefObject<ArrayList<ArrayList<Double>>>(
								i2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_v2 = new RefObject<ArrayList<ArrayList<String>>>(
								v2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_o2 = new RefObject<ArrayList<ArrayList<String>>>(
								o2);
						RefObject<ArrayList<ArrayList<Object>>> tempRef_n2 = new RefObject<ArrayList<ArrayList<Object>>>(
								n2);

						da = syntaxFailureMatchLabel(state.GraphNodeNumber, ilabel[ilbl], olabel, text, cpos,
								cposafterspaces, tunb, textmft, annotations, tempRef_l2, tempRef_i2, tempRef_v2,
								tempRef_o2, tempRef_n2, xmltext, recursiveCalls, grm);

						l2 = tempRef_l2.argvalue;
						i2 = tempRef_i2.argvalue;
						v2 = tempRef_v2.argvalue;
						o2 = tempRef_o2.argvalue;
						n2 = tempRef_n2.argvalue;
					}

					else
					{
						RefObject<ArrayList<Double>> tempRef_l22 = new RefObject<ArrayList<Double>>(l2);
						RefObject<ArrayList<ArrayList<Double>>> tempRef_i22 = new RefObject<ArrayList<ArrayList<Double>>>(
								i2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_v22 = new RefObject<ArrayList<ArrayList<String>>>(
								v2);
						RefObject<ArrayList<ArrayList<String>>> tempRef_o22 = new RefObject<ArrayList<ArrayList<String>>>(
								o2);
						RefObject<ArrayList<ArrayList<Object>>> tempRef_n22 = new RefObject<ArrayList<ArrayList<Object>>>(
								n2);

						da = syntaxFailureMatchSequenceOfLabels(state.GraphNodeNumber, ilabel[ilbl], seqlabels, olabel,
								text, cpos, cposafterspaces, tunb, textmft, annotations, tempRef_l22, tempRef_i22,
								tempRef_v22, tempRef_o22, tempRef_n22, xmltext, recursiveCalls);

						l2 = tempRef_l22.argvalue;
						i2 = tempRef_i22.argvalue;
						v2 = tempRef_v22.argvalue;
						o2 = tempRef_o22.argvalue;
						n2 = tempRef_n22.argvalue;
					}

					for (int iamb = 0; iamb < Math.abs(da); iamb++)
					{
						double newpos = cpos;
						if (l2 != null)
						{
							newpos += l2.get(iamb);
						}

						// compute the new trace and insert it in the stack
						STrace newtrc = new STrace();
						newtrc.Statenb = dst;
						newtrc.Pos = newpos;
						newtrc.Inputs.addAll(inputs);
						newtrc.Variables.addAll(variables);
						newtrc.Outputs.addAll(outputs);
						newtrc.Nodes.addAll(nodes);

						if (i2 != null)
						{
							newtrc.Inputs.addAll(i2.get(iamb));
							newtrc.Variables.addAll(v2.get(iamb));
							newtrc.Outputs.addAll(o2.get(iamb));
							newtrc.Nodes.addAll(n2.get(iamb));
						}
						if (dst == 1 && da > 0) 
						{
							// add terminal state to nodes and sync with other arrays
							newtrc.Inputs.add(cpos);
							newtrc.Variables.add(null);
							newtrc.Outputs.add(null);
							newtrc.Nodes.add(1);
						}

						if (da > 0 && (newtrc.Inputs == null || newtrc.Inputs.size() < 1000))
						{
							stack.push(newtrc);
						}
						double sollength = newpos - currentpos;

						if (sollengths.argvalue == null)
						{
							sollengths.argvalue = new ArrayList<Double>();
							solpositions.argvalue = new ArrayList<ArrayList<Double>>();
							solvariables.argvalue = new ArrayList<ArrayList<String>>();
							soloutputs.argvalue = new ArrayList<ArrayList<String>>();
							solnodes.argvalue = new ArrayList<ArrayList<Object>>();

							sollengths.argvalue.add(sollength);
							solpositions.argvalue.add(newtrc.Inputs);
							solvariables.argvalue.add(newtrc.Variables);
							soloutputs.argvalue.add(newtrc.Outputs);
							solnodes.argvalue.add(newtrc.Nodes);
						}
						else if (typeofmatch == MatchType.LONGEST) // longest
						{
							if (sollength > sollengths.argvalue.get(0)
									|| (newtrc.Nodes).size() > (solnodes.argvalue.get(0)).size())
							{
								sollengths.argvalue.set(0, sollength);
								solpositions.argvalue.set(0, newtrc.Inputs);
								solvariables.argvalue.set(0, newtrc.Variables);
								soloutputs.argvalue.set(0, newtrc.Outputs);
								solnodes.argvalue.set(0, newtrc.Nodes);
								int size = sollengths.argvalue.size() - 1;
								if (size > 0)
								{
									sollengths.argvalue.subList(1, size + 1).clear();
									solpositions.argvalue.subList(1, size + 1).clear();
									solvariables.argvalue.subList(1, size + 1).clear();
									soloutputs.argvalue.subList(1, size + 1).clear();
									solnodes.argvalue.subList(1, size + 1).clear();
								}
							}
							else if (sollength == sollengths.argvalue.get(0))
							{
								if (!alreadyThere(sollengths.argvalue, sollength, solpositions.argvalue, newtrc.Inputs,
										solvariables.argvalue, newtrc.Variables, soloutputs.argvalue, newtrc.Outputs,
										solnodes.argvalue, newtrc.Nodes))
								{
									sollengths.argvalue.add(sollength);
									solpositions.argvalue.add(newtrc.Inputs);
									solvariables.argvalue.add(newtrc.Variables);
									soloutputs.argvalue.add(newtrc.Outputs);
									solnodes.argvalue.add(newtrc.Nodes);
								}
							}
							continue;
						}
						else
						// all matches
						{
							if (!alreadyThereFailure(sollengths.argvalue, sollength, solpositions.argvalue,
									newtrc.Inputs, solvariables.argvalue, newtrc.Variables, soloutputs.argvalue,
									newtrc.Outputs))
							{
								sollengths.argvalue.add(sollength);
								solpositions.argvalue.add(newtrc.Inputs);
								solvariables.argvalue.add(newtrc.Variables);
								soloutputs.argvalue.add(newtrc.Outputs);
								solnodes.argvalue.add(newtrc.Nodes);
							}
						}
					}
				}
			}
		}
		if (sollengths.argvalue == null)
		{
			return 0;
		}
		else
		{
			return sollengths.argvalue.size();
		}
	}

	public static Grammar loadONooJGrammar(String fullname)
	{
		String line, line2;
		String sep = " ";
		String sep2 = ":";
		String[] fields = null;
		String[] fparams = null;

		// Value Initialization
		int nbofgraphs = 0;
		Dimension size = new Dimension(0, 0);
		int scale = 0;

		Grammar gram = new Grammar();
		gram.fullName = fullname;

		try
		{
			FileInputStream sr = new FileInputStream(fullname);
			BufferedReader reader = new BufferedReader(new InputStreamReader(sr, "UTF8"));

			// Header
			line = reader.readLine();
			if (!line.equals("#OpenNooJ 1.0"))
			{
				reader.close();
				sr.close();

				return null;
			}

			while (true)
			{
				line = reader.readLine();
				if (line.equals("#"))
					break;

				fields = line.split(sep);

				if (fields[0].equals("TYPE"))
				{
					if (fields[1].equals("SYNTAX"))
						gram.gramType = GramType.SYNTAX;
					else if (fields[1].equals("INFLECTION"))
						gram.gramType = GramType.FLX;
					else if (fields[1].equals("MORPHOLOGY"))
						gram.gramType = GramType.MORPHO;
				}
				else if (fields[0].equals("ILANGUAGE"))
				{
					gram.iLan = new Language(fields[1]);
					gram.iLanguage = gram.iLan.isoName;
				}
				else if (fields[0].equals("OLANGUAGE"))
				{
					gram.oLanguage = fields[1];
				}
				else if (fields[0].equals("NUMBEROFGRAPHS"))
				{
					nbofgraphs = Integer.parseInt(fields[1]);
				}
				else if (fields[0].equals("SIZE"))
				{
					size = new Dimension(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
				}
				else if (fields[0].equals("SCALE"))
				{
					scale = Integer.parseInt(fields[1]);
				}
				else if (fields[0].equals("IFONT"))
				{
					// parse FontName:FontStyle:FontSize
					line2 = line.substring(fields[0].length() + 1);
					fparams = line2.split(sep2);

					gram.iFontName = fparams[0];
					gram.iFontStyle = FontStyle.NORMAL.ordinal(); // default

					if (fparams[1].equals("B"))
						gram.iFontStyle = FontStyle.BOLD.ordinal();
					else if (fparams[1].equals("I"))
						gram.iFontStyle = FontStyle.ITALIC.ordinal();

					gram.iFontSize = Integer.parseInt(fparams[2]);
				}
				else if (fields[0].equals("OFONT"))
				{
					// parse FontName:FontStyle:FontSize
					line2 = line.substring(fields[0].length() + 1);
					fparams = line2.split(sep2);

					gram.oFontName = fparams[0];
					gram.oFontStyle = FontStyle.NORMAL.ordinal();

					if (fparams[1].equals("B"))
						gram.oFontStyle = FontStyle.BOLD.ordinal();
					else if (fparams[1].equals("I"))
						gram.oFontStyle = FontStyle.ITALIC.ordinal();

					gram.oFontSize = Integer.parseInt(fparams[2]);
				}
				else if (fields[0].equals("CFONT"))
				{
					// parse FontName:FontStyle:FontSize
					line2 = line.substring(fields[0].length() + 1);
					fparams = line2.split(sep2);

					gram.cFontName = fparams[0];
					gram.cFontStyle = FontStyle.NORMAL.ordinal();

					if (fparams[1].equals("B"))
						gram.cFontStyle = FontStyle.BOLD.ordinal();
					else if (fparams[1].equals("I"))
						gram.cFontStyle = FontStyle.ITALIC.ordinal();

					gram.cFontSize = Integer.parseInt(fparams[2]);
				}
				else if (fields[0].equals("AUXICOLOR"))
				{
					gram.aColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("BACKCOLOR"))
				{
					gram.bColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("COMMCOLOR"))
				{
					gram.cColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("FORECOLOR"))
				{
					gram.fColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("SELECOLOR"))
				{
					gram.sColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("VARCOLOR"))
				{
					gram.vColor = Graph.convertColor(Integer.parseInt(fields[1]));
				}
				else if (fields[0].equals("DISPFRAME"))
				{
					gram.dispFrame = (fields[1].equals("y"));
				}
				else if (fields[0].equals("DISPDATE"))
				{
					gram.dispDate = (fields[1].equals("y"));
				}
				else if (fields[0].equals("DISPFILENAME"))
				{
					gram.dispFile = (fields[1].equals("y"));
				}
				else if (fields[0].equals("DISPDIRNAME"))
				{
					gram.dispDir = (fields[1].equals("y"));
				}
			}

			for (int i = 0; i < nbofgraphs; i++)
			{
				Graph grf = new Graph(size);
				grf.wholeGrammar = gram;
				grf.fits = scale;
				grf.loadONooJ(reader);
				gram.graphs.add(grf);
			}

			// load contract
			String helperLine = null;
			StringBuilder checkText = new StringBuilder();
			while ((helperLine = reader.readLine()) != null)
			{
				checkText.append(helperLine);
				checkText.append('\n');
			}

			gram.checkText = checkText.toString();
			reader.close();

			gram.isTextual = false;
			return gram;
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_FILE_NOT_FOUND + fullname,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void saveONooJGrammar(String fullname) // Save Open Source NooJ Graph
	{
		String fstyle;
		int color;

		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullname, false),
					"UTF8"));

			// Header
			writer.write("#OpenNooJ 1.0");
			writer.write('\n'); // This is necessary, because StreamWriter.WriteLine terminates the line.

			// Type
			if (this.gramType == GramType.SYNTAX)
			{
				writer.write("TYPE SYNTAX");
				writer.write('\n');
			}
			else if (this.gramType == GramType.FLX)
			{
				writer.write("TYPE INFLECTION");
				writer.write('\n');
			}
			else
		
			{
				writer.write("TYPE MORPHOLOGY");
				writer.write('\n');
			}

			// Language
			writer.write("ILANGUAGE " + this.iLan.isoName);
			writer.write('\n');

			writer.write("OLANGUAGE " + this.oLanguage);
			writer.write('\n');

			writer.write("NUMBEROFGRAPHS " + this.graphs.size());
			writer.write('\n');

			Graph g = this.graphs.get(0);
			writer.write("SIZE " + g.size.width + " " + g.size.height);
			writer.write('\n');
			writer.write("SCALE " + g.fits);
			writer.write('\n');

			// Fonts
			fstyle = "R";
			if (this.iFontStyle == FontStyle.BOLD.ordinal())
				fstyle = "B";
			else if (this.iFontStyle == FontStyle.ITALIC.ordinal())
				fstyle = "I";
			writer.write("IFONT " + this.iFontName + ":" + fstyle + ":" + (int) this.iFontSize);
			writer.write('\n');

			fstyle = "R";
			if (this.oFontStyle == FontStyle.BOLD.ordinal())
				fstyle = "B";
			else if (this.oFontStyle == FontStyle.ITALIC.ordinal())
				fstyle = "I";
			writer.write("OFONT " + this.oFontName + ":" + fstyle + ":" + (int) this.oFontSize);
			writer.write('\n');

			fstyle = "R";
			if (this.cFontStyle == FontStyle.BOLD.ordinal())
				fstyle = "B";
			else if (this.cFontStyle == FontStyle.ITALIC.ordinal())
				fstyle = "I";
			writer.write("CFONT " + this.cFontName + ":" + fstyle + ":" + (int) this.cFontSize);
			writer.write('\n');

			// Colors
			color = (int) Graph.convertColor(this.aColor);
			writer.write("AUXICOLOR " + color);
			writer.write('\n');
			color = (int) Graph.convertColor(this.bColor);
			writer.write("BACKCOLOR " + color);
			writer.write('\n');
			color = (int) Graph.convertColor(this.cColor);
			writer.write("COMMCOLOR " + color);
			writer.write('\n');
			color = (int) Graph.convertColor(this.fColor);
			writer.write("FORECOLOR " + color);
			writer.write('\n');
			color = (int) Graph.convertColor(this.sColor);
			writer.write("SELECOLOR " + color);
			writer.write('\n');
			color = (int) Graph.convertColor(this.vColor);
			writer.write("VARCOLOR " + color);
			writer.write('\n');

			// Presentation
			writer.write("DISPFRAME " + (this.dispFrame ? "y" : "n"));
			writer.write('\n');
			writer.write("DISPDATE " + (this.dispDate ? "y" : "n"));
			writer.write('\n');
			writer.write("DISPFILENAME " + (this.dispFile ? "y" : "n"));
			writer.write('\n');
			writer.write("DISPDIRNAME " + (this.dispDir ? "y" : "n"));
			writer.write('\n');

			// End of headers
			writer.write("#");
			writer.write('\n');

			// each graph network
			for (Graph grf : this.graphs)
			{
				grf.saveONooJGraph(grf.name, writer);
			}

			// Contract
			writer.write(this.checkText);
			writer.write('\n');

			writer.close();
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_FILE_NOT_FOUND + fullname,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (UnsupportedEncodingException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					Constants.ERROR_MESSAGE_TITLE_UNSUPPORTED_ENCODING, Constants.NOOJ_APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR,
					Constants.NOOJ_APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public ArrayList<Object> getExtraParams()
	{
		return extraParams;
	}

	public void setExtraParams(ArrayList<Object> extraParams)
	{
		this.extraParams = extraParams;
	}
}