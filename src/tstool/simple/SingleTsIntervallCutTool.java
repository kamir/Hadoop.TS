package tstool.simple;

/**
 *  SingleTsFilterTool 
 *  arbeitet mit einer Zeitreihe und schreibt ggf. in zwei
 *  verschiedene FileWriter.
 * 
 *  
 */


import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.SingleRowTSO;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
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
public class SingleTsIntervallCutTool extends SingleRowTSO {
    
    Vector<Integer> ids;
    Hashtable<Integer, Integer> von;
    Hashtable<Integer, Integer> bis;
    
    @Override
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter explodeWriter ) throws Exception {

        int l = reihe.getLabel().length()-1;
        Integer id = Integer.parseInt( reihe.getLabel().substring(0, l)  );
        
        if ( ids.contains(id) ) {
            
            int von1 = von.get(id);
            int bis1 = bis.get(id);

            Messreihe binned = reihe.setBinningX_sum(24);
            double sumAll = stdlib.StdStats.sum( binned.getYData() );
            
            
            int n = 60;
            Messreihe a = binned.cutOut( von1, von1 + n );
            Messreihe b = binned.cutOut( von1 + n, von1 + n + n );
            
            double sum1 = stdlib.StdStats.sum( a.getYData() );
            double sum2 = stdlib.StdStats.sum( b.getYData() );
            double std1 = stdlib.StdStats.stddev( a.getYData() );
            double std2 = stdlib.StdStats.stddev( b.getYData() );
            
            
            String line = sumAll + "\t";
            line = line.concat( von1 + "\t" + bis1 + "\t"  + sum1 + "\t" + std1 + "\t"  + sum2 + "\t" + std2 );

            try {
                fw.write( "" + id + "\t" + line + "\n" );
                fw.flush();
            } 
            catch (IOException ex) {
                Logger.getLogger(SingleTsIntervallCutTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return reihe;
        
    }
    
}
