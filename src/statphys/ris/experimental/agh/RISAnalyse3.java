package statphys.ris.experimental.agh;

import data.io.MessreihenLoader;
import data.series.Messreihe;
import chart.simple.MultiChart;
import data.series.MesswertTabelle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTML;

import org.apache.poi.hssf.usermodel.*;

import org.apache.commons.math.stat.regression.SimpleRegression;
import report.HTMLReport;
/**
 *
 * @author kamir
 */
public class RISAnalyse3 {

    static boolean storHTML = true;

    static String folderIN = "./data/in/agh/";
    static String folderOUT = "./data/out/agh/";

    static boolean showCharts = true;
    static boolean showRAWData = false;
    static boolean showRAWRIS = false;

    // Grenzen hängen ggf. von der Anzahl der Blöcke ab
    static double linFit_MIN = 1.5;
    static double linFit_MAX = 2.5;

    // Defaultparameter
    static String label = "(?)";
    static boolean scaleRQ = false;

    static boolean sLN = true;
    static boolean sLOG = false;

    static int pScale = 5;
    static int pBinning = 5;

    static int zINTER = 9;
    static int zL = 100;
    
    // Forderer Bereich - 10 Blöcke
    public static void setVariables_A() {
        label = "(a)";

        scaleRQ = false;

        linFit_MIN = 1;
        linFit_MAX = 4.5;

        pScale = 8;
        pBinning = 25;
    };

    // Hinterer Bereich - 10 Blöcke
    public static void setVariables_B() {
        label = "(b)";

        scaleRQ = true;

        linFit_MIN = 1.5;
        linFit_MAX = 4.5;

        pScale = 8;
        pBinning = 2;
    };

    public static void setVariables_C() {
        setVariables_A();
        label = "(c)";

        zINTER = 18;
        zL = 50;
    };

    public static void setVariables_D() {
        setVariables_B();
        label = "(d)";

        linFit_MIN = 1.5;
        linFit_MAX = 3.5;

        linFit_MAX = 5;
        zINTER = 18;
        zL = 50;
    };

    public static void setVariables_E() {
        setVariables_A();
        label = "(e)";

        zINTER = 36;
        zL = 25;
    };

    public static void setVariables_F() {
        setVariables_B();
        label = "(f)";

        zINTER = 36;
        zL = 25;

        linFit_MIN = 1.5;
        linFit_MAX = 3;
    };

    public static void setVariables_G() {
        setVariables_F();
        label = "(g)";


        scaleRQ = false;
        linFit_MIN = 1.5;
        linFit_MAX = 3;

    };


    public static void setVariables_Ha() {
        setVariables_A(); // 8, 25
        label = "(h_a)";

        scaleRQ = false;

 
        linFit_MIN = 1.5;
        linFit_MAX = 3;

        // wir haben nur 999 Werte, also verlieren wir den letzten ...
        zINTER = 20;
        zL = 50;
    };

    public static void setVariables_Hb() {
        setVariables_A(); // 8, 25
        label = "(h_b)";

        scaleRQ = false;


        linFit_MIN = 1.5;
        linFit_MAX = 3;

        // wir haben nur 999 Werte, also verlieren iwr den letzten ...
        zINTER = 10;
        zL = 100;
    };


    public static void setVariables_Ia() {
        setVariables_B(); // 8, 25
        label = "(i_a)";

        scaleRQ = true;


        linFit_MIN = 1.5;
        linFit_MAX = 4;

        // wir haben nur 999 Werte, also verlieren iwr den letzten ...
        zINTER = 20;
        zL = 50; 
    };

     public static void setVariables_Ib() {
        setVariables_B(); // 8, 25
        label = "(i_b)";

        scaleRQ = true;


        linFit_MIN = 1.5;
        linFit_MAX = 4;

        // wir haben nur 999 Werte, also verlieren iwr den letzten ...

        zINTER = 10;
        zL = 100;
    };





    public static String getParameterSetHTML() {  
        return getParameterSet( "<br/>\n");
    };
    
    public static String getParameterSet() {  
        return getParameterSet( "\n");
    };

    public static String getParameterSet( String br ) {
        StringBuffer s = new StringBuffer();
        s.append( "label      = " + label +br );
        s.append( "scaleRQ    = " + scaleRQ +br  );
        s.append( "sLN        = " + sLN +br);
        s.append( "sLOG       = " + sLOG +br );
        s.append( "folderIN   = " + folderIN +br );
        s.append( "folderOUT  = " + folderOUT +br );
        s.append( "linFit_MIN = " + linFit_MIN +br );
        s.append( "linFit_MAX = " + linFit_MAX +br );
        s.append( "binning    = " + pBinning +br);
        s.append( "scale      = " + pScale +br );
        s.append( "zINTER     = " + zINTER +br );
        s.append( "zL (Länge) = " + zL +br );
        return s.toString();
    };

    static HTMLReport report;
    public static void main(String[] args) throws InterruptedException, Exception {

        report = new HTMLReport( folderOUT, "report_"+ label +".html" );
//        setVariables_A();
//        calcCharts();
////
//        setVariables_B();
//        calcCharts();
//
//        setVariables_C();
//        calcCharts();
//
//        setVariables_D();
//        calcCharts();
//
//        setVariables_E();
//        calcCharts();

//        setVariables_F();
//        calcCharts();

        setVariables_Ha();
        calcCharts();

        setVariables_Hb();
        calcCharts();

        setVariables_Ia();
        calcCharts();

        setVariables_Ib();
        calcCharts();

        
        if ( storHTML ) {
            report.writeReport();            
        }
        //System.exit(0);
    }//

    public static void calcCharts() throws Exception {

        

        // alle Dateien einlesen im obigen Ordner
        File[] files = new File(folderIN).listFiles();
        int i = 0;

        Messreihe[] RQs = new Messreihe[zINTER];
        Messreihe[] RISs = new Messreihe[zINTER];
        Vector<Messreihe>[] vRISs = new Vector[zINTER];
        Vector<Messreihe>[] vFITDATA = new Vector[zINTER];

        ReturnIntervallStatistik[] ris = new ReturnIntervallStatistik[zINTER];

        // 58 files and 9 sections ...
        double tTEST_DATA[][][] = new double[files.length][zINTER][5];
        // 0=RQs,  1=m  2=RR  3=FEHLENDE-m  4=FEHLENDE-RR

        for( int ii = 0; ii < zINTER ; ii++ ) {

            ris[ii] = new ReturnIntervallStatistik(pScale,pBinning);
//            ris[ii].scaleY_LN = sLN;
//            ris[ii].scaleY_LOG = sLOG;

            RQs[ii] = new Messreihe("RQ_" + ii);

            RISs[ii] = new Messreihe("RIS_" + ii);
            vRISs[ii] = new Vector<Messreihe>();
            vFITDATA[ii] = new Vector<Messreihe>();
        }


        Vector<Messreihe> all = new Vector<Messreihe>();
        
        double linFitMIN;
        double linFitMAX;


        for (File f : files) {
            
            if (f.getName().endsWith(".txt")) {

                DecimalFormat nf = new DecimalFormat("0.0000000");

                Messreihe data = MessreihenLoader.getLoader().loadMessreihe(
                        new File(folderIN + f.getName()));

                Messreihe[] parts = data.split( zL , zINTER );

                int j = 0;

                // r ist die Messreihe mit den normalen Daten
                for( Messreihe r : parts ) {

                    double vRQ = r.getAvarage();
                    //System.out.print(  "file -> " + i + " part -> " + j + "\t" + nf.format( vRQ ) + "\t" );
                    //System.out.print( r.toString() );
                    tTEST_DATA[i][j][0] = vRQ;

                    vRISs[j].add(r);

                    RQs[j].addValue( vRQ );

                    ReturnIntervallStatistik rr = new ReturnIntervallStatistik(
                            pScale,pBinning);

                    rr.doScale_by_Rq = scaleRQ;
//                    rr.scaleY_LN = sLN;
//                    rr.scaleY_LOG = sLOG;
                    rr.setRQ( vRQ );

                    rr.addDistData(r.getYValues());

                    if ( scaleRQ ) {
                        
                        linFitMIN = linFit_MIN * vRQ;
                        linFitMAX = linFit_MAX * vRQ;
                        //System.out.println( "( " + linFit_MIN + ", " + linFit_MAX + ") ");
                       
                    }
                    else { 
                        linFitMIN = linFit_MIN;
                        linFitMAX = linFit_MAX;
                        //System.out.println( "( " + linFitMIN + ", " + linFitMAX + ") ");
                    };

                    // hier sollten nun die Daten für jede Reihe da sein ...

                    /**
                     * Problem: in den einzelnen Reihen kann man nicht sinnvoll
                     * den Anstieg bestimmen !!!
                     *
                     */
                    Messreihe toFit = rr.mrHaeufigkeit.shrinkX( linFit_MIN, linFit_MAX);

                    SimpleRegression reg;
                    try {
                        reg = toFit.linFit(linFit_MIN, linFit_MAX);

                        double slope = reg.getSlope();
                        double RR = reg.getRSquare();

                        //System.out.println("m=" + slope + " \tRR=" + RR);
                        tTEST_DATA[i][j][1] = slope;
                        tTEST_DATA[i][j][2] = RR;
                        all.add(toFit);

                    }
                    catch (Exception ex) {
                        //Logger.getLogger(RISAnalyse3.class.getName()).log(Level.SEVERE, null, ex);

                    }

                    
                    vFITDATA[j].add(rr.mrHaeufigkeit);
                    try {
                        ris[j].add(rr);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(RISAnalyse3.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    j++;
                }
                i++;
            }
        }

        if( showRAWData ) {
            for( int i3 = 0; i3 < zINTER ; i3++ ) {
                            MultiChart.openAndStore(vRISs[i3] , label + " Intervall: " + i3 + " - scale="+pScale+", binning="+pBinning,
                        "r",  "P(r)", false, folderOUT, label + "_P(r)", getParameterSet() );
            }
        }

        System.out.print( folderOUT );
        Messreihe rq = new Messreihe("Rq(i)");
        Messreihe rqSIGMA = new Messreihe("sigma(Rq(i))");
        Vector<Messreihe> chart2Data = new Vector<Messreihe>();
        chart2Data.add(rq);
        chart2Data.add(rqSIGMA);

        Vector<Messreihe> vMR = new Vector<Messreihe>();
        // für alle Intervalle des Files ...
        for ( int d=0; d < zINTER ; d++  ) {
            try {
                ris[d].calcMessreihen();
            }
            catch (Exception ex) {
                Logger.getLogger(RISAnalyse3.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            RISs[d] = ris[d].mrHaeufigkeit;

            // System.out.println( "(b)\n" + ris[d].toString() );
            ris[d].mrVerteilung.setLabel("Int_"+(d+1));
            vMR.add( ris[d].mrVerteilung );

            RQs[d].calcAverage();

            rq.addValuePair((double)d, RQs[d].getAvarage() );
            
            rqSIGMA.addValuePair((double)d, RQs[d].getStddev() );

        }
        rq.normalize();
        rqSIGMA.normalize();

        // auf VMR nun den Fit ANWENDEN ...
        Messreihe fitData1 = new Messreihe();
        fitData1.setLabel("m");
        
        Messreihe fitData2 = new Messreihe();
        fitData2.setLabel("RR");
        
        Vector<Messreihe> vFITS = new Vector<Messreihe>();
        
        SimpleRegression reg2;
        int c = 0;
        for( Messreihe mr : vMR ) {
        try {
            reg2 = mr.linFit(linFit_MIN, linFit_MAX);

            double slope = reg2.getSlope();
            double RR = reg2.getRSquare();
            
            fitData1.addValuePair(c, slope);
            fitData2.addValuePair(c, RR);
            
            System.out.println("\tm=" + slope + " \tRR=" + RR);
            c++;
        }
        catch(Exception ex) {



        }};
        fitData1.normalize();
        fitData2.normalize();

        vFITS.add(fitData1);
        vFITS.add(fitData2);



        if( showRAWRIS ) {
            for( int i3 = 0; i3 < zINTER ; i3++ ) {
                            MultiChart.openAndStore(vFITDATA[i3] , "vFIT " + label + " Intervall: " + i3 + " - scale="+pScale+", binning="+pBinning,
                        "r",  "P(r)", false, folderOUT, label + "_P(r)", getParameterSet() );
            }
        }

        String labelX = "r/Rq";
        String labelY = "P(r)*Rq";
        
        if ( !scaleRQ ) {
            labelX = "r";
            labelY = "P(r)";
        }
        if ( sLOG ){
            labelY = "log(" + labelY + ")";
        }
        if ( sLN ){ 
            labelY = "ln(" + labelY + ")";
        }

        if( showCharts) {
            MultiChart.openAndStore(vMR, label + " - scale="+pScale+", binning="+pBinning,
                    labelX,  labelY, true, folderOUT, label + "_P(r)", getParameterSet() );
            
             MultiChart.openAndStore(vFITS, "FIT " + label + " - scale="+pScale+", binning="+pBinning,
                    "i",  "m(i) RR(i)", true, folderOUT, label + "_m(i)_RR(i)", getParameterSet() );

            MultiChart.openAndStore(chart2Data, label + " - Rq(i)",
                    "i","<Rq>, <sigma(Rq)>", true, folderOUT, label + "_Rq(i)" , getParameterSet());


            MultiChart.open(all, "ALL " + label + " - scale="+pScale+", binning="+pBinning,
                    "x",  "y", false, getParameterSet());
        }

        MesswertTabelle tab1 = new MesswertTabelle();
        tab1.setHeader( getParameterSet() );
        tab1.setLabel(label + "_P(r)");
        tab1.setMessReihen(vMR);
        tab1.writeToFile( 
                new File( folderOUT +
                          File.separator + tab1.getLabel() + ".dat" ) );

        MesswertTabelle tab6 = new MesswertTabelle();
        tab6.setHeader( getParameterSet() );
        tab6.setLabel(label + "_m(i)_RR(i)");
        tab6.setMessReihen(vFITS);
        tab6.writeToFile(
                new File( folderOUT + File.separator + "out" +
                          File.separator + tab6.getLabel() + ".dat" ) );


        MesswertTabelle tab2 = new MesswertTabelle();
        tab2.setHeader( getParameterSet() );
        tab2.setLabel(label + "_Rq(i)");
        tab2.setMessReihen(chart2Data);
        tab2.writeToFile( 
                new File( folderOUT +
                          File.separator + tab2.getLabel() + ".dat" ) );
        


        String fn = folderOUT + "/tTests_"+ label +".xls";
        String fnTEMPL = folderOUT + "/tTests.xls";

        HSSFWorkbook wb = null;
        HSSFSheet[] sheets = new HSSFSheet[3];

        File f2 = new File( fn );
        File f1 = new File( fnTEMPL );

        // Lesen der bestehenden Tabelle
        FileInputStream fis = null;
        try {
            //
            // Create a FileInputStream that will be use to read the excel file.
            //
            fis = new FileInputStream(fn);

            //
            // Create an excel workbook from the file system.
            //
            wb = new HSSFWorkbook(fis);
            //
            // Get the first sheet on the workbook.
            //
        }
        catch( Exception ex) {
            ex.printStackTrace();
            
            System.err.println( ">>> " + ex.getMessage() );
            System.out.println("> Erzeuge neue XLS Datei ... ");

            wb = new HSSFWorkbook();
            sheets[0] =  wb.createSheet("R_q");
            sheets[1] =  wb.createSheet("m");
            sheets[2] =  wb.createSheet("RR");
        }

        Messreihe[] mrRESULTS = new Messreihe[5];
        try {
            sheets[0] =  wb.getSheet("R_q");
            sheets[1] =  wb.getSheet("m");
            sheets[2] =  wb.getSheet("RR");

            // zur Sammlung der Mittelwerte für die Resultate ...
            mrRESULTS[0] =  new Messreihe( "<R_q>");
            mrRESULTS[1] =  new Messreihe( "<m>");
            mrRESULTS[2] =  new Messreihe( "<RR>");
            mrRESULTS[3] =  new Messreihe( "{m}");
            mrRESULTS[4] =  new Messreihe( "{RR}");

            // SCHRANKE für auszulassende Werte
            double epsilon = 1e-6;
            
            // ANZAHL der Reihen
            for( int i2 = 1; i2 < files.length; i2++ ) { 
                // ANZAHL der INTERVALLE 
                for( int j2 = 0; j2 < zINTER; j2++ ) {
                    // ANZAHL der MESSSGRÖSSEN
                    for( int k2 = 0; k2 < 3; k2++ ) {     
                       
                        HSSFRow row = sheets[k2].getRow(i2);
                        if ( row == null ) row = sheets[k2].createRow(i2);
                        
                        row.createCell(1).setCellValue(i2);
                        double value = tTEST_DATA[i2][j2][k2];
                        if ( !Double.isNaN(value) ) {
                            if ( value > epsilon ) {
                                row.createCell(j2+1).setCellValue(value);
                            }
                        }
                        else {
                            row.createCell(j2+1).setCellValue( "" );
                        }
                        // System.out.println( i2 +"\t" + j2 + "\t" + k2 + "\t" + tTEST_DATA[i2][j2][k2]);

                    }
                }
            }

            // MW für alle Spalten berechnen ... für drei Messwerte ...
            for( int x = 0; x < 3; x++ ) { 

                double eps = 1e-3;

                double anz = 0.0;
                double summe = 0.0;

                int counterNAN_values = 0;
                // ANZAHL der INTERVALLE
                for( int j2 = 0; j2 < zINTER; j2++ ) {
                    
                    // ANZAHL der Reihen
                    for( int i2 = 1; i2 < files.length; i2++ ) { 
                        double value = tTEST_DATA[i2][j2][x];

                        double RR = tTEST_DATA[i2][j2][2];

                        if ( !Double.isNaN(value) ) {
//                            if ( value > eps ) {  // Variante 1
//                            if ( RR > 0.75) {  // Variante 2
                                summe = summe + value;
                                anz++;
//                            }
                        }
                        else {
                            counterNAN_values++;
                        }
                    }
                    double mw = summe  / anz;
                    mrRESULTS[x].addValuePair(j2, mw);
                    if ( x > 0 ) mrRESULTS[x+2].addValuePair( j2, counterNAN_values );

                    anz=0.0;
                    summe=0.0;
                    counterNAN_values =0;
                }
            }

            mrRESULTS[3].normalize();
            mrRESULTS[4].normalize();

            Vector<Messreihe> v = new Vector<Messreihe>();
            v.add( mrRESULTS[0] );
            v.add( mrRESULTS[1] );
            v.add( mrRESULTS[2] );
            v.add( mrRESULTS[3] );
            v.add( mrRESULTS[4] );

            if( showCharts) {
                MultiChart.openAndStore(v, label + " - scale="+pScale+", binning="+pBinning,
                    "i",  "f(i)", true, folderOUT, label + "_chart3" , getParameterSet() );             
            }

            FileOutputStream fout2 = new FileOutputStream(f2.getAbsolutePath());
            wb.write( fout2 );
            fout2.close();

            String[] titles = { "P(r)", "Rq(i)", "chart3" , "m(i)_RR(i)" };
            report.createResultlinePNG( label , titles, getParameterSetHTML() );

            System.out.println("[READY] "+label+"");

        }

        catch( Exception ex) {
            ex.printStackTrace();
        }
        
    }







    
    
}
