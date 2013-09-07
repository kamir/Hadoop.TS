package statphys.detrending;

import chart.simple.MultiChart;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.methods.IDetrendingMethod;
import stdlib.StdDraw;
import stdlib.StdStats;

public class DFATester {

    public static void main(String args[]) throws Exception {

        stdlib.StdRandom.initRandomGen(1);

        // Durch 4 TEILBAR !!!
        int N = 150000;

        // Wie wollen wir die Daten übergeben?

        /**
         * Vorbedingungen:
         *
         * Eine Zeitreihe mit bereinigten Werten, ohne Lücken, ausser
         * es hat einen fahlichen Sinn, Lücken zu nutzen.
         */
        double[] zr = new double[N];
        double[] zr2 = new double[N];
        double[] zr3 = new double[N];
        double[] zr4 = new double[N];

        Messreihe d1 = TestDataFactory.getDataSeriesRandomValues_RW(N/100);
        Messreihe d2 = TestDataFactory.getDataSeriesRandomValues2(N/100);
        Messreihe d3 = TestDataFactory.getDataSeriesRandomValues3(N/100);
        MessreiheFFT d4 = (MessreiheFFT) MessreiheFFT.getGaussianDistribution(N);
        
        Vector<Messreihe> vr = new Vector<Messreihe>();
        vr.add( d1 );
        vr.add( d2 );
        vr.add( d3 );
        vr.add( d4 );
        
        // test2( vr );
        
        zr = d1.getData()[1];
        zr2 = d2.getData()[1];
        zr3 = d3.getData()[1];
        zr4 = d4.getData()[1];

        IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
        int order = dfa.getPara().getGradeOfPolynom();
        dfa.getPara().setzSValues( 1500 );
        


        // Anzahl der Werte in der Zeitreihe
        dfa.setNrOfValues(N);

        // die Werte für die Fensterbreiten sind zu wählen ...
        dfa.initIntervalS();
        dfa.showS();



        // nun wird das Array mit den Daten der ZR übergeben
        dfa.setZR(zr);

        // Start der Berechnung
        dfa.calc();


        // Kontrolle
        Vector<Messreihe> k = new Vector<Messreihe>();
        k.add(dfa.getZeitreiheMR());
        k.add(dfa.getProfilMR());
        //k.addAll(dfa.getMRFit());

        // Übergabe der Ergebnisse ...
        double[][] results = dfa.getResults();
        Vector<Messreihe> v = new Vector<Messreihe>();
        Messreihe mr1 = dfa.getResultsMRLogLog();
        mr1.setLabel( d1.getLabel() );
        //v.add(mr1);

        dfa.setZR(zr2);
        dfa.calc();
        Messreihe mr2 = dfa.getResultsMRLogLog();
        
        //v.add(mr2);
        mr2.setLabel( d2.getLabel() );
        k.add(dfa.getZeitreiheMR());
        k.add(dfa.getProfilMR());
        
        dfa.setZR(zr3);
        dfa.calc();
         Messreihe mr3 = dfa.getResultsMRLogLog();
        mr3.setLabel( d3.getLabel() );
        // v.add(mr3);
        k.add(dfa.getZeitreiheMR());
        k.add(dfa.getProfilMR());
         
        
        MessreiheFFT mr4_NEW = (MessreiheFFT)d4;
        MessreiheFFT temp = mr4_NEW.getModifiedFFT_INV( 1.5 );
        
        dfa.setZR(temp.getData()[1]);
        
        dfa.calc();
        Messreihe mr4 = dfa.getResultsMRLogLog();
        mr4.setLabel( d4.getLabel() + " ("+1.5+")" );
        v.add(mr4);
        k.add(dfa.getZeitreiheMR());
        k.add(dfa.getProfilMR());
        String status = dfa.getStatus();

        Vector<Messreihe> vmr2 = new Vector<Messreihe>();

        Messreihe alphaVonI = new Messreihe();
        alphaVonI.setLabel( "alpha( beta )" );
        
        Messreihe theorie = new Messreihe();
        theorie.setLabel( "alpha = (1+beta)/2" );



        
        for( int i = -15; i < 25 ; i = i + 1 ) {
            System.out.println( "(" + i + ")" );
            MessreiheFFT mr5_NEW = (MessreiheFFT)d4;
            MessreiheFFT temp5 = mr5_NEW.getModifiedFFT_INV( 0.05 * i );

            dfa.setZR(temp5.getData()[1]);

            dfa.calc();
            Messreihe mr5 = dfa.getResultsMRLogLog();
            mr5.setLabel( d4.getLabel() + " beta="+(0.05 * i)+"" );
            v.add(mr5);
            
            SimpleRegression alpha = mr5.linFit(1.2, 3.5);
    
            System.out.println( i*0.05 + " " + alpha.getSlope() );

            alphaVonI.addValuePair( 0.05 * i, alpha.getSlope() );
            
            theorie.addValuePair( (alpha.getSlope() * 2.0 ) - 1.0 , alpha.getSlope() );

    //        k.add(dfa.getZeitreiheMR());
    //        k.add(dfa.getProfilMR());
    //        String status = dfa.getStatus();
        }   
        
        System.out.println( alphaVonI );
        vmr2.add( alphaVonI );
        vmr2.add( theorie );
        
        System.out.println( "Anstieg: alpha( beta ) = " + alphaVonI.linFit(0.5, 0.8).getSlope() );
        
        System.out.println("> DFA-Status: " + "\n" + status + "\n#s=" + results[1].length);

        MultiChart.open(v, "fluctuation function F(s) [order:" + order + "] ", "log(s)", "log(F(s))", false, "?");
        MultiChart.open(k, "Kontrolldaten", "t", "y(t)", true, "?");
        MultiChart.open(vmr2, "alpha vs. beta", "beta", "alpha", true, "?");
        
    }
    
    public static void _test2( Vector<Messreihe> mr ) { 
        MultiDFATool dfaTool = new MultiDFATool();
        dfaTool.runDFA(mr, 2);
        System.out.println( "done... ");
    };
}
