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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import net.nooj4nlp.engine.helper.ParameterCheck;
import net.nooj4nlp.gui.main.Launcher;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Silberztein Max
 */
public class Corpus implements Serializable
{
	private static final long serialVersionUID = 3406281615638257605L;

	private static final String CORPUS_BIN = "corpus.bin";

	public ArrayList<String> listOfFileTexts; // list of text file names

	public String languageName; // Name of the language of corpus texts
	public transient Language lan; // Language of corpus
	public int encodingType; // Type of encoding for corpus
	// Encoding changed to String
	public String encodingCode; // Code of encoding for corpus
	public String encodingName; // Name of the corpus encoding
	public String delimPattern; // String used as a pattern for delimiting
	public String[] xmlNodes; // Array of XML tags

	public int nbOfTextUnits; // Number of text units in corpus
	public int nbOfChars;// Number of characters in corpus
	public int nbOfDiffChars;// Number of different characters in corpus
	public int nbOfLetters;// Number of letters in corpus
	public int nbOfDiffLetters;// Number of different letters in corpus
	public int nbOfDelimiters;// Number of delimiters in corpus
	public int nbOfDiffDelimiters;// Number of different delimiters in corpus
	public int nbOfBlanks;// Number of blanks in corpus
	public int nbOfDiffBlanks;// Number of different blanks in corpus
	public int nbOfDigits;// Number of digits in corpus
	public int nbOfDiffDigits;// Number of different digits in corpus
	public int nbOfTokens;// Number of tokens in corpus
	public int nbOfDiffTokens;// Number of different tokens in corpus
	public int nbOfWords;// Number of words in corpus
	public int nbOfDiffWords;// Number of different words in corpus

	public transient Charlist charlist = null; // Charlist for this corpus
	public transient double multiplier = 1.0;
	public transient HashMap<String, Integer> hTokens = null; // Tokens
	public transient HashMap<String, ArrayList<Object>> hAmbiguities = null; // Ambiguities
	public transient HashMap<String, ArrayList<Object>> hUnambiguities = null; // Unambiguities
	public transient HashMap<String, Integer> hDigrams = null; // Digrams
	public transient HashMap<String, Integer> hLexemes = null; // Lexemes
	public transient HashMap<String, Integer> hUnknowns = null; // Unkowns
	public transient HashMap<String, Integer> hPhrases = null; // Phrases
	public transient ArrayList<String> listOfResources = null; // List of resources
	public ArrayList<Object> annotations = null; // List of annotations

	/**
	 * Constructor - initializes list of text files, language with the given name, annotations, lexemes and phrases, as
	 * well as number of blanks, letters, etc. Sets pattern for delimiting, encoding type, code, name, language name and
	 * xml nodes to given values.
	 * 
	 * @param delimPattern
	 *            - string that represents pattern to be used as a delimiter
	 * @param xmlNodes
	 *            - list of xml tags defined while creating corpus
	 * @param encodingType
	 *            - encoding type for corpus (1 for raw text, 2 for raw text with given encoding code, 3 for RTF, 4 for
	 *            .doc, 5 for HTML, 6 for .docx, 7 for PDF)
	 * @param encodingCode
	 *            - encoding code for corpus
	 * @param encodingName
	 *            - encoding name for corpus
	 * @param languageName
	 *            - name of the corpus language
	 */
	public Corpus(String delimPattern, String[] xmlNodes, int encodingType, String encodingCode, String encodingName,
			String languageName)
	{
		this.listOfFileTexts = new ArrayList<String>();
		this.delimPattern = delimPattern;
		this.xmlNodes = xmlNodes;
		this.encodingType = encodingType;
		this.encodingCode = encodingCode;
		// Hardcoded the case when encodingName is null
		if (encodingName == null)
			encodingName = "";
		this.encodingName = encodingName;
		this.languageName = languageName;
		this.lan = new Language(languageName);

		this.annotations = new ArrayList<Object>();
		this.hLexemes = new HashMap<String, Integer>();
		this.hPhrases = new HashMap<String, Integer>();

		nbOfTextUnits = nbOfChars = nbOfDiffChars = nbOfLetters = nbOfDiffLetters = nbOfDelimiters = nbOfDiffDelimiters = nbOfBlanks = nbOfDiffBlanks = nbOfDigits = nbOfDiffDigits = nbOfTokens = nbOfDiffTokens = nbOfWords = nbOfDiffWords = -1;
	}

	/**
	 * Loads text from file and adds it to corpus.
	 * 
	 * @param corpusDirName
	 *            - name of directory that contains corpus files
	 * @param textFullPath
	 *            - path to text file that will be added to corpus
	 * @param engine
	 *            - engine used for delimiting text units and XML tags
	 * @return true if text is successfully added to corpus, false if otherwise.
	 * @throws IOException
	 *             if an error occurs while loading the text
	 * @throws BadLocationException
	 *             if an error occurs while reading RTF file
	 * @throws ClassNotFoundException
	 *             if an error occurs while loading the text
	 */
	public final boolean addTextFile(String corpusDirName, String textFullPath, Engine engine) throws IOException,
			BadLocationException, ClassNotFoundException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("corpusDirName", corpusDirName);
		ParameterCheck.mandatoryString("textFullPath", textFullPath);

		Ntext ntext = null;

		String fileExtension = FilenameUtils.getExtension(textFullPath);

		if (fileExtension.equals(Constants.JNOT_EXTENSION) && this.encodingName.equals("Default"))
		{
			String errMessage = null;
			RefObject<String> tempErrMessage = new RefObject<String>(errMessage);
			ntext = Ntext.load(textFullPath, this.languageName, tempErrMessage);
			errMessage = tempErrMessage.argvalue;

			if (ntext == null)
			{
				System.out.println("Format is incorrect for file: " + textFullPath + "\n" + errMessage
						+ "NooJ: text file format does not match corpus file format");
			}
		}
		else
		{
			// import text file
			ntext = new Ntext(this);

			try
			{
				ntext.buffer = TextIO.loadText(textFullPath, this.encodingType, this.encodingCode, this.encodingName,
						this.lan.chartable);
			}
			catch (RuntimeException ex)
			{
				System.out.println("Format is incorrect for file: " + textFullPath + "\n" + ex.getMessage()
						+ "NooJ: text file format does not match corpus file format");
				return false;
			}
		}

		if (ntext.buffer == null)
		{
			System.out.println("Cannot load text from file: " + textFullPath);
			return false;
		}

		// Delimit text using ntext
		ntext.DelimPattern = this.delimPattern;
		ntext.XmlNodes = this.xmlNodes;
		if (engine != null)
		{
			if (this.xmlNodes != null)
			{
				ntext.delimitXmlTextUnitsAndImportXmlTags(this, engine, this.xmlNodes, this.annotations, this.hLexemes,
						this.hPhrases);
			}
			else
			{
				ntext.delimitTextUnits(engine);
			}
		}

		// Save text into corpus
		String fileNameWithExtension = FilenameUtils.getName(textFullPath);
		String fileNameWithoutExtension = FilenameUtils.removeExtension(fileNameWithExtension) + "."
				+ Constants.JNOT_EXTENSION;

		// Corpus directory must be created before files are created inside it.
		File corpusDir = new File(corpusDirName);
		if (!corpusDir.exists())
			new File(corpusDirName).mkdir();

		File textFile = new File(corpusDirName, fileNameWithoutExtension);
		String textFileName = textFile.getPath();
		ntext.saveForCorpus(textFileName);

	
		if (this.listOfFileTexts == null)
		{
			this.listOfFileTexts = new ArrayList<String>();
		}

		if (this.listOfFileTexts.indexOf(fileNameWithoutExtension) == -1)
		{
			this.listOfFileTexts.add(fileNameWithoutExtension);
		}

		return true;
	}

	/**
	 * Saves corpus file ('corpus.bin') in the specified directory.
	 * 
	 * @param resDirPath
	 *            Path of the directory which should contain 'corpus.bin' after saving.
	 * @throws IOException
	 *             if an error occurs while opening new input stream or writing to it.
	 */
	public final void saveIn(String resDirPath) throws IOException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("resDirPath", resDirPath);

		int nbOfHacks = 0;
		if (this.annotations != null)
		{
			if (this.hTokens != null)
			{
				this.annotations.add(0, "$tokens$");
				this.annotations.add(1, this.hTokens);
				nbOfHacks++;
			}

			if (this.listOfResources != null)
			{
				this.annotations.add(0, "$resources$");
				this.annotations.add(1, this.listOfResources);
				nbOfHacks++;
			}

			this.annotations.add(0, "$multiplier$"); // multiplies all positions per 100 to store (double)stPostions as
		
			this.annotations.add(1, null);
			nbOfHacks++;
		}

		// Creating new file in corpus directory
		File resDir = new File(resDirPath);
		resDir.mkdir();
		File corpusDir = new File(resDirPath, CORPUS_BIN);
		String corpusDirFullName = corpusDir.getName();
		try
		{
			// By default, serialization in Java is binary
			FileOutputStream fileOutputStream = new FileOutputStream(corpusDir);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.flush();

			objectOutputStream.close();
			fileOutputStream.close();
		}
		catch (RuntimeException e)
		{
			System.out.println("NooJ: Cannot save corpus in file " + corpusDirFullName + ":\n" + e.getMessage());
			return;
		}

		if (nbOfHacks > 0)
		{
			for (int i = 0; i < nbOfHacks; i++)
			{
				this.annotations.remove(0);
				this.annotations.remove(0);
			}
		}
	}

	/**
	 * Loads corpus with proper language set.
	 * 
	 * @param corpusName
	 *            - name of directory that contains corpus files
	 * @param languageName
	 *            - name of the language of corpus texts
	 * @return loaded corpus
	 * @throws IOException
	 *             if zipped dir cannot be unzipped or if corpus cannot be loaded
	 * @throws ClassNotFoundException
	 *             if corpus cannot be loaded
	 */
	public static Corpus load(String corpusName, String languageName) throws IOException, ClassNotFoundException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("corpusName", corpusName);
		ParameterCheck.mandatoryString("languageName", languageName);

		String crpDirName = corpusName + Constants.DIRECTORY_SUFFIX;
		File corpusDir = new File(crpDirName);
		File corpusFile = new File(corpusName);
		if (corpusDir.exists())
		{
			if (corpusDir.isDirectory())
			{
				// check if directory date is after corpus file date
				Date corpusDirDate = new Date(corpusDir.lastModified());
				Date corpusDate = new Date(corpusFile.lastModified());
				if (corpusDirDate.before(corpusDate))
				{
					// directory was before corpus file => delete directory
					corpusDir.delete();
				}
			}
		}
		else
		{
			try
			{
				Zip.uncompressDir(crpDirName, corpusName);
			}
			catch (RuntimeException ex)
			{
				System.out.println("NooJ cannot load corrupted corpus file: " + ex.getMessage());
				if ((new File(crpDirName)).isDirectory())
				{
					try
					{
						corpusDir.delete();
					}
					catch (java.lang.Exception e)
					{
					}
				}
				return null;
			}
		}

		Corpus corpus = Corpus.loadIn(crpDirName, languageName);
		if (corpus == null)
		{
			System.out.println("NooJ cannot load corrupted corpus file in folder: " + crpDirName);
			return null;
		}

		corpus.listOfFileTexts = new ArrayList<String>();

		for (File fileFromCorpusDir : corpusDir.listFiles())
		{
			String fileFromCorpusDirName = fileFromCorpusDir.getName();
			String ext = FilenameUtils.getExtension(fileFromCorpusDirName);
			if (ext.equals(Constants.JNOT_EXTENSION))
			{
				if (fileFromCorpusDir.isFile())
				{
					corpus.listOfFileTexts.add(fileFromCorpusDirName);
				}
			}
		}
		return corpus;
	}

	/**
	 * Computes hLexemes for the given corpus and sets them in appropriate hLexemes hashTable, as well as hPhrases and
	 * hUnknowns.
	 * 
	 * @param crp
	 *            - corpus
	 */
	private static void computehLexemes(Corpus crp)
	{
		// Mandatory parameter check
		ParameterCheck.mandatory("crp", crp);

		// compute corpus.hLexemes and hPhrases
		crp.hLexemes = new HashMap<String, Integer>();
		crp.hPhrases = new HashMap<String, Integer>();
		crp.hUnknowns = new HashMap<String, Integer>();

		if (crp.annotations.size() > 0)
		{
			for (int i = 0; i < crp.annotations.size(); i++)
			{
				String lex = (String) crp.annotations.get(i);
				if (lex == null)
				{
					continue;
				}

				String entry = null;
				String lemma = null;
				String info = null;

				RefObject<String> entryBuilder = new RefObject<String>(entry);
				RefObject<String> lemmaBuilder = new RefObject<String>(lemma);
				RefObject<String> infoBuilder = new RefObject<String>(info);

				boolean tempVar = !Dic.parseDELAF(lex, entryBuilder, lemmaBuilder, infoBuilder);

				entry = entryBuilder.argvalue;
				lemma = lemmaBuilder.argvalue;
				info = infoBuilder.argvalue;

				if (tempVar)
				{
					Dic.writeLog("Error: invalid annotation: \"" + lex + "\"");
				}
				if (info.equals("UNKNOWN"))
				{
					if (!crp.hUnknowns.containsKey(lex))
					{
						crp.hUnknowns.put(lex, i);
					}
				}
				else if (lemma.equals("SYNTAX"))
				{
					if (!crp.hPhrases.containsKey(lex))
					{
						crp.hPhrases.put(lex, i);
					}
				}
				else
				{
					if (!crp.hLexemes.containsKey(lex))
					{
						crp.hLexemes.put(lex, i);
					}
				}
			}
		}
	}

	/**
	 * Loads corpus which is found in the directory for which the path is given, and which is in the given language.
	 * 
	 * @param resDirPath
	 *            - path do directory which contains corpus
	 * @param languagename
	 *            - name of the language corpus' texts are in
	 * @return loaded corpus
	 * @throws IOException
	 *             if an error occurs while creating new ObjectInputStream
	 * @throws ClassNotFoundException
	 *             if the class of the serialized object cannot be found
	 */
	private static Corpus loadIn(String resDirPath, String languagename) throws IOException, ClassNotFoundException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("resDirPath", resDirPath);

		FileInputStream fileInputStream = null;
		Corpus crp = null;

		File corpusDir = new File(resDirPath, CORPUS_BIN);
		String corpusDirFullName = corpusDir.getName();

		try
		{
			fileInputStream = new FileInputStream(corpusDir);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			crp = (Corpus) objectInputStream.readObject();
			fileInputStream.close();

			crp.lan = new Language(crp.languageName);
		}
		catch (RuntimeException ex)
		{
			System.out.println("NooJ: Cannot load corpus file " + corpusDirFullName + "\n" + ex.getMessage());
			if (fileInputStream != null)
			{
				fileInputStream.close();
			}
			return null;
		}

		if (crp.lan != null)
		{
			if (crp.annotations != null && crp.annotations.size() > 0)
			{
				crp.multiplier = 1.0;
				String keyword;
				do
				{
					keyword = (String) crp.annotations.get(0);
					if (keyword != null)
					{
						if (keyword.equals("$colors$"))
						{
							crp.annotations.remove(0);
							crp.annotations.remove(0);
						}
						else if (keyword.equals("$multiplier$"))
						{
							crp.multiplier = 100.0;
							crp.annotations.remove(0);
							crp.annotations.remove(0);
						}
						else if (keyword.equals("$resources$"))
						{
							// Unchecked cast cannot be avoided here; crp.annotations is an ArrayList that contains of
							// objects of different data types (HashMaps, ArrayLists...)
							crp.listOfResources = (ArrayList<String>) crp.annotations.get(1);
							crp.annotations.remove(0);
							crp.annotations.remove(0);
						}
						else if (keyword.equals("$tokens$"))
						{
							// Unchecked cast cannot be avoided here; crp.annotations is an ArrayList that contains of
							// objects of different data types (HashMaps, ArrayLists...)
							crp.hTokens = (HashMap<String, Integer>) crp.annotations.get(1);
							crp.annotations.remove(0);
							crp.annotations.remove(0);
						}
					}
				}
				while (crp.annotations.size() > 0
						&& (keyword != null && (keyword.equals("$tokens$") || keyword.equals("$colors$")
								|| keyword.equals("$multiplier$") || keyword.equals("$resources$"))));
			}
			if (crp.annotations != null)
			{
				computehLexemes(crp);
			}
			return crp;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Function gets rid of unused annotations for corpus.
	 * 
	 * @return
	 */
	public boolean getRidOfUnusedAnnotationsForCorpus(String corpusFullName)
	{
		boolean[] exist = new boolean[annotations.size()];
		for (int i = 0; i < exist.length; i++)
		{
			exist[i] = false;
		}

		for (String fName : listOfFileTexts)
		{
			String fullName = corpusFullName + Constants.DIRECTORY_SUFFIX + System.getProperty("file.separator")
					+ fName;
			try
			{
				Ntext myText = Ntext.loadForCorpus(fullName, lan, multiplier);
				if (myText == null)
					continue;

				// Scan mft
				for (int tuNb = 1; tuNb <= myText.mft.tuAddresses.length - 1; tuNb++)
				{
					
					ArrayList<TransitionObject> transitions = myText.mft.aTransitions.get(tuNb); // All the transitions
																									// in text
					// unit

				
					for (int it = 0; it < transitions.size(); it++)
					{
						
						ArrayList<TransitionPair> outgoings = transitions.get(it).getOutgoings();
						
						for (int io = 0; io < outgoings.size(); io++)
						{
							
							int tokenId = outgoings.get(io).getTokenId();
							if (tokenId >= exist.length)
							{
								
							}
							else
							{
								exist[tokenId] = true;
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Launcher.getDesktopPane(), e.getMessage(),
						Constants.ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		// Update annotations
		for (int ia = 0; ia < annotations.size(); ia++)
		{
			if (!exist[ia])
			{
				String label = (String) annotations.get(ia);
				if (label == null)
					continue;

				String entry = null, info = null;
				RefObject<String> entryRef = new RefObject<String>(entry);
				RefObject<String> infoRef = new RefObject<String>(info);

				if (Dic.parseDELAS(label, entryRef, infoRef))
				{
					// A lexeme does not exist anymore => delete it
					
					annotations.set(ia, null);
				}
			}
		}

		return true;
	}
}