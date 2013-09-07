package tstool.simple;

import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.SingleRowTSO;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import statphys.detrending.DetrendingMethodFactory;
import statphys.detrending.MultiDFATool4;
import statphys.detrending.SingleDFATool;
import statphys.detrending.methods.IDetrendingMethod;
import app.BucketAnalyserTool;

/**
 *
 * @author kamir
 */
public class SingleTsDFATool extends SingleRowTSO {

    Boolean doDFA_on_raw_data = false;
    Boolean doDFA_on_normalized_data = false;
                
    public SingleTsDFATool(String[] args) {
        
        doDFA_on_raw_data = Boolean.parseBoolean( args[2] );  
        doDFA_on_normalized_data = Boolean.parseBoolean( args[3] );  
        
    }
    
    /**
     *   OUTPUT is not collected, it is written directly to the FW 
     */
    @Override
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter explodeWriter ) throws Exception {

        String line = "\t";
   
        // NORMALISIEREN
        Messreihe normalized1 = MRT.normalizeByPeriodeTrend(reihe, 24*7 );
        
        /**
         * 
         * Collect in CONTAINER per FLAG steuern ...
         * 
         */
        
        Vector<Messreihe> vmr_raw = new Vector<Messreihe>();
        Vector<Messreihe> vmr_norm = new Vector<Messreihe>();
        
        //*********
        // TODO
        //
        // DFA orders could be initialized via GLOBAL Parameter ...
        //
        int[] orders = {2};
        
        vmr_raw.add( reihe );
        vmr_norm.add( normalized1 );
        
//        MyBucketTool.cont_PEAKS.add( normalized1 ); 
        
        for ( int ord : orders) {
            try {
                
                if ( doDFA_on_normalized_data ) {
                    SingleDFATool tool1 = new SingleDFATool();
                    tool1.logLogResults = true;

                    Vector<Messreihe> mFS = tool1.runDFA(vmr_norm, ord);       
                    String trends1 = tool1.getAlphas();
//                    MyBucketTool.cont_FS_NORMALIZED.add(mFS.elementAt(0));
                    line = line.concat(  "\t t1 :" + trends1 );

                }
                
                if( doDFA_on_raw_data ) {
                    SingleDFATool tool2 = new SingleDFATool();
                    tool2.logLogResults = true;

                    Vector<Messreihe> mFS2 = tool2.runDFA(vmr_raw, ord);       
                    String trends2 = tool2.getAlphas();
//                    MyBucketTool.cont_FS_RAW.add(mFS2.elementAt(0));
                    
                    line = line.concat(  "\t t2: " + trends2 );
                }    
  
            }
            catch (Exception ex) {
               ex.printStackTrace();
            }
        }

        try {
            fw.write( reihe.getLabel() + " " + reihe.summeY() + line + "\n" );
            fw.flush();
        } 
        catch (IOException ex) {
            Logger.getLogger(SingleTsDFATool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reihe;
        
    }
    
}
