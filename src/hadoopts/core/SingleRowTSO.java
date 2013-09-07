/*
 *  Operates on a singel time serie ...
 * 
 *  and writes data into a single file.
 * 
 */
package hadoopts.core;

import data.series.Messreihe;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class SingleRowTSO extends TSOperation {
    
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter exploder ) throws Exception {
        
        try {
            fw.write( reihe.getLabel() + "\t" + reihe.summeY() + "\n" );
            fw.flush();
        } 
        catch (IOException ex) {
            Logger.getLogger(SingleRowTSO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reihe;
        
    }
   
    public Messreihe processReihe( FileWriter fw, Messreihe reihe, Object para, FileWriter exploder ) throws Exception {
        
        try {
            fw.write( reihe.getLabel() + " " + reihe.summeY() + "\n" );
            fw.flush();
        } 
        catch (IOException ex) {
            Logger.getLogger(SingleRowTSO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reihe;
        
    }

    @Override
    public String processReihe(Messreihe reihe) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
