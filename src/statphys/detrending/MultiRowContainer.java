/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package statphys.detrending;

import data.series.Messreihe;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author root
 */
public class MultiRowContainer {

    Vector<String> labels = new Vector<String>();

    Hashtable<String, Vector<Messreihe>> reihenHash = new Hashtable<String, Vector<Messreihe>>();

    public void addNewRow( Messreihe mr, String label ) {
        if ( !labels.contains(label) ) labels.add(label);
        Vector<Messreihe> reihen = reihenHash.get(label);
        if ( reihen == null ) { 
            reihen = new Vector<Messreihe>();
            reihenHash.put(label, reihen);
        }
        reihen.add(mr);
        
    };

    public void addNewRows( Vector<Messreihe> v, String label ) {
       for( Messreihe mr : v ) {
          addNewRow(mr, label);
       }
    };

}
