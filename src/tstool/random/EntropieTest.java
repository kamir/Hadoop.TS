/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tstool.random;

import chart.simple.MultiChart;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import java.text.DecimalFormat;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.DetrendingMethodFactory;
import statphys.detrending.methods.IDetrendingMethod;
import static tstool.random.FFTPhaseRandomizer.order;

/**
 *
 * @author kamir
 */
public class EntropieTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        // generation of the test data ...

        double PI = Math.PI;

        double[] f = {20, 100, 200, 500, 1000, 0.00001};
        double[] a = {5, 3, 8, 2, 1, 5};
        double[] phase = {0.5, -0.2, -0.20, 0.8, 1.0, 0.0};
        double[] noise = {0.81, 0.82, 0.81, 0.801, 0.825, 10.0};

        double time = 5;
        double samplingRate = 10000;

        Messreihe mr = new Messreihe();
        

        Vector<Messreihe> mrv = new Vector<Messreihe>();
        for (int i = 0; i < f.length; i++) {
            Messreihe m = TSGenerator.getSinusWave(f[i], time, samplingRate, a[i], phase[i], noise[i] * PI);
            mr = mr.add(m);
            mrv.add(m.copy());
        }
        
        mr.setLabel("mix");
        mrv.add(mr);

        MultiChart.open(mrv, true);
        
        // DFA ...
        applyDFA(mrv);

        // simple randomisation
        Messreihe temp = mr.copy();
        int nsh = 100;
        temp.shuffleYValues( nsh );
        Messreihe m2 = temp;

        Messreihe m3 = FFTPhaseRandomizer.getPhaseRandomizedRow(mr.copy(), false, false, 0);

        Vector<Messreihe> mrv2 = new Vector<Messreihe>();
        m2.setLabel("shuffled (" + nsh + ")" );
        mrv2.add( mr );
        mrv2.add( m2 );
        mrv2.add( m3 );
        
        
        MultiChart.open(mrv2, true);

        // DFA ...
        applyDFA(mrv2);
        
        
        // teste die Kreuzkorrelation
        
        

    }
    
    
    static double fitMIN = 1.2;
    static double fitMAX = 3.5;

    private static void applyDFA(Vector<Messreihe> mrv) throws Exception {

            
        int nrOfSValues = 250;
        
        Vector<Messreihe> v = new Vector<Messreihe>();
        
        for (Messreihe d4 : mrv) {
            
            int N = d4.yValues.size();
            double[] zr = new double[N];

            Vector<Messreihe> vr = new Vector<Messreihe>();
            
            vr.add(d4);

            zr = d4.getData()[1];

            IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
            order = dfa.getPara().getGradeOfPolynom();
            dfa.getPara().setzSValues(nrOfSValues);

            // Anzahl der Werte in der Zeitreihe
            dfa.setNrOfValues(N);

            // die Werte für die Fensterbreiten sind zu wählen ...
            //dfa.initIntervalS();
            dfa.initIntervalSlog();
           
            dfa.showS();
            


            // http://stackoverflow.com/questions/12049407/build-sample-data-for-apache-commons-fast-fourier-transform-algorithm

            dfa.setZR(d4.getData()[1]);

            dfa.calc();

            Messreihe mr4 = dfa.getResultsMRLogLog();
            mr4.setLabel(d4.getLabel());
            v.add(mr4);

            String status = dfa.getStatus();

//            SimpleRegression alphaSR = mr4.linFit(fitMIN, fitMAX);
//
//            double alpha = alphaSR.getSlope();

  
            System.out.println( status );
        }
        
        if ( true ) {
                DecimalFormat df = new DecimalFormat("0.000");
                MultiChart.open(v, "fluctuation function F(s) [order:" + order + "] ", "log(s)", "log(F(s))", true, "???");
                
//                System.out.println(" alpha = " + df.format(alpha));
//                System.out.println("       = " + ((2 * alpha) - 1.0));

        }
    }
}
