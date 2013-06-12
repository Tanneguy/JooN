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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.nooj4nlp.controller.DictionaryDialog.DictionaryDialogController;
import net.nooj4nlp.engine.helper.ParameterCheck;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * This class provides static utility methods for dictionary manipulation.
 * 
 * @author Silberztein Max
 */
public class Dictionary
{
	private static int USE_PREFIX_LENGTH = "#use ".length();
	private static int FLX_PREFIX_LENGTH = "FLX=".length();
	private static int DRV_PREFIX_LENGTH = "DRV=".length();

	public static StringBuilder errMessage = null;

	/**
	 * Reads language name from file header.
	 * 
	 * @param filePath
	 *            source file path
	 * @return language name if found on specified position in file, otherwise <code>null</code>
	 * @throws IOException
	 * @throws IOException
	 *             if file not found, or I/O error occurs during the operation
	 */
	public static String getLanguage(String filePath) throws IOException
	{
		ParameterCheck.mandatoryString("filePath", filePath);

		BufferedReader reader = null;
		String line = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
			

			// read header and get language
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine(); // language
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		if (line == null || line.length() < 21)
		{
			return null;
		}
		return line.substring(21);
	}

	public static boolean inflect(String fullname, String resName, boolean checkagreement, Language lan,
			Preferences preferences) throws IOException
	{
		if (Launcher.preferences.ldic.get(lan.isoName) == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find any lex resource for " + lan.isoName,
					"NooJ", JOptionPane.INFORMATION_MESSAGE);
		}

		RefObject<Language> lanRef = new RefObject<Language>(lan);
		Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		lan = lanRef.argvalue;

		StringBuilder errMessage = new StringBuilder();
		int nbOfEntries = DictionaryDialogController.count(fullname);
		String dname = org.apache.commons.io.FilenameUtils.getFullPath(fullname); // directory where I will find the
																					// inflectional desc. files

		// first load all inflections/derivational paradigms
		Grammar grammar = null;
		BufferedReader sr = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		OutputStreamWriter sw = null;
		try
		{
			sw = new OutputStreamWriter(new FileOutputStream(resName));
			Dic.initLoad(sw, lan.isoName);
			sw.write("# This dictionary was automatically built from "
					+ org.apache.commons.io.FilenameUtils.getName(fullname) + "\n");
			sw.write("#\n");
		}
		catch (IOException e)
		{
			if (sr != null)
				sr.close();
			if (sw != null)
				sw.close();

			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		int itu = 0;
		int progressPercentage = 0;

		if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				sr.close();
				sw.close();
				return false;
			}
			Launcher.progressMessage = "1/2 Compiling Paradigms...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		for (String line0 = sr.readLine(); line0 != null; line0 = sr.readLine())
		{
			line0 = line0.trim();
			if (line0.equals(""))
				continue;
			if (line0.startsWith("#use "))
			{
				// load corresponding inflectional information
				String fname = line0.substring(USE_PREFIX_LENGTH);
				fname = fname.trim();
				String flxname = dname + fname;
				String ext = "." + org.apache.commons.io.FilenameUtils.getExtension(flxname);
				File file = new File(flxname);
				if (!file.exists())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "cannot find file " + flxname,
							"NooJ: file does not exist", JOptionPane.INFORMATION_MESSAGE);
					continue;
				}
				sw.write(line0 + "\n");
				Grammar cgrammar;
				if ((ext.equals(".nof")) || ext.equals(".grm") || (ext.equals(".NOF") || ext.equals(".GRM")))
				{
					boolean istextual = Grammar.isItTextual(flxname);
					if (istextual)
					{
						String errmessage = null;
						RefObject<String> errmessage_Ref = new RefObject<String>(errmessage);
						cgrammar = Grammar.loadTextual(flxname, GramType.FLX, errmessage_Ref);
						errmessage = errmessage_Ref.argvalue;
						if (errmessage != null)
						{
							if (sr != null)
								sr.close();
							if (sw != null)
								sw.close();

							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Problem in grammar " + flxname
									+ ":\n" + errmessage, "NooJ: Cannot handle grammar",
									JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
					else
						
						cgrammar = Grammar.loadONooJGrammar(flxname);
					if (cgrammar == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load grammar " + flxname,
								"NooJ", JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						sw.close();
						continue;
					}
					if (cgrammar.gramType != GramType.FLX)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), flxname
								+ " is not an inflectional/derivational description", "NooJ: invalid grammar type",
								JOptionPane.INFORMATION_MESSAGE);
						continue;
					}
					cgrammar.compileAll(null);
					if (grammar == null)
						grammar = cgrammar;
					else
						grammar.addGrams(cgrammar);
				}
				else if ((ext.equals(".def")) || (ext.equals(".DEF")))
				{
					JOptionPane
							.showMessageDialog(
									null,
									"In NooJ V4, the only property defininition file is stored in Lexical Analysis\\_properties.def",
									"NooJ V4", JOptionPane.INFORMATION_MESSAGE);
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							"In NooJ V4, there is no more support for the obsolete command \"#use properties.def\"",
							"NooJ V4", JOptionPane.INFORMATION_MESSAGE);
					sr.close();

					// StreamWriter should be closed, and there is no point in saving the empty file on disk?
					sw.close();
					File file1 = new File(resName);
					file1.delete();
					return false;
				}
				else if ((ext.equals(".flx")) || (ext.equals(".FLX")))
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							"In NooJ V4, the only valid inflection/derivation files are stored in .nof files",
							"NooJ V4", JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					// StreamWriter should be closed?
					sw.close();
					return false;
				}
				else
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File " + flxname
							+ " has an incorrect extension", "NooJ: cannot handle extension " + ext,
							JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					// StreamWriter should be closed?
					sw.close();
					return false;
				}
			}
		}

		if (grammar == null || grammar.grams == null || grammar.grams.size() == 0)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
					"Dictionary has no associated inflectional/derivational description", "NooJ",
					JOptionPane.INFORMATION_MESSAGE);
			sr.close();
			sw.close();
			return false;
		}

		// now inflect all dictionary entries
	if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				sr.close();
				sw.close();
				return false;
			}
			Launcher.progressMessage = "2/2 Processing Inflection...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		int nbofinflectedforms = 0;
		sr.close();

		sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));

		sw.write("#\n");
		int nboferrors = 0;
		for (String line = sr.readLine(); line != null; line = sr.readLine())
		{
			line = line.trim();

			String entry = null, category = null, lemma = null, info = null;
			if (line.equals("") || line.charAt(0) == '#')
			{
				continue;
			}
			itu++;
			if (itu % 1000 == 0)
			{
				
			}

		if (Launcher.multithread)
			{
				if (Launcher.backgroundWorker.isCancellationPending())
				{
					sr.close();
					sw.close();
					return false;
				}
				int nprogress = (int) (itu * 100.0F / nbOfEntries);
				if (nprogress != progressPercentage)
				{
					progressPercentage = nprogress;
					if (Launcher.backgroundWorker.isBusy())
						Launcher.backgroundWorker.reportProgress(nprogress);
				}
			}

			int nbofcommas = Dic.nbOfCommas(line);
			if (nbofcommas == 1)
			{
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String> infoRef = new RefObject<String>(info);
				boolean tmp = Dic.parseDELAS(line, entryRef, categoryRef, infoRef);
				entry = entryRef.argvalue;
				category = categoryRef.argvalue;
				info = infoRef.argvalue;
				if (!tmp)
				{
					boolean tmp1 = Dic.parseContracted(line, entryRef, infoRef);
					entry = entryRef.argvalue;
					info = infoRef.argvalue;

					if (!tmp1)
					{
						errMessage.append("\n* Invalid dictionary line: " + line);
						nboferrors++;
						if (nboferrors > 100)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									"Too many errors while trying to compress dictionary", "NooJ: cannot compile",
									JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							sw.close();
							return false;
						}
						continue;
					}
					category = null;
					lemma = entry;
				}
				else
					lemma = entry;
			}
			else if (nbofcommas == 2)
			{
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> lemmaRef = new RefObject<String>(lemma);
				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String> infoRef = new RefObject<String>(info);
				boolean tmp = Dic.parseDELAF(line, entryRef, lemmaRef, categoryRef, infoRef);
				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				info = infoRef.argvalue;
				if (!tmp)
				{
					errMessage.append("\n* Invalid dictionary line: " + line);
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compress dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						sw.close();
						return false;
					}
					continue;
				}
				if (lemma == null || lemma.equals(""))
				{
					errMessage.append("\n* No lemma in line: " + line);
					nboferrors++;
					if (nboferrors > 100)
					{
						sr.close();
						sw.close();
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compress dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						return false;
					}
					continue;
				}
			}
			else
			{
				errMessage.append("* Cannot parse line: " + line + " (nb of commas " + nbofcommas + " is invalid)\n");
				Dic.writeLog("Cannot parse line: " + line + " (nb of commas " + nbofcommas + " is invalid)");
				nboferrors++;
				if (nboferrors > 100)
				{
					sr.close();
					sw.close();
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							"Too many errors while trying to compress dictionary", "NooJ: cannot compile",
							JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				continue;
			}

			// get inflectional class
			String myfeature = Dic.lookFor("FLX", info);
			if (myfeature == null)
			{
				// no inflection
				String res = entry + "," + lemma + "," + (category == null ? "" : category)
						+ (info == null ? "" : info);
				sw.write(res + "\n");
				nbofinflectedforms++;
			}
			else
			{
				String expname = myfeature.substring(FLX_PREFIX_LENGTH);
				if (grammar == null || grammar.grams == null || !grammar.grams.containsKey(expname))
				{
					errMessage.append("\n* Cannot find inflectional rule \"" + expname + "\" for lexical entry: "
							+ line);
					continue;
				}
				Gram grm = grammar.grams.get(expname);
				if (grm == null)
				{
					errMessage.append("\n* Cannot load inflectional rule \"" + expname + "\" for lexical entry: "
							+ line);
					continue;
				}
				String[] forms = null, outputs = null;
				RefObject<String[]> formsRef = new RefObject<String[]>(forms);
				RefObject<String[]> outputsRef = new RefObject<String[]>(outputs);
				grm.inflect(lan, entry, formsRef, outputsRef, grammar.grams);
				forms = formsRef.argvalue;
				outputs = outputsRef.argvalue;
				if (forms == null || forms.length == 0)
				{
					errMessage.append("\n* Cannot apply inflectional rule \"" + expname + "\"");
					continue;
				}
				for (int ires = 0; ires < forms.length; ires++)
				{
					if (checkagreement && agreementProblemsIn(category, outputs[ires], engine.properties))
						continue;
					String infoflex = removeDuplicates(outputs[ires]);
					String res = forms[ires] + "," + lemma + "," + category + Dic.removeFeature("NW", info) + infoflex;
					sw.write(res + "\n");
					nbofinflectedforms++;
				}
			}

			// get derivational classes
			String[] myfeatures = Dic.lookForAll("DRV", info);
			if (myfeatures == null)
			{
				// no derivation
				continue;
			}
			else
			{
				for (String mydrv : myfeatures)
				{
					String expname = null;
					String flxname = null;
					String expname0 = mydrv.substring(DRV_PREFIX_LENGTH);
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
						if (myfeature != null)
							flxname = myfeature.substring(FLX_PREFIX_LENGTH);
						else
							flxname = null;
					}

					// look for derivation paradigm name
					if (grammar == null || grammar.grams == null || !grammar.grams.containsKey(expname))
					{
						errMessage.append("\n* Cannot find derivational rule \"" + expname
								+ "\" for lexical information: " + info);
						continue;
					}
					Gram grm = grammar.grams.get(expname);
					if (grm == null)
					{
						errMessage.append("\n* Cannot load derivational rule \"" + expname
								+ "\" for lexical information: " + info);
						continue;
					}

					String[] forms = null, outputs = null;
					RefObject<String[]> formsRef = new RefObject<String[]>(forms);
					RefObject<String[]> outputsRef = new RefObject<String[]>(outputs);
					grm.inflect(lan, entry, formsRef, outputsRef, grammar.grams);
					forms = formsRef.argvalue;
					outputs = outputsRef.argvalue;

					if (forms == null || forms.length == 0)
					{
						errMessage.append("\n* Cannot use derivational rule \"" + expname + "\"");
						continue;
					}

					for (int ires = 0; ires < forms.length; ires++)
					{
						// process derivation: we get rid of the initial category, and we take the new one
						String initialinfo = Dic.removeFeature("NW", info); // e.g. "V+tr+FLX=Manger+DRV=Mangeable"
					
						String newinfo;
						if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
							newinfo = outputs[ires].substring(1); // to get rid of initial '+'
						else
							newinfo = outputs[ires];
						if (flxname == null)
						{
							String res = forms[ires] + "," + lemma + "," + newinfo + initialinfo;
							sw.write(res + "\n");
							nbofinflectedforms++;
						}
						else
						{
							if (grammar == null || grammar.grams == null || !grammar.grams.containsKey(flxname))
							{
								errMessage.append("\n* Cannot find inflectional rule \"" + expname
										+ "\" for lexical entry: " + line);
								continue;
							}
							Gram grm2 = grammar.grams.get(flxname);
							if (grm2 == null)
							{
								errMessage.append("\n* Cannot compile inflectional rule \"" + flxname + "\"");
								continue;
							}
							String[] dforms = null, doutputs = null;
							RefObject<String[]> dformsRef = new RefObject<String[]>(dforms);
							RefObject<String[]> doutputsRef = new RefObject<String[]>(doutputs);
							grm2.inflect(lan, forms[ires], dformsRef, doutputsRef, grammar.grams);
							dforms = dformsRef.argvalue;
							doutputs = doutputsRef.argvalue;
							if (dforms == null || dforms.length == 0)
							{
								errMessage.append("\n* Cannot use inflectional rule \"" + flxname + "\"");
								continue;
							}
							for (int i2res = 0; i2res < dforms.length; i2res++)
							{
								String lastinfo = doutputs[i2res];
								String res = dforms[i2res] + "," + lemma + "," + newinfo + initialinfo + lastinfo;
								sw.write(res + "\n");
								nbofinflectedforms++;
							}
						}
					}
				}
			}
		}
	
		sr.close();
		sw.close();

		if (nbofinflectedforms > 0)
			JOptionPane.showMessageDialog(
					Launcher.getDesktopPane(),
					"Dictionary " + resName + " has been successfully compiled:\n"
							+ Integer.toString(nbofinflectedforms) + " inflected forms.", "NooJ: Success",
					JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	private static String removeDuplicates(String inflectionalcodes)
	{
		String[] features = Dic.getAllFeaturesWithoutPlus(inflectionalcodes);
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < features.length; i++)
		{
			String ifeat = features[i];
			boolean dup = false;
			for (int j = i + 1; j < features.length; j++)
			{
				String jfeat = features[j];
				if (ifeat == jfeat)
				{
					dup = true;
					break;
				}
			}
			if (!dup)
			{
				res.add(ifeat);
			}
		}
		StringBuilder sbres = new StringBuilder();
		for (String feat : res)
		{
			sbres.append("+" + feat);
		}
		return sbres.toString();
	}

	private static boolean agreementProblemsIn(String category, String inflectionalcodes,
			HashMap<String, String> properties)
	{
		String[] features = Dic.getAllFeaturesWithoutPlus(inflectionalcodes);
		for (int i = 0; i < features.length; i++)
		{
			String feat = features[i];
			String ipropname = null, ipropval = null;
			RefObject<String> ipropnameRef = new RefObject<String>(ipropname);
			RefObject<String> ipropvalRef = new RefObject<String>(ipropval);
			Dic.getProperty(feat, category, properties, ipropnameRef, ipropvalRef);
			ipropname = ipropnameRef.argvalue;
			ipropval = ipropvalRef.argvalue;
			for (int j = i + 1; j < features.length; j++)
			{
				String jfeat = features[j];
				String jpropname = null, jpropval = null;
				RefObject<String> jpropnameRef = new RefObject<String>(jpropname);
				RefObject<String> jpropvalRef = new RefObject<String>(jpropval);
				Dic.getProperty(jfeat, category, properties, jpropnameRef, jpropvalRef);
				jpropname = jpropnameRef.argvalue;
				jpropval = jpropvalRef.argvalue;
				if (jpropname == ipropname && ipropval != jpropval)
					return true; // two incompatible features
			}
		}
		return false;
	}

	public static boolean compile(String fullname, String resName, boolean checkagreement, Language lan)
			throws IOException
	{
		if (Launcher.preferences.ldic.get(lan.isoName) == null)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find any lex resource for " + lan.isoName,
					"NooJ", JOptionPane.INFORMATION_MESSAGE);
		}

		RefObject<Language> lanRef = new RefObject<Language>(lan);
		Engine engine = new Engine(lanRef, Paths.applicationDir, Paths.docDir, Paths.projectDir, Launcher.projectMode,
				Launcher.preferences, Launcher.backgroundWorking, Launcher.backgroundWorker);
		lan = lanRef.argvalue;

		errMessage = new StringBuilder();
		int nbOfEntries = DictionaryDialogController.count(fullname);
		String dname = FilenameUtils.getFullPath(fullname); // directory where I will find the inflectional desc. files

		int nboferrors = 0;

		Dic.writeLog("Starting compilation for dictionary: " + fullname);
		Date today = new Date();
		Dic.writeLog(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, lan.locale).format(today));

		FSDic dics = new FSDic(lan.isoName);
		FSDic dicsLemma = new FSDic(lan.isoName);
		FSDic dicc = new FSDic(lan.isoName);
		FSDic diccLemma = new FSDic(lan.isoName);

		// first load all inflections
		Dic.writeLog("1/4 Compiling inflectional & derivational paradigms");

	if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				return false;
			}

			Launcher.getStatusBar().getProgressLabel().setText("1/4 Compiling Paradigms...");
			Launcher.progressMessage = "1/4 Compiling Paradigms...";
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		Grammar grammar = null;
		BufferedReader sr = null;
		try
		{
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
					Constants.ERROR_MESSAGE_TITLE_GET_FILE_STREAM, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		for (String line0 = sr.readLine(); line0 != null; line0 = sr.readLine())
		{
			line0 = line0.trim();
			if (line0.equals(""))
				continue;
			if (line0.startsWith("#use "))
			{
				// load corresponding inflectional information
				String fname = line0.substring(USE_PREFIX_LENGTH);
				fname = fname.trim();
				String flxname = dname + fname;
				String ext = "." + FilenameUtils.getExtension(flxname);
				File file = new File(flxname);
				if (!file.exists())
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot find file " + flxname,
							"NooJ: file does not exist", JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					return false;
				}
				Grammar cgrammar = null;
				if ((ext.equals(".def")) || (ext.equals(".DEF")))
				{
					JOptionPane
							.showMessageDialog(
									Launcher.getDesktopPane(),
									"In NooJ V4, the only property defininition file is stored in Lexical Analysis\\_properties.def",
									"NooJ V4", JOptionPane.INFORMATION_MESSAGE);
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							"In NooJ V4, there is no more support for the obsolete command \"#use properties.def\"",
							"NooJ V4", JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					return false;
															
				}
				else if ((ext.equals(".nof") || ext.equals(".grm")) || (ext.equals(".NOF") || ext.equals(".GRM")))
				{
					boolean istextual = Grammar.isItTextual(flxname);
					if (istextual)
					{
						String errmessage = null;
						RefObject<String> errmessageRef = new RefObject<String>(errmessage);
						cgrammar = Grammar.loadTextual(flxname, GramType.FLX, errmessageRef);
						errmessage = errmessageRef.argvalue;
						if (errmessage != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Problem in grammar " + flxname
									+ ":\n" + errmessage, "NooJ: Cannot handle grammar",
									JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							return false;
						}
					}
					else
						
						cgrammar = Grammar.loadONooJGrammar(flxname);
					if (cgrammar == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Cannot load grammar \"" + flxname
								+ "\"", "NooJ: grammar has errors", JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					cgrammar.compileAll(null);
					if (grammar == null)
						grammar = cgrammar;
					else
					{
						String em = grammar.addGrams(cgrammar);
						if (em != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), em, "Problem with paradigms",
									JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							return false;
						}
					}
				}
				else if (ext.equals(".flx") || ext.equals(".FLX"))
				{
					String errmessage = null, ilanguagename = null, olanguagename = null;
					RefObject<String> errmessageRef = new RefObject<String>(errmessage);
					RefObject<String> ilanguagenameRef = new RefObject<String>(ilanguagename);
					RefObject<String> olanguagenameRef = new RefObject<String>(olanguagename);
					Regexps rs = Regexps.load(flxname, errmessageRef, ilanguagenameRef, olanguagenameRef);
					errmessage = errmessageRef.argvalue;
					ilanguagename = ilanguagenameRef.argvalue;
					olanguagename = olanguagenameRef.argvalue;
					if (rs == null)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Grammar file \"" + flxname
								+ "\" contains invalid statements:\n" + errmessage, "NooJ: cannot handle file.",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					if (rs.grammar.grams.size() == 0)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File \"" + flxname + "\" is empty",
								"NooJ: cannot handle file.", JOptionPane.INFORMATION_MESSAGE);
						continue;
					}
					if (!ilanguagename.equals(lan.isoName))
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Language in \"" + flxname
								+ "\" does not match dictionary's", "WARNING", JOptionPane.INFORMATION_MESSAGE);
					}
					for (String expname : (rs.grammar.grams.keySet()))
					{
						
						Gram grm = rs.grammar.grams.get(expname);
						if (grm == null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Error in rule " + expname,
									"NooJ: rule has not compiled successfully", JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							return false;
						}
						if (grm.vocabIn == null)
						{
							grm.prepareForParsing();
						}
					}
					cgrammar = rs.grammar;
					if (grammar == null)
						grammar = cgrammar;
					else
					{
						String em = grammar.addGrams(cgrammar);
						if (em != null)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(), em, "Problem with paradigms",
									JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							return false;
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "File \"" + flxname
							+ "\" has an incorrect extension", "NooJ: cannot handle extension " + ext,
							JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					return false;
				}
			}
		}
		sr.close();
		if (grammar == null || grammar.grams == null || grammar.grams.size() == 0)
		{
			if (JOptionPane.showConfirmDialog(Launcher.getDesktopPane(),
					"Dictionary has no associated inflectional/derivational description.\nDo you want to continue?",
					"NooJ", JOptionPane.YES_NO_OPTION) != 0)
				return false;
		}

		dics.paradigms = new HashMap<String, Gram>();
		if (grammar != null && grammar.grams != null && grammar.grams.size() > 0)
		{
			for (String paradname : grammar.grams.keySet())
			{
				if (dics.paradigms.containsKey(paradname))
				{
					// inflectional paradigm is already there!
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Morphological paradigm " + paradname
							+ " is defined more than once.", "NooJ ignores the 2nd version of duplicate paradigms",
							JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					dics.paradigms.put(paradname, grammar.grams.get(paradname));
				}
			}
		}
		Dic.writeLog(" > " + dics.paradigms.size() + " inflectional/derivational paradigms.");

		// now inflect all dictionary entries
		Dic.writeLog("2/4 Processing inflection & derivation");

		if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				return false;
			}

			Launcher.progressMessage = "2/4 Processing inflection & derivation...";
			Launcher.getStatusBar().getProgressLabel().setText("2/4 Processing inflection & derivation...");
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		int nbofinflectedforms = 0;

		sr = new BufferedReader(new InputStreamReader(new FileInputStream(fullname), "UTF8"));

		int progressPercentage = 0;
		int itu = 0;
		HashMap<String, Integer> hDicInfos = new HashMap<String, Integer>();
		ArrayList<String> aDicInfos = new ArrayList<String>();
		aDicInfos.add("NA"); // so that infonb always > 0

		for (String line = sr.readLine(); line != null; line = sr.readLine())
		{
			if (line.equals(""))
				continue;
			if (line.charAt(0) == '#')
			{
				if (line.length() >= 2 && line.charAt(1) == '#')
					dics.Comments += line.substring(2) + "\r\n";
				continue;
			}
			itu++;

	if (Launcher.multithread)
			{
				if (Launcher.backgroundWorker.isCancellationPending())
				{
					sr.close();
					return false;
				}
				int nprogress = (int) (itu * 100.0F / nbOfEntries);
				if (nprogress != progressPercentage)
				{
					progressPercentage = nprogress;
					if (Launcher.backgroundWorker.isBusy())
						Launcher.backgroundWorker.reportProgress(nprogress);
				}
			}

			String entry = null, lemma = null, category = null, info = null;
			int nbofcommas = Dic.nbOfCommas(line);
			if (nbofcommas == 1)
			{
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String> infoRef = new RefObject<String>(info);
				boolean tmp = Dic.parseDELAS(line, entryRef, categoryRef, infoRef);
				entry = entryRef.argvalue;
				category = categoryRef.argvalue;
				info = infoRef.argvalue;
				if (!tmp)
				{
					boolean tmp1 = Dic.parseContracted(line, entryRef, infoRef);
					entry = entryRef.argvalue;
					info = infoRef.argvalue;
					if (!tmp1)
					{
						errMessage.append("* Cannot parse line: " + line + "\n");
						nboferrors++;
						if (nboferrors > 100)
						{
							JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
									"Too many errors (100) while trying to compile dictionary", "NooJ: cannot compile",
									JOptionPane.INFORMATION_MESSAGE);
							sr.close();
							return false;
						}
						continue;
					}
					category = null;
					lemma = entry;
				}
				else
					lemma = entry;
			}
			else if (nbofcommas == 2)
			{
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> lemmaRef = new RefObject<String>(lemma);
				RefObject<String> categoryRef = new RefObject<String>(category);
				RefObject<String> infoRef = new RefObject<String>(info);
				boolean tmp = Dic.parseDELAF(line, entryRef, lemmaRef, categoryRef, infoRef);
				entry = entryRef.argvalue;
				lemma = lemmaRef.argvalue;
				category = categoryRef.argvalue;
				info = infoRef.argvalue;
				if (!tmp)
				{
					errMessage.append("* Cannot parse line: " + line + "\n");
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors (100) while trying to compile dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					continue;
				}
				if (lemma == null || lemma.equals(""))
					lemma = entry;
			}
			else
			{
				errMessage.append("* Cannot parse line: " + line + " (nb of commas " + nbofcommas + " is invalid)\n");
				Dic.writeLog("Cannot parse line: " + line + " (nb of commas " + nbofcommas + " is invalid)");
				nboferrors++;
				if (nboferrors > 100)
				{
					JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
							"Too many errors (100) while trying to compile dictionary", "NooJ: cannot compile",
							JOptionPane.INFORMATION_MESSAGE);
					sr.close();
					return false;
				}
				continue;
			}

			// get inflectional class
			String myfeatureflx = Dic.lookFor("FLX", info);
			String[] myfeaturesdrv = Dic.lookForAll("DRV", info);
			if (myfeatureflx == null)
			{
				// NEWVERSION: store invariable lexical entry in dics / dicc
				if (dics.Lan.isACompound(entry) || lan.asianTokenizer)
				{
					dicc.addLexLineToDic(entry, lemma, (category == null ? "" : category) + (info == null ? "" : info),
							"", dics, dics, hDicInfos, aDicInfos, true);
				}
				else
				{
					dics.addLexLineToDic(entry, lemma, (category == null ? "" : category) + (info == null ? "" : info),
							"", dics, dics, hDicInfos, aDicInfos, false);
				}
				nbofinflectedforms++; // the word is not inflected, but it still counts
				if (myfeaturesdrv == null)
				{
					// the entry is not inflected, and is not derived => continue with the next entry
					continue;
				}
			}

			// NEWVERSION: store lemma in dicsLemma / diccLemma
			if (dics.Lan.isACompound(entry) || lan.asianTokenizer)
			{
				diccLemma.addLexLineToDic(entry, lemma, category + Dic.removeFeature("NW", info), "", dics, dics,
						hDicInfos, aDicInfos, true);
			}
			else
			{
				dicsLemma.addLexLineToDic(entry, lemma, category + Dic.removeFeature("NW", info), "", dics, dics,
						hDicInfos, aDicInfos, false);
			}

			if (myfeatureflx != null)
			{
				if (myfeatureflx.length() < 5)
				{
					errMessage.append("* Cannot parse FLX code in: " + line + "\n");
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compile dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					continue;
				}
				String expname = myfeatureflx.substring(FLX_PREFIX_LENGTH);

				Gram grm = null;
				if (!dics.paradigms.containsKey(expname))
				{
					errMessage.append("* Cannot find inflectional class: " + expname + " in line '" + line + "'\n");
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compile dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					nbofinflectedforms++;
					continue;
				}
				grm = dics.paradigms.get(expname);
				if (grm == null)
				{
					errMessage.append("* inflectional class: " + expname + " did not compile\n");
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compile dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					nbofinflectedforms++;
					continue;
				}

				String[] forms = null, outputs = null;
				RefObject<String[]> formsRef = new RefObject<String[]>(forms);
				RefObject<String[]> outputsRef = new RefObject<String[]>(outputs);
				grm.inflect(lan, entry, formsRef, outputsRef, dics.paradigms);
				forms = formsRef.argvalue;
				outputs = outputsRef.argvalue;
				if (forms != null && forms.length > 0)
				{
					for (int ires = 0; ires < forms.length; ires++)
					{
						// NEWVERSION store form in dics / dicc
						if (dics.Lan.isACompound(forms[ires]) || dics.Lan.asianTokenizer)
						{
							if (checkagreement && agreementProblemsIn(category, outputs[ires], engine.properties))
								continue;
							String infoflex = removeDuplicates(outputs[ires]);
							dicc.addLexLineToDic(forms[ires], lemma, category + "+" + myfeatureflx, infoflex, dics,
									dics, hDicInfos, aDicInfos, true);
						}
						else
						{
							
							dics.addLexLineToDic(forms[ires], lemma, category + "+" + myfeatureflx, outputs[ires],
									dics, dics, hDicInfos, aDicInfos, false);
						}
						nbofinflectedforms++;
					}
				}
				else
				{
					errMessage.append("* Cannot process inflectional class: " + expname + "\n");
					nboferrors++;
					if (nboferrors > 100)
					{
						JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
								"Too many errors while trying to compile dictionary", "NooJ: cannot compile",
								JOptionPane.INFORMATION_MESSAGE);
						sr.close();
						return false;
					}
					nbofinflectedforms++;
					continue;
				}
			}

			if (myfeaturesdrv != null)
			{
				for (String mydrv : myfeaturesdrv)
				{
					String expname = null;
					String flxname = null;
					String expname0 = mydrv.substring(DRV_PREFIX_LENGTH);
					int icomb = expname0.indexOf(':');
					if (icomb != -1)
					{
						expname = expname0.substring(0, icomb);
						flxname = expname0.substring(icomb + 1);
					}
					else
					{
						expname = expname0;
						// if no :FLX is specified, then get default inflection paradigm name
						if (myfeatureflx != null)
							flxname = myfeatureflx.substring(FLX_PREFIX_LENGTH);
						else
							flxname = null;
					}

					if (grammar == null || grammar.grams == null || !grammar.grams.containsKey(expname))
					{
						errMessage.append("* Cannot find derivational rule \"" + expname
								+ "\" for lexical information:\n" + info);
						continue;
					}
					Gram grm = grammar.grams.get(expname);
					if (grm == null)
					{
						errMessage.append("* Cannot load derivational rule \"" + expname
								+ "\" for lexical information:\n" + info);
						continue;
					}

					String[] forms = null, outputs = null;
					RefObject<String[]> formsRef = new RefObject<String[]>(forms);
					RefObject<String[]> outputsRef = new RefObject<String[]>(outputs);
					grm.inflect(lan, entry, formsRef, outputsRef, grammar.grams);
					forms = formsRef.argvalue;
					outputs = outputsRef.argvalue;
					if (forms == null || forms.length == 0)
					{
						errMessage.append("* Cannot use derivational rule \"" + expname + "\"\n");
						continue;
					}

					for (int ires = 0; ires < forms.length; ires++)
					{
						// process derivation: we get rid of the initial category, and we take the new one
						String newcategory;
						if (outputs[ires].length() > 1 && outputs[ires].charAt(0) == '+')
							newcategory = outputs[ires].substring(1); // to get rid of initial '+'
						else
							newcategory = outputs[ires];

						if (flxname == null)
						{
							// NEWVERSION with dicsLemma and diccLemma
							if (dics.Lan.isACompound(forms[ires]) || dics.Lan.asianTokenizer)
							{
								
								diccLemma.addLexLineToDic(forms[ires], lemma, newcategory + "+" + mydrv, "", dics,
										dics, hDicInfos, aDicInfos, true);
							}
							else
							{
								
								dics.addLexLineToDic(forms[ires], lemma, newcategory + "+" + mydrv, "", dics, dics,
										hDicInfos, aDicInfos, false);
							}
							nbofinflectedforms++;
						}
						else
						{
							if (grammar == null || grammar.grams == null || !grammar.grams.containsKey(flxname))
							{
								errMessage.append("* Cannot find inflectional rule \"" + expname
										+ "\" for lexical entry:\n" + line);
								continue;
							}
							Gram grm2 = grammar.grams.get(flxname);
							if (grm2 == null)
							{
								errMessage.append("* Cannot find inflectional rule \"" + expname
										+ "\" for lexical entry:\n" + line);
								continue;
							}
							String[] dforms = null, doutputs = null;
							RefObject<String[]> dformsRef = new RefObject<String[]>(dforms);
							RefObject<String[]> doutputsRef = new RefObject<String[]>(doutputs);
							grm2.inflect(lan, forms[ires], dformsRef, doutputsRef, grammar.grams);
							dforms = dformsRef.argvalue;
							doutputs = doutputsRef.argvalue;
							if (dforms == null || dforms.length == 0)
							{
								errMessage.append("* Cannot use rule \"" + flxname + "\"");
								continue;
							}
							for (int i2res = 0; i2res < dforms.length; i2res++)
							{
								String lastinfo = doutputs[i2res];
								if (lastinfo.equals("+"))
									lastinfo = ""; // hack
								if (dics.Lan.isACompound(dforms[i2res]) || dics.Lan.asianTokenizer)
								{
									
									dicc.addLexLineToDic(dforms[i2res], lemma, newcategory + "+" + mydrv, lastinfo,
											dics, dics, hDicInfos, aDicInfos, true);
								}
								else
								{
									
									dics.addLexLineToDic(dforms[i2res], lemma, newcategory + "+" + mydrv, lastinfo,
											dics, dics, hDicInfos, aDicInfos, false);
								}
								nbofinflectedforms++;
							}
						}
					}
				}
			}
		}
		sr.close();
		Dic.writeLog(" > " + itu + " entries, " + nbofinflectedforms + " forms, (" + dics.states.size() + "/"
				+ dicsLemma.states.size() + ", " + dicc.states.size() + "/" + diccLemma.states.size() + ") states, "
				+ aDicInfos.size() + " different infos.");

		// CLEANUP: transfer hashtable and ArrayList into array "infos"
		hDicInfos = null;
		aDicInfos.trimToSize();
		
		dics.infos = aDicInfos.toArray(new String[aDicInfos.size()]);
		aDicInfos = null;
		dics.cleanupInflectionCommands();

		// Minimize dictionary
		Dic.writeLog("3/4 Minimizing dictionary");

	if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				return false;
			}

			Launcher.progressMessage = "3/4 Minimize dictionary...";
			Launcher.getStatusBar().getProgressLabel().setText("3/4 Minimize dictionary...");
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		// Final Minimization
		dics.minimize(dics.infos.length);
		dicsLemma.minimize(dics.infos.length);
		dicc.minimize(dics.infos.length);
		diccLemma.minimize(dics.infos.length);

		// Save dictionary
		Dic.writeLog("4/4 Saving dictionary file");

		if (Launcher.multithread)
		{
			if (Launcher.backgroundWorker.isCancellationPending())
			{
				return false;
			}

			Launcher.progressMessage = "4/4 Save dictionary...";
			Launcher.getStatusBar().getProgressLabel().setText("4/4 Save dictionary...");
			Launcher.progressPercentage = 0;
			if (Launcher.backgroundWorker.isBusy())
				Launcher.backgroundWorker.reportProgress(0);
		}

		if (dics == null || dicc == null || dicsLemma == null || diccLemma == null)
		{
			JOptionPane
					.showMessageDialog(null, "Problem with the compression", "NooJ", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		FSDic.computeAllLogs(dics, dicc, dicsLemma, diccLemma);
		dics.buffer = dics.toBinary(dics);
		dics.bufferc = dicc.toBinary(dics);
		dics.bufferl = dicsLemma.toBinary(dics);
		dics.buffercl = diccLemma.toBinary(dics);
		dics.toBinaryAlphabetInfobitstable();

		// save the result in Lexical Analysis instead of in the current directory
		String dnamelex = FilenameUtils.concat(Paths.docDir, FilenameUtils.concat(lan.isoName, "Lexical Analysis"));
		String nodname = FilenameUtils.concat(dnamelex, FilenameUtils.getBaseName(fullname) + "."
				+ Constants.JNOD_EXTENSION);
		dics.save(nodname);
		String txtname = FilenameUtils.concat(dnamelex, FilenameUtils.getBaseName(fullname) + ".txt");
		dics.saveComments(txtname, nbofinflectedforms, dics.states.size(), dicc.states.size(), dicsLemma.states.size(),
				diccLemma.states.size());

		Dic.writeLog(" > Dictionary successfully compiled: " + dics.states.size() + "/" + dicc.states.size()
				+ " states; " + dics.infos.length + " infos; recognizes " + nbofinflectedforms + " forms");
		today = new Date();
		Dic.writeLog(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, lan.locale).format(today));
		Dic.writeLog("");
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(), "Dictionary has been successfully compiled in file:\n"
				+ nodname + "\n(" + dics.states.size() + "/" + dicc.states.size() + " states; " + dics.infos.length
				+ " infos; recognizes " + nbofinflectedforms + " forms)", "NooJ: Success",
				JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(Launcher.getDesktopPane(),
				"Make sure the resource " + FilenameUtils.getBaseName(nodname)
						+ "\nis checked in Info > Preferences > Lexical Analysis", "NooJ",
				JOptionPane.INFORMATION_MESSAGE);
		dics = dicc = dicsLemma = diccLemma = null;
		return true;
	}
}