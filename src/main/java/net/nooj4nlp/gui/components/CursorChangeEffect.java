package net.nooj4nlp.gui.components;

import java.awt.Component;
import java.awt.Cursor;

import net.nooj4nlp.gui.main.Launcher;

/**
 * Class implements changing cursor while hovering main frame.
 */
public class CursorChangeEffect
{
	// constants: cross, default, wait/busy and text cursor
	public static final int CURSOR_BUSY = Cursor.WAIT_CURSOR;
	private static final int CURSOR_CROSS = Cursor.CROSSHAIR_CURSOR;
	public static final int CURSOR_DEFAULT = Cursor.DEFAULT_CURSOR;
	private static final int CURSOR_IBEAM = Cursor.TEXT_CURSOR;

	/**
	 * Function changes cursor for all open windows to desirable cursor.
	 * 
	 * @param cursor
	 *            - desirable cursor
	 */
	public static void setCustomCursor(int cursor)
	{
		Launcher.getDesktopPane().setCursor(Cursor.getPredefinedCursor(cursor));
		Component[] componentArray = Launcher.getDesktopPane().getComponents();
		for (Component component : componentArray)
			component.setCursor(Cursor.getPredefinedCursor(cursor));
	}

	/**
	 * Functions sets cross cursor over the given component.
	 * 
	 * @param component
	 *            - desirable component
	 */
	public static void setCrossCursor(Component component)
	{
		component.setCursor(Cursor.getPredefinedCursor(CURSOR_CROSS));
	}

	/**
	 * Function sets text cursor over the given component.
	 * 
	 * @param component
	 *            - desirable component
	 */
	public static void setIBeamCursor(Component component)
	{
		component.setCursor(Cursor.getPredefinedCursor(CURSOR_IBEAM));
	}
}