package tstool.simple.prep;

/*
 *  wird auf eine einzelne Reihe angewendet ...
 * 
 *  schreibt in eine Datei ...
 * 
 */


import tstool.simple.prep.SingleTsRISTool;
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
public class SingleTsMFDFATool extends SingleRowTSO {
                
    public SingleTsMFDFATool(String[] args) {
        
    }
    
       /**
     *   OUTPUT is not collected, it is written directly to the FW 
     */
    @Override
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter explodeWriter ) throws Exception {

        String line = "\t";
        
        try {
            fw.write( reihe.getLabel() + "\t" + line + "\n" );
            fw.flush();
        } 
        catch (IOException ex) {
            Logger.getLogger(SingleTsRISTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reihe;
        
    }
    
    /**
     * 
     * @param reihe
     * @return
     * @throws Exception 
     */
    public String processReihe(Messreihe reihe) throws Exception {
        return "... (" + this.getClass().getName() + ") ";
    }
    
}
