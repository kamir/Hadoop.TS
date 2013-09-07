package hadoopts.analysis;

import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.TSOperation;
import java.io.FileWriter;
import java.util.Vector;

/**
 * Just to show another example of a TSTool
 *  
 * TODO: Create a better JSON representation ...
 * 
 * @author kamir
 */
public class SingleTSToolPeakDetector extends TSOperation {
    
    public String processReihe( Messreihe reihe ) throws Exception {

        double sum = reihe.summeY(); 
        
        String line = "";
        
        // Detrending
        Messreihe normalized1 = MRT.normalizeByPeriodeTrend(reihe, 24*7);
        
        Messreihe peaks = normalized1.setBinningX_sum(24);
        peaks.add_to_Y( -24.0 );
           
        int[] tss = { 2, 4, 6, 8, 10 };
        line = line.concat( "{Peaks:\t"  );
        for( int ts : tss ) {
            Vector<Integer> p = MRT.getPeaksDaysOverTS( ts , peaks );
            int nr = p.size();
            line = line.concat( ts +":"+nr + "{" );
            for( int i : p ) {
                line = line.concat( i + "," );
            }
            line = line.concat("}") ;
        }
        line = line.concat( "}"  );
        
        return line;
        
    }

    @Override
    public Messreihe processReihe(FileWriter fw, Messreihe reihe, FileWriter exploder) throws Exception {
        return null;
    }

    @Override
    public Messreihe processReihe(FileWriter fw, Messreihe reihe, Object para, FileWriter exploder) throws Exception {
        return null;
    }
    
}
