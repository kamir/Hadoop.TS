/*
 * Berechnet das logarithmische Binning einer Messreihe bezogen auf die X-Achse.
 */
package tool;

import chart.simple.MultiChart;
import data.io.MessreihenLoader;
import data.series.Messreihe;
import data.series.MesswertTabelle;
import data.export.OriginProject;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class LogBinningTool {
    
    public static Messreihe getLogBinnedMessreihe( Messreihe mrOrig , int nrOfBins ) { 
        Messreihe mr = new Messreihe();
         
        double data[][] = new double[2][nrOfBins];
                
        mr.setLabel( mrOrig.getLabel() + "_logBin" );
         
        return mr;
    };
    
    public static void main( String[] args ) throws IOException { 
        
                
        Vector<Messreihe> rows = new Vector<Messreihe>();
              
     
        
        // String fn = "D:\\WIKI\\outdegree_all_1.dat";
        
        String fn1 = "D:\\WIKI\\count_links\\out_degree_hist.dat";
        String fn2 = "D:\\WIKI\\count_links\\in_degree_hist.dat";
        
        String fn3 = "D:\\WIKI\\count_links\\tab_log_binned_degree_distr.dat";
        
        File f1 = new File( fn1 );
        File f2 = new File( fn2 );
        File f3 = new File( fn3 );
        
        Messreihe mr1 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f1, 1, 2, 1.2, 1000000);
        mr1 = mr1.calcLogLog();

        Messreihe mr2 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f2, 1, 2, 1.2, 1000000);
        mr2 = mr2.calcLogLog();

        rows.add( mr1 );
        rows.add( mr2 );
        
        MesswertTabelle tab = new MesswertTabelle();
        tab.addMessreihe(mr1);
        tab.addMessreihe(mr2);
        tab.writeToFile( new File( fn3 ) );
        
//        OriginProject pro = new OriginProject();
//        pro.addMessreihen(rows, "log_binned" );
//        
//        pro.closeAllWriter();
        
        MultiChart.open( rows , "", "log(# of links per page)", "log(# of pages)", true);
    }
    
}
