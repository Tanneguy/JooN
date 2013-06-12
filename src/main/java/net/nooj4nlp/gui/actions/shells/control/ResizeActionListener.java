package net.nooj4nlp.gui.actions.shells.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class ResizeActionListener implements ActionListener {

	private JFrame mainFrame;
	private int size;
	
	public ResizeActionListener(JFrame f, int s){
		mainFrame = f;
		size = s;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(size == 1){
			mainFrame.setSize(800, 600);
		}
		else{
			mainFrame.setSize(1024, 768);
		}
	}

}
