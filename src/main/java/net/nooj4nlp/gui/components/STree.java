package net.nooj4nlp.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import net.nooj4nlp.engine.Dic;
import net.nooj4nlp.engine.RefObject;
import net.nooj4nlp.engine.helper.PenAttributes;

public class STree
{

	public static int LINE;
	public static int currentleftposition;
	public String label;
	private ArrayList<Object> children;
	private int depth, width;
	private int position, x, y;

	private JSTree panel;
	private Font font, boldFont, largeFont;
	private HashMap<String, PenAttributes<Color, Float>> pen;
	private Color bColor, fColor, rColor;

	public STree(String label, JSTree panel1)
	{
		this.label = label;
		children = null;
		x = y = -1;
		bColor = Color.BLACK;
		fColor = Color.BLUE;
		rColor = Color.RED;
	
		pen = new HashMap<String, PenAttributes<Color, Float>>();
		pen.put("pen", new PenAttributes<Color, Float>(bColor, 1));
		pen.put("fpen", new PenAttributes<Color, Float>(fColor, 1));

		this.panel = panel1;
		this.font = this.panel.getFont().deriveFont(Font.PLAIN);
		this.boldFont = this.panel.getFont().deriveFont(Font.BOLD);
		this.largeFont = this.panel.getFont().deriveFont(Font.BOLD, font.getSize() * 3);
	}

	public void addChild(STree c)
	{
		if (children == null)
			children = new ArrayList<Object>();
		children.add(c);
	}

	public int computeDepth()
	{
		if (this.children == null)
		{
			depth = 1;
		}
		else
		{
			int max = 0;
			for (Object c : this.children)
			{
				if (c != null)
				{
					int under = ((STree) c).computeDepth();
					if (under > max)
						max = under;
				}
			}
			depth = max + 1;
		}
		return depth;
	}

	public void computeYPosition(int panelheight, int height)
	{
		y = panelheight - depth * height; // +height / 4;
		if (this.children != null)
		{
			for (Object c : this.children)
			{
				if (c != null)
					((STree) c).computeYPosition(panelheight, height);
			}
		}
	}

	public void computeWidth(Graphics g, Font f, boolean dispLexeme)
	{
		FontMetrics metrics = g.getFontMetrics(f);
		if (this.children == null)
		{
			if (this.label.charAt(0) == '$')
			{
				width = metrics.stringWidth(this.label);
				return;
			}
			// a lexeme
			String entry = null, lemma = null, category = null;
			String[] features = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			RefObject<String[]> featuresRef = new RefObject<String[]>(features);
			Dic.parseLexemeSymbol(this.label, entryRef, lemmaRef, categoryRef, featuresRef);
			entry = entryRef.argvalue;
			lemma = lemmaRef.argvalue;
			category = categoryRef.argvalue;
			features = featuresRef.argvalue;
			if (dispLexeme)
			{
				width = entry != null ? metrics.stringWidth(entry) : 0;
				int w2 = lemma != null ? metrics.stringWidth(lemma) : 0;
				if (w2 > width)
					width = w2;
				int w3 = category + Dic.getRidOfSpecialFeaturesPlus(features) != null ? metrics.stringWidth(category
						+ Dic.getRidOfSpecialFeaturesPlus(features)) : 0;
				if (w3 > width)
					width = w3;
			}
			else
			{
				if (entry != null && entry.equals(""))
					width = metrics.stringWidth(lemma);
				else
					width = entry != null ? metrics.stringWidth(entry) : 0;
			}
		}
		else
		{
			width = this.label != null ? metrics.stringWidth(this.label) : 0;
			for (Object c : this.children)
			{
				if (c != null)
					((STree) c).computeWidth(g, f, dispLexeme);
			}
		}
	}

	public void computeXPosition(int margin)
	{
		if (this.children == null)
		{
			this.x = currentleftposition;
			currentleftposition += this.width + margin;
			this.position = this.x + this.width / 2;
		}
		else
		{
			int pos = 0;
			for (int i = 0; i < this.children.size(); i++)
			{
				STree c = (STree) this.children.get(i);
				if (c != null)
				{
					c.computeXPosition(margin);
					pos += c.position;
				}
			}
			this.position = pos / this.children.size(); // current position is average of all children's
			this.x = this.position - this.width / 2;
		}
	}

	public void draw(Graphics g, boolean dispLexeme, HashMap<String, ArrayList<Integer>> bridges)
	{
		if (x == -1 || y == -1)
			return;

		if (this.children == null)
		{
			if (this.label.charAt(0) == '$')
			{
				// a variable
				drawLeafVariable(g, rColor, this.label.substring(1));
				return;
			}

			// a lexeme
			String entry = null, lemma = null, category = null;
			String[] features = null;
			RefObject<String> entryRef = new RefObject<String>(entry);
			RefObject<String> lemmaRef = new RefObject<String>(lemma);
			RefObject<String> categoryRef = new RefObject<String>(category);
			RefObject<String[]> featuresRef = new RefObject<String[]>(features);

			Dic.parseLexemeSymbol(this.label, entryRef, lemmaRef, categoryRef, featuresRef);

			entry = entryRef.argvalue;
			lemma = lemmaRef.argvalue;
			category = categoryRef.argvalue;
			features = featuresRef.argvalue;
			String information = Dic.getRidOfSpecialFeatures(features);
			boolean frozen = (information.indexOf("XREF") != -1);
			if (frozen)
			{
				information = Dic.getRidOfSpecialFeaturesPlus(features);
				drawLeaf(g, dispLexeme, fColor, pen.get("fpen"), entry, lemma, category, information);

				// compute and keep the XREF value to draw the bridges between the XREF components
				String xref = null;
				for (String feat : features)
				{
					String pname = null, pvalue = null;
					RefObject<String> pnameRef = new RefObject<String>(pname);
					RefObject<String> pvalueRef = new RefObject<String>(pvalue);
					Dic.getPropertyNameValue(feat, pnameRef, pvalueRef);
					pname = pnameRef.argvalue;
					pvalue = pvalueRef.argvalue;
					if (pname != null && pname.equals("XREF"))
					{
						xref = pvalue;
						break;
					}
				}
				if (xref != null)
				{
					if (!bridges.containsKey(xref))
						bridges.put(xref, new ArrayList<Integer>());
					ArrayList<Integer> a = bridges.get(xref);
					a.add(this.x + this.width / 2);
				}
			}
			else
			{
				drawLeaf(g, dispLexeme, bColor, pen.get("pen"), entry, lemma, category, information);
			}
		}
		else
		{
			Graphics2D graphics = (Graphics2D) g;
			graphics.setColor(bColor);
			graphics.setFont(boldFont);
			graphics.drawString(label, x, y + LINE / 2);
			for (Object o : this.children)
			{
				STree c = (STree) o;
				if (c != null)
				{
					c.draw(g, dispLexeme, bridges);
					
					if (c.children != null || c.label.charAt(0) != '$')
					{
						graphics.setColor(pen.get("pen").color);
						graphics.setStroke(new BasicStroke(pen.get("pen").stroke));
						graphics.drawLine(position, y + LINE, c.position, y + LINE * 4);
						graphics.drawLine(c.position, y + LINE * 4, c.position, c.y - LINE / 2);
					}
				}
			}
		}
	}

	private void drawLeaf(Graphics g, boolean dispLexeme, Color brush, PenAttributes<Color, Float> penAttributes,
			String entry, String lemma, String category, String information)
	{
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(brush);
		graphics.setFont(font);
		if (dispLexeme)
		{
			if (information != null && information.equals("WF"))
			{
				if (entry != null && !entry.equals(lemma))
				{
					graphics.drawString(entry, x, y + LINE / 3 + LINE / 2);
					graphics.drawString(lemma, x, y + 2 * LINE - LINE / 3 + LINE / 2);
				}
				else
				{
					graphics.drawString(entry, x, y + LINE + LINE / 2);
				}
			}
			else
			{
				if (entry != null && !entry.equals(lemma) && !entry.equals("") && !lemma.equals("SYNTAX"))
				{
					graphics.drawString(entry, x, y + LINE / 2);
					graphics.drawString(lemma, x, y + LINE + LINE / 2);
					graphics.drawString(category + information, x, y + 2 * LINE + LINE / 2);
				}
				else
				{
					if (entry != null && !entry.equals(""))
						graphics.drawString(entry, x, y + LINE / 3 + LINE / 2);
					else if (lemma != null && !lemma.equals("SYNTAX"))
						graphics.drawString(lemma, x, y + LINE / 3 + LINE / 2);
					graphics.drawString(category + information, x, y + 2 * LINE - LINE / 3 + LINE / 2);
				}
			}
			graphics.setColor(pen.get("pen").color);
			graphics.setStroke(new BasicStroke(pen.get("pen").stroke));
			graphics.drawLine(x, y - LINE / 2, x + width, y - LINE / 2);
			graphics.drawLine(x, y + 3 * LINE, x + width, y + 3 * LINE);
			graphics.drawLine(x, y + 3 * LINE, x, y - LINE / 2);
			graphics.drawLine(x + width, y + 3 * LINE, x + width, y - LINE / 2);
		}
		else
		{
			if (entry != null && entry.equals(""))
				graphics.drawString(lemma, x, y + LINE / 2);
			else
				graphics.drawString(entry, x, y + LINE / 2);
			graphics.setColor(pen.get("pen").color);
			graphics.setStroke(new BasicStroke(pen.get("pen").stroke));
			graphics.drawLine(x, y - LINE / 2, x + width, y - LINE / 2);
			graphics.drawLine(x, y + LINE, x + width, y + LINE);
			graphics.drawLine(x, y + LINE, x, y - LINE / 2);
			graphics.drawLine(x + width, y + LINE, x + width, y - LINE / 2);
		}

	}

	private void drawLeafVariable(Graphics g, Color brush, String substring)
	{
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(brush);
		graphics.setFont(largeFont);
		graphics.drawString(Character.toString(label.charAt(0)), x, y + LINE / 2);
		if (label.length() > 1)
		{
			FontMetrics metrics = g.getFontMetrics(largeFont);
			int w = metrics.stringWidth("(");
			int h = metrics.getHeight();
			graphics.setFont(font);
			graphics.drawString(label.substring(1), x + w / 2, y + h + LINE / 2);
		}
	}
}