/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hadoopts.data;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class TSBucketFileFilter implements FileFilter {

    public TSBucketFileFilter() {}

    public boolean accept(File file) {
        boolean step1 = false;
        
        if ( filter != null ) { 
            if ( file.getName().contains(filter) ) step1 = true;
        }
        
        if ( step1 && file.getAbsolutePath().endsWith( ".tsb.vec.seq" ) )
            return true;
        else 
            return false;
    }

    String filter = null;
    public void setFilter(String f) {
        filter = f;
    }
    
}
