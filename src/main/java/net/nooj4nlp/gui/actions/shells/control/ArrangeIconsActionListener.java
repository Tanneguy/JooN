package net.nooj4nlp.gui.actions.shells.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * 
 * ActionListener that arranges iconified (minimized) shells
 *
 */
public class ArrangeIconsActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	
	public ArrangeIconsActionListener(JDesktopPane p){
		desktopPane = p;
	}
	@Override
	public void actionPerformed(ActionEvent e){
		int offset = 0;
		for (JInternalFrame frame : desktopPane.getAllFrames()){    
            if (frame.isIcon()){    
                JInternalFrame.JDesktopIcon icon = frame.getDesktopIcon();    
                icon.setLocation(offset, desktopPane.getHeight() - icon.getHeight());
                offset += icon.getWidth();
            }    
        }    
	}
}