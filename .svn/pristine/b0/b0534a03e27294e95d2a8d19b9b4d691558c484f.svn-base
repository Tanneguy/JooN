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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.nooj4nlp.engine.helper.ParameterCheck;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * The class provides methods for extracting text from docx file format
 * 
 * @author Silberztein Max
 * 
 */
public class DocxToText
{

	private String docxFilePath = "";

	/**
	 * Constructor to use to store document file path, before calling extractText method
	 * 
	 * @param docxFilePath
	 */
	public DocxToText(String docxFilePath)
	{
		ParameterCheck.mandatoryString("docxFilePath", docxFilePath);
		this.docxFilePath = docxFilePath;
	}

	/**
	 * Extracts text from the underlying docx file.
	 * 
	 * @return Extracted text.
	 * @throws IOException
	 */
	public final String extractText() throws IOException
	{
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(docxFilePath);
			XWPFDocument doc = new XWPFDocument(inputStream);
			XWPFWordExtractor ex = new XWPFWordExtractor(doc);
			String text = ex.getText();
			return text;
		}
		finally
		{
			if (inputStream != null)
				inputStream.close();
		}
	}

}
