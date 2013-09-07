package tstool.random.experimental;

import statphys.detrending.*;
import chart.simple.MultiChart;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.methods.IDetrendingMethod;
import stdlib.StdDraw;
import stdlib.StdStats;

public class LongTermCorrelationSeries {

    public static void main(String args[]) throws Exception {

        stdlib.StdRandom.initRandomGen(1);
        
        Messreihe mr1 = getRandomRow( 8000, 0.9, true );
        Messreihe mr2 = getRandomRow( 8000, 1.2, false );
        
    }
    
    public static Messreihe getRandomRow( int length, double i, boolean showTest ) throws Exception {

        // Durch 4 TEILBAR !!!
        int N = length;

        /**
         * Vorbedingungen:
         *
         * Eine Zeitreihe mit bereinigten Werten, ohne Lücken, ausser
         * es hat einen fahlichen Sinn, Lücken zu nutzen.
         */
        double[] zr = new double[N];

        MessreiheFFT d4 = (MessreiheFFT) MessreiheFFT.getGaussianDistribution(N);
        
        Vector<Messreihe> vr = new Vector<Messreihe>();
        Vector<Messreihe> v = new Vector<Messreihe>();
        vr.add( d4 );
        
        zr = d4.getData()[1];

        IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
        int order = dfa.getPara().getGradeOfPolynom();
        dfa.getPara().setzSValues( 500 );

        // Anzahl der Werte in der Zeitreihe
        dfa.setNrOfValues(N);

        // die Werte für die Fensterbreiten sind zu wählen ...
        dfa.initIntervalS();
        dfa.showS();

        // nun wird das Array mit den Daten der ZR übergeben
        MessreiheFFT mr4_NEW = (MessreiheFFT)d4;
        MessreiheFFT temp = mr4_NEW.getModifiedFFT_INV( i );
        
        dfa.setZR(temp.getData()[1]);
        
        dfa.calc();
        
        Messreihe mr4 = dfa.getResultsMRLogLog();
        mr4.setLabel( d4.getLabel() + " (" + i + ")" );
        v.add(mr4);

        String status = dfa.getStatus();
        
        SimpleRegression alpha = mr4.linFit(1.2, 3.5);

        System.out.println( alpha );
        
        System.out.println( " alpha=" + alpha.getSlope() );
        System.out.println( "     i=" + i );

        if ( showTest ) MultiChart.open(v, "fluctuation function F(s) [order:" + order + "] ", "log(s)", "log(F(s))", false, "?");
        
        return temp;
    }
    

}
