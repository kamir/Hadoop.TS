
package statphys.eventsynchronisation;

import chart.simple.MultiChart;
import data.series.Messreihe;
import java.util.Collections;
import java.util.Vector;


public class ESCalc2 {
    
    public static boolean debug = true;

    /**
     * For two given time-series ti and tj, the "Event-Synchronisation" 
     * is calculated. 
     * 
     *  
     * sync:   Strength of Event Synchronization (Q_ij)
     * delay:  Delay quantity (q_ij)
     * 	
     * The second delay q_ji(=-q_ij) is not given, as well as Q_ji(=Q_ij).
     *
     */
    public static double[] calcTS(int[] x , int[] y) {
        
        int N1 = x.length;
        int N2 = y.length;

        // get Inter Event Times
        int[] tx = ESCalc2.getIETS(x);
        int[] ty = ESCalc2.getIETS(y);
        
        // NR of event in each row 
        int M1 = tx.length;
        int M2 = ty.length;
        
        double synch = 0.0;
        double delay = 0.0;       
        
        double[] data = new double[2];
        data[0] = 0.0;
        data[1] = 0.0;

        System.out.println("> event density : " + N1 + " " + M1 + " " + N2 + " " + M2 );

        if ( M1 == 0 ) return data;
        if ( M2 == 0 ) return data;
        
        

//
//        double valij=0.0;
//        double valji=0.0;
//
//        int ch = 0;
//        for( int i=1; i < N1-1; i++) {
//          for( int j=1; j < N2-1; j++) {
//            ch++;
//            
//            Vector<Double> temp = new Vector<Double>();
//
//            double a = ti[l+1]-ti[l];
//            double b = ti[l]-ti[l-1];
//            double c = tj[m+1]-tj[m];
//            double d = tj[m]-tj[m-1];
//            
//            temp.add( a );
//            temp.add( b );
//            temp.add( c );
//            temp.add( d );
//            
//            double tau = Collections.min( temp );
//            
//            double tau2 = tau * 0.5;
//            
//            double e = ti[l]-tj[m];
//            double f = tj[m]-ti[l];
//            
//            if( debug ) System.out.println( tau + " " + e + " " + f );
//            
//            
//            double eps = 0.0000000001;
//            
//            if( e > (tau2 - eps) && e < (tau2 + eps)  ) { 
//              valij=valij + 0.5; 
//              valji=valji + 0.5; 
////              System.out.println(".");
//            }
//            
//            if ( 0.0 <= e && e <= tau2 - eps ) {
//              valij=valij+1.0; 
////              System.out.println("..");
//            }  
//            if ( 0.0 <= f && f <= tau2 - eps ) {
//              valji=valji+1.0;
////              System.out.println("...");
//            }
//            
//            if( debug )
//                System.out.println(ch + ")" + temp.toString() + " tau=" + tau + "   "
//                    + "e=" + e + "   f=" + f + " ij=" + valij + " ji="+valji  );
//
//          }
//        }
//        
//        System.out.println( valij + " " + valji );
//        
//        synch=(valij+valji) / Math.sqrt( (di)*(dj) ) ;
//        delay=(valij-valji) / Math.sqrt( (di)*(dj) ) ;
 
        data[0] = synch;
        data[1] = delay;
        
        return data;
    }

    public static double[] calc(int[] tx , int[] ty) {
        
        double[] data = new double[2];
        data[0] = -2.0;
        data[1] = -2.0;

        
        // NR of event in each row 
        int M1 = tx.length;
        int M2 = ty.length;
        
        if ( M1 < 3 || M2 < 3 ) return data;
        
        double synch = 0.0;
        double delay = 0.0;       
        

        double fNorm = Math.sqrt( M1 * M2 );
        System.out.println("> nr of events : " + M1 + " " + M2 + " fNorm=" + fNorm );

        if ( M1 == 0 ) return data;
        if ( M2 == 0 ) return data;
                
        double cij = calcCIJ( tx, ty );
        
        double cji = calcCIJ( ty, tx );
            
        
        System.out.println("C_ij="+cij + " C_ji="+ cji );
        double f = Math.sqrt( (M1-1)*(M2-1) );
        System.out.println("f="+f );
        synch=(cij+cji) / f ;
        delay=(cij-cji) / f ;
 
        data[0] = synch;
        data[1] = delay;
        
        return data;
    }

    
    public static int[] createEventTS(int length, int nr) {
        int[] r1 = new int[length];
        for (int i = 0; i < nr; i++) {
            r1[i] = 0;
        }

        int e = 0;
        while (e < nr) {
            int pos = (int) (Math.random() * (double) (length));
            if (r1[pos] != 1) {
                r1[pos] = 1;
                e++;
            }
        }
        return r1;
    }
    
    public static int[] moveEventRows2(int[] r1, int dt) {
        // andere Richtung verschieben ...
        
        int[] r2 = new int[r1.length];

        int j = 0;
        for (int i = dt; i < r1.length; i++) {
            r2[j] = r1[i];
            j++;
        }
        for (int i = 0; i < dt; i++) {
            r2[j] = r1[i];
            j++;
        }
        return r2;
    }
    
    /**
     * Verschiebung der Reihen für Methoden-Tests
     * 
     * @param r1
     * @param dt
     * @return 
     */
    public static int[] moveEventRows(int[] r1, int dt) {
        int[] r2 = new int[r1.length];

        int j = 0;
        for (int i = r1.length - dt; i < r1.length; i++) {
            r2[j] = r1[ i];
            j++;
        }
        for (int i = 0; i < r1.length - dt; i++) {
            r2[j] = r1[i];
            j++;
        }
        return r2;
    }

    /**
     * Konvertiere Daten in eine Inter-Event Reihe.
     * 
     * @param r2
     * @return 
     */
    public static int[] getIETS(int[] r2) {

        Vector<Integer> r = new Vector<Integer>();
        int iet = 1;
        for (int i : r2) {
            if (i == 1) { 
                r.add(iet);
//                System.out.print("*");
                iet = 1;
            }
            else {
                iet++;
            }
        }    
        
        int p = 0;
        int[] r1 = new int[r.size()];
 
        for (int i : r) {
            r1[p] = i;
//            System.out.println( p + ": " + i );
            p++; 
        }
//        System.out.println( ">>> " + r1.length );
        return r1;
    }
    /**
     * Konvertiere Daten in eine Event Reihe.
     * 
     * @param r2
     * @return 
     */
    public static int[] getIETS2(int[] ETS) {

        Vector<Integer> rr2 = new Vector<Integer>();
        int et = 0;
        
        for (int i : ETS) {
            et++;
            if (i == 1) { 
                // wann war der EVENT?
                rr2.add(et);
            }    
        }    
        
        int p = 0;
        int[] r1 = new int[rr2.size()];
 
        for (int i : rr2) {
            r1[p] = i;
//            System.out.println( p + ": " + i );
            p++; 
        }
        System.out.println( ">>> " + r1.length );
        return r1;
    }

    public static void checkRows(int[] r1, int[] r2) {
        checkRows(r1, r2, "ESCalc" );
    }

    /**
     * Berechne die Mittlere Inter-Event-Time
     * Bezogen auf den Index im Feld.
     * 
     * Dabei ist die Einheit egal 1 ZE = 1 Index-Wert
     * 
     * @param r1 
     * @return 
     */
    public static double getAVIET(int[] r1) {
        Vector<Integer> r = new Vector<Integer>();
        int iet = 0;
        for (int i : r1) {
            if (i == 1) {
                r.add(iet);
                iet = 0;
            } else {
                iet++;
            }
        }
        int sum = 0;
        for (int i : r) {
            sum = sum + i;
        }
        double av = (double) sum / (double) r.size();

        return av;

    }

    /**
     *  Erzeuge Messreihen und zeige diese dann an. 
     */
    public static void checkRows(int[] r1, int[] r2, String title) {
        Messreihe a1 = new Messreihe();
        Messreihe a2 = new Messreihe();

        Messreihe[] r = new Messreihe[2];
        r[0] = a1;
        r[1] = a2;

        for (int i : r1) {
            a1.addValue(i);
        }
        for (int i : r2) {
            a2.addValue(i);
        }

        MultiChart.open(r , title , "t", "event" , true);
    }

    private static double calcCIJ(int[] tx, int[] ty ) {
        double cij = 0.0;
        int M1 = tx.length;
        int M2 = ty.length;
        
        for( int i=1; i < M1-1; i++) {
          for( int j=1; j < M2-1; j++) {
            
            Vector<Double> temp = new Vector<Double>();

            double a = tx[i+1]-tx[i];
            double b = tx[i]-tx[i-1];
            double c = ty[j+1]-ty[j];
            double d = ty[j]-ty[j-1];
            
            temp.add( a );
            temp.add( b );
            temp.add( c );
            temp.add( d );
            double tau = Collections.min( temp );
            tau = tau * 0.5;
//            System.err.println( a + " " + b + " " + c + " " + d + " " + " --- " + tau );
            
            // wir brauchen hier die Event-Times nicht die Abstände 
            double e = tx[i];
            double f = ty[j];
            
            double DIFF = e-f;
            double dij = 0.0;

            String tab = "   ";
            if( 0.0 < DIFF && DIFF <= tau ) { 
              dij=1.0; 
            }
            else if ( e == f ) {
                    dij = 0.5;  
            }
            else {
                dij = 0.0;
            }
            cij = cij + dij;
//            System.out.print( tab + "i,j =>(" +i+","+j+") " + e + " " + f + " >>>" + DIFF + "<<< ***" + tau + "*** " ); 
//            System.out.println(" dij=" + dij + " dji=" + dji ); 
          }
        }

        return cij;
    }
    
}
