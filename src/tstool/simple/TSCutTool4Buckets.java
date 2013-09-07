package tstool.simple;

import data.series.Messreihe;
import hadoopts.buckets.BucketLoader;
import hadoopts.core.TSBucket;
import java.io.BufferedReader;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * 
 * Im Cluster Modus wird dann die einzelne Reihe mit einer einzelnen
 * Fluctuations-Function in einem neuen Bucket gespeichert.
 * 
 * Über diesem kann mann dann einfach mitteln oder Filtern und dann mitteln.
 * 
 * Die Zeitaufwendige DFA kann damit pro Reihe parrallel und stets nur einmal
 * erstellt werden.
 * 
 * Dann wird immer nur ein neues Bucket erstellt, wenn neue Filter nötig sind.
 * 
 * Damit hat man hier ein Beispiel für ein "DERIVED" Bucket, welches exakt einem
 * Master zugeordnet ist.
 * 
 * 
 */



/**
 * Begrenzung der Segementlänge auf N/4 aufgehoben ...
 *
 * Das MultiDFA-Tool berechnet für eine Menge von Zeitreihen in einem, die z.B. zu
 * TSBucket DFA einzeln.
 *
 * @author kamir
 */
public class TSCutTool4Buckets {
    
    static String VERSION = "2.1.2";

    static Hashtable<Integer,Integer> von = new Hashtable<Integer,Integer>();
    static Hashtable<Integer,Integer> bis = new Hashtable<Integer,Integer>();

    public static void main(String[] args) throws IOException {
        
        Vector<Integer> ids = loadIdList();
                
        System.out.println( "VERSION: " + VERSION );
        System.err.println( "VERSION: " + VERSION );
        long start = System.currentTimeMillis();
    
        String chartLabel = args[1];
        System.out.println("> folder : " +  args[0] );
        System.out.println("> file   : " +  args[1] );
        
        SingleTsIntervallCutTool tool = new SingleTsIntervallCutTool();
        tool.ids = ids;
        tool.von = von;
        tool.bis = bis;
        
        
        File f = new File( args[0] + args[1] );
        
        System.out.println( ">>> " + f.getAbsolutePath() );  
        System.out.println( "    " + f.exists() );  
        if ( !f.exists() ) System.exit(-1);

        TSBucket.default_LIMIT = Integer.MAX_VALUE; 
        
        BucketLoader bl = new BucketLoader();
        
        TSBucket tsb = bl.processBucket( f.getAbsolutePath(), tool );
        
//        Vector<Messreihe> vmr = tsb.getBucketData();
//        
//        MultiChart.open(vmr);
//        
//        System.out.println( vmr.size() + " rows." );
//        
        Vector<Messreihe> fs = new Vector<Messreihe>();
        
        // nun wird die Berechnungen durchgeführt
        try {


        }
        catch (Exception ex) {
           ex.printStackTrace();
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println( "> runtime : " + ( (end - start) / (1000 * 60) ) +  " min" );
    }

    private static Vector<Integer> loadIdList() throws FileNotFoundException, IOException {
        Vector<Integer> dat = new Vector<Integer>();
        
        // RESULT DES JOINS
        BufferedReader br = new BufferedReader( new FileReader("/home/kamir/SYNC_MAC_C/DEV/Hadoop.TS/mergedList.csv") );
        br.readLine();
        while( br.ready() ) { 
            String l = br.readLine();
            
            StringTokenizer st = new StringTokenizer( l , ",");
            String t1 = st.nextToken(); 
            String t2 = st.nextToken(); 
            String t3 = st.nextToken(); 
            String t4 = st.nextToken(); 
            String t5 = st.nextToken(); 
            
            
            int id = Integer.parseInt(t1.substring(1, t1.length()-1)); 
            int ivon = Integer.parseInt(t4.substring(1, t4.length()-1)); 
            int ibis = Integer.parseInt(t5.substring(1, t5.length()-1)); 
            
            dat.add(id);

            Integer iid = new Integer( id );
            
            von.put(iid, new Integer(ivon));
            bis.put(iid, new Integer(ibis));
            
            System.out.println( id + " " +ivon + " " + ibis );
        }
        return dat;
        
    }
    



}
