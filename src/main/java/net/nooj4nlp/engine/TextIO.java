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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import net.nooj4nlp.engine.helper.ParameterCheck;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

/**
 * This class provides static methods for extraction of text from files of various formats.
 * 
 * @author Silberztein Max
 */
public class TextIO
{
	private static final String UTF_8_ENCODING = "UTF-8";

	/**
	 * Loads raw text from a file, using default NooJ encoding - UTF-8.
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	private static String loadRawText(String filePath) throws IOException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		File file = new File(filePath);

		if (!file.exists())
		{
			throw new IOException("Source file on path '" + filePath + "' does not exist.");
		}

		if (!file.isFile())
		{
			throw new IOException("Source file on path '" + filePath + "' exists but is not a file.");
		}

		return FileUtils.readFileToString(file, UTF_8_ENCODING);
	}

	/**
	 * Loads raw text from a file with specific encoding.
	 * 
	 * @param fullname
	 *            - the path to the file containing the text
	 * @param encodingCode
	 *            - encoding of the file
	 * @return The text from the file
	 * @throws IOException
	 *             if an IO error occurs while reading lines from file
	 */
	private static String loadRawText(String filePath, String encoding) throws IOException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		File file = new File(filePath);

		if (!file.exists())
		{
			throw new IOException("Source file on path '" + filePath + "' does not exist.");
		}

		if (!file.isFile())
		{
			throw new IOException("Source file on path '" + filePath + "' exists but is not a file");
		}

		return FileUtils.readFileToString(file, encoding);
	}

	/**
	 * Normalizes characters and character sequences such as "œ" => "oe", in string
	 * 
	 * @param buffer
	 *            - string that needs to be changed
	 * @param charTable
	 *            - table of replacements - charTable[i] - text to be replaced, charTable[i+1] - replacement text
	 * @return Fixed string that represents standardized text
	 */
	private static String standardizeText(String buffer, ArrayList<String> charTable)
	{
		ParameterCheck.mandatoryString("buffer", buffer);
		// chartable is allowed to be null

		// Replaces carriage return with empty string.
		buffer = buffer.replace("\r", "");

		if (charTable == null)
		{
			return buffer;
		}

		String res = buffer;

		for (int i = 0; i < charTable.size(); i += 2)
		{
			String pattern = charTable.get(i);
			String replace = charTable.get(i + 1);

			// indexOf comparison is case sensitive
			int start = res.indexOf(pattern, 0);
			if (start < 0)
			{
				continue;
			}

			int currentpos = 0;
			StringBuilder tmp = new StringBuilder();
			while (start >= 0)
			{
				tmp.append(res.substring(currentpos, start));
				tmp.append(replace);
				currentpos = start + pattern.length();

				// indexOf comparison is case sensitive
				start = res.indexOf(pattern, currentpos);
			}
			tmp.append(res.substring(currentpos));
			res = tmp.toString();
		}
		return res;
	}

	/**
	 * Loads text from .doc file
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs while opening the file stream
	 * @throws IOException
	 *             if an error occurs during creation of wordExtractor from a stream
	 */
	private static String loadWordFile(String filePath) throws IOException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		FileInputStream fileInputStream = null;
		WordExtractor wordExtractor = null;

		try
		{
			// Extractor creation based on a stream
			fileInputStream = new FileInputStream(filePath);
			wordExtractor = new WordExtractor(fileInputStream);

			// Returning the text
			return wordExtractor.getText();
		}
		finally
		{
			// Closing input stream
			if (fileInputStream != null)
				fileInputStream.close();
		}
	}

	/**
	 * Loads text from .html file with appropriate encoding
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	private static String loadHtmlText(String filePath) throws IOException
	{
		// Mandatory check
		ParameterCheck.mandatoryString("filePath", filePath);

		String result, source;
		File sourceFile = new File(filePath);

		if (!sourceFile.exists())
		{
			throw new IOException("Source file on path '" + filePath + "' does not exist.");
		}

		if (!sourceFile.isFile())
		{
			throw new IOException("Source file on path '" + filePath + "' exists but is not a file");
		}
		source = FileUtils.readFileToString(sourceFile, UTF_8_ENCODING);

		String encoding;

		Pattern p = Pattern.compile("(charset=|encoding=)[-0-9A-Za-z_]+");
		Matcher m = p.matcher(source);
		if (m.find())
		{
			String group = m.group();
			encoding = group.substring(group.indexOf("=") + 1);

			if (!encoding.equalsIgnoreCase(UTF_8_ENCODING))
			{
				source = FileUtils.readFileToString(sourceFile, encoding);
			}
		}

		try
		{
			// Remove HTML Development formatting
			result = source.replaceAll("[\r\n\t ]+", " ");
			
			result = result.replaceAll("\\(\"\\[>\\]\"\\)", "");

			// Remove the header
			// Prepare first by clearing attributes
			result = result.replaceAll("(?i)<( )*head([^>])*>", "<head>");
			result = result.replaceAll("(?i)(<( )*(/)( )*head( )*>)", "</head>");
			result = result.replaceAll("(?i)(<head>).*(</head>)", "");

			// Remove all scripts
			// Prepare first by clearing attributes
			result = result.replaceAll("(?i)<( )*script([^>])*>", "");
			result = result.replaceAll("(?i)(<( )*(/)( )*script( )*>)", "");
			result = result.replaceAll("(?i)<( )*noscript([^>])*>", "<script>");
			result = result.replaceAll("(?i)(<( )*(/)( )*noscript( )*>)", "</script>");

			result = result.replaceAll("(?i)<!--( )*([^-])*( )*-->", "");

			// remove all styles
			// Prepare first by clearing attributes
			result = result.replaceAll("(?i)<( )*style([^>])*>", "<style>");
			result = result.replaceAll("(?i)(<( )*(/)( )*style( )*>)", "</style>");
			result = result.replaceAll("(?i)(<style>).*(</style>)", "");

			// insert tabs in spaces of <td>
			result = result.replaceAll("(?i)<( )*td([^>])*>", "\t");

			// insert line breaks in places of <BR> and <LI>
			result = result.replaceAll("(?i)<( )*br( )*/?()*>", "\n");
			result = result.replaceAll("(?i)<( )*li( )*>", "\n");

			// insert line paragraphs (double line breaks) in place if <P>, <DIV> and <TR>
			result = result.replaceAll("(?i)<( )*div([^>])*>", "\n\n");
			result = result.replaceAll("(?i)<( )*tr([^>])*>", "\n\n");
			result = result.replaceAll("(?i)<( )*p([^>])*>", "\n\n");

			// Remove remaining tags like <a>, links, images, comments etc - anything thats inside < >
			result = result.replaceAll("(?i)<[^>]*>", "");

			// Remove extra line breaks and tabs:
			// Replace over 2 breaks with 2 and over 4 tabs with 4.
			result = result.replaceAll("(?i)(\n)( )+(\n)", "\n\n");
			result = result.replaceAll("(?i)(\t)( )+(\t)", "\t\t");
			result = result.replaceAll("(?i)(\t)( )+(\n)", "\t\n");
			result = result.replaceAll("(?i)(\n)( )+(\t)", "\n\t");
			result = result.replaceAll("(?i)(\n)(\t)+(\n)", "\n\n");
			result = result.replaceAll("(?i)(\n)(\t)+", "\n\t");

			// convert special codes from Hex to Decimal (from &#x161; to &#353; because of Mono)
			Pattern patternHex = Pattern.compile("&#(x|X)\\d+;");
			Matcher matcherHex = patternHex.matcher(result);
			StringBuffer patternStringBuffer = new StringBuffer();
			while (matcherHex.find())
			{
				String matchedSequence = matcherHex.group();
				String replaceableSequence = hexToDecimal(matchedSequence);
				matcherHex.appendReplacement(patternStringBuffer, replaceableSequence);
			}
			matcherHex.appendTail(patternStringBuffer);
			result = patternStringBuffer.toString();

			// replace special characters and return result
			return StringEscapeUtils.unescapeHtml4(result);

		}
		catch (java.lang.Exception e)
		{
			return source;
		}
	}

	/**
	 * Converts special hexadecimal codes to decimal codes (&#x161; ==> &#353;)
	 * 
	 * @param m
	 *            - special hexadecimal code
	 * @return Special decimal code
	 */
	private static String hexToDecimal(String m)
	{
		// Get the matched string.
		return "&#" + Integer.parseInt(m.substring(3, m.length() - 1), 16) + ';';
	}

	/**
	 * Loads text from .docx file using DocxToText class.
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs during extraction of text from .docx file
	 */
	private static String loadWord2007File(String filePath) throws IOException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		DocxToText dtt = new DocxToText(filePath);
		return dtt.extractText();
	}

	/**
	 * This method loads text from .rtf file.
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs while opening the stream
	 * @throws BadLocationException
	 *             if an error occurs while reading into RTFEditorKit or reading content from document
	 */
	public static String loadRtfFile(String filePath) throws IOException, BadLocationException
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		FileInputStream fis = null;

		try
		{
			// Creating RTFEditor needed for reading RTF file
			HTMLEditorKit editorKit = new HTMLEditorKit();
			Document doc = editorKit.createDefaultDocument();

			File file = new File(filePath);

			if (!file.exists())
			{
				throw new IOException("Source file on path '" + filePath + "' does not exist.");
			}

			if (!file.isFile())
			{
				throw new IOException("Source file on path '" + filePath + "' exists but is not a file");
			}

			// Reading RTF file from file input stream into document
			fis = new FileInputStream(file);
			editorKit.read(fis, doc, 0);

			// Returning text from document
			String textToBeReturned = doc.getText(0, doc.getLength());
			return textToBeReturned;
		}
		finally
		{
			// Closing input stream
			if (fis != null)
				fis.close();
		}
	}

	/**
	 * Loads text from PDF file using iText library.
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @return The text from the file
	 * @throws IOException
	 *             if an error occurs while reading text from a page
	 */
	private static String loadPdfFile(String filePath) throws IOException // PDF document TEXT FORMAT
	{
		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		PdfReader reader = new PdfReader(filePath);

		// StringBuffer used instead of String.
		StringBuffer stringBuffer = new StringBuffer();

		for (int page = 1; page <= reader.getNumberOfPages(); page++)
		{
			TextExtractionStrategy its = new SimpleTextExtractionStrategy();
			String s = PdfTextExtractor.getTextFromPage(reader, page, its);

			// Appending the text to stringBuffer, page by page
			stringBuffer.append(s);

			reader.close();
		}

		return stringBuffer.toString();
	}

	public static List<String> loadPdfFileToStrings(String filePath) throws IOException // PDF document TEXT
	// FORMAT
	{
		// List of Strings used instead of String.
		List<String> resultingList = new ArrayList<String>();

		// Mandatory parameter check
		ParameterCheck.mandatoryString("filePath", filePath);

		PdfReader reader = new PdfReader(filePath);

		for (int page = 1; page <= reader.getNumberOfPages(); page++)
		{
			TextExtractionStrategy its = new SimpleTextExtractionStrategy();
			String s = PdfTextExtractor.getTextFromPage(reader, page, its);

			// Adding the text to the list, page by page
			resultingList.add(s);

			reader.close();
		}

		return resultingList;
	}

	/**
	 * Loads text from any kind of file.
	 * 
	 * @param filePath
	 *            - the path to the file containing the text
	 * @param encodingtype
	 *            - encoding type (1 for raw text, 2 for raw text with given encoding code, 3 for RTF, 4 for .doc, 5 for
	 *            HTML, 6 for .docx, 7 for PDF)
	 * @param encodingcode
	 *            - encoding code of raw file
	 * @param encodingname
	 *            - encoding name of file of unknown format
	 * @param chartable
	 *            - table replacement
	 * @return the text from the file
	 * @throws IOException
	 *             if an error occurs while reading text from a file
	 * @throws BadLocationException
	 *             if an error occurs while reading RTF file
	 */
	public static String loadText(String filePath, int encodingtype, String encodingcode, String encodingname,
			ArrayList<String> chartable) throws IOException, BadLocationException
	{
		String buf = null;
		switch (Math.abs(encodingtype))
		{
			case 1:
				buf = loadRawText(filePath);
				break;
			case 2:
				buf = loadRawText(filePath, encodingcode);
				break;
			case 3: // RTF
				buf = loadRtfFile(filePath);
				break;
			case 4: // WORD (.doc)
				buf = loadWordFile(filePath);
				break;
			case 5: // HTML
				buf = loadHtmlText(filePath);
				break;
			case 6: // WORD (.docx)
				buf = loadWord2007File(filePath);
				break;
			case 7:
				buf = loadPdfFile(filePath);
				break;
			default:
				return null;
		}
		return standardizeText(buf, chartable); // return buf;
	}
}