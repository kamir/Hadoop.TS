/*
 *   Die y-Spalten zweier Reihen werden zu einer neuen xy Reihe vereint.
 *
 */

package data.series;

import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class CombinedMessreihe {

    public static Messreihe combine2Rows( Messreihe mr1 , Messreihe mr2 ) {
        Messreihe r = new Messreihe();
        r.setLabel( mr1.getLabel() +" & " + mr2.getLabel() );
        r.setLabel_X( mr1.getLabel_Y() );
        r.setLabel_Y( mr1.getLabel_Y() );

        Enumeration<Double> en = mr1.yValues.elements();
        int i = 0;
        while( en.hasMoreElements() ) {
            double xn = en.nextElement();
            double yn = (Double)mr2.yValues.elementAt(i);
            r.addValuePair(xn, yn);
            i = i+1;
        };
        return r;
    };

    public static Vector<Messreihe> combine2RowVectors(Vector<Messreihe> rFLUX, Vector<Messreihe> rDENSITY) {
        Vector<Messreihe> r = new Vector<Messreihe>();
        for( int i = 0; i < rFLUX.size() ; i++ ) {
            Messreihe rn = combine2Rows(rFLUX.elementAt(i).setBinningX_average( 15 ),rDENSITY.elementAt(i).setBinningX_average(15));
            
            r.add( rn );
        };
        return r;
    }
}
