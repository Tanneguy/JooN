package net.nooj4nlp.controller.MorphologyDialog;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.nooj4nlp.engine.Language;

public class ListListener implements ListSelectionListener
{

	private JList listLanguage;
	private JComboBox comboWordRoot, comboCommandSuffix, comboLemma, comboExpression, comboLookup;
	private JLabel lblLanguage;

	public ListListener(JList listLanguage, JComboBox comboWordRoot, JComboBox comboCommandSuffix,
			JComboBox comboLemma, JComboBox comboExpression, JComboBox comboLookup, JLabel lblLanguage)
	{
		super();
		this.listLanguage = listLanguage;
		this.comboWordRoot = comboWordRoot;
		this.comboCommandSuffix = comboCommandSuffix;
		this.comboLemma = comboLemma;
		this.comboExpression = comboExpression;
		this.comboLookup = comboLookup;
		this.lblLanguage = lblLanguage;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0)
	{
		Language lan = new Language((String) listLanguage.getSelectedValue());
		UpdateExamplesForLan(lan);
	}

	private void UpdateExamplesForLan(Language lan)
	{
		lblLanguage.setText("<html>" + lan.natName + " / " + lan.engName + "</html>");

		if (lan.isoName.equals("la"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] artista");
			comboWordRoot.addItem("[2] amo");
			comboWordRoot.addItem("[3] cado");
			comboWordRoot.addItem("[4] altus");
			comboWordRoot.addItem("[5] is");
			comboWordRoot.addItem("[6] res publica");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] rum");
			comboCommandSuffix.addItem("[2] <B>amus");
			comboCommandSuffix.addItem("[3] <B>unt");
			comboCommandSuffix.addItem("[4] <B2>ior");
			comboCommandSuffix.addItem("[5] <L>u<L2>e");
			comboCommandSuffix.addItem("[6] rum<P><B>rum");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] artista");
			comboLemma.addItem("[2] amo");
			comboLemma.addItem("[3] cado");
			comboLemma.addItem("[4] altus");
			comboLemma.addItem("[5] is");
			comboLemma.addItem("[6] res publica");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/nom+s | <E>/voc+s | m/ac+s | e/gen+s | e/dat+s | <E>/ab+s");
			comboExpression
					.addItem("[2] <B>amus/Act+1+p+Pres+Ind | <B>atis/Act+2+p+Pres+Ind | <B>ant/Act+3+p+Pres+Ind");
			comboExpression.addItem("[3] <E>/Act+1+s+Pres+Ind | <B>is/Act+2+s+Pres+Ind | <B>it/Act+3+s+Pres+Ind");
			comboExpression.addItem("[4] <B2>iores/comp+nom+p+m | <B2>iores/comp+voc+p+m | <B2>iores/comp+ac+p+m");
			comboExpression
					.addItem("[5] <E>/nom+m+s | <B>i/nom+m+p | <L>u<L2>e/gen+m+s | <B2>eorum/gen+m+p | <B2>eum/ac+m+s | <B2>eos/ac+m+p");
			comboExpression
					.addItem("[6] <E>/nom+s | e/nom+p | e<P><B>i/gen+s | rum<P><B>rum/gen+p | m<P><B>m/ac+s | s/ac+s");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("artista");
			comboLookup.addItem("amo");
			comboLookup.addItem("cado");
			comboLookup.addItem("altus");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("it"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] uomo");
			comboWordRoot.addItem("[2] arma");
			comboWordRoot.addItem("[3] re");
			comboWordRoot.addItem("[4] avere");
			comboWordRoot.addItem("[5] rompere");
			comboWordRoot.addItem("[6] mostro sacro");
			comboWordRoot.addItem("[7] tavola rotonda");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] <B>ini");
			comboCommandSuffix.addItem("[2] <B>i");
			comboCommandSuffix.addItem("[3] gina");
			comboCommandSuffix.addItem("[4] <BW>hanno");
			comboCommandSuffix.addItem("[5] <B6>uppi");
			comboCommandSuffix.addItem("[6] <B>i<P><B>i");
			comboCommandSuffix.addItem("[7] <B>e<P><B>e");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] artista");
			comboLemma.addItem("[2] amare");
			comboLemma.addItem("[3] cadere");
			comboLemma.addItem("[4] andare");
			comboLemma.addItem("[5] compagno di strada");
			comboLemma.addItem("[6] foglio rosa");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/m+s | <E>/f+s | <B>i/m+p | <B>e/f+p");
			comboExpression
					.addItem("[2] <B3>o/Pr+1+s | <B3>i/Pr+2+s | <B2>/Pr+3+s | <B3>iamo/Pr+1+p | <B2>te/Pr+2+p | <B2>no/Pr+3+p");
			comboExpression
					.addItem("[3] <B3><D>i/Pass+1+s | <B2>sti/Pass+2+s | <B3><D>e/Pass+3+s | <B2>mmo/Pass+1+p | <B2>ste/Pass+2+p | <B3><D>ero/Pass+3+p");
			comboExpression
					.addItem("[4] <BW>vado/Pr+1+s | <BW>vai/Pr+2+s | <BW>va/Pr+3+s |  <B3>iamo/Pr+1+p | <B2>te/Pr+2+p | <BW>vanno/Pr+3+p");
			comboExpression.addItem("[5] <E>/m+s | <P2><B>a/f+s | <P2><B>e/f+p | <P2><B>i/m+p");
			comboExpression.addItem("[6] <E>/m+s  |  <P><B>/m+p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
		}
		else if (lan.isoName.equals("fr") || lan.isoName.equals("ac"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] cousin");
			comboWordRoot.addItem("[2] cheval");
			comboWordRoot.addItem("[3] recordman");
			comboWordRoot.addItem("[4] avoir");
			comboWordRoot.addItem("[5] lever");
			comboWordRoot.addItem("[5] mener");
			comboWordRoot.addItem("[6] cousin germain");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] es");
			comboCommandSuffix.addItem("[2] <B>ux");
			comboCommandSuffix.addItem("[3] <B3>women");
			comboCommandSuffix.addItem("[4] <BW>ont");
			comboCommandSuffix.addItem("[5] <B><L2><B>è<RW>nt");
			comboCommandSuffix.addItem("[6] es<P>es<NW>");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] artiste");
			comboLemma.addItem("[2] aimer");
			comboLemma.addItem("[3] appeler");
			comboLemma.addItem("[3] jeter");
			comboLemma.addItem("[4] cousin germain");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/m+s | <E>/f+s | s/m+p | s/f+p");
			comboExpression
					.addItem("[2] <B>/PR+1+s | <B>s/PR+2+s | <B>/PR+3+s | <B2>ons/PR+1+p | <B>z/PR+2+p | <B>nt/PR+3+p");
			comboExpression
					.addItem("[3] <B2><D>e/PR+1+s | <B2><D>es/PR+2+s | <B2><D>e/PR+3+s + <B2>ons/PR+1+p | <B>z/PR+2+p | <B2><D>ent/PR+3+p");
			comboExpression.addItem("[4] <E>/m+s | e<P>e/f+s | s<P>s/m+p | es<P>es/f+p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("aimer");
			comboLookup.addItem("avoir");
			comboLookup.addItem("cousin");
			comboLookup.addItem("mangerions");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("hy"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] տարի");
			comboWordRoot.addItem("[2] սիրել");
			comboWordRoot.addItem("[3] քոյր");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] ներ");
			comboCommandSuffix.addItem("[2] <B>ցի");
			comboCommandSuffix.addItem("[3] <L><B2><R>ոջ");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] խաղալ");
			comboLemma.addItem("[2] ուտել");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <B>մ/PR+s+1 | <B>ս/PR+s+2 | <B>յ/PR+s+3");
			comboExpression.addItem("[2] <BW>կերայ/Aor+1+s | <BW>կերար/Aor+2+s | <BW>կերաւ/Aor+3+s");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("խաղալ");
			comboLookup.addItem("ուտել");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("ca"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] peu");
			comboWordRoot.addItem("[2] dia");
			comboWordRoot.addItem("[3] bacallà");
			comboWordRoot.addItem("[4] veí");
			comboWordRoot.addItem("[5] arròs");
			comboWordRoot.addItem("[6] professor associat");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] s");
			comboCommandSuffix.addItem("[2] <B>es");
			comboCommandSuffix.addItem("[3] <A>ns");
			comboCommandSuffix.addItem("[4] <B>ïns");
			comboCommandSuffix.addItem("[5] <L><A><R><D>os");
			comboCommandSuffix.addItem("[6] <B>des<P>es");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] tebi");
			comboLemma.addItem("[2] ser");
			comboLemma.addItem("[3] estimar");
			comboLemma.addItem("[4] professor associat");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/m+s | a<L3><À>/f+s | s/m+p | es<L4><À>/f+p");
			comboExpression.addItem("[2] <B2>óc/PR+1+s | <BW>ets/PR+2+s | <BW>és/PR+3+s");
			comboExpression
					.addItem("[3] <B2>o/PR+1+s | <B2>es/PR+2+s | <B>/PR+3+s | <B2>em/PR+1+p | <B2>eu/PR+2+p | <B2>en/PR+3+p");
			comboExpression.addItem("[4] <E>/m+s | <B>da<P>a/f+s | s<P>s/m+p | <B>des<P>es/f+p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("cantar");
			comboLookup.addItem("bacallà");
			comboLookup.addItem("arròs");
			comboLookup.addItem("professor associat");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("sp"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] libro");
			comboWordRoot.addItem("[2] curriculum");
			comboWordRoot.addItem("[3] avión");
			comboWordRoot.addItem("[4] tener");
			comboWordRoot.addItem("[5] hombre rana");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] s");
			comboCommandSuffix.addItem("[2] <B2>a");
			comboCommandSuffix.addItem("[3] <L><A><R>es");
			comboCommandSuffix.addItem("[4] <B4>ienen");
			comboCommandSuffix.addItem("[5] s<P>s");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] ser");
			comboLemma.addItem("[2] conducir");
			comboLemma.addItem("[3] crimen");
			comboLemma.addItem("[3] joven");
			comboLemma.addItem("[4] profesor asociado");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <B2>oy/Pr+1+s | <BW>eres/Pr+2+s | <BW>es/Pr+3+s");
			comboExpression.addItem("[2] <B3>zco/Pr+1+s | <B2>es/Pr+2+s | <B2>e/Pr+3+s | <B>mos/Pr+1+p");
			comboExpression.addItem("[3] es<L5><Á>/m+p");
			comboExpression.addItem("[4] <E>/m+s | s<P>es/m+p | <B>a<P>a/f+s | <B>as<P>as/f+p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("cantar");
			comboLookup.addItem("profesor");
			comboLookup.addItem("profesor asociado");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("ro"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] copac");
			comboWordRoot.addItem("[2] fiu");
			comboWordRoot.addItem("[3] amendă");
			comboWordRoot.addItem("[4] astru");
			comboWordRoot.addItem("[5] floare-de-nu-mă-uita");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] i");
			comboCommandSuffix.addItem("[2] <B>i");
			comboCommandSuffix.addItem("[3] <B2>zi");
			comboCommandSuffix.addItem("[4] <B>i<L3><B>ş");
			comboCommandSuffix.addItem("[5] <PW><B><L><B><R>i");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] cânta");
			comboLemma.addItem("[2] doctor");
			comboLemma.addItem("[3] avea");
			comboLemma.addItem("[4] profesor asociat");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <B>/Pr+1+s | <B2>ţi/Pr+2+s | <B>ă/Pr+3+s");
			comboExpression.addItem("[2] <E>/m+s | iţă/f+s | i/m+p | iţe/f+p");
			comboExpression
					.addItem("[3] <B3> (m/Pr+1+s | i/Pr+2+s | re/Pr+3+s) | <B>m/Pr+1+p | <B>ţi/Pr+2+p | <B3>u/Pr+3+p");
			comboExpression.addItem("[4] <E>/m+s | ă<P><L>a<R>ă/f+s | <B>ţi<P>i/m+p | e<P><L>a<R>e/f+p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("cânta");
			comboLookup.addItem("avea");
			comboLookup.addItem("profesor asociat");
			comboLookup.setSelectedIndex(-1);
		}
		else if (lan.isoName.equals("bg"))
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] бор");
			comboWordRoot.addItem("[2] жена");
			comboWordRoot.addItem("[3] съм");
			comboWordRoot.addItem("[4] връх");
			comboWordRoot.addItem("[5] звънец");
			comboWordRoot.addItem("[6] медицинска сестра");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] ове");
			comboCommandSuffix.addItem("[2] <B>и");
			comboCommandSuffix.addItem("[3] <BW>бях");
			comboCommandSuffix.addItem("[4] <L3>ъ<R><S><R>ът");
			comboCommandSuffix.addItem("[5] <L><B><R1>и");
			comboCommandSuffix.addItem("[6] <P>та");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] Бор");
			comboLemma.addItem("[2] министър");
			comboLemma.addItem("[3] връх");
			comboLemma.addItem("[4] свят");
			comboLemma.addItem("[5] медицинска сестра");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/s+0 | ът/s+l | а/s+h | а/s+c | ове/pl+0 | овете/pl+d");
			comboExpression.addItem("[2] <E>/s+0 | ът/s+l | а/s+h + <L><B><R> (и/pl+0 | ите/pl+d + е/v)");
			comboExpression.addItem("[3] <E>/s+0 | <L3>ъ<R><S><R> (ът/s+l | а/s+h + а/c | ове/pl+0 | овете/pl+d)");
			comboExpression.addItem("[4] <E>/s+0 |  a/c   + <L><B>е<R> (ът/s+l | а/s+h  | ове/pl+0  | овете/pl+d)");
			comboExpression.addItem("[5] <E>/s+0 | <P>та/s+d | <B>и<P><B>и/pl+0 | <B>и<P><B>ите/pl+d)");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
			comboLookup.addItem("изглед");
			comboLookup.addItem("пиша");
			comboLookup.addItem("французин");
			comboLookup.setSelectedIndex(-1);
		}
		else
		// DEFAULT == ENGLISH
		{
			comboWordRoot.removeAllItems();
			comboWordRoot.addItem("[1] table");
			comboWordRoot.addItem("[2] accessory");
			comboWordRoot.addItem("[3] man");
			comboWordRoot.addItem("[4] be");
			comboWordRoot.addItem("[5] stir");
			comboWordRoot.addItem("[5] stop");
			comboWordRoot.addItem("[5] trig");
			comboWordRoot.addItem("[6] knight commander");
			comboWordRoot.setSelectedIndex(-1);

			comboCommandSuffix.removeAllItems();
			comboCommandSuffix.addItem("[1] s");
			comboCommandSuffix.addItem("[2] <B>ies");
			comboCommandSuffix.addItem("[3] <B3>woman");
			comboCommandSuffix.addItem("[4] <BW>is");
			comboCommandSuffix.addItem("[5] <D>ed");
			comboCommandSuffix.addItem("[6] s<P>s");
			comboCommandSuffix.setSelectedIndex(-1);

			comboLemma.removeAllItems();
			comboLemma.addItem("[1] artist");
			comboLemma.addItem("[2] love");
			comboLemma.addItem("[2] help");
			comboLemma.addItem("[3] stir");
			comboLemma.addItem("[3] stop");
			comboLemma.addItem("[3] trig");
			comboLemma.addItem("[4] knight commander");
			comboLemma.setSelectedIndex(-1);

			comboExpression.removeAllItems();
			comboExpression.addItem("[1] <E>/s | s/p");
			comboExpression.addItem("[2] <E>/Pr+1+s | <E>/Pr+2+s | s/Pr+3+s | <E>/Pr+1+p | <E>/Pr+2+p | <E>/Pr+3+p");
			comboExpression.addItem("[3] <D>ed/Pret | <D>ed/PP");
			comboExpression.addItem("[4] <E>/s | s<P>s/p");
			comboExpression.setSelectedIndex(-1);

			comboLookup.removeAllItems();
		}
	}

}