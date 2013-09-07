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
public class SingleTsTrendCalculatorTool extends SingleRowTSO {
    
    static Messreihe[] sum = null;
    
    public void init() { 
        sum = new Messreihe[3];
        sum[0] = new Messreihe();
        sum[1] = new Messreihe();
        sum[2] = new Messreihe();
        for( int i=0; i < 300; i++ ) { 
            sum[0].addValuePair(i, 0);
            sum[1].addValuePair(i, 0);
            sum[2].addValuePair(i, 0);
        }
    }
   
    public static int getKlasse( int z ) { 
        if ( z < 1000 ) return 0;
        else if ( z < 10000 ) return 1;
        else return 2;
    }
    
    @Override
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter explodeWriter ) throws Exception {

        Messreihe binned = reihe.setBinningX_sum(24);
        
        int z = (int)binned.summeY();
        int k = getKlasse( z );
//        System.out.println( k );
        
        Messreihe summe = sum[ k ];
        sum[k] = sum[k].add( binned );
                
        return null;
        
    }
    
}
