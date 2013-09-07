/***
 *
 *  Calculates EventSynchronisation for random Event-TimeSeries
 * 
 ***/
package statphys.eventsynchronisation.tests;

//import hadoopts.analysis.eventsynchronisation.ESCalc;
import statphys.eventsynchronisation.ESCalc2;

/**
 *
 * @author kamir
 */
public class ESMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        StringBuffer sb = new StringBuffer();
        
        int n1 = 2000;
        
        ESCalc2.debug = false;
        
        int[] r1 = ESCalc2.createEventTS(n1, 100); 
        int[] r2 = ESCalc2.createEventTS(n1, 100); 
//        
//        r1[2] = 1;
//        r1[5] = 1;
//        r1[13] = 1;
//        r1[18] = 1; 
//        r1[20] = 1; 
//        
//        
        int t = -1;
//        
//        r2[2+t] = 1;
//        r2[5+t] = 1;        
//        r2[13+t] = 1;
//        r2[18+t] = 1;
//        r2[20+t] = 1;

        ESCalc2.checkRows(r1, r2, "Events");

        int[] r1ET = ESCalc2.getIETS2(r1);
        int[] r2ET = ESCalc2.getIETS2(r2); 
        
//        ESCalc.checkRows(r1IETS, r2IETS, "IETS");            
//
        double[] es1 = ESCalc2.calc( r1ET, r1ET );
        double[] es2 = ESCalc2.calc( r2ET, r2ET );

        double[] es3 = ESCalc2.calc( r1ET, r2ET );

        System.out.println( "r1) " + es1[0] + " " + es1[1] );
        System.out.println( "r2) " + es2[0] + " " + es2[1] );
        sb.append( "r1,r2) " + es3[0] + " " + es3[1] + "\n" );

        System.out.println( sb.toString() );
    }
}
