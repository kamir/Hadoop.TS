package statphys.detrending.methods;

import data.series.Messreihe;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public interface IDetrendingMethod {

        public double fx(double x, double[] terms);

    /**
     * Ein Parameterobjekt überschreibt das intern vorhandene.
     * 
     * @param parameter
     */
    public void setParameter( Object parameter );

    /**
     * Intern wird ein Parameterobject erzeugt und übergeben,
     * damit es ausserhalb initialisiert werden kann.
     * 
     * @return
     */
    public Object initParameter();

    /**
     * Vor Beginn der Rechnung muss die Zahl der Parameter der Zeitreihe
     * bekannt sein.
     *
     * @param length
     */
    public void setNrOfValues(int length);

    /**
     * Hier werden die Fensterbreiten festgelegt ...
     */
    public void initIntervalS();

    /**
     * Hier werden die Fensterbreiten logaritmisch festgelegt ...
     */
    public void initIntervalSlog();
    public void initIntervalS_FULL();
    
//    public void initIntervalS_version3();
    public void initIntervalS_version4();

    /**
     * das Array mit den Daten wird nun übergeben ...
     */
    public void setZR(double[] zr);

    /**
     * das Array mit den Daten wird nun zum ProfilArray umgerechnet ...
     */
    public void calcProfile();
    public Messreihe getProfilMR();

    public Messreihe getZeitreiheMR();
    /**
     * korrelationskoeffizient ...
     */
    public double getCorr_coef();

    /**
     * run dfa calculation ...
     */
    public void calc();

    /**
     * calc alpha and return value ...fitted from beginning to end of F(s)
     */
    public double getAlpha();
    /**
     * calc alpha and return value ... for given fit border
     */
    public double getAlpha(double min, double max);

    /**
     * Rückgabe der werte s und F(s) in einem Array
     */
    public double[][] getResults();

    /**
     * Rückgabe der werte s und F(s) in einer Messreihe
     */
    public Messreihe getResultsMR();
    public Messreihe getResultsMRLogLog();

    public Vector<Messreihe> getMRFit();




    /**
     * Falls es etwas zu berichten gibt steht es hier ...
     *
     * @return status
     */
    public String getStatus();

    /**
     * returns the dfa parameter
     */
    public DFAParameter getPara();

    /**
     * returns the actually used fitminborder
     */
    public double getAlphaFitMax();

    /**
     * returns the actually used fitaxborder
     */
    public double getAlphaFitMin();

        /**
     * returns the dfa parameter
     */
    public int[] getS();
    public void showS();

}
