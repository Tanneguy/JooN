package net.nooj4nlp.controller.CorpusEditorShell.ExportXmlDialog;

/**
 * Helper class, used for imitating ComponentResourceManager for text pane.
 * 
 */
public class XmlAnnotationsTextPaneResources
{
	private final StringBuilder text;

	public XmlAnnotationsTextPaneResources()
	{
		text = new StringBuilder();
		text.append("NooJ's annotations will be represented as XML tags and inserted in the text.");
		text.append("\n\n");
		text.append("(1) lexical annotations are represented as <LU> XML tags; the lexical properties are represented as XML tag's properties, for instance:");
		text.append("\n\n");
		text.append("<LU LEMMA=\"eat\" CAT=V TENSE=PR PERSON=2 NUMBER=s>eats</LU>");
		text.append("\n\n");
		text.append("(2) syntactic annotations are represented exactly as XML tags, for instance:");
		text.append("\n\n");
		text.append("<DATE>Monday, March 13th</DATE>");
		text.append("\n\n");
		text.append("(3) The special syntactic annotation TRANS is used to perform translations or replacements in the text.");
		text.append("\n\n");
		text.append("For instance, <TRANS+EN> will replace the annotated text with the value of the property \"EN\". For instance, if we have the following annotation:");
		text.append("\n\n");
		text.append("lundi 2 mars,TRANS+EN=Monday, March 2nd");
		text.append("\n\n");
		text.append("then in the text, all sequences \"lundi 2 mars\" will be replaced with:");
		text.append("\n\n");
		text.append("<EN>Monday, March 2nd</EN>");
		text.append("\n\n");
		text.append("OPTIONS:");
		text.append("\n\n");
		text.append("\"Tag all syntactic annotations\" converts all syntactic annotations into XML tags.");
		text.append("\n\n");
		text.append("\"Tag only following annotations\": the user must enter the list of the lexical or syntactic annotations to be exported.");
		text.append("\n\n");
		text.append("If the user enters one or more <TRANS+XXX>, then all the other lexical or syntactic annotations are simply ignored.");
		text.append("\n\n");
		text.append("\"Export Annotated Text Only\" will only export parts of the text that are annotated with the specified annotations, and simply ignore the remaining text.");
		text.append("\n\n");
		text.append("(3) In case of ambiguity:");
		text.append("\n\n");
		text.append("(a) NooJ will give priority to the longest annotations (there could be more than one), and simply ignore shorter ones.");
		text.append("\n\n");
		text.append("(b) if two or more ambiguous annotations to be exported have the same length, NooJ will export all of them.");
		text.append("\n\n");
		text.append("(c) NooJ does not manage ambiguous TRANS annotations: if several TRANS annotations occur at the same place with the same length, NooJ will export only one of them.");
	}

	public StringBuilder getText()
	{
		return text;
	}
}
