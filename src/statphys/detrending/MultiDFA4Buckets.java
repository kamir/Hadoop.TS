package statphys.detrending;

import chart.simple.MultiChart;
import data.series.Messreihe;
import hadoopts.core.TSBucket;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import statphys.detrending.methods.DFACore;
import app.experimental.SimpleBucketTool;

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
public class MultiDFA4Buckets {
    
    static String VERSION = "2.1.2";
    
    public static void main(String[] args) throws IOException {
        
        System.out.println( "VERSION: " + VERSION );
        System.err.println( "VERSION: " + VERSION );
        long start = System.currentTimeMillis();
    
        String chartLabel = args[1];
        System.out.println("> folder : " +  args[0] );
        System.out.println("> file   : " +  args[1] );
        
        MultiDFATool4 tool = new MultiDFATool4();
        tool.logLogResults = true;
        tool.showCharts = true;
        tool.storeCharts = false;
        
        File f = new File( args[0] + args[1] );
        
        System.out.println( ">>> " + f.getAbsolutePath() );  
        System.out.println( "    " + f.exists() );  
        if ( !f.exists() ) System.exit(-1);

        TSBucket.default_LIMIT = Integer.parseInt(args[2]); 
        
        TSBucket tsb = SimpleBucketTool.loadBucket( f.getAbsolutePath() );
        
        
        Vector<Messreihe> vmr = SimpleBucketTool.removePeriodicTrend( tsb, 7 );
                
//        Vector<Messreihe> vmr = tsb.getBucketData();
        
        MultiChart.open(vmr);
        
        System.out.println( vmr.size() + " rows." );
        
        int omin = Integer.parseInt(args[3]);
        int omax = Integer.parseInt(args[4]);
        
        double sf = Double.parseDouble( args[5] );
        DFACore.S_SCALE_FACTOR = sf;
        
        int zOrders = omax - omin + 1;
        System.out.println( omax + " " + omin + " " + zOrders );
        int[] orders = new int[zOrders] ;
        int i = 0;
        int o = omin;
        while ( o <= omax) {
            orders[i] = o;
//            System.out.println( i + " " + o );
            i++;
            o++;
        }
        
        Vector<Messreihe> fs = new Vector<Messreihe>();
        
        // nun werden die Berechnungen für verschiedene Ordnungen durchgeführt
        for ( int ord : orders) {
            try {
        
                // MultiDFATool3 implementiert die 
                // Mittelwertsbildung der F(s) FUnktion 
                // für alle Reihen - geht nur gut, wenn 
                // alle gleich sind.
                tool.runDFA(vmr, ord);       
                
                if ( tool.fsFMW.size() != 0 ) {
                    Messreihe mFS = tool.fsFMW.elementAt(0);
                    fs.add( mFS );
                }    
            }
            catch (Exception ex) {
               ex.printStackTrace();
            }
            tool.Fs.clear();
        }
        
        int rows = vmr.size();

        MultiChart.open(fs, "F(s) " + chartLabel + "+rows=" + rows, "log(s)", "log(F(s))", false );
        // MultiChart.open(vmr , "raw data", "t", "y(t)", false );
        
        long end = System.currentTimeMillis();
        
        System.out.println( "> runtime : " + ( (end - start) / (1000 * 60) ) +  " min" );
    }
    
    public MultiDFA4Buckets() { };



}
