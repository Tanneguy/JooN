package net.nooj4nlp.controller.GrammarEditorShell;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.nooj4nlp.engine.Graph;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.gui.shells.GrammarEditorShell;

public class TextBoxKeyListener implements KeyListener
{

	private GrammarEditorShell editor;
	private GrammarEditorShellController controller;

	public TextBoxKeyListener(GrammarEditorShell shell)
	{
		editor = shell;
		controller = editor.getController();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (editor.rtBox.getWidth() == 0)
		{
			// TODO:
			
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER && (e.isControlDown() || e.isAltDown())) // validate
		{
			controller.grf.label.set(controller.mouseEdited, controller.cleanup(editor.rtBox.getText()));
			if (controller.grf.label.get(controller.mouseEdited).equals(""))
			{
				if (controller.mouseEdited == 0) // do not delete initial or terminal node
					controller.grf.label.set(controller.mouseEdited, "<E>");
				else if (controller.mouseEdited > 1)
					controller.grf.deleteNode(controller.mouseEdited);
			}
			controller.modify("edit node #" + controller.mouseEdited, false, true);
			controller.hideRtbox();
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) // escape
		{
			controller.hideRtbox();
			
		}
		else
		// resize current node
		{
			if (editor.rtBox.getText().equals(""))
			{
				int height = controller.grf.interline * 1 + 10;
				int width = 5 * controller.grf.ifont.getSize();

				if (editor.rtBox.getWidth() < 50)
					width = 50;
				editor.rtBox.setSize(width, height);
				return;
			}
			String output = null;
			int height = controller.grf.interline * (editor.rtBox.getLineCount() + 1) + 10;

			controller.grf.label.set(controller.mouseEdited, controller.cleanup(editor.rtBox.getText()));
			RefObject<String> outputRef = new RefObject<String>(output);
			String[] terms = Graph.inLineLabel(controller.grf.label.get(controller.mouseEdited), outputRef);
			output = outputRef.argvalue;
			int maxlen = 0;
			if (editor.getGraphics() != null)
			{
				for (int it = 0; it < terms.length; it++)
				{
					int len = terms[it].length() * controller.grf.ifont.getSize();
					if (len > maxlen)
						maxlen = len;
				}
			}
			if (maxlen < controller.grf.widB.get(controller.mouseEdited))
				maxlen = controller.grf.widB.get(controller.mouseEdited);
			if (maxlen < 50)
				maxlen = 50;
			int width = maxlen;

			editor.rtBox.setSize(width, height);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
