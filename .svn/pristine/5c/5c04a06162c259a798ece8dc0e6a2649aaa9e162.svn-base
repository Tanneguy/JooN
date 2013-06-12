package net.nooj4nlp.controller.CorpusEditorShell;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.nooj4nlp.controller.LocateDialog.LocateDialogTextActionListener;

/**
 * Class for implementation of right click pop up menu of Corpus.
 * 
 */

public class RightClickPopupMenuForCorpus extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - corpus controller
	 */

	public RightClickPopupMenuForCorpus(CorpusEditorShellController controller)
	{
		// create the same items that are already in CORPUS menu
		JMenuItem mntmLinguisticAnalysis = new JMenuItem("Linguistic analysis");
		add(mntmLinguisticAnalysis);
		mntmLinguisticAnalysis.addActionListener(new LinguisticAnalysisActionListener(controller));

		JMenuItem mntmLocate = new JMenuItem("Locate");
		add(mntmLocate);
		mntmLocate.addActionListener(new LocateDialogTextActionListener(null, controller));

		JSeparator separator_12 = new JSeparator();
		add(separator_12);

		JMenuItem mntmExportColoredTexts = new JMenuItem("Export colored texts as HTML documents");
		add(mntmExportColoredTexts);
		mntmExportColoredTexts.setEnabled(controller.isColored());
		mntmExportColoredTexts.addActionListener(new ExportColoredToHtmlActionListener(controller, null));

		JMenuItem mntmExportAnnotatedTexts = new JMenuItem("Export annotated texts as XML documents");
		add(mntmExportAnnotatedTexts);
		mntmExportAnnotatedTexts.addActionListener(new ExportXmlActionListener(controller));
	}
}