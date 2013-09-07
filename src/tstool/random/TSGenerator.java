/*
 * This Generator creates Time series for tests of our packages.
 * 
 *   - Sin / Cos wave ...
 *      
 * 
 */
package tstool.random;

import data.series.Messreihe;
import java.util.Vector;
import chart.simple.MultiChart;
import data.series.MessreiheFFT;

/**
 *
 * @author kamir
 */
public class TSGenerator {
    
    static StringBuffer log = null;
    
    static Vector<Messreihe> testsA = null;
    static Vector<Messreihe> testsB = null;
    
    /** 
     * 
     * Test some sinus waves with FFT 
     *
     */
    public static void main( String[] args ) { 
        
        // never forget !!!
        stdlib.StdRandom.initRandomGen(1);
        
        testsA = new Vector<Messreihe>();
        testsB= new Vector<Messreihe>();
        
        log = new StringBuffer();
    
        // some sample frequencies ...
        double[] fs = { 10.0, 20.0, 50.0, 117.0, 1000 }; //, 2000, 5000 };  
        
        /**
         * SamplingRate SR;
         * 
         *         defines the number of samples per unit of time (usually seconds)
         */
        double samplingRate = 10000.0;
        
        for( double f : fs ) {
            
            double totaltime = 16.384; // s
            double ampl = 1.0;
                        
            MessreiheFFT mr = TSGenerator.getSinusWave(f, totaltime, samplingRate, ampl);
            testsA.add(mr);
            
            /**
             * 
             * 
             * WARNING TERMS NOT ACCURATE !!! 
             * 
             *             // http://de.sci.mathematik.narkive.com/gfJHegzO/frequenz-aus-fft-fourier-transform-bestimmen

Zuerst musst du mal den Nullpunkt der FF-transforierten Daten wissen.
Eine FFT produziert aus n Werten wieder n Werte, wobei n/2 Werte für
die Frequenzen von 0 bis zur halben Sampling-Frequenz (SF) stehen. Die
andere Hälfte ist meistens oben dran zu finden (von halber Sampling-
Frequenz bis zur Samplingfrequenz) oder unten dran (von minus halber
Sampling-Frequenz bis 0) oder ist gar nicht dargestellt, weil sowieso
redundant (= gespiegelt).

Dann steht der k-te Wert rechts vom Nullpunkt für die Frequenz k/n mal
Samplingfrequenz. Die Samplingfrequenz (in Hz) ist 1 durch die
Samplingrate. Und die Samplingrate ist die Zeit zwischen zwei
aufeinanderfolgenden Werten im Zeitbereich, oder die Länge des Signals
in Sekunden dividiert durch n. Die Wellenlänge ist dann c durch die
Samplingfrequenz, oder eingesetzt n/k mal Samplingrate mal c.
             **/
            
            Messreihe mrFFT = new Messreihe(); 
            MessreiheFFT mrFFT2 = new MessreiheFFT(); 

            mrFFT2.calcFFT2( mr, mrFFT, samplingRate);
            
            testsB.add( mrFFT );
        }
        boolean showLegend = true;
//        MultiChart.open(testsA, "raw data", 
//                        "t [s]", "y(t)", showLegend, log.toString() );
        MultiChart.open(testsB, "FFT tests", 
                        "f [Hz]", "c", showLegend, log.toString() );
    
    };
    
    /**
     * 
     * totaltime         = Laenge in sekunden
     * samplingRate = anzahl werte pro Sekunde
     * 
     * f            = Frequenz
     * a            = Amplitude
     */
    public static MessreiheFFT getSinusWave(double f, double time, double samplingRate, double a ) {
        MessreiheFFT mr = new MessreiheFFT();
        int steps = (int)(time * samplingRate);
                
        mr.setLabel("N="+ steps + " (SR="+ samplingRate +" Hz, f="+ f +" Hz)");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        
        double dt = 1.0 / samplingRate; // s
        
        for( int j = 0 ; j<steps; j++ ) {
            double t = j * dt;  // sekunden
            mr.addValuePair( t , a * Math.sin( 2.0 * Math.PI * f * t ) );
        }
        return mr;
    }
    
}
