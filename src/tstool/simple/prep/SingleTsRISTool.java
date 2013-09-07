package tstool.simple.prep;

/*
 *  wird auf eine einzelne Reihe angewendet ...
 * 
 *  schreibt in eine Datei ...
 * 
 */
import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.SingleRowTSO;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.SingleDFATool;
import statphys.ris.experimental.ReturnIntervallStatistik2;
import statphys.ris.experimental.agh.RISAnalyse3;

/**
 *
 * @author kamir
 */
public class SingleTsRISTool extends SingleRowTSO {
    
    int debugViews = 0;
    int maxDebugViews = 0;

    public static void init() { 
         
//         ReturnIntervallStatistik2.initRqMIN();
         ReturnIntervallStatistik2.initRqCollectors();
         
         risCollector = new ReturnIntervallStatistik2("RISdata");
         risCollector.isContainerInstanz = true;
         
         av11 = new Vector<Messreihe>(); 
         av21 = new Vector<Messreihe>(); 
         av31 = new Vector<Messreihe>(); 

         av12 = new Vector<Messreihe>(); 
         av22 = new Vector<Messreihe>(); 
         av32 = new Vector<Messreihe>(); 
    }
    
    public static void finish() { 
        try {
            risCollector.showData();
        } 
        catch (Exception ex) {
            Logger.getLogger(SingleTsRISTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static public Vector<Messreihe> av11 = new Vector<Messreihe>(); 
    static public Vector<Messreihe> av21 = new Vector<Messreihe>(); 
    static public Vector<Messreihe> av31 = new Vector<Messreihe>(); 

    static public Vector<Messreihe> av12 = new Vector<Messreihe>(); 
    static public Vector<Messreihe> av22 = new Vector<Messreihe>(); 
    static public Vector<Messreihe> av32 = new Vector<Messreihe>(); 

    public SingleTsRISTool(String[] args) {
    }

    public SingleTsRISTool() {   }

    static boolean sLN = true;
    static boolean sLOG = true;
    static boolean scaleRQ = true;
    
    private double linFit_MIN = 1.0;
    private double linFit_MAX = 3.0;
    
    public static ReturnIntervallStatistik2 risCollector = null;

    /**
     * OUTPUT is not collected, it is written directly to the FW
     */
    @Override
    public Messreihe processReihe(FileWriter fw, Messreihe r, FileWriter explodeWriter) throws Exception {

        String line = "";

        ReturnIntervallStatistik2 rr1;
        rr1 = new ReturnIntervallStatistik2("R1");
               
        // calc averag inter-event time
        double vRQ1 = calcAverageOfDist(r.yValues );
        System.out.println( vRQ1 );
        
        rr1.setRQ(vRQ1);
        
        // create interevent time series ... on the way ...
        // set data
        rr1._addData( r.yValues, vRQ1 );        
//        rr1.calcMessreihen();
        
        if ( debugViews < maxDebugViews ) {
            rr1.showData();
            debugViews++;
                    
        };    
             
        risCollector.add( rr1 );



//        if (scaleRQ) {
//            linFitMIN = linFit_MIN * vRQ;
//            linFitMAX = linFit_MAX * vRQ;
//            //System.out.println( "( " + linFit_MIN + ", " + linFit_MAX + ") ");
//
//        } else {
//            linFitMIN = linFit_MIN;
//            linFitMAX = linFit_MAX;
//            //System.out.println( "( " + linFitMIN + ", " + linFitMAX + ") ");
//        };
 

        line = line + "\t" + vRQ1 + "\t"; //  + vRQ2 + "\t" + vRQ3;



        /**
         * Problem: in den einzelnen Reihen kann man nicht sinnvoll den Anstieg
         * bestimmen !!!
         *
         */
//        Messreihe toFit = rr.mrHaeufigkeit.shrinkX(linFit_MIN, linFit_MAX);
//
//        SimpleRegression reg;
//        try {
//            reg = toFit.linFit(linFit_MIN, linFit_MAX);
//
//            double slope = reg.getSlope();
//            double RR = reg.getRSquare();
//
//            System.out.println("m=" + slope + " \tRR=" + RR);
//
//
//
////            tTEST_DATA[i][j][1] = slope;
////            tTEST_DATA[i][j][2] = RR;
////            all.add(toFit);
//
//            ris.add(rr);
//        } catch (Exception ex) {
//            Logger.getLogger(RISAnalyse3.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            fw.write(r.getLabel() + "\t" + line + "\n");
            fw.flush();
        } 
        catch (IOException ex) {
            Logger.getLogger(SingleTsRISTool.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;

    }

    /**
     *
     * @param reihe
     * @return
     * @throws Exception
     */
    public String processReihe(Messreihe reihe) throws Exception {
        return "... (" + this.getClass().getName() + ") ";
    }

    private double calcAverageOfDist(Vector<Double> rr1) {
        double sum = 0.0;
        double c = 0.0;

        double last = 0;
        double delta = 0;
        
        for( double l : rr1 ) { 
            delta = l - last;
            sum = sum + delta;
            last = l;
            c++;
        }
        return sum / (1.0*c);
    }
    
    private double calcAverage(Vector<Double> rr1) {
        double sum = 0.0;
        double c = 0.0;
       
        for( double l : rr1 ) { 
            sum = sum + l;
            c++;
        }
        return sum / (1.0*c);
    }
}
