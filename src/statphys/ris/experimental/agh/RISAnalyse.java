package statphys.ris.experimental.agh;

import data.io.MessreihenLoader;
import data.series.Messreihe;
import chart.simple.MultiChart;
import data.series.MesswertTabelle;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Calculations for AGH paper in the SOCIONICAL project.
 *
 * @author kamir
 */
public class RISAnalyse {

    // Defaultparameter
    static String label = "(?)";
    static boolean scaleRQ = false;

    static boolean sLN = true;
    static boolean sLOG = false;

    static int binning = 5;
    static int scale = 5;

    static int zINTERVALL = 9;
    static int zINTER = 9;
    static int zL = 100;

    public static String getParameterSet() {
        StringBuffer s = new StringBuffer();
        s.append( "label      = " + label +"\n" );
        s.append( "scaleRQ    = " + scaleRQ +"\n"  );
        s.append( "sLN        = " + sLN +"\n" );
        s.append( "sLOG       = " + sLOG +"\n" );
//        s.append( "folderIN   = " + folderIN +"\n" );
//        s.append( "folderOUT  = " + folderOUT +"\n" );
//        s.append( "linFit_MIN = " + linFit_MIN +"\n" );
//        s.append( "linFit_MAX = " + linFit_MAX +"\n" );
        s.append( "binning    = " + binning +"\n" );
        s.append( "scale      = " + scale +"\n" );
        s.append( "zINTER     = " + zINTER +"\n" );
        s.append( "zL         = " + zL +"\n" );

        return s.toString();
    };

    public static void setVariables_A() {
        label = "(a)";

        scaleRQ = false;

        binning = 8;
        scale = 25;
    };

    public static void setVariables_B() {
        label = "(b)";

        scaleRQ = true;


        binning = 8;
        scale = 2;
    };

    public static void setVariables_A2() {
        label = "(a2)";

        scaleRQ = false;
        sLN = false;
        sLOG = true;


        binning = 8;
        scale = 25;
    };

    public static void setVariables_B2() {
        label = "(b2)";

        scaleRQ = true;
        sLN = false;
        sLOG = true;

        binning = 8;
        scale = 2;
    };


    public static void main(String[] args) throws InterruptedException {
        String folder = "/home/kamir/NetBeansProjects/DataAnalysisProjects/data/in/agh/";
        
        // G:/PHYSICS/PHASE1
                setVariables_A();
                calcCharts();
                setVariables_B();
                calcCharts();

        int binning = 5;
        int scale = 25;

    }

    public static void calcCharts() {
        //ReturnIntervallStatistik.doScale_by_Rq = scaleRQ;
        String folder = "./data/in/agh/";

        
        File f2 = new File(folder);
        File[] files = f2.listFiles();
        int i = 0;

        System.out.println( f2.getAbsolutePath() + " " + f2.exists() + " " +  f2.canRead() );

        Messreihe[] RQs = new Messreihe[zINTERVALL];
        Messreihe[] RISs = new Messreihe[zINTERVALL];

        ReturnIntervallStatistik[] ris = new ReturnIntervallStatistik[zINTERVALL];

        for( int ii = 0; ii < zINTERVALL ; ii++ ) {

            ris[ii] = new ReturnIntervallStatistik(binning,scale);
//            ris[ii].scaleY_LN = sLN;
//            ris[ii].scaleY_LOG = sLOG;

            RQs[ii] = new Messreihe("RQ_" + ii);
            RISs[ii] = new Messreihe("RIS_" + ii);
        }

        for (File f : files) {
            i++;
            if (f.getName().endsWith(".txt")) {

                DecimalFormat nf = new DecimalFormat("0.0000000");
           

                File f3 = new File(folder + f.getName());
                Messreihe data = MessreihenLoader.getLoader().loadMessreihe( f3 );
                System.out.println( f3 );

                
                Messreihe[] parts = data.split( zL , zINTERVALL );
                
                int j = 0;
                for( Messreihe r : parts ) {
                    //System.out.print( nf.format( r.getAvarage() ) + "\t" );
                    //System.out.print( r.toString() );
                    RQs[j].addValue( r.getAvarage() );

                    ReturnIntervallStatistik rr = new ReturnIntervallStatistik(
                            binning,scale);
//                    rr.scaleY_LN = sLN;
//                    rr.scaleY_LOG = sLOG;
                    rr.doScale_by_Rq = scaleRQ;

                    rr.addDistData(r.getYValues());
                    try {
                        ris[j].add(rr);
                    } catch (Exception ex) {
                        Logger.getLogger(RISAnalyse.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }

                    j++;
                }
                System.out.println();

            }
        }


        Messreihe rq = new Messreihe("Rq(i)");
        Messreihe rqSIGMA = new Messreihe("sigma(Rq(i))");
        Vector<Messreihe> chart2Data = new Vector<Messreihe>();
        chart2Data.add(rq);
        chart2Data.add(rqSIGMA);


        Vector<Messreihe> vMR = new Vector<Messreihe>();
        for ( int d=0; d < zINTER ; d++  ) {
            try {
                ris[d].calcMessreihen();
            }
            catch (Exception ex) {
                Logger.getLogger(RISAnalyse.class.getName()).log(
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
        String title = "Binning="+binning+"; Scale="+scale;
        MultiChart.open(vMR, title, "r/Rq", "P(r)*Rq" , false, "-");
        MultiChart.open(RQs, title, "i", "r" , true);

        String labelX = "r/Rq";
        String labelY = "P(r)*Rq";
        
        if ( !scaleRQ ) {
            labelX = "r";
            labelY = "P(r)";
        }
        MultiChart.open(vMR, label + " - binning="+binning+", scale="+scale,
                labelX,  labelY, false, getParameterSet() );
        MultiChart.open(chart2Data, label + " - Rq(i)",
                "Rq", "i", true, getParameterSet() );

        MesswertTabelle tab1 = new MesswertTabelle();
        tab1.setLabel(label + "_P(r)");
        tab1.setMessReihen(vMR);
        tab1.writeToFile( 
                new File( folder + File.separator + "out" +
                          File.separator + tab1.getLabel() + ".dat" ) );

        MesswertTabelle tab2 = new MesswertTabelle();
        tab2.setLabel(label + "_Rq(i)");
        tab2.setMessReihen(chart2Data);
        tab2.writeToFile( 
                new File( folder + File.separator + "out" +
                          File.separator + tab2.getLabel() + ".dat" ) );
        

    }


    
}
