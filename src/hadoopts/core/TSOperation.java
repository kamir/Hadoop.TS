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
public abstract class TSOperation {
    
    abstract public String processReihe( Messreihe reihe ) throws Exception;
    abstract public Messreihe processReihe( FileWriter fw, Messreihe reihe, FileWriter exploder ) throws Exception;
    abstract public Messreihe processReihe( FileWriter fw, Messreihe reihe, Object para, FileWriter exploder ) throws Exception;
    
}
