package statphys.detrending;

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
 * Das MultiDFA-Tool berechnet für eine Menge von Zeitreihen, die z.B. zu
 * einer bestimmten Klasse gehöen die DFA einzeln.
 *
 * Ausserdem wird für jede Fensterbreite s die Summe von (x-schlange)^2
 * und die Anzahl der betrachteten Segmente ermittelt. {FORMEL:II.A.20}
 *
 * Am Ende wird für alle s aus allen Beiträgen der unterschielich langen
 * Zeitreihen ein <F(s)> bestimmt.
 *
 * @author kamir
 */
public class MultiDFATool2 {

    public static boolean logLogResults = false;
    public static boolean debug;

    public MultiDFATool2() {
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

    static void initPuffer(int zahlS) {

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
        
        mw = null;

    }

    int[] S = null;

    // Für den Graphen TITEL
    int phase = 0;
    String value = "_";   // Welche Messgröße wird betrachtet?

    // Für jede Ordnung / PHASE wird hier eine Kurve erstellt.
    static public Vector<Messreihe> kurven = new Vector<Messreihe>();

    public static MultiDFATool2 tool = new MultiDFATool2();
            
    public static void calcMultiDFA(Vector<Messreihe> vmr , int phase, String v) {
//
//        System.out.println( vmr.size() + " Reihen in Phase: " + phase );
//        MultiChart.open(vmr, "Messwerte - [Phase " + phase + "] (all rows)", "t", "y(s)", false, "?");

        tool = new MultiDFATool2();

        kurven = new Vector<Messreihe>(); // für jede Ordnung eine ...

        doClear = true;
        
        tool.phase = phase;
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

        MultiDFATool2 tool = new MultiDFATool2();
        tool.logLogResults = true;
        tool.showCharts = true;
        tool.storeCharts = true;

        Vector<Messreihe> vmr = getTestReihen( 4 );

        // nun werden die Berechnungen für verschiedene Ordnungen durchgeführt
        int[] orders = { 2 };
        for (int i : orders) {
            try {
                tool.runDFA(vmr, i);                
            }
            catch (Exception ex) {
               ex.printStackTrace();
            }
            tool.Fs.clear();
        }

        MultiChart.open(vmr);

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
    public void runDFA(Vector<Messreihe> vmr, int order)  {

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
        dfa.setNrOfValues(laengsteReihe.getXValues().size());

        // die Werte für die Fensterbreiten sind zu wählen ...

        int s_max = zahlW;
        int s_min = Math.abs( order  + 2 );

        dfa.initIntervalS_FULL( s_min , s_max );

        S=dfa.getS();
        dfa.showS();
        
        zahlS = dfa.getS().length;
        
        if ( debug ) System.out.println(zahlS + " s werden betrachtet.");

        initPuffer(zahlS);

        String savePath = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/";

        double fit_min = 1.0, fit_max = 3.0;  // willkürliche Festlegung
        double alpha_cor_min = 0.0;  // zu klären was hier gemeint ist !!!

        System.out.println(
                "> Berechne DFA für " + vmr.size() +
                " Messreihen mit order=" + order + ".");

        StringBuffer info = new StringBuffer("> Infos zur DFA-Rechnung");
        info.append("\nnr.\t| file\t| save path");

        // je Zeitreihe wird ein alpha bestimmt ....
        double[] alpha = new double[vmr.size()];

        //double[] FS_ = new double[zahlS];

        // für jedes s wird eine Summe ( xs^2 ) bestimmt und die Anzahl der

        // Segmente anzw


//
//        double[] FS_MW_gewichtet = new double[zahlS];
//
        // Schleife über alle Zeitreihen dieser Klasse
//        System.out.println("Anzahl von Reihen: " + vmr.size() );

       
        for (int z = 0; z < vmr.size(); z++) {

            double[][] dfa_results = null;

            Messreihe mr = vmr.elementAt(z);

            if (mr != null ) {

                // set number of values
                double[] zr = mr.getData()[1];
                dfa.setZR(zr);
                dfa.setNrOfValues(zr.length);

                dfa.debug = false;

                dfa.calc();

                if (debug) System.out.println( "> useLogLogResulst: " + logLogResults );
                if ( logLogResults ) {
                   Fs.add( dfa.getResultsMRLogLog() );
                }
                else {
                   Fs.add( dfa.getResultsMR() );
                }

                

//                System.out.println( "Reihe=" + z + " d < " + dfa.sMax );
//                System.out.println( (dfa.sMax) + " => maximaler Index für größtes s der längsten Reihe.");

                
                // Für alle s nun die Beiträge für die MW-Funktion sammeln
                for( int d = 0; d < dfa.sMax ; d++ ) {
                    sum_xs_xs_durch_s[d] = dfa.FSMW[0][d];
                    anzSegmente[d] = dfa.FSMW[1][d];
                   
                    sum_xs_xs_durch_s_ALL[d] = sum_xs_xs_durch_s_ALL[d] + dfa.FSMW[0][d];
                    anzSegmente_ALL[d] = anzSegmente_ALL[d]+ dfa.FSMW[1][d];

                    // System.out.println( "d: " + d + "\t " + sum_xs_xs_durch_s[d] + "  "+sum_xs_xs_durch_s_ALL[d] + "  " + anzSegmente[d] + "  " + anzSegmente_ALL[d]);
                }

                //save result data to file
//                for (int i = 0; i < dfa.FSMW[1].length; i++) {
//                    // Ergebnis der einzelnen Reihe erfassen und Speichern ...
//                    res_mr.addValuePair(Math.log10(dfa_results[0][i]), Math.log10( Math.sqrt( dfa.FSMW[1][i] / dfa.FSMW[2][i] ) )) ;
//                    // Teilergebnis in den Container übergeben.
//                    anzw_[i] = anzw_[i] + dfa_results[1][i];  // Anzahl
//                    sum_xs_xs_[i] = sum_xs_xs_[i] + dfa_results[3][i];
//                }
                // double tmp_alpha2 = res_mr.linFit(fit_min, fit_max).getSlope();

//                String dfaFilesPath = savePath + (z + 1) + "_dfa" + dfa.getPara().getGradeOfPolynom() + ".txt";
//                File dfaFile = new File(dfaFilesPath);
////                res_mr.writeToFile(dfaFile); // Messreihe schreiben
//
//                //save alphas to file
//                info.append("\n-----------------------------------------------------------------------------------");
//                info.append("\n" + (z + 1) + "\t| " + vmr.elementAt(z).getLabel() + "\t| " + dfaFilesPath + "\t| " + tmp_alpha2);
//}
            }
            else {
                System.out.println( "MR = null !!! ");

            }
        
        }


                 
          // MultiChart.open(dfa.getMRFit(), "fit", "s", "F(s)", false, "?");


//        for (int i = 0;
//                i < act_Alpha.length;
//                i++) {
//            alpha_mr.addValuePair(i, act_Alpha[i]);
//        }
//        alpha_mr.setLabel(
//                "alphas");
//        alpha_mr.addComment(
//                "alpha_fit_min: " + dfa.getAlphaFitMin());
//        alpha_mr.addComment(
//                "alpha_fit_max: " + dfa.getAlphaFitMax());
//        alpha_mr.addComment(
//                "DFA-Ordnung:   " + dfa.getPara().getGradeOfPolynom());
//        String alphaFilePath = savePath + "/alphas_dfa" + dfa.getPara().getGradeOfPolynom() + ".txt";
//
//        alpha_mr.addComment("Pfad:          "
//                + alphaFilePath);
//        File alphaFile = new File(alphaFilePath);
//        alpha_mr.writeToFile(alphaFile);
//        info.append(
//                "\n\n" + alphaFilePath);
//
//        alpha_mr.show();



        // und nun sammeln wir die Werte ein und berechnen ein Fs
        finalFS = new Messreihe("<F(s)>");
        
        Messreihe finalZ = new Messreihe("#segments");
        Messreihe finalS = new Messreihe("s");

        for( int d = 0; d < dfa.sMax-1 ; d++ ) {
            if ( anzSegmente_ALL[d] > 4 ) {
                if ( logLogResults ) {
                    finalFS.addValuePair(Math.log10(S[d]), Math.log10(Math.sqrt( sum_xs_xs_durch_s_ALL[d] / anzSegmente_ALL[d])));
                }
                else {
                    finalFS.addValuePair( S[d], Math.sqrt( sum_xs_xs_durch_s_ALL[d] / anzSegmente_ALL[d]));
                }
                finalZ.addValuePair(S[d], anzSegmente_ALL[d] );
            }
        }

        fsFMW = new Vector<Messreihe>();
        fsFMW.add(finalFS);

        fsF2 = new Vector<Messreihe>();
        fsF2.add(finalZ);

        mw = finalFS;
        mw.setLabel("<F(s)> phase=" + phase );

        try{
            double TEST_alpha = finalFS.linFit( 1.25, 2.0 ).getSlope();
            System.out.println( ">>> alpha=" + TEST_alpha );
        }
        catch( Exception ex ) {
            System.err.println( ex.getCause() );
            //ex.printStackTrace();
        };


//      System.out.println(info.toString());

//      SimpleRegression reg = finalFS.linFit(fit_min, fit_max);
//      System.out.println("alpha=" + (reg.getSlope()));

        String[] normal_Lable = {"s", "F(s)" };
        String[] logLog_Lable = {"log(s)" , "log( F(s) )" };

        String[] label = normal_Lable;

        if ( logLogResults ) label = logLog_Lable;
        
        if (showCharts) {
//            MultiChart.open(Fs, "[Phase " + phase + "] F(s) [DFA-order:" + order + "] {" + value + "}", label[0], label[1], true, "?");
//            MultiChart.open(fsF2, "Anzahl Segmente je s [order:" + order + "] (all rows)", "s", "z", true, "?");
            MultiChart.open(fsFMW, "<F(s)>", label[0], label[1], true, "?");

        }
        else {   // Fs, "[Phase " + phase + "] F(s) [DFA-order:" + order + "] {" + value + "}", "log(s)", "log(F(s))", false, "?");
            String file= "Phase_" + phase + " DFA-order_"+ order + " v=" + value;
            System.out.println( file );

//            MultiChart.openAndStore( Fs, "F(s) - [Phase " + phase + "] [DFA-order:" + order + "] {" + value + "}",  "log(s)", "log(F(s))", true, "data/dfa", file, "?");
//            MultiChart.open(Fs, "F(s)", "s", "F(s)", false, "?");
//            MultiChart.open(fsFMW, "<F(s)> - [Phase " + phase + "] F(s) [order:" + order + "] (all rows)", "log(s)", "log(<F(s)>)", false, "?");
//            MultiChart.open(fsF2, "Anzahl Segmente je s - [Phase " + phase + "]  [order:" + order + "] (all rows)", "s", "#Segmente", false, "?");
        }

        resetPuffer();
        
    }

    static Messreihe mw = null;
    
    /**
     * zurückgeben nur einmal möglich !!!
     * @return 
     */
    static public Messreihe getMWKurve() {
        Messreihe r = mw;
        mw = null;
        return r;
    }



    public static Vector<Messreihe> getTestReihen( int anz ) {
        stdlib.StdRandom.initRandomGen(1);


        int[] length = { 1000, 500, 250, 500, 2500,
                         3500, 1450, 2500, 700, 3000,
                         1000, 600, 200, 100, 2000,
                         1234, 5678, 2222, 100, 10  };

        Vector<Messreihe> vmr = new Vector<Messreihe>();
        for (int i = 0; i < anz; i++) {

            Messreihe mr = TestDataFactory.getDataSeriesRandomValues_RW(length[i]);
            mr.setLabel("R" + i + " [" + length[i] + "]");
            vmr.add(mr);
        }  // nun liegen verschieden Lange Testreihen vor ...

        return vmr;
    }


}
