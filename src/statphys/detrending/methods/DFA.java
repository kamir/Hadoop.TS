package statphys.detrending.methods;

import data.series.Messreihe;

public class DFA extends DFACore {

    @Override
    public void calc() {
        double t1 = System.currentTimeMillis();
        initF();
        status.append("[");

        //create profile of timeline
        calcProfile();

        int zS = 0;

        // over all window sizes ...
        for (int currentS = 0; currentS < s.length; currentS++) {
            zS++;
            int valueofS = s[currentS];

            if(debug) System.out.println(">>> zS=" + zS + " # " + valueofS);
            double FS = 0;

            // dfa caculation ...
            if(debug) System.out.print("\ts=" + valueofS + "\n");
            int zw = 0;
            // move window over profile ...
            for (int pr_pos = 0;
                    pr_pos < pr.length - (valueofS - 1);
                    pr_pos = pr_pos + valueofS) {

                double FS_sum = 0;
                zw++;
                if(debug) System.out.print(pr_pos + ": " + zw + " : ");

                double[] fit = null;
                // size of array depends on order of dfa
                if (para.getGradeOfPolynom() == 1) {
                    fit = fit1(pr_pos, pr_pos + valueofS-1);
                }
                if (para.getGradeOfPolynom() == 2) {
                    fit = fitn(pr_pos, pr_pos + valueofS-1,
                            para.getGradeOfPolynom());
                    //fit = fit2(pr_pos,pr_pos+s[currentS]-1);
                }
                if (para.getGradeOfPolynom() > 2) {
                    fit = fitn(pr_pos, pr_pos + valueofS-1,
                            para.getGradeOfPolynom());
                }
                int z = 0;
                Messreihe mrFitN = null;
                if (currentS == 8) {
                    mrFitN = new Messreihe();
                    mrFitN.setLabel(pr_pos + " - " + (pr_pos + valueofS));
                }
                //  System.out.print("\t\t(" + fit[0] + " " + fit[1] );
                // sum of all (x-schlange)^2 ...
                for (int x = pr_pos; x < pr_pos + valueofS; x++) {

//                  calc y value of fit function depending on poly. degree
                    double fit_y = fx(x-pr_pos, fit);
                    if (currentS == 8) {
                        mrFitN.addValuePair( x, fit_y);
                    }
                    //System.out.print(x+", ");
                    double x_s = (pr[x] - fit_y);  //x~ = pr_value - fit_val

                    FS_sum += x_s * x_s; // x~^2;
                }
                if (currentS == 8) {
                   // fitMR.add(mrFitN);
                }


                FS_sum = FS_sum / valueofS; // Zeilen durch Fensterbreite

                if(debug) System.out.println(")");
                F[2][currentS] += FS_sum;


            }

             if(debug) System.out.println("\t");

            // ist es korrekt, hier durch Länge der Profils zu teilen 
            // oder müsste es die Zahl der summierten Beiträge sein?
            FS = Math.sqrt(F[2][currentS] / zw);  // / anzw:

            F[0][currentS] = valueofS; // s
            F[1][currentS] = zw;       // Zahl Segmente
            F[3][currentS] = FS;       // F(s)

            if(debug) System.out.println(
                    ">> STORE RESULTS ... " + valueofS + ", " + zw);
            zw = 0;

        } // für alle s;

        System.out.println();
        double t2 = System.currentTimeMillis();
        status.append("]");
        status.append("\n> Dauer: " + ((t2 - t1) / 1000) + " s. ");
    }



    // kann erst berechnet werden, wenn das Parameter-Objekt vorliegt
    public void initIntervalS_version4() {

        int s_start = Math.abs( para.getGradeOfPolynom() ) + 2 ;
        int s_end = para.getN();

        para.setzSValues( s_end - s_start);
        s = new int[s_end - s_start];

        // linearen Teil belegen
        for (int i = 0; i < ( s_end-s_start ); i++) {
            s[i] = i+s_start;
            if ( debug ) System.out.println(s[i]);
        }
        initF();
    }

    public void initIntervalS_FULL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
