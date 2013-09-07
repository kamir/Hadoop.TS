
package statphys.eventsynchronisation;

import chart.simple.MultiChart;
import data.series.Messreihe;
import java.util.Collections;
import java.util.Vector;


public class ESCalc {

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
     * 
     */
    public static double[] calc(int[] ti, int[] tj) {
        
        int di = ti.length;
        int dj = tj.length;
        
        double synch = 0.0;
        double delay = 0.0;
        
        double[] data = new double[2];

        double valij=0;
        double valji=0;

        for( int l=1; l < di-1; l++) {
          for( int m=1; m < dj-1; m++) {

            Vector<Integer> temp = new Vector<Integer>();

            temp.add( ti[l+1]-ti[l] );
            temp.add( ti[l]-ti[l-1] );
            temp.add( tj[m+1]-tj[m] );
            temp.add( tj[m]-tj[m-1] );
            
            Integer tau_temp = Collections.min( temp );

            double tau = (double)tau_temp * 0.5;
            
            if( ti[l]-tj[m] == tau ) { 
              valij=valij + 0.5; 
              valji=valji + 0.5; 
            }
            else if ( 0.0 < ti[l]-tj[m] && ((ti[l]-tj[m] )) < tau ) {
              valij=valij+1.0; 
            }  
            else if ( 0.0 < tj[m]-ti[l] && ((tj[m]-ti[l] )) < tau ) {
              valji=valji+1.0;
            }
          }
        }
        
        synch=(valij+valji) / (Math.sqrt( (di-1) * (dj-1) ) );
        delay=(valij-valji) / (Math.sqrt( (di-1) * (dj-1) ) );
 
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

    public static int[] getIETS(int[] r2) {
        
        Vector<Integer> r = new Vector<Integer>();
        int iet = 1;
        for (int i : r2) {
            if (i == 1) { 
                r.add(iet);
                iet = 1;
            }
            else {
                iet++;
            }
        }    
        int p = 0;
        int mem = 0;
        int[] r1 = new int[r.size()];
 
        for (int i : r) {
            mem = mem + i;
            r1[p] = mem - 1;
            // System.out.println( (mem-1) + " " + i );
            p++; 
        }
        // System.out.println(  );
        return r1;
    }

    public static void checkRows(int[] r1, int[] r2) {
        checkRows(r1, r2, "ESCalc" );
    }

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

        MultiChart.open(r , title , "t", "event" , false);
    }
    
}
