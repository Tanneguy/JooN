package net.nooj4nlp.controller.TextEditorShell;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.nooj4nlp.controller.CorpusEditorShell.CorpusEditorShellController;
import net.nooj4nlp.engine.Ntext;
import net.nooj4nlp.engine.TuGraph;
import net.nooj4nlp.gui.shells.TextEditorShell;

public class JMftPanel extends JPanel
{
	private static final long serialVersionUID = 1000554144734771086L;

	private TuGraph tuGraph;
	private JScrollPane parentScrollPane;
	private TextEditorShellController textController;
	private double selectAllAnnotationsAtPosition;

	public JMftPanel(TextEditorShellController textController)
	{
		super();
		this.textController = textController;
		this.selectAllAnnotationsAtPosition = textController.getSelectAllAnnotationsAtPosition();

		this.setLayout(null);

		// Setting listeners
		this.addMouseListener(new JMftPanelMouseAdapter(textController));
	}

	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;

		// tuGraph is set in the controller, only after clicking on 'Show Text Annotation Structure'.
		TextEditorShell textShell = textController.getTextShell();
		textController.setTuGraph((Integer) textShell.getSpinner().getValue());
		tuGraph = textController.getTuGraph();

		CorpusEditorShellController corpusController = textShell.getCorpusController();
		Ntext myText = textController.getMyText();
		if (corpusController != null && corpusController.getShell() != null)
			myText.annotations = corpusController.getCorpus().annotations;

		if (tuGraph == null || myText.annotations == null)
			return;

		if (tuGraph.needToBeComputed)
		{
			tuGraph.computeDrawing(myText.annotations, this, g);
		}

		tuGraph.draw(myText.annotations, selectAllAnnotationsAtPosition, this, g);
		super.paintChildren(g);
	}

	public TextEditorShellController getTextController()
	{
		return textController;
	}

	public void setTextController(TextEditorShellController textController)
	{
		this.textController = textController;
	}

	public JScrollPane getParentScrollPane()
	{
		return parentScrollPane;
	}

	public void setParentScrollPane(JScrollPane parentScrollPane)
	{
		this.parentScrollPane = parentScrollPane;
	}

	public double getSelectAllAnnotationsAtPosition()
	{
		return selectAllAnnotationsAtPosition;
	}

	public void setSelectAllAnnotationsAtPosition(double selectAllAnnotationsAtPosition)
	{
		this.selectAllAnnotationsAtPosition = selectAllAnnotationsAtPosition;
	}
}