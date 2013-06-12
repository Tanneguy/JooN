package net.nooj4nlp.engine;

import java.util.Comparator;


public class DicItemComparer implements Comparator<String> {

	private Language lan;
	
	public DicItemComparer(Language lan) {
		super();
		this.lan = lan;
	}

	@Override
	public int compare(String textx, String texty) {
        if (textx.length() == 0 && texty.length() == 0) return 0;
        else if (textx.length() == 0) return -1;
        else if (texty.length() == 0) return 1;

        // remove comments
        StringBuilder tmp1 = new StringBuilder();
        for (int i = 0; i < textx.length() && textx.charAt(i) != '#'; i++)
        {
           if (textx.charAt(i) == '\\')
           {
              i++;
           }
           tmp1.append(textx.charAt(i));
        }

        StringBuilder tmp2 = new StringBuilder();
        for (int i = 0; i < texty.length() && texty.charAt(i) != '#'; i++)
        {
           if (texty.charAt(i) == '\\')
           {
              i++;
           }
           tmp2.append(texty.charAt(i));
        }

        if (tmp1.length() == 0) return -1;
        else if (tmp2.length() == 0) return 1;
        else
        	return lan.sortTexts(tmp1.toString(), tmp2.toString(), false);
	}
	
}
