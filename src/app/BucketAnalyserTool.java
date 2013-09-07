/**
 *    BucketTool loads a full TSBucket into memory to
 *    work on this in a closed processing procedure.
 * 
 *    To test an algorithm localy, before it operates on a cluster.
 * 
 **/
package app;

import data.series.MRT;
import data.series.Messreihe; 
import hadoopts.core.SingleRowTSO;
import hadoopts.core.TSBucket;
import hadoopts.data.TSBucketFileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import app.experimental.SimpleFilteredBucketTool;
import static app.experimental.SimpleFilteredBucketTool.procm_RECORD_STREAM;
import statphys.ris.experimental.ReturnIntervallStatistik2;
import tstool.simple.SingleTsDFATool;
import tstool.simple.prep.SingleTsRISTool;

/**
 *
 * @author kamir
 */
public class BucketAnalyserTool extends SimpleFilteredBucketTool {
    
    /**
     * containers are accessed directly ...
     */
//    public static Vector<Messreihe> cont_PEAKS = new Vector<Messreihe>();
//    public static Vector<Messreihe> cont_FS_NORMALIZED = new Vector<Messreihe>();
//    public static Vector<Messreihe> cont_FS_RAW = new Vector<Messreihe>();
    
    
    public static final int mode_PEAKS = 0;
    public static final int mode_DFA = 1;
    public static final int mode_MFDFA = 2;
    public static final int mode_RIS = 3;

    
    public static void main(String[] args) throws Exception {
        
        args = new String[4];
        args[0] = "tstest/sample";
        args[1] = "b2";
        args[2] = "false";
        args[3] = "true";
        
        int mode = mode_RIS;
//        int mode = mode_DFA;
        
        int proc_mode = procm_RECORD_STREAM ;
        
        TSBucket.useHDFS = false;
//        int LIMIT = Integer.MAX_VALUE;
        int LIMIT = 50;
        
        TSBucketFileFilter tsbf = new TSBucketFileFilter();
        String filter = "LRC_beta_0.9";
//        String filter = "alpha_0.5";   
        
        tsbf.setFilter(filter);

        // default settings 
        File folder = new File( args[0] );
                     
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        
        BucketAnalyserTool tool = null;
        
        //
        // What analysis should be done?
        //
        switch( mode ) {
            
            case mode_PEAKS : { 
                
                //  (a) - OK
                TSBucket.FN_EXT = "(PEAK)";
                tool = new BucketAnalyserTool( new tstool.simple.SingleTsPeakFilterTool() );
                
                break;
            }
                
            case mode_DFA : { 

                //  (b) - OK
                TSBucket.FN_EXT = "(DFA)";
                SingleTsDFATool t = new  SingleTsDFATool( args );
                
                tool = new BucketAnalyserTool( t );
               

                break;
            }
                
            case mode_MFDFA : { 
                //        //  (c) - ??? JUST WORKS WITH DFA, not MFDFA
                //        TSBucket.FN_EXT = "(MFDFA)";
                //        tool = new BucketAnalyserTool( new tstool.simple.SingleTsMFDFATool( args ) );
                

                break;
            }
                
            case mode_RIS : { 
                
                //  (d) - ??? RIS
                TSBucket.FN_EXT = "(RIS)";
                SingleTsRISTool t = new SingleTsRISTool( args );

                ReturnIntervallStatistik2.debug = true;
                ReturnIntervallStatistik2.binning = 15;
                ReturnIntervallStatistik2.scale = 3;
                
                t.init();
                
                tool = new BucketAnalyserTool( t );
                break;
                
            }    
        }
  
        tool.procm = proc_mode;        
        tool.bl.setLimit( LIMIT );
        
        System.out.println(">>> input folder : " + folder.getAbsolutePath() + " # exists() => " + folder.exists() );
        System.out.println(">>> mode         : " + procm );

        // get a file-listing of the default input folder 
        // (a folder in the local FS)
        File[] files = folder.listFiles( tsbf );
        tool.workOnBucketFolder( files );

        //
        // What analysis should be done?
        //
        switch( mode ) {
            
            case mode_PEAKS : { 
                
                break;
            }
                
            case mode_DFA : { 

                break;
            }
                
            case mode_MFDFA : { 

                break;
            }
                
            case mode_RIS : { 

                SingleTsRISTool.finish();
                
                break;
            }
        }  
    
        
//        MultiChart.open(cont_PEAKS, true, "Peaks");
//        MultiChart.open(cont_FS_RAW, false, "FS raw");
//        MultiChart.open(cont_FS_NORMALIZED, false, "FS normalized");
        
        
    }
    
    
    private BucketAnalyserTool(SingleRowTSO op) {
        super();
        super.setTSOperation(op);
    }
    
    
    
    
}

