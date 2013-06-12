package net.nooj4nlp.controller.MorphologyDialog;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;

import net.nooj4nlp.engine.Gram;
import net.nooj4nlp.engine.Language;
import net.nooj4nlp.engine.RefObject;

public class AnimationTask extends TimerTask {

	private DefaultListModel resultModel;
	private String word, command;
    private int icommand;
    private Language lan;
    private Timer timer;
	
	public AnimationTask(DefaultListModel resultModel,
			String word, String command, int icommand, Language lan, Timer timer) {
		super();
		this.resultModel = resultModel;
		this.word = word;
		this.command = command;
		this.icommand = icommand;
		this.lan = lan;
		this.timer = timer;
	}

	@Override
	public void run() {
        String com = command.substring (0,icommand);

        int pos = 0;
        RefObject<Integer> posRef = new RefObject<Integer>(pos);
        String result = Gram.processInflection (lan, word, com, posRef);
        pos = posRef.argvalue;
        int index = result.indexOf ('<'); // test if result contains an "<INVALID CMD=...>"
        resultModel.removeAllElements();
        resultModel.addElement(result.substring(0, pos) + "│" + result.substring(pos) + " " + command.substring(icommand));
        if (index != -1)
        {
           // <INVALID CMD=...> : stop timer
           timer.cancel();
        }
        else
        {
           if (icommand < command.length() && command.charAt(icommand) == '<')
              for (icommand++;icommand<command.length() && command.charAt(icommand)!='>';icommand++);
           else if (icommand < command.length() && command.charAt(icommand) == '"')
              for (icommand++;icommand<command.length() && command.charAt(icommand)!='"';icommand++);
           else if (icommand < command.length() && command.charAt(icommand) == '\\')
              icommand++;
           icommand++;
           if (icommand > command.length())
           {
              timer.cancel();
           }
        }

	}

}
