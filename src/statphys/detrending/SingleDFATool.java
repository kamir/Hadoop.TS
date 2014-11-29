package statphys.detrending;

import chart.simple.MultiBarChart;
import chart.simple.MultiChart;
import data.series.Messreihe;
import java.util.Vector;
import java.io.File;
import statphys.detrending.methods.DFA;
import statphys.detrending.methods.DFAmulti;
import statphys.detrending.methods.IDetrendingMethod;

/**
 * Hier wird nun die Begrenzung der Segementlänge auf N/4 aufgehoben ...
 *
 * Das SingleDFA-Tool berechnet für eine Zeitreihe die DFA einzeln.
 *
 * @author kamir
 */
public class SingleDFATool {

    public static boolean logLogResults = false;
    public static boolean debug;

    public SingleDFATool() {
        resetPuffer();
    };

    static double[] sum_xs_xs_durch_s = null;
    static double[] anzSegmente = null;

    static double[] sum_xs_xs_durch_s_ALL = null;
    static double[] anzSegmente_ALL = null;

    static boolean doClear = true;

    Messreihe finalFS = null;

    static void resetPuffer() {

        sum_xs_xs_durch_s = null;
        anzSegmente = null;

        sum_xs_xs_durch_s_ALL = null;

        anzSegmente_ALL = null;

        sum_xs_xs_durch_s_ALL = null;
        anzSegmente_ALL = null;
        
    }

    static void _initPuffer(int zahlS) {

        sum_xs_xs_durch_s = new double[zahlS];
        anzSegmente = new double[zahlS];

        for( int i = 0; i < zahlS; i++ ) {
            anzSegmente[i] = 0;
            sum_xs_xs_durch_s[i] = 0;
        }
        
        if ( sum_xs_xs_durch_s_ALL == null )sum_xs_xs_durch_s_ALL = new double[zahlS];

        if( anzSegmente_ALL == null ) anzSegmente_ALL = new double[zahlS];

        if ( doClear ) {
            sum_xs_xs_durch_s_ALL = new double[zahlS];
            anzSegmente_ALL = new double[zahlS];
            doClear = false;
        }
        
        

    }

    int[] S = null;

    
    String value = "_";   // Welche Messgröße wird betrachtet?

    // Für jede Ordnung / PHASE wird hier eine Kurve erstellt.
    static public Vector<Messreihe> kurven = new Vector<Messreihe>();

    public static SingleDFATool tool = new SingleDFATool();
            
    public static void calcMultiDFA(Vector<Messreihe> vmr , int phase, String v) throws Exception {
//
//        System.out.println( vmr.size() + " Reihen in Phase: " + phase );
//        MultiChart.open(vmr, "Messwerte - [Phase " + phase + "] (all rows)", "t", "y(s)", false, "?");

        tool = new SingleDFATool();

        kurven = new Vector<Messreihe>(); // für jede Ordnung eine ...

        doClear = true;
        
        tool.value = v;
        
        // nun werden die Berechnungen für verschiedene Ordnungen durchgeführt
        int[] orders = {2};
        
        for (int i : orders) {
            if (vmr.size() > 0) {

                tool.runDFA(vmr, i);
                kurven.add( tool.Fs.elementAt(0) );
                
                // System.out.println( tool.getMWKurve() );

            }
            else {
                System.out.println("Der Vector<Messreie> vmr ist leer");
            }
            tool.Fs.clear();
        }
    }

    public static void main(String[] main) {
        
        stdlib.StdRandom.initRandomGen(1);

        SingleDFATool tool = new SingleDFATool();
        tool.logLogResults = true;
        tool.showCharts = true;

        
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        vmr.add( Messreihe.getGaussianDistribution( 4000 ) );
        vmr.add( Messreihe.getGaussianDistribution( 4000 ) );

        // nun werden die Berechnungen für verschiedene Ordnungen durchgeführt
        int[] orders = { 2 };
        for (int i : orders) {
            try {
                tool.runDFA(vmr, i);       
            }
            catch (Exception ex) {
               ex.printStackTrace();
            }
        }
    }


    DFAmulti dfa;

    // alle Fluktuationsfunktionen...
    public Vector<Messreihe> Fs = new Vector<Messreihe>();
    public Vector<Messreihe> fsFMW = new Vector<Messreihe>();

    // Für alle s wird eine Mittler Fluctuationsfunktion bestimmt
    Vector<Messreihe> Fs_MW = new Vector<Messreihe>();

    // Die Fensterbreiten
    Vector<Integer> s = new Vector<Integer>();

    public static boolean showCharts = false;
    public static boolean storeCharts = false;

    Vector<Messreihe> fsF2 = null;

    // Für eine Klasse von Messreiehn wird die DFA einer Ordnung berechnet.
    public Vector<Messreihe> runDFA(Vector<Messreihe> vmr, int order) throws Exception  {

        // s bestimmen ... anhand der längsten Reihe
        int zahlW = 0;
        int zahlT = 0;
        int zahlS = 0;

        Messreihe laengsteReihe = vmr.elementAt(0);
        
//        int max = 10;
        int max = vmr.size();
        System.out.println( "zahlW=" + zahlW + " max=" + max + " " + laengsteReihe.getLabel() );
        
        for (int z = 0; z < max ; z++) {
            Messreihe mr = vmr.elementAt(z);
            System.out.println( z+ " size x=" + mr.xValues.size() );
            
            if (mr.xValues.size() > zahlW) {
                        zahlW = mr.xValues.size();
                        laengsteReihe = mr;
                        
            }
            
        }
        
        
        System.out.println(""
                + "> Ermittle s für längste Reihe mit l=" + zahlW
                + " Werten. \n> Label der längsten Reihe: " + laengsteReihe.getLabel() );

        // ohne Begrenzung auf N/4 !!!

      
        
        dfa = (DFAmulti)DetrendingMethodFactory2.getDetrendingMethod(order);

        dfa.setZR( laengsteReihe.getData()[1] );

        // Anzahl der Werte in der Zeitreihe
        dfa.setNrOfValues((int)(laengsteReihe.getXValues().size() / 4));

        // die Werte für die Fensterbreiten sind zu wählen ...

        int s_max = (int)(zahlW);
        int s_min = Math.abs( order + 2 );

//        dfa.initIntervalS_FULL_LOG( s_min , s_max );
        dfa.initIntervalS_FULL2( s_min , s_max );
        
        S=dfa.getS();
        dfa.showS();
        
        zahlS = dfa.getS().length;
        
        System.out.println( " <<< " + zahlS + "=s >>> werden betrachtet.");

        _initPuffer(zahlS);

        String savePath = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/";
        

        double fit_min[] = { 0.8, 1.5, 2.2 };
        double fit_max[] = { 1.3, 2.1, 2.8 };
                
//        double alpha_cor_min = 0.0;  // zu klären was hier gemeint ist !!!

        System.out.println(
                "> Berechne DFA für " + vmr.size() +
                " Messreihen mit order=" + order + ".");

        StringBuffer info = new StringBuffer("> Infos zur DFA-Rechnung");
        info.append("\nnr.\t| file\t| save path");


        for (int z = 0; z < vmr.size(); z++) {

            System.out.println(  (z*1.0) / vmr.size() );
            
            double[][] dfa_results = null;

            Messreihe mr = vmr.elementAt(z);
            if (mr != null ) {

                // set number of values
                double[] zr = mr.getData()[1];
                dfa.setZR(zr);
                dfa.setNrOfValues(zr.length);

                dfa.debug = false;

                dfa.calc();

                // System.out.println( "> useLogLogResulst: " + logLogResults );
                if ( logLogResults ) {
                   Fs.add( dfa.getResultsMRLogLog() );
                }
                else {
                   Fs.add( dfa.getResultsMR() );
                }

                System.out.println( "Reihe=" + z + " d < " + dfa.sMax );
                System.out.println( (dfa.sMax) + " => maximaler Index für größtes s der längsten Reihe.");

                
                // Für alle s nun die Beiträge für die MW-Funktion sammeln
//                for( int d = 0; d < dfa.sMax ; d++ ) {
//                    sum_xs_xs_durch_s[d] = dfa.FSMW[0][d];
//                    anzSegmente[d] = dfa.FSMW[1][d];
//                   
//                    sum_xs_xs_durch_s_ALL[d] = sum_xs_xs_durch_s_ALL[d] + dfa.FSMW[0][d];
//                    anzSegmente_ALL[d] = anzSegmente_ALL[d]+ dfa.FSMW[1][d];
//                }
                
            }
            else {
                System.out.println( "MR = null !!! ");
            }
        }
        
        return Fs;
        
    }

    public String getAlphas() throws Exception {
        String s = "";
        double fit_min[] = { 0.8, 1.5, 2.2 };
        double fit_max[] = { 1.3, 2.1, 2.8 };
        double alpha1 = Fs.elementAt(0).linFit(fit_min[0], fit_max[0]).getSlope();
        double alpha2 = Fs.elementAt(0).linFit(fit_min[1], fit_max[1]).getSlope();
        double alpha3 = Fs.elementAt(0).linFit(fit_min[2], fit_max[2]).getSlope();
        s = alpha1 + "\t" + alpha2 + "\t" + alpha3;
        return s;
    }
}
