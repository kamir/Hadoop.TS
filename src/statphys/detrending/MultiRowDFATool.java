/*
 *
 *   Das Tool berechnet für jeden
 *
 *      MultiRowContainer
 *
 *   eine FluctuationFunktion, zu der dann
 *   im std.-Fitbereich das alpha berechnet
 *   werden kann.
 *
 *
 *
 */

package statphys.detrending;

import chart.simple.MultiChart;
import data.series.Messreihe;
import java.util.Vector;

/**
 *
 * @author root
 */
public class MultiRowDFATool {

    static Fitbereich defaultFit = new Fitbereich( 1.25, 2.0 );

    public static Vector<Messreihe> convertToMessreihe(Vector<FluctuationFunktion> vFs ) {
        Vector<Messreihe> mr = new Vector<Messreihe>();
        for( FluctuationFunktion m : vFs ) {

            mr.add( m.Fs );
            System.out.println( m.getLabel() +  "   alpha = " + m.calcAlpha(defaultFit) );

        };
        return mr;
    }

    MultiRowContainer cont = null;

    public static Vector<FluctuationFunktion> _calcMultiRowsDFA( MultiRowContainer cont, int order ) {

        Vector<FluctuationFunktion> v = new Vector<FluctuationFunktion>();


        for( String l : cont.labels ) {

            // mit Begrenzung auf N/4
//            MultiDFATool tool = new MultiDFATool();

            // ohne Begrenzung auf N/4
            MultiDFATool2 tool = new MultiDFATool2();
            System.out.println( "Container-Label: [" + l +"]" );

            Vector<Messreihe> r = cont.reihenHash.get(l);
            System.out.println( "Label des 1. Elements: [" + r.elementAt(0).getLabel() + "] size=" + r.size() );
            tool.runDFA( r, 2 );

            FluctuationFunktion fs = new FluctuationFunktion( tool.finalFS, l );

            v.add( fs );
        }

        return v;
    }

    public static void main( String[] args ) {

        stdlib.StdRandom.initRandomGen(1);        

        Vector<Messreihe> mr1 = new Vector<Messreihe>();
        for ( int a = 0; a < 10; a++ ) {
            Messreihe m = TestDataFactory.getDataSeriesRandomValues_RW(500);
            mr1.add(m);
        }

        Vector<Messreihe> mr2 = new Vector<Messreihe>();
        for ( int a = 0; a < 10; a++ ) {
            Messreihe m = TestDataFactory.getDataSeriesRandomValues2(500);
            mr2.add(m);
        }

        Vector<Messreihe> mr3 = new Vector<Messreihe>();
        for ( int a = 0; a < 10; a++ ) {
            Messreihe m = TestDataFactory.getDataSeriesRandomValues3(500);
            mr3.add(m);
        }


        Vector<Messreihe> mr4 = new Vector<Messreihe>();
        for ( int a = 0; a < 10; a++ ) {
            Messreihe m = TestDataFactory.getDataSeriesRandomValues_JAVA_CORE(500);
            mr4.add(m);
        }

        MultiRowContainer rmc = new MultiRowContainer();
        rmc.addNewRows(mr1, "Sorte 1");
        rmc.addNewRows(mr2, "Sorte 2");
        rmc.addNewRows(mr3, "Sorte 3");
        rmc.addNewRows(mr4, "Sorte 4");


        // Vector<FluctuationFunktion> vFs = MultiRowDFATool.calcMultiRowsDFA(rmc, 2);
        
        // MultiChart.open( MultiRowDFATool.convertToMessreihe( vFs ) , true, "F(s)");

    };

}
