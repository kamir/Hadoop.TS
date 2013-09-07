/*
 *  Es wird eine Liste mit Messreihen übergeben, die alle die gleiche Zeitachse
 *  haben
 *
 *      Anfang, Schrittweite, und Länge sind als identisch !!!
 *
 * Der Parameter "fensterBreite" legt fest, über wieviel Punkte der Zeitreihe
 * zu mitteln ist.
 *
 * Der Parameter "offset" gibt an wie weit ein Fenster verschoben wird.
 *
 * Für die Bestimmung des Wochengangs wird dann über alle übergebenen Reihen,
 * über das jeweils an der gelcihen Stelle (bzgl. der X-Achse liegende Fenster)
 * der Mittelwert errechnet.
 *
 * Es entsteht eine neue Messreihe, in der
 *      x = Nr des Fensters
 *      y = MITTELWERT des Wochengangs.
 *
 * Die Werte der originalen Messreihen weden dann mit dem jeweiligen Wochen-
 * mittelwert dividiert. Man erhält die um den "Wochentrend" bereinigte
 * Zeitrehie.
 *
 */

package data.io;

import data.series.Messreihe;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class WochengangFilter {

    public static Messreihe getWochenGang( Vector<Messreihe> liste, int fensterBreite, int vorschub ) {
        Messreihe mr = new Messreihe();
        mr.setLabel("Wochengang");

        // Anzahl von X-Werten
        int zahlX = liste.elementAt(0).getXValues().size();

        int rest = zahlX;
        int j = 0;

        // so lange wie noch ein volles Fenster berechnet werden kann ...
        while( rest > fensterBreite ) {
            System.out.println( "\t" + j );
            int counter = 0;
            double summe = 0;
            
            int pos = 0;
            for( int p = 0 ; p < fensterBreite ; p++ ) {

                counter++;

                pos = p + j*vorschub;

                System.out.println( "\t\t"+pos );

                Enumeration<Messreihe> en = liste.elements();
                while( en.hasMoreElements() ) {
                    Messreihe r = en.nextElement();
                    summe = summe + (Double)r.getYValues().elementAt( pos );
                };

            }
            double mw = summe / counter;            

            mr.addValuePair( j , mw );

            j++;
            rest = zahlX - ( vorschub * j );
        }

        return mr;
    };



    public static Vector<Messreihe> entferneWochengang( Vector<Messreihe> liste, Messreihe wg ) {
        Vector<Messreihe> v = new Vector<Messreihe>();



        return v;
    };

    public static void main( String[] args ) {
        
        stdlib.StdRandom.initRandomGen(8);

        Messreihe a = Messreihe.getGaussianDistribution( 100 );
        Messreihe b = Messreihe.getGaussianDistribution( 100 );
        Messreihe c = Messreihe.getGaussianDistribution( 100 );

        Vector<Messreihe> liste = new Vector<Messreihe>();
        liste.add(a);
        liste.add(b);
        liste.add(c);

        System.out.println( WochengangFilter.getWochenGang(liste, 10, 5) );

    };

}
