
package statistics;

import chart.simple.MultiChart;
import data.series.Messreihe;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.apache.commons.math.stat.Frequency;

/**
 *
 * @author kamir
 */
public class HaeufigkeitsZaehler {

    public long min = 0;
    public long max = 1000;
    

    public int intervalle = 1000;

    public Vector<Long> dists = new Vector<Long>(); 
    public Vector<Long> distsPROCESSED = new Vector<Long>();
    
    public String label = "HISTOGRAM";

    public Messreihe mr = new Messreihe();
    public String folder = "./data/out/topic6/";

    public HaeufigkeitsZaehler( String lab ) { 
        label = lab;
    }
    
    public HaeufigkeitsZaehler() { };

    /**
     * Einfügen eines Wertes in die Haeufigkeitszählung ...
     * 
     * @param dates
     */
    public void addData(Long value) {
         dists.add(value);
    };
    


    /**
     * Einfügen einer Reihe von Zeitpunkten und
     * Berechnung aller Abstände aller Zeitpunkte dieser Reihe.
     *
     * @param dates
     */
    public void processData(Vector<Long> dates) {

        
        int anzahl = dates.size();

        long[][] l = new long[anzahl][anzahl];

        for (int i = 0; i < anzahl; i++) {
            for (int j = 0; j < anzahl; j++) {

                long timeA = dates.elementAt(i);
                long timeB = dates.elementAt(j);

                long dist = Math.abs(timeA - timeB);

                l[i][j] = dist;

                distsPROCESSED.add(dist);

                if (dist > max) {
                    max = dist;
                }
                if (dist < min) {
                    min = dist;
                }

                // System.out.println( dist );
            }
        }

    }



    // Zaehler
    public Frequency f = new Frequency();
    
    public void calcWS() {
        
        processData(dists);

        int st = intervalle;

        Vector<Long> steps = new Vector<Long>();
        for (int i = 0; i < dists.size(); ++i) {
            f.addValue(Double.valueOf(distsPROCESSED.elementAt(i)));
        }
      
       
        min--;

        long delta = (max - min + 1) / st;

        System.out.println("DELTA: " + delta);

        long vor = 0;

        
        mr.setLabel(label);
        
        for (int i = 0; i < st; i++) {

            long x = i * delta + min + 1;
            long w = f.getCumFreq(Double.valueOf(x));

            long y = w - vor;
            vor = w;

            System.out.println(x + "\t" + y + "\t" + vor);
            mr.addValuePair(x, y);

        }

        long f30 = f.getCumFreq(Double.valueOf(min));

        long f20 = f.getCumFreq(Double.valueOf(max));

        long f00 = f.getSumFreq();


        System.out.println("\n\n< min: " + (f30));
        System.out.println(">= min and < max: " + (f20 - f30));
        System.out.println(">= max  " + (f00 - f20));

        System.out.println("total " + (f00));


    }

    public void store() throws IOException {
            FileWriter fw = new FileWriter( folder + label );
            fw.write( mr.toString() );
            fw.flush();
            fw.close();
    };
    
    static public void showDistribution(Vector<Messreihe> rows, String label) {
        MultiChart.open(rows, "Distribution (" + label + ")","length of rows","anz", true );
    }  

    public void showDistribution() {
        this.calcWS();
        Vector<Messreihe> rows = new Vector<Messreihe>();
        rows.add(mr);
        String label = "length of rows";
        showDistribution(rows, label);
    }    
    
    public Messreihe getDistributionMR() { 
        this.calcWS();
        return mr;
    };


        
}
