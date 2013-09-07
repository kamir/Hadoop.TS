package hadoopts.analysis;

import data.series.Messreihe;
import hadoopts.core.TSOperation;
import java.io.FileWriter;

/**
 * Just to show an example of a TSTool
 * 
 * Calculate the sum of all y-values
 *  
 * @author kamir
 */
public class SingleTSToolSum extends TSOperation {
    
    public String processReihe( Messreihe reihe ) throws Exception {

        double sum = reihe.summeY(); 
        
        String line = sum+"\n";
        
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
