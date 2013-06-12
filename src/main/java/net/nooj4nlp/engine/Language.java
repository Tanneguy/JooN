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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import net.nooj4nlp.engine.helper.ParameterCheck;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that takes charge of everything specific for each language.
 * 
 * @author Silberztein Max
 * 
 */
public class Language implements Serializable
{
	private static final long serialVersionUID = 849794032433789267L;

	public String isoName; // ISO 2-letter code, e.g. "en" or "fr"
	public String natName; // ISO name of the language in its native language
	public String engName; // ISO name of the language in English
	public boolean rightToLeft; // display text from right to left
	public boolean asianTokenizer; // word forms are single characters
	public boolean russianTokenizer; // word forms may contain apostrophes

	// We have to use Locale, and Java Collator object for locale sensitive string compare.
	public Locale locale;
	public ArrayList<String> chartable;

	// FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
	public static final String UTF8_BOM = "\uFEFF";

	/**
	 * Constructor.
	 * 
	 * @param isoname
	 *            - ISO 2-letter code for language
	 */
	public Language(String isoname)
	{
		isoName = isoname;
		chartable = null;
		natName = null;
		engName = null;

		if (isoname == null)
			return;

		// SPECIAL LANGUAGE acadien
		if (isoname.equals("ac"))
		{
			natName = "Acadien";
			engName = "Acadian";
			locale = new Locale("fr", "CA"); 
			rightToLeft = false;
		}
		else if (isoname.equals("ar"))
		{
			engName = "Arabic";
			locale = new Locale("ar", "SA"); 
			rightToLeft = true;
		}
		else if (isoname.equals("bg"))
		{
			locale = new Locale("bg", "BG"); 
			rightToLeft = false;
		}
		// SPECIAL LANGUAGE berbere
		else if (isoname.equals("br"))
		{
			natName = "Berber";
			engName = "Berber";
			locale = new Locale("en", "US"); 
			rightToLeft = false;
		}
		// belarussian
		else if (isoname.equals("be"))
		{
			locale = new Locale("be", "BY"); 
			rightToLeft = false;
			russianTokenizer = true;
		}
		else if (isoname.equals("ca"))
		{
			locale = new Locale("ca", "ES");
			rightToLeft = false;
		}
		else if (isoname.equals("cz"))
		{
			locale = new Locale("cs", "CZ"); 
			rightToLeft = false;
		}
		else if (isoname.equals("da"))
		{
			locale = new Locale("da", "DK"); 
			rightToLeft = false;
		}
		else if (isoname.equals("de"))
		{
			locale = new Locale("de", "DE"); 
			rightToLeft = false;
		}
		// Greek
		else if (isoname.equals("el"))
		{
			locale = new Locale("el", "GR"); 
			rightToLeft = false;
		}
		else if (isoname.equals("en"))
		{
			locale = new Locale("en", "US"); 
			rightToLeft = false;
		}
		else if (isoname.equals("fa"))
		{
			locale = new Locale("fa", "IR"); 
			rightToLeft = true;
		}
		else if (isoname.equals("fi"))
		{
			locale = new Locale("fi", "FI"); 
			rightToLeft = false;
		}
		else if (isoname.equals("fr"))
		{
			locale = new Locale("fr", "FR"); 
			rightToLeft = false;
		}
		// hebrew
		else if (isoname.equals("he"))
		{
			locale = new Locale("he", "IL");
			rightToLeft = true;
		}
		// hindi
		else if (isoname.equals("hi"))
		{
			locale = new Locale("hi", "IN"); 
			rightToLeft = false;
		}
		else if (isoname.equals("hr"))
		{
			locale = new Locale("hr", "HR"); 
			rightToLeft = false;
		}
		else if (isoname.equals("hu"))
		{
			locale = new Locale("hu", "HU"); 
			rightToLeft = false;
		}
		// armenian
		else if (isoname.equals("hy"))
		{
			locale = new Locale("hy", "AM"); 
			rightToLeft = false;
		}
		else if (isoname.equals("ma"))
		{
			natName = "kreol morisyen";
			engName = "Mauritian";
			locale = new Locale("fr", "FR"); 
			rightToLeft = false;
		}
		// Italian
		else if (isoname.equals("it"))
		{
			locale = new Locale("it", "IT"); 
			rightToLeft = false;
		}
		// Japanese
		else if (isoname.equals("ja"))
		{
			locale = new Locale("ja", "JP");
			rightToLeft = false;
			asianTokenizer = true;
		}
		// Korean
		else if (isoname.equals("ko"))
		{
			locale = new Locale("ko", "KR");
			rightToLeft = false;
		}
		// SPECIAL LANGUAGE Kurd
		else if (isoname.equals("ku"))
		{
			natName = "kurmanc\u00ee";
			engName = "Kurdish";
			locale = new Locale("en", "US"); 
			rightToLeft = false;
		}
		else if (isoname.equals("ks"))
		{
			natName = "Kurdish (sorani dialect)";
			engName = "\u06a9\u0648\u0631\u062f\u064a";
			locale = new Locale("fa", "IR"); 
			rightToLeft = true;
		}
		// SPECIAL LANGUAGE Latin
		else if (isoname.equals("la"))
		{
			natName = "Lingva Latina";
			engName = "Latin";
			locale = new Locale("en", "US");
			rightToLeft = false;
		}
		// SPECIAL LANGUAGE Malgache
		else if (isoname.equals("mg"))
		{
			natName = "Malagasy";
			engName = "Malagasy";
			locale = new Locale("en", "US");
			rightToLeft = false;
		}
		else if (isoname.equals("mk"))
		{
			locale = new Locale("mk", "MK");
			rightToLeft = false;
		}
		// SPECIAL LANGUAGE Nez Perc\u00e9
		else if (isoname.equals("np"))
		{
			natName = "Nimipuu";
			engName = "Nez Perce";
			locale = new Locale("en", "US");
			rightToLeft = false;
		}
		else if (isoname.equals("pt"))
		{
			locale = new Locale("pt", "PT"); 
			rightToLeft = false;
		}
		else if (isoname.equals("ro"))
		{
			locale = new Locale("ro", "RO"); 
			rightToLeft = false;
		}
		else if (isoname.equals("ru"))
		{
			locale = new Locale("ru", "RU");
			rightToLeft = false;
			russianTokenizer = true;
		}
		else if (isoname.equals("pl"))
		{
			locale = new Locale("pl", "PL");
			rightToLeft = false;
		}
		else if (isoname.equals("sa"))
		{
			locale = new Locale("sa", "IN"); 
			rightToLeft = false;
		}
		else if (isoname.equals("sy"))
		{
			natName = "Syriac";
			engName = "Syriac";
			locale = new Locale("ar", "SA"); 
			rightToLeft = true;
		}
		else if (isoname.equals("sp"))
		{
			locale = new Locale("es", "ES"); 
			rightToLeft = false;
		}
		else if (isoname.equals("sq"))
		{
			locale = new Locale("sq", "AL"); 
			rightToLeft = false;
		}
		// serbo-croatian cyrilic
		else if (isoname.equals("sr"))
		{
			// Serbian (Cyrillic) Serbia
			locale = new Locale("sr", "RS");
			rightToLeft = false;
		}
		// kiswahili
		else if (isoname.equals("sw"))
		{
			natName = "Kiswahili";
			engName = "Kiswahili";
			locale = new Locale("sw", "KE"); 
			rightToLeft = false;
		}
		else if (isoname.equals("th"))
		{
			locale = new Locale("th", "TH"); 
			rightToLeft = false;
			asianTokenizer = true;
		}
		// SPECIAL LANGUAGE tamajaq
		else if (isoname.equals("tm"))
		{
			natName = "Tamajaq";
			engName = "Tamajaq";
			locale = new Locale("en", "US"); 
			rightToLeft = false;
		}
		// Turkish
		else if (isoname.equals("tr"))
		{
			locale = new Locale("tr", "TR"); 
			rightToLeft = false;
		}
		// Ukrainian
		else if (isoname.equals("uk"))
		{
			locale = new Locale("uk", "UA"); 
			rightToLeft = false;
		}
		else if (isoname.equals("vi"))
		{
			locale = new Locale("vi", "VN"); 
			rightToLeft = false;
		}
		else if (isoname.equals("wo"))
		{
			locale = new Locale("wo", "SN"); 
			rightToLeft = false;
		}
		// chinese
		else if (isoname.equals("zh"))
		{
			locale = new Locale("zh", "TW"); 
			rightToLeft = false;
			asianTokenizer = true;
		}
		if (natName == null) // if not a special language
		{
			if (locale == null)
			{
				locale = new Locale("en", "US"); 
				natName = "Unknown";
				engName = "Unknown";
			}
			else
			{
				natName = locale.getDisplayName(locale); 
				engName = locale.getDisplayName(Locale.ENGLISH); 
			}
		}
	}

	/**
	 * List all NooJ available languages
	 * 
	 * @return list of isonames (ISO 2-letter codes) for available languages
	 */
	public static String[] getAllLanguages()
	{
		ArrayList<String> all = new ArrayList<String>();
		all.add("ac"); // SPECIAL LANGUAGE Acadien
		all.add("ar");
		all.add("be"); // belarusian
		all.add("bg");
		all.add("br"); // SPECIAL LANGUAGE berber
		all.add("ca"); // Catalan
		all.add("cz"); // Czech
		all.add("da"); // Danish
		all.add("de"); // German
		all.add("el"); // Greek
		all.add("en");
		all.add("fa"); // farsi = persian
		all.add("fi"); // finnish
		all.add("fr");
		all.add("he");
		all.add("hr"); // Croatian
		all.add("hi"); // hindi
		all.add("hu");
		all.add("hy"); // Armenian
		all.add("it");
		all.add("ja"); // Japanese
		all.add("ko");
		all.add("ku"); // Kurde
		all.add("ks"); // Kurde Surani
		all.add("la"); // SPECIAL LANGUAGE Latin
		all.add("mk"); // Macedonian
		all.add("ma"); // Creole mauritien
		all.add("mg"); // Malgache
		all.add("np"); // nez perc\u00e9
		all.add("pl");
		all.add("pt");
		all.add("ro");
		all.add("ru");
		all.add("sa"); // Sanscrit
		all.add("sy"); // Syriac
		all.add("sp");
		all.add("sq"); // Albanian
		all.add("sr");
		all.add("sw");
		all.add("th"); // Thai
		all.add("tm"); // SPECIAL LANGUAGE tamajaq
		all.add("tr"); // turkish
		all.add("uk"); // Ukrainian
		all.add("vi"); // vietnamese
		all.add("wo"); // wolof
		all.add("zh"); // chinese

		return all.toArray(new String[all.size()]);
	}

	/**
	 * Checks whether language with given ISO name is available.
	 * 
	 * @param isoName
	 *            - ISO 2-letter code to be checked
	 * @return true if language is available, false otherwise
	 */
	public static boolean isALanguage(String isoName)
	{
		String[] alllanguages = getAllLanguages();
		int index = -1;
		for (int i = 0; i < alllanguages.length; i++)
		{
			if (isoName.equals(alllanguages[i]))
			{
				index = i;
			}
		}
		return (index != -1);
	}

	// single character tests

	/**
	 * Checks whether given character is a vowel.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is a vowel, false otherwise.
	 */
	static boolean isVowel(char c)
	{
		switch (c)
		{
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
				return true;
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return true;

				// hebrew
			case '\u05b0':
			case '\u05b1':
			case '\u05b2':
			case '\u05b3':
			case '\u05b4':
			case '\u05b5':
			case '\u05b6':
			case '\u05b7':
			case '\u05b8':
			case '\u05c2':
			case '\u05c1':
			case '\u05b9':
			case '\u05bc':
			case '\u05bb':
				return true;

				// arabic
			case '\u064C': // Dhammatan
			case '\u064B': // Fathatan
			case '\u064D': // Kasratan
			case '\u064F': // Dhamma
			case '\u064E': // Fatha
			case '\u0650': // Kasra
			case '\u0652': // Sukun
			case '\u0651': // Shadda
				return true;

			default:
				return false;
		}
	}

	/**
	 * Checks whether given character is a letter.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is a letter, false otherwise.
	 */
	public static boolean isLetter(char c)
	{
		if (Character.isLetter(c))
		{
			return true;
		}

		switch (c)
		{
		// this is a Catalan letter
			case '\u00b7':
				return true;
				// these Armenian punctuation marks must be processed as letters
			case '\u055c':
				return true; // these three marks are IN words
			case '\u055e':
				return true;
			case '\u055b':
				return true;

				// these Hebrew vowels must be processed as letters
			case '\u05b0':
				return true;
			case '\u05b1':
				return true;
			case '\u05b2':
				return true;
			case '\u05b3':
				return true;
			case '\u05b4':
				return true;
			case '\u05b5':
				return true;
			case '\u05b6':
				return true;
			case '\u05b7':
				return true;
			case '\u05b8':
				return true;
			case '\u05c2':
				return true;
			case '\u05c1':
				return true;
			case '\u05b9':
				return true;
			case '\u05bc':
				return true;
			case '\u05bb':
				return true;

				// these Arabic characters must be processed as letters
			case '\u064C': // Dhammatan
				return true;
			case '\u064B': // Fathatan
				return true;
			case '\u064D': // Kasratan
				return true;
			case '\u064F': // Dhamma
				return true;
			case '\u064E': // Fatha
				return true;
			case '\u0650': // Kasra
				return true;
			case '\u0652': // Sukun
				return true;
			case '\u0651': // Shadda
				return true;

				// these Vietnamese characters must be processed as letters
			case '\u0301':
				return true;
			case '\u0300':
				return true;
			case '\u0303':
				return true;
			case '\u0309':
				return true;
			case '\u0323':
				return true;
			case '\u01A0':
				return true;

				// these Hindi characters must be processed as letters
			case '\u0901':
				return true;
			case '\u0902':
				return true;
			case '\u0903':
				return true;
			case '\u093C':
				return true;
			case '\u093E':
				return true;
			case '\u093F':
				return true;
			case '\u0940':
				return true;
			case '\u0941':
				return true;
			case '\u0942':
				return true;
			case '\u0943':
				return true;
			case '\u0945':
				return true;
			case '\u0947':
				return true;
			case '\u0948':
				return true;
			case '\u0949':
				return true;
			case '\u094B':
				return true;
			case '\u094C':
				return true;
			case '\u094D':
				return true;

				// this russian accent must be processed as a letter
			case '\u0341':
				return true;

				// this tamajaq letter must be processed as a letter
			case '\u030C':
				return true;

			default:
				return false;
		}
	}

	/**
	 * Checks whether given character is not accented.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is not accented, false if it is.
	 */
	private static boolean isNotAccented(char c)
	{
		switch (c)
		{
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return true;

				// Greek
			case '\u0391':
			case '\u0395':
			case '\u0389':
			case '\u0399':
			case '\u039f':
			case '\u03a5':
			case '\u03a9':
			case '\u03b1':
			case '\u03b5':
			case '\u03b7':
			case '\u03b9':
			case '\u03bf':
			case '\u03c5':
			case '\u03c9':
				return true;
		}
		return false;
	}

	/**
	 * Checks whether given character is accented.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is accented, false if it is not.
	 */
	static boolean isAccented(char c)
	{
		switch (c)
		{
			case '\u00e1':
			case '\u00e0':
			case '\u00e2':
			case '\u00e4':
			case '\u00e9':
			case '\u00e8':
			case '\u00ea':
			case '\u00eb':
			case '\u00ed':
			case '\u00ec':
			case '\u00ee':
			case '\u00ef':
			case '\u00f4':
			case '\u00f3':
			case '\u00f2':
			case '\u00f6':
			case '\u0151': // hungarian
			case '\u00fa':
			case '\u00f9':
			case '\u00fb':
			case '\u00fc':
			case '\u0171': // hungarian
				return true;

			case '\u00c1':
			case '\u00c0':
			case '\u00c2':
			case '\u00c4':
			case '\u00c9':
			case '\u00c8':
			case '\u00ca':
			case '\u00cb':
			case '\u00cc':
			case '\u00ce':
			case '\u00cf':
			case '\u00d3':
			case '\u00d2':
			case '\u00d4':
			case '\u00d6':
			case '\u00da':
			case '\u00d9':
			case '\u00db':
			case '\u00dc':
				return true;

				// spiritus lenis
			case '\u1f08':
			case '\u1f18':
			case '\u1f28':
			case '\u1f38':
			case '\u1f48':
			case '\u1f68':

			case '\u1f00':
			case '\u1f10':
			case '\u1f20':
			case '\u1f30':
			case '\u1f40':
			case '\u03cd':
			case '\u03ce':
			case '\u0390':
			case '\u03b0':
			case '\u03ca':
			case '\u03cb':
				return true;

				// spiritus asper
			case '\u1f09':
			case '\u1f19':
			case '\u1f29':
			case '\u1f39':
			case '\u1f49':
			case '\u1fec':
			case '\u1f59':
			case '\u1f69':

			case '\u1f01':
			case '\u1f11':
			case '\u1f21':
			case '\u1f31':
			case '\u1f41':
			case '\u1fe5':
			case '\u1f51':
			case '\u1f61':
				return true;

				// circumflex
			case '\u1fb6':
			case '\u1fc6':
			case '\u1fe6':
			case '\u1ff6':
				return true;

				// iota subscript

			case '\u1fcc':
			case '\u1ffc':
			case '\u1fb3':
			case '\u1fc3':
			case '\u1ff3':
				return true;

				// grave accent
			case '\u1f70':
			case '\u1f72':
			case '\u1f74':
			case '\u1f76':
			case '\u1f78':
			case '\u1f7a':
			case '\u1f7c':
				return true;

			case '\u1fba':
			case '\u1fc8':
			case '\u1fca':
			case '\u1fda':
			case '\u1ff8':
			case '\u1fea':
			case '\u1ffa':
				return true;

				// acute accent
			case '\u1f71':
			case '\u1f73':
			case '\u1f75':
			case '\u1f77':
			case '\u1f79':
			case '\u1f7b':
			case '\u1f7d':
				return true;

			case '\u1fbb':
			case '\u1fc9':
			case '\u1fcb':
			case '\u1fdb':
			case '\u1ff9':
			case '\u1ffb':
				return true;

				// Russian
			case '\u0451':
				return true;
			case '\u0401':
				return true;

			default:
				return false;
		}
	}

	/**
	 * Checks whether given character is Katakana.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is Katakana, false otherwise.
	 */
	static boolean isKatakana(char c)
	{
		return (c >= '\u30A0' && c <= '\u30FF');
	}

	/**
	 * Checks whether given character is UniHan.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is UniHan, false otherwise.
	 */
	static boolean isUniHan(char c)
	{
		return ((c >= '\u4E00' && c <= '\u9FCF') || (c >= '\u3400' && c <= '\u4DBF'));
	}

	/**
	 * Checks whether given character is Hiragana.
	 * 
	 * @param c
	 *            - given character
	 * @return true if it is Hiragana, false otherwise.
	 */
	static boolean isHiragana(char c)
	{
		return (c >= '\u3040' && c <= '\u309F');
	}

	/**
	 * Replace Hebrew character with appropriate final character (for example, '\u05db' - kaf with '\u05da' - final kaf)
	 * 
	 * @param c
	 *            - character to finalize
	 * @return appropriate finalized character
	 */
	private static char finalize(char c)
	{
		switch (c)
		{
			case '\u05db':
				return '\u05da';
			case '\u05de':
				return '\u05dd';
			case '\u05e0':
				return '\u05df';
			case '\u05e4':
				return '\u05e3';
			case '\u05e6':
				return '\u05e5';

			case '\u05da':
				return '\u05db';
			case '\u05dd':
				return '\u05de';
			case '\u05df':
				return '\u05e0';
			case '\u05e3':
				return '\u05e4';
			case '\u05e5':
				return '\u05e6';
			default:
				return c;
		}
	}

	/**
	 * Transforms accented character to it's nonaccented match.
	 * 
	 * @param c
	 *            - given accented character
	 * @return nonaccented character
	 */
	private static char toNoAccent(char u)
	{
		switch (u)
		{
			case '\u00c1':
			case '\u00c0':
			case '\u00c2':
			case '\u00c4':
				return 'A';
			case '\u00c9':
			case '\u00c8':
			case '\u00ca':
			case '\u00cb':
				return 'E';
			case '\u00cd':
			case '\u00cc':
			case '\u00ce':
			case '\u00cf':
				return 'I';
			case '\u00d4':
			case '\u00d3':
			case '\u00d2':
			case '\u00d6':
				return 'O';
			case '\u0150':
				return '\u00d6'; // hungarian
			case '\u00da':
			case '\u00d9':
			case '\u00db':
			case '\u00dc':
				return 'U';
			case '\u0170':
				return '\u00dc'; // hungarian

			case '\u00e0':
			case '\u00e1':
			case '\u00e2':
			case '\u00e4':
				return 'a';
			case '\u00e9':
			case '\u00e8':
			case '\u00ea':
			case '\u00eb':
				return 'e';
			case '\u00ed':
			case '\u00ec':
			case '\u00ee':
			case '\u00ef':
				return 'i';
			case '\u00f4':
			case '\u00f3':
			case '\u00f2':
			case '\u00f6':
				return 'o';
			case '\u0151':
				return '\u00f6'; // hungarian
			case '\u00fa':
			case '\u00f9':
			case '\u00fb':
			case '\u00fc':
				return 'u';
			case '\u0171':
				return '\u00fc'; // hungarian

				// Greek

				// spiritus lenis
			case '\u1f08':
				return '\u0391';
			case '\u1f18':
				return '\u0395';
			case '\u1f28':
				return '\u0397';
			case '\u1f38':
				return '\u0399';
			case '\u1f48':
				return '\u039f';
			case '\u1f68':
				return '\u03a9';

			case '\u1f00':
				return '\u03b1';
			case '\u1f10':
				return '\u03b5';
			case '\u1f20':
				return '\u03b7';
			case '\u1f30':
				return '\u03b9';
			case '\u1f40':
				return '\u03bf';
			case '\u03cd':
				return '\u03c5';
			case '\u03ce':
				return '\u03c9';
			case '\u0390':
				return '\u03ca';
			case '\u03b0':
				return '\u03cb';
			case '\u03ca':
				return '\u03b9';
			case '\u03cb':
				return '\u03c5';

				// spiritus asper
			case '\u1f09':
				return '\u0391';
			case '\u1f19':
				return '\u0395';
			case '\u1f29':
				return '\u0397';
			case '\u1f39':
				return '\u0399';
			case '\u1f49':
				return '\u039f';
			case '\u1fec':
				return '\u03a1';
			case '\u1f59':
				return '\u03a5';
			case '\u1f69':
				return '\u03a9';

			case '\u1f01':
				return '\u03b1';
			case '\u1f11':
				return '\u03b5';
			case '\u1f21':
				return '\u03b7';
			case '\u1f31':
				return '\u03b9';
			case '\u1f41':
				return '\u03bf';
			case '\u1fe5':
				return '\u03c1';
			case '\u1f51':
				return '\u03c5';
			case '\u1f61':
				return '\u03c9';

				// circumflex
			case '\u1fb6':
				return '\u03b1';
			case '\u1fc6':
				return '\u03b7';
			case '\u1fe6':
				return '\u03c5';
			case '\u1ff6':
				return '\u03c9';

				// iota subscript

			case '\u1fcc':
				return '\u0397';
			case '\u1ffc':
				return '\u03a9';
			case '\u1fb3':
				return '\u03b1';
			case '\u1fc3':
				return '\u03b7';
			case '\u1ff3':
				return '\u03c9';

				// grave accent
			case '\u1f70':
				return '\u03b1';
			case '\u1f72':
				return '\u03b5';
			case '\u1f74':
				return '\u03b7';
			case '\u1f76':
				return '\u03b9';
			case '\u1f78':
				return '\u03bf';
			case '\u1f7a':
				return '\u03c5';
			case '\u1f7c':
				return '\u03c9';

			case '\u1fba':
				return '\u0391';
			case '\u1fc8':
				return '\u0395';
			case '\u1fca':
				return '\u0397';
			case '\u1fda':
				return '\u0399';
			case '\u1ff8':
				return '\u039f';
			case '\u1fea':
				return '\u03a5';
			case '\u1ffa':
				return '\u03a9';

				// acute accent
			case '\u1f71':
				return '\u03b1';
			case '\u1f73':
				return '\u03b5';
			case '\u1f75':
				return '\u03b7';
			case '\u1f77':
				return '\u03b9';
			case '\u1f79':
				return '\u03bf';
			case '\u1f7b':
				return '\u03c5';
			case '\u1f7d':
				return '\u03c9';

			case '\u1fbb':
				return '\u0391';
			case '\u1fc9':
				return '\u0395';
			case '\u1fcb':
				return '\u0397';
			case '\u1fdb':
				return '\u0399';
			case '\u1ff9':
				return '\u039f';
			case '\u1ffb':
				return '\u03a9';

				// Russian
			case '\u0451':
				return '\u0435';
			case '\u0401':
				return '\u0415';

			default:
				return u;
		}
	}

	/**
	 * Transforms character without acute accent to it's acuted match.
	 * 
	 * @param c
	 *            - given nonacuted character
	 * @return acuted character
	 */
	private char toAcute(char u)
	{
		switch (u)
		{
			case 'A':
				return '\u00c1';
			case 'E':
				return '\u00c9';
			case 'I':
				return '\u00cd';
			case 'O':
				return '\u00d3';
			case 'U':
				return '\u00da';

			case 'a':
				return '\u00e1';
			case 'e':
				return '\u00e9';
			case 'i':
				return '\u00ed';
			case 'o':
				return '\u00f3';
			case 'u':
				return '\u00fa';

				// Greek
			case '\u0391':
				return '\u0386';
			case '\u0395':
				return '\u0388';
			case '\u0397':
				return '\u0389';
			case '\u0399':
				return '\u038a';
			case '\u039f':
				return '\u038c';
			case '\u03a5':
				return '\u038e';
			case '\u03a9':
				return '\u038f';

			case '\u03b1':
				return '\u03ac';
			case '\u03b5':
				return '\u03ad';
			case '\u03b7':
				return '\u03ae';
			case '\u03b9':
				return '\u03af';
			case '\u03bf':
				return '\u03cc';
			case '\u03c5':
				return '\u03cd';
			case '\u03c9':
				return '\u03ce';
			case '\u03ca':
				return '\u0390';
			case '\u03cb':
				return '\u03b0';

			default:
				return u;
		}
	}

	/**
	 * Transforms character without grave accent to it's graved match.
	 * 
	 * @param c
	 *            - given nongraved character
	 * @return graved character
	 */
	private char toGrave(char u)
	{
		switch (u)
		{
			case 'A':
				return '\u00c0';
			case 'E':
				return '\u00c8';
			case 'I':
				return '\u00cc';
			case 'O':
				return '\u00d2';
			case 'U':
				return '\u00d9';

			case 'a':
				return '\u00e0';
			case 'e':
				return '\u00e8';
			case 'i':
				return '\u00ec';
			case 'o':
				return '\u00f2';
			case 'u':
				return '\u00f9';

				// Greek
			case '\u0391':
				return '\u1fba';
			case '\u0395':
				return '\u1fc8';
			case '\u0389':
				return '\u1fca';
			case '\u0399':
				return '\u1fda';
			case '\u039f':
				return '\u1ff8';
			case '\u03a5':
				return '\u1fea';
			case '\u03a9':
				return '\u1ffa';
			case '\u03b1':
				return '\u1f70';
			case '\u03b5':
				return '\u1f72';
			case '\u03b7':
				return '\u1f74';
			case '\u03b9':
				return '\u1f76';
			case '\u03bf':
				return '\u1f78';
			case '\u03c5':
				return '\u1f7a';
			case '\u03c9':
				return '\u1f7c';

			default:
				return u;
		}
	}

	/**
	 * Transforms character without circumflex accent to it's circumflexed match.
	 * 
	 * @param c
	 *            - given noncircumflexed character
	 * @return circumflexed character
	 */
	private char toCircumflex(char u)
	{
		switch (u)
		{
			case 'A':
				return '\u00c2';
			case 'E':
				return '\u00ca';
			case 'I':
				return '\u00ce';
			case 'O':
				return '\u00d4';
			case 'U':
				return '\u00db';

			case 'a':
				return '\u00e2';
			case 'e':
				return '\u00ea';
			case 'i':
				return '\u00ee';
			case 'o':
				return '\u00f4';
			case 'u':
				return '\u00fb';

				// Greek
			case '\u03b1':
				return '\u1fb6';
			case '\u03b7':
				return '\u1fc6';
			case '\u03c5':
				return '\u1fe6';
			case '\u03c9':
				return '\u1ff6';
			default:
				return u;
		}
	}

	/**
	 * Transforms character without trema accent to it's character with trema accent.
	 * 
	 * @param c
	 *            - given character without trema accent
	 * @return character with trema accent
	 */
	private char toTrema(char u)
	{
		switch (u)
		{
			case 'A':
				return '\u00c4';
			case 'E':
				return '\u00cb';
			case 'I':
				return '\u00cf';
			case 'O':
				return '\u00d6';
			case 'U':
				return '\u00dc';

			case 'a':
				return '\u00e4';
			case 'e':
				return '\u00eb';
			case 'i':
				return '\u00ef';
			case 'o':
				return '\u00f6';
			case 'u':
				return '\u00fc';

				// GREEK
			case '\u0391':
				return '\u1f08';
			case '\u0395':
				return '\u1f18';
			case '\u0397':
				return '\u1f28';
			case '\u0399':
				return '\u1f38';
			case '\u039f':
				return '\u1f48';
			case '\u03a9':
				return '\u1f68';

			case '\u03b1':
				return '\u1f00';
			case '\u03b5':
				return '\u1f10';
			case '\u03b7':
				return '\u1f20';
			case '\u03b9':
				return '\u03ca';
			case '\u03bf':
				return '\u1f40';
			case '\u03c9':
				return '\u03ce';
			case '\u03c5':
				return '\u03cb';

			default:
				return u;
		}
	}

	/**
	 * Transforms the character to it's iota transcript match.
	 * 
	 * @param u
	 *            - given character
	 * @return transformed character
	 */
	private char toIota(char u)
	{
		// Greek
		switch (u)
		{
			case '\u03b1':
				return '\u1fb3';
			case '\u1f00':
				return '\u1f80';
			case '\u1f01':
				return '\u1f81';
			case '\u1f02':
				return '\u1f82';
			case '\u1f03':
				return '\u1f83';
			case '\u1f04':
				return '\u1f84';
			case '\u1f05':
				return '\u1f85';
			case '\u1f06':
				return '\u1f86';
			case '\u1f07':
				return '\u1f87';
			case '\u1f08':
				return '\u1f88';
			case '\u1f09':
				return '\u1f89';
			case '\u1f0a':
				return '\u1f8a';
			case '\u1f0b':
				return '\u1f8b';
			case '\u1f0c':
				return '\u1f8c';
			case '\u1f0d':
				return '\u1f8d';
			case '\u1f0e':
				return '\u1f8e';
			case '\u1f0f':
				return '\u1f8f';
			case '\u03c9':
				return '\u1ff3';
			case '\u1f60':
				return '\u1fa0';
			case '\u1f61':
				return '\u1fa1';
			case '\u1f62':
				return '\u1fa2';
			case '\u1f63':
				return '\u1fa3';
			case '\u1f64':
				return '\u1fa4';
			case '\u1f65':
				return '\u1fa5';
			case '\u1f66':
				return '\u1fa6';
			case '\u1f67':
				return '\u1fa7';
			case '\u1f68':
				return '\u1fa8';
			case '\u1f69':
				return '\u1fa9';
			case '\u1f6a':
				return '\u1faa';
			case '\u1f6b':
				return '\u1fab';
			case '\u1f6c':
				return '\u1fac';
			case '\u1f6d':
				return '\u1fad';
			case '\u1f6e':
				return '\u1fae';
			case '\u1f6f':
				return '\u1faf';
			case '\u03b7':
				return '\u1fc3';
			case '\u1f20':
				return '\u1f90';
			case '\u1f21':
				return '\u1f91';
			case '\u1f22':
				return '\u1f92';
			case '\u1f23':
				return '\u1f93';
			case '\u1f24':
				return '\u1f94';
			case '\u1f25':
				return '\u1f95';
			case '\u1f26':
				return '\u1f96';
			case '\u1f27':
				return '\u1f97';
			case '\u1f74':
				return '\u1fc2';
			case '\u1f75':
				return '\u1fc4';
			case '\u1fc6':
				return '\u1fc7';
			case '\u1f28':
				return '\u1f98';
			case '\u1f29':
				return '\u1f99';
			case '\u1f2a':
				return '\u1f9a';
			case '\u1f2b':
				return '\u1f9b';
			case '\u1f2c':
				return '\u1f9c';
			case '\u1f2d':
				return '\u1f9d';
			case '\u1f2e':
				return '\u1f9e';
			case '\u1f2f':
				return '\u1f9f';

			default:
				return u;
		}
	}

	/**
	 * Checks whether given string is written in lower case.
	 * 
	 * @param word
	 *            - string to be checked
	 * @return true if it is written in lower case, false otherwise
	 */
	static boolean isLower(String word)
	{
		return StringUtils.isAllLowerCase(word);
	}

	/**
	 * Checks whether given string is written in upper case.
	 * 
	 * @param word
	 *            - string to be checked
	 * @return true if it is written in upper case, false otherwise
	 */
	static boolean isUpper(String word)
	{
		return StringUtils.isAllUpperCase(word);
	}

	/**
	 * Checks whether given string is capitalized.
	 * 
	 * @param word
	 *            - string to be checked
	 * @return true if it is capitalized, false otherwise
	 */
	static boolean isCapital(String word)
	{
		if (word == null || word.length() == 0)
		{
			return false;
		}
		if (!Character.isUpperCase(word.charAt(0)))
		{
			return false;
		}
		for (int i = 1; i < word.length(); i++)
		{
			if (!Character.isLowerCase(word.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether given string is a compound.
	 * 
	 * @param word
	 *            - string to be checked
	 * @return true if it is a compound, false otherwise
	 */
	final boolean isACompound(String word)
	{
		if (word == null || word.length() == 0)
		{
			return false;
		}
		for (int i = 0; i < word.length(); i++)
		{
			if (!isLetter(word.charAt(i)))
			{
				return true;
			}
		}
		return false;
	}

	// region letters and words comparison

	/**
	 * 
	 * @param filePath
	 * @param errMessage
	 * @return
	 * @throws IOException
	 */
	public final boolean loadCharacterVariants(String filePath, StringBuilder errMessage) throws IOException
	{
		ParameterCheck.mandatory("errMessage", errMessage);

		boolean thereIsNoError = true;
		this.chartable = new ArrayList<String>();
		errMessage.delete(0, errMessage.length());

		BufferedReader reader = null;
		String line;

		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));

			for (line = reader.readLine(); line != null; line = reader.readLine())
			{
				if (line.equals(""))
				{
					continue;
				}

				// Removing UTF8 BOM if present
				if (line.startsWith(UTF8_BOM))
					line = line.substring(1);

				if (line.charAt(0) == '#')
				{
					continue;
				}
				if (line.length() < 3)
				{
					// this value is set to passed parametar "out string errMessage"
					errMessage.append("Line \"" + line + "\" is too short");
					Dic.writeLog(errMessage.toString());
					thereIsNoError = false;
					break;
				}
				int i;
				for (i = 0; i < line.length() && line.charAt(i) != ':'; i++)
				{
					;
				}
				if (i >= line.length())
				{
					errMessage.append("Cannot parse line \"" + line + "\" (no colon)");
					Dic.writeLog(errMessage.toString());
					thereIsNoError = false;
					break;
				}

				String pattern = line.substring(0, i);
				String replace = line.substring(i + 1);
				this.chartable.add(pattern);
				this.chartable.add(replace);
			}

		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		return thereIsNoError;
	}

	/**
	 * Checks whether given characters match.
	 * 
	 * @param ctext
	 *            -first character
	 * @param cdic
	 *            - second character
	 * @return true if characters match, false otherwise
	 */
	final boolean doLettersMatch(char ctext, char cdic)
	{
		if (ctext == cdic) // eg. 'b' == 'b'
		{
			return true;
		}

	

		if (this.isoName.equals("ar") || this.isoName.equals("he") || this.isoName.equals("ja")
				|| this.isoName.equals("ko") || this.isoName.equals("zh")) // no uppercase/lowercase to match)
		{
			return false;
		}

		if (this.isoName.equals("ru"))
		{
			if (Language.isAccented(cdic))
			{
				cdic = Language.toNoAccent(cdic);
			}
			if (ctext == cdic) // eg. 'b' == 'b'
			{
				return true;
			}
		}

		if (Character.isUpperCase(ctext))
		{
			char cdicupper = Character.toUpperCase(cdic);
			if (ctext == cdicupper) // eg. 'B' == 'b' or '\u00ca' == '\u00ea'
			{
				return true;
			}
			if (!this.isoName.equals("de") && ctext == toNoAccent(cdicupper)) // eg. 'E' == '\u00ea'
			{
				return true;
			}
		}
		return false; // both letters are lowercase but not equal
	}

	/**
	 * 
	 * @param ctext
	 * @param cdic
	 * @return
	 */
	final boolean doWordFormsMatch(String ctext, String cdic)
	{
		// TODO: process chinese character table
		if (cdic.length() == 0)
		{
			if (ctext.length() == 0)
			{
				return true;
			}
			return false;
		}

		if (Character.isUpperCase(cdic.charAt(0)))
		{
			Collator collator = Collator.getInstance(this.locale);
			// TODO Need to test Collator and next option
			

			return (collator.compare(ctext, cdic) == 0);
		}
		else
		{
			Collator collator = Collator.getInstance(this.locale);
			

			return (collator.compare(ctext.toLowerCase(this.locale), cdic.toLowerCase(this.locale)) == 0);
		}
	}

	/**
	 * Parses a sequence of tokens into array of tokens.
	 * 
	 * @param sequence
	 *            - string to be parsed
	 * @return array of tokens
	 */
	final String[] parseSequenceOfTokens(String sequence)
	{
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < sequence.length();)
		{
			int j;
			if (sequence.charAt(i) == '\\')
			{
				result.add(sequence.substring(i, i + 2));
				i += 2;
				continue;
			}
			else if (sequence.charAt(i) == '<')
			{
				int embeddedAngle = 0;
				for (j = 1; (i + j) < sequence.length()
						&& (sequence.charAt(i + j) != '>' || (sequence.charAt(i + j) == '>' && embeddedAngle != 0)); j++)
				{
					if (sequence.charAt(i + j) == '<')
					{
						embeddedAngle++;
					}
					else if (sequence.charAt(i + j) == '>')
					{
						embeddedAngle--;
					}
				}
				if (i + j >= sequence.length())
				{
					return null;
				}
				j++;
				result.add(sequence.substring(i, i + j));
				i += j;
				continue;
			}
			else if (sequence.charAt(i) == '"')
			{
				for (j = 1; (i + j) < sequence.length() && sequence.charAt(i + j) != '"'; j++)
				{
					;
				}
				if (i + j >= sequence.length())
				{
					return null;
				}
				j++;
				result.add(sequence.substring(i, i + j));
				i += j;
				continue;
			}
			else
			{
				if (Character.isWhitespace(sequence.charAt(i)))
				{
					i++;
					continue;
				}
				else if (isLetter(sequence.charAt(i)))
				{
					for (j = 0; (i + j) < sequence.length() && isLetter(sequence.charAt(i + j)); j++)
					{
						;
					}
					result.add(sequence.substring(i, i + j));
					i += j;
					continue;
				}
				else
				{
					result.add(String.valueOf(sequence.charAt(i)));
					i++;
					continue;
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Parses a sequence of tokens into an array of tokens.
	 * 
	 * @param sequence
	 *            - string to be parsed
	 * @return array of tokens
	 */
	final String[] parseSequenceOfTokensAndMetaNodes(String sequence)
	{
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < sequence.length();)
		{
			int j;
			if (sequence.charAt(i) == '\\')
			{
				result.add(sequence.substring(i, i + 2));
				i += 2;
				continue;
			}
			else if (sequence.charAt(i) == '<')
			{
				int embeddedAngle = 0;
				for (j = 1; (i + j) < sequence.length()
						&& (sequence.charAt(i + j) != '>' || (sequence.charAt(i + j) == '>' && embeddedAngle != 0)); j++)
				{
					if (sequence.charAt(i + j) == '<')
					{
						embeddedAngle++;
					}
					else if (sequence.charAt(i + j) == '>')
					{
						embeddedAngle--;
					}
				}
				if (i + j >= sequence.length())
				{
					return null;
				}
				j++;
				result.add(sequence.substring(i, i + j));
				i += j;
				continue;
			}
			else if (sequence.charAt(i) == '"')
			{
				for (j = 1; (i + j) < sequence.length() && sequence.charAt(i + j) != '"'; j++)
				{
					;
				}
				if (i + j >= sequence.length())
				{
					return null;
				}
				j++;
				result.add(sequence.substring(i, i + j));
				i += j;
				continue;
			}
			else if (sequence.charAt(i) == ':')
			{
				for (j = 1; (i + j) < sequence.length() && sequence.charAt(i + j) != '#'; j++)
				{
					;
				}
				if (j < sequence.length())
				{
					j++;
					result.add(sequence.substring(i, i + j));
				}
				else
				{
					result.add(sequence.substring(i));
				}
				i += j;
				continue;
			}
			else if (sequence.charAt(i) == '$')
			{
				for (j = 1; (i + j) < sequence.length() && sequence.charAt(i + j) != '#'; j++)
				{
					;
				}
				if (j < sequence.length())
				{
					j++;
					result.add(sequence.substring(i, i + j));
				}
				else
				{
					result.add(sequence.substring(i));
				}
				i += j;
				continue;
			}
			else
			{
				if (Character.isWhitespace(sequence.charAt(i)))
				{
					i++;
					continue;
				}
				else if (isLetter(sequence.charAt(i)))
				{
					for (j = 0; (i + j) < sequence.length() && isLetter(sequence.charAt(i + j)); j++)
					{
						;
					}
					result.add(sequence.substring(i, i + j));
					i += j;
					continue;
				}
				else
				{
					result.add(String.valueOf(sequence.charAt(i)));
					i++;
					continue;
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	// Language-Specific Morphological inflectional/derivational Operators

	/**
	 * Returns number of daguesh shin dots or daguesh sin dots in given string.
	 * 
	 * @param res
	 *            - text to be checked
	 * @param ipos
	 *            - position from which check starts
	 * @param val
	 *            - number of characters to check
	 * @return
	 */
	static int nbOfDagueshShinSinDotsIn(StringBuilder res, int ipos, int val)
	{
		ParameterCheck.mandatory("res", res);

		int val0 = val;
		int i = 0;
		while (i < val0 && ipos - i - 1 >= 0)
		{
			if (res.charAt(ipos - i - 1) == '\u05C1' || res.charAt(ipos - i - 1) == '\u05C2'
					|| res.charAt(ipos - i - 1) == '\u05BC') // shin dot or sin dot or daguesh
			{
				val0++;
			}
			i++;
		}
		return val0;
	}

	final int processInflection(String commands, StringBuilder res, int ic, int ires)
	{
		if (isoName.equals("ar"))
		{
			if (commands.charAt(ic + 1) == 'T')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u0629')
				{
					char c = '\u062A';
					res.setCharAt(ires - 1, c);
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'M')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u0646')
				{
					res.insert(ires, '\u0651');
					return ires + 1;
				}
				else
				{
					res.insert(ires, '\u0652');
					res.insert(ires + 1, '\u0646');
					return ires + 2;
				}
			}
			else if (commands.charAt(ic + 1) == 'Z')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u062A')
				{
					res.insert(ires, '\u0651');
					return ires + 1;
				}
				else
				{
					res.insert(ires, '\u0652');
					res.insert(ires + 1, '\u062A');
					return ires + 2;
				}
			}
		}
		else if (isoName.equals("br"))
		{
			if (commands.charAt(ic + 1) == 'D')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u0263')
				{
					res.setCharAt(ires - 1, 'q');
					res.insert(ires, 'q'); // insert
					ires++;
				}
				else if (res.charAt(ires - 1) == '\u1e0d')
				{
					res.setCharAt(ires - 1, '\u1e6d');
					res.insert(ires, '\u1e6d'); // insert
					ires++;
				}
				else
				{
					res.insert(ires, res.charAt(ires - 1)); // insert
					ires++;
				}
				return ires;
			}
		}
		else if (isoName.equals("hu"))
		{
			if (commands.charAt(ic + 1) == 'A') // remove accent on last accented letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toNoAccent(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c1')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == 'a')
				{
					res.setCharAt(ires - 1, '\u00e1');
				}
				else if (res.charAt(ires - 1) == 'e')
				{
					res.setCharAt(ires - 1, '\u00e9');
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'D')
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == 'b' && ires >= 2 && res.charAt(ires - 2) == res.charAt(ires - 1))
				{
					// bb=>bb do nothing
				}
				else if (res.charAt(ires - 1) == 'a' || res.charAt(ires - 1) == '\u00e1' || res.charAt(ires - 1) == 'h')
				{
					res.setCharAt(ires - 1, '\u00e1');
					res.insert(ires, 'v'); // a=>Ăˇv h=>hv
					ires++;
				}
				else if (res.charAt(ires - 1) == 'e' || res.charAt(ires - 1) == '\u00e9')
				{
					res.setCharAt(ires - 1, '\u00e9');
					res.insert(ires, 'v'); // \u00e9=>\u00e9v insert v
					ires++;
				}
				else if (res.charAt(ires - 1) == 'i' || res.charAt(ires - 1) == '\u00ed' || res.charAt(ires - 1) == 'o'
						|| res.charAt(ires - 1) == '\u00f3' || res.charAt(ires - 1) == '\u00f6'
						|| res.charAt(ires - 1) == '\u0151' || res.charAt(ires - 1) == 'u'
						|| res.charAt(ires - 1) == '\u00fa' || res.charAt(ires - 1) == '\u00fc'
						|| res.charAt(ires - 1) == '\u0171')
				{
					res.insert(ires, 'v'); // insert v
					ires++;
				}
				else if (ires >= 3)
				{
					if (res.charAt(ires - 3) == 'c' && res.charAt(ires - 2) == 'c' && res.charAt(ires - 1) == 's')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 'g' && res.charAt(ires - 2) == 'g' && res.charAt(ires - 1) == 'y')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 'l' && res.charAt(ires - 2) == 'l' && res.charAt(ires - 1) == 'y')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 'n' && res.charAt(ires - 2) == 'n' && res.charAt(ires - 1) == 'y')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 't' && res.charAt(ires - 2) == 't' && res.charAt(ires - 1) == 'y')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 's' && res.charAt(ires - 2) == 's' && res.charAt(ires - 1) == 'z')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 'z' && res.charAt(ires - 2) == 'z' && res.charAt(ires - 1) == 's')
					{
						// do nothing
					}
					else if (res.charAt(ires - 3) == 'd' && res.charAt(ires - 2) == 'z' && res.charAt(ires - 1) == 's')
					{
						// => ddzs
						res.setCharAt(ires - 2, 'd');
						res.setCharAt(ires - 1, 'z');
						res.insert(ires, 's');
						ires++;
					}
				}
				else if (ires >= 2)
				{
					if (res.charAt(ires - 2) == 'c' && res.charAt(ires - 1) == 's')
					{
						// cs => ccs
						res.setCharAt(ires - 1, 'c');
						res.insert(ires, 's');
						ires++;
					}
					else if (res.charAt(ires - 2) == 'd' && res.charAt(ires - 1) == 'z')
					{
						// dz => ddz
						res.setCharAt(ires - 1, 'd');
						res.insert(ires, 'z');
						ires++;
					}
					else if (res.charAt(ires - 2) == 'g' && res.charAt(ires - 1) == 'y')
					{
						// gy => ggy
						res.setCharAt(ires - 1, 'g');
						res.insert(ires, 'y');
						ires++;
					}
					else if (res.charAt(ires - 2) == 'l' && res.charAt(ires - 1) == 'y')
					{
						// ly => lly
						res.setCharAt(ires - 1, 'l');
						res.insert(ires, 'y');
						ires++;
					}
					else if (res.charAt(ires - 2) == 'n' && res.charAt(ires - 1) == 'y')
					{
						// ny => nny
						res.setCharAt(ires - 1, 'n');
						res.insert(ires, 'y');
						ires++;
					}
					else if (res.charAt(ires - 2) == 's' && res.charAt(ires - 1) == 'z')
					{
						// sz => ssz
						res.setCharAt(ires - 1, 's');
						res.insert(ires, 'z');
						ires++;
					}
					else if (res.charAt(ires - 2) == 't' && res.charAt(ires - 1) == 'y')
					{
						// ty => tty
						res.setCharAt(ires - 1, 't');
						res.insert(ires, 'y');
						ires++;
					}
					else if (res.charAt(ires - 2) == 'z' && res.charAt(ires - 1) == 's')
					{
						// zs => zzs
						res.setCharAt(ires - 1, 'z');
						res.insert(ires, 's');
						ires++;
					}
				}
				else
				{
					res.insert(ires, res.charAt(ires - 1)); // just duplicate
					ires++;
				}
				return ires;
			}
		}
		else if (isoName.equals("sp"))
		{
			if (commands.charAt(ic + 1) == 'U') // replace e with ie and o with ue
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (res.charAt(j) == 'e' || res.charAt(j) == 'E' || res.charAt(j) == 'o' || res.charAt(j) == 'O')
					{
						break;
					}
				}
				if (j >= 0)
				{
					if (res.charAt(j) == 'e')
					{
						res.insert(j, 'i');
					}
					else if (res.charAt(j) == 'E')
					{
						res.insert(j, 'I');
					}
					else if (res.charAt(j) == 'o')
					{
						res.setCharAt(j, 'e');
						res.insert(j, 'u');
					}
					else if (res.charAt(j) == 'O')
					{
						res.setCharAt(j, 'E');
						res.insert(j, 'U');
					}
					ires++;
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'A') // remove accent on last accented letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toNoAccent(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c1') // add acute to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toAcute(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c0') // add grave to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toGrave(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c2') // add circumflex to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toCircumflex(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c4') // add trema/umlaut to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toTrema(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'I') // add iota accent to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toIota(res.charAt(j)));
				}
				return ires;
			}
		}
		else if (isoName.equals("pt") || isoName.equals("ca") || isoName.equals("de") || isoName.equals("el")
				|| isoName.equals("it"))
		{
			if (commands.charAt(ic + 1) == 'A') // remove accent on last accented letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toNoAccent(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c1') // add acute to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toAcute(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c0') // add grave to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toGrave(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c2') // add circumflex to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toCircumflex(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == '\u00c4') // add trema/umlaut to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toTrema(res.charAt(j)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'I') // add iota accent to last AEIOU letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				int j;
				for (j = ires - 1; j >= 0; j--)
				{
					if (Language.isNotAccented(res.charAt(j)))
					{
						break;
					}
				}
				if (j >= 0)
				{
					res.setCharAt(j, toIota(res.charAt(j)));
				}
				return ires;
			}
		}
		else if (isoName.equals("he"))
		{
			if (commands.charAt(ic + 1) == 'F') // unFinalize a letter
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u05b0' && res.charAt(ires - 2) == '\u05da')
				{
					res.setCharAt(ires - 2, '\u05db');
					res.deleteCharAt(ires - 1);
					ires--;
				}
				else if (res.charAt(ires - 1) == '\u05db')
				{
					if (ires - 1 < 0)
					{
						return -1;
					}
					res.setCharAt(ires - 1, '\u05da');
					res.insert(ires, '\u05b0');
					ires++;
				}
				else
				{
					if (ires - 1 < 0)
					{
						return -1;
					}
					res.setCharAt(ires - 1, finalize(res.charAt(ires - 1)));
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'G') // insert dagesh if current letter is Begadkefat
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u05d1' || res.charAt(ires - 1) == '\u05d2'
						|| res.charAt(ires - 1) == '\u05d3' || res.charAt(ires - 1) == '\u05db'
						|| res.charAt(ires - 1) == '\u05e4' || res.charAt(ires - 1) == '\u05ea')
				{
					res.insert(ires, '\u05bc'); // insert dagesh
					ires++;
				}
				return ires;
			}
			else if (commands.charAt(ic + 1) == 'H') // insert atef-patah if current letter is guturale; else shwa
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u05d0' || res.charAt(ires - 1) == '\u05d4'
						|| res.charAt(ires - 1) == '\u05d7' || res.charAt(ires - 1) == '\u05e2') 
				{
					res.insert(ires, '\u05b2'); // insert atef-patah
				}
				else
				{
					res.insert(ires, '\u05b0'); // insert shwa
				}
				return ires + 1;
			}
			else if (commands.charAt(ic + 1) == 'M') // delete last letter (vowel or consonant); takes care of doubles
			{
				if (ires - 1 < 0)
				{
					return -1;
				}
				if (res.charAt(ires - 1) == '\u05C1' || res.charAt(ires - 1) == '\u05C2') // dot for shin or sin
				{
					ires -= 2;
					res.delete(ires, 2);
				}
				else if (ires >= 2 && res.charAt(ires - 2) == '\u05bc') // remove current letter with dagesh
				{
					ires -= 2;
					res.delete(ires, 2);
				}
				else
				{
					ires--;
					res.deleteCharAt(ires);
				}
				for (ic++; ic < commands.length() && commands.charAt(ic) != '>'; ic++)
				{
					;
				}
				ic++;
				return ires;
			}
		}
		else
		{
			return -1;
		}
		return -1;
	}

	/**
	 * Returns inflection commands.
	 * 
	 * @return inflection commands
	 */
	public final String inflectionCommands()
	{
		if (isoName.equals("ar"))
		{
			return "# <T>: replaces Teh marbuta with Teh\n"
					+ "# <M>: processes consonant 'n' for past form, 3rd person, feminine plural\n"
					+ "# <Z>: processes consonant 't' for past form, 1st person, singular\n";
		}
		else if (isoName.equals("pt"))
		{
			return "# <A>: remove all accents in the word form\n";
		}
		else if (isoName.equals("el")) // Greek
		{
			return "# <A>: remove Accent in the current letter AEIOU\n"
					+ "# <\u00c1>: add acute accent to the current letter AEIOU\n"
					+ "# <\u00c0>: add grave accent to the current letter AEIOU\n"
					+ "# <\u00c2>: add circumflexe to the current letter AEIOU\n"
					+ "# <I>: add iota subscript to the current letter A\u0397\u03a9\n"
					+ "# <\u00c4>: add dieresis/trema/umlaut to the current letter AEIOU\n";
		}
		else if (isoName.equals("hu")) // Hungarian
		{
			return "# <A>: remove accent from the current letter AEIOU\n"
					+ "# <\u00c1>: add accent to the current letter AEIOU\n" + "# <D>: duplicate consonant\n";
		}
		else if (isoName.equals("ca") || isoName.equals("de") || isoName.equals("it"))
		{
			return "# <A>: remove accute accent in the current letter AEIOU\n"
					+ "# <\u00c1>: add acute accent to the current  letter AEIOU\n"
					+ "# <\u00c0>: add grave accent to the current  letter AEIOU\n"
					+ "# <\u00c2>: add circumflexe to the current  letter AEIOU\n"
					+ "# <\u00c4>: add dieresis/trema/umlaut to the current  letter AEIOU\n";
		}
		else if (isoName.equals("he"))
		{
			return "# <F>: replace Final letter with regular, and regular letter with Final\n"
					+ "# <G>: insert dagesh is current letter is begadkefat\n"
					+ "# <H>: insert atef-patah if current letter is guturale; else shwa\n"
					+ "# <M>: delete current letter; takes care of shin/sin dots and dagesh\n";
		}
		else if (isoName.equals("sp"))
		{
			return "# <A>: remove accute accent in the current letter AEIOU\n"
					+ "# <\u00c1>: add acute accent to the current  letter AEIOU\n"
					+ "# <\u00c0>: add grave accent to the current  letter AEIOU\n"
					+ "# <\u00c2>: add circumflexe to the current  letter AEIOU\n"
					+ "# <\u00c4>: add dieresis/trema/umlaut to the current  letter AEIOU\n"
					+ "# <U>: replace last 'e' with 'ie' or last 'o' with 'ue'\n";
		}
		else
		{
			return "# (None)\n";
		}
	}

	// Sort

	/**
	 * Sorts given strings ignoring case or not (depending on the flag).
	 * 
	 * @param text1
	 *            - first string to be compared
	 * @param text2
	 *            - second string to be compared
	 * @param ignoreCase
	 *            - flag for ignoring case or not
	 * @return less than 0 if text1 is less than text2, 0 if they are equal and greater than 0 if text1 is greater than
	 *         text2.
	 */
	public final int sortTexts(String text1, String text2, boolean ignoreCase)
	{
		ParameterCheck.mandatory("text1", text1);
		ParameterCheck.mandatory("text2", text2);

		Collator collator = Collator.getInstance(this.locale);

		
		if (ignoreCase)
		{
			return collator.compare(text1.toLowerCase(this.locale), text2.toLowerCase(this.locale));
		}
		else
		{
			return collator.compare(text1, text2);
		}
	}
}