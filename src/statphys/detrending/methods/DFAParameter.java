package statphys.detrending.methods;


public class DFAParameter {
    
    double q = 1; // normale DFA; q != 1 => MFDFA

    // Controllparameter p
    private int gradeOfPolynom = 1;

    // Anzahl der Werte der Zeitreihe
    private int N = 0;

    // Anzahl der s-Werte
    private int zSValues = 10;

    public static double logScaleFactor = 1.1;
    public static int log_start = 10;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\norder          =" + gradeOfPolynom+";");
        sb.append("\nnrOfS          =" + zSValues+";");
        sb.append("\nN              =" + N+";");
        sb.append("\nlogScaleFactor =" + logScaleFactor+";");
        return sb.toString();
    };

    public int getGradeOfPolynom() {
        return gradeOfPolynom;
    }

    public void setGradeOfPolynom(int gradeOfPolynom) {
        this.gradeOfPolynom = gradeOfPolynom;
    }

    /**
     * Anzahl von Werten in der Zeitreihe
     *
     * @param N
     */
    public void setN(int N) {
        this.N = N;
    }

    public int getN() {
        return N;
    }

    public void setzSValues(int zSValues) {
        this.zSValues = zSValues;
    }
    /**
     * Anzahl der s-Werte für die F(s) berechnet wird.
     **/
    public int getzSValues() {
        return zSValues;
    }

    public void setQ(double d) {
        this.q = d;
    }

}
