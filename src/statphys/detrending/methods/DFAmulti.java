package statphys.detrending.methods;

/**
 *
 * DFA Multi bietet eine Möglichkeit, für viele Zeitreihen z.B. einer Phase
 * eine F(s) Kurve zu errechnen und gemäß deren Länge zu gewichten.
 *
 * Eine einfache Mittelwertsbildung der einzelnen F(s) Kurven würde nicht
 * genügen, da dabei die kurzen Abschnitte zu hohen Einfluss hätten.
 *
 */
import chart.simple.MultiChart;
import data.series.Messreihe;
import java.util.Vector;
import polysolve.Pair;

public class DFAmulti extends DFACore {

    public double[][] FSMW = null;

    int indexMaxS = 0;


    @Override
    public void calc() {
    
        _initFelder();

        //create profile of timeline
        calcProfile();

        int zS = 0;

        indexMaxS = this.getMaxIndex( this.para.getN() );

        if ( debug ) System.out.println( "N="+this.para.getN() + " zSValues=" + this.para.getzSValues()
                + " indexMaxS=" + indexMaxS + " s[indexMaxS]=" + s[indexMaxS]);

        // over all window sizes ...
        for (int currentS = 0; currentS < indexMaxS ; currentS++) {
            zS++;
            int valueofS = s[currentS];

            if(debug) System.out.println(">>> zS=" + zS + "\n>>> valueofS=" + valueofS);
            double FS = 0;

            // Fensterzähler zurücksetzen ...
            int zw = 0;
            
            // move window over profile ...
            for (int pr_pos = 0;
                    pr_pos < ( pr.length - valueofS) ;
                    pr_pos = pr_pos + valueofS) { 

                double FS_sum = 0;
                zw++;
                if(debug) System.out.println( "pr_pos="+pr_pos + " zw=" + zw + " : KRIT=" + ( pr.length - valueofS) ); 

                double[] fit = null;
                // size of array depends on order of dfa
                if (para.getGradeOfPolynom() == 1) {
                    fit = fit1(pr_pos, pr_pos + valueofS-1);
                }
                if (para.getGradeOfPolynom() == 2) {
                    fit = fitn(pr_pos, pr_pos + valueofS-1,
                            para.getGradeOfPolynom());
                }
                if (para.getGradeOfPolynom() > 2) {
                    fit = fitn(pr_pos, pr_pos + valueofS-1,
                            para.getGradeOfPolynom());
                }

                int z = 0;

                int sCheck = 5; // für welches s soll denn die Fitkurve
                                // gezeigt werden?

//                Messreihe mrFitN = null;
//                if (currentS == sCheck) {
//                    mrFitN = new Messreihe();
//                    mrFitN.setLabel(pr_pos + " [" + (pr_pos +","+ valueofS)+"]");
//                }

                // sum of all (x-schlange)^2 ...
                for (int x = pr_pos; x < pr_pos + valueofS; x++) {

                    // calc y value of fit function depending on poly. degree
                    double fit_y = fx(x-pr_pos, fit);

                    if ( Double.isNaN( pr[x] ) )
                        z = 1;

//                    if (currentS == sCheck) {
//                        mrFitN.addValuePair( x, fit_y);
//                    }
                    double x_s = (pr[x] - fit_y);  //x~ = pr_value - fit_val


                    FS_sum += x_s * x_s; // x~^2;

                }
//                if (currentS == sCheck) {
//                    fitMR.add(mrFitN);
//                }

                FS_sum = FS_sum / valueofS; // Fensterbreite

                F[2][currentS] = F[2][currentS] + FS_sum;
            }
    
            // ist es korrekt, hier durch Länge der Profils zu teilen
            // oder müsste es die Zahl der summierten Beiträge sein?
            FS = Math.sqrt(F[2][currentS] / zw);  // / anzw:

            F[0][currentS] = valueofS; // s
            F[1][currentS] = zw;       // Zahl Segmente
            F[3][currentS] = FS;       // F(s)

            FSMW[0][currentS] = F[2][currentS];  // SUMME der xs^2 / s
            FSMW[1][currentS] = F[1][currentS];  // Zahl segmente

            double a = FSMW[0][currentS];
            double b = FSMW[1][currentS];

//            if ( Double.isNaN(a) || Double.isNaN(b)  ) {
//                System.out.println( a );
//            }

            if(debug) System.out.println(
              ">> STORE RESULTS ... valueofS="+valueofS+", zw="+zw+"\n");
            
            zw = 0;
        } // für alle s;

        double t2 = System.currentTimeMillis();

    }

    /**
     * Einfache lineare Auswahl von n=steps
     * Fensterbreiten. Maximal Länge auf N/4 beschränkt, damit auch einzelne
     * Reihen verwendet werden können.
     *
     * Bei vielen Reihen ist das nicht nötig !!!
     *
     * mit:   s_max = N/4
     * und:   s_min = p+2
     */
    public void initIntervalS_FULL( int s_min, int s_max ) {

        rangeS = s_max-s_min;
        System.out.println("Range:" + rangeS );

        // WICHITIG !!!
        sMax = rangeS;

        para.setzSValues(rangeS);

        s = new int[rangeS];

        s[0] = s_min;
        s[para.getzSValues()-1] = s_max;

        int step = 1;

        for ( int i = 1; i < rangeS; i++ ) {
            s[i] = s_min + step * i;
        }
    }

    public void initIntervalS_FULL2( int s_min, int s_max ) {

        Vector<Double> lin = new Vector<Double>();
        Vector<Double> log = new Vector<Double>();
        
        for ( int sv = s_min; sv < s_min + 200; sv++ ) {
            lin.add(sv * 1.0);
        }
        
        double offset = s_min + 200;
        double sv = offset;
        
        double f = DFACore.S_SCALE_FACTOR;
        while( sv < s_max) {
            f = f * DFACore.S_SCALE_FACTOR;
            sv = offset + f;
            
            if ( !log.contains( sv ) ) log.add( sv );
        }
        
        
        rangeS = lin.size() + log.size();

        System.out.println("Range:" + rangeS );
        
        para.setzSValues(rangeS);
        sMax = rangeS;

        s = new int[rangeS];

        int i = 0;
        for( double svec : lin ) {
            s[i] = (int)svec;
            //System.err.println("i=" + i + " " + s[i] );
            i++;
        }
        
        for( double svec : log ) {
            s[i] = (int)svec;
            //System.err.println("i=" + i + " " + s[i] );
            i++;
        }
    }

    
        /**
     * Einfache lineare Auswahl von n=steps
     * Fensterbreiten. Maximal Länge auf N/4 beschränkt, damit auch einzelne
     * Reihen verwendet werden können.
     *
     * Bei vielen Reihen ist das nicht nötig !!!
     *
     * mit:   s_max = N/4
     * und:   s_min = p+2
     */
    
    public void initIntervalS_FULL_LOG( int s_min, int s_max ) {

        Vector<Integer> sss = new Vector<Integer>();
        int sx = 0;
        int i = 0;
        int j = 0;
        
        double bbb = S_SCALE_FACTOR;
        while ( sx < s_max ) {
            sx = (int)(Math.pow( bbb, i));
            if ( !sss.contains(sx) ) {
                sss.add(sx);
                j++;
            }
            i = i + 1;
        } 
        
        rangeS = j;

        para.setzSValues(rangeS);

        s = new int[rangeS];

        s[0] = s_min;
        
        int ix = 0;
        for( int x : sss ) {
           s[ix] = s_min + x;   
           ix++;
        }
        
        
    }


    int rangeS=0;
    public int sMax = 0;

    public void initIntervalS_version4() {
        this.initIntervalSlog();
    }

    public void initIntervalS_FULL() {
      throw new UnsupportedOperationException("Not supported any more.");
    };

    private int getMaxIndex(int laenge) {
        int max_ind = 0;
        for ( int vs = 0; vs < s.length; vs++ )  {
            //System.out.println( s[vs]);
            if ( s[vs] < laenge ) max_ind = vs;
            else break;
        }
        return max_ind;
    }

    private void _initFelder() {
        initF();
        FSMW = new double[2][rangeS];
    }

}
