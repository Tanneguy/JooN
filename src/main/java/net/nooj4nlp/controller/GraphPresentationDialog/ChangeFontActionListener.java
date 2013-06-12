package net.nooj4nlp.controller.GraphPresentationDialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.nooj4nlp.gui.dialogs.JFontChooser;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements changing font of Graph Presentation Dialog.
 * 
 */

public class ChangeFontActionListener implements ActionListener
{

	private GraphPresentationController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            - controller of an active Graph Presentation Dialog
	 */
	public ChangeFontActionListener(GraphPresentationController controller)
	{
		super();

		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFontChooser fontChooser = new JFontChooser();

		// depending on clicked button, set initial font in font chooser
		if (e.getActionCommand().equals("Input"))
		{
			Font iFont = controller.getiFont();
			fontChooser.setSelectedFontFamily(iFont.getFamily());
			fontChooser.setSelectedFontSize(iFont.getSize());
			fontChooser.setSelectedFontStyle(iFont.getStyle());
		}
		else if (e.getActionCommand().equals("Output"))
		{
			Font oFont = controller.getoFont();
			fontChooser.setSelectedFontFamily(oFont.getFamily());
			fontChooser.setSelectedFontSize(oFont.getSize());
			fontChooser.setSelectedFontStyle(oFont.getStyle());
		}
		else if (e.getActionCommand().equals("Comment"))
		{
			Font cFont = controller.getcFont();
			fontChooser.setSelectedFontFamily(cFont.getFamily());
			fontChooser.setSelectedFontSize(cFont.getSize());
			fontChooser.setSelectedFontStyle(cFont.getStyle());
		}

		int result = fontChooser.showDialog(Launcher.getDesktopPane());

		// when the font is chosen, set its values and change the describing value in label
		if (result == JFontChooser.OK_OPTION)
		{
			if (e.getActionCommand().equals("Input"))
			{
				Font iFont = fontChooser.getSelectedFont();
				String labelInputText = iFont.getFamily() + ", " + iFont.getSize(); // input font
				if (iFont.isBold())
					labelInputText += ", Bold";
				if (iFont.isItalic())
					labelInputText += ", Italic";

				controller.getDialog().getLabelInput().setText(labelInputText);

				// input font
				controller.setiFont(iFont);
			}

			else if (e.getActionCommand().equals("Output"))
			{
				Font oFont = fontChooser.getSelectedFont();
				String labelOutputText = oFont.getFamily() + ", " + oFont.getSize(); // output font
				if (oFont.isBold())
					labelOutputText += ", Bold";
				if (oFont.isItalic())
					labelOutputText += ", Italic";

				controller.getDialog().getLabelOutput().setText(labelOutputText);

				// input font
				controller.setoFont(oFont);
			}

			else if (e.getActionCommand().equals("Comment")) // comment font
			{
				Font cFont = fontChooser.getSelectedFont();
				String labelComText = cFont.getFamily() + ", " + cFont.getSize(); // comment font
				if (cFont.isBold())
					labelComText += ", Bold";
				if (cFont.isItalic())
					labelComText += ", Italic";

				controller.getDialog().getLabelCom().setText(labelComText);

				// input font
				controller.setcFont(cFont);
			}
		}
	}
}