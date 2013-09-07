/*
 * A representation of a local link-network of wikipedia pages.
 * 
 * It is used for data deduplication within the mapper of our
 * data extraction tool.
 * 
 */
package hadoopts.topics.wikipedia;

import data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.Text; 
/**
 *
 * @author kamir
 */
public class LocalWikipediaNetwork2 {

    HashSet<String> names = new HashSet<String>();
    HashSet<String> langs = new HashSet<String>();
    public HashSet<String> codes = new HashSet<String>();
    public HashMap<String, String> inverted_coded_pages = new HashMap<String, String>();
    public HashMap<String, Vector<String>> coded_pages = new HashMap<String, Vector<String>>();

    public void loadListFile(File f) throws FileNotFoundException, IOException {

        System.out.println(">>> LOAD LISTFILE ... " + f.getAbsolutePath());
        System.out.println(">>> debug = " + debug );

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {

            String line = br.readLine();

            String[] l = line.split("\t");

            String code = l[0];
            String lang = l[1];
            String name = l[2];
            // System.out.println("[ > " + line);

            if (!codes.contains(code)) {
                codes.add(code);
            }

            if (!langs.contains(lang)) {
                langs.add(lang);
            }

            if (!names.contains(l[1] + "\t" + l[2])) {
                names.add(l[1] + "\t" + l[2]);
                
            }

            inverted_coded_pages.put(l[1] + "___" + l[2], code);
            
            Vector<String> listeFuerCode = coded_pages.get(code);
            if ( listeFuerCode == null ) { 
                listeFuerCode = new Vector<String>();
            }
            listeFuerCode.add(l[1] + "___" + l[2]);
            coded_pages.put(code,listeFuerCode);

            // VERARBEITE HIER noch die "lang" und "page"
            // System.out.println( line );
        }
        
//        System.out.println( "+++~~~ " + inverted_coded_pages.size() );
    }
    public static Hashtable<String, Vector<String[]>> nodeListeHASHED = null;

    public void loadListFile2(File f) throws FileNotFoundException, IOException {

        nodeListeHASHED = new Hashtable<String, Vector<String[]>>();

        System.out.println(">>> LOAD LISTFILE ... as WikiNodes " + f.getAbsolutePath() + " " + f.canRead() );

        int i = 0;
        
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {

            String line = br.readLine();
            // System.out.println("[*> " + line);

            String[] l = line.split("\t");

            String code = l[0];

            Vector<String[]> liste = getList(code);
            String[] wn = new String[2];
            wn[0] = l[1];
            wn[1] = l[2];
            
            liste.add(wn);
            i++;
        }


        System.out.println("> nr groups            : " + nodeListeHASHED.keySet().size() );
        System.out.println("> WikiNodes            : " + i );
    }
    
    public static boolean debug = false;

    public String getGroup(Text key) {
        String group = inverted_coded_pages.get(key.toString());
        if ( debug && group == null ) {
            System.out.println( "lookup: " + key.toString() + " ... " + group );
        }
        return group;
    }

    private Vector<String[]> getList(String code) {
        Vector<String[]> l = nodeListeHASHED.get(code);
        if (l == null) {
            l = new Vector<String[]>();
            nodeListeHASHED.put(code, l);
        }
        return l;
    }

    public Vector<String> getNamesForGroup(String g) {
        return coded_pages.get(g);
    }

    
    
 

 

 
}
