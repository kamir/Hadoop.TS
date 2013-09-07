/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statphys.eventsynchronisation.tests;

import chart.simple.MultiChart;
import chart.simple.MyXYPlot;
import data.series.Messreihe;
import statphys.eventsynchronisation.ESCalc2;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class Chart1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // relative shift      :  rs
        // time shift          :  dt
        // <inter event times> :  <t_be>
        // rs = dt / < t_be > 
        
        ESCalc2.debug = false;


        // log the results later
        StringBuffer sb = new StringBuffer();

        int k_MAX = 10; // Anzahl von Iterationen

        int n1 = 365 * 24;
        int n2 = 365 * 24;

        int rhoMAX = 600;

        int dt_MAX = 350;

        Vector<Messreihe> vmr = new Vector<Messreihe>();

        for (int rho = 100; rho < rhoMAX; rho = rho + 50) {

            Messreihe mrES_vs_SHIFT = new Messreihe();
            mrES_vs_SHIFT.setLabel("Q_" + n1 + "_" + dt_MAX + "_" + rho + "_" + k_MAX);

            Messreihe mrES_q_vs_SHIFT = new Messreihe();
            mrES_q_vs_SHIFT.setLabel("q_" + n1 + "_" + dt_MAX + "_" + rho + "_" + k_MAX);

            for (int dt = 1; dt < dt_MAX; dt = dt + 1) {

                double sumX = 0;
                double sumY = 0;
                double sumY2 = 0;

                for (int k = 0; k < k_MAX; k++) {
                    
                    int r1[] = ESCalc2.createEventTS(n1, rho);
                    int[] r1_IETS = ESCalc2.getIETS2(r1);

                    double aviet = ESCalc2.getAVIET(r1);
                    
                    //int r2[] = ESCalc2.moveEventRows2(r1, dt);  // positives q
                    int r2[] = ESCalc2.moveEventRows(r1, dt);     // negatives q
                    
//                    ESCalc2.checkRows(r1, r2);
                    
                    int[] r2_IETS = ESCalc2.getIETS2(r2);

                    double[] esTEST1 = ESCalc2.calc(r1_IETS, r1_IETS);
                    double[] esTEST2 = ESCalc2.calc(r2_IETS, r2_IETS);                    
                    double[] es3 = ESCalc2.calc(r1_IETS, r2_IETS);
                    
                    System.out.print( esTEST1[0] + " " + esTEST1[1] );
                    System.out.print( " " + esTEST2[0] + " " + esTEST2[1] );
                    System.out.println( " " + es3[0] + " " + es3[1] );
                    
                    //sb.append( (k+1) + ") " + es3[0] + " " + es3[1] + " " + aviet + "\n" );
                    //sb.append( "\t" + es2[0] + " " + es2[1] + "\n" );

                    double x = (double) dt / ((double) aviet);

                    double y = es3[0];
                    double y2 = es3[1];

                    sumX = sumX + x;
                    sumY = sumY + y;
                    sumY2 = sumY2 + y2;

                }
                mrES_vs_SHIFT.addValuePair(sumX / (double) k_MAX, sumY / (double) k_MAX);
                mrES_q_vs_SHIFT.addValuePair(sumX / (double) k_MAX, sumY2 / (double) k_MAX);
                
                //System.out.println(sumY / (double) k_MAX + " " + sumX / (double) k_MAX + " " + rho + " " + dt + " " + aviet);

            }
            System.out.println( sb.toString() );
            sb = new StringBuffer();
            vmr.add(mrES_vs_SHIFT);
            vmr.add(mrES_q_vs_SHIFT);
            MyXYPlot.xRangDEFAULT_MAX=5;
            MyXYPlot.xRangDEFAULT_MIN=0;
            MyXYPlot.yRangDEFAULT_MIN=-1.5;
            MyXYPlot.yRangDEFAULT_MAX=1.5;
            
            MyXYPlot.open(vmr, " ", "dt / <iet>", "synch", true);
        }
    }

    
}
