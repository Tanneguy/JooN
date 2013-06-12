package net.nooj4nlp.gui.actions.shells.control;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * 
 * ActionListener that cascades open shells
 *
 */
public class CascadeWindowsActionListener implements ActionListener {

	private JDesktopPane desktopPane;
	
	public CascadeWindowsActionListener(JDesktopPane p){
		desktopPane = p;
	}
	@Override
	public void actionPerformed(ActionEvent e){
		JInternalFrame[] frames = desktopPane.getAllFrames();
	    
		if (frames.length == 0) {
			return;
		}
	 
	    cascadeWindows(frames, desktopPane.getBounds());
	}
	
	private void cascadeWindows(JInternalFrame[] frames, Rectangle dBounds){
	    int margin = 0;
	    Dimension d, cd;
	    
	    // Calculate margins (to make sure each title bar remains visible)
	    for (int i = 0; i < frames.length; i++){
	        d = frames[i].getSize();
	        cd = frames[i].getContentPane().getSize();
	        margin += Math.max(d.width - cd.width,
	                            d.height - cd.height);
	    }
	 
	    int width = dBounds.width - margin;
	    int height = dBounds.height - margin;
	    int offset = 0;
	    
	    
	    for (int i = 0; i < frames.length; i++){
	    	// Don't resize windows / dialog boxes that are not meant to be resized
	    	if(!frames[i].isResizable()){
	    		width = frames[i].getSize().width;
	    		height = frames[i].getSize().height;
	    	}
	    	
	        frames[i].setBounds(dBounds.x + offset,
	                             dBounds.y + offset,
	                             width, height);
	        d = frames[i].getSize();
	        cd = frames[i].getContentPane().getSize();
	        
	        // Bring window forward
	        try {
				frames[i].setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			
	        offset += Math.max(d.width - cd.width,
	                            d.height - cd.height);
	    }
	}
}
