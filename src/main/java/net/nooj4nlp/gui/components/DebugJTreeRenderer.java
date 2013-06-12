package net.nooj4nlp.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.miginfocom.swing.MigLayout;
import net.nooj4nlp.engine.Constants;
import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements renderer of Debug Shell's JTree.
 */

public class DebugJTreeRenderer extends DefaultTreeCellRenderer
{

	private static final long serialVersionUID = 1L;
	private static final String PATH = "src/main/java/net/nooj4nlp/gui/components/";

	private Color backgroundColor;

	// images
	private JLabel plusImage;
	private JLabel minusImage;

	/**
	 * Constructor.
	 * 
	 * @param tree
	 *            - JTree to be rendered
	 * @param renderer
	 *            - renderer of a JTree
	 */

	public DebugJTreeRenderer(Color backgroundColor)
	{
		super();

		this.backgroundColor = backgroundColor;

		//get the images, otherwise, put empty labels
		try
		{
			BufferedImage plus = ImageIO.read(new File(PATH + "plus.png"));
			BufferedImage minus = ImageIO.read(new File(PATH + "minus.png"));
			this.plusImage = new JLabel(new ImageIcon(plus));
			this.minusImage = new JLabel(new ImageIcon(minus));
			this.plusImage.setOpaque(false);
			this.minusImage.setOpaque(false);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(Launcher.getDesktopPane(), Constants.ERROR_MESSAGE_CANNOT_LOAD_ICONS,
					Constants.NOOJ_ERROR, JOptionPane.ERROR_MESSAGE);
			this.plusImage = new JLabel();
			this.minusImage = new JLabel();
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{
		if (!leaf)
		{
			//if not a leaf, create a new panel where we will show the data
			JPanel panel = new JPanel();
			String text = (String) ((DefaultMutableTreeNode) value).getUserObject();
			JLabel textLabel = new JLabel(text);
			panel.setLayout(new MigLayout("ins 0", "[16][]", "[]"));
			
			//set expanded/collapsed icon
			panel.add(expanded ? this.minusImage : this.plusImage, "cell 0 0, alignx center, aligny center");
			panel.add(textLabel, "cell 1 0, alignx center, aligny center");
			
			//background color
			panel.setBackground(this.backgroundColor);
			return panel;
		}

		else
			return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	//getters and setters
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}
}