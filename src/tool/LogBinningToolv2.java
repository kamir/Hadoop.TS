/*
 * Berechnet das logarithmische Binning einer Messreihe bezogen auf die X-Achse.
 */
package tool;

import data.io.datafile.DataFileTool;
import chart.simple.MultiChart;
import data.io.LineFilter;
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
public class LogBinningToolv2 {
    
    public static Messreihe getLogBinnedMessreihe( Messreihe mrOrig , int nrOfBins ) { 
        Messreihe mr = new Messreihe();         
        double data[][] = new double[2][nrOfBins];
        mr.setLabel( mrOrig.getLabel() + "_logBin" );
        return mr;
    };
    
    public static void main( String[] args ) throws IOException { 
           
        Vector<Messreihe> rows = new Vector<Messreihe>();   // log 
        Vector<Messreihe> rows2 = new Vector<Messreihe>();  // normal
                
                
        MessreihenLoader.debug = true;
//        String fn1 = "D:\\WIKI\\count_links\\out_degree_hist.dat";
//        String fn2 = "D:\\WIKI\\count_links\\in_degree_hist.dat"; 
        
        String fn1 = "P:\\DATA\\out_degree_hist2.dat";
        String fn2 = "P:\\DATA\\in_degree_hist2.dat"; 
        
        String fn0 = "P:\\DATA\\in_degree_hist2_REDIRECT_IN_ONLY.dat";
        String fn3 = "P:\\DATA\\out_degree_hist2_REDIRECT_IN_ONLY.dat";
        
        String fn4 = "P:\\DATA\\in_degree_hist2_REDIRECT_OUT_ONLY.dat";
        String fn5 = "P:\\DATA\\out_degree_hist2_REDIRECT_OUT_ONLY.dat";
        
        File f0 = new File( fn0 );
        File f1 = new File( fn1 );
        File f2 = new File( fn2 );
        File f3 = new File( fn3 );        
        File f4 = new File( fn4 );
        File f5 = new File( fn5 );
        
//        DataFileTool.head( f0 , 10 );
//        DataFileTool.head( f1 , 10 );
//        DataFileTool.head( f2 , 10 );
//        DataFileTool.head( f3 , 10 );
//        DataFileTool.head( f4 , 10 );
//        DataFileTool.head( f5 , 10 );
        
//         System.exit( 0 );
//           
        int maxValue = 1000000;
        double f = 1.2;
        MessreihenLoader.debug = false;
        //MessreihenLoader.limit = 10000;
        
               
        Messreihe mr1 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f1, 1, 2, f, maxValue, " ", new LineFilter());
        Messreihe mr2 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f2, 1, 2, f, maxValue, " ", new LineFilter());
        Messreihe mr3 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f0, 1, 2, f, maxValue, " ", new LineFilter());
        Messreihe mr4 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f3, 1, 2, f, maxValue, " ", new LineFilter() );
        Messreihe mr5 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f4, 1, 2, f, maxValue, " ", new LineFilter());
        Messreihe mr6 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f5, 1, 2, f, maxValue, " ", new LineFilter() );

        Messreihe mr6L = mr6.calcLogLog();
        Messreihe mr5L = mr5.calcLogLog();
        Messreihe mr4L = mr4.calcLogLog();
        Messreihe mr3L = mr3.calcLogLog();
        Messreihe mr2L = mr2.calcLogLog();
        Messreihe mr1L = mr1.calcLogLog();
                
        mr1L.setFileName( "mr1L" );
        mr2L.setFileName( "mr2L" );
        mr3L.setFileName( "mr3L" );
        mr4L.setFileName( "mr4L" );
        mr5L.setFileName( "mr5L" );
        mr6L.setFileName( "mr6L" );
        
        rows.add( mr1L );
        rows.add( mr2L );
        rows.add( mr3L );
        rows.add( mr4L );
        rows.add( mr5L );
        rows.add( mr6L );
        
        rows2.add( mr1 );
        rows2.add( mr2 );
        rows2.add( mr3 );
        rows2.add( mr4 );
        rows2.add( mr5 );
        rows2.add( mr6 );
        
        
        OriginProject pro = new OriginProject();
        pro.folderName = "P:/DATA/DegreeDistributions/LogBinningToolv2/";
        pro.addMessreihen(rows, "log_log_binned", true );
        pro.addMessreihen(rows2, "normal_log_binned", true );
        
        pro.closeAllWriter();
        
        MultiChart.open( rows , "degree distribution (Master Join) rows", "log(# of links per page)", "log(# of pages)", true);
        MultiChart.open( rows2 , "degree distribution (Master Join) rows2", "log(# of links per page)", "log(# of pages)", true);
        
    }
        
}
 