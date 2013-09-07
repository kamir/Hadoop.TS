package statphys.detrending.methods;

import data.series.Messreihe;
import java.util.Vector;
import polysolve.Pair;

/**
 *
 * @author kamir
 */
abstract public class DFACore implements IDetrendingMethod {

    public static double S_SCALE_FACTOR = 1.15;
    
    int[] s = null; // Stellen der Auswertung
    double[] zr = null; // Zeitreihe der ROHDATEN
    double[] pr = null; // Profilreihe
    double[][] F = null; // Fluctuationsfunktion
    double alpha = 0; // Steigung aus loglog Plot der F(s)
    DFAParameter para = null;
    StringBuffer status = new StringBuffer();
    private double corr_coef = -9;

    public static boolean debug = false;

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
    public void initIntervalS() {

     //   int s_max = para.getN() / 4;
        int s_max = para.getN() ;

        int s_min = Math.abs( para.getGradeOfPolynom()) + 2;

        int rangeS = s_max-s_min;

        s = new int[para.getzSValues()];

        s[0] = s_min;
        s[para.getzSValues()-1] = s_max;

        int step = rangeS / para.getzSValues();

        for ( int i = 1; i < (para.getzSValues()-1); i++ ) {
            s[i] = s_min + step * i;
        }
        System.out.println("\n  s (" + s_min + ", " + s_max +") ");
        System.out.println("======");
        for ( int i = 0; i < s.length; i++ ) {
            System.out.println( i + " \t " + s[i] );
        }
        initF();
    }

    Vector<Messreihe> fitMR = new Vector<Messreihe>();

    public Vector<Messreihe> getMRFit() {
        return fitMR;
    };


    public void showS() {
        System.out.println("----------");

        System.out.println("#s: " + s.length);
        System.out.println("----------");
        int i = 0;
        while (i < s.length) {
        System.out.println(i + "\t" + s[i]);
        i++;
        }
        System.out.println("----------");
    }

    public Messreihe getResultsMRLogLog() {
        Messreihe mr = new Messreihe();
        mr.setLabel_X( "s" );
        mr.setLabel_Y( "F(s)" );
        mr.setLabel( "F(s)" ); // para.toString() );
        for( int i = 0; i < F[0].length ; i++ ) {
            double x =  F[0][i];
            double y =  F[3][i];
            if ( x != 0.0 && y != 0.0 ) mr.addValuePair( Math.log10(x) , Math.log10( y ));
        }
        return mr;
    };

    public Messreihe getResultsMR() {
        Messreihe mr = new Messreihe();
        mr.setLabel_X( "s" );
        mr.setLabel_Y( "F(s)" );
        mr.setLabel( "F(s)" ); // para.toString() );
        for( int i = 0; i < F[1].length ; i++ ) { 
            mr.addValuePair( F[0][i], F[3][i]);
        }
        return mr;
    };

    public Messreihe getZeitreiheMR() {
        Messreihe mr = new Messreihe();
        mr.setLabel_X( "t" );
        mr.setLabel_Y( "y(t)" );
        mr.setLabel( "Zeitreihe" );
        for( int i = 0; i < zr.length ; i++ ) {
            mr.addValuePair( i, zr[i]);
        }
        return mr;
    };


    public Messreihe getProfilMR() {
        Messreihe mr = new Messreihe();
        mr.setLabel_X( "t" );
        mr.setLabel_Y( "prof( y(t) )" );
        mr.setLabel( "Profil" );
        for( int i = 0; i < pr.length ; i++ ) {
            mr.addValuePair( i, pr[i]);
        }
        return mr;
    };

    public void initIntervalSlog() {
        int steps = para.getzSValues();

        int s_start = Math.abs( para.getGradeOfPolynom() ) + 2;

        int s_end = para.getN();

        int log_start=20;

        int lin_steps=log_start-s_start;//first steps linear

        int log_steps=0;

        double tmp=0;
        for (double i=log_start*1.03; Math.round(i) < s_end; i=i*1.03) {//      adding number of steps of log scale

            if(Math.round(i)!=tmp){
                tmp=Math.round(i);
                log_steps++;
            }
        }
        para.setzSValues(lin_steps+log_steps);
        s = new int[lin_steps+log_steps];

        for (int i =s_start; i<=log_start;i++) s[i-s_start] = i;
        s[lin_steps]=log_start;

        double newS=s[lin_steps];
        for ( int step = 1; step < log_steps;) {
             newS=Double.parseDouble(String.valueOf(newS))*1.03;
             s[lin_steps+step] = Integer.parseInt(String.valueOf(Math.round(newS)));
             if(s[lin_steps+step] != s[lin_steps+step-1]) step++;
        }

        if ( debug ) {
            System.out.println("\n  s   ");
            System.out.println("\n======");
            for ( int i = 0; i < steps; i++ ) {
                System.out.println( i + " \t " + s[i] );
            }
        }
        initF();
    }

    /**
     * Nach Aufruf dieser Methode ist das RESULT-Array definiert.
     */
    void initF() {
        if (debug) System.out.println("initF() ... " + para.getzSValues() );
        F = new double[4][para.getzSValues()];
    }

    /*
     * Übergibt neues Parameterobjekt
     */
    public void setParameter(Object parameter) {
        para = (DFAParameter) parameter;
    }

    public String getStatus() {
        return status.toString();
    }

    /**
     * Erzeugt neues Parameter-Object
     *
     * @return
     */
    public Object initParameter() {
        para = new DFAParameter();
        return (Object) para;
    }

    public void setNrOfValues(int N) {
        para.setN(N);
    }

    public int[] getS() {
        return s;
    }

 

 

    public void setZR(double[] zr) {
        this.zr = zr;
    }

    /**
     *   Profil berechnen
     *   ==> entspricht das auch der Zahl der Fenster je s? NEIN
     */int a,b =0;
    public void calcProfile() {
        
        pr = new double[zr.length];

        // mean value of data
        double mw = stdlib.StdStats.mean(zr);
        
        // calc profile from data
        
        double prsum = 0.0;
        for (int i = 0; i < zr.length; i++) {
            prsum = 0.0;
            for (int j = 0; j <= i; j++) {
                prsum = prsum + (zr[j] - mw);
                b = j;
            }
            pr[i] = prsum;
            a = i;            
        }
        
        //System.out.println();
    }

    /**
     * Hier wird das Array der Fensterbreiten also s zurückgegeben ...
     */
    public int[] getIntervalS(){
        return s;
    }

    /**
     * Hier wird das Array der F(s) zurückgegeben ...
     */
    public double[][] getResults() {
        return F;
    }

    public DFAParameter getPara() {
        return para;
    }

    public void setPara(DFAParameter para) {
        this.para = para;
    }

    public double getCorr_coef() {
        return corr_coef;
    }

    public void calc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    public double[] fit1(int u, int o) {
        double[] x = new double[o + 1];
        for (int i = 0; i <= o; i++) {
            x[i] = i;
        }
        return fit1(x, u, o);
    }

    public double[] fit1(double[] x, int u, int o) {
        double[] fit = new double[2]; // 2 values: slope and constant term
        //calculation for linear term yi=a*xi+b with method of least squares ...
        double N = (o - u) + 1;  //# of fitable values
        if (N > 1) {
            double S, Sx, Sxx, Sy, Sxy, Syy, delta;
            Sx = 0;
            Sxx = 0;
            Sy = 0;
            Sxy = 0;
            Syy = 0;
            for (int i = u; i <= o; i++) {
                S = x[i] - x[u];         //Subtraktion der unteren Grenze um Zahlen klein zu halten und Differenz hervorzuheben
                Sx += S;               //sum of all x values
                Sxx += S * S;            //sum of all x^2 values
                Sy += pr[i];           //sum of all y values
                Syy += pr[i] * pr[i];    //sum of all y^2 values
                Sxy += pr[i] * S;        //sum of all x*y values
            }
            delta = N * Sxx - Sx * Sx;
            fit[0] = (Sxx * Sy - Sxy * Sx) / delta;    //constant term b of the linear function
            fit[1] = (N * Sxy - Sx * Sy) / delta;    // slope a of the linear function

            //standard deviation
            double stdw, stdw_slope, stdw_const; //stdw sigma^2
            stdw = (fit[0] * fit[0] * Sxx + 2 * fit[0] * fit[1] * Sx - 2 * fit[0] * Sxy - 2 * fit[1] * Sy + Syy + fit[1] * fit[1] * N) / (N - 2);
            stdw_slope = stdw * N / delta;
            stdw_const = stdw * Sxx / delta;

            //correlation coefficient
            double cov_xy = 1 / (N - 1) * (Sxy - Sx * Sy / N); //covarianz from Lit.: Bartsch mathematics
            double cov_xx = 1 / (N - 1) * (Sxx - Sx * Sx / N);
            double cov_yy = 1 / (N - 1) * (Syy - Sy * Sy / N);
            corr_coef = cov_xy / Math.sqrt(cov_xx * cov_yy); // calculation of corcoef from matlab
//           System.out.println("alpha fit = " + fit[1] + " Corr = " + corr_coef);
        } else {
            // sb.append("min 3 values for linear fit");
            // System.out.println(".");
        }
        return fit;
    }

    public double[] fit2(int u, int o) {
        int N;
        double S = 0, Sx = 0, Sxx = 0, Sxxx = 0, Sxxxx = 0, Sy = 0, Sxy = 0, Syy = 0, Sxxy = 0, delta = 0;
        N = (o - u) + 1;  //# of fitable values
        //calculation of quadratic term yi=a*xi^2+b*xi+c with method of least squares ...
        double[] fit = null;
        if (N > 2) {
            fit = new double[3]; // 3 values c, b, a of the quadratic term ...
            
            Sx = 0;
            Sxx = 0;
            Sxxx = 0;
            Sxxxx = 0;
            Sy = 0;
            Sxy = 0;
            Sxxy = 0;
            Syy = 0;
            for (int i = u; i <= o; i++) {
                S = i - u;
                Sx += S;              //sum of all x values
                Sxx += (S *= S);        //sum of all x^2 values
                Sxxx += S * (i - u);          //sum of all x^3 values
                Sxxxx += S * S;         //sum of all x^4 values
                Sy += pr[i];          //sum of all y values
                Syy += pr[i] * pr[i];   //sum of all y^2 values
                Sxy += (i - u) * pr[i];       //sum of all x*y values
                Sxxy += S * pr[i];      //sum of all x^2*y values
            }
            delta = Sxxxx * (N * Sxx - Sx * Sx) + Sxxx * (Sx * Sxx - N * Sxxx) + Sxx * (Sxxx * Sx - Sxx * Sxx);
            fit[0] = (Sxxxx * (Sxx * Sy - Sxy * Sx) + Sxxx * (Sxy * Sxx - Sxxx * Sy) + Sxxy * (Sxxx * Sx - Sxx * Sxx)) / delta;//calculation of c of quadratic term yi=a*xi^2+b*xi+c ...
            fit[1] = (Sxxxx * (N * Sxy - Sx * Sy) + Sxxy * (Sx * Sxx - N * Sxxx) + Sxx * (Sxxx * Sy - Sxy * Sxx)) / delta;//calculation of b of quadratic term yi=a*xi^2+b*xi+c ...
            fit[2] = (Sxxy * (N * Sxx - Sx * Sx) + Sxxx * (Sx * Sy - N * Sxy) + Sxx * (Sxy * Sx - Sxx * Sy)) / delta;//calculation of a of quadratic term yi=a*xi^2+b*xi+c ...
        } else {
            status.append("min 4 values for quadratic fit");
        }
//        if ( Double.isNaN( fit[0] ) ||  Double.isNaN( fit[1] ) ||  Double.isNaN( fit[2]  ) ) {
//            System.out.println( delta + "\t" + S  + "\t" + Sx + "\t" + Sxx + "\t" + Sxxx + "\t" + Sxxxx + "\t" + Sy + "\t" + Sxy + "\t" + Sxxy + "\t" + Syy );
//            fit[0] = 0;
//            fit[1] = 5;
//            fit[2] = 4;
//
//        }
        return fit;
    }

// fit with polysolve for higher grades than 2
    public double[] fitn(int u, int o, int n) {

        polysolve.MatrixFunctions mfunct = new polysolve.MatrixFunctions();

        int N = (o - u) + 1;  //# of fitable values

        polysolve.Pair[] pData = new polysolve.Pair[N];

        for (int i = 0; i < N; i++) {
            Pair paar = new polysolve.Pair();
            paar.x = i;
            paar.y = pr[u + i];
            pData[i] = paar;
        }
        double[] fit = mfunct.polyregress(pData, n);
//
//
//         if ( Double.isNaN( fit[0] ) ||  Double.isNaN( fit[1] ) ||  Double.isNaN( fit[2]  ) ) {
//            //System.out.println( delta + "\t" + S  + "\t" + Sx + "\t" + Sxx + "\t" + Sxxx + "\t" + Sxxxx + "\t" + Sy + "\t" + Sxy + "\t" + Sxxy + "\t" + Syy );
//            fit[0] = 0;
//            fit[1] = 5;
//            fit[2] = 4;
//
//        }


        return fit;
    }
    double alphaFitMin, alphaFitMax;

    // linear fit of loglogplot of F(s) and s to get alpha
    public void calcAlpha(double log_fit_min, double log_fit_max) {
        DFAmulti instance = new DFAmulti();
        double[][] result = new double[2][F[0].length];
        for (int i = 0; i < F[0].length; i++) {
            result[0][i] = Math.log10(F[0][i]);
            result[1][i] = Math.log10(F[1][i]);
            status.append("\n. log F(s):" + (result[0][i]) + "\t log s: " + (result[1][i]));
        }
        instance.pr = result[1];
        int min = 0, max = 0;
        int i;
        for (i = 0; i < result[0].length; i++) {
            if (result[0][i] >= log_fit_min && min == 0) {
                min = i;
            }
            if (result[0][i] >= log_fit_max && max == 0) {
                max = i;
                break;
            }
        }
        if (max == 0) {
            max = result[0].length - 1;
        }

        double[] fit = instance.fit1(result[0], min, max);
        corr_coef = instance.getCorr_coef();
        alpha = fit[1];
//        System.out.println(F[0][min] + " " + F[0][max]);
        alphaFitMin = F[0][min];
        alphaFitMax = F[0][max];
    }

    public double fx( double posX , double[] terms ){
        double a = 0;
        for ( int i = 0; i < terms.length ; i++) {
            a += terms[ i ] * Math.pow(posX, i);
            //System.out.println( "posX=" + posX +"; i=" + i + ", a=" + "  " + a + "\t" );
        }
//        if(debug) System.out.println("\ta=" + a );
        return a;
    }

    public double getAlphaFitMax() {
        return alphaFitMax;
    }

    public double getAlphaFitMin() {
        return alphaFitMin;
    }

    public double getAlpha() {
        this.calcAlpha(Math.log10(F[0][0]), Math.log10(F[0][F[0].length - 1]));
        return alpha;
    }

    public double getAlpha(double min, double max) {
        this.calcAlpha(min, max);
        return alpha;
    }












//
//
//        // kann erst berechnet werden, wenn das Parameter-Objekt vorliegt
//    public void initIntervalS_version3() {
//
//        int s_start = Math.abs( para.getGradeOfPolynom() ) + 2 ;
//        int s_end = para.getN();
//
//        //System.out.println( log_start > s_end );
//        int lin_steps=para.log_start-s_start;//first steps linear
//        int log_steps=0;
//
//        double tmp=0;
//        for (double i=para.log_start*para.logScaleFactor; Math.round(i) < s_end; i=i*para.logScaleFactor) {//      adding number of steps of log scale
//
//            if(Math.round(i)!=tmp){
//                tmp=Math.round(i);
//                log_steps++;
//            }
//        }
//        para.setzSValues(lin_steps+log_steps);
//        s = new int[lin_steps+log_steps];
//
////        if ( s_end < log_start ) {
////            log-s_start = s_end
////        }
//// else {
//        // linearen Teil belegen
//        for (int i =s_start; i<=para.log_start;i++) {
//            s[i-s_start] = i;
//        }
//
//        s[lin_steps]=para.log_start;
//
//        // log-Teil belegen
//        double newS=s[lin_steps];
//        for ( int step = 1; step < log_steps;) {
//             newS=Double.parseDouble(String.valueOf(newS))*para.logScaleFactor;
//             s[lin_steps+step] = Integer.parseInt(String.valueOf(Math.round(newS)));
//             if(s[lin_steps+step] != s[lin_steps+step-1]) step++;
//        }
////
////        for ( int i = 0; i < lin_steps+log_steps; i++ ) {
////            System.out.println( "* " + i + " \t " + s[i] );
////        }
//        initF();
//    }
}
