package net.nooj4nlp.controller.ConcordanceShell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.nooj4nlp.gui.actions.shells.construct.OpenStatsActionListener;
import net.nooj4nlp.gui.actions.shells.construct.SyntacticTreeActionListener;
import net.nooj4nlp.gui.components.CursorChangeEffect;

/**
 * Pop up menu for right click event in Concordance Shell.
 */
public class RightClickPopupMenuForConcordance extends MouseAdapter
{
	private ConcordanceShellController controller;
	private static boolean enableSyntacticAndAnnotations = true;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 */
	public RightClickPopupMenuForConcordance(ConcordanceShellController controller)
	{
		this.controller = controller;
	}

	/**
	 * If the right mouse button is clicked, show pop up menu.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			PopUpConcordance menu = new PopUpConcordance(controller, enableSyntacticAndAnnotations);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	// getters and setters
	public static void setenableSyntacticAndAnnotations(boolean enableSyntacticAndAnnotations)
	{
		RightClickPopupMenuForConcordance.enableSyntacticAndAnnotations = enableSyntacticAndAnnotations;
	}
}

/**
 * Help class for creating pop up menu that triggers when right mouse button is clicked in Concordance Shell.
 * 
 */
class PopUpConcordance extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - concordance controller
	 * @param enableSyntacticAndAnnotations
	 *            - flag to determine whether to enable "Display Syntactic Analysis" and "Add/Remove Annotations" menu
	 *            items
	 */
	public PopUpConcordance(final ConcordanceShellController controller, boolean enableSyntacticAndAnnotations)
	{
		// create the same items that are already in CONCORDANCE menu
		JMenuItem mntmSelectAll = new JMenuItem("Select all");
		add(mntmSelectAll);
		mntmSelectAll.addActionListener(new ConcordanceFilterSelectionActionListener(controller, true, false, false));

		JMenuItem mntmUnselectAll = new JMenuItem("Unselect all");
		add(mntmUnselectAll);
		mntmUnselectAll
				.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false, false, false));

		JMenuItem mntmFilterSelectedLines = new JMenuItem("Filter out selected lines");
		add(mntmFilterSelectedLines);
		mntmFilterSelectedLines.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false, true,
				true));

		JMenuItem mntmFilterUnselectedLines = new JMenuItem("Filter out unselected lines");
		add(mntmFilterUnselectedLines);
		mntmFilterUnselectedLines.addActionListener(new ConcordanceFilterSelectionActionListener(controller, false,
				true, false));

		JMenuItem mntmRepeatSegmentsHideHapaxes = new JMenuItem("Repeted segments only / Hide hapaxes");
		add(mntmRepeatSegmentsHideHapaxes);
		mntmRepeatSegmentsHideHapaxes.addActionListener(new ConcordanceFilterSelectionActionListener(controller));

		JSeparator separator_1 = new JSeparator();
		add(separator_1);

		JMenuItem mntmAddRemoveAnnotations = new JMenuItem("Annotate Text (add/remove annotations)");
		add(mntmAddRemoveAnnotations);
		mntmAddRemoveAnnotations.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_BUSY);
					controller.annotate();
					
				}
				finally
				{
					CursorChangeEffect.setCustomCursor(CursorChangeEffect.CURSOR_DEFAULT);
				}

			}
		});

		JMenuItem mntmDisplaySyntacticAnalysis = new JMenuItem("Display Syntactic Analysis");
		add(mntmDisplaySyntacticAnalysis);
		mntmDisplaySyntacticAnalysis.addActionListener(new SyntacticTreeActionListener(controller));

		if (!enableSyntacticAndAnnotations)
		{
			mntmAddRemoveAnnotations.setEnabled(false);
			mntmDisplaySyntacticAnalysis.setEnabled(false);
		}

		JSeparator separator_2 = new JSeparator();
		add(separator_2);

		JMenuItem mntmExportConcordanceAsText = new JMenuItem("Export Concordance As TXT");
		add(mntmExportConcordanceAsText);
		mntmExportConcordanceAsText.addActionListener(new ExportConcordanceActionListener(controller, 1));

		JMenuItem mntmExportConcordanceAsWeb = new JMenuItem("Export Concordance As Web Page");
		add(mntmExportConcordanceAsWeb);
		mntmExportConcordanceAsWeb.addActionListener(new ExportConcordanceActionListener(controller, 2));

		JMenuItem mntmExportIndex = new JMenuItem("Export Index");
		add(mntmExportIndex);
		mntmExportIndex.addActionListener(new ExportConcordanceActionListener(controller, 3));

		JMenuItem mntmExtractMatchingTU = new JMenuItem("Extract Matching Text Units");
		add(mntmExtractMatchingTU);
		mntmExtractMatchingTU.addActionListener(new ExtractConcordanceActionListener(controller, true));

		JMenuItem mntmExtractNonMatchingTU = new JMenuItem("Extract Non Matching Text Units");
		add(mntmExtractNonMatchingTU);
		mntmExtractNonMatchingTU.addActionListener(new ExtractConcordanceActionListener(controller, false));

		JMenuItem mntmStatisticalAnalyses = new JMenuItem("Statistical Analyses");
		add(mntmStatisticalAnalyses);
		mntmStatisticalAnalyses.addActionListener(new OpenStatsActionListener(controller.getCorpusController(),
				controller.getTextController(), controller));
	}
}
