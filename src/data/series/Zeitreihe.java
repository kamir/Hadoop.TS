
package data.series;

import data.series.Messreihe;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * Bei einer Zeitreihe werden die Werte für:
 *
 * x:  long (Zeitstempel)
 * y:  beliebige Größe gespeichert.
 *
 * Benötigt man nun Werte wie z.B. die Anzahl eines
 * Ereignisses pro Tag oder pro zeitintervall, so sind die Rohdaten zunächst
 * zusammenzufassen.
 *
 * Danach erhält jeder Intervall einen laufenden Index.
 * Die intervallbreite und die absolute Startposition des ersten Intervalls
 * werden ebenfalls festgehalten.
 *
 * @author kamir
 */
public class Zeitreihe extends Messreihe {

    public static Zeitreihe getTestZeitreihe_X_RANDOM() {
        Zeitreihe zr = new Zeitreihe(); 
        double sum = 0.0;
        
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 0, 1, 0, 0, 0);
        
        long begin = cal.getTimeInMillis();
        
        long t = begin;

        double anz = Math.random() * 512.0;
        int i = 0;
        double raum = 300.0*24.0;
        while ( i < anz ) {

            double dt1 = Math.random() * raum;
            
            
            t = begin + 60*60*1000* (long)dt1;
            zr.addValuePair(1.0 * t, dt1);
            
            i = i+1;
        }
        //System.out.println( zr.toString() );
        return zr;
                
    }

    public static Zeitreihe getTestZeitreihe_EINZELTEST() {
        Zeitreihe zr = new Zeitreihe(); 
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 0, 1, 0, 0, 0);
        long[] dts = { 2, 5, 8, 12, 16, 24, 28, 30, 40, 50, 80, 100, 200, 300, 200, 200, 100, 500, 20, 25, 30, 60, 90,400, 600 }; 
        long begin = cal.getTimeInMillis();
        int j = 0;
        long t = begin;
        zr.addValuePair(t, 1 );
        while ( j < 1 ) {
            for( long dt1 : dts ) {
                t = t + 1000 * 60 * 60 * (long)dt1;
                zr.addValuePair(1.0 * t, dt1);
            }
            j++;
        }    
        System.out.println( zr.toString() );
        return zr;
    }

    // zur Umrechnung der Funktionswerte y(t) in eine Anzahl von
    // Events pro Intervall
    int nrOfInterval;
    long beginOffirstInterval;

    public Messreihe countedEvents = null;
    public Messreihe nrOfEvents_over_t = null;

    public Messreihe countedEvents_h = null;
    public Messreihe nrOfEvents_over_t_h = null;


    public Zeitreihe() {
        xValues = new Vector<String>();
        yValues = new Vector<Integer>();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int size = xValues.size();

        sb.append("#\n# ZR: " + size + "\n#" +
                this.getClass() + " : " + label + "\n#\n");

        for (int i = 0; i < size; i++) {
            Double d = (Double) getXValues().elementAt(i);
            long dd = d.longValue();
            String x = DateFormat.getDateTimeInstance().format(
                    new Date(dd));
            Double y = (Double) getYValues().elementAt(i);
            sb.append(x + "\t" + y + "\t ("+ getXValues().elementAt(i) +")\n");
        }
        return sb.toString();
    }



    public void countEventsPerDay() {
        Messreihe mr = new Messreihe();
        Messreihe mr2 = new Messreihe();

        int size = xValues.size();
        
        if ( size > 0 ) {

            int counter = 1;
            Double d = (Double) getXValues().elementAt(0);

            beginOffirstInterval = d.longValue();
            Date lastDate = new Date(beginOffirstInterval);

            Calendar cal = Calendar.getInstance();
            cal.setTime(lastDate);

            // den Tages-Wert des ersten Wertepaares ermitteln
            int lastDay = cal.get( Calendar.DAY_OF_YEAR );

            for (int i = 1; i < size; i++) {

                // für jeden weiteren tag den Tageswert ermitteln ...
                Double d1 = (Double) getXValues().elementAt(i);
                long dd2 = d1.longValue();
                Date ddd = new Date(dd2);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(ddd);

                int dayOfYear = cal2.get( Calendar.DAY_OF_YEAR );
                if ( dayOfYear > lastDay ) {

                    mr.addValuePair(1.0 * lastDay, 1.0*counter);

                    lastDay = dayOfYear;
                    counter = 1;
                }
                else {
                    counter++;
                }
            }


            // nun liegen die COUNTS per day vor ... aber ohne der Werte
            // an denen keine Counts waren ...
            for( int i = 0; i < mr.getMaxX() ; i++ ) {
                mr2.addValuePair(i, mr.getYValueForX(i) );
            }
        }

        this.countedEvents = mr;
        this.nrOfEvents_over_t = mr2;
    }

     public void countEventsPerHour() {
        Messreihe mr = new Messreihe();
        int size = xValues.size();

        // relativ zum 1.1.2009 um 0:0
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 0, 1, 0, 0, 0);

        long dat = cal.getTime().getTime() - 1;

        int counter = 1;


        beginOffirstInterval = dat;

        long lastHour = 0;
        long teiler = 1000 * 60 * 60 * 24;

        for (int i = 0; i < size; i++) {

           

            // für jeden weiteren tag den Tageswert ermitteln ...
            Double d1 = (Double) getXValues().elementAt(i);
            long dd2 = d1.longValue();

            long hour = dd2 % teiler;

            mr.addValuePair( hour, d1 );

            if ( hour > lastHour ) {

               //  mr.addValuePair(1.0 * lastHour, 1.0*counter);

                lastHour = hour;
                counter = 1;
            }
            else {
                counter++;
            }
        }

//        Messreihe mr2 = new Messreihe();
//        // nun liegen die COUNTS per day vor ... aber ohne der Werte
//        // an denen keine Counts waren ...
//        for( int i = 0; i < mr.getMaxX() ; i++ ) {
//            mr2.addValuePair(i, mr.getYValueForX(i) );
//        };

        this.countedEvents_h = mr;
      //  this.nrOfEvents_over_t_h = mr2;
    }


}
