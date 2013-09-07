package statphys.detrending;

import chart.simple.MultiChart;
import chart.simple.MultiChartDialog;
import data.series.Messreihe;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import statphys.detrending.methods.DFA;
import statphys.detrending.methods.IDetrendingMethod;
import org.apache.commons.math.stat.regression.SimpleRegression;

/**
 * Das MultiDFA-Tool berechnet für eine Menge von Zeitreihen, die z.B. zu
 * einer bestimmten Klasse gehÃ¶ren die DFA einzeln.
 *
 * Ausserdem wird fÃ¼r jede Fensterbreite s die Summe von (x-schlange)^2
 * und die Anzahl der betrachteten Segmente ermittelt.
 *
 * Am Ende wird fÃ¼r alle s aus allen BeitrÃ¤gen der unterschieliche langen
 * Zeitreihen ein F(s) bestimmt.
 *
 * @author kamir
 */
public class MultiDFATool {

    Messreihe finalFS = null;


    public static void main(String[] main) {

        MultiDFATool tool = new MultiDFATool();
        stdlib.StdRandom.initRandomGen(1);

        int[] length = {500, 1500, 2500, 1000, 5000,
            2500, 3500, 1450, 2500, 7000,
            3000, 10000, 6000, 2000, 1000};

        Vector<Messreihe> vmr = new Vector<Messreihe>();
        for (int i = 0; i < 15; i++) {
            Messreihe mr = TestDataFactory.getDataSeriesRandomValues_RW(length[i]);
            mr.setLabel("R" + i + " [" + length[i] + "]");
            vmr.add(mr);
        }  // nun liegen verschieden Lange Testreihen vor ...

        // nun werden die Berechnungen fÃ¼r verschiedene Ordnungen durchgefÃ¼hrt
//        int[] orders = {1, 2, 3};
//        for (int i : orders) {
//            tool.runDFA(vmr, i);
//        }

         tool.runDFA(vmr, 2);
    }


    IDetrendingMethod dfa;
            // Für alle s wird am Ende ein Fluctuationsfunktion Ã¼ber alle Reihen
        // bestimmt
        Vector<Messreihe> Fs = new Vector<Messreihe>();
        Vector<Integer> s = new Vector<Integer>();
        
    // FÃ¼r eine Klasse von Messreiehn wird die DFA einer Ordnung berechnet.
    public void runDFA( Messreihe[] mr, int order) {     
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        for( Messreihe m : mr ) { 
            vmr.add(m);
        }
        runDFA(vmr, order);
    }    
    // FÃ¼r eine Klasse von Messreiehn wird die DFA einer Ordnung berechnet.
    public void runDFA(Vector<Messreihe> vmr, int order) {

        dfa = DetrendingMethodFactory.getDetrendingMethod(order);

        // s bestimmen ...
        int zahlS = 0;
        
        // die laengste ZR bestimmt unsere EInteilung von s ...
        Messreihe lang = null;
        for (int z = 0; z < vmr.size(); z++) {

            Messreihe mr = vmr.elementAt(z);
            if ( mr.xValues.size() > zahlS ) {
                zahlS = mr.xValues.size(); 
                lang = mr;
            }
            
        }
        System.out.println("> Längste Reihe = " + zahlS );
        zahlS = calcNrOf_s(lang, order);
        System.out.println("> Anzahl(s)     = " + zahlS );

        String savePath = "./data/out/";
        

        double fit_min = 1, fit_max = 2;  // willkürliche Festlegung
        double alpha_cor_min = 0.0;  // zu klÃ¤ren was hier gemeint ist !!!

        System.out.println("> Berechne DFA für " + vmr.size() + " Messreihen.");
        StringBuffer info = new StringBuffer("> Infos zur DFA-Rechnung");
        info.append("\nnr.\t| file\t| save path");



        // je Zeitreihe wird ein alpha bestimmt ....
        double[] alpha = new double[vmr.size()];

        // für jedes s wird eine Summe ( xs^2 ) bestimmt und die Anzahl der
        // Segmente anzw
        double[] FS_ = new double[ zahlS ];
        double[] anzw_ = new double[ zahlS ];
        double[] sum_xs_xs_ = new double[ zahlS ];

        // Schleife über alle Zeitreihen dieser Klasse
        for (int z = 0; z < vmr.size(); z++) {

            Messreihe mr = null;
            double[][] dfa_results = null;

            mr = vmr.elementAt(z);
            if (mr != null) {

                // set number of values
                double[] zr = mr.getData()[1];
                dfa.setNrOfValues(zr.length);
                // set values for windowsizes (s) ...
                dfa.initIntervalSlog();
                
                if ( mr.equals(lang)) showS();
                // set input data (zr=zeitreihe)
                dfa.setZR(zr);
                // start dfa calculation
                dfa.calc();
                // get results ...
                dfa_results = dfa.getResults();  // Funktion F(s),

                /*
                F[0][s_pos] = s[s_pos];
                F[1][s_pos] = anzw;
                F[2][s_pos] = FS;
                F[3][s_pos] = FSUM ==> SUMME(xs^2);
                 */

//                // get alpha
//                double tmp_alpha = dfa.getAlpha(fit_min, fit_max);

                Messreihe res_mr = new Messreihe();
                String labelstr = mr.getLabel();
                res_mr.setLabel(labelstr);
                res_mr.setAddinfo(
                        " alpha_fit_min:    " + dfa.getAlphaFitMin()
                        + "\n# alpha_fit_max: " + dfa.getAlphaFitMax()
                        + "#\n DFA-Ordnung:   " + dfa.getPara().getGradeOfPolynom());

//                double cor_coef=dfa.getCorr_coef();
//                System.out.println(cor_coef + " : " + alpha_cor_min);
//
//                if (cor_coef > alpha_cor_min) {
//                    act_Alpha[z] = tmp_alpha;

                System.out.println( ">>>" + dfa_results[1].length + " Werte in dfa_RESULTS[1] ..." + anzw_.length);
                
           
                for (int i = 0; i < dfa_results[1].length; i++) {
                    // Ergebnis der einzelnen Reihe erfassen und Speichern ...
                    res_mr.addValuePair(Math.log10(dfa_results[0][i]), Math.log( dfa_results[2][i] ));

                    // Teilergebnis in den Container übergeben.
                    anzw_[i] = anzw_[i] + dfa_results[1][i];  // Anzahl
                    sum_xs_xs_[i] = sum_xs_xs_[i] + dfa_results[3][i];
                }

                String dfaFilesPath = savePath + (z + 1) + "_dfa" + dfa.getPara().getGradeOfPolynom() + ".txt";
                File dfaFile = new File(dfaFilesPath);
                res_mr.writeToFile(dfaFile); // Messreihe schreiben

                //save alphas to file
                info.append("\n-----------------------------------------------------------------------------------");
                info.append("\n" + (z + 1) + "\t| " + vmr.elementAt(z).getLabel() + "\t| " + dfaFilesPath);
//}
                Fs.add(res_mr);
            }
        }

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
        Messreihe finalFS = new Messreihe();
        Messreihe finalZ = new Messreihe();
        for (int i = 0; i < zahlS; i++) {
            finalFS.addValuePair( Math.log10( S[i] ) , Math.log10( Math.sqrt( sum_xs_xs_[i] / anzw_[i] )));
            finalZ.addValuePair( S[i] , anzw_[i] );
        }

        MultiChart.open(Fs, "F(s) over s [order:" + order + "]", "log(s)", "log(F(s))", true, "?");

        Vector<Messreihe> fsF = new Vector<Messreihe>();
        fsF.add(finalFS);
        MultiChart.open(fsF, "F(s) over s [order:" + order + "] (all rows)", "log(s)", "log(F(s))", true, "?");

        Vector<Messreihe> fsF2 = new Vector<Messreihe>();
        fsF2.add(finalZ);
        MultiChart.open(fsF2, "Anzahl Segmente je s [order:" + order + "] (all rows)", "s", "z", true, "?");

        System.out.println(info.toString());

        SimpleRegression reg = null;
        try {
            reg = finalFS.linFit(1.0, 3.0);
            System.out.println( "alpha=" + reg.getSlope() );
        } 
        catch (Exception ex) {
            Logger.getLogger(MultiDFATool.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }        

    }

    public int calcNrOf_s( Messreihe mr, int order) {
        int s_start = Math.abs( order ) + 2 ;
        int s_end = mr.xValues.size() / 4;

        int lin_steps= dfa.getPara().log_start-s_start;//first steps linear
        int log_steps=0;

        double tmp=0;
        for (double i=dfa.getPara().log_start*dfa.getPara().logScaleFactor; Math.round(i) < s_end; i=i*dfa.getPara().logScaleFactor) {//      adding number of steps of log scale

            if(Math.round(i)!=tmp){
                tmp=Math.round(i);
                log_steps++;
            }
        }


        return lin_steps+log_steps;
    };

    int[] S = null;

    private void showS() {
//        S = dfa.getS();
//        int i = 0;
//        while( i < S.length ) {
//            System.out.print( S[i] + ", ");
//            i++;
//        }
        //System.out.println();
    }
}
