

//first class, for editor




package spellcheck;

import javax.swing.*; // * load all classes
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.event.WindowAdapter; // loading WindowAdapter
import java.awt.event.WindowEvent;

public class Editor extends JFrame implements ActionListener {

		private JFrame frejm;
		private SCheck sc;
		private JTextArea txt;
		private JTextArea txt2;
		private String otxt="";				//original text
		private String poslednjidir="D:\\Java\\SpellChecker";
		private String otvorenfajl="";
		private String dictionary="D:\\Java\\SpellChecker\\dictionary.txt";
		
		Editor(){
			
			sc=new SCheck();
			sc.readR(dictionary);
			frejm = new JFrame("Mini-Editor");
			JPanel txtarea = new JPanel();
			txtarea.setLayout(new GridLayout(1,2));

			txt = new JTextArea(100,200);
			txt2 = new JTextArea(100,200);
			JScrollPane scroll = new JScrollPane(txt); 			//makes scroll pane and adds text
			JScrollPane scroll2 = new JScrollPane(txt2);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			txt.setCaretPosition(0);
			txt2.setCaretPosition(0);
			frejm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JMenuBar meni = new JMenuBar(); //meni
			JMenu fajl= new JMenu("Fajl"); //first column
			
			JMenuItem novi= new JMenuItem("Novi"); //options of first column
			JMenuItem otvori= new JMenuItem("Otvori");
			JMenuItem snimi= new JMenuItem("Snimi");
			JMenuItem sacuvajna=new JMenuItem("Sacuvaj na");
			fajl.add(novi); //adding options to first column
			fajl.add(otvori);
			fajl.add(snimi);
			fajl.add(sacuvajna);
			meni.add(fajl); //adding first column on meni
			novi.addActionListener(this); 
			otvori.addActionListener(this);
			snimi.addActionListener(this);
			sacuvajna.addActionListener(this);
			
			JMenu obrada= new JMenu("Obrada"); //second column
			JMenuItem ispravi= new JMenuItem("Ispravi"); 
			obrada.add(ispravi);
			meni.add(obrada);
			ispravi.addActionListener(this);
			
			JMenu dictionary= new JMenu("dictionary");
			JMenuItem izaberi=new JMenuItem("Izaberi");
			dictionary.add(izaberi);
			meni.add(dictionary);
			izaberi.addActionListener(this);
			
			frejm.setJMenuBar(meni);
			frejm.add(txtarea);
			txtarea.add(scroll);
			txtarea.add(scroll2);
			frejm.setSize(1366,780);
			frejm.setVisible(true);
			txt2.setEnabled(true);
		}
		
		public void actionPerformed(ActionEvent e) { 
			String s =e.getActionCommand();
			if (s.equals("Snimi")) snimi();
			else if (s.equals("Otvori")) otvori();
			else if(s.equals("Sacuvaj na")) sacuvajna();
			else if(s.equals("Izaberi")) izaberi();
			else if (s.equals("Novi")) {
				txt.setText("");
				txt2.setText("");
				otvorenfajl="";
			}
			else if (s.equals("Ispravi")) {
				
					try {
						otxt=txt.getText();
						txt2.setText(ispravi());
						txt.setCaretPosition(0);
						txt2.setCaretPosition(0);
					}
					catch(Exception a){
						JOptionPane.showMessageDialog(frejm,"Desio se problem"+a);
					}
					
				}
				else
					JOptionPane.showMessageDialog(frejm, "Vec ste uradili ispravak, prvo povratite txt");
		} 
		public void otvori() {
			JFileChooser jfc = new JFileChooser(poslednjidir);
			int odgovor = jfc.showOpenDialog(null);
				if(odgovor == JFileChooser.APPROVE_OPTION) {
					File fajl = new File(jfc.getSelectedFile().getAbsolutePath());
					try {
						String s="",t="";
						FileReader fr = new FileReader(fajl, StandardCharsets.ISO_8859_1);
						BufferedReader br = new BufferedReader(fr);
						s=br.readLine();
						while((t=br.readLine())!= null)
							s=s+"\n"+t;
						br.close();
						txt.setText(s);
					}
					catch(Exception e) {
						JOptionPane.showMessageDialog(frejm, e.getMessage());
					}
					poslednjidir = fajl.getParent();
					otvorenfajl = fajl.getAbsolutePath();
				}
				else JOptionPane.showMessageDialog(frejm, "Korisnik je odustao");
			txt.setCaretPosition(0);
			otxt=txt.getText();
			txt2.setText("");
			
		}
		
		public void snimi() {
			if(!otvorenfajl.equals("")) {
				File fajl = new File(otvorenfajl);
				try {
					FileWriter fw = new FileWriter(fajl,StandardCharsets.ISO_8859_1);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(txt.getText());
					bw.flush();
					bw.close();
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(frejm, e.getMessage());
				}
				otxt=txt.getText();
			}
			else sacuvajna();
		}
		
		public void sacuvajna() {
			
			JFileChooser jfc = new JFileChooser(poslednjidir);
			int odgovor = jfc.showSaveDialog(null);
			if (odgovor == JFileChooser.APPROVE_OPTION) {
				File fajl = new File(jfc.getSelectedFile().getAbsolutePath());
				try {
					FileWriter fw = new FileWriter(fajl,StandardCharsets.ISO_8859_1);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(txt.getText());
					bw.flush();
					bw.close();
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(frejm, e.getMessage());
				}
				poslednjidir = fajl.getParent();
				otvorenfajl = fajl.getAbsolutePath();
				otxt=txt.getText();
			}
			
		}
		public String ispravi() {
			
			Highlighter highlight = txt.getHighlighter();
			highlight.removeAllHighlights();
			snimi();							//if its new document, we need to save it first
			sc.readT(otvorenfajl);						//read txt
			if(sc.empty())
				return "Niste uneli nista";
			if(sc.emptyr())
				return "Empty dictionary";
			String[] words=sc.words(); 			//pointer on words from txt
			List<String> marked = new ArrayList<>();				//list of marked words
			String entire="";
			String change="";
			String part=otxt;
			for(int i=0;i<words.length;i++) {
				try {
						int p=0;
						entire+=part.substring(p,part.toUpperCase().indexOf(words[i].toUpperCase(),0));			//takes part in front of string
						p=part.toUpperCase().indexOf(words[i].toUpperCase(),0)+words[i].length(); 				//starting point moves to end of the word so we could later cut off string
						part=part.substring(p);					//cuts off remainder of string, gets part which needs to be checked further
						change=sc.check(words[i]);
						if(change.toUpperCase().equals(words[i].toUpperCase())) { 	//checks if change is equal to word and if its already highlighted
							if(!(marked.contains(words[i])))
							{
								markRight(words[i]);						//hightlight
								marked.add(words[i]);
							}
						}
						else
							{
							if(!(marked.contains(words[i])))
							{
								markMistake(words[i]);
								marked.add(words[i]);
							}
							}
						entire+=change;		// we add change in between separated strings
				}
				catch(Exception e) {
						System.out.println("Desio se problem"+e);
				}
			}
			marked.clear();
			return entire; 			//in the end part comes down to empty string and we return entire text
		}
		public void izaberi() {
			
			JFileChooser jfc = new JFileChooser(poslednjidir);
			int odgovor = jfc.showOpenDialog(null);
				if(odgovor == JFileChooser.APPROVE_OPTION) {
					dictionary=jfc.getSelectedFile().getAbsolutePath();
					sc.readR(dictionary);
					Highlighter highlight = txt.getHighlighter();
					highlight.removeAllHighlights();
					txt2.setText("");
				}
				else 
					JOptionPane.showMessageDialog(frejm, "Korisnik je odustao");
			
		}
		public void markMistake(String s) {
			try {
			Highlighter highlight = txt.getHighlighter();
		    HighlightPainter paint = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
		    int p=0;
		    while((p=txt.getText().toUpperCase().indexOf(s.toUpperCase(),p))>=0){ 		//p gets value of first string that corresponds to word
		    	highlight.addHighlight(p,p+s.length(),paint);
		    	p+=s.length();				//skips that word after highlighting
		    }
			}
			catch(Exception e) {
				System.out.println("Desio se problem"+e);
			}
		}
		public void markRight(String s) {
			try {
			Highlighter highlight = txt.getHighlighter();
		    HighlightPainter paint = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
		    int p=0;
		    while((p=txt.getText().toUpperCase().indexOf(s.toUpperCase(),p))>=0){
		    	highlight.addHighlight(p,p+s.length(),paint);
		    	p+=s.length();
		    }
			}
			catch(Exception e) {
				System.out.println("Desio se problem"+e);
			}
		}
		
		public static void main(String args[]) {
			
			Editor editor= new Editor();
		
		}
}







// second class, for backend operations and levenshtein algorithm







package spellcheck;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SCheck {
	private String[] dictionary;
	private String[] txt;
	
	public void readT(String naziv) {
		try {
			txt=Files.readAllLines(Paths.get(naziv)).toArray(new String[0]);
			List<String> words = new ArrayList<>();   		//list of words
			for(int i=0;i<txt.length;i++) {
				if(txt[i].equals(""))continue;
				String[] pom=txt[i].split("[\t\n\"\',; .:<>@|#$!+=-]+");  		//division of line into words
				for(int j=0;j<pom.length;j++) { 
					words.add(pom[j]);  				//adding every word to list
				}
			}
			txt = words.toArray(new String[words.size()]); 		//conversion of list to array of words
			words.clear();
		}
		catch(Exception e) {
			System.out.print("Desio se problem "+ e);
		}
	}
	public void readR(String naziv) {
		try {
			dictionary = Files.readAllLines(Paths.get(naziv)).toArray(new String[0]);
			List<String> words = new ArrayList<>();			//list of words
			for(int i=0;i<dictionary.length;i++) {
				if(dictionary[i].equals(""))continue;
				String[] pom=dictionary[i].split("[\t\n\"\',; .:<>@|#$!+=-]+");  		//division of line in words
			for(int j=0;j<pom.length;j++) 
				words.add(pom[j]); 			//adding words to list
			
			}
			dictionary = words.toArray(new String[words.size()]); 			//conversion of list to array of words
			words.clear();
		}
		catch(Exception e) {
			System.out.print("Desio se problem "+ e);
		}
	}
	public boolean empty() {
		if(txt == null)
			return true;
		else
			return false;
	}
	public boolean emptyr() {
		if(dictionary == null)
			return true;
		else
			return false;
	}
	public double levenstein(String s, String t) { //levenshtein algorithm
		s = s.toLowerCase();
		t = t.toLowerCase();
		int p=s.length(); 			//word length
		int q=t.length();
		int[][] m = new int[p+1][q+1]; 		//matrix
		
		for(int i=0;i<=p;i++)
			m[i][0]=i;
		for(int i=0;i<=q;i++)
			m[0][i]=i;
		
		for(int i=1;i<=p;i++)
			for(int j=1;j<=q;j++)
			{
				int min=m[i-1][j-1];
				if(s.charAt(i-1) != t.charAt(j-1))
					min++;
				if(min>m[i][j-1]+1)
					min=m[i][j-1]+1;
				if(min>m[i-1][j]+1)
					min=m[i-1][j]+1;
				m[i][j]=min;
				
			}
		return (double) m[p][q]/(p+q);
		
	}
	
	public String[] words() {
		return txt;
	}
	
	public String check(String s) throws Exception{
		if(dictionary == null) 
			throw new NullPointerException();
		for(int i=0;i<dictionary.length;i++) 
			if (s.toUpperCase().equals(dictionary[i].toUpperCase()))					//checks if word is in dictionary
				return s; 							
		double min = levenstein(dictionary[0],s);
		List<String> words = new ArrayList<>();			//list of words similar to desired
		for(int i=0;i<dictionary.length;i++) {				//going through dictionary and comparing words
			double r = levenstein(dictionary[i],s);			//for every word levenshteins distance is calculated
			if(r<min) {
				min = r;				//if distance of current word is less then the previous min, min is changed
				words.clear();			//words that were at previous min are being cleared
			}
			if(r == min) {				//if current word is on equal distance, and its not in list, we add it
				if(!words.contains(dictionary[i]))words.add(dictionary[i]);
			}
		}
		String[] pom1=words.toArray(new String[words.size()]);			
		String pom=s+"("+pom1[0];			//variable for return
		for(int i=1;i<pom1.length;i++) 
			pom+=", "+pom1[i];			
		return pom+")";
		
	}

}

