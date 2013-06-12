package net.nooj4nlp.gui.shells;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultEditorKit;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorKeyListener;
import net.nooj4nlp.controller.GrammarEditorShell.GrammarEditorShellController;
import net.nooj4nlp.controller.GrammarEditorShell.JPGraphMouseListener;
import net.nooj4nlp.controller.GrammarEditorShell.JPGraphMouseMotionListener;
import net.nooj4nlp.controller.GrammarEditorShell.JPGraphMouseWheelListener;
import net.nooj4nlp.controller.GrammarEditorShell.TextBoxKeyListener;
import net.nooj4nlp.controller.HistoryDialog.PurgeActionListener;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.gui.actions.grammar.CopyActionListener;
import net.nooj4nlp.gui.actions.grammar.CreateNewNodeActionListener;
import net.nooj4nlp.gui.actions.grammar.CutActionListener;
import net.nooj4nlp.gui.actions.grammar.DeleteGraphActionListener;
import net.nooj4nlp.gui.actions.grammar.DeleteGraphAndChildrenActionListener;
import net.nooj4nlp.gui.actions.grammar.ExportGrammarActionListener;
import net.nooj4nlp.gui.actions.grammar.ImportGrammarActionListener;
import net.nooj4nlp.gui.actions.grammar.NewGraphActionListener;
import net.nooj4nlp.gui.actions.grammar.PasteActionListener;
import net.nooj4nlp.gui.actions.grammar.SelectAllActionListener;
import net.nooj4nlp.gui.actions.shells.construct.AlignmentActionListener;
import net.nooj4nlp.gui.actions.shells.construct.ContractActionListener;
import net.nooj4nlp.gui.actions.shells.construct.DebugActionListener;
import net.nooj4nlp.gui.actions.shells.construct.GenerateLanguageActionListener;
import net.nooj4nlp.gui.actions.shells.construct.GramStructActionListener;
import net.nooj4nlp.gui.actions.shells.construct.HistoryDialogActionListener;
import net.nooj4nlp.gui.actions.shells.construct.PresentationActionListener;
import net.nooj4nlp.gui.actions.shells.construct.ProduceParaphrasesActionListener;
import net.nooj4nlp.gui.actions.shells.control.GrammarCommandInternalFrameListener;
import net.nooj4nlp.gui.components.JPGraph;
import net.nooj4nlp.gui.main.Launcher;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 * 
 * Shell for editting grammars
 * 
 */
public class GrammarEditorShell extends JInternalFrame
{
	private static final long serialVersionUID = 2202250733280414442L;
	
	

	public JPopupMenu popText;
	public JMenu grammarMenu;
	public JLabel info;

	public JTextArea rtBox;
	public JPanel pBackGraph;
	public JPGraph pGraph;

	public Timer timerSel;

	private JMenuItem mntmShowStructure;
	private JMenuItem mntmDebug;
	private JMenuItem mntmShowContract;
	

	public GramStructShell gramStruct;

	private GrammarEditorShellController controller;

	public GramStructShell formGramStruct;
	public ContractShell contractShell;

	public GrammarEditorShell(Language lan, Language lan2)
	{
		construct();
		controller = new GrammarEditorShellController(this, lan, lan2);
		formGramStruct = new GramStructShell(this);

		pGraph.addMouseMotionListener(new JPGraphMouseMotionListener(this));
		pGraph.addMouseListener(new JPGraphMouseListener(this));
		pGraph.addMouseWheelListener(new JPGraphMouseWheelListener(this));
		this.addKeyListener(new GrammarEditorKeyListener(this));
		rtBox.addKeyListener(new TextBoxKeyListener(this));

		rtBox.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

		popText = createPopupMenu();
		grammarMenu = createGrammarMenu();
		this.addInternalFrameListener(new GrammarCommandInternalFrameListener(controller));
	}

	/**
	 * @wbp.parser.constructor
	 */
	public GrammarEditorShell(String fullname)
	{
		construct();
		controller = new GrammarEditorShellController(this, fullname);
		formGramStruct = new GramStructShell(this);

		pGraph.addMouseMotionListener(new JPGraphMouseMotionListener(this));
		pGraph.addMouseListener(new JPGraphMouseListener(this));
		pGraph.addMouseWheelListener(new JPGraphMouseWheelListener(this));
		this.addKeyListener(new GrammarEditorKeyListener(this));
		rtBox.addKeyListener(new TextBoxKeyListener(this));

		popText = createPopupMenu();
		grammarMenu = createGrammarMenu();
		this.addInternalFrameListener(new GrammarCommandInternalFrameListener(controller));
	}

	/**
	 * Create the frame.
	 * 
	 * @param
	 */
	private void construct()
	{
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setClosable(true);
		setBounds(100, 100, 950, 400);
		setPreferredSize(new Dimension(1200, 800));

		getContentPane().setLayout(new MigLayout("ins 7", "[grow,fill]", "[grow,fill]"));

		rtBox = new JTextArea();
		rtBox.setBorder(BorderFactory.createLoweredBevelBorder());

		info = new JLabel();

		pBackGraph = new JPanel();
		pBackGraph.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		pBackGraph.setPreferredSize(new Dimension(1047, 763));

		pGraph = new JPGraph(this);
		pBackGraph.add(pGraph);
		JScrollPane graphScrollPane = new JScrollPane(pBackGraph);
		getContentPane().add(graphScrollPane);

		pGraph.setLayout(null);
		pGraph.add(rtBox);
	}

	/**
	 * Create a right-click menu containing grammar-related options
	 * 
	 * @return pop-up menu containing grammar-related options
	 */
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu pop = new JPopupMenu();

		mntmShowStructure = new JMenuItem("Show Structure");
		mntmShowStructure.addActionListener(new GramStructActionListener(this));
		pop.add(mntmShowStructure);

		JMenu mnHistory = new JMenu("History");
		pop.add(mnHistory);

		JMenuItem mntmShow = new JMenuItem("Show");
		mnHistory.add(mntmShow);
		mntmShow.addActionListener(new HistoryDialogActionListener(this));

		JMenuItem mntmPurge = new JMenuItem("Purge");
		mnHistory.add(mntmPurge);
		mntmPurge.addActionListener(new PurgeActionListener(this));

		JMenuItem mntmShowContract = new JMenuItem("Show Contract");
		mntmShowContract.addActionListener(new ContractActionListener(this));
		pop.add(mntmShowContract);

		mntmDebug = new JMenuItem("Debug...");
		mntmDebug.addActionListener(new DebugActionListener(this.controller));
		pop.add(mntmDebug);

		JSeparator separator_1 = new JSeparator();
		pop.add(separator_1);

		JMenuItem mntmGenerateLanguage = new JMenuItem("Generate Language...");
		mntmGenerateLanguage.addActionListener(new GenerateLanguageActionListener(this.controller));
		pop.add(mntmGenerateLanguage);

		

		JSeparator separator_2 = new JSeparator();
		pop.add(separator_2);

		JMenuItem mntmAlignmnet = new JMenuItem("Alignment");
		mntmAlignmnet.addActionListener(new AlignmentActionListener(this.controller));
		pop.add(mntmAlignmnet);

		JMenuItem mntmPresentation = new JMenuItem("Presentation");
		mntmPresentation.addActionListener(new PresentationActionListener(this.controller));
		pop.add(mntmPresentation);

		JMenu mnZoom = new JMenu("Zoom");
		pop.add(mnZoom);

		JMenuItem mntmZoomFit = new JMenuItem("Fit");
		mntmZoomFit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(1);
			}
		});
		mnZoom.add(mntmZoomFit);

		JMenuItem mntmZoom30 = new JMenuItem("30%");
		mntmZoom30.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(30);
			}
		});
		mnZoom.add(mntmZoom30);

		JMenuItem mntmZoom50 = new JMenuItem("50%");
		mntmZoom50.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(50);
			}
		});
		mnZoom.add(mntmZoom50);

		JMenuItem mntmZoom75 = new JMenuItem("75%");
		mntmZoom75.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(75);
			}
		});
		mnZoom.add(mntmZoom75);

		JMenuItem mntmZoom100 = new JMenuItem("100%");
		mntmZoom100.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(100);
			}
		});
		mnZoom.add(mntmZoom100);

		JMenuItem mntmZoom125 = new JMenuItem("125%");
		mntmZoom125.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(125);
			}
		});
		mnZoom.add(mntmZoom125);

		JMenuItem mntmZoom150 = new JMenuItem("150%");
		mntmZoom150.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(150);
			}
		});
		mnZoom.add(mntmZoom150);

		JMenuItem mntmZoom200 = new JMenuItem("200%");
		mntmZoom200.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(200);
			}
		});
		mnZoom.add(mntmZoom200);

	

		JSeparator separator_3 = new JSeparator();
		pop.add(separator_3);

		JMenu mnEdit_1 = new JMenu("Edit");
		pop.add(mnEdit_1);
		
	

		JMenuItem mntmCreateNewNode = new JMenuItem("Create New Node");
		mntmCreateNewNode.addActionListener(new CreateNewNodeActionListener(this));
		mnEdit_1.add(mntmCreateNewNode);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
	    mntmCopy.addActionListener(new CopyActionListener(this));
		mnEdit_1.add(mntmCopy);

		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mntmCut.addActionListener(new CutActionListener(this));
		mnEdit_1.add(mntmCut);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mntmPaste.addActionListener(new PasteActionListener(this));
		mnEdit_1.add(mntmPaste);
		

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmSelectAll.addActionListener(new SelectAllActionListener(this));
		mnEdit_1.add(mntmSelectAll);
		
		

		JSeparator separator_4 = new JSeparator();
		pop.add(separator_4);

		JMenuItem mntmNewGraph = new JMenuItem("New Graph");
		mntmNewGraph.addActionListener(new NewGraphActionListener(this));
		pop.add(mntmNewGraph);

		JMenuItem mntmDeleteGraph = new JMenuItem("Delete Graph");
		mntmDeleteGraph.addActionListener(new DeleteGraphActionListener(this));
		pop.add(mntmDeleteGraph);

		JMenuItem mntmDeleteGraphChildren = new JMenuItem("Delete Graph & Children");
		mntmDeleteGraphChildren.addActionListener(new DeleteGraphAndChildrenActionListener(this.controller));
		pop.add(mntmDeleteGraphChildren);

		JMenuItem mntmImportGrammar = new JMenuItem("Import Grammar");
		pop.add(mntmImportGrammar);
		mntmImportGrammar.addActionListener(new ImportGrammarActionListener(this));

		JMenuItem mntmExportGrammar = new JMenuItem("Export Grammar");
		pop.add(mntmExportGrammar);
		mntmExportGrammar.addActionListener(new ExportGrammarActionListener(this));

		return pop;
	}

	private JMenu createGrammarMenu()
	{
		
		Launcher.mnEdit.setVisible(false);
		JMenu pop = new JMenu("GRAMMAR");

		mntmShowStructure = new JMenuItem("Show Structure");
		mntmShowStructure.addActionListener(new GramStructActionListener(this));
		pop.add(mntmShowStructure);

		JMenu mnHistory = new JMenu("History");
		pop.add(mnHistory);

		JMenuItem mntmShow = new JMenuItem("Show");
		mnHistory.add(mntmShow);
		mntmShow.addActionListener(new HistoryDialogActionListener(this));

		JMenuItem mntmPurge = new JMenuItem("Purge");
		mnHistory.add(mntmPurge);
		mntmPurge.addActionListener(new PurgeActionListener(this));

		JMenuItem mntmShowContract = new JMenuItem("Show Contract");
		mntmShowContract.addActionListener(new ContractActionListener(this));
		pop.add(mntmShowContract);

		mntmDebug = new JMenuItem("Debug...");
		mntmDebug.addActionListener(new DebugActionListener(this.controller));
		pop.add(mntmDebug);

		JSeparator separator_1 = new JSeparator();
		pop.add(separator_1);

		JMenuItem mntmGenerateLanguage = new JMenuItem("Generate Language...");
		mntmGenerateLanguage.addActionListener(new GenerateLanguageActionListener(this.controller));
		pop.add(mntmGenerateLanguage);

	

		JSeparator separator_2 = new JSeparator();
		pop.add(separator_2);

		JMenuItem mntmAlignmnet = new JMenuItem("Alignment");
		mntmAlignmnet.addActionListener(new AlignmentActionListener(this.controller));
		pop.add(mntmAlignmnet);

		JMenuItem mntmPresentation = new JMenuItem("Presentation");
		mntmPresentation.addActionListener(new PresentationActionListener(this.controller));
		pop.add(mntmPresentation);

		JMenu mnZoom = new JMenu("Zoom");
		pop.add(mnZoom);

		JMenuItem mntmZoomFit = new JMenuItem("Fit");
		mntmZoomFit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(1);
			}
		});
		mnZoom.add(mntmZoomFit);

		JMenuItem mntmZoom30 = new JMenuItem("30%");
		mntmZoom30.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(30);
			}
		});
		mnZoom.add(mntmZoom30);

		JMenuItem mntmZoom50 = new JMenuItem("50%");
		mntmZoom50.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(50);
			}
		});
		mnZoom.add(mntmZoom50);

		JMenuItem mntmZoom75 = new JMenuItem("75%");
		mntmZoom75.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(75);
			}
		});
		mnZoom.add(mntmZoom75);

		JMenuItem mntmZoom100 = new JMenuItem("100%");
		mntmZoom100.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(100);
			}
		});
		mnZoom.add(mntmZoom100);

		JMenuItem mntmZoom125 = new JMenuItem("125%");
		mntmZoom125.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(125);
			}
		});
		mnZoom.add(mntmZoom125);

		JMenuItem mntmZoom150 = new JMenuItem("150%");
		mntmZoom150.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(150);
			}
		});
		mnZoom.add(mntmZoom150);

		JMenuItem mntmZoom200 = new JMenuItem("200%");
		mntmZoom200.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.zoom(200);
			}
		});
		mnZoom.add(mntmZoom200);

		

		JSeparator separator_3 = new JSeparator();
		pop.add(separator_3);

		JMenu mnEdit_1 = new JMenu("Edit");
		pop.add(mnEdit_1);

		JMenuItem mntmCreateNewNode = new JMenuItem("Create New Node");
		mntmCreateNewNode.addActionListener(new CreateNewNodeActionListener(this));
		mnEdit_1.add(mntmCreateNewNode);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mntmCopy.addActionListener(new CopyActionListener(this));
		mnEdit_1.add(mntmCopy);

		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mntmCut.addActionListener(new CutActionListener(this));
		mnEdit_1.add(mntmCut);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mntmPaste.addActionListener(new PasteActionListener(this));
		mnEdit_1.add(mntmPaste);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmSelectAll.addActionListener(new SelectAllActionListener(this));
		mnEdit_1.add(mntmSelectAll);

		
		
		JSeparator separator_4 = new JSeparator();
		pop.add(separator_4);

		JMenuItem mntmNewGraph = new JMenuItem("New Graph");
		mntmNewGraph.addActionListener(new NewGraphActionListener(this));
		pop.add(mntmNewGraph);

		JMenuItem mntmDeleteGraph = new JMenuItem("Delete Graph");
		mntmDeleteGraph.addActionListener(new DeleteGraphActionListener(this));
		pop.add(mntmDeleteGraph);

		JMenuItem mntmImportGrammar = new JMenuItem("Import Grammar");
		pop.add(mntmImportGrammar);
		mntmImportGrammar.addActionListener(new ImportGrammarActionListener(this));

		JMenuItem mntmExportGrammar = new JMenuItem("Export Grammar");
		pop.add(mntmExportGrammar);
		mntmExportGrammar.addActionListener(new ExportGrammarActionListener(this));

		return pop;
	}

	public JMenuItem getMntmShowStructure()
	{
		return mntmShowStructure;
	}

	public JMenuItem getMntmDebug()
	{
		return mntmDebug;
	}

	public JMenuItem getMntmShowContract()
	{
		return mntmShowContract;
	}

	

	public GrammarEditorShellController getController()
	{
		return controller;
	}

	public JTextArea getRtBox()
	{
		return rtBox;
	}
}