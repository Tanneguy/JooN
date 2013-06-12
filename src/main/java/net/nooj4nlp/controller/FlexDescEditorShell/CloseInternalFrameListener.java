package net.nooj4nlp.controller.FlexDescEditorShell;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class CloseInternalFrameListener implements InternalFrameListener
{

	private FlexDescEditorShellController controller;

	public CloseInternalFrameListener(FlexDescEditorShellController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent arg0)
	{
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent arg0)
	{
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0)
	{
		controller.close();
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0)
	{
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0)
	{
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent arg0)
	{
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent arg0)
	{
	}
}
