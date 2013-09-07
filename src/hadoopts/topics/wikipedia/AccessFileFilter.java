/**
 * A filter for wikipedia-access-time-series files.
 * 
 */
package hadoopts.topics.wikipedia;

import java.io.File;
import java.io.FileFilter;

public class AccessFileFilter implements FileFilter {
    
    public static boolean debug = false;

    public boolean accept(File file) {
        if (debug ) 
            System.out.println(file.getName() + " " + file.getName().endsWith("nrv.h.dat") );
        if( file.getName().endsWith("nrv.h.dat") ) return true;
        else return false;
    }

}