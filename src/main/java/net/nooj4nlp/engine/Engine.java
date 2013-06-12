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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import net.nooj4nlp.engine.helper.BackgroundWorker;
import net.nooj4nlp.engine.helper.ParameterCheck;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author Silberztein Max
 * 
 */
public class Engine
{
	public Language Lan;

	private HashMap<String, Gram> paradigms;
	private String applicationDir;
	public String docDir;
	private String projectDir;
	private boolean projectMode;

	public boolean BackgroundWorking;
	public BackgroundWorker backgroundWorker;
	public Preferences preferences;

	public boolean ResourcesLoaded;

	/**
	 * 
	 * @param lexresources
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private final int loadNodResources(ArrayList<String> lexresources, RefObject<String> errmessage)
			throws IOException, ClassNotFoundException
	{
		// load only .jnod resources to get the inflectional/derivational paradigms
		int cpt = 0;
		errmessage.argvalue = null;

		if (lexresources == null)
		{
			return 0;
		}
		for (String prefname0 : lexresources)
		{

			String prefname = new File(prefname0).getName();
			String prio = prefname.substring(0, 2);
			int priority = 0;
			try
			{
				priority = Integer.parseInt(prio);
			}
			catch (java.lang.Exception e)
			{
				errmessage.argvalue = "Cannot parse priority for file " + prefname;
				Dic.writeLog(errmessage.argvalue);
				return -1;
			}

			String fullname;
			if (docDir != null)
			{
				String lname = this.Lan.isoName;
				String fname = prefname.substring(2);
				String dname;
				if (this.projectMode)
				{
					dname = FilenameUtils.concat(this.projectDir, FilenameUtils.concat(lname, "Lexical Analysis"));
					fullname = FilenameUtils.concat(dname, prefname);
				}
				else
				{
					dname = FilenameUtils.concat(docDir, FilenameUtils.concat(lname, "Lexical Analysis"));
					fullname = FilenameUtils.concat(dname, fname);
				}
			}
			else
			{
				String dname = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(prefname0));
				fullname = FilenameUtils.concat(dname, prefname.substring(2));
			}
			if (!(new File(fullname)).isFile())
			{
				errmessage.argvalue = "Cannot find file " + fullname;
				Dic.writeLog(errmessage.argvalue);
				return -1;
			}

			String ext = FilenameUtils.getExtension(fullname);
			if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
			{
				FSDic[] dics = FSDic.load(fullname, this, errmessage);
				if (dics == null)
				{
					errmessage.argvalue = "Cannot load dictionary " + fullname;
					Dic.writeLog(errmessage.argvalue);
					return -1;
				}
				else
				{
					for (FSDic dic : dics)
					{
						if (dic != null)
						{
							cpt++;
							lexBins.add(dic);
							lexBins.add(priority);
							if (dic.paradigms != null)
							{
								for (String expname : dic.paradigms.keySet())
								{
									if (!paradigms.containsKey(expname))
									{
										paradigms.put(expname, dic.paradigms.get(expname));
									}
								}
							}
						}
					}
				}
			}
		}
		ResourcesLoaded = true;
		return cpt;
	}

	/**
	 * 
	 * @param lexresources
	 * @param synresources
	 * @param handlelexicongrammarpairs
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final boolean loadResources(ArrayList<String> lexresources, ArrayList<String> synresources,
			boolean handlelexicongrammarpairs, RefObject<String> errmessage) throws IOException, ClassNotFoundException
	{
		errmessage.argvalue = null;

		// load lexical resources
		if (lexresources == null)
		{
			errmessage.argvalue = "no lexical resource is provided";
			Dic.writeLog(errmessage.argvalue);
			return false;
		}
		for (String prefname0 : lexresources)
		{
			String prefname = new File(prefname0).getName();
			String prio = prefname.substring(0, 2);
			int priority = 0;
			try
			{
				priority = Integer.parseInt(prio);
			}
			catch (java.lang.Exception e)
			{
				errmessage.argvalue = "Cannot parse priority for file " + prefname;
				Dic.writeLog(errmessage.argvalue);
				return false;
			}

			String fullname;
			if (docDir != null) // in NooJ
			{
				String lname = this.Lan.isoName;
				String fname = prefname.substring(2);
				String dname;
				if (this.projectMode)
				{
					// everything in \en, *NOT* in \en\Lexical Analysis
					dname = FilenameUtils.concat(this.projectDir, FilenameUtils.concat(lname, "Lexical Analysis"));
					if (!(new File(dname)).isDirectory())
					{
						dname = FilenameUtils.concat(this.projectDir, lname);
					}
					fullname = FilenameUtils.concat(dname, prefname);
				}
				else
				{
					dname = FilenameUtils.concat(docDir, FilenameUtils.concat(lname, "Lexical Analysis"));
					fullname = FilenameUtils.concat(dname, fname);
				}
			}
			else
			// in noojapply
			{
				String dname = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(prefname0));
				fullname = FilenameUtils.concat(dname, prefname.substring(2));
			}

			File newFile = new File(fullname);
			if (!newFile.isFile())
			{
				errmessage.argvalue = "Cannot find resource file " + fullname;
				Dic.writeLog(errmessage.argvalue);
				return false;
			}

			String ext = FilenameUtils.getExtension(fullname);
			if (ext.equalsIgnoreCase(Constants.JNOD_EXTENSION))
			{
				FSDic[] dics = null;
				dics = FSDic.load(fullname, this, errmessage);
				if (dics == null)
				{
					errmessage.argvalue = "Cannot load dictionary binary file " + fullname;
					Dic.writeLog(errmessage.argvalue);
					return false;
				}
				for (FSDic dic : dics)
				{
					if (dic != null)
					{
						lexBins.add(dic);
						lexBins.add(priority);
						if (dic.paradigms != null)
						{
							for (String expname : dic.paradigms.keySet())
							{
								if (!paradigms.containsKey(expname))
								{
									paradigms.put(expname, dic.paradigms.get(expname));
								}
							}
						}
					}
				}
				if (handlelexicongrammarpairs)
				{
					String lg = FilenameUtils.concat(
							FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(fullname)),
							FilenameUtils.getBaseName(fullname) + "." + Constants.JNOG_EXTENSION);
					if ((new File(lg)).isFile())
					{
						
						Grammar grm = Grammar.loadONooJGrammar(lg);
						if (grm != null)
						{
							if (grm.compileAll(this) == null)
							{
								synGrms.add(grm);
								synGrms.add(0);
								synGrms.add('X');
							}
						}
					}
				}
			}
			else if (ext.equals("nom") || ext.equals("NOM"))
			{
				Grammar grm = null;
				boolean istextual = Grammar.isItTextual(fullname);
				if (istextual)
				{
					grm = Grammar.loadTextual(fullname, GramType.MORPHO, errmessage);
					if (errmessage.argvalue != null)
					{
						errmessage.argvalue = "Problem in grammar " + fullname + ":\n" + errmessage.argvalue;
						Dic.writeLog(errmessage.argvalue);
						return false;
					}
				}
				else
				{
					grm=Grammar.loadONooJGrammar(fullname);
					
				}
				if (grm != null)
				{
					if (grm.compileAll(this) == null)
					{
						lexGrms.add(grm);
						lexGrms.add(priority);
					}
				}
			}
		}

		// load syntactic resources
		if (synresources == null)
		{
			return true;
		}
		for (String prefname0 : synresources)
		{
			String prefname = new File(prefname0).getName();
			String prio = prefname.substring(0, 2);
			int priority;
			try
			{
				priority = Integer.parseInt(prio);
			}
			catch (java.lang.Exception e2)
			{
				errmessage.argvalue = "Cannot parse order number for resource file " + prefname;
				Dic.writeLog(errmessage.argvalue);
				return false;
			}

			String fullname;
			if (docDir != null)
			{
				String lname = this.Lan.isoName;
				String dname;
				if (this.projectMode)
				{
					// everything in \en, *NOT* in \en\Syntactic Analysis
					dname = FilenameUtils.concat(this.projectDir, FilenameUtils.concat(lname, "Syntactic Analysis"));
					if (!(new File(dname)).isDirectory())
					{
						dname = FilenameUtils.concat(this.projectDir, lname);
					}
					fullname = FilenameUtils.concat(dname, prefname);
				}
				else
				{
					String fname = prefname.substring(2);
					dname = FilenameUtils.concat(docDir, FilenameUtils.concat(lname, "Syntactic Analysis"));
					fullname = FilenameUtils.concat(dname, fname);
				}
			}
			else
			{
				String dname = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(prefname0));
				fullname = FilenameUtils.concat(dname, prefname.substring(2));
			}
			if (!(new File(fullname)).isFile())
			{
				errmessage.argvalue = "Cannot find file " + fullname;
				Dic.writeLog(errmessage.argvalue);
				return false;
			}

			String ext = FilenameUtils.getExtension(fullname);
			String fn = FilenameUtils.getBaseName(fullname);
			char cfn = '\0';
			if (fn.length() > 2 && fn.charAt(fn.length() - 2) == '-')
			{
				cfn = fn.charAt(fn.length() - 1);
			}
			if (ext.equalsIgnoreCase(Constants.JNOG_EXTENSION))
			{
				Grammar grm = null;
				boolean istextual = Grammar.isItTextual(fullname);
				if (istextual)
				{
					grm = Grammar.loadTextual(fullname, GramType.SYNTAX, errmessage);
					if (errmessage.argvalue != null)
					{
						return false;
					}
				}
				else
				{
					grm=Grammar.loadONooJGrammar(fullname);
					
				}
				if (grm != null)
				{
					if (grm.compileAll(this) == null)
					{
						synGrms.add(grm);
						synGrms.add(priority);
						if (cfn == 'A')
						{
							synGrms.add('A');
						}
						else if (cfn == 'S')
						{
							synGrms.add('S');
						}
						else
						{
							synGrms.add('L');
						}
					}
				}
			}
			else if (ext.equals("nox") || ext.equals("NOX"))
			{

				String rawBuffer = FileUtils.readFileToString(new File(fullname), "UTF-8");

				Regexp regexp = new Regexp(this.Lan, rawBuffer, GramType.SYNTAX);
				if (regexp.Grm != null)
				{
					regexp.Grm.prepareForParsing();
					Regexps regexps = new Regexps(this.Lan, GramType.SYNTAX, regexp.Grm, this);
					Grammar grm = regexps.grammar;
					if (grm != null)
					{
						synGrms.add(grm);
						synGrms.add(priority);
						if (cfn == 'A')
						{
							synGrms.add('A');
						}
						else if (cfn == 'S')
						{
							synGrms.add('S');
						}
						else
						{
							synGrms.add('L');
						}
					}
				}

			}
		}
		ResourcesLoaded = true;
		return true;
	}

	HashMap<String, String> properties;

	private HashMap<String, Boolean> prop_cat;
	private HashMap<String, Boolean> prop_inf;

	/**
	 * 
	 * @param errmessage
	 * @return
	 */
	private final boolean loadCategoryPropertiesFeatures(RefObject<String> errmessage)
	{
		this.prop_inf = null;
		this.prop_cat = null;
		this.properties = null;
		errmessage.argvalue = null;

		String propertydefinitionfile;

		if (this.projectMode)
		{
			propertydefinitionfile = FilenameUtils.concat(Paths.projectDir,
					FilenameUtils.concat(this.Lan.isoName, FilenameUtils.concat("Lexical Analysis", "_properties.def")));
			if (!(new File(propertydefinitionfile)).isFile())
			{
				propertydefinitionfile = FilenameUtils.concat(
						Paths.projectDir,
						FilenameUtils.concat(this.Lan.isoName,
								FilenameUtils.concat("Lexical Analysis", "properties.def")));
				if (!(new File(propertydefinitionfile)).isFile())
				{
					errmessage.argvalue = "Cannot find Property Definition File: " + propertydefinitionfile;
					Dic.writeLog(errmessage.argvalue);
					return false;
				}
			}
		}
		else
		{
			propertydefinitionfile = FilenameUtils.concat(docDir,
					FilenameUtils.concat(this.Lan.isoName, FilenameUtils.concat("Lexical Analysis", "properties.def")));
			if (!(new File(propertydefinitionfile)).isFile())
			{
				propertydefinitionfile = FilenameUtils.concat(
						docDir,
						FilenameUtils.concat(this.Lan.isoName,
								FilenameUtils.concat("Lexical Analysis", "_properties.def")));
				if (!(new File(propertydefinitionfile)).isFile())
				{
					errmessage.argvalue = "Cannot find Property Definition File: " + propertydefinitionfile;
					Dic.writeLog(errmessage.argvalue);
					return false;
				}
			}
		}
		BufferedReader bufferedReader = null;
		try
		{
		
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(propertydefinitionfile),
					"UTF8"));
			String header = bufferedReader.readLine();
			if (!header.equals("# NooJ V1") && !header.equals("# NooJ V2") && !header.equals("# NooJ V3")&& !header.equals("# NooJ V4"))
			{
				errmessage.argvalue = "Property Definition File Format is invalid in " + propertydefinitionfile;
				Dic.writeLog(errmessage.argvalue);
				return false;
			}

			// load the file
			// ATTENTION: cannot use sr.ReadLine because sometimes, a few \x0d are weirdly inserted
			// in text. I Need to correct that:
			String rawBuffer = "";
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				rawBuffer += line;
				rawBuffer += "\n";
			}
			String buf = Dic.noComment(rawBuffer);

			int end;
			for (int ibuf = 0; ibuf < buf.length(); ibuf = end)
			{
				String category = null, property = null;
				String[] features = null;
				RefObject<String> tempRef_category = new RefObject<String>(category);
				RefObject<String> tempRef_property = new RefObject<String>(property);
				RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
				end = Dic.getRule(buf, ibuf, tempRef_category, tempRef_property, tempRef_features, errmessage);
				category = tempRef_category.argvalue;
				property = tempRef_property.argvalue;
				features = tempRef_features.argvalue;
				if (end == -1)
				{
					if (errmessage.argvalue != null)
					{
						return false;
					}
					break;
				}

				if (category.equals("INFLECTION"))
				{
					if (this.prop_inf == null)
					{
						this.prop_inf = new HashMap<String, Boolean>();
					}
					for (int ifeat = 0; ifeat < features.length; ifeat++)
					{
						if (!this.prop_inf.containsKey(features[ifeat]))
						{
							this.prop_inf.put(features[ifeat], true);
						}
					}
				}
				else
				{
					if (this.prop_cat == null)
					{
						this.prop_cat = new HashMap<String, Boolean>();
					}
					if (!this.prop_cat.containsKey(category + "_" + property))
					{
						this.prop_cat.put(category + "_" + property, true);
					}
					for (int ifeat = 0; ifeat < features.length; ifeat++)
					{
						if (this.properties == null)
						{
							this.properties = new HashMap<String, String>();
						}
						String ncat = category + "_" + features[ifeat];
						if (!this.properties.containsKey(ncat))
						{
							properties.put(ncat, property);
						}
					}
				}
			}
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param lan
	 * @param appdir
	 * @param docdir
	 * @param projdir
	 * @param projectmode
	 * @param prefs
	 * @param backgroundworking
	 * @param backgroundworker
	 * @throws IOException
	 */

	public Engine(RefObject<Language> lan, String appdir, String docdir, String projdir, boolean projectmode,
			Preferences prefs, boolean backgroundworking, BackgroundWorker backgroundworker)
	{
		this.Lan = lan.argvalue;
		applicationDir = appdir;
		docDir = docdir;
		projectDir = projdir;
		projectMode = projectmode;

		// if GUI
		BackgroundWorking = backgroundworking;
		backgroundWorker = backgroundworker;
		preferences = prefs;

		lexBins = new ArrayList<Object>();
		lexGrms = new ArrayList<Object>();
		paradigms = new HashMap<String, Gram>();

		
		synGrms = new ArrayList<Object>();

		recursiveMorphology = null;
		engine2 = null;

		// attempt to load _properties.def
		String errmessage = null;
		RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
		loadCategoryPropertiesFeatures(tempRef_errmessage);
		errmessage = tempRef_errmessage.argvalue;
		if (errmessage != null)
		{
			System.out.println(errmessage);
		}
		// attempt to load characters' variation table (not only for zh or vi)
		String chartname = FilenameUtils.concat(docDir,
				FilenameUtils.concat(Lan.isoName, FilenameUtils.concat("Lexical Analysis", "charvariants.txt")));
		if (!(new File(chartname)).isFile())
		{
			chartname = FilenameUtils.concat(docDir,
					FilenameUtils.concat(Lan.isoName, FilenameUtils.concat("Lexical Analysis", "_charvariants.txt")));
			if (!(new File(chartname)).isFile())
			{
				return;
			}
		}
		StringBuilder errmessage2t = new StringBuilder("");
		boolean tempVar = false;
		try
		{
			tempVar = !Lan.loadCharacterVariants(chartname, errmessage2t);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		if (tempVar)
		{
			System.out.println(errmessage2t.toString());
		}
	}

	/**
	 * 
	 * @param lan
	 */

	private static class xmlnode implements Comparable<Object>
	{
		private int beg, end;

		/**
		 * 
		 * @param b
		 * @param e
		 */
		private xmlnode(int b, int e)
		{
			beg = b;
			end = e;
		}

		/**
		 * 
		 */
		@Override
		public int compareTo(Object o)
		{
			xmlnode other = (xmlnode) o;
			if (beg < other.beg)
			{
				return -1;
			}
			else if (beg > other.beg)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public final Mft delimit(Ntext text)
	{
		Mft mft = null;

		// parse the text buffer into text units
		if (text.DelimPattern == null || text.DelimPattern.equals(""))
		{
			if (text.buffer.length() > UnsignedShort.MAX_VALUE)
			{
				return null;
			}
			text.nbOfTextUnits = 1;
			mft = new Mft(1);
			mft.tuAddresses[1] = 0;
			mft.tuLengths[1] = text.buffer.length();
		}
		else
		{
			Pattern rexp = Pattern.compile(text.DelimPattern, Pattern.MULTILINE);
			Matcher mc = rexp.matcher(text.buffer);
			int groupCount = 0;
			while (mc.find())
			{
				groupCount++;
			}
			mc = rexp.matcher(text.buffer);
			mft = new Mft(groupCount + 1);
			int itu = 1;
			int strt = 0;
			int end;
			int len = 0;
			boolean needtoresize = false;
			while (mc.find())
			{
				mc.group();
				end = mc.start();
				len = mc.end() - end;

				// is there anything in the text unit?
				boolean foundsomething = false;
				for (int i = strt; i < end; i++)
				{
					if (!Character.isWhitespace(text.buffer.charAt(i)))
					{
						foundsomething = true;
					}
				}

				if (foundsomething)
				{
					needtoresize = true;
					mft.tuAddresses[itu] = strt;
					mft.tuLengths[itu] = end - strt;
					itu++;
				}
				strt = end + len;
			}
			mft.tuAddresses[itu] = strt;
			mft.tuLengths[itu] = text.buffer.length() - strt;
			if (needtoresize)
			{
				mft.reSize(itu);
			}
			text.nbOfTextUnits = itu;
		}
		return mft;
	}

	/**
	 * 
	 * @param mytext
	 * @return
	 */
	public final String delimitTextUnits(Ntext mytext)
	{
		// delimit text units
		return mytext.delimitTextUnits(this);
	}

	/**
	 * 
	 * @param text
	 * @param tags
	 * @param errmessage
	 * @return
	 */
	public final Mft delimitXml(Ntext text, String[] tags, RefObject<String> errmessage) // XML
	{
		errmessage.argvalue = null;
		if (tags == null || tags.length == 0)
		{
			errmessage.argvalue = "no XML tag for text unit?";
			Dic.writeLog(errmessage.argvalue);
			return null;
		
		}

		ArrayList<xmlnode> nodes = new ArrayList<xmlnode>();
		for (String tag : tags)
		{
			StringBuilder perltag0 = new StringBuilder();
			for (int i = 0; i < tag.length() - 1; i++)
			{
				if (tag.charAt(i) == '"' || tag.charAt(i) == '\'')
				{
					perltag0.append('\\');
				}
				perltag0.append(tag.charAt(i));
			}
			String perltag = perltag0.toString() + "(>| [^>]*>)";
			Pattern begexp = Pattern.compile(perltag, Pattern.MULTILINE);
			Matcher begExpMatcher = begexp.matcher(text.buffer);
		
			int begexpGroupCount = 0;
			while (begExpMatcher.find())
			{
				begexpGroupCount++;
			}
			if (begexpGroupCount == 0)
			{
				errmessage.argvalue = "Cannot find any opening tag for TUs";
				Dic.writeLog(errmessage.argvalue);
				return null;
			}
			String tagending;
			int index = tag.indexOf(' ');
			if (index == -1)
			{
				tagending = tag.substring(0, tag.length() - 1);
			}
			else
			{
				tagending = tag.substring(0, index);
			}

			String endtag = "</" + tagending.substring(1) + ">";
			Pattern endexp = Pattern.compile(endtag, Pattern.MULTILINE);
			Matcher endExpMatcher = endexp.matcher(text.buffer);
			
			int endexpGroupCount = 0;
			while (endExpMatcher.find())
			{
				endexpGroupCount++;
			}
			if (endexpGroupCount == 0)
			{
				errmessage.argvalue = "Cannot find any ending tag for TUs";
				Dic.writeLog(errmessage.argvalue);
				return null;
			}
			if (begexpGroupCount != endexpGroupCount)
			{
				errmessage.argvalue = "Numbers of beginning tags (" + begexpGroupCount + ") and ending tags ("
						+ endexpGroupCount + ") are not equal";
				Dic.writeLog(errmessage.argvalue);
				return null;
			}
			begExpMatcher = begexp.matcher(text.buffer);
			while (begExpMatcher.find())
			{
				endExpMatcher = endexp.matcher(text.buffer);
				int strt = begExpMatcher.end();

				while (endExpMatcher.find())
				{
					int end = endExpMatcher.start();

					if (end < strt)
					{
						continue;
					}
					if (end > strt)
					{
						xmlnode node = new xmlnode(strt, end);
						nodes.add(node);
					}
				}
			}
		}
		if (nodes.isEmpty()) // cannot find even one <TU>... </TU>
		{
			errmessage.argvalue = "Cannot find even one pair <TU> ... </TU>";
			Dic.writeLog(errmessage.argvalue);
			return null;
			
		}
		Collections.sort(nodes);
	

		// get rid of overlapping and embedded tags
		xmlnode node0 = nodes.get(0);
		for (int i = 1; i < nodes.size();)
		{
			xmlnode cnode = nodes.get(i);
			if (cnode.beg < node0.end)
			{
				nodes.remove(i);
			}
			else
			{
				node0 = cnode;
				i++;
			}
		}

		Mft mft = new Mft(nodes.size());
		int tu = 1;
		for (int i = 0; i < nodes.size(); i++)
		{
			xmlnode node = nodes.get(i);
			int beg = node.beg;
			int end = node.end;
			if (end > beg)
			{
				mft.tuAddresses[tu] = beg;
				mft.tuLengths[tu] = end - beg;

				tu++;
			}
		}
		text.nbOfTextUnits = (tu - 1);
		return mft;
	}

	/**
	 * 
	 * @param corpus
	 * @param mytext
	 * @return
	 */
	public final String delimitXmlTextUnitsAndImportXmlTags(Corpus corpus, Ntext mytext)
	{
		mytext.hLexemes = new HashMap<String, Integer>();
		mytext.hPhrases = new HashMap<String, Integer>();
		mytext.annotations = new ArrayList<Object>();

		// delimit text units

		String errmessage = mytext.delimitXmlTextUnitsAndImportXmlTags(corpus, this, mytext.XmlNodes,
				mytext.annotations, mytext.hLexemes, mytext.hPhrases);
		return errmessage;
	}

	/**
	 * 
	 * @param text
	 * @param thechars
	 */
	public final void countChars(Ntext text, HashMap<Character, Integer> thechars)
	{
		char c; // current char; its frequence
		int frq;

		for (int i = 0; i < text.buffer.length(); i++)
		{
			c = text.buffer.charAt(i);
			if (!thechars.containsKey(c))
			{
				frq = 1;
				thechars.put(c, frq);
			}
			else
			{
				frq = thechars.get(c);
				thechars.put(c, frq + 1);
			}
		}
	}

	/**
	 * 
	 * @param text
	 */
	public final void computeAlphabet(Ntext text)
	{
		// Build the char list
		text.nbOfChars = text.nbOfDiffChars = 0;
		text.nbOfLetters = text.nbOfDiffLetters = 0;
		text.nbOfDelimiters = text.nbOfDiffDelimiters = 0;
		text.nbOfBlanks = text.nbOfDiffBlanks = 0;
		text.nbOfDigits = text.nbOfDiffDigits = 0;

		HashMap<Character, Integer> thechars = new HashMap<Character, Integer>();
		countChars(text, thechars);

		text.charlist = new Charlist();
		for (char cc : thechars.keySet())
		{
			text.nbOfDiffChars++;
			text.nbOfChars += thechars.get(cc);

			text.charlist.chars.add(cc);
			text.charlist.freqs.add((int) thechars.get(cc));
			if (Language.isLetter(cc))
			{
				text.nbOfDiffLetters++;
				text.nbOfLetters += thechars.get(cc);
				text.charlist.types.add("WFORM");
			}
			else if (Character.isWhitespace(cc))
			{
				text.nbOfDiffBlanks++;
				text.nbOfBlanks += thechars.get(cc);
				text.charlist.types.add("WSPACE");
			}
			else if (Character.isDigit(cc))
			{
				text.nbOfDiffDigits++;
				text.nbOfDigits += thechars.get(cc);
				text.charlist.types.add("DIGIT");
			}
			else
			{
				text.nbOfDiffDelimiters++;
				text.nbOfDelimiters += thechars.get(cc);
				text.charlist.types.add("DELIM");
			}
		}
	}

	/**
	 * 
	 * @param corpus
	 * @param thechars
	 */
	public final void computeAlphabet(Corpus corpus, HashMap<Character, Integer> thechars)
	{
		// Build the char list
		corpus.nbOfChars = corpus.nbOfDiffChars = 0;
		corpus.nbOfLetters = corpus.nbOfDiffLetters = 0;
		corpus.nbOfDelimiters = corpus.nbOfDiffDelimiters = 0;
		corpus.nbOfBlanks = corpus.nbOfDiffBlanks = 0;
		corpus.nbOfDigits = corpus.nbOfDiffDigits = 0;

		corpus.charlist = new Charlist();
		for (char cc : thechars.keySet())
		{
			corpus.nbOfDiffChars++;
			corpus.nbOfChars += thechars.get(cc);

			corpus.charlist.chars.add(cc);
			corpus.charlist.freqs.add((int) thechars.get(cc));
			if (Language.isLetter(cc))
			{
				corpus.nbOfDiffLetters++;
				corpus.nbOfLetters += thechars.get(cc);
				corpus.charlist.types.add("WFORM");
			}
			else if (Character.isWhitespace(cc))
			{
				corpus.nbOfDiffBlanks++;
				corpus.nbOfBlanks += thechars.get(cc);
				corpus.charlist.types.add("WSPACE");
			}
			else if (Character.isDigit(cc))
			{
				corpus.nbOfDiffDigits++;
				corpus.nbOfDigits += thechars.get(cc);
				corpus.charlist.types.add("DIGIT");
			}
			else
			{
				corpus.nbOfDiffDelimiters++;
				corpus.nbOfDelimiters += thechars.get(cc);
				corpus.charlist.types.add("DELIM");
			}
		}
	}

	/**
	 * 
	 * @param hCorpusTokens
	 * @param hTextTokens
	 * @param token
	 * @param begaddress
	 * @param endaddress
	 */
	private final void addToken(HashMap<String, Integer> hCorpusTokens, HashMap<String, Indexkey> hTextTokens,
			String token, int begaddress, int endaddress)
	{
		// hTextTokens contains the addresses of each occurrence in the text
		if (hTextTokens.containsKey(token))
		{
			Indexkey indexkey = hTextTokens.get(token);
			indexkey.addresses.add(begaddress);
			indexkey.addresses.add(endaddress);
		}
		else
		{
			Indexkey indexkey = new Indexkey();
			indexkey.addresses.add(begaddress);
			indexkey.addresses.add(endaddress);
			hTextTokens.put(token, indexkey);
		}

		if (hCorpusTokens != null)
		{
			// hCorpusTokens contains ONLY the frequency of the token in the corpus
			if (hCorpusTokens.containsKey(token))
			{
				// Unsigned integer types have no direct equivalent in Java:
			
				int freq = hCorpusTokens.get(token);
				freq++;
				hCorpusTokens.put(token, freq);
			}
			else
			{
				// Unsigned integer types have no direct equivalent in Java:
				
				hCorpusTokens.put(token, 1);
			}
		}
	}

	/**
	 * 
	 * @param annotations
	 * @param hTextLexemes
	 * @param lexeme
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 */
	public final void addLexemeToText(ArrayList<Object> annotations, HashMap<String, Integer> hTextLexemes,
			String lexeme, Mft textmft, int tunb, double relbegaddress, double relendaddress)
	{
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			lexeme = "invalid,INVALID";
		}
		// hTextLexemes contains the tokenId of lexeme if text already contains lexeme
		int tokenId;
		if (hTextLexemes.containsKey(lexeme))
		{
			tokenId = hTextLexemes.get(lexeme);
		}
		else
		{
			annotations.add(lexeme);
			tokenId = annotations.size() - 1;
			hTextLexemes.put(lexeme, tokenId);
		}
		textmft.addTransition(tunb, relbegaddress, tokenId, relendaddress);
	}

	/**
	 * 
	 * @param annotations
	 * @param hCorpusLexemes
	 * @param lexeme
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 */
	public final void addLexemeToCorpus(ArrayList<Object> annotations, HashMap<String, Integer> hCorpusLexemes,
			String lexeme, Mft textmft, int tunb, double relbegaddress, double relendaddress)
	{
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			lexeme = "invalid,INVALID";
		}
		// hCorpusLexemes contains the tokenId of lexeme if corpus already contains lexeme
		int tokenId;
		if (hCorpusLexemes.containsKey(lexeme))
		{
			tokenId = hCorpusLexemes.get(lexeme);
		}
		else
		{
			annotations.add(lexeme);
			tokenId = annotations.size() - 1;
			hCorpusLexemes.put(lexeme, tokenId);
		}
		textmft.addTransition(tunb, relbegaddress, tokenId, relendaddress);
	}

	/**
	 * 
	 * @param annotations
	 * @param coloredtext
	 * @param hTextLexemes
	 * @param lexeme
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 * @param resetannotations
	 */

	private final void addSyntaxToText(ArrayList<Object> annotations, ArrayList<Object> coloredtext,
			HashMap<String, Integer> hTextLexemes, String lexeme, Mft textmft, int tunb, double relbegaddress,
			double relendaddress, boolean resetannotations)
	{
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			lexeme = "invalid,INVALID";
		}
		ArrayList<String> infos = Dic.normalizeInformation(category, features, this.properties);

		if (coloredtext == null)
			coloredtext = new ArrayList<Object>();

		for (String inf : infos)
		{
			String lex = entry + "," + lemma + "," + inf;
			String myfeature = Dic.lookFor("COLOR", lex);
			if (myfeature != null)
			{
				coloredtext.add(textmft.tuAddresses[tunb] + relbegaddress);
				String colorname = myfeature.substring((new String("COLOR=")).length());
				Color c;

				if (colorname.equals("RED"))
				{
					c = Color.RED;
				}
				else if (colorname.equals("GREEN"))
				{
					c = Color.GREEN;
				}
				else if (colorname.equals("BLUE"))
				{
					c = Color.BLUE;
				}
				else if (colorname.equals("LIGHTRED"))
				{
					c = Color.PINK;
				}
				else if (colorname.equals("LIGHTGREEN"))
				{
					
					c = Color.green;
				}
				else if (colorname.equals("LIGHTBLUE"))
				{
					
					c = Color.blue;
				}
				else if (colorname.equals("YELLOW"))
				{
					c = Color.YELLOW;
				}
				else if (colorname.equals("PURPLE"))
				{
					
					c = Color.MAGENTA;
				}
				else if (colorname.equals("CYAN"))
				{
					c = Color.CYAN;
				}
				else
				{
					c = Color.BLACK;
				}
				coloredtext.add(c);
				coloredtext.add(relendaddress - relbegaddress);
			}

			// hTextLexemes contains the tokenId of lexeme if text already contains lexeme
			if (hTextLexemes.containsKey(lex))
			{
				int tokenId = hTextLexemes.get(lex);
				textmft.addTransition(tunb, relbegaddress, tokenId, relendaddress);
			}
			else
			{
				int tokenId;
				annotations.add(lex);
				tokenId = annotations.size() - 1;
				if (!hTextLexemes.containsKey(lex))
				{
					hTextLexemes.put(lex, tokenId);
				}
				if (resetannotations)
				{
					textmft.deleteNonXrefsAndAddTransition(tunb, relbegaddress, tokenId, annotations, relendaddress);
				}
				else
				{
					textmft.addTransition(tunb, relbegaddress, tokenId, relendaddress);
				}
			}
		}
	}

	/**
	 * 
	 * @param annotations
	 * @param coloredtext
	 * @param hCorpusLexemes
	 * @param lexeme
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 * @param resetannotations
	 */

	public final void addSyntaxToCorpus(ArrayList<Object> annotations, ArrayList<Object> coloredtext,
			HashMap<String, Integer> hCorpusLexemes, String lexeme, Mft textmft, int tunb, double relbegaddress,
			double relendaddress, boolean resetannotations)
	{
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry, tempRef_lemma, tempRef_category,
				tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			lexeme = "invalid,INVALID";
		}
		String myfeature = Dic.lookFor("COLOR", lexeme);
		if (myfeature != null)
		{
			if (coloredtext == null)
				coloredtext = new ArrayList<Object>();

			coloredtext.add(textmft.tuAddresses[tunb] + relbegaddress);
			String colorname = myfeature.substring((new String("COLOR=")).length());
			Color c;

			if (colorname.equals("RED"))
			{
				c = Color.RED;
			}
			else if (colorname.equals("GREEN"))
			{
				c = Color.GREEN;
			}
			else if (colorname.equals("BLUE"))
			{
				c = Color.BLUE;
			}
			else if (colorname.equals("LIGHTRED"))
			{
				c = Color.PINK;
			}
			else if (colorname.equals("LIGHTGREEN"))
			{
				
				c = Color.green;
			}
			else if (colorname.equals("LIGHTBLUE"))
			{
				
				c = Color.blue;
			}
			else if (colorname.equals("YELLOW"))
			{
				c = Color.YELLOW;
			}
			else if (colorname.equals("PURPLE"))
			{
			
				c = Color.MAGENTA;
			}
			else if (colorname.equals("CYAN"))
			{
				c = Color.CYAN;
			}
			else
			{
				c = Color.BLACK;
			}
			coloredtext.add(c);
			coloredtext.add(relendaddress - relbegaddress);
		}

		// hCorpusLexemes contains the tokenId of lexeme if corpus already contains lexeme
		int tokenId;
		if (hCorpusLexemes.containsKey(lexeme))
		{
			tokenId = hCorpusLexemes.get(lexeme);
		}
		else
		{
			annotations.add(lexeme);
			tokenId = annotations.size() - 1;
			hCorpusLexemes.put(lexeme, tokenId);
		}
		if (resetannotations)
		{
			textmft.deleteNonXrefsAndAddTransition(tunb, relbegaddress, tokenId, annotations, relendaddress);
		}
		else
		{
			textmft.addTransition(tunb, relbegaddress, tokenId, relendaddress);
		}
	}

	/**
	 * 
	 * @param annotations
	 * @param hCorpusUnknowns
	 * @param hTextUnknowns
	 * @param unknownform
	 */
	private final void addUnknown(ArrayList<Object> annotations, HashMap<String, Integer> hCorpusUnknowns,
			HashMap<String, Integer> hTextUnknowns, String unknownform)
	{
		String lex = unknownform + ",UNKNOWN";
		// hCorpusUnknowns & hTextUnknowns contain the tokenId of the unknown (if corpus/text contains unknown)

		int tokenId;
		if (hCorpusUnknowns != null)
		{
			if (hCorpusUnknowns.containsKey(lex))
			{
				tokenId = hCorpusUnknowns.get(lex);
			}
			else
			{
				annotations.add(lex);
				tokenId = annotations.size() - 1;
				hCorpusUnknowns.put(lex, tokenId);
			}
		}
		else
		{
			annotations.add(lex);
			tokenId = annotations.size() - 1;
		}
		if (!hTextUnknowns.containsKey(lex))
		{
			hTextUnknowns.put(lex, tokenId);
		}
	}

	/**
	 * 
	 * @param annotations
	 * @param hCorpusLexemes
	 * @param hTextLexemes
	 * @param tokens
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 */

	private void addSequenceToText(ArrayList<Object> annotations, HashMap<String, Integer> hCorpusLexemes,
			HashMap<String, Integer> hTextLexemes, ArrayList<String> tokens, Mft textmft, int tunb, int relbegaddress,
			int relendaddress)
	{
		double strtadd = relbegaddress;
		double intadd;
		textmft.multiplier = 100.0;

		for (int i = 0; i < tokens.size(); i++)
		{
			String token = tokens.get(i);
			if (token.charAt(0) == '<' || token.charAt(0) == '{')
			{
				String lexeme = token.substring(1, 1 + token.length() - 2); // get rid of '{' '}' or '<' '>'
				if (i == tokens.size() - 1)
				{
					intadd = relendaddress;
				}
				else
				{
					// compute intadd
					ArrayList<TransitionPair> outgoings = textmft.getOutgoingTransitions(tunb, strtadd);

					int zz = (int) ((strtadd + 0.005) * 100.0) + 1;
					intadd = zz / 100.0;

					if (outgoings != null)
					{
						String entry = null, info = null, currententry = null, currentinfo = null;
						RefObject<String> tempRef_entry = new RefObject<String>(entry);
						RefObject<String> tempRef_info = new RefObject<String>(info);

						Dic.parseDELAS(lexeme, tempRef_entry, tempRef_info);

						entry = tempRef_entry.argvalue;
						info = tempRef_info.argvalue;

						// maybe the lexeme is already there ?
						boolean foundcompatible = false;

						for (int io = 0; io < outgoings.size(); io++)
						{
							int tid = outgoings.get(io).getTokenId();
							String currentlexeme = (String) annotations.get(tid);
							RefObject<String> tempRef_currententry = new RefObject<String>(currententry);
							RefObject<String> tempRef_currentinfo = new RefObject<String>(currentinfo);

							Dic.parseDELAS(currentlexeme, tempRef_currententry, tempRef_currentinfo);

							currententry = tempRef_currententry.argvalue;
							currentinfo = tempRef_currentinfo.argvalue;
							if (entry.equals(currententry) && info.equals(currentinfo)) // annotations are compatible
							{
								intadd = outgoings.get(io).getRelEndAddress();
								foundcompatible = true;
								break;
							}
						}
						if (!foundcompatible)
						{
							// we need to find a new, virgin position
							intadd = textmft.getANewVirginAddress(tunb, strtadd, relendaddress);
						}
					}
				}
				addLexemeToText(annotations, hTextLexemes, lexeme, textmft, tunb, strtadd, intadd);
				
			}
			else
			{
				int zz = (int) ((strtadd + 0.005) * 100.0) + 1;
				intadd = zz / 100.0;
			}
			strtadd = intadd;
		}
	}

	/**
	 * 
	 * @param annotations
	 * @param hCorpusLexemes
	 * @param hTextLexemes
	 * @param tokens
	 * @param textmft
	 * @param tunb
	 * @param relbegaddress
	 * @param relendaddress
	 */

	private void addSequenceToCorpus(ArrayList<Object> annotations, HashMap<String, Integer> hCorpusLexemes,
			HashMap<String, Integer> hTextLexemes, ArrayList<String> tokens, Mft textmft, int tunb, int relbegaddress,
			int relendaddress)
	{
		double strtadd = relbegaddress;
		double intadd;
		textmft.multiplier = 100.0;

		for (int i = 0; i < tokens.size(); i++)
		{
			String token = tokens.get(i);
			if (token.charAt(0) == '<' || token.charAt(0) == '{')
			{
				String lexeme = token.substring(1, 1 + token.length() - 2); // get rid of '{' '}' or '<' '>'
				if (i == tokens.size() - 1)
				{
					intadd = relendaddress;
				}
				else
				{
					// compute intadd
					ArrayList<TransitionPair> outgoings = textmft.getOutgoingTransitions(tunb, strtadd);
					{
						int zz = (int) ((strtadd + 0.005) * 100.0) + 1;
						intadd = zz / 100.0;
					}
					if (outgoings != null)
					{
						String entry = null, info = null, currententry = null, currentinfo = null;
						RefObject<String> tempRef_entry = new RefObject<String>(entry);
						RefObject<String> tempRef_info = new RefObject<String>(info);

						Dic.parseDELAS(lexeme, tempRef_entry, tempRef_info);

						entry = tempRef_entry.argvalue;
						info = tempRef_info.argvalue;

						// maybe the lexeme is already there ?
						boolean foundcompatible = false;
						for (int io = 0; io < outgoings.size(); io++)
						{
							int tid = outgoings.get(io).getTokenId();
							String currentlexeme = (String) annotations.get(tid);
							RefObject<String> tempRef_currententry = new RefObject<String>(currententry);
							RefObject<String> tempRef_currentinfo = new RefObject<String>(currentinfo);

							Dic.parseDELAS(currentlexeme, tempRef_currententry, tempRef_currentinfo);

							currententry = tempRef_currententry.argvalue;
							currentinfo = tempRef_currentinfo.argvalue;
							if (entry.equals(currententry) && info.equals(currentinfo)) // annotations are compatible
							{
								intadd = outgoings.get(io).getRelEndAddress();
								foundcompatible = true;
								break;
							}
						}
						if (!foundcompatible)
						{
							// we need to find a new, virgin position
							intadd = textmft.getANewVirginAddress(tunb, strtadd, relendaddress);
						}
					}
				}
				addLexemeToCorpus(annotations, hCorpusLexemes, lexeme, textmft, tunb, strtadd, intadd);
				// System.out.println("Add annotation: " + strtadd + " " + lexeme + " => " + intadd);
			}
			else
			{
				int zz = (int) ((strtadd + 0.005) * 100.0) + 1;
				intadd = zz / 100.0;
			}
			strtadd = intadd;
		}
	}

	private ArrayList<Object> lexBins;
	private ArrayList<Object> lexGrms;
	public ArrayList<Object> synGrms; // grammar, priority, type = disamb or annotation

	/**
	 * 
	 * @param position
	 * @param simpletoken
	 * @return
	 */
	private final ArrayList<String> lookupAllLexsForCompounds(int position, String simpletoken)
	{
		ArrayList<String> sols = null;

		for (int iprio = -9; iprio < 10; iprio++)
		{
			// dictionaries *.jnod *.bin
			for (int idic = 0; idic < lexBins.size(); idic += 2)
			{
				if ((Integer) lexBins.get(idic + 1) != iprio)
				{
					continue;
				}
				FSDic lexBin = (FSDic) lexBins.get(idic);
				if (lexBin.bufferc == null)
				{
					continue;
				}
				ArrayList<String> tmp;
				if (this.Lan.isoName.equals("ar") || this.Lan.isoName.equals("he"))
				{
					tmp = lexBin.lookUpCompoundSemitic(this.CurrentLine, position, this);
				}
				else
				{
					tmp = lexBin.lookUpCompound(this.CurrentLine, position, this);
				}
				if (tmp != null && tmp.size() > 0)
				{
					if (sols == null)
					{
						sols = new ArrayList<String>();
					}
					sols.addAll(tmp);
				}
			}
			if (sols != null)
			{
				break;
			}
		}

		if (sols != null)
		{
			// keep only compounds
			if (!Lan.asianTokenizer)
			{
				filterSimples(sols, simpletoken.length());
			}
		}
		return sols;
	}

	/**
	 * 
	 * @param simpletoken
	 * @param processconstraints
	 * @param currentline
	 * @param cpos
	 * @return
	 */
	final ArrayList<String> lookupAllLexsAndMorphsForSimples(String simpletoken, boolean processconstraints,
			String currentline, int cpos)
	{
		ArrayList<String> sols = null;

		for (int iprio = -9; iprio < 10; iprio++)
		{
			// dictionaries *.jnod *.bin
			for (int idic = 0; idic < lexBins.size(); idic += 2)
			{
				FSDic lexBin = (FSDic) lexBins.get(idic);
				if ((Integer) lexBins.get(idic + 1) != iprio)
				{
					continue;
				}
				ArrayList<String> tmp;
				if (this.Lan.isoName.equals("ar") || this.Lan.isoName.equals("he"))
				{
					tmp = lexBin.lookUpSimpleSemitic(simpletoken, 0, this); // vowellization
				}
				else
				{
					tmp = lexBin.lookUpSimple(simpletoken, 0, this);
				}
				if (tmp != null && tmp.size() > 0)
				{
					if (sols == null)
					{
						sols = new ArrayList<String>();
					}
					sols.addAll(tmp);
				}
			}
			// morpho grams *.nom *.grm
			for (int idic = 0; idic < lexGrms.size(); idic += 2)
			{
				Grammar lexGrm = (Grammar) lexGrms.get(idic);
				if ((Integer) lexGrms.get(idic + 1) != iprio)
				{
					continue;
				}
				ArrayList<String> tmp = lexGrm.matchWord(simpletoken, this, processconstraints, currentline, cpos);
				if (tmp != null && tmp.size() > 0)
				{
					if (sols == null)
					{
						sols = new ArrayList<String>();
					}
					sols.addAll(tmp);
				}
			}

			if (sols != null)
			{
				break;
			}
		}
		if (sols == null || sols.isEmpty())
		{
			return null;
		}
		// need to add length to all simple words, so that I can unify with processing of compounds
		ArrayList<String> res = new ArrayList<String>();
		for (int isol = 0; isol < sols.size(); isol++)
		{
			if (sols.get(isol).getClass() == String.class)
			{
				res.add(sols.get(isol));
				res.add(Integer.toString(simpletoken.length()));
			}
			else
			{
				res.add(sols.get(isol + 1));
				res.add(sols.get(isol));
				isol++;
			}
		}
		return res;
	}

	/**
	 * 
	 * @param token
	 * @param errmessage
	 * @return
	 */
	private final ArrayList<String> lookupAndAnalyzeSimpleOrCompound(String token, RefObject<String> errmessage)
	{
		ArrayList<String> ressols = null;
		ArrayList<String> sols;
		errmessage.argvalue = null;

		if (this.Lan.isACompound(token))
		{
			sols = lookupAllLexsForCompounds(0, token);
		}
		else
		{
			sols = lookupAllLexsAndMorphsForSimples(token, false, null, 0);
		}
		if (sols != null)
		{
			ressols = new ArrayList<String>();
			for (int i = 0; i < sols.size(); i += 2)
			{
				String entry = null, info = null;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_info = new RefObject<String>(info);
				Dic.parseDELAS(sols.get(i), tempRef_entry, tempRef_info);
				entry = tempRef_entry.argvalue;
				info = tempRef_info.argvalue;
				if (isComplex(info))
				{
					// cannot,<can,V> <not,ADV>
					ArrayList<ArrayList<String>> resultingexp = processTokenAnalysis(1, info, token, 1, errmessage);
					if (resultingexp == null)
					{
						if (errmessage.argvalue == null)
						{
							continue;
						}
						Dic.writeLog(errmessage.argvalue);
						return null;
					}
					ArrayList<ArrayList<String>> defactorizedexp = new ArrayList<ArrayList<String>>();
					recursiveDevelop(new ArrayList<String>(), resultingexp, defactorizedexp);
					for (int iterm = 0; iterm < defactorizedexp.size(); iterm++)
					{
						ArrayList<String> sequence = defactorizedexp.get(iterm);
						ArrayList<String> seq2 = getRidOfConstraints(sequence);
						ArrayList<String> seq3 = getRidOfAngles(seq2);
						StringBuilder asol = Grammar.computeInput(seq3);
						ressols.add(asol.toString());
					}
				}
				else
				{
					ressols.add(sols.get(i));
				}
			}
		}
		return ressols;
	}

	/**
	 * @param sequence
	 * @return
	 */
	final ArrayList<String> getRidOfConstraints(ArrayList<String> sequence)
	{
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < sequence.size(); i++)
		{
			String tok = sequence.get(i);
			if (tok == null)
			{
				continue;
			}
			if (Dic.isALexicalConstraint(tok))
			{
				continue;
			}
			res.add(tok);
		}
		return res;
	}

	/**
	 * 
	 * @param sequence
	 * @return
	 */
	private final ArrayList<String> getRidOfAngles(ArrayList<String> sequence)
	{
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < sequence.size(); i++)
		{
			String tok = sequence.get(i);
			if (tok == null)
			{
				continue;
			}
			if (tok.charAt(0) == '<' || tok.charAt(0) == '{')
			{
				res.add(tok.substring(1, 1 + tok.length() - 2));
			}
			else
			{
				res.add(tok);
			}
		}
		return res;
	}

	transient HashMap<String, String> recursiveMorphology = null;

	/**
	 * 
	 * @param sols
	 * @param simpleTokenLength
	 */
	private void filterSimples(ArrayList<String> sols, int simpleTokenLength)
	{
		String entry = null, info = null; // information in the lexicon

		for (int i = 0; i < sols.size();)
		{
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			Dic.parseDELAS(sols.get(i), tempRef_entry, tempRef_info);
			entry = tempRef_entry.argvalue;
			info = tempRef_info.argvalue;
			if (entry.length() == simpleTokenLength)
			{
				sols.subList(i, 2 + i).clear();
			}
			else
			{
				i += 2;
			}
		}
	}

	/**
	 * 
	 * @param sols
	 * @return
	 */
	private final boolean filterUnamb(ArrayList<String> sols)
	{
		String entry = null, info = null; // information in the lexicon

		// Look for UNAMB
		boolean found = false;
		for (int i = 0; i < sols.size(); i += 2)
		{
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			Dic.parseDELAS(sols.get(i), tempRef_entry, tempRef_info);
			entry = tempRef_entry.argvalue;
			info = tempRef_info.argvalue;
			String myfeature = Dic.lookFor("UNAMB", info);
			if (myfeature != null)
			{
				found = true;
				break;
			}
		}
		if (!found)
		{
			return false;
		}

		if (found) // remove all solutions that are not UNAMB
		{
			for (int i = 0; i < sols.size();)
			{
				RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
				RefObject<String> tempRef_info2 = new RefObject<String>(info);
				Dic.parseDELAS(sols.get(i), tempRef_entry2, tempRef_info2);
				entry = tempRef_entry2.argvalue;
				info = tempRef_info2.argvalue;
				String myfeature = Dic.lookFor("UNAMB", info);
				if (myfeature == null)
				{
					sols.subList(i, 2 + i).clear();
					continue;
				}
				i += 2;
			}
		}

		// remove all UNAMB feature from lexical information
		for (int i = 0; i < sols.size(); i += 2)
		{
			String line = sols.get(i);
			int index = line.indexOf("+UNAMB");
			if (index == -1)
			{
				continue;
			}
			String line2 = line.substring(0, index) + line.substring(index + (new String("+UNAMB")).length());
			sols.set(i, line2);
		}

		return true;
	}

	/**
	 * 
	 * @param sols
	 */
	public static void filterNonWords(ArrayList<String> sols)
	{
		String entry = null, info = null; // information in the lexicon

		// Look for +NW
		for (int i = 0; i < sols.size();)
		{
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_info = new RefObject<String>(info);
			Dic.parseDELAS(sols.get(i), tempRef_entry, tempRef_info);
			entry = tempRef_entry.argvalue;
			info = tempRef_info.argvalue;
			String myfeature = Dic.lookFor("NW", info);
			if (myfeature != null)
			{
				sols.subList(i, 2 + i).clear();
				continue;
			}
			i += 2;
		}
	}

	/**
	 * 
	 * @param itu
	 * @param tokenSequence
	 * @param initialtoken
	 * @param priority
	 * @param errmessage
	 * @return
	 */
	private ArrayList<ArrayList<String>> processTokenAnalysis(int itu, String tokenSequence, String initialtoken,
			int priority, RefObject<String> errmessage)
	{
		String[] tokens;
		ArrayList<ArrayList<String>> result = null;
		errmessage.argvalue = null;

		tokens = Lan.parseSequenceOfTokens(tokenSequence);
		tokens = concatenateAllINFOs(tokens);
		if (tokens == null)
		{
			// invalid syntax for complex lexical information
			errmessage.argvalue = "invalid lexical info: " + tokenSequence;
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

				Dic.parseLexicalConstraint(tokens[i], tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features,
						tempRef_op, tempRef_negation);

				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				category = tempRef_category.argvalue;
				features = tempRef_features.argvalue;
				op = tempRef_op.argvalue;
				negation = tempRef_negation.argvalue;
				if (!op.equals("=:"))
				{
					Dic.writeLog("Operator '" + op + "' in symbol " + tokens[i] + " is invalid");
				}
				if (category != null && category.length() > 1)
				{
					
					if (!initialtoken.equals(entry))
					{
						if (this.recursiveMorphology == null)
						{
							recursiveMorphology = new HashMap<String, String>();
						}
						else if (recursiveMorphology.containsKey(entry))
						{
							return null;
						}
						this.recursiveMorphology.put(entry, null);
						csols0 = this.lookupAndAnalyzeSimpleOrCompound(entry, errmessage);
						if (csols0 == null && errmessage.argvalue != null)
						{
							return null;
						}
						this.recursiveMorphology.remove(entry);
					}
				}
				else if (lemma != null && lemma.length() > 1)
				{
			
					if (!initialtoken.equals(entry))
					{
						if (this.recursiveMorphology == null)
						{
							recursiveMorphology = new HashMap<String, String>();
						}
						else if (recursiveMorphology.containsKey(entry))
						{
							return null;
						}
						this.recursiveMorphology.put(entry, null);
						csols0 = this.lookupAndAnalyzeSimpleOrCompound(entry, errmessage); 
						if (csols0 == null && errmessage.argvalue != null)
						{
							return null;
						}
						this.recursiveMorphology.remove(entry);
					}
				}
				
				if (csols0 == null || csols0.isEmpty())
				{
					return null;
				}
				// Using copy-constructor instead of clone() - recommended because of unchecked class cast
				ArrayList<String> csols2 = new ArrayList<String>(csols0);
				if (category != null)
				{
					Grammar.filterConstraint(csols2, entry, lemma, category, features, negation);
				}
				if (csols2.isEmpty())
				{
					return null;
				}
				csols = Grammar.transformConstraintIntoLUNoLU(csols2); // add angles around lexical constraints
			}
			else if (tokens[i].charAt(0) == '<')
			{
				// a lexeme symbol, e.g. <can,V+PR+1+2+3+s+p>
				csols = new ArrayList<String>();
				// Normalize lexeme symbol
				RefObject<ArrayList<String>> tempRef_csols = new RefObject<ArrayList<String>>(csols);
				Dic.normalizeLexemeSymbol(tokens[i], this, tempRef_csols);
				csols = tempRef_csols.argvalue;
			}
			else
			// a token
			{
				ArrayList<String> csols0 = lookupAllLexsAndMorphsForSimples(tokens[i], false, tokens[i], 0);
				if (csols0 == null)
				{
					csols = new ArrayList<String>();
					csols.add(tokens[i]);
				}
				else
				{
					csols = Grammar.addBracketsAround(csols0); // add angles around lexical entries
				}
			}
			if (result == null)
			{
				result = new ArrayList<ArrayList<String>>();
			}
			result.add(csols);
		}
		return result;
	}

	/**
	 * 
	 * @param prefix
	 * @param expression
	 * @param result
	 */
	private static void recursiveDevelop(ArrayList<String> prefix, ArrayList<ArrayList<String>> expression,
			ArrayList<ArrayList<String>> result)
	{
		if (expression.isEmpty())
		{
			// Using copy-constructor instead of clone() - recommended because of unchecked class cast
			result.add(new ArrayList<String>(prefix));
			return;
		}

		ArrayList<String> fact = expression.get(0);
		if (fact == null)
		{
			ArrayList<ArrayList<String>> expSublist = new ArrayList<ArrayList<String>>();
			if (expression.size() == 1)
				expSublist = new ArrayList<ArrayList<String>>();
			else if (expression.size() == 2)
				expSublist.add(expression.get(1));
			else
				expSublist = (ArrayList<ArrayList<String>>) expression.subList(1, expression.size() - 1);

			recursiveDevelop(prefix, expSublist, result);
		}
		else
		{
			for (int iterm = 0; iterm < fact.size(); iterm++)
			{
				ArrayList<ArrayList<String>> expSublist = new ArrayList<ArrayList<String>>();
				// Using copy-constructor instead of clone() - recommended because of unchecked class cast
				ArrayList<String> tmp = new ArrayList<String>(prefix);
				tmp.add(fact.get(iterm));

				if (expression.size() == 1)
					expSublist = new ArrayList<ArrayList<String>>();
				else if (expression.size() == 2)
					expSublist.add(expression.get(1));
				else
					expSublist = (ArrayList<ArrayList<String>>) expression.subList(1, expression.size() - 1);

				recursiveDevelop(tmp, expSublist, result);
			}
		}
	}

	/**
	 * 
	 * @param info
	 * @return
	 */
	private final boolean isComplex(String info)
	{
		if (info != null && !info.equals("") && (info.charAt(0) == '<' || info.charAt(0) == '{'))
		{
			return true;
		}
		return false;
		
	}

	/**
	 * 
	 * @param info
	 * @param thereisalexicalunit
	 * @return
	 */
	private final boolean isComplex(String info, RefObject<Boolean> thereisalexicalunit)
	{
		thereisalexicalunit.argvalue = false;
		boolean iscomplex = false;
		if (info == null)
		{
			return false;
		}
		if (info.equals(""))
		{
			return false;
		}
		if (info.charAt(0) != '<' && info.charAt(0) != '{')
		{
			return false;
		}

		for (int i = 0; i < info.length(); i++)
		{
			String simplelex;
			int j;
			if (info.charAt(i) == '<')
			{
				iscomplex = true;
				for (j = 0; j + i < info.length() && info.charAt(i + j) != '>'; j++)
				{
					;
				}
				simplelex = info.substring(i, i + j + 1);
			}
			else if (info.charAt(i) == '{')
			{
				iscomplex = true;
				for (j = 0; j + i < info.length() && info.charAt(i + j) != '}'; j++)
				{
					;
				}
				simplelex = info.substring(i, i + j + 1);
			}
			else
			{
				return false;
			}
			if (Dic.isALexicalSymbol(simplelex))
			{
				thereisalexicalunit.argvalue = true;
				return true;
			}
			i += j;
		}
		return iscomplex;
	}

	/**
	 * 
	 * @param sequence
	 * @param varnumber
	 * @return
	 */
	private String getLexicalUnit(ArrayList<String> sequence, int varnumber)
	{
		int nb = 1;
		for (int iseq = 0; iseq < sequence.size(); iseq++)
		{
			String token = sequence.get(iseq);
			if (token != null && token.length() > 4)
			{
				int istrt = 0;
				int found = token.indexOf("<LU=", istrt);
				while (found != -1)
				{
					int len;
					int level = 1;
					for (len = 4; found + len < token.length(); len++)
					{
						if (token.charAt(found + len) == '<')
						{
							level++;
						}
						else if (token.charAt(found + len) == '>')
						{
							level--;
							if (level == 0)
							{
								len++;
								break;
							}
						}
					}
					if (nb == varnumber)
					{
						return token.substring(found, found + len);
					}
					nb++;
					found = token.indexOf("<LU=", found + len);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param sequence
	 */
	private void deleteAllLUsFromOutput(ArrayList<String> sequence)
	{
		for (int iseq = 0; iseq < sequence.size();)
		{
			String token = sequence.get(iseq);
			if (token == null || token.length() < 4)
			{
				iseq++;
				continue;
			}

			// look for <LU=...>
			int istrt = 0;
			int found = token.indexOf("<LU=", istrt);
			if (found == -1)
			{
				iseq++;
				continue;
			}
			int len;
			for (len = 4; found + len < token.length() && token.charAt(found + len) != '>'; len++)
			{
				;
			}
			sequence.set(iseq, token.substring(0, found) + token.substring(found + len + 1));
		}
	}

	/**
	 * 
	 * @param feature
	 * @return
	 */
	private final boolean isASyntacticFeature(String feature)
	{
		if (this.prop_inf == null) // e.g. "Hum"
		{
			return feature.length() > 1;
		}
		int index = feature.indexOf('=');
		if (index != -1) // e.g. "Nb=sing"
		{
			String feat = feature.substring(index + 1); // "sing"
			return !this.prop_inf.containsKey(feat);
		}
		else
		{
			return !this.prop_inf.containsKey(feature);
		}
	}

	/**
	 * 
	 * @param feature
	 * @return
	 */
	private final boolean isAninflectionalFeature(String feature)
	{
		if (feature.equals("NW"))
		{
			return false;
		}
		if (feature.equals("FXC"))
		{
			return false;
		}
		if (this.prop_inf == null) // e.g. "m"
		{
			return feature.length() == 1;
		}
		int index = feature.indexOf('=');
		if (index != -1) // e.g. "Nb=sing"
		{
			String feat = feature.substring(index + 1); // sing
			return this.prop_inf.containsKey(feat);
		}
		else
		{
			return this.prop_inf.containsKey(feature);
		}
	}

	/**
	 * 
	 * @param sequence
	 * @param varnumber
	 * @param vartype
	 * @return
	 */
	private String getVariableValue(ArrayList<String> sequence, int varnumber, char vartype)
	{
		String lu = getLexicalUnit(sequence, varnumber);
		if (lu == null)
		{
			return null;
		}

		String entry = null, category = null, lemma = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		Dic.parseLexicalUnit(lu, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;

		StringBuilder res = null;
		switch (vartype)
		{
			case 'E':
				return entry;
			case 'L':
				return lemma;
			case 'C':
				return category;
			case 'S':
				if (features == null)
				{
					return "";
				}
				res = new StringBuilder();
				for (int i = 0; i < features.length; i++)
				{
					if (features[i] == null || features[i].equals(""))
					{
						continue;
					}
					if (isASyntacticFeature(features[i]) && !features[i].equals("NW"))
					{
						res.append("+" + features[i]);
					}
				}
				return res.toString();
			case 'F':
				if (features == null)
				{
					return "";
				}
				res = new StringBuilder();
				for (int i = 0; i < features.length; i++)
				{
					if (features[i] == null || features[i].equals(""))
					{
						continue;
					}
					if (isAninflectionalFeature(features[i]))
					{
						res.append("+" + features[i]);
					}
				}
				return res.toString();
		}
		return null;
	}

	/**
	 * 
	 * @param varname
	 * @param inputs
	 * @return
	 */
	private String getMorphoValue(String varname, ArrayList<String> inputs)
	{
		// scan the input to the left to get the range of the variable
		int index = -1;
		for (int iinput = inputs.size() - 1; iinput >= 0; iinput--)
		{
			// look for $(varname
			String si = inputs.get(iinput);
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
		StringBuilder res = new StringBuilder();
		int rec = 0;
		for (index++; index < inputs.size(); index++)
		{
			String si = inputs.get(index);
			if (si == null || si.length() == 0)
			{
				continue;
			}
			if (si.length() > 2 && si.charAt(0) == '$' && si.charAt(1) == '(')
			{
				rec++;
				continue;
			}
			else if (si.length() >= 2 && si.charAt(0) == '$' && si.charAt(1) == ')')
			{
				if (rec == 0)
				{
					break;
				}
				else
				{
					rec--;
				}
				continue;
			}
			res.append(si);
		}
		return res.toString();
	}

	/**
	 * 
	 * @param sequence
	 * @param wholetoken
	 */
	public final void processELCSFVariables(ArrayList<String> sequence, String wholetoken)
	{
		for (int iseq = 0; iseq < sequence.size(); iseq++)
		{
			String token = sequence.get(iseq);
			if (token != null && token.length() > 0) 
			{
				// are there any variable $1E, $1L or $1S ?
				StringBuilder res = new StringBuilder();
				for (int i = 0; i < token.length();)
				{
					if (token.charAt(i) == '$')
					{
						if (Character.isDigit(token.charAt(i + 1)))
						{
							int varnb = Character.digit(token.charAt(i + 1), 10);
							String val = null;
							if (varnb > 0)
							{
								val = getVariableValue(sequence, varnb, token.charAt(i + 2));
								res.append(val);
								i += 3;
								continue;
							}
							else
							{
								val = wholetoken;
								res.append(val);
								i += 2;
								continue;
							}
						}
						res.append(token.charAt(i++));
					}
					else if (token.charAt(i) == '\\')
					{
						res.append(token.charAt(i++));
						res.append(token.charAt(i++));
					}
					else
					{
						res.append(token.charAt(i++));
					}
				}
				sequence.set(iseq, res.toString());
			}
		}
	}

	/**
	 * 
	 * @param sequence
	 */
	private void removeConstraints(ArrayList<String> sequence)
	{
		for (int iseq = 0; iseq < sequence.size();)
		{
			String token = sequence.get(iseq);
			if (Dic.isALexicalConstraint(token))
			{
				sequence.remove(iseq);
			}
			else
			{
				iseq++;
			}
		}
	}

	/**
	 * 
	 * @param sequence
	 * @return
	 */
	private String[] concatenateAllINFOs(String[] sequence)
	{
		// sequence = {"<zz,zz,ZZ>","<INFO+HUM>","<$0,$1L,V$1S$1F>","<INFO+DERIV>"}
		// => result = {"<zz,zz,ZZ>","<$0,$1L,V$1S$1F+HUM+DERIV>"}
		// concatenates all INFO tags to the last LU
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> suffixes = new ArrayList<String>();
		for (int iseq = 0; iseq < sequence.length; iseq++)
		{
			String token0 = sequence[iseq];
			String token = token0.replace('{', '<').replace('}', '>');
			if (token.length() >= 5 && token.substring(0, 5).equals("<INFO"))
			{
				suffixes.add(token);
			}
			else
			{
				result.add(token);
			}
		}
		if (suffixes.size() > 0)
		{
			if (result.isEmpty())
			{
				result.add("<INVALIDANNOTATION>");
				String[] res0 = new String[result.size()];
				result.toArray(res0);
				return res0;
			}
			String last0 = result.get(result.size() - 1); // e.g. "<$0,$1L,V$1S$1F>"
			String last = last0.substring(0, last0.length() - 1); // e.g. "<$0,$1L,V$1S$1F"
			for (int i = 0; i < suffixes.size(); i++)
			{
				String suf0 = suffixes.get(i); // e.g. "<INFO+DERIV>"
				String suf = suf0.substring(5, 5 + suf0.length() - 6); // e.g. "+DERIV"
				last += suf; // e.g. "<$0,$1L,V$1S$1F+DERIV"
			}
			result.set(result.size() - 1, last + ">"); // e.g. "<$0,$1L,V$1S$1F+DERIV>"
		}
		String[] res = new String[result.size()];
		result.toArray(res);
		return res;
	}

	/**
	 * 
	 * @param sequence
	 * @return
	 */
	private ArrayList<String> concatenateAllINFOs(ArrayList<String> sequence)
	{
		// sequence = {"<zz,zz,ZZ>","<INFO+HUM>","<$0,$1L,V$1S$1F>","<INFO+DERIV>"}
		// => result = {"<zz,zz,ZZ>","<$0,$1L,V$1S$1F+HUM+DERIV>"}
		// concatenates all INFO tags to the last LU
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> suffixes = new ArrayList<String>();
		for (int iseq = 0; iseq < sequence.size(); iseq++)
		{
			String token0 = sequence.get(iseq);
			String token = token0.replace('{', '<').replace('}', '>');
			if (token.length() >= 5 && token.substring(0, 5).equals("<INFO"))
			{
				suffixes.add(token);
			}
			else
			{
				result.add(token);
			}
		}
		if (suffixes.size() > 0)
		{
			if (result.isEmpty())
			{
				result.add("<INVALIDANNOTATION>");
				return result;
			}
			String last0 = result.get(result.size() - 1); // e.g. "<$0,$1L,V$1S$1F>"
			String last = last0.substring(0, last0.length() - 1); // e.g. "<$0,$1L,V$1S$1F"
			for (int i = 0; i < suffixes.size(); i++)
			{
				String suf0 = suffixes.get(i); // e.g. "<INFO+DERIV>"
				String suf = suf0.substring(5, 5 + suf0.length() - 6); // e.g. "+DERIV"
				last += suf; // e.g. "<$0,$1L,V$1S$1F+DERIV"
			}
			result.set(result.size() - 1, last + ">"); // e.g. "<$0,$1L,V$1S$1F+DERIV>"
		}
		return result;
	}

	/**
	 * 
	 * @param sequence
	 * @param dollarzerovalue
	 * @return
	 */
	private final ArrayList<String> processDollarZero(ArrayList<String> sequence, String dollarzerovalue)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int iseq = 0; iseq < sequence.size(); iseq++)
		{
			String token = sequence.get(iseq);
			result.add(token.replace("$0", dollarzerovalue));
		}
		return result;
	}

	private String CurrentLine;

	/**
	 * 
	 * @param corpus
	 * @param text
	 * @param annotations
	 * @param simpleWordMorphology
	 * @param errmessage
	 * @return
	 */
	public final boolean tokenize(Corpus corpus, Ntext text, ArrayList<Object> annotations,
			HashMap<String, ArrayList<String>> simpleWordMorphology, RefObject<String> errmessage)
	{
		HashMap<String, Integer> hCorpusLexemes, hCorpusUnknowns; // hCorpusTokens;
		errmessage.argvalue = null;
		if (corpus == null)
		{
			hCorpusLexemes = null;
			hCorpusUnknowns = null;
			
		}
		else
		{
			hCorpusLexemes = corpus.hLexemes;
			hCorpusUnknowns = corpus.hUnknowns;
			
		}
		text.hLexemes = new HashMap<String, Integer>();
		text.hUnknowns = new HashMap<String, Integer>();
		text.hTokens = new HashMap<String, Indexkey>();
		text.nbOfTokens = text.nbOfWords = 0;

		String token; // current token
	

		text.nbOfWords = text.nbOfTokens = text.nbOfDigits = text.nbOfDelimiters = 0;

		// main loop: tokenize each text unit

		int progressPercentage = 0;
		HashMap<String, ArrayList<ArrayList<String>>> processConstraints = new HashMap<String, ArrayList<ArrayList<String>>>();
		for (int itu = 1; itu < text.mft.tuAddresses.length; itu++)
		{
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					return false;
				}
				if (corpus == null)
				{
					int nprogress = (int) (itu * 100.0F / text.nbOfTextUnits);
					if (nprogress != progressPercentage)
					{
						progressPercentage = nprogress;
						if (backgroundWorker.isBusy())
						{
							backgroundWorker.reportProgress(nprogress);
						}
					}
				}
			}
			CurrentLine = text.buffer.substring(text.mft.tuAddresses[itu], text.mft.tuAddresses[itu]
					+ text.mft.tuLengths[itu]);
		
			int cpos; // current and ending positions of token in current text unit
			for (int ichar = 0; ichar < CurrentLine.length();)
			{
				// get rid of white spaces
				if (Character.isWhitespace(CurrentLine.charAt(ichar)))
				{
					ichar++;
					continue;
				}
				cpos = ichar; // starting position of the token in the text unit
				text.nbOfTokens++;
				if (Language.isLetter(CurrentLine.charAt(cpos))) // WORD FORM
				{
					text.nbOfWords++;
					if (Lan.asianTokenizer)
					{
						ichar++;
						token = CurrentLine.substring(cpos, cpos + 1);
					}
					else
					{
						for (ichar++; ichar < CurrentLine.length() && Language.isLetter(CurrentLine.charAt(ichar)); ichar++)
						{
							;
						}
						token = CurrentLine.substring(cpos, ichar);
					}
				}
				else if (text.XmlNodes != null && CurrentLine.charAt(cpos) == '<') // XML TAG
				{
					for (ichar++; ichar < CurrentLine.length() && CurrentLine.charAt(ichar) != '>'; ichar++)
					{
						;
					}
					if (ichar < CurrentLine.length())
					{
						ichar++;
						token = CurrentLine.substring(cpos, ichar);
						continue;
					}
					else
					// non closed '<' are processed as delimiters
					{
						ichar = cpos + 1;
						token = CurrentLine.substring(cpos, cpos + 1);
						continue;
					}
				}
				else
				// DELIMITER
				{
					if (Character.isDigit(CurrentLine.charAt(cpos)))
					{
						text.nbOfDigits++;
					}
					else
					{
						text.nbOfDelimiters++;
					}
					ichar++;
					token = CurrentLine.substring(cpos, cpos + 1);
				}

				// lookup the linguistic resource and store the result
				ArrayList<String> sols;
				boolean found = false;
				ArrayList<String> s_sols = null;
				if (!Lan.asianTokenizer && Language.isLetter(token.charAt(0)))
				{
					// process simple words
					if (Lan.isoName.equals("ar") || Lan.isoName.equals("he") || Lan.isoName.equals("vi"))
					{
						// agglutinative languages: the token could be a prefix of a multiword unit, e.g.
						// "inthewhite house" ("do do" in vietnamese)
						s_sols = this.lookupAllLexsAndMorphsForSimples(token, true, CurrentLine, cpos);
						if (s_sols != null)
						{
							filterNonWords(s_sols); // filter out all non words +NW
							filterUnamb(s_sols); // filter all solutions but +UNAMB if there is one +UNAMB
						}
					}
					else
					{
						// non-agglutinative language: the token can be parsed once for all, without looking at context
						if (simpleWordMorphology.containsKey(token))
						{
							s_sols = simpleWordMorphology.get(token);
						}
						else
						{
							s_sols = this.lookupAllLexsAndMorphsForSimples(token, true, CurrentLine, cpos);
							if (s_sols == null)
							{
								simpleWordMorphology.put(token, null);
							}
							else
							{
								filterNonWords(s_sols); // filter out all non words +NW
								filterUnamb(s_sols); // filter all solutions but +UNAMB if there is one +UNAMB
								simpleWordMorphology.put(token, s_sols);
							}
						}
					}
				}

				boolean thereisunamb = false;
				ArrayList<String> c_sols = this.lookupAllLexsForCompounds(cpos, token);
				if (c_sols != null && c_sols.size() > 0)
				{
					filterNonWords(c_sols); // filter out all non words +NW
					thereisunamb = filterUnamb(c_sols); // filter all solutions but +UNAMB if there is one +UNAMB
					if (s_sols != null && s_sols.size() > 0)
					{
						if (!thereisunamb)
						{
							// Using copy-constructor instead of clone() - recommended because of unchecked class cast
							sols = new ArrayList<String>(s_sols);
							sols.addAll(c_sols);
						}
						else
						{
							// Using copy-constructor instead of clone() - recommended because of unchecked class cast
							sols = new ArrayList<String>(c_sols);
						}
					}
					else
					{
						// Using copy-constructor instead of clone() - recommended because of unchecked class cast
						sols = new ArrayList<String>(c_sols);
					}
				}
				else
				{
					if (s_sols != null && s_sols.size() > 0)
					{
						// Using copy-constructor instead of clone() - recommended because of unchecked class cast
						sols = new ArrayList<String>(s_sols);
					}
					else
					{
						sols = null;
					}
				}

				if (sols != null)
				{
					for (int i = 0; i < sols.size(); i += 2)
					{
						String entry = null, lemma = null, info = null;
						RefObject<String> tempRef_entry = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
						RefObject<String> tempRef_info = new RefObject<String>(info);

						boolean tempVar = !Dic.parseDELAF(sols.get(i).toString(), tempRef_entry, tempRef_lemma,
								tempRef_info);

						entry = tempRef_entry.argvalue;
						lemma = tempRef_lemma.argvalue;
						info = tempRef_info.argvalue;
						if (tempVar)
						{
							continue;
						}
						
						int len = Integer.parseInt(sols.get(i + 1).toString());
						if (thereisunamb)
						{
							ichar = cpos + len;
						}
						boolean lextags = false;
						RefObject<Boolean> tempRef_lextags = new RefObject<Boolean>(lextags);
						boolean tempVar2 = isComplex(info, tempRef_lextags);
						lextags = tempRef_lextags.argvalue;
						if (tempVar2) // i.e. no "," in info
						{
							

							ArrayList<ArrayList<String>> defactorizedexp = new ArrayList<ArrayList<String>>();
							if (processConstraints.containsKey(info))
							{
								defactorizedexp = processConstraints.get(info);
							}
							else
							{
								ArrayList<ArrayList<String>> resultingexp = processTokenAnalysis(itu, info, token, 1,
										errmessage);
								if (resultingexp == null)
								{
									if (errmessage.argvalue == null || errmessage.argvalue.equals(""))
									{
										continue;
									}
									return false;
								}
								recursiveDevelop(new ArrayList<String>(), resultingexp, defactorizedexp);
								if (lextags)
								{
									for (int iterm = 0; iterm < defactorizedexp.size(); iterm++)
									{
										ArrayList<String> sequence = defactorizedexp.get(iterm);
										processELCSFVariables(sequence, token);
										removeConstraints(sequence); // <am,be,V><be=:V><INFO+Aux> =>
																		// <am,be,V><INFO+Aux>
										sequence = concatenateAllINFOs(sequence); // <am,be,V><INFO+Aux> =>
																					// <am,be,V+Aux>
										sequence = processDollarZero(sequence, token); // <$0,table,N> =>
																						// <tables,table,N>
										defactorizedexp.set(iterm, sequence);
									}
								}
								processConstraints.put(info, defactorizedexp);
							}
							for (int iterm = 0; iterm < defactorizedexp.size(); iterm++)
							{
								ArrayList<String> sequence = defactorizedexp.get(iterm);
								if (corpus != null)
								{
									addSequenceToCorpus(annotations, hCorpusLexemes, text.hLexemes, sequence, text.mft,
											itu, cpos, cpos + len);
								}
								else
								{
									addSequenceToText(annotations, hCorpusLexemes, text.hLexemes, sequence, text.mft,
											itu, cpos, cpos + len);
								}
							}
							found = true;
						}
						else
						{
							// aidons,aider,V+t+Pres+1+p
							// pomme de terre,,N+Conc+f+s
							if (corpus != null)
							{
								addLexemeToCorpus(annotations, hCorpusLexemes, sols.get(i).toString(), text.mft, itu,
										cpos, cpos + len);
							}
							else
							{
								addLexemeToText(annotations, text.hLexemes, sols.get(i).toString(), text.mft, itu,
										cpos, cpos + len);
							}
							found = true;
						}
					}
				}

				if (!found)
				{
					if (Language.isLetter(CurrentLine.charAt(cpos)))
					{
						addUnknown(annotations, hCorpusUnknowns, text.hUnknowns, token);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param corpus
	 * @param fname
	 * @param text
	 * @param annotations
	 * @param hAmbiguities
	 * @return
	 */
	public final boolean computeAmbiguities(Corpus corpus, String fname, Ntext text, ArrayList<Object> annotations,
			RefObject<HashMap<String, ArrayList<Object>>> hAmbiguities)
	{
		

		// main loop: tokenize each text unit

		int progressPercentage = 0;
		for (int itu = 1; itu < text.mft.tuAddresses.length; itu++)
		{
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					return false;
				}
				if (corpus == null)
				{
					int nprogress = (int) (itu * 100.0F / text.nbOfTextUnits);
					if (nprogress != progressPercentage)
					{
						progressPercentage = nprogress;
						if (backgroundWorker.isBusy())
						{
							backgroundWorker.reportProgress(nprogress);
						}
					}
				}
			}

			ArrayList<AmbiguitiesUnambiguitiesObject> ambs = text.mft.getAllAmbiguitiesInTextUnit(itu);
			for (int i = 0; i < ambs.size(); i++)
			{
				double beg = ambs.get(i).getRelBegAddress() + text.mft.tuAddresses[itu];
				ArrayList<Integer> tokenids = ambs.get(i).getTokenIds();
				double end = ambs.get(i).getRelEndAddress() + text.mft.tuAddresses[itu];

				StringBuilder ambannotations = new StringBuilder();
				for (int tk : tokenids)
				{
					String lex = (String) annotations.get(tk);
					String entry = null, lemma = null, info = null;
					RefObject<String> tempRef_entry = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_info = new RefObject<String>(info);

					boolean tempVar = !Dic.parseDELAF(lex, tempRef_entry, tempRef_lemma, tempRef_info);

					entry = tempRef_entry.argvalue;
					lemma = tempRef_lemma.argvalue;
					info = tempRef_info.argvalue;
					if (tempVar)
					{
						continue;
					}
					if (lemma != null)
					{
						ambannotations.append("<" + Dic.protectComma(lemma) + "," + info + "> ");
					}
					else
					{
						ambannotations.append("<" + info + "> ");
					}
				}
				String amb = ambannotations.toString();
				if (hAmbiguities.argvalue.containsKey(amb))
				{
					ArrayList<Object> addresses = hAmbiguities.argvalue.get(amb);
					addresses.add(beg);
					addresses.add(end);
					if (corpus != null)
					{
						addresses.add(fname);
					}
					addresses.add(itu);
				}
				else
				{
					ArrayList<Object> addresses = new ArrayList<Object>();
					addresses.add(beg);
					addresses.add(end);
					if (corpus != null)
					{
						addresses.add(fname);
					}
					addresses.add(itu);
					hAmbiguities.argvalue.put(amb, addresses);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param corpus
	 * @param fname
	 * @param text
	 * @param annotations
	 * @param hUnambiguities
	 * @return
	 */
	public final boolean computeUnambiguities(Corpus corpus, String fname, Ntext text, ArrayList<Object> annotations,
			RefObject<HashMap<String, ArrayList<Object>>> hUnambiguities)
	{
		

		

		int progressPercentage = 0;
		for (int itu = 1; itu < text.mft.tuAddresses.length; itu++)
		{
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					return false;
				}
				if (corpus == null)
				{
					int nprogress = (int) (itu * 100.0F / text.nbOfTextUnits);
					if (nprogress != progressPercentage)
					{
						progressPercentage = nprogress;
						if (backgroundWorker.isBusy())
						{
							backgroundWorker.reportProgress(nprogress);
						}
					}
				}
			}
			ArrayList<AmbiguitiesUnambiguitiesObject> ambs = text.mft.getAllUnambiguitiesInTextUnit(itu);
			for (int i = 0; i < ambs.size(); i++)
			{
				double beg = ambs.get(i).getRelBegAddress() + text.mft.tuAddresses[itu];
				ArrayList<Integer> tokenids = ambs.get(i).getTokenIds();
				double end = ambs.get(i).getRelEndAddress() + text.mft.tuAddresses[itu];

				StringBuilder ambannotations = new StringBuilder();
				for (int tk : tokenids)
				{
					String lex = (String) annotations.get(tk);
					String entry = null, lemma = null, info = null;
					RefObject<String> tempRef_entry = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_info = new RefObject<String>(info);

					boolean tempVar = !Dic.parseDELAF(lex, tempRef_entry, tempRef_lemma, tempRef_info);

					entry = tempRef_entry.argvalue;
					lemma = tempRef_lemma.argvalue;
					info = tempRef_info.argvalue;
					if (tempVar)
					{
						continue;
					}
					if (lemma != null)
					{
						ambannotations.append("<" + Dic.protectComma(lemma) + "," + info + ">");
					}
					else
					{
						ambannotations.append("<" + info + ">");
					}
				}
				String amb = ambannotations.toString();
				if (hUnambiguities.argvalue.containsKey(amb))
				{
					ArrayList<Object> addresses = hUnambiguities.argvalue.get(amb);
					addresses.add(beg);
					addresses.add(end);
					if (corpus != null)
					{
						addresses.add(fname);
					}
					addresses.add(itu);
				}
				else
				{
					ArrayList<Object> addresses = new ArrayList<Object>();
					addresses.add(beg);
					addresses.add(end);
					if (corpus != null)
					{
						addresses.add(fname);
					}
					addresses.add(itu);
					hUnambiguities.argvalue.put(amb, addresses);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param dictionarylines
	 * @return
	 */
	public final String enrichDictionary(String[] dictionarylines)
	{
		

		StringBuilder resultingdic = new StringBuilder();

		

		int progressPercentage = 0;
		for (int itu = 0; itu < dictionarylines.length; itu++)
		{
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					return null;
				}
				int nprogress = (int) (itu * 100.0F / dictionarylines.length);
				if (nprogress != progressPercentage)
				{
					progressPercentage = nprogress;
					if (backgroundWorker.isBusy())
					{
						backgroundWorker.reportProgress(nprogress);
					}
				}
			}
			String currentline = dictionarylines[itu];
			if (currentline.equals("") || currentline.charAt(0) == '#')
			{
				resultingdic.append(currentline + "\n");
				continue;
			}

			String lineentry = null, linelemma = null, linecategory = null;
			String[] linefeatures = null;
			RefObject<String> tempRef_lineentry = new RefObject<String>(lineentry);
			RefObject<String> tempRef_linelemma = new RefObject<String>(linelemma);
			RefObject<String> tempRef_linecategory = new RefObject<String>(linecategory);
			RefObject<String[]> tempRef_linefeatures = new RefObject<String[]>(linefeatures);

			boolean tempVar = !Dic.parseDELAFFeatureArray(currentline, tempRef_lineentry, tempRef_linelemma,
					tempRef_linecategory, tempRef_linefeatures);

			lineentry = tempRef_lineentry.argvalue;
			linelemma = tempRef_linelemma.argvalue;
			linecategory = tempRef_linecategory.argvalue;
			linefeatures = tempRef_linefeatures.argvalue;
			if (tempVar)
			{
				continue;
			}
			if (linecategory.equals(""))
			{
				continue;
			}

			// lookup the linguistic resource and store the result
			ArrayList<String> sols;
			ArrayList<String> s_sols = null;
			if (!Lan.asianTokenizer)
			{
				s_sols = this.lookupAllLexsAndMorphsForSimples(lineentry, true, null, 0);
			}

			ArrayList<String> c_sols = this.lookupAllLexsForCompounds(0, lineentry);
			if (c_sols != null && c_sols.size() > 0)
			{
				if (s_sols != null && s_sols.size() > 0)
				{
					sols = (ArrayList<String>) s_sols.clone();
					sols.addAll(c_sols);
				}
				else
				{
					sols = (ArrayList<String>) c_sols.clone();
				}
			}
			else
			{
				if (s_sols != null && s_sols.size() > 0)
				{
					sols = (ArrayList<String>) s_sols.clone();
				}
				else
				{
					sols = null;
				}
			}

			if (sols == null)
			{
				resultingdic.append(currentline + "\n");
				continue;
			}

			StringBuilder linefeats = new StringBuilder();
			if (linefeatures != null)
			{
				for (String linefeat : linefeatures)
				{
					linefeats.append("+" + linefeat);
				}
			}

			boolean didsomething = false;
			for (int i = 0; i < sols.size(); i += 2)
			{
				StringBuilder newfeats = new StringBuilder(linefeats.toString());
				String entry = null, lemma = null, category = null;
				String[] features = null;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
				RefObject<String> tempRef_category = new RefObject<String>(category);
				RefObject<String[]> tempRef_features = new RefObject<String[]>(features);

				boolean tempVar2 = !Dic.parseDELAFFeatureArray(sols.get(i), tempRef_entry, tempRef_lemma,
						tempRef_category, tempRef_features);

				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				category = tempRef_category.argvalue;
				features = tempRef_features.argvalue;
				if (tempVar2)
				{
					continue;
				}
				if (entry.length() < lineentry.length())
				{
					continue;
				}
				if (!entry.equals(lemma))
				{
					continue;
				}
				if (!linecategory.equals(category))
				{
					continue;
				}

				Dic.getRidOfSpecialFeatures(features); // get rid of UNAMB FLX DRV COLOR
				if (features != null && features.length > 0)
				{
					ArrayList<String> newfeatures = new ArrayList<String>();
					for (String feat : features)
					{
						boolean found = false;
						if (linefeatures != null && linefeatures.length > 0)
						{
							for (String linefeat : linefeatures)
							{
								if (feat.equals(linefeat))
								{
									found = true;
									break;
								}
							}
						}
						if (!found)
						{
							newfeatures.add(feat);
						}
					}
					for (String newfeat : newfeatures)
					{
						newfeats.append("+" + newfeat);
					}
				}

				String resline = entry + "," + category + newfeats;
				resultingdic.append(resline + "\n");
				didsomething = true;
			}
			if (!didsomething)
			{
				resultingdic.append(currentline + "\n");
				continue;
			}
		}
		return resultingdic.toString();
	}

	/**
	 * 
	 * @param corpus
	 * @param text
	 */
	public final void computeTokens(Corpus corpus, Ntext text)
	{
		HashMap<String, Integer> hCorpusTokens;
		if (corpus == null)
		{
			hCorpusTokens = null;
		}
		else
		{
			hCorpusTokens = corpus.hTokens;
		}
		text.hTokens = new HashMap<String, Indexkey>();
		text.nbOfTokens = text.nbOfWords = 0;

		int progressPercentage = 0;
		for (int itu = 1; itu < text.mft.tuAddresses.length; itu++)
		{
			CurrentLine = text.buffer.substring(text.mft.tuAddresses[itu], text.mft.tuAddresses[itu]
					+ text.mft.tuLengths[itu]);
			for (int ichar = 0; ichar < CurrentLine.length();)
			{
				if (BackgroundWorking)
				{
					if (backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (corpus == null)
					{
						int nprogress = (int) (ichar * 100.0F / CurrentLine.length());
						if (nprogress != progressPercentage)
						{
							progressPercentage = nprogress;
							if (backgroundWorker.isBusy())
							{
								backgroundWorker.reportProgress(nprogress);
							}
						}
					}
				}

				// get rid of white spaces
				if (Character.isWhitespace(CurrentLine.charAt(ichar)))
				{
					ichar++;
					continue;
				}

				int cpos = ichar; // starting position of the token in the text unit
				int epos;
				String token; // current token

				text.nbOfTokens++;
				if (Language.isLetter(CurrentLine.charAt(cpos))
						|| (CurrentLine.charAt(cpos) == '\'' && Lan.russianTokenizer)) // WORD FORM
				{
					if (Lan.asianTokenizer) // word forms are single characters
					{
						text.nbOfWords++;
						ichar++;
						token = CurrentLine.substring(cpos, cpos + 1);
						epos = ichar;
					}
					else if (Lan.russianTokenizer) // word forms contain apostrophes
					{
						text.nbOfWords++;
						for (ichar++; ichar < CurrentLine.length()
								&& (CurrentLine.charAt(ichar) == '\'' || Language.isLetter(CurrentLine.charAt(ichar))); ichar++)
						{
							;
						}
						token = CurrentLine.substring(cpos, ichar);
						epos = ichar;
					}
					else
					{
						text.nbOfWords++;
						for (ichar++; ichar < CurrentLine.length() && Language.isLetter(CurrentLine.charAt(ichar)); ichar++)
						{
							;
						}
						token = CurrentLine.substring(cpos, ichar);
						epos = ichar;
					}
					addToken(hCorpusTokens, text.hTokens, token, text.mft.tuAddresses[itu] + cpos,
							text.mft.tuAddresses[itu] + epos);
				}
				else if (text.XmlNodes != null && text.buffer.charAt(cpos) == '<') // XML TAG
				{
					for (ichar++; ichar < CurrentLine.length() && CurrentLine.charAt(ichar) != '>'; ichar++)
					{
						;
					}
					if (ichar < CurrentLine.length())
					{
						ichar++;
						token = CurrentLine.substring(cpos, ichar);
						epos = ichar;
						continue;
					}
					else
					// non closed '<' are processed as delimiters
					{
						ichar = cpos + 1;
						token = CurrentLine.substring(cpos, cpos + 1);
						epos = ichar;
						continue;
					}
				}
				else
				// DELIMITER
				{
					ichar++;
					token = CurrentLine.substring(cpos, cpos + 1);
					epos = ichar;
					continue;
				}
			}
		}
	}

	/**
	 * 
	 * @param corpus
	 * @param text
	 */
	public final void computeDigrams(Corpus corpus, Ntext text)
	{
		HashMap<String, Integer> hCorpusDigrams;
		if (corpus == null)
		{
			hCorpusDigrams = null;
		}
		else
		{
			hCorpusDigrams = corpus.hDigrams;
		}

		text.hDigrams = new HashMap<String, Indexkey>();

		// main loop: tokenize each text unit
		int progressPercentage = 0;
		for (int itu = 1; itu < text.mft.tuAddresses.length; itu++)
		{
			String lasttoken = null;
			int lastbeg = 0;
			CurrentLine = text.buffer.substring(text.mft.tuAddresses[itu], text.mft.tuAddresses[itu]
					+ text.mft.tuLengths[itu]);
			for (int ichar = 0; ichar < CurrentLine.length();)
			{
				if (BackgroundWorking)
				{
					if (backgroundWorker.isCancellationPending())
					{
						return;
					}
					if (corpus == null)
					{
						int nprogress = (int) (ichar * 100.0F / CurrentLine.length());
						if (nprogress != progressPercentage)
						{
							progressPercentage = nprogress;
							if (backgroundWorker.isBusy())
							{
								backgroundWorker.reportProgress(nprogress);
							}
						}
					}
				}
				int cpos, epos; // current and ending positions of token in current text unit

				// get rid of white spaces
				if (Character.isWhitespace(CurrentLine.charAt(ichar)))
				{
					ichar++;
					continue;
				}

				cpos = ichar; // starting position of the token in the text unit
				String token; // current token

				if (Language.isLetter(CurrentLine.charAt(cpos))) // WORD FORM
				{
					if (Lan.asianTokenizer)
					{
						ichar++;
						token = CurrentLine.substring(cpos, cpos + 1);
						epos = ichar;
					}
					else
					{
						for (ichar++; ichar < CurrentLine.length() && Language.isLetter(CurrentLine.charAt(ichar)); ichar++)
						{
							;
						}
						token = CurrentLine.substring(cpos, ichar);
						epos = ichar;
					}

					if (lasttoken != null)
					{
						String digram;
						if (Lan.asianTokenizer)
						{
							digram = lasttoken + token;
						}
						else
						{
							digram = lasttoken + " " + token;
						}
						addToken(hCorpusDigrams, text.hDigrams, digram, text.mft.tuAddresses[itu] + lastbeg,
								text.mft.tuAddresses[itu] + epos);
					}
					lasttoken = token;
					lastbeg = cpos;
				}
				else if (text.XmlNodes != null && text.buffer.charAt(cpos) == '<') // XML TAG
				{
					for (ichar++; ichar < CurrentLine.length() && CurrentLine.charAt(ichar) != '>'; ichar++)
					{
						;
					}
					if (ichar < CurrentLine.length())
					{
						ichar++;
						token = CurrentLine.substring(cpos, ichar);
						continue;
					}
					else
					// non closed '<' are processed as delimiters
					{
						ichar = cpos + 1;
						token = CurrentLine.substring(cpos, cpos + 1);
						continue;
					}
				}
				else
				// DELIMITER
				{
					ichar++;
					token = CurrentLine.substring(cpos, cpos + 1);
					continue;
				}
			}
		}
	}

	private boolean alreadythere(TheSolutions thesolutions, int inewsols, int tunb, double position, double length,
			ArrayList<Double> input, ArrayList<String> output)
	{
		if (inewsols == -1)
		{
			inewsols = 0;
		}
		for (int isol = inewsols; isol < thesolutions.list.size(); isol++)
		{
			int stunb = thesolutions.getTuNb(isol);
			if (stunb != tunb)
			{
				continue;
			}

			double sposition = thesolutions.getBegAddress(isol);
			if (sposition != position)
			{
				continue;
			}

			double slength = thesolutions.getLength(isol);
			if (slength != length)
			{
				continue;
			}

			boolean identical = true;
			ArrayList<Double> sinput = thesolutions.getInput(isol);
			if (sinput.size() != input.size())
			{
				continue;
			}
			for (int j = 0; j < sinput.size(); j++)
			{
				double f = input.get(j);
				double sf = sinput.get(j);
				if (f != sf)
				{
					identical = false;
					break;
				}
			}
			if (!identical)
			{
				continue;
			}

			ArrayList<String> soutput = thesolutions.getOutput(isol);
			if (soutput.size() != output.size())
			{
				continue;
			}
			for (int j = 0; j < soutput.size(); j++)
			{
				String o = output.get(j);
				String so = soutput.get(j);
				if (o==null && so==null) continue;
				if (o==null || so==null || !o.equals(so))
				{
					identical = false;
					break;
				}
			}
			if (!identical)
			{
				continue;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param sollengths
	 * @param solinputs
	 * @param solvariables
	 * @param soloutputs
	 * @param found
	 * @param smallerlength
	 * @return
	 */
	private int filterUnamb(ArrayList<Double> sollengths, ArrayList<ArrayList<Double>> solinputs,
			ArrayList<ArrayList<String>> solvariables, ArrayList<ArrayList<String>> soloutputs,
			RefObject<Boolean> found, RefObject<Double> smallerlength)
	{
		found.argvalue = false;
		smallerlength.argvalue = -1.0;

		for (int isol = 0; isol < soloutputs.size(); isol++)
		{
			ArrayList<String> output = soloutputs.get(isol);
			for (int io = 0; io < output.size(); io++)
			{
				String po = output.get(io);
				if (po == null || po.equals(""))
				{
					continue;
				}
				if (po.indexOf("UNAMB") != -1)
				{
					found.argvalue = true;
					break;
				}
			}
			if (found.argvalue)
			{
				break;
			}
		}
		if (!found.argvalue)
		{
			return soloutputs.size();
		}

		// there are UNAMB: filter out all solutions that have no UNAMB
		int maxfound2 = -1;
		for (int isol = 0; isol < soloutputs.size();)
		{
			int found2 = 0;
			ArrayList<String> output = soloutputs.get(isol);
			for (int io = 0; io < output.size(); io++)
			{
				String po = output.get(io);
				if (po == null || po.equals(""))
				{
					continue;
				}
				if (po.indexOf("UNAMB") != -1)
				{
					found2++;
				}
			}
			if (found2 > maxfound2)
			{
				maxfound2 = found2;
			}
			if (found2 == 0)
			{
				// need to remove solution isol
				sollengths.remove(isol);
				solinputs.remove(isol);
				solvariables.remove(isol);
				soloutputs.remove(isol);
			}
			else
			{
				isol++;
			}
		}

		if (soloutputs.size() > 1) // if there are more than 1 remaining solution with +UNAMB
		{
			// this comes from the syntactic parsing => just keep the ones with the maximum number of UNAMB
			for (int isol = 0; isol < soloutputs.size();)
			{
				int found2 = 0;
				ArrayList<String> output = soloutputs.get(isol);
				for (int io = 0; io < output.size(); io++)
				{
					String po = output.get(io);
					if (po == null || po.equals(""))
					{
						continue;
					}
					if (po.indexOf("UNAMB") != -1)
					{
						found2++;
					}
				}
				if (found2 < maxfound2)
				{
					// need to remove solution isol
					sollengths.remove(isol);
					solinputs.remove(isol);
					solvariables.remove(isol);
					soloutputs.remove(isol);
				}
				else
				{
					isol++;
				}
			}
		}

		// finally: get rid of the +UNAMB feature, everywhere and compute smallerlength
		smallerlength.argvalue = (double) sollengths.get(0);
		for (int isol = 0; isol < soloutputs.size(); isol++)
		{
			if (sollengths.get(isol) < smallerlength.argvalue)
			{
				smallerlength.argvalue = (double) sollengths.get(isol);
			}
			ArrayList<String> output = soloutputs.get(isol);
			for (int io = 0; io < output.size(); io++)
			{
				String po = output.get(io);
				if (po == null || po.equals(""))
				{
					continue;
				}
				if (po.equals("UNAMB") || po.equals("+UNAMB"))
				{
					output.set(io, "");
				}
				int iu = po.indexOf("+UNAMB");
				if (iu != -1)
				{
					output.set(io, po.substring(0, iu));
					output.set(io, output.get(io) + po.substring(iu + 6));
				}
			}
		}
		return soloutputs.size();
	}

	/**
	 * 
	 * @param sollengths
	 * @param solinputs
	 * @param solvariables
	 * @param soloutputs
	 * @param minimlength
	 * @param longestlength
	 * @return
	 */
	private int filterExclude(ArrayList<Double> sollengths, ArrayList<ArrayList<Double>> solinputs,
			ArrayList<ArrayList<String>> solvariables, ArrayList<ArrayList<String>> soloutputs,
			RefObject<Boolean> minimlength, RefObject<Double> longestlength)
	{
		minimlength.argvalue = false;
		longestlength.argvalue = -1.0;

		for (int isol = 0; isol < soloutputs.size(); isol++)
		{
			ArrayList<String> output = soloutputs.get(isol);
			for (int io = 0; io < output.size(); io++)
			{
				String po = output.get(io);
				if (po == null || po.equals(""))
				{
					continue;
				}
				if (po.indexOf("EXCLUDE") != -1)
				{
					if (sollengths.get(isol) > longestlength.argvalue)
					{
						minimlength.argvalue = true;
						longestlength.argvalue = (double) sollengths.get(isol);
					}
				}
			}
		}

		if (minimlength.argvalue)
		{
			sollengths.clear();
			solinputs.clear();
			solvariables.clear();
			soloutputs.clear();
			return 0;
		}
		return soloutputs.size();
	}

	/**
	 * 
	 * @param sollengths
	 * @param solinputs
	 * @param solvariables
	 * @param soloutputs
	 * @return
	 */
	private int keepLongest(ArrayList<Double> sollengths, ArrayList<ArrayList<Double>> solinputs,
			ArrayList<ArrayList<String>> solvariables, ArrayList<ArrayList<String>> soloutputs)
	{
		double longestlength = sollengths.get(0);
		for (int isol = 1; isol < sollengths.size(); isol++)
		{
			if (sollengths.get(isol) > longestlength)
			{
				longestlength = sollengths.get(isol);
			}
		}

		for (int isol = 0; isol < sollengths.size();)
		{
			if (sollengths.get(isol) < longestlength)
			{
				sollengths.remove(isol);
				solinputs.remove(isol);
				solvariables.remove(isol);
				soloutputs.remove(isol);
			}
			else
			{
				isol++;
			}
		}
		return sollengths.size();
	}

	/**
	 * 
	 * @param sollengths
	 * @param solinputs
	 * @param solvariables
	 * @param soloutputs
	 * @return
	 */
	private int keepShortest(ArrayList<Double> sollengths, ArrayList<ArrayList<Double>> solinputs,
			ArrayList<ArrayList<String>> solvariables, ArrayList<ArrayList<String>> soloutputs)
	{
		double shortestlength = sollengths.get(0);
		for (int isol = 1; isol < sollengths.size(); isol++)
		{
			if (sollengths.get(isol) < shortestlength)
			{
				shortestlength = sollengths.get(isol);
			}
		}

		for (int isol = 0; isol < soloutputs.size();)
		{
			if (sollengths.get(isol) > shortestlength)
			{
				sollengths.remove(isol);
				solinputs.remove(isol);
				solvariables.remove(isol);
				soloutputs.remove(isol);
			}
			else
			{
				isol++;
			}
		}
		return sollengths.size();
	}

	/**
	 * 
	 * @param output
	 */
	private void deleteONCE(ArrayList<String> output)
	{
		for (int io = 0; io < output.size(); io++)
		{
			String po = output.get(io);
			if (po == null)
			{
				continue;
			}
			if (po.indexOf("<ONCE") == 0)
			{
				output.set(io, null);
			}
		}
	}

	/**
	 * 
	 * @param sollengths
	 * @param solinputs
	 * @param solvariables
	 * @param soloutputs
	 * @return
	 */
	private int filterXrefs(ArrayList<Double> sollengths, ArrayList<ArrayList<Double>> solinputs,
			ArrayList<ArrayList<String>> solvariables, ArrayList<ArrayList<String>> soloutputs)
	{
		while (true)
		{
			int index = -1;
			boolean removeSolutionBreaked = false;
			REMOVESOLUTION: for (int isol = 0; isol < solvariables.size(); isol++)
			{
				String xref = null;
				int nbofxrefs = 0;

				ArrayList<String> variable = solvariables.get(isol);
				for (int iv = 0; iv < variable.size(); iv++)
				{
					String lex = variable.get(iv);
					if (lex == null || lex.equals(""))
					{
						continue;
					}
					int posxref = lex.indexOf("XREF=");
					if (posxref != -1)
					{
						StringBuilder sb = new StringBuilder();
						for (int i = posxref + (new String("XREF=")).length(); i < lex.length()
								&& (Character.isDigit(lex.charAt(i)) || lex.charAt(i) == '.'); i++)
						{
							sb.append(lex.charAt(i));
						}
						String currentxref = sb.toString();
						if (xref == null)
						{
							xref = currentxref;
							nbofxrefs = 1;
						}
						else
						{
							if (!xref.equals(currentxref))
							{
								index = isol;
								removeSolutionBreaked = true;
								break REMOVESOLUTION;
							}
							else
							{
								nbofxrefs++;
							}
						}
					}
				}
				if (xref == null)
				{
					continue;
				}

				String suf = xref.substring(xref.indexOf('.') + 1);
				int xrefnumber = Integer.parseInt(suf);
				if (xrefnumber != nbofxrefs)
				{
					index = isol;
					removeSolutionBreaked = true;
					break REMOVESOLUTION;
				}
			}
			if (!removeSolutionBreaked)
				return soloutputs.size();

			// I need to remove this inconsistant XREF
			sollengths.remove(index);
			solinputs.remove(index);
			solvariables.remove(index);
			soloutputs.remove(index);
			continue;
		}
	}

	/**
	 * 
	 * @param lexcategory
	 * @param lexfeatures
	 * @param morphoperator
	 * @return
	 */
	private boolean lexInfoMatchOperator(String lexcategory, String[] lexfeatures, String morphoperator)
	{
		String[] operators;
		int index = morphoperator.indexOf('+');
		if (index != -1 && index != 0)
		{
			// morphoperator contains a category, e.g. "N+s"
			String cat = morphoperator.substring(0, index);
			if (!lexcategory.equals(cat))
			{
				return false;
			}
			operators = Dic.getAllFeaturesWithoutPlus(morphoperator.substring(index));
		}
		else
		{
			operators = Dic.getAllFeaturesWithoutPlus(morphoperator);
		}
		for (String op : operators)
		{
			if (lexcategory.equals(op))
			{
				continue; // morpho-op category matches lex-category: look at other features
			}
			boolean found = false;
			for (String lexfeat : lexfeatures)
			{
				if (op.equals(lexfeat))
				{
					found = true; // morpho-op feature matches lex-feature: look at other features
					break;
				}
				if (lexfeat.indexOf('=') != -1)
				{
					String propname = null, propvalue = null;
					RefObject<String> tempRef_propname = new RefObject<String>(propname);
					RefObject<String> tempRef_propvalue = new RefObject<String>(propvalue);
					Dic.getPropertyNameValue(lexfeat, tempRef_propname, tempRef_propvalue);
					propname = tempRef_propname.argvalue;
					propvalue = tempRef_propvalue.argvalue;
					if (op.equals(propname) || op.equals(propvalue))
					{
						found = true;
						break;
					}
				}
			}
			if (!found)
			{
				return false;
			}
		}
		return true;
	}

	private Engine engine2;

	/**
	 * 
	 * @param text
	 * @param varcompletename
	 * @param compoundvariable
	 * @param positions
	 * @param rightmargin
	 * @param variables
	 * @param cpos
	 * @param ipos
	 * @param lan1
	 * @param textmft
	 * @param annotations
	 * @param tunb
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String[] getValues(String text, String varcompletename, RefObject<Boolean> compoundvariable,
			ArrayList<Double> positions, int rightmargin, ArrayList<String> variables, double cpos, int ipos,
			RefObject<Language> lan1, Mft textmft, ArrayList<Object> annotations, int tunb) throws IOException,
			ClassNotFoundException
	{
		lan1.argvalue = null;
		compoundvariable.argvalue = false;

		boolean listvariable = varcompletename.charAt(0) == '$'; // e.g. $$variable: used to store lists of values
		if (listvariable)
		{
			varcompletename = varcompletename.substring(1);
		}

		// get variable name (remove _ and $ suffixes)
		String varname;
		int index1 = varcompletename.indexOf('_');
		int index2 = varcompletename.indexOf('$', 1);
		int index = index1;
		if (index == -1)
		{
			index = index2;
		}
		else if (index2 == -1)
		{
			index = index1;
		}
		else if (index2 < index)
		{
			index = index2;
		}
		if (index == -1)
		{
			index = varcompletename.indexOf('$');
			if (index == -1)
			{
				varname = varcompletename;
			}
			else
			{
				varname = varcompletename.substring(0, index);
			}
		}
		else
		{
			varname = varcompletename.substring(0, index);
		}
		if (!varcompletename.equals(varname))
		{
			compoundvariable.argvalue = true;
		}
		if (!listvariable)
		{
			// scan the input to the left to get the range of the variable $(varname
			boolean symbolvar = false;
			index = -1;
			for (int iinput = ipos; iinput >= 0; iinput--)
			{
				// look for $(varname
				String si = variables.get(iinput);
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
				// nothing to the left: scan the input to the right $(varname
				for (int iinput = ipos; iinput < variables.size(); iinput++)
				{
					// look for $(varname
					String si = variables.get(iinput);
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
			}

			
			if (index == -1)
			{
				return null;
			}

			int begaddress = (int) (double) positions.get(index);
			int endaddress;
			int jinput;

			String sj = null;
			if (symbolvar)
			{
				jinput = index;
				if (index + 1 < positions.size())
				{
					endaddress = (int) (double) positions.get(index + 1);
				}
				else
				{
					endaddress = rightmargin;
				}
				if (endaddress == -1)
				{
					int iindex = 1;
					while (endaddress == -1 && index + iindex < positions.size())
					{
						iindex++;
						endaddress = (int) (double) positions.get(index + iindex);
					}
				}
				if (varcompletename.length() == varname.length()) // variable is a simple text, e.g. $Var
				{
					String res = text.substring(begaddress, endaddress).trim();
					String[] results = new String[1];
					results[0] = res;
					return results;
				}
				else
				// variable includes morphological operators, e.g. $Var$Nb or $Var_V+PP or $Var$FR_N+p
				{
					// get all the annotations for position begaddress
					String lex = variables.get(index);
					ArrayList<String> lexs = new ArrayList<String>();
					lexs.add(lex);
					String[] results = computeDerivations(varcompletename.substring(varname.length()), lexs, text,
							positions, lan1, index, jinput);
					return results;
				}
			}
			else
			{
				// look for corresponding $)
				int reclevel = 0;
				for (jinput = index + 1; jinput < variables.size(); jinput++)
				{
					sj = variables.get(jinput);
					if (sj == null || sj.length() == 0)
					{
						continue;
					}
					if (sj.charAt(0) == '$' && sj.length() > 2 && sj.charAt(1) == '(')
					{
						reclevel++;
						continue;
					}
					else if (sj.charAt(0) == '$' && sj.length() >= 2 && sj.charAt(1) == ')')
					{
						if (reclevel == 0)
						{
							break;
						}
						reclevel--;
					}
				}
				if (jinput < positions.size())
				{
					endaddress = (int) (double) positions.get(jinput);
				}
				else
				{
					// did not find the closing parenthesis
					String[] results = new String[1];
					results[0] = "*NoClosingParenthesisForVariable " + varname + "*";
					return results;
				}
				if (sj != null && sj.length() > 2)
				{
					// variable's value is set after closing parenthesis
					String[] results = new String[1];
					results[0] = sj.substring(2);
					return results;
				}
				
				else
				{
					if (varcompletename.length() == varname.length()) // variable is a simple text, e.g. $Var
					{
						String res = text.substring(begaddress, endaddress).trim();
						String[] results = new String[1];
						results[0] = res;
						return results;
					}
					else
					// variable includes morphological operators, e.g. $Var$Nb or $Var_V+PP or $Var$FR_N+p
					{
						// get all the annotations for position begaddress
						String lex = getLexFromVariable(variables, index, varname);
						ArrayList<String> lexs = new ArrayList<String>();
						lexs.add(lex);
						String[] results = computeDerivations(varcompletename.substring(varname.length()), lexs, text,
								positions, lan1, index + 1, jinput);
						return results;
					}
				}
			}
		}
		else
		{
			// double $$variable: used for lists of values
			// scan the input from the left to get each range of the variable
			boolean foundatleastone = false;
			String[] results = new String[1];
			results[0] = "";
			for (int iinput = 0; iinput < variables.size(); iinput++)
			{
				String si = variables.get(iinput);
				if (si == null || si.length() == 0)
				{
					continue;
				}
				if (!si.equals("$(" + varname))
				{
					continue;
				}
				foundatleastone = true;

				int begaddress = (int) (double) positions.get(iinput);
				int endaddress;
				int jinput;
				String sj = null;

				// look for corresponding $)
				int reclevel = 0;
				for (jinput = iinput + 1; jinput < variables.size(); jinput++)
				{
					sj = variables.get(jinput);
					if (sj == null || sj.length() == 0)
					{
						continue;
					}
					if (sj.charAt(0) == '$' && sj.length() > 2 && sj.charAt(1) == '(')
					{
						reclevel++;
						continue;
					}
					else if (sj.charAt(0) == '$' && sj.length() >= 2 && sj.charAt(1) == ')')
					{
						if (reclevel == 0)
						{
							break;
						}
						reclevel--;
					}
				}
				if (jinput < positions.size())
				{
					endaddress = (int) (double) positions.get(jinput);
				}
				else
				// did not find the closing parenthesis
				{
					results[0] += "*NoClosingParenthesisForVariable " + varname + "*";

					iinput = jinput;
					continue;
				}

				if (sj != null && sj.length() > 2) // variable's value is set after closing parenthesis
				{
					results[0] += sj.substring(2);

					iinput = jinput;
					continue;
				}
				else if (begaddress == endaddress)
				{
					results[0] += "*EmptyString*";

					iinput = jinput;
					continue;
				}
				else
				{
					if (varcompletename.length() == varname.length()) // variable is a simple text, e.g. $Var
					{
						String res = text.substring(begaddress, endaddress).trim();
						results[0] += res;

						iinput = jinput;
						continue;
					}
					else
					// variable includes morphological operators, e.g. $Var$Nb or $Var_V+PP or $Var$FR_N+p
					{
						// get all the annotations for position begaddress
						String lex = getLexFromVariable(variables, iinput, varname);
						ArrayList<String> lexs = new ArrayList<String>();
						lexs.add(lex);
						String[] intermediateresults = computeDerivations(varcompletename.substring(varname.length()),
								lexs, text, positions, lan1, index + 1, jinput);
						results[0] += intermediateresults[0];

						iinput = jinput;
						continue;
					}
				}
			}
			if (!foundatleastone)
			{
				// no $(varname to the left nor the right of the calling $varname: check out categories in symbol to the
				// left
				for (int iinput = 0; iinput < variables.size(); iinput++)
				{
					String si = variables.get(iinput);
					if (si == null || si.length() == 0)
					{
						continue;
					}
					String entry = null, lemma = null, cat = null, features = null;
					RefObject<String> tempRef_entry = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_cat = new RefObject<String>(cat);
					RefObject<String> tempRef_features = new RefObject<String>(features);

					boolean tempVar = !Dic.parseDELAF(si, tempRef_entry, tempRef_lemma, tempRef_cat, tempRef_features);

					entry = tempRef_entry.argvalue;
					lemma = tempRef_lemma.argvalue;
					cat = tempRef_cat.argvalue;
					features = tempRef_features.argvalue;
					if (tempVar)
					{
						continue;
					}
					if (cat == null || cat.equals(""))
					{
						continue;
					}
					if (!varname.equals(cat))
					{
						continue;
					}
					foundatleastone = true;

					int begaddress = (int) (double) positions.get(index);
					int endaddress;
					int jinput;

					jinput = iinput;
					endaddress = (int) cpos;
					if (varcompletename.length() == varname.length()) // variable is a simple text, e.g. $Var
					{
						String res = text.substring(begaddress, endaddress).trim();
						results[0] += res;

						iinput = jinput;
						continue;
					}
					else
					// variable includes morphological operators, e.g. $Var$Nb or $Var_V+PP or $Var$FR_N+p
					{
						// get all the annotations for position begaddress
						String lex = variables.get(index);
						ArrayList<String> lexs = new ArrayList<String>();
						lexs.add(lex);
						String[] intermediateresults = computeDerivations(varcompletename.substring(varname.length()),
								lexs, text, positions, lan1, index, jinput);
						results[0] += intermediateresults[0];

						iinput = jinput;
						continue;
					}
				}

			}
			return results;
		}
	}

	/**
	 * 
	 * @param listofresults
	 * @param index
	 * @return
	 */
	private ArrayList<String> recDevelop(ArrayList<String[]> listofresults, int index)
	{
		// listofresults is a factorized list of results, e.g. {a,b}{x,y}
		// we want to develop it, => {a x,b x, a y,b y}
		ArrayList<String> res = new ArrayList<String>();
		if (index >= listofresults.size())
		{
			res.add(null);
			return res;
		}

		ArrayList<String> tail = recDevelop(listofresults, index + 1);
		for (String c : listofresults.get(index))
		{
			for (String t : tail)
			{
				if (t != null)
				{
					res.add(c + " " + t);
				}
				else
				{
					res.add(c);
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param text
	 * @param varcompletename
	 * @param compoundvariable
	 * @param positions
	 * @param rightmargin
	 * @param variables
	 * @param cpos
	 * @param ipos
	 * @param lan1
	 * @param textmft
	 * @param annotations
	 * @param tunb
	 * @param varvalues
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String[] newGetValues(String text, String varcompletename, RefObject<Boolean> compoundvariable,
			ArrayList<Double> positions, int rightmargin, ArrayList<String> variables, double cpos, int ipos,
			RefObject<Language> lan1, Mft textmft, ArrayList<Object> annotations, int tunb,
			RefObject<ArrayList<HashMap<String, String>>> varvalues, RefObject<String> errmessage) throws IOException,
			ClassNotFoundException
	{
		lan1.argvalue = null;
		compoundvariable.argvalue = false;
		varvalues.argvalue = null;
		errmessage.argvalue = null;

		// get variable name (remove _ and $ suffixes)
		String varname;
		int index1 = varcompletename.indexOf('_');
		int index2 = varcompletename.indexOf('$', 1);
		int index = index1;
		if (index == -1)
		{
			index = index2;
		}
		else if (index2 == -1)
		{
			index = index1;
		}
		else if (index2 < index)
		{
			index = index2;
		}
		if (index == -1)
		{
			index = varcompletename.indexOf('$');
			if (index == -1)
			{
				varname = varcompletename;
			}
			else
			{
				varname = varcompletename.substring(0, index);
			}
		}
		else
		{
			varname = varcompletename.substring(0, index);
		}
		if (!varcompletename.equals(varname))
		{
			compoundvariable.argvalue = true;
		}

		int begaddress, endaddress;
		int indexend;
		if (varname.equals("THIS"))
		{
			index = ipos;
			begaddress = (int) (double) positions.get(index);

			for (indexend = index + 1; indexend < positions.size(); indexend++)
			{
				String var = variables.get(indexend);
				if (var == null)
				{
					continue;
				}
				break;
			}
			if (indexend >= positions.size()) // no lexeme is stored for THIS
			{
				indexend = index;
				endaddress = (int) (double) positions.get(indexend);
			}
			else
			{
				indexend++;
				if (indexend >= positions.size())
				{
					endaddress = (int) (double) positions.get(indexend - 1);
				}
				else
				{
					endaddress = (int) (double) positions.get(indexend);
				}
			}
		}
		else
		{
			// scan the input to the left to get the range of the variable $(varname
			index = -1;
			for (int iinput = ipos; iinput >= 0; iinput--)
			{
				// look for $(varname
				String si = variables.get(iinput);
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
				// nothing to the left: scan the input to the right $(varname
				for (int iinput = ipos; iinput < variables.size(); iinput++)
				{
					// look for $(varname
					String si = variables.get(iinput);
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
			}

			if (index == -1)
			{
				errmessage.argvalue = "Undefined variable $" + varname;
				Dic.writeLog(errmessage.argvalue);
				return null;

			}

			begaddress = (int) (double) positions.get(index);
			String sj = null;

			// look for corresponding $)
			int reclevel = 0;
			for (indexend = index + 1; indexend < variables.size(); indexend++)
			{
				sj = variables.get(indexend);
				if (sj == null || sj.length() == 0)
				{
					continue;
				}
				if (sj.charAt(0) == '$' && sj.length() > 2 && sj.charAt(1) == '(')
				{
					reclevel++;
					continue;
				}
				else if (sj.charAt(0) == '$' && sj.length() >= 2 && sj.charAt(1) == ')')
				{
					if (reclevel == 0)
					{
						break;
					}
					reclevel--;
				}
			}
			if (indexend < positions.size())
			{
				endaddress = (int) (double) positions.get(indexend);
			}
			else
			{
				// did not find the closing parenthesis
				errmessage.argvalue = "No closing Parenthesis for variable " + varname;
				Dic.writeLog(errmessage.argvalue);
				return null;
			}
			if (sj != null && sj.length() > 2)
			{
				// variable's value is set after closing parenthesis
				String[] results = new String[1];
				results[0] = sj.substring(2);
				return results;
			}
		}
		if (varcompletename.length() == varname.length()) // variable is a simple variable, e.g. $Var
		{
			String[] results;
			if (begaddress == endaddress)
			{
				// *EMPTYSTRING*"
				results = new String[1];
				results[0] = "";
				return results;
			}
			String res = text.substring(begaddress, endaddress).trim();
			results = new String[1];
			results[0] = res;

			varvalues.argvalue = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> varvalue = new HashMap<String, String>();
			varvalue.put(varname, res);
			varvalues.argvalue.add(varvalue);

			String lex = null;
			for (int ii = index + 1; ii < indexend; ii++)
			{
				String var = variables.get(ii);
				String entry = null, lemma = null, info = null;
				RefObject<String> tempRef_entry = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
				RefObject<String> tempRef_info = new RefObject<String>(info);

				boolean tempVar = var != null && Dic.parseDELAF(var, tempRef_entry, tempRef_lemma, tempRef_info);

				entry = tempRef_entry.argvalue;
				lemma = tempRef_lemma.argvalue;
				info = tempRef_info.argvalue;
				if (tempVar)
				{
					lex = var;
					break;
				}
			}
			if (lex != null)
			{
				varvalue.put("LU_" + varname, lex);
			}
			return results;
		}
		else
		// variable includes morphological operators, e.g. $Var$Nb or $Var_V+PP or $Var$FR_N+p
		{
			// get all the annotations from position index to jinput

			ArrayList<String[]> listofresults = new ArrayList<String[]>();
			String lex = null;
			for (int ii = index + 1; ii < indexend; ii++)
			{
				String var = variables.get(ii);
				String entry = null, lemma = null, info = null;
				RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
				RefObject<String> tempRef_info2 = new RefObject<String>(info);

				boolean tempVar2 = var != null && Dic.parseDELAF(var, tempRef_entry2, tempRef_lemma2, tempRef_info2);

				entry = tempRef_entry2.argvalue;
				lemma = tempRef_lemma2.argvalue;
				info = tempRef_info2.argvalue;
				if (tempVar2)
				{
					lex = var;
					ArrayList<String> lexs = new ArrayList<String>();
					lexs.add(lex);
					String[] cresults = computeDerivations(varcompletename.substring(varname.length()), lexs, text,
							positions, lan1, index + 1, indexend);
					if (cresults != null)
					{
						listofresults.add(cresults);
					}
				}
			}
			// combine every list of results to get all possible set of variable values

			if (listofresults.isEmpty())
			{
				return null;
			}

			ArrayList<String> devlistofresults = recDevelop(listofresults, 0);
			String[] results = new String[devlistofresults.size()];
			varvalues.argvalue = new ArrayList<HashMap<String, String>>();
			int i = 0;
			for (String res : devlistofresults)
			{
				results[i++] = res;
				HashMap<String, String> varvalue = new HashMap<String, String>();
				varvalue.put("LU_" + varname, lex);
				varvalue.put(varcompletename, res);
				varvalues.argvalue.add(varvalue);
			}
			return results;
		}
	}

	/**
	 * 
	 * @param varcompletename
	 * @param variables
	 * @param ipos
	 * @return
	 */
	private int getPositionIndex(String varcompletename, ArrayList<String> variables, int ipos)
	{
		// get variable name (remove _ suffix)
		String varname;
		int index1 = varcompletename.indexOf('_');
		int index2 = varcompletename.indexOf('$');
		int index = index1;
		if (index == -1)
		{
			index = index2;
		}
		else if (index2 == -1)
		{
			index = index1;
		}
		else if (index2 < index)
		{
			index = index2;
		}
		if (index == -1)
		{
			varname = varcompletename;
		}
		else
		{
			varname = varcompletename.substring(0, index);
		}

		// scan the input to the left to get the value of the variable
		for (int iinput = ipos; iinput >= 0; iinput--)
		{
			// look for $(varname
			String si = variables.get(iinput);
			if (si == null || si.length() == 0)
			{
				continue;
			}
			if (si.equals("$(" + varname))
			{
				return iinput;
			}
		}
		// not to the left: scan the input to the right
		for (int iinput = ipos; iinput < variables.size(); iinput++)
		{
			// look for $(varname
			String si = variables.get(iinput);
			if (si == null || si.length() == 0)
			{
				continue;
			}
			if (si.equals("$(" + varname))
			{
				return iinput;
			}
		}

	
		return -1;
	}

	/**
	 * 
	 * @param label
	 * @param inputs
	 * @return
	 */
	final String processVariableInMorphoLabel(String label, ArrayList<String> inputs)
	{
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < label.length();)
		{
			if (label.charAt(i) == '\\')
			{
				res.append(label.charAt(i));
				res.append(label.charAt(i + 1));
				i += 2;
				continue;
			}
			else if (label.charAt(i) != '$')
			{
				res.append(label.charAt(i));
				i++;
				continue;
			}
			if (i + 1 < label.length() && (label.charAt(i + 1) == '(' || label.charAt(i + 1) == ')'))
			// that's a setting, e.g. "$(Var" or "$)"
			{
				res.append(label.charAt(i));
				i++;
				continue;
			}
			else if (i + 1 < label.length()
					&& (label.charAt(i + 1) == '>' || label.charAt(i + 1) == '}' || label.charAt(i + 1) == '$'
							|| label.charAt(i + 1) == ',' || label.charAt(i + 1) == '=' || label.charAt(i + 1) == '#'))
			// that's a <$>, i.e. end of text unit
			{
				res.append(label.charAt(i));
				i++;
				continue;
			}

			// get variable name
			int j;
			for (i++, j = 0; i + j < label.length() && !Character.isWhitespace(label.charAt(i + j))
					&& label.charAt(i + j) != '#' && label.charAt(i + j) != '=' && label.charAt(i + j) != ','
					&& label.charAt(i + j) != '>' && label.charAt(i + j) != '}'; j++)
			{
				;
			}
			String varname = label.toString().substring(i, i + j);
			if (i + j < label.length() && label.charAt(i + j) == '#')
			{
				j++;
			}

			String val = getMorphoValue(varname, inputs);
			if (val == null || val.length() == 0)
			{
				res.append("*UNDEFINED*");
			}
			else
			{
				res.append(val);
			}
			i += j;
		}
		return res.toString();
	}

	/**
	 * 
	 * @param label
	 * @return
	 */
	final boolean thereIsAVariableInLabel(String label)
	{
		for (int i = 0; i < label.length();)
		{
			if (label.charAt(i) == '\\')
			{
				i += 2;
				continue;
			}
			else if (label.charAt(i) == '"')
			{
				for (i++; i < label.length() && label.charAt(i) != '"'; i++)
				{
					;
				}
				i++;
				continue;
			}
			else if (label.charAt(i) == '$')
			{
				if (i + 1 < label.length() && (label.charAt(i + 1) == '(' || label.charAt(i + 1) == ')'))
				{
					// that's a setting, e.g. "$(Var" or "$)"
					i++;
					continue;
				}
				else if (i + 1 < label.length() && label.charAt(i + 1) == '>')
				{
					// that's a <$>, i.e. end of text unit
					i++;
					continue;
				}
				else if (i + 1 == label.length())
				{
					return false;
				}
				return true;
			}
			else
			{
				i++;
				continue;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param variable
	 * @param pos1
	 * @return
	 */
	private String getLexFromVariable(ArrayList<String> variable, int pos1)
	{
		for (int i = pos1 + 1; i < variable.size(); i++)
		{
			String line = null, entry = null, lemma = null, info = null;
			line = variable.get(i);
			if (line == null || line.equals(""))
			{
				continue;
			}
			int level = 1;
			if (line.length() > 2 && line.charAt(0) == '$' && line.charAt(1) == '(')
			{
				level++;
			}
			else if (line.length() >= 2 && line.charAt(0) == '$' && line.charAt(1) == ')')
			{
				level--;
				if (level == 0)
				{
					return null;
				}
			}
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_info = new RefObject<String>(info);

			boolean tempVar = Dic.parseDELAF(line, tempRef_entry, tempRef_lemma, tempRef_info);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			info = tempRef_info.argvalue;
			if (tempVar)
			{
				return line;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param variables
	 * @param position
	 * @param varname
	 * @return
	 */
	private String getLexFromVariable(ArrayList<String> variables, int position, String varname)
	{
		int vpos = getPositionIndex(varname, variables, position);
		if (vpos == -1)
		{
			return null;
		}
		return getLexFromVariable(variables, vpos);
	}

	/**
	 * 
	 * @param current
	 * @param textmft
	 * @param tunb
	 * @param annotations
	 * @param grammar
	 * @param position
	 * @param rightmargin
	 * @param variable
	 * @param cpos
	 * @param ipos
	 * @param so
	 * @param sols
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private final boolean processConstraint(String current, Mft textmft, int tunb, ArrayList<Object> annotations,
			Grammar grammar, ArrayList<Double> position, int rightmargin, ArrayList<String> variable, double cpos,
			int ipos, String so, RefObject<ArrayList<String>> sols, RefObject<String> errmessage) throws IOException,
			ClassNotFoundException
	{
		ParameterCheck.mandatory("grammar", grammar);
		ParameterCheck.mandatoryString("so", so);
		ParameterCheck.mandatory("sols", sols);
		ParameterCheck.mandatory("errmessage", errmessage);

		sols.argvalue = null;
		// there might be one or two variables, e.g. <yy=zz>, <yy!=zz>, <$Det=:DET+s>, <$Det$Nb=$N$Nb>
		int pos1 = -1, pos2 = -1;
		Language lan1 = null;
		String[] str1 = null;
		String[] str2 = null;
		boolean compound1 = false;
		boolean compound2 = false;
		errmessage.argvalue = null;

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
			int j = 0;
			RefObject<Integer> tempRef_j = new RefObject<Integer>(j);
			String varname = Dic.getFullVariableName(so, i, tempRef_j);
			j = tempRef_j.argvalue;
			i = j;

			int vpos = getPositionIndex(varname, variable, ipos);
			if (vpos == -1)
			{
				pos1 = -2;
				errmessage.argvalue = "$" + varname + " is undefined";
				res.append("*UNDEFINED*");
			}
			else
			{
				if (pos1 == -1)
				{
					// left variable
					pos1 = vpos;
					RefObject<Boolean> tempRef_compound1 = new RefObject<Boolean>(compound1);
					RefObject<Language> tempRef_lan1 = new RefObject<Language>(lan1);
					str1 = getValues(current, varname, tempRef_compound1, position, rightmargin, variable, cpos, pos1,
							tempRef_lan1, textmft, annotations, tunb);
					compound1 = tempRef_compound1.argvalue;
					lan1 = tempRef_lan1.argvalue;
					if (str1 == null)
					{
						res.append("*UNDEFINED*");
						errmessage.argvalue = "$" + varname + " has no value";
						Dic.writeLog(errmessage.argvalue);
					}
					else
					{
						res.append(str1[0]);
					}
				}
				else
				{
					// right variable
					pos2 = vpos;
					RefObject<Boolean> tempRef_compound2 = new RefObject<Boolean>(compound2);
					RefObject<Language> tempRef_lan12 = new RefObject<Language>(lan1);
					str2 = getValues(current, varname, tempRef_compound2, position, rightmargin, variable, cpos, pos2,
							tempRef_lan12, textmft, annotations, tunb);
					compound2 = tempRef_compound2.argvalue;
					lan1 = tempRef_lan12.argvalue;
					if (str2 == null)
					{
						res.append("*UNDEFINED*");
						errmessage.argvalue = "$" + varname + " has no value";
						Dic.writeLog(errmessage.argvalue);
					}
					else
					{
						res.append(str2[0]);
					}
				}
			}
		}
		String constraint = res.toString();

		// process lexical constraint
		String left = null, lemma = null, category = null;
		String[] features = null;
		String op = null;
		boolean negation = false;

		RefObject<String> tempRef_left = new RefObject<String>(left);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		RefObject<String> tempRef_op = new RefObject<String>(op);
		RefObject<Boolean> tempRef_negation = new RefObject<Boolean>(negation);

		boolean tempVar = !Dic.parseLexicalConstraint(constraint, tempRef_left, tempRef_lemma, tempRef_category,
				tempRef_features, tempRef_op, tempRef_negation);

		left = tempRef_left.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		op = tempRef_op.argvalue;
		negation = tempRef_negation.argvalue;
		if (tempVar)
		{
			return false;
		}
		if (op.equals("=:"))
		{
			// a syntactically lexical constraint, e.g. <$Det=:DET+s> or <!$Det=:DET+s> or <$V=:have,V+PR+3+s> or
			// <$V=:!have,V+PR+3+s>
			if (pos1 == -1)
			{
				errmessage.argvalue = "no variable";
				Dic.writeLog(errmessage.argvalue);
				return false; // no variable to work with ???
			}
			sols.argvalue = new ArrayList<String>();
			if (lan1 == null)
			{
				// the constraint is on the current language
				if (pos2 == -1)
				{
					if (!compound1)
					{
						// one simple variable, e.g. <$Det=:DET+s>
						String lex = getLexFromVariable(variable, pos1);
						if (lex == null || lex.indexOf(",WF") != -1)
						{
							for (int istr = 0; istr < str1.length; istr++)
							{
								ArrayList<String> tsols = this.lookupAndAnalyzeSimpleOrCompound(str1[istr], errmessage);
								if (errmessage.argvalue != null)
								{
									return false;
								}
								if (tsols != null && tsols.size() > 0)
								{
									sols.argvalue.addAll(tsols);
								}
							}
						}
						else
						{
							sols.argvalue.add(lex);
						}
					}
					else
					{
						// one compound variable, e.g. <$NP$Head=:N+Hum>
						for (int istr = 0; istr < str1.length; istr++)
						{
							ArrayList<String> tsols = this.lookupAndAnalyzeSimpleOrCompound(str1[istr], errmessage);
							if (errmessage.argvalue != null)
							{
								return false;
							}
							if (tsols != null && tsols.size() > 0)
							{
								sols.argvalue.addAll(tsols);
							}
						}
					}
				}
				else
				{
					// two variables, e.g. <$Det#$Det=:N> or <$V=:$N$VSup>
					StringBuilder sb = new StringBuilder();
					for (int ileft = 0; ileft < left.length(); ileft++)
					{
						if (left.charAt(ileft) != '#')
						{
							sb.append(left.charAt(ileft));
						}
					}
					String word = sb.toString();
					ArrayList<String> tsols = this.lookupAndAnalyzeSimpleOrCompound(word, errmessage);
					if (errmessage.argvalue != null)
					{
						return false;
					}
					if (tsols != null && tsols.size() > 0)
					{
						sols.argvalue.addAll(tsols);
					}
				}
			}
			else
			{
				// the constraint is on the *target* language, e.g. $Noun$FR=:N+f+s
				if (engine2 == null || !engine2.Lan.isoName.equals(lan1.isoName))
				{
					RefObject<Language> tempRef_lan13 = new RefObject<Language>(lan1);
					engine2 = new Engine(tempRef_lan13, this.applicationDir, this.docDir, this.projectDir,
							this.projectMode, preferences, this.BackgroundWorking, this.backgroundWorker);
					lan1 = tempRef_lan13.argvalue;
					if (engine2.loadNodResources(this.preferences.ldic.get(lan1.isoName), errmessage) == -1)
					{
						errmessage.argvalue = "no linguistic resource for " + lan1.isoName;
						Dic.writeLog(errmessage.argvalue);
						return false;
					}
				}
				sols.argvalue = new ArrayList<String>();
				for (int istr = 0; istr < str1.length; istr++)
				{
					ArrayList<String> tsols = engine2.lookupAndAnalyzeSimpleOrCompound(str1[istr], errmessage);
					if (errmessage.argvalue != null)
					{
						errmessage.argvalue = "no lexical entry for " + str1[istr];
						Dic.writeLog(errmessage.argvalue);
						return false;
					}
					if (tsols != null && tsols.size() > 0)
					{
						sols.argvalue.addAll(tsols);
					}
				}

				// remove duplicates
				for (int isol = 0; isol < sols.argvalue.size(); isol++)
				{
					String si = sols.argvalue.get(isol);
					for (int jsol = isol + 1; jsol < sols.argvalue.size();)
					{
						String sj = sols.argvalue.get(jsol);
						if (si.equals(sj))
						{
							sols.argvalue.remove(jsol);
						}
						else
						{
							jsol++;
						}
					}
				}
			}

			// check if at least one annotation matches the constraint
			if (sols.argvalue == null || sols.argvalue.isEmpty())
			{
				return negation;
			}
			grammar.filterLexemes(sols, lemma, category, features, negation);
			if (sols.argvalue == null || sols.argvalue.isEmpty())
			{
				errmessage.argvalue = constraint + " failed";
		
				return negation;
			}
			return !negation;
		}
		else if (op.equals("=")) // two strings to be compared, e.g. <table=table> or <f=f>
		{
			if (str1 == null)
			{
				// take the right side of the constraint <xx!=yy>
				str1 = new String[1];
				str1[0] = "*UNDEFINED*";
			}
			if (str2 == null)
			{
				// take the right side of the constraint <xx=yy>
				str2 = new String[1];
				str2[0] = lemma;
			}
			boolean thereisamatch = false;
			ENDEQ: for (int uu = 0; uu < 1; uu++)
			{
				if (str1 != null)
				{
					for (String s1 : str1)
					{
						if (str2 != null)
						{
							for (String s2 : str2)
							{
								if (s2 != null && s2.length() >= 2 && s2.charAt(0) == '"'
										&& s2.charAt(s2.length() - 1) == '"')
								{
									String s22 = s2.substring(1, 1 + s2.length() - 2);
									if (Lan.doWordFormsMatch(s1, s22))
									{
										thereisamatch = true;
										break ENDEQ;
									}
								}
								else if (Lan.doWordFormsMatch(s1, s2))
								{
									thereisamatch = true;
									break ENDEQ;
								}
							}
						}
					}
				}
			}
			if (thereisamatch)
			{
				return !negation;
			}
			else
			{
				errmessage.argvalue = constraint + " failed";
				
				return negation;
			}
		}
		else if (op.equals("!=")) // two tokens to be compared, e.g. <table!=table> or <f!=f>
		{
			if (str1 == null)
			{
				// take the right side of the constraint <xx!=yy>
				str1 = new String[1];
				str1[0] = "*UNDEFINED*";
			}
			if (str2 == null)
			{
				// take the right side of the constraint <xx!=yy>
				str2 = new String[1];
				str2[0] = lemma;
			}
			boolean thereisamatch = false;
			ENDDIF: for (String s1 : str1)
			{
				for (String s2 : str2)
				{
					if (!Lan.doWordFormsMatch(s1, s2))
					{
						thereisamatch = true;
						break ENDDIF;
					}
				}
			}

			if (thereisamatch)
			{
				return !negation;
			}
			else
			{
				errmessage.argvalue = constraint + " failed";
	
				return negation;
			}
		}
		else
		{
			errmessage.argvalue = "operator '" + op + "' unknown in constraint '" + constraint + "'";
			Dic.writeLog(errmessage.argvalue);
			return false;
		}
	}

	/**
	 * 
	 * @param currentline
	 * @param cpos
	 * @param ipos
	 * @param textmft
	 * @param rightmargin
	 * @param annotations
	 * @param tunb
	 * @param positiontrace
	 * @param variabletrace
	 * @param soutput
	 * @param grammar
	 * @param resoutputs
	 * @param resvariables
	 * @param recindex
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private final boolean newProcessVariablesInString(String currentline, double cpos, int ipos, Mft textmft,
			int rightmargin, ArrayList<Object> annotations, int tunb, ArrayList<Double> positiontrace,
			ArrayList<String> variabletrace, String soutput, Grammar grammar, RefObject<ArrayList<String>> resoutputs,
			RefObject<ArrayList<HashMap<String, String>>> resvariables, int recindex) throws IOException,
			ClassNotFoundException
	{
		ParameterCheck.mandatoryString("soutput", soutput);
		ParameterCheck.mandatory("resoutputs", resoutputs);
		boolean res;

		if (recindex >= soutput.length())
		{
			resoutputs.argvalue = new ArrayList<String>();
			resoutputs.argvalue.add("");
			return true;
		}

		int iso; // position in current output string -> next recursive index
		ArrayList<String> tailoutputs = null;
		if (soutput.charAt(recindex) != '$')
		{
			StringBuilder sb = new StringBuilder();
			for (iso = recindex; iso < soutput.length() && soutput.charAt(iso) != '$'; iso++)
			{
				if (soutput.charAt(iso) != '#') // skip '#'
				{
					sb.append(soutput.charAt(iso));
				}
			}
			String curoutput = sb.toString();
			RefObject<ArrayList<String>> tempRef_tailoutputs = new RefObject<ArrayList<String>>(tailoutputs);

			res = newProcessVariablesInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
					positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs, resvariables, iso);

			tailoutputs = tempRef_tailoutputs.argvalue;
			if (res == false)
			{
				resoutputs.argvalue = null;
				return false;
			}
			if (tailoutputs == null)
			{
				resoutputs.argvalue = null;
				return true;
			}
			resoutputs.argvalue = new ArrayList<String>();
			for (int i2 = 0; i2 < tailoutputs.size(); i2++)
			{
				String aresoutput = curoutput + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
			return true;
		}

		// get variable name
		int j = 0;
		RefObject<Integer> tempRef_j = new RefObject<Integer>(j);
		String varname = Dic.getFullVariableName(soutput, recindex, tempRef_j);
		j = tempRef_j.argvalue;
		iso = j;

		if (varname.charAt(0) >= '0' && varname.charAt(0) <= '9')
		{
			// these are special variables, e.g. $1L which will actually be set when computing constraints: DO NOT TRY
			// TO COMPUTE NOW
			String curoutput = '$' + varname;
			RefObject<ArrayList<String>> tempRef_tailoutputs2 = new RefObject<ArrayList<String>>(tailoutputs);
			res = newProcessVariablesInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
					positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs2, resvariables, iso);
			tailoutputs = tempRef_tailoutputs2.argvalue;
			if (res == false)
			{
				resoutputs.argvalue = null;
				return false;
			}
			if (tailoutputs == null)
			{
				resoutputs.argvalue = null;
				return true;
			}
			resoutputs.argvalue = new ArrayList<String>();
			for (int i2 = 0; i2 < tailoutputs.size(); i2++)
			{
				String aresoutput = curoutput + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
			return true;
		}

		ArrayList<String> curoutputs = new ArrayList<String>();
		ArrayList<HashMap<String, String>> newresvariables = new ArrayList<HashMap<String, String>>();
		for (int ivars = 0; ivars < resvariables.argvalue.size(); ivars++)
		{
			// compute each variable's set of values
			HashMap<String, String> hvariables = resvariables.argvalue.get(ivars);
			if (hvariables.containsKey(varname))
			{
				// the variable's value has already been computed in the trace
				newresvariables.add(hvariables);
				curoutputs.add(hvariables.get(varname));
				continue;
			}
			else if (variabletrace != null)
			{
				// variabletrace might be null if called from FormTransformation
				// need to compute and add varname to hvariables
				Language lan1 = null;
				boolean compoundvariable = false;
				ArrayList<HashMap<String, String>> curvars = null;
				String errmessage = null;
				RefObject<Boolean> tempRef_compoundvariable = new RefObject<Boolean>(compoundvariable);
				RefObject<Language> tempRef_lan1 = new RefObject<Language>(lan1);
				RefObject<ArrayList<HashMap<String, String>>> tempRef_curvars = new RefObject<ArrayList<HashMap<String, String>>>(
						curvars);
				RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
				String[] tmpoutputs = newGetValues(currentline, varname, tempRef_compoundvariable, positiontrace,
						rightmargin, variabletrace, cpos, ipos, tempRef_lan1, textmft, annotations, tunb,
						tempRef_curvars, tempRef_errmessage);
				compoundvariable = tempRef_compoundvariable.argvalue;
				lan1 = tempRef_lan1.argvalue;
				curvars = tempRef_curvars.argvalue;
				errmessage = tempRef_errmessage.argvalue;
				if (tmpoutputs == null || curvars == null)
				{
					// cannot compute variable's possible values in this set of values
					if (errmessage == null)
					{
						continue;
					}
					System.out.println(errmessage);
					continue;
				}
				for (int jvars = 0; jvars < curvars.size(); jvars++)
				{
					// add curoutput to resoutputs
					String tmpoutput = tmpoutputs[jvars];
					curoutputs.add(tmpoutput);

					// merge curvars with resvariables
					// Using copy-constructor instead of clone() - recommended because of unchecked class cast
					HashMap<String, String> newhvariables = new HashMap<String, String>(hvariables);
					HashMap<String, String> curvar = curvars.get(jvars);
					newhvariables.put(varname, curvar.get(varname));
					if (curvar.containsKey("LU_" + varname))
					{
						newhvariables.put("LU_" + varname, curvar.get("LU_" + varname));
					}
					newresvariables.add(newhvariables);
				}
			}
		}
		if (newresvariables.isEmpty())
		{
			resoutputs.argvalue = null;
			return false;
		}
		resvariables.argvalue = newresvariables;
		// compute tail
		RefObject<ArrayList<String>> tempRef_tailoutputs3 = new RefObject<ArrayList<String>>(tailoutputs);
		res = newProcessVariablesInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
				positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs3, resvariables, iso);
		tailoutputs = tempRef_tailoutputs3.argvalue;
		if (res == false)
		{
			resoutputs.argvalue = null;
			return false;
		}
		// combine curoutputs with tailoutputs with resvariables
		if (tailoutputs == null)
		{
			resoutputs.argvalue = null;
			return true;
		}
		resoutputs.argvalue = new ArrayList<String>();
		for (int icur = 0; icur < curoutputs.size(); icur++)
		{
			String curoutput = curoutputs.get(icur);
			for (int itail = 0; itail < tailoutputs.size(); itail++)
			{
				String sres = curoutput + tailoutputs.get(itail);
				resoutputs.argvalue.add(sres);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param left
	 * @return
	 */
	private boolean pureVariable(String left)
	{
		ParameterCheck.mandatoryString("left", left);
		if (left.length() == 0)
		{
			return false;
		}
		if (left.charAt(0) != '$')
		{
			return false;
		}
		int j;
		for (j = 1; j < left.length() && !Character.isWhitespace(left.charAt(j)) && left.charAt(j) != '#'
				&& left.charAt(j) != '=' && left.charAt(j) != ',' && left.charAt(j) != '>'; j++)
		{
			;
		}
		if (j < left.length())
		{
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param originalleft
	 * @param leftstrings
	 * @param rightstrings
	 * @param nop
	 * @param negation
	 * @param grammar
	 * @param curoutputs
	 * @param resvariables
	 * @param errmessage
	 * @return
	 */
	private boolean newSolveConstraint(String originalleft, ArrayList<String> leftstrings,
			ArrayList<String> rightstrings, String nop, boolean negation, Grammar grammar,
			RefObject<ArrayList<String>> curoutputs, RefObject<ArrayList<HashMap<String, String>>> resvariables,
			RefObject<String> errmessage)
	{
		ParameterCheck.mandatory("leftstrings", leftstrings);
		ParameterCheck.mandatory("rightstrings", rightstrings);
		ParameterCheck.mandatory("nop", nop);
		ParameterCheck.mandatory("grammar", grammar);
		ParameterCheck.mandatory("resvariables", resvariables);
		ParameterCheck.mandatory("errmessage", errmessage);

		errmessage.argvalue = null;

		if (nop.equals("="))
		{
			boolean match = false;

			for (String left : leftstrings)
			{
				for (String right : rightstrings)
				{
					if (Dic.isALexemeSymbol(left) && !Dic.isALexemeSymbol(right))
					{
						String entry = null, lemma = null, info = null;
						RefObject<String> tempRef_entry = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
						RefObject<String> tempRef_info = new RefObject<String>(info);

						Dic.parseLexemeSymbol(left, tempRef_entry, tempRef_lemma, tempRef_info);

						entry = tempRef_entry.argvalue;
						lemma = tempRef_lemma.argvalue;
						info = tempRef_info.argvalue;
						if (this.Lan.doWordFormsMatch(left, entry))
						{
							match = true;

							break;
						}
					}
					else if (!Dic.isALexemeSymbol(left) && Dic.isALexemeSymbol(right))
					{
						String entry = null, lemma = null, info = null;
						RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
						RefObject<String> tempRef_info2 = new RefObject<String>(info);

						Dic.parseLexemeSymbol(right, tempRef_entry2, tempRef_lemma2, tempRef_info2);

						entry = tempRef_entry2.argvalue;
						lemma = tempRef_lemma2.argvalue;
						info = tempRef_info2.argvalue;
						if (this.Lan.doWordFormsMatch(left, entry))
						{
							match = true;

							break;
						}
					}
					else
					{
						if (this.Lan.doWordFormsMatch(left, right))
						{
							match = true;

							break;
						}
					}
				}
			}
			if (match)
			{
				curoutputs.argvalue = new ArrayList<String>();
				curoutputs.argvalue.add("");
				return !negation;
			}
			curoutputs.argvalue = null;
			return negation;
		}
		else if (nop.equals("!="))
		{
			boolean match = false;

			for (String left : leftstrings)
			{
				for (String right : rightstrings)
				{
					if (Dic.isALexemeSymbol(left) && !Dic.isALexemeSymbol(right))
					{
						String entry = null, lemma = null, info = null;
						RefObject<String> tempRef_entry3 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma3 = new RefObject<String>(lemma);
						RefObject<String> tempRef_info3 = new RefObject<String>(info);
						Dic.parseLexemeSymbol(left, tempRef_entry3, tempRef_lemma3, tempRef_info3);
						entry = tempRef_entry3.argvalue;
						lemma = tempRef_lemma3.argvalue;
						info = tempRef_info3.argvalue;
						if (this.Lan.doWordFormsMatch(left, entry))
						{
							match = true;

							break;
						}
					}
					else if (!Dic.isALexemeSymbol(left) && Dic.isALexemeSymbol(right))
					{
						String entry = null, lemma = null, info = null;
						RefObject<String> tempRef_entry4 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma4 = new RefObject<String>(lemma);
						RefObject<String> tempRef_info4 = new RefObject<String>(info);
						Dic.parseLexemeSymbol(right, tempRef_entry4, tempRef_lemma4, tempRef_info4);
						entry = tempRef_entry4.argvalue;
						lemma = tempRef_lemma4.argvalue;
						info = tempRef_info4.argvalue;
						if (this.Lan.doWordFormsMatch(left, entry))
						{
							match = true;

							break;
						}
					}
					else
					{
						if (this.Lan.doWordFormsMatch(left, right))
						{
							match = true;

							break;
						}
					}
				}
			}
			if (!match)
			{
				curoutputs.argvalue = new ArrayList<String>();
				curoutputs.argvalue.add("");
				return !negation;
			}
			curoutputs.argvalue = null;
			return negation;
		}
		else if (nop.equals("=:"))
		{
			if (pureVariable(originalleft))
			{
				// compute list of lexemes in the left
				ArrayList<String> sols = new ArrayList<String>();
				for (int ilex = 0; ilex < resvariables.argvalue.size(); ilex++)
				{
					HashMap<String, String> vals = resvariables.argvalue.get(ilex);
					if (originalleft.length() > 1 && originalleft.charAt(0) == '$')
					{
						if (vals.containsKey("LU_" + originalleft.substring(1)))
						{
							// left looks like a single variable, e.g. <$var=:xxx> or <$var$fr=:xxx> => then get the
							// lexeme
							String lexeme = vals.get("LU_" + originalleft.substring(1));
							sols.add(lexeme);
						}
					}
				}
				if (sols.isEmpty())
				{
					curoutputs.argvalue = null;
					return negation;
				}
				boolean amatch = false;
				for (String right : rightstrings)
				{
					// Using copy-constructor instead of clone() - recommended because of unchecked class cast
					ArrayList<String> tsols = new ArrayList<String>(sols);
					String category = null, lemma = null;
					String[] features = null;
					boolean negation2 = false;

					RefObject<String> tempRef_lemma5 = new RefObject<String>(lemma);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					RefObject<Boolean> tempRef_negation2 = new RefObject<Boolean>(negation2);

					Dic.parseLexicalConstraintRightSide(right, tempRef_lemma5, tempRef_category, tempRef_features,
							tempRef_negation2);

					lemma = tempRef_lemma5.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					negation2 = tempRef_negation2.argvalue;
					RefObject<ArrayList<String>> tempRef_tsols = new RefObject<ArrayList<String>>(tsols);

					grammar.filterLexemes(tempRef_tsols, lemma, category, features, (negation && !negation2)
							|| (!negation && negation2));

					tsols = tempRef_tsols.argvalue;
					if (tsols != null && tsols.size() > 0)
					{
						sols = tsols;
						amatch = true;
						break;
					}
				}
				if (!amatch)
				{
					curoutputs.argvalue = null;
					return negation;
				}
				// there are matches
				curoutputs.argvalue = new ArrayList<String>();
				for (String lex : sols)
				{
					curoutputs.argvalue.add("<LU=" + lex + ">");
				}
				return !negation;
			}
			else
			{
				// e.e. <$M#$N#$P=:N> we process the constraint with the string values of left
				curoutputs.argvalue = new ArrayList<String>();
				ArrayList<String> sols = new ArrayList<String>();
				for (String left : leftstrings)
				{
					ArrayList<String> tsols = this.lookupAndAnalyzeSimpleOrCompound(left, errmessage);
					if (errmessage.argvalue != null)
					{
						return false;
					}
					if (tsols == null || tsols.isEmpty())
					{
						continue;
					}

					boolean amatch = false;
					for (String right : rightstrings)
					{
						// Using copy-constructor instead of clone() - recommended because of unchecked class cast
						ArrayList<String> tsols2 = new ArrayList<String>(tsols);
						String category = null, lemma = null;
						String[] features = null;
						boolean negation2 = false;
						RefObject<String> tempRef_lemma6 = new RefObject<String>(lemma);
						RefObject<String> tempRef_category2 = new RefObject<String>(category);
						RefObject<String[]> tempRef_features2 = new RefObject<String[]>(features);
						RefObject<Boolean> tempRef_negation22 = new RefObject<Boolean>(negation2);

						Dic.parseLexicalConstraintRightSide(right, tempRef_lemma6, tempRef_category2,
								tempRef_features2, tempRef_negation22);

						lemma = tempRef_lemma6.argvalue;
						category = tempRef_category2.argvalue;
						features = tempRef_features2.argvalue;
						negation2 = tempRef_negation22.argvalue;
						RefObject<ArrayList<String>> tempRef_tsols2 = new RefObject<ArrayList<String>>(tsols2);

						grammar.filterLexemes(tempRef_tsols2, lemma, category, features, (negation && !negation2)
								|| (!negation && negation2));

						tsols2 = tempRef_tsols2.argvalue;
						if (tsols2 != null && tsols2.size() > 0)
						{
							tsols = tsols2;
							amatch = true;
							break;
						}
					}
					if (!amatch)
					{
						continue;
					}
					sols.addAll(tsols);
				}
				for (String lex : sols)
				{
					curoutputs.argvalue.add("<LU=" + lex + ">");
				}
				return !negation;
			}
		}
		else
		{
			// what operator?
			curoutputs.argvalue = null;
			return false;
		}
	}

	/**
	 * 
	 * @param originalleft
	 * @param leftstrings
	 * @param rightstrings
	 * @param nop
	 * @param negation
	 * @param grammar
	 * @param resvariables
	 * @param errmessage
	 * @return
	 */
	private boolean newSolveConstraintForTransformed(String originalleft, ArrayList<String> leftstrings,
			ArrayList<String> rightstrings, String nop, boolean negation, Grammar grammar,
			RefObject<ArrayList<HashMap<String, String>>> resvariables, RefObject<String> errmessage)
	{
		// identical to New_SolveConstraint, but variables are stored without prefix LU_, see ***, and method does not
		// produce outputs (just a check)

		ParameterCheck.mandatory("leftstrings", leftstrings);
		ParameterCheck.mandatory("rightstrings", rightstrings);
		ParameterCheck.mandatory("nop", nop);
		ParameterCheck.mandatory("grammar", grammar);
		ParameterCheck.mandatory("resvariables", resvariables);
		ParameterCheck.mandatory("errmessage", errmessage);
		errmessage.argvalue = null;
		if (nop.equals("="))
		{
			boolean match = false;
			@SuppressWarnings("unused")
			String res = null;
			for (String left : leftstrings)
			{
				for (String right : rightstrings)
				{
					if (left.equals(right))
					{
						match = true;
						res = left;
						break;
					}
				}
			}
			if (match)
			{
				return !negation;
			}
			return negation;
		}
		else if (nop.equals("!="))
		{
			boolean match = false;
			@SuppressWarnings("unused")
			String res = null;
			for (String left : leftstrings)
			{
				for (String right : rightstrings)
				{
					if (left.equals(right))
					{
						match = true;
						break;
					}
					res = left;
				}
			}
			if (!match)
			{
				return !negation;
			}
			return negation;
		}
		else if (nop.equals("=:"))
		{
			if (pureVariable(originalleft))
			{
				// compute list of lexemes in the left
				ArrayList<String> sols = new ArrayList<String>();
				for (int ilex = 0; ilex < resvariables.argvalue.size(); ilex++)
				{
					HashMap<String, String> vals = resvariables.argvalue.get(ilex);
					if (originalleft.length() > 1 && originalleft.charAt(0) == '$')
					{
						// *** no LU_
						if (vals.containsKey(originalleft.substring(1)))
						{
							// left looks like a single variable, e.g. <$var=:xxx> or <$var$fr=:xxx> => then get the
							// lexeme
							String lexeme = vals.get(originalleft.substring(1));
							sols.add(lexeme.substring(1, 1 + lexeme.length() - 2)); // get rid of < >
						}
					}
				}
				if (sols.isEmpty())
				{
					return negation;
				}
				boolean amatch = false;
				for (String right : rightstrings)
				{
					ArrayList<String> tsols = (ArrayList<String>) sols.clone();
					String category = null, lemma = null;
					String[] features = null;
					boolean negation2 = false;
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
					RefObject<Boolean> tempRef_negation2 = new RefObject<Boolean>(negation2);
					Dic.parseLexicalConstraintRightSide(right, tempRef_lemma, tempRef_category, tempRef_features,
							tempRef_negation2);
					lemma = tempRef_lemma.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					negation2 = tempRef_negation2.argvalue;
					RefObject<ArrayList<String>> tempRef_tsols = new RefObject<ArrayList<String>>(tsols);
					grammar.filterLexemes(tempRef_tsols, lemma, category, features, (negation && !negation2)
							|| (!negation && negation2));
					tsols = tempRef_tsols.argvalue;
					if (tsols != null && tsols.size() > 0)
					{
						sols = tsols;
						amatch = true;
						break;
					}
				}
				if (!amatch)
				{
					return negation;
				}
				// there are matches
				return !negation;
			}
			else
			{
				// e.e. <$M#$N#$P=:N> we process the constraint with the string values of left
				ArrayList<String> sols = new ArrayList<String>();
				for (String left : leftstrings)
				{
					ArrayList<String> tsols = this.lookupAndAnalyzeSimpleOrCompound(left, errmessage);
					if (errmessage.argvalue != null)
					{
						return false;
					}
					if (tsols == null || tsols.isEmpty())
					{
						continue;
					}

					boolean amatch = false;
					for (String right : rightstrings)
					{
						ArrayList<String> tsols2 = (ArrayList<String>) tsols.clone();
						String category = null, lemma = null;
						String[] features = null;
						boolean negation2 = false;
						RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
						RefObject<String> tempRef_category2 = new RefObject<String>(category);
						RefObject<String[]> tempRef_features2 = new RefObject<String[]>(features);
						RefObject<Boolean> tempRef_negation22 = new RefObject<Boolean>(negation2);
						Dic.parseLexicalConstraintRightSide(right, tempRef_lemma2, tempRef_category2,
								tempRef_features2, tempRef_negation22);
						lemma = tempRef_lemma2.argvalue;
						category = tempRef_category2.argvalue;
						features = tempRef_features2.argvalue;
						negation2 = tempRef_negation22.argvalue;
						RefObject<ArrayList<String>> tempRef_tsols2 = new RefObject<ArrayList<String>>(tsols2);
						grammar.filterLexemes(tempRef_tsols2, lemma, category, features, (negation && !negation2)
								|| (!negation && negation2));
						tsols2 = tempRef_tsols2.argvalue;
						if (tsols2 != null && tsols2.size() > 0)
						{
							tsols = tsols2;
							amatch = true;
							break;
						}
					}
					if (!amatch)
					{
						continue;
					}
					sols.addAll(tsols);
				}
				if (sols.isEmpty())
				{
					return negation;
				}
				else
				{
					return !negation;
				}
			}
		}
		else
		{
			// what operator?
			return false;
		}
	}

	/**
	 * 
	 * @param currentline
	 * @param cpos
	 * @param ipos
	 * @param textmft
	 * @param rightmargin
	 * @param annotations
	 * @param tunb
	 * @param positiontrace
	 * @param variabletrace
	 * @param soutput
	 * @param grammar
	 * @param resoutputs
	 * @param resvariables
	 * @param recindex
	 * @param iso
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private boolean newProcessSingleVariableInString(String currentline, double cpos, int ipos, Mft textmft,
			int rightmargin, ArrayList<Object> annotations, int tunb, ArrayList<Double> positiontrace,
			ArrayList<String> variabletrace, String soutput, Grammar grammar, RefObject<ArrayList<String>> resoutputs,
			RefObject<ArrayList<HashMap<String, String>>> resvariables, int recindex, RefObject<Integer> iso)
			throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatory("resoutputs", resoutputs);
		ParameterCheck.mandatory("resvariables", resvariables);
		ParameterCheck.mandatory("iso", iso);
		resoutputs.argvalue = new ArrayList<String>();

		// get variable name
		int j = 0;
		RefObject<Integer> tempRef_j = new RefObject<Integer>(j);
		String varname = Dic.getFullVariableName(soutput, recindex, tempRef_j);
		j = tempRef_j.argvalue;
		iso.argvalue = j;

		if (varname.charAt(0) >= '0' && varname.charAt(0) <= '9')
		{
			// these are special variables, e.g. $1L which will actually be set when computing constraints: DO NOT TRY
			// TO COMPUTE NOW
			String curoutput = '$' + varname;
			resoutputs.argvalue.add(curoutput);
			return true; // I do not process (but there is no error)
		}

		ArrayList<HashMap<String, String>> newresvariables = new ArrayList<HashMap<String, String>>();
		for (int ivars = 0; ivars < resvariables.argvalue.size(); ivars++)
		{
			// compute each variable's set of values
			HashMap<String, String> hvariables = resvariables.argvalue.get(ivars);
			if (hvariables.containsKey(varname))
			{
				// the variable's value has already been computed in the trace
				newresvariables.add(hvariables);
				resoutputs.argvalue.add(hvariables.get(varname));
				continue;
			}
			else
			{
				// need to compute and add varname to hvariables
				Language lan1 = null;
				boolean compoundvariable = false;
				ArrayList<HashMap<String, String>> curvars = null;
				String errmessage = null;
				RefObject<Boolean> tempRef_compoundvariable = new RefObject<Boolean>(compoundvariable);
				RefObject<Language> tempRef_lan1 = new RefObject<Language>(lan1);
				RefObject<ArrayList<HashMap<String, String>>> tempRef_curvars = new RefObject<ArrayList<HashMap<String, String>>>(
						curvars);
				RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);

				String[] tmpoutputs = newGetValues(currentline, varname, tempRef_compoundvariable, positiontrace,
						rightmargin, variabletrace, cpos, ipos, tempRef_lan1, textmft, annotations, tunb,
						tempRef_curvars, tempRef_errmessage);

				compoundvariable = tempRef_compoundvariable.argvalue;
				lan1 = tempRef_lan1.argvalue;
				curvars = tempRef_curvars.argvalue;
				errmessage = tempRef_errmessage.argvalue;
				if (tmpoutputs == null)
				{
					continue;
				}
				for (int jvars = 0; jvars < curvars.size(); jvars++)
				{
					// add curoutput to resoutputs
					String tmpoutput = tmpoutputs[jvars];
					resoutputs.argvalue.add(tmpoutput);

					// merge curvars with resvariables
					// Using copy-constructor instead of clone() - recommended because of unchecked class cast
					HashMap<String, String> newhvariables = new HashMap<String, String>(hvariables);
					HashMap<String, String> curvar = curvars.get(jvars);
					if (varname.length() < 4 || (varname.length() == 4 && !varname.equals("THIS"))
							|| (varname.length() > 4 && !varname.substring(0, 4).equals("THIS")))
					{
						newhvariables.put(varname, curvar.get(varname));
						if (curvar.containsKey("LU_" + varname))
						{
							newhvariables.put("LU_" + varname, curvar.get("LU_" + varname));
						}
					}
					newresvariables.add(newhvariables);
				}
			}
		}
		resvariables.argvalue = newresvariables;
		return resvariables.argvalue.size() > 0;
	}

	/**
	 * 
	 * @param soutput
	 * @param hvariables
	 * @param recindex
	 * @param nextindex
	 * @param resoutputs
	 * @param errmessage
	 * @return
	 */
	private boolean newProcessSingleVariableInString(String soutput, HashMap<String, String> hvariables, int recindex,
			RefObject<Integer> nextindex, RefObject<ArrayList<String>> resoutputs, RefObject<String> errmessage)
	{
		ParameterCheck.mandatory("hvariables", hvariables);
		ParameterCheck.mandatory("nextindex", nextindex);
		ParameterCheck.mandatory("resoutputs", resoutputs);
		ParameterCheck.mandatory("errmessage", errmessage);
		errmessage.argvalue = null;
		resoutputs.argvalue = new ArrayList<String>();

		// get variable name
		int j = 0;
		RefObject<Integer> tempRef_j = new RefObject<Integer>(j);
		String varname = Dic.getFullVariableName(soutput, recindex, tempRef_j);
		j = tempRef_j.argvalue;
		nextindex.argvalue = j;

		@SuppressWarnings("unused")
		ArrayList<String> curoutputs = new ArrayList<String>();
		if (varname.charAt(0) >= '0' && varname.charAt(0) <= '9')
		{
			// these are special variables, e.g. $1L which will actually be set when computing constraints: DO NOT TRY
			// TO COMPUTE NOW
			String curoutput = '$' + varname;
			resoutputs.argvalue.add(curoutput);
			return true; // I do not process (but there is no error)
		}

		if (hvariables.containsKey(varname))
		{
			// the variable's value has already been computed in the trace
			resoutputs.argvalue.add(hvariables.get(varname));
			return true;
		}
		else
		{
			errmessage.argvalue = varname + " undefined";
			return false;
		}
	}

	/**
	 * 
	 * @param soutput
	 * @param hvariables
	 * @param recindex
	 * @param resoutputs
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final boolean newProcessVariablesInString(String soutput, HashMap<String, String> hvariables, int recindex,
			RefObject<ArrayList<String>> resoutputs) throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatoryString("soutput", soutput);
		ParameterCheck.mandatory("resoutputs", resoutputs);
		boolean res;
		if (recindex >= soutput.length())
		{
			resoutputs.argvalue = new ArrayList<String>();
			resoutputs.argvalue.add("");
			return true;
		}

		int iso; // position in current output string -> next recursive index
		ArrayList<String> tailoutputs = null;
		if (soutput.charAt(recindex) != '$')
		{
			StringBuilder sb = new StringBuilder();
			for (iso = recindex; iso < soutput.length() && soutput.charAt(iso) != '$'; iso++)
			{
				if (soutput.charAt(iso) != '#') // skip '#'
				{
					sb.append(soutput.charAt(iso));
				}
			}
			String curoutput = sb.toString();
			RefObject<ArrayList<String>> tempRef_tailoutputs = new RefObject<ArrayList<String>>(tailoutputs);
			res = newProcessVariablesInString(soutput, hvariables, iso, tempRef_tailoutputs);
			tailoutputs = tempRef_tailoutputs.argvalue;
			if (res == false)
			{
				resoutputs.argvalue = null;
				return false;
			}
			if (tailoutputs == null)
			{
				resoutputs.argvalue = null;
				return true;
			}
			resoutputs.argvalue = new ArrayList<String>();
			for (int i2 = 0; i2 < tailoutputs.size(); i2++)
			{
				String aresoutput = curoutput + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
			return true;
		}

		// get variable name
		int j = 0;
		RefObject<Integer> tempRef_j = new RefObject<Integer>(j);
		String varname = Dic.getFullVariableName(soutput, recindex, tempRef_j);
		j = tempRef_j.argvalue;
		iso = j;

		if (varname.charAt(0) >= '0' && varname.charAt(0) <= '9')
		{
			// these are special variables, e.g. $1L which will actually be set when computing constraints: DO NOT TRY
			// TO COMPUTE NOW
			String curoutput = '$' + varname;
			RefObject<ArrayList<String>> tempRef_tailoutputs2 = new RefObject<ArrayList<String>>(tailoutputs);
			res = newProcessVariablesInString(soutput, hvariables, iso, tempRef_tailoutputs2);
			tailoutputs = tempRef_tailoutputs2.argvalue;
			if (res == false)
			{
				resoutputs.argvalue = null;
				return false;
			}
			if (tailoutputs == null)
			{
				resoutputs.argvalue = null;
				return true;
			}
			resoutputs.argvalue = new ArrayList<String>();
			for (int i2 = 0; i2 < tailoutputs.size(); i2++)
			{
				String aresoutput = curoutput + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
			return true;
		}

		ArrayList<String> curoutputs = new ArrayList<String>();
		if (hvariables.containsKey(varname))
		{
			curoutputs.add(hvariables.get(varname));
		}
		else if (varname.indexOf('$') != -1)
		{
			// compound variable, e.g. HeadNP0$Nb

			// get base variable, e.g. Head
			String basename = varname.substring(0, varname.indexOf('$'));
			String lex = hvariables.get(basename);
			if (lex == null)
			{
				resoutputs.argvalue = null;
				return false;
			}
			ArrayList<String> lexs = null;
			RefObject<ArrayList<String>> tempRef_lexs = new RefObject<ArrayList<String>>(lexs);
			Dic.normalizeLexemeSymbol(lex, this, tempRef_lexs);
			lexs = tempRef_lexs.argvalue;
			if (lexs == null)
			{
				resoutputs.argvalue = null;
				return false;
			}
			Language lan1 = null;
			String errmessage = null;
			curoutputs = new ArrayList<String>();
			for (String lex0 : lexs)
			{
				RefObject<Language> tempRef_lan1 = new RefObject<Language>(lan1);
				RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
				curoutputs.addAll(computeDerivations(varname.substring(basename.length()), lex0, tempRef_lan1,
						tempRef_errmessage));
				lan1 = tempRef_lan1.argvalue;
				errmessage = tempRef_errmessage.argvalue;
			}
			if (curoutputs.isEmpty())
			{
				// lex0 exists, but there no derivation that matches the operator in varname
				resoutputs.argvalue = null;
				return false;
			}
		}
		else
		{
			resoutputs.argvalue = null;
			return false;
		}
		// compute tail
		RefObject<ArrayList<String>> tempRef_tailoutputs3 = new RefObject<ArrayList<String>>(tailoutputs);
		res = newProcessVariablesInString(soutput, hvariables, iso, tempRef_tailoutputs3);
		tailoutputs = tempRef_tailoutputs3.argvalue;
		if (res == false)
		{
			resoutputs.argvalue = null;
			return false;
		}
		// combine curoutputs with tailoutputs with resvariables
		if (tailoutputs == null)
		{
			resoutputs.argvalue = null;
			return true;
		}
		resoutputs.argvalue = new ArrayList<String>();
		for (int icur = 0; icur < curoutputs.size(); icur++)
		{
			String curoutput = curoutputs.get(icur);
			for (int itail = 0; itail < tailoutputs.size(); itail++)
			{
				String sres = curoutput + tailoutputs.get(itail);
				resoutputs.argvalue.add(sres);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param currentline
	 * @param cpos
	 * @param ipos
	 * @param textmft
	 * @param rightmargin
	 * @param annotations
	 * @param tunb
	 * @param positiontrace
	 * @param variabletrace
	 * @param soutput
	 * @param grammar
	 * @param resoutputs
	 * @param resvariables
	 * @param recindex
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private final boolean newProcessConstraintsInString(String currentline, double cpos, int ipos, Mft textmft,
			int rightmargin, ArrayList<Object> annotations, int tunb, ArrayList<Double> positiontrace,
			ArrayList<String> variabletrace, String soutput, Grammar grammar, RefObject<ArrayList<String>> resoutputs,
			RefObject<ArrayList<HashMap<String, String>>> resvariables, int recindex, RefObject<String> errmessage)
			throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatoryString("soutput", soutput);
		ParameterCheck.mandatory("resoutputs", resoutputs);
		ParameterCheck.mandatory("errmessage", errmessage);

		errmessage.argvalue = null;

		if (recindex >= soutput.length())
		{
			resoutputs.argvalue = new ArrayList<String>();
			resoutputs.argvalue.add("");
			return true;
		}
		boolean check;

		int iso = 0; // position in current output string -> next recursive index
		ArrayList<String> curoutputs = null;
		ArrayList<String> tailoutputs = null;
		COMBINEWITHTAIL: for (int uu = 0; uu < 1; uu++)
		{
			if (soutput.charAt(recindex) == '$')
			{
				// A VARIABLE
				RefObject<ArrayList<String>> tempRef_curoutputs = new RefObject<ArrayList<String>>(curoutputs);
				RefObject<Integer> tempRef_iso = new RefObject<Integer>(iso);
				check = newProcessSingleVariableInString(currentline, cpos, ipos, textmft, rightmargin, annotations,
						tunb, positiontrace, variabletrace, soutput, grammar, tempRef_curoutputs, resvariables,
						recindex, tempRef_iso);
				curoutputs = tempRef_curoutputs.argvalue;
				iso = tempRef_iso.argvalue;
				if (!check)
				{
					errmessage.argvalue = soutput.substring(recindex) + " undefined";
					resoutputs.argvalue = null;
					resvariables.argvalue = null;
					return false;
				}
				break COMBINEWITHTAIL;
			}
			else if (soutput.charAt(recindex) == '"')
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				sb.append('"');
				for (iso = recindex + 1; iso < soutput.length() && soutput.charAt(iso) != '"'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				sb.append('"');
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
						positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs, resvariables, iso + 1,
						errmessage);
				tailoutputs = tempRef_tailoutputs.argvalue;
				if (errmessage.argvalue != null || !check)
				{
					resoutputs.argvalue = null;
					resvariables.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}
			else if (soutput.charAt(recindex) != '<')
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				for (iso = recindex; iso < soutput.length() && soutput.charAt(iso) != '<' && soutput.charAt(iso) != '"'
						&& soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs2 = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
						positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs2, resvariables, iso,
						errmessage);
				tailoutputs = tempRef_tailoutputs2.argvalue;
				if (errmessage.argvalue != null || !check)
				{
					resoutputs.argvalue = null;
					resvariables.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			int j;
			for (j = 1; recindex + j < soutput.length() && soutput.charAt(recindex + j) != '>'; j++)
			{
				;
			}
			if (recindex + j >= soutput.length()) // an opening '<' not followed by a closing '>': it cannot be a
													// constraint
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				sb.append("<");
				for (iso = recindex + 1; iso < soutput.length() && soutput.charAt(iso) != '<'
						&& soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs3 = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
						positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs3, resvariables, iso,
						errmessage);
				tailoutputs = tempRef_tailoutputs3.argvalue;
				if (!check)
				{
					resoutputs.argvalue = null;
					resvariables.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			// found a < ... > in soutput
			String constraint = soutput.substring(recindex, recindex + j + 1);
			if (!Dic.isALexicalConstraint(constraint))
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				sb.append("<");
				for (iso = recindex + 1; iso < soutput.length() && soutput.charAt(iso) != '<'
						&& soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs4 = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
						positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs4, resvariables, iso,
						errmessage);
				tailoutputs = tempRef_tailoutputs4.argvalue;
				if (!check)
				{
					resoutputs.argvalue = null;
					resvariables.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			// now process the current lexical contraint
			String left = null, nop = null, right = null;
			RefObject<String> tempRef_left = new RefObject<String>(left);
			RefObject<String> tempRef_nop = new RefObject<String>(nop);
			RefObject<String> tempRef_right = new RefObject<String>(right);

			Dic.parseLexicalConstraint(constraint, tempRef_left, tempRef_nop, tempRef_right);

			left = tempRef_left.argvalue;
			nop = tempRef_nop.argvalue;
			right = tempRef_right.argvalue;
			boolean negation = false;
			ArrayList<String> leftstrings = null;
			ArrayList<String> rightstrings = null;
			RefObject<ArrayList<String>> tempRef_leftstrings = new RefObject<ArrayList<String>>(leftstrings);

			boolean leftcheck = newProcessVariablesInString(currentline, cpos, ipos, textmft, rightmargin, annotations,
					tunb, positiontrace, variabletrace, left, grammar, tempRef_leftstrings, resvariables, 0);

			leftstrings = tempRef_leftstrings.argvalue;
			if (!leftcheck)
			{
				// in the left a variable is undefined
				errmessage.argvalue = constraint + " undefined";
			
			}
			boolean rightcheck = false;
			if (leftcheck)
			{
				RefObject<ArrayList<String>> tempRef_rightstrings = new RefObject<ArrayList<String>>(rightstrings);
				rightcheck = newProcessVariablesInString(currentline, cpos, ipos, textmft, rightmargin, annotations,
						tunb, positiontrace, variabletrace, right, grammar, tempRef_rightstrings, resvariables, 0);
				rightstrings = tempRef_rightstrings.argvalue;
				if (!rightcheck)
				{
					// in the right a variable is undefined
					errmessage.argvalue = constraint + " undefined";
		
				}
				if (rightcheck)
				{
					RefObject<ArrayList<String>> tempRef_curoutputs2 = new RefObject<ArrayList<String>>(curoutputs);
					check = newSolveConstraint(left, leftstrings, rightstrings, nop, negation, grammar,
							tempRef_curoutputs2, resvariables, errmessage);
					curoutputs = tempRef_curoutputs2.argvalue;
					if (!check)
					{
						errmessage.argvalue = constraint + " failed";
					
						resoutputs.argvalue = null;
						resvariables.argvalue = null;
						return false;
					}
				}
				else
				{
					curoutputs = new ArrayList<String>();
					curoutputs.add("");
				}
			}
			else
			{
				curoutputs = new ArrayList<String>();
				curoutputs.add("");
			}
			iso = recindex + j + 1;

			// combine current constraint with tail
		}
		RefObject<ArrayList<String>> tempRef_tailoutputs5 = new RefObject<ArrayList<String>>(tailoutputs);
		check = newProcessConstraintsInString(currentline, cpos, ipos, textmft, rightmargin, annotations, tunb,
				positiontrace, variabletrace, soutput, grammar, tempRef_tailoutputs5, resvariables, iso, errmessage);
		tailoutputs = tempRef_tailoutputs5.argvalue;
		if (!check)
		{
			resoutputs.argvalue = null;
			resvariables.argvalue = null;
			return false;
		}
		resoutputs.argvalue = new ArrayList<String>();
		for (int i2 = 0; i2 < tailoutputs.size(); i2++)
		{
			for (int icur = 0; icur < curoutputs.size(); icur++)
			{
				String aresoutput = curoutputs.get(icur) + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param soutput
	 * @param hvars
	 * @param recindex
	 * @param grammar
	 * @param resoutputs
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final boolean newProcessConstraintsInStringForTransformed(String soutput, HashMap<String, String> hvars,
			int recindex, Grammar grammar, RefObject<ArrayList<String>> resoutputs, RefObject<String> errmessage)
			throws IOException, ClassNotFoundException
	{
	
		ParameterCheck.mandatory("resoutputs", resoutputs);
		ParameterCheck.mandatory("errmessage", errmessage);

		errmessage.argvalue = null;

		if (recindex >= soutput.length())
		{
			resoutputs.argvalue = new ArrayList<String>();
			resoutputs.argvalue.add("");
			return true;
		}
		boolean check;

		int iso = 0; // position in current output string -> next recursive index
		ArrayList<String> curoutputs = null;
		ArrayList<String> tailoutputs = null;
		COMBINEWITHTAIL: for (int uu = 0; uu < 1; uu++)
		{
			if (soutput.charAt(recindex) == '$')
			{
				// A VARIABLE
				RefObject<Integer> tempRef_iso = new RefObject<Integer>(iso);
				RefObject<ArrayList<String>> tempRef_curoutputs = new RefObject<ArrayList<String>>(curoutputs);
				check = newProcessSingleVariableInString(soutput, hvars, recindex, tempRef_iso, tempRef_curoutputs,
						errmessage);
				iso = tempRef_iso.argvalue;
				curoutputs = tempRef_curoutputs.argvalue;
				if (!check)
				{
					errmessage.argvalue = soutput + " undefined";
					String aresoutput = "*" + soutput + " undefined*";
					resoutputs.argvalue = new ArrayList<String>();
					resoutputs.argvalue.add(aresoutput);
					return true;
				}
				break COMBINEWITHTAIL;
			}
			else if (soutput.charAt(recindex) != '<')
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				for (iso = recindex; iso < soutput.length() && soutput.charAt(iso) != '<' && soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInStringForTransformed(soutput, hvars, iso, grammar, tempRef_tailoutputs,
						errmessage);
				tailoutputs = tempRef_tailoutputs.argvalue;
				if (errmessage.argvalue != null || !check)
				{
					resoutputs.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			int j;
			for (j = 1; recindex + j < soutput.length() && soutput.charAt(recindex + j) != '>'; j++)
			{
				;
			}
			if (recindex + j >= soutput.length()) // an opening '<' not followed by a closing '>': it cannot be a
													// constraint
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				sb.append("<");
				for (iso = recindex + 1; iso < soutput.length() && soutput.charAt(iso) != '<'
						&& soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs2 = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInStringForTransformed(soutput, hvars, iso, grammar, tempRef_tailoutputs2,
						errmessage);
				tailoutputs = tempRef_tailoutputs2.argvalue;
				if (!check)
				{
					resoutputs.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			// found a < ... > in soutput
			String constraint = soutput.substring(recindex, recindex + j + 1);
			if (!Dic.isALexicalConstraint(constraint))
			{
				// NOTALEXICALCONSTRAINT
				StringBuilder sb = new StringBuilder();
				sb.append("<");
				for (iso = recindex + 1; iso < soutput.length() && soutput.charAt(iso) != '<'
						&& soutput.charAt(iso) != '$'; iso++)
				{
					sb.append(soutput.charAt(iso));
				}
				String curoutput = sb.toString();
				RefObject<ArrayList<String>> tempRef_tailoutputs3 = new RefObject<ArrayList<String>>(tailoutputs);
				check = newProcessConstraintsInStringForTransformed(soutput, hvars, iso, grammar, tempRef_tailoutputs3,
						errmessage);
				tailoutputs = tempRef_tailoutputs3.argvalue;
				if (!check)
				{
					resoutputs.argvalue = null;
					return false;
				}
				resoutputs.argvalue = new ArrayList<String>();
				for (int i2 = 0; i2 < tailoutputs.size(); i2++)
				{
					String aresoutput = curoutput + tailoutputs.get(i2);
					resoutputs.argvalue.add(aresoutput);
				}
				return true;
			}

			// this is a lexical constraint => no output
			curoutputs = new ArrayList<String>();
			curoutputs.add("");

			// now process the current lexical contraint
			String left = null, nop = null, right = null;
			RefObject<String> tempRef_left = new RefObject<String>(left);
			RefObject<String> tempRef_nop = new RefObject<String>(nop);
			RefObject<String> tempRef_right = new RefObject<String>(right);
			Dic.parseLexicalConstraint(constraint, tempRef_left, tempRef_nop, tempRef_right);
			left = tempRef_left.argvalue;
			nop = tempRef_nop.argvalue;
			right = tempRef_right.argvalue;
			boolean negation = false;
			ArrayList<String> leftstrings = null, rightstrings = null;
			RefObject<ArrayList<String>> tempRef_leftstrings = new RefObject<ArrayList<String>>(leftstrings);
			boolean leftcheck = newProcessVariablesInString(left, hvars, 0, tempRef_leftstrings);
			leftstrings = tempRef_leftstrings.argvalue;
			if (!leftcheck)
			{
				// in the left a variable is undefined
				errmessage.argvalue = constraint + " undefined";
				
			}
			boolean rightcheck = false;
			if (leftcheck)
			{
				RefObject<ArrayList<String>> tempRef_rightstrings = new RefObject<ArrayList<String>>(rightstrings);
				rightcheck = newProcessVariablesInString(right, hvars, 0, tempRef_rightstrings);
				rightstrings = tempRef_rightstrings.argvalue;
				if (!rightcheck)
				{
					// in the right a variable is undefined
					errmessage.argvalue = constraint + " undefined";
					
				}
				if (rightcheck)
				{
					ArrayList<HashMap<String, String>> listofhvars = new ArrayList<HashMap<String, String>>();
					listofhvars.add(hvars);
					RefObject<ArrayList<HashMap<String, String>>> tempRef_listofhvars = new RefObject<ArrayList<HashMap<String, String>>>(
							listofhvars);
					check = newSolveConstraintForTransformed(left, leftstrings, rightstrings, nop, negation, grammar,
							tempRef_listofhvars, errmessage);
					listofhvars = tempRef_listofhvars.argvalue;
					if (!check)
					{
						errmessage.argvalue = constraint + " failed";
					
						resoutputs.argvalue = null;
						return false;
					}
				}
			}
			iso = recindex + j + 1;

			// combine current constraint with tail
		}
		RefObject<ArrayList<String>> tempRef_tailoutputs4 = new RefObject<ArrayList<String>>(tailoutputs);
		check = newProcessConstraintsInStringForTransformed(soutput, hvars, iso, grammar, tempRef_tailoutputs4,
				errmessage);
		tailoutputs = tempRef_tailoutputs4.argvalue;
		if (!check)
		{
			resoutputs.argvalue = null;
			return false;
		}
		resoutputs.argvalue = new ArrayList<String>();
		for (int i2 = 0; i2 < tailoutputs.size(); i2++)
		{
			for (int icur = 0; icur < curoutputs.size(); icur++)
			{
				String aresoutput = curoutputs.get(icur) + tailoutputs.get(i2);
				resoutputs.argvalue.add(aresoutput);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param h1
	 * @param h2
	 * @return
	 */
	private boolean sameHashMaps(HashMap<String, String> h1, HashMap<String, String> h2)
	{
		if (h1 == null)
		{
			if (h2 == null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		if (h2 == null)
		{
			return false;
		}
		if (h1.size() != h2.size())
		{
			return false;
		}
		for (String key : h1.keySet())
		{
			if (!h2.containsKey(key))
			{
				return false;
			}
			String val1 = h1.get(key);
			String val2 = h2.get(key);
			if (!val1.equals(val2))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param resvariables
	 */
	private void removeAllThisVariables(RefObject<ArrayList<HashMap<String, String>>> resvariables)
	{
		ParameterCheck.mandatory("resvariables", resvariables);
		for (int i = 0; i < resvariables.argvalue.size(); i++)
		{
			HashMap<String, String> hvar = resvariables.argvalue.get(i);
			ArrayList<String> allkeystoremove = new ArrayList<String>();
			for (String key : hvar.keySet())
			{
				if (key.length() >= (new String("THIS")).length()
						&& key.substring(0, (new String("THIS")).length()).equals("THIS"))
				{
					allkeystoremove.add(key);
				}
			}
			for (String key : allkeystoremove)
			{
				hvar.remove(key);
			}
		}
	}

	/**
	 * 
	 * @param currentline
	 * @param textmft
	 * @param annotations
	 * @param tunb
	 * @param grammar
	 * @param positiontrace
	 * @param rightmargin
	 * @param variabletrace
	 * @param cpos
	 * @param output
	 * @param resoutputs
	 * @param resvariables
	 * @param recindex
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final boolean newProcessConstraints(String currentline, Mft textmft, ArrayList<Object> annotations,
			int tunb, Grammar grammar, ArrayList<Double> positiontrace, int rightmargin,
			ArrayList<String> variabletrace, double cpos, ArrayList<String> output,
			RefObject<ArrayList<ArrayList<String>>> resoutputs,
			RefObject<ArrayList<HashMap<String, String>>> resvariables, int recindex, RefObject<String> errmessage)
			throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatory("output", output);
		ParameterCheck.mandatory("resoutputs", resoutputs);
		ParameterCheck.mandatory("errmessage", errmessage);
		errmessage.argvalue = null;
		if (recindex >= output.size())
		{
			resoutputs.argvalue = new ArrayList<ArrayList<String>>();
			resoutputs.argvalue.add(new ArrayList<String>());
			return true;
		}

		boolean check;

		// call recursively with output's tail
		ArrayList<ArrayList<String>> tailoutputs = null;
		RefObject<ArrayList<ArrayList<String>>> tempRef_tailoutputs = new RefObject<ArrayList<ArrayList<String>>>(
				tailoutputs);

		check = newProcessConstraints(currentline, textmft, annotations, tunb, grammar, positiontrace, rightmargin,
				variabletrace, cpos, output, tempRef_tailoutputs, resvariables, recindex + 1, errmessage);

		tailoutputs = tempRef_tailoutputs.argvalue;
		if (!check)
		{
			resoutputs.argvalue = null;
			return false;
		}

		// now replace all variable calls in output[recindex] with their content found in input
		ArrayList<String> curoutputs2 = null;
		String soutput = output.get(recindex);
		if (soutput == null)
		{
			curoutputs2 = new ArrayList<String>();
			curoutputs2.add(null);
		}
		else
		{
			RefObject<ArrayList<String>> tempRef_curoutputs2 = new RefObject<ArrayList<String>>(curoutputs2);
			check = newProcessConstraintsInString(currentline, cpos, recindex, textmft, rightmargin, annotations, tunb,
					positiontrace, variabletrace, soutput, grammar, tempRef_curoutputs2, resvariables, 0, errmessage);
			curoutputs2 = tempRef_curoutputs2.argvalue;
			if (!check)
			{
				resoutputs.argvalue = null;
				resvariables.argvalue = null;
				return false;
			}
			// clean up resvariables by removing all its $THIS variables
			removeAllThisVariables(resvariables);
			if (curoutputs2 != null && curoutputs2.size() >= 2)
			{
				// filter out all unnecessary duplicates
				for (int i = 0; i < curoutputs2.size(); i++)
				{
					for (int j = i + 1; j < curoutputs2.size();)
					{
						if (curoutputs2.get(i) == curoutputs2.get(j)
								&& sameHashMaps(resvariables.argvalue.get(i), resvariables.argvalue.get(j)))
						{
							// remove j
							curoutputs2.remove(j);
							resvariables.argvalue.remove(j);
						}
						else
						{
							j++;
						}
					}
				}
			}
		}
		// now concatenate curoutputs in front of each tailoutputs and set resoutputs
		resoutputs.argvalue = new ArrayList<ArrayList<String>>();
		if (curoutputs2 != null)
		{
			for (int itail = 0; itail < tailoutputs.size(); itail++)
			{
				ArrayList<String> atailoutput = tailoutputs.get(itail);
				for (int icur = 0; icur < curoutputs2.size(); icur++)
				{
					String curoutput = curoutputs2.get(icur);
					// Using copy-constructor instead of clone() - recommended because of unchecked class cast
					ArrayList<String> aresoutput = new ArrayList<String>(atailoutput);
					aresoutput.add(0, curoutput);
					resoutputs.argvalue.add(aresoutput);
				}
			}

		}
		return true;
	}

	/**
	 * 
	 * @param buffer
	 * @param expectXMLtags
	 * @param absolutebegaddress
	 */
	public final void moveBegAddressOufOfSpaces(String buffer, boolean expectXMLtags,
			RefObject<Double> absolutebegaddress)
	{
		ParameterCheck.mandatoryString("buffer", buffer);
		ParameterCheck.mandatory("absolutebegaddress", absolutebegaddress);

		boolean aproblem = true;
		while (aproblem)
		{
			aproblem = false;
			if (Character.isWhitespace(buffer.charAt((int) (double) absolutebegaddress.argvalue)))
			{
				aproblem = true;
				absolutebegaddress.argvalue++;
			}
			else if (expectXMLtags && buffer.charAt((int) (double) absolutebegaddress.argvalue) == '<')
			{
				aproblem = true;
				for (absolutebegaddress.argvalue++; buffer.charAt((int) (double) absolutebegaddress.argvalue) != '>'; absolutebegaddress.argvalue++)
				{
					;
				}
				absolutebegaddress.argvalue++;
			}
		}
	}

	/**
	 * 
	 * @param output
	 * @param lexemes
	 * @return
	 */
	private ArrayList<String> mergeOutputWithLexemes(ArrayList<String> output, ArrayList<String> lexemes)
	{
		ParameterCheck.mandatory("output", output);
		ParameterCheck.mandatory("lexemes", lexemes);

		ArrayList<String> merged = new ArrayList<String>();
		for (int i = 0; i < output.size(); i++)
		{
			String op = output.get(i);
			String lx = lexemes.get(i);

			if (op != null)
			{
				merged.add(op + "#");
			}

			if (lx != null)
			{
				if (lx.length() >= 1 && lx.charAt(0) == ':')
				{
					merged.add(lx + "#"); // add graph name
				}
				else if (lx.length() > 2 && lx.charAt(0) == '$' && lx.charAt(1) == '(' || lx.charAt(1) == ')')
				{
					merged.add(lx + "#"); // add variable
				}
				else
				{
					merged.add("<" + Dic.getRidOfSpecialFeatures(lx) + ">" + "#"); // add lexeme
				}
			}
		}
		return merged;
	}

	/**
	 * 
	 * @param corpus
	 * @param text
	 * @param annotations
	 * @param grammar
	 * @param cm
	 * @param limitation
	 * @param keepLexemesInSolution
	 * @param enforcecompletexrefs
	 * @param thereisunamb
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final TheSolutions syntacticParsing(Corpus corpus, Ntext text, ArrayList<Object> annotations,
			Grammar grammar, char cm, int limitation, boolean keepLexemesInSolution, boolean enforcecompletexrefs,
			RefObject<Boolean> thereisunamb, RefObject<String> errmessage) throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatory("text", text);
		ParameterCheck.mandatory("grammar", grammar);
		ParameterCheck.mandatory("thereisunamb", thereisunamb);
		ParameterCheck.mandatory("errmessage", errmessage);

 		errmessage.argvalue = null;

		ArrayList<String> recursiveCalls = new ArrayList<String>();
		TheSolutions sols = null;
		
		double cpos; // current position in text unit
		thereisunamb.argvalue = false;

	

		Gram grm = grammar.grams.get("Main");
		if (grm == null)
		{
			errmessage.argvalue = "Grammar has no main graph/rule";
			Dic.writeLog(errmessage.argvalue);
			return null;
		}
		// mode
		MatchType typeofmatch;
		if (cm == 'A')
		{
			typeofmatch = MatchType.ALL;
		}
		else if (cm == 'S')
		{
			typeofmatch = MatchType.SHORTEST;
		}
		else if (cm == 'L')
		{
			typeofmatch = MatchType.LONGEST;
		}
		else if (cm == 'X')
		{
			typeofmatch = MatchType.ALL;
		}
		else
		{
			System.out.println("type of match unknown");
			return null;
		}

		// main loop: parse each text unit

		boolean limitfinish = false;
		int nbofmatches = 0;
		int progressPercentage = 0;
		for (int itu = 1; itu <= text.mft.tuAddresses.length - 1 && !limitfinish; itu++)
		{
			int tuaddress = text.mft.tuAddresses[itu];
			CurrentLine = text.buffer.substring(tuaddress, tuaddress + text.mft.tuLengths[itu]);
			int inewsols = -1;
			if (sols != null)
			{
				inewsols = sols.list.size();
			}
			for (int ichar = 0; ichar < CurrentLine.length() && !limitfinish;)
			{
				

				// get rid of white spaces
				if (Character.isWhitespace(CurrentLine.charAt(ichar)))
				{
					ichar++;
					continue;
				}

				cpos = ichar; // starting position of the token in the text unit
				

				if (!Lan.asianTokenizer && Language.isLetter(CurrentLine.charAt(ichar))) // word form
				{
					for (ichar++; ichar < CurrentLine.length() && Language.isLetter(CurrentLine.charAt(ichar)); ichar++)
					{
						;
					}
					
				}
				else if (text.XmlNodes != null && CurrentLine.charAt((int) cpos) == '<') // xml tag
				{
					for (ichar++; ichar < CurrentLine.length() && CurrentLine.charAt(ichar) != '>'; ichar++)
					{
						;
					}
					if (ichar < CurrentLine.length())
					{
						ichar++;
						
						continue;
					}
					else
					// non closing '<' are processed as delimiters
					{
						ichar = (int) cpos + 1;
					
					}
				}
				else
				// delimiter
				{
					ichar++;
					
				}

				// apply the grammar and store the result
				ArrayList<Double> sollengths = null;
				ArrayList<ArrayList<Double>> solinputs = null;
				ArrayList<ArrayList<String>> soloutputs = null;
				ArrayList<ArrayList<Object>> solnodes = null;
				ArrayList<ArrayList<String>> solvariables = null;

				while (true)
				{
					if (BackgroundWorking)
					{
						if (backgroundWorker.isCancellationPending())
						{
							errmessage.argvalue = "Cancelled";
							Dic.writeLog(errmessage.argvalue);
							return null;
						}
						if (corpus == null)
						{
							int nprogress = (int) (itu * 100.0F / text.nbOfTextUnits);
							if (nprogress != progressPercentage)
							{
								progressPercentage = nprogress;
								if (backgroundWorker.isBusy())
								{
									backgroundWorker.reportProgress(nprogress);
								}
							}
						}
					}
				
					// First get ALL matches; priority LONGEST or SHORTEST can only be computed AFTER dealing with ONCE,
					// UNAMB, XREFs and EXCLUDE
					RefObject<ArrayList<Double>> tempRef_sollengths = new RefObject<ArrayList<Double>>(sollengths);
					RefObject<ArrayList<ArrayList<Double>>> tempRef_solinputs = new RefObject<ArrayList<ArrayList<Double>>>(
							solinputs);
					RefObject<ArrayList<ArrayList<String>>> tempRef_solvariables = new RefObject<ArrayList<ArrayList<String>>>(
							solvariables);
					RefObject<ArrayList<ArrayList<String>>> tempRef_soloutputs = new RefObject<ArrayList<ArrayList<String>>>(
							soloutputs);
					RefObject<ArrayList<ArrayList<Object>>> tempRef_solnodes = new RefObject<ArrayList<ArrayList<Object>>>(
							solnodes);

					int da = grammar.syntaxMatch(grammar.fullName, -1, CurrentLine, cpos, itu, text.mft, annotations,
							grm, tempRef_sollengths, tempRef_solinputs, tempRef_solvariables, tempRef_soloutputs,
							tempRef_solnodes, MatchType.ALL, true, text.XmlNodes != null, recursiveCalls);

					sollengths = tempRef_sollengths.argvalue;
					solinputs = tempRef_solinputs.argvalue;
					solvariables = tempRef_solvariables.argvalue;
					soloutputs = tempRef_soloutputs.argvalue;

					// now filter out all incorrect XREFs
					if (da > 0 && enforcecompletexrefs)
					{
						da = filterXrefs(sollengths, solinputs, solvariables, soloutputs);
					}

					if (da > 0)
					{
						// now process constraints
						ArrayList<ArrayList<Double>> res_solinputs = new ArrayList<ArrayList<Double>>();
						ArrayList<ArrayList<String>> res_soloutputs = new ArrayList<ArrayList<String>>();
						ArrayList<Double> res_sollengths = new ArrayList<Double>();
						ArrayList<ArrayList<String>> res_solvariables = new ArrayList<ArrayList<String>>();

						for (int isol = 0; isol < sollengths.size(); isol++)
						{
							ArrayList<String> soloutput = soloutputs.get(isol);
							deleteONCE(soloutput);
							int rightmargin = (int) (cpos + sollengths.get(isol));
							ArrayList<ArrayList<String>> tmpoutputs = null;
							ArrayList<HashMap<String, String>> resvariables = new ArrayList<HashMap<String, String>>();
							resvariables.add(new HashMap<String, String>());
							RefObject<ArrayList<ArrayList<String>>> tempRef_tmpoutputs = new RefObject<ArrayList<ArrayList<String>>>(
									tmpoutputs);
							RefObject<ArrayList<HashMap<String, String>>> tempRef_resvariables = new RefObject<ArrayList<HashMap<String, String>>>(
									resvariables);

							boolean check = newProcessConstraints(CurrentLine, text.mft, annotations, itu, grammar,
									solinputs.get(isol), rightmargin, solvariables.get(isol), cpos, soloutput,
									tempRef_tmpoutputs, tempRef_resvariables, 0, errmessage);

							tmpoutputs = tempRef_tmpoutputs.argvalue;
							resvariables = tempRef_resvariables.argvalue;
							if (!check)
							{
								continue;
							}
							for (int i2 = 0; i2 < tmpoutputs.size(); i2++)
							{
								// all lexical constraints check OK => replace $1, $2, etc. with the corresponding LUs
								// in the output
								soloutput = tmpoutputs.get(i2);
								processELCSFVariables(soloutput, "");
								deleteAllLUsFromOutput(soloutput);

								res_solinputs.add(solinputs.get(isol));
								res_soloutputs.add(tmpoutputs.get(i2));
								res_sollengths.add(sollengths.get(isol));
								res_solvariables.add(solvariables.get(isol));
							}
						}

						// Using copy-constructor instead of clone() - recommended because of unchecked class cast
						solinputs = new ArrayList<ArrayList<Double>>(res_solinputs);
						soloutputs = new ArrayList<ArrayList<String>>(res_soloutputs);
						sollengths = new ArrayList<Double>(res_sollengths);
						solvariables = new ArrayList<ArrayList<String>>(res_solvariables);

						da = sollengths.size();
					}

					// (only now) filter out EXCLUDE (because EXCLUDE in paths that do not check do not count)
					boolean needminimlength = false;
					double smallerlength = 0.0;
					if (da > 0)
					{
						RefObject<Boolean> tempRef_needminimlength = new RefObject<Boolean>(needminimlength);
						RefObject<Double> tempRef_smallerlength = new RefObject<Double>(smallerlength);
						da = filterExclude(sollengths, solinputs, solvariables, soloutputs, tempRef_needminimlength,
								tempRef_smallerlength);
						needminimlength = tempRef_needminimlength.argvalue;
						smallerlength = tempRef_smallerlength.argvalue;
					}

					// now filter out UNAMBs
					if (da > 0)
					{
						RefObject<Double> tempRef_smallerlength2 = new RefObject<Double>(smallerlength);
						da = filterUnamb(sollengths, solinputs, solvariables, soloutputs, thereisunamb,
								tempRef_smallerlength2);
						smallerlength = tempRef_smallerlength2.argvalue;
						if (thereisunamb.argvalue)
						{
							needminimlength = true;
						}
					}

				if (da > 0)
					{
						// now process priority levels SHORTEST LONGEST
						if (typeofmatch == MatchType.LONGEST)
						{
							if (da > 1)
							{
								da = keepLongest(sollengths, solinputs, solvariables, soloutputs);
							}
							needminimlength = true;
							smallerlength = sollengths.get(0);
						}
						else if (typeofmatch == MatchType.SHORTEST)
						{
							if (da > 1)
							{
								da = keepShortest(sollengths, solinputs, solvariables, soloutputs);
							}
							needminimlength = true;
							smallerlength = sollengths.get(0);
						}

					for (int isol = 0; !limitfinish && isol < sollengths.size(); isol++)
						{
							if (sollengths.get(isol) == 0.0)
							{
								errmessage.argvalue = "Grammar " + new File(grammar.fullName).getName()
										+ " recognizes the empty string.";
								Dic.writeLog(errmessage.argvalue);
								return null;
							}
							if (needminimlength)
							{
								smallerlength = sollengths.get(isol);
							}
							if (sols == null)
							{
								sols = new TheSolutions();
							}
							ArrayList<String> output2 = soloutputs.get(isol);
							if (keepLexemesInSolution) 
							{
								ArrayList<String> o3 = mergeOutputWithLexemes(output2, solvariables.get(isol));

								if (!alreadythere(sols, inewsols, itu, cpos, sollengths.get(isol), solinputs.get(isol),
										o3))
								{
									sols.addASolution(itu, cpos, sollengths.get(isol), solinputs.get(isol), o3);

									// check for limitation
									nbofmatches++;
									if (limitation != -1 && nbofmatches > limitation)
									{
										limitfinish = true;
										break;
									}
								}
							}
							else
							{
								if (!alreadythere(sols, inewsols, itu, cpos, sollengths.get(isol), solinputs.get(isol),
										output2))
								{
									sols.addASolution(itu, cpos, sollengths.get(isol), solinputs.get(isol), output2);

									// check for limitation
									nbofmatches++;
									if (limitation != -1 && nbofmatches > limitation)
									{
										limitfinish = true;
										break;
									}
								}
							}
						}
					}
					if (needminimlength)
					{
						if (da > 0 && thereisunamb.argvalue)
						{
							// compute the smallest +UNAMB path in order: that's where we will go from here
							ArrayList<String> output = soloutputs.get(0);
							int minlen = output.size();
							int iminlen = 0;
							for (int isol = 1; isol < soloutputs.size(); isol++)
							{
								output = soloutputs.get(isol);
								if (output.size() < minlen)
								{
									minlen = output.size();
									iminlen = isol;
								}
							}
							cpos += sollengths.get(iminlen);
							if (cpos < ichar)
							{// it's ok for me to do continue here because it will only go to the while's beggining
								continue;
							}
							if (cpos > ichar)
							{
								ichar = (int) cpos;
							}
						}
						else
						
						{
							cpos += smallerlength;
							if (cpos < ichar)
							{// it's ok for me to do continue here because it will only go to the while's beggining
								continue;
							}
							if (cpos > ichar)
							{
								ichar = (int) cpos;
							}
						}
					}
					else
					{
						if (text.mft.thereAreLexs(itu, cpos + 0.01))
						{
							cpos += 0.01;
							// it's ok for me to do continue here because it will only go to the while's beggining
							continue;
						}
					}
					break;
				}
			}
		}
		errmessage.argvalue = null;
		return sols;
	}

	public boolean processConstraints(String current, Mft textmft, int tunb, ArrayList<Object> annotations,
			Grammar grammar, ArrayList<Double> position, int rightmargin, ArrayList<String> variable, double cpos,
			ArrayList<String> output, RefObject<String> errMessage) throws IOException, ClassNotFoundException
	{
		errMessage.argvalue = "";

		for (int ipos = 0; ipos < output.size(); ipos++)
		{
			String so = output.get(ipos);

			if (so == null || so.equals(""))
				continue;

			boolean remove = false;

			for (int iso = 0; iso < so.length();)
			{
				if (so.charAt(iso) != '<')
				{
					iso++;
					continue;
				}

				int len = -1;
				for (len = 1; iso + len < so.length(); len++)
				{
					if (so.charAt(iso + len) == '>')
						break;
				}

				if (iso + len < so.length())
				{
					String so2 = so.substring(iso, iso + len + 1);

					if (Dic.isALexicalConstraint(so2))
					{
						RefObject<ArrayList<String>> constraintresults = new RefObject<ArrayList<String>>(
								new ArrayList<String>());

						if (!this.processConstraint(current, textmft, tunb, annotations, grammar, position,
								rightmargin, variable, cpos, ipos, so2, constraintresults, errMessage))
							return false;

						if (constraintresults != null && constraintresults.argvalue.size() > 0)
						{
							// BUG ALERT: in fact I should replace the constraint with ALL its results and multiply all
							// solutions...
							output.add(ipos, "<LU=" + constraintresults.argvalue.get(0) + ">");
						}
						else
							remove = true;
					}
				}
				iso += len + 1;
			}
			if (remove)
				
				output.add(ipos, ""); // constraint has been satisfied => remove it
		}

		return true;
	}

	/**
	 * 
	 * @param reladdresses
	 * @param absbegaddress
	 * @return
	 */
	public static ArrayList<Double> rel2Abs(ArrayList<Double> reladdresses, long absbegaddress)
	{
		ParameterCheck.mandatory("reladdresses", reladdresses);
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i = 0; i < reladdresses.size(); i++)
		{
			long hundaddress = (long) (100 * reladdresses.get(i)) + 100 * absbegaddress;
			double absaddress = hundaddress / 100.0;
			res.add(absaddress);
		}
		return res;
	}

	/**
	 * 
	 * @param corpus
	 * @param mytext
	 * @param annotations
	 * @param startingpoint
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final boolean applyAllGrammars(Corpus corpus, Ntext mytext, ArrayList<Object> annotations,
			int startingpoint, RefObject<String> errmessage) throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatory("mytext", mytext);
		ParameterCheck.mandatory("errmessage", errmessage);

		errmessage.argvalue = null;
		boolean xmltext;
		HashMap<String, Integer> hCorpusPhrases;
		if (corpus != null)
		{
			hCorpusPhrases = corpus.hPhrases;
			xmltext = (corpus.xmlNodes != null);
		}
		else
		{
			hCorpusPhrases = null;
			xmltext = (mytext.XmlNodes != null);
		}

		if (synGrms == null || synGrms.isEmpty())
		{
			errmessage.argvalue = "No grammar to apply";
			Dic.writeLog(errmessage.argvalue);
			return false;
		}

		for (int iprio = startingpoint; iprio < 100; iprio++)
		{
			if (startingpoint == 0 && iprio > 0) // when applying dictionary/grammars, only apply dictionary/grammar
													// pairs (priority = 0)
			{
				break;
			}
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					errmessage.argvalue = "Cancelled";
					Dic.writeLog(errmessage.argvalue);
					return false;
				}
				if (corpus == null)
				{
					if (backgroundWorker.isBusy())
					{
						backgroundWorker.reportProgress(0);
					}
				}
			}
			for (int idic = 0; idic < synGrms.size(); idic += 3)
			{
				Grammar synGrm = (Grammar) synGrms.get(idic);

				if ((Integer) synGrms.get(idic + 1) != iprio)
				{
					continue;
				}

				char parsingmode = (Character) synGrms.get(idic + 2); // A X S L
				boolean thereisunamb = false;
				RefObject<Boolean> tempRef_thereisunamb = new RefObject<Boolean>(thereisunamb);
				TheSolutions thesolutions = syntacticParsing(corpus, mytext, annotations, synGrm, parsingmode, -1,
						false, true, tempRef_thereisunamb, errmessage); // WE DO NOT PROCESS AMBIGUITIES
				thereisunamb = tempRef_thereisunamb.argvalue;
				if (thesolutions == null && errmessage.argvalue != null)
				{
					return false;
				}
				if (thesolutions == null || thesolutions.list.isEmpty())
				{
					continue;
				}
				for (int isol = 0; isol < thesolutions.list.size(); isol++)
				{
					int tunb = thesolutions.getTuNb(isol);
					long hund_begaddress = (long) (100 * thesolutions.getBegAddress(isol)) + 100
							* mytext.mft.tuAddresses[tunb];
					double absolutebegaddress = hund_begaddress / 100.0;
					long hund_endaddress = (long) ((absolutebegaddress + 0.005) * 100.0)
							+ (long) (thesolutions.getLength(isol) * 100.0);
					double absoluteendaddress = hund_endaddress / 100.0;

					ArrayList<Double> reladdresses = thesolutions.getInput(isol);
					ArrayList<Double> absaddresses = rel2Abs(reladdresses, mytext.mft.tuAddresses[tunb]);
					ArrayList<String> output = thesolutions.getOutput(isol);
					ArrayList<Object> seqofannotations = this.mergeIntoAnnotations(mytext.buffer, tunb,
							absolutebegaddress, absoluteendaddress, absaddresses, output, false);

					if (seqofannotations == null)
					{
						continue;
					}

					int xrefnb;
					if (corpus == null)
					{
						xrefnb = mytext.annotations.size();
					}
					else
					{
						xrefnb = corpus.annotations.size();
					}

					// first: apply all disambiguation annotations
					for (int ia = 4; ia < seqofannotations.size(); ia += 3)
					{
						double item_absolutebegaddress = (Double) seqofannotations.get(ia);
						// make sure beginning of annotation does not start at spaces or XML tags
						RefObject<Double> tempRef_item_absolutebegaddress = new RefObject<Double>(
								item_absolutebegaddress);
						this.moveBegAddressOufOfSpaces(mytext.buffer, xmltext, tempRef_item_absolutebegaddress);
						item_absolutebegaddress = tempRef_item_absolutebegaddress.argvalue;
						String item_soutput = (String) seqofannotations.get(ia + 1);
						double item_absoluteendaddress = (Double) seqofannotations.get(ia + 2);

						if (item_absolutebegaddress == item_absoluteendaddress)
						{
							// the item is to be disambiguated
							// Check to see that the embedding sequence is not recognized by another match (before or
							// after)
							int nbofsolutions = 1;
							for (int c_isol = 0; c_isol < thesolutions.list.size(); c_isol++)
							{
								int c_tunb = thesolutions.getTuNb(c_isol);
								if (c_tunb < tunb)
								{
									continue;
								}
								else if (c_tunb > tunb)
								{
									break;
								}
								if (c_isol == isol)
								{
									continue;
								}

								hund_begaddress = (long) (100 * thesolutions.getBegAddress(c_isol)) + 100
										* mytext.mft.tuAddresses[c_tunb];
								double c_begaddress = hund_begaddress / 100.0;
								if (c_begaddress < absolutebegaddress)
								{
									continue;
								}
								else if (c_begaddress > absolutebegaddress)
								{
									break;
								}

								hund_endaddress = (long) (100 * c_begaddress)
										+ (long) (100 * thesolutions.getLength(c_isol));
								double c_endaddress = hund_endaddress / 100.0;
								if (c_endaddress != absoluteendaddress)
								{
									continue;
								}

								nbofsolutions++;
								break;
							}
							if (nbofsolutions == 1) // the sequence has matched only once
							{
								// arithmetic problem: following line does not work
								
								long a = (long) ((item_absolutebegaddress + 0.005) * 100);
								long r = a - (100 * mytext.mft.tuAddresses[tunb]);
								double relbegaddress = r / 100.0;
								boolean anxrefwasremoved = false;
								RefObject<Boolean> tempRef_anxrefwasremoved = new RefObject<Boolean>(anxrefwasremoved);
								mytext.mft.filterTransitions(annotations, tunb, relbegaddress, item_soutput,
										tempRef_anxrefwasremoved);
								anxrefwasremoved = tempRef_anxrefwasremoved.argvalue;
								if (anxrefwasremoved)
								{
									mytext.mft.filterInconsistentXrefs(annotations, tunb);
								}
							}
						}
					}

					// get the number of XREFs
					int nbxref = 0;
					for (int ia = 4; ia < seqofannotations.size(); ia += 3)
					{
						String item_soutput = (String) seqofannotations.get(ia + 1);
						if (item_soutput != null && item_soutput.indexOf("XREF") != -1)
						{
							nbxref++;
						}
					}

					// second: add all new annotations
					for (int ia = 4; ia < seqofannotations.size(); ia += 3)
					{
						double item_absolutebegaddress = (Double) seqofannotations.get(ia);
						// make sure beginning of annotation does not start at spaces or XML tags
						RefObject<Double> tempRef_item_absolutebegaddress2 = new RefObject<Double>(
								item_absolutebegaddress);
						this.moveBegAddressOufOfSpaces(mytext.buffer, xmltext, tempRef_item_absolutebegaddress2);
						item_absolutebegaddress = tempRef_item_absolutebegaddress2.argvalue;
						String item_soutput = (String) seqofannotations.get(ia + 1);
						double item_absoluteendaddress = (Double) seqofannotations.get(ia + 2);

						if (item_absolutebegaddress < item_absoluteendaddress)
						{
							// this is a proper ANNOTATION insertion, e.g. SYNTAX,DATE
							long hund_relbegaddress = (long) (100 * item_absolutebegaddress) - (long) 100
									* mytext.mft.tuAddresses[tunb];
							double relbegaddress = hund_relbegaddress / 100.0;
							long hund_relendaddress = (long) (100 * item_absoluteendaddress) - (long) 100
									* mytext.mft.tuAddresses[tunb];
							double relendaddress = hund_relendaddress / 100.0;
							String sinput = Dic.cleanupEntry(mytext.buffer.substring((int) item_absolutebegaddress,
									(int) item_absoluteendaddress), xmltext);
							if (item_soutput != null && item_soutput.indexOf("XREF") != -1)
							{
								item_soutput = item_soutput.replace("XREF", "XREF=" + xrefnb + "." + nbxref);
							}
							item_soutput = Dic.cleanUpDoubleQuotes(item_soutput);
							if (item_soutput.length() > 0 && item_soutput.charAt(0) == '<')
							{
								// output contains a lexeme, e.g. "<the,DET>+XREF" or
								// "<take,V+DET=<a,DET>+N1=<nap,N>>+XREF" => remove external < and >
								StringBuilder sb = new StringBuilder();
								int reclevel = 1;
								for (int i = 1; i < item_soutput.length(); i++)
								{
									if (reclevel == 1 && item_soutput.charAt(i) == '>')
									{
										reclevel--; // do not copy '>'
										continue;
									}
									if (item_soutput.charAt(i) == '<')
									{
										reclevel++;
									}
									if (item_soutput.charAt(i) == '>')
									{
										reclevel--;
									}
									sb.append(item_soutput.charAt(i));
								}
								item_soutput = sb.toString();
							}
							boolean resetannotations = (parsingmode == 'X' && thereisunamb);
							if (Dic.isALexicalAnnotation(item_soutput))
							{
								// a lexical annotation, e.g. "eat,V"
								if (sinput.equals(""))
								{
									if (hCorpusPhrases != null)
									
									{
										this.addSyntaxToCorpus(annotations, null, hCorpusPhrases, item_soutput,
												mytext.mft, tunb, relbegaddress, relendaddress, resetannotations);
									}
									else
									
									{
										this.addSyntaxToText(annotations, null, mytext.hPhrases, item_soutput,
												mytext.mft, tunb, relbegaddress, relendaddress, resetannotations);
									}
								}
								else
								{
									if (hCorpusPhrases != null)
									
									{
										this.addSyntaxToCorpus(annotations, null, hCorpusPhrases, sinput + ","
												+ item_soutput, mytext.mft, tunb, relbegaddress, relendaddress,
												resetannotations);
									}
									else
									
									{
										this.addSyntaxToText(annotations, null, mytext.hPhrases, sinput + ","
												+ item_soutput, mytext.mft, tunb, relbegaddress, relendaddress,
												resetannotations);
									}
								}
							}
							else
							{
								// a syntactic annotation, e.g. "DATE+Informal" or "NP+Human+plural"
								String lexeme = sinput + ",SYNTAX," + item_soutput;
								if (Dic.isALexicalAnnotation(lexeme))
								{
									if (hCorpusPhrases != null)
									
									{
										this.addSyntaxToCorpus(annotations, null, hCorpusPhrases, lexeme, mytext.mft,
												tunb, relbegaddress, relendaddress, resetannotations);
									}
									else
									
									{
										this.addSyntaxToText(annotations, null, mytext.hPhrases, lexeme, mytext.mft,
												tunb, relbegaddress, relendaddress, resetannotations);
									}
								}
								else
								{
									if (hCorpusPhrases != null)
								
									{
										this.addSyntaxToCorpus(annotations, null, hCorpusPhrases, sinput
												+ ",INVALIDLEXEME", mytext.mft, tunb, relbegaddress, relendaddress,
												resetannotations);
									}
									else
								
									{
										this.addSyntaxToText(annotations, null, mytext.hPhrases, sinput
												+ ",INVALIDLEXEME", mytext.mft, tunb, relbegaddress, relendaddress,
												resetannotations);
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param alloriginalfeatures
	 * @return
	 */
	private String combineAllFeatures(String[] alloriginalfeatures)
	{
		if (alloriginalfeatures == null || alloriginalfeatures.length == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String feat : alloriginalfeatures)
		{
			sb.append("+" + feat);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param entry
	 * @param lemma
	 * @param category
	 * @param alloriginalfeatures
	 * @return
	 */
	private ArrayList<String> inflectAndDerive(String entry, String lemma, String category, String[] alloriginalfeatures)
	{
		ParameterCheck.mandatoryString("category", category);
		ArrayList<String> lexemes = null;

		String[] lexfeatures = Dic.getRidOfInflectionalFeatures(alloriginalfeatures, this.prop_inf);

		// inflect
		String myfeatureflx = Dic.lookFor("FLX", lexfeatures);
		String expname = null;
		if (myfeatureflx != null)
		{
			expname = myfeatureflx.substring((new String("FLX=")).length());
			Gram grm = this.paradigms.get(expname);
			if (grm != null)
			{
				if (grm.vocabIn == null)
				{
					grm.prepareForParsing();
				}
				String[] forms = null, outputs = null;
				RefObject<String[]> tempRef_forms = new RefObject<String[]>(forms);
				RefObject<String[]> tempRef_outputs = new RefObject<String[]>(outputs);
				grm.inflect(Lan, lemma, tempRef_forms, tempRef_outputs, this.paradigms);
				forms = tempRef_forms.argvalue;
				outputs = tempRef_outputs.argvalue;
				for (int ires = 0; ires < forms.length; ires++)
				{
					String lex0 = forms[ires] + "," + lemma + "," + category + outputs[ires]
							+ combineAllFeatures(lexfeatures);
					RefObject<ArrayList<String>> tempRef_lexemes = new RefObject<ArrayList<String>>(lexemes);
					Dic.normalizeLexicalEntry(lex0, this, tempRef_lexemes);
					lexemes = tempRef_lexemes.argvalue;
				}
			}
		}

		// derive
		String[] myfeaturesdrv = Dic.lookForAll("DRV", lexfeatures);
		if (myfeaturesdrv == null)
		{
			return lexemes;
		}

		String[] nonflxfeatures = Dic.getRidOfInflectionalFeatures(lexfeatures, this.prop_inf);
		if (nonflxfeatures == null)
		{
			return lexemes;
		}
		for (String myfeaturedrv : myfeaturesdrv)
		{
			String drvname, flxname;
			String expname2 = myfeaturedrv.substring((new String("DRV=")).length());
			int index = expname2.indexOf(':');
			if (index == -1)
			{
				drvname = expname2;
				flxname = expname;
			}
			else
			{
				drvname = expname2.substring(0, index);
				flxname = expname2.substring(index + 1);
			}
			Gram grm = this.paradigms.get(drvname);
			if (grm != null)
			{
				if (grm.vocabIn == null)
				{
					grm.prepareForParsing();
				}
				String[] forms = null, outputs = null;
				RefObject<String[]> tempRef_forms = new RefObject<String[]>(forms);
				RefObject<String[]> tempRef_outputs = new RefObject<String[]>(outputs);
				grm.inflect(Lan, lemma, tempRef_forms, tempRef_outputs, this.paradigms);
				forms = tempRef_forms.argvalue;
				outputs = tempRef_outputs.argvalue;

				StringBuilder initialinfo = new StringBuilder();
				for (String feat : nonflxfeatures)
				{
					if (!feat.equals("NW"))
					{
						initialinfo.append("+" + feat);
					}
				}

				for (int ires = 0; ires < forms.length; ires++) // inflect each derived form
				{
					if (flxname == null)
					{
						String res = forms[ires] + "," + lemma + ",";
						if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
						{
							res += outputs[ires].substring(1); // remove the initial "+"
						}
						else
						{
							res += outputs[ires];
						}

						// make info explicit and defactorized
						RefObject<ArrayList<String>> tempRef_lexemes2 = new RefObject<ArrayList<String>>(lexemes);
						Dic.normalizeLexicalEntry(res, this, tempRef_lexemes2);
						lexemes = tempRef_lexemes2.argvalue;
						continue;
					}
					Gram grm2 = this.paradigms.get(flxname);
					if (grm2 == null)
					{
						String res = forms[ires] + "," + lemma + ",";
						if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
						{
							res += outputs[ires].substring(1); // remove the initial "+"
						}
						else
						{
							res += outputs[ires];
						}

						// make info explicit and defactorized
						RefObject<ArrayList<String>> tempRef_lexemes3 = new RefObject<ArrayList<String>>(lexemes);
						Dic.normalizeLexicalEntry(res + combineAllFeatures(lexfeatures), this, tempRef_lexemes3);
						lexemes = tempRef_lexemes3.argvalue;
					}
					else
					{
						if (grm2.vocabIn == null)
						{
							grm2.prepareForParsing();
						}
						String[] forms2 = null, outputs2 = null;
						RefObject<String[]> tempRef_forms2 = new RefObject<String[]>(forms2);
						RefObject<String[]> tempRef_outputs2 = new RefObject<String[]>(outputs2);
						grm2.inflect(Lan, forms[ires], tempRef_forms2, tempRef_outputs2, this.paradigms); // inflection
																											// of the
																											// derived
																											// form
						forms2 = tempRef_forms2.argvalue;
						outputs2 = tempRef_outputs2.argvalue;
						if (forms2 != null)
						{
							for (int ires2 = 0; ires2 < forms2.length; ires2++)
							{
								String res = forms2[ires2] + "," + lemma + ",";
								if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
								{
									res += outputs[ires].substring(1); // remove the initial "+"
								}
								else
								{
									res += outputs[ires];
								}
								res += initialinfo.toString() + outputs2[ires2];

								// make info explicit and defactorized
								RefObject<ArrayList<String>> tempRef_lexemes4 = new RefObject<ArrayList<String>>(
										lexemes);
								Dic.normalizeLexicalEntry(res + combineAllFeatures(lexfeatures), this, tempRef_lexemes4);
								lexemes = tempRef_lexemes4.argvalue;
							}
						}
					}
				}
			}
		}
		return lexemes;
	}

	/**
	 * 
	 * @param sols
	 * @return
	 */
	public final ArrayList<String> inflectSolutions(ArrayList<String> sols)
	{
		ParameterCheck.mandatory("sols", sols);
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<String> lemmas = new ArrayList<String>();
		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<String> paradigms = new ArrayList<String>();

		for (int isols = 0; isols < sols.size(); isols++)
		{
			String lex = sols.get(isols);
			String entry = null, lemma = null, category = null, info = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String> tempRef_info = new RefObject<String>(info);

			boolean tempVar = !Dic.parseDELAF(lex, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_info);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			info = tempRef_info.argvalue;
			if (tempVar)
			{
				continue;
			}
			String myfeature = Dic.lookFor("FLX", info);
			if (myfeature != null)
			{
				String expname = myfeature.substring((new String("FLX=")).length());
				int index = -1;
				for (int i = 0; i < lemmas.size(); i++)
				{
					if (lemma.equals(lemmas.get(i)) && category.equals(categories.get(i))
							&& expname.equals(paradigms.get(i)))
					{
						index = i;
						break;
					}
				}
				if (index != -1) // already there
				{
					continue;
				}
				lemmas.add(lemma);
				categories.add(category);
				paradigms.add(expname);

				if (!this.paradigms.containsKey(expname))
				{
					continue;
				}
				Gram grm = this.paradigms.get(expname);
				if (grm == null)
				{
					continue;
				}
				if (grm.vocabIn == null)
				{
					grm.prepareForParsing();
				}
				String[] forms = null, outputs = null;
				RefObject<String[]> tempRef_forms = new RefObject<String[]>(forms);
				RefObject<String[]> tempRef_outputs = new RefObject<String[]>(outputs);

				grm.inflect(Lan, lemma, tempRef_forms, tempRef_outputs, this.paradigms);

				forms = tempRef_forms.argvalue;
				outputs = tempRef_outputs.argvalue;
				for (int ires = 0; ires < forms.length; ires++)
				{
					results.add(forms[ires]); // add word form

					// then add specific inflectional codes
					if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
					{
						results.add(outputs[ires].substring(1));
					}
					else
					{
						results.add(outputs[ires]);
					}
				}
			}

			String[] myfeatures = Dic.lookForAll("DRV", info);
			if (myfeatures != null)
			{
				for (String mydrv : myfeatures)
				{
					String expname = null;
					String flxname = null;
					String expname0 = mydrv.substring((new String("DRV=")).length());
					int icomb = expname0.indexOf(':');
					if (icomb != -1)
					{
						expname = expname0.substring(0, icomb);
						flxname = expname0.substring(icomb + 1);
					}
					else
					{
						expname = expname0;

						// get default inflection paradigm name
						flxname = myfeature.substring((new String("FLX=")).length());
					}

					// look for derivation paradigm name
					if (!this.paradigms.containsKey(expname))
					{
						continue;
					}

					Gram grm = this.paradigms.get(expname);
					if (grm == null)
					{
						continue;
					}
					if (grm.vocabIn == null)
					{
						grm.prepareForParsing();
					}

					String[] forms = null, outputs = null;
					RefObject<String[]> tempRef_forms2 = new RefObject<String[]>(forms);
					RefObject<String[]> tempRef_outputs2 = new RefObject<String[]>(outputs);

					grm.inflect(Lan, entry, tempRef_forms2, tempRef_outputs2, this.paradigms);

					forms = tempRef_forms2.argvalue;
					outputs = tempRef_outputs2.argvalue;
					if (forms == null || forms.length == 0)
					{
						continue;
					}

					for (int ires = 0; ires < forms.length; ires++)
					{
						// process derivation: we get rid of the initial category, and we take the new one
						String initialinfo = Dic.removeFeature("NW", info); // e.g. "V+tr+FLX=Manger+DRV=Mangeable"
						int index = initialinfo.indexOf('+');
						if (index != -1)
						{
							initialinfo = initialinfo.substring(index); // e.g. "+tr+FLX=Manger+DRV=Mangeable"
						}
						String newinfo;
						if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
						{
							newinfo = outputs[ires].substring(1); // to get rid of initial '+'
						}
						else
						{
							newinfo = outputs[ires];
						}
						if (flxname == null)
						{
							results.add(forms[ires]);
							results.add(newinfo);
						}
						else
						{
							Gram grm2 = this.paradigms.get(flxname);
							if (grm2 == null)
							{
								continue;
							}
							if (grm2.vocabIn == null)
							{
								grm2.prepareForParsing();
							}

							String[] dforms = null, doutputs = null;
							RefObject<String[]> tempRef_dforms = new RefObject<String[]>(dforms);
							RefObject<String[]> tempRef_doutputs = new RefObject<String[]>(doutputs);

							grm2.inflect(Lan, forms[ires], tempRef_dforms, tempRef_doutputs, this.paradigms);

							dforms = tempRef_dforms.argvalue;
							doutputs = tempRef_doutputs.argvalue;
							if (dforms == null || dforms.length == 0)
							{
								continue;
							}

							for (int i2res = 0; i2res < dforms.length; i2res++)
							{
								String lastinfo = doutputs[i2res];
								results.add(dforms[i2res]);
								results.add(newinfo + lastinfo);
							}
						}
					}
				}
			}
		}
		return results;
	}

	private transient HashMap<String, ArrayList<String>> lexforalldics;

	/**
	 * 
	 * @param sols
	 */
	private void filterOutComplexInfos(ArrayList<String> sols) // get rid of all complex lexical infos
	{
		for (int i = 0; i < sols.size();)
		{
			String entry = null, info = null; // information in the lexicon
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_info = new RefObject<String>(info);

			Dic.parseDELAS(sols.get(i), tempRef_entry, tempRef_info);

			entry = tempRef_entry.argvalue;
			info = tempRef_info.argvalue;
			if (isComplex(info))
			{
				sols.subList(i, i + 1).clear();
			}
			else
			{
				i++;
			}
		}
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public final ArrayList<String> lookupAllSDics(String token) // only true simple word dictionaries with no complex
	// infos
	{
		if (lexforalldics == null)
		{
			lexforalldics = new HashMap<String, ArrayList<String>>();
		}
		if (lexforalldics.containsKey(token))
		{
			return lexforalldics.get(token);
		}

		ArrayList<String> sols = null;
		for (int iprio = -9; iprio < 10; iprio++)
		{
			// dictionaries .jnod, .bin
			if (lexBins == null)
			{
				continue;
			}
			for (int idic = 0; idic < lexBins.size(); idic += 2)
			{
				FSDic lexBin = (FSDic) lexBins.get(idic);
				if ((Integer) lexBins.get(idic + 1) > iprio)
				{
					continue;
				}
				ArrayList<String> tmp;
				if (this.Lan.isoName.equals("ar") || this.Lan.isoName.equals("he"))
				{
					tmp = lexBin.lookUpSimpleSemitic(token, 0, this);
				}
				else
				{
					tmp = lexBin.lookUpSimple(token, 0, this);
				}
				if (tmp != null && tmp.size() > 0)
				{
					if (sols == null)
					{
						sols = new ArrayList<String>();
					}
					sols.addAll(tmp);
				}
			}
			if (sols != null)
			{
				filterOutComplexInfos(sols);
			}
			if (sols != null && sols.size() > 0)
			{
				break;
			}
		}

	
		lexforalldics.put(token, sols);
		return sols;
	}

	/**
	 * 
	 * @param op
	 * @param lexs
	 * @param text
	 * @param positions
	 * @param lan1
	 * @param beg
	 * @param end
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String[] computeDerivations(String op, ArrayList<String> lexs, String text, ArrayList<Double> positions,
			RefObject<Language> lan1, int beg, int end) throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatoryString("op", op);
		ParameterCheck.mandatory("lexs", lexs);
		ParameterCheck.mandatory("lan1", lan1);

		String errmessage = null;
		lan1.argvalue = null;
		String[] finalres = null;
		if (lexs.isEmpty())
		{
			return null;
		}

		ArrayList<String> results = new ArrayList<String>();
		for (int ilex = 0; ilex < lexs.size(); ilex++)
		{
			String entry = null, lemma = null, category = null;
			String[] features = null;
			RefObject<String> tempRef_entry = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
			RefObject<String> tempRef_category = new RefObject<String>(category);
			RefObject<String[]> tempRef_features = new RefObject<String[]>(features);

			boolean tempVar = !Dic.parseDELAFFeatureArray(lexs.get(ilex), tempRef_entry, tempRef_lemma,
					tempRef_category, tempRef_features);

			entry = tempRef_entry.argvalue;
			lemma = tempRef_lemma.argvalue;
			category = tempRef_category.argvalue;
			features = tempRef_features.argvalue;
			if (tempVar)
			{
				continue;
			}
			if (op.equals("_"))
			{
				results.add(lemma);
			}
			else if (op.length() > 1 && op.charAt(0) == '_')
			{
				// simple inflection/derivation
				if (!this.ResourcesLoaded)
				{
					// need to load .jnod dictionaries to get inflectional paradigms
					RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
					this.loadNodResources(this.preferences.ldic.get(this.Lan.isoName), tempRef_errmessage);
					errmessage = tempRef_errmessage.argvalue;
				}
				ArrayList<String> lexemes = inflectAndDerive(entry, lemma, category, features);
				if (lexemes == null || lexemes.size() == 0)
				{
					errmessage = "Cannot derive " + lemma;
					Dic.writeLog(errmessage);
					continue;
				}
				for (String lexeme : lexemes)
				{
					String[] listoffeatures = null;
					RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
					RefObject<String> tempRef_category2 = new RefObject<String>(category);
					RefObject<String[]> tempRef_listoffeatures = new RefObject<String[]>(listoffeatures);

					boolean tempVar2 = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry2, tempRef_lemma2,
							tempRef_category2, tempRef_listoffeatures);

					entry = tempRef_entry2.argvalue;
					lemma = tempRef_lemma2.argvalue;
					category = tempRef_category2.argvalue;
					listoffeatures = tempRef_listoffeatures.argvalue;
					if (tempVar2)
					{
						continue;
					}
					if (lexInfoMatchOperator(category, listoffeatures, op.substring(1)))
					{
						results.add(entry);
					}
				}
			}
			else if (op.length() > 1 && op.charAt(0) == '$') // op = "$FR" or "$FR_V+PP" or "$FR$Genre"
			{
				// get property name
				String op2 = op.substring(1); // op2 = "FR" or "FR_V+PP" or FR$VSup
				String op3, oplanguage, opfield, opconstraints;
				int index1 = op2.indexOf('_');
				if (index1 == -1) // op2 = "FR"
				{
					op3 = op2; // op3 = "FR"
					opconstraints = null;
				}
				else
				// op2 = "FR_V+PP"
				{
					op3 = op2.substring(0, index1); // oplanguage = "FR"
					opconstraints = op2.substring(index1 + 1); // opconstraints = "V+PP"
				}

				int index2 = op3.indexOf('$');
				if (index2 == -1) // op3 = "FR"
				{
					oplanguage = op3;
					opfield = null;
				}
				else
				// op3 = "FR$VSUP"
				{
					oplanguage = op3.substring(0, index2); // oplanguage = "FR"
					opfield = op3.substring(index2 + 1); // opconstraints = "VSUP"
				}

				if (oplanguage.equals("CAT"))
				{
					results.add(category);
					continue;
				}
				else if (oplanguage.equals("ENT"))
				{
					results.add(entry);
					continue;
				}
				else if (oplanguage.equals("ALLS"))
				{
					StringBuilder sb = new StringBuilder();
					if (features != null) // there might be no syntactic-semantic feature in lex
					{
						for (int i = 0; i < features.length; i++)
						{
							String feat = features[i];
							if (feat == null || feat.equals(""))
							{
								System.out.println("a feature is empty???");
								continue;
							}
							String propval = Dic.getPropertyValue(feat);
							if (isASyntacticFeature(propval) && !propval.equals("NW") && !propval.equals("FXC"))
							{
								sb.append("+" + feat);
							}
						}
					}
					results.add(sb.toString());
					continue;
				}
				else if (oplanguage.equals("ALLF"))
				{
					StringBuilder sb = new StringBuilder();
					if (features != null) // there might be no inflectional feature in lex
					{
						for (int i = 0; i < features.length; i++)
						{
							String feat = features[i];
							if (feat == null || feat.equals(""))
							{
								System.out.println("a feature is empty???");
								continue;
							}
							String propval = Dic.getPropertyValue(feat);
							if (isAninflectionalFeature(propval))
							{
								sb.append("+" + feat);
							}
						}
					}
					results.add(sb.toString());
					continue;
				}
				else if (features == null) // no feature in lex
				{
					continue;
				}

				// get the property values
				ArrayList<String> myfeatures = new ArrayList<String>();
				for (String feat : features)
				{
					String propname = null, propvalue = null;
					RefObject<String> tempRef_propname = new RefObject<String>(propname);
					RefObject<String> tempRef_propvalue = new RefObject<String>(propvalue);
					Dic.getPropertyNameValue(feat, tempRef_propname, tempRef_propvalue);
					propname = tempRef_propname.argvalue;
					propvalue = tempRef_propvalue.argvalue;
					if (oplanguage.equals(propname))
					{
						myfeatures.add(propvalue);
					}
				}
				if (index1 == -1 && index2 == -1)
				{
					// simple property value
					results.addAll(myfeatures);
					continue;
				}

				Language lan2 = null;
				if (oplanguage.length() == 2 && Character.isUpperCase(oplanguage.charAt(0))
						&& Character.isUpperCase(oplanguage.charAt(1)))
				{
					lan2 = new Language(oplanguage.toLowerCase());
					if (lan1.argvalue == null)
					{
						lan1.argvalue = lan2;
					}
				}
				if (lan2 == null)
				{
					// use current engine's language
					engine2 = this;
					lan2 = this.Lan;
				}
				else
				{
					// create a new engine for target language
					if (engine2 == null || !engine2.Lan.isoName.equals(lan2.isoName))
					{
						RefObject<Language> tempRef_lan2 = new RefObject<Language>(lan2);
						engine2 = new Engine(tempRef_lan2, this.applicationDir, this.docDir, this.projectDir,
								this.projectMode, preferences, this.BackgroundWorking, this.backgroundWorker);
						lan2 = tempRef_lan2.argvalue;
						RefObject<String> tempRef_errmessage2 = new RefObject<String>(errmessage);
						engine2.loadNodResources(this.preferences.ldic.get(lan2.isoName), tempRef_errmessage2);
						errmessage = tempRef_errmessage2.argvalue;
					}
					else
					{
						lan2 = engine2.Lan;
					}
				}

				// lookup the second linguistic resource and store the result
				ArrayList<String> lexs2;
				ArrayList<String> s_sols = null;
				for (String val : myfeatures)
				{
					if (!lan2.asianTokenizer)
					{
						s_sols = engine2.lookupAllLexsAndMorphsForSimples(val, true, null, 0);
						if (s_sols != null)
						{
							filterNonWords(s_sols); // filter out all non words +NW
							filterUnamb(s_sols); // filter all solutions but +UNAMB if there is one +UNAMB
						}
					}

					ArrayList<String> c_sols = engine2.lookupAllLexsAndMorphsForSimples(val, false, val, 0);
					if (c_sols != null && c_sols.size() > 0)
					{
						filterNonWords(c_sols); // filter out all non words +NW
						filterUnamb(c_sols); // filter all solutions but +UNAMB if there is one +UNAMB
						// Using copy-constructor instead of clone() - recommended because of unchecked class cast
						lexs2 = new ArrayList<String>(c_sols);
					}
					else
					{
						if (s_sols != null && s_sols.size() > 0)
						{
							// Using copy-constructor instead of clone() - recommended because of unchecked class cast
							lexs2 = new ArrayList<String>(s_sols);
						}
						else
						{
							lexs2 = null;
						}
					}

					if (lexs2 == null)
					{
						errmessage = "Cannot find " + val + " in target language " + lan2.isoName;
						Dic.writeLog(errmessage);
						continue;
					}
					for (int ilex2 = 0; ilex2 < lexs2.size(); ilex2 += 2)
					{
						String lex = lexs2.get(ilex2);
						RefObject<String> tempRef_entry3 = new RefObject<String>(entry);
						RefObject<String> tempRef_lemma3 = new RefObject<String>(lemma);
						RefObject<String> tempRef_category3 = new RefObject<String>(category);
						RefObject<String[]> tempRef_features2 = new RefObject<String[]>(features);

						boolean tempVar3 = !Dic.parseDELAFFeatureArray(lex, tempRef_entry3, tempRef_lemma3,
								tempRef_category3, tempRef_features2);

						entry = tempRef_entry3.argvalue;
						lemma = tempRef_lemma3.argvalue;
						category = tempRef_category3.argvalue;
						features = tempRef_features2.argvalue;
						if (tempVar3)
						{
							continue;
						}
						if (opfield != null)
						{
							// get the property value
							ArrayList<String> myfeatures2 = new ArrayList<String>();
							if (features != null)
							{
								for (String feat : features)
								{
									String propname = null, propvalue = null;
									RefObject<String> tempRef_propname2 = new RefObject<String>(propname);
									RefObject<String> tempRef_propvalue2 = new RefObject<String>(propvalue);
									Dic.getPropertyNameValue(feat, tempRef_propname2, tempRef_propvalue2);
									propname = tempRef_propname2.argvalue;
									propvalue = tempRef_propvalue2.argvalue;
									if (opfield.equals(propname))
									{
										myfeatures2.add(propvalue);
									}
								}
							}
							results.addAll(myfeatures2);
						}
						else
						{
							ArrayList<String> lexemes = engine2.inflectAndDerive(val, lemma, category, features);
							if (lexemes == null || lexemes.isEmpty())
							{
								errmessage = "Cannot derive '" + val + "' in target language '"
										+ lan2.isoName.toUpperCase() + "' to compute " + op;
								Dic.writeLog(errmessage);
								continue;
							}
							for (String lexeme : lexemes)
							{
								String[] listoffeatures = null;
								RefObject<String> tempRef_entry4 = new RefObject<String>(entry);
								RefObject<String> tempRef_lemma4 = new RefObject<String>(lemma);
								RefObject<String> tempRef_category4 = new RefObject<String>(category);

								RefObject<String[]> tempRef_listoffeatures2 = new RefObject<String[]>(listoffeatures);
								boolean tempVar4 = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry4, tempRef_lemma4,
										tempRef_category4, tempRef_listoffeatures2);

								entry = tempRef_entry4.argvalue;
								lemma = tempRef_lemma4.argvalue;
								category = tempRef_category4.argvalue;
								listoffeatures = tempRef_listoffeatures2.argvalue;
								if (tempVar4)
								{
									continue;
								}
								if (lexInfoMatchOperator(category, listoffeatures, opconstraints))
								{
									results.add(entry);
								}
							}
						}
					}
				}
			}
		}

		if (results.isEmpty())
		{
			return null;
			
		}
		else
		{
			// remove duplicates
			for (int i = 0; i < results.size(); i++)
			{
				String res = results.get(i);
				for (int j = i + 1; j < results.size();)
				{
					String res2 = results.get(j);
					if (res.equals(res2))
					{
						results.remove(j);
					}
					else
					{
						j++;
					}
				}
			}
		}
		finalres = new String[results.size()];
		results.toArray(finalres);
		return finalres;
	}

	/**
	 * 
	 * @param op
	 * @param lex
	 * @param lan1
	 * @param errmessage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private ArrayList<String> computeDerivations(String op, String lex, RefObject<Language> lan1,
			RefObject<String> errmessage) throws IOException, ClassNotFoundException
	{
		ParameterCheck.mandatoryString("op", op);
		ParameterCheck.mandatory("lan1", lan1);
		ParameterCheck.mandatory("errmessage", errmessage);

		errmessage.argvalue = null;
		lan1.argvalue = null;
		String entry = null, lemma = null, category = null;
		String[] features = null;
		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);
		boolean tempVar = !Dic.parseLexemeSymbol(lex, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);
		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		if (tempVar)
		{
			errmessage.argvalue = "<" + lex + ">" + "is not a valid lexeme";
			return null;
		}
		ArrayList<String> results = new ArrayList<String>();
		if (op.equals("_"))
		{
			results.add(lemma);
		}
		else if (op.length() > 1 && op.charAt(0) == '_')
		{
			// simple inflection/derivation
			if (!this.ResourcesLoaded)
			{
				// need to load .jnod dictionaries to get inflectional paradigms
				this.loadNodResources(this.preferences.ldic.get(this.Lan.isoName), errmessage);
			}
			ArrayList<String> lexemes = inflectAndDerive(entry, lemma, category, features);
			if (lexemes == null || lexemes.size() == 0)
			{
				errmessage.argvalue = "Cannot derive " + lemma;
				return null;
			}
			for (String lexeme : lexemes)
			{
				String[] listoffeatures = null;
				RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
				RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
				RefObject<String> tempRef_category2 = new RefObject<String>(category);
				RefObject<String[]> tempRef_listoffeatures = new RefObject<String[]>(listoffeatures);
				boolean tempVar2 = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry2, tempRef_lemma2,
						tempRef_category2, tempRef_listoffeatures);
				entry = tempRef_entry2.argvalue;
				lemma = tempRef_lemma2.argvalue;
				category = tempRef_category2.argvalue;
				listoffeatures = tempRef_listoffeatures.argvalue;
				if (tempVar2)
				{
					continue;
				}
				if (lexInfoMatchOperator(category, listoffeatures, op.substring(1)))
				{
					results.add(entry);
				}
			}
		}
		else if (op.length() > 1 && op.charAt(0) == '$') // op = "$FR" or "$FR_V+PP" or "$FR$Genre"
		{
			// get property name
			String op2 = op.substring(1); // op2 = "FR" or "FR_V+PP" or FR$VSup
			String op3, oplanguage, opfield, opconstraints;
			int index1 = op2.indexOf('_');
			if (index1 == -1) // op2 = "FR"
			{
				op3 = op2; // op3 = "FR"
				opconstraints = null;
			}
			else
			// op2 = "FR_V+PP"
			{
				op3 = op2.substring(0, index1); // oplanguage = "FR"
				opconstraints = op2.substring(index1 + 1); // opconstraints = "V+PP"
			}

			int index2 = op3.indexOf('$');
			if (index2 == -1) // op3 = "FR"
			{
				oplanguage = op3;
				opfield = null;
			}
			else
			// op3 = "FR$VSUP"
			{
				oplanguage = op3.substring(0, index2); // oplanguage = "FR"
				opfield = op3.substring(index2 + 1); // opconstraints = "VSUP"
			}

			if (oplanguage.equals("CAT"))
			{
				results.add(category);
				return results;
			}
			else if (oplanguage.equals("ENT"))
			{
				results.add(entry);
				return results;
			}
			else if (oplanguage.equals("ALLS"))
			{
				StringBuilder sb = new StringBuilder();
				if (features != null) // there might be no syntactic-semantic feature in lex
				{
					for (int i = 0; i < features.length; i++)
					{
						String feat = features[i];
						if (feat == null || feat.equals(""))
						{
							System.out.println("a feature is empty???");
							continue;
						}
						String propval = Dic.getPropertyValue(feat);
						if (isASyntacticFeature(propval) && !propval.equals("NW") && !propval.equals("FXC"))
						{
							sb.append("+" + feat);
						}
					}
				}
				results.add(sb.toString());
				return results;
			}
			else if (oplanguage.equals("ALLF"))
			{
				StringBuilder sb = new StringBuilder();
				if (features != null) // there might be no inflectional feature in lex
				{
					for (int i = 0; i < features.length; i++)
					{
						String feat = features[i];
						if (feat == null || feat.equals(""))
						{
							System.out.println("a feature is empty???");
							continue;
						}
						String propval = Dic.getPropertyValue(feat);
						if (isAninflectionalFeature(propval))
						{
							sb.append("+" + feat);
						}
					}
				}
				results.add(sb.toString());
				return results;
			}
			else if (features == null) // no feature in lex
			{
				return results;
			}

			// get the property values
			ArrayList<String> myfeatures = new ArrayList<String>();
			for (String feat : features)
			{
				String propname = null, propvalue = null;
				RefObject<String> tempRef_propname = new RefObject<String>(propname);
				RefObject<String> tempRef_propvalue = new RefObject<String>(propvalue);
				Dic.getPropertyNameValue(feat, tempRef_propname, tempRef_propvalue);
				propname = tempRef_propname.argvalue;
				propvalue = tempRef_propvalue.argvalue;
				if (oplanguage.equals(propname))
				{
					myfeatures.add(propvalue);
				}
			}
			if (index1 == -1 && index2 == -1)
			{
				// simple property value
				results.addAll(myfeatures);
				return results;
			}

			Language lan2 = null;
			if (oplanguage.length() == 2 && Character.isUpperCase(oplanguage.charAt(0))
					&& Character.isUpperCase(oplanguage.charAt(1)))
			{
				lan2 = new Language(oplanguage.toLowerCase());
				if (lan1.argvalue == null)
				{
					lan1.argvalue = lan2;
				}
			}
			if (lan2 == null)
			{
				// use current engine's language
				engine2 = this;
				lan2 = this.Lan;
			}
			else
			{
				// create a new engine for target language
				if (engine2 == null || !engine2.Lan.isoName.equals(lan2.isoName))
				{
					RefObject<Language> tempRef_lan2 = new RefObject<Language>(lan2);
					engine2 = new Engine(tempRef_lan2, this.applicationDir, this.docDir, this.projectDir,
							this.projectMode, preferences, this.BackgroundWorking, this.backgroundWorker);
					lan2 = tempRef_lan2.argvalue;
					engine2.loadNodResources(this.preferences.ldic.get(lan2.isoName), errmessage);
				}
				else
				{
					lan2 = engine2.Lan;
				}
			}

			// lookup the second linguistic resource and store the result
			ArrayList<String> lexs2;
			ArrayList<String> s_sols = null;
			for (String val : myfeatures)
			{
				if (!lan2.asianTokenizer)
				{
					s_sols = engine2.lookupAllLexsAndMorphsForSimples(val, true, null, 0);
					if (s_sols != null)
					{
						filterNonWords(s_sols); // filter out all non words +NW
						filterUnamb(s_sols); // filter all solutions but +UNAMB if there is one +UNAMB
					}
				}
				@SuppressWarnings("unused")
				boolean thereisunamb = false;
				ArrayList<String> c_sols = engine2.lookupAllLexsForCompounds(0, val);
				if (c_sols != null && c_sols.size() > 0)
				{
					filterNonWords(c_sols); // filter out all non words +NW
					thereisunamb = filterUnamb(c_sols); // filter all solutions but +UNAMB if there is one +UNAMB
					lexs2 = (ArrayList<String>) c_sols.clone();
				}
				else
				{
					if (s_sols != null && s_sols.size() > 0)
					{
						lexs2 = (ArrayList<String>) s_sols.clone();
					}
					else
					{
						lexs2 = null;
					}
				}

				if (lexs2 == null)
				{
					errmessage.argvalue = "Cannot find " + val + " in target language " + lan2.isoName;
					Dic.writeLog(errmessage.argvalue);
					return null;
				}
				for (int ilex2 = 0; ilex2 < lexs2.size(); ilex2 += 2)
				{
					String lex2 = lexs2.get(ilex2);
					RefObject<String> tempRef_entry3 = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma3 = new RefObject<String>(lemma);
					RefObject<String> tempRef_category3 = new RefObject<String>(category);
					RefObject<String[]> tempRef_features2 = new RefObject<String[]>(features);
					boolean tempVar3 = !Dic.parseDELAFFeatureArray(lex2, tempRef_entry3, tempRef_lemma3,
							tempRef_category3, tempRef_features2);
					entry = tempRef_entry3.argvalue;
					lemma = tempRef_lemma3.argvalue;
					category = tempRef_category3.argvalue;
					features = tempRef_features2.argvalue;
					if (tempVar3)
					{
						continue;
					}
					if (opfield != null)
					{
						// get the property value
						ArrayList<String> myfeatures2 = new ArrayList<String>();
						if (features != null)
						{
							for (String feat : features)
							{
								String propname = null, propvalue = null;
								RefObject<String> tempRef_propname2 = new RefObject<String>(propname);
								RefObject<String> tempRef_propvalue2 = new RefObject<String>(propvalue);
								Dic.getPropertyNameValue(feat, tempRef_propname2, tempRef_propvalue2);
								propname = tempRef_propname2.argvalue;
								propvalue = tempRef_propvalue2.argvalue;
								if (opfield.equals(propname))
								{
									myfeatures2.add(propvalue);
								}
							}
						}
						results.addAll(myfeatures2);
					}
					else
					{
						ArrayList<String> lexemes = engine2.inflectAndDerive(val, lemma, category, features);
						if (lexemes == null || lexemes.isEmpty())
						{
							errmessage.argvalue = "Cannot derive '" + val + "' in target language '"
									+ lan2.isoName.toUpperCase() + "' to compute " + op;
							Dic.writeLog(errmessage.argvalue);
							continue;
						}
						for (String lexeme : lexemes)
						{
							String[] listoffeatures = null;
							RefObject<String> tempRef_entry4 = new RefObject<String>(entry);
							RefObject<String> tempRef_lemma4 = new RefObject<String>(lemma);
							RefObject<String> tempRef_category4 = new RefObject<String>(category);
							RefObject<String[]> tempRef_listoffeatures2 = new RefObject<String[]>(listoffeatures);
							boolean tempVar4 = !Dic.parseDELAFFeatureArray(lexeme, tempRef_entry4, tempRef_lemma4,
									tempRef_category4, tempRef_listoffeatures2);
							entry = tempRef_entry4.argvalue;
							lemma = tempRef_lemma4.argvalue;
							category = tempRef_category4.argvalue;
							listoffeatures = tempRef_listoffeatures2.argvalue;
							if (tempVar4)
							{
								continue;
							}
							if (lexInfoMatchOperator(category, listoffeatures, opconstraints))
							{
								results.add(entry);
							}
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * 
	 * @param lexeme
	 * @param constraint
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final String[] computeDerivations(String lexeme, String constraint) throws IOException,
			ClassNotFoundException
	{
		ArrayList<String> results = new ArrayList<String>();
		String entry = null, lemma = null, category = null;
		String[] features = null;

		RefObject<String> tempRef_entry = new RefObject<String>(entry);
		RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
		RefObject<String> tempRef_category = new RefObject<String>(category);
		RefObject<String[]> tempRef_features = new RefObject<String[]>(features);

		Dic.parseLexemeSymbol(lexeme, tempRef_entry, tempRef_lemma, tempRef_category, tempRef_features);

		entry = tempRef_entry.argvalue;
		lemma = tempRef_lemma.argvalue;
		category = tempRef_category.argvalue;
		features = tempRef_features.argvalue;
		// simple inflection/derivation
		if (!this.ResourcesLoaded)
		{
			// need to load .jnod dictionaries to get inflectional paradigms
			String errmessage = null;
			RefObject<String> tempRef_errmessage = new RefObject<String>(errmessage);
			this.loadNodResources(this.preferences.ldic.get(this.Lan.isoName), tempRef_errmessage);
			errmessage = tempRef_errmessage.argvalue;
			if (errmessage != null)
			{
				return null;
			}
		}
		ArrayList<String> lexemes = inflectAndDerive(entry, lemma, category, features);
		if (lexemes == null || lexemes.isEmpty())
		{
			return null;
		}
		for (String lex : lexemes)
		{
			String[] listoffeatures = null;
			RefObject<String> tempRef_entry2 = new RefObject<String>(entry);
			RefObject<String> tempRef_lemma2 = new RefObject<String>(lemma);
			RefObject<String> tempRef_category2 = new RefObject<String>(category);
			RefObject<String[]> tempRef_listoffeatures = new RefObject<String[]>(listoffeatures);

			boolean tempVar = !Dic.parseDELAFFeatureArray(lex, tempRef_entry2, tempRef_lemma2, tempRef_category2,
					tempRef_listoffeatures);

			entry = tempRef_entry2.argvalue;
			lemma = tempRef_lemma2.argvalue;
			category = tempRef_category2.argvalue;
			listoffeatures = tempRef_listoffeatures.argvalue;
			if (tempVar)
			{
				continue;
			}
			if (listoffeatures != null)
			{
				if (lexInfoMatchOperator(category, listoffeatures, constraint))
				{
					results.add("<" + lex + ">");
				}
			}
		}

		if (results.isEmpty())
		{
			return null;
		}
		else
		{
			// remove duplicates
			for (int i = 0; i < results.size(); i++)
			{
				String res = results.get(i);
				for (int j = i + 1; j < results.size();)
				{
					String res2 = results.get(j);
					if (res.equals(res2))
					{
						results.remove(j);
					}
					else
					{
						j++;
					}
				}
			}
		}
		String[] finalres = new String[results.size()];
		results.toArray(finalres);
		return finalres;
	}

	/**
	 * 
	 * @param textbuffer
	 * @param tunb
	 * @param absolutebegaddress
	 * @param absoluteendaddress
	 * @param absaddresses
	 * @param output
	 * @param keepLexemes
	 * @return
	 */
	public final ArrayList<Object> mergeIntoAnnotations(String textbuffer, int tunb, double absolutebegaddress,
			double absoluteendaddress, ArrayList<Double> absaddresses, ArrayList<String> output, boolean keepLexemes)
	{
		ParameterCheck.mandatoryString("textbuffer", textbuffer);
		if (output == null || output.isEmpty() || absaddresses == null || absaddresses.isEmpty())
		{
			return null;
		}

		ArrayList<Object> annotations = new ArrayList<Object>();

		// compute global soutput
		StringBuilder sboutput = new StringBuilder();
		for (int iout = 0; iout < output.size(); iout++)
		{
			if (output.get(iout) != null)
			{
				sboutput.append(output.get(iout));
			}
		}
		String soutput = sboutput.toString();
		annotations.add(tunb);
		annotations.add(soutput);
		annotations.add(absolutebegaddress);
		annotations.add(absoluteendaddress);

		int reclevel;
		// compute each <...> annotation
		for (int i = 0; i < output.size() && i < absaddresses.size(); i++)
		{
			String out1 = output.get(i);
			if (out1 == null || out1.equals(""))
			{
				continue;
			}
			if (out1.charAt(0) == '<') 
			{
				if (!keepLexemes)
				{
					// we compute the address of the beginning
					double beg = absaddresses.get(i);
					if (beg < textbuffer.length() && beg > 0.0 && Character.isWhitespace(textbuffer.charAt((int) beg)))
					{
						while (beg < textbuffer.length() && Character.isWhitespace(textbuffer.charAt((int) beg)))
						{
							beg++;
						}
					}
					reclevel = 1;
					for (int iout1 = 1; iout1 < out1.length(); iout1++)
					{
						if (out1.charAt(iout1) == '>')
						{
							reclevel--;
						}
						else if (out1.charAt(iout1) == '<')
						{
							reclevel++;
						}
					}
					if (reclevel == 0)
					{
						// found a FILTER annotation: we keep the < >
						annotations.add(beg);
						annotations.add(out1);
						annotations.add(beg);
					}
					else
					{
						reclevel = 1;
						// compute the full < ... > => annotation to be inserted in the text mft
						String outputannotation = out1.substring(1);
						int j;
						for (j = i + 1; j < output.size() && j < absaddresses.size(); j++)
						{
							String out2 = output.get(j);
							if (out2 == null || out2.equals(""))
							{
								continue;
							}

							if (out2.charAt(0) == '<')
							{
								reclevel++;
							}
							if (out2.charAt(out2.length() - 1) == '>') // found an annotation from i to j
							{
								reclevel--;
								if (reclevel == 0)
								{
									annotations.add(beg);

									// we get rid of the >
									outputannotation += out2.substring(0, out2.length() - 1);
									annotations.add(outputannotation);

									annotations.add((double) absaddresses.get(j));
									break;
								}
							}
							else if (reclevel == 1)
							{
								outputannotation += out2;
							}
						}
					}
				}
				else
				// keep lexemes
				{
					// we compute the address of the beginning
					double beg = absaddresses.get(i);
					if (beg < textbuffer.length() && beg > 0.0 && Character.isWhitespace(textbuffer.charAt((int) beg)))
					{
						while (beg < textbuffer.length() && Character.isWhitespace(textbuffer.charAt((int) beg)))
						{
							beg++;
						}
					}

					out1 = out1.substring(0, out1.length() - 1); // out1 == "<Phrase#" or "<the,the,DET>#" => get rid of
																	// final "#"
					reclevel = 1;
					for (int iout1 = 1; iout1 < out1.length(); iout1++)
					{
						if (out1.charAt(iout1) == '>')
						{
							reclevel--;
						}
						else if (out1.charAt(iout1) == '<')
						{
							reclevel++;
						}
					}
					if (reclevel == 0)
					{
						// found a lexeme: we keep the < >
						annotations.add(beg);
						annotations.add(out1);
						annotations.add(beg);
					}
					else
					{
						reclevel = 1;
						// compute the full < ... > => annotation to be inserted in the text mft
						String outputannotation = out1.substring(1);
						int j;
						for (j = i + 1; j < output.size() && j < absaddresses.size(); j++)
						{
							String out2 = output.get(j);
							out2 = out2.substring(0, out2.length() - 1);
							if (out2 == null || out2.equals(""))
							{
								continue;
							}

							if (out2.charAt(0) == '<')
							{
								if (out2.charAt(out2.length() - 1) != '>')
								{
									reclevel++;
								}
							}
							else if (out2.charAt(0) == '>') // found an structure from i to j
							{
								reclevel--;
								if (reclevel == 0)
								{
									annotations.add(beg);

									// we get rid of the >
									outputannotation += out2.substring(0, out2.length() - 1);
									annotations.add(outputannotation);

									annotations.add((double) absaddresses.get(j));
									break;
								}
							}
							else if (reclevel == 1 && out2.charAt(0) != ':')
							{
								outputannotation += out2;
							}
						}
					}
				}
			}
		}
		return annotations;
	}

	/**
	 * 
	 * @param buffer
	 * @param newbuffer
	 * @return
	 */
	private ArrayList<Object> addXmlAnnotations(String buffer, RefObject<String> newbuffer)
	{
		ParameterCheck.mandatoryString("buffer", buffer);
		ParameterCheck.mandatory("newbuffer", newbuffer);

		// compute regexps to recognize all XML tags
		String begpat = "<[^/][^ >]*( |[^>])*>";
		String endpat = "</[^>]*>";
		Pattern compileBegPat = Pattern.compile(begpat, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);
		Pattern compileEndPat = Pattern.compile(endpat, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);
		Matcher matcherBegPat = compileBegPat.matcher(buffer);

		StringBuilder sbuffer = new StringBuilder(buffer);
		ArrayList<Object> tags = null;

		while (matcherBegPat.find())
		{
			Matcher matcherEndPat = compileEndPat.matcher(buffer);
			String begTag = matcherBegPat.group();
			String xmlTag = begTag;
			int startIndexOfBeg = matcherBegPat.start();
			int endIndexOfBeg = matcherBegPat.end();
			int startIndexOfEnd = 0;
			int endIndexOfEnd = 0;
			String endTag = null;
			begTag = begTag.substring(1, begTag.length() - 1);
			int ibeg = begTag.indexOf(' ');
			if (ibeg != -1)
				begTag = begTag.substring(0, ibeg);
			// get corresponding endMatch
			int endFound = -1;
			int jj = 0;
			while (matcherEndPat.find())
			{
				endTag = matcherEndPat.group();
				startIndexOfEnd = matcherEndPat.start();
				endIndexOfEnd = matcherEndPat.end();

				if (startIndexOfEnd < endIndexOfBeg)
				{
					continue;
				}
				endTag = endTag.substring(1, endTag.length() - 1);
				if (endTag.equals("/" + begTag))
				{
					endFound = jj;
					break;
				}
				jj++;
			}
			if (endFound == -1)
			{
				break;
			}
			if (tags == null)
			{
				tags = new ArrayList<Object>();
			}
		
			double ipos = endIndexOfBeg;
			RefObject<Double> tempRef_ipos = new RefObject<Double>(ipos);
			Grammar.skipSpaces(buffer, tempRef_ipos, true);
			ipos = tempRef_ipos.argvalue;
			double len = startIndexOfEnd - ipos;
			if (len > 0)
			{
				String sinput = Dic.cleanupXmlEntry(buffer.substring((int) ipos, (int) ipos + (int) len));
				String mcategory = null;
				String xmltag = xmlTag;
				RefObject<String> tempRef_mcategory = new RefObject<String>(mcategory);
				String label = Dic.cleanupXmlInfo(xmltag.substring(1, 1 + xmltag.length() - 2), tempRef_mcategory);
				mcategory = tempRef_mcategory.argvalue;
				if (mcategory.equals("LU") && label.length() > 1)
				{
					
					String entry = null, lemma = null, category = null, features = null;
					RefObject<String> tempRef_entry = new RefObject<String>(entry);
					RefObject<String> tempRef_lemma = new RefObject<String>(lemma);
					RefObject<String> tempRef_category = new RefObject<String>(category);
					RefObject<String> tempRef_features = new RefObject<String>(features);

					boolean tempVar = !Dic.parseXmlInfo(label, tempRef_entry, tempRef_lemma, tempRef_category,
							tempRef_features);

					entry = tempRef_entry.argvalue;
					lemma = tempRef_lemma.argvalue;
					category = tempRef_category.argvalue;
					features = tempRef_features.argvalue;
					if (tempVar)
					{
						continue;
					}
					if (category.equals("W") || category.equals("L") || category.equals("U") || category.equals("P")
							|| category.equals("D") || category.equals("UPP") || category.equals("LOW")
							|| category.equals("CAP") || category.equals("NB"))
					{
						// special NooJ codes: we add suffix "XML"
						category += "XML";
					}
					String lexeme;
					if (entry == null)
					{
						entry = sinput;
					}
					lexeme = entry + ",";
					if (lemma == null)
					{
						lexeme += entry + "," + category;
					}
					else
					{
						lexeme += lemma + "," + category;
					}
					if (features != null)
					{
						lexeme += "+" + features;
					}

					tags.add(ipos);
					tags.add(lexeme);
					tags.add(len);
				}
				else
				// import a syntactic annotation
				{
					if (mcategory.equals("W") || mcategory.equals("L") || mcategory.equals("U")
							|| mcategory.equals("P") || mcategory.equals("D") || mcategory.equals("UPP")
							|| mcategory.equals("LOW") || mcategory.equals("CAP") || mcategory.equals("NB"))
					{
						// special NooJ codes: we add suffix "XML"
						mcategory += "XML";
					}
					tags.add(ipos);
					tags.add(sinput + ",SYNTAX," + mcategory + label);
					tags.add(len);
				}

				// delete XML tags from text buffer
				int begMatchLength = endIndexOfBeg - startIndexOfBeg;
				for (int j = 0; j < begMatchLength; j++)
				{
					sbuffer.setCharAt(startIndexOfBeg + j, '\0');
				}
				int endMatchLength = endIndexOfEnd - startIndexOfEnd;
				for (int j = 0; j < endMatchLength; j++)
				{
					sbuffer.setCharAt(startIndexOfEnd + j, '\0');
				}
			}

		}
		newbuffer.argvalue = sbuffer.toString();
		return tags;
	}

	/**
	 * 
	 * 
	 * @param corpus
	 * @param text
	 * @param annotations
	 * @return
	 */
	final boolean addAllXmlAnnotations(Corpus corpus, Ntext text, ArrayList<Object> annotations)
	{
		ParameterCheck.mandatory("text", text);

		HashMap<String, Integer> hCorpusPhrases;
		if (corpus == null)
		{
			hCorpusPhrases = null;
		}
		else
		{
			hCorpusPhrases = corpus.hPhrases;
		}
	

		StringBuilder alltext = new StringBuilder(text.buffer);

		// main loop: parse each text unit
		for (int itu = 1; itu <= text.nbOfTextUnits; itu++)
		{
			if (BackgroundWorking)
			{
				if (backgroundWorker.isCancellationPending())
				{
					return false;
				}
				if (corpus == null)
				{
					if (backgroundWorker.isBusy())
					{
						backgroundWorker.reportProgress(0);
					}
				}
			}
			CurrentLine = text.buffer.substring(text.mft.tuAddresses[itu], text.mft.tuAddresses[itu]
					+ text.mft.tuLengths[itu]);

			String newcurrentline = null;
			RefObject<String> tempRef_newcurrentline = new RefObject<String>(newcurrentline);

			ArrayList<Object> sols = addXmlAnnotations(CurrentLine, tempRef_newcurrentline);

			newcurrentline = tempRef_newcurrentline.argvalue;

			if (sols != null && sols.size() > 0)
			{
				for (int ichar = 0; ichar < text.mft.tuLengths[itu]; ichar++)
				{
					alltext.setCharAt(text.mft.tuAddresses[itu] + ichar, newcurrentline.charAt(ichar));
				}
				for (int i = 0; i < sols.size(); i += 3)
				{
					int startaddress = ((Double) (sols.get(i))).intValue();
					String output = (String) sols.get(i + 1);
					int ichar = ((Double) (sols.get(i))).intValue() + ((Double) (sols.get(i + 2))).intValue();
					for (; ichar < CurrentLine.length() && Character.isWhitespace(CurrentLine.charAt(ichar)); ichar++)
						;
					int endaddress = ichar;
					if (corpus != null)
					{
						this.addLexemeToCorpus(annotations, hCorpusPhrases, output, text.mft, itu, startaddress,
								endaddress);
					}
					else
					{
						this.addLexemeToText(annotations, text.hPhrases, output, text.mft, itu, startaddress,
								endaddress);
					}
				}
			}
		}
		text.buffer = alltext.toString();

		// now manage the shifts when removing XML tags
		alltext = new StringBuilder();
		int[] shift = new int[text.buffer.length()];

		int currentshift = 0;
		for (int i = 0; i < text.buffer.length(); i++)
		{
			shift[i] = currentshift;
			if (text.buffer.charAt(i) != '\0')
			{
				alltext.append(text.buffer.charAt(i));
				currentshift++;
			}
		}
		text.mft.shiftAllTransitions(shift);
		text.buffer = alltext.toString();

		return true;
	}
}
