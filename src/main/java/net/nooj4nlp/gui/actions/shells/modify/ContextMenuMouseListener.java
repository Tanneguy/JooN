package net.nooj4nlp.gui.actions.shells.modify;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

/**
 * 
 * MouseListener that displays a right-click (pop-up) menu
 *
 */
public class ContextMenuMouseListener implements MouseListener {

	private JPopupMenu pop;
	
	public ContextMenuMouseListener(JPopupMenu p){
		pop = p;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.isPopupTrigger()){
			pop.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}
}
